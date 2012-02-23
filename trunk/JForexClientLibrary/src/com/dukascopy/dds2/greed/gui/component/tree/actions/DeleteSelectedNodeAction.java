/*     */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.CustomIndicatorBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.TesterChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ class DeleteSelectedNodeAction extends TreeAction
/*     */ {
/*     */   IWorkspaceHelper workspaceHelper;
/*     */   DDSChartsController ddsChartsController;
/*     */   IChartTabsAndFramesController chartTabsAndFramesController;
/*     */   ClientSettingsStorage clientSettingsStorage;
/*     */ 
/*     */   DeleteSelectedNodeAction(WorkspaceJTree workspaceJTree, IWorkspaceHelper workspaceHelper, DDSChartsController ddsChartsController, IChartTabsAndFramesController chartTabsAndFramesController, ClientSettingsStorage clientSettingsStorage)
/*     */   {
/*  54 */     super(workspaceJTree);
/*  55 */     this.workspaceHelper = workspaceHelper;
/*  56 */     this.ddsChartsController = ddsChartsController;
/*  57 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*  58 */     this.clientSettingsStorage = clientSettingsStorage;
/*     */   }
/*     */ 
/*     */   protected Object executeInternal(Object param)
/*     */   {
/*  63 */     if (this.workspaceJTree.getSelectionCount() < 2)
/*  64 */       performSingleDeletion((WorkspaceTreeNode)param);
/*     */     else {
/*  66 */       performMultipleDeletion(this.workspaceJTree.getSelectionPaths());
/*     */     }
/*     */ 
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */   void performMultipleDeletion(TreePath[] selectionPaths)
/*     */   {
/*  75 */     Map itemsToBeDeleted = new HashMap();
/*  76 */     for (TreePath selectionPath : selectionPaths) {
/*  77 */       WorkspaceTreeNode aNode = (WorkspaceTreeNode)selectionPath.getLastPathComponent();
/*  78 */       ChartTreeNode chartTreeNode = (ChartTreeNode)aNode.getParent();
/*  79 */       Map map = (Map)itemsToBeDeleted.get(Integer.valueOf(chartTreeNode.getChartPanelId()));
/*  80 */       if (map == null) {
/*  81 */         map = new HashMap();
/*  82 */         itemsToBeDeleted.put(Integer.valueOf(chartTreeNode.getChartPanelId()), map);
/*     */       }
/*  84 */       if ((aNode instanceof DrawingTreeNode)) {
/*  85 */         List drawings = (List)map.get(Integer.valueOf(0));
/*  86 */         if (drawings == null) {
/*  87 */           drawings = new LinkedList();
/*  88 */           map.put(Integer.valueOf(0), drawings);
/*     */         }
/*  90 */         drawings.add((DrawingTreeNode)aNode);
/*  91 */       } else if ((aNode instanceof IndicatorTreeNode)) {
/*  92 */         List drawings = (List)map.get(Integer.valueOf(1));
/*  93 */         if (drawings == null) {
/*  94 */           drawings = new LinkedList();
/*  95 */           map.put(Integer.valueOf(1), drawings);
/*     */         }
/*  97 */         drawings.add((IndicatorTreeNode)aNode);
/*     */       }
/*     */     }
/*     */ 
/* 101 */     for (Map.Entry chartsMap : itemsToBeDeleted.entrySet())
/*     */     {
/* 103 */       Map chartObjectsMap = (Map)chartsMap.getValue();
/*     */ 
/* 105 */       List drawings = (List)chartObjectsMap.get(Integer.valueOf(0));
/* 106 */       if (drawings != null) {
/* 107 */         List chartObjects = new ArrayList(drawings.size());
/* 108 */         for (DrawingTreeNode drawing : drawings) {
/* 109 */           chartObjects.add(drawing.getDrawing());
/*     */         }
/* 111 */         this.ddsChartsController.remove((Integer)chartsMap.getKey(), chartObjects);
/*     */       }
/*     */ 
/* 114 */       List indicators = (List)chartObjectsMap.get(Integer.valueOf(1));
/* 115 */       if (indicators != null) {
/* 116 */         List indicatorWrappers = new ArrayList(indicators.size());
/* 117 */         for (IndicatorTreeNode indicatorTreeNode : indicators) {
/* 118 */           indicatorWrappers.add(indicatorTreeNode.getIndicator());
/*     */         }
/* 120 */         this.ddsChartsController.deleteIndicators((Integer)chartsMap.getKey(), indicatorWrappers);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void performSingleDeletion(WorkspaceTreeNode workspaceTreeNode)
/*     */   {
/* 128 */     if ((workspaceTreeNode instanceof ChartTreeNode))
/* 129 */       deleteChart((ChartTreeNode)workspaceTreeNode);
/* 130 */     else if ((workspaceTreeNode instanceof IndicatorTreeNode))
/* 131 */       deleteIndicator((IndicatorTreeNode)workspaceTreeNode);
/* 132 */     else if ((workspaceTreeNode instanceof StrategyTreeNode))
/* 133 */       deleteStrategy((StrategyTreeNode)workspaceTreeNode);
/* 134 */     else if ((workspaceTreeNode instanceof CustIndTreeNode))
/* 135 */       deleteCustInd((CustIndTreeNode)workspaceTreeNode);
/* 136 */     else if ((workspaceTreeNode instanceof DrawingTreeNode))
/* 137 */       deleteDrawing((DrawingTreeNode)workspaceTreeNode);
/*     */   }
/*     */ 
/*     */   void deleteChart(ChartTreeNode chartTreeNode)
/*     */   {
/* 142 */     this.workspaceJTree.changeSelectedNode(chartTreeNode);
/* 143 */     this.workspaceJTree.getModel().removeNodeFromParent(chartTreeNode);
/* 144 */     this.chartTabsAndFramesController.removeEventHandlerFor(Integer.valueOf(chartTreeNode.getChartPanelId()));
/*     */   }
/*     */ 
/*     */   void deleteCustInd(CustIndTreeNode custIndTreeNode)
/*     */   {
/* 149 */     this.workspaceJTree.changeSelectedNode(custIndTreeNode);
/* 150 */     this.workspaceJTree.getModel().removeNodeFromParent(custIndTreeNode);
/* 151 */     CustIndicatorWrapper indicatorWrapper = custIndTreeNode.getServiceWrapper();
/* 152 */     CustomIndicatorBean indicatorBean = new CustomIndicatorBean(custIndTreeNode.getId(), indicatorWrapper.getSourceFile(), indicatorWrapper.getBinaryFile());
/* 153 */     this.clientSettingsStorage.remove(indicatorBean);
/*     */   }
/*     */ 
/*     */   void deleteStrategy(StrategyTreeNode strategyTreeNode) {
/* 157 */     if (!strategyTreeNode.isRunning()) {
/* 158 */       this.workspaceJTree.removeServiceTreeNode(strategyTreeNode);
/*     */ 
/* 160 */       JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 161 */       clientFormLayoutManager.getStrategiesPanel().removeStrategy(strategyTreeNode.getStrategy().getId().intValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   void deleteIndicator(IndicatorTreeNode indicatorTreeNode) {
/* 166 */     this.workspaceJTree.changeSelectedNode(indicatorTreeNode);
/* 167 */     ChartTreeNode chartTreeNode = (ChartTreeNode)indicatorTreeNode.getParent();
/* 168 */     this.ddsChartsController.deleteIndicator(Integer.valueOf(chartTreeNode.getChartPanelId()), indicatorTreeNode.getIndicator());
/*     */   }
/*     */ 
/*     */   void deleteDrawing(DrawingTreeNode drawingTreeNode) {
/* 172 */     this.workspaceJTree.changeSelectedNode(drawingTreeNode);
/* 173 */     ChartTreeNode chartTreeNode = (ChartTreeNode)drawingTreeNode.getParent();
/* 174 */     this.ddsChartsController.remove(Integer.valueOf(chartTreeNode.getChartPanelId()), drawingTreeNode.getDrawing());
/*     */   }
/*     */ 
/*     */   boolean userAgreesToDeleteCharts() {
/* 178 */     return JOptionPane.showConfirmDialog(this.workspaceJTree, LocalizationManager.getText("joption.pane.open.charts.for.currency"), LocalizationManager.getText("joption.pane.confirmation"), 0) == 0;
/*     */   }
/*     */ 
/*     */   boolean userAgreesToStopStrategies()
/*     */   {
/* 186 */     return JOptionPane.showConfirmDialog(this.workspaceJTree, LocalizationManager.getText("joption.pane.running.strategies.for.currency"), LocalizationManager.getText("joption.pane.confirmation"), 0) == 0;
/*     */   }
/*     */ 
/*     */   boolean areThereAnyOpenChartsWithThisCurrency(Instrument instrument, ChartsNode chartsTreeNode)
/*     */   {
/* 194 */     for (int i = 0; i < chartsTreeNode.getChildCount(); i++) {
/* 195 */       TreeNode childAt = chartsTreeNode.getChildAt(i);
/* 196 */       if (((childAt instanceof ChartTreeNode)) && (!(childAt instanceof TesterChartTreeNode))) {
/* 197 */         ChartTreeNode node = (ChartTreeNode)childAt;
/* 198 */         if (node.getInstrument().equals(instrument)) {
/* 199 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 203 */     return false;
/*     */   }
/*     */ 
/*     */   void removeOpenChartNodesWithThisCurrency(Instrument instrument, ChartsNode chartsTreeNode) {
/* 207 */     Map chartNodesToDelete = new HashMap();
/* 208 */     for (int i = 0; i < chartsTreeNode.getChildCount(); i++) {
/* 209 */       TreeNode childAt = chartsTreeNode.getChildAt(i);
/* 210 */       if (((childAt instanceof ChartTreeNode)) && (!(childAt instanceof TesterChartTreeNode))) {
/* 211 */         ChartTreeNode node = (ChartTreeNode)childAt;
/* 212 */         if (node.getInstrument().equals(instrument)) {
/* 213 */           chartNodesToDelete.put(Integer.valueOf(i), node);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 218 */     int[] indArray = new int[chartNodesToDelete.size()];
/* 219 */     ChartTreeNode[] chartsArray = new ChartTreeNode[chartNodesToDelete.size()];
/* 220 */     int i = 0;
/* 221 */     for (Integer key : chartNodesToDelete.keySet()) {
/* 222 */       indArray[i] = key.intValue();
/* 223 */       chartsArray[i] = ((ChartTreeNode)chartNodesToDelete.get(key));
/* 224 */       i++;
/*     */     }
/* 226 */     chartsTreeNode.removeAll(chartNodesToDelete.values());
/* 227 */     this.workspaceJTree.getModel().nodesWereRemoved(chartsTreeNode, indArray, chartsArray);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.DeleteSelectedNodeAction
 * JD-Core Version:    0.6.0
 */