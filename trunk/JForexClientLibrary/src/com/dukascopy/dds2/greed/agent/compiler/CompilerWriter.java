/*    */ package com.dukascopy.dds2.greed.agent.compiler;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ public class CompilerWriter extends PrintWriter
/*    */ {
/*    */   public static final int LINE_CORRECTION = 12;
/*    */ 
/*    */   public CompilerWriter(OutputStream out)
/*    */   {
/* 15 */     super(out, true);
/*    */   }
/*    */ 
/*    */   public void print(String s)
/*    */   {
/* 22 */     String[] strs = s.split("\\n");
/* 23 */     for (int i = 0; i < strs.length; i++) {
/* 24 */       super.print(strs[i]);
/* 25 */       super.flush();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.compiler.CompilerWriter
 * JD-Core Version:    0.6.0
 */