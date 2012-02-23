/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class JavadocReturnStatement extends ReturnStatement
/*    */ {
/*    */   public JavadocReturnStatement(int s, int e)
/*    */   {
/* 20 */     super(null, s, e);
/* 21 */     this.bits |= 294912;
/*    */   }
/*    */ 
/*    */   public void resolve(BlockScope scope)
/*    */   {
/* 28 */     MethodScope methodScope = scope.methodScope();
/* 29 */     MethodBinding methodBinding = null;
/* 30 */     TypeBinding methodType = 
/* 31 */       (methodScope.referenceContext instanceof AbstractMethodDeclaration) ? 
/* 34 */       methodBinding.returnType : (methodBinding = ((AbstractMethodDeclaration)methodScope.referenceContext).binding) == null ? 
/* 33 */       null : 
/* 35 */       TypeBinding.VOID;
/* 36 */     if ((methodType == null) || (methodType == TypeBinding.VOID))
/* 37 */       scope.problemReporter().javadocUnexpectedTag(this.sourceStart, this.sourceEnd);
/* 38 */     else if ((this.bits & 0x40000) != 0)
/* 39 */       scope.problemReporter().javadocEmptyReturnTag(this.sourceStart, this.sourceEnd, scope.getDeclarationModifiers());
/*    */   }
/*    */ 
/*    */   public StringBuffer printStatement(int tab, StringBuffer output)
/*    */   {
/* 47 */     printIndent(tab, output).append("return");
/* 48 */     if ((this.bits & 0x40000) == 0)
/* 49 */       output.append(' ').append(" <not empty>");
/* 50 */     return output;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 58 */     visitor.visit(this, scope);
/* 59 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope)
/*    */   {
/* 66 */     visitor.visit(this, scope);
/* 67 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement
 * JD-Core Version:    0.6.0
 */