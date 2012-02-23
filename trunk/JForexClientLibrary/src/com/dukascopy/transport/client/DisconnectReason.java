/*    */ package com.dukascopy.transport.client;
/*    */ 
/*    */ public enum DisconnectReason
/*    */ {
/* 23 */   AUTHORIZATION_FAILED(0), 
/* 24 */   AUTHORIZATION_TIMEOUT(1), 
/* 25 */   CONNECTION_PROBLEM(2), 
/* 26 */   EXCEPTION_CAUGHT(3), 
/* 27 */   UNKNOWN(4), 
/* 28 */   CLIENT_APP_REQUEST(5), 
/* 29 */   CLIENT_LISTENER_THREAD_POOL_QUEUE_OVERLOADED(6), 
/* 30 */   SLOW_CONNECTION_SENDING_QUEUE_OVERLOADED(7), 
/* 31 */   CERTIFICATE_EXCEPTION(8);
/*    */ 
/*    */   private int reason;
/*    */ 
/* 36 */   private DisconnectReason(int reason) { this.reason = reason; }
/*    */ 
/*    */   public int getReason()
/*    */   {
/* 40 */     return this.reason;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.DisconnectReason
 * JD-Core Version:    0.6.0
 */