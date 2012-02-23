/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.OhlcChartObject;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.EditorFactory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.EditorRegistry;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.FileChangeListener;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.buttons.ButtonPanelActionListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.ChartToolBarDDSChartsActionListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler.Event;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.TabsAndTreeDDSChartsActionListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.ButtonType;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.Container;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class ChartTabsAndFramesController
/*     */   implements IChartTabsAndFramesController
/*     */ {
/*     */   private final TabsAndFramesTabbedPane tabsAndFramesTabbedPane;
/*  57 */   private final Map<Integer, IEventHandler> eventHandlers = new HashMap();
/*     */   private WorkspaceTreeController workspaceController;
/*     */ 
/*     */   public ChartTabsAndFramesController(TabsAndFramesTabbedPane tabsAndFramesTabbedPane)
/*     */   {
/*  61 */     this.tabsAndFramesTabbedPane = tabsAndFramesTabbedPane;
/*     */   }
/*     */ 
/*     */   public void setWorkspaceController(WorkspaceTreeController workspaceController) {
/*  65 */     this.workspaceController = workspaceController;
/*     */   }
/*     */ 
/*     */   public WorkspaceTreeController getWorkspaceController() {
/*  69 */     return this.workspaceController;
/*     */   }
/*     */ 
/*     */   public void addChart(int chartPanelId, Instrument instrument, JForexPeriod period, OfferSide offerSide, boolean isUndocked, boolean isExpanded)
/*     */   {
/*  76 */     ChartBean chartBean = new ChartBean(chartPanelId, instrument, period, offerSide);
/*  77 */     chartBean.setNeedCreateDefaultChartObjects(true);
/*  78 */     chartBean.setReadOnly(GreedContext.isReadOnly());
/*  79 */     addChart(chartBean, isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   public void addChart(ChartBean chartBean, boolean isUndocked, boolean isExpanded)
/*     */   {
/*  84 */     addChart(chartBean, null, chartBean.isHistoricalTesterChart(), isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   public void addOrSelectInstumentTesterChart(int chartPanelId, String toolTip, JForexPeriod jForexPeriod, Instrument instrument, OfferSide offerSide, IFeedDataProvider feedDataProvider) {
/*  88 */     if (selectPanel(chartPanelId)) {
/*  89 */       return;
/*     */     }
/*     */ 
/*  92 */     ChartBean chartBean = new ChartBean(chartPanelId, instrument, jForexPeriod, offerSide);
/*  93 */     chartBean.setHistoricalTesterChart(true);
/*  94 */     chartBean.setReadOnly(GreedContext.isReadOnly());
/*  95 */     chartBean.setFeedDataProvider(feedDataProvider);
/*     */ 
/*  97 */     addChart(chartBean, toolTip, true, false, true);
/*     */   }
/*     */ 
/*     */   public void addOrSelectInstumentTesterChart(String toolTip, ChartBean chartBean, IFeedDataProvider feedDataProvider)
/*     */   {
/* 102 */     if (selectPanel(chartBean.getId())) {
/* 103 */       return;
/*     */     }
/*     */ 
/* 107 */     chartBean.setHistoricalTesterChart(true);
/* 108 */     chartBean.setReadOnly(GreedContext.isReadOnly());
/* 109 */     chartBean.setFeedDataProvider(feedDataProvider);
/*     */ 
/* 111 */     addChart(chartBean, toolTip, true, false, true);
/*     */   }
/*     */ 
/*     */   public void addServiceSourceEditor(int panelId, String name, File strategySrc, ServiceSourceType serviceSourceType, boolean isUndocked) {
/* 115 */     addServiceSourceEditor(panelId, name, strategySrc, serviceSourceType, isUndocked, true);
/*     */   }
/*     */ 
/*     */   public void addServiceSourceEditor(int panelId, String name, File strategySrc, ServiceSourceType serviceSourceType, boolean isUndocked, boolean isNewFile) {
/*     */     try {
/* 120 */       ServiceSourceEditorPanel sourceEditorPanel = new ServiceSourceEditorPanel(panelId, strategySrc, serviceSourceType);
/* 121 */       sourceEditorPanel.build();
/* 122 */       sourceEditorPanel.addFileChangeListener(new FileChangeListener(panelId) {
/*     */         public void fileChanged(File file) {
/* 124 */           ChartTabsAndFramesController.this.workspaceController.renameServiceByName(this.val$panelId, file);
/* 125 */           ChartTabsAndFramesController.this.tabsAndFramesTabbedPane.setTitleForPanelId(this.val$panelId, file.getName());
/*     */         }
/*     */       });
/* 129 */       sourceEditorPanel.setIsNewFile(isNewFile);
/* 130 */       this.tabsAndFramesTabbedPane.addFrame(sourceEditorPanel, name, isUndocked, false);
/*     */     } catch (IOException ex) {
/* 132 */       JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), "Cannot open file: " + ex.getMessage(), "Error", 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void openServiceSourceEditor(int panelId, File newContentSrcFile)
/*     */   {
/* 138 */     StrategyWrapper strategyWrapper = new StrategyWrapper();
/* 139 */     strategyWrapper.setSourceFile(newContentSrcFile);
/*     */ 
/* 141 */     if (this.tabsAndFramesTabbedPane.getEditorPanel(strategyWrapper) != null)
/* 142 */       this.tabsAndFramesTabbedPane.selectPanel(this.tabsAndFramesTabbedPane.getEditorPanel(strategyWrapper).getPanelId());
/*     */     else
/*     */       try {
/* 145 */         ServiceSourceEditorPanel sourceEditorPanel = new ServiceSourceEditorPanel(panelId, newContentSrcFile, ServiceSourceType.STRATEGY);
/* 146 */         sourceEditorPanel.build();
/* 147 */         sourceEditorPanel.addFileChangeListener(new FileChangeListener(panelId) {
/*     */           public void fileChanged(File file) {
/* 149 */             ChartTabsAndFramesController.this.workspaceController.renameServiceByName(this.val$panelId, file);
/* 150 */             ChartTabsAndFramesController.this.tabsAndFramesTabbedPane.setTitleForPanelId(this.val$panelId, file.getName());
/*     */           }
/*     */         });
/* 154 */         this.tabsAndFramesTabbedPane.reloadEditorFrame(panelId, sourceEditorPanel);
/* 155 */         this.workspaceController.renameServiceByName(panelId, newContentSrcFile);
/* 156 */         setTabTitle(panelId, strategyWrapper.getName());
/*     */       } catch (IOException ex) {
/* 158 */         JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), "Cannot open file: " + ex.getMessage(), "Error", 0);
/*     */       }
/*     */   }
/*     */ 
/*     */   public JPanel createOrGetCustomMainTab(String title, Integer panelId)
/*     */   {
/* 164 */     TabsAndFramePanel customMainTab = this.tabsAndFramesTabbedPane.getFrameContent(panelId.intValue());
/* 165 */     if (customMainTab != null) {
/* 166 */       return customMainTab;
/*     */     }
/* 168 */     customMainTab = new MainCustomTab(panelId);
/* 169 */     this.tabsAndFramesTabbedPane.addFrame(customMainTab, title, false, false);
/* 170 */     return customMainTab;
/*     */   }
/*     */ 
/*     */   public boolean selectPanel(int panelId)
/*     */   {
/* 175 */     return this.tabsAndFramesTabbedPane.selectPanel(panelId);
/*     */   }
/*     */ 
/*     */   public boolean selectServiceSourceEditor(int editorId) {
/* 179 */     Editor editor = EditorFactory.getRegistry().getEditor(editorId);
/* 180 */     if (editor == null) {
/* 181 */       return false;
/*     */     }
/* 183 */     editor.focus();
/*     */ 
/* 185 */     Container container = SwingUtilities.getAncestorOfClass(ServiceSourceEditorPanel.class, editor.getGUIComponent());
/*     */ 
/* 187 */     if (container == null) {
/* 188 */       return false;
/*     */     }
/*     */ 
/* 191 */     ServiceSourceEditorPanel sourceEditorPanel = (ServiceSourceEditorPanel)container;
/* 192 */     Container root = sourceEditorPanel.getTopLevelAncestor();
/*     */ 
/* 194 */     if (root == null) {
/* 195 */       return false;
/*     */     }
/*     */ 
/* 198 */     if ((root instanceof UndockedJFrame))
/* 199 */       root.setVisible(true);
/*     */     else {
/* 201 */       this.tabsAndFramesTabbedPane.syncTabAndFrameSelection(sourceEditorPanel.getPanelId());
/*     */     }
/* 203 */     return true;
/*     */   }
/*     */ 
/*     */   public void selectLineNumber(int panelId, int lineNumber) {
/* 207 */     ServiceWrapper serviceWrapper = this.workspaceController.getServiceWrapperById(panelId);
/* 208 */     if (!serviceWrapper.isEditable()) {
/* 209 */       return;
/*     */     }
/* 211 */     TabsAndFramePanel tabsAndFramePanel = this.tabsAndFramesTabbedPane.getFrameContent(panelId);
/* 212 */     if (tabsAndFramePanel == null) {
/* 213 */       addServiceSourceEditor(panelId, serviceWrapper.getName(), serviceWrapper.getSourceFile(), ServiceSourceType.STRATEGY, false);
/*     */     }
/* 215 */     tabsAndFramePanel = this.tabsAndFramesTabbedPane.getFrameContent(panelId);
/* 216 */     if (!(tabsAndFramePanel instanceof ServiceSourceEditorPanel)) {
/* 217 */       return;
/*     */     }
/* 219 */     this.tabsAndFramesTabbedPane.syncTabAndFrameSelection(panelId);
/* 220 */     ServiceSourceEditorPanel editorPanel = (ServiceSourceEditorPanel)tabsAndFramePanel;
/* 221 */     editorPanel.getEditor().focus();
/* 222 */     editorPanel.getEditor().selectLine(lineNumber);
/*     */   }
/*     */ 
/*     */   public void closeChart(int panelId)
/*     */   {
/* 228 */     this.tabsAndFramesTabbedPane.closeFrame(panelId);
/* 229 */     ((DDSChartsController)GreedContext.get("chartsController")).removeChart(Integer.valueOf(panelId));
/* 230 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).remove(Integer.valueOf(panelId));
/*     */   }
/*     */ 
/*     */   public void closeServiceEditor(int editorId) {
/* 234 */     EditorRegistry editorRegistry = EditorFactory.getRegistry();
/*     */ 
/* 236 */     Editor editor = editorRegistry.getEditor(editorId);
/* 237 */     if (editor == null) {
/* 238 */       return;
/*     */     }
/*     */ 
/* 241 */     Container container = SwingUtilities.getAncestorOfClass(ServiceSourceEditorPanel.class, editor.getGUIComponent());
/*     */ 
/* 243 */     if (container == null) {
/* 244 */       return;
/*     */     }
/*     */ 
/* 247 */     ServiceSourceEditorPanel editorPanel = (ServiceSourceEditorPanel)container;
/* 248 */     this.tabsAndFramesTabbedPane.closeFrame(editorPanel.getPanelId());
/* 249 */     editorPanel.removeEditors();
/*     */   }
/*     */ 
/*     */   public void removeCustomMainTab(String key, Integer panelId) {
/* 253 */     this.tabsAndFramesTabbedPane.closeTabAndInternalFrame(panelId.intValue());
/*     */   }
/*     */ 
/*     */   public IChart getIChartBy(Instrument instrument) {
/* 257 */     int chartPanelId = this.tabsAndFramesTabbedPane.getFirstChartPanelIdFor(instrument);
/* 258 */     return ((DDSChartsController)GreedContext.get("chartsController")).getIChartBy(Integer.valueOf(chartPanelId));
/*     */   }
/*     */ 
/*     */   public ChartPanel getChartPanelByPanelId(int panelId) {
/* 262 */     DockedUndockedFrame frame = this.tabsAndFramesTabbedPane.getPanelByPanelId(panelId);
/* 263 */     if ((frame != null) && ((frame.getContent() instanceof ChartPanel))) {
/* 264 */       return (ChartPanel)frame.getContent();
/*     */     }
/* 266 */     return null;
/*     */   }
/*     */ 
/*     */   public ServiceSourceEditorPanel getEditorPanelByPanelId(int panelId)
/*     */   {
/* 271 */     DockedUndockedFrame frame = this.tabsAndFramesTabbedPane.getPanelByPanelId(panelId);
/* 272 */     if ((frame != null) && ((frame.getContent() instanceof ServiceSourceEditorPanel))) {
/* 273 */       return (ServiceSourceEditorPanel)frame.getContent();
/*     */     }
/* 275 */     return null;
/*     */   }
/*     */ 
/*     */   public ServiceSourceEditorPanel getEditorPanel(ServiceWrapper service)
/*     */   {
/* 280 */     return this.tabsAndFramesTabbedPane.getEditorPanel(service);
/*     */   }
/*     */ 
/*     */   public void setTabTitle(int panelId, String title) {
/* 284 */     this.tabsAndFramesTabbedPane.setTitleForPanelId(panelId, title);
/*     */   }
/*     */ 
/*     */   public void updatePeriod(int chartPanelId, Period newPeriod) {
/* 288 */     this.tabsAndFramesTabbedPane.updatePeriod(chartPanelId, newPeriod);
/*     */   }
/*     */ 
/*     */   public void changePeriodForChartPanel(Integer panelId, JForexPeriod period) {
/* 292 */     TabsAndFramePanel panel = this.tabsAndFramesTabbedPane.getFrameContent(panelId.intValue());
/* 293 */     if (!(panel instanceof ChartPanel)) {
/* 294 */       return;
/*     */     }
/* 296 */     ChartPanel content = (ChartPanel)panel;
/* 297 */     content.changePeriod(period);
/*     */   }
/*     */ 
/*     */   public void addFrameListener(FrameListener listener) {
/* 301 */     this.tabsAndFramesTabbedPane.addFrameListener(listener);
/*     */   }
/*     */ 
/*     */   public void removeEventHandlerFor(Integer panelId) {
/* 305 */     this.eventHandlers.remove(panelId);
/*     */   }
/*     */ 
/*     */   public void closeAll() {
/* 309 */     this.tabsAndFramesTabbedPane.closeAll();
/*     */   }
/*     */ 
/*     */   public void saveState() {
/* 313 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 314 */     Preferences chartsNode = clientSettingsStorage.getChartsNode();
/* 315 */     Preferences mainPreferencesNode = clientSettingsStorage.getMainFramePreferencesNode();
/* 316 */     this.tabsAndFramesTabbedPane.saveState(clientSettingsStorage, chartsNode, mainPreferencesNode);
/*     */   }
/*     */ 
/*     */   public void restoreState() {
/* 320 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 321 */     FramesState framesState = clientSettingsStorage.getFramesStateOf(clientSettingsStorage.getChartsNode());
/* 322 */     boolean isFramesExpanded = clientSettingsStorage.isFramesExpandedOf(clientSettingsStorage.getChartsNode());
/* 323 */     Preferences mainFramePreferencesNode = clientSettingsStorage.getMainFramePreferencesNode();
/* 324 */     DockedUndockedFrame selectedFrame = this.tabsAndFramesTabbedPane.restoreState(clientSettingsStorage, framesState, isFramesExpanded, mainFramePreferencesNode);
/* 325 */     SwingUtilities.invokeLater(new Runnable(selectedFrame) {
/*     */       public void run() {
/* 327 */         if (this.val$selectedFrame != null)
/* 328 */           ChartTabsAndFramesController.this.selectPanel(this.val$selectedFrame.getPanelId());
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void populatePopupMenuWithMenuItems(JPopupMenu popupMenu) {
/* 335 */     this.tabsAndFramesTabbedPane.populatePopupMenuWithMenuItems(popupMenu);
/*     */   }
/*     */ 
/*     */   private void addChart(ChartBean chartBean, String toolTipText, boolean dontSaveSettings, boolean isUndocked, boolean isExpanded)
/*     */   {
/* 351 */     int chartId = chartBean.getId();
/* 352 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 354 */     if (chartBean.getStartLoadingDataRunnable() == null) {
/* 355 */       Runnable dataLoadingStarter = new Runnable(chartId, chartBean) {
/*     */         public void run() {
/* 357 */           DDSChartsController chartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 358 */           if (chartsController != null)
/* 359 */             chartsController.startLoadingData(Integer.valueOf(this.val$chartId), this.val$chartBean.getAutoShiftActiveAsBoolean(), this.val$chartBean.getChartShiftInPx());
/*     */         }
/*     */       };
/* 363 */       chartBean.setStartLoadingDataRunnable(dataLoadingStarter);
/*     */     }
/*     */ 
/* 366 */     if ((chartBean.getChartObjects() == null) && 
/* 367 */       (!dontSaveSettings)) {
/* 368 */       List chartObjects = clientSettingsStorage.getDrawingsFor(Integer.valueOf(chartId));
/* 369 */       chartBean.setChartObjects(chartObjects);
/*     */     }
/*     */ 
/* 373 */     if ((chartBean.getIndicatorWrappers() == null) && 
/* 374 */       (!dontSaveSettings)) {
/* 375 */       List indicatorWrappers = clientSettingsStorage.getIndicatorWrappers(chartBean.getId(), false);
/*     */ 
/* 378 */       for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 379 */         List chartObjects = indicatorWrapper.getChartObjects();
/* 380 */         if (chartObjects != null) {
/* 381 */           for (int i = chartObjects.size() - 1; i >= 0; i--) {
/* 382 */             IChartObject chartObject = (IChartObject)chartObjects.get(i);
/* 383 */             if ((!(chartObject instanceof ChartObject)) || 
/* 384 */               (!((ChartObject)chartObject).isGlobal())) continue;
/* 385 */             chartObjects.remove(i);
/*     */ 
/* 387 */             if (chartBean.getChartObjects() != null) {
/* 388 */               chartBean.getChartObjects().add(chartObject);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 396 */       chartBean.setIndicatorWrappers(indicatorWrappers);
/*     */     }
/*     */ 
/* 400 */     if (chartBean.getTheme() == null) {
/* 401 */       chartBean.setTheme(ThemeManager.getTheme().clone());
/*     */     }
/*     */ 
/* 404 */     JPanel chartPanel = createChart(chartBean);
/*     */ 
/* 406 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 407 */     ChartToolBar chartToolBar = new ChartToolBar(chartBean, new ButtonPanelActionListener(Integer.valueOf(chartBean.getId()), ddsChartsController), this.tabsAndFramesTabbedPane);
/*     */ 
/* 412 */     ChartPanel chartAndButtonsPanel = new ChartPanel(chartId, chartBean.getInstrument(), chartToolBar, chartPanel, !dontSaveSettings);
/*     */ 
/* 414 */     ddsChartsController.addEnableDisableListener(chartId, chartToolBar);
/* 415 */     ddsChartsController.addChartModeChangeListener(chartId, chartToolBar);
/* 416 */     ddsChartsController.addChartsActionListener(Integer.valueOf(chartId), new TabsAndTreeDDSChartsActionListener(chartId, this.workspaceController, this));
/* 417 */     ddsChartsController.addChartsActionListener(Integer.valueOf(chartId), new ChartToolBarDDSChartsActionListener(chartId, chartToolBar));
/*     */ 
/* 419 */     if (!dontSaveSettings) {
/* 420 */       ddsChartsController.addChartsActionListener(Integer.valueOf(chartId), new SettingsStorageDDSChartsActionAdapter(Integer.valueOf(chartId), (ClientSettingsStorage)GreedContext.get("settingsStorage")));
/*     */     }
/*     */ 
/* 425 */     applyChartBeanForToolBar(chartBean, chartToolBar);
/*     */ 
/* 427 */     ddsChartsController.add(Integer.valueOf(chartId), chartBean.getChartObjects());
/* 428 */     addDefaultChartObjects(ddsChartsController, chartBean, chartId);
/*     */ 
/* 430 */     ddsChartsController.addIndicators(Integer.valueOf(chartId), chartBean.getIndicatorWrappers());
/*     */ 
/* 432 */     this.eventHandlers.put(Integer.valueOf(chartId), chartToolBar);
/*     */ 
/* 434 */     this.tabsAndFramesTabbedPane.addFrame(chartAndButtonsPanel, "", toolTipText, isUndocked, isExpanded);
/* 435 */     this.tabsAndFramesTabbedPane.updateHeader(chartId, chartBean);
/*     */ 
/* 437 */     ddsChartsController.addProgressListener(chartId, (ProgressListener)chartAndButtonsPanel.getClientProperty("progress"));
/*     */ 
/* 439 */     chartToolBar.applyChartBean(chartBean);
/*     */   }
/*     */ 
/*     */   private void addDefaultChartObjects(DDSChartsController ddsChartsController, ChartBean chartBean, int chartId)
/*     */   {
/* 444 */     if (chartBean.isNeedCreateDefaultChartObjects()) {
/* 445 */       IOhlcChartObject ohlc = new OhlcChartObject();
/* 446 */       ohlc.setColor(ThemeManager.getTheme().getColor(ITheme.ChartElement.OHLC));
/* 447 */       ohlc.setFillColor(ThemeManager.getTheme().getColor(ITheme.ChartElement.OHLC_BACKGROUND));
/* 448 */       ddsChartsController.add(Integer.valueOf(chartId), Collections.singletonList(ohlc));
/*     */     }
/*     */   }
/*     */ 
/*     */   private JPanel createChart(ChartBean chartBean) {
/* 453 */     return ((DDSChartsController)GreedContext.get("chartsController")).createNewChartOrGetById(chartBean);
/*     */   }
/*     */ 
/*     */   private void applyChartBeanForToolBar(ChartBean chartBean, ChartToolBar chartToolBar) {
/* 457 */     if (chartBean.getMouseCursorVisible() == 1) {
/* 458 */       chartToolBar.getButton(ChartToolBar.ButtonType.CURSOR).doClick();
/*     */     }
/* 460 */     if (chartBean.getVerticalMovementEnabled() == 1) {
/* 461 */       chartToolBar.getButton(ChartToolBar.ButtonType.VERTICAL_SHIFT).doClick();
/*     */     }
/* 463 */     if (chartBean.getAutoShiftActiveAsBoolean())
/* 464 */       chartToolBar.getButton(ChartToolBar.ButtonType.AUTOSHIFT).doClick();
/*     */   }
/*     */ 
/*     */   public void handle(IEventHandler.Event event, Object params)
/*     */   {
/* 471 */     for (IEventHandler eventHandler : this.eventHandlers.values())
/* 472 */       eventHandler.handle(event, params);
/*     */   }
/*     */ 
/*     */   public void updateInstrument(int chartPanelId, Instrument instrument)
/*     */   {
/* 478 */     this.tabsAndFramesTabbedPane.updateInstrument(chartPanelId, instrument);
/*     */   }
/*     */ 
/*     */   public void changeInsturmentForChartPanel(Integer panelId, Instrument instrument)
/*     */   {
/* 483 */     TabsAndFramePanel panel = this.tabsAndFramesTabbedPane.getFrameContent(panelId.intValue());
/* 484 */     if (!(panel instanceof ChartPanel)) {
/* 485 */       return;
/*     */     }
/* 487 */     ChartPanel content = (ChartPanel)panel;
/* 488 */     content.changeInstrument(instrument);
/*     */   }
/*     */ 
/*     */   public void updateDataType(int chartPanelId, DataType dataType)
/*     */   {
/* 493 */     this.tabsAndFramesTabbedPane.updateDataType(chartPanelId, dataType);
/*     */   }
/*     */ 
/*     */   public void updatePriceRange(int chartPanelId, PriceRange priceRange)
/*     */   {
/* 498 */     this.tabsAndFramesTabbedPane.updatePriceRange(chartPanelId, priceRange);
/*     */   }
/*     */ 
/*     */   public void updateJForexPeriod(int chartPanelId, JForexPeriod jForexPeriod)
/*     */   {
/* 503 */     this.tabsAndFramesTabbedPane.updateJForexPeriod(chartPanelId, jForexPeriod);
/*     */   }
/*     */ 
/*     */   public void updatePinUnpinBtnState()
/*     */   {
/* 508 */     this.tabsAndFramesTabbedPane.synchTabbedPanesPinBtns();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ChartTabsAndFramesController
 * JD-Core Version:    0.6.0
 */