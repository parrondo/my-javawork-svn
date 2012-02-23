/*     */ package com.dukascopy.dds2.match;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.RejectReason;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.group.TradeMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class TradeMatcher
/*     */ {
/*     */   private static Logger log;
/*  28 */   private Map<String, OrderMessage> orderIndex = new HashMap();
/*  29 */   private Map<String, String> sessionIndex = new HashMap();
/*  30 */   private Map<String, List<TradeMessage>> actualTrades = new HashMap();
/*     */ 
/*     */   public void onAcknowledge(String sessionId, OrderMessage order, String requestId)
/*     */   {
/*  46 */     assert (requestId != null);
/*  47 */     if (this.actualTrades.get(requestId) != null) {
/*  48 */       String errorMessage = "MATCH FAILURE: tried to accept already acknowledged order: " + requestId;
/*  49 */       log.error(errorMessage);
/*  50 */       throw new MatchException(errorMessage);
/*     */     }
/*  52 */     this.orderIndex.put(requestId, order);
/*  53 */     this.sessionIndex.put(requestId, sessionId);
/*  54 */     this.actualTrades.put(requestId, new ArrayList());
/*     */   }
/*     */ 
/*     */   public OrderMessage onTrade(String requestId, TradeMessage trade)
/*     */   {
/*  66 */     OrderMessage order = (OrderMessage)this.orderIndex.get(requestId);
/*  67 */     List orderTrades = (List)this.actualTrades.get(requestId);
/*     */ 
/*  69 */     if ((order == null) || (orderTrades == null)) {
/*  70 */       String errorMessage = "MATCH FAILURE: unable to match trade " + requestId + "with any of previously accepted orders.";
/*  71 */       log.error(errorMessage);
/*  72 */       throw new MatchException(errorMessage);
/*     */     }
/*     */ 
/*  75 */     trade.setOrderId(order.getOrderId());
/*  76 */     orderTrades.add(trade);
/*  77 */     BigDecimal matchedAmount = BigDecimal.ZERO;
/*     */ 
/*  79 */     for (TradeMessage previouslyMatchedTrade : orderTrades) {
/*  80 */       matchedAmount = matchedAmount.add(previouslyMatchedTrade.getAmountPrimary().getValue().abs());
/*     */     }
/*     */ 
/*  83 */     if (order.getAmount().getValue().compareTo(matchedAmount) <= 0) {
/*  84 */       log.info("MATCH: Order " + order.getOrderId() + " has been fully matched on amount of " + matchedAmount);
/*  85 */       order.setOrderState(OrderState.FILLED);
/*     */ 
/*  87 */       String sessionId = (String)this.sessionIndex.get(requestId);
/*  88 */       if (sessionId != null);
/*  94 */       clearIndices(requestId);
/*     */     }
/*  96 */     return order;
/*     */   }
/*     */ 
/*     */   public void onRemainingCancelled(String requestId)
/*     */   {
/* 104 */     OrderMessage order = (OrderMessage)this.orderIndex.get(requestId);
/* 105 */     List orderTrades = (List)this.actualTrades.get(requestId);
/*     */ 
/* 107 */     if ((order == null) || (orderTrades == null)) {
/* 108 */       String errorMessage = "MATCH FAILURE: unable to match trade " + requestId + " with any of previously accepted orders.";
/* 109 */       log.error(errorMessage);
/* 110 */       throw new MatchException(errorMessage);
/*     */     }
/*     */ 
/* 113 */     BigDecimal matchedAmount = BigDecimal.ZERO;
/*     */ 
/* 115 */     for (TradeMessage previouslyMatchedTrade : orderTrades) {
/* 116 */       matchedAmount = matchedAmount.add(previouslyMatchedTrade.getAmountPrimary().getValue());
/*     */     }
/*     */ 
/* 119 */     log.info("MATCH: Order " + order.getOrderId() + " has been partially matched with amount of " + matchedAmount.toPlainString());
/*     */ 
/* 121 */     if (matchedAmount.compareTo(BigDecimal.ZERO) == 0)
/* 122 */       order.setOrderState(OrderState.REJECTED);
/*     */     else {
/* 124 */       order.setOrderState(OrderState.FILLED);
/*     */     }
/*     */ 
/* 127 */     String sessionId = (String)this.sessionIndex.get(requestId);
/*     */ 
/* 129 */     if (sessionId != null);
/*     */   }
/*     */ 
/*     */   public void onRejected(String requestId, RejectReason reason)
/*     */   {
/* 142 */     OrderMessage order = (OrderMessage)this.orderIndex.get(requestId);
/* 143 */     if (order != null) {
/* 144 */       order.setRejectReason(reason);
/*     */     }
/* 146 */     resetOrderState(requestId, OrderState.REJECTED);
/*     */   }
/*     */ 
/*     */   public void onCancelled(String requestId)
/*     */   {
/* 155 */     resetOrderState(requestId, OrderState.CANCELLED);
/*     */   }
/*     */ 
/*     */   private void clearIndices(String hsFxId)
/*     */   {
/* 171 */     this.orderIndex.remove(hsFxId);
/* 172 */     this.actualTrades.remove(hsFxId);
/* 173 */     this.sessionIndex.remove(hsFxId);
/*     */   }
/*     */ 
/*     */   private void resetOrderState(String requestId, OrderState state)
/*     */   {
/* 189 */     OrderMessage order = (OrderMessage)this.orderIndex.get(requestId);
/* 190 */     if (order == null) {
/* 191 */       String errorMessage = "MATCH FAILURE: unable to match trade " + requestId + " with any of previously accepted orders.";
/* 192 */       log.error(errorMessage);
/* 193 */       throw new MatchException(errorMessage);
/*     */     }
/* 195 */     order.setOrderState(state);
/*     */ 
/* 197 */     String sessionId = (String)this.sessionIndex.get(requestId);
/*     */ 
/* 199 */     if (sessionId != null);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  26 */     log = LoggerFactory.getLogger(TradeMatcher.class);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.match.TradeMatcher
 * JD-Core Version:    0.6.0
 */