/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class InstanceOfExpression extends OperatorExpression
/*     */ {
/*     */   public Expression expression;
/*     */   public TypeReference type;
/*     */ 
/*     */   public InstanceOfExpression(Expression expression, TypeReference type)
/*     */   {
/*  25 */     this.expression = expression;
/*  26 */     this.type = type;
/*  27 */     this.bits |= 1984;
/*  28 */     this.sourceStart = expression.sourceStart;
/*  29 */     this.sourceEnd = type.sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/*  33 */     LocalVariableBinding local = this.expression.localVariableBinding();
/*  34 */     if ((local != null) && ((local.type.tagBits & 0x2) == 0L)) {
/*  35 */       flowContext.recordUsingNullReference(currentScope, local, 
/*  36 */         this.expression, 1025, flowInfo);
/*  37 */       flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo)
/*  38 */         .unconditionalInits();
/*  39 */       FlowInfo initsWhenTrue = flowInfo.copy();
/*  40 */       initsWhenTrue.markAsComparedEqualToNonNull(local);
/*     */ 
/*  42 */       return FlowInfo.conditional(initsWhenTrue, flowInfo.copy());
/*     */     }
/*  44 */     return this.expression.analyseCode(currentScope, flowContext, flowInfo)
/*  45 */       .unconditionalInits();
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  56 */     int pc = codeStream.position;
/*  57 */     this.expression.generateCode(currentScope, codeStream, true);
/*  58 */     codeStream.instance_of(this.type.resolvedType);
/*  59 */     if (valueRequired)
/*  60 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     else {
/*  62 */       codeStream.pop();
/*     */     }
/*  64 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
/*  68 */     this.expression.printExpression(indent, output).append(" instanceof ");
/*  69 */     return this.type.print(0, output);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope) {
/*  73 */     this.constant = Constant.NotAConstant;
/*  74 */     TypeBinding expressionType = this.expression.resolveType(scope);
/*  75 */     TypeBinding checkedType = this.type.resolveType(scope, true);
/*  76 */     if ((expressionType == null) || (checkedType == null)) {
/*  77 */       return null;
/*     */     }
/*  79 */     if (!checkedType.isReifiable())
/*  80 */       scope.problemReporter().illegalInstanceOfGenericType(checkedType, this);
/*  81 */     else if (((expressionType != TypeBinding.NULL) && (expressionType.isBaseType())) || 
/*  82 */       (!checkCastTypesCompatibility(scope, checkedType, expressionType, null))) {
/*  83 */       scope.problemReporter().notCompatibleTypesError(this, expressionType, checkedType);
/*     */     }
/*  85 */     return this.resolvedType = TypeBinding.BOOLEAN;
/*     */   }
/*     */ 
/*     */   public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType)
/*     */   {
/*  93 */     if (this.expression.resolvedType != TypeBinding.NULL)
/*  94 */       scope.problemReporter().unnecessaryInstanceof(this, castType);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/*  98 */     if (visitor.visit(this, scope)) {
/*  99 */       this.expression.traverse(visitor, scope);
/* 100 */       this.type.traverse(visitor, scope);
/*     */     }
/* 102 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression
 * JD-Core Version:    0.6.0
 */