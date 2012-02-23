/*    */ package com.dukascopy.api.nlink.win32.engine;
/*    */ 
/*    */ public class MethodInfo
/*    */ {
/*    */   public String signature;
/*    */   public int jargsize;
/*    */   public int nargsize;
/*    */   public int[] argtypes;
/*    */ 
/*    */   public MethodInfo(String sig, int jas, int nas, int[] at)
/*    */   {
/* 10 */     this.signature = sig;
/* 11 */     this.jargsize = jas;
/* 12 */     this.nargsize = nas;
/* 13 */     this.argtypes = new int[at.length];
/* 14 */     System.arraycopy(at, 0, this.argtypes, 0, at.length);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.engine.MethodInfo
 * JD-Core Version:    0.6.0
 */