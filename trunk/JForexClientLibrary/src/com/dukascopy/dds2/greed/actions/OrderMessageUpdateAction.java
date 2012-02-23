/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.GlobalOrderTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*    */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*    */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ import java.util.Currency;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class OrderMessageUpdateAction extends AppActionEvent
/*    */ {
/* 22 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderGroupUpdateAction.class);
/*    */   private OrderGroupMessage orderGroup;
/*    */   private OrderMessage orderMessage;
/*    */   private ClientForm gui;
/*    */ 
/*    */   public OrderMessageUpdateAction(Object source, OrderMessage orderMessage)
/*    */   {
/* 34 */     super(source, false, true);
/* 35 */     this.orderMessage = orderMessage;
/*    */ 
/* 37 */     this.orderGroup = new OrderGroupMessage(orderMessage);
/*    */ 
/* 39 */     this.orderGroup.replaceOrAddOrder(orderMessage);
/* 40 */     this.orderGroup.setTransactionId(orderMessage.getTransactionId());
/* 41 */     this.orderGroup.setUserId(orderMessage.getUserId());
/* 42 */     this.orderGroup.setInstrument(orderMessage.getInstrument());
/* 43 */     this.orderGroup.setOrderGroupId(orderMessage.getOrderGroupId());
/*    */ 
/* 45 */     this.gui = ((ClientForm)GreedContext.get("clientGui"));
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 53 */     verifyInstrumentSubsription();
/*    */ 
/* 55 */     OrdersTable ordersTable = this.gui.getOrdersPanel().getOrdersTable();
/*    */ 
/* 57 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/* 58 */       TableSorter sorter = (TableSorter)ordersTable.getModel();
/* 59 */       GlobalOrderTableModel model = (GlobalOrderTableModel)sorter.getTableModel();
/*    */ 
/* 61 */       model.updateTable(this.orderMessage);
/* 62 */       ordersTable.refreshHighlight();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void verifyInstrumentSubsription()
/*    */   {
/* 68 */     if ((this.orderGroup.getOrders() != null) && (this.orderGroup.getOrders().size() > 0)) {
/* 69 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 70 */       Set oldInstrumentSet = new HashSet(clientSettingsStorage.restoreSelectedInstruments());
/*    */ 
/* 72 */       String orderGroupInstrument = this.orderGroup.getInstrument();
/*    */ 
/* 74 */       Set perhapsMissingInstrumentSet = new HashSet();
/* 75 */       perhapsMissingInstrumentSet.add(orderGroupInstrument);
/*    */ 
/* 77 */       AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/* 78 */       if (accountStatement.getLastAccountState() != null) {
/* 79 */         Currency accountCurrency = accountStatement.getLastAccountState().getCurrency();
/* 80 */         Set conversionDeps = CurrencyConverter.getConversionStringDeps(Currency.getInstance(orderGroupInstrument.substring(4, 7)), accountCurrency);
/*    */ 
/* 83 */         perhapsMissingInstrumentSet.addAll(conversionDeps);
/*    */ 
/* 85 */         Set subscribeInsrtuments = new HashSet(perhapsMissingInstrumentSet);
/* 86 */         subscribeInsrtuments.removeAll(oldInstrumentSet);
/*    */ 
/* 90 */         if (subscribeInsrtuments.size() > 0) {
/* 91 */           LOGGER.debug("this group reguire subscription for Instruments: " + subscribeInsrtuments);
/* 92 */           GreedContext.publishEvent(new DirectInstrumentSubscribeAction(this, subscribeInsrtuments));
/*    */         }
/*    */       } else {
/* 95 */         LOGGER.warn("last accountState is null: " + accountStatement);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.OrderMessageUpdateAction
 * JD-Core Version:    0.6.0
 */