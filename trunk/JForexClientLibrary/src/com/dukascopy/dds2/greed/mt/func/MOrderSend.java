/*     */ package com.dukascopy.dds2.greed.mt.func;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.connect.JForexAPI;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*     */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*     */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MOrderSend extends AgentBase.CommonExecution
/*     */ {
/*  31 */   private static Logger log = LoggerFactory.getLogger(MOrderSend.class);
/*     */ 
/*     */   public String execute(int id, String symbol, int cmd, double volume, double price, int slippage, double stoploss, double takeprofit, String comment, int magic, long expiration, long arrow_color)
/*     */     throws MTAgentException
/*     */   {
/*  54 */     Instrument instrument = MTAPIHelpers.fromMTString(symbol);
/*  55 */     if (instrument == null) {
/*  56 */       setError(id, 4106, "ERR_UNKNOWN_SYMBOL_MSG");
/*     */ 
/*  58 */       throw new MTAgentException(-12, "ERR_UNKNOWN_SYMBOL_MSG");
/*     */     }
/*     */ 
/*  62 */     if (magic == 0) {
/*  63 */       magic = (int)System.currentTimeMillis();
/*     */     }
/*     */ 
/*  66 */     String label = new StringBuilder().append("MTAG").append(String.valueOf(magic)).append("__").append(System.currentTimeMillis()).toString();
/*     */ 
/*  68 */     OrderGroupMessage orderGroupMessage = JForexAPI.submitOrder("Metatrader external DLL strategy", label, instrument, com.dukascopy.api.IEngine.OrderCommand.values()[cmd], StratUtils.round05Pips(volume), StratUtils.round05Pips(price), StratUtils.round05Pips(slippage), StratUtils.round05Pips(stoploss), StratUtils.round05Pips(takeprofit), comment, (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*     */ 
/*  74 */     OrderMessage openingOrder = orderGroupMessage.getOpeningOrder();
/*     */ 
/*  81 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*     */     try
/*     */     {
/*  87 */       ProtocolMessage submitResult = client.controlRequest(orderGroupMessage);
/*     */       ErrorResponseMessage error;
/*  89 */       if ((submitResult instanceof ErrorResponseMessage)) {
/*  90 */         error = (ErrorResponseMessage)submitResult;
/*     */       }
/*     */       else {
/*  93 */         DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/*  94 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  95 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Order ").append(openingOrder.isPlaceOffer() ? "PLACE OFFER" : openingOrder.getSide() == OrderSide.BUY ? "PLACE BID" : new StringBuilder().append(openingOrder.getStopDirection() != null ? "ENTRY " : "").append(openingOrder.getSide() == OrderSide.BUY ? "BUY" : "SELL").toString()).append(" ").append(openingOrder.getAmount().getValue().stripTrailingZeros().toPlainString()).append(" ").append(openingOrder.getInstrument()).append(" @ ").append((openingOrder.getStopDirection() == null) || (openingOrder.getPriceTrailingLimit() == null) ? "MKT" : openingOrder.isPlaceOffer() ? openingOrder.getPriceClient().getValue().toPlainString() : new StringBuilder().append("LIMIT ").append(openingOrder.getSide() == OrderSide.BUY ? openingOrder.getPriceStop().getValue().add(openingOrder.getPriceTrailingLimit().getValue()).toPlainString() : openingOrder.getPriceStop().getValue().subtract(openingOrder.getPriceTrailingLimit().getValue()).toPlainString()).toString()).append(openingOrder.getStopDirection() != null ? new StringBuilder().append(" IF ").append((openingOrder.getStopDirection() == StopDirection.ASK_LESS) || (openingOrder.getStopDirection() == StopDirection.ASK_GREATER) ? "ASK" : "BID").append(" ").append((openingOrder.getStopDirection() == StopDirection.ASK_LESS) || (openingOrder.getStopDirection() == StopDirection.BID_LESS) ? "<=" : "=>").append(" ").append(openingOrder.getPriceStop().getValue().toPlainString()).toString() : openingOrder.isPlaceOffer() ? openingOrder.getTTLAsString() : "").append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" manually").toString());
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */ 
/* 114 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 115 */     return label;
/*     */   }
/*     */ 
/*     */   public String execute(int id, String symbol, int cmd, double volume, double price, int slippage, double stoploss, double takeprofit, String comment, String label, long expiration, long arrow_color)
/*     */     throws MTAgentException
/*     */   {
/* 139 */     Instrument instrument = MTAPIHelpers.fromMTString(symbol);
/* 140 */     if (instrument == null) {
/* 141 */       setError(id, 4106, "ERR_UNKNOWN_SYMBOL_MSG");
/*     */ 
/* 143 */       throw new MTAgentException(-12, "ERR_UNKNOWN_SYMBOL_MSG");
/*     */     }
/*     */ 
/* 147 */     OrderGroupMessage orderGroupMessage = JForexAPI.submitOrder("Metatrader external DLL strategy", label, instrument, com.dukascopy.api.IEngine.OrderCommand.values()[cmd], StratUtils.round05Pips(volume), StratUtils.round05Pips(price), StratUtils.round05Pips(slippage), StratUtils.round05Pips(stoploss), StratUtils.round05Pips(takeprofit), comment, (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*     */ 
/* 153 */     OrderMessage openingOrder = orderGroupMessage.getOpeningOrder();
/*     */ 
/* 155 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*     */     try {
/* 157 */       ProtocolMessage submitResult = client.controlRequest(orderGroupMessage);
/*     */       ErrorResponseMessage error;
/* 159 */       if ((submitResult instanceof ErrorResponseMessage)) {
/* 160 */         error = (ErrorResponseMessage)submitResult;
/*     */       } else {
/* 162 */         DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/* 163 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 164 */         NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("Order ").append(openingOrder.isPlaceOffer() ? "PLACE OFFER" : openingOrder.getSide() == OrderSide.BUY ? "PLACE BID" : new StringBuilder().append(openingOrder.getStopDirection() != null ? "ENTRY " : "").append(openingOrder.getSide() == OrderSide.BUY ? "BUY" : "SELL").toString()).append(" ").append(openingOrder.getAmount().getValue().stripTrailingZeros().toPlainString()).append(" ").append(openingOrder.getInstrument()).append(" @ ").append((openingOrder.getStopDirection() == null) || (openingOrder.getPriceTrailingLimit() == null) ? "MKT" : openingOrder.isPlaceOffer() ? openingOrder.getPriceClient().getValue().toPlainString() : new StringBuilder().append("LIMIT ").append(openingOrder.getSide() == OrderSide.BUY ? openingOrder.getPriceStop().getValue().add(openingOrder.getPriceTrailingLimit().getValue()).toPlainString() : openingOrder.getPriceStop().getValue().subtract(openingOrder.getPriceTrailingLimit().getValue()).toPlainString()).toString()).append(openingOrder.getStopDirection() != null ? new StringBuilder().append(" IF ").append((openingOrder.getStopDirection() == StopDirection.ASK_LESS) || (openingOrder.getStopDirection() == StopDirection.ASK_GREATER) ? "ASK" : "BID").append(" ").append((openingOrder.getStopDirection() == StopDirection.ASK_LESS) || (openingOrder.getStopDirection() == StopDirection.BID_LESS) ? "<=" : "=>").append(" ").append(openingOrder.getPriceStop().getValue().toPlainString()).toString() : openingOrder.isPlaceOffer() ? openingOrder.getTTLAsString() : "").append(" is sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" manually").toString());
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */ 
/* 183 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 184 */     return label;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderSend
 * JD-Core Version:    0.6.0
 */