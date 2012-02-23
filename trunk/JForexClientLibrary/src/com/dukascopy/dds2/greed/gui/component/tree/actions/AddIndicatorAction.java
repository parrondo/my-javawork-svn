/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*    */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*    */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.tree.DefaultTreeModel;
/*    */ import javax.swing.tree.TreePath;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class AddIndicatorAction extends TreeAction
/*    */ {
/* 23 */   private static final Logger LOGGER = LoggerFactory.getLogger(AddIndicatorAction.class);
/*    */   DDSChartsController ddsChartsController;
/*    */ 
/*    */   AddIndicatorAction(WorkspaceJTree workspaceJTree, DDSChartsController ddsChartsController)
/*    */   {
/* 28 */     super(workspaceJTree);
/* 29 */     this.ddsChartsController = ddsChartsController;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 33 */     Object[] params = (Object[])(Object[])param;
/* 34 */     ChartTreeNode chartTreeNode = (ChartTreeNode)params[0];
/* 35 */     int chartPanelId = chartTreeNode.getChartPanelId();
/*    */ 
/* 37 */     List indicatorWrappers = new ArrayList();
/* 38 */     if ((params[1] != null) && ((params[1] instanceof IndicatorWrapper))) {
/* 39 */       indicatorWrappers.add((IndicatorWrapper)params[1]);
/*    */     }
/*    */ 
/* 42 */     if (indicatorWrappers.isEmpty())
/* 43 */       indicatorWrappers = this.ddsChartsController.createAddEditIndicatorsDialog(chartPanelId);
/*    */     else
/* 45 */       this.ddsChartsController.addIndicators(Integer.valueOf(chartPanelId), indicatorWrappers);
/*    */     ClientSettingsStorage clientSettingsStorage;
/* 48 */     if ((indicatorWrappers != null) && (!indicatorWrappers.isEmpty())) {
/* 49 */       clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*    */ 
/* 51 */       for (IndicatorWrapper iw : indicatorWrappers) {
/* 52 */         LastUsedIndicatorBean indicatorBean = DDSChartsActionAdapter.convertToLastUsedIndicatorBean(iw);
/* 53 */         clientSettingsStorage.addLastUsedIndicatorName(indicatorBean);
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 59 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 60 */     TreePath path = new TreePath(new WorkspaceTreeNode[] { workspaceRootNode, chartTreeNode });
/* 61 */     this.workspaceJTree.expandPath(path);
/*    */ 
/* 63 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.AddIndicatorAction
 * JD-Core Version:    0.6.0
 */