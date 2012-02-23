/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.listener.ChartModeChangeListener;
/*     */ import com.dukascopy.charts.listener.ChartModeChangeListener.ChartMode;
/*     */ import com.dukascopy.charts.listeners.MainComponentSizeListener;
/*     */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*     */ import com.dukascopy.charts.tablebuilder.ITablePresentationManager;
/*     */ import com.dukascopy.charts.tablebuilder.component.table.DataTablePresentationAbstractJTable;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingsManagerContainer;
/*     */ import com.dukascopy.charts.view.paintingtechnic.PaintingTechnicBuilder;
/*     */ import com.dukascopy.charts.view.swing.AbstractChartWidgetPanel;
/*     */ import com.dukascopy.charts.view.swing.ChartSwingViewBuilder;
/*     */ import java.awt.CardLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLayeredPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class ChartsGuiManager extends DDSChartsActionAdapter
/*     */   implements GuiRefresher, ChartModeChangeListener
/*     */ {
/*  41 */   private static final Logger LOGGER = LoggerFactory.getLogger(ChartsGuiManager.class);
/*     */   private final ChartSwingViewBuilder chartSwingViewBuilder;
/*     */   private final ISwingComponentListenerBuilder swingComponentListenerBuilder;
/*     */   private final PaintingTechnicBuilder paintingTechnicBuilder;
/*     */   private final ChartsLayoutManager chartsLayoutManager;
/*     */   private final ChartState chartState;
/*     */   private final JComponent chartViewContainer;
/*     */   private JComponent mainChartView;
/*     */   private JComponent commonAxisXPanel;
/*  54 */   private final Set<Integer> reservedSubWindowIds = new HashSet();
/*  55 */   private final AtomicInteger subWindowIdGenerator = new AtomicInteger(0);
/*  56 */   private final Map<Integer, Integer> subIndicatorIdToSubWindowIds = new HashMap();
/*  57 */   private final Map<Integer, DivisionPanelAndSubChartView> subChartViews = new HashMap();
/*     */   private final IDrawingsManagerContainer drawingsManagerContainer;
/*     */   private final ITablePresentationManager tickTablePresentationManager;
/*     */   private final ITablePresentationManager candleTablePresentationManager;
/*     */   private final ITablePresentationManager priceRangeTablePresentationManager;
/*     */   private final ITablePresentationManager pointAndFigureTablePresentationManager;
/*     */   private final ITablePresentationManager tickBarTablePresentationManager;
/*     */   private final ITablePresentationManager renkoTablePresentationManager;
/*     */   private CardLayout mainCardLayout;
/*     */   private JPanel mainPanel;
/*     */   private JRootPane chartRootPane;
/*     */   private JPanel tableDataPresentationPanel;
/*     */   private CardLayout tableDataPresentationCardLayout;
/*  76 */   private final String CHART_VIEW_CONTAINER = "MAIN_CHART_VIEW";
/*  77 */   private final String TABLE_DATA_PRESENTATION_VIEW = "TABLE_DATA_PRESENTATION_VIEW";
/*     */ 
/*  79 */   private final String CANDLE_DATA_PRESENTATION_VIEW = "CANDLE_DATA_PRESENTATION_VIEW";
/*  80 */   private final String TICK_DATA_PRESENTATION_VIEW = "TICK_DATA_PRESENTATION_VIEW";
/*  81 */   private final String PRICE_RANGE_DATA_PRESENTATION_VIEW = "PRICE_RANGE_DATA_PRESENTATION_VIEW";
/*  82 */   private final String POINT_AND_FIGURE_DATA_PRESENTATION_VIEW = "POINT_AND_FIGURE_DATA_PRESENTATION_VIEW";
/*  83 */   private final String TICK_BAR_DATA_PRESENTATION_VIEW = "TICK_BAR_DATA_PRESENTATION_VIEW";
/*  84 */   private final String RENKO_DATA_PRESENTATION_VIEW = "RENKO_DATA_PRESENTATION_VIEW";
/*     */   private ChartModeChangeListener.ChartMode currentChartMode;
/*     */ 
/*     */   public ChartsGuiManager(ChartSwingViewBuilder chartSwingViewBuilder, ISwingComponentListenerBuilder swingComponentListenerBuilder, PaintingTechnicBuilder paintingTechnicBuilder, IDrawingsManagerContainer drawingsManagerContainer, ChartState chartState, ITablePresentationManager tickTablePresentationManager, ITablePresentationManager candleTablePresentationManager, ITablePresentationManager priceRangeTablePresentationManager, ITablePresentationManager pointAndFigureTablePresentationManager, ITablePresentationManager tickBarTablePresentationManager, ITablePresentationManager renkoTablePresentationManager)
/*     */   {
/* 101 */     this.chartSwingViewBuilder = chartSwingViewBuilder;
/* 102 */     this.swingComponentListenerBuilder = swingComponentListenerBuilder;
/* 103 */     this.paintingTechnicBuilder = paintingTechnicBuilder;
/*     */ 
/* 105 */     this.drawingsManagerContainer = drawingsManagerContainer;
/*     */ 
/* 107 */     this.chartState = chartState;
/*     */ 
/* 109 */     this.tickTablePresentationManager = tickTablePresentationManager;
/* 110 */     this.candleTablePresentationManager = candleTablePresentationManager;
/* 111 */     this.priceRangeTablePresentationManager = priceRangeTablePresentationManager;
/* 112 */     this.pointAndFigureTablePresentationManager = pointAndFigureTablePresentationManager;
/* 113 */     this.tickBarTablePresentationManager = tickBarTablePresentationManager;
/* 114 */     this.renkoTablePresentationManager = renkoTablePresentationManager;
/*     */ 
/* 116 */     this.chartsLayoutManager = new ChartsLayoutManager();
/*     */ 
/* 118 */     this.chartViewContainer = new JPanel();
/* 119 */     this.chartViewContainer.setName("chartViewContainer");
/* 120 */     this.chartViewContainer.setLayout(this.chartsLayoutManager);
/*     */   }
/*     */ 
/*     */   public void createMainChartView()
/*     */   {
/* 125 */     if (getMainChartView() != null) {
/* 126 */       return;
/*     */     }
/*     */ 
/* 129 */     setMainChartView(this.chartSwingViewBuilder.createMainChartView(this.paintingTechnicBuilder));
/*     */ 
/* 131 */     getMainPanel().add(getChartRootPane(), "MAIN_CHART_VIEW");
/*     */ 
/* 133 */     this.commonAxisXPanel = this.chartSwingViewBuilder.createCommonAxisXPane(this.paintingTechnicBuilder);
/*     */ 
/* 135 */     getMainChartView().getComponent(0).addComponentListener(this.swingComponentListenerBuilder.createFirstResizeListenerToRunTask());
/*     */ 
/* 137 */     MainComponentSizeListener mainComponentSizeListener = this.swingComponentListenerBuilder.createMainComponentListener();
/* 138 */     getMainChartView().getComponent(0).addComponentListener(mainComponentSizeListener);
/* 139 */     getMainChartView().getComponent(0).addHierarchyBoundsListener(mainComponentSizeListener);
/*     */ 
/* 141 */     getChartViewContainer().add(getMainChartView());
/* 142 */     getChartViewContainer().add(this.commonAxisXPanel);
/*     */ 
/* 144 */     switchViewToChartPresentation();
/*     */   }
/*     */ 
/*     */   private JRootPane getChartRootPane() {
/* 148 */     if (this.chartRootPane == null) {
/* 149 */       this.chartRootPane = new JRootPane();
/* 150 */       this.chartRootPane.setName("chartRootPane");
/* 151 */       this.chartRootPane.setContentPane(getChartViewContainer());
/*     */ 
/* 153 */       this.chartRootPane.addComponentListener(new ComponentAdapter()
/*     */       {
/*     */         public void componentResized(ComponentEvent e)
/*     */         {
/* 158 */           Rectangle parentBounds = e.getComponent().getBounds();
/* 159 */           Component[] children = ChartsGuiManager.this.chartRootPane.getLayeredPane().getComponents();
/* 160 */           for (Component child : children) {
/* 161 */             if (!(child instanceof AbstractChartWidgetPanel))
/*     */               continue;
/* 163 */             Point childLocation = child.getLocation();
/* 164 */             Dimension childSize = ((AbstractChartWidgetPanel)child).getChartObjectSize();
/* 165 */             if ((childLocation.x + childSize.width <= parentBounds.width) && (childLocation.y + childSize.height <= parentBounds.height)) {
/*     */               continue;
/*     */             }
/* 168 */             int x = Math.min(childLocation.x, Math.max(0, parentBounds.width - childSize.width));
/* 169 */             int y = Math.min(childLocation.y, Math.max(0, parentBounds.height - childSize.height));
/*     */ 
/* 171 */             ((AbstractChartWidgetPanel)child).setWidgetPosition(x, y);
/*     */           }
/*     */         }
/*     */ 
/*     */       });
/*     */     }
/*     */ 
/* 179 */     return this.chartRootPane;
/*     */   }
/*     */ 
/*     */   public JLayeredPane getChartsLayeredPane() {
/* 183 */     return getChartRootPane().getLayeredPane();
/*     */   }
/*     */ 
/*     */   public Integer getSubChartViewIdFor(int indicatorId)
/*     */   {
/* 189 */     return (Integer)this.subIndicatorIdToSubWindowIds.get(Integer.valueOf(indicatorId));
/*     */   }
/*     */ 
/*     */   public int createSubChartView()
/*     */   {
/* 194 */     int subWindowId = this.subWindowIdGenerator.incrementAndGet();
/* 195 */     while (this.reservedSubWindowIds.contains(Integer.valueOf(subWindowId))) {
/* 196 */       subWindowId = this.subWindowIdGenerator.incrementAndGet();
/*     */     }
/* 198 */     this.reservedSubWindowIds.add(Integer.valueOf(subWindowId));
/* 199 */     createSubChartViewInternal(subWindowId);
/* 200 */     return subWindowId;
/*     */   }
/*     */ 
/*     */   public void createSubChartView(Integer subChartId)
/*     */   {
/* 205 */     this.reservedSubWindowIds.add(subChartId);
/* 206 */     createSubChartViewInternal(subChartId.intValue());
/*     */   }
/*     */ 
/*     */   public void deleteSubChartView(Integer subWindowId)
/*     */   {
/* 211 */     LOGGER.trace("deleting sub chart view: " + subWindowId + ")");
/*     */ 
/* 213 */     this.drawingsManagerContainer.deleteSubDrawingsManagersFor(subWindowId.intValue());
/*     */ 
/* 215 */     DivisionPanelAndSubChartView pair = (DivisionPanelAndSubChartView)this.subChartViews.get(subWindowId);
/* 216 */     if (pair == null) {
/* 217 */       return;
/*     */     }
/*     */ 
/* 220 */     getChartViewContainer().remove(pair.divisionPanel);
/* 221 */     getChartViewContainer().remove(pair.subChartView);
/*     */ 
/* 223 */     this.subChartViews.remove(subWindowId);
/* 224 */     this.paintingTechnicBuilder.deletePaintingTechnicForSubPanel(subWindowId.intValue());
/*     */ 
/* 226 */     this.reservedSubWindowIds.remove(subWindowId);
/*     */ 
/* 228 */     getChartViewContainer().validate();
/*     */   }
/*     */ 
/*     */   public void addSubIndicatorToSubChartView(int subWindowId, IndicatorWrapper indicatorWrapper)
/*     */   {
/* 234 */     LOGGER.trace("adding indicator: " + indicatorWrapper + " to sub chart view: " + subWindowId + ")");
/* 235 */     DivisionPanelAndSubChartView pair = (DivisionPanelAndSubChartView)this.subChartViews.get(Integer.valueOf(subWindowId));
/* 236 */     if (pair == null) {
/* 237 */       return;
/*     */     }
/* 239 */     SubIndicatorGroup subIndicatorGroup = pair.subIndicatorGroup;
/* 240 */     subIndicatorGroup.addIndicator(indicatorWrapper);
/* 241 */     this.swingComponentListenerBuilder.addSubIndicatorToSubChartView(indicatorWrapper, pair.subChartView.getComponent(0).getHeight());
/* 242 */     this.drawingsManagerContainer.createSubDrawingsManagerForIndicator(subWindowId, indicatorWrapper.getId());
/* 243 */     this.subIndicatorIdToSubWindowIds.put(Integer.valueOf(indicatorWrapper.getId()), Integer.valueOf(subWindowId));
/*     */   }
/*     */ 
/*     */   public int deleteSubIndicatorFromSubChartView(int subWindowId, IndicatorWrapper indicatorWrapper)
/*     */   {
/* 248 */     LOGGER.trace("deleting indicator: " + indicatorWrapper + " from sub chart view: " + subWindowId + ")");
/* 249 */     DivisionPanelAndSubChartView chartView = (DivisionPanelAndSubChartView)this.subChartViews.get(Integer.valueOf(subWindowId));
/* 250 */     if (chartView != null) {
/* 251 */       chartView.subIndicatorGroup.subIndicatorDeleted(indicatorWrapper);
/*     */     }
/* 253 */     int subHeight = this.swingComponentListenerBuilder.deleteSubIndicatorFromSubChartView(indicatorWrapper);
/* 254 */     this.drawingsManagerContainer.deleteSubDrawingsManagerForIndicator(subWindowId, indicatorWrapper.getId());
/* 255 */     this.subIndicatorIdToSubWindowIds.remove(Integer.valueOf(indicatorWrapper.getId()));
/* 256 */     return subHeight;
/*     */   }
/*     */ 
/*     */   public void deleteSubChartViewsIfNecessary(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/* 262 */     StringBuffer logMessage = new StringBuffer("deleting sub chart views for indicators: ");
/* 263 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 264 */       logMessage.append(indicatorWrapper.getId()).append(", ");
/*     */     }
/* 266 */     logMessage.append(")");
/* 267 */     LOGGER.trace(logMessage.toString());
/*     */ 
/* 269 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 270 */       Integer subChartId = (Integer)this.subIndicatorIdToSubWindowIds.get(Integer.valueOf(indicatorWrapper.getId()));
/* 271 */       DivisionPanelAndSubChartView pair = (DivisionPanelAndSubChartView)this.subChartViews.get(subChartId);
/* 272 */       if (pair == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 276 */       this.swingComponentListenerBuilder.deleteSubIndicatorFromSubChartView(indicatorWrapper);
/* 277 */       pair.subIndicatorGroup.subIndicatorDeleted(indicatorWrapper);
/*     */ 
/* 279 */       if (pair.subIndicatorGroup.getSubIndicators().isEmpty()) {
/* 280 */         getChartViewContainer().remove(pair.divisionPanel);
/* 281 */         getChartViewContainer().remove(pair.subChartView);
/* 282 */         this.subChartViews.remove(Integer.valueOf(indicatorWrapper.getId()));
/* 283 */         this.paintingTechnicBuilder.deletePaintingTechnicForSubPanel(subChartId.intValue());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 288 */     getChartViewContainer().validate();
/*     */   }
/*     */ 
/*     */   public boolean isIndicatorShownOnSubWindow(int indicatorId)
/*     */   {
/* 293 */     return this.subIndicatorIdToSubWindowIds.containsKey(Integer.valueOf(indicatorId));
/*     */   }
/*     */ 
/*     */   public boolean isSubViewEmpty(Integer subWindowId)
/*     */   {
/* 298 */     DivisionPanelAndSubChartView pair = (DivisionPanelAndSubChartView)this.subChartViews.get(subWindowId);
/* 299 */     if (pair == null) {
/* 300 */       return true;
/*     */     }
/* 302 */     List subIndicators = pair.subIndicatorGroup.getSubIndicators();
/* 303 */     return subIndicators.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean doesSubViewExists(Integer subWindowId)
/*     */   {
/* 308 */     return this.subChartViews.get(subWindowId) != null;
/*     */   }
/*     */ 
/*     */   public JComponent getChartsContainer()
/*     */   {
/* 313 */     return getChartViewContainer();
/*     */   }
/*     */ 
/*     */   public void refreshMainContent()
/*     */   {
/* 321 */     LOGGER.trace("refreshing main content...");
/* 322 */     this.paintingTechnicBuilder.invalidateMainWindowsContent();
/* 323 */     getMainChartView().repaint();
/*     */   }
/*     */ 
/*     */   public void refreshSubContentByIndicatorId(Integer indicatorId)
/*     */   {
/* 328 */     LOGGER.trace("refreshing sub content for: " + indicatorId + "...");
/* 329 */     Integer subChartId = (Integer)this.subIndicatorIdToSubWindowIds.get(indicatorId);
/* 330 */     if (subChartId == null) {
/* 331 */       return;
/*     */     }
/* 333 */     this.paintingTechnicBuilder.invalidateSubWindowsContent(subChartId.intValue());
/* 334 */     DivisionPanelAndSubChartView pair = (DivisionPanelAndSubChartView)this.subChartViews.get(subChartId);
/* 335 */     if (pair == null) {
/* 336 */       return;
/*     */     }
/* 338 */     pair.subChartView.repaint();
/*     */   }
/*     */ 
/*     */   public void refreshSubContentBySubViewId(int subViewId)
/*     */   {
/* 343 */     this.paintingTechnicBuilder.invalidateSubWindowsContent(subViewId);
/* 344 */     DivisionPanelAndSubChartView pair = (DivisionPanelAndSubChartView)this.subChartViews.get(Integer.valueOf(subViewId));
/* 345 */     if (pair == null) {
/* 346 */       return;
/*     */     }
/* 348 */     pair.subChartView.repaint();
/*     */   }
/*     */ 
/*     */   public void refreshSubContents()
/*     */   {
/* 353 */     Set subViewIds = this.subChartViews.keySet();
/* 354 */     for (Integer subViewId : subViewIds)
/* 355 */       refreshSubContentBySubViewId(subViewId.intValue());
/*     */   }
/*     */ 
/*     */   public void refreshAllContent()
/*     */   {
/* 361 */     LOGGER.trace("refreshing all content...");
/* 362 */     this.paintingTechnicBuilder.invalidateAllContent();
/* 363 */     getChartViewContainer().repaint();
/*     */ 
/* 365 */     DataTablePresentationAbstractJTable table = getCurrentChartDataTable();
/* 366 */     if (table != null)
/* 367 */       table.repaint();
/*     */   }
/*     */ 
/*     */   public void repaintMainContent()
/*     */   {
/* 373 */     LOGGER.trace("repainting main content...");
/* 374 */     getChartViewContainer().repaint();
/*     */   }
/*     */ 
/*     */   public void repaintSubContentBySubViewId(int subViewId)
/*     */   {
/* 379 */     LOGGER.trace("repainting sub content " + subViewId + " ...");
/* 380 */     DivisionPanelAndSubChartView pair = (DivisionPanelAndSubChartView)this.subChartViews.get(Integer.valueOf(subViewId));
/* 381 */     if (pair == null) {
/* 382 */       return;
/*     */     }
/* 384 */     pair.subChartView.repaint();
/*     */   }
/*     */ 
/*     */   public void invalidateMainContent()
/*     */   {
/* 389 */     LOGGER.trace("invalidating main content...");
/* 390 */     this.paintingTechnicBuilder.invalidateMainWindowsContent();
/*     */   }
/*     */ 
/*     */   public void invalidateAllContent()
/*     */   {
/* 395 */     LOGGER.trace("invalidating all content...");
/* 396 */     this.paintingTechnicBuilder.invalidateAllContent();
/*     */   }
/*     */ 
/*     */   public void setFocusToMainChartView()
/*     */   {
/* 404 */     getMainChartView().getComponent(0).requestFocus();
/*     */   }
/*     */ 
/*     */   public int getWindowsCount()
/*     */   {
/* 409 */     return this.subChartViews.size() + 1;
/*     */   }
/*     */ 
/*     */   public Integer getBasicIndicatorIdByWindowIndex(int index)
/*     */   {
/* 414 */     if (index == 0) {
/* 415 */       return null;
/*     */     }
/*     */ 
/* 418 */     DivisionPanelAndSubChartView divisionPanelAndSubChartView = (DivisionPanelAndSubChartView)this.subChartViews.get(new Integer(index));
/*     */ 
/* 420 */     if (divisionPanelAndSubChartView != null) {
/* 421 */       IndicatorWrapper basicSubIndicator = divisionPanelAndSubChartView.subIndicatorGroup.getBasicSubIndicator();
/* 422 */       if (basicSubIndicator != null) {
/* 423 */         return new Integer(basicSubIndicator.getId());
/*     */       }
/*     */     }
/*     */ 
/* 427 */     return new Integer(-1);
/*     */   }
/*     */ 
/*     */   void createSubChartViewInternal(int subWindowId)
/*     */   {
/* 432 */     SubIndicatorGroup subIndicatorGroup = new SubIndicatorGroup(subWindowId);
/* 433 */     LOGGER.trace("creating sub chart view: " + subWindowId + ")");
/*     */ 
/* 435 */     this.drawingsManagerContainer.createSubDrawingsManagersMapFor(subWindowId);
/*     */ 
/* 437 */     JComponent subChartView = this.chartSwingViewBuilder.createSubChartView(this.paintingTechnicBuilder, subIndicatorGroup);
/* 438 */     JComponent divisionPanel = this.chartSwingViewBuilder.createDivisionPanel(getChartViewContainer(), getMainChartView());
/* 439 */     getChartViewContainer().add(divisionPanel, getChartViewContainer().getComponentCount() - 1);
/* 440 */     getChartViewContainer().add(subChartView, getChartViewContainer().getComponentCount() - 1);
/*     */ 
/* 442 */     subChartView.addComponentListener(this.swingComponentListenerBuilder.createSubComponentListener(subIndicatorGroup));
/*     */ 
/* 444 */     Dimension prevSize = getMainChartView().getSize();
/* 445 */     getMainChartView().setSize(new Dimension((int)prevSize.getWidth(), (int)(prevSize.getHeight() - divisionPanel.getSize().getHeight() - subChartView.getSize().getHeight())));
/* 446 */     this.subChartViews.put(Integer.valueOf(subWindowId), new DivisionPanelAndSubChartView(divisionPanel, subChartView, subIndicatorGroup));
/* 447 */     getChartViewContainer().validate();
/*     */   }
/*     */ 
/*     */   public boolean isSubChartLast(int subWindowId)
/*     */   {
/* 452 */     JComponent container = getChartViewContainer();
/* 453 */     int count = container.getComponentCount();
/* 454 */     if (count > 1)
/*     */     {
/* 457 */       Component lastComponent = container.getComponent(count - 2);
/*     */ 
/* 459 */       DivisionPanelAndSubChartView subChart = (DivisionPanelAndSubChartView)this.subChartViews.get(Integer.valueOf(subWindowId));
/* 460 */       if ((subChart != null) && (subChart.subChartView == lastComponent)) {
/* 461 */         return true;
/*     */       }
/*     */     }
/* 464 */     return false;
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getTickTablePresentationManager()
/*     */   {
/* 491 */     return this.tickTablePresentationManager;
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getCandleTablePresentationManager()
/*     */   {
/* 496 */     return this.candleTablePresentationManager;
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getPriceRangeTablePresentationManager()
/*     */   {
/* 501 */     return this.priceRangeTablePresentationManager;
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getPointAndFigureTablePresentationManager()
/*     */   {
/* 506 */     return this.pointAndFigureTablePresentationManager;
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getTickBarTablePresentationManager()
/*     */   {
/* 511 */     return this.tickBarTablePresentationManager;
/*     */   }
/*     */ 
/*     */   private void switchViewToChartPresentation() {
/* 515 */     setCurrentChartMode(ChartModeChangeListener.ChartMode.CHART);
/*     */ 
/* 517 */     getTickTablePresentationManager().stop();
/* 518 */     getCandleTablePresentationManager().stop();
/* 519 */     getPriceRangeTablePresentationManager().stop();
/* 520 */     getPointAndFigureTablePresentationManager().stop();
/* 521 */     getTickBarTablePresentationManager().stop();
/* 522 */     getRenkoTablePresentationManager().stop();
/*     */ 
/* 524 */     getMainCardLayout().show(getMainPanel(), "MAIN_CHART_VIEW");
/*     */   }
/*     */ 
/*     */   private void switchViewToCandleTablePresentation() {
/* 528 */     setCurrentChartMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */ 
/* 530 */     getTickTablePresentationManager().stop();
/* 531 */     getCandleTablePresentationManager().start();
/* 532 */     getPriceRangeTablePresentationManager().stop();
/* 533 */     getPointAndFigureTablePresentationManager().stop();
/* 534 */     getTickBarTablePresentationManager().stop();
/* 535 */     getRenkoTablePresentationManager().stop();
/*     */ 
/* 537 */     getTableDataPresentationCardLayout().show(getTableDataPresentationView(), "CANDLE_DATA_PRESENTATION_VIEW");
/* 538 */     getMainCardLayout().show(getMainPanel(), "TABLE_DATA_PRESENTATION_VIEW");
/*     */   }
/*     */ 
/*     */   private void switchViewToTickTablePresentation() {
/* 542 */     setCurrentChartMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */ 
/* 544 */     getTickTablePresentationManager().start();
/* 545 */     getCandleTablePresentationManager().stop();
/* 546 */     getPriceRangeTablePresentationManager().stop();
/* 547 */     getPointAndFigureTablePresentationManager().stop();
/* 548 */     getTickBarTablePresentationManager().stop();
/* 549 */     getRenkoTablePresentationManager().stop();
/*     */ 
/* 551 */     getTableDataPresentationCardLayout().show(getTableDataPresentationView(), "TICK_DATA_PRESENTATION_VIEW");
/* 552 */     getMainCardLayout().show(getMainPanel(), "TABLE_DATA_PRESENTATION_VIEW");
/*     */   }
/*     */ 
/*     */   private void switchViewToPriceRangeTablePresentation() {
/* 556 */     setCurrentChartMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */ 
/* 558 */     getTickTablePresentationManager().stop();
/* 559 */     getCandleTablePresentationManager().stop();
/* 560 */     getPointAndFigureTablePresentationManager().stop();
/* 561 */     getPriceRangeTablePresentationManager().start();
/* 562 */     getTickBarTablePresentationManager().stop();
/* 563 */     getRenkoTablePresentationManager().stop();
/*     */ 
/* 565 */     getTableDataPresentationCardLayout().show(getTableDataPresentationView(), "PRICE_RANGE_DATA_PRESENTATION_VIEW");
/* 566 */     getMainCardLayout().show(getMainPanel(), "TABLE_DATA_PRESENTATION_VIEW");
/*     */   }
/*     */ 
/*     */   private void switchViewToPointAndFigureTablePresentation() {
/* 570 */     setCurrentChartMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */ 
/* 572 */     getTickTablePresentationManager().stop();
/* 573 */     getCandleTablePresentationManager().stop();
/* 574 */     getPriceRangeTablePresentationManager().stop();
/* 575 */     getPointAndFigureTablePresentationManager().start();
/* 576 */     getTickBarTablePresentationManager().stop();
/* 577 */     getRenkoTablePresentationManager().stop();
/*     */ 
/* 579 */     getTableDataPresentationCardLayout().show(getTableDataPresentationView(), "POINT_AND_FIGURE_DATA_PRESENTATION_VIEW");
/* 580 */     getMainCardLayout().show(getMainPanel(), "TABLE_DATA_PRESENTATION_VIEW");
/*     */   }
/*     */ 
/*     */   private void switchViewToTickBarTablePresentation() {
/* 584 */     setCurrentChartMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */ 
/* 586 */     getTickTablePresentationManager().stop();
/* 587 */     getCandleTablePresentationManager().stop();
/* 588 */     getPriceRangeTablePresentationManager().stop();
/* 589 */     getPointAndFigureTablePresentationManager().stop();
/* 590 */     getTickBarTablePresentationManager().start();
/* 591 */     getRenkoTablePresentationManager().stop();
/*     */ 
/* 593 */     getTableDataPresentationCardLayout().show(getTableDataPresentationView(), "TICK_BAR_DATA_PRESENTATION_VIEW");
/* 594 */     getMainCardLayout().show(getMainPanel(), "TABLE_DATA_PRESENTATION_VIEW");
/*     */   }
/*     */ 
/*     */   private void switchViewToRenkoTablePresentation() {
/* 598 */     setCurrentChartMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */ 
/* 600 */     getTickTablePresentationManager().stop();
/* 601 */     getCandleTablePresentationManager().stop();
/* 602 */     getPriceRangeTablePresentationManager().stop();
/* 603 */     getPointAndFigureTablePresentationManager().stop();
/* 604 */     getTickBarTablePresentationManager().stop();
/* 605 */     getRenkoTablePresentationManager().start();
/*     */ 
/* 607 */     getTableDataPresentationCardLayout().show(getTableDataPresentationView(), "RENKO_DATA_PRESENTATION_VIEW");
/* 608 */     getMainCardLayout().show(getMainPanel(), "TABLE_DATA_PRESENTATION_VIEW");
/*     */   }
/*     */ 
/*     */   private CardLayout getMainCardLayout() {
/* 612 */     if (this.mainCardLayout == null) {
/* 613 */       this.mainCardLayout = new CardLayout();
/*     */     }
/* 615 */     return this.mainCardLayout;
/*     */   }
/*     */ 
/*     */   private JComponent getMainChartView() {
/* 619 */     return this.mainChartView;
/*     */   }
/*     */ 
/*     */   private void setMainChartView(JComponent mainChartView) {
/* 623 */     this.mainChartView = mainChartView;
/*     */   }
/*     */ 
/*     */   private JComponent getChartViewContainer() {
/* 627 */     return this.chartViewContainer;
/*     */   }
/*     */ 
/*     */   private JPanel getMainPanel() {
/* 631 */     if (this.mainPanel == null) {
/* 632 */       this.mainPanel = new JPanel(getMainCardLayout());
/* 633 */       this.mainPanel.add(getTableDataPresentationView(), "TABLE_DATA_PRESENTATION_VIEW");
/*     */     }
/* 635 */     return this.mainPanel;
/*     */   }
/*     */ 
/*     */   private JPanel getTableDataPresentationView() {
/* 639 */     if (this.tableDataPresentationPanel == null) {
/* 640 */       this.tableDataPresentationPanel = new JPanel(getTableDataPresentationCardLayout());
/*     */ 
/* 642 */       this.tableDataPresentationPanel.add(getCandleTablePresentationManager().getTablePresentationComponent(), "CANDLE_DATA_PRESENTATION_VIEW");
/* 643 */       this.tableDataPresentationPanel.add(getTickTablePresentationManager().getTablePresentationComponent(), "TICK_DATA_PRESENTATION_VIEW");
/* 644 */       this.tableDataPresentationPanel.add(getPriceRangeTablePresentationManager().getTablePresentationComponent(), "PRICE_RANGE_DATA_PRESENTATION_VIEW");
/* 645 */       this.tableDataPresentationPanel.add(getPointAndFigureTablePresentationManager().getTablePresentationComponent(), "POINT_AND_FIGURE_DATA_PRESENTATION_VIEW");
/* 646 */       this.tableDataPresentationPanel.add(getTickBarTablePresentationManager().getTablePresentationComponent(), "TICK_BAR_DATA_PRESENTATION_VIEW");
/* 647 */       this.tableDataPresentationPanel.add(getRenkoTablePresentationManager().getTablePresentationComponent(), "RENKO_DATA_PRESENTATION_VIEW");
/*     */     }
/* 649 */     return this.tableDataPresentationPanel;
/*     */   }
/*     */ 
/*     */   private CardLayout getTableDataPresentationCardLayout() {
/* 653 */     if (this.tableDataPresentationCardLayout == null) {
/* 654 */       this.tableDataPresentationCardLayout = new CardLayout();
/*     */     }
/* 656 */     return this.tableDataPresentationCardLayout;
/*     */   }
/*     */ 
/*     */   public JComponent getMainContainer()
/*     */   {
/* 661 */     return getMainPanel();
/*     */   }
/*     */ 
/*     */   public void chartModeChanged(ChartModeChangeListener.ChartMode chartMode)
/*     */   {
/* 666 */     switch (chartMode) { case CHART:
/* 667 */       switchToChartMode(); break;
/*     */     case TABLE:
/* 668 */       switchToTableMode(); break;
/*     */     default:
/* 669 */       throw new IllegalArgumentException("Unsupported mode - " + chartMode); }
/*     */   }
/*     */ 
/*     */   private void switchToTableMode()
/*     */   {
/* 674 */     switch (2.$SwitchMap$com$dukascopy$api$DataType[getChartState().getDataType().ordinal()]) {
/*     */     case 1:
/* 676 */       switchViewToTickTablePresentation();
/* 677 */       break;
/*     */     case 2:
/* 680 */       switchViewToCandleTablePresentation();
/* 681 */       break;
/*     */     case 3:
/* 684 */       switchViewToPriceRangeTablePresentation();
/* 685 */       break;
/*     */     case 4:
/* 688 */       switchViewToPointAndFigureTablePresentation();
/* 689 */       break;
/*     */     case 5:
/* 692 */       switchViewToTickBarTablePresentation();
/* 693 */       break;
/*     */     case 6:
/* 696 */       switchViewToRenkoTablePresentation();
/* 697 */       break;
/*     */     default:
/* 698 */       throw new IllegalArgumentException("Unsupported Data Type - " + getChartState().getDataType());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void switchToChartMode() {
/* 703 */     switchViewToChartPresentation();
/*     */   }
/*     */ 
/*     */   private ChartState getChartState() {
/* 707 */     return this.chartState;
/*     */   }
/*     */ 
/*     */   public void periodChanged(Period newPeriod)
/*     */   {
/* 712 */     if (ChartModeChangeListener.ChartMode.TABLE.equals(getCurrentChartMode()))
/* 713 */       switchToTableMode();
/*     */   }
/*     */ 
/*     */   public void dataTypeChanged(DataType newDataType)
/*     */   {
/* 722 */     if (ChartModeChangeListener.ChartMode.TABLE.equals(getCurrentChartMode()))
/* 723 */       switchToTableMode();
/*     */   }
/*     */ 
/*     */   private ChartModeChangeListener.ChartMode getCurrentChartMode()
/*     */   {
/* 728 */     return this.currentChartMode;
/*     */   }
/*     */ 
/*     */   private void setCurrentChartMode(ChartModeChangeListener.ChartMode currentChartMode) {
/* 732 */     this.currentChartMode = currentChartMode;
/*     */   }
/*     */ 
/*     */   public DataTablePresentationAbstractJTable<?, ?> getCurrentChartDataTable()
/*     */   {
/* 737 */     ChartModeChangeListener.ChartMode currrentChartMode = getCurrentChartMode();
/* 738 */     switch (currrentChartMode) {
/*     */     case CHART:
/* 740 */       return null;
/*     */     case TABLE:
/* 743 */       if (Boolean.TRUE.equals(getTickTablePresentationManager().isRunning())) {
/* 744 */         return getTickTablePresentationManager().getDataPresentationTable();
/*     */       }
/* 746 */       if (Boolean.TRUE.equals(getCandleTablePresentationManager().isRunning())) {
/* 747 */         return getCandleTablePresentationManager().getDataPresentationTable();
/*     */       }
/* 749 */       if (Boolean.TRUE.equals(getPriceRangeTablePresentationManager().isRunning())) {
/* 750 */         return getPriceRangeTablePresentationManager().getDataPresentationTable();
/*     */       }
/* 752 */       if (Boolean.TRUE.equals(getPointAndFigureTablePresentationManager().isRunning())) {
/* 753 */         return getPointAndFigureTablePresentationManager().getDataPresentationTable();
/*     */       }
/* 755 */       if (Boolean.TRUE.equals(getTickBarTablePresentationManager().isRunning())) {
/* 756 */         return getTickBarTablePresentationManager().getDataPresentationTable();
/*     */       }
/* 758 */       if (Boolean.TRUE.equals(getRenkoTablePresentationManager().isRunning())) {
/* 759 */         return getRenkoTablePresentationManager().getDataPresentationTable();
/*     */       }
/*     */ 
/* 762 */       return null;
/*     */     }
/*     */ 
/* 766 */     throw new IllegalArgumentException("Unsupported mode - " + currrentChartMode);
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getRenkoTablePresentationManager()
/*     */   {
/* 773 */     return this.renkoTablePresentationManager;
/*     */   }
/*     */ 
/*     */   class DivisionPanelAndSubChartView
/*     */   {
/*     */     final JComponent divisionPanel;
/*     */     final JComponent subChartView;
/*     */     final SubIndicatorGroup subIndicatorGroup;
/*     */ 
/*     */     public DivisionPanelAndSubChartView(JComponent divisionPanel, JComponent subChartView, SubIndicatorGroup subIndicatorGroup)
/*     */     {
/* 482 */       this.divisionPanel = divisionPanel;
/* 483 */       this.subChartView = subChartView;
/* 484 */       this.subIndicatorGroup = subIndicatorGroup;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ChartsGuiManager
 * JD-Core Version:    0.6.0
 */