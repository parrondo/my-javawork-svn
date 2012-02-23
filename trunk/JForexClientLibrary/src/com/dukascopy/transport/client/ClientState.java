/*    */ package com.dukascopy.transport.client;
/*    */ 
/*    */ public class ClientState
/*    */ {
/*    */   public static final int STATE_DISCONNECTED = 0;
/*    */   public static final int STATE_DISCONNECTING = 1;
/*    */   public static final int STATE_ONLINE = 4;
/*    */   public static final int STATE_CONNECTING = 2;
/*    */   public static final int STATE_AUTHORIZING = 3;
/* 19 */   private int state = 0;
/*    */ 
/* 21 */   private DisconnectReason disconnectReason = DisconnectReason.EXCEPTION_CAUGHT;
/*    */ 
/*    */   public void setState(int state, DisconnectReason disconnectReason) {
/* 24 */     this.state = state;
/* 25 */     this.disconnectReason = disconnectReason;
/*    */   }
/*    */ 
/*    */   public int getState() {
/* 29 */     return this.state;
/*    */   }
/*    */ 
/*    */   public DisconnectReason getDisconnectReason() {
/* 33 */     return this.disconnectReason;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.ClientState
 * JD-Core Version:    0.6.0
 */