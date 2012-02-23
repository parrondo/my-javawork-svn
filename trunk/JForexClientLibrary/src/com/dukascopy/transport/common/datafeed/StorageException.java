/*    */ package com.dukascopy.transport.common.datafeed;
/*    */ 
/*    */ public class StorageException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 340283395905412712L;
/*    */ 
/*    */   public StorageException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public StorageException(String message, Throwable cause)
/*    */   {
/* 12 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public StorageException(String message) {
/* 16 */     super(message);
/*    */   }
/*    */ 
/*    */   public StorageException(Throwable cause) {
/* 20 */     super(cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.datafeed.StorageException
 * JD-Core Version:    0.6.0
 */