/*    */ package com.dukascopy.api.nlink;
/*    */ 
/*    */ public class Win32NLinkException extends NLinkException
/*    */ {
/*    */   private static final long serialVersionUID = 1529746978177244594L;
/* 19 */   private final int errorCode = Native.getLastError();
/*    */ 
/* 24 */   private final String errorCodeMessage = Native.formatErrorMessage(this.errorCode);
/*    */ 
/*    */   public Win32NLinkException(String msg, String fileName, int line)
/*    */   {
/* 28 */     super(msg, fileName, line);
/*    */   }
/*    */ 
/*    */   public Win32NLinkException(String message)
/*    */   {
/* 33 */     super(message);
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 38 */     if (this.errorCodeMessage == null) {
/* 39 */       return super.getMessage();
/*    */     }
/* 41 */     return super.getMessage() + " : " + this.errorCodeMessage;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.Win32NLinkException
 * JD-Core Version:    0.6.0
 */