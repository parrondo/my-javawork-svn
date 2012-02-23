/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategiesNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyProcessDescriptor;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.MutableTreeNode;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class WorkspaceJTree extends JTree
/*     */   implements Localizable
/*     */ {
/*  40 */   private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceJTree.class);
/*     */   private IWorkspaceHelper workspaceHelper;
/*  43 */   private final List<ActionListener> strategyChangeListeners = new LinkedList();
/*     */ 
/*  74 */   private int initialInstrumentsLoaded = 2;
/*     */ 
/*     */   public WorkspaceJTree()
/*     */   {
/*  47 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/*  52 */     updateUI();
/*     */   }
/*     */ 
/*     */   public void build() {
/*  56 */     setSelectionModel(new WorkspaceJTreeSelectionModel());
/*  57 */     setCellRenderer(new WorkspaceTreeCellRenderer());
/*     */ 
/*  59 */     setEditable(false);
/*  60 */     setRootVisible(false);
/*  61 */     setShowsRootHandles(true);
/*  62 */     setExpandsSelectedPaths(true);
/*  63 */     setBackground(GreedContext.GLOBAL_BACKGROUND);
/*     */   }
/*     */ 
/*     */   public void populateWorkspaceAndSubscribeToCurrenciesForProfitLossCalculation()
/*     */   {
/*  77 */     this.workspaceHelper.findAndsubscribeToCurrenciesForProfitLossCalculation();
/*  78 */     this.workspaceHelper.loadDataIntoWorkspace(this);
/*  79 */     this.initialInstrumentsLoaded = 1;
/*     */   }
/*     */ 
/*     */   void checkDependantCurrenciesAndAddThemIfNecessary(String orderCurrency) {
/*  83 */     if (this.initialInstrumentsLoaded != 0)
/*     */     {
/*  87 */       return;
/*     */     }
/*  89 */     this.workspaceHelper.checkDependantCurrenciesAndAddThemIfNecessary(this, orderCurrency);
/*     */   }
/*     */ 
/*     */   private void addNodeInSortedOrder(MutableTreeNode parent, MutableTreeNode child)
/*     */   {
/*  96 */     int n = parent.getChildCount();
/*  97 */     if (n == 0) {
/*  98 */       getModel().insertNodeInto(child, parent, parent.getChildCount());
/*  99 */       return;
/*     */     }
/* 101 */     AbstractServiceTreeNode node = null;
/* 102 */     for (int i = 0; i < n; i++) {
/* 103 */       node = (AbstractServiceTreeNode)parent.getChildAt(i);
/* 104 */       if (node.compareTo(child) > 0) {
/* 105 */         getModel().insertNodeInto(child, parent, i);
/* 106 */         return;
/*     */       }
/*     */     }
/* 109 */     getModel().insertNodeInto(child, parent, parent.getChildCount());
/*     */   }
/*     */ 
/*     */   public Instrument getCurrentInstrument()
/*     */   {
/* 114 */     TreePath selectionPath = getSelectionPath();
/* 115 */     Instrument prevSelected = null;
/*     */     try
/*     */     {
/* 118 */       String instr = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getOrderEntryPanel().getInstrument();
/* 119 */       prevSelected = Instrument.fromString(instr);
/*     */     } catch (Exception e) {
/* 121 */       LOGGER.error("Default instrument detecting problem: " + e.getMessage());
/* 122 */       return Instrument.EURUSD;
/*     */     }
/*     */ 
/* 125 */     if (selectionPath == null) {
/* 126 */       return prevSelected;
/*     */     }
/* 128 */     Object[] path = selectionPath.getPath();
/* 129 */     if (path.length < 2) {
/* 130 */       return prevSelected;
/*     */     }
/*     */ 
/* 133 */     if (!(path[1] instanceof ChartTreeNode)) {
/* 134 */       return prevSelected;
/*     */     }
/*     */ 
/* 137 */     ChartTreeNode chartTreeNode = (ChartTreeNode)path[1];
/* 138 */     return chartTreeNode.getInstrument();
/*     */   }
/*     */ 
/*     */   public DefaultTreeModel getModel() {
/* 142 */     return (DefaultTreeModel)super.getModel();
/*     */   }
/*     */ 
/*     */   public void setWorkspaceHelper(IWorkspaceHelper workspaceHelper) {
/* 146 */     this.workspaceHelper = workspaceHelper;
/*     */   }
/*     */ 
/*     */   public void addStrategyListChangeListener(ActionListener listener) {
/* 150 */     if (!this.strategyChangeListeners.contains(listener))
/* 151 */       this.strategyChangeListeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void removeStrategyListChangeListener(ActionListener listener)
/*     */   {
/* 156 */     this.strategyChangeListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public void fireStrategyListChanged() {
/* 160 */     for (ActionListener listener : this.strategyChangeListeners)
/* 161 */       listener.actionPerformed(null);
/*     */   }
/*     */ 
/*     */   public String getToolTipText(MouseEvent event)
/*     */   {
/* 166 */     if (getRowForLocation(event.getX(), event.getY()) == -1) {
/* 167 */       return null;
/*     */     }
/* 169 */     TreePath curPath = getPathForLocation(event.getX(), event.getY());
/* 170 */     WorkspaceTreeNode workspaceTreeNode = (WorkspaceTreeNode)curPath.getLastPathComponent();
/* 171 */     return workspaceTreeNode.getToolTipText();
/*     */   }
/*     */ 
/*     */   public WorkspaceRootNode getWorkspaceRoot() {
/* 175 */     return (WorkspaceRootNode)getModel().getRoot();
/*     */   }
/*     */ 
/*     */   public int addChartNode(ChartTreeNode loadedChart) {
/* 179 */     ChartsNode chartsTreeNode = getWorkspaceRoot().getChartsNode();
/* 180 */     int chartNodeIdx = chartsTreeNode.addInstrumentChartTreeNode(loadedChart);
/* 181 */     getModel().nodesWereInserted(chartsTreeNode, new int[] { chartNodeIdx });
/* 182 */     return chartNodeIdx;
/*     */   }
/*     */ 
/*     */   public void removeInstrumentNode(int index, ChartTreeNode loadedChartNode) {
/* 186 */     ChartsNode chartsTreeNode = getWorkspaceRoot().getChartsNode();
/* 187 */     chartsTreeNode.remove(index);
/* 188 */     getModel().nodesWereRemoved(chartsTreeNode, new int[] { index }, new Object[] { loadedChartNode });
/*     */   }
/*     */ 
/*     */   public void addIndicatorNode(IndicatorTreeNode indicatorTreeNode) {
/* 192 */     WorkspaceTreeNode chartTreeNode = (WorkspaceTreeNode)indicatorTreeNode.getParent();
/* 193 */     getModel().insertNodeInto(indicatorTreeNode, chartTreeNode, getIndexToInsert(chartTreeNode, IndicatorTreeNode.class));
/*     */   }
/*     */ 
/*     */   public void addStrategyTreeNode(StrategyTreeNode strategyTreeNode) {
/* 197 */     StrategiesNode servicesTreeNode = getWorkspaceRoot().getStrategiesTreeNode();
/* 198 */     addNodeInSortedOrder(servicesTreeNode, strategyTreeNode);
/*     */ 
/* 200 */     fireStrategyListChanged();
/*     */   }
/*     */ 
/*     */   public boolean closeUnsavedStrategies()
/*     */   {
/* 208 */     return true;
/*     */   }
/*     */ 
/*     */   public void removeServiceTreeNode(AbstractServiceTreeNode serviceNode) {
/* 212 */     getModel().removeNodeFromParent(serviceNode);
/* 213 */     fireStrategyListChanged();
/*     */   }
/*     */ 
/*     */   public void changeSelectedNode(WorkspaceTreeNode nodeToBeDeleted) {
/* 217 */     WorkspaceTreeNode newSelectionNode = getPreviousNode(nodeToBeDeleted);
/* 218 */     if (newSelectionNode.getParent() != nodeToBeDeleted.getParent())
/*     */     {
/* 222 */       newSelectionNode = getNextNode(nodeToBeDeleted);
/*     */     }
/* 224 */     TreePath path = getPath(newSelectionNode);
/* 225 */     setSelectionPath(path);
/*     */   }
/*     */ 
/*     */   public void addDrawingNode(DrawingTreeNode drawingTreeNode)
/*     */   {
/* 230 */     WorkspaceTreeNode chartTreeNode = (WorkspaceTreeNode)drawingTreeNode.getParent();
/* 231 */     getModel().insertNodeInto(drawingTreeNode, chartTreeNode, getIndexToInsert(chartTreeNode, DrawingTreeNode.class));
/*     */   }
/*     */ 
/*     */   public void addCustIndTreeNode(CustIndTreeNode custIndTreeNode) {
/* 235 */     IndicatorsNode indicatorsTreeNode = getWorkspaceRoot().getIndicatorsTreeNode();
/* 236 */     addNodeInSortedOrder(indicatorsTreeNode, custIndTreeNode);
/*     */   }
/*     */ 
/*     */   private static int getIndexToInsert(WorkspaceTreeNode treeNode, Class<? extends WorkspaceTreeNode> childClass) {
/* 240 */     if (childClass.equals(DrawingTreeNode.class)) {
/* 241 */       return treeNode.getChildCount();
/*     */     }
/*     */ 
/* 244 */     if (treeNode.getChildCount() == 0) {
/* 245 */       return 0;
/*     */     }
/*     */ 
/* 248 */     return getLastChildIndex(treeNode, childClass, -1) + 1;
/*     */   }
/*     */ 
/*     */   private static int getLastChildIndex(WorkspaceTreeNode treeNode, Class<? extends WorkspaceTreeNode> childClass, int initialIndex) {
/* 252 */     int lastChildIndex = initialIndex;
/* 253 */     int childCount = treeNode.getChildCount();
/* 254 */     for (int i = 0; i < childCount; i++) {
/* 255 */       TreeNode childNode = treeNode.getChildAt(i);
/*     */ 
/* 257 */       if (childNode.getClass().equals(childClass)) {
/* 258 */         lastChildIndex = i;
/*     */       }
/*     */     }
/*     */ 
/* 262 */     return lastChildIndex;
/*     */   }
/*     */ 
/*     */   public void selectNode(WorkspaceTreeNode workspaceTreeNode) {
/* 266 */     requestFocus();
/* 267 */     TreePath path = getPath(workspaceTreeNode);
/* 268 */     expandPath(path);
/* 269 */     scrollPathToVisible(path);
/* 270 */     setSelectionRow(getRowForPath(path));
/*     */   }
/*     */ 
/*     */   public void selectPreviousNode(WorkspaceTreeNode workspaceTreeNode) {
/* 274 */     selectNode(getPreviousNode(workspaceTreeNode));
/*     */   }
/*     */ 
/*     */   public TreePath getPath(WorkspaceTreeNode workspaceTreeNode) {
/* 278 */     List nodes = new ArrayList();
/*     */ 
/* 280 */     WorkspaceTreeNode node = workspaceTreeNode;
/*     */     while (true) {
/* 282 */       nodes.add(0, node);
/*     */ 
/* 284 */       if (node.equals(getModel().getRoot()))
/*     */       {
/*     */         break;
/*     */       }
/* 288 */       node = (WorkspaceTreeNode)node.getParent();
/*     */     }
/*     */ 
/* 291 */     return new TreePath(nodes.toArray(new WorkspaceTreeNode[0]));
/*     */   }
/*     */ 
/*     */   public WorkspaceTreeNode getPreviousNode(WorkspaceTreeNode workspaceTreeNode) {
/* 295 */     WorkspaceTreeNode parentNode = (WorkspaceTreeNode)workspaceTreeNode.getParent();
/*     */ 
/* 297 */     int childrenCount = parentNode.getChildCount();
/* 298 */     if (childrenCount > 1) {
/* 299 */       int nodeIndex = parentNode.getIndex(workspaceTreeNode);
/* 300 */       int nearestNodeIndex = this.workspaceHelper.calculatePreviousNodeIndxToBeFocused(nodeIndex);
/*     */ 
/* 302 */       if (nearestNodeIndex >= 0) {
/* 303 */         return (WorkspaceTreeNode)parentNode.getChildAt(nearestNodeIndex);
/*     */       }
/*     */     }
/*     */ 
/* 307 */     return parentNode;
/*     */   }
/*     */ 
/*     */   public WorkspaceTreeNode getNextNode(WorkspaceTreeNode node) {
/* 311 */     WorkspaceTreeNode parentNode = (WorkspaceTreeNode)node.getParent();
/*     */ 
/* 313 */     int childrenCount = parentNode.getChildCount();
/* 314 */     if (childrenCount > 1) {
/* 315 */       int nodeIndex = parentNode.getIndex(node);
/* 316 */       int nearestNodeIndex = this.workspaceHelper.calculateNextNodeIndexToBeFocused(nodeIndex);
/*     */ 
/* 318 */       if (nearestNodeIndex >= 0) {
/* 319 */         return (WorkspaceTreeNode)parentNode.getChildAt(nearestNodeIndex);
/*     */       }
/*     */     }
/*     */ 
/* 323 */     return parentNode;
/*     */   }
/*     */ 
/*     */   public void updateRemoteStrategy(StrategyTreeNode strategyTreeNode, StrategyProcessDescriptor descriptor, boolean initializing, ClientSettingsStorage storage)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void strategyNodeUpdated(StrategyTreeNode node, ClientSettingsStorage storage)
/*     */   {
/* 334 */     getModel().nodeChanged(node);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree
 * JD-Core Version:    0.6.0
 */