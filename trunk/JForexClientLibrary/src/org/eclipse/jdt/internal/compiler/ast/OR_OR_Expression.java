/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class OR_OR_Expression extends BinaryExpression
/*     */ {
/*  22 */   int rightInitStateIndex = -1;
/*  23 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public OR_OR_Expression(Expression left, Expression right, int operator) {
/*  26 */     super(left, right, operator);
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  34 */     Constant cst = this.left.optimizedBooleanConstant();
/*  35 */     boolean isLeftOptimizedTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  36 */     boolean isLeftOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  38 */     if (isLeftOptimizedFalse)
/*     */     {
/*  42 */       FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
/*  43 */       mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
/*  44 */       this.mergedInitStateIndex = 
/*  45 */         currentScope.methodScope().recordInitializationStates(mergedInfo);
/*  46 */       return mergedInfo;
/*     */     }
/*     */ 
/*  49 */     FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);
/*     */ 
/*  53 */     FlowInfo rightInfo = leftInfo.initsWhenFalse().unconditionalCopy();
/*  54 */     this.rightInitStateIndex = 
/*  55 */       currentScope.methodScope().recordInitializationStates(rightInfo);
/*     */ 
/*  57 */     int previousMode = rightInfo.reachMode();
/*  58 */     if ((isLeftOptimizedTrue) && 
/*  59 */       ((rightInfo.reachMode() & 0x1) == 0)) {
/*  60 */       currentScope.problemReporter().fakeReachable(this.right);
/*  61 */       rightInfo.setReachMode(1);
/*     */     }
/*     */ 
/*  64 */     rightInfo = this.right.analyseCode(currentScope, flowContext, rightInfo);
/*  65 */     FlowInfo mergedInfo = FlowInfo.conditional(
/*  67 */       leftInfo.initsWhenTrue().unconditionalInits().mergedWith(
/*  68 */       rightInfo.safeInitsWhenTrue().setReachMode(previousMode).unconditionalInits()), 
/*  69 */       rightInfo.initsWhenFalse());
/*  70 */     this.mergedInitStateIndex = 
/*  71 */       currentScope.methodScope().recordInitializationStates(mergedInfo);
/*  72 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  79 */     int pc = codeStream.position;
/*  80 */     if (this.constant != Constant.NotAConstant)
/*     */     {
/*  82 */       if (valueRequired)
/*  83 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/*  84 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*  85 */       return;
/*     */     }
/*  87 */     Constant cst = this.right.constant;
/*  88 */     if (cst != Constant.NotAConstant)
/*     */     {
/*  90 */       if (cst.booleanValue()) {
/*  91 */         this.left.generateCode(currentScope, codeStream, false);
/*  92 */         if (valueRequired) codeStream.iconst_1(); 
/*     */       }
/*     */       else
/*     */       {
/*  95 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*     */       }
/*  97 */       if (this.mergedInitStateIndex != -1) {
/*  98 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/* 100 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 101 */       codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/* 102 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 103 */       return;
/*     */     }
/*     */ 
/* 106 */     BranchLabel trueLabel = new BranchLabel(codeStream);
/* 107 */     cst = this.left.optimizedBooleanConstant();
/* 108 */     boolean leftIsConst = cst != Constant.NotAConstant;
/* 109 */     boolean leftIsTrue = (leftIsConst) && (cst.booleanValue());
/*     */ 
/* 111 */     cst = this.right.optimizedBooleanConstant();
/* 112 */     boolean rightIsConst = cst != Constant.NotAConstant;
/* 113 */     boolean rightIsTrue = (rightIsConst) && (cst.booleanValue());
/*     */ 
/* 116 */     if (leftIsConst) {
/* 117 */       this.left.generateCode(currentScope, codeStream, false);
/* 118 */       if (leftIsTrue)
/* 119 */         break label326;
/*     */     }
/*     */     else {
/* 122 */       this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, true);
/*     */     }
/*     */ 
/* 125 */     if (this.rightInitStateIndex != -1) {
/* 126 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
/*     */     }
/* 128 */     if (rightIsConst)
/* 129 */       this.right.generateCode(currentScope, codeStream, false);
/*     */     else {
/* 131 */       this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, valueRequired);
/*     */     }
/*     */ 
/* 134 */     label326: if (this.mergedInitStateIndex != -1) {
/* 135 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/*     */ 
/* 142 */     if (valueRequired) {
/* 143 */       if ((leftIsConst) && (leftIsTrue)) {
/* 144 */         codeStream.iconst_1();
/* 145 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */       } else {
/* 147 */         if ((rightIsConst) && (rightIsTrue)) {
/* 148 */           codeStream.iconst_1();
/* 149 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */         } else {
/* 151 */           codeStream.iconst_0();
/*     */         }
/* 153 */         if (trueLabel.forwardReferenceCount() > 0) {
/* 154 */           if ((this.bits & 0x10) != 0) {
/* 155 */             codeStream.generateImplicitConversion(this.implicitConversion);
/* 156 */             codeStream.generateReturnBytecode(this);
/* 157 */             trueLabel.place();
/* 158 */             codeStream.iconst_1();
/*     */           }
/*     */           else
/*     */           {
/*     */             BranchLabel endLabel;
/* 160 */             codeStream.goto_(endLabel = new BranchLabel(codeStream));
/* 161 */             codeStream.decrStackSize(1);
/* 162 */             trueLabel.place();
/* 163 */             codeStream.iconst_1();
/* 164 */             endLabel.place();
/*     */           }
/*     */         }
/* 167 */         else trueLabel.place();
/*     */       }
/*     */ 
/* 170 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 171 */       codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */     } else {
/* 173 */       trueLabel.place();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*     */   {
/* 181 */     if (this.constant != Constant.NotAConstant) {
/* 182 */       super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/* 183 */       return;
/*     */     }
/*     */ 
/* 187 */     Constant cst = this.right.constant;
/* 188 */     if ((cst != Constant.NotAConstant) && (!cst.booleanValue())) {
/* 189 */       int pc = codeStream.position;
/* 190 */       this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/* 191 */       if (this.mergedInitStateIndex != -1) {
/* 192 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/* 194 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 195 */       return;
/*     */     }
/*     */ 
/* 198 */     cst = this.left.optimizedBooleanConstant();
/* 199 */     boolean leftIsConst = cst != Constant.NotAConstant;
/* 200 */     boolean leftIsTrue = (leftIsConst) && (cst.booleanValue());
/*     */ 
/* 202 */     cst = this.right.optimizedBooleanConstant();
/* 203 */     boolean rightIsConst = cst != Constant.NotAConstant;
/* 204 */     boolean rightIsTrue = (rightIsConst) && (cst.booleanValue());
/*     */ 
/* 208 */     if (falseLabel == null) {
/* 209 */       if (trueLabel != null)
/*     */       {
/* 211 */         this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, !leftIsConst);
/*     */ 
/* 213 */         if (leftIsTrue) {
/* 214 */           if (valueRequired) codeStream.goto_(trueLabel);
/* 215 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */         }
/*     */         else {
/* 218 */           if (this.rightInitStateIndex != -1) {
/* 219 */             codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
/*     */           }
/* 221 */           this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, (valueRequired) && (!rightIsConst));
/* 222 */           if ((valueRequired) && (rightIsTrue)) {
/* 223 */             codeStream.goto_(trueLabel);
/* 224 */             codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 229 */     else if (trueLabel == null) {
/* 230 */       BranchLabel internalTrueLabel = new BranchLabel(codeStream);
/* 231 */       this.left.generateOptimizedBoolean(currentScope, codeStream, internalTrueLabel, null, !leftIsConst);
/*     */ 
/* 233 */       if (leftIsTrue) {
/* 234 */         internalTrueLabel.place();
/*     */       }
/*     */       else {
/* 237 */         if (this.rightInitStateIndex != -1) {
/* 238 */           codeStream
/* 239 */             .addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
/*     */         }
/* 241 */         this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, (valueRequired) && (!rightIsConst));
/* 242 */         if ((valueRequired) && (rightIsConst) && (!rightIsTrue)) {
/* 243 */           codeStream.goto_(falseLabel);
/* 244 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */         }
/* 246 */         internalTrueLabel.place();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 252 */     if (this.mergedInitStateIndex != -1)
/* 253 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */   }
/*     */ 
/*     */   public boolean isCompactableOperation()
/*     */   {
/* 258 */     return false;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 265 */     TypeBinding result = super.resolveType(scope);
/*     */ 
/* 267 */     Binding leftDirect = Expression.getDirectBinding(this.left);
/* 268 */     if ((leftDirect != null) && (leftDirect == Expression.getDirectBinding(this.right)) && 
/* 269 */       (!(this.right instanceof Assignment))) {
/* 270 */       scope.problemReporter().comparingIdenticalExpressions(this);
/*     */     }
/* 272 */     return result;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 276 */     if (visitor.visit(this, scope)) {
/* 277 */       this.left.traverse(visitor, scope);
/* 278 */       this.right.traverse(visitor, scope);
/*     */     }
/* 280 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression
 * JD-Core Version:    0.6.0
 */