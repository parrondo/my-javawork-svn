/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.NullInfoRegistry;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class TryStatement extends SubRoutineStatement
/*     */ {
/*  22 */   private static final char[] SECRET_RETURN_ADDRESS_NAME = " returnAddress".toCharArray();
/*  23 */   private static final char[] SECRET_ANY_HANDLER_NAME = " anyExceptionHandler".toCharArray();
/*  24 */   private static final char[] SECRET_RETURN_VALUE_NAME = " returnValue".toCharArray();
/*     */   public Block tryBlock;
/*     */   public Block[] catchBlocks;
/*     */   public Argument[] catchArguments;
/*     */   public Block finallyBlock;
/*     */   BlockScope scope;
/*     */   public UnconditionalFlowInfo subRoutineInits;
/*     */   ReferenceBinding[] caughtExceptionTypes;
/*     */   boolean[] catchExits;
/*     */   BranchLabel subRoutineStartLabel;
/*     */   public LocalVariableBinding anyExceptionVariable;
/*     */   public LocalVariableBinding returnAddressVariable;
/*     */   public LocalVariableBinding secretReturnValue;
/*     */   ExceptionLabel[] declaredExceptionLabels;
/*     */   private Object[] reusableJSRTargets;
/*     */   private BranchLabel[] reusableJSRSequenceStartLabels;
/*     */   private int[] reusableJSRStateIndexes;
/*  51 */   private int reusableJSRTargetsCount = 0;
/*     */   private static final int NO_FINALLY = 0;
/*     */   private static final int FINALLY_SUBROUTINE = 1;
/*     */   private static final int FINALLY_DOES_NOT_COMPLETE = 2;
/*     */   private static final int FINALLY_INLINE = 3;
/*  59 */   int mergedInitStateIndex = -1;
/*  60 */   int preTryInitStateIndex = -1;
/*  61 */   int naturalExitMergeInitStateIndex = -1;
/*     */   int[] catchExitInitStateIndexes;
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  73 */     this.preTryInitStateIndex = 
/*  74 */       currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */ 
/*  76 */     if (this.anyExceptionVariable != null) {
/*  77 */       this.anyExceptionVariable.useFlag = 1;
/*     */     }
/*  79 */     if (this.returnAddressVariable != null) {
/*  80 */       this.returnAddressVariable.useFlag = 1;
/*     */     }
/*  82 */     if (this.subRoutineStartLabel == null)
/*     */     {
/*  85 */       ExceptionHandlingFlowContext handlingContext = 
/*  86 */         new ExceptionHandlingFlowContext(
/*  87 */         flowContext, 
/*  88 */         this, 
/*  89 */         this.caughtExceptionTypes, 
/*  90 */         null, 
/*  91 */         this.scope, 
/*  92 */         flowInfo.unconditionalInits());
/*  93 */       handlingContext.initsOnFinally = 
/*  94 */         new NullInfoRegistry(flowInfo.unconditionalInits());
/*     */       FlowInfo tryInfo;
/*     */       FlowInfo tryInfo;
/*  99 */       if (this.tryBlock.isEmptyBlock()) {
/* 100 */         tryInfo = flowInfo;
/*     */       } else {
/* 102 */         tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, flowInfo.copy());
/* 103 */         if ((tryInfo.tagBits & 0x1) != 0) {
/* 104 */           this.bits |= 536870912;
/*     */         }
/*     */       }
/*     */ 
/* 108 */       handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);
/*     */ 
/* 111 */       if (this.catchArguments != null)
/*     */       {
/*     */         int catchCount;
/* 113 */         this.catchExits = new boolean[catchCount = this.catchBlocks.length];
/* 114 */         this.catchExitInitStateIndexes = new int[catchCount];
/* 115 */         for (int i = 0; i < catchCount; i++)
/*     */         {
/*     */           FlowInfo catchInfo;
/* 118 */           if (this.caughtExceptionTypes[i].isUncheckedException(true))
/* 119 */             catchInfo = 
/* 120 */               handlingContext.initsOnFinally.mitigateNullInfoOf(
/* 121 */               flowInfo.unconditionalCopy()
/* 122 */               .addPotentialInitializationsFrom(
/* 123 */               handlingContext.initsOnException(
/* 124 */               this.caughtExceptionTypes[i]))
/* 125 */               .addPotentialInitializationsFrom(tryInfo)
/* 126 */               .addPotentialInitializationsFrom(
/* 127 */               handlingContext.initsOnReturn));
/*     */           else {
/* 129 */             catchInfo = 
/* 130 */               flowInfo.unconditionalCopy()
/* 131 */               .addPotentialInitializationsFrom(
/* 132 */               handlingContext.initsOnException(
/* 133 */               this.caughtExceptionTypes[i]))
/* 134 */               .addPotentialInitializationsFrom(
/* 135 */               tryInfo.nullInfoLessUnconditionalCopy())
/* 138 */               .addPotentialInitializationsFrom(
/* 139 */               handlingContext.initsOnReturn
/* 140 */               .nullInfoLessUnconditionalCopy());
/*     */           }
/*     */ 
/* 144 */           LocalVariableBinding catchArg = this.catchArguments[i].binding;
/* 145 */           catchInfo.markAsDefinitelyAssigned(catchArg);
/* 146 */           catchInfo.markAsDefinitelyNonNull(catchArg);
/*     */ 
/* 154 */           if (this.tryBlock.statements == null) {
/* 155 */             catchInfo.setReachMode(1);
/*     */           }
/* 157 */           FlowInfo catchInfo = 
/* 158 */             this.catchBlocks[i].analyseCode(
/* 159 */             currentScope, 
/* 160 */             flowContext, 
/* 161 */             catchInfo);
/* 162 */           this.catchExitInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(catchInfo);
/* 163 */           this.catchExits[i] = 
/* 164 */             ((catchInfo.tagBits & 0x1) != 0 ? 1 : false);
/* 165 */           tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
/*     */         }
/*     */       }
/* 168 */       this.mergedInitStateIndex = 
/* 169 */         currentScope.methodScope().recordInitializationStates(tryInfo);
/*     */ 
/* 172 */       if (flowContext.initsOnFinally != null) {
/* 173 */         flowContext.initsOnFinally.add(handlingContext.initsOnFinally);
/*     */       }
/*     */ 
/* 176 */       return tryInfo;
/*     */     }
/*     */ 
/* 182 */     InsideSubRoutineFlowContext insideSubContext = new InsideSubRoutineFlowContext(flowContext, this);
/*     */     FinallyFlowContext finallyContext;
/* 184 */     UnconditionalFlowInfo subInfo = 
/* 185 */       this.finallyBlock
/* 186 */       .analyseCode(
/* 187 */       currentScope, 
/* 188 */       finallyContext = new FinallyFlowContext(flowContext, this.finallyBlock), 
/* 189 */       flowInfo.nullInfoLessUnconditionalCopy())
/* 190 */       .unconditionalInits();
/* 191 */     if (subInfo == FlowInfo.DEAD_END) {
/* 192 */       this.bits |= 16384;
/* 193 */       this.scope.problemReporter().finallyMustCompleteNormally(this.finallyBlock);
/*     */     }
/* 195 */     this.subRoutineInits = subInfo;
/*     */ 
/* 197 */     ExceptionHandlingFlowContext handlingContext = 
/* 198 */       new ExceptionHandlingFlowContext(
/* 199 */       insideSubContext, 
/* 200 */       this, 
/* 201 */       this.caughtExceptionTypes, 
/* 202 */       null, 
/* 203 */       this.scope, 
/* 204 */       flowInfo.unconditionalInits());
/* 205 */     handlingContext.initsOnFinally = 
/* 206 */       new NullInfoRegistry(flowInfo.unconditionalInits());
/*     */     FlowInfo tryInfo;
/*     */     FlowInfo tryInfo;
/* 211 */     if (this.tryBlock.isEmptyBlock()) {
/* 212 */       tryInfo = flowInfo;
/*     */     } else {
/* 214 */       tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, flowInfo.copy());
/* 215 */       if ((tryInfo.tagBits & 0x1) != 0) {
/* 216 */         this.bits |= 536870912;
/*     */       }
/*     */     }
/*     */ 
/* 220 */     handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);
/*     */ 
/* 223 */     if (this.catchArguments != null)
/*     */     {
/*     */       int catchCount;
/* 225 */       this.catchExits = new boolean[catchCount = this.catchBlocks.length];
/* 226 */       this.catchExitInitStateIndexes = new int[catchCount];
/* 227 */       for (int i = 0; i < catchCount; i++)
/*     */       {
/*     */         FlowInfo catchInfo;
/* 230 */         if (this.caughtExceptionTypes[i].isUncheckedException(true))
/* 231 */           catchInfo = 
/* 232 */             handlingContext.initsOnFinally.mitigateNullInfoOf(
/* 233 */             flowInfo.unconditionalCopy()
/* 234 */             .addPotentialInitializationsFrom(
/* 235 */             handlingContext.initsOnException(
/* 236 */             this.caughtExceptionTypes[i]))
/* 237 */             .addPotentialInitializationsFrom(tryInfo)
/* 238 */             .addPotentialInitializationsFrom(
/* 239 */             handlingContext.initsOnReturn));
/*     */         else {
/* 241 */           catchInfo = 
/* 242 */             flowInfo.unconditionalCopy()
/* 243 */             .addPotentialInitializationsFrom(
/* 244 */             handlingContext.initsOnException(
/* 245 */             this.caughtExceptionTypes[i]))
/* 246 */             .addPotentialInitializationsFrom(
/* 247 */             tryInfo.nullInfoLessUnconditionalCopy())
/* 250 */             .addPotentialInitializationsFrom(
/* 251 */             handlingContext.initsOnReturn
/* 252 */             .nullInfoLessUnconditionalCopy());
/*     */         }
/*     */ 
/* 256 */         LocalVariableBinding catchArg = this.catchArguments[i].binding;
/* 257 */         catchInfo.markAsDefinitelyAssigned(catchArg);
/* 258 */         catchInfo.markAsDefinitelyNonNull(catchArg);
/*     */ 
/* 266 */         if (this.tryBlock.statements == null) {
/* 267 */           catchInfo.setReachMode(1);
/*     */         }
/* 269 */         FlowInfo catchInfo = 
/* 270 */           this.catchBlocks[i].analyseCode(
/* 271 */           currentScope, 
/* 272 */           insideSubContext, 
/* 273 */           catchInfo);
/* 274 */         this.catchExitInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(catchInfo);
/* 275 */         this.catchExits[i] = 
/* 276 */           ((catchInfo.tagBits & 0x1) != 0 ? 1 : false);
/* 277 */         tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 282 */     finallyContext.complainOnDeferredChecks(
/* 283 */       handlingContext.initsOnFinally.mitigateNullInfoOf(
/* 284 */       (tryInfo.tagBits & 0x1) == 0 ? 
/* 285 */       flowInfo.unconditionalCopy()
/* 286 */       .addPotentialInitializationsFrom(tryInfo)
/* 289 */       .addPotentialInitializationsFrom(insideSubContext.initsOnReturn) : 
/* 290 */       insideSubContext.initsOnReturn), 
/* 291 */       currentScope);
/*     */ 
/* 294 */     if (flowContext.initsOnFinally != null) {
/* 295 */       flowContext.initsOnFinally.add(handlingContext.initsOnFinally);
/*     */     }
/*     */ 
/* 298 */     this.naturalExitMergeInitStateIndex = 
/* 299 */       currentScope.methodScope().recordInitializationStates(tryInfo);
/* 300 */     if (subInfo == FlowInfo.DEAD_END) {
/* 301 */       this.mergedInitStateIndex = 
/* 302 */         currentScope.methodScope().recordInitializationStates(subInfo);
/* 303 */       return subInfo;
/*     */     }
/* 305 */     FlowInfo mergedInfo = tryInfo.addInitializationsFrom(subInfo);
/* 306 */     this.mergedInitStateIndex = 
/* 307 */       currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 308 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public ExceptionLabel enterAnyExceptionHandler(CodeStream codeStream)
/*     */   {
/* 314 */     if (this.subRoutineStartLabel == null)
/* 315 */       return null;
/* 316 */     return super.enterAnyExceptionHandler(codeStream);
/*     */   }
/*     */ 
/*     */   public void enterDeclaredExceptionHandlers(CodeStream codeStream) {
/* 320 */     int i = 0; for (int length = this.declaredExceptionLabels == null ? 0 : this.declaredExceptionLabels.length; i < length; i++)
/* 321 */       this.declaredExceptionLabels[i].placeStart();
/*     */   }
/*     */ 
/*     */   public void exitAnyExceptionHandler()
/*     */   {
/* 326 */     if (this.subRoutineStartLabel == null)
/* 327 */       return;
/* 328 */     super.exitAnyExceptionHandler();
/*     */   }
/*     */ 
/*     */   public void exitDeclaredExceptionHandlers(CodeStream codeStream) {
/* 332 */     int i = 0; for (int length = this.declaredExceptionLabels == null ? 0 : this.declaredExceptionLabels.length; i < length; i++)
/* 333 */       this.declaredExceptionLabels[i].placeEnd();
/*     */   }
/*     */ 
/*     */   private int finallyMode()
/*     */   {
/* 338 */     if (this.subRoutineStartLabel == null)
/* 339 */       return 0;
/* 340 */     if (isSubRoutineEscaping())
/* 341 */       return 2;
/* 342 */     if (this.scope.compilerOptions().inlineJsrBytecode) {
/* 343 */       return 3;
/*     */     }
/* 345 */     return 1;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 354 */     if ((this.bits & 0x80000000) == 0) {
/* 355 */       return;
/*     */     }
/* 357 */     boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
/*     */ 
/* 360 */     this.anyExceptionLabel = null;
/* 361 */     this.reusableJSRTargets = null;
/* 362 */     this.reusableJSRSequenceStartLabels = null;
/* 363 */     this.reusableJSRTargetsCount = 0;
/*     */ 
/* 365 */     int pc = codeStream.position;
/* 366 */     int finallyMode = finallyMode();
/*     */ 
/* 368 */     boolean requiresNaturalExit = false;
/*     */ 
/* 370 */     int maxCatches = this.catchArguments == null ? 0 : this.catchArguments.length;
/*     */     ExceptionLabel[] exceptionLabels;
/* 372 */     if (maxCatches > 0) {
/* 373 */       ExceptionLabel[] exceptionLabels = new ExceptionLabel[maxCatches];
/* 374 */       for (i = 0; i < maxCatches; i++) {
/* 375 */         ExceptionLabel exceptionLabel = new ExceptionLabel(codeStream, this.catchArguments[i].binding.type);
/* 376 */         exceptionLabel.placeStart();
/* 377 */         exceptionLabels[i] = exceptionLabel;
/*     */       }
/*     */     } else {
/* 380 */       exceptionLabels = (ExceptionLabel[])null;
/*     */     }
/* 382 */     if (this.subRoutineStartLabel != null) {
/* 383 */       this.subRoutineStartLabel.initialize(codeStream);
/* 384 */       enterAnyExceptionHandler(codeStream);
/*     */     }
/*     */     try
/*     */     {
/* 388 */       this.declaredExceptionLabels = exceptionLabels;
/* 389 */       this.tryBlock.generateCode(this.scope, codeStream);
/*     */     } finally {
/* 391 */       this.declaredExceptionLabels = null;
/*     */     }
/* 393 */     boolean tryBlockHasSomeCode = codeStream.position != pc;
/*     */ 
/* 397 */     if (tryBlockHasSomeCode)
/*     */     {
/* 399 */       BranchLabel naturalExitLabel = new BranchLabel(codeStream);
/* 400 */       BranchLabel postCatchesFinallyLabel = null;
/* 401 */       for (int i = 0; i < maxCatches; i++) {
/* 402 */         exceptionLabels[i].placeEnd();
/*     */       }
/* 404 */       if ((this.bits & 0x20000000) == 0) {
/* 405 */         int position = codeStream.position;
/* 406 */         switch (finallyMode) {
/*     */         case 1:
/*     */         case 3:
/* 409 */           requiresNaturalExit = true;
/* 410 */           if (this.naturalExitMergeInitStateIndex != -1) {
/* 411 */             codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/* 412 */             codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/*     */           }
/* 414 */           codeStream.goto_(naturalExitLabel);
/* 415 */           break;
/*     */         case 0:
/* 417 */           if (this.naturalExitMergeInitStateIndex != -1) {
/* 418 */             codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/* 419 */             codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/*     */           }
/* 421 */           codeStream.goto_(naturalExitLabel);
/* 422 */           break;
/*     */         case 2:
/* 424 */           codeStream.goto_(this.subRoutineStartLabel);
/*     */         }
/*     */ 
/* 427 */         codeStream.updateLastRecordedEndPC(this.tryBlock.scope, position);
/*     */       }
/*     */ 
/* 434 */       exitAnyExceptionHandler();
/* 435 */       if (this.catchArguments != null) {
/* 436 */         postCatchesFinallyLabel = new BranchLabel(codeStream);
/*     */ 
/* 438 */         for (int i = 0; i < maxCatches; i++)
/*     */         {
/* 444 */           if (exceptionLabels[i].count != 0) {
/* 445 */             enterAnyExceptionHandler(codeStream);
/*     */ 
/* 447 */             if (this.preTryInitStateIndex != -1) {
/* 448 */               codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
/* 449 */               codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
/*     */             }
/* 451 */             codeStream.pushExceptionOnStack(exceptionLabels[i].exceptionType);
/* 452 */             exceptionLabels[i].place();
/*     */ 
/* 455 */             int varPC = codeStream.position;
/*     */             LocalVariableBinding catchVar;
/* 456 */             if ((catchVar = this.catchArguments[i].binding).resolvedPosition != -1) {
/* 457 */               codeStream.store(catchVar, false);
/* 458 */               catchVar.recordInitializationStartPC(codeStream.position);
/* 459 */               codeStream.addVisibleLocalVariable(catchVar);
/*     */             } else {
/* 461 */               codeStream.pop();
/*     */             }
/* 463 */             codeStream.recordPositionsFrom(varPC, this.catchArguments[i].sourceStart);
/*     */ 
/* 466 */             this.catchBlocks[i].generateCode(this.scope, codeStream);
/* 467 */             exitAnyExceptionHandler();
/* 468 */             if (this.catchExits[i] == 0) {
/* 469 */               switch (finallyMode)
/*     */               {
/*     */               case 3:
/* 472 */                 if (isStackMapFrameCodeStream) {
/* 473 */                   ((StackMapFrameCodeStream)codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
/*     */                 }
/* 475 */                 if (this.catchExitInitStateIndexes[i] != -1) {
/* 476 */                   codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[i]);
/* 477 */                   codeStream.addDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[i]);
/*     */                 }
/*     */ 
/* 480 */                 this.finallyBlock.generateCode(this.scope, codeStream);
/* 481 */                 codeStream.goto_(postCatchesFinallyLabel);
/* 482 */                 if (!isStackMapFrameCodeStream) continue;
/* 483 */                 ((StackMapFrameCodeStream)codeStream).popStateIndex();
/*     */ 
/* 485 */                 break;
/*     */               case 1:
/* 487 */                 requiresNaturalExit = true;
/*     */               case 0:
/* 490 */                 if (this.naturalExitMergeInitStateIndex != -1) {
/* 491 */                   codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/* 492 */                   codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/*     */                 }
/* 494 */                 codeStream.goto_(naturalExitLabel);
/* 495 */                 break;
/*     */               case 2:
/* 497 */                 codeStream.goto_(this.subRoutineStartLabel);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 504 */       ExceptionLabel naturalExitExceptionHandler = (requiresNaturalExit) && (finallyMode == 1) ? 
/* 505 */         new ExceptionLabel(codeStream, null) : 
/* 506 */         null;
/*     */ 
/* 510 */       int finallySequenceStartPC = codeStream.position;
/* 511 */       if ((this.subRoutineStartLabel != null) && (this.anyExceptionLabel.count != 0)) {
/* 512 */         codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
/* 513 */         if (this.preTryInitStateIndex != -1)
/*     */         {
/* 515 */           codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
/* 516 */           codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
/*     */         }
/* 518 */         placeAllAnyExceptionHandler();
/* 519 */         if (naturalExitExceptionHandler != null) naturalExitExceptionHandler.place();
/*     */ 
/* 521 */         switch (finallyMode)
/*     */         {
/*     */         case 1:
/* 524 */           codeStream.store(this.anyExceptionVariable, false);
/* 525 */           codeStream.jsr(this.subRoutineStartLabel);
/* 526 */           codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
/* 527 */           int position = codeStream.position;
/* 528 */           codeStream.throwAnyException(this.anyExceptionVariable);
/* 529 */           codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
/*     */ 
/* 531 */           this.subRoutineStartLabel.place();
/* 532 */           codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
/* 533 */           position = codeStream.position;
/* 534 */           codeStream.store(this.returnAddressVariable, false);
/* 535 */           codeStream.recordPositionsFrom(position, this.finallyBlock.sourceStart);
/* 536 */           this.finallyBlock.generateCode(this.scope, codeStream);
/* 537 */           position = codeStream.position;
/* 538 */           codeStream.ret(this.returnAddressVariable.resolvedPosition);
/* 539 */           codeStream.recordPositionsFrom(
/* 540 */             position, 
/* 541 */             this.finallyBlock.sourceEnd);
/*     */ 
/* 543 */           break;
/*     */         case 3:
/* 546 */           codeStream.store(this.anyExceptionVariable, false);
/* 547 */           codeStream.addVariable(this.anyExceptionVariable);
/* 548 */           codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
/*     */ 
/* 550 */           this.finallyBlock.generateCode(currentScope, codeStream);
/* 551 */           int position = codeStream.position;
/* 552 */           codeStream.throwAnyException(this.anyExceptionVariable);
/* 553 */           codeStream.removeVariable(this.anyExceptionVariable);
/* 554 */           if (this.preTryInitStateIndex != -1) {
/* 555 */             codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
/*     */           }
/* 557 */           this.subRoutineStartLabel.place();
/* 558 */           codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
/* 559 */           break;
/*     */         case 2:
/* 562 */           codeStream.pop();
/* 563 */           this.subRoutineStartLabel.place();
/* 564 */           codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
/*     */ 
/* 566 */           this.finallyBlock.generateCode(this.scope, codeStream);
/*     */         }
/*     */ 
/* 571 */         if (requiresNaturalExit) {
/* 572 */           switch (finallyMode) {
/*     */           case 1:
/* 574 */             naturalExitLabel.place();
/* 575 */             int position = codeStream.position;
/* 576 */             naturalExitExceptionHandler.placeStart();
/* 577 */             codeStream.jsr(this.subRoutineStartLabel);
/* 578 */             naturalExitExceptionHandler.placeEnd();
/* 579 */             codeStream.recordPositionsFrom(
/* 580 */               position, 
/* 581 */               this.finallyBlock.sourceEnd);
/* 582 */             break;
/*     */           case 3:
/* 585 */             if (isStackMapFrameCodeStream) {
/* 586 */               ((StackMapFrameCodeStream)codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
/*     */             }
/* 588 */             if (this.naturalExitMergeInitStateIndex != -1) {
/* 589 */               codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/* 590 */               codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/*     */             }
/* 592 */             naturalExitLabel.place();
/*     */ 
/* 594 */             this.finallyBlock.generateCode(this.scope, codeStream);
/* 595 */             if (postCatchesFinallyLabel != null) {
/* 596 */               int position = codeStream.position;
/*     */ 
/* 598 */               codeStream.goto_(postCatchesFinallyLabel);
/* 599 */               codeStream.recordPositionsFrom(
/* 600 */                 position, 
/* 601 */                 this.finallyBlock.sourceEnd);
/*     */             }
/* 603 */             if (!isStackMapFrameCodeStream) break;
/* 604 */             ((StackMapFrameCodeStream)codeStream).popStateIndex();
/*     */ 
/* 606 */             break;
/*     */           case 2:
/* 608 */             break;
/*     */           default:
/* 610 */             naturalExitLabel.place();
/*     */           }
/*     */         }
/*     */ 
/* 614 */         if (postCatchesFinallyLabel != null)
/* 615 */           postCatchesFinallyLabel.place();
/*     */       }
/*     */       else
/*     */       {
/* 619 */         naturalExitLabel.place();
/*     */       }
/*     */ 
/*     */     }
/* 623 */     else if (this.subRoutineStartLabel != null) {
/* 624 */       this.finallyBlock.generateCode(this.scope, codeStream);
/*     */     }
/*     */ 
/* 628 */     if (this.mergedInitStateIndex != -1) {
/* 629 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 630 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/* 632 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public boolean generateSubRoutineInvocation(BlockScope currentScope, CodeStream codeStream, Object targetLocation, int stateIndex, LocalVariableBinding secretLocal)
/*     */   {
/* 640 */     boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
/* 641 */     int finallyMode = finallyMode();
/* 642 */     switch (finallyMode) {
/*     */     case 2:
/* 644 */       codeStream.goto_(this.subRoutineStartLabel);
/* 645 */       return true;
/*     */     case 0:
/* 648 */       exitDeclaredExceptionHandlers(codeStream);
/* 649 */       return false;
/*     */     case 1:
/*     */     }
/* 652 */     if (targetLocation != null) {
/* 653 */       boolean reuseTargetLocation = true;
/* 654 */       if (this.reusableJSRTargetsCount > 0) {
/* 655 */         int i = 0; for (int count = this.reusableJSRTargetsCount; i < count; i++) {
/* 656 */           Object reusableJSRTarget = this.reusableJSRTargets[i];
/*     */ 
/* 658 */           if ((targetLocation != reusableJSRTarget) && (
/* 660 */             (!(targetLocation instanceof Constant)) || 
/* 661 */             (!(reusableJSRTarget instanceof Constant)) || 
/* 662 */             (!((Constant)targetLocation).hasSameValue((Constant)reusableJSRTarget))))
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 669 */           if ((this.reusableJSRStateIndexes[i] != stateIndex) && (finallyMode == 3)) {
/* 670 */             reuseTargetLocation = false;
/* 671 */             break;
/*     */           }
/* 673 */           codeStream.goto_(this.reusableJSRSequenceStartLabels[i]);
/* 674 */           return true;
/*     */         }
/*     */       }
/*     */       else {
/* 678 */         this.reusableJSRTargets = new Object[3];
/* 679 */         this.reusableJSRSequenceStartLabels = new BranchLabel[3];
/* 680 */         this.reusableJSRStateIndexes = new int[3];
/*     */       }
/* 682 */       if (reuseTargetLocation) {
/* 683 */         if (this.reusableJSRTargetsCount == this.reusableJSRTargets.length) {
/* 684 */           System.arraycopy(this.reusableJSRTargets, 0, this.reusableJSRTargets = new Object[2 * this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
/* 685 */           System.arraycopy(this.reusableJSRSequenceStartLabels, 0, this.reusableJSRSequenceStartLabels = new BranchLabel[2 * this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
/* 686 */           System.arraycopy(this.reusableJSRStateIndexes, 0, this.reusableJSRStateIndexes = new int[2 * this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
/*     */         }
/* 688 */         this.reusableJSRTargets[this.reusableJSRTargetsCount] = targetLocation;
/* 689 */         BranchLabel reusableJSRSequenceStartLabel = new BranchLabel(codeStream);
/* 690 */         reusableJSRSequenceStartLabel.place();
/* 691 */         this.reusableJSRStateIndexes[this.reusableJSRTargetsCount] = stateIndex;
/* 692 */         this.reusableJSRSequenceStartLabels[(this.reusableJSRTargetsCount++)] = reusableJSRSequenceStartLabel;
/*     */       }
/*     */     }
/* 695 */     if (finallyMode == 3) {
/* 696 */       if (isStackMapFrameCodeStream) {
/* 697 */         ((StackMapFrameCodeStream)codeStream).pushStateIndex(stateIndex);
/* 698 */         if ((this.naturalExitMergeInitStateIndex != -1) || (stateIndex != -1))
/*     */         {
/* 700 */           codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/* 701 */           codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/*     */         }
/*     */       }
/* 704 */       else if (this.naturalExitMergeInitStateIndex != -1)
/*     */       {
/* 706 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/* 707 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
/*     */       }
/*     */ 
/* 710 */       if (secretLocal != null) {
/* 711 */         codeStream.addVariable(secretLocal);
/*     */       }
/*     */ 
/* 715 */       exitAnyExceptionHandler();
/* 716 */       exitDeclaredExceptionHandlers(codeStream);
/* 717 */       this.finallyBlock.generateCode(currentScope, codeStream);
/* 718 */       if (isStackMapFrameCodeStream)
/* 719 */         ((StackMapFrameCodeStream)codeStream).popStateIndex();
/*     */     }
/*     */     else
/*     */     {
/* 723 */       codeStream.jsr(this.subRoutineStartLabel);
/* 724 */       exitAnyExceptionHandler();
/* 725 */       exitDeclaredExceptionHandlers(codeStream);
/*     */     }
/* 727 */     return false;
/*     */   }
/*     */   public boolean isSubRoutineEscaping() {
/* 730 */     return (this.bits & 0x4000) != 0;
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output) {
/* 734 */     printIndent(indent, output).append("try \n");
/* 735 */     this.tryBlock.printStatement(indent + 1, output);
/*     */ 
/* 738 */     if (this.catchBlocks != null) {
/* 739 */       for (int i = 0; i < this.catchBlocks.length; i++) {
/* 740 */         output.append('\n');
/* 741 */         printIndent(indent, output).append("catch (");
/* 742 */         this.catchArguments[i].print(0, output).append(") ");
/* 743 */         this.catchBlocks[i].printStatement(indent + 1, output);
/*     */       }
/*     */     }
/* 746 */     if (this.finallyBlock != null) {
/* 747 */       output.append('\n');
/* 748 */       printIndent(indent, output).append("finally\n");
/* 749 */       this.finallyBlock.printStatement(indent + 1, output);
/*     */     }
/* 751 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope upperScope)
/*     */   {
/* 756 */     this.scope = new BlockScope(upperScope);
/*     */ 
/* 758 */     BlockScope tryScope = new BlockScope(this.scope);
/* 759 */     BlockScope finallyScope = null;
/*     */ 
/* 761 */     if (this.finallyBlock != null) {
/* 762 */       if (this.finallyBlock.isEmptyBlock()) {
/* 763 */         if ((this.finallyBlock.bits & 0x8) != 0)
/* 764 */           this.scope.problemReporter().undocumentedEmptyBlock(this.finallyBlock.sourceStart, this.finallyBlock.sourceEnd);
/*     */       }
/*     */       else {
/* 767 */         finallyScope = new BlockScope(this.scope, false);
/*     */ 
/* 770 */         MethodScope methodScope = this.scope.methodScope();
/*     */ 
/* 773 */         if (!upperScope.compilerOptions().inlineJsrBytecode) {
/* 774 */           this.returnAddressVariable = 
/* 775 */             new LocalVariableBinding(SECRET_RETURN_ADDRESS_NAME, upperScope.getJavaLangObject(), 0, false);
/* 776 */           finallyScope.addLocalVariable(this.returnAddressVariable);
/* 777 */           this.returnAddressVariable.setConstant(Constant.NotAConstant);
/*     */         }
/* 779 */         this.subRoutineStartLabel = new BranchLabel();
/*     */ 
/* 781 */         this.anyExceptionVariable = 
/* 782 */           new LocalVariableBinding(SECRET_ANY_HANDLER_NAME, this.scope.getJavaLangThrowable(), 0, false);
/* 783 */         finallyScope.addLocalVariable(this.anyExceptionVariable);
/* 784 */         this.anyExceptionVariable.setConstant(Constant.NotAConstant);
/*     */ 
/* 786 */         if (!methodScope.isInsideInitializer()) {
/* 787 */           MethodBinding methodBinding = 
/* 788 */             ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
/* 789 */           if (methodBinding != null) {
/* 790 */             TypeBinding methodReturnType = methodBinding.returnType;
/* 791 */             if (methodReturnType.id != 6) {
/* 792 */               this.secretReturnValue = 
/* 793 */                 new LocalVariableBinding(
/* 794 */                 SECRET_RETURN_VALUE_NAME, 
/* 795 */                 methodReturnType, 
/* 796 */                 0, 
/* 797 */                 false);
/* 798 */               finallyScope.addLocalVariable(this.secretReturnValue);
/* 799 */               this.secretReturnValue.setConstant(Constant.NotAConstant);
/*     */             }
/*     */           }
/*     */         }
/* 803 */         this.finallyBlock.resolveUsing(finallyScope);
/*     */ 
/* 805 */         finallyScope.shiftScopes = new BlockScope[this.catchArguments == null ? 1 : this.catchArguments.length + 1];
/* 806 */         finallyScope.shiftScopes[0] = tryScope;
/*     */       }
/*     */     }
/* 809 */     this.tryBlock.resolveUsing(tryScope);
/*     */ 
/* 812 */     if (this.catchBlocks != null) {
/* 813 */       int length = this.catchArguments.length;
/* 814 */       TypeBinding[] argumentTypes = new TypeBinding[length];
/* 815 */       boolean catchHasError = false;
/* 816 */       for (int i = 0; i < length; i++) {
/* 817 */         BlockScope catchScope = new BlockScope(this.scope);
/* 818 */         if (finallyScope != null) {
/* 819 */           finallyScope.shiftScopes[(i + 1)] = catchScope;
/*     */         }
/*     */ 
/* 822 */         if ((argumentTypes[i] =  = this.catchArguments[i].resolveForCatch(catchScope)) == null) {
/* 823 */           catchHasError = true;
/*     */         }
/* 825 */         this.catchBlocks[i].resolveUsing(catchScope);
/*     */       }
/* 827 */       if (catchHasError) {
/* 828 */         return;
/*     */       }
/*     */ 
/* 832 */       this.caughtExceptionTypes = new ReferenceBinding[length];
/* 833 */       for (int i = 0; i < length; i++) {
/* 834 */         this.caughtExceptionTypes[i] = ((ReferenceBinding)argumentTypes[i]);
/* 835 */         for (int j = 0; j < i; j++)
/* 836 */           if (this.caughtExceptionTypes[i].isCompatibleWith(argumentTypes[j]))
/* 837 */             this.scope.problemReporter().wrongSequenceOfExceptionTypesError(this, this.caughtExceptionTypes[i], i, argumentTypes[j]);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 842 */       this.caughtExceptionTypes = new ReferenceBinding[0];
/*     */     }
/*     */ 
/* 845 */     if (finallyScope != null)
/*     */     {
/* 849 */       this.scope.addSubscope(finallyScope);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 854 */     if (visitor.visit(this, blockScope)) {
/* 855 */       this.tryBlock.traverse(visitor, this.scope);
/* 856 */       if (this.catchArguments != null) {
/* 857 */         int i = 0; for (int max = this.catchBlocks.length; i < max; i++) {
/* 858 */           this.catchArguments[i].traverse(visitor, this.scope);
/* 859 */           this.catchBlocks[i].traverse(visitor, this.scope);
/*     */         }
/*     */       }
/* 862 */       if (this.finallyBlock != null)
/* 863 */         this.finallyBlock.traverse(visitor, this.scope);
/*     */     }
/* 865 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.TryStatement
 * JD-Core Version:    0.6.0
 */