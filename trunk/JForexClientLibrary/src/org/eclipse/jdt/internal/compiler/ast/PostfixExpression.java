/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ 
/*    */ public class PostfixExpression extends CompoundAssignment
/*    */ {
/*    */   public PostfixExpression(Expression lhs, Expression expression, int operator, int pos)
/*    */   {
/* 20 */     super(lhs, expression, operator, pos);
/* 21 */     this.sourceStart = lhs.sourceStart;
/* 22 */     this.sourceEnd = pos;
/*    */   }
/*    */   public boolean checkCastCompatibility() {
/* 25 */     return false;
/*    */   }
/*    */ 
/*    */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*    */   {
/* 39 */     int pc = codeStream.position;
/* 40 */     ((Reference)this.lhs).generatePostIncrement(currentScope, codeStream, this, valueRequired);
/* 41 */     if (valueRequired) {
/* 42 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*    */     }
/* 44 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*    */   }
/*    */ 
/*    */   public String operatorToString() {
/* 48 */     switch (this.operator) {
/*    */     case 14:
/* 50 */       return "++";
/*    */     case 13:
/* 52 */       return "--";
/*    */     }
/* 54 */     return "unknown operator";
/*    */   }
/*    */ 
/*    */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
/* 58 */     return this.lhs.printExpression(indent, output).append(' ').append(operatorToString());
/*    */   }
/*    */ 
/*    */   public boolean restrainUsageToNumericTypes() {
/* 62 */     return true;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 67 */     if (visitor.visit(this, scope)) {
/* 68 */       this.lhs.traverse(visitor, scope);
/*    */     }
/* 70 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.PostfixExpression
 * JD-Core Version:    0.6.0
 */