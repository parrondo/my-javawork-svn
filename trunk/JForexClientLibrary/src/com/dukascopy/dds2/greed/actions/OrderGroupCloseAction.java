/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderGroupCloseAction extends AppActionEvent
/*     */ {
/*  32 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderGroupCloseAction.class);
/*     */   private OrderGroupMessage group;
/*     */   private ProtocolMessage response;
/*     */   private OrderMessage closingOrder;
/*  37 */   private Money currentPrice = null;
/*  38 */   private Money amount = null;
/*  39 */   private Money slippageInRealValue = null;
/*     */ 
/*     */   public OrderGroupCloseAction(Object source, OrderGroupMessage group) {
/*  42 */     super(source, false, true);
/*  43 */     this.group = group;
/*     */   }
/*     */ 
/*     */   public OrderGroupCloseAction(Object source, OrderGroupMessage group, Money currentPrice) {
/*  47 */     this(source, group);
/*  48 */     this.currentPrice = currentPrice;
/*     */   }
/*     */ 
/*     */   public OrderGroupCloseAction(Object source, OrderGroupMessage group, Money currentPrice, Money amount) {
/*  52 */     this(source, group, currentPrice);
/*  53 */     this.amount = amount;
/*     */   }
/*     */   public OrderGroupCloseAction(Object source, OrderGroupMessage group, Money currentPrice, Money amount, Money slippageInRealValue) {
/*  56 */     this(source, group, currentPrice);
/*  57 */     this.amount = amount;
/*  58 */     this.slippageInRealValue = slippageInRealValue;
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  65 */     if (GreedContext.isReadOnly()) {
/*  66 */       LOGGER.info("Not possible action for view mode");
/*  67 */       return;
/*     */     }
/*  69 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*  70 */     OrderGroupMessage newGroup = new OrderGroupMessage(this.group);
/*     */ 
/*  72 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/*  73 */       this.closingOrder = GuiUtilsAndConstants.createClosingGlobalOrderModified(this.currentPrice, this.group);
/*     */     } else {
/*  75 */       this.closingOrder = newGroup.createClosingOrderModified(this.currentPrice);
/*  76 */       this.closingOrder.setExecutingTimes(this.group.getOpeningOrder().getExecutionTime());
/*     */     }
/*     */ 
/*  79 */     if (this.closingOrder != null) {
/*  80 */       if (this.amount != null) {
/*  81 */         this.closingOrder.setAmount(this.amount);
/*     */       }
/*     */ 
/*  84 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/*  86 */       if (clientSettingsStorage.restoreApplySlippageToAllMarketOrders())
/*     */       {
/*  88 */         String slippage = GuiUtilsAndConstants.getSlippageAmount(clientSettingsStorage.restoreDefaultSlippageAsText(), this.closingOrder.getSide(), this.closingOrder.getInstrument());
/*  89 */         this.closingOrder.setPriceTrailingLimit(slippage);
/*     */       }
/*     */ 
/*  93 */       if ((this.slippageInRealValue != null) && (this.slippageInRealValue.getValue().doubleValue() > 0.0D)) {
/*  94 */         this.closingOrder.setPriceTrailingLimit(this.slippageInRealValue);
/*     */       }
/*     */ 
/*  98 */       List newOrders = new ArrayList();
/*  99 */       newOrders.add(this.closingOrder);
/* 100 */       newGroup.setOrders(newOrders);
/* 101 */       if (LOGGER.isDebugEnabled()) {
/* 102 */         LOGGER.info("CLOSE:" + newGroup);
/*     */       }
/*     */ 
/* 105 */       for (OrderMessage order : newGroup.getOrders()) {
/* 106 */         PlatformInitUtils.setSecurityInfo4Order(order);
/*     */       }
/*     */ 
/* 109 */       PlatformInitUtils.setExtIdforOrderMessages(newGroup);
/* 110 */       this.response = client.controlRequest(newGroup);
/*     */     } else {
/* 112 */       this.response = new ErrorResponseMessage("FATAL: Failed to generate closing order. Please contact Dukascopy.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter() {
/* 117 */     if (GreedContext.isReadOnly()) {
/* 118 */       LOGGER.info("Not possible action for view mode");
/* 119 */       return;
/*     */     }
/* 121 */     if ((this.response instanceof OkResponseMessage)) {
/* 122 */       ((NotificationUtils)NotificationUtilsProvider.getNotificationUtils()).postOrderActionMessage(this.closingOrder);
/* 123 */     } else if ((this.response instanceof ErrorResponseMessage)) {
/* 124 */       ErrorResponseMessage error = (ErrorResponseMessage)this.response;
/* 125 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Order has not been sent (" + error.getReason() + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ClosePositionResultCode closePositionById(Object source, String orderGroupId)
/*     */   {
/* 132 */     return closePositionById(source, orderGroupId, null, null, null);
/*     */   }
/*     */ 
/*     */   public static ClosePositionResultCode closePositionById(Object source, String orderGroupId, Money price, Money amount, Money slippageInRealValue) {
/* 136 */     ClosePositionResultCode result = ClosePositionResultCode.OK;
/* 137 */     OrderGroupMessage ogm = OrderMessageUtils.getOrderGroupById(orderGroupId);
/*     */ 
/* 139 */     if (ogm == null) {
/* 140 */       return ClosePositionResultCode.POSITION_NOT_FOUND;
/*     */     }
/* 142 */     fireOrderGroupCloseAction(source, ogm, price, amount, slippageInRealValue);
/*     */ 
/* 144 */     return result;
/*     */   }
/*     */ 
/*     */   public static void fireOrderGroupCloseAction(Object source, OrderGroupMessage ogm, Money price, Money amount, Money slippageInRealValue) {
/* 148 */     OrderGroupCloseAction orderGroupCloseAction = new OrderGroupCloseAction(source, ogm, price, amount, slippageInRealValue);
/*     */ 
/* 150 */     GreedContext.publishEvent(orderGroupCloseAction);
/* 151 */     if (GreedContext.isActivityLoggingEnabled()) {
/* 152 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/* 153 */       logger.add(source, ogm, OrderDirection.CLOSE);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum ClosePositionResultCode
/*     */   {
/* 129 */     POSITION_NOT_FOUND, OK;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.OrderGroupCloseAction
 * JD-Core Version:    0.6.0
 */