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
/*     */ public class AND_AND_Expression extends BinaryExpression
/*     */ {
/*  22 */   int rightInitStateIndex = -1;
/*  23 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public AND_AND_Expression(Expression left, Expression right, int operator) {
/*  26 */     super(left, right, operator);
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  31 */     Constant cst = this.left.optimizedBooleanConstant();
/*  32 */     boolean isLeftOptimizedTrue = (cst != Constant.NotAConstant) && (cst.booleanValue());
/*  33 */     boolean isLeftOptimizedFalse = (cst != Constant.NotAConstant) && (!cst.booleanValue());
/*     */ 
/*  35 */     if (isLeftOptimizedTrue)
/*     */     {
/*  40 */       FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo)
/*  41 */         .unconditionalInits();
/*  42 */       mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
/*  43 */       this.mergedInitStateIndex = currentScope.methodScope()
/*  44 */         .recordInitializationStates(mergedInfo);
/*  45 */       return mergedInfo;
/*     */     }
/*     */ 
/*  48 */     FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);
/*     */ 
/*  52 */     FlowInfo rightInfo = leftInfo.initsWhenTrue().unconditionalCopy();
/*  53 */     this.rightInitStateIndex = currentScope.methodScope().recordInitializationStates(rightInfo);
/*     */ 
/*  55 */     int previousMode = rightInfo.reachMode();
/*  56 */     if ((isLeftOptimizedFalse) && 
/*  57 */       ((rightInfo.reachMode() & 0x1) == 0)) {
/*  58 */       currentScope.problemReporter().fakeReachable(this.right);
/*  59 */       rightInfo.setReachMode(1);
/*     */     }
/*     */ 
/*  62 */     rightInfo = this.right.analyseCode(currentScope, flowContext, rightInfo);
/*  63 */     FlowInfo mergedInfo = FlowInfo.conditional(
/*  64 */       rightInfo.safeInitsWhenTrue(), 
/*  65 */       leftInfo.initsWhenFalse().unconditionalInits().mergedWith(
/*  66 */       rightInfo.initsWhenFalse().setReachMode(previousMode).unconditionalInits()));
/*     */ 
/*  68 */     this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
/*  69 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  77 */     int pc = codeStream.position;
/*  78 */     if (this.constant != Constant.NotAConstant)
/*     */     {
/*  80 */       if (valueRequired)
/*  81 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/*  82 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*  83 */       return;
/*     */     }
/*  85 */     Constant cst = this.right.constant;
/*  86 */     if (cst != Constant.NotAConstant)
/*     */     {
/*  88 */       if (cst.booleanValue()) {
/*  89 */         this.left.generateCode(currentScope, codeStream, valueRequired);
/*     */       }
/*     */       else {
/*  92 */         this.left.generateCode(currentScope, codeStream, false);
/*  93 */         if (valueRequired) codeStream.iconst_0();
/*     */       }
/*  95 */       if (this.mergedInitStateIndex != -1) {
/*  96 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/*  98 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*  99 */       codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/* 100 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 101 */       return;
/*     */     }
/*     */ 
/* 104 */     BranchLabel falseLabel = new BranchLabel(codeStream);
/* 105 */     cst = this.left.optimizedBooleanConstant();
/* 106 */     boolean leftIsConst = cst != Constant.NotAConstant;
/* 107 */     boolean leftIsTrue = (leftIsConst) && (cst.booleanValue());
/*     */ 
/* 109 */     cst = this.right.optimizedBooleanConstant();
/* 110 */     boolean rightIsConst = cst != Constant.NotAConstant;
/* 111 */     boolean rightIsTrue = (rightIsConst) && (cst.booleanValue());
/*     */ 
/* 114 */     if (leftIsConst) {
/* 115 */       this.left.generateCode(currentScope, codeStream, false);
/* 116 */       if (!leftIsTrue)
/* 117 */         break label326;
/*     */     }
/*     */     else {
/* 120 */       this.left.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, true);
/*     */     }
/*     */ 
/* 123 */     if (this.rightInitStateIndex != -1) {
/* 124 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
/*     */     }
/* 126 */     if (rightIsConst)
/* 127 */       this.right.generateCode(currentScope, codeStream, false);
/*     */     else {
/* 129 */       this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
/*     */     }
/*     */ 
/* 132 */     label326: if (this.mergedInitStateIndex != -1) {
/* 133 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/*     */ 
/* 140 */     if (valueRequired) {
/* 141 */       if ((leftIsConst) && (!leftIsTrue)) {
/* 142 */         codeStream.iconst_0();
/* 143 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */       } else {
/* 145 */         if ((rightIsConst) && (!rightIsTrue)) {
/* 146 */           codeStream.iconst_0();
/* 147 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */         } else {
/* 149 */           codeStream.iconst_1();
/*     */         }
/* 151 */         if (falseLabel.forwardReferenceCount() > 0) {
/* 152 */           if ((this.bits & 0x10) != 0) {
/* 153 */             codeStream.generateImplicitConversion(this.implicitConversion);
/* 154 */             codeStream.generateReturnBytecode(this);
/* 155 */             falseLabel.place();
/* 156 */             codeStream.iconst_0();
/*     */           }
/*     */           else
/*     */           {
/*     */             BranchLabel endLabel;
/* 158 */             codeStream.goto_(endLabel = new BranchLabel(codeStream));
/* 159 */             codeStream.decrStackSize(1);
/* 160 */             falseLabel.place();
/* 161 */             codeStream.iconst_0();
/* 162 */             endLabel.place();
/*     */           }
/*     */         }
/* 165 */         else falseLabel.place();
/*     */       }
/*     */ 
/* 168 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 169 */       codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */     } else {
/* 171 */       falseLabel.place();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*     */   {
/* 180 */     if (this.constant != Constant.NotAConstant) {
/* 181 */       super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, 
/* 182 */         valueRequired);
/* 183 */       return;
/*     */     }
/*     */ 
/* 187 */     Constant cst = this.right.constant;
/* 188 */     if ((cst != Constant.NotAConstant) && (cst.booleanValue())) {
/* 189 */       int pc = codeStream.position;
/* 190 */       this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
/* 191 */       if (this.mergedInitStateIndex != -1) {
/* 192 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/* 194 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 195 */       return;
/*     */     }
/* 197 */     cst = this.left.optimizedBooleanConstant();
/* 198 */     boolean leftIsConst = cst != Constant.NotAConstant;
/* 199 */     boolean leftIsTrue = (leftIsConst) && (cst.booleanValue());
/*     */ 
/* 201 */     cst = this.right.optimizedBooleanConstant();
/* 202 */     boolean rightIsConst = cst != Constant.NotAConstant;
/* 203 */     boolean rightIsTrue = (rightIsConst) && (cst.booleanValue());
/*     */ 
/* 207 */     if (falseLabel == null) {
/* 208 */       if (trueLabel != null)
/*     */       {
/* 210 */         BranchLabel internalFalseLabel = new BranchLabel(codeStream);
/* 211 */         this.left.generateOptimizedBoolean(currentScope, codeStream, null, internalFalseLabel, !leftIsConst);
/*     */ 
/* 213 */         if ((leftIsConst) && (!leftIsTrue)) {
/* 214 */           internalFalseLabel.place();
/*     */         }
/*     */         else {
/* 217 */           if (this.rightInitStateIndex != -1) {
/* 218 */             codeStream
/* 219 */               .addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
/*     */           }
/* 221 */           this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, 
/* 222 */             (valueRequired) && (!rightIsConst));
/* 223 */           if ((valueRequired) && (rightIsConst) && (rightIsTrue)) {
/* 224 */             codeStream.goto_(trueLabel);
/* 225 */             codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */           }
/* 227 */           internalFalseLabel.place();
/*     */         }
/*     */       }
/*     */     }
/* 231 */     else if (trueLabel == null) {
/* 232 */       this.left.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, !leftIsConst);
/*     */ 
/* 234 */       if ((leftIsConst) && (!leftIsTrue)) {
/* 235 */         if (valueRequired) codeStream.goto_(falseLabel);
/* 236 */         codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */       }
/*     */       else {
/* 239 */         if (this.rightInitStateIndex != -1) {
/* 240 */           codeStream
/* 241 */             .addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
/*     */         }
/* 243 */         this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, (valueRequired) && (!rightIsConst));
/* 244 */         if ((valueRequired) && (rightIsConst) && (!rightIsTrue)) {
/* 245 */           codeStream.goto_(falseLabel);
/* 246 */           codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 253 */     if (this.mergedInitStateIndex != -1)
/* 254 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */   }
/*     */ 
/*     */   public boolean isCompactableOperation()
/*     */   {
/* 259 */     return false;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 266 */     TypeBinding result = super.resolveType(scope);
/*     */ 
/* 268 */     Binding leftDirect = Expression.getDirectBinding(this.left);
/* 269 */     if ((leftDirect != null) && (leftDirect == Expression.getDirectBinding(this.right)) && 
/* 270 */       (!(this.right instanceof Assignment))) {
/* 271 */       scope.problemReporter().comparingIdenticalExpressions(this);
/*     */     }
/* 273 */     return result;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 277 */     if (visitor.visit(this, scope)) {
/* 278 */       this.left.traverse(visitor, scope);
/* 279 */       this.right.traverse(visitor, scope);
/*     */     }
/* 281 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression
 * JD-Core Version:    0.6.0
 */