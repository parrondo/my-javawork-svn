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
/*     */ 
/*     */ public class WhileStatement extends Statement
/*     */ {
/*     */   public Expression condition;
/*     */   public Statement action;
/*     */   private BranchLabel breakLabel;
/*     */   private BranchLabel continueLabel;
/*  25 */   int preCondInitStateIndex = -1;
/*  26 */   int condIfTrueInitStateIndex = -1;
/*  27 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public WhileStatement(Expression condition, Statement action, int s, int e)
/*     */   {
/*  31 */     this.condition = condition;
/*  32 */     this.action = action;
/*     */ 
/*  34 */     if ((action instanceof EmptyStatement)) action.bits |= 1;
/*  35 */     this.sourceStart = s;
/*  36 */     this.sourceEnd = e;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  41 */     this.breakLabel = new BranchLabel();
/*  42 */     this.continueLabel = new BranchLabel();
/*  43 */     int initialComplaintLevel = (flowInfo.reachMode() & 0x1) != 0 ? 1 : 0;
/*     */ 
/*  45 */     Constant cst = this.condition.constant;
/*  46 */     boolean isConditionTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  47 */     boolean isConditionFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  49 */     cst = this.condition.optimizedBooleanConstant();
/*  50 */     boolean isConditionOptimizedTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  51 */     boolean isConditionOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  53 */     this.preCondInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
/*     */ 
/*  55 */     FlowInfo condInfo = flowInfo.nullInfoLessUnconditionalCopy();
/*     */     LoopingFlowContext condLoopContext;
/*  60 */     condInfo = this.condition.analyseCode(
/*  61 */       currentScope, 
/*  62 */       condLoopContext = 
/*  63 */       new LoopingFlowContext(flowContext, flowInfo, this, null, 
/*  64 */       null, currentScope), 
/*  65 */       condInfo);
/*     */ 
/*  70 */     if ((this.action == null) || (
/*  71 */       (this.action.isEmptyBlock()) && (currentScope.compilerOptions().complianceLevel <= 3080192L))) {
/*  72 */       condLoopContext.complainOnDeferredFinalChecks(currentScope, 
/*  73 */         condInfo);
/*  74 */       condLoopContext.complainOnDeferredNullChecks(currentScope, 
/*  75 */         condInfo.unconditionalInits());
/*  76 */       if (isConditionTrue) {
/*  77 */         return FlowInfo.DEAD_END;
/*     */       }
/*  79 */       FlowInfo mergedInfo = flowInfo.copy().addInitializationsFrom(condInfo.initsWhenFalse());
/*  80 */       if (isConditionOptimizedTrue) {
/*  81 */         mergedInfo.setReachMode(1);
/*     */       }
/*  83 */       this.mergedInitStateIndex = 
/*  84 */         currentScope.methodScope().recordInitializationStates(mergedInfo);
/*  85 */       return mergedInfo;
/*     */     }
/*     */ 
/*  90 */     LoopingFlowContext loopingContext = 
/*  91 */       new LoopingFlowContext(
/*  92 */       flowContext, 
/*  93 */       flowInfo, 
/*  94 */       this, 
/*  95 */       this.breakLabel, 
/*  96 */       this.continueLabel, 
/*  97 */       currentScope);
/*     */     FlowInfo actionInfo;
/*     */     FlowInfo actionInfo;
/*  98 */     if (isConditionFalse) {
/*  99 */       actionInfo = FlowInfo.DEAD_END;
/*     */     } else {
/* 101 */       actionInfo = condInfo.initsWhenTrue().copy();
/* 102 */       if (isConditionOptimizedFalse) {
/* 103 */         actionInfo.setReachMode(1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 108 */     this.condIfTrueInitStateIndex = 
/* 109 */       currentScope.methodScope().recordInitializationStates(
/* 110 */       condInfo.initsWhenTrue());
/*     */ 
/* 112 */     if (this.action.complainIfUnreachable(actionInfo, currentScope, initialComplaintLevel) < 2) {
/* 113 */       actionInfo = this.action.analyseCode(currentScope, loopingContext, actionInfo);
/*     */     }
/*     */ 
/* 117 */     FlowInfo exitBranch = flowInfo.copy();
/*     */ 
/* 120 */     if ((actionInfo.tagBits & 
/* 121 */       loopingContext.initsOnContinue.tagBits & 
/* 122 */       0x1) != 0) {
/* 123 */       this.continueLabel = null;
/* 124 */       exitBranch.addInitializationsFrom(condInfo.initsWhenFalse());
/*     */     } else {
/* 126 */       condLoopContext.complainOnDeferredFinalChecks(currentScope, 
/* 127 */         condInfo);
/* 128 */       actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue.unconditionalInits());
/* 129 */       condLoopContext.complainOnDeferredNullChecks(currentScope, 
/* 130 */         actionInfo);
/* 131 */       loopingContext.complainOnDeferredFinalChecks(currentScope, 
/* 132 */         actionInfo);
/* 133 */       loopingContext.complainOnDeferredNullChecks(currentScope, 
/* 134 */         actionInfo);
/* 135 */       exitBranch
/* 136 */         .addPotentialInitializationsFrom(
/* 137 */         actionInfo.unconditionalInits())
/* 138 */         .addInitializationsFrom(condInfo.initsWhenFalse());
/*     */     }
/*     */ 
/* 143 */     FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(
/* 144 */       (loopingContext.initsOnBreak.tagBits & 
/* 145 */       0x1) != 0 ? 
/* 146 */       loopingContext.initsOnBreak : 
/* 147 */       flowInfo.addInitializationsFrom(loopingContext.initsOnBreak), 
/* 148 */       isConditionOptimizedTrue, 
/* 149 */       exitBranch, 
/* 150 */       isConditionOptimizedFalse, 
/* 151 */       !isConditionTrue);
/* 152 */     this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 153 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 164 */     if ((this.bits & 0x80000000) == 0) {
/* 165 */       return;
/*     */     }
/* 167 */     int pc = codeStream.position;
/* 168 */     Constant cst = this.condition.optimizedBooleanConstant();
/* 169 */     boolean isConditionOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/* 170 */     if (isConditionOptimizedFalse) {
/* 171 */       this.condition.generateCode(currentScope, codeStream, false);
/*     */ 
/* 173 */       if (this.mergedInitStateIndex != -1) {
/* 174 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 175 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/* 177 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 178 */       return;
/*     */     }
/*     */ 
/* 181 */     this.breakLabel.initialize(codeStream);
/*     */ 
/* 184 */     if (this.continueLabel == null)
/*     */     {
/* 186 */       if (this.condition.constant == Constant.NotAConstant)
/* 187 */         this.condition.generateOptimizedBoolean(
/* 188 */           currentScope, 
/* 189 */           codeStream, 
/* 190 */           null, 
/* 191 */           this.breakLabel, 
/* 192 */           true);
/*     */     }
/*     */     else {
/* 195 */       this.continueLabel.initialize(codeStream);
/* 196 */       if (((this.condition.constant == Constant.NotAConstant) || 
/* 197 */         (!this.condition.constant.booleanValue())) && 
/* 198 */         (this.action != null) && 
/* 199 */         (!this.action.isEmptyBlock())) {
/* 200 */         int jumpPC = codeStream.position;
/* 201 */         codeStream.goto_(this.continueLabel);
/* 202 */         codeStream.recordPositionsFrom(jumpPC, this.condition.sourceStart);
/*     */       }
/*     */     }
/*     */ 
/* 206 */     BranchLabel actionLabel = new BranchLabel(codeStream);
/* 207 */     if (this.action != null) {
/* 208 */       actionLabel.tagBits |= 2;
/*     */ 
/* 210 */       if (this.condIfTrueInitStateIndex != -1)
/*     */       {
/* 212 */         codeStream.addDefinitelyAssignedVariables(
/* 213 */           currentScope, 
/* 214 */           this.condIfTrueInitStateIndex);
/*     */       }
/* 216 */       actionLabel.place();
/* 217 */       this.action.generateCode(currentScope, codeStream);
/*     */ 
/* 219 */       if (this.preCondInitStateIndex != -1)
/* 220 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
/*     */     }
/*     */     else {
/* 223 */       actionLabel.place();
/*     */     }
/*     */ 
/* 226 */     if (this.continueLabel != null) {
/* 227 */       this.continueLabel.place();
/* 228 */       this.condition.generateOptimizedBoolean(
/* 229 */         currentScope, 
/* 230 */         codeStream, 
/* 231 */         actionLabel, 
/* 232 */         null, 
/* 233 */         true);
/*     */     }
/*     */ 
/* 237 */     if (this.mergedInitStateIndex != -1) {
/* 238 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 239 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/* 241 */     this.breakLabel.place();
/* 242 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope)
/*     */   {
/* 247 */     TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
/* 248 */     this.condition.computeConversion(scope, type, type);
/* 249 */     if (this.action != null)
/* 250 */       this.action.resolve(scope);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int tab, StringBuffer output)
/*     */   {
/* 255 */     printIndent(tab, output).append("while (");
/* 256 */     this.condition.printExpression(0, output).append(')');
/* 257 */     if (this.action == null)
/* 258 */       output.append(';');
/*     */     else
/* 260 */       this.action.printStatement(tab + 1, output);
/* 261 */     return output;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 268 */     if (visitor.visit(this, blockScope)) {
/* 269 */       this.condition.traverse(visitor, blockScope);
/* 270 */       if (this.action != null)
/* 271 */         this.action.traverse(visitor, blockScope);
/*     */     }
/* 273 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.WhileStatement
 * JD-Core Version:    0.6.0
 */