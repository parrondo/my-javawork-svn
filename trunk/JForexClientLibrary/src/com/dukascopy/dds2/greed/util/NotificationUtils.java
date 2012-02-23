/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class NotificationUtils
/*     */   implements INotificationUtils
/*     */ {
/*  25 */   private static NotificationUtils instance = new NotificationUtils();
/*     */ 
/*     */   public static INotificationUtils getInstance()
/*     */   {
/*  29 */     return instance;
/*     */   }
/*     */ 
/*     */   public void postInfoMessage(String message) {
/*  33 */     postMessage("INFO", message);
/*     */   }
/*     */   public void postInfoMessage(String message, boolean isLocal) {
/*  36 */     postMessageImpl("INFO", message, isLocal);
/*     */   }
/*     */   public void postInfoMessage(String message, Throwable t) {
/*  39 */     postMessage("INFO", message, t);
/*     */   }
/*     */   public void postInfoMessage(String message, Throwable t, boolean isLocal) {
/*  42 */     postMessageImpl("INFO", message, t, isLocal);
/*     */   }
/*     */   public void postWarningMessage(String message) {
/*  45 */     postMessage("WARNING", message);
/*     */   }
/*     */   public void postWarningMessage(String message, boolean isLocal) {
/*  48 */     postMessageImpl("WARNING", message, isLocal);
/*     */   }
/*     */   public void postWarningMessage(String message, Throwable t) {
/*  51 */     postMessage("WARNING", message, t);
/*     */   }
/*     */   public void postWarningMessage(String message, Throwable t, boolean isLocal) {
/*  54 */     postMessageImpl("WARNING", message, t, isLocal);
/*     */   }
/*     */   public void postErrorMessage(String message) {
/*  57 */     postMessage("ERROR", message);
/*     */   }
/*     */   public void postErrorMessage(String message, boolean isLocal) {
/*  60 */     postMessageImpl("ERROR", message, isLocal);
/*     */   }
/*     */   public void postErrorMessage(String message, Throwable t) {
/*  63 */     postMessage("ERROR", message, t);
/*     */   }
/*     */ 
/*     */   public void postErrorMessage(String message, Throwable t, boolean isLocal) {
/*  67 */     postMessageImpl("ERROR", message, t, isLocal);
/*     */   }
/*     */ 
/*     */   public void postFatalMessage(String message) {
/*  71 */     postMessage("ALERT", message);
/*     */   }
/*     */   public void postFatalMessage(String message, boolean isLocal) {
/*  74 */     postMessageImpl("ALERT", message, isLocal);
/*     */   }
/*     */   public void postFatalMessage(String message, Throwable t) {
/*  77 */     postMessage("ALERT", message, t);
/*     */   }
/*     */   public void postFatalMessage(String message, Throwable t, boolean isLocal) {
/*  80 */     postMessageImpl("ALERT", message, t, isLocal);
/*     */   }
/*     */ 
/*     */   public void postMessage(String message, NotificationLevel level) {
/*  84 */     String priority = "";
/*     */ 
/*  87 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$util$NotificationLevel[level.ordinal()]) { case 1:
/*  88 */       priority = "WARNING"; break;
/*     */     case 2:
/*  89 */       priority = "ERROR"; break;
/*     */     case 3:
/*  90 */       priority = "INFOCLIENT"; break;
/*     */     case 4:
/*  91 */       priority = "NOTIFCLIENT"; break;
/*     */     default:
/*  92 */       priority = "INFO";
/*     */     }
/*  94 */     postMessageImpl(priority, message, true);
/*     */   }
/*     */ 
/*     */   private void postMessage(String priority, String message) {
/*  98 */     postMessageImpl(priority, message, false);
/*     */   }
/*     */ 
/*     */   protected void postMessageImpl(String priority, String message, boolean localMessage) {
/* 102 */     Notification notification = new Notification(new Date(), message);
/* 103 */     notification.setPriority(priority);
/* 104 */     PostMessageAction post = new PostMessageAction(this, notification, localMessage);
/* 105 */     GreedContext.publishEvent(post);
/*     */   }
/*     */ 
/*     */   private void postMessage(String priority, String message, Throwable t) {
/* 109 */     postMessageImpl(priority, message, t, false);
/*     */   }
/*     */ 
/*     */   protected void postMessageImpl(String priority, String message, Throwable t, boolean localMessage) {
/* 113 */     Notification notification = new Notification(new Date(), message);
/* 114 */     notification.setPriority(priority);
/* 115 */     StringWriter sw = new StringWriter();
/* 116 */     t.printStackTrace(new PrintWriter(sw));
/* 117 */     String stacktrace = sw.toString();
/* 118 */     notification.setFullStackTrace(stacktrace);
/* 119 */     PostMessageAction post = new PostMessageAction(this, notification, localMessage);
/* 120 */     GreedContext.publishEvent(post);
/*     */   }
/*     */ 
/*     */   public void postMessage(Notification notification) {
/* 124 */     PostMessageAction post = new PostMessageAction(this, notification, true);
/* 125 */     GreedContext.publishEvent(post);
/*     */   }
/*     */ 
/*     */   public void postOrderActionMessage(OrderMessage orderMessage) {
/* 129 */     DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'");
/* 130 */     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/* 132 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append(orderMessage.isClosing() ? "Closing order " : orderMessage.getOrderState() == OrderState.CANCELLED ? "Order cancellation request " : "Order ").append(orderMessage.getOrderId() != null ? new StringBuilder().append("#").append(orderMessage.getOrderId()).append(" ").toString() : "").append(orderMessage.isStopLoss() ? "STOP LOSS" : orderMessage.isPlaceOffer() ? "PLACE OFFER" : orderMessage.getSide() == OrderSide.BUY ? "PLACE BID" : new StringBuilder().append(orderMessage.getStopDirection() != null ? "ENTRY " : orderMessage.isTakeProfit() ? "TAKE PROFIT" : "").append(orderMessage.getSide() == OrderSide.BUY ? "BUY" : "SELL").toString()).append(" ").append(orderMessage.getAmount().getValue().stripTrailingZeros().toPlainString()).append(" ").append(orderMessage.getInstrument()).append(" @ ").append((orderMessage.getStopDirection() == null) || (orderMessage.getPriceTrailingLimit() == null) ? "MKT" : orderMessage.isPlaceOffer() ? orderMessage.getPriceClient().getValue().toPlainString() : new StringBuilder().append("LIMIT ").append(orderMessage.getSide() == OrderSide.BUY ? orderMessage.getPriceStop().getValue().add(orderMessage.getPriceTrailingLimit().getValue()).toPlainString() : orderMessage.getPriceStop().getValue().subtract(orderMessage.getPriceTrailingLimit().getValue()).toPlainString()).toString()).append(orderMessage.getStopDirection() != null ? new StringBuilder().append(" IF ").append((orderMessage.getStopDirection() == StopDirection.ASK_LESS) || (orderMessage.getStopDirection() == StopDirection.ASK_GREATER) ? "ASK" : "BID").append(" ").append((orderMessage.getStopDirection() == StopDirection.ASK_LESS) || (orderMessage.getStopDirection() == StopDirection.BID_LESS) ? "<=" : "=>").append(" ").append(orderMessage.getPriceStop().getValue().toPlainString()).toString() : orderMessage.isPlaceOffer() ? orderMessage.getTTLAsString() : "").append(" had been sent at ").append(format.format(Long.valueOf(System.currentTimeMillis()))).append(" manually").toString());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.NotificationUtils
 * JD-Core Version:    0.6.0
 */