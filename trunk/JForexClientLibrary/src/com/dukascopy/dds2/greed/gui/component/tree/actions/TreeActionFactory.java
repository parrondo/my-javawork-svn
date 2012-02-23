/*     */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*     */ 
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TreeActionFactory
/*     */ {
/*  19 */   private static final Logger LOGGER = LoggerFactory.getLogger(TreeActionFactory.class);
/*     */   final WorkspaceTreePanel workspaceTreePanel;
/*     */   final TabsAndFramesTabbedPane tabsAndFramesTabbedPane;
/*     */   final OrdersPanel ordersPanel;
/*     */   final PositionsPanel positionsPanel;
/*     */   final WorkspaceTreeController workspaceTreeController;
/*     */   final DDSChartsController ddsChartsController;
/*     */   final IChartTabsAndFramesController chartTabsAndFramesController;
/*     */   final IWorkspaceHelper workspaceHelper;
/*     */   final WorkspaceNodeFactory workspaceNodeFactory;
/*     */   final ClientSettingsStorage clientSettingsStorage;
/*     */ 
/*     */   public TreeActionFactory(WorkspaceTreePanel workspaceTreePanel, TabsAndFramesTabbedPane tabsAndFramesTabbedPane, OrdersPanel ordersPanel, PositionsPanel positionsPanel, WorkspaceTreeController workspaceTreeController, DDSChartsController ddsChartsController, IChartTabsAndFramesController chartTabsAndFramesController, IWorkspaceHelper workspaceHelper, WorkspaceNodeFactory workspaceNodeFactory, ClientSettingsStorage clientSettingsStorage)
/*     */   {
/*  47 */     this.workspaceTreePanel = workspaceTreePanel;
/*  48 */     this.tabsAndFramesTabbedPane = tabsAndFramesTabbedPane;
/*  49 */     this.ordersPanel = ordersPanel;
/*  50 */     this.positionsPanel = positionsPanel;
/*  51 */     this.workspaceTreeController = workspaceTreeController;
/*  52 */     this.ddsChartsController = ddsChartsController;
/*  53 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*  54 */     this.workspaceHelper = workspaceHelper;
/*  55 */     this.workspaceNodeFactory = workspaceNodeFactory;
/*  56 */     this.clientSettingsStorage = clientSettingsStorage;
/*     */   }
/*     */ 
/*     */   public ITreeAction createAction(String actionType, WorkspaceJTree workspaceJTree) {
/*  60 */     return createAction(TreeActionType.valueOf(actionType), workspaceJTree);
/*     */   }
/*     */ 
/*     */   public ITreeAction createAction(TreeActionType treeActionType, WorkspaceJTree workspaceJTree)
/*     */   {
/*  68 */     ITreeAction treeAction = new NullTreeAction(null);
/*     */ 
/*  70 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$tree$actions$TreeActionType[treeActionType.ordinal()]) {
/*     */     case 1:
/*  72 */       treeAction = new CollapseIndicatorsAction(workspaceJTree);
/*  73 */       break;
/*     */     case 2:
/*  75 */       treeAction = new ExpandIndicatorsAction(workspaceJTree);
/*  76 */       break;
/*     */     case 3:
/*  78 */       treeAction = new CollapseStrategiesAction(workspaceJTree);
/*  79 */       break;
/*     */     case 4:
/*  81 */       treeAction = new ExpandStrategiesAction(workspaceJTree);
/*  82 */       break;
/*     */     case 5:
/*  84 */       treeAction = new CollapseChartsAction(workspaceJTree);
/*  85 */       break;
/*     */     case 6:
/*  87 */       treeAction = new ExpandChartsAction(workspaceJTree);
/*  88 */       break;
/*     */     case 7:
/*  90 */       treeAction = new AddChartTreeAction(this.chartTabsAndFramesController, workspaceJTree, this.workspaceNodeFactory, this.workspaceHelper, this.clientSettingsStorage);
/*  91 */       break;
/*     */     case 8:
/*  93 */       treeAction = new AddTesterChartTreeAction(this.chartTabsAndFramesController, workspaceJTree, this.workspaceNodeFactory, this.workspaceHelper, this.clientSettingsStorage);
/*  94 */       break;
/*     */     case 9:
/*  96 */       treeAction = new AddIndicatorAction(workspaceJTree, this.ddsChartsController);
/*  97 */       break;
/*     */     case 10:
/*  99 */       treeAction = new EditIndicatorAction(workspaceJTree, this.ddsChartsController);
/* 100 */       break;
/*     */     case 11:
/* 102 */       treeAction = new OpenStrategyAction(workspaceJTree, this.workspaceNodeFactory, this.chartTabsAndFramesController, this.clientSettingsStorage);
/* 103 */       break;
/*     */     case 12:
/* 105 */       treeAction = new AddStrategyAction(workspaceJTree, this.workspaceNodeFactory, this.chartTabsAndFramesController, this.clientSettingsStorage);
/* 106 */       break;
/*     */     case 13:
/* 108 */       treeAction = new OpenCustIndAction(workspaceJTree, this.workspaceNodeFactory, this.chartTabsAndFramesController, this.clientSettingsStorage);
/* 109 */       break;
/*     */     case 14:
/* 111 */       treeAction = new AddCustomIndicatorAction(workspaceJTree, this.workspaceNodeFactory, this.chartTabsAndFramesController);
/* 112 */       break;
/*     */     case 15:
/* 114 */       treeAction = new AddCustomIndicatorToChartAction(workspaceJTree, this.ddsChartsController);
/* 115 */       break;
/*     */     case 16:
/* 126 */       treeAction = new StopAllTasksAction(workspaceJTree);
/* 127 */       break;
/*     */     case 17:
/* 132 */       treeAction = new EditStrategyAction(workspaceJTree, this.chartTabsAndFramesController, this.workspaceNodeFactory);
/* 133 */       break;
/*     */     case 18:
/* 135 */       treeAction = new EditCustomIndicatorAction(workspaceJTree, this.chartTabsAndFramesController, this.workspaceNodeFactory);
/* 136 */       break;
/*     */     case 19:
/* 138 */       treeAction = new DeleteSelectedNodeAction(workspaceJTree, this.workspaceHelper, this.ddsChartsController, this.chartTabsAndFramesController, this.clientSettingsStorage);
/* 139 */       break;
/*     */     case 20:
/* 141 */       treeAction = new ClearAllAction(workspaceJTree, this.workspaceHelper, this.ddsChartsController, this.chartTabsAndFramesController, this.clientSettingsStorage);
/* 142 */       break;
/*     */     case 21:
/* 144 */       treeAction = new TestTaskAction(workspaceJTree);
/* 145 */       break;
/*     */     case 22:
/* 147 */       treeAction = new CompileServiceAction(workspaceJTree, this.chartTabsAndFramesController);
/* 148 */       break;
/*     */     case 23:
/* 150 */       treeAction = new ClearChartAction(workspaceJTree, this.ddsChartsController);
/* 151 */       break;
/*     */     case 24:
/* 153 */       treeAction = new AddChartTemplateTreeAction(this.chartTabsAndFramesController, workspaceJTree, this.workspaceNodeFactory, this.workspaceHelper, this.clientSettingsStorage);
/* 154 */       break;
/*     */     case 25:
/* 156 */       treeAction = new AddChartTemplateTreeAction(this.chartTabsAndFramesController, workspaceJTree, this.workspaceNodeFactory, this.workspaceHelper, this.clientSettingsStorage);
/* 157 */       break;
/*     */     case 26:
/* 159 */       treeAction = new SaveAsAction(workspaceJTree);
/* 160 */       break;
/*     */     case 27:
/* 162 */       treeAction = new OpenStrategiesControlPanelAction(workspaceJTree);
/* 163 */       break;
/*     */     default:
/* 165 */       LOGGER.warn("Unsupported action : {}", treeActionType);
/*     */     }
/*     */ 
/* 168 */     return treeAction;
/*     */   }
/*     */   private class NullTreeAction implements ITreeAction {
/*     */     private NullTreeAction() {
/*     */     }
/* 173 */     public Object execute(Object param) { TreeActionFactory.LOGGER.error("Tree action not found");
/* 174 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory
 * JD-Core Version:    0.6.0
 */