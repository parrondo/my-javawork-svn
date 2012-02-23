/*    */ package com.dukascopy.transport.common.datafeed;
/*    */ 
/*    */ public class FileAlreadyExistException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 3807001342683217162L;
/*    */ 
/*    */   public FileAlreadyExistException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FileAlreadyExistException(String message, Throwable cause)
/*    */   {
/* 12 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public FileAlreadyExistException(String message) {
/* 16 */     super(message);
/*    */   }
/*    */ 
/*    */   public FileAlreadyExistException(Throwable cause) {
/* 20 */     super(cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.datafeed.FileAlreadyExistException
 * JD-Core Version:    0.6.0
 */