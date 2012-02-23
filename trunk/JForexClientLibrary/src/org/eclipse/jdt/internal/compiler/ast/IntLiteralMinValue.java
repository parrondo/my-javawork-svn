/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.impl.IntConstant;
/*    */ 
/*    */ public class IntLiteralMinValue extends IntLiteral
/*    */ {
/* 17 */   static final char[] CharValue = { '-', '2', '1', '4', '7', '4', '8', '3', '6', '4', '8' };
/*    */ 
/*    */   public IntLiteralMinValue() {
/* 20 */     super(CharValue, 0, 0, -2147483648);
/* 21 */     this.constant = IntConstant.fromValue(-2147483648);
/*    */   }
/*    */ 
/*    */   public void computeConstant()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.IntLiteralMinValue
 * JD-Core Version:    0.6.0
 */