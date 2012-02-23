/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ 
/*    */ public class OpenStrategyAction extends StrategyAction
/*    */ {
/*    */   ClientSettingsStorage clientSettingsStorage;
/*    */   WorkspaceNodeFactory workspaceNodeFactory;
/*    */   ServiceSourceEditorPanel panel;
/*    */ 
/*    */   public OpenStrategyAction(WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IChartTabsAndFramesController chartTabsAndFramesController, ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 23 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory);
/*    */ 
/* 25 */     this.clientSettingsStorage = clientSettingsStorage;
/* 26 */     this.workspaceNodeFactory = workspaceNodeFactory;
/*    */   }
/*    */ 
/*    */   public OpenStrategyAction(WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IChartTabsAndFramesController chartTabsAndFramesController, ClientSettingsStorage clientSettingsStorage, ServiceSourceEditorPanel panel)
/*    */   {
/* 36 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory);
/*    */ 
/* 38 */     this.clientSettingsStorage = clientSettingsStorage;
/* 39 */     this.workspaceNodeFactory = workspaceNodeFactory;
/* 40 */     this.panel = panel;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 46 */     JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 47 */     clientFormLayoutManager.getStrategiesPanel().openStrategiesSelection(true);
/* 48 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.OpenStrategyAction
 * JD-Core Version:    0.6.0
 */