/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterChartData;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ServicesTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategiesNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class WorkspaceTreeControllerImpl
/*     */   implements WorkspaceTreeController
/*     */ {
/*     */   WorkspaceJTree workspaceJTree;
/*     */   WorkspaceNodeFactory workspaceNodeFactory;
/*     */   WorkspaceTreePanel workspaceTreePanel;
/*     */   TreeActionFactory treeActionFactory;
/*     */   IWorkspaceHelper workspaceHelper;
/*     */   ClientSettingsStorage clientSettingsStorage;
/*     */ 
/*     */   public void indicatorAdded(int chartPanelId, int subChartId, IndicatorWrapper indicatorWrapper)
/*     */   {
/*  57 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/*  58 */     ChartsNode chartsTreeNode = workspaceRootNode.getChartsNode();
/*  59 */     ChartTreeNode chartTreeNode = chartsTreeNode.getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/*  60 */     IndicatorTreeNode indicatorTreeNode = this.workspaceNodeFactory.createIndicatorTreeNode(subChartId, indicatorWrapper, chartTreeNode);
/*  61 */     this.workspaceJTree.addIndicatorNode(indicatorTreeNode);
/*     */ 
/*  63 */     if (indicatorWrapper.isChangeTreeSelection()) {
/*  64 */       this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(indicatorTreeNode));
/*     */     }
/*     */     else
/*  67 */       this.workspaceJTree.expandPath(this.workspaceJTree.getPath((WorkspaceTreeNode)indicatorTreeNode.getParent()));
/*     */   }
/*     */ 
/*     */   public void synchronizeCustomIndicatorsNode()
/*     */   {
/*  72 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/*  73 */     IndicatorsNode indicatorsTreeNode = workspaceRootNode.getIndicatorsTreeNode();
/*     */ 
/*  75 */     IndicatorsProvider indicatorsProvider = IndicatorsProvider.getInstance();
/*  76 */     Collection indNamesToAdd = indicatorsProvider.getCustomIndictorNames();
/*     */ 
/*  78 */     for (Enumeration nodes = indicatorsTreeNode.children(); nodes.hasMoreElements(); ) {
/*  79 */       CustIndTreeNode node = (CustIndTreeNode)nodes.nextElement();
/*  80 */       nodeBinFile = node.getServiceWrapper().getBinaryFile();
/*  81 */       if (nodeBinFile == null)
/*     */       {
/*     */         continue;
/*     */       }
/*  85 */       for (String name : indNamesToAdd) {
/*  86 */         File binFile = indicatorsProvider.getCustomIndicatorWrapperByName(name).getBinaryFile();
/*  87 */         if ((binFile != null) && (binFile.getName().equals(nodeBinFile.getName()))) {
/*  88 */           indNamesToAdd.remove(name);
/*  89 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     File nodeBinFile;
/*  94 */     for (String indName : indNamesToAdd) {
/*  95 */       CustIndicatorWrapper custIndWrapper = indicatorsProvider.getCustomIndicatorWrapperByName(indName);
/*  96 */       CustIndTreeNode custIndTreeNode = this.workspaceNodeFactory.createServiceTreeNodeFrom(custIndWrapper);
/*  97 */       this.workspaceJTree.addCustIndTreeNode(custIndTreeNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void indicatorChanged(Integer chartPanelId, IndicatorWrapper indicatorWrapper) {
/* 102 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 103 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(chartPanelId);
/* 104 */     IndicatorTreeNode indicatorTreeNode = null;
/* 105 */     for (int i = 0; i < chartTreeNode.getChildCount(); i++) {
/* 106 */       TreeNode chartTreeNodeChild = chartTreeNode.getChildAt(i);
/* 107 */       if (!(chartTreeNodeChild instanceof IndicatorTreeNode)) {
/*     */         continue;
/*     */       }
/* 110 */       IndicatorTreeNode treeNode = (IndicatorTreeNode)chartTreeNodeChild;
/* 111 */       if (treeNode.getIndicator().getId() == indicatorWrapper.getId()) {
/* 112 */         indicatorTreeNode = treeNode;
/*     */       }
/*     */     }
/*     */ 
/* 116 */     if (indicatorTreeNode != null) {
/* 117 */       this.workspaceJTree.getModel().nodeChanged(indicatorTreeNode);
/* 118 */       this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(indicatorTreeNode));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void indicatorRemoved(Integer chartPanelId, IndicatorWrapper indicatorWrapper) {
/* 123 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 124 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(chartPanelId);
/*     */ 
/* 126 */     for (int i = 0; i < chartTreeNode.getChildCount(); i++) {
/* 127 */       TreeNode chartTreeNodeChild = chartTreeNode.getChildAt(i);
/* 128 */       if (!(chartTreeNodeChild instanceof IndicatorTreeNode)) {
/*     */         continue;
/*     */       }
/* 131 */       IndicatorTreeNode treeNode = (IndicatorTreeNode)chartTreeNodeChild;
/* 132 */       if (treeNode.getIndicator().getId() != indicatorWrapper.getId())
/*     */         continue;
/* 134 */       this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(this.workspaceJTree.getPreviousNode(treeNode)));
/* 135 */       this.workspaceJTree.getModel().removeNodeFromParent(treeNode);
/* 136 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void openChartsForInstruments(Map<Instrument, TesterChartData> instrumentsAndProviders, String toolTipText)
/*     */   {
/* 142 */     Object[] params = { instrumentsAndProviders, toolTipText };
/* 143 */     this.treeActionFactory.createAction(TreeActionType.ADD_TESTER_CHART, this.workspaceJTree).execute(params);
/*     */   }
/*     */ 
/*     */   public void closeChart(int chartPanelId)
/*     */   {
/* 148 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 149 */     ChartsNode chartsNode = workspaceRootNode.getChartsNode();
/* 150 */     ChartTreeNode chartTreeNode = chartsNode.getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 151 */     this.workspaceJTree.getModel().removeNodeFromParent(chartTreeNode);
/*     */   }
/*     */ 
/*     */   public void drawingAdded(int chartPanelId, IChartObject chartObject) {
/* 155 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 156 */     ChartsNode chartsTreeNode = workspaceRootNode.getChartsNode();
/* 157 */     ChartTreeNode chartTreeNode = chartsTreeNode.getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 158 */     DrawingTreeNode drawingTreeNode = this.workspaceNodeFactory.createDrawingTreeNode(chartObject, chartTreeNode);
/* 159 */     this.workspaceJTree.addDrawingNode(drawingTreeNode);
/*     */   }
/*     */ 
/*     */   public void drawingChanged(int chartPanelId, IChartObject chartObject)
/*     */   {
/* 165 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 166 */     ChartsNode chartsTreeNode = workspaceRootNode.getChartsNode();
/* 167 */     ChartTreeNode chartTreeNode = chartsTreeNode.getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/*     */ 
/* 169 */     for (int i = 0; i < chartTreeNode.getChildCount(); i++) {
/* 170 */       TreeNode childTreeNode = chartTreeNode.getChildAt(i);
/*     */ 
/* 172 */       if ((childTreeNode instanceof DrawingTreeNode)) {
/* 173 */         DrawingTreeNode drawingTreeNode = (DrawingTreeNode)childTreeNode;
/*     */ 
/* 175 */         if (chartObject.equals(drawingTreeNode.getDrawing())) {
/* 176 */           this.workspaceJTree.getModel().nodeChanged(drawingTreeNode);
/* 177 */           this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(drawingTreeNode));
/*     */ 
/* 179 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(int chartPanelId, IChartObject chartObject) {
/* 186 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 187 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/*     */ 
/* 189 */     for (int i = 0; i < chartTreeNode.getChildCount(); i++) {
/* 190 */       TreeNode chartTreeNodeChild = chartTreeNode.getChildAt(i);
/* 191 */       if (!(chartTreeNodeChild instanceof DrawingTreeNode)) {
/*     */         continue;
/*     */       }
/* 194 */       DrawingTreeNode treeNode = (DrawingTreeNode)chartTreeNodeChild;
/* 195 */       if (treeNode.getDrawing().equals(chartObject)) {
/* 196 */         this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(this.workspaceJTree.getPreviousNode(treeNode)));
/* 197 */         this.workspaceJTree.getModel().removeNodeFromParent(treeNode);
/* 198 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void periodChanged(Integer chartPanelId, Period newPeriod) {
/* 204 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 205 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(chartPanelId);
/* 206 */     chartTreeNode.getJForexPeriod().setPeriod(newPeriod);
/* 207 */     this.workspaceJTree.getModel().nodeChanged(chartTreeNode);
/*     */   }
/*     */ 
/*     */   public void chartClosed(int chartPanelId) {
/* 211 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 212 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 213 */     if (chartTreeNode != null)
/* 214 */       this.workspaceJTree.getModel().removeNodeFromParent(chartTreeNode);
/*     */   }
/*     */ 
/*     */   public ServiceWrapper getServiceWrapperById(int panelId)
/*     */   {
/* 223 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 224 */     WorkspaceTreeNode serviceNode = workspaceRootNode.getServiceByPanelId(panelId);
/*     */ 
/* 226 */     if (serviceNode != null) {
/* 227 */       return ((AbstractServiceTreeNode)serviceNode).getServiceWrapper();
/*     */     }
/* 229 */     return null;
/*     */   }
/*     */ 
/*     */   public Map<Integer, StrategyWrapper> getStrategies()
/*     */   {
/* 234 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 235 */     List serviceTreeNodes = workspaceRootNode.getStrategiesTreeNode().getStrategyTreeNodes();
/* 236 */     Map strategies = new HashMap();
/* 237 */     for (StrategyTreeNode serviceTreeNode : serviceTreeNodes) {
/* 238 */       strategies.put(Integer.valueOf(serviceTreeNode.getId()), serviceTreeNode.getServiceWrapper());
/*     */     }
/* 240 */     return strategies;
/*     */   }
/*     */ 
/*     */   public void addStrategyListChangeListener(ActionListener listener)
/*     */   {
/* 245 */     this.workspaceJTree.addStrategyListChangeListener(listener);
/*     */   }
/*     */ 
/*     */   public void removeStrategyListChangeListener(ActionListener listener)
/*     */   {
/* 250 */     this.workspaceJTree.removeStrategyListChangeListener(listener);
/*     */   }
/*     */ 
/*     */   public void setSelectedInstrumentByPanelId(int panelId)
/*     */   {
/* 265 */     WorkspaceRootNode rootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 266 */     ChartsNode chartsNode = rootNode.getChartsNode();
/* 267 */     ChartTreeNode chartTreeNode = chartsNode.getChartTreeNodeByChartPanelId(Integer.valueOf(panelId));
/*     */ 
/* 269 */     if (chartTreeNode != null) {
/* 270 */       this.workspaceJTree.requestFocus();
/* 271 */       this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(chartTreeNode));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setWorkspaceTreePanel(WorkspaceTreePanel workspaceTreePanel)
/*     */   {
/* 320 */     this.workspaceTreePanel = workspaceTreePanel;
/*     */   }
/*     */ 
/*     */   public void setTreeActionFactory(TreeActionFactory treeActionFactory) {
/* 324 */     this.treeActionFactory = treeActionFactory;
/*     */   }
/*     */ 
/*     */   public void setWorkspaceJTree(WorkspaceJTree workspaceJTree) {
/* 328 */     this.workspaceJTree = workspaceJTree;
/*     */   }
/*     */ 
/*     */   public void setWorkspaceNodeFactory(WorkspaceNodeFactory workspaceNodeFactory) {
/* 332 */     this.workspaceNodeFactory = workspaceNodeFactory;
/*     */   }
/*     */ 
/*     */   public void setWorkspaceHelper(IWorkspaceHelper workspaceHelper) {
/* 336 */     this.workspaceHelper = workspaceHelper;
/*     */   }
/*     */ 
/*     */   public void setClientSettingsStorage(ClientSettingsStorage clientSettingsStorage) {
/* 340 */     this.clientSettingsStorage = clientSettingsStorage;
/*     */   }
/*     */ 
/*     */   private boolean selectInstrument(int chartPanelId)
/*     */   {
/* 349 */     WorkspaceRootNode rootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 350 */     ChartsNode chartsNode = rootNode.getChartsNode();
/* 351 */     ChartTreeNode chartNode = chartsNode.getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 352 */     if (chartNode == null) {
/* 353 */       return false;
/*     */     }
/* 355 */     TreePath selectedPath = this.workspaceJTree.getSelectionPath();
/* 356 */     if (selectedPath != null) {
/* 357 */       for (Object pathElement : selectedPath.getPath()) {
/* 358 */         if (pathElement.equals(chartNode)) {
/* 359 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 364 */     this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(chartNode));
/* 365 */     return true;
/*     */   }
/*     */ 
/*     */   private void selectStrategy(int strategyPanelId)
/*     */   {
/* 370 */     WorkspaceRootNode rootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 371 */     StrategiesNode strategiesNode = rootNode.getStrategiesTreeNode();
/*     */ 
/* 373 */     WorkspaceTreeNode node = findServiceTreeNodeFor(strategyPanelId, strategiesNode);
/* 374 */     if ((node != null) && ((node instanceof StrategyTreeNode))) {
/* 375 */       StrategyTreeNode strategyTreeNode = (StrategyTreeNode)node;
/* 376 */       this.workspaceJTree.setSelectionPath(this.workspaceJTree.getPath(strategyTreeNode));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void restoreExpandedStatus() {
/* 381 */     if (this.clientSettingsStorage.isChartsExpanded()) {
/* 382 */       this.treeActionFactory.createAction(TreeActionType.EXPAND_CHARTS, this.workspaceJTree).execute(null);
/*     */     }
/* 384 */     if (this.clientSettingsStorage.isStrategiesExpanded())
/* 385 */       this.treeActionFactory.createAction(TreeActionType.EXPAND_STRATEGIES, this.workspaceJTree).execute(null);
/*     */   }
/*     */ 
/*     */   void applyChanges(ServiceWrapper wrapper, File newFile)
/*     */   {
/* 429 */     wrapper.setSourceFile(newFile);
/* 430 */     wrapper.setBinaryFile(null);
/* 431 */     wrapper.setNewUnsaved(false);
/*     */   }
/*     */ 
/*     */   WorkspaceTreeNode findServiceTreeNodeFor(int editorPanelId, ServicesTreeNode servicesTreeNode) {
/* 435 */     int i = 0; for (int j = servicesTreeNode.getChildCount(); i < j; i++) {
/* 436 */       WorkspaceTreeNode strTreeNode = (WorkspaceTreeNode)servicesTreeNode.getChildAt(i);
/* 437 */       if ((strTreeNode != null) && ((strTreeNode instanceof AbstractServiceTreeNode))) {
/* 438 */         AbstractServiceTreeNode serviceTreeNod = (AbstractServiceTreeNode)strTreeNode;
/* 439 */         if (editorPanelId == serviceTreeNod.getId()) {
/* 440 */           return serviceTreeNod;
/*     */         }
/*     */       }
/*     */     }
/* 444 */     return null;
/*     */   }
/*     */ 
/*     */   public void instrumentChanged(int chartPanelId, Instrument instrument)
/*     */   {
/* 449 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 450 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 451 */     chartTreeNode.setInstrument(instrument);
/* 452 */     this.workspaceJTree.getModel().nodeChanged(chartTreeNode);
/* 453 */     this.workspaceHelper.refreshDealPanel(instrument);
/*     */   }
/*     */ 
/*     */   public void dataTypeChanged(int chartPanelId, DataType dataType)
/*     */   {
/* 458 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 459 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 460 */     chartTreeNode.getJForexPeriod().setDataType(dataType);
/* 461 */     this.workspaceJTree.getModel().nodeChanged(chartTreeNode);
/*     */   }
/*     */ 
/*     */   public void priceRangeChanged(int chartPanelId, PriceRange priceRange)
/*     */   {
/* 466 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 467 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 468 */     chartTreeNode.getJForexPeriod().setPriceRange(priceRange);
/* 469 */     this.workspaceJTree.getModel().nodeChanged(chartTreeNode);
/*     */   }
/*     */ 
/*     */   public void jForexPeriodChanged(int chartPanelId, JForexPeriod jForexPeriod)
/*     */   {
/* 474 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 475 */     ChartTreeNode chartTreeNode = workspaceRootNode.getChartsNode().getChartTreeNodeByChartPanelId(Integer.valueOf(chartPanelId));
/* 476 */     chartTreeNode.setJForexPeriod(jForexPeriod);
/* 477 */     this.workspaceJTree.getModel().nodeChanged(chartTreeNode);
/*     */   }
/*     */ 
/*     */   public void selectNode(int panelId) {
/* 481 */     selectInstrument(panelId);
/* 482 */     selectStrategy(panelId);
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> currencyNames)
/*     */   {
/* 490 */     restoreExpandedStatus();
/*     */   }
/*     */ 
/*     */   public void strategyAdded(StrategyNewBean strategyBean)
/*     */   {
/* 499 */     StrategyTreeNode node = this.workspaceNodeFactory.createStrategyTreeNodeFrom(strategyBean);
/* 500 */     this.workspaceJTree.addStrategyTreeNode(node);
/* 501 */     this.workspaceJTree.selectNode(node);
/*     */   }
/*     */ 
/*     */   public void strategyRemoved(StrategyNewBean strategyBean)
/*     */   {
/* 506 */     WorkspaceRootNode rootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 507 */     StrategiesNode strategiesNode = rootNode.getStrategiesTreeNode();
/*     */ 
/* 509 */     List strategiesTreeNodes = strategiesNode.getStrategyTreeNodes();
/*     */ 
/* 511 */     for (StrategyTreeNode treeNode : strategiesTreeNodes)
/* 512 */       if (treeNode.getStrategy().equals(strategyBean)) {
/* 513 */         this.workspaceJTree.removeServiceTreeNode(treeNode);
/* 514 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void deleteStrategyById(int id)
/*     */   {
/* 522 */     WorkspaceRootNode rootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 523 */     StrategiesNode strategiesNode = rootNode.getStrategiesTreeNode();
/*     */ 
/* 525 */     List strategiesTreeNodes = strategiesNode.getStrategyTreeNodes();
/*     */ 
/* 527 */     for (StrategyTreeNode treeNode : strategiesTreeNodes)
/* 528 */       if (treeNode.getStrategy().getId().intValue() == id) {
/* 529 */         this.workspaceJTree.removeServiceTreeNode(treeNode);
/* 530 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void renameServiceByName(int panelId, File newFile)
/*     */   {
/* 537 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 538 */     WorkspaceTreeNode service = workspaceRootNode.getServiceByPanelId(panelId);
/*     */ 
/* 540 */     if (!(service instanceof AbstractServiceTreeNode)) {
/* 541 */       return;
/*     */     }
/* 543 */     ServiceWrapper wrapper = ((AbstractServiceTreeNode)service).getServiceWrapper();
/*     */ 
/* 545 */     applyChanges(wrapper, newFile);
/* 546 */     this.workspaceJTree.getModel().nodeChanged(service);
/*     */ 
/* 548 */     this.workspaceJTree.revalidate();
/* 549 */     this.workspaceJTree.repaint();
/*     */   }
/*     */ 
/*     */   public void strategyUpdated(int strategyId)
/*     */   {
/* 555 */     StrategiesNode node = this.workspaceJTree.getWorkspaceRoot().getStrategiesTreeNode();
/* 556 */     this.workspaceJTree.getModel().nodeChanged(node.getServiceByPanelId(strategyId));
/* 557 */     this.workspaceJTree.revalidate();
/* 558 */     this.workspaceJTree.repaint();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeControllerImpl
 * JD-Core Version:    0.6.0
 */