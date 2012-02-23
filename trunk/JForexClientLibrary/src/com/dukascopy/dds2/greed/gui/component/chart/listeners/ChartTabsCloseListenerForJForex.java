/*    */ package com.dukascopy.dds2.greed.gui.component.chart.listeners;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.ChartPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramePanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ 
/*    */ public class ChartTabsCloseListenerForJForex extends FrameListenerAdapter
/*    */ {
/*    */   private WorkspaceTreeController workspaceTreeController;
/*    */   private IChartTabsAndFramesController iChartTabsAndFramesController;
/*    */   private ClientSettingsStorage clientSettingsStorage;
/*    */ 
/*    */   public ChartTabsCloseListenerForJForex(WorkspaceTreeController workspaceTreeController, IChartTabsAndFramesController chartTabsAndFramesController, ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 23 */     this.workspaceTreeController = workspaceTreeController;
/* 24 */     this.iChartTabsAndFramesController = chartTabsAndFramesController;
/* 25 */     this.clientSettingsStorage = clientSettingsStorage;
/*    */   }
/*    */ 
/*    */   public void frameClosed(TabsAndFramePanel tabsAndFramePanel, int tabCount) {
/* 29 */     if ((tabsAndFramePanel instanceof ChartPanel)) {
/* 30 */       this.workspaceTreeController.chartClosed(tabsAndFramePanel.getPanelId());
/* 31 */       this.iChartTabsAndFramesController.removeEventHandlerFor(Integer.valueOf(tabsAndFramePanel.getPanelId()));
/* 32 */     } else if ((tabsAndFramePanel instanceof ServiceSourceEditorPanel)) {
/* 33 */       ServiceSourceEditorPanel editorPanel = (ServiceSourceEditorPanel)tabsAndFramePanel;
/* 34 */       if (editorPanel.isNewFile()) {
/* 35 */         this.workspaceTreeController.deleteStrategyById(editorPanel.getPanelId());
/*    */       }
/* 37 */       this.clientSettingsStorage.removeServiceEditor(Integer.valueOf(editorPanel.getPanelId()));
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean isCloseAllowed(TabsAndFramePanel tabsAndFramePanel) {
/* 42 */     return tabsAndFramePanel.isCloseAllowed();
/*    */   }
/*    */ 
/*    */   public void frameSelected(int panelId)
/*    */   {
/* 47 */     if (GreedContext.isStrategyAllowed())
/* 48 */       this.workspaceTreeController.selectNode(panelId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.listeners.ChartTabsCloseListenerForJForex
 * JD-Core Version:    0.6.0
 */