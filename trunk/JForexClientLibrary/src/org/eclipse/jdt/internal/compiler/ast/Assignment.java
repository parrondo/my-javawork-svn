/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.NullInfoRegistry;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class Assignment extends Expression
/*     */ {
/*     */   public Expression lhs;
/*     */   public Expression expression;
/*     */ 
/*     */   public Assignment(Expression lhs, Expression expression, int sourceEnd)
/*     */   {
/*  28 */     this.lhs = lhs;
/*  29 */     lhs.bits |= 8192;
/*  30 */     this.expression = expression;
/*  31 */     this.sourceStart = lhs.sourceStart;
/*  32 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  39 */     LocalVariableBinding local = this.lhs.localVariableBinding();
/*  40 */     int nullStatus = this.expression.nullStatus(flowInfo);
/*  41 */     if ((local != null) && ((local.type.tagBits & 0x2) == 0L) && 
/*  42 */       (nullStatus == 1)) {
/*  43 */       flowContext.recordUsingNullReference(currentScope, local, this.lhs, 
/*  44 */         769, flowInfo);
/*     */     }
/*     */ 
/*  47 */     flowInfo = ((Reference)this.lhs)
/*  48 */       .analyseAssignment(currentScope, flowContext, flowInfo, this, false)
/*  49 */       .unconditionalInits();
/*  50 */     if ((local != null) && ((local.type.tagBits & 0x2) == 0L)) {
/*  51 */       switch (nullStatus) {
/*     */       case 1:
/*  53 */         flowInfo.markAsDefinitelyNull(local);
/*  54 */         break;
/*     */       case -1:
/*  56 */         flowInfo.markAsDefinitelyNonNull(local);
/*  57 */         break;
/*     */       case 0:
/*     */       default:
/*  59 */         flowInfo.markAsDefinitelyUnknown(local);
/*     */       }
/*  61 */       if (flowContext.initsOnFinally != null) {
/*  62 */         switch (nullStatus) {
/*     */         case 1:
/*  64 */           flowContext.initsOnFinally.markAsDefinitelyNull(local);
/*  65 */           break;
/*     */         case -1:
/*  67 */           flowContext.initsOnFinally.markAsDefinitelyNonNull(local);
/*  68 */           break;
/*     */         case 0:
/*     */         default:
/*  70 */           flowContext.initsOnFinally.markAsDefinitelyUnknown(local);
/*     */         }
/*     */       }
/*     */     }
/*  74 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   void checkAssignment(BlockScope scope, TypeBinding lhsType, TypeBinding rhsType) {
/*  78 */     FieldBinding leftField = getLastField(this.lhs);
/*  79 */     if ((leftField != null) && (rhsType != TypeBinding.NULL) && (lhsType.kind() == 516) && (((WildcardBinding)lhsType).boundKind != 2))
/*  80 */       scope.problemReporter().wildcardAssignment(lhsType, rhsType, this.expression);
/*  81 */     else if ((leftField != null) && (!leftField.isStatic()) && (leftField.declaringClass != null) && (leftField.declaringClass.isRawType()))
/*  82 */       scope.problemReporter().unsafeRawFieldAssignment(leftField, rhsType, this.lhs);
/*  83 */     else if (rhsType.needsUncheckedConversion(lhsType))
/*  84 */       scope.problemReporter().unsafeTypeConversion(this.expression, rhsType, lhsType);
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  93 */     int pc = codeStream.position;
/*  94 */     ((Reference)this.lhs).generateAssignment(currentScope, codeStream, this, valueRequired);
/*     */ 
/*  97 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   FieldBinding getLastField(Expression someExpression) {
/* 101 */     if ((someExpression instanceof SingleNameReference)) {
/* 102 */       if ((someExpression.bits & 0x7) == 1)
/* 103 */         return (FieldBinding)((SingleNameReference)someExpression).binding;
/*     */     } else {
/* 105 */       if ((someExpression instanceof FieldReference))
/* 106 */         return ((FieldReference)someExpression).binding;
/* 107 */       if ((someExpression instanceof QualifiedNameReference)) {
/* 108 */         QualifiedNameReference qName = (QualifiedNameReference)someExpression;
/* 109 */         if (qName.otherBindings == null) {
/* 110 */           if ((someExpression.bits & 0x7) == 1)
/* 111 */             return (FieldBinding)qName.binding;
/*     */         }
/*     */         else
/* 114 */           return qName.otherBindings[(qName.otherBindings.length - 1)];
/*     */       }
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 121 */     return this.expression.nullStatus(flowInfo);
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int indent, StringBuffer output)
/*     */   {
/* 126 */     printIndent(indent, output);
/* 127 */     return printExpressionNoParenthesis(indent, output);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 131 */     output.append('(');
/* 132 */     return printExpressionNoParenthesis(0, output).append(')');
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
/* 136 */     this.lhs.printExpression(indent, output).append(" = ");
/* 137 */     return this.expression.printExpression(0, output);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output)
/*     */   {
/* 142 */     return print(indent, output).append(';');
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 147 */     this.constant = Constant.NotAConstant;
/* 148 */     if ((!(this.lhs instanceof Reference)) || (this.lhs.isThis())) {
/* 149 */       scope.problemReporter().expressionShouldBeAVariable(this.lhs);
/* 150 */       return null;
/*     */     }
/* 152 */     TypeBinding lhsType = this.lhs.resolveType(scope);
/* 153 */     this.expression.setExpectedType(lhsType);
/* 154 */     if (lhsType != null) {
/* 155 */       this.resolvedType = lhsType.capture(scope, this.sourceEnd);
/*     */     }
/* 157 */     TypeBinding rhsType = this.expression.resolveType(scope);
/* 158 */     if ((lhsType == null) || (rhsType == null)) {
/* 159 */       return null;
/*     */     }
/*     */ 
/* 162 */     Binding left = getDirectBinding(this.lhs);
/* 163 */     if ((left != null) && (left == getDirectBinding(this.expression))) {
/* 164 */       scope.problemReporter().assignmentHasNoEffect(this, left.shortReadableName());
/*     */     }
/*     */ 
/* 169 */     if (lhsType != rhsType) {
/* 170 */       scope.compilationUnitScope().recordTypeConversion(lhsType, rhsType);
/*     */     }
/* 172 */     if ((this.expression.isConstantValueOfTypeAssignableToType(rhsType, lhsType)) || 
/* 173 */       (rhsType.isCompatibleWith(lhsType))) {
/* 174 */       this.expression.computeConversion(scope, lhsType, rhsType);
/* 175 */       checkAssignment(scope, lhsType, rhsType);
/* 176 */       if (((this.expression instanceof CastExpression)) && 
/* 177 */         ((this.expression.bits & 0x4000) == 0)) {
/* 178 */         CastExpression.checkNeedForAssignedCast(scope, lhsType, (CastExpression)this.expression);
/*     */       }
/* 180 */       return this.resolvedType;
/* 181 */     }if (isBoxingCompatible(rhsType, lhsType, this.expression, scope)) {
/* 182 */       this.expression.computeConversion(scope, lhsType, rhsType);
/* 183 */       if (((this.expression instanceof CastExpression)) && 
/* 184 */         ((this.expression.bits & 0x4000) == 0)) {
/* 185 */         CastExpression.checkNeedForAssignedCast(scope, lhsType, (CastExpression)this.expression);
/*     */       }
/* 187 */       return this.resolvedType;
/*     */     }
/* 189 */     scope.problemReporter().typeMismatchError(rhsType, lhsType, this.expression, this.lhs);
/* 190 */     return lhsType;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveTypeExpecting(BlockScope scope, TypeBinding expectedType)
/*     */   {
/* 198 */     TypeBinding type = super.resolveTypeExpecting(scope, expectedType);
/* 199 */     if (type == null) return null;
/* 200 */     TypeBinding lhsType = this.resolvedType;
/* 201 */     TypeBinding rhsType = this.expression.resolvedType;
/*     */ 
/* 203 */     if ((expectedType == TypeBinding.BOOLEAN) && 
/* 204 */       (lhsType == TypeBinding.BOOLEAN) && 
/* 205 */       ((this.lhs.bits & 0x2000) != 0)) {
/* 206 */       scope.problemReporter().possibleAccidentalBooleanAssignment(this);
/*     */     }
/* 208 */     checkAssignment(scope, lhsType, rhsType);
/* 209 */     return type;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 213 */     if (visitor.visit(this, scope)) {
/* 214 */       this.lhs.traverse(visitor, scope);
/* 215 */       this.expression.traverse(visitor, scope);
/*     */     }
/* 217 */     visitor.endVisit(this, scope);
/*     */   }
/*     */   public LocalVariableBinding localVariableBinding() {
/* 220 */     return this.lhs.localVariableBinding();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Assignment
 * JD-Core Version:    0.6.0
 */