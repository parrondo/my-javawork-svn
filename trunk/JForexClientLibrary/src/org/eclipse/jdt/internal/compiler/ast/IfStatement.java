/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class IfStatement extends Statement
/*     */ {
/*     */   public Expression condition;
/*     */   public Statement thenStatement;
/*     */   public Statement elseStatement;
/*  29 */   int thenInitStateIndex = -1;
/*  30 */   int elseInitStateIndex = -1;
/*  31 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public IfStatement(Expression condition, Statement thenStatement, int sourceStart, int sourceEnd) {
/*  34 */     this.condition = condition;
/*  35 */     this.thenStatement = thenStatement;
/*     */ 
/*  37 */     if ((thenStatement instanceof EmptyStatement)) thenStatement.bits |= 1;
/*  38 */     this.sourceStart = sourceStart;
/*  39 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public IfStatement(Expression condition, Statement thenStatement, Statement elseStatement, int sourceStart, int sourceEnd) {
/*  43 */     this.condition = condition;
/*  44 */     this.thenStatement = thenStatement;
/*     */ 
/*  46 */     if ((thenStatement instanceof EmptyStatement)) thenStatement.bits |= 1;
/*  47 */     this.elseStatement = elseStatement;
/*  48 */     if ((elseStatement instanceof IfStatement)) elseStatement.bits |= 536870912;
/*  49 */     if ((elseStatement instanceof EmptyStatement)) elseStatement.bits |= 1;
/*  50 */     this.sourceStart = sourceStart;
/*  51 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  56 */     FlowInfo conditionFlowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo);
/*  57 */     int initialComplaintLevel = (flowInfo.reachMode() & 0x1) != 0 ? 1 : 0;
/*     */ 
/*  59 */     Constant cst = this.condition.optimizedBooleanConstant();
/*  60 */     boolean isConditionOptimizedTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  61 */     boolean isConditionOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  64 */     FlowInfo thenFlowInfo = conditionFlowInfo.safeInitsWhenTrue();
/*  65 */     if (isConditionOptimizedFalse) {
/*  66 */       thenFlowInfo.setReachMode(1);
/*     */     }
/*  68 */     FlowInfo elseFlowInfo = conditionFlowInfo.initsWhenFalse();
/*  69 */     if (isConditionOptimizedTrue) {
/*  70 */       elseFlowInfo.setReachMode(1);
/*     */     }
/*  72 */     if (this.thenStatement != null)
/*     */     {
/*  74 */       this.thenInitStateIndex = currentScope.methodScope().recordInitializationStates(thenFlowInfo);
/*  75 */       if ((isConditionOptimizedFalse) && (
/*  76 */         (!isKnowDeadCodePattern(this.condition)) || (currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement))) {
/*  77 */         this.thenStatement.complainIfUnreachable(thenFlowInfo, currentScope, initialComplaintLevel);
/*     */       }
/*     */ 
/*  80 */       thenFlowInfo = this.thenStatement.analyseCode(currentScope, flowContext, thenFlowInfo);
/*     */     }
/*     */ 
/*  83 */     if ((thenFlowInfo.tagBits & 0x1) != 0) {
/*  84 */       this.bits |= 1073741824;
/*     */     }
/*     */ 
/*  88 */     if (this.elseStatement != null)
/*     */     {
/*  90 */       if ((thenFlowInfo == FlowInfo.DEAD_END) && 
/*  91 */         ((this.bits & 0x20000000) == 0) && 
/*  92 */         (!(this.elseStatement instanceof IfStatement))) {
/*  93 */         currentScope.problemReporter().unnecessaryElse(this.elseStatement);
/*     */       }
/*     */ 
/*  96 */       this.elseInitStateIndex = currentScope.methodScope().recordInitializationStates(elseFlowInfo);
/*  97 */       if ((isConditionOptimizedTrue) && (
/*  98 */         (!isKnowDeadCodePattern(this.condition)) || (currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement))) {
/*  99 */         this.elseStatement.complainIfUnreachable(elseFlowInfo, currentScope, initialComplaintLevel);
/*     */       }
/*     */ 
/* 102 */       elseFlowInfo = this.elseStatement.analyseCode(currentScope, flowContext, elseFlowInfo);
/*     */     }
/*     */ 
/* 105 */     FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(
/* 106 */       thenFlowInfo, 
/* 107 */       isConditionOptimizedTrue, 
/* 108 */       elseFlowInfo, 
/* 109 */       isConditionOptimizedFalse, 
/* 110 */       true);
/* 111 */     this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 112 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 122 */     if ((this.bits & 0x80000000) == 0) {
/* 123 */       return;
/*     */     }
/* 125 */     int pc = codeStream.position;
/* 126 */     BranchLabel endifLabel = new BranchLabel(codeStream);
/*     */     Constant cst;
/* 130 */     boolean hasThenPart = 
/* 131 */       (((cst = this.condition.optimizedBooleanConstant()) == Constant.NotAConstant) || 
/* 132 */       (cst.booleanValue())) && 
/* 133 */       (this.thenStatement != null) && (!
/* 134 */       this.thenStatement.isEmptyBlock());
/* 135 */     boolean hasElsePart = 
/* 136 */       ((cst == Constant.NotAConstant) || (!cst.booleanValue())) && 
/* 137 */       (this.elseStatement != null) && (!
/* 138 */       this.elseStatement.isEmptyBlock());
/* 139 */     if (hasThenPart) {
/* 140 */       BranchLabel falseLabel = null;
/*     */ 
/* 142 */       this.condition.generateOptimizedBoolean(
/* 143 */         currentScope, 
/* 144 */         codeStream, 
/* 145 */         null, 
/* 146 */         hasElsePart ? (falseLabel = new BranchLabel(codeStream)) : endifLabel, 
/* 147 */         true);
/*     */ 
/* 149 */       if (this.thenInitStateIndex != -1) {
/* 150 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.thenInitStateIndex);
/* 151 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.thenInitStateIndex);
/*     */       }
/*     */ 
/* 154 */       this.thenStatement.generateCode(currentScope, codeStream);
/*     */ 
/* 156 */       if (hasElsePart) {
/* 157 */         if ((this.bits & 0x40000000) == 0) {
/* 158 */           this.thenStatement.branchChainTo(endifLabel);
/* 159 */           int position = codeStream.position;
/* 160 */           codeStream.goto_(endifLabel);
/*     */ 
/* 162 */           codeStream.updateLastRecordedEndPC((this.thenStatement instanceof Block) ? ((Block)this.thenStatement).scope : currentScope, position);
/*     */         }
/*     */ 
/* 166 */         if (this.elseInitStateIndex != -1) {
/* 167 */           codeStream.removeNotDefinitelyAssignedVariables(
/* 168 */             currentScope, 
/* 169 */             this.elseInitStateIndex);
/* 170 */           codeStream.addDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
/*     */         }
/* 172 */         if (falseLabel != null) falseLabel.place();
/* 173 */         this.elseStatement.generateCode(currentScope, codeStream);
/*     */       }
/* 175 */     } else if (hasElsePart)
/*     */     {
/* 177 */       this.condition.generateOptimizedBoolean(
/* 178 */         currentScope, 
/* 179 */         codeStream, 
/* 180 */         endifLabel, 
/* 181 */         null, 
/* 182 */         true);
/*     */ 
/* 185 */       if (this.elseInitStateIndex != -1) {
/* 186 */         codeStream.removeNotDefinitelyAssignedVariables(
/* 187 */           currentScope, 
/* 188 */           this.elseInitStateIndex);
/* 189 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
/*     */       }
/* 191 */       this.elseStatement.generateCode(currentScope, codeStream);
/*     */     }
/*     */     else {
/* 194 */       this.condition.generateCode(currentScope, codeStream, false);
/* 195 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */     }
/*     */ 
/* 198 */     if (this.mergedInitStateIndex != -1) {
/* 199 */       codeStream.removeNotDefinitelyAssignedVariables(
/* 200 */         currentScope, 
/* 201 */         this.mergedInitStateIndex);
/* 202 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/* 204 */     endifLabel.place();
/* 205 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public static boolean isKnowDeadCodePattern(Expression expression)
/*     */   {
/* 216 */     if ((expression instanceof UnaryExpression)) {
/* 217 */       expression = ((UnaryExpression)expression).expression;
/*     */     }
/*     */ 
/* 220 */     return (expression instanceof Reference);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output)
/*     */   {
/* 241 */     printIndent(indent, output).append("if (");
/* 242 */     this.condition.printExpression(0, output).append(")\n");
/* 243 */     this.thenStatement.printStatement(indent + 2, output);
/* 244 */     if (this.elseStatement != null) {
/* 245 */       output.append('\n');
/* 246 */       printIndent(indent, output);
/* 247 */       output.append("else\n");
/* 248 */       this.elseStatement.printStatement(indent + 2, output);
/*     */     }
/* 250 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope) {
/* 254 */     TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
/* 255 */     this.condition.computeConversion(scope, type, type);
/* 256 */     if (this.thenStatement != null)
/* 257 */       this.thenStatement.resolve(scope);
/* 258 */     if (this.elseStatement != null)
/* 259 */       this.elseStatement.resolve(scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 263 */     if (visitor.visit(this, blockScope)) {
/* 264 */       this.condition.traverse(visitor, blockScope);
/* 265 */       if (this.thenStatement != null)
/* 266 */         this.thenStatement.traverse(visitor, blockScope);
/* 267 */       if (this.elseStatement != null)
/* 268 */         this.elseStatement.traverse(visitor, blockScope);
/*     */     }
/* 270 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.IfStatement
 * JD-Core Version:    0.6.0
 */