/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class DoStatement extends Statement
/*     */ {
/*     */   public Expression condition;
/*     */   public Statement action;
/*     */   private BranchLabel breakLabel;
/*     */   private BranchLabel continueLabel;
/*  27 */   int mergedInitStateIndex = -1;
/*  28 */   int preConditionInitStateIndex = -1;
/*     */ 
/*     */   public DoStatement(Expression condition, Statement action, int sourceStart, int sourceEnd)
/*     */   {
/*  32 */     this.sourceStart = sourceStart;
/*  33 */     this.sourceEnd = sourceEnd;
/*  34 */     this.condition = condition;
/*  35 */     this.action = action;
/*     */ 
/*  37 */     if ((action instanceof EmptyStatement)) action.bits |= 1; 
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  41 */     this.breakLabel = new BranchLabel();
/*  42 */     this.continueLabel = new BranchLabel();
/*  43 */     LoopingFlowContext loopingContext = 
/*  44 */       new LoopingFlowContext(
/*  45 */       flowContext, 
/*  46 */       flowInfo, 
/*  47 */       this, 
/*  48 */       this.breakLabel, 
/*  49 */       this.continueLabel, 
/*  50 */       currentScope);
/*     */ 
/*  52 */     Constant cst = this.condition.constant;
/*  53 */     boolean isConditionTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  54 */     cst = this.condition.optimizedBooleanConstant();
/*  55 */     boolean isConditionOptimizedTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  56 */     boolean isConditionOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  58 */     int previousMode = flowInfo.reachMode();
/*     */ 
/*  60 */     UnconditionalFlowInfo actionInfo = flowInfo.nullInfoLessUnconditionalCopy();
/*     */ 
/*  64 */     if ((this.action != null) && (!this.action.isEmptyBlock())) {
/*  65 */       actionInfo = this.action
/*  66 */         .analyseCode(currentScope, loopingContext, actionInfo)
/*  67 */         .unconditionalInits();
/*     */ 
/*  70 */       if ((actionInfo.tagBits & 
/*  71 */         loopingContext.initsOnContinue.tagBits & 
/*  72 */         0x1) != 0) {
/*  73 */         this.continueLabel = null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  81 */     actionInfo.setReachMode(previousMode);
/*     */     LoopingFlowContext condLoopContext;
/*  84 */     FlowInfo condInfo = 
/*  85 */       this.condition.analyseCode(
/*  86 */       currentScope, 
/*  87 */       condLoopContext = 
/*  88 */       new LoopingFlowContext(flowContext, flowInfo, this, null, 
/*  89 */       null, currentScope), 
/*  90 */       (this.action == null ? 
/*  91 */       actionInfo : 
/*  92 */       actionInfo.mergedWith(loopingContext.initsOnContinue)).copy());
/*  93 */     this.preConditionInitStateIndex = currentScope.methodScope().recordInitializationStates(actionInfo);
/*  94 */     if ((!isConditionOptimizedFalse) && (this.continueLabel != null)) {
/*  95 */       loopingContext.complainOnDeferredFinalChecks(currentScope, condInfo);
/*  96 */       condLoopContext.complainOnDeferredFinalChecks(currentScope, condInfo);
/*  97 */       loopingContext.complainOnDeferredNullChecks(currentScope, 
/*  98 */         flowInfo.unconditionalCopy().addPotentialNullInfoFrom(
/*  99 */         condInfo.initsWhenTrue().unconditionalInits()));
/* 100 */       condLoopContext.complainOnDeferredNullChecks(currentScope, 
/* 101 */         actionInfo.addPotentialNullInfoFrom(
/* 102 */         condInfo.initsWhenTrue().unconditionalInits()));
/*     */     }
/*     */ 
/* 106 */     FlowInfo mergedInfo = 
/* 107 */       FlowInfo.mergedOptimizedBranches(
/* 108 */       (loopingContext.initsOnBreak.tagBits & 0x1) != 0 ? 
/* 109 */       loopingContext.initsOnBreak : 
/* 110 */       flowInfo.unconditionalCopy().addInitializationsFrom(loopingContext.initsOnBreak), 
/* 112 */       isConditionOptimizedTrue, 
/* 113 */       (condInfo.tagBits & 0x1) == 0 ? 
/* 114 */       flowInfo.addInitializationsFrom(condInfo.initsWhenFalse()) : 
/* 115 */       condInfo, 
/* 117 */       false, 
/* 118 */       !isConditionTrue);
/* 119 */     this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 120 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 128 */     if ((this.bits & 0x80000000) == 0) {
/* 129 */       return;
/*     */     }
/* 131 */     int pc = codeStream.position;
/*     */ 
/* 134 */     BranchLabel actionLabel = new BranchLabel(codeStream);
/* 135 */     if (this.action != null) actionLabel.tagBits |= 2;
/* 136 */     actionLabel.place();
/* 137 */     this.breakLabel.initialize(codeStream);
/* 138 */     boolean hasContinueLabel = this.continueLabel != null;
/* 139 */     if (hasContinueLabel) {
/* 140 */       this.continueLabel.initialize(codeStream);
/*     */     }
/*     */ 
/* 144 */     if (this.action != null) {
/* 145 */       this.action.generateCode(currentScope, codeStream);
/*     */     }
/*     */ 
/* 148 */     if (hasContinueLabel) {
/* 149 */       this.continueLabel.place();
/*     */ 
/* 151 */       if (this.preConditionInitStateIndex != -1) {
/* 152 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preConditionInitStateIndex);
/* 153 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.preConditionInitStateIndex);
/*     */       }
/*     */ 
/* 156 */       Constant cst = this.condition.optimizedBooleanConstant();
/* 157 */       boolean isConditionOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/* 158 */       if (isConditionOptimizedFalse)
/* 159 */         this.condition.generateCode(currentScope, codeStream, false);
/*     */       else {
/* 161 */         this.condition.generateOptimizedBoolean(
/* 162 */           currentScope, 
/* 163 */           codeStream, 
/* 164 */           actionLabel, 
/* 165 */           null, 
/* 166 */           true);
/*     */       }
/*     */     }
/*     */ 
/* 170 */     if (this.mergedInitStateIndex != -1) {
/* 171 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 172 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/* 174 */     if (this.breakLabel.forwardReferenceCount() > 0) {
/* 175 */       this.breakLabel.place();
/*     */     }
/*     */ 
/* 178 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output) {
/* 182 */     printIndent(indent, output).append("do");
/* 183 */     if (this.action == null) {
/* 184 */       output.append(" ;\n");
/*     */     } else {
/* 186 */       output.append('\n');
/* 187 */       this.action.printStatement(indent + 1, output).append('\n');
/*     */     }
/* 189 */     output.append("while (");
/* 190 */     return this.condition.printExpression(0, output).append(");");
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope) {
/* 194 */     TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
/* 195 */     this.condition.computeConversion(scope, type, type);
/* 196 */     if (this.action != null)
/* 197 */       this.action.resolve(scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 201 */     if (visitor.visit(this, scope)) {
/* 202 */       if (this.action != null) {
/* 203 */         this.action.traverse(visitor, scope);
/*     */       }
/* 205 */       this.condition.traverse(visitor, scope);
/*     */     }
/* 207 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.DoStatement
 * JD-Core Version:    0.6.0
 */