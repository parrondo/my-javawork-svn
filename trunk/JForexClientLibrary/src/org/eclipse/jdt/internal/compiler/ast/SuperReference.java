/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class SuperReference extends ThisReference
/*    */ {
/*    */   public SuperReference(int sourceStart, int sourceEnd)
/*    */   {
/* 23 */     super(sourceStart, sourceEnd);
/*    */   }
/*    */ 
/*    */   public static ExplicitConstructorCall implicitSuperConstructorCall()
/*    */   {
/* 28 */     return new ExplicitConstructorCall(1);
/*    */   }
/*    */ 
/*    */   public boolean isImplicitThis()
/*    */   {
/* 33 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isSuper()
/*    */   {
/* 38 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean isThis()
/*    */   {
/* 43 */     return false;
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output)
/*    */   {
/* 48 */     return output.append("super");
/*    */   }
/*    */ 
/*    */   public TypeBinding resolveType(BlockScope scope)
/*    */   {
/* 54 */     this.constant = Constant.NotAConstant;
/* 55 */     if (!checkAccess(scope.methodScope()))
/* 56 */       return null;
/* 57 */     ReferenceBinding enclosingReceiverType = scope.enclosingReceiverType();
/* 58 */     if (enclosingReceiverType.id == 1) {
/* 59 */       scope.problemReporter().cannotUseSuperInJavaLangObject(this);
/* 60 */       return null;
/*    */     }
/* 62 */     return this.resolvedType = enclosingReceiverType.superclass();
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 66 */     visitor.visit(this, blockScope);
/* 67 */     visitor.endVisit(this, blockScope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.SuperReference
 * JD-Core Version:    0.6.0
 */