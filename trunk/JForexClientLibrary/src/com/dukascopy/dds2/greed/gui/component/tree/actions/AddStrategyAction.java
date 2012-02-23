/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.charts.persistence.IdManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.FileChooserDialogHelper;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ 
/*    */ public class AddStrategyAction extends StrategyAction
/*    */ {
/*    */   WorkspaceNodeFactory workspaceNodeFactory;
/*    */   ClientSettingsStorage clientSettingsStorage;
/*    */ 
/*    */   public AddStrategyAction(WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IChartTabsAndFramesController chartTabsAndFramesController, ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 26 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory);
/* 27 */     this.workspaceNodeFactory = workspaceNodeFactory;
/* 28 */     this.clientSettingsStorage = clientSettingsStorage;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 32 */     new Thread(param)
/*    */     {
/*    */       public void run() {
/* 35 */         AddStrategyAction.this.createNew(this.val$param != null);
/*    */       }
/*    */     }
/* 32 */     .start();
/*    */ 
/* 39 */     return null;
/*    */   }
/*    */ 
/*    */   private void createNew(boolean isEmptyFile)
/*    */   {
/* 44 */     StrategyWrapper strategyWrapper = new StrategyWrapper();
/* 45 */     strategyWrapper.setSourceFile(isEmptyFile ? FileChooserDialogHelper.createEmptyFile("java") : FileChooserDialogHelper.findOrCreateFileFor("str"));
/*    */ 
/* 48 */     strategyWrapper.setNewUnsaved(true);
/*    */ 
/* 50 */     StrategyNewBean strategyBean = new StrategyNewBean();
/* 51 */     strategyBean.setId(Integer.valueOf(IdManager.getInstance().getNextServiceId()));
/* 52 */     strategyBean.resetDates();
/* 53 */     strategyBean.setType(StrategyType.LOCAL);
/* 54 */     strategyBean.setStatus(StrategyStatus.STOPPED);
/* 55 */     strategyBean.setName(strategyWrapper.getName());
/*    */ 
/* 57 */     this.chartTabsAndFramesController.addServiceSourceEditor(strategyBean.getId().intValue(), strategyWrapper.getName(), strategyWrapper.getSourceFile(), ServiceSourceType.STRATEGY, false);
/*    */ 
/* 65 */     StrategyTreeNode strategyTreeNode = this.workspaceNodeFactory.createStrategyTreeNodeFrom(strategyWrapper, strategyBean);
/*    */ 
/* 68 */     this.workspaceJTree.addStrategyTreeNode(strategyTreeNode);
/* 69 */     this.workspaceJTree.selectNode(strategyTreeNode);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.AddStrategyAction
 * JD-Core Version:    0.6.0
 */