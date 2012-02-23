/*     */ package com.dukascopy.dds2.greed.gui;
/*     */ 
/*     */ import com.dukascopy.api.INewsFilter.NewsSource;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.charts.listener.CustomIndicatorsActionListener;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.charts.persistence.BottomPanelBean;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.IGUIManagerImpl;
/*     */ import com.dukascopy.dds2.greed.console.MessagePanelWrapper;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.HistoricalDataManagerBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.alerter.AlerterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomPanelForMessages;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomPanelWithoutProfitLossLabel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramePanel.TabsTypes;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.DockedUndockedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.ChartTabsCloseListenerForJForex;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListenerAdapter;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.DowJonesCalendarPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.news.DowJonesNewsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.export.historicaldata.HistoricalDataManagerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.menu.MainMenu;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.AbstractMessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.TabComponent;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTreePopupFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeControllerImpl;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeKeyListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeModelListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeMouseListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeWillExpandListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.JForexWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.SplitPaneResizeListener;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBoxMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.ToolTipManager;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.event.TreeSelectionListener;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeSelectionModel;
/*     */ 
/*     */ public class JForexClientFormLayoutManager extends ClientFormLayoutManager
/*     */ {
/*     */   public static final int EXPOSURE_TAB_INDEX = 0;
/*     */   public static final int POSITIONS_TAB_INDEX = 1;
/*     */   public static final int ORDERS_TAB_INDEX = 2;
/*     */   public static final int MESSAGES_TAB_INDEX = 3;
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final int TESTER_TAB_INDEX = 4;
/*     */   public static final int STRATEGIES_TAB_INDEX = 5;
/*     */   public static final int NEWS_TAB_INDEX = 6;
/*     */   public static final int CALENDAR_TAB_INDEX = 7;
/*     */   public static final int SCIENTIFIC_CALCULATOR_TAB_INDEX = 8;
/*     */   public static final int ALERTER_TAB_INDEX = 9;
/*     */   public static final int HISTORICAL_DATA_MANAGER_TAB_INDEX = 10000;
/*     */   private IdManager idManager;
/*     */   private TreeActionFactory treeActionFactory;
/*     */   private WorkspaceNodeFactory workspaceNodeFactory;
/*     */   private WorkspaceJTreePopupFactory workspaceJTreePopupFactory;
/*     */   private BodySplitPanel bodySplitPanel;
/*     */   private WorkspaceTreePanel workspaceTreePanel;
/*     */   private WorkspaceJTree workspaceJTree;
/*     */   private StrategiesContentPane strategiesPanel;
/*     */   private TickerPanel tickerPanel;
/*     */   private BottomTabsAndFramesTabbedPane bottomTabsAndFramesTabbedPane;
/*     */   private IGUIManagerImpl IGUIManager;
/*     */   private boolean splittersResized;
/*     */   private WorkspaceTreeControllerImpl workspaceTreeController;
/*     */ 
/*     */   public JForexClientFormLayoutManager()
/*     */   {
/* 101 */     this.idManager = IdManager.getInstance();
/*     */   }
/*     */ 
/*     */   protected void createSpecificComponents()
/*     */   {
/* 136 */     this.workspaceTreeController = new WorkspaceTreeControllerImpl();
/*     */ 
/* 138 */     this.bottomTabsAndFramesTabbedPane = new BottomTabsAndFramesTabbedPane(this.exposurePanel, this.positionsPanel, this.ordersPanel, this);
/*     */ 
/* 140 */     this.workspaceJTree = new WorkspaceJTree();
/* 141 */     this.workspaceTreePanel = new WorkspaceTreePanel(this.dealPanel, this.workspaceTreeController);
/*     */ 
/* 143 */     this.tickerPanel = new TickerPanel(this.dealPanel);
/*     */ 
/* 145 */     if (!GreedContext.isContest()) {
/* 146 */       this.strategiesPanel = new StrategiesContentPane((ClientSettingsStorage)GreedContext.get("settingsStorage"), this.bottomTabsAndFramesTabbedPane, this.chartTabsAndFramesController);
/*     */     }
/*     */ 
/* 151 */     this.splitPane = new ExpandableSplitPane("MainSplitPane", 0, this.chartTabsAndFramesPanel, this.bottomTabsAndFramesTabbedPane);
/*     */ 
/* 153 */     this.workspaceNodeFactory = new WorkspaceNodeFactory(this.workspaceJTree, this.idManager);
/*     */ 
/* 155 */     this.workspaceHelper = new JForexWorkspaceHelper(this, this.chartTabsAndFramesController, this.bottomTabsAndFramesTabbedPane, this.workspaceNodeFactory);
/*     */ 
/* 162 */     this.treeActionFactory = new TreeActionFactory(this.workspaceTreePanel, this.bottomTabsAndFramesTabbedPane, this.ordersPanel, this.positionsPanel, this.workspaceTreeController, (DDSChartsController)GreedContext.get("chartsController"), this.chartTabsAndFramesController, this.workspaceHelper, this.workspaceNodeFactory, (ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/* 175 */     this.workspaceJTreePopupFactory = new WorkspaceJTreePopupFactory(this.workspaceJTree, this.workspaceNodeFactory, this.chartTabsAndFramesController, this.treeActionFactory, this.workspaceHelper, (ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/* 184 */     ToolTipManager.sharedInstance().registerComponent(this.workspaceJTree);
/*     */ 
/* 186 */     this.IGUIManager = new IGUIManagerImpl(this.chartTabsAndFramesController, this.bottomTabsAndFramesTabbedPane, this.idManager);
/*     */   }
/*     */ 
/*     */   protected void setSpecificDependencies()
/*     */   {
/* 191 */     this.dealPanel.setWorkspacePanel(this.tickerPanel);
/* 192 */     this.dealPanel.setWorkspaceTreePanel(this.workspaceTreePanel);
/*     */ 
/* 194 */     this.workspaceTreePanel.setWorkspaceJTree(this.workspaceJTree);
/*     */ 
/* 196 */     this.workspaceJTree.setModel(new DefaultTreeModel(this.workspaceNodeFactory.createWorkspaceRootNode()));
/* 197 */     this.workspaceJTree.setWorkspaceHelper(this.workspaceHelper);
/*     */ 
/* 199 */     this.workspaceTreeController.setWorkspaceJTree(this.workspaceJTree);
/* 200 */     this.workspaceTreeController.setWorkspaceNodeFactory(this.workspaceNodeFactory);
/* 201 */     this.workspaceTreeController.setWorkspaceTreePanel(this.workspaceTreePanel);
/* 202 */     this.workspaceTreeController.setTreeActionFactory(this.treeActionFactory);
/* 203 */     this.workspaceTreeController.setWorkspaceHelper(this.workspaceHelper);
/* 204 */     this.workspaceTreeController.setClientSettingsStorage((ClientSettingsStorage)GreedContext.get("settingsStorage"));
/*     */ 
/* 206 */     this.chartTabsAndFramesController.setWorkspaceController(this.workspaceTreeController);
/*     */ 
/* 208 */     if (this.strategiesPanel != null)
/* 209 */       this.strategiesPanel.setWorkspaceTreeController(this.workspaceTreeController);
/*     */   }
/*     */ 
/*     */   protected void initSpecificComponents()
/*     */   {
/* 214 */     this.splitPane.setContinuousLayout(true);
/* 215 */     this.splitPane.setResizeWeight(1.0D);
/* 216 */     this.splitPane.setDividerSize(5);
/* 217 */     this.splitPane.setOrientation(0);
/* 218 */     this.splitPane.setOneTouchExpandable(false);
/* 219 */     this.splitPane.addPropertyChangeListener(new SplitPaneResizeListener(this.splitPane));
/*     */ 
/* 221 */     this.splitPane.setBorder(BorderFactory.createEmptyBorder());
/*     */ 
/* 223 */     this.workspaceJTree.build();
/* 224 */     this.workspaceTreePanel.build();
/*     */ 
/* 226 */     this.exposurePanel.setLayout(new BorderLayout());
/* 227 */     this.positionsPanel.setLayout(new BorderLayout());
/* 228 */     this.ordersPanel.setLayout(new BorderLayout());
/*     */ 
/* 230 */     this.exposurePanel.setBorder(BorderFactory.createEmptyBorder());
/* 231 */     this.positionsPanel.setBorder(BorderFactory.createEmptyBorder());
/* 232 */     this.ordersPanel.setBorder(BorderFactory.createEmptyBorder());
/* 233 */     this.dealPanel.setBorder(BorderFactory.createEmptyBorder());
/*     */   }
/*     */ 
/*     */   protected void initSpecificListeners() {
/* 237 */     this.workspaceJTree.addTreeWillExpandListener(this.workspaceJTreePopupFactory);
/* 238 */     this.workspaceJTree.addTreeWillExpandListener(new WorkspaceJTreeWillExpandListener((ClientSettingsStorage)GreedContext.get("settingsStorage")));
/* 239 */     this.workspaceJTree.addMouseListener(new WorkspaceJTreeMouseListener(this.chartTabsAndFramesController, (DDSChartsController)GreedContext.get("chartsController"), this.workspaceJTree, this.workspaceJTreePopupFactory, this.workspaceHelper, this.treeActionFactory));
/* 240 */     this.workspaceJTree.addKeyListener(new WorkspaceJTreeKeyListener(this.treeActionFactory, this.workspaceHelper));
/* 241 */     this.workspaceJTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
/*     */       public void valueChanged(TreeSelectionEvent event) {
/* 243 */         JForexClientFormLayoutManager.this.workspaceHelper.refreshDealPanel(JForexClientFormLayoutManager.this.workspaceHelper.getSelectedInstrument(event));
/*     */       }
/*     */     });
/* 246 */     this.workspaceJTree.getModel().addTreeModelListener(new WorkspaceJTreeModelListener(this.chartTabsAndFramesController));
/*     */ 
/* 248 */     this.chartTabsAndFramesController.addFrameListener(new ChartTabsCloseListenerForJForex(this.workspaceTreeController, this.chartTabsAndFramesController, (ClientSettingsStorage)GreedContext.get("settingsStorage")));
/* 249 */     this.chartTabsAndFramesController.addFrameListener(new FrameListenerAdapter() {
/*     */       public void frameAdded(boolean isUndocked, int tabCount) {
/* 251 */         if ((!isUndocked) && (tabCount == 1) && 
/* 252 */           (JForexClientFormLayoutManager.this.getSplitPane().isMinimized()))
/* 253 */           JForexClientFormLayoutManager.this.getSplitPane().setDividerLocation(0.75D);
/*     */       }
/*     */     });
/* 259 */     this.chartTabsAndFramesController.addFrameListener(new FrameListenerAdapter()
/*     */     {
/*     */       public void frameSelected(int panelId)
/*     */       {
/* 263 */         DockedUndockedFrame strategiesFrame = JForexClientFormLayoutManager.this.bottomTabsAndFramesTabbedPane.getPanelByPanelId(5);
/*     */ 
/* 265 */         if ((strategiesFrame != null) && (JForexClientFormLayoutManager.this.strategiesPanel != null)) {
/* 266 */           JForexClientFormLayoutManager.this.strategiesPanel.selectStrategy(panelId);
/*     */         }
/*     */ 
/* 269 */         JForexClientFormLayoutManager.this.workspaceTreeController.setSelectedInstrumentByPanelId(panelId);
/*     */       }
/*     */     });
/* 275 */     this.bottomTabsAndFramesTabbedPane.addFrameListener(new FrameListenerAdapter() {
/*     */       public void frameClosed(TabsAndFramePanel tabsAndFramePanel, int tabCount) {
/* 277 */         ((ClientSettingsStorage)GreedContext.get("settingsStorage")).remove(new BottomPanelBean(tabsAndFramePanel.getPanelId()));
/*     */ 
/* 280 */         JCheckBoxMenuItem menuItem = null;
/*     */ 
/* 282 */         switch (tabsAndFramePanel.getPanelId()) {
/*     */         case 6:
/* 284 */           menuItem = JForexClientFormLayoutManager.this.mainMenu.getNewsMenuItem();
/* 285 */           break;
/*     */         case 7:
/* 287 */           menuItem = JForexClientFormLayoutManager.this.mainMenu.getCalendarMenuItem();
/* 288 */           break;
/*     */         case 9:
/* 290 */           menuItem = JForexClientFormLayoutManager.this.mainMenu.getAlerterMenuItem();
/* 291 */           break;
/*     */         case 5:
/* 293 */           menuItem = JForexClientFormLayoutManager.this.mainMenu.getStrategiesMenuItem();
/* 294 */           break;
/*     */         case 10000:
/* 296 */           menuItem = JForexClientFormLayoutManager.this.mainMenu.getHistoricalDataManagerMenuItem();
/* 297 */           break;
/*     */         }
/*     */ 
/* 302 */         if (menuItem != null)
/* 303 */           menuItem.setSelected(false);
/*     */       }
/*     */     });
/* 308 */     IndicatorsProvider.getInstance().addCustomIndicatorsActionListener(new CustomIndicatorsActionListener()
/*     */     {
/*     */       public void customIndicatorRegistered(CustIndicatorWrapper custIndicatorWrapper)
/*     */       {
/* 312 */         JForexClientFormLayoutManager.this.workspaceTreeController.synchronizeCustomIndicatorsNode();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   protected void placeSpecificComponents() {
/* 318 */     this.body.setLayout(new BorderLayout());
/* 319 */     this.desktop.setLayout(new BoxLayout(this.desktop, 1));
/*     */ 
/* 321 */     this.rightPanel.setLayout(new BoxLayout(this.rightPanel, 1));
/* 322 */     this.rightPanel.add(this.splitPane);
/*     */ 
/* 324 */     this.bodySplitPanel = new BodySplitPanel();
/* 325 */     this.body.add(this.bodySplitPanel, "West");
/* 326 */     this.body.add(this.rightPanel, "Center");
/*     */ 
/* 328 */     this.desktop.add(this.body);
/* 329 */     this.desktop.add(Box.createVerticalStrut(2));
/* 330 */     this.desktop.add(this.statusBar);
/*     */ 
/* 332 */     this.content.setLayout(new BorderLayout());
/* 333 */     this.content.add(this.desktop, "Center");
/*     */ 
/* 335 */     this.bottomTabsAndFramesTabbedPane.setMinimumSize(new Dimension(0, 0));
/*     */   }
/*     */ 
/*     */   public void resetlayout() {
/* 339 */     this.splitPane.setDividerLocation(0.8D);
/*     */   }
/*     */ 
/*     */   protected void saveSpecificClientSettings(ClientSettingsStorage settingsStorage)
/*     */   {
/* 344 */     settingsStorage.saveSplitPane(this.splitPane);
/* 345 */     this.chartTabsAndFramesController.saveState();
/* 346 */     Preferences framePreferencesNode = settingsStorage.getBottomFramePreferencesNode();
/* 347 */     this.bottomTabsAndFramesTabbedPane.saveState(settingsStorage, framePreferencesNode, framePreferencesNode);
/*     */ 
/* 349 */     settingsStorage.setBodySplitExpanded(this.bodySplitPanel.isExpanded());
/*     */ 
/* 351 */     List testPanels = this.bottomTabsAndFramesTabbedPane.getStrategyTestPanels();
/*     */ 
/* 353 */     List beans = new LinkedList();
/* 354 */     for (StrategyTestPanel strategyTestPanel : testPanels) {
/* 355 */       int panelId = this.bottomTabsAndFramesTabbedPane.getPanelChartId(strategyTestPanel);
/*     */ 
/* 357 */       StrategyTestBean strategyTestBean = new StrategyTestBean();
/* 358 */       strategyTestBean.setPanelChartId(panelId);
/* 359 */       strategyTestPanel.save(strategyTestBean);
/* 360 */       beans.add(strategyTestBean);
/*     */     }
/* 362 */     settingsStorage.save(beans);
/*     */ 
/* 364 */     settingsStorage.save(this.newsPanel.getFilter(), INewsFilter.NewsSource.DJ_NEWSWIRES);
/* 365 */     settingsStorage.save(this.calendarPanel.getFilter(), INewsFilter.NewsSource.DJ_LIVE_CALENDAR);
/*     */ 
/* 368 */     HistoricalDataManagerBean historicalDataManagerBean = this.historicalDataManagerPanel.getSettings();
/* 369 */     settingsStorage.save(historicalDataManagerBean);
/*     */ 
/* 371 */     settingsStorage.saveTableColumns(this.strategiesPanel.getTable().getTableId(), this.strategiesPanel.getTable().getColumnModel());
/*     */   }
/*     */ 
/*     */   public void resizeSplitters(ClientSettingsStorage settingsSaver) {
/* 375 */     if (!this.splittersResized) {
/* 376 */       settingsSaver.restoreSplitPane(this.splitPane, 0.8D);
/* 377 */       this.splittersResized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public TabsAndFramesTabbedPane getTabbedPane() {
/* 382 */     return this.bottomTabsAndFramesTabbedPane;
/*     */   }
/*     */ 
/*     */   public DowJonesNewsPanel getNewsPanel() {
/* 386 */     return this.newsPanel;
/*     */   }
/*     */ 
/*     */   public DowJonesCalendarPanel getCalendarPanel() {
/* 390 */     return this.calendarPanel;
/*     */   }
/*     */ 
/*     */   public BodySplitPanel getBodySplitPanel() {
/* 394 */     return this.bodySplitPanel;
/*     */   }
/*     */ 
/*     */   public TreeActionFactory getTreeActionFactory() {
/* 398 */     return this.treeActionFactory;
/*     */   }
/*     */ 
/*     */   public WorkspaceJTree getWorkspaceJTree() {
/* 402 */     return this.workspaceJTree;
/*     */   }
/*     */ 
/*     */   public WorkspaceNodeFactory getWorkspaceNodeFactory() {
/* 406 */     return this.workspaceNodeFactory;
/*     */   }
/*     */ 
/*     */   public IGUIManagerImpl getIGUIManager() {
/* 410 */     return this.IGUIManager;
/*     */   }
/*     */ 
/*     */   public StrategiesContentPane getStrategiesPanel() {
/* 414 */     return this.strategiesPanel;
/*     */   }
/*     */ 
/*     */   public void addMessagesPanel(int panelId, AbstractMessagePanel panelToWrap, String title, boolean isUndocked, boolean isExpanded, boolean isClosable, boolean select, boolean saveInSettings)
/*     */   {
/* 424 */     DockedUndockedFrame dockedUndockedFrame = this.bottomTabsAndFramesTabbedPane.getPanelByPanelId(panelId);
/* 425 */     if (dockedUndockedFrame == null) {
/* 426 */       TabsAndFramePanel wrappedPanel = BottomTabsAndFramePanel.create(panelId, panelToWrap, BottomTabsAndFramePanel.TabsTypes.MESSAGE_TAB);
/* 427 */       ((BottomPanelForMessages)wrappedPanel).setCloseButtonVisible(isClosable);
/* 428 */       this.bottomTabsAndFramesTabbedPane.addFrame(wrappedPanel, "tab.messages", isUndocked, isExpanded, select);
/* 429 */       this.bottomTabsAndFramesTabbedPane.setTitleForPanelId(panelId, title);
/* 430 */       if (saveInSettings)
/* 431 */         ((ClientSettingsStorage)GreedContext.get("settingsStorage")).save(new BottomPanelBean(panelId));
/*     */     }
/* 433 */     else if (select) {
/* 434 */       this.bottomTabsAndFramesTabbedPane.selectPanel(dockedUndockedFrame.getPanelId());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addExposurePanel(boolean isUndocked, boolean isExpanded)
/*     */   {
/* 440 */     createAndAddBottomTab(0, this.exposurePanel, BottomTabsAndFramePanel.TabsTypes.FIXED, JForexWorkspaceHelper.getPositionsSummaryLabelKey(), isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   public void addPositionsPanel(boolean isUndocked, boolean isExpanded) {
/* 444 */     createAndAddBottomTab(1, this.positionsPanel, BottomTabsAndFramePanel.TabsTypes.FIXED, JForexWorkspaceHelper.getPositionsLabelKey(), isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   public void addOrdersPanel(boolean isUndocked, boolean isExpanded) {
/* 448 */     createAndAddBottomTab(2, this.ordersPanel, BottomTabsAndFramePanel.TabsTypes.FIXED, "tab.orders", isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   public HistoricalDataManagerPanel addHistoricalDataManagerPanel(boolean isUndocked, boolean isExpanded) {
/* 452 */     this.mainMenu.getHistoricalDataManagerMenuItem().setSelected(true);
/*     */ 
/* 454 */     createAndAddBottomTab(10000, this.historicalDataManagerPanel, BottomTabsAndFramePanel.TabsTypes.HISTORICAL_DATA_MANAGER, "tab.historical.data.manager", isUndocked, isExpanded);
/*     */ 
/* 461 */     return this.historicalDataManagerPanel;
/*     */   }
/*     */ 
/*     */   public void closeHistoricalDataManagerPanel() {
/* 465 */     this.mainMenu.getHistoricalDataManagerMenuItem().setSelected(false);
/* 466 */     closePanel(10000);
/*     */   }
/*     */ 
/*     */   public void addMessagesPanel(boolean isUndocked, boolean isExpanded)
/*     */   {
/* 471 */     DockedUndockedFrame dockedUndockedFrame = this.bottomTabsAndFramesTabbedPane.getPanelByPanelId(3);
/* 472 */     if (dockedUndockedFrame != null) {
/* 473 */       return;
/*     */     }
/*     */ 
/* 480 */     JLocalizableButton btnCopyMessage = new JLocalizableButton(ResizingManager.ComponentSize.SIZE_120X24);
/* 481 */     btnCopyMessage.setAction(this.messagePanel.getActionCopyMessages());
/* 482 */     JLocalizableButton btnClear = new JLocalizableButton(ResizingManager.ComponentSize.SIZE_120X24);
/* 483 */     btnClear.setAction(this.messagePanel.getActionClearLog());
/*     */ 
/* 485 */     JPanel pnlButtonsInner = new JPanel(new GridLayout(1, 0, 5, 0));
/* 486 */     pnlButtonsInner.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 487 */     pnlButtonsInner.add(btnCopyMessage);
/* 488 */     pnlButtonsInner.add(btnClear);
/*     */ 
/* 490 */     JPanel pnlButtons = new JPanel(new BorderLayout());
/* 491 */     pnlButtons.add(pnlButtonsInner, "West");
/*     */ 
/* 496 */     AbstractMessagePanel panelToWrap = new AbstractMessagePanel()
/*     */     {
/*     */       public void setTabLabel(TabComponent tabComponent) {
/* 499 */         JForexClientFormLayoutManager.this.messagePanel.setTabLabel(tabComponent);
/*     */       }
/*     */     };
/* 502 */     panelToWrap.setLayout(new BorderLayout());
/* 503 */     panelToWrap.add(pnlButtons, "North");
/* 504 */     panelToWrap.add(this.messagePanel, "Center");
/*     */ 
/* 506 */     String panelTitle = LocalizationManager.getText("tab.messages");
/* 507 */     addMessagesPanel(3, panelToWrap, panelTitle, isUndocked, isExpanded, false, true, true);
/*     */   }
/*     */ 
/*     */   public StrategyTestPanel addStrategyTesterPanel(int chartPanelId, boolean isUndocked, boolean isExpanded)
/*     */   {
/* 513 */     StrategyTestPanel strategyTestPanel = new StrategyTestPanel(this.chartTabsAndFramesController, this.workspaceTreeController);
/* 514 */     strategyTestPanel.build();
/*     */ 
/* 516 */     int panelId = this.bottomTabsAndFramesTabbedPane.addTesterPanel(strategyTestPanel, chartPanelId, isUndocked, isExpanded);
/* 517 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).save(new BottomPanelBean(panelId));
/* 518 */     return strategyTestPanel;
/*     */   }
/*     */ 
/*     */   public void addAlerterPanel(boolean isUndocked, boolean isExpanded) {
/* 522 */     createAndAddBottomTab(9, new AlerterPanel(), BottomTabsAndFramePanel.TabsTypes.PRICE_ALERTER, "tab.price.alerter", isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   public void addStrategiesPanel(boolean isUndocked, boolean isExpanded)
/*     */   {
/* 532 */     this.mainMenu.getStrategiesMenuItem().setSelected(true);
/*     */ 
/* 534 */     createAndAddBottomTab(5, this.strategiesPanel, BottomTabsAndFramePanel.TabsTypes.STRATEGIES, "tab.strategies", isUndocked, isExpanded);
/*     */ 
/* 541 */     this.bottomTabsAndFramesTabbedPane.selectPanel(5);
/*     */   }
/*     */ 
/*     */   public boolean isScientificCalculatorPanelVisible()
/*     */   {
/* 562 */     return this.bottomTabsAndFramesTabbedPane.getPanelByPanelId(8) != null;
/*     */   }
/*     */ 
/*     */   public void addNewsPanel(boolean isUndocked, boolean isExpanded) {
/* 566 */     createAndAddBottomTab(6, this.newsPanel, BottomTabsAndFramePanel.TabsTypes.NEWS, "tab.dowjones.news", isUndocked, isExpanded);
/* 567 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).save(new BottomPanelBean(6));
/* 568 */     this.newsPanel.subscribe();
/*     */   }
/*     */ 
/*     */   public void addCalendarPanel(boolean isUndocked, boolean isExpanded) {
/* 572 */     createAndAddBottomTab(7, this.calendarPanel, BottomTabsAndFramePanel.TabsTypes.NEWS, "tab.dowjones.calendar", isUndocked, isExpanded);
/* 573 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).save(new BottomPanelBean(7));
/* 574 */     this.calendarPanel.subscribe();
/*     */   }
/*     */ 
/*     */   private void createAndAddBottomTab(int chartPanelId, JPanel panelToWrap, BottomTabsAndFramePanel.TabsTypes tabType, String label, boolean isUndocked, boolean isExpanded) {
/* 578 */     createAndAddBottomTab(chartPanelId, panelToWrap, tabType, label, null, isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   private void createAndAddBottomTab(int chartPanelId, JPanel panelToWrap, BottomTabsAndFramePanel.TabsTypes tabType, String label, String toolTip, boolean isUndocked, boolean isExpanded) {
/* 582 */     DockedUndockedFrame dockedUndockedFrame = this.bottomTabsAndFramesTabbedPane.getPanelByPanelId(chartPanelId);
/* 583 */     if (dockedUndockedFrame != null) {
/* 584 */       return;
/*     */     }
/* 586 */     TabsAndFramePanel wrappedPanel = BottomTabsAndFramePanel.create(chartPanelId, panelToWrap, tabType);
/* 587 */     this.bottomTabsAndFramesTabbedPane.addFrame(wrappedPanel, LocalizationManager.getText(label), toolTip, isUndocked, isExpanded);
/* 588 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).save(new BottomPanelBean(chartPanelId));
/*     */   }
/*     */ 
/*     */   public StrategyTestPanel getStrategyTestPanel(String strategyName, boolean includingBusy) {
/* 592 */     return this.bottomTabsAndFramesTabbedPane.getStrategyTestPanel(strategyName, includingBusy);
/*     */   }
/*     */ 
/*     */   public StrategyTestPanel getStrategyTestPanel(int chartPanelId) {
/* 596 */     return this.bottomTabsAndFramesTabbedPane.getStrategyTestPanel(chartPanelId);
/*     */   }
/*     */ 
/*     */   public void closeStrategyTesterPanel(int chartPanelId) {
/* 600 */     closePanel(chartPanelId);
/*     */   }
/*     */ 
/*     */   public List<StrategyTestPanel> getStrategyTestPanels() {
/* 604 */     return this.bottomTabsAndFramesTabbedPane.getStrategyTestPanels();
/*     */   }
/*     */ 
/*     */   public void selectStrategyTestPanel(StrategyTestPanel strategyTestPanel) {
/* 608 */     this.bottomTabsAndFramesTabbedPane.selectStrategyTestPanel(strategyTestPanel);
/*     */   }
/*     */ 
/*     */   public StrategyTestPanel getSelectedStrategyTestPanel() {
/* 612 */     int panelId = this.bottomTabsAndFramesTabbedPane.getSelectedPanelId();
/* 613 */     return getStrategyTestPanel(panelId);
/*     */   }
/*     */ 
/*     */   public void closeScientificCalculator() {
/* 617 */     closePanel(8);
/*     */   }
/*     */ 
/*     */   public void closeNewsPanel() {
/* 621 */     closePanel(6);
/* 622 */     this.newsPanel.unsubscribe();
/*     */   }
/*     */ 
/*     */   public void closeCalendarPanel() {
/* 626 */     closePanel(7);
/* 627 */     this.calendarPanel.unsubscribe();
/*     */   }
/*     */ 
/*     */   public void closeAlerterPanel()
/*     */   {
/* 632 */     BottomPanelWithoutProfitLossLabel alerterPanel = (BottomPanelWithoutProfitLossLabel)this.bottomTabsAndFramesTabbedPane.getPanelByPanelId(9).getContent();
/*     */ 
/* 635 */     AlerterPanel alerter = (AlerterPanel)alerterPanel.getContent();
/* 636 */     ((ClientForm)GreedContext.get("clientGui")).removeMarketWatcher(alerter);
/*     */ 
/* 638 */     closePanel(9);
/*     */   }
/*     */ 
/*     */   public void closeStrategiesPanel() {
/* 642 */     this.mainMenu.getStrategiesMenuItem().setSelected(false);
/* 643 */     closePanel(5);
/*     */   }
/*     */ 
/*     */   public void closeMessagesPanel(MessagePanelWrapper messagePanelWrapper) {
/* 647 */     if (messagePanelWrapper != null)
/* 648 */       closePanel(messagePanelWrapper.getPanelId());
/*     */   }
/*     */ 
/*     */   private void closePanel(int panelId)
/*     */   {
/* 653 */     DockedUndockedFrame dockedUndockedFrame = this.bottomTabsAndFramesTabbedPane.getPanelByPanelId(panelId);
/* 654 */     if (dockedUndockedFrame == null) {
/* 655 */       return;
/*     */     }
/* 657 */     this.bottomTabsAndFramesTabbedPane.closeFrame(panelId);
/* 658 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).remove(new BottomPanelBean(panelId));
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 750 */     super.dispose();
/* 751 */     this.bottomTabsAndFramesTabbedPane.closeAll();
/*     */   }
/*     */ 
/*     */   public class BodySplitPanel extends JPanel
/*     */   {
/* 665 */     private JPanel borderPanel = new JPanel(new GridBagLayout());
/* 666 */     private boolean expanded = false;
/*     */ 
/*     */     public BodySplitPanel() {
/* 669 */       super();
/*     */ 
/* 671 */       GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/* 673 */       this.borderPanel.addMouseListener(new MouseAdapter(JForexClientFormLayoutManager.this)
/*     */       {
/*     */         public void mousePressed(MouseEvent e) {
/* 676 */           if (JForexClientFormLayoutManager.BodySplitPanel.this.expanded)
/* 677 */             JForexClientFormLayoutManager.BodySplitPanel.this.collapse();
/*     */           else {
/* 679 */             JForexClientFormLayoutManager.BodySplitPanel.this.expand();
/*     */           }
/* 681 */           JForexClientFormLayoutManager.this.body.validate();
/*     */         }
/*     */ 
/*     */         public void mouseEntered(MouseEvent e)
/*     */         {
/* 686 */           JForexClientFormLayoutManager.BodySplitPanel.this.borderPanel.setCursor(Cursor.getPredefinedCursor(11));
/*     */         }
/*     */ 
/*     */         public void mouseExited(MouseEvent e)
/*     */         {
/* 691 */           JForexClientFormLayoutManager.BodySplitPanel.this.borderPanel.setCursor(Cursor.getDefaultCursor());
/*     */         }
/*     */       });
/* 695 */       gbc.gridx = 0;
/* 696 */       gbc.gridy = 0;
/* 697 */       gbc.fill = 2;
/* 698 */       gbc.weightx = 1.0D;
/* 699 */       gbc.weighty = 1.0D;
/* 700 */       this.borderPanel.add(Box.createHorizontalStrut(4));
/*     */ 
/* 702 */       expand();
/*     */     }
/*     */ 
/*     */     public boolean isExpanded() {
/* 706 */       return this.expanded;
/*     */     }
/*     */ 
/*     */     public void expand() {
/* 710 */       if (this.expanded) {
/* 711 */         return;
/*     */       }
/*     */ 
/* 714 */       this.expanded = true;
/* 715 */       removeAll();
/* 716 */       GridBagConstraints gbc = new GridBagConstraints();
/* 717 */       gbc.gridx = 0;
/* 718 */       gbc.gridy = 0;
/* 719 */       gbc.fill = 1;
/* 720 */       gbc.weightx = 1.0D;
/* 721 */       gbc.weighty = 1.0D;
/* 722 */       add(JForexClientFormLayoutManager.this.dealPanel, gbc);
/* 723 */       gbc.gridx = 1;
/* 724 */       gbc.gridy = 0;
/* 725 */       gbc.fill = 1;
/* 726 */       gbc.weightx = 0.0D;
/* 727 */       gbc.weighty = 1.0D;
/* 728 */       add(this.borderPanel, gbc);
/*     */     }
/*     */ 
/*     */     public void collapse() {
/* 732 */       if (!this.expanded) {
/* 733 */         return;
/*     */       }
/*     */ 
/* 736 */       this.expanded = false;
/* 737 */       removeAll();
/* 738 */       GridBagConstraints gbc = new GridBagConstraints();
/* 739 */       gbc.gridx = 0;
/* 740 */       gbc.gridy = 0;
/* 741 */       gbc.fill = 1;
/* 742 */       gbc.weightx = 0.0D;
/* 743 */       gbc.weighty = 1.0D;
/* 744 */       add(this.borderPanel, gbc);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager
 * JD-Core Version:    0.6.0
 */