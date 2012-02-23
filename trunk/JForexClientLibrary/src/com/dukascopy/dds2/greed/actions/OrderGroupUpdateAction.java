/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.api.OrdersModel;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreePanel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.ScreenSendingUtilities;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.io.IOException;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderGroupUpdateAction extends AppActionEvent
/*     */ {
/*  34 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderGroupUpdateAction.class);
/*     */   private final OrderGroupMessage orderGroup;
/*     */   private final ClientForm gui;
/*     */ 
/*     */   public OrderGroupUpdateAction(Object source, OrderGroupMessage orderGroup)
/*     */   {
/*  45 */     super(source, false, true);
/*  46 */     this.orderGroup = orderGroup;
/*  47 */     this.gui = ((ClientForm)GreedContext.get("clientGui"));
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String notifyCancelled(OrderMessage order)
/*     */   {
/*  57 */     if ((order.getOrderState() == OrderState.CANCELLED) && (order.isPlaceOffer())) {
/*  58 */       String message = "Bid/Offer CANCELLED #" + order.getOrderId() + " reason: " + order.getNotes();
/*  59 */       if ((order.getNotes() != null) && (order.getNotes().indexOf("FILLED FULLY") != -1)) {
/*  60 */         message = "OFFER #" + order.getOrderId() + " FILLED FULLY";
/*     */       }
/*  62 */       return message;
/*  63 */     }if (order.getOrderState() == OrderState.CANCELLED) {
/*  64 */       String message = "Order CANCELLED: " + order.asString(LotAmountChanger.getCurrentLotAmount()) + " - Position #" + order.getOrderGroupId();
/*  65 */       return message;
/*     */     }
/*  67 */     return null;
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/*  77 */     verifyInstrumentSubsription();
/*     */ 
/*  79 */     OrdersModel.getInstance().updateLastOrders(this.orderGroup);
/*     */ 
/*  81 */     this.gui.getOrdersPanel().updateOrderGroup(this.orderGroup);
/*     */ 
/*  83 */     if ((GreedContext.isContest()) && (this.orderGroup.getExternalSysId() != null)) {
/*     */       try {
/*  85 */         ScreenSendingUtilities.sendImage2PhpServer(this.orderGroup.getExternalSysId(), this.orderGroup.getOrderGroupId(), getOrderIdList(this.orderGroup), isOpeningGroup(this.orderGroup));
/*     */       } catch (IOException e) {
/*  87 */         LOGGER.warn(e.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*  91 */     WorkspacePanel workspacePanel = this.gui.getDealPanel().getWorkspacePanel();
/*  92 */     if ((workspacePanel instanceof WorkspaceTreePanel))
/*  93 */       ((WorkspaceTreePanel)workspacePanel).updateOrderGroup(this.orderGroup);
/*     */   }
/*     */ 
/*     */   private void verifyInstrumentSubsription()
/*     */   {
/*  99 */     if ((this.orderGroup.getOrders() != null) && (this.orderGroup.getOrders().size() > 0)) {
/* 100 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 101 */       Set oldInstrumentSet = new HashSet(clientSettingsStorage.restoreSelectedInstruments());
/*     */ 
/* 103 */       String orderGroupInstrument = this.orderGroup.getInstrument();
/*     */ 
/* 105 */       Set perhapsMissingInstrumentSet = new HashSet();
/* 106 */       perhapsMissingInstrumentSet.add(orderGroupInstrument);
/*     */ 
/* 108 */       AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/* 109 */       if (accountStatement.getLastAccountState() != null) {
/* 110 */         Currency accountCurrency = accountStatement.getLastAccountState().getCurrency();
/* 111 */         Set conversionDeps = AbstractCurrencyConverter.getConversionStringDeps(Currency.getInstance(orderGroupInstrument.substring(4, 7)), accountCurrency);
/*     */ 
/* 116 */         perhapsMissingInstrumentSet.addAll(conversionDeps);
/*     */ 
/* 118 */         Set subscribeInsrtuments = new HashSet(perhapsMissingInstrumentSet);
/* 119 */         subscribeInsrtuments.removeAll(oldInstrumentSet);
/*     */ 
/* 123 */         if (subscribeInsrtuments.size() > 0) {
/* 124 */           LOGGER.debug("This group require subscription for instruments: " + subscribeInsrtuments);
/* 125 */           GreedContext.publishEvent(new DirectInstrumentSubscribeAction(this, subscribeInsrtuments));
/*     */         }
/*     */       } else {
/* 128 */         LOGGER.warn("Last account state is null : " + accountStatement);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int isOpeningGroup(OrderGroupMessage groupMessage) {
/* 134 */     for (OrderMessage message : groupMessage.getOrders()) {
/* 135 */       if ((OrderDirection.OPEN.equals(message.getOrderDirection())) && (OrderState.EXECUTING.equals(message.getOrderState())))
/*     */       {
/* 137 */         return 1;
/*     */       }
/*     */     }
/* 140 */     return 0;
/*     */   }
/*     */ 
/*     */   private String getOrderIdList(OrderGroupMessage groupMessage) {
/* 144 */     String idList = "";
/* 145 */     for (OrderMessage message : groupMessage.getOrders()) {
/* 146 */       idList = idList + message.getOrderId() + ",";
/*     */     }
/*     */ 
/* 149 */     if ((idList != null) && (idList.length() > 0)) {
/* 150 */       idList = idList.substring(0, idList.lastIndexOf(','));
/*     */     }
/* 152 */     return idList;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.OrderGroupUpdateAction
 * JD-Core Version:    0.6.0
 */