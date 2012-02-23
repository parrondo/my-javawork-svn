/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ForStatement extends Statement
/*     */ {
/*     */   public Statement[] initializations;
/*     */   public Expression condition;
/*     */   public Statement[] increments;
/*     */   public Statement action;
/*     */   public BlockScope scope;
/*     */   private BranchLabel breakLabel;
/*     */   private BranchLabel continueLabel;
/*  34 */   int preCondInitStateIndex = -1;
/*  35 */   int preIncrementsInitStateIndex = -1;
/*  36 */   int condIfTrueInitStateIndex = -1;
/*  37 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public ForStatement(Statement[] initializations, Expression condition, Statement[] increments, Statement action, boolean neededScope, int s, int e)
/*     */   {
/*  48 */     this.sourceStart = s;
/*  49 */     this.sourceEnd = e;
/*  50 */     this.initializations = initializations;
/*  51 */     this.condition = condition;
/*  52 */     this.increments = increments;
/*  53 */     this.action = action;
/*     */ 
/*  55 */     if ((action instanceof EmptyStatement)) action.bits |= 1;
/*  56 */     if (neededScope)
/*  57 */       this.bits |= 536870912;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  62 */     this.breakLabel = new BranchLabel();
/*  63 */     this.continueLabel = new BranchLabel();
/*  64 */     int initialComplaintLevel = (flowInfo.reachMode() & 0x1) != 0 ? 1 : 0;
/*     */ 
/*  67 */     if (this.initializations != null) {
/*  68 */       int i = 0; for (int count = this.initializations.length; i < count; i++) {
/*  69 */         flowInfo = this.initializations[i].analyseCode(this.scope, flowContext, flowInfo);
/*     */       }
/*     */     }
/*  72 */     this.preCondInitStateIndex = 
/*  73 */       currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */ 
/*  75 */     Constant cst = this.condition == null ? null : this.condition.constant;
/*  76 */     boolean isConditionTrue = (cst == null) || ((cst != Constant.NotAConstant) && (cst.booleanValue()));
/*  77 */     boolean isConditionFalse = (cst != null) && (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  79 */     cst = this.condition == null ? null : this.condition.optimizedBooleanConstant();
/*  80 */     boolean isConditionOptimizedTrue = (cst == null) || ((cst != Constant.NotAConstant) && (cst.booleanValue()));
/*  81 */     boolean isConditionOptimizedFalse = (cst != null) && (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  84 */     LoopingFlowContext condLoopContext = null;
/*  85 */     FlowInfo condInfo = flowInfo.nullInfoLessUnconditionalCopy();
/*  86 */     if ((this.condition != null) && 
/*  87 */       (!isConditionTrue))
/*  88 */       condInfo = 
/*  89 */         this.condition.analyseCode(
/*  90 */         this.scope, 
/*  91 */         condLoopContext = 
/*  92 */         new LoopingFlowContext(flowContext, flowInfo, this, null, 
/*  93 */         null, this.scope), 
/*  94 */         condInfo);
/*     */     LoopingFlowContext loopingContext;
/*     */     LoopingFlowContext loopingContext;
/*     */     UnconditionalFlowInfo actionInfo;
/* 101 */     if ((this.action == null) || (
/* 102 */       (this.action.isEmptyBlock()) && (currentScope.compilerOptions().complianceLevel <= 3080192L))) {
/* 103 */       if (condLoopContext != null)
/* 104 */         condLoopContext.complainOnDeferredFinalChecks(this.scope, condInfo);
/* 105 */       if (isConditionTrue) {
/* 106 */         if (condLoopContext != null) {
/* 107 */           condLoopContext.complainOnDeferredNullChecks(currentScope, 
/* 108 */             condInfo);
/*     */         }
/* 110 */         return FlowInfo.DEAD_END;
/*     */       }
/* 112 */       if (isConditionFalse) {
/* 113 */         this.continueLabel = null;
/*     */       }
/* 115 */       UnconditionalFlowInfo actionInfo = condInfo.initsWhenTrue().unconditionalCopy();
/* 116 */       loopingContext = 
/* 117 */         new LoopingFlowContext(flowContext, flowInfo, this, 
/* 118 */         this.breakLabel, this.continueLabel, this.scope);
/*     */     }
/*     */     else
/*     */     {
/* 122 */       loopingContext = 
/* 123 */         new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, 
/* 124 */         this.continueLabel, this.scope);
/* 125 */       FlowInfo initsWhenTrue = condInfo.initsWhenTrue();
/* 126 */       this.condIfTrueInitStateIndex = 
/* 127 */         currentScope.methodScope().recordInitializationStates(initsWhenTrue);
/*     */       UnconditionalFlowInfo actionInfo;
/* 129 */       if (isConditionFalse) {
/* 130 */         actionInfo = FlowInfo.DEAD_END;
/*     */       } else {
/* 132 */         actionInfo = initsWhenTrue.unconditionalCopy();
/* 133 */         if (isConditionOptimizedFalse) {
/* 134 */           actionInfo.setReachMode(1);
/*     */         }
/*     */       }
/* 137 */       if (this.action.complainIfUnreachable(actionInfo, this.scope, initialComplaintLevel) < 2) {
/* 138 */         actionInfo = this.action.analyseCode(this.scope, loopingContext, actionInfo).unconditionalInits();
/*     */       }
/*     */ 
/* 142 */       if ((actionInfo.tagBits & 
/* 143 */         loopingContext.initsOnContinue.tagBits & 
/* 144 */         0x1) != 0) {
/* 145 */         this.continueLabel = null;
/*     */       }
/*     */       else {
/* 148 */         if (condLoopContext != null) {
/* 149 */           condLoopContext.complainOnDeferredFinalChecks(this.scope, 
/* 150 */             condInfo);
/*     */         }
/* 152 */         actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue);
/* 153 */         loopingContext.complainOnDeferredFinalChecks(this.scope, 
/* 154 */           actionInfo);
/*     */       }
/*     */     }
/*     */ 
/* 158 */     FlowInfo exitBranch = flowInfo.copy();
/*     */ 
/* 160 */     LoopingFlowContext incrementContext = null;
/* 161 */     if (this.continueLabel != null) {
/* 162 */       if (this.increments != null) {
/* 163 */         incrementContext = 
/* 164 */           new LoopingFlowContext(flowContext, flowInfo, this, null, 
/* 165 */           null, this.scope);
/* 166 */         FlowInfo incrementInfo = actionInfo;
/* 167 */         this.preIncrementsInitStateIndex = 
/* 168 */           currentScope.methodScope().recordInitializationStates(incrementInfo);
/* 169 */         int i = 0; for (int count = this.increments.length; i < count; i++) {
/* 170 */           incrementInfo = this.increments[i]
/* 171 */             .analyseCode(this.scope, incrementContext, incrementInfo);
/*     */         }
/* 173 */         incrementContext.complainOnDeferredFinalChecks(this.scope, 
/* 174 */           actionInfo = incrementInfo.unconditionalInits());
/*     */       }
/* 176 */       exitBranch.addPotentialInitializationsFrom(actionInfo)
/* 177 */         .addInitializationsFrom(condInfo.initsWhenFalse());
/*     */     } else {
/* 179 */       exitBranch.addInitializationsFrom(condInfo.initsWhenFalse());
/* 180 */       if ((this.increments != null) && 
/* 181 */         (initialComplaintLevel == 0)) {
/* 182 */         currentScope.problemReporter().fakeReachable(this.increments[0]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 187 */     if (condLoopContext != null) {
/* 188 */       condLoopContext.complainOnDeferredNullChecks(currentScope, 
/* 189 */         actionInfo);
/*     */     }
/* 191 */     loopingContext.complainOnDeferredNullChecks(currentScope, 
/* 192 */       actionInfo);
/* 193 */     if (incrementContext != null) {
/* 194 */       incrementContext.complainOnDeferredNullChecks(currentScope, 
/* 195 */         actionInfo);
/*     */     }
/*     */ 
/* 199 */     FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(
/* 200 */       (loopingContext.initsOnBreak.tagBits & 
/* 201 */       0x1) != 0 ? 
/* 202 */       loopingContext.initsOnBreak : 
/* 203 */       flowInfo.addInitializationsFrom(loopingContext.initsOnBreak), 
/* 204 */       isConditionOptimizedTrue, 
/* 205 */       exitBranch, 
/* 206 */       isConditionOptimizedFalse, 
/* 207 */       !isConditionTrue);
/* 208 */     this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 209 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 220 */     if ((this.bits & 0x80000000) == 0) {
/* 221 */       return;
/*     */     }
/* 223 */     int pc = codeStream.position;
/*     */ 
/* 226 */     if (this.initializations != null) {
/* 227 */       int i = 0; for (int max = this.initializations.length; i < max; i++) {
/* 228 */         this.initializations[i].generateCode(this.scope, codeStream);
/*     */       }
/*     */     }
/* 231 */     Constant cst = this.condition == null ? null : this.condition.optimizedBooleanConstant();
/* 232 */     boolean isConditionOptimizedFalse = (cst != null) && (cst != Constant.NotAConstant) && (!cst.booleanValue());
/* 233 */     if (isConditionOptimizedFalse) {
/* 234 */       this.condition.generateCode(this.scope, codeStream, false);
/*     */ 
/* 236 */       if ((this.bits & 0x20000000) != 0) {
/* 237 */         codeStream.exitUserScope(this.scope);
/*     */       }
/* 239 */       if (this.mergedInitStateIndex != -1) {
/* 240 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 241 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/* 243 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 244 */       return;
/*     */     }
/*     */ 
/* 248 */     BranchLabel actionLabel = new BranchLabel(codeStream);
/* 249 */     actionLabel.tagBits |= 2;
/* 250 */     BranchLabel conditionLabel = new BranchLabel(codeStream);
/* 251 */     this.breakLabel.initialize(codeStream);
/* 252 */     if (this.continueLabel == null) {
/* 253 */       conditionLabel.place();
/* 254 */       if ((this.condition != null) && (this.condition.constant == Constant.NotAConstant))
/* 255 */         this.condition.generateOptimizedBoolean(this.scope, codeStream, null, this.breakLabel, true);
/*     */     }
/*     */     else {
/* 258 */       this.continueLabel.initialize(codeStream);
/*     */ 
/* 260 */       if ((this.condition != null) && 
/* 261 */         (this.condition.constant == Constant.NotAConstant) && (
/* 262 */         ((this.action != null) && (!this.action.isEmptyBlock())) || (this.increments != null))) {
/* 263 */         conditionLabel.tagBits |= 2;
/* 264 */         int jumpPC = codeStream.position;
/* 265 */         codeStream.goto_(conditionLabel);
/* 266 */         codeStream.recordPositionsFrom(jumpPC, this.condition.sourceStart);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 271 */     if (this.action != null)
/*     */     {
/* 273 */       if (this.condIfTrueInitStateIndex != -1)
/*     */       {
/* 275 */         codeStream.addDefinitelyAssignedVariables(
/* 276 */           currentScope, 
/* 277 */           this.condIfTrueInitStateIndex);
/*     */       }
/* 279 */       actionLabel.place();
/* 280 */       this.action.generateCode(this.scope, codeStream);
/*     */     } else {
/* 282 */       actionLabel.place();
/*     */     }
/* 284 */     if (this.preIncrementsInitStateIndex != -1) {
/* 285 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preIncrementsInitStateIndex);
/* 286 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.preIncrementsInitStateIndex);
/*     */     }
/*     */ 
/* 289 */     if (this.continueLabel != null) {
/* 290 */       this.continueLabel.place();
/*     */ 
/* 292 */       if (this.increments != null) {
/* 293 */         int i = 0; for (int max = this.increments.length; i < max; i++) {
/* 294 */           this.increments[i].generateCode(this.scope, codeStream);
/*     */         }
/*     */       }
/*     */ 
/* 298 */       if (this.preCondInitStateIndex != -1) {
/* 299 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
/*     */       }
/*     */ 
/* 302 */       conditionLabel.place();
/* 303 */       if ((this.condition != null) && (this.condition.constant == Constant.NotAConstant))
/* 304 */         this.condition.generateOptimizedBoolean(this.scope, codeStream, actionLabel, null, true);
/*     */       else {
/* 306 */         codeStream.goto_(actionLabel);
/*     */       }
/*     */ 
/*     */     }
/* 311 */     else if (this.preCondInitStateIndex != -1) {
/* 312 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
/*     */     }
/*     */ 
/* 318 */     if ((this.bits & 0x20000000) != 0) {
/* 319 */       codeStream.exitUserScope(this.scope);
/*     */     }
/* 321 */     if (this.mergedInitStateIndex != -1) {
/* 322 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 323 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/* 325 */     this.breakLabel.place();
/* 326 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int tab, StringBuffer output)
/*     */   {
/* 331 */     printIndent(tab, output).append("for (");
/*     */ 
/* 333 */     if (this.initializations != null) {
/* 334 */       for (int i = 0; i < this.initializations.length; i++)
/*     */       {
/* 336 */         if (i > 0) output.append(", ");
/* 337 */         this.initializations[i].print(0, output);
/*     */       }
/*     */     }
/* 340 */     output.append("; ");
/*     */ 
/* 342 */     if (this.condition != null) this.condition.printExpression(0, output);
/* 343 */     output.append("; ");
/*     */ 
/* 345 */     if (this.increments != null) {
/* 346 */       for (int i = 0; i < this.increments.length; i++) {
/* 347 */         if (i > 0) output.append(", ");
/* 348 */         this.increments[i].print(0, output);
/*     */       }
/*     */     }
/* 351 */     output.append(") ");
/*     */ 
/* 353 */     if (this.action == null) {
/* 354 */       output.append(';');
/*     */     } else {
/* 356 */       output.append('\n');
/* 357 */       this.action.printStatement(tab + 1, output);
/*     */     }
/* 359 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope upperScope)
/*     */   {
/* 365 */     this.scope = ((this.bits & 0x20000000) != 0 ? new BlockScope(upperScope) : upperScope);
/* 366 */     if (this.initializations != null) {
/* 367 */       int i = 0; for (int length = this.initializations.length; i < length; i++)
/* 368 */         this.initializations[i].resolve(this.scope); 
/*     */     }
/* 369 */     if (this.condition != null) {
/* 370 */       TypeBinding type = this.condition.resolveTypeExpecting(this.scope, TypeBinding.BOOLEAN);
/* 371 */       this.condition.computeConversion(this.scope, type, type);
/*     */     }
/* 373 */     if (this.increments != null) {
/* 374 */       int i = 0; for (int length = this.increments.length; i < length; i++)
/* 375 */         this.increments[i].resolve(this.scope); 
/*     */     }
/* 376 */     if (this.action != null)
/* 377 */       this.action.resolve(this.scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 384 */     if (visitor.visit(this, blockScope)) {
/* 385 */       if (this.initializations != null) {
/* 386 */         int initializationsLength = this.initializations.length;
/* 387 */         for (int i = 0; i < initializationsLength; i++) {
/* 388 */           this.initializations[i].traverse(visitor, this.scope);
/*     */         }
/*     */       }
/* 391 */       if (this.condition != null) {
/* 392 */         this.condition.traverse(visitor, this.scope);
/*     */       }
/* 394 */       if (this.increments != null) {
/* 395 */         int incrementsLength = this.increments.length;
/* 396 */         for (int i = 0; i < incrementsLength; i++) {
/* 397 */           this.increments[i].traverse(visitor, this.scope);
/*     */         }
/*     */       }
/* 400 */       if (this.action != null)
/* 401 */         this.action.traverse(visitor, this.scope);
/*     */     }
/* 403 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ForStatement
 * JD-Core Version:    0.6.0
 */