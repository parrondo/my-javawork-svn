/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ public abstract class MagicLiteral extends Literal
/*    */ {
/*    */   public MagicLiteral(int start, int end)
/*    */   {
/* 17 */     super(start, end);
/*    */   }
/*    */ 
/*    */   public boolean isValidJavaStatement()
/*    */   {
/* 22 */     return false;
/*    */   }
/*    */ 
/*    */   public char[] source()
/*    */   {
/* 27 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.MagicLiteral
 * JD-Core Version:    0.6.0
 */