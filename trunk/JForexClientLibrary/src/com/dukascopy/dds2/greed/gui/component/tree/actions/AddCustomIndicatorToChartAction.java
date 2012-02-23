/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*    */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*    */ import com.dukascopy.charts.persistence.IChartClient;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ 
/*    */ public class AddCustomIndicatorToChartAction extends TreeAction
/*    */ {
/*    */   DDSChartsController ddsChartsController;
/*    */ 
/*    */   protected AddCustomIndicatorToChartAction(WorkspaceJTree workspaceJTree, DDSChartsController ddsChartsController)
/*    */   {
/* 15 */     super(workspaceJTree);
/* 16 */     this.ddsChartsController = ddsChartsController;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 22 */     if ((param instanceof CustIndTreeNode)) {
/* 23 */       CustIndTreeNode custIndTreeNode = (CustIndTreeNode)param;
/*    */ 
/* 25 */       int chartPanelId = this.ddsChartsController.getChartClient().getLastActiveChartPanelId();
/*    */ 
/* 27 */       String name = IndicatorsProvider.getInstance().enableIndicator(custIndTreeNode.getServiceWrapper(), NotificationUtilsProvider.getNotificationUtils());
/*    */ 
/* 30 */       if ((name != null) && (!name.isEmpty())) {
/* 31 */         IndicatorWrapper wrapper = new IndicatorWrapper(name);
/* 32 */         this.ddsChartsController.addIndicator(Integer.valueOf(chartPanelId), wrapper);
/*    */ 
/* 35 */         this.ddsChartsController.editIndicator(Integer.valueOf(chartPanelId), wrapper.getSubPanelId().intValue(), wrapper);
/*    */       }
/*    */     }
/*    */ 
/* 39 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.AddCustomIndicatorToChartAction
 * JD-Core Version:    0.6.0
 */