/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.request.QuoteUnsubscribeRequestMessage;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class InstrumentSubscribeAction extends AppActionEvent
/*     */ {
/*  29 */   private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentSubscribeAction.class);
/*     */   private Set<String> newInstrumentSet;
/*     */   private Set<String> oldInstrumentSet;
/*     */   private Set<String> unsubscribeInstruments;
/*     */   private Set<String> subscribeInsrtuments;
/*     */ 
/*     */   public InstrumentSubscribeAction(Object source, Set<String> newInstrumentList)
/*     */   {
/*  37 */     super(source, false, true);
/*  38 */     this.newInstrumentSet = newInstrumentList;
/*  39 */     LOGGER.debug("subscribe action: " + newInstrumentList);
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  44 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*     */     try
/*     */     {
/*  48 */       Runnable runnable = new Runnable() {
/*     */         public void run() {
/*  50 */           ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  51 */           InstrumentSubscribeAction.access$002(InstrumentSubscribeAction.this, new HashSet(clientSettingsStorage.restoreSelectedInstruments()));
/*     */ 
/*  53 */           ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/*  54 */           if ((clientGui != null) && (clientGui.getPositionsPanel() != null) && (clientGui.getPositionsPanel().getTable().getModel() != null))
/*     */           {
/*  57 */             List positionList = ((PositionsTableModel)clientGui.getPositionsPanel().getTable().getModel()).getPositions();
/*  58 */             for (Position pos : positionList) {
/*  59 */               if (!InstrumentSubscribeAction.this.newInstrumentSet.contains(pos.getInstrument())) {
/*  60 */                 InstrumentSubscribeAction.this.newInstrumentSet.add(pos.getInstrument());
/*     */               }
/*     */             }
/*     */           }
/*  64 */           if ((clientGui != null) && (clientGui.getOrdersPanel() != null) && (clientGui.getOrdersPanel().getOrdersTable().getModel() != null))
/*     */           {
/*  67 */             OrderCommonTableModel orderModel = (OrderCommonTableModel)((TableSorter)clientGui.getOrdersPanel().getOrdersTable().getModel()).getTableModel();
/*  68 */             int i = 0; for (int n = orderModel.getRowCount(); i < n; i++)
/*     */             {
/*  70 */               String instrument = orderModel.getOrder(i).getInstrument();
/*  71 */               if (!InstrumentSubscribeAction.this.newInstrumentSet.contains(instrument))
/*  72 */                 InstrumentSubscribeAction.this.newInstrumentSet.add(instrument);
/*     */             }
/*     */           }
/*     */         }
/*     */       };
/*  78 */       if (!SwingUtilities.isEventDispatchThread())
/*  79 */         SwingUtilities.invokeAndWait(runnable);
/*     */       else
/*  81 */         runnable.run();
/*     */     }
/*     */     catch (InterruptedException e) {
/*  84 */       LOGGER.error(e.getMessage(), e);
/*     */     } catch (InvocationTargetException e) {
/*  86 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/*  89 */     this.unsubscribeInstruments = new HashSet(this.oldInstrumentSet);
/*  90 */     this.unsubscribeInstruments.removeAll(this.newInstrumentSet);
/*     */ 
/*  92 */     if (this.unsubscribeInstruments.size() > 0) {
/*  93 */       QuoteUnsubscribeRequestMessage unsubscribe = new QuoteUnsubscribeRequestMessage();
/*  94 */       unsubscribe.setInstruments(new ArrayList(this.unsubscribeInstruments));
/*  95 */       LOGGER.debug("Unsubscribe from " + this.unsubscribeInstruments);
/*  96 */       transport.controlRequest(unsubscribe);
/*     */     }
/*     */ 
/*  99 */     this.subscribeInsrtuments = new HashSet(this.newInstrumentSet);
/* 100 */     this.subscribeInsrtuments.removeAll(this.oldInstrumentSet);
/*     */ 
/* 103 */     if (this.subscribeInsrtuments.size() > 0) {
/* 104 */       QuoteSubscribeRequestMessage subscribe = new QuoteSubscribeRequestMessage();
/* 105 */       ArrayList subscribeInstrumentList = new ArrayList(this.subscribeInsrtuments);
/*     */ 
/* 107 */       subscribe.setInstruments(subscribeInstrumentList);
/* 108 */       subscribe.setQuotesOnly(Boolean.valueOf(true));
/* 109 */       transport.controlRequest(subscribe);
/*     */ 
/* 111 */       if (LOGGER.isDebugEnabled())
/* 112 */         LOGGER.debug("Subscribe to " + this.subscribeInsrtuments);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/* 120 */     FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/*     */ 
/* 122 */     if (feedDataProvider != null) {
/* 123 */       feedDataProvider.setInstrumentNamesSubscribed(this.newInstrumentSet);
/*     */     }
/*     */ 
/* 126 */     ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/* 127 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 128 */     clientSettingsStorage.saveSelectedInstruments(this.newInstrumentSet);
/* 129 */     clientGui.refresh();
/* 130 */     ((MarketOverviewFrame)GreedContext.get("Dock")).updateSubscription();
/*     */ 
/* 133 */     if (this.unsubscribeInstruments.size() > 0) {
/* 134 */       MarketView mv = (MarketView)GreedContext.get("marketView");
/* 135 */       if (LOGGER.isDebugEnabled()) {
/* 136 */         LOGGER.debug("cleaning mv except: " + this.newInstrumentSet);
/*     */       }
/* 138 */       mv.cleanCurrencyMarketsExcept(this.newInstrumentSet);
/*     */     }
/*     */ 
/* 141 */     WorkspacePanel workspacePanel = clientGui.getDealPanel().getWorkspacePanel();
/*     */ 
/* 143 */     if (LOGGER.isDebugEnabled()) {
/* 144 */       LOGGER.debug("WorkspacePanel set instruments : " + this.newInstrumentSet);
/*     */     }
/*     */ 
/* 147 */     workspacePanel.setInstruments(new ArrayList(this.newInstrumentSet));
/*     */ 
/* 149 */     if (LOGGER.isDebugEnabled())
/* 150 */       LOGGER.debug("WorkspacePanel instruments after set : " + workspacePanel.getInstruments());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.InstrumentSubscribeAction
 * JD-Core Version:    0.6.0
 */