/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.persistence.ChartBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import java.util.List;
/*    */ 
/*    */ class AddChartTreeAction extends TreeAction
/*    */ {
/*    */   IChartTabsAndFramesController chartTabsAndFramesController;
/*    */   WorkspaceNodeFactory workspaceNodeFactory;
/*    */   IWorkspaceHelper workspaceHelper;
/*    */   ClientSettingsStorage clientSettingsStorage;
/*    */ 
/*    */   AddChartTreeAction(IChartTabsAndFramesController chartTabsAndFramesController, WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IWorkspaceHelper workspaceHelper, ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 30 */     super(workspaceJTree);
/*    */ 
/* 32 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/* 33 */     this.workspaceNodeFactory = workspaceNodeFactory;
/* 34 */     this.workspaceHelper = workspaceHelper;
/* 35 */     this.clientSettingsStorage = clientSettingsStorage;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 40 */     Object[] params = (Object[])(Object[])param;
/* 41 */     Instrument selectedCurrency = (Instrument)params[1];
/*    */ 
/* 43 */     if (((params[0] instanceof ChartsNode)) && (!this.workspaceHelper.isInstrumentSubscribed(selectedCurrency))) {
/* 44 */       this.workspaceHelper.addDependantCurrenciesAndSubscribe(selectedCurrency);
/*    */     }
/*    */ 
/* 47 */     ChartTreeNode child = createChild(selectedCurrency);
/*    */ 
/* 49 */     this.workspaceJTree.addChartNode(child);
/* 50 */     this.workspaceJTree.selectNode(child);
/*    */ 
/* 52 */     addChart(child);
/* 53 */     saveClientSettingStorage(child, selectedCurrency, this.clientSettingsStorage);
/*    */ 
/* 55 */     return null;
/*    */   }
/*    */ 
/*    */   protected ChartTreeNode createChild(Instrument selectedCurrency) {
/* 59 */     List periods = this.clientSettingsStorage.restoreChartPeriods();
/*    */ 
/* 63 */     JForexPeriod period = (JForexPeriod)periods.get(0);
/* 64 */     return this.workspaceNodeFactory.createChartTreeNode(selectedCurrency, OfferSide.BID, period);
/*    */   }
/*    */ 
/*    */   protected void addChart(ChartTreeNode child) {
/* 68 */     this.chartTabsAndFramesController.addChart(child.getChartPanelId(), child.getInstrument(), child.getJForexPeriod(), child.getOfferSide(), false, true);
/*    */   }
/*    */ 
/*    */   protected void saveClientSettingStorage(ChartTreeNode child, Instrument selectedCurrency, ClientSettingsStorage clientSettingsStorage) {
/* 72 */     clientSettingsStorage.save(new ChartBean(child.getChartPanelId(), selectedCurrency, child.getJForexPeriod(), child.getOfferSide()));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.AddChartTreeAction
 * JD-Core Version:    0.6.0
 */