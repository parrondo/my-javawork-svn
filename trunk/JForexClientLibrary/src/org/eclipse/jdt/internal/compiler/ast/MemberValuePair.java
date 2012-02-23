/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class MemberValuePair extends ASTNode
/*     */ {
/*     */   public char[] name;
/*     */   public Expression value;
/*     */   public MethodBinding binding;
/*  33 */   public ElementValuePair compilerElementPair = null;
/*     */ 
/*     */   public MemberValuePair(char[] token, int sourceStart, int sourceEnd, Expression value) {
/*  36 */     this.name = token;
/*  37 */     this.sourceStart = sourceStart;
/*  38 */     this.sourceEnd = sourceEnd;
/*  39 */     this.value = value;
/*  40 */     if ((value instanceof ArrayInitializer))
/*  41 */       value.bits |= 1;
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int indent, StringBuffer output)
/*     */   {
/*  49 */     output
/*  50 */       .append(this.name)
/*  51 */       .append(" = ");
/*  52 */     this.value.print(0, output);
/*  53 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolveTypeExpecting(BlockScope scope, TypeBinding requiredType)
/*     */   {
/*  58 */     if (this.value == null) {
/*  59 */       this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
/*  60 */       return;
/*     */     }
/*  62 */     if (requiredType == null)
/*     */     {
/*  64 */       if ((this.value instanceof ArrayInitializer))
/*  65 */         this.value.resolveTypeExpecting(scope, null);
/*     */       else {
/*  67 */         this.value.resolveType(scope);
/*     */       }
/*  69 */       this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
/*  70 */       return;
/*     */     }
/*     */ 
/*  73 */     this.value.setExpectedType(requiredType);
/*     */     TypeBinding valueType;
/*     */     TypeBinding valueType;
/*  75 */     if ((this.value instanceof ArrayInitializer)) {
/*  76 */       ArrayInitializer initializer = (ArrayInitializer)this.value;
/*  77 */       valueType = initializer.resolveTypeExpecting(scope, this.binding.returnType);
/*     */     }
/*     */     else
/*     */     {
/*     */       TypeBinding valueType;
/*  78 */       if ((this.value instanceof ArrayAllocationExpression)) {
/*  79 */         scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
/*  80 */         this.value.resolveType(scope);
/*  81 */         valueType = null;
/*     */       } else {
/*  83 */         valueType = this.value.resolveType(scope);
/*     */       }
/*     */     }
/*  85 */     this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
/*  86 */     if (valueType == null) {
/*  87 */       return;
/*     */     }
/*  89 */     TypeBinding leafType = requiredType.leafComponentType();
/*  90 */     if ((!this.value.isConstantValueOfTypeAssignableToType(valueType, requiredType)) && 
/*  91 */       (!valueType.isCompatibleWith(requiredType)))
/*     */     {
/*  93 */       if ((!requiredType.isArrayType()) || 
/*  94 */         (requiredType.dimensions() != 1) || (
/*  95 */         (!this.value.isConstantValueOfTypeAssignableToType(valueType, leafType)) && 
/*  96 */         (!valueType.isCompatibleWith(leafType))))
/*     */       {
/*  98 */         if ((leafType.isAnnotationType()) && (!valueType.isAnnotationType()))
/*  99 */           scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, this.value, leafType);
/*     */         else {
/* 101 */           scope.problemReporter().typeMismatchError(valueType, requiredType, this.value, null);
/*     */         }
/* 103 */         return;
/*     */       }
/*     */     } else {
/* 106 */       scope.compilationUnitScope().recordTypeConversion(requiredType.leafComponentType(), valueType.leafComponentType());
/* 107 */       this.value.computeConversion(scope, requiredType, valueType);
/*     */     }
/*     */ 
/* 112 */     switch (leafType.erasure().id) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/* 122 */       if ((this.value instanceof ArrayInitializer)) {
/* 123 */         ArrayInitializer initializer = (ArrayInitializer)this.value;
/* 124 */         Expression[] expressions = initializer.expressions;
/* 125 */         if (expressions == null) break;
/* 126 */         int i = 0; for (int max = expressions.length; i < max; i++) {
/* 127 */           Expression expression = expressions[i];
/* 128 */           if ((expression.resolvedType == null) || 
/* 129 */             (expression.constant != Constant.NotAConstant)) continue;
/* 130 */           scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, expressions[i], false);
/*     */         }
/*     */       }
/*     */       else {
/* 134 */         if (this.value.constant != Constant.NotAConstant) break;
/* 135 */         if (valueType.isArrayType())
/* 136 */           scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
/*     */         else {
/* 138 */           scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, false);
/*     */         }
/*     */       }
/* 141 */       break;
/*     */     case 16:
/* 143 */       if ((this.value instanceof ArrayInitializer)) {
/* 144 */         ArrayInitializer initializer = (ArrayInitializer)this.value;
/* 145 */         Expression[] expressions = initializer.expressions;
/* 146 */         if (expressions == null) break;
/* 147 */         int i = 0; for (int max = expressions.length; i < max; i++) {
/* 148 */           Expression currentExpression = expressions[i];
/* 149 */           if (!(currentExpression instanceof ClassLiteralAccess))
/* 150 */             scope.problemReporter().annotationValueMustBeClassLiteral(this.binding.declaringClass, this.name, currentExpression);
/*     */         }
/*     */       }
/*     */       else {
/* 154 */         if ((this.value instanceof ClassLiteralAccess)) break;
/* 155 */         scope.problemReporter().annotationValueMustBeClassLiteral(this.binding.declaringClass, this.name, this.value);
/*     */       }
/* 157 */       break;
/*     */     case 6:
/*     */     case 12:
/*     */     case 13:
/*     */     case 14:
/*     */     case 15:
/*     */     default:
/* 159 */       if (leafType.isEnum()) {
/* 160 */         if ((this.value instanceof NullLiteral)) {
/* 161 */           scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
/* 162 */         } else if ((this.value instanceof ArrayInitializer)) {
/* 163 */           ArrayInitializer initializer = (ArrayInitializer)this.value;
/* 164 */           Expression[] expressions = initializer.expressions;
/* 165 */           if (expressions == null) break;
/* 166 */           int i = 0; for (int max = expressions.length; i < max; i++) {
/* 167 */             Expression currentExpression = expressions[i];
/* 168 */             if ((currentExpression instanceof NullLiteral)) {
/* 169 */               scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, currentExpression, true);
/* 170 */             } else if ((currentExpression instanceof NameReference)) {
/* 171 */               NameReference nameReference = (NameReference)currentExpression;
/* 172 */               Binding nameReferenceBinding = nameReference.binding;
/* 173 */               if (nameReferenceBinding.kind() == 1) {
/* 174 */                 FieldBinding fieldBinding = (FieldBinding)nameReferenceBinding;
/* 175 */                 if (!fieldBinding.declaringClass.isEnum()) {
/* 176 */                   scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, currentExpression, true);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 182 */         else if ((this.value instanceof NameReference)) {
/* 183 */           NameReference nameReference = (NameReference)this.value;
/* 184 */           Binding nameReferenceBinding = nameReference.binding;
/* 185 */           if (nameReferenceBinding.kind() != 1) break;
/* 186 */           FieldBinding fieldBinding = (FieldBinding)nameReferenceBinding;
/* 187 */           if (fieldBinding.declaringClass.isEnum()) break;
/* 188 */           if (!fieldBinding.type.isArrayType())
/* 189 */             scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
/*     */           else {
/* 191 */             scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 196 */           scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
/*     */         }
/*     */       }
/*     */       else {
/* 200 */         if (!leafType.isAnnotationType()) break;
/* 201 */         if (!valueType.leafComponentType().isAnnotationType()) {
/* 202 */           scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, this.value, leafType);
/* 203 */         } else if ((this.value instanceof ArrayInitializer)) {
/* 204 */           ArrayInitializer initializer = (ArrayInitializer)this.value;
/* 205 */           Expression[] expressions = initializer.expressions;
/* 206 */           if (expressions == null) break;
/* 207 */           int i = 0; for (int max = expressions.length; i < max; i++) {
/* 208 */             Expression currentExpression = expressions[i];
/* 209 */             if (((currentExpression instanceof NullLiteral)) || (!(currentExpression instanceof Annotation)))
/* 210 */               scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, currentExpression, leafType);
/*     */           }
/*     */         }
/*     */         else {
/* 214 */           if ((this.value instanceof Annotation)) break;
/* 215 */           scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, this.value, leafType);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 223 */     if ((visitor.visit(this, scope)) && 
/* 224 */       (this.value != null)) {
/* 225 */       this.value.traverse(visitor, scope);
/*     */     }
/*     */ 
/* 228 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 * JD-Core Version:    0.6.0
 */