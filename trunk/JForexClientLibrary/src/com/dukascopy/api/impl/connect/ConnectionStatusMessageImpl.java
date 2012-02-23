/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.IConnectionStatusMessage;
/*    */ import com.dukascopy.api.IMessage.Type;
/*    */ 
/*    */ class ConnectionStatusMessageImpl extends PlatformMessageImpl
/*    */   implements IConnectionStatusMessage
/*    */ {
/*    */   private final boolean connected;
/*    */ 
/*    */   public ConnectionStatusMessageImpl(boolean connected, long creationTime)
/*    */   {
/* 14 */     super(null, null, IMessage.Type.CONNECTION_STATUS, creationTime);
/* 15 */     this.connected = connected;
/*    */   }
/*    */ 
/*    */   public boolean isConnected()
/*    */   {
/* 20 */     return this.connected;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 25 */     return "MessageType " + getType() + " Connected : " + this.connected;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.ConnectionStatusMessageImpl
 * JD-Core Version:    0.6.0
 */