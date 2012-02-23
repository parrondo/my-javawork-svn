/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class JavadocArraySingleTypeReference extends ArrayTypeReference
/*    */ {
/*    */   public JavadocArraySingleTypeReference(char[] name, int dim, long pos)
/*    */   {
/* 23 */     super(name, dim, pos);
/* 24 */     this.bits |= 32768;
/*    */   }
/*    */ 
/*    */   protected void reportInvalidType(Scope scope) {
/* 28 */     scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
/*    */   }
/*    */   protected void reportDeprecatedType(TypeBinding type, Scope scope) {
/* 31 */     scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 39 */     visitor.visit(this, scope);
/* 40 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 44 */     visitor.visit(this, scope);
/* 45 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference
 * JD-Core Version:    0.6.0
 */