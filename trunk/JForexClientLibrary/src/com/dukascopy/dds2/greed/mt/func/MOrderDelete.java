/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.impl.connect.JForexAPI;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderDelete extends AgentBase.CommonExecution
/*    */ {
/* 19 */   private static Logger log = LoggerFactory.getLogger(MOrderDelete.class);
/*    */ 
/*    */   public String execute(int id, int ticket, long Color) throws MTAgentException
/*    */   {
/* 23 */     Integer mtId = new Integer(id);
/* 24 */     String label = "";
/* 25 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 26 */     if (msg != null) {
/* 27 */       label = msg.getOpeningOrder().getExternalSysId();
/*    */       try
/*    */       {
/* 32 */         Instrument instrument = Instrument.fromString(msg.getOpeningOrder().getInstrument());
/*    */ 
/* 34 */         OrderMessage om = JForexAPI.cancelOrder(null, label, instrument, msg.getOpeningOrder().getOrderGroupId(), msg.getOpeningOrder().getOrderId(), (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*    */ 
/* 38 */         GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 40 */         ProtocolMessage submitResult = client.controlRequest(om);
/* 41 */         if ((submitResult instanceof ErrorResponseMessage)) {
/* 42 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/*    */ 
/* 44 */           throw new MTAgentException(-99, error.getReason());
/*    */         }
/*    */       }
/*    */       catch (Exception ex) {
/* 48 */         log.error(ex.getMessage(), ex);
/*    */       }
/*    */ 
/* 51 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 53 */     return label;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderDelete
 * JD-Core Version:    0.6.0
 */