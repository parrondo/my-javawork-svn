/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class JavadocArrayQualifiedTypeReference extends ArrayQualifiedTypeReference
/*    */ {
/*    */   public int tagSourceStart;
/*    */   public int tagSourceEnd;
/*    */ 
/*    */   public JavadocArrayQualifiedTypeReference(JavadocQualifiedTypeReference typeRef, int dim)
/*    */   {
/* 26 */     super(typeRef.tokens, dim, typeRef.sourcePositions);
/*    */   }
/*    */ 
/*    */   protected void reportInvalidType(Scope scope) {
/* 30 */     scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
/*    */   }
/*    */   protected void reportDeprecatedType(TypeBinding type, Scope scope) {
/* 33 */     scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 41 */     visitor.visit(this, scope);
/* 42 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 46 */     visitor.visit(this, scope);
/* 47 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference
 * JD-Core Version:    0.6.0
 */