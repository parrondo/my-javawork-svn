/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ServicesTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ 
/*    */ class ClearAllAction extends DeleteSelectedNodeAction
/*    */ {
/*    */   ClearAllAction(WorkspaceJTree workspaceJTree, IWorkspaceHelper workspaceHelper, DDSChartsController ddsChartsController, IChartTabsAndFramesController chartTabsAndFramesController, ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 21 */     super(workspaceJTree, workspaceHelper, ddsChartsController, chartTabsAndFramesController, clientSettingsStorage);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 26 */     WorkspaceTreeNode workspaceTreeNode = (WorkspaceTreeNode)param;
/*    */ 
/* 28 */     if (!(workspaceTreeNode instanceof ServicesTreeNode)) {
/* 29 */       return null;
/*    */     }
/*    */ 
/* 32 */     ServicesTreeNode servicesTreeNode = (ServicesTreeNode)workspaceTreeNode;
/*    */ 
/* 34 */     int servicesCount = servicesTreeNode.getChildCount();
/*    */ 
/* 36 */     for (int i = servicesCount - 1; i >= 0; i--) {
/* 37 */       WorkspaceTreeNode serviceTreeNode = (WorkspaceTreeNode)servicesTreeNode.getChildAt(i);
/*    */ 
/* 39 */       if ((serviceTreeNode instanceof CustIndTreeNode)) {
/* 40 */         deleteCustInd((CustIndTreeNode)serviceTreeNode);
/*    */       }
/* 42 */       else if ((serviceTreeNode instanceof StrategyTreeNode)) {
/* 43 */         StrategyTreeNode strategyTreeNode = (StrategyTreeNode)serviceTreeNode;
/* 44 */         deleteStrategy(strategyTreeNode);
/*    */       }
/*    */     }
/*    */ 
/* 48 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.ClearAllAction
 * JD-Core Version:    0.6.0
 */