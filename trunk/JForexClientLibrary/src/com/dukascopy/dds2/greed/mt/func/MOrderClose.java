/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.impl.connect.JForexAPI;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
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
/*    */ public class MOrderClose extends AgentBase.CommonExecution
/*    */ {
/* 20 */   private static Logger log = LoggerFactory.getLogger(MOrderClose.class);
/*    */ 
/*    */   public String execute(int id, int ticket, double lots, double price, int slippage, long Color) throws MTAgentException
/*    */   {
/* 24 */     String label = "";
/* 25 */     OrderGroupMessage msg = getOrderGroup(ticket);
/* 26 */     if (msg != null) {
/*    */       try {
/* 28 */         label = msg.getOpeningOrder().getExternalSysId();
/* 29 */         Instrument instrument = Instrument.fromString(msg.getOpeningOrder().getInstrument());
/* 30 */         OrderMessage om = JForexAPI.closePosition(msg.getOpeningOrder(), StratUtils.round05Pips(lots), StratUtils.round05Pips(price), instrument.getPipValue() * slippage, (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*    */ 
/* 36 */         GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 38 */         ProtocolMessage submitResult = client.controlRequest(om);
/* 39 */         if ((submitResult instanceof ErrorResponseMessage)) {
/* 40 */           ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/* 41 */           throw new MTAgentException(-99, error.getReason());
/*    */         }
/*    */       }
/*    */       catch (CloneNotSupportedException cex)
/*    */       {
/* 46 */         log.error(cex.getMessage(), cex);
/*    */       } catch (Exception ex) {
/* 48 */         log.error(ex.getMessage(), ex);
/*    */       }
/*    */ 
/* 51 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/* 53 */     return label;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderClose
 * JD-Core Version:    0.6.0
 */