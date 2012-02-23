/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class PrimeBrokerExposureRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "expoRequest";
/*    */   public static final String PRIME_BROKER = "broker";
/*    */ 
/*    */   public PrimeBrokerExposureRequestMessage()
/*    */   {
/* 22 */     setType("expoRequest");
/*    */   }
/*    */ 
/*    */   public PrimeBrokerExposureRequestMessage(ProtocolMessage message)
/*    */   {
/* 31 */     super(message);
/*    */ 
/* 33 */     setType("expoRequest");
/*    */ 
/* 35 */     put("broker", message.getString("broker"));
/*    */   }
/*    */ 
/*    */   public void setPrimeBroker(String primeBroker)
/*    */   {
/* 42 */     put("broker", primeBroker);
/*    */   }
/*    */ 
/*    */   public String getPrimeBroker()
/*    */   {
/* 50 */     return getString("broker");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.PrimeBrokerExposureRequestMessage
 * JD-Core Version:    0.6.0
 */