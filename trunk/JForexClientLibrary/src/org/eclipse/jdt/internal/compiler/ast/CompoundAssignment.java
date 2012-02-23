/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class CompoundAssignment extends Assignment
/*     */   implements OperatorIds
/*     */ {
/*     */   public int operator;
/*     */   public int preAssignImplicitConversion;
/*     */ 
/*     */   public CompoundAssignment(Expression lhs, Expression expression, int operator, int sourceEnd)
/*     */   {
/*  31 */     super(lhs, expression, sourceEnd);
/*  32 */     lhs.bits &= -8193;
/*  33 */     lhs.bits |= 65536;
/*  34 */     this.operator = operator;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  42 */     if (this.resolvedType.id != 11) {
/*  43 */       this.lhs.checkNPE(currentScope, flowContext, flowInfo);
/*     */     }
/*  45 */     return ((Reference)this.lhs).analyseAssignment(currentScope, flowContext, flowInfo, this, true).unconditionalInits();
/*     */   }
/*     */ 
/*     */   public boolean checkCastCompatibility() {
/*  49 */     return true;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  57 */     int pc = codeStream.position;
/*  58 */     ((Reference)this.lhs).generateCompoundAssignment(currentScope, codeStream, this.expression, this.operator, this.preAssignImplicitConversion, valueRequired);
/*  59 */     if (valueRequired) {
/*  60 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     }
/*  62 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/*  66 */     return -1;
/*     */   }
/*     */ 
/*     */   public String operatorToString()
/*     */   {
/*  71 */     switch (this.operator) {
/*     */     case 14:
/*  73 */       return "+=";
/*     */     case 13:
/*  75 */       return "-=";
/*     */     case 15:
/*  77 */       return "*=";
/*     */     case 9:
/*  79 */       return "/=";
/*     */     case 2:
/*  81 */       return "&=";
/*     */     case 3:
/*  83 */       return "|=";
/*     */     case 8:
/*  85 */       return "^=";
/*     */     case 16:
/*  87 */       return "%=";
/*     */     case 10:
/*  89 */       return "<<=";
/*     */     case 17:
/*  91 */       return ">>=";
/*     */     case 19:
/*  93 */       return ">>>=";
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 11:
/*     */     case 12:
/*  95 */     case 18: } return "unknown operator";
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output)
/*     */   {
/* 100 */     this.lhs.printExpression(indent, output).append(' ').append(operatorToString()).append(' ');
/* 101 */     return this.expression.printExpression(0, output);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope) {
/* 105 */     this.constant = Constant.NotAConstant;
/* 106 */     if ((!(this.lhs instanceof Reference)) || (this.lhs.isThis())) {
/* 107 */       scope.problemReporter().expressionShouldBeAVariable(this.lhs);
/* 108 */       return null;
/*     */     }
/* 110 */     TypeBinding originalLhsType = this.lhs.resolveType(scope);
/* 111 */     TypeBinding originalExpressionType = this.expression.resolveType(scope);
/* 112 */     if ((originalLhsType == null) || (originalExpressionType == null)) {
/* 113 */       return null;
/*     */     }
/*     */ 
/* 116 */     LookupEnvironment env = scope.environment();
/* 117 */     TypeBinding lhsType = originalLhsType; TypeBinding expressionType = originalExpressionType;
/* 118 */     boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
/* 119 */     boolean unboxedLhs = false;
/* 120 */     if (use15specifics) {
/* 121 */       if ((!lhsType.isBaseType()) && (expressionType.id != 11) && (expressionType.id != 12)) {
/* 122 */         TypeBinding unboxedType = env.computeBoxingType(lhsType);
/* 123 */         if (unboxedType != lhsType) {
/* 124 */           lhsType = unboxedType;
/* 125 */           unboxedLhs = true;
/*     */         }
/*     */       }
/* 128 */       if ((!expressionType.isBaseType()) && (lhsType.id != 11) && (lhsType.id != 12)) {
/* 129 */         expressionType = env.computeBoxingType(expressionType);
/*     */       }
/*     */     }
/*     */ 
/* 133 */     if ((restrainUsageToNumericTypes()) && (!lhsType.isNumericType())) {
/* 134 */       scope.problemReporter().operatorOnlyValidOnNumericType(this, lhsType, expressionType);
/* 135 */       return null;
/*     */     }
/* 137 */     int lhsID = lhsType.id;
/* 138 */     int expressionID = expressionType.id;
/* 139 */     if ((lhsID > 15) || (expressionID > 15)) {
/* 140 */       if (lhsID != 11) {
/* 141 */         scope.problemReporter().invalidOperator(this, lhsType, expressionType);
/* 142 */         return null;
/*     */       }
/* 144 */       expressionID = 1;
/*     */     }
/*     */ 
/* 153 */     int result = OperatorExpression.OperatorSignatures[this.operator][((lhsID << 4) + expressionID)];
/* 154 */     if (result == 0) {
/* 155 */       scope.problemReporter().invalidOperator(this, lhsType, expressionType);
/* 156 */       return null;
/*     */     }
/* 158 */     if (this.operator == 14) {
/* 159 */       if ((lhsID == 1) && (scope.compilerOptions().complianceLevel < 3342336L))
/*     */       {
/* 161 */         scope.problemReporter().invalidOperator(this, lhsType, expressionType);
/* 162 */         return null;
/*     */       }
/*     */ 
/* 165 */       if (((lhsType.isNumericType()) || (lhsID == 5)) && (!expressionType.isNumericType())) {
/* 166 */         scope.problemReporter().invalidOperator(this, lhsType, expressionType);
/* 167 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 171 */     TypeBinding resultType = TypeBinding.wellKnownType(scope, result & 0xF);
/* 172 */     if ((checkCastCompatibility()) && 
/* 173 */       (originalLhsType.id != 11) && (resultType.id != 11) && 
/* 174 */       (!checkCastTypesCompatibility(scope, originalLhsType, resultType, null))) {
/* 175 */       scope.problemReporter().invalidOperator(this, originalLhsType, expressionType);
/* 176 */       return null;
/*     */     }
/*     */ 
/* 180 */     this.lhs.computeConversion(scope, TypeBinding.wellKnownType(scope, result >>> 16 & 0xF), originalLhsType);
/* 181 */     this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, result >>> 8 & 0xF), originalExpressionType);
/* 182 */     this.preAssignImplicitConversion = ((unboxedLhs ? 512 : 0) | lhsID << 4 | result & 0xF);
/* 183 */     if (unboxedLhs) scope.problemReporter().autoboxing(this, lhsType, originalLhsType);
/* 184 */     return this.resolvedType = originalLhsType;
/*     */   }
/*     */ 
/*     */   public boolean restrainUsageToNumericTypes() {
/* 188 */     return false;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 192 */     if (visitor.visit(this, scope)) {
/* 193 */       this.lhs.traverse(visitor, scope);
/* 194 */       this.expression.traverse(visitor, scope);
/*     */     }
/* 196 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.CompoundAssignment
 * JD-Core Version:    0.6.0
 */