/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class JavadocSingleTypeReference extends SingleTypeReference
/*     */ {
/*     */   public int tagSourceStart;
/*     */   public int tagSourceEnd;
/*     */   public PackageBinding packageBinding;
/*     */ 
/*     */   public JavadocSingleTypeReference(char[] source, long pos, int tagStart, int tagEnd)
/*     */   {
/*  30 */     super(source, pos);
/*  31 */     this.tagSourceStart = tagStart;
/*  32 */     this.tagSourceEnd = tagEnd;
/*  33 */     this.bits |= 32768;
/*     */   }
/*     */ 
/*     */   protected TypeBinding internalResolveType(Scope scope)
/*     */   {
/*  41 */     this.constant = Constant.NotAConstant;
/*  42 */     if (this.resolvedType != null) {
/*  43 */       if (this.resolvedType.isValidBinding()) {
/*  44 */         return this.resolvedType;
/*     */       }
/*  46 */       switch (this.resolvedType.problemId()) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 5:
/*  50 */         TypeBinding type = this.resolvedType.closestMatch();
/*  51 */         return type;
/*     */       case 3:
/*  53 */       case 4: } return null;
/*     */     }
/*     */ 
/*  57 */     this.resolvedType = getTypeBinding(scope);
/*     */ 
/*  61 */     if (this.resolvedType == null) return null;
/*     */ 
/*  63 */     if (!this.resolvedType.isValidBinding()) {
/*  64 */       char[][] tokens = { this.token };
/*  65 */       Binding binding = scope.getTypeOrPackage(tokens);
/*  66 */       if ((binding instanceof PackageBinding)) {
/*  67 */         this.packageBinding = ((PackageBinding)binding);
/*     */       }
/*     */       else {
/*  70 */         if (this.resolvedType.problemId() == 7) {
/*  71 */           TypeBinding closestMatch = this.resolvedType.closestMatch();
/*  72 */           if ((closestMatch != null) && (closestMatch.isTypeVariable())) {
/*  73 */             this.resolvedType = closestMatch;
/*  74 */             return this.resolvedType;
/*     */           }
/*     */         }
/*  77 */         reportInvalidType(scope);
/*     */       }
/*  79 */       return null;
/*     */     }
/*  81 */     if (isTypeUseDeprecated(this.resolvedType, scope)) {
/*  82 */       reportDeprecatedType(this.resolvedType, scope);
/*     */     }
/*     */ 
/*  85 */     if ((this.resolvedType.isGenericType()) || (this.resolvedType.isParameterizedType())) {
/*  86 */       this.resolvedType = scope.environment().convertToRawType(this.resolvedType, true);
/*     */     }
/*  88 */     return this.resolvedType;
/*     */   }
/*     */   protected void reportDeprecatedType(TypeBinding type, Scope scope) {
/*  91 */     scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
/*     */   }
/*     */ 
/*     */   protected void reportInvalidType(Scope scope) {
/*  95 */     scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 103 */     visitor.visit(this, scope);
/* 104 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 108 */     visitor.visit(this, scope);
/* 109 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference
 * JD-Core Version:    0.6.0
 */