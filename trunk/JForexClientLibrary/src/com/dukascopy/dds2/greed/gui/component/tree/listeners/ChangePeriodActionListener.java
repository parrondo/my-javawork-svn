/*    */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ 
/*    */ public class ChangePeriodActionListener
/*    */   implements ActionListener
/*    */ {
/*    */   private JForexPeriod period;
/*    */   private WorkspaceTreeNode workspaceTreeNode;
/*    */   private IChartTabsAndFramesController chartTabsAndFramesController;
/*    */ 
/*    */   public ChangePeriodActionListener(IChartTabsAndFramesController chartTabsAndFramesController, JForexPeriod period, WorkspaceTreeNode workspaceTreeNode)
/*    */   {
/* 18 */     this.period = period;
/* 19 */     this.workspaceTreeNode = workspaceTreeNode;
/* 20 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent event) {
/* 24 */     ChartTreeNode selectedInstrumentNode = (ChartTreeNode)this.workspaceTreeNode;
/* 25 */     Integer panelId = Integer.valueOf(selectedInstrumentNode.getChartPanelId());
/* 26 */     this.chartTabsAndFramesController.changePeriodForChartPanel(panelId, this.period);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.ChangePeriodActionListener
 * JD-Core Version:    0.6.0
 */