/*     */ package org.eclipse.jdt.internal.compiler.flow;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Reference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TryStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class FlowContext
/*     */   implements TypeConstants
/*     */ {
/*  39 */   public static final FlowContext NotContinuableContext = new FlowContext(null, null);
/*     */   public ASTNode associatedNode;
/*     */   public FlowContext parent;
/*     */   public NullInfoRegistry initsOnFinally;
/*     */   boolean deferNullDiagnostic;
/*     */   boolean preemptNullDiagnostic;
/*     */   public static final int CAN_ONLY_NULL_NON_NULL = 0;
/*     */   public static final int CAN_ONLY_NULL = 1;
/*     */   public static final int CAN_ONLY_NON_NULL = 2;
/*     */   public static final int MAY_NULL = 3;
/*     */   public static final int CHECK_MASK = 255;
/*     */   public static final int IN_COMPARISON_NULL = 256;
/*     */   public static final int IN_COMPARISON_NON_NULL = 512;
/*     */   public static final int IN_ASSIGNMENT = 768;
/*     */   public static final int IN_INSTANCEOF = 1024;
/*     */   public static final int CONTEXT_MASK = -256;
/*     */ 
/*     */   public FlowContext(FlowContext parent, ASTNode associatedNode)
/*     */   {
/*  68 */     this.parent = parent;
/*  69 */     this.associatedNode = associatedNode;
/*  70 */     if (parent != null) {
/*  71 */       this.deferNullDiagnostic = 
/*  72 */         ((parent.deferNullDiagnostic) || (parent.preemptNullDiagnostic));
/*  73 */       this.initsOnFinally = parent.initsOnFinally;
/*     */     }
/*     */   }
/*     */ 
/*     */   public BranchLabel breakLabel() {
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   public void checkExceptionHandlers(TypeBinding raisedException, ASTNode location, FlowInfo flowInfo, BlockScope scope)
/*     */   {
/*  87 */     FlowContext traversedContext = this;
/*  88 */     while (traversedContext != null)
/*     */     {
/*     */       SubRoutineStatement sub;
/*  90 */       if (((sub = traversedContext.subroutine()) != null) && (sub.isSubRoutineEscaping()))
/*     */       {
/*  93 */         return;
/*     */       }
/*     */ 
/*  98 */       if ((traversedContext instanceof ExceptionHandlingFlowContext)) {
/*  99 */         ExceptionHandlingFlowContext exceptionContext = 
/* 100 */           (ExceptionHandlingFlowContext)traversedContext;
/*     */         ReferenceBinding[] caughtExceptions;
/* 102 */         if ((caughtExceptions = exceptionContext.handledExceptions) != Binding.NO_EXCEPTIONS) {
/* 103 */           boolean definitelyCaught = false;
/* 104 */           int caughtIndex = 0; int caughtCount = caughtExceptions.length;
/* 105 */           while (caughtIndex < caughtCount)
/*     */           {
/* 107 */             ReferenceBinding caughtException = caughtExceptions[caughtIndex];
/* 108 */             int state = caughtException == null ? 
/* 109 */               -1 : 
/* 110 */               Scope.compareTypes(raisedException, caughtException);
/* 111 */             switch (state) {
/*     */             case -1:
/* 113 */               exceptionContext.recordHandlingException(
/* 114 */                 caughtException, 
/* 115 */                 flowInfo.unconditionalInits(), 
/* 116 */                 raisedException, 
/* 117 */                 location, 
/* 118 */                 definitelyCaught);
/*     */ 
/* 120 */               definitelyCaught = true;
/* 121 */               break;
/*     */             case 1:
/* 123 */               exceptionContext.recordHandlingException(
/* 124 */                 caughtException, 
/* 125 */                 flowInfo.unconditionalInits(), 
/* 126 */                 raisedException, 
/* 127 */                 location, 
/* 128 */                 false);
/*     */             case 0:
/*     */             }
/* 106 */             caughtIndex++;
/*     */           }
/*     */ 
/* 132 */           if (definitelyCaught) {
/* 133 */             return;
/*     */           }
/*     */         }
/* 136 */         if (exceptionContext.isMethodContext) {
/* 137 */           if (raisedException.isUncheckedException(false)) {
/* 138 */             return;
/*     */           }
/*     */ 
/* 142 */           if (!(exceptionContext.associatedNode instanceof AbstractMethodDeclaration)) break;
/* 143 */           AbstractMethodDeclaration method = (AbstractMethodDeclaration)exceptionContext.associatedNode;
/* 144 */           if ((!method.isConstructor()) || (!method.binding.declaringClass.isAnonymousType()))
/*     */             break;
/* 146 */           exceptionContext.mergeUnhandledException(raisedException);
/* 147 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 154 */       traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
/*     */ 
/* 156 */       if ((traversedContext instanceof InsideSubRoutineFlowContext)) {
/* 157 */         ASTNode node = traversedContext.associatedNode;
/* 158 */         if ((node instanceof TryStatement)) {
/* 159 */           TryStatement tryStatement = (TryStatement)node;
/* 160 */           flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
/*     */         }
/*     */       }
/* 163 */       traversedContext = traversedContext.parent;
/*     */     }
/*     */ 
/* 166 */     scope.problemReporter().unhandledException(raisedException, location);
/*     */   }
/*     */ 
/*     */   public void checkExceptionHandlers(TypeBinding[] raisedExceptions, ASTNode location, FlowInfo flowInfo, BlockScope scope)
/*     */   {
/*     */     int raisedCount;
/* 176 */     if ((raisedExceptions == null) || 
/* 177 */       ((raisedCount = raisedExceptions.length) == 0))
/* 178 */       return;
/*     */     int raisedCount;
/* 179 */     int remainingCount = raisedCount;
/*     */ 
/* 183 */     System.arraycopy(
/* 184 */       raisedExceptions, 
/* 185 */       0, 
/* 186 */       raisedExceptions = new TypeBinding[raisedCount], 
/* 187 */       0, 
/* 188 */       raisedCount);
/* 189 */     FlowContext traversedContext = this;
/*     */ 
/* 191 */     while (traversedContext != null)
/*     */     {
/*     */       SubRoutineStatement sub;
/* 193 */       if (((sub = traversedContext.subroutine()) != null) && (sub.isSubRoutineEscaping()))
/*     */       {
/* 196 */         return;
/*     */       }
/*     */ 
/* 200 */       if ((traversedContext instanceof ExceptionHandlingFlowContext)) {
/* 201 */         ExceptionHandlingFlowContext exceptionContext = 
/* 202 */           (ExceptionHandlingFlowContext)traversedContext;
/*     */         ReferenceBinding[] caughtExceptions;
/* 204 */         if ((caughtExceptions = exceptionContext.handledExceptions) != Binding.NO_EXCEPTIONS) {
/* 205 */           int caughtCount = caughtExceptions.length;
/* 206 */           boolean[] locallyCaught = new boolean[raisedCount];
/*     */ 
/* 208 */           for (int caughtIndex = 0; caughtIndex < caughtCount; caughtIndex++) {
/* 209 */             ReferenceBinding caughtException = caughtExceptions[caughtIndex];
/* 210 */             for (int raisedIndex = 0; raisedIndex < raisedCount; raisedIndex++)
/*     */             {
/*     */               TypeBinding raisedException;
/* 212 */               if ((raisedException = raisedExceptions[raisedIndex]) != null) {
/* 213 */                 int state = caughtException == null ? 
/* 214 */                   -1 : 
/* 215 */                   Scope.compareTypes(raisedException, caughtException);
/* 216 */                 switch (state) {
/*     */                 case -1:
/* 218 */                   exceptionContext.recordHandlingException(
/* 219 */                     caughtException, 
/* 220 */                     flowInfo.unconditionalInits(), 
/* 221 */                     raisedException, 
/* 222 */                     location, 
/* 223 */                     locallyCaught[raisedIndex]);
/*     */ 
/* 225 */                   if (locallyCaught[raisedIndex] != 0) continue;
/* 226 */                   locallyCaught[raisedIndex] = true;
/*     */ 
/* 228 */                   remainingCount--;
/*     */ 
/* 230 */                   break;
/*     */                 case 1:
/* 232 */                   exceptionContext.recordHandlingException(
/* 233 */                     caughtException, 
/* 234 */                     flowInfo.unconditionalInits(), 
/* 235 */                     raisedException, 
/* 236 */                     location, 
/* 237 */                     false);
/*     */                 case 0:
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 244 */           for (int i = 0; i < raisedCount; i++) {
/* 245 */             if (locallyCaught[i] != 0) {
/* 246 */               raisedExceptions[i] = null;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 251 */         if (exceptionContext.isMethodContext) {
/* 252 */           for (int i = 0; i < raisedCount; i++)
/*     */           {
/*     */             TypeBinding raisedException;
/* 254 */             if (((raisedException = raisedExceptions[i]) == null) || 
/* 255 */               (!raisedException.isUncheckedException(false))) continue;
/* 256 */             remainingCount--;
/* 257 */             raisedExceptions[i] = null;
/*     */           }
/*     */ 
/* 263 */           if (!(exceptionContext.associatedNode instanceof AbstractMethodDeclaration)) break;
/* 264 */           AbstractMethodDeclaration method = (AbstractMethodDeclaration)exceptionContext.associatedNode;
/* 265 */           if ((!method.isConstructor()) || (!method.binding.declaringClass.isAnonymousType()))
/*     */             break;
/* 267 */           for (int i = 0; i < raisedCount; i++)
/*     */           {
/*     */             TypeBinding raisedException;
/* 269 */             if ((raisedException = raisedExceptions[i]) != null) {
/* 270 */               exceptionContext.mergeUnhandledException(raisedException);
/*     */             }
/*     */           }
/* 273 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 279 */       if (remainingCount == 0) {
/* 280 */         return;
/*     */       }
/* 282 */       traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
/*     */ 
/* 284 */       if ((traversedContext instanceof InsideSubRoutineFlowContext)) {
/* 285 */         ASTNode node = traversedContext.associatedNode;
/* 286 */         if ((node instanceof TryStatement)) {
/* 287 */           TryStatement tryStatement = (TryStatement)node;
/* 288 */           flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
/*     */         }
/*     */       }
/* 291 */       traversedContext = traversedContext.parent;
/*     */     }
/*     */ 
/* 294 */     for (int i = 0; i < raisedCount; i++)
/*     */     {
/*     */       TypeBinding exception;
/* 296 */       if ((exception = raisedExceptions[i]) == null)
/*     */         continue;
/* 298 */       int j = 0;
/* 299 */       while (raisedExceptions[j] != exception)
/*     */       {
/* 298 */         j++; if (j < i) {
/*     */           continue;
/*     */         }
/* 301 */         scope.problemReporter().unhandledException(exception, location);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public BranchLabel continueLabel() {
/* 307 */     return null;
/*     */   }
/*     */ 
/*     */   public FlowInfo getInitsForFinalBlankInitializationCheck(TypeBinding declaringType, FlowInfo flowInfo) {
/* 311 */     FlowContext current = this;
/* 312 */     FlowInfo inits = flowInfo;
/*     */     do
/* 314 */       if ((current instanceof InitializationFlowContext)) {
/* 315 */         InitializationFlowContext initializationContext = (InitializationFlowContext)current;
/* 316 */         if (((TypeDeclaration)initializationContext.associatedNode).binding == declaringType) {
/* 317 */           return inits;
/*     */         }
/* 319 */         inits = initializationContext.initsBeforeContext;
/* 320 */         current = initializationContext.initializationParent;
/* 321 */       } else if ((current instanceof ExceptionHandlingFlowContext)) {
/* 322 */         ExceptionHandlingFlowContext exceptionContext = (ExceptionHandlingFlowContext)current;
/* 323 */         current = exceptionContext.initializationParent == null ? exceptionContext.parent : exceptionContext.initializationParent;
/*     */       } else {
/* 325 */         current = current.parent;
/*     */       }
/* 327 */     while (current != null);
/*     */ 
/* 329 */     return null;
/*     */   }
/*     */ 
/*     */   public FlowContext getTargetContextForBreakLabel(char[] labelName)
/*     */   {
/* 336 */     FlowContext current = this; FlowContext lastNonReturningSubRoutine = null;
/* 337 */     while (current != null) {
/* 338 */       if (current.isNonReturningContext())
/* 339 */         lastNonReturningSubRoutine = current;
/*     */       char[] currentLabelName;
/* 342 */       if (((currentLabelName = current.labelName()) != null) && 
/* 343 */         (CharOperation.equals(currentLabelName, labelName))) {
/* 344 */         ((LabeledStatement)current.associatedNode).bits |= 64;
/* 345 */         if (lastNonReturningSubRoutine == null)
/* 346 */           return current;
/* 347 */         return lastNonReturningSubRoutine;
/*     */       }
/* 349 */       current = current.parent;
/*     */     }
/*     */ 
/* 352 */     return null;
/*     */   }
/*     */ 
/*     */   public FlowContext getTargetContextForContinueLabel(char[] labelName)
/*     */   {
/* 359 */     FlowContext current = this;
/* 360 */     FlowContext lastContinuable = null;
/* 361 */     FlowContext lastNonReturningSubRoutine = null;
/*     */ 
/* 363 */     while (current != null) {
/* 364 */       if (current.isNonReturningContext()) {
/* 365 */         lastNonReturningSubRoutine = current;
/*     */       }
/* 367 */       else if (current.isContinuable())
/* 368 */         lastContinuable = current;
/*     */       char[] currentLabelName;
/* 373 */       if (((currentLabelName = current.labelName()) != null) && (CharOperation.equals(currentLabelName, labelName))) {
/* 374 */         ((LabeledStatement)current.associatedNode).bits |= 64;
/*     */ 
/* 377 */         if ((lastContinuable != null) && 
/* 378 */           (current.associatedNode.concreteStatement() == lastContinuable.associatedNode))
/*     */         {
/* 380 */           if (lastNonReturningSubRoutine == null) return lastContinuable;
/* 381 */           return lastNonReturningSubRoutine;
/*     */         }
/*     */ 
/* 384 */         return NotContinuableContext;
/*     */       }
/* 386 */       current = current.parent;
/*     */     }
/*     */ 
/* 389 */     return null;
/*     */   }
/*     */ 
/*     */   public FlowContext getTargetContextForDefaultBreak()
/*     */   {
/* 396 */     FlowContext current = this; FlowContext lastNonReturningSubRoutine = null;
/* 397 */     while (current != null) {
/* 398 */       if (current.isNonReturningContext()) {
/* 399 */         lastNonReturningSubRoutine = current;
/*     */       }
/* 401 */       if ((current.isBreakable()) && (current.labelName() == null)) {
/* 402 */         if (lastNonReturningSubRoutine == null) return current;
/* 403 */         return lastNonReturningSubRoutine;
/*     */       }
/* 405 */       current = current.parent;
/*     */     }
/*     */ 
/* 408 */     return null;
/*     */   }
/*     */ 
/*     */   public FlowContext getTargetContextForDefaultContinue()
/*     */   {
/* 415 */     FlowContext current = this; FlowContext lastNonReturningSubRoutine = null;
/* 416 */     while (current != null) {
/* 417 */       if (current.isNonReturningContext()) {
/* 418 */         lastNonReturningSubRoutine = current;
/*     */       }
/* 420 */       if (current.isContinuable()) {
/* 421 */         if (lastNonReturningSubRoutine == null)
/* 422 */           return current;
/* 423 */         return lastNonReturningSubRoutine;
/*     */       }
/* 425 */       current = current.parent;
/*     */     }
/*     */ 
/* 428 */     return null;
/*     */   }
/*     */ 
/*     */   public String individualToString() {
/* 432 */     return "Flow context";
/*     */   }
/*     */ 
/*     */   public FlowInfo initsOnBreak() {
/* 436 */     return FlowInfo.DEAD_END;
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo initsOnReturn() {
/* 440 */     return FlowInfo.DEAD_END;
/*     */   }
/*     */ 
/*     */   public boolean isBreakable() {
/* 444 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isContinuable() {
/* 448 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isNonReturningContext() {
/* 452 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isSubRoutine() {
/* 456 */     return false;
/*     */   }
/*     */ 
/*     */   public char[] labelName() {
/* 460 */     return null;
/*     */   }
/*     */ 
/*     */   public void recordBreakFrom(FlowInfo flowInfo)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void recordBreakTo(FlowContext targetContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void recordContinueFrom(FlowContext innerFlowContext, FlowInfo flowInfo)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected boolean recordFinalAssignment(VariableBinding variable, Reference finalReference) {
/* 476 */     return true;
/*     */   }
/*     */ 
/*     */   protected void recordNullReference(LocalVariableBinding local, Expression expression, int status)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void recordReturnFrom(UnconditionalFlowInfo flowInfo)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void recordSettingFinal(VariableBinding variable, Reference finalReference, FlowInfo flowInfo)
/*     */   {
/* 501 */     if ((flowInfo.tagBits & 0x1) == 0)
/*     */     {
/* 503 */       FlowContext context = this;
/* 504 */       while (context != null) {
/* 505 */         if (!context.recordFinalAssignment(variable, finalReference)) {
/*     */           break;
/*     */         }
/* 508 */         context = context.parent;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void recordUsingNullReference(Scope scope, LocalVariableBinding local, Expression reference, int checkType, FlowInfo flowInfo)
/*     */   {
/* 536 */     if (((flowInfo.tagBits & 0x1) != 0) || 
/* 537 */       (flowInfo.isDefinitelyUnknown(local))) {
/* 538 */       return;
/*     */     }
/* 540 */     switch (checkType) {
/*     */     case 256:
/*     */     case 512:
/* 543 */       if (flowInfo.isDefinitelyNonNull(local)) {
/* 544 */         if (checkType == 512)
/* 545 */           scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
/*     */         else {
/* 547 */           scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
/*     */         }
/* 549 */         return;
/*     */       }
/* 551 */       if (!flowInfo.cannotBeDefinitelyNullOrNonNull(local)) break;
/* 552 */       return;
/*     */     case 257:
/*     */     case 513:
/*     */     case 769:
/*     */     case 1025:
/* 559 */       if (flowInfo.isDefinitelyNull(local))
/* 560 */         switch (checkType & 0xFFFFFF00) {
/*     */         case 256:
/* 562 */           scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
/* 563 */           return;
/*     */         case 512:
/* 565 */           scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
/* 566 */           return;
/*     */         case 768:
/* 568 */           scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
/* 569 */           return;
/*     */         case 1024:
/* 571 */           scope.problemReporter().localVariableNullInstanceof(local, reference);
/* 572 */           return;
/*     */         }
/* 574 */       else if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
/* 575 */         return;
/*     */       }
/*     */ 
/*     */     case 3:
/* 579 */       if (flowInfo.isDefinitelyNull(local)) {
/* 580 */         scope.problemReporter().localVariableNullReference(local, reference);
/* 581 */         return;
/*     */       }
/* 583 */       if (flowInfo.isPotentiallyNull(local)) {
/* 584 */         scope.problemReporter().localVariablePotentialNullReference(local, reference);
/* 585 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 591 */     if (this.parent != null)
/* 592 */       this.parent.recordUsingNullReference(scope, local, reference, checkType, 
/* 593 */         flowInfo);
/*     */   }
/*     */ 
/*     */   void removeFinalAssignmentIfAny(Reference reference)
/*     */   {
/*     */   }
/*     */ 
/*     */   public SubRoutineStatement subroutine()
/*     */   {
/* 602 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 606 */     StringBuffer buffer = new StringBuffer();
/* 607 */     FlowContext current = this;
/* 608 */     int parentsCount = 0;
/* 609 */     while ((current = current.parent) != null) {
/* 610 */       parentsCount++;
/*     */     }
/* 612 */     FlowContext[] parents = new FlowContext[parentsCount + 1];
/* 613 */     current = this;
/* 614 */     int index = parentsCount;
/* 615 */     while (index >= 0) {
/* 616 */       parents[(index--)] = current;
/* 617 */       current = current.parent;
/*     */     }
/* 619 */     for (int i = 0; i < parentsCount; i++) {
/* 620 */       for (int j = 0; j < i; j++)
/* 621 */         buffer.append('\t');
/* 622 */       buffer.append(parents[i].individualToString()).append('\n');
/*     */     }
/* 624 */     buffer.append('*');
/* 625 */     for (int j = 0; j < parentsCount + 1; j++)
/* 626 */       buffer.append('\t');
/* 627 */     buffer.append(individualToString()).append('\n');
/* 628 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.FlowContext
 * JD-Core Version:    0.6.0
 */