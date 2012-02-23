/*     */ package com.dukascopy.dds2.greed.gui;
/*     */ 
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartTabsAndFramesPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.DowJonesCalendarPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.news.DowJonesNewsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.export.historicaldata.HistoricalDataManagerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.menu.MainMenu;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.RowSorter;
/*     */ 
/*     */ public abstract class ClientFormLayoutManager
/*     */ {
/*  49 */   public final int DIVIDER_SIZE = 10;
/*     */ 
/*  51 */   public final double DEFAULT_TOP = 0.3D;
/*  52 */   public final double DEFAULT_BOTTOM = 0.7D;
/*  53 */   public final double DEFAULT_SPLIT = 0.5D;
/*  54 */   public final double DEFAULT_JFOREX_SPLIT = 0.8D;
/*     */   protected JPanel content;
/*     */   protected JPanel desktop;
/*     */   protected JPanel body;
/*     */   protected GreedStatusBar statusBar;
/*     */   protected DealPanel dealPanel;
/*     */   protected JPanel rightPanel;
/*     */   protected ExpandableSplitPane splitPane;
/*     */   protected ExposurePanel exposurePanel;
/*     */   protected PositionsPanel positionsPanel;
/*     */   protected MessagePanel messagePanel;
/*     */   protected OrdersPanel ordersPanel;
/*     */   protected DowJonesNewsPanel newsPanel;
/*     */   protected DowJonesCalendarPanel calendarPanel;
/*     */   protected HistoricalDataManagerPanel historicalDataManagerPanel;
/*     */   protected IWorkspaceHelper workspaceHelper;
/*     */   private TabsAndFramesTabbedPane tabsAndFramesTabbedPane;
/*     */   protected ChartTabsAndFramesPanel chartTabsAndFramesPanel;
/*     */   protected ChartTabsAndFramesController chartTabsAndFramesController;
/*     */   protected MainMenu mainMenu;
/*     */ 
/*     */   public static ClientFormLayoutManager getLayoutManager()
/*     */   {
/*  42 */     if (GreedContext.isStrategyAllowed()) {
/*  43 */       return new JForexClientFormLayoutManager();
/*     */     }
/*  45 */     return new CommonClientFormLayoutManager();
/*     */   }
/*     */ 
/*     */   public void build()
/*     */   {
/*  83 */     createComponents();
/*  84 */     setDependencies();
/*  85 */     initComponents();
/*  86 */     initListeners();
/*  87 */     placeSpecificComponents();
/*     */   }
/*     */ 
/*     */   protected void setDependencies() {
/*  91 */     setSpecificDependencies();
/*  92 */     setCommonDependencies();
/*     */   }
/*     */ 
/*     */   protected abstract void setSpecificDependencies();
/*     */ 
/*     */   private void setCommonDependencies() {
/*     */   }
/*     */ 
/*     */   private void initComponents() {
/* 102 */     initSpecificComponents();
/* 103 */     initCommonComponents();
/*     */   }
/*     */   protected abstract void initSpecificComponents();
/*     */ 
/*     */   private void initCommonComponents() {
/* 109 */     this.dealPanel.build();
/* 110 */     this.positionsPanel.build();
/* 111 */     this.exposurePanel.build();
/* 112 */     this.ordersPanel.build();
/* 113 */     this.messagePanel.build();
/* 114 */     this.historicalDataManagerPanel.build();
/*     */   }
/*     */ 
/*     */   private void initListeners() {
/* 118 */     initCommonListeners();
/* 119 */     initSpecificListeners();
/*     */   }
/*     */ 
/*     */   private void initCommonListeners() {
/* 123 */     this.tabsAndFramesTabbedPane.addFrameListener(this.chartTabsAndFramesPanel);
/*     */   }
/*     */   protected abstract void initSpecificListeners();
/*     */ 
/*     */   private void createComponents() {
/* 129 */     createCommonComponents();
/* 130 */     createSpecificComponents();
/*     */   }
/*     */ 
/*     */   private void createCommonComponents() {
/* 134 */     this.tabsAndFramesTabbedPane = new TabsAndFramesTabbedPane();
/* 135 */     this.chartTabsAndFramesPanel = new ChartTabsAndFramesPanel(this.tabsAndFramesTabbedPane);
/*     */ 
/* 137 */     this.content = new JPanel();
/* 138 */     this.desktop = new JPanel();
/*     */ 
/* 140 */     this.body = new JPanel();
/* 141 */     this.statusBar = new GreedStatusBar();
/*     */ 
/* 143 */     this.dealPanel = new DealPanel();
/* 144 */     this.rightPanel = new JPanel();
/*     */ 
/* 146 */     this.positionsPanel = new PositionsPanel();
/* 147 */     this.ordersPanel = new OrdersPanel();
/* 148 */     this.exposurePanel = new ExposurePanel();
/* 149 */     this.messagePanel = new MessagePanel(true);
/* 150 */     this.newsPanel = new DowJonesNewsPanel();
/* 151 */     this.calendarPanel = new DowJonesCalendarPanel();
/* 152 */     this.historicalDataManagerPanel = new HistoricalDataManagerPanel();
/*     */ 
/* 154 */     this.chartTabsAndFramesController = new ChartTabsAndFramesController(this.tabsAndFramesTabbedPane);
/*     */ 
/* 156 */     this.mainMenu = new MainMenu(); } 
/*     */   protected abstract void createSpecificComponents();
/*     */ 
/*     */   protected abstract void placeSpecificComponents();
/*     */ 
/*     */   protected abstract void resetlayout();
/*     */ 
/* 164 */   public void setGuiElements(ClientForm clientForm) { clientForm.desktop = this.desktop;
/* 165 */     clientForm.dealPanel = this.dealPanel;
/* 166 */     clientForm.positionsPanel = this.positionsPanel;
/* 167 */     clientForm.ordersPanel = this.ordersPanel;
/* 168 */     clientForm.newsPanel = this.newsPanel;
/* 169 */     clientForm.calendarPanel = this.calendarPanel;
/* 170 */     clientForm.exposurePanel = this.exposurePanel;
/* 171 */     clientForm.messagePanel = this.messagePanel;
/* 172 */     clientForm.statusBar = this.statusBar;
/* 173 */     clientForm.content = this.content;
/* 174 */     clientForm.mainMenu = this.mainMenu; }
/*     */ 
/*     */   public void saveClientSettings(ClientSettingsStorage settingsSaver)
/*     */   {
/* 178 */     saveSpecificClientSettings(settingsSaver);
/* 179 */     saveCommonClientSettings(settingsSaver);
/*     */   }
/*     */   protected abstract void saveSpecificClientSettings(ClientSettingsStorage paramClientSettingsStorage);
/*     */ 
/*     */   private void saveCommonClientSettings(ClientSettingsStorage settingsStorage) {
/* 185 */     MarketOverviewFrame dock = (MarketOverviewFrame)GreedContext.get("Dock");
/* 186 */     settingsStorage.saveSpotAtMarket(dock.isVisible());
/* 187 */     settingsStorage.saveOneClickState(this.statusBar.getAccountStatement().getOneClickCheckbox().isSelected());
/*     */ 
/* 189 */     settingsStorage.save((ClientForm)GreedContext.get("clientGui"));
/* 190 */     settingsStorage.save(this.dealPanel);
/*     */ 
/* 192 */     settingsStorage.removeAllThemes();
/* 193 */     for (ITheme theme : ThemeManager.getThemes()) {
/* 194 */       if (!ThemeManager.isDefault(theme.getName())) {
/* 195 */         settingsStorage.save(theme);
/*     */       }
/*     */     }
/* 198 */     settingsStorage.saveSelectedTheme(ThemeManager.getTheme());
/*     */ 
/* 205 */     DDSChartsController ddsChartController = (DDSChartsController)GreedContext.get("chartsController");
/* 206 */     for (Integer chartId : ddsChartController.getChartControllerIdies())
/*     */     {
/* 209 */       ChartPanel chartPanel = getChartTabsController().getChartPanelByPanelId(chartId.intValue());
/*     */ 
/* 213 */       if ((chartPanel == null) || (chartPanel.isShouldSaveSettings()))
/*     */       {
/* 215 */         ChartBean chartBean = ddsChartController.synchronizeAndGetChartBean(chartId);
/* 216 */         settingsStorage.save(chartBean);
/*     */       }
/*     */     }
/*     */ 
/* 220 */     settingsStorage.saveSystemProperties();
/*     */ 
/* 222 */     settingsStorage.saveTableSortKeys(this.positionsPanel.getTable().getTableId(), this.positionsPanel.getTable().getRowSorter().getSortKeys());
/* 223 */     settingsStorage.saveTableSortKeys(this.ordersPanel.getOrdersTable().getTableId(), ((TableSorter)this.ordersPanel.getOrdersTable().getModel()).getSortKeys());
/* 224 */     settingsStorage.saveTableSortKeys(this.exposurePanel.getTable().getTableId(), this.exposurePanel.getTable().getRowSorter().getSortKeys());
/*     */ 
/* 226 */     settingsStorage.saveTableColumns(this.positionsPanel.getTable().getTableId(), this.positionsPanel.getTable().getColumnModel());
/* 227 */     settingsStorage.saveTableColumns(this.ordersPanel.getOrdersTable().getTableId(), this.ordersPanel.getOrdersTable().getColumnModel());
/* 228 */     settingsStorage.saveTableColumns(this.exposurePanel.getTable().getTableId(), this.exposurePanel.getTable().getColumnModel());
/*     */ 
/* 230 */     settingsStorage.saveLastSelectedInstrument();
/*     */   }
/*     */ 
/*     */   protected abstract void resizeSplitters(ClientSettingsStorage paramClientSettingsStorage);
/*     */ 
/*     */   public ExpandableSplitPane getSplitPane()
/*     */   {
/* 244 */     return this.splitPane;
/*     */   }
/*     */ 
/*     */   public IChartTabsAndFramesController getChartTabsController() {
/* 248 */     return this.chartTabsAndFramesController;
/*     */   }
/*     */ 
/*     */   public JPanel getChartTabsAndFramesPanel() {
/* 252 */     return this.chartTabsAndFramesPanel;
/*     */   }
/*     */ 
/*     */   public IWorkspaceHelper getWorkspaceHelper() {
/* 256 */     return this.workspaceHelper;
/*     */   }
/*     */ 
/*     */   public HistoricalDataManagerPanel getHistoricalDataManagerPanel() {
/* 260 */     return this.historicalDataManagerPanel;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 267 */     this.tabsAndFramesTabbedPane.closeAll();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.ClientFormLayoutManager
 * JD-Core Version:    0.6.0
 */