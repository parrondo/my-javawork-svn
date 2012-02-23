/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class JavadocImplicitTypeReference extends TypeReference
/*     */ {
/*     */   public char[] token;
/*     */ 
/*     */   public JavadocImplicitTypeReference(char[] name, int pos)
/*     */   {
/*  23 */     this.token = name;
/*  24 */     this.sourceStart = pos;
/*  25 */     this.sourceEnd = pos;
/*     */   }
/*     */ 
/*     */   public TypeReference copyDims(int dim)
/*     */   {
/*  31 */     return null;
/*     */   }
/*     */ 
/*     */   protected TypeBinding getTypeBinding(Scope scope)
/*     */   {
/*  38 */     this.constant = Constant.NotAConstant;
/*  39 */     return this.resolvedType = scope.enclosingReceiverType();
/*     */   }
/*     */ 
/*     */   public char[] getLastToken() {
/*  43 */     return this.token;
/*     */   }
/*     */ 
/*     */   public char[][] getTypeName()
/*     */   {
/*  50 */     if (this.token != null) {
/*  51 */       char[][] tokens = { this.token };
/*  52 */       return tokens;
/*     */     }
/*  54 */     return null;
/*     */   }
/*     */   public boolean isThis() {
/*  57 */     return true;
/*     */   }
/*     */ 
/*     */   protected TypeBinding internalResolveType(Scope scope)
/*     */   {
/*  66 */     this.constant = Constant.NotAConstant;
/*  67 */     if (this.resolvedType != null) {
/*  68 */       if (this.resolvedType.isValidBinding()) {
/*  69 */         return this.resolvedType;
/*     */       }
/*  71 */       switch (this.resolvedType.problemId()) {
/*     */       case 1:
/*     */       case 2:
/*  74 */         TypeBinding type = this.resolvedType.closestMatch();
/*  75 */         return type;
/*     */       }
/*  77 */       return null;
/*     */     }
/*     */ 
/*  82 */     TypeBinding type = this.resolvedType = getTypeBinding(scope);
/*  83 */     if (type == null)
/*  84 */       return null;
/*     */     boolean hasError;
/*  85 */     if ((hasError = type.isValidBinding() ? 0 : 1) != 0) {
/*  86 */       reportInvalidType(scope);
/*  87 */       switch (type.problemId()) {
/*     */       case 1:
/*     */       case 2:
/*  90 */         type = type.closestMatch();
/*  91 */         if (type != null) break; return null;
/*     */       default:
/*  94 */         return null;
/*     */       }
/*     */     }
/*  97 */     if ((type.isArrayType()) && (((ArrayBinding)type).leafComponentType == TypeBinding.VOID)) {
/*  98 */       scope.problemReporter().cannotAllocateVoidArray(this);
/*  99 */       return null;
/*     */     }
/* 101 */     if (isTypeUseDeprecated(type, scope)) {
/* 102 */       reportDeprecatedType(type, scope);
/*     */     }
/*     */ 
/* 106 */     if ((type.isGenericType()) || (type.isParameterizedType())) {
/* 107 */       type = scope.environment().convertToRawType(type, true);
/*     */     }
/*     */ 
/* 110 */     if (hasError)
/*     */     {
/* 112 */       return type;
/*     */     }
/* 114 */     return this.resolvedType = type;
/*     */   }
/*     */ 
/*     */   protected void reportInvalidType(Scope scope) {
/* 118 */     scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
/*     */   }
/*     */   protected void reportDeprecatedType(TypeBinding type, Scope scope) {
/* 121 */     scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 125 */     visitor.visit(this, scope);
/* 126 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 130 */     visitor.visit(this, scope);
/* 131 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 135 */     return new StringBuffer();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference
 * JD-Core Version:    0.6.0
 */