/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ 
/*    */ public class PrefixExpression extends CompoundAssignment
/*    */ {
/*    */   public PrefixExpression(Expression lhs, Expression expression, int operator, int pos)
/*    */   {
/* 25 */     super(lhs, expression, operator, lhs.sourceEnd);
/* 26 */     this.sourceStart = pos;
/* 27 */     this.sourceEnd = lhs.sourceEnd;
/*    */   }
/*    */   public boolean checkCastCompatibility() {
/* 30 */     return false;
/*    */   }
/*    */   public String operatorToString() {
/* 33 */     switch (this.operator) {
/*    */     case 14:
/* 35 */       return "++";
/*    */     case 13:
/* 37 */       return "--";
/*    */     }
/* 39 */     return "unknown operator";
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output)
/*    */   {
/* 44 */     output.append(operatorToString()).append(' ');
/* 45 */     return this.lhs.printExpression(0, output);
/*    */   }
/*    */ 
/*    */   public boolean restrainUsageToNumericTypes() {
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 53 */     if (visitor.visit(this, scope)) {
/* 54 */       this.lhs.traverse(visitor, scope);
/*    */     }
/* 56 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.PrefixExpression
 * JD-Core Version:    0.6.0
 */