/*     */ package com.dukascopy.dds2.greed.gui.helpers;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.CandleInfoParams;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.TickInfoParams;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.impl.connect.StrategyListener;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.drawings.OhlcChartObject;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.charts.persistence.BottomPanelBean;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.CustomIndicatorBean;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.FullDepthInstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.agent.Strategies;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.HistoricalDataManagerBean;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramePanel.TabsTypes;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.DockedUndockedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.FramesState;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler.Event;
/*     */ import com.dukascopy.dds2.greed.gui.component.export.historicaldata.HistoricalDataManagerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.RemoteStrategyMonitor;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNodeChild;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrencyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JForexWorkspaceHelper extends CommonWorkspaceHellper
/*     */ {
/* 107 */   private static Logger LOGGER = LoggerFactory.getLogger(JForexWorkspaceHelper.class);
/*     */   final JForexClientFormLayoutManager jForexClientFormLayoutManager;
/*     */   final WorkspaceNodeFactory workspaceNodeFactory;
/*     */   final IChartTabsAndFramesController mainTabsAndFramesController;
/*     */   final TabsAndFramesTabbedPane bottomTabsAndFramesTabbedPane;
/* 114 */   final Collection<Thread> threads = new LinkedHashSet();
/*     */ 
/*     */   public JForexWorkspaceHelper(JForexClientFormLayoutManager jForexClientFormLayoutManager, IChartTabsAndFramesController mainTabsAndFramesController, TabsAndFramesTabbedPane bottomTabsAndFramesTabbedPane, WorkspaceNodeFactory workpsaceNodeFactory)
/*     */   {
/* 122 */     this.jForexClientFormLayoutManager = jForexClientFormLayoutManager;
/* 123 */     this.mainTabsAndFramesController = mainTabsAndFramesController;
/* 124 */     this.bottomTabsAndFramesTabbedPane = bottomTabsAndFramesTabbedPane;
/* 125 */     this.workspaceNodeFactory = workpsaceNodeFactory;
/*     */   }
/*     */ 
/*     */   public WorkspaceNodeFactory getWorkspaceNodeFactory() {
/* 129 */     return ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceNodeFactory();
/*     */   }
/*     */ 
/*     */   public IChartTabsAndFramesController getChartTabsAndFramesController() {
/* 133 */     return ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController();
/*     */   }
/*     */ 
/*     */   public TabsAndFramesTabbedPane getTabsAndFramesTabbedPane() {
/* 137 */     return ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTabbedPane();
/*     */   }
/*     */ 
/*     */   public ClientFormLayoutManager getLayoutManager() {
/* 141 */     return (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */   }
/*     */ 
/*     */   public void selectTabForSelectedNode(WorkspaceJTree workspaceJTree) {
/* 145 */     if (workspaceJTree.getSelectionCount() > 1) {
/* 146 */       return;
/*     */     }
/* 148 */     TreePath selectionPath = workspaceJTree.getSelectionPath();
/* 149 */     if (selectionPath == null) {
/* 150 */       return;
/*     */     }
/* 152 */     Object lastPathComponent = selectionPath.getLastPathComponent();
/* 153 */     if ((lastPathComponent instanceof ChartTreeNode)) {
/* 154 */       ChartTreeNode selectedChartTreeNode = (ChartTreeNode)lastPathComponent;
/* 155 */       Integer panelId = Integer.valueOf(selectedChartTreeNode.getChartPanelId());
/* 156 */       this.mainTabsAndFramesController.selectPanel(panelId.intValue());
/* 157 */     } else if ((lastPathComponent instanceof AbstractServiceTreeNode)) {
/* 158 */       AbstractServiceTreeNode selectedService = (AbstractServiceTreeNode)lastPathComponent;
/* 159 */       this.mainTabsAndFramesController.selectServiceSourceEditor(selectedService.getId());
/* 160 */     } else if ((lastPathComponent instanceof ChartTreeNodeChild)) {
/* 161 */       ChartTreeNode selectedChart = (ChartTreeNode)((ChartTreeNodeChild)lastPathComponent).getParent();
/* 162 */       if (this.mainTabsAndFramesController.selectPanel(selectedChart.getChartPanelId()))
/* 163 */         workspaceJTree.setSelectionPath(selectionPath);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int calculatePreviousNodeIndxToBeFocused(int nodeToBeRemovedIndx)
/*     */   {
/* 169 */     return nodeToBeRemovedIndx - 1;
/*     */   }
/*     */ 
/*     */   public void loadDataIntoWorkspace(WorkspaceJTree workspaceJTree) {
/* 173 */     getClientSettingsStorage().cleanUp();
/*     */ 
/* 175 */     loadStrategies(workspaceJTree);
/*     */ 
/* 177 */     if ((needRunOnStart()) && (GreedContext.isStrategyAllowed()) && (!GreedContext.isContest())) {
/* 178 */       runStrategyOnStart();
/* 179 */       GreedContext.setConfig("jnlp.run.strategy.on.start", Boolean.valueOf(false));
/*     */     }
/*     */ 
/* 182 */     if (getClientSettingsStorage().isUserFirstLoading()) {
/* 183 */       ChartSettings.resetOptions();
/* 184 */       ThemeManager.setDefaultTheme();
/* 185 */       SwingUtilities.invokeLater(new Runnable(workspaceJTree) {
/*     */         public void run() {
/* 187 */           String[] defaultInstrumentList = JForexWorkspaceHelper.this.getClientSettingsStorage().getDefaultInstrumentList();
/* 188 */           JForexWorkspaceHelper.this.createDefaultCharts(this.val$workspaceJTree, defaultInstrumentList);
/*     */         }
/*     */       });
/* 191 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 193 */           JForexWorkspaceHelper.this.initObligatoryButtomPanels();
/*     */         } } );
/*     */     }
/*     */     else {
/* 198 */       List themes = getClientSettingsStorage().loadThemes();
/* 199 */       for (ITheme theme : themes) {
/* 200 */         ThemeManager.add(theme);
/*     */       }
/*     */ 
/* 203 */       ThemeManager.setTheme(getClientSettingsStorage().restoreSelectedTheme());
/*     */ 
/* 206 */       restoreData(workspaceJTree, this.jForexClientFormLayoutManager);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initObligatoryButtomPanels() {
/* 211 */     this.bottomTabsAndFramesTabbedPane.addFrame(BottomTabsAndFramePanel.create(0, getExposurePanel(), BottomTabsAndFramePanel.TabsTypes.FIXED), LocalizationManager.getText(getPositionsSummaryLabelKey()), false, true);
/*     */ 
/* 219 */     this.bottomTabsAndFramesTabbedPane.addFrame(BottomTabsAndFramePanel.create(1, getPositionsPanel(), BottomTabsAndFramePanel.TabsTypes.FIXED), LocalizationManager.getText(getPositionsLabelKey()), false, true);
/*     */ 
/* 227 */     this.bottomTabsAndFramesTabbedPane.addFrame(BottomTabsAndFramePanel.create(2, getOrdersPanel(), BottomTabsAndFramePanel.TabsTypes.FIXED), LocalizationManager.getText("tab.orders"), false, true);
/*     */ 
/* 235 */     this.jForexClientFormLayoutManager.addMessagesPanel(3, getMessagePanel(), LocalizationManager.getText("tab.messages"), false, true, false, true, true);
/*     */ 
/* 242 */     if (!GreedContext.isContest())
/*     */     {
/* 244 */       this.jForexClientFormLayoutManager.addStrategiesPanel(false, true);
/*     */     }
/*     */ 
/* 247 */     getClientSettingsStorage().save(new BottomPanelBean(0));
/* 248 */     getClientSettingsStorage().save(new BottomPanelBean(1));
/* 249 */     getClientSettingsStorage().save(new BottomPanelBean(2));
/*     */   }
/*     */ 
/*     */   private void restoreData(WorkspaceJTree workspaceJTree, JForexClientFormLayoutManager jForexClientFormLayoutManager) {
/*     */     try {
/* 254 */       loadCustomIndicators(workspaceJTree);
/*     */     } catch (Exception e) {
/* 256 */       LOGGER.warn("Failed to load custom indicators", e);
/*     */     }
/*     */     try {
/* 259 */       IndicatorsProvider.getInstance().registerIndicatorsFromPrefs();
/*     */     } catch (Exception e) {
/* 261 */       LOGGER.warn("Failed to register indicators from prefs", e);
/*     */     }
/*     */     try {
/* 264 */       isExpanded = getClientSettingsStorage().isFramesExpandedOf(getClientSettingsStorage().getChartsNode());
/* 265 */       List chartBeans = getClientSettingsStorage().getChartBeans();
/* 266 */       if (!chartBeans.isEmpty()) {
/* 267 */         mainFramePreferencesNode = getClientSettingsStorage().getMainFramePreferencesNode();
/* 268 */         for (ChartBean chartBean : chartBeans)
/* 269 */           if (InstrumentAvailabilityManager.getInstance().isAllowed(chartBean.getInstrument()))
/*     */           {
/* 271 */             ChartTreeNode loadedChartNode = this.workspaceNodeFactory.createChartTreeNodeFrom(chartBean);
/* 272 */             int index = -1;
/*     */             try {
/* 274 */               index = workspaceJTree.addChartNode(loadedChartNode);
/* 275 */               workspaceJTree.selectNode(loadedChartNode);
/*     */             } catch (Exception e) {
/* 277 */               LOGGER.error("Failed to add instrument node to the workspace tree: " + loadedChartNode.getChartPanelId());
/* 278 */             }continue;
/*     */             try
/*     */             {
/* 281 */               this.mainTabsAndFramesController.addChart(chartBean, getClientSettingsStorage().isFrameUndocked(mainFramePreferencesNode, Integer.valueOf(loadedChartNode.getChartPanelId())), isExpanded);
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 287 */               LOGGER.error("Failed to add chart with id: " + loadedChartNode.getChartPanelId());
/* 288 */               workspaceJTree.removeInstrumentNode(index, loadedChartNode);
/* 289 */             }continue;
/*     */           }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       boolean isExpanded;
/*     */       Preferences mainFramePreferencesNode;
/* 295 */       LOGGER.warn("Failed to load charts", e);
/*     */     }
/* 297 */     boolean isBottomPanelsExpanded = true;
/*     */     try {
/* 299 */       List bottomPanelBeans = getClientSettingsStorage().getBottomPanelBeans();
/* 300 */       if (bottomPanelBeans.isEmpty())
/* 301 */         initObligatoryButtomPanels();
/*     */       else
/* 303 */         isBottomPanelsExpanded = loadBottomPanels(jForexClientFormLayoutManager, bottomPanelBeans);
/*     */     }
/*     */     catch (Exception e) {
/* 306 */       LOGGER.warn("Failed to load bottom panels", e);
/*     */     }
/*     */     try {
/* 309 */       this.mainTabsAndFramesController.restoreState();
/*     */     } catch (Exception e) {
/* 311 */       LOGGER.warn("Failed to restore state of main panels", e);
/*     */     }
/*     */     try {
/* 314 */       Preferences bottomFramesPreferencesNode = getClientSettingsStorage().getBottomFramePreferencesNode();
/* 315 */       FramesState bottomFramesState = getClientSettingsStorage().getFramesStateOf(bottomFramesPreferencesNode);
/* 316 */       DockedUndockedFrame selectedFrame = this.bottomTabsAndFramesTabbedPane.restoreState(getClientSettingsStorage(), bottomFramesState, isBottomPanelsExpanded, bottomFramesPreferencesNode);
/* 317 */       SwingUtilities.invokeLater(new Runnable(selectedFrame) {
/*     */         public void run() {
/* 319 */           if (this.val$selectedFrame != null) {
/* 320 */             JForexWorkspaceHelper.this.bottomTabsAndFramesTabbedPane.selectPanel(this.val$selectedFrame.getPanelId());
/*     */           } else {
/* 322 */             JForexWorkspaceHelper.this.bottomTabsAndFramesTabbedPane.selectPanel(0);
/* 323 */             JForexWorkspaceHelper.this.bottomTabsAndFramesTabbedPane.maximizePanel(0);
/*     */           }
/*     */         } } );
/*     */     } catch (Exception e) {
/* 328 */       LOGGER.warn("Failed to restore state of bottom panels", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean loadBottomPanels(JForexClientFormLayoutManager jForexClientFormLayoutManager, List<BottomPanelBean> bottomPanelBeans)
/*     */   {
/* 335 */     boolean isExpanded = true;
/* 336 */     if (getClientSettingsStorage().bottomPanelFramesPreferencesNodeExists()) {
/* 337 */       Preferences bottomPreferencesNode = getClientSettingsStorage().getBottomFramePreferencesNode();
/* 338 */       isExpanded = getClientSettingsStorage().isFramesExpandedOf(bottomPreferencesNode);
/*     */     }
/* 340 */     Preferences bottomPreferencesNode = getClientSettingsStorage().getBottomFramePreferencesNode();
/* 341 */     for (BottomPanelBean bottomPanelBean : bottomPanelBeans) {
/* 342 */       boolean isUndocked = getClientSettingsStorage().isFrameUndocked(bottomPreferencesNode, Integer.valueOf(bottomPanelBean.getPanelId()));
/*     */ 
/* 344 */       int panelId = bottomPanelBean.getPanelId();
/* 345 */       switch (panelId) {
/*     */       case 0:
/* 347 */         jForexClientFormLayoutManager.addExposurePanel(isUndocked, isExpanded);
/* 348 */         break;
/*     */       case 1:
/* 350 */         jForexClientFormLayoutManager.addPositionsPanel(isUndocked, isExpanded);
/* 351 */         break;
/*     */       case 2:
/* 353 */         jForexClientFormLayoutManager.addOrdersPanel(isUndocked, isExpanded);
/* 354 */         break;
/*     */       case 3:
/* 356 */         jForexClientFormLayoutManager.addMessagesPanel(isUndocked, isExpanded);
/* 357 */         break;
/*     */       case 6:
/* 359 */         jForexClientFormLayoutManager.addNewsPanel(isUndocked, isExpanded);
/* 360 */         break;
/*     */       case 7:
/* 362 */         jForexClientFormLayoutManager.addCalendarPanel(isUndocked, isExpanded);
/* 363 */         break;
/*     */       case 8:
/* 367 */         break;
/*     */       case 9:
/* 369 */         jForexClientFormLayoutManager.addAlerterPanel(isUndocked, isExpanded);
/* 370 */         break;
/*     */       case 5:
/* 372 */         if (GreedContext.isContest()) break;
/* 373 */         jForexClientFormLayoutManager.addStrategiesPanel(isUndocked, isExpanded); break;
/*     */       case 10000:
/* 376 */         HistoricalDataManagerPanel historicalDataManagerPanel = jForexClientFormLayoutManager.addHistoricalDataManagerPanel(isUndocked, isExpanded);
/* 377 */         HistoricalDataManagerBean historicalDataManagerBean = getClientSettingsStorage().getHistoricalDataManagerBean();
/* 378 */         historicalDataManagerPanel.restoreSettings(historicalDataManagerBean);
/* 379 */         break;
/*     */       case 4:
/* 384 */         StrategyTestPanel testPanel = jForexClientFormLayoutManager.addStrategyTesterPanel(-1, isUndocked, isExpanded);
/* 385 */         getClientSettingsStorage().remove(new BottomPanelBean(4));
/* 386 */         StrategyTestBean testBean = getClientSettingsStorage().getStrategyTestBean(-1);
/* 387 */         if (testBean == null)
/*     */           break;
/* 389 */         testPanel.set(testBean); break;
/*     */       default:
/* 395 */         StrategyTestPanel panel = jForexClientFormLayoutManager.addStrategyTesterPanel(panelId, isUndocked, isExpanded);
/*     */ 
/* 397 */         IdManager.getInstance().reserveChartId(panelId);
/*     */ 
/* 399 */         StrategyTestBean bean = getClientSettingsStorage().getStrategyTestBean(panelId);
/* 400 */         if (bean == null)
/*     */           break;
/* 402 */         panel.set(bean);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 408 */     return isExpanded;
/*     */   }
/*     */ 
/*     */   private void createDefaultCharts(WorkspaceJTree workspaceJTree, String[] defaultInstrumentList) {
/* 412 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/*     */ 
/* 415 */     for (String defaultInstrumentName : defaultInstrumentList) {
/* 416 */       Instrument defaultInstrument = Instrument.fromString(defaultInstrumentName);
/*     */       ITheme theme;
/*     */       OhlcChartObject ohlc;
/*     */       List chartObjects;
/*     */       IndicatorWrapper sma;
/* 417 */       switch (10.$SwitchMap$com$dukascopy$api$Instrument[defaultInstrument.ordinal()]) {
/*     */       case 1:
/* 419 */         JForexPeriod jForexPeriod = new JForexPeriod(DataType.TICKS, Period.TICK, null, null, null);
/* 420 */         ChartTreeNode eurUsd = this.workspaceNodeFactory.createChartTreeNode(Instrument.EURUSD, OfferSide.BID, jForexPeriod);
/* 421 */         workspaceJTree.addChartNode(eurUsd);
/* 422 */         this.mainTabsAndFramesController.addChart(eurUsd.getChartPanelId(), eurUsd.getInstrument(), eurUsd.getJForexPeriod(), eurUsd.getOfferSide(), false, false);
/* 423 */         getClientSettingsStorage().save(new ChartBean(eurUsd.getChartPanelId(), eurUsd.getInstrument(), eurUsd.getJForexPeriod(), eurUsd.getOfferSide()));
/* 424 */         ddsChartsController.setTheme(eurUsd.getChartPanelId(), "Green and Red on White");
/* 425 */         ddsChartsController.removeAllDrawings(eurUsd.getChartPanelId());
/*     */ 
/* 427 */         theme = ThemeManager.getTheme("Green and Red on White");
/* 428 */         ohlc = new OhlcChartObject();
/* 429 */         ohlc.setColor(theme.getColor(ITheme.ChartElement.OHLC));
/* 430 */         ohlc.setFillColor(theme.getColor(ITheme.ChartElement.OHLC_BACKGROUND));
/* 431 */         ohlc.setHeaderVisible(false);
/* 432 */         ohlc.setParamVisibility(IOhlcChartObject.TickInfoParams.DATE, false);
/* 433 */         ohlc.setParamVisibility(IOhlcChartObject.TickInfoParams.TIME, false);
/* 434 */         ohlc.setPreferredSize(new Dimension(300, 50));
/*     */ 
/* 436 */         chartObjects = new ArrayList();
/* 437 */         chartObjects.add(ohlc);
/* 438 */         ddsChartsController.add(Integer.valueOf(eurUsd.getChartPanelId()), chartObjects);
/* 439 */         break;
/*     */       case 2:
/* 441 */         ChartTreeNode usdChf = this.workspaceNodeFactory.createChartTreeNode(Instrument.USDCHF, OfferSide.BID, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.FIFTEEN_MINS, null, null, null));
/* 442 */         workspaceJTree.addChartNode(usdChf);
/* 443 */         this.mainTabsAndFramesController.addChart(usdChf.getChartPanelId(), usdChf.getInstrument(), usdChf.getJForexPeriod(), usdChf.getOfferSide(), false, false);
/* 444 */         getClientSettingsStorage().save(new ChartBean(usdChf.getChartPanelId(), usdChf.getInstrument(), usdChf.getJForexPeriod(), usdChf.getOfferSide()));
/* 445 */         ddsChartsController.changeLineType(Integer.valueOf(usdChf.getChartPanelId()), DataType.DataPresentationType.BAR);
/* 446 */         ddsChartsController.setTheme(usdChf.getChartPanelId(), "Blue and Grey on White");
/* 447 */         ddsChartsController.removeAllDrawings(usdChf.getChartPanelId());
/*     */ 
/* 449 */         theme = ThemeManager.getTheme("Blue and Grey on White");
/*     */ 
/* 451 */         IndicatorWrapper cci = new IndicatorWrapper("CCI");
/* 452 */         cci.setOptParam(0, Integer.valueOf(14));
/* 453 */         cci.setOutputColor(0, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 454 */         cci.setOutputColor2(0, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 455 */         for (LevelInfo level : cci.getLevelInfoList()) {
/* 456 */           level.setOpacityAlpha(0.3F);
/*     */         }
/* 458 */         ddsChartsController.addIndicator(Integer.valueOf(usdChf.getChartPanelId()), cci);
/*     */ 
/* 460 */         IndicatorWrapper bbands = new IndicatorWrapper("BBANDS");
/* 461 */         bbands.setOptParam(0, Integer.valueOf(20));
/* 462 */         bbands.setOutputColor(0, Color.DARK_GRAY.darker());
/* 463 */         bbands.setOutputColor2(0, Color.DARK_GRAY.darker());
/* 464 */         bbands.setOptParam(1, Double.valueOf(2.0D));
/* 465 */         bbands.setOutputColor(1, theme.getColor(ITheme.ChartElement.LINE_UP));
/* 466 */         bbands.setOutputColor2(1, theme.getColor(ITheme.ChartElement.LINE_UP));
/* 467 */         bbands.setOptParam(2, Double.valueOf(2.0D));
/* 468 */         bbands.setOutputColor(2, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 469 */         bbands.setOutputColor2(2, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 470 */         ddsChartsController.addIndicator(Integer.valueOf(usdChf.getChartPanelId()), bbands);
/*     */ 
/* 472 */         ohlc = new OhlcChartObject();
/* 473 */         ohlc.setColor(theme.getColor(ITheme.ChartElement.OHLC));
/* 474 */         ohlc.setFillColor(theme.getColor(ITheme.ChartElement.OHLC_BACKGROUND));
/* 475 */         ohlc.setHeaderVisible(false);
/* 476 */         ohlc.setParamVisibility(IOhlcChartObject.CandleInfoParams.INDEX, false);
/*     */ 
/* 479 */         chartObjects = new ArrayList();
/* 480 */         chartObjects.add(ohlc);
/* 481 */         ddsChartsController.add(Integer.valueOf(usdChf.getChartPanelId()), chartObjects);
/* 482 */         break;
/*     */       case 3:
/* 484 */         ChartTreeNode gbpUsd = this.workspaceNodeFactory.createChartTreeNode(Instrument.GBPUSD, OfferSide.BID, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.THIRTY_MINS, null, null, null));
/* 485 */         workspaceJTree.addChartNode(gbpUsd);
/* 486 */         this.mainTabsAndFramesController.addChart(gbpUsd.getChartPanelId(), gbpUsd.getInstrument(), gbpUsd.getJForexPeriod(), gbpUsd.getOfferSide(), false, false);
/* 487 */         getClientSettingsStorage().save(new ChartBean(gbpUsd.getChartPanelId(), gbpUsd.getInstrument(), gbpUsd.getJForexPeriod(), gbpUsd.getOfferSide()));
/* 488 */         ddsChartsController.changeLineType(Integer.valueOf(gbpUsd.getChartPanelId()), DataType.DataPresentationType.CANDLE);
/* 489 */         ddsChartsController.removeAllDrawings(gbpUsd.getChartPanelId());
/* 490 */         ddsChartsController.setTheme(gbpUsd.getChartPanelId(), "Blue and Grey on Grey");
/*     */ 
/* 492 */         theme = ThemeManager.getTheme("Blue and Grey on Grey");
/*     */ 
/* 494 */         sma = new IndicatorWrapper("SMA");
/* 495 */         sma.setOptParam(0, Integer.valueOf(14));
/* 496 */         sma.setOutputColor(0, theme.getColor(ITheme.ChartElement.LINE_UP));
/* 497 */         sma.setOutputColor2(0, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/*     */ 
/* 499 */         IndicatorWrapper vol = new IndicatorWrapper("VOLUME");
/* 500 */         vol.setOutputColor(0, theme.getColor(ITheme.ChartElement.CANDLE_BEAR));
/* 501 */         ddsChartsController.addIndicator(Integer.valueOf(gbpUsd.getChartPanelId()), sma);
/* 502 */         ddsChartsController.addIndicator(Integer.valueOf(gbpUsd.getChartPanelId()), vol);
/*     */ 
/* 504 */         IndicatorWrapper fractal = new IndicatorWrapper("FRACTAL");
/* 505 */         fractal.setOptParam(0, Integer.valueOf(12));
/* 506 */         fractal.setOutputColor(0, Color.RED);
/* 507 */         ddsChartsController.addIndicator(Integer.valueOf(gbpUsd.getChartPanelId()), fractal);
/*     */ 
/* 509 */         OhlcChartObject gbpOhlc = new OhlcChartObject();
/* 510 */         gbpOhlc.setColor(theme.getColor(ITheme.ChartElement.OHLC));
/* 511 */         gbpOhlc.setFillColor(theme.getColor(ITheme.ChartElement.OHLC_BACKGROUND));
/* 512 */         gbpOhlc.setHeaderVisible(false);
/* 513 */         gbpOhlc.setParamVisibility(IOhlcChartObject.CandleInfoParams.INDEX, false);
/*     */ 
/* 516 */         chartObjects = new ArrayList();
/* 517 */         chartObjects.add(gbpOhlc);
/* 518 */         ddsChartsController.add(Integer.valueOf(gbpUsd.getChartPanelId()), chartObjects);
/* 519 */         break;
/*     */       case 4:
/* 521 */         ChartTreeNode usdJpy = this.workspaceNodeFactory.createChartTreeNode(Instrument.USDJPY, OfferSide.BID, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.ONE_HOUR, null, null, null));
/* 522 */         workspaceJTree.addChartNode(usdJpy);
/* 523 */         this.mainTabsAndFramesController.addChart(usdJpy.getChartPanelId(), usdJpy.getInstrument(), usdJpy.getJForexPeriod(), usdJpy.getOfferSide(), false, false);
/* 524 */         getClientSettingsStorage().save(new ChartBean(usdJpy.getChartPanelId(), usdJpy.getInstrument(), usdJpy.getJForexPeriod(), usdJpy.getOfferSide()));
/* 525 */         ddsChartsController.changeLineType(Integer.valueOf(usdJpy.getChartPanelId()), DataType.DataPresentationType.LINE);
/* 526 */         ddsChartsController.removeAllDrawings(usdJpy.getChartPanelId());
/* 527 */         ddsChartsController.setTheme(usdJpy.getChartPanelId(), "Orange and Grey on Grey");
/*     */ 
/* 529 */         theme = ThemeManager.getTheme("Orange and Grey on Grey");
/*     */ 
/* 531 */         sma = new IndicatorWrapper("SMA");
/* 532 */         sma.setOptParam(0, Integer.valueOf(30));
/* 533 */         sma.setOutputColor(0, Color.GRAY);
/* 534 */         sma.setOutputColor2(0, Color.GRAY);
/* 535 */         ddsChartsController.addIndicator(Integer.valueOf(usdJpy.getChartPanelId()), sma);
/*     */ 
/* 537 */         sma = new IndicatorWrapper("SMA");
/* 538 */         sma.setOptParam(0, Integer.valueOf(200));
/* 539 */         sma.setOutputColor(0, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 540 */         sma.setOutputColor2(0, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 541 */         ddsChartsController.addIndicator(Integer.valueOf(usdJpy.getChartPanelId()), sma);
/*     */ 
/* 543 */         IndicatorWrapper stoch = new IndicatorWrapper("STOCH");
/* 544 */         stoch.setOutputColor(0, theme.getColor(ITheme.ChartElement.LINE_UP));
/* 545 */         stoch.setOutputColor2(0, theme.getColor(ITheme.ChartElement.LINE_UP));
/* 546 */         stoch.setOutputColor(1, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 547 */         stoch.setOutputColor2(1, theme.getColor(ITheme.ChartElement.LINE_DOWN));
/* 548 */         for (LevelInfo level : stoch.getLevelInfoList()) {
/* 549 */           level.setOpacityAlpha(0.3F);
/*     */         }
/* 551 */         ddsChartsController.addIndicator(Integer.valueOf(usdJpy.getChartPanelId()), stoch);
/*     */ 
/* 554 */         ohlc = new OhlcChartObject();
/* 555 */         ohlc.setColor(theme.getColor(ITheme.ChartElement.OHLC));
/* 556 */         ohlc.setFillColor(theme.getColor(ITheme.ChartElement.OHLC_BACKGROUND));
/* 557 */         ohlc.setHeaderVisible(false);
/* 558 */         ohlc.setParamVisibility(IOhlcChartObject.CandleInfoParams.INDEX, false);
/*     */ 
/* 561 */         chartObjects = new ArrayList();
/* 562 */         chartObjects.add(ohlc);
/* 563 */         ddsChartsController.add(Integer.valueOf(usdJpy.getChartPanelId()), chartObjects);
/* 564 */         break;
/*     */       default:
/* 566 */         ChartTreeNode defaultChartTreeNode = this.workspaceNodeFactory.createChartTreeNode(defaultInstrument, OfferSide.BID, new JForexPeriod(DataType.TICKS, Period.TICK, null, null, null));
/* 567 */         workspaceJTree.addChartNode(defaultChartTreeNode);
/* 568 */         this.mainTabsAndFramesController.addChart(defaultChartTreeNode.getChartPanelId(), defaultChartTreeNode.getInstrument(), defaultChartTreeNode.getJForexPeriod(), defaultChartTreeNode.getOfferSide(), false, false);
/* 569 */         getClientSettingsStorage().save(new ChartBean(defaultChartTreeNode.getChartPanelId(), defaultChartTreeNode.getInstrument(), defaultChartTreeNode.getJForexPeriod(), defaultChartTreeNode.getOfferSide()));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean needRunOnStart()
/*     */   {
/* 576 */     if (GreedContext.getConfig("jnlp.run.strategy.on.start") == null) {
/* 577 */       return false;
/*     */     }
/* 579 */     return ((Boolean)GreedContext.getConfig("jnlp.run.strategy.on.start")).booleanValue();
/*     */   }
/*     */ 
/*     */   private void runStrategyOnStart() {
/* 583 */     this.jForexClientFormLayoutManager.getStrategiesPanel().checkStrategiesRunOnStart();
/*     */   }
/*     */ 
/*     */   public void checkDependantCurrenciesAndAddThemIfNecessary(WorkspaceJTree workspaceJTree, String orderInstrument) {
/* 587 */     Set instrumentsToBeAdded = OrderUtils.fetchCurrenciesNeededForProfitlossCalculation(orderInstrument);
/* 588 */     instrumentsToBeAdded.add(orderInstrument);
/*     */ 
/* 590 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/* 591 */     List existingInstrumentNames = workspacePanel.getInstruments();
/* 592 */     instrumentsToBeAdded.removeAll(existingInstrumentNames);
/*     */ 
/* 594 */     if (instrumentsToBeAdded.isEmpty()) {
/* 595 */       return;
/*     */     }
/*     */ 
/* 598 */     Set mergedInstruments = new HashSet();
/* 599 */     mergedInstruments.addAll(existingInstrumentNames);
/* 600 */     mergedInstruments.addAll(instrumentsToBeAdded);
/*     */ 
/* 602 */     subscribeToInstruments(mergedInstruments);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSubscribedInstruments() {
/* 606 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/* 607 */     List existingInstrumentNames = workspacePanel.getInstruments();
/*     */ 
/* 609 */     Set instruments = new HashSet();
/* 610 */     if (!ObjectUtils.isNullOrEmpty(existingInstrumentNames)) {
/* 611 */       for (String instrument : existingInstrumentNames) {
/* 612 */         instruments.add(Instrument.fromString(instrument));
/*     */       }
/*     */     }
/* 615 */     return instruments;
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getUnsubscribedInstruments() {
/* 619 */     Set unsubInstruments = new HashSet();
/* 620 */     for (Instrument instr : getAvailableInstrumentsAsArray()) {
/* 621 */       if ((instr != null) && (!isInstrumentSubscribed(instr)))
/* 622 */         unsubInstruments.add(instr);
/*     */     }
/* 624 */     return unsubInstruments;
/*     */   }
/*     */ 
/*     */   public void findAndsubscribeToCurrenciesForProfitLossCalculation() {
/* 628 */     Set currencies = findCurrenciesForProfitLossCalculation();
/* 629 */     subscribeToInstruments(currencies);
/*     */   }
/*     */ 
/*     */   private Set<String> findCurrenciesForProfitLossCalculation() {
/* 633 */     Set currencies = getStoredCurrencies();
/*     */ 
/* 635 */     Set tempSet = new HashSet();
/* 636 */     for (String currencyName : currencies) {
/* 637 */       Set currenciesToBeAdded = OrderUtils.fetchCurrenciesNeededForProfitlossCalculation(currencyName);
/* 638 */       tempSet.addAll(currenciesToBeAdded);
/*     */     }
/* 640 */     currencies.addAll(tempSet);
/*     */ 
/* 642 */     return currencies;
/*     */   }
/*     */ 
/*     */   public void refreshDealPanel(Instrument selectedInstrument) {
/* 646 */     if (selectedInstrument == null) {
/* 647 */       return;
/*     */     }
/*     */ 
/* 650 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */ 
/* 652 */     DealPanel dealPanel = clientForm.getDealPanel();
/* 653 */     OrderEntryPanel orderEntryPanel = dealPanel.getOrderEntryPanel();
/*     */ 
/* 655 */     if ((orderEntryPanel.getInstrument() != null) && (orderEntryPanel.getInstrument().equals(selectedInstrument.toString()))) {
/* 656 */       return;
/*     */     }
/* 658 */     orderEntryPanel.clearEverything(false);
/*     */ 
/* 660 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/*     */ 
/* 663 */     if ((workspacePanel instanceof TickerPanel)) ((TickerPanel)workspacePanel).clearSelection();
/*     */ 
/* 665 */     orderEntryPanel.setInstrument(selectedInstrument.toString());
/* 666 */     InstrumentStatusUpdateMessage instrumentState = getMarketView().getInstrumentState(selectedInstrument.toString());
/* 667 */     int tradable = null == instrumentState ? 1 : instrumentState.getTradable();
/* 668 */     orderEntryPanel.setSubmitEnabled(tradable);
/* 669 */     orderEntryPanel.setDefaultStopConditionLabels();
/*     */ 
/* 671 */     if (tradable == 0) {
/* 672 */       FullDepthInstrumentSubscribeAction action = new FullDepthInstrumentSubscribeAction(this);
/* 673 */       GreedContext.publishEvent(action);
/*     */     }
/*     */ 
/* 676 */     CurrencyMarketWrapper lastMarketState = getMarketView().getLastMarketState(selectedInstrument.toString());
/* 677 */     if (lastMarketState == null) {
/* 678 */       return;
/*     */     }
/* 680 */     orderEntryPanel.onMarketState(lastMarketState);
/* 681 */     dealPanel.onMarketState(lastMarketState);
/*     */   }
/*     */ 
/*     */   Set<String> getStoredCurrencies() {
/* 685 */     List restoredInstruments = getClientSettingsStorage().restoreSelectedInstruments();
/* 686 */     return new HashSet(restoredInstruments);
/*     */   }
/*     */ 
/*     */   void loadStrategies(WorkspaceJTree workspaceJTree)
/*     */   {
/* 695 */     if (GreedContext.isContest()) {
/* 696 */       return;
/*     */     }
/*     */ 
/* 700 */     this.threads.add(new RemoteStrategyMonitor());
/*     */   }
/*     */ 
/*     */   void loadCustomIndicators(WorkspaceJTree workspaceJTree) {
/* 704 */     List customIndicatorBeans = getClientSettingsStorage().getCustomIndicatorBeans();
/* 705 */     if (customIndicatorBeans.isEmpty()) {
/* 706 */       return;
/*     */     }
/*     */ 
/* 709 */     Preferences mainFramePreferencesNode = getClientSettingsStorage().getMainFramePreferencesNode();
/* 710 */     for (CustomIndicatorBean customIndicatorBean : customIndicatorBeans) {
/* 711 */       CustIndTreeNode customIndicatorTreeNode = this.workspaceNodeFactory.createCustIndTreeNode(customIndicatorBean);
/* 712 */       if (customIndicatorTreeNode == null) {
/* 713 */         getClientSettingsStorage().remove(customIndicatorBean);
/* 714 */         continue;
/*     */       }
/* 716 */       CustIndicatorWrapper customIndicatorWrapper = customIndicatorTreeNode.getServiceWrapper();
/* 717 */       if (customIndicatorBean.isEditable()) {
/* 718 */         this.mainTabsAndFramesController.addServiceSourceEditor(customIndicatorBean.getId().intValue(), customIndicatorWrapper.getName(), customIndicatorWrapper.getSourceFile(), ServiceSourceType.INDICATOR, getClientSettingsStorage().isFrameUndocked(mainFramePreferencesNode, customIndicatorBean.getId()));
/*     */       }
/*     */ 
/* 726 */       workspaceJTree.addCustIndTreeNode(customIndicatorTreeNode);
/*     */     }
/*     */ 
/* 729 */     if (getClientSettingsStorage().isIndicatorsExpanded())
/* 730 */       this.jForexClientFormLayoutManager.getTreeActionFactory().createAction(TreeActionType.EXPAND_INDICATORS, workspaceJTree).execute(null);
/*     */   }
/*     */ 
/*     */   public Instrument getSelectedInstrument(TreeSelectionEvent event)
/*     */   {
/* 737 */     WorkspaceTreeNode selectedWorkspaceNode = (WorkspaceTreeNode)event.getPath().getLastPathComponent();
/* 738 */     if ((selectedWorkspaceNode instanceof ChartTreeNode)) {
/* 739 */       ChartTreeNode selectedInstrumentNode = (ChartTreeNode)selectedWorkspaceNode;
/* 740 */       return selectedInstrumentNode.getInstrument();
/* 741 */     }if ((selectedWorkspaceNode instanceof CurrencyTreeNode)) {
/* 742 */       CurrencyTreeNode selectedInstrumentNode = (CurrencyTreeNode)selectedWorkspaceNode;
/* 743 */       return selectedInstrumentNode.getInstrument();
/* 744 */     }if ((selectedWorkspaceNode instanceof IndicatorTreeNode)) {
/* 745 */       ChartTreeNode chartTreeNode = (ChartTreeNode)selectedWorkspaceNode.getParent();
/* 746 */       return chartTreeNode.getInstrument();
/* 747 */     }if ((selectedWorkspaceNode instanceof DrawingTreeNode)) {
/* 748 */       ChartTreeNode chartTreeNode = (ChartTreeNode)selectedWorkspaceNode.getParent();
/* 749 */       return chartTreeNode.getInstrument();
/*     */     }
/* 751 */     return null;
/*     */   }
/*     */ 
/*     */   public int calculateNextNodeIndexToBeFocused(int nodeToBeRemovedIndex)
/*     */   {
/* 757 */     return nodeToBeRemovedIndex + 1;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 762 */     for (Thread thread : this.threads) {
/*     */       try {
/* 764 */         if (thread.isAlive())
/* 765 */           thread.interrupt();
/*     */       }
/*     */       catch (Exception ex) {
/* 768 */         LOGGER.error("Error while interrupting thread : " + thread, ex);
/*     */       }
/*     */     }
/* 771 */     this.threads.clear();
/*     */   }
/*     */ 
/*     */   public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException
/*     */   {
/* 776 */     StrategyWrapper strategyWrapper = new StrategyWrapper();
/* 777 */     if (jfxFile.getName().endsWith(".jfx"))
/* 778 */       strategyWrapper.setBinaryFile(jfxFile);
/*     */     else {
/* 780 */       throw new JFException("File [" + jfxFile + "] is not a .jfx file");
/*     */     }
/* 782 */     if (!jfxFile.exists()) {
/* 783 */       throw new JFException("File [" + jfxFile + "] does not exist");
/*     */     }
/* 785 */     StrategyTreeNode[] strategyTreeNodeVar = new StrategyTreeNode[1];
/*     */     try {
/* 787 */       SwingUtilities.invokeAndWait(new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/*     */         }
/*     */ 
/*     */       });
/*     */       try
/*     */       {
/* 797 */         SwingUtilities.invokeAndWait(new Runnable(strategyTreeNodeVar)
/*     */         {
/*     */           public void run() {
/* 800 */             JForexWorkspaceHelper.this.workspaceNodeFactory.getWorkspaceJTree().getModel().nodeChanged(this.val$strategyTreeNodeVar[0]);
/*     */           }
/*     */         });
/* 803 */         long strategyId = Strategies.get().startStrategyFromStrategy(jfxFile, createStrategyListener(strategyTreeNodeVar[0]), configurables, fullAccess, listener);
/*     */ 
/* 808 */         if (strategyId != 0L)
/* 809 */           SwingUtilities.invokeAndWait(new Runnable(strategyTreeNodeVar)
/*     */           {
/*     */             public void run() {
/* 812 */               JForexWorkspaceHelper.this.workspaceNodeFactory.getWorkspaceJTree().getModel().nodeChanged(this.val$strategyTreeNodeVar[0]);
/*     */             }
/*     */           });
/* 816 */         return strategyId;
/*     */       } catch (JFException e) {
/* 818 */         LOGGER.debug("Error starting strategy.", e);
/* 819 */         SwingUtilities.invokeAndWait(new Runnable(strategyTreeNodeVar)
/*     */         {
/*     */           public void run() {
/* 822 */             JForexWorkspaceHelper.this.workspaceNodeFactory.getWorkspaceJTree().getModel().nodeChanged(this.val$strategyTreeNodeVar[0]);
/*     */           }
/*     */         });
/* 825 */         throw e;
/*     */       } catch (RuntimeException e) {
/* 827 */         LOGGER.debug("Error starting strategy.", e);
/* 828 */         SwingUtilities.invokeAndWait(new Runnable(strategyTreeNodeVar)
/*     */         {
/*     */           public void run() {
/* 831 */             JForexWorkspaceHelper.this.workspaceNodeFactory.getWorkspaceJTree().getModel().nodeChanged(this.val$strategyTreeNodeVar[0]);
/*     */           }
/*     */         });
/* 834 */         throw new JFException(e);
/*     */       }
/*     */     } catch (Exception e) {
/* 837 */       LOGGER.error(e.getMessage(), e);
/* 838 */     }throw new JFException(e);
/*     */   }
/*     */ 
/*     */   private StrategyListener createStrategyListener(StrategyTreeNode strategyTreeNode)
/*     */   {
/* 843 */     return new StrategyListener(strategyTreeNode) {
/*     */       public void strategyStarted(long processId) {
/* 845 */         SwingUtilities.invokeLater(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 850 */             JForexWorkspaceHelper.this.workspaceNodeFactory.getWorkspaceJTree().getModel().nodeChanged(JForexWorkspaceHelper.9.this.val$strategyTreeNode);
/* 851 */             JForexWorkspaceHelper.this.mainTabsAndFramesController.handle(IEventHandler.Event.STRATEGY_STATE_CHANGED, null);
/*     */           } } );
/*     */       }
/*     */ 
/*     */       public void strategyStopped(long processId) {
/* 857 */         SwingUtilities.invokeLater(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 861 */             JForexWorkspaceHelper.this.workspaceNodeFactory.getWorkspaceJTree().getModel().nodeChanged(JForexWorkspaceHelper.9.this.val$strategyTreeNode);
/* 862 */             JForexWorkspaceHelper.this.mainTabsAndFramesController.handle(IEventHandler.Event.STRATEGY_STATE_CHANGED, null);
/*     */           }
/*     */         });
/*     */       }
/*     */ 
/*     */       public void strategyStartingFailed()
/*     */       {
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess) throws JFException {
/* 876 */     throw new JFException("Not implemented yet");
/*     */   }
/*     */ 
/*     */   public void populateWorkspace()
/*     */   {
/* 881 */     ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree().populateWorkspaceAndSubscribeToCurrenciesForProfitLossCalculation();
/*     */   }
/*     */ 
/*     */   public void showChart(Instrument instr)
/*     */   {
/* 887 */     ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTreeActionFactory().createAction(TreeActionType.ADD_CHART, ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree()).execute(new Object[] { ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree().getWorkspaceRoot(), instr });
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.helpers.JForexWorkspaceHelper
 * JD-Core Version:    0.6.0
 */