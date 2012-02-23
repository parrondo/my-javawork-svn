/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public abstract class TypeReference extends Expression
/*     */ {
/*     */   public static final TypeReference baseTypeReference(int baseType, int dim)
/*     */   {
/*  36 */     if (dim == 0) {
/*  37 */       switch (baseType) {
/*     */       case 6:
/*  39 */         return new SingleTypeReference(TypeBinding.VOID.simpleName, 0L);
/*     */       case 5:
/*  41 */         return new SingleTypeReference(TypeBinding.BOOLEAN.simpleName, 0L);
/*     */       case 2:
/*  43 */         return new SingleTypeReference(TypeBinding.CHAR.simpleName, 0L);
/*     */       case 9:
/*  45 */         return new SingleTypeReference(TypeBinding.FLOAT.simpleName, 0L);
/*     */       case 8:
/*  47 */         return new SingleTypeReference(TypeBinding.DOUBLE.simpleName, 0L);
/*     */       case 3:
/*  49 */         return new SingleTypeReference(TypeBinding.BYTE.simpleName, 0L);
/*     */       case 4:
/*  51 */         return new SingleTypeReference(TypeBinding.SHORT.simpleName, 0L);
/*     */       case 10:
/*  53 */         return new SingleTypeReference(TypeBinding.INT.simpleName, 0L);
/*     */       case 7:
/*  55 */       }return new SingleTypeReference(TypeBinding.LONG.simpleName, 0L);
/*     */     }
/*     */ 
/*  58 */     switch (baseType) {
/*     */     case 6:
/*  60 */       return new ArrayTypeReference(TypeBinding.VOID.simpleName, dim, 0L);
/*     */     case 5:
/*  62 */       return new ArrayTypeReference(TypeBinding.BOOLEAN.simpleName, dim, 0L);
/*     */     case 2:
/*  64 */       return new ArrayTypeReference(TypeBinding.CHAR.simpleName, dim, 0L);
/*     */     case 9:
/*  66 */       return new ArrayTypeReference(TypeBinding.FLOAT.simpleName, dim, 0L);
/*     */     case 8:
/*  68 */       return new ArrayTypeReference(TypeBinding.DOUBLE.simpleName, dim, 0L);
/*     */     case 3:
/*  70 */       return new ArrayTypeReference(TypeBinding.BYTE.simpleName, dim, 0L);
/*     */     case 4:
/*  72 */       return new ArrayTypeReference(TypeBinding.SHORT.simpleName, dim, 0L);
/*     */     case 10:
/*  74 */       return new ArrayTypeReference(TypeBinding.INT.simpleName, dim, 0L);
/*     */     case 7:
/*  76 */     }return new ArrayTypeReference(TypeBinding.LONG.simpleName, dim, 0L);
/*     */   }
/*     */ 
/*     */   public void aboutToResolve(Scope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  85 */     return flowInfo;
/*     */   }
/*     */   public void checkBounds(Scope scope) {
/*     */   }
/*     */   public abstract TypeReference copyDims(int paramInt);
/*     */ 
/*  92 */   public int dimensions() { return 0;
/*     */   }
/*     */ 
/*     */   public abstract char[] getLastToken();
/*     */ 
/*     */   public char[][] getParameterizedTypeName()
/*     */   {
/* 102 */     return getTypeName();
/*     */   }
/*     */ 
/*     */   protected abstract TypeBinding getTypeBinding(Scope paramScope);
/*     */ 
/*     */   public abstract char[][] getTypeName();
/*     */ 
/*     */   protected TypeBinding internalResolveType(Scope scope) {
/* 112 */     this.constant = Constant.NotAConstant;
/* 113 */     if (this.resolvedType != null) {
/* 114 */       if (this.resolvedType.isValidBinding()) {
/* 115 */         return this.resolvedType;
/*     */       }
/* 117 */       switch (this.resolvedType.problemId()) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 5:
/* 121 */         TypeBinding type = this.resolvedType.closestMatch();
/* 122 */         if (type == null) return null;
/* 123 */         return scope.environment().convertToRawType(type, false);
/*     */       case 3:
/* 125 */       case 4: } return null;
/*     */     }
/*     */ 
/* 130 */     TypeBinding type = this.resolvedType = getTypeBinding(scope);
/* 131 */     if (type == null)
/* 132 */       return null;
/*     */     boolean hasError;
/* 133 */     if ((hasError = type.isValidBinding() ? 0 : 1) != 0) {
/* 134 */       reportInvalidType(scope);
/* 135 */       switch (type.problemId()) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 5:
/* 139 */         type = type.closestMatch();
/* 140 */         if (type != null) break; return null;
/*     */       case 3:
/*     */       case 4:
/*     */       default:
/* 143 */         return null;
/*     */       }
/*     */     }
/* 146 */     if ((type.isArrayType()) && (((ArrayBinding)type).leafComponentType == TypeBinding.VOID)) {
/* 147 */       scope.problemReporter().cannotAllocateVoidArray(this);
/* 148 */       return null;
/*     */     }
/* 150 */     if (isTypeUseDeprecated(type, scope)) {
/* 151 */       reportDeprecatedType(type, scope);
/*     */     }
/* 153 */     type = scope.environment().convertToRawType(type, false);
/* 154 */     if ((type.leafComponentType().isRawType()) && 
/* 155 */       ((this.bits & 0x40000000) == 0) && 
/* 156 */       (scope.compilerOptions().getSeverity(536936448) != -1)) {
/* 157 */       scope.problemReporter().rawTypeReference(this, type);
/*     */     }
/* 159 */     if (hasError)
/*     */     {
/* 161 */       return type;
/*     */     }
/* 163 */     return this.resolvedType = type;
/*     */   }
/*     */   public boolean isTypeReference() {
/* 166 */     return true;
/*     */   }
/*     */ 
/*     */   protected void reportDeprecatedType(TypeBinding type, Scope scope) {
/* 170 */     scope.problemReporter().deprecatedType(type, this);
/*     */   }
/*     */ 
/*     */   protected void reportInvalidType(Scope scope) {
/* 174 */     scope.problemReporter().invalidType(this, this.resolvedType);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveSuperType(ClassScope scope)
/*     */   {
/* 179 */     TypeBinding superType = resolveType(scope);
/* 180 */     if (superType == null) return null;
/*     */ 
/* 182 */     if (superType.isTypeVariable()) {
/* 183 */       if (this.resolvedType.isValidBinding()) {
/* 184 */         this.resolvedType = new ProblemReferenceBinding(getTypeName(), (ReferenceBinding)this.resolvedType, 9);
/* 185 */         reportInvalidType(scope);
/*     */       }
/* 187 */       return null;
/*     */     }
/* 189 */     return superType;
/*     */   }
/*     */ 
/*     */   public final TypeBinding resolveType(BlockScope blockScope) {
/* 193 */     return resolveType(blockScope, true);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
/* 197 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(ClassScope scope) {
/* 201 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveTypeArgument(BlockScope blockScope, ReferenceBinding genericType, int rank) {
/* 205 */     return resolveType(blockScope, true);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveTypeArgument(ClassScope classScope, ReferenceBinding genericType, int rank) {
/* 209 */     return resolveType(classScope);
/*     */   }
/*     */ 
/*     */   public abstract void traverse(ASTVisitor paramASTVisitor, BlockScope paramBlockScope);
/*     */ 
/*     */   public abstract void traverse(ASTVisitor paramASTVisitor, ClassScope paramClassScope);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.TypeReference
 * JD-Core Version:    0.6.0
 */