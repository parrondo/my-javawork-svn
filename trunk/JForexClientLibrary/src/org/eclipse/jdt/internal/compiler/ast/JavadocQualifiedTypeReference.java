/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class JavadocQualifiedTypeReference extends QualifiedTypeReference
/*    */ {
/*    */   public int tagSourceStart;
/*    */   public int tagSourceEnd;
/*    */   public PackageBinding packageBinding;
/*    */ 
/*    */   public JavadocQualifiedTypeReference(char[][] sources, long[] pos, int tagStart, int tagEnd)
/*    */   {
/* 29 */     super(sources, pos);
/* 30 */     this.tagSourceStart = tagStart;
/* 31 */     this.tagSourceEnd = tagEnd;
/* 32 */     this.bits |= 32768;
/*    */   }
/*    */ 
/*    */   private TypeBinding internalResolveType(Scope scope, boolean checkBounds)
/*    */   {
/* 40 */     this.constant = Constant.NotAConstant;
/* 41 */     if (this.resolvedType != null) {
/* 42 */       return this.resolvedType.isValidBinding() ? this.resolvedType : this.resolvedType.closestMatch();
/*    */     }
/* 44 */     TypeBinding type = this.resolvedType = getTypeBinding(scope);
/*    */ 
/* 48 */     if (type == null) return null;
/* 49 */     if (!type.isValidBinding()) {
/* 50 */       Binding binding = scope.getTypeOrPackage(this.tokens);
/* 51 */       if ((binding instanceof PackageBinding)) {
/* 52 */         this.packageBinding = ((PackageBinding)binding);
/*    */       }
/*    */       else {
/* 55 */         reportInvalidType(scope);
/*    */       }
/* 57 */       return null;
/*    */     }
/* 59 */     if (isTypeUseDeprecated(type, scope)) {
/* 60 */       reportDeprecatedType(type, scope);
/*    */     }
/*    */ 
/* 63 */     if ((type.isGenericType()) || (type.isParameterizedType())) {
/* 64 */       this.resolvedType = scope.environment().convertToRawType(type, true);
/*    */     }
/* 66 */     return this.resolvedType;
/*    */   }
/*    */   protected void reportDeprecatedType(TypeBinding type, Scope scope) {
/* 69 */     scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
/*    */   }
/*    */ 
/*    */   protected void reportInvalidType(Scope scope) {
/* 73 */     scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
/*    */   }
/*    */   public TypeBinding resolveType(BlockScope blockScope, boolean checkBounds) {
/* 76 */     return internalResolveType(blockScope, checkBounds);
/*    */   }
/*    */ 
/*    */   public TypeBinding resolveType(ClassScope classScope) {
/* 80 */     return internalResolveType(classScope, false);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 88 */     visitor.visit(this, scope);
/* 89 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 93 */     visitor.visit(this, scope);
/* 94 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference
 * JD-Core Version:    0.6.0
 */