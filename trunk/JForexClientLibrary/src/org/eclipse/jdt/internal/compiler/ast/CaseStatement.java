/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CaseLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.IntConstant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class CaseStatement extends Statement
/*     */ {
/*     */   public Expression constantExpression;
/*     */   public CaseLabel targetLabel;
/*     */ 
/*     */   public CaseStatement(Expression constantExpression, int sourceEnd, int sourceStart)
/*     */   {
/*  33 */     this.constantExpression = constantExpression;
/*  34 */     this.sourceEnd = sourceEnd;
/*  35 */     this.sourceStart = sourceStart;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  43 */     if (this.constantExpression != null) {
/*  44 */       if ((this.constantExpression.constant == Constant.NotAConstant) && 
/*  45 */         (!this.constantExpression.resolvedType.isEnum())) {
/*  46 */         currentScope.problemReporter().caseExpressionMustBeConstant(this.constantExpression);
/*     */       }
/*  48 */       this.constantExpression.analyseCode(currentScope, flowContext, flowInfo);
/*     */     }
/*  50 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int tab, StringBuffer output) {
/*  54 */     printIndent(tab, output);
/*  55 */     if (this.constantExpression == null) {
/*  56 */       output.append("default : ");
/*     */     } else {
/*  58 */       output.append("case ");
/*  59 */       this.constantExpression.printExpression(0, output).append(" : ");
/*     */     }
/*  61 */     return output.append(';');
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  69 */     if ((this.bits & 0x80000000) == 0) {
/*  70 */       return;
/*     */     }
/*  72 */     int pc = codeStream.position;
/*  73 */     this.targetLabel.place();
/*  74 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Constant resolveCase(BlockScope scope, TypeBinding switchExpressionType, SwitchStatement switchStatement)
/*     */   {
/*  90 */     scope.enclosingCase = this;
/*     */ 
/*  92 */     if (this.constantExpression == null)
/*     */     {
/*  94 */       if (switchStatement.defaultCase != null) {
/*  95 */         scope.problemReporter().duplicateDefaultCase(this);
/*     */       }
/*     */ 
/*  98 */       switchStatement.defaultCase = this;
/*  99 */       return Constant.NotAConstant;
/*     */     }
/*     */ 
/* 102 */     switchStatement.cases[(switchStatement.caseCount++)] = this;
/*     */ 
/* 104 */     if ((switchExpressionType != null) && (switchExpressionType.isEnum()) && ((this.constantExpression instanceof SingleNameReference))) {
/* 105 */       ((SingleNameReference)this.constantExpression).setActualReceiverType((ReferenceBinding)switchExpressionType);
/*     */     }
/* 107 */     TypeBinding caseType = this.constantExpression.resolveType(scope);
/* 108 */     if ((caseType == null) || (switchExpressionType == null)) return Constant.NotAConstant;
/* 109 */     if ((this.constantExpression.isConstantValueOfTypeAssignableToType(caseType, switchExpressionType)) || 
/* 110 */       (caseType.isCompatibleWith(switchExpressionType))) {
/* 111 */       if (caseType.isEnum()) {
/* 112 */         if ((this.constantExpression.bits & 0x1FE00000) >> 21 != 0) {
/* 113 */           scope.problemReporter().enumConstantsCannotBeSurroundedByParenthesis(this.constantExpression);
/*     */         }
/*     */ 
/* 116 */         if (((this.constantExpression instanceof NameReference)) && 
/* 117 */           ((this.constantExpression.bits & 0x7) == 1)) {
/* 118 */           NameReference reference = (NameReference)this.constantExpression;
/* 119 */           FieldBinding field = reference.fieldBinding();
/* 120 */           if ((field.modifiers & 0x4000) == 0)
/* 121 */             scope.problemReporter().enumSwitchCannotTargetField(reference, field);
/* 122 */           else if ((reference instanceof QualifiedNameReference)) {
/* 123 */             scope.problemReporter().cannotUseQualifiedEnumConstantInCaseLabel(reference, field);
/*     */           }
/* 125 */           return IntConstant.fromValue(field.original().id + 1);
/*     */         }
/*     */       } else {
/* 128 */         return this.constantExpression.constant;
/*     */       }
/* 130 */     } else if (isBoxingCompatible(caseType, switchExpressionType, this.constantExpression, scope))
/*     */     {
/* 132 */       return this.constantExpression.constant;
/*     */     }
/* 134 */     scope.problemReporter().typeMismatchError(caseType, switchExpressionType, this.constantExpression, switchStatement.expression);
/* 135 */     return Constant.NotAConstant;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 139 */     if ((visitor.visit(this, blockScope)) && 
/* 140 */       (this.constantExpression != null)) this.constantExpression.traverse(visitor, blockScope);
/*     */ 
/* 142 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.CaseStatement
 * JD-Core Version:    0.6.0
 */