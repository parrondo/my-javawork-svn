/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.persistence.ChartBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ 
/*    */ public class AddChartTemplateTreeAction extends AddChartTreeAction
/*    */ {
/*    */   private ChartBean chartBean;
/*    */ 
/*    */   public AddChartTemplateTreeAction(IChartTabsAndFramesController chartTabsAndFramesController, WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IWorkspaceHelper workspaceHelper, ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 27 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory, workspaceHelper, clientSettingsStorage);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 33 */     Object[] params = (Object[])(Object[])param;
/*    */ 
/* 36 */     this.chartBean = ((ChartBean)params[2]);
/*    */ 
/* 38 */     return super.executeInternal(param);
/*    */   }
/*    */ 
/*    */   protected ChartTreeNode createChild(Instrument selectedCurrency)
/*    */   {
/*    */     ChartTreeNode chartTreeNode;
/*    */     ChartTreeNode chartTreeNode;
/* 45 */     if (this.chartBean.isHistoricalTesterChart())
/* 46 */       chartTreeNode = this.workspaceNodeFactory.createTesterChartTreeNode(selectedCurrency, this.chartBean.getOfferSide(), this.chartBean.getJForexPeriod());
/*    */     else {
/* 48 */       chartTreeNode = this.workspaceNodeFactory.createChartTreeNode(selectedCurrency, this.chartBean.getOfferSide(), this.chartBean.getJForexPeriod());
/*    */     }
/*    */ 
/* 51 */     this.chartBean.setId(chartTreeNode.getChartPanelId());
/* 52 */     this.chartBean.setInstrument(selectedCurrency);
/* 53 */     return chartTreeNode;
/*    */   }
/*    */ 
/*    */   protected void addChart(ChartTreeNode child)
/*    */   {
/* 58 */     this.chartTabsAndFramesController.addChart(this.chartBean, false, true);
/*    */   }
/*    */ 
/*    */   protected void saveClientSettingStorage(ChartTreeNode child, Instrument selectedCurrency, ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 64 */     if (!this.chartBean.isHistoricalTesterChart())
/* 65 */       clientSettingsStorage.save(new ChartBean(this.chartBean.getId(), this.chartBean.getInstrument(), this.chartBean.getJForexPeriod(), this.chartBean.getOfferSide()));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.AddChartTemplateTreeAction
 * JD-Core Version:    0.6.0
 */