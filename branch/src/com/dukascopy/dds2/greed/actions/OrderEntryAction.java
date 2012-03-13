/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.DDSAgent;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderEntryAction extends AppActionEvent
/*     */ {
/*  33 */   private static Logger LOGGER = LoggerFactory.getLogger(OrderEntryAction.class);
/*     */   private OrderGroupMessage orderGroup;
/*     */   private AccountStatement accountStatement;
/*     */   private OrderDirection orderDirection;
/*     */   private ProtocolMessage submitResult;
/*     */   private boolean deferedTradeLog;
/*     */   private ClientSettingsStorage storage;
/*     */ 
/*     */   public OrderEntryAction(Object source, OrderGroupMessage orderGroup, boolean defered)
/*     */   {
/*  45 */     this(source, false, orderGroup, OrderDirection.OPEN, defered);
/*     */   }
/*     */ 
/*     */   public OrderEntryAction(Object source, boolean noLogging, OrderGroupMessage orderGroup) {
/*  49 */     this(source, noLogging, orderGroup, OrderDirection.OPEN, false);
/*     */   }
/*     */ 
/*     */   public OrderEntryAction(Object source, OrderGroupMessage orderGroup) {
/*  53 */     this(source, false, orderGroup, OrderDirection.OPEN, false);
/*     */   }
/*     */ 
/*     */   public OrderEntryAction(Object source, OrderGroupMessage orderGroup, OrderDirection orderDirection, boolean deferedTradeLog) {
/*  57 */     this(source, false, orderGroup, orderDirection, deferedTradeLog);
/*     */   }
/*     */ 
/*     */   public OrderEntryAction(Object source, boolean noLogging, OrderGroupMessage orderGroup, OrderDirection orderDirection, boolean deferedTradeLog) {
/*  61 */     super(source, true, true);
/*     */ 
/*  40 */     this.deferedTradeLog = false;
/*     */ 
/*  42 */     this.storage = ((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/*  62 */     this.orderGroup = orderGroup;
/*  63 */     long creationTime = System.currentTimeMillis();
/*  64 */     this.deferedTradeLog = deferedTradeLog;
/*  65 */     this.orderDirection = orderDirection;
/*     */ 
/*  67 */     if (GreedContext.isReadOnly()) {
/*  68 */       LOGGER.info("Not possible action for view mode");
/*  69 */       return;
/*     */     }
/*     */ 
/*  72 */     if ((!(noLogging)) && (GreedContext.isActivityLoggingEnabled())) {
/*  73 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*  74 */       logger.add(source, orderGroup, orderDirection, deferedTradeLog, creationTime);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiBefore() {
/*  79 */     if (GreedContext.isReadOnly()) {
/*  80 */       LOGGER.info("Not possible action for view mode");
/*  81 */       return;
/*     */     }
/*  83 */     orderActionNotification("Sending order: {0} ");
/*     */   }
/*     */ 
/*     */   public void doAction() {
/*  87 */     if (GreedContext.isReadOnly()) {
/*  88 */       LOGGER.info("Not possible action for view mode");
/*  89 */       return;
/*     */     }
/*  91 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*  92 */     this.accountStatement = ((AccountStatement)GreedContext.get("accountStatement"));
/*     */ 
/*  94 */     List newOrders = new ArrayList();
/*  95 */     for (OrderMessage order : this.orderGroup.getOrders()) {
/*  96 */       order.remove("trades");
/*  97 */       newOrders.add(order);
/*  98 */       PlatformInitUtils.setSecurityInfo4Order(order);
/*  99 */       if (order.getTag() != null) {
/* 100 */         order.setTag(order.getTag().replace("*", ""));
/*     */       }
/*     */     }
/* 103 */     if ((((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended()))) && ((
/* 104 */       (this.orderGroup.getOrderGroupId() == null) || (this.orderGroup.getOrderGroupId().length() < 1)))) {
/* 105 */       this.orderGroup.setOrderGroupId(this.accountStatement.getLastAccountState().getUserId() + this.orderGroup.getInstrument());
/*     */     }
/*     */ 
/* 108 */     this.orderGroup.setOrders(newOrders);
/*     */ 
/* 110 */     OrderMessage openingOrder = this.orderGroup.getOpeningOrder();
/*     */ 
/* 112 */     if (openingOrder != null) {
/* 113 */       String tagForOrder = DDSAgent.generateLabel(null);
/* 114 */       if (openingOrder.getExternalSysId() == null) {
/* 115 */         openingOrder.setExternalSysId(tagForOrder);
/*     */       }
/* 117 */       if (openingOrder.getSignalId() == null) {
/* 118 */         openingOrder.setSignalId(tagForOrder);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 123 */     if (LOGGER.isDebugEnabled()) {
/* 124 */       LOGGER.debug("OrderGroupMessage: " + this.orderGroup.toString());
/*     */     }
/*     */ 
/* 128 */     if (this.orderGroup.getTag() != null) {
/* 129 */       this.orderGroup.setTag(this.orderGroup.getTag().replace("*", ""));
/*     */     }
/* 131 */     if ((((GreedContext.isTest()) || (GreedContext.isPreDemo()))) && 
/* 132 */       (this.storage.restoreFillOrKillOrders())) {
/* 133 */       openingOrder.setFillOrKill(Boolean.valueOf(true));
/*     */     }
/*     */ 
/* 136 */     OrderMessage orderMessage = null;
/*     */ 
/* 139 */     if ((GreedContext.isGlobalExtended()) && (!(OrderState.CREATED.equals(this.orderGroup.getOpeningOrder().getOrderState()))))
/*     */     {
/* 141 */       OrderMessage limit = this.orderGroup.getCloseLimitOrder();
/* 142 */       OrderMessage stop = this.orderGroup.getCloseStopOrder();
/*     */ 
/* 144 */       if (limit != null) {
/* 145 */         this.submitResult = client.controlRequest(limit);
/* 146 */         orderMessage = limit;
/* 147 */       } else if (stop != null) {
/* 148 */         this.submitResult = client.controlRequest(stop);
/* 149 */         orderMessage = stop;
/*     */       } else {
/* 151 */         this.submitResult = client.controlRequest(this.orderGroup.getOpeningOrder());
/* 152 */         orderMessage = this.orderGroup.getOpeningOrder();
/*     */       }
/*     */     }
/*     */     else {
/* 156 */       this.submitResult = client.controlRequest(this.orderGroup);
/*     */     }
/*     */ 
/* 159 */     if ((this.orderDirection == OrderDirection.OPEN) && (openingOrder != null) && (!(GreedContext.isGlobalExtended())))
/*     */     {
/* 161 */       orderMessage = openingOrder;
/* 162 */     } else if ((this.orderDirection == OrderDirection.CLOSE) && (!(GreedContext.isGlobalExtended()))) {
/* 163 */       for (OrderMessage order : this.orderGroup.getOrders()) {
/* 164 */         if (order.getOrderDirection() == OrderDirection.CLOSE) {
/* 165 */           orderMessage = order;
/* 166 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 170 */     if ((this.submitResult instanceof OkResponseMessage) && (orderMessage != null)) {
/* 171 */       ((NotificationUtils)NotificationUtilsProvider.getNotificationUtils()).postOrderActionMessage(orderMessage);
/* 172 */     } else if (this.submitResult instanceof ErrorResponseMessage) {
/* 173 */       LOGGER.debug("{error}: " + this.submitResult);
/* 174 */       ErrorResponseMessage error = (ErrorResponseMessage)this.submitResult;
/* 175 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Order has not been sent (" + error.getReason() + ")");
/*     */     } else {
/* 177 */       LOGGER.warn("{error} uknown response: " + this.submitResult);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void orderActionNotification(String orderActionMessage) {
/* 182 */     for (OrderMessage order : this.orderGroup.getOrders())
/* 183 */       if ((((order.getOrderState() == OrderState.CREATED) || (order.getOrderState() == OrderState.PENDING))) && (order.getOrderId() == null)) {
/* 184 */         Notification notification = new Notification(GreedContext.getPlatformTime(), MessageFormat.format(orderActionMessage, new Object[] { order.asString(LotAmountChanger.getCurrentLotAmount()) }));
/* 185 */         String wrapDate = GreedContext.getPlatformTimeForLogger();
/* 186 */         notification.setServerTimestamp(wrapDate);
/* 187 */         PostMessageAction post = new PostMessageAction(this, notification);
/* 188 */         post.updateGuiAfter();
/*     */       }
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isDeferedTradeLog()
/*     */   {
/* 198 */     return this.deferedTradeLog;
/*     */   }
/*     */ 
/*     */   public void setDeferedTradeLog(boolean deferedTradeLog) {
/* 202 */     this.deferedTradeLog = deferedTradeLog;
/*     */   }
/*     */ }

/* Location:           J:\javaworksvn\JForexClientLibrary_branch\libs\DDS2-jClient-JForex-2.14.26.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.OrderEntryAction
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */