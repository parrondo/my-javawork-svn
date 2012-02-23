/*    */ package org.eclipse.jdt.internal.compiler.codegen;
/*    */ 
/*    */ public abstract class Label
/*    */ {
/*    */   public CodeStream codeStream;
/* 16 */   public int position = -1;
/*    */   public static final int POS_NOT_SET = -1;
/*    */ 
/*    */   public Label()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Label(CodeStream codeStream)
/*    */   {
/* 25 */     this.codeStream = codeStream;
/*    */   }
/*    */ 
/*    */   public abstract void place();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.Label
 * JD-Core Version:    0.6.0
 */