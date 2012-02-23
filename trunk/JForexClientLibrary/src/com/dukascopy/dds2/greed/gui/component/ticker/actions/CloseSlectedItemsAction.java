/*     */ package com.dukascopy.dds2.greed.gui.component.ticker.actions;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.TesterChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ public class CloseSlectedItemsAction extends AbstractAction
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private WorkspacePanel workspacePanel;
/*     */   private ChartsNode chartsTreeNode;
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/*  53 */     this.workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/*  54 */     if (e.getActionCommand().equals("item.close.currency"))
/*  55 */       removeInstruments(this.workspacePanel.getSelectedInstruments());
/*  56 */     else if (e.getActionCommand().equals("item.remove.currency.all"))
/*  57 */       removeAllInstruments();
/*     */   }
/*     */ 
/*     */   private void removeInstruments(String[] removableInstruments)
/*     */   {
/*  62 */     List workspaceInstruments = this.workspacePanel.getInstruments();
/*     */ 
/*  64 */     for (String instr : removableInstruments) {
/*  65 */       if (OrderUtils.isInstrumentsUsedByThePlatform(Instrument.fromString(instr)))
/*     */       {
/*  67 */         JOptionPane.showMessageDialog(this.workspacePanel, LocalizationManager.getText("joption.pane.instrument.cannot.be.deleted"));
/*     */       }
/*  70 */       else if (areThereAnyOpenChartsWithThisCurrency(instr)) {
/*  71 */         if (userAgreesToDeleteCharts()) {
/*  72 */           removeOpenChartNodesWithThisCurrency(instr);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  77 */         workspaceInstruments.remove(instr);
/*     */       }
/*     */     }
/*     */ 
/*  81 */     if (workspaceInstruments.size() < 1) {
/*  82 */       JOptionPane.showMessageDialog(this.workspacePanel, LocalizationManager.getText("joption.pane.at.least.one.currency"));
/*  83 */       return;
/*     */     }
/*     */ 
/*  86 */     ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper().subscribeToInstruments(new HashSet(workspaceInstruments));
/*     */   }
/*     */ 
/*     */   private void removeAllInstruments()
/*     */   {
/*  91 */     List workspaceInstruments = this.workspacePanel.getInstruments();
/*  92 */     List unremovableInstruments = new ArrayList();
/*  93 */     for (String instr : workspaceInstruments) {
/*  94 */       if ((OrderUtils.isOpenedPosOrOrdersForInstrument(Instrument.fromString(instr))) || (areThereAnyOpenChartsWithThisCurrency(instr)))
/*     */       {
/*  96 */         unremovableInstruments.addAll(OrderUtils.fetchCurrenciesNeededForProfitlossCalculation(instr));
/*  97 */         if (!unremovableInstruments.contains(instr)) {
/*  98 */           unremovableInstruments.add(instr);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 103 */     if (unremovableInstruments.isEmpty()) {
/* 104 */       unremovableInstruments.add(Instrument.EURUSD.toString());
/*     */     }
/*     */ 
/* 107 */     ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper().subscribeToInstruments(new HashSet(unremovableInstruments));
/*     */ 
/* 110 */     ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel().setInstruments(unremovableInstruments);
/*     */   }
/*     */ 
/*     */   private ChartsNode getChartsNode()
/*     */   {
/* 116 */     if (this.chartsTreeNode == null) {
/* 117 */       WorkspaceJTree workspaceJTree = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree();
/* 118 */       this.chartsTreeNode = ((WorkspaceRootNode)workspaceJTree.getModel().getRoot()).getChartsNode();
/*     */     }
/* 120 */     return this.chartsTreeNode;
/*     */   }
/*     */ 
/*     */   private boolean userAgreesToDeleteCharts() {
/* 124 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/* 125 */     return JOptionPane.showConfirmDialog(workspacePanel, LocalizationManager.getText("joption.pane.open.charts.for.currency"), LocalizationManager.getText("joption.pane.confirmation"), 0) == 0;
/*     */   }
/*     */ 
/*     */   private boolean areThereAnyOpenChartsWithThisCurrency(String instrument)
/*     */   {
/* 134 */     for (int i = 0; i < getChartsNode().getChildCount(); i++) {
/* 135 */       TreeNode childAt = getChartsNode().getChildAt(i);
/* 136 */       if (((childAt instanceof ChartTreeNode)) && (!(childAt instanceof TesterChartTreeNode))) {
/* 137 */         ChartTreeNode node = (ChartTreeNode)childAt;
/* 138 */         if (node.getInstrument().equals(Instrument.fromString(instrument))) {
/* 139 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 143 */     return false;
/*     */   }
/*     */ 
/*     */   private void removeOpenChartNodesWithThisCurrency(String instrument) {
/* 147 */     WorkspaceJTree workspaceJTree = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree();
/* 148 */     Map chartNodesToDelete = new HashMap();
/* 149 */     for (int i = 0; i < getChartsNode().getChildCount(); i++) {
/* 150 */       TreeNode childAt = getChartsNode().getChildAt(i);
/* 151 */       if (((childAt instanceof ChartTreeNode)) && (!(childAt instanceof TesterChartTreeNode))) {
/* 152 */         ChartTreeNode node = (ChartTreeNode)childAt;
/* 153 */         if (node.getInstrument().equals(Instrument.fromString(instrument))) {
/* 154 */           chartNodesToDelete.put(Integer.valueOf(i), node);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 159 */     int[] indArray = new int[chartNodesToDelete.size()];
/* 160 */     ChartTreeNode[] chartsArray = new ChartTreeNode[chartNodesToDelete.size()];
/* 161 */     int i = 0;
/* 162 */     for (Integer key : chartNodesToDelete.keySet()) {
/* 163 */       indArray[i] = key.intValue();
/* 164 */       chartsArray[i] = ((ChartTreeNode)chartNodesToDelete.get(key));
/* 165 */       i++;
/*     */     }
/* 167 */     getChartsNode().removeAll(chartNodesToDelete.values());
/* 168 */     workspaceJTree.getModel().nodesWereRemoved(getChartsNode(), indArray, chartsArray);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ticker.actions.CloseSlectedItemsAction
 * JD-Core Version:    0.6.0
 */