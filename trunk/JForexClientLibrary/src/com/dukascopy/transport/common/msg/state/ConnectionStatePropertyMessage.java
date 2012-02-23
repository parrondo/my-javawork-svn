/*    */ package com.dukascopy.transport.common.msg.state;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.ConnectionState;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class ConnectionStatePropertyMessage extends StatePropertyMessage
/*    */ {
/*    */   public static final String TYPE = "state_conn";
/*    */   private static final String URL = "url";
/*    */   private static final String STATE = "state";
/*    */ 
/*    */   public ConnectionStatePropertyMessage()
/*    */   {
/* 20 */     setType("state_conn");
/*    */   }
/*    */ 
/*    */   public ConnectionStatePropertyMessage(ProtocolMessage msg) {
/* 24 */     super(msg);
/* 25 */     setType("state_conn");
/* 26 */     put("url", msg.getString("url"));
/* 27 */     put("state", msg.getString("state"));
/*    */   }
/*    */ 
/*    */   public ConnectionState getConnectionState() {
/* 31 */     return ConnectionState.fromString(getString("state"));
/*    */   }
/*    */ 
/*    */   public void setConnectionState(ConnectionState state) {
/* 35 */     put("state", state.toString());
/*    */   }
/*    */ 
/*    */   public String getUrl() {
/* 39 */     return getString("url");
/*    */   }
/*    */ 
/*    */   public void setUrl(String url) {
/* 43 */     put("url", url);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.state.ConnectionStatePropertyMessage
 * JD-Core Version:    0.6.0
 */