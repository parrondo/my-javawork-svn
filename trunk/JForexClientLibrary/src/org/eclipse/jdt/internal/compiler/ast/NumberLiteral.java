/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ public abstract class NumberLiteral extends Literal
/*    */ {
/*    */   char[] source;
/*    */ 
/*    */   public NumberLiteral(char[] token, int s, int e)
/*    */   {
/* 18 */     this(s, e);
/* 19 */     this.source = token;
/*    */   }
/*    */ 
/*    */   public NumberLiteral(int s, int e) {
/* 23 */     super(s, e);
/*    */   }
/*    */ 
/*    */   public boolean isValidJavaStatement() {
/* 27 */     return false;
/*    */   }
/*    */ 
/*    */   public char[] source() {
/* 31 */     return this.source;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.NumberLiteral
 * JD-Core Version:    0.6.0
 */