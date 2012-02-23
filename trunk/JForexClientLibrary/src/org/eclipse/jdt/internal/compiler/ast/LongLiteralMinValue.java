/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.impl.LongConstant;
/*    */ 
/*    */ public class LongLiteralMinValue extends LongLiteral
/*    */ {
/* 17 */   static final char[] CharValue = { '-', '9', '2', '2', '3', '3', '7', '2', '0', '3', '6', '8', '5', '4', '7', '7', '5', '8', '0', '8', 'L' };
/*    */ 
/*    */   public LongLiteralMinValue() {
/* 20 */     super(CharValue, 0, 0);
/* 21 */     this.constant = LongConstant.fromValue(-9223372036854775808L);
/*    */   }
/*    */ 
/*    */   public void computeConstant()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.LongLiteralMinValue
 * JD-Core Version:    0.6.0
 */