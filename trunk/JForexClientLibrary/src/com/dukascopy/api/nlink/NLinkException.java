/*    */ package com.dukascopy.api.nlink;
/*    */ 
/*    */ public class NLinkException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1850730308138365402L;
/*    */   private final String fileName;
/*    */   private final int line;
/*    */ 
/*    */   public NLinkException(String msg, String fileName, int line)
/*    */   {
/* 22 */     super(msg);
/* 23 */     this.fileName = fileName;
/* 24 */     this.line = line;
/*    */   }
/*    */ 
/*    */   public NLinkException(String message) {
/* 28 */     this(message, null, -1);
/*    */   }
/*    */ 
/*    */   public NLinkException(String message, Throwable cause) {
/* 32 */     this(message);
/* 33 */     initCause(cause);
/*    */   }
/*    */ 
/*    */   public NLinkException(Throwable cause) {
/* 37 */     this(cause.getMessage());
/* 38 */     initCause(cause);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 43 */     String s = super.toString();
/* 44 */     if (this.fileName != null) {
/* 45 */       s = s + " : " + this.fileName + ':' + this.line;
/*    */     }
/* 47 */     return s;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.NLinkException
 * JD-Core Version:    0.6.0
 */