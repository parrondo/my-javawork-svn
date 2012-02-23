/*     */ package org.eclipse.jdt.internal.compiler.flow;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Reference;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class LoopingFlowContext extends SwitchFlowContext
/*     */ {
/*     */   public BranchLabel continueLabel;
/*  30 */   public UnconditionalFlowInfo initsOnContinue = FlowInfo.DEAD_END;
/*     */   private UnconditionalFlowInfo upstreamNullFlowInfo;
/*  32 */   private LoopingFlowContext[] innerFlowContexts = null;
/*  33 */   private UnconditionalFlowInfo[] innerFlowInfos = null;
/*  34 */   private int innerFlowContextsCount = 0;
/*  35 */   private LabelFlowContext[] breakTargetContexts = null;
/*  36 */   private int breakTargetsCount = 0;
/*     */   Reference[] finalAssignments;
/*     */   VariableBinding[] finalVariables;
/*  40 */   int assignCount = 0;
/*     */   LocalVariableBinding[] nullLocals;
/*     */   Expression[] nullReferences;
/*     */   int[] nullCheckTypes;
/*     */   int nullCount;
/*     */   Scope associatedScope;
/*     */ 
/*     */   public LoopingFlowContext(FlowContext parent, FlowInfo upstreamNullFlowInfo, ASTNode associatedNode, BranchLabel breakLabel, BranchLabel continueLabel, Scope associatedScope)
/*     */   {
/*  56 */     super(parent, associatedNode, breakLabel);
/*  57 */     this.preemptNullDiagnostic = true;
/*     */ 
/*  59 */     this.continueLabel = continueLabel;
/*  60 */     this.associatedScope = associatedScope;
/*  61 */     this.upstreamNullFlowInfo = upstreamNullFlowInfo.unconditionalCopy();
/*     */   }
/*     */ 
/*     */   public void complainOnDeferredFinalChecks(BlockScope scope, FlowInfo flowInfo)
/*     */   {
/*  72 */     for (int i = 0; i < this.assignCount; i++) {
/*  73 */       VariableBinding variable = this.finalVariables[i];
/*  74 */       if (variable != null) {
/*  75 */         boolean complained = false;
/*  76 */         if ((variable instanceof FieldBinding)) {
/*  77 */           if (flowInfo.isPotentiallyAssigned((FieldBinding)variable)) {
/*  78 */             complained = true;
/*  79 */             scope.problemReporter().duplicateInitializationOfBlankFinalField(
/*  80 */               (FieldBinding)variable, 
/*  81 */               this.finalAssignments[i]);
/*     */           }
/*     */         }
/*  84 */         else if (flowInfo.isPotentiallyAssigned((LocalVariableBinding)variable)) {
/*  85 */           complained = true;
/*  86 */           scope.problemReporter().duplicateInitializationOfFinalLocal(
/*  87 */             (LocalVariableBinding)variable, 
/*  88 */             this.finalAssignments[i]);
/*     */         }
/*     */ 
/*  93 */         if (complained) {
/*  94 */           FlowContext context = this.parent;
/*  95 */           while (context != null) {
/*  96 */             context.removeFinalAssignmentIfAny(this.finalAssignments[i]);
/*  97 */             context = context.parent;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void complainOnDeferredNullChecks(BlockScope scope, FlowInfo callerFlowInfo)
/*     */   {
/* 109 */     for (int i = 0; i < this.innerFlowContextsCount; i++) {
/* 110 */       this.upstreamNullFlowInfo
/* 111 */         .addPotentialNullInfoFrom(
/* 112 */         this.innerFlowContexts[i].upstreamNullFlowInfo)
/* 113 */         .addPotentialNullInfoFrom(this.innerFlowInfos[i]);
/*     */     }
/* 115 */     this.innerFlowContextsCount = 0;
/* 116 */     UnconditionalFlowInfo flowInfo = this.upstreamNullFlowInfo
/* 117 */       .addPotentialNullInfoFrom(callerFlowInfo.unconditionalInitsWithoutSideEffect());
/* 118 */     if (this.deferNullDiagnostic)
/*     */     {
/* 120 */       for (int i = 0; i < this.nullCount; i++) {
/* 121 */         LocalVariableBinding local = this.nullLocals[i];
/* 122 */         Expression expression = this.nullReferences[i];
/*     */ 
/* 124 */         switch (this.nullCheckTypes[i]) {
/*     */         case 258:
/*     */         case 514:
/* 127 */           if (!flowInfo.isDefinitelyNonNull(local)) break;
/* 128 */           this.nullReferences[i] = null;
/* 129 */           if (this.nullCheckTypes[i] == 514) {
/* 130 */             scope.problemReporter().localVariableRedundantCheckOnNonNull(local, expression); continue;
/*     */           }
/* 132 */           scope.problemReporter().localVariableNonNullComparedToNull(local, expression);
/*     */ 
/* 134 */           break;
/*     */         case 256:
/*     */         case 512:
/* 139 */           if (flowInfo.isDefinitelyNonNull(local)) {
/* 140 */             this.nullReferences[i] = null;
/* 141 */             if (this.nullCheckTypes[i] == 512) {
/* 142 */               scope.problemReporter().localVariableRedundantCheckOnNonNull(local, expression); continue;
/*     */             }
/* 144 */             scope.problemReporter().localVariableNonNullComparedToNull(local, expression);
/*     */ 
/* 146 */             continue;
/*     */           }
/* 148 */           if (!flowInfo.isDefinitelyNull(local)) break;
/* 149 */           this.nullReferences[i] = null;
/* 150 */           if (this.nullCheckTypes[i] == 256) {
/* 151 */             scope.problemReporter().localVariableRedundantCheckOnNull(local, expression); continue;
/*     */           }
/* 153 */           scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
/*     */ 
/* 155 */           break;
/*     */         case 257:
/*     */         case 513:
/*     */         case 769:
/*     */         case 1025:
/* 162 */           if (!flowInfo.isDefinitelyNull(local)) break;
/* 163 */           this.nullReferences[i] = null;
/* 164 */           switch (this.nullCheckTypes[i] & 0xFFFFFF00) {
/*     */           case 256:
/* 166 */             scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
/* 167 */             break;
/*     */           case 512:
/* 169 */             scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
/* 170 */             break;
/*     */           case 768:
/* 172 */             scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
/* 173 */             break;
/*     */           case 1024:
/* 175 */             scope.problemReporter().localVariableNullInstanceof(local, expression);
/* 176 */             continue;
/*     */           }
/*     */ 
/* 179 */           break;
/*     */         case 3:
/* 181 */           if (!flowInfo.isDefinitelyNull(local)) break;
/* 182 */           this.nullReferences[i] = null;
/* 183 */           scope.problemReporter().localVariableNullReference(local, expression);
/* 184 */           break;
/*     */         }
/*     */ 
/* 190 */         this.parent.recordUsingNullReference(scope, local, expression, 
/* 191 */           this.nullCheckTypes[i], flowInfo);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 196 */       for (int i = 0; i < this.nullCount; i++) {
/* 197 */         Expression expression = this.nullReferences[i];
/*     */ 
/* 199 */         LocalVariableBinding local = this.nullLocals[i];
/* 200 */         switch (this.nullCheckTypes[i]) {
/*     */         case 256:
/*     */         case 512:
/* 203 */           if (!flowInfo.isDefinitelyNonNull(local)) break;
/* 204 */           this.nullReferences[i] = null;
/* 205 */           if (this.nullCheckTypes[i] == 512)
/* 206 */             scope.problemReporter().localVariableRedundantCheckOnNonNull(local, expression);
/*     */           else {
/* 208 */             scope.problemReporter().localVariableNonNullComparedToNull(local, expression);
/*     */           }
/* 210 */           break;
/*     */         case 257:
/*     */         case 513:
/*     */         case 769:
/*     */         case 1025:
/* 217 */           if (flowInfo.isDefinitelyNull(local)) {
/* 218 */             this.nullReferences[i] = null;
/* 219 */             switch (this.nullCheckTypes[i] & 0xFFFFFF00) {
/*     */             case 256:
/* 221 */               scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
/* 222 */               break;
/*     */             case 512:
/* 224 */               scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
/* 225 */               break;
/*     */             case 768:
/* 227 */               scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
/* 228 */               break;
/*     */             case 1024:
/* 230 */               scope.problemReporter().localVariableNullInstanceof(local, expression);
/*     */             }
/*     */           }
/*     */ 
/* 234 */           break;
/*     */         case 3:
/* 236 */           if (flowInfo.isDefinitelyNull(local)) {
/* 237 */             this.nullReferences[i] = null;
/* 238 */             scope.problemReporter().localVariableNullReference(local, expression);
/*     */           }
/* 241 */           else if (flowInfo.isPotentiallyNull(local)) {
/* 242 */             this.nullReferences[i] = null;
/* 243 */             scope.problemReporter().localVariablePotentialNullReference(local, expression);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 253 */     this.initsOnBreak.addPotentialNullInfoFrom(flowInfo);
/* 254 */     for (int i = 0; i < this.breakTargetsCount; i++)
/* 255 */       this.breakTargetContexts[i].initsOnBreak.addPotentialNullInfoFrom(flowInfo);
/*     */   }
/*     */ 
/*     */   public BranchLabel continueLabel()
/*     */   {
/* 260 */     return this.continueLabel;
/*     */   }
/*     */ 
/*     */   public String individualToString() {
/* 264 */     StringBuffer buffer = new StringBuffer("Looping flow context");
/* 265 */     buffer.append("[initsOnBreak - ").append(this.initsOnBreak.toString()).append(']');
/* 266 */     buffer.append("[initsOnContinue - ").append(this.initsOnContinue.toString()).append(']');
/* 267 */     buffer.append("[finalAssignments count - ").append(this.assignCount).append(']');
/* 268 */     buffer.append("[nullReferences count - ").append(this.nullCount).append(']');
/* 269 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean isContinuable() {
/* 273 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isContinuedTo() {
/* 277 */     return this.initsOnContinue != FlowInfo.DEAD_END;
/*     */   }
/*     */ 
/*     */   public void recordBreakTo(FlowContext targetContext) {
/* 281 */     if ((targetContext instanceof LabelFlowContext))
/*     */     {
/*     */       int current;
/* 283 */       if ((current = this.breakTargetsCount++) == 0)
/* 284 */         this.breakTargetContexts = new LabelFlowContext[2];
/* 285 */       else if (current == this.breakTargetContexts.length) {
/* 286 */         System.arraycopy(this.breakTargetContexts, 0, this.breakTargetContexts = new LabelFlowContext[current + 2], 0, current);
/*     */       }
/* 288 */       this.breakTargetContexts[current] = ((LabelFlowContext)targetContext);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void recordContinueFrom(FlowContext innerFlowContext, FlowInfo flowInfo) {
/* 293 */     if ((flowInfo.tagBits & 0x1) == 0) {
/* 294 */       if ((this.initsOnContinue.tagBits & 0x1) == 0) {
/* 295 */         this.initsOnContinue = this.initsOnContinue
/* 296 */           .mergedWith(flowInfo.unconditionalInitsWithoutSideEffect());
/*     */       }
/*     */       else {
/* 299 */         this.initsOnContinue = flowInfo.unconditionalCopy();
/*     */       }
/* 301 */       FlowContext inner = innerFlowContext;
/* 302 */       while ((inner != this) && (!(inner instanceof LoopingFlowContext))) {
/* 303 */         inner = inner.parent;
/*     */       }
/* 305 */       if (inner == this) {
/* 306 */         this.upstreamNullFlowInfo
/* 307 */           .addPotentialNullInfoFrom(
/* 308 */           flowInfo.unconditionalInitsWithoutSideEffect());
/*     */       }
/*     */       else {
/* 311 */         int length = 0;
/* 312 */         if (this.innerFlowContexts == null) {
/* 313 */           this.innerFlowContexts = new LoopingFlowContext[5];
/* 314 */           this.innerFlowInfos = new UnconditionalFlowInfo[5];
/*     */         }
/* 316 */         else if (this.innerFlowContextsCount == 
/* 317 */           (length = this.innerFlowContexts.length) - 1) {
/* 318 */           System.arraycopy(this.innerFlowContexts, 0, 
/* 319 */             this.innerFlowContexts = new LoopingFlowContext[length + 5], 
/* 320 */             0, length);
/* 321 */           System.arraycopy(this.innerFlowInfos, 0, 
/* 322 */             this.innerFlowInfos = new UnconditionalFlowInfo[length + 5], 
/* 323 */             0, length);
/*     */         }
/* 325 */         this.innerFlowContexts[this.innerFlowContextsCount] = ((LoopingFlowContext)inner);
/* 326 */         this.innerFlowInfos[(this.innerFlowContextsCount++)] = 
/* 327 */           flowInfo.unconditionalInitsWithoutSideEffect();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean recordFinalAssignment(VariableBinding binding, Reference finalAssignment)
/*     */   {
/* 337 */     if ((binding instanceof LocalVariableBinding)) {
/* 338 */       Scope scope = ((LocalVariableBinding)binding).declaringScope;
/* 339 */       while ((scope = scope.parent) != null) {
/* 340 */         if (scope == this.associatedScope)
/* 341 */           return false;
/*     */       }
/*     */     }
/* 344 */     if (this.assignCount == 0) {
/* 345 */       this.finalAssignments = new Reference[5];
/* 346 */       this.finalVariables = new VariableBinding[5];
/*     */     } else {
/* 348 */       if (this.assignCount == this.finalAssignments.length)
/* 349 */         System.arraycopy(
/* 350 */           this.finalAssignments, 
/* 351 */           0, 
/* 352 */           this.finalAssignments = new Reference[this.assignCount * 2], 
/* 353 */           0, 
/* 354 */           this.assignCount);
/* 355 */       System.arraycopy(
/* 356 */         this.finalVariables, 
/* 357 */         0, 
/* 358 */         this.finalVariables = new VariableBinding[this.assignCount * 2], 
/* 359 */         0, 
/* 360 */         this.assignCount);
/*     */     }
/* 362 */     this.finalAssignments[this.assignCount] = finalAssignment;
/* 363 */     this.finalVariables[(this.assignCount++)] = binding;
/* 364 */     return true;
/*     */   }
/*     */ 
/*     */   protected void recordNullReference(LocalVariableBinding local, Expression expression, int status)
/*     */   {
/* 369 */     if (this.nullCount == 0) {
/* 370 */       this.nullLocals = new LocalVariableBinding[5];
/* 371 */       this.nullReferences = new Expression[5];
/* 372 */       this.nullCheckTypes = new int[5];
/*     */     }
/* 374 */     else if (this.nullCount == this.nullLocals.length) {
/* 375 */       System.arraycopy(this.nullLocals, 0, 
/* 376 */         this.nullLocals = new LocalVariableBinding[this.nullCount * 2], 0, this.nullCount);
/* 377 */       System.arraycopy(this.nullReferences, 0, 
/* 378 */         this.nullReferences = new Expression[this.nullCount * 2], 0, this.nullCount);
/* 379 */       System.arraycopy(this.nullCheckTypes, 0, 
/* 380 */         this.nullCheckTypes = new int[this.nullCount * 2], 0, this.nullCount);
/*     */     }
/* 382 */     this.nullLocals[this.nullCount] = local;
/* 383 */     this.nullReferences[this.nullCount] = expression;
/* 384 */     this.nullCheckTypes[(this.nullCount++)] = status;
/*     */   }
/*     */ 
/*     */   public void recordUsingNullReference(Scope scope, LocalVariableBinding local, Expression reference, int checkType, FlowInfo flowInfo)
/*     */   {
/* 389 */     if (((flowInfo.tagBits & 0x1) != 0) || 
/* 390 */       (flowInfo.isDefinitelyUnknown(local))) {
/* 391 */       return;
/*     */     }
/* 393 */     switch (checkType) {
/*     */     case 256:
/*     */     case 512:
/* 396 */       if (flowInfo.isDefinitelyNonNull(local)) {
/* 397 */         if (checkType == 512)
/* 398 */           scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
/*     */         else
/* 400 */           scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
/*     */       }
/* 402 */       else if (flowInfo.isDefinitelyNull(local)) {
/* 403 */         if (checkType == 256)
/* 404 */           scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
/*     */         else
/* 406 */           scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
/*     */       }
/* 408 */       else if (!flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
/* 409 */         if (flowInfo.isPotentiallyNonNull(local))
/* 410 */           recordNullReference(local, reference, 0x2 | checkType & 0xFFFFFF00);
/*     */         else {
/* 412 */           recordNullReference(local, reference, checkType);
/*     */         }
/*     */       }
/* 415 */       return;
/*     */     case 257:
/*     */     case 513:
/*     */     case 769:
/*     */     case 1025:
/* 420 */       if ((flowInfo.isPotentiallyNonNull(local)) || 
/* 421 */         (flowInfo.isPotentiallyUnknown(local))) {
/* 422 */         return;
/*     */       }
/* 424 */       if (flowInfo.isDefinitelyNull(local)) {
/* 425 */         switch (checkType & 0xFFFFFF00) {
/*     */         case 256:
/* 427 */           scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
/* 428 */           return;
/*     */         case 512:
/* 430 */           scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
/* 431 */           return;
/*     */         case 768:
/* 433 */           scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
/* 434 */           return;
/*     */         case 1024:
/* 436 */           scope.problemReporter().localVariableNullInstanceof(local, reference);
/* 437 */           return;
/*     */         }
/*     */       }
/* 440 */       recordNullReference(local, reference, checkType);
/* 441 */       return;
/*     */     case 3:
/* 443 */       if (flowInfo.isDefinitelyNonNull(local)) {
/* 444 */         return;
/*     */       }
/* 446 */       if (flowInfo.isDefinitelyNull(local)) {
/* 447 */         scope.problemReporter().localVariableNullReference(local, reference);
/* 448 */         return;
/*     */       }
/* 450 */       if (flowInfo.isPotentiallyNull(local)) {
/* 451 */         scope.problemReporter().localVariablePotentialNullReference(local, reference);
/* 452 */         return;
/*     */       }
/* 454 */       recordNullReference(local, reference, checkType);
/* 455 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeFinalAssignmentIfAny(Reference reference)
/*     */   {
/* 462 */     for (int i = 0; i < this.assignCount; i++)
/* 463 */       if (this.finalAssignments[i] == reference) {
/* 464 */         this.finalAssignments[i] = null;
/* 465 */         this.finalVariables[i] = null;
/* 466 */         return;
/*     */       }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext
 * JD-Core Version:    0.6.0
 */