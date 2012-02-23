/*      */ package com.dukascopy.charts.main;
/*      */ 
/*      */ import com.dukascopy.api.DataType.DataPresentationType;
/*      */ import com.dukascopy.api.IChart;
/*      */ import com.dukascopy.api.IChart.Type;
/*      */ import com.dukascopy.api.IChartObject;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.PriceRange;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.charts.chartbuilder.ChartBuilder;
/*      */ import com.dukascopy.charts.chartbuilder.ChartState;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.listener.ChartModeChangeListener;
/*      */ import com.dukascopy.charts.listener.DisableEnableListener;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsActionListener;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.main.interfaces.IChartController;
/*      */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*      */ import com.dukascopy.charts.main.nulls.NullIChart;
/*      */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*      */ import com.dukascopy.charts.persistence.ChartBean;
/*      */ import com.dukascopy.charts.persistence.IChartClient;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ThemeManager;
/*      */ import java.awt.Component;
/*      */ import java.awt.Point;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class DDSChartsControllerImpl
/*      */   implements DDSChartsController
/*      */ {
/*   45 */   private static final Logger LOGGER = LoggerFactory.getLogger(DDSChartsControllerImpl.class);
/*      */   private static final String ERROR_MESSAGE = "Failed to create a chart for %1$s, %2$s, %3$s! See log for details. Please close this chart and create a new one.";
/*   49 */   public final Map<Integer, IChartController> chartControllers = new HashMap();
/*      */   private static DDSChartsController instance;
/*      */   private IChartClient chartClient;
/*      */ 
/*      */   private DDSChartsControllerImpl(IChartClient chartClient)
/*      */   {
/*   57 */     this.chartClient = chartClient;
/*      */   }
/*      */ 
/*      */   public static void initialize(IChartClient chartClient)
/*      */   {
/*   62 */     instance = new DDSChartsControllerImpl(chartClient);
/*      */   }
/*      */ 
/*      */   public static DDSChartsController getInstance() {
/*   66 */     if (instance == null) {
/*   67 */       instance = new DDSChartsControllerImpl(null);
/*      */     }
/*   69 */     return instance;
/*      */   }
/*      */ 
/*      */   public JPanel getChartPanel(Integer chartPanelId)
/*      */   {
/*   74 */     if (!idExists(chartPanelId)) {
/*   75 */       return null;
/*      */     }
/*   77 */     return (JPanel)((IChartController)this.chartControllers.get(chartPanelId)).getChartsContainer();
/*      */   }
/*      */ 
/*      */   public IChart getLastActiveIChart()
/*      */   {
/*   82 */     return getIChartBy(Integer.valueOf(this.chartClient.getLastActiveChartPanelId()));
/*      */   }
/*      */ 
/*      */   public JPanel createNewChartOrGetById(ChartBean chartBean)
/*      */   {
/*   87 */     return createOrGet(chartBean);
/*      */   }
/*      */ 
/*      */   public void removeChart(Integer chartPanelId)
/*      */   {
/*   92 */     if (!idExists(chartPanelId)) {
/*   93 */       return;
/*      */     }
/*   95 */     if (LOGGER.isDebugEnabled()) {
/*   96 */       LOGGER.debug("removing chart: " + chartPanelId);
/*      */     }
/*   98 */     ((IChartController)this.chartControllers.remove(chartPanelId)).dispose();
/*   99 */     this.chartControllers.remove(chartPanelId);
/*      */   }
/*      */ 
/*      */   public IChart getIChartBy(Instrument instrument)
/*      */   {
/*      */     try {
/*  105 */       if (instrument == null) {
/*  106 */         LOGGER.info("IChart for instrument: " + instrument + " not found!");
/*  107 */         return new NullIChart();
/*      */       }
/*  109 */       for (IChartController iChartController : this.chartControllers.values()) {
/*  110 */         if (instrument.equals(iChartController.getChartState().getInstrument())) {
/*  111 */           return iChartController.getIChart();
/*      */         }
/*      */       }
/*  114 */       return null;
/*      */     } catch (Throwable e) {
/*  116 */       LOGGER.warn("Failed to get IChart by instrument: " + instrument, e);
/*  117 */     }return null;
/*      */   }
/*      */ 
/*      */   public IChart getIChartBy(Integer chartPanelId)
/*      */   {
/*      */     try
/*      */     {
/*  124 */       if (!idExists(chartPanelId)) {
/*  125 */         return new NullIChart();
/*      */       }
/*  127 */       IChart iChart = ((IChartController)this.chartControllers.get(chartPanelId)).getIChart();
/*  128 */       if (iChart == null) {
/*  129 */         return new NullIChart();
/*      */       }
/*  131 */       return iChart;
/*      */     } catch (Throwable e) {
/*  133 */       LOGGER.warn("Failed to get IChart by chart panel id: " + chartPanelId, e);
/*  134 */     }return new NullIChart();
/*      */   }
/*      */ 
/*      */   public void startDrawing(Integer chartPanelId, IChart.Type drawingType)
/*      */   {
/*      */     try
/*      */     {
/*  141 */       if (!idExists(chartPanelId)) {
/*  142 */         return;
/*      */       }
/*  144 */       if (LOGGER.isDebugEnabled()) {
/*  145 */         LOGGER.debug("Start drawing: " + drawingType + " on chart: " + chartPanelId);
/*      */       }
/*  147 */       ((IChartController)this.chartControllers.get(chartPanelId)).startDrawing(drawingType);
/*      */     } catch (Throwable e) {
/*  149 */       LOGGER.warn("Failed to start drawing on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void add(Integer chartPanelId, List<IChartObject> chartObjects)
/*      */   {
/*      */     try {
/*  156 */       if (!idExists(chartPanelId)) {
/*  157 */         return;
/*      */       }
/*  159 */       if (LOGGER.isDebugEnabled()) {
/*  160 */         LOGGER.debug("Add drawings on chart : " + chartPanelId);
/*      */       }
/*  162 */       ((IChartController)this.chartControllers.get(chartPanelId)).addDrawings(chartObjects);
/*      */     } catch (Throwable e) {
/*  164 */       LOGGER.warn("Failed to add drawings to chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addDrawingToIndicator(int chartPanelId, int subPanelId, int indicatorId, IChartObject iChartObject)
/*      */   {
/*      */     try {
/*  171 */       if (!idExists(Integer.valueOf(chartPanelId))) {
/*  172 */         return;
/*      */       }
/*  174 */       if (LOGGER.isDebugEnabled()) {
/*  175 */         LOGGER.debug("Add drawing: " + iChartObject + " on chart : " + chartPanelId + " on sub panel: " + subPanelId);
/*      */       }
/*  177 */       ((IChartController)this.chartControllers.get(Integer.valueOf(chartPanelId))).addDrawingToIndicator(Integer.valueOf(subPanelId), indicatorId, iChartObject);
/*      */     } catch (Throwable e) {
/*  179 */       LOGGER.warn("Failed to add drawing:" + iChartObject + " to chart panel: " + chartPanelId + " on sub chart panel: " + subPanelId + " for indicator: " + indicatorId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void remove(Integer chartPanelId, IChartObject chartObject)
/*      */   {
/*      */     try {
/*  186 */       if (!idExists(chartPanelId)) {
/*  187 */         return;
/*      */       }
/*  189 */       if (LOGGER.isDebugEnabled()) {
/*  190 */         LOGGER.debug("Removing drawing: " + chartObject.getType() + " from chart: " + chartPanelId);
/*      */       }
/*  192 */       ((IChartController)this.chartControllers.get(chartPanelId)).remove(chartObject);
/*      */     } catch (Throwable e) {
/*  194 */       LOGGER.warn("Failed to remove drawing: " + chartObject + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void remove(Integer chartPanelId, List<IChartObject> chartObjects)
/*      */   {
/*      */     try {
/*  201 */       if (!idExists(chartPanelId)) {
/*  202 */         return;
/*      */       }
/*  204 */       if (LOGGER.isDebugEnabled()) {
/*  205 */         LOGGER.debug("Removing drawings from chart : " + chartPanelId);
/*      */       }
/*  207 */       ((IChartController)this.chartControllers.get(chartPanelId)).remove(chartObjects);
/*      */     } catch (Throwable e) {
/*  209 */       LOGGER.warn("Failed to remove drawings on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeAllDrawings(int chartPanelId)
/*      */   {
/*      */     try {
/*  216 */       if (!idExists(Integer.valueOf(chartPanelId))) {
/*  217 */         return;
/*      */       }
/*  219 */       if (LOGGER.isDebugEnabled()) {
/*  220 */         LOGGER.debug("Removing all drawings from chart: " + chartPanelId);
/*      */       }
/*  222 */       ((IChartController)this.chartControllers.get(Integer.valueOf(chartPanelId))).removeAllDrawings();
/*      */     } catch (Throwable e) {
/*  224 */       LOGGER.warn("Failed to remove all drawings on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addIndicator(Integer chartPanelId, IndicatorWrapper indicatorWrapper)
/*      */   {
/*      */     try
/*      */     {
/*  234 */       if (!idExists(chartPanelId)) {
/*  235 */         return;
/*      */       }
/*  237 */       if (LOGGER.isDebugEnabled()) {
/*  238 */         LOGGER.debug("adding indicator: " + indicatorWrapper.getName() + " on chart: " + chartPanelId);
/*      */       }
/*  240 */       ((IChartController)this.chartControllers.get(chartPanelId)).addIndicator(indicatorWrapper);
/*      */     } catch (Throwable e) {
/*  242 */       LOGGER.warn("Failed to add indicator: " + indicatorWrapper + " to chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addIndicators(Integer chartPanelId, List<IndicatorWrapper> indicatorWrappers)
/*      */   {
/*  248 */     if ((indicatorWrappers == null) || (indicatorWrappers.isEmpty())) {
/*  249 */       return;
/*      */     }
/*      */ 
/*  252 */     for (Iterator i$ = indicatorWrappers.iterator(); i$.hasNext(); ) { indicator = (IndicatorWrapper)i$.next();
/*  253 */       subPanelId = indicator.getSubPanelId();
/*  254 */       if ((subPanelId == null) || (subPanelId.intValue() == -1)) {
/*  255 */         addIndicator(chartPanelId, indicator);
/*      */       } else {
/*  257 */         addIndicator(chartPanelId, subPanelId, indicator);
/*  258 */         List chartObjects = indicator.getChartObjects();
/*  259 */         if (chartObjects != null)
/*  260 */           for (IChartObject iChartObject : chartObjects)
/*  261 */             addDrawingToIndicator(chartPanelId.intValue(), subPanelId.intValue(), indicator.getId(), iChartObject);
/*      */       } }
/*      */     IndicatorWrapper indicator;
/*      */     Integer subPanelId;
/*      */   }
/*      */ 
/*      */   public void addIndicator(Integer chartPanelId, Integer subChartId, IndicatorWrapper indicatorWrapper)
/*      */   {
/*      */     try {
/*  272 */       if (!idExists(chartPanelId)) {
/*  273 */         return;
/*      */       }
/*  275 */       if (LOGGER.isDebugEnabled()) {
/*  276 */         LOGGER.debug("adding indicator: " + indicatorWrapper.getName() + " on chart: " + chartPanelId + " on sub win: " + subChartId);
/*      */       }
/*  278 */       ((IChartController)this.chartControllers.get(chartPanelId)).addIndicatorOnSubWin(subChartId, indicatorWrapper);
/*      */     } catch (Throwable e) {
/*  280 */       LOGGER.warn("Failed to add indicator: " + indicatorWrapper + " to chart panel: " + chartPanelId + " on sub win: " + subChartId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void editIndicator(Integer chartPanelId, int subPanelId, IndicatorWrapper indicatorWrapper)
/*      */   {
/*      */     try {
/*  287 */       if (!idExists(chartPanelId)) {
/*  288 */         return;
/*      */       }
/*  290 */       if (LOGGER.isDebugEnabled()) {
/*  291 */         LOGGER.debug("edit indicator: " + indicatorWrapper.getName() + " on chart: " + chartPanelId);
/*      */       }
/*  293 */       ((IChartController)this.chartControllers.get(chartPanelId)).editIndicator(subPanelId, indicatorWrapper);
/*      */     } catch (Throwable e) {
/*  295 */       LOGGER.warn("Failed to edit indicator: " + indicatorWrapper + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteIndicator(Integer chartPanelId, IndicatorWrapper indicatorWrapper)
/*      */   {
/*      */     try {
/*  302 */       if (!idExists(chartPanelId)) {
/*  303 */         return;
/*      */       }
/*  305 */       if (LOGGER.isDebugEnabled()) {
/*  306 */         LOGGER.debug("deleting indicator: " + indicatorWrapper.getName() + " on chart: " + chartPanelId);
/*      */       }
/*  308 */       ((IChartController)this.chartControllers.get(chartPanelId)).deleteIndicator(indicatorWrapper);
/*      */     } catch (Throwable e) {
/*  310 */       LOGGER.warn("Failed to delete indicator: " + indicatorWrapper + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteIndicators(Integer chartPanelId, List<IndicatorWrapper> indicatorWrappers)
/*      */   {
/*      */     try {
/*  317 */       if (!idExists(chartPanelId)) {
/*  318 */         return;
/*      */       }
/*  320 */       StringBuffer logMessage = new StringBuffer("deleting indicators: ");
/*  321 */       for (IndicatorWrapper wrapper : indicatorWrappers) {
/*  322 */         logMessage.append("(").append(wrapper.getId()).append(", ").append(wrapper.getName()).append(")");
/*      */       }
/*  324 */       logMessage.append(" on chart: ").append(chartPanelId);
/*      */ 
/*  326 */       if (LOGGER.isDebugEnabled()) {
/*  327 */         LOGGER.debug(logMessage.toString());
/*      */       }
/*  329 */       ((IChartController)this.chartControllers.get(chartPanelId)).deleteIndicators(indicatorWrappers);
/*      */     } catch (Throwable e) {
/*  331 */       LOGGER.warn("Failed to delete indicators on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeLineType(Integer chartPanelId, DataType.DataPresentationType timePeriodAggregationPresentationType)
/*      */   {
/*      */     try
/*      */     {
/*  339 */       if (!idExists(chartPanelId)) {
/*  340 */         return;
/*      */       }
/*  342 */       if (LOGGER.isDebugEnabled()) {
/*  343 */         LOGGER.debug("changing line type: " + timePeriodAggregationPresentationType + " on chart: " + chartPanelId);
/*      */       }
/*  345 */       ((IChartController)this.chartControllers.get(chartPanelId)).setLineType(timePeriodAggregationPresentationType);
/*      */     } catch (Throwable e) {
/*  347 */       LOGGER.warn("Failed to change line type to: " + timePeriodAggregationPresentationType + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeTickType(Integer chartPanelId, DataType.DataPresentationType ticksPresentationType)
/*      */   {
/*      */     try {
/*  354 */       if (!idExists(chartPanelId)) {
/*  355 */         return;
/*      */       }
/*  357 */       if (LOGGER.isDebugEnabled()) {
/*  358 */         LOGGER.debug("changing tick type: " + ticksPresentationType + " on chart: " + chartPanelId);
/*      */       }
/*  360 */       ((IChartController)this.chartControllers.get(chartPanelId)).setTickType(ticksPresentationType);
/*      */     } catch (Throwable e) {
/*  362 */       LOGGER.warn("Failed to change tick type to: " + ticksPresentationType + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeAggregationPeriod(Integer chartPanelId, Period selectedPeriod)
/*      */   {
/*      */     try {
/*  369 */       if (!idExists(chartPanelId)) {
/*  370 */         return;
/*      */       }
/*  372 */       if (LOGGER.isDebugEnabled()) {
/*  373 */         LOGGER.debug("changing period to: " + selectedPeriod + " on chart: " + chartPanelId);
/*      */       }
/*  375 */       ((IChartController)this.chartControllers.get(chartPanelId)).setPeriod(selectedPeriod);
/*      */     } catch (Throwable e) {
/*  377 */       LOGGER.warn("Failed to set period: " + selectedPeriod + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void switchBidAskTo(Integer chartPanelId, OfferSide offerSide)
/*      */   {
/*      */     try {
/*  384 */       if (!idExists(chartPanelId)) {
/*  385 */         return;
/*      */       }
/*  387 */       if (LOGGER.isDebugEnabled()) {
/*  388 */         LOGGER.debug("changing offer side to: " + offerSide + " on chart: " + chartPanelId);
/*      */       }
/*  390 */       ((IChartController)this.chartControllers.get(chartPanelId)).setOfferSide(offerSide);
/*      */     } catch (Throwable e) {
/*  392 */       LOGGER.warn("Failed to set offer side: " + offerSide + " for chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void zoomIn(Integer chartPanelId)
/*      */   {
/*      */     try {
/*  399 */       if (!idExists(chartPanelId)) {
/*  400 */         return;
/*      */       }
/*  402 */       if (LOGGER.isDebugEnabled()) {
/*  403 */         LOGGER.debug("initiating zooming in for chart: " + chartPanelId);
/*      */       }
/*  405 */       ((IChartController)this.chartControllers.get(chartPanelId)).zoomIn();
/*      */     } catch (Throwable e) {
/*  407 */       LOGGER.warn("Failed to zoom in chart: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void zoomOut(Integer chartPanelId)
/*      */   {
/*      */     try {
/*  414 */       if (!idExists(chartPanelId)) {
/*  415 */         return;
/*      */       }
/*  417 */       if (LOGGER.isDebugEnabled()) {
/*  418 */         LOGGER.debug("initiating zooming out for chart: " + chartPanelId);
/*      */       }
/*  420 */       ((IChartController)this.chartControllers.get(chartPanelId)).zoomOut();
/*      */     } catch (Throwable e) {
/*  422 */       LOGGER.warn("Failed to zoom out chart: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startZoomingToArea(Integer chartPanelId)
/*      */   {
/*      */     try {
/*  429 */       if (!idExists(chartPanelId)) {
/*  430 */         return;
/*      */       }
/*  432 */       if (LOGGER.isDebugEnabled()) {
/*  433 */         LOGGER.debug("initiating zooming to area for chart: " + chartPanelId);
/*      */       }
/*  435 */       ((IChartController)this.chartControllers.get(chartPanelId)).startZoomingToArea();
/*      */     } catch (Throwable e) {
/*  437 */       LOGGER.warn("Failed to start manual zooming to area on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void shiftChartToFront(Integer chartPanelId)
/*      */   {
/*      */     try {
/*  444 */       if (!idExists(chartPanelId)) {
/*  445 */         return;
/*      */       }
/*  447 */       if (LOGGER.isDebugEnabled()) {
/*  448 */         LOGGER.debug("initiating chart shift to front for chart: " + chartPanelId);
/*      */       }
/*  450 */       ((IChartController)this.chartControllers.get(chartPanelId)).shiftChartToFront();
/*      */     } catch (Throwable e) {
/*  452 */       LOGGER.warn("Failed to shift chart: " + chartPanelId + " to front", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void shiftChartSilent(Integer chartPanelId, int unitsCount)
/*      */   {
/*      */     try {
/*  459 */       if (!idExists(chartPanelId)) {
/*  460 */         return;
/*      */       }
/*  462 */       if (LOGGER.isDebugEnabled()) {
/*  463 */         LOGGER.debug("Shifting chart: " + chartPanelId + " to " + unitsCount);
/*      */       }
/*  465 */       ((IChartController)this.chartControllers.get(chartPanelId)).shiftChartSilent(unitsCount);
/*      */     } catch (Throwable e) {
/*  467 */       LOGGER.warn("Failed to shift chart: " + chartPanelId + " to " + unitsCount + " units", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void shiftChartSilent(Integer chartPanelId, int draggedFromPx, int draggedToPx, int draggedYFrom, int draggedYTo, boolean isNewDragging)
/*      */   {
/*      */     try {
/*  474 */       if (!idExists(chartPanelId)) {
/*  475 */         return;
/*      */       }
/*  477 */       if (LOGGER.isDebugEnabled()) {
/*  478 */         LOGGER.debug("Shifting chart: " + chartPanelId);
/*      */       }
/*  480 */       ((IChartController)this.chartControllers.get(chartPanelId)).moveTimeFrameSilent(draggedFromPx, draggedToPx, draggedYFrom, draggedYTo, isNewDragging);
/*      */     } catch (Throwable e) {
/*  482 */       LOGGER.warn("Failed to shift chart: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void navigateToDrawing(Integer chartPanelId, IChartObject chartObject)
/*      */   {
/*      */     try {
/*  489 */       if (!idExists(chartPanelId)) {
/*  490 */         return;
/*      */       }
/*  492 */       IChartController chartController = (IChartController)this.chartControllers.get(chartPanelId);
/*  493 */       int dataUnitsCount = chartController.getIChart().getBarsCount();
/*  494 */       if (chartObject.getPointsCount() > 0) {
/*  495 */         chartController.setCustomRange(dataUnitsCount / 2, chartObject.getTime(0), dataUnitsCount / 2);
/*      */       }
/*      */       else {
/*  498 */         chartController.unselectDrawingToBeEdited();
/*  499 */         chartController.refreshContent();
/*      */       }
/*  501 */       chartController.selectDrawing(chartObject);
/*      */     } catch (Throwable e) {
/*  503 */       LOGGER.warn("Failed to navigate to drawing on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void switchMouseCursor(Integer chartPanelId, boolean isMouseCursorVisible)
/*      */   {
/*      */     try {
/*  510 */       if (!idExists(chartPanelId)) {
/*  511 */         return;
/*      */       }
/*  513 */       if (LOGGER.isDebugEnabled()) {
/*  514 */         LOGGER.debug("setting mouse cursor visibility to: " + isMouseCursorVisible + " on chart: " + chartPanelId);
/*      */       }
/*  516 */       ((IChartController)this.chartControllers.get(chartPanelId)).setMouseCursorVisible(isMouseCursorVisible);
/*      */     } catch (Throwable e) {
/*  518 */       LOGGER.warn("Failed to set mouse cursor visibility to: " + isMouseCursorVisible + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void switchRangeScrollBarVisibility(Integer chartPanelId)
/*      */   {
/*      */     try
/*      */     {
/*  526 */       if (!idExists(chartPanelId))
/*  527 */         return;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  531 */       LOGGER.warn("Failed to switch range scroll bar on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setVerticalChartMovementEnabled(Integer chartPanelId, boolean isVerticalChartMovementEnabled)
/*      */   {
/*      */     try {
/*  538 */       if (!idExists(chartPanelId)) {
/*  539 */         return;
/*      */       }
/*  541 */       if (LOGGER.isDebugEnabled()) {
/*  542 */         LOGGER.debug("setting vertical chart movement enabled to: " + isVerticalChartMovementEnabled + " on chart: " + chartPanelId);
/*      */       }
/*  544 */       ((IChartController)this.chartControllers.get(chartPanelId)).setVerticalChartMovementEnabled(isVerticalChartMovementEnabled);
/*      */     } catch (Throwable e) {
/*  546 */       LOGGER.warn("Failed to set vertical chart movement to: " + isVerticalChartMovementEnabled + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addProgressListener(int chartPanelId, ProgressListener progressListener)
/*      */   {
/*      */     try
/*      */     {
/*  554 */       if (!idExists(Integer.valueOf(chartPanelId))) {
/*  555 */         return;
/*      */       }
/*  557 */       if (LOGGER.isDebugEnabled()) {
/*  558 */         LOGGER.debug("adding progress listener to chart: " + chartPanelId);
/*      */       }
/*  560 */       ((IChartController)this.chartControllers.get(Integer.valueOf(chartPanelId))).addProgressListener(progressListener);
/*      */     } catch (Throwable e) {
/*  562 */       LOGGER.warn("Failed to add progress listener for chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addEnableDisableListener(int chartPanelId, DisableEnableListener disableEnableListener)
/*      */   {
/*      */     try {
/*  569 */       if (!idExists(Integer.valueOf(chartPanelId))) {
/*  570 */         return;
/*      */       }
/*  572 */       if (LOGGER.isDebugEnabled()) {
/*  573 */         LOGGER.debug("adding enable disable listener to chart " + chartPanelId);
/*      */       }
/*  575 */       ((IChartController)this.chartControllers.get(Integer.valueOf(chartPanelId))).addDisabledEnableListener(disableEnableListener);
/*      */     } catch (Throwable e) {
/*  577 */       LOGGER.warn("Failed to add enable/disable listener for chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addChartModeChangeListener(int chartPanelId, ChartModeChangeListener chartModeChangeListener)
/*      */   {
/*      */     try {
/*  584 */       if (!idExists(Integer.valueOf(chartPanelId))) {
/*  585 */         return;
/*      */       }
/*  587 */       if (LOGGER.isDebugEnabled()) {
/*  588 */         LOGGER.debug("adding ChartModeChangeListener to chart " + chartPanelId);
/*      */       }
/*  590 */       ((IChartController)this.chartControllers.get(Integer.valueOf(chartPanelId))).addChartModeChangeListener(chartModeChangeListener);
/*      */     } catch (Throwable e) {
/*  592 */       LOGGER.warn("Failed to add ChartModeChangeListener for chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addChartsActionListener(Integer chartPanelId, DDSChartsActionListener ddsChartsActionListener)
/*      */   {
/*      */     try
/*      */     {
/*  600 */       if (!idExists(chartPanelId)) {
/*  601 */         return;
/*      */       }
/*  603 */       if (LOGGER.isDebugEnabled()) {
/*  604 */         LOGGER.debug("adding chart action listener to chart: " + chartPanelId);
/*      */       }
/*  606 */       ((IChartController)this.chartControllers.get(chartPanelId)).addChartsActionListener(ddsChartsActionListener);
/*      */     } catch (Throwable e) {
/*  608 */       LOGGER.warn("Failed to add charts action listener for chart panel id: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startLoadingData(Integer chartPanelId, boolean autoShiftActive, int chartShiftInPx)
/*      */   {
/*      */     try
/*      */     {
/*  616 */       if (!idExists(chartPanelId)) {
/*  617 */         return;
/*      */       }
/*  619 */       if (LOGGER.isDebugEnabled()) {
/*  620 */         LOGGER.debug("starting loading data for chart: " + chartPanelId + " (auto shift is enabled=" + autoShiftActive + ", chart shift px=" + chartShiftInPx + ")");
/*      */       }
/*  622 */       ((IChartController)this.chartControllers.get(chartPanelId)).startLoadingData(autoShiftActive, chartShiftInPx);
/*      */     } catch (Throwable e) {
/*  624 */       LOGGER.warn("Failed to start loading data for chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  630 */     LOGGER.debug("initiating disposing of all charts...");
/*  631 */     for (Integer panelId : this.chartControllers.keySet())
/*      */       try {
/*  633 */         ((IChartController)this.chartControllers.get(panelId)).dispose();
/*  634 */         if (LOGGER.isDebugEnabled())
/*  635 */           LOGGER.debug("chart panel: " + panelId + " disposed successfully");
/*      */       }
/*      */       catch (Throwable e) {
/*  638 */         LOGGER.warn("Failed to dispose chart panel: " + panelId, e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void saveWorkspaceImageToFile(Integer chartPanelId, File file)
/*      */   {
/*      */     try
/*      */     {
/*  646 */       if (!idExists(chartPanelId)) {
/*  647 */         return;
/*      */       }
/*  649 */       if (LOGGER.isDebugEnabled()) {
/*  650 */         LOGGER.debug("saving workspace image of chart:" + chartPanelId + " to the file: " + file.getName());
/*      */       }
/*  652 */       ((IChartController)this.chartControllers.get(chartPanelId)).saveWorkspaceImageToFile(file);
/*      */     } catch (Throwable e) {
/*  654 */       LOGGER.warn("Failed to save chart panel: " + chartPanelId + " to file " + file.getName(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveWorkspaceImageToClipboard(Integer chartPanelId) {
/*      */     try {
/*  660 */       if (!idExists(chartPanelId)) {
/*  661 */         return;
/*      */       }
/*  663 */       if (LOGGER.isDebugEnabled()) {
/*  664 */         LOGGER.debug("saving workspace image of chart: " + chartPanelId + " to clipboard");
/*      */       }
/*  666 */       ((IChartController)this.chartControllers.get(chartPanelId)).saveWorkspaceImageToClipboard();
/*      */     } catch (Throwable e) {
/*  668 */       LOGGER.warn("Failed to save chart panel: " + chartPanelId + " to clipboard", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void printWorkspaceImage(Integer chartPanelId) {
/*      */     try {
/*  674 */       if (!idExists(chartPanelId)) {
/*  675 */         return;
/*      */       }
/*  677 */       if (LOGGER.isDebugEnabled()) {
/*  678 */         LOGGER.debug("printing workspace image of chart: " + chartPanelId + " to clipboard");
/*      */       }
/*  680 */       ((IChartController)this.chartControllers.get(chartPanelId)).printWorkspaceImage();
/*      */     } catch (Throwable e) {
/*  682 */       LOGGER.warn("Failed to print chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<IndicatorWrapper> createAddEditIndicatorsDialog(int chartPanelId)
/*      */   {
/*      */     try {
/*  689 */       if (!idExists(Integer.valueOf(chartPanelId))) {
/*  690 */         return null;
/*      */       }
/*  692 */       LOGGER.debug("showing Add/Edit dialog for indicators");
/*  693 */       return ((IChartController)this.chartControllers.get(Integer.valueOf(chartPanelId))).createAddEditIndicatorsDialog();
/*      */     } catch (Throwable e) {
/*  695 */       LOGGER.warn("Failed to create add/edit dialog for chart panel: " + chartPanelId, e);
/*  696 */     }return null;
/*      */   }
/*      */ 
/*      */   public JPopupMenu createPopupMenuForDrawing(int chartPanelId, IChartObject drawing, Component component, Point locationOnScreen)
/*      */   {
/*      */     try {
/*  702 */       if (!idExists(Integer.valueOf(chartPanelId))) {
/*  703 */         return null;
/*      */       }
/*  705 */       if (LOGGER.isDebugEnabled()) {
/*  706 */         LOGGER.debug("creating popup menu for drawing:" + drawing + " on chart panel: " + chartPanelId);
/*      */       }
/*  708 */       return ((IChartController)this.chartControllers.get(Integer.valueOf(chartPanelId))).createPopupMenuForDrawings(drawing, component, locationOnScreen);
/*      */     } catch (Throwable e) {
/*  710 */       LOGGER.warn("Failed to create popup menu for drawing: " + drawing + " for chart panel: " + chartPanelId, e);
/*  711 */     }return null;
/*      */   }
/*      */ 
/*      */   public void refreshChartsContent()
/*      */   {
/*  716 */     LOGGER.debug("refreshing content of all charts...");
/*  717 */     Set keys = this.chartControllers.keySet();
/*  718 */     for (Integer chartPanelId : keys)
/*      */       try {
/*  720 */         ((IChartController)this.chartControllers.get(chartPanelId)).refreshContent();
/*      */       } catch (Throwable e) {
/*  722 */         LOGGER.warn("Failed to refresh content on chart panel: " + chartPanelId, e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void selectCustomRange(Integer chartPanelId, List<JForexPeriod> allowedPeriods)
/*      */   {
/*      */     try {
/*  729 */       if (!idExists(chartPanelId)) {
/*  730 */         return;
/*      */       }
/*  732 */       LOGGER.debug("showing custom range dialog");
/*  733 */       ((IChartController)this.chartControllers.get(chartPanelId)).selectCustomRange(allowedPeriods);
/*      */     } catch (Throwable e) {
/*  735 */       LOGGER.warn("Failed to select custom range on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerGuiObserversFor(int chartPanelId, JComponent[] guiObservers)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean idExists(Integer chartPanelId)
/*      */   {
/*  748 */     return this.chartControllers.containsKey(chartPanelId);
/*      */   }
/*      */ 
/*      */   JPanel createOrGet(ChartBean chartBean)
/*      */   {
/*  755 */     int chartId = chartBean.getId();
/*      */ 
/*  757 */     IChartController chartController = (IChartController)this.chartControllers.get(Integer.valueOf(chartId));
/*      */ 
/*  759 */     if (chartController != null) {
/*  760 */       if (LOGGER.isDebugEnabled()) {
/*  761 */         LOGGER.debug("Chart # " + chartId + " found");
/*      */       }
/*  763 */       return (JPanel)chartController.getMainContainer();
/*      */     }
/*      */ 
/*  766 */     if (LOGGER.isDebugEnabled()) {
/*  767 */       LOGGER.debug("Chart # " + chartId + " not found. Creating it...");
/*      */     }
/*  769 */     JComponent mainPanel = null;
/*      */     try {
/*  771 */       chartController = new ChartBuilder(chartBean).buildChart();
/*  772 */       this.chartControllers.put(Integer.valueOf(chartId), chartController);
/*  773 */       mainPanel = chartController.getMainContainer();
/*      */       try
/*      */       {
/*  776 */         chartController.addMainChart();
/*      */       } catch (Throwable ex) {
/*  778 */         LOGGER.warn("Exception occured while adding main chart!", ex);
/*      */       }
/*      */     }
/*      */     catch (Throwable ex) {
/*  782 */       LOGGER.warn("Exception occured while building the chart!", ex);
/*  783 */       mainPanel = new JPanel();
/*  784 */       mainPanel.add(new JLabel(String.format("Failed to create a chart for %1$s, %2$s, %3$s! See log for details. Please close this chart and create a new one.", new Object[] { chartBean.getInstrument(), chartBean.getPeriod(), chartBean.getOfferSide() })));
/*      */     }
/*      */ 
/*  787 */     return (JPanel)mainPanel;
/*      */   }
/*      */ 
/*      */   public Set<Integer> getChartControllerIdies() {
/*  791 */     return this.chartControllers.keySet();
/*      */   }
/*      */ 
/*      */   public ChartBean synchronizeAndGetChartBean(Integer chartId) {
/*  795 */     if (!this.chartControllers.containsKey(chartId)) {
/*  796 */       return null;
/*      */     }
/*  798 */     IChartController chartController = (IChartController)this.chartControllers.get(chartId);
/*      */ 
/*  800 */     ChartBean chartBean = chartController.getChartBean();
/*      */ 
/*  802 */     chartBean.setEndTime(chartController.getTime());
/*  803 */     chartBean.setMinPrice(chartController.getMinPrice());
/*  804 */     chartBean.setMaxPrice(chartController.getMaxPrice());
/*      */ 
/*  806 */     chartBean.setAutoShiftActive(chartController.getChartState().isChartShiftActive() ? 1 : 0);
/*  807 */     chartBean.setChartShiftInPx(chartController.getChartState().getChartShiftHandlerCoordinate());
/*  808 */     chartBean.setYAxisPadding(chartController.getYAxisPadding());
/*  809 */     chartBean.setDataUnitWidth(chartController.getGeometryCalculator().getDataUnitWidth());
/*  810 */     chartBean.setTimePeriodPresentationType(chartController.getChartState().getCandleType());
/*      */ 
/*  813 */     chartBean.setInstrument(chartController.getChartState().getInstrument());
/*      */ 
/*  816 */     chartBean.setOfferSide(chartController.getChartState().getOfferSide());
/*  817 */     chartBean.setPeriod(chartController.getChartState().getPeriod());
/*      */ 
/*  819 */     chartBean.setTicksPresentationType(chartController.getChartState().getTickType());
/*      */ 
/*  821 */     chartBean.setDataType(chartController.getChartState().getDataType());
/*  822 */     chartBean.setPriceRange(chartController.getChartState().getPriceRange());
/*  823 */     chartBean.setPriceRangePresentationType(chartController.getChartState().getPriceRangesPresentationType());
/*  824 */     chartBean.setReversalAmount(chartController.getChartState().getReversalAmount());
/*  825 */     chartBean.setPointAndFigurePresentationType(chartController.getChartState().getPointAndFigurePresentationType());
/*  826 */     chartBean.setVerticalMovementEnabledAsBoolean(chartController.getChartState().isVerticalChartMovementEnabled());
/*  827 */     chartBean.setTickBarPresentationType(chartController.getChartState().getTickBarPresentationType());
/*  828 */     chartBean.setTickBarSize(chartController.getChartState().getTickBarSize());
/*      */ 
/*  830 */     chartBean.setTheme(chartController.getChartState().getTheme());
/*      */ 
/*  832 */     return chartBean;
/*      */   }
/*      */ 
/*      */   public List<IndicatorWrapper> getIndicators(Integer chartId) {
/*  836 */     if (!this.chartControllers.containsKey(chartId)) {
/*  837 */       return new ArrayList();
/*      */     }
/*  839 */     IChartController chartController = (IChartController)this.chartControllers.get(chartId);
/*  840 */     return chartController.getIndicators();
/*      */   }
/*      */ 
/*      */   public void changeInstrument(Integer chartPanelId, Instrument instrument)
/*      */   {
/*      */     try {
/*  846 */       if (!idExists(chartPanelId)) {
/*  847 */         return;
/*      */       }
/*  849 */       if (LOGGER.isDebugEnabled()) {
/*  850 */         LOGGER.debug("changing instrument: " + instrument + " on chart: " + chartPanelId);
/*      */       }
/*  852 */       ((IChartController)this.chartControllers.get(chartPanelId)).setInstrument(instrument);
/*      */     } catch (Throwable e) {
/*  854 */       LOGGER.warn("Failed to changeinstrument to: " + instrument + " on chart panel: " + chartPanelId, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getMainChartDrawings(Integer chartId)
/*      */   {
/*  860 */     if (!this.chartControllers.containsKey(chartId)) {
/*  861 */       return null;
/*      */     }
/*      */ 
/*  864 */     IChartController chartController = (IChartController)this.chartControllers.get(chartId);
/*  865 */     List drawings = chartController.getMainChartDrawings();
/*      */ 
/*  867 */     return drawings;
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getSubChartDrawings(Integer chartId, int subPanelId, int indicatorId)
/*      */   {
/*  872 */     if (!this.chartControllers.containsKey(chartId)) {
/*  873 */       return null;
/*      */     }
/*      */ 
/*  876 */     IChartController chartController = (IChartController)this.chartControllers.get(chartId);
/*  877 */     List drawings = chartController.getSubChartDrawings(subPanelId, indicatorId);
/*      */ 
/*  879 */     return drawings;
/*      */   }
/*      */ 
/*      */   public void saveChartTableDataToFile(Integer chartPanelId, File file)
/*      */   {
/*      */     try {
/*  885 */       if (!idExists(chartPanelId)) {
/*  886 */         return;
/*      */       }
/*  888 */       if (LOGGER.isDebugEnabled()) {
/*  889 */         LOGGER.debug("saving chart table data of chart:" + chartPanelId + " to the file: " + file.getName());
/*      */       }
/*  891 */       ((IChartController)this.chartControllers.get(chartPanelId)).saveChartTableDataToFile(file);
/*      */     } catch (Throwable e) {
/*  893 */       LOGGER.warn("Failed to save chart table data : " + chartPanelId + " to file " + file.getName(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void applyQuickTableDataFilter(Integer chartPanelId, String pattern)
/*      */   {
/*      */     try {
/*  900 */       if (!idExists(chartPanelId)) {
/*  901 */         return;
/*      */       }
/*  903 */       if (LOGGER.isDebugEnabled()) {
/*  904 */         LOGGER.debug("Applying Quick Table Data Filter for chart :" + chartPanelId + " for pattern: " + pattern);
/*      */       }
/*  906 */       ((IChartController)this.chartControllers.get(chartPanelId)).applyQuickTableDataFilter(pattern);
/*      */     } catch (Throwable e) {
/*  908 */       LOGGER.debug("Failed to apply Quick Table Data Filter for chart :" + chartPanelId + " for pattern: " + pattern);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changePriceRange(Integer chartPanelId, PriceRange priceRange)
/*      */   {
/*      */     try {
/*  915 */       if (!idExists(chartPanelId)) {
/*  916 */         return;
/*      */       }
/*  918 */       if (LOGGER.isDebugEnabled()) {
/*  919 */         LOGGER.debug("Change Price Range for chart :" + chartPanelId + " Price Range : " + priceRange);
/*      */       }
/*  921 */       ((IChartController)this.chartControllers.get(chartPanelId)).changePriceRange(priceRange);
/*      */     } catch (Throwable e) {
/*  923 */       LOGGER.debug("Failed to change Price Range for chart :" + chartPanelId + " Price Range : " + priceRange);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changePriceRangePresentationType(Integer chartPanelId, DataType.DataPresentationType priceRangePresentationType)
/*      */   {
/*      */     try {
/*  930 */       if (!idExists(chartPanelId)) {
/*  931 */         return;
/*      */       }
/*  933 */       if (LOGGER.isDebugEnabled()) {
/*  934 */         LOGGER.debug("Change Price Range Presentation Type for chart :" + chartPanelId + " Price Range : " + priceRangePresentationType);
/*      */       }
/*  936 */       ((IChartController)this.chartControllers.get(chartPanelId)).changePriceRangePresentationType(priceRangePresentationType);
/*      */     } catch (Throwable e) {
/*  938 */       LOGGER.debug("Failed to change Price Range Presentation Type for chart :" + chartPanelId + " Price Range Presentation Type : " + priceRangePresentationType);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set<IChart> getICharts(Instrument instrument)
/*      */   {
/*      */     try {
/*  945 */       if (instrument == null) {
/*  946 */         LOGGER.info("IChart for instrument: " + instrument + " not found!");
/*  947 */         return Collections.singleton(new NullIChart());
/*      */       }
/*  949 */       Set result = new LinkedHashSet();
/*  950 */       for (IChartController iChartController : this.chartControllers.values()) {
/*  951 */         if (instrument.equals(iChartController.getChartBean().getInstrument())) {
/*  952 */           result.add(iChartController.getIChart());
/*      */         }
/*      */       }
/*  955 */       return result;
/*      */     } catch (Throwable e) {
/*  957 */       LOGGER.warn("Failed to get IChart by instrument: " + instrument, e);
/*  958 */     }return null;
/*      */   }
/*      */ 
/*      */   public void changeJForexPeriod(Integer chartPanelId, JForexPeriod jForexPeriod)
/*      */   {
/*      */     try
/*      */     {
/*  965 */       if (!idExists(chartPanelId)) {
/*  966 */         return;
/*      */       }
/*  968 */       if (LOGGER.isDebugEnabled()) {
/*  969 */         LOGGER.debug("Change JForexPeriod for chart :" + chartPanelId + " JForexPeriod : " + jForexPeriod);
/*      */       }
/*  971 */       ((IChartController)this.chartControllers.get(chartPanelId)).changeJForexPeriod(jForexPeriod);
/*      */     } catch (Throwable e) {
/*  973 */       LOGGER.error(e.getMessage(), e);
/*  974 */       LOGGER.debug("Failed to change JForexPeriod for chart :" + chartPanelId + " JForexPeriod : " + jForexPeriod);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changePointAndFigurePresentationType(Integer chartPanelId, DataType.DataPresentationType pointAndFigurePresentationType)
/*      */   {
/*      */     try
/*      */     {
/*  984 */       if (!idExists(chartPanelId)) {
/*  985 */         return;
/*      */       }
/*  987 */       if (LOGGER.isDebugEnabled()) {
/*  988 */         LOGGER.debug("Change PointAndFigurePresentationType for chart :" + chartPanelId + " PointAndFigurePresentationType : " + pointAndFigurePresentationType);
/*      */       }
/*  990 */       ((IChartController)this.chartControllers.get(chartPanelId)).changePointAndFigurePresentationType(pointAndFigurePresentationType);
/*      */     } catch (Throwable e) {
/*  992 */       LOGGER.debug("Failed to change PointAndFigurePresentationType for chart :" + chartPanelId + ", " + pointAndFigurePresentationType);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeTickBarPresentationType(Integer chartPanelId, DataType.DataPresentationType tickBarPresentationType)
/*      */   {
/*      */     try {
/*  999 */       if (!idExists(chartPanelId)) {
/* 1000 */         return;
/*      */       }
/* 1002 */       if (LOGGER.isDebugEnabled()) {
/* 1003 */         LOGGER.debug("Change TickBarPresentationType for chart :" + chartPanelId + " TickBarPresentationType : " + tickBarPresentationType);
/*      */       }
/* 1005 */       ((IChartController)this.chartControllers.get(chartPanelId)).changeTickBarPresentationType(tickBarPresentationType);
/*      */     } catch (Throwable e) {
/* 1007 */       LOGGER.debug("Failed to change TickBarPresentationType for chart :" + chartPanelId + ", " + tickBarPresentationType);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setChartClient(IChartClient chartClient)
/*      */   {
/* 1013 */     this.chartClient = chartClient;
/*      */   }
/*      */ 
/*      */   public IChartClient getChartClient()
/*      */   {
/* 1019 */     return this.chartClient;
/*      */   }
/*      */ 
/*      */   public void reactivate(Integer chartPanelId)
/*      */   {
/* 1024 */     ((IChartController)this.chartControllers.get(chartPanelId)).reactivate();
/*      */   }
/*      */ 
/*      */   public IChartObject getDrawingByKey(int chartId, String key)
/*      */   {
/*      */     try
/*      */     {
/* 1031 */       if (!idExists(Integer.valueOf(chartId))) {
/* 1032 */         return null;
/*      */       }
/* 1034 */       if (LOGGER.isDebugEnabled()) {
/* 1035 */         LOGGER.debug("call getDrawingByKey for chart :" + chartId + ", " + key);
/*      */       }
/* 1037 */       return ((IChartController)this.chartControllers.get(Integer.valueOf(chartId))).getDrawingByKey(key);
/*      */     } catch (Throwable e) {
/* 1039 */       LOGGER.debug("Failed to call getDrawingByKey for chart :" + chartId + ", " + key);
/* 1040 */     }return null;
/*      */   }
/*      */ 
/*      */   public boolean setTheme(int chartId, String themeName)
/*      */   {
/* 1046 */     if (!idExists(Integer.valueOf(chartId))) {
/* 1047 */       return false;
/*      */     }
/* 1049 */     if (LOGGER.isDebugEnabled()) {
/* 1050 */       LOGGER.debug("call setTheme for chart :" + chartId + ", " + themeName);
/*      */     }
/* 1052 */     ITheme theme = ThemeManager.getTheme(themeName);
/* 1053 */     if (theme == null) {
/* 1054 */       return false;
/*      */     }
/* 1056 */     ((IChartController)this.chartControllers.get(Integer.valueOf(chartId))).setTheme(theme);
/* 1057 */     return true;
/*      */   }
/*      */ 
/*      */   public String getTheme(int chartId)
/*      */   {
/* 1063 */     if (!idExists(Integer.valueOf(chartId))) {
/* 1064 */       return null;
/*      */     }
/* 1066 */     if (LOGGER.isDebugEnabled()) {
/* 1067 */       LOGGER.debug("call getTheme for chart :" + chartId);
/*      */     }
/* 1069 */     return ((IChartController)this.chartControllers.get(Integer.valueOf(chartId))).getTheme().getName();
/*      */   }
/*      */ 
/*      */   public void changeRenkoPresentationType(Integer chartPanelId, DataType.DataPresentationType renkoPresentationType)
/*      */   {
/*      */     try {
/* 1075 */       if (!idExists(chartPanelId)) {
/* 1076 */         return;
/*      */       }
/* 1078 */       if (LOGGER.isDebugEnabled()) {
/* 1079 */         LOGGER.debug("Change RenkoPresentationType for chart :" + chartPanelId + " RenkoPresentationType : " + renkoPresentationType);
/*      */       }
/* 1081 */       ((IChartController)this.chartControllers.get(chartPanelId)).changeRenkoPresentationType(renkoPresentationType);
/*      */     } catch (Throwable e) {
/* 1083 */       LOGGER.debug("Failed to change RenkoPresentationType for chart :" + chartPanelId + ", " + renkoPresentationType);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.main.DDSChartsControllerImpl
 * JD-Core Version:    0.6.0
 */