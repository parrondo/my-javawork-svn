/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.IEngine.OrderCommand;
/*    */ import com.dukascopy.api.impl.connect.JForexAPI;
/*    */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.model.type.OrderState;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderModify extends AgentBase.CommonExecution
/*    */ {
/* 21 */   private static Logger log = LoggerFactory.getLogger(MOrderModify.class);
/*    */ 
/*    */   public String execute(int id, int ticket, double price, double stoploss, double takeprofit, long expiration, long arrow_color)
/*    */     throws MTAgentException, CloneNotSupportedException
/*    */   {
/* 26 */     String label = "";
/* 27 */     Integer mtId = new Integer(id);
/* 28 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */ 
/* 31 */     OrderGroupMessage msg = getOrderGroup(ticket);
/* 32 */     if (msg != null) {
/* 33 */       label = msg.getOpeningOrder().getExternalSysId();
/* 34 */       IEngine.OrderCommand orderCommand = OrdersProvider.convert(msg.getOpeningOrder().getSide(), msg.getOpeningOrder().getStopDirection(), false, msg.getOpeningOrder().isPlaceOffer());
/*    */ 
/* 38 */       OrderGroupMessage om = null;
/* 39 */       if (msg.getOpeningOrder().getOrderState().equals(OrderState.PENDING))
/*    */       {
/* 42 */         om = JForexAPI.modifyOrderGroup(msg, StratUtils.round05Pips(price), StratUtils.round05Pips(stoploss), StratUtils.round05Pips(takeprofit), 0L, orderCommand.isLong(), (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*    */       }
/* 49 */       else if (msg.getOpeningOrder().getOrderState().equals(OrderState.FILLED)) {
/* 50 */         om = JForexAPI.modifyOrderGroup(msg, 0.0D, StratUtils.round05Pips(stoploss), StratUtils.round05Pips(takeprofit), 0L, orderCommand.isLong(), (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*    */       }
/* 57 */       else if (msg.getOpeningOrder().isPlaceOffer()) {
/* 58 */         om = JForexAPI.modifyOrderGroup(msg, StratUtils.round05Pips(price), 0.0D, 0.0D, expiration * 1000L, orderCommand.isLong(), (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*    */       }
/*    */       else
/*    */       {
/* 68 */         setError(id, 4109, "ERR_TRADE_NOT_ALLOWED_MSG");
/* 69 */         throw new MTAgentException(-12);
/*    */       }
/* 71 */       GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*    */       try
/*    */       {
/* 74 */         ProtocolMessage submitResult = client.controlRequest(om);
/* 75 */         if ((submitResult instanceof ErrorResponseMessage))
/* 76 */           error = (ErrorResponseMessage)submitResult;
/*    */       }
/*    */       catch (Exception ex)
/*    */       {
/*    */         ErrorResponseMessage error;
/* 80 */         log.error(ex.getMessage(), ex);
/*    */       }
/*    */     } else {
/* 83 */       setError(id, 4105, "ERR_NO_ORDER_SELECTED_MSG");
/* 84 */       throw new MTAgentException(-12);
/*    */     }
/* 86 */     return label;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderModify
 * JD-Core Version:    0.6.0
 */