/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.Strategies;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.Action;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.listeners.ChangeInsturmentActionListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.listeners.ChangePeriodActionListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.listeners.ChartsMenuListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorsNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategiesNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ChartTemplateSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.IChartTemplateSettingsStorage;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.util.List;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.event.TreeExpansionEvent;
/*     */ import javax.swing.event.TreeWillExpandListener;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.ExpandVetoException;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class WorkspaceJTreePopupFactory
/*     */   implements TreeWillExpandListener
/*     */ {
/*  89 */   private static final Logger LOGGER = LoggerFactory.getLogger(Strategies.class);
/*     */ 
/*  93 */   private boolean chartsExpanded = true;
/*  94 */   private boolean strategiesExpanded = false;
/*  95 */   private boolean indicatorsExpanded = true;
/*     */   final WorkspaceJTree workspaceJTree;
/*     */   final IChartTabsAndFramesController chartTabsAndFramesController;
/*     */   final IWorkspaceHelper workspaceHelper;
/*     */   final WorkspaceNodeFactory workspaceNodeFactory;
/*     */   final TreeActionFactory treeActionFactory;
/*     */   final ClientSettingsStorage clientSettingsStorage;
/*     */ 
/*     */   public void treeWillExpand(TreeExpansionEvent event)
/*     */     throws ExpandVetoException
/*     */   {
/*  98 */     Object lastPathComponent = event.getPath().getLastPathComponent();
/*     */ 
/* 100 */     if ((lastPathComponent instanceof ChartsNode)) {
/* 101 */       this.chartsExpanded = true;
/* 102 */       return;
/*     */     }
/*     */ 
/* 105 */     if ((lastPathComponent instanceof StrategiesNode)) {
/* 106 */       this.strategiesExpanded = true;
/* 107 */       return;
/*     */     }
/*     */ 
/* 110 */     if ((lastPathComponent instanceof IndicatorsNode)) {
/* 111 */       this.indicatorsExpanded = true;
/* 112 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
/*     */   {
/* 118 */     Object lastPathComponent = event.getPath().getLastPathComponent();
/*     */ 
/* 120 */     if ((lastPathComponent instanceof ChartsNode)) {
/* 121 */       this.chartsExpanded = false;
/* 122 */       return;
/*     */     }
/*     */ 
/* 125 */     if ((lastPathComponent instanceof StrategiesNode)) {
/* 126 */       this.strategiesExpanded = false;
/* 127 */       return;
/*     */     }
/*     */ 
/* 130 */     if ((lastPathComponent instanceof IndicatorsNode)) {
/* 131 */       this.indicatorsExpanded = false;
/* 132 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public WorkspaceJTreePopupFactory(WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IChartTabsAndFramesController chartTabsAndFramesController, TreeActionFactory treeActionFactory, IWorkspaceHelper workspaceHelper, ClientSettingsStorage clientSettingsStorage)
/*     */   {
/* 154 */     this.workspaceJTree = workspaceJTree;
/* 155 */     this.workspaceNodeFactory = workspaceNodeFactory;
/* 156 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/* 157 */     this.treeActionFactory = treeActionFactory;
/* 158 */     this.workspaceHelper = workspaceHelper;
/* 159 */     this.clientSettingsStorage = clientSettingsStorage;
/*     */   }
/*     */ 
/*     */   public JPopupMenu getPopupMenuFor(MouseEvent event, WorkspaceTreeNode workspaceTreeNode) {
/* 163 */     if ((workspaceTreeNode instanceof StrategiesNode))
/* 164 */       return createMenuForStrategies(workspaceTreeNode);
/* 165 */     if ((workspaceTreeNode instanceof StrategyTreeNode))
/* 166 */       return createPopupMenuForStrategy((StrategyTreeNode)workspaceTreeNode);
/* 167 */     if ((workspaceTreeNode instanceof IndicatorsNode))
/* 168 */       return createMenuForIndicators(workspaceTreeNode);
/* 169 */     if ((workspaceTreeNode instanceof CustIndTreeNode))
/* 170 */       return createPopupMenuForCustomIndicator(workspaceTreeNode);
/* 171 */     if ((workspaceTreeNode instanceof ChartsNode))
/* 172 */       return createPopupMenuForChart(workspaceTreeNode);
/* 173 */     if ((workspaceTreeNode instanceof ChartTreeNode))
/* 174 */       return createPopupMenuForChartTreeNode(workspaceTreeNode);
/* 175 */     if ((workspaceTreeNode instanceof IndicatorTreeNode))
/* 176 */       return createPopupMenuForIndicatorTreeNode(workspaceTreeNode);
/* 177 */     if ((workspaceTreeNode instanceof DrawingTreeNode)) {
/* 178 */       DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 179 */       DrawingTreeNode drawingTreeNode = (DrawingTreeNode)workspaceTreeNode;
/* 180 */       IChartObject drawing = drawingTreeNode.getDrawing();
/* 181 */       ChartTreeNode chartTreeNode = (ChartTreeNode)drawingTreeNode.getParent();
/* 182 */       return ddsChartsController.createPopupMenuForDrawing(chartTreeNode.getChartPanelId(), drawing, event.getComponent(), event.getLocationOnScreen());
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   private JPopupMenu createMenuForStrategies(WorkspaceTreeNode workspaceTreeNode)
/*     */   {
/* 189 */     JPopupMenu popupMenu = new JPopupMenu();
/*     */ 
/* 191 */     if (workspaceTreeNode.getChildCount() > 0) {
/* 192 */       if (this.strategiesExpanded)
/* 193 */         addMenuItem(popupMenu, "item.collapse", TreeActionType.COLLAPSE_STRATEGIES, workspaceTreeNode);
/*     */       else {
/* 195 */         addMenuItem(popupMenu, "item.expand", TreeActionType.EXPAND_STRATEGIES, workspaceTreeNode);
/*     */       }
/* 197 */       popupMenu.addSeparator();
/*     */     }
/*     */ 
/* 200 */     boolean hasChilds = workspaceTreeNode.getChildCount() > 0;
/*     */ 
/* 202 */     if (!GreedContext.isContest()) {
/* 203 */       addMenuItem(popupMenu, "item.new.strategy", TreeActionType.ADD_TASK, null);
/* 204 */       addMenuItem(popupMenu, "item.open.strategy", TreeActionType.OPEN_TASK, workspaceTreeNode);
/* 205 */       addMenuItemTo(popupMenu, "item.remove.all", TreeActionType.CLEAR_ALL, hasChilds, workspaceTreeNode);
/* 206 */       popupMenu.addSeparator();
/*     */     }
/*     */ 
/* 209 */     addMenuItemTo(popupMenu, "item.control.panel", TreeActionType.CONTROL_PANEL, true, workspaceTreeNode);
/*     */ 
/* 211 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   private JPopupMenu createMenuForIndicators(WorkspaceTreeNode workspaceTreeNode) {
/* 215 */     JPopupMenu popupMenu = new JPopupMenu();
/*     */ 
/* 217 */     if (workspaceTreeNode.getChildCount() > 0) {
/* 218 */       if (this.indicatorsExpanded)
/* 219 */         addMenuItem(popupMenu, "item.collapse", TreeActionType.COLLAPSE_INDICATORS, workspaceTreeNode);
/*     */       else {
/* 221 */         addMenuItem(popupMenu, "item.expand", TreeActionType.EXPAND_INDICATORS, workspaceTreeNode);
/*     */       }
/* 223 */       popupMenu.addSeparator();
/*     */     }
/*     */ 
/* 226 */     boolean hasChilds = workspaceTreeNode.getChildCount() > 0;
/*     */ 
/* 228 */     if (!GreedContext.isContest()) {
/* 229 */       addMenuItem(popupMenu, "item.new.indicator", TreeActionType.ADD_CUST_IND, workspaceTreeNode);
/* 230 */       addMenuItem(popupMenu, "item.open.indicator", TreeActionType.OPEN_CUST_IND, workspaceTreeNode);
/* 231 */       addMenuItemTo(popupMenu, "item.remove.all", TreeActionType.CLEAR_ALL, hasChilds, workspaceTreeNode);
/*     */     }
/*     */ 
/* 234 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   private JPopupMenu createPopupMenuForChart(WorkspaceTreeNode workspaceTreeNode)
/*     */   {
/* 239 */     JPopupMenu popupMenu = new JPopupMenu();
/*     */ 
/* 241 */     if (this.chartsExpanded)
/* 242 */       addMenuItem(popupMenu, "item.collapse", TreeActionType.COLLAPSE_CHARTS, workspaceTreeNode);
/*     */     else {
/* 244 */       addMenuItem(popupMenu, "item.expand", TreeActionType.EXPAND_CHARTS, workspaceTreeNode);
/*     */     }
/*     */ 
/* 247 */     popupMenu.addSeparator();
/* 248 */     addChartMenuItem(popupMenu, "item.add.chart", workspaceTreeNode);
/*     */ 
/* 250 */     WorkspaceRootNode chartsNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 251 */     if (chartsNode.getChildCount() > 1) {
/* 252 */       popupMenu.addSeparator();
/* 253 */       this.chartTabsAndFramesController.populatePopupMenuWithMenuItems(popupMenu);
/*     */     }
/*     */ 
/* 256 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   private JPopupMenu createPopupMenuForCustomIndicator(WorkspaceTreeNode workspaceTreeNode) {
/* 260 */     CustIndTreeNode custIndTreeNode = (CustIndTreeNode)workspaceTreeNode;
/* 261 */     CustIndicatorWrapper custIndicatorWrapper = custIndTreeNode.getServiceWrapper();
/*     */ 
/* 263 */     JPopupMenu popupMenu = new JPopupMenu();
/* 264 */     addMenuItemTo(popupMenu, "item.edit", TreeActionType.EDIT_CUST_IND, custIndicatorWrapper.isEditable(), workspaceTreeNode);
/* 265 */     if (custIndicatorWrapper.isEditable()) {
/* 266 */       addMenuItemTo(popupMenu, "item.compile", TreeActionType.COMPILE, true, workspaceTreeNode);
/*     */     }
/* 268 */     addMenuItemTo(popupMenu, "item.add.cust.ind.to.chart", TreeActionType.ADD_CUST_IND_TO_CHART, true, workspaceTreeNode);
/*     */ 
/* 270 */     popupMenu.addSeparator();
/* 271 */     addMenuItemTo(popupMenu, "SAVE_AS_TOOLTIP", TreeActionType.SAVE_AS, true, workspaceTreeNode);
/* 272 */     addMenuItemTo(popupMenu, "item.remove", TreeActionType.DELETE, true, workspaceTreeNode);
/*     */ 
/* 274 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   private JPopupMenu createPopupMenuForStrategy(StrategyTreeNode strategyTreeNode) {
/* 278 */     StrategyWrapper strategyWrapper = strategyTreeNode.getServiceWrapper();
/*     */ 
/* 280 */     JPopupMenu popupMenu = new JPopupMenu();
/*     */ 
/* 282 */     addMenuItemTo(popupMenu, "item.control", TreeActionType.CONTROL_PANEL, true, strategyTreeNode);
/* 283 */     addMenuItemTo(popupMenu, "item.edit", TreeActionType.EDIT_TASK, strategyWrapper.isEditable(), strategyTreeNode);
/* 284 */     addMenuItemTo(popupMenu, "item.compile", TreeActionType.COMPILE, (strategyWrapper.isEditable()) && (!isRunning(strategyTreeNode)), strategyTreeNode);
/*     */ 
/* 286 */     boolean allowTesting = (!strategyTreeNode.isNewUnsaved()) && (!strategyTreeNode.isRemote()) && (strategyTreeNode.getStrategy().getStrategyBinaryFile() != null);
/*     */ 
/* 289 */     addMenuItemTo(popupMenu, "item.test", TreeActionType.TEST_TASK, allowTesting, strategyTreeNode);
/* 290 */     popupMenu.addSeparator();
/*     */ 
/* 292 */     if ((!strategyWrapper.isEditable()) && (strategyWrapper.getBinaryFile() != null)) {
/* 293 */       addMenuItemTo(popupMenu, "SAVE_AS_TOOLTIP", TreeActionType.SAVE_AS, true, strategyTreeNode);
/*     */     }
/* 295 */     addMenuItemTo(popupMenu, "item.remove", TreeActionType.DELETE, (!isRunning(strategyTreeNode)) && (!GreedContext.isContest()), strategyTreeNode);
/*     */ 
/* 297 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   private boolean isRunning(StrategyTreeNode node) {
/* 301 */     return (node.isRunning()) || (node.isRunningRemotely());
/*     */   }
/*     */ 
/*     */   private JPopupMenu createPopupMenuForIndicatorTreeNode(WorkspaceTreeNode workspaceTreeNode) {
/* 305 */     JPopupMenu popupMenu = new JPopupMenu();
/* 306 */     addMenuItem(popupMenu, "item.edit.indicator", TreeActionType.EDIT_INDICATOR, workspaceTreeNode);
/* 307 */     addMenuItem(popupMenu, "item.remove.indicator", TreeActionType.DELETE, workspaceTreeNode);
/* 308 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   private JPopupMenu createPopupMenuForChartTreeNode(WorkspaceTreeNode workspaceTreeNode) {
/* 312 */     JPopupMenu popupMenu = new JPopupMenu();
/* 313 */     addCloneChartMenuItem(popupMenu, "clone.chart.popup.menu.item", workspaceTreeNode);
/* 314 */     addOpenTemplateMenuItem(popupMenu, "open.template.popup.menu.item", workspaceTreeNode);
/* 315 */     addSaveTemplateMenuItem(popupMenu, "save.template.popup.menu.item", workspaceTreeNode);
/* 316 */     popupMenu.addSeparator();
/* 317 */     addChangeInstrumentMenuItem(popupMenu, "item.change.instrument", workspaceTreeNode);
/* 318 */     addChangePeriodMenuItem(popupMenu, "item.change.period", workspaceTreeNode);
/* 319 */     addIndicatorMenuItem(popupMenu, "item.add.indicator", workspaceTreeNode);
/* 320 */     popupMenu.addSeparator();
/* 321 */     addMenuItem(popupMenu, "item.clear.chart", TreeActionType.CLEAR_CHART, workspaceTreeNode);
/* 322 */     addMenuItem(popupMenu, "item.close.chart", TreeActionType.DELETE, workspaceTreeNode);
/* 323 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   private void addMenuItem(JPopupMenu popupMenu, String label, TreeActionType treeActionType, WorkspaceTreeNode workspaceTreeNode) {
/* 327 */     addMenuItemTo(popupMenu, label, treeActionType, true, workspaceTreeNode);
/*     */   }
/*     */ 
/*     */   private void addMenuItemTo(JPopupMenu popupMenu, String label, TreeActionType treeActionType, boolean isEnabled, WorkspaceTreeNode workspaceTreeNode) {
/* 331 */     JLocalizableMenuItem menuItem = new JLocalizableMenuItem(label);
/* 332 */     menuItem.setActionCommand(treeActionType.name());
/* 333 */     menuItem.setEnabled(isEnabled);
/* 334 */     menuItem.addActionListener(new ActionListener(workspaceTreeNode) {
/*     */       public void actionPerformed(ActionEvent event) {
/* 336 */         WorkspaceJTreePopupFactory.this.treeActionFactory.createAction(event.getActionCommand(), WorkspaceJTreePopupFactory.this.workspaceJTree).execute(this.val$workspaceTreeNode);
/*     */       }
/*     */     });
/* 339 */     popupMenu.add(menuItem);
/*     */   }
/*     */ 
/*     */   private void addCloneChartMenuItem(JPopupMenu popupMenu, String label, WorkspaceTreeNode workspaceTreeNode) {
/* 343 */     JLocalizableMenuItem cloneChartMenuItem = new JLocalizableMenuItem(label);
/* 344 */     cloneChartMenuItem.addActionListener(new ActionListener(workspaceTreeNode)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 347 */         if ((this.val$workspaceTreeNode instanceof ChartTreeNode)) {
/* 348 */           ChartTreeNode selectedInstrumentNode = (ChartTreeNode)this.val$workspaceTreeNode;
/* 349 */           ChartTemplateSettingsStorage chartTemplateSettingsStorage = (ChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/* 350 */           ChartBean chartBean = chartTemplateSettingsStorage.cloneChart(selectedInstrumentNode.getChartPanelId());
/* 351 */           WorkspaceJTreePopupFactory.this.treeActionFactory.createAction(TreeActionType.OPEN_CHART_TEMPLATE, WorkspaceJTreePopupFactory.this.workspaceJTree).execute(new Object[] { this.val$workspaceTreeNode, selectedInstrumentNode.getInstrument(), chartBean });
/*     */         }
/*     */       }
/*     */     });
/* 355 */     popupMenu.add(cloneChartMenuItem);
/*     */   }
/*     */ 
/*     */   private void addOpenTemplateMenuItem(JPopupMenu popupMenu, String label, WorkspaceTreeNode workspaceTreeNode) {
/* 359 */     JLocalizableMenuItem openChartMenuItem = new JLocalizableMenuItem(label);
/* 360 */     openChartMenuItem.addActionListener(new ActionListener(workspaceTreeNode)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 363 */         if ((this.val$workspaceTreeNode instanceof ChartTreeNode)) {
/* 364 */           IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/* 365 */           ChartTreeNode chartTreeNode = (ChartTreeNode)this.val$workspaceTreeNode;
/* 366 */           ChartBean chartBean = chartTemplateSettingsStorage.openChartTemplate();
/* 367 */           if (chartBean != null)
/* 368 */             WorkspaceJTreePopupFactory.this.treeActionFactory.createAction(TreeActionType.OPEN_CHART_TEMPLATE, WorkspaceJTreePopupFactory.this.workspaceJTree).execute(new Object[] { this.val$workspaceTreeNode, chartTreeNode.getInstrument(), chartBean });
/*     */         }
/*     */       }
/*     */     });
/* 373 */     popupMenu.add(openChartMenuItem);
/*     */   }
/*     */ 
/*     */   private void addSaveTemplateMenuItem(JPopupMenu popupMenu, String label, WorkspaceTreeNode workspaceTreeNode) {
/* 377 */     JLocalizableMenuItem saveChartMenuItem = new JLocalizableMenuItem(label);
/* 378 */     saveChartMenuItem.addActionListener(new ActionListener(workspaceTreeNode)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 381 */         if ((this.val$workspaceTreeNode instanceof ChartTreeNode)) {
/* 382 */           IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/* 383 */           ChartTreeNode chartTreeNode = (ChartTreeNode)this.val$workspaceTreeNode;
/* 384 */           chartTemplateSettingsStorage.saveChartTemplate(chartTreeNode.getChartPanelId());
/*     */         }
/*     */       }
/*     */     });
/* 388 */     popupMenu.add(saveChartMenuItem);
/*     */   }
/*     */ 
/*     */   private void addChangePeriodMenuItem(JPopupMenu popupMenu, String label, WorkspaceTreeNode workspaceTreeNode) {
/* 392 */     JLocalizableMenu periodsMenu = new JLocalizableMenu(label);
/* 393 */     List periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreChartPeriods();
/* 394 */     for (JForexPeriod period : periods) {
/* 395 */       JMenuItem periodItem = new JMenuItem(ChartsLocalizator.localize(period));
/* 396 */       periodItem.addActionListener(new ChangePeriodActionListener(this.chartTabsAndFramesController, period, workspaceTreeNode));
/* 397 */       periodsMenu.add(periodItem);
/*     */     }
/*     */ 
/* 400 */     popupMenu.add(periodsMenu);
/*     */   }
/*     */ 
/*     */   private void addChangeInstrumentMenuItem(JPopupMenu popupMenu, String label, WorkspaceTreeNode workspaceTreeNode) {
/* 404 */     JLocalizableMenu instrumentsMenu = new JLocalizableMenu(label);
/*     */ 
/* 406 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 407 */     List strInstruments = clientSettingsStorage.restoreSelectedInstruments();
/*     */ 
/* 409 */     for (String instrument : strInstruments) {
/* 410 */       JMenuItem insturmentItem = new JMenuItem(instrument);
/* 411 */       insturmentItem.addActionListener(new ChangeInsturmentActionListener(this.chartTabsAndFramesController, Instrument.fromString(instrument), workspaceTreeNode));
/* 412 */       instrumentsMenu.add(insturmentItem);
/*     */     }
/*     */ 
/* 415 */     popupMenu.add(instrumentsMenu);
/*     */   }
/*     */ 
/*     */   private void addIndicatorMenuItem(JPopupMenu popupMenu, String label, WorkspaceTreeNode workspaceTreeNode) {
/* 419 */     JLocalizableMenu indicatorsMenu = new JLocalizableMenu(label);
/* 420 */     JLocalizableMenu lastUsedMenu = new JLocalizableMenu("item.last.used.indicator");
/*     */ 
/* 422 */     List lastUsedIndicators = this.clientSettingsStorage.getLastUsedIndicatorNames();
/* 423 */     IndicatorsProvider provider = IndicatorsProvider.getInstance();
/* 424 */     boolean atLeastOneAdded = false;
/* 425 */     for (LastUsedIndicatorBean lastUsedIndicator : lastUsedIndicators) {
/* 426 */       if (provider.isIndicatorRegistered(lastUsedIndicator.getName())) {
/*     */         IndicatorWrapper indicatorWrp;
/*     */         try { indicatorWrp = new IndicatorWrapper(lastUsedIndicator.getName(), lastUsedIndicator.getSidesForTicks(), lastUsedIndicator.getAppliedPricesForCandles(), lastUsedIndicator.getOptParams(), lastUsedIndicator.getOutputColors(), lastUsedIndicator.getOutputColors2(), lastUsedIndicator.getShowValuesOnChart(), lastUsedIndicator.getShowOutputs(), lastUsedIndicator.getOpacityAlphas(), lastUsedIndicator.getDrawingStyles(), lastUsedIndicator.getLineWidths(), lastUsedIndicator.getOutputShifts());
/*     */ 
/* 443 */           for (int i = 0; i < indicatorWrp.getShowOutputs().length; i++)
/* 444 */             indicatorWrp.getIndicator().getOutputParameterInfo(i).setShowOutput(indicatorWrp.showOutput(i));
/*     */         }
/*     */         catch (InvalidParameterException e)
/*     */         {
/* 448 */           indicatorWrp = new IndicatorWrapper(lastUsedIndicator.getName());
/*     */         }
/* 450 */         IndicatorWrapper indicatorWrapper = indicatorWrp;
/* 451 */         String props = indicatorWrapper.getPropsStr();
/*     */         String indicatorLabel;
/*     */         String indicatorLabel;
/* 453 */         if (props != null)
/* 454 */           indicatorLabel = indicatorWrapper.getName() + "(" + props + ")";
/*     */         else {
/* 456 */           indicatorLabel = indicatorWrapper.getName();
/*     */         }
/* 458 */         JMenuItem menuItem = new JMenuItem(indicatorLabel);
/* 459 */         menuItem.setActionCommand(TreeActionType.ADD_INDICATOR.name());
/* 460 */         menuItem.setEnabled(true);
/* 461 */         menuItem.addActionListener(new ActionListener(workspaceTreeNode, indicatorWrapper) {
/*     */           public void actionPerformed(ActionEvent event) {
/* 463 */             WorkspaceJTreePopupFactory.this.treeActionFactory.createAction(TreeActionType.ADD_INDICATOR, WorkspaceJTreePopupFactory.this.workspaceJTree).execute(new Object[] { this.val$workspaceTreeNode, this.val$indicatorWrapper });
/*     */           }
/*     */         });
/* 466 */         lastUsedMenu.add(menuItem);
/* 467 */         atLeastOneAdded = true;
/*     */       }
/*     */     }
/*     */ 
/* 471 */     if (atLeastOneAdded) {
/* 472 */       JLocalizableMenuItem clearItem = new JLocalizableMenuItem("item.clear.indicator");
/* 473 */       clearItem.setActionCommand(ChartToolBar.Action.CLEAR_INDICATORS.name());
/* 474 */       clearItem.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 477 */           ((ClientSettingsStorage)GreedContext.get("settingsStorage")).clearAllLastUsedIndicatorNames();
/*     */         }
/*     */       });
/* 480 */       lastUsedMenu.add(clearItem);
/* 481 */       indicatorsMenu.add(lastUsedMenu);
/* 482 */       indicatorsMenu.addSeparator();
/* 483 */       popupMenu.addSeparator();
/*     */     }
/*     */ 
/* 486 */     JLocalizableMenuItem menuItem = new JLocalizableMenuItem("item.more");
/* 487 */     menuItem.setActionCommand(TreeActionType.ADD_INDICATOR.name());
/* 488 */     menuItem.setEnabled(true);
/* 489 */     menuItem.addActionListener(new ActionListener(workspaceTreeNode) {
/*     */       public void actionPerformed(ActionEvent event) {
/* 491 */         WorkspaceJTreePopupFactory.this.treeActionFactory.createAction(TreeActionType.ADD_INDICATOR, WorkspaceJTreePopupFactory.this.workspaceJTree).execute(new Object[] { this.val$workspaceTreeNode, null });
/*     */       }
/*     */     });
/* 494 */     indicatorsMenu.add(menuItem);
/*     */ 
/* 496 */     popupMenu.add(indicatorsMenu);
/*     */   }
/*     */ 
/*     */   private void addChartMenuItem(JPopupMenu popupMenu, String label, WorkspaceTreeNode workspaceTreeNode) {
/* 500 */     JLocalizableMenu chartsMenu = new JLocalizableMenu(label);
/* 501 */     chartsMenu.addMenuListener(new ChartsMenuListener(chartsMenu, this.workspaceJTree, this.workspaceHelper, this.treeActionFactory, workspaceTreeNode));
/* 502 */     popupMenu.add(chartsMenu);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTreePopupFactory
 * JD-Core Version:    0.6.0
 */