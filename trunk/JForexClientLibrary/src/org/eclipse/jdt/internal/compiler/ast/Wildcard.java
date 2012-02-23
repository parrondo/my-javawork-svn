/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*     */ 
/*     */ public class Wildcard extends SingleTypeReference
/*     */ {
/*     */   public static final int UNBOUND = 0;
/*     */   public static final int EXTENDS = 1;
/*     */   public static final int SUPER = 2;
/*     */   public TypeReference bound;
/*     */   public int kind;
/*     */ 
/*     */   public Wildcard(int kind)
/*     */   {
/*  30 */     super(WILDCARD_NAME, 0L);
/*  31 */     this.kind = kind;
/*     */   }
/*     */ 
/*     */   public char[][] getParameterizedTypeName() {
/*  35 */     switch (this.kind) {
/*     */     case 0:
/*  37 */       return new char[][] { WILDCARD_NAME };
/*     */     case 1:
/*  39 */       return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_EXTENDS, CharOperation.concatWith(this.bound.getParameterizedTypeName(), '.')) };
/*     */     }
/*  41 */     return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_SUPER, CharOperation.concatWith(this.bound.getParameterizedTypeName(), '.')) };
/*     */   }
/*     */ 
/*     */   public char[][] getTypeName()
/*     */   {
/*  46 */     switch (this.kind) {
/*     */     case 0:
/*  48 */       return new char[][] { WILDCARD_NAME };
/*     */     case 1:
/*  50 */       return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_EXTENDS, CharOperation.concatWith(this.bound.getTypeName(), '.')) };
/*     */     }
/*  52 */     return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_SUPER, CharOperation.concatWith(this.bound.getTypeName(), '.')) };
/*     */   }
/*     */ 
/*     */   private TypeBinding internalResolveType(Scope scope, ReferenceBinding genericType, int rank)
/*     */   {
/*  57 */     TypeBinding boundType = null;
/*  58 */     if (this.bound != null) {
/*  59 */       boundType = scope.kind == 3 ? 
/*  60 */         this.bound.resolveType((ClassScope)scope) : 
/*  61 */         this.bound.resolveType((BlockScope)scope, true);
/*     */ 
/*  63 */       if (boundType == null) {
/*  64 */         return null;
/*     */       }
/*     */     }
/*  67 */     WildcardBinding wildcard = scope.environment().createWildcard(genericType, rank, boundType, null, this.kind);
/*  68 */     return this.resolvedType = wildcard;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/*  72 */     switch (this.kind) {
/*     */     case 0:
/*  74 */       output.append(WILDCARD_NAME);
/*  75 */       break;
/*     */     case 1:
/*  77 */       output.append(WILDCARD_NAME).append(WILDCARD_EXTENDS);
/*  78 */       this.bound.printExpression(0, output);
/*  79 */       break;
/*     */     default:
/*  81 */       output.append(WILDCARD_NAME).append(WILDCARD_SUPER);
/*  82 */       this.bound.printExpression(0, output);
/*     */     }
/*     */ 
/*  85 */     return output;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope, boolean checkBounds)
/*     */   {
/*  90 */     if (this.bound != null) {
/*  91 */       this.bound.resolveType(scope, checkBounds);
/*     */     }
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(ClassScope scope) {
/*  97 */     if (this.bound != null) {
/*  98 */       this.bound.resolveType(scope);
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */   public TypeBinding resolveTypeArgument(BlockScope blockScope, ReferenceBinding genericType, int rank) {
/* 103 */     return internalResolveType(blockScope, genericType, rank);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveTypeArgument(ClassScope classScope, ReferenceBinding genericType, int rank) {
/* 107 */     return internalResolveType(classScope, genericType, rank);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 111 */     if ((visitor.visit(this, scope)) && 
/* 112 */       (this.bound != null)) {
/* 113 */       this.bound.traverse(visitor, scope);
/*     */     }
/*     */ 
/* 116 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 120 */     if ((visitor.visit(this, scope)) && 
/* 121 */       (this.bound != null)) {
/* 122 */       this.bound.traverse(visitor, scope);
/*     */     }
/*     */ 
/* 125 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Wildcard
 * JD-Core Version:    0.6.0
 */