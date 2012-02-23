/*     */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*     */ 
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTreePopupFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrencyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class WorkspaceJTreeMouseListener extends MouseAdapter
/*     */ {
/*     */   private IChartTabsAndFramesController iChartTabsAndFramesController;
/*     */   private DDSChartsController ddsChartsController;
/*     */   private WorkspaceJTree workspaceJTree;
/*     */   private WorkspaceJTreePopupFactory workspaceJTreePopupFactory;
/*     */   private IWorkspaceHelper workspaceHelper;
/*     */   private TreeActionFactory treeActionFactory;
/*     */ 
/*     */   public WorkspaceJTreeMouseListener(IChartTabsAndFramesController iChartTabsAndFramesController, DDSChartsController ddsChartsController, WorkspaceJTree workspaceJTree, WorkspaceJTreePopupFactory workspaceJTreePopupFactory, IWorkspaceHelper workspaceHelper, TreeActionFactory treeActionFactory)
/*     */   {
/*  40 */     this.iChartTabsAndFramesController = iChartTabsAndFramesController;
/*  41 */     this.ddsChartsController = ddsChartsController;
/*  42 */     this.workspaceJTree = workspaceJTree;
/*  43 */     this.workspaceJTreePopupFactory = workspaceJTreePopupFactory;
/*  44 */     this.workspaceHelper = workspaceHelper;
/*  45 */     this.treeActionFactory = treeActionFactory;
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent event)
/*     */   {
/*  53 */     processMouseClickedAction(event);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent event) {
/*  57 */     processMousePressedReleasedAction(event);
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent event) {
/*  61 */     processMousePressedReleasedAction(event);
/*     */   }
/*     */ 
/*     */   private void processMouseClickedAction(MouseEvent event)
/*     */   {
/*  68 */     if (event.getClickCount() < 2) {
/*  69 */       return;
/*     */     }
/*  71 */     WorkspaceJTree workspaceJTree = (WorkspaceJTree)event.getSource();
/*  72 */     TreePath closestPathForLocation = workspaceJTree.getClosestPathForLocation(event.getX(), event.getY());
/*  73 */     WorkspaceTreeNode workspaceTreeNode = (WorkspaceTreeNode)closestPathForLocation.getLastPathComponent();
/*     */ 
/*  75 */     if ((workspaceTreeNode instanceof AbstractServiceTreeNode)) {
/*  76 */       AbstractServiceTreeNode serviceTreeNode = (AbstractServiceTreeNode)workspaceTreeNode;
/*  77 */       ServiceWrapper serviceWrapper = serviceTreeNode.getServiceWrapper();
/*  78 */       boolean editable = serviceWrapper.isEditable();
/*  79 */       serviceTreeNode.setEditable(editable);
/*  80 */       if (!editable) {
/*  81 */         return;
/*     */       }
/*  83 */       if (!this.iChartTabsAndFramesController.selectServiceSourceEditor(serviceTreeNode.getId()))
/*     */       {
/*  85 */         this.iChartTabsAndFramesController.addServiceSourceEditor(serviceTreeNode.getId(), serviceWrapper.getName(), serviceWrapper.getSourceFile(), serviceTreeNode.getServiceSourceType(), false, serviceWrapper.isNewUnsaved());
/*     */       }
/*     */ 
/*     */     }
/*  92 */     else if ((workspaceTreeNode instanceof CurrencyTreeNode)) {
/*  93 */       CurrencyTreeNode currencyTreeNode = (CurrencyTreeNode)workspaceTreeNode;
/*  94 */       this.treeActionFactory.createAction(TreeActionType.ADD_CHART, workspaceJTree).execute(new Object[] { workspaceTreeNode, currencyTreeNode.getInstrument() });
/*  95 */     } else if ((workspaceTreeNode instanceof DrawingTreeNode)) {
/*  96 */       DrawingTreeNode drawingTreeNode = (DrawingTreeNode)workspaceTreeNode;
/*  97 */       ChartTreeNode chartTreeNode = (ChartTreeNode)drawingTreeNode.getParent();
/*  98 */       this.ddsChartsController.navigateToDrawing(Integer.valueOf(chartTreeNode.getChartPanelId()), drawingTreeNode.getDrawing());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processMousePressedReleasedAction(MouseEvent event) {
/* 103 */     if (event.isPopupTrigger()) {
/* 104 */       showPopup(event);
/* 105 */       return;
/*     */     }
/* 107 */     this.workspaceHelper.selectTabForSelectedNode(this.workspaceJTree);
/*     */   }
/*     */ 
/*     */   private boolean showPopup(MouseEvent event) {
/* 111 */     WorkspaceJTree workspaceJTree = (WorkspaceJTree)event.getSource();
/*     */ 
/* 114 */     TreePath closestPathForLocation = workspaceJTree.getClosestPathForLocation(event.getX(), event.getY());
/*     */ 
/* 117 */     if (workspaceJTree.getSelectionCount() == 1) {
/* 118 */       workspaceJTree.setSelectionPath(closestPathForLocation);
/*     */     }
/*     */ 
/* 121 */     WorkspaceTreeNode workspaceTreeNode = closestPathForLocation == null ? null : (WorkspaceTreeNode)closestPathForLocation.getLastPathComponent();
/* 122 */     JPopupMenu popupMenu = this.workspaceJTreePopupFactory.getPopupMenuFor(event, workspaceTreeNode);
/* 123 */     if (popupMenu == null) {
/* 124 */       return false;
/*     */     }
/*     */ 
/* 127 */     popupMenu.show(workspaceJTree, event.getX(), event.getY());
/*     */ 
/* 129 */     workspaceJTree.requestFocus();
/*     */ 
/* 131 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeMouseListener
 * JD-Core Version:    0.6.0
 */