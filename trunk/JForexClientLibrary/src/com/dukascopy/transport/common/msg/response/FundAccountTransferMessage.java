/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.SerializableProtocolMessage;
/*    */ 
/*    */ public class FundAccountTransferMessage extends SerializableProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "ftransfer";
/*    */ 
/*    */   public FundAccountTransferMessage()
/*    */   {
/* 16 */     setType("ftransfer");
/*    */   }
/*    */ 
/*    */   public FundAccountTransferMessage(ProtocolMessage message) {
/* 20 */     super(message);
/* 21 */     setType("ftransfer");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.FundAccountTransferMessage
 * JD-Core Version:    0.6.0
 */