/*    */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ 
/*    */ public class ChangeInsturmentActionListener
/*    */   implements ActionListener
/*    */ {
/*    */   private Instrument instrument;
/*    */   private WorkspaceTreeNode workspaceTreeNode;
/*    */   private IChartTabsAndFramesController chartTabsAndFramesController;
/*    */ 
/*    */   public ChangeInsturmentActionListener(IChartTabsAndFramesController chartTabsAndFramesController, Instrument instrument, WorkspaceTreeNode workspaceTreeNode)
/*    */   {
/* 22 */     this.instrument = instrument;
/* 23 */     this.workspaceTreeNode = workspaceTreeNode;
/* 24 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent event) {
/* 28 */     ChartTreeNode selectedInstrumentNode = (ChartTreeNode)this.workspaceTreeNode;
/* 29 */     Integer panelId = Integer.valueOf(selectedInstrumentNode.getChartPanelId());
/* 30 */     this.chartTabsAndFramesController.changeInsturmentForChartPanel(panelId, this.instrument);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.ChangeInsturmentActionListener
 * JD-Core Version:    0.6.0
 */