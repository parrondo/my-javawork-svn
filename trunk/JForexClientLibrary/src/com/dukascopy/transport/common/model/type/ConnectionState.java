/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum ConnectionState
/*    */ {
/*  6 */   OK(0), 
/*  7 */   NO_FEED(1), 
/*  8 */   NO_CONNECTION(2), 
/*  9 */   NO_NETWORK(3);
/*    */ 
/*    */   private int priority;
/*    */ 
/* 14 */   private ConnectionState(int priority) { this.priority = priority; }
/*    */ 
/*    */   public int getPriority()
/*    */   {
/* 18 */     return this.priority;
/*    */   }
/*    */ 
/*    */   public static ConnectionState fromString(String value) {
/* 22 */     if (OK.toString().equals(value))
/* 23 */       return OK;
/* 24 */     if (NO_FEED.toString().equals(value))
/* 25 */       return NO_FEED;
/* 26 */     if (NO_CONNECTION.toString().equals(value))
/* 27 */       return NO_CONNECTION;
/* 28 */     if (NO_NETWORK.toString().equals(value)) {
/* 29 */       return NO_NETWORK;
/*    */     }
/* 31 */     throw new IllegalArgumentException("Invalid connection states: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.ConnectionState
 * JD-Core Version:    0.6.0
 */