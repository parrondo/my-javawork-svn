/*     */ package com.dukascopy.dds2.greed.gui.helpers;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.InstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ 
/*     */ public abstract class CommonWorkspaceHellper
/*     */   implements IWorkspaceHelper
/*     */ {
/*     */   public ClientSettingsStorage getClientSettingsStorage()
/*     */   {
/*  54 */     return (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   }
/*     */ 
/*     */   public MarketView getMarketView() {
/*  58 */     return (MarketView)GreedContext.get("marketView");
/*     */   }
/*     */ 
/*     */   public AccountStatement getAccountStatement() {
/*  62 */     return (AccountStatement)GreedContext.get("accountStatement");
/*     */   }
/*     */ 
/*     */   public CurrencyConverter getCurrencyConverter() {
/*  66 */     return (CurrencyConverter)GreedContext.get("currencyConverter");
/*     */   }
/*     */ 
/*     */   protected MessagePanel getMessagePanel() {
/*  70 */     return ((ClientForm)GreedContext.get("clientGui")).getMessagePanel();
/*     */   }
/*     */ 
/*     */   protected ExposurePanel getExposurePanel() {
/*  74 */     return ((ClientForm)GreedContext.get("clientGui")).getExposurePanel();
/*     */   }
/*     */ 
/*     */   protected PositionsPanel getPositionsPanel() {
/*  78 */     return ((ClientForm)GreedContext.get("clientGui")).getPositionsPanel();
/*     */   }
/*     */ 
/*     */   protected OrdersPanel getOrdersPanel() {
/*  82 */     return ((ClientForm)GreedContext.get("clientGui")).getOrdersPanel();
/*     */   }
/*     */ 
/*     */   public void subscribeToInstruments(Set<String> newInstrumentList)
/*     */   {
/*  87 */     AppActionEvent event = new InstrumentSubscribeAction(this, newInstrumentList);
/*  88 */     GreedContext.publishEvent(event);
/*     */   }
/*     */ 
/*     */   public boolean isInstrumentSubscribed(Instrument instr)
/*     */   {
/*  93 */     FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/*  94 */     if (feedDataProvider == null) {
/*  95 */       return false;
/*     */     }
/*  97 */     List list = feedDataProvider.getInstrumentsCurrentlySubscribed();
/*  98 */     return (list != null) && (list.contains(instr));
/*     */   }
/*     */ 
/*     */   public void addDependantCurrenciesAndSubscribe(Instrument addedInstrument)
/*     */   {
/* 103 */     Set dependantCurrencyNames = OrderUtils.fetchCurrenciesNeededForProfitlossCalculation(addedInstrument.toString());
/*     */ 
/* 105 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/*     */ 
/* 107 */     List workspaceInstruments = workspacePanel.getInstruments();
/* 108 */     workspaceInstruments.add(addedInstrument.toString());
/*     */ 
/* 110 */     Set mergedInstruments = new HashSet();
/*     */ 
/* 112 */     mergedInstruments.addAll(workspaceInstruments);
/* 113 */     mergedInstruments.addAll(dependantCurrencyNames);
/*     */ 
/* 115 */     subscribeToInstruments(mergedInstruments);
/*     */   }
/*     */ 
/*     */   public Instrument[] getAvailableInstrumentsAsArray()
/*     */   {
/* 120 */     List instruments = new ArrayList();
/*     */ 
/* 122 */     for (Instrument instr : getMarketView().getActiveInstruments()) {
/* 123 */       if (InstrumentAvailabilityManager.getInstance().isAllowed(instr)) {
/* 124 */         instruments.add(instr);
/*     */       }
/*     */     }
/* 127 */     return (Instrument[])(Instrument[])instruments.toArray(new Instrument[0]);
/*     */   }
/*     */ 
/*     */   public void addDependantCurrenciesAndSubscribe(Set<Instrument> currenciesToAdd) {
/* 131 */     Set mergedInstruments = new HashSet();
/* 132 */     for (Instrument instrument : currenciesToAdd) {
/* 133 */       if (!InstrumentAvailabilityManager.getInstance().isAllowed(instrument)) {
/* 134 */         NotificationUtils.getInstance().postWarningMessage("It's not allowed to subscribe commodity instruments");
/*     */       } else {
/* 136 */         Set dependantCurrencyNames = OrderUtils.fetchCurrenciesNeededForProfitlossCalculation(instrument.toString());
/* 137 */         mergedInstruments.addAll(dependantCurrencyNames);
/* 138 */         mergedInstruments.add(instrument.toString());
/*     */       }
/*     */     }
/*     */ 
/* 142 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/* 143 */     List existingCurrencyNames = workspacePanel.getInstruments();
/* 144 */     mergedInstruments.addAll(existingCurrencyNames);
/*     */ 
/* 146 */     subscribeToInstruments(mergedInstruments);
/*     */   }
/*     */ 
/*     */   public static String getPositionsLabelKey() {
/* 150 */     if (!GreedContext.IS_FXDD_LABEL) {
/* 151 */       if (GreedContext.isStrategyAllowed()) {
/* 152 */         return "tab.positions";
/*     */       }
/* 154 */       return "header.position.structure";
/*     */     }
/* 156 */     return "header.exposure.structure";
/*     */   }
/*     */ 
/*     */   public static String getPositionsSummaryLabelKey()
/*     */   {
/* 161 */     if (!GreedContext.IS_FXDD_LABEL) {
/* 162 */       if (GreedContext.isStrategyAllowed()) {
/* 163 */         return "tab.position.summary";
/*     */       }
/* 165 */       return "header.position.summary";
/*     */     }
/* 167 */     return "header.exposure.summary";
/*     */   }
/*     */ 
/*     */   public abstract int calculateNextNodeIndexToBeFocused(int paramInt);
/*     */ 
/*     */   public abstract int calculatePreviousNodeIndxToBeFocused(int paramInt);
/*     */ 
/*     */   public abstract void checkDependantCurrenciesAndAddThemIfNecessary(WorkspaceJTree paramWorkspaceJTree, String paramString);
/*     */ 
/*     */   public abstract void dispose();
/*     */ 
/*     */   public abstract void findAndsubscribeToCurrenciesForProfitLossCalculation();
/*     */ 
/*     */   public abstract Instrument getSelectedInstrument(TreeSelectionEvent paramTreeSelectionEvent);
/*     */ 
/*     */   public abstract Set<Instrument> getSubscribedInstruments();
/*     */ 
/*     */   public abstract Set<Instrument> getUnsubscribedInstruments();
/*     */ 
/*     */   public abstract void loadDataIntoWorkspace(WorkspaceJTree paramWorkspaceJTree);
/*     */ 
/*     */   public abstract void refreshDealPanel(Instrument paramInstrument);
/*     */ 
/*     */   public abstract void selectTabForSelectedNode(WorkspaceJTree paramWorkspaceJTree);
/*     */ 
/*     */   public abstract long startStrategy(File paramFile, IStrategyListener paramIStrategyListener, Map<String, Object> paramMap, boolean paramBoolean)
/*     */     throws JFException;
/*     */ 
/*     */   public abstract long startStrategy(IStrategy paramIStrategy, IStrategyListener paramIStrategyListener, boolean paramBoolean)
/*     */     throws JFException;
/*     */ 
/*     */   public abstract void populateWorkspace();
/*     */ 
/*     */   public abstract void showChart(Instrument paramInstrument);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.helpers.CommonWorkspaceHellper
 * JD-Core Version:    0.6.0
 */