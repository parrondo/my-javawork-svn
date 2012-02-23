/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CancelOrderAction extends AppActionEvent
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderAction.class);
/*     */   private OrderGroupMessage group;
/*     */   private OrderMessage order;
/*     */   private ProtocolMessage submitResult;
/*     */ 
/*     */   public CancelOrderAction(Object source, OrderGroupMessage group, OrderMessage order)
/*     */   {
/*  35 */     super(source, false, false);
/*  36 */     this.group = group;
/*  37 */     this.order = order;
/*     */   }
/*     */ 
/*     */   public void doAction() {
/*  41 */     if ((this.order == null) || (this.group == null)) {
/*  42 */       return;
/*     */     }
/*  44 */     if (GreedContext.isReadOnly()) {
/*  45 */       LOGGER.info("Not possible action for view mode");
/*  46 */       return;
/*     */     }
/*     */ 
/*  49 */     this.order = new OrderMessage(this.order);
/*  50 */     OrderGroupMessage groupToCancel = new OrderGroupMessage();
/*  51 */     groupToCancel.setOrderGroupId(this.order.getOrderGroupId());
/*  52 */     groupToCancel.setInstrument(this.order.getInstrument());
/*  53 */     this.order.setOrderState(OrderState.CANCELLED);
/*  54 */     groupToCancel.replaceOrAddOrder(this.order);
/*     */ 
/*  56 */     if (GreedContext.isSignalServerInUse()) {
/*  57 */       groupToCancel.setExternalSysId(this.group.getExternalSysId());
/*  58 */       groupToCancel.setSignalId(this.group.getSignalId());
/*     */     }
/*     */ 
/*  61 */     for (OrderMessage order : this.group.getOrders()) {
/*  62 */       PlatformInitUtils.setSecurityInfo4Order(order);
/*     */     }
/*     */ 
/*  65 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage("CANCELLING: " + this.order.asString(LotAmountChanger.getCurrentLotAmount()));
/*     */ 
/*  68 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*  69 */     this.submitResult = client.controlRequest(groupToCancel);
/*  70 */     LOGGER.debug("Cancelling: " + groupToCancel);
/*  71 */     if ((this.submitResult instanceof OkResponseMessage)) {
/*  72 */       ((NotificationUtils)NotificationUtilsProvider.getNotificationUtils()).postOrderActionMessage(this.order);
/*  73 */     } else if ((this.submitResult instanceof ErrorResponseMessage)) {
/*  74 */       ErrorResponseMessage error = (ErrorResponseMessage)this.submitResult;
/*  75 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Order cancel has not been sent(" + error.getReason() + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static OrderCancelResultCode cancelOrderById(Object source, String orderId)
/*     */   {
/*  82 */     return cancelOrderById(source, orderId, 0.0D, 0.0D, -1.0D);
/*     */   }
/*     */ 
/*     */   public static OrderCancelResultCode cancelOrderById(Object source, String orderId, double amount, double price, double slippage)
/*     */   {
/*  87 */     if ((source == null) || (orderId == null)) {
/*  88 */       throw new IllegalArgumentException("Parameter cannot be null");
/*     */     }
/*     */ 
/*  91 */     OrderGroupMessage ogm = OrderMessageUtils.getOrderGroupByOrderId(orderId);
/*  92 */     OrderMessage orderToCancel = null;
/*     */ 
/*  94 */     if (GreedContext.isGlobalExtended())
/*  95 */       orderToCancel = OrderMessageUtils.getIfDoneOrderById(orderId);
/*  96 */     else if (ogm != null) {
/*  97 */       orderToCancel = ogm.getOrderById(orderId, null);
/*     */     }
/*     */ 
/* 100 */     if (orderToCancel == null)
/* 101 */       return OrderCancelResultCode.POSITION_NOT_FOUND;
/* 102 */     if (orderToCancel == null) {
/* 103 */       return OrderCancelResultCode.ORDER_NOT_FOUND;
/*     */     }
/* 105 */     if (amount > 0.0D) {
/* 106 */       orderToCancel.setAmount(new Money(amount + "", orderToCancel.getCurrencyPrimary()));
/*     */     }
/* 108 */     if (price > 0.0D) {
/* 109 */       orderToCancel.setPriceClient(new Money(price + "", orderToCancel.getCurrencyPrimary()));
/*     */     }
/* 111 */     if (slippage >= 0.0D) {
/* 112 */       orderToCancel.setPriceTrailingLimit(slippage + "");
/*     */     }
/* 114 */     CancelOrderAction cancelOrderAction = new CancelOrderAction(source, ogm, orderToCancel);
/*     */ 
/* 117 */     GreedContext.publishEvent(cancelOrderAction);
/* 118 */     if (GreedContext.isActivityLoggingEnabled()) {
/* 119 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/* 120 */       logger.add(orderToCancel);
/*     */     }
/*     */ 
/* 124 */     return OrderCancelResultCode.OK;
/*     */   }
/*     */ 
/*     */   public static enum OrderCancelResultCode
/*     */   {
/*  79 */     POSITION_NOT_FOUND, ORDER_NOT_FOUND, OK;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.CancelOrderAction
 * JD-Core Version:    0.6.0
 */