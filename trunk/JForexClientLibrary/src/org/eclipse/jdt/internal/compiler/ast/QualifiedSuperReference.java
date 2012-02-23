/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class QualifiedSuperReference extends QualifiedThisReference
/*    */ {
/*    */   public QualifiedSuperReference(TypeReference name, int pos, int sourceEnd)
/*    */   {
/* 19 */     super(name, pos, sourceEnd);
/*    */   }
/*    */ 
/*    */   public boolean isSuper() {
/* 23 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean isThis() {
/* 27 */     return false;
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 31 */     return this.qualification.print(0, output).append(".super");
/*    */   }
/*    */ 
/*    */   public TypeBinding resolveType(BlockScope scope) {
/* 35 */     if ((this.bits & 0x1FE00000) != 0) {
/* 36 */       scope.problemReporter().invalidParenthesizedExpression(this);
/* 37 */       return null;
/*    */     }
/* 39 */     super.resolveType(scope);
/* 40 */     if (this.currentCompatibleType == null) {
/* 41 */       return null;
/*    */     }
/* 43 */     if (this.currentCompatibleType.id == 1) {
/* 44 */       scope.problemReporter().cannotUseSuperInJavaLangObject(this);
/* 45 */       return null;
/*    */     }
/* 47 */     return this.resolvedType = this.currentCompatibleType.superclass();
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*    */   {
/* 54 */     if (visitor.visit(this, blockScope)) {
/* 55 */       this.qualification.traverse(visitor, blockScope);
/*    */     }
/* 57 */     visitor.endVisit(this, blockScope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope blockScope)
/*    */   {
/* 63 */     if (visitor.visit(this, blockScope)) {
/* 64 */       this.qualification.traverse(visitor, blockScope);
/*    */     }
/* 66 */     visitor.endVisit(this, blockScope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference
 * JD-Core Version:    0.6.0
 */