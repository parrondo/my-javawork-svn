/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.dialogs.customrange.CustomRange;
/*     */ import com.dukascopy.charts.dialogs.customrange.CustomRangeDialog;
/*     */ import com.dukascopy.charts.dialogs.indicators.AddIndicatorDialog;
/*     */ import com.dukascopy.charts.dialogs.indicators.EditIndicatorDialog;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.MainDrawingsManagerImpl;
/*     */ import com.dukascopy.charts.drawings.PopupManagerForDrawings;
/*     */ import com.dukascopy.charts.drawings.SubDrawingsManagerImpl;
/*     */ import com.dukascopy.charts.listener.ChartModeChangeListener;
/*     */ import com.dukascopy.charts.listener.ChartModeChangeListener.ChartMode;
/*     */ import com.dukascopy.charts.listener.DisableEnableListener;
/*     */ import com.dukascopy.charts.listeners.ChartSystemListenerManager;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.listeners.lock.ChartModeChangeListenersRegistry;
/*     */ import com.dukascopy.charts.listeners.lock.DisableEnableListenersRegistry;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsActionListener;
/*     */ import com.dukascopy.charts.main.interfaces.IChartController;
/*     */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.IIndicatorsContainer;
/*     */ import com.dukascopy.charts.orders.OrdersController;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.tablebuilder.ITablePresentationManager;
/*     */ import com.dukascopy.charts.tablebuilder.component.table.DataTablePresentationAbstractJTable;
/*     */ import com.dukascopy.charts.utils.PrintUtilities;
/*     */ import com.dukascopy.charts.utils.SnapshotHelper;
/*     */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*     */ import com.dukascopy.charts.view.displayabledatapart.DrawingsManagerContainerImpl;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.table.TableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class ChartController
/*     */   implements IChartController
/*     */ {
/*  65 */   private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);
/*     */   private final ChartBean chartBean;
/*     */   private final GuiRefresher guiRefresher;
/*     */   private final IDataManager dataManager;
/*     */   private final IIndicatorsContainer indicatorsContainer;
/*     */   private final DrawingsManagerContainerImpl drawingsManagerContainer;
/*     */   private final OrdersController ordersController;
/*     */   private final IMainOperationManager mainOperationManager;
/*     */   private final IChartWrapper chartWrapper;
/*     */   private final ChartSystemListenerManager chartSystemListenerManager;
/*     */   private final ChartState chartState;
/*     */   private final GeometryCalculator geometryCalculator;
/*     */   private final PopupManagerForDrawings popupManagerForDrawings;
/*  79 */   private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
/*     */ 
/*     */   ChartController(ChartBean chartBean, GuiRefresher guiRefresher, IDataManager dataManager, IIndicatorsContainer indicatorsContainer, DrawingsManagerContainerImpl drawingsManagerContainer, OrdersController ordersController, IMainOperationManager mainOperationManager, IChartWrapper iChartWrapper, ChartSystemListenerManager chartSystemListenerManager, ChartState chartState, GeometryCalculator geometryCalculator, PopupManagerForDrawings popupManagerForDrawings)
/*     */   {
/*  94 */     this.chartBean = chartBean;
/*  95 */     this.guiRefresher = guiRefresher;
/*  96 */     this.dataManager = dataManager;
/*  97 */     this.indicatorsContainer = indicatorsContainer;
/*  98 */     this.drawingsManagerContainer = drawingsManagerContainer;
/*  99 */     this.ordersController = ordersController;
/* 100 */     this.mainOperationManager = mainOperationManager;
/* 101 */     this.chartWrapper = iChartWrapper;
/* 102 */     this.chartSystemListenerManager = chartSystemListenerManager;
/* 103 */     this.chartState = chartState;
/* 104 */     this.geometryCalculator = geometryCalculator;
/* 105 */     this.popupManagerForDrawings = popupManagerForDrawings;
/*     */ 
/* 107 */     this.chartWrapper.setChartController(this);
/* 108 */     this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */   }
/*     */ 
/*     */   public ChartBean getChartBean()
/*     */   {
/* 113 */     return this.chartBean;
/*     */   }
/*     */ 
/*     */   public JComponent getChartsContainer()
/*     */   {
/* 118 */     return this.guiRefresher.getChartsContainer();
/*     */   }
/*     */ 
/*     */   public void addMainChart()
/*     */   {
/* 123 */     this.guiRefresher.createMainChartView();
/*     */   }
/*     */ 
/*     */   public IChart getIChart()
/*     */   {
/* 128 */     return this.chartWrapper;
/*     */   }
/*     */ 
/*     */   public JComponent getMainContainer()
/*     */   {
/* 133 */     return this.guiRefresher.getMainContainer();
/*     */   }
/*     */ 
/*     */   public void startDrawing(IChart.Type drawingType)
/*     */   {
/* 140 */     this.mainOperationManager.startDrawing(drawingType);
/*     */   }
/*     */ 
/*     */   public void addDrawings(List<IChartObject> chartObjects)
/*     */   {
/* 145 */     this.drawingsManagerContainer.getMainDrawingsManager().addChartObjects(chartObjects);
/*     */   }
/*     */ 
/*     */   public void addDrawingToIndicator(Integer subPanelId, int indicatorId, IChartObject iChartObject)
/*     */   {
/* 150 */     ((SubDrawingsManagerImpl)this.drawingsManagerContainer.getSubDrawingsManagers(subPanelId.intValue()).get(Integer.valueOf(indicatorId))).addChartObject(iChartObject);
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject)
/*     */   {
/* 155 */     this.drawingsManagerContainer.remove(chartObject);
/*     */   }
/*     */ 
/*     */   public void remove(List<IChartObject> chartObjects)
/*     */   {
/* 160 */     this.drawingsManagerContainer.remove(chartObjects);
/*     */   }
/*     */ 
/*     */   public void removeAllDrawings()
/*     */   {
/* 165 */     this.drawingsManagerContainer.removeAllDrawings();
/*     */   }
/*     */ 
/*     */   public void selectDrawing(IChartObject chartObject)
/*     */   {
/* 170 */     this.drawingsManagerContainer.selectDrawing(chartObject);
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEdited()
/*     */   {
/* 175 */     this.drawingsManagerContainer.unselectDrawingToBeEdited();
/*     */   }
/*     */ 
/*     */   public double getYAxisPadding()
/*     */   {
/* 180 */     return this.drawingsManagerContainer.getMainValueToYMapper().getPadding();
/*     */   }
/*     */ 
/*     */   public List<IndicatorWrapper> getIndicators()
/*     */   {
/* 187 */     return this.indicatorsContainer.getIndicators();
/*     */   }
/*     */ 
/*     */   public void addIndicators(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/* 192 */     if (indicatorWrappers == null) {
/* 193 */       return;
/*     */     }
/* 195 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers)
/* 196 */       addIndicator(indicatorWrapper);
/*     */   }
/*     */ 
/*     */   public void addIndicator(IndicatorWrapper indicatorWrapper)
/*     */   {
/* 202 */     if (indicatorWrapper == null) {
/* 203 */       return;
/*     */     }
/* 205 */     if (indicatorWrapper.shouldBeShownOnSubWin()) {
/* 206 */       Integer subChartViewId = Integer.valueOf(this.guiRefresher.createSubChartView());
/*     */       try {
/* 208 */         this.guiRefresher.addSubIndicatorToSubChartView(subChartViewId.intValue(), indicatorWrapper);
/* 209 */         this.indicatorsContainer.addIndicator(indicatorWrapper, subChartViewId.intValue());
/*     */       } catch (Exception e) {
/* 211 */         this.guiRefresher.deleteSubIndicatorFromSubChartView(subChartViewId.intValue(), indicatorWrapper);
/* 212 */         this.guiRefresher.deleteSubChartView(subChartViewId);
/* 213 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     } else {
/*     */       try {
/* 217 */         this.indicatorsContainer.addIndicator(indicatorWrapper, -1);
/*     */       } catch (Exception e) {
/* 219 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addIndicatorOnSubWin(Integer subChartId, IndicatorWrapper indicatorWrapper)
/*     */   {
/* 226 */     if ((indicatorWrapper == null) || (subChartId == null)) {
/* 227 */       return;
/*     */     }
/* 229 */     if (indicatorWrapper.shouldBeShownOnSubWin()) {
/* 230 */       if (!this.guiRefresher.doesSubViewExists(subChartId))
/* 231 */         this.guiRefresher.createSubChartView(subChartId);
/*     */       try
/*     */       {
/* 234 */         this.guiRefresher.addSubIndicatorToSubChartView(subChartId.intValue(), indicatorWrapper);
/* 235 */         this.indicatorsContainer.addIndicator(indicatorWrapper, subChartId.intValue());
/*     */       } catch (Exception e) {
/* 237 */         this.guiRefresher.deleteSubIndicatorFromSubChartView(subChartId.intValue(), indicatorWrapper);
/* 238 */         if (this.guiRefresher.isSubViewEmpty(subChartId)) {
/* 239 */           this.guiRefresher.deleteSubChartView(subChartId);
/*     */         }
/* 241 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     } else {
/*     */       try {
/* 245 */         this.indicatorsContainer.addIndicator(indicatorWrapper, -1);
/*     */       } catch (Exception e) {
/* 247 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void editIndicator(int subPanelId, IndicatorWrapper indicatorWrapper)
/*     */   {
/* 254 */     new EditIndicatorDialog(subPanelId, indicatorWrapper, (JFrame)getJFrame(), (IDataManagerAndIndicatorsContainer)this.dataManager, this.guiRefresher, getChartState().getPeriod(), getChartState().getDataType()).dispose();
/*     */   }
/*     */ 
/*     */   public void deleteIndicator(IndicatorWrapper indicatorWrapper)
/*     */   {
/*     */     try
/*     */     {
/* 268 */       this.indicatorsContainer.deleteIndicator(indicatorWrapper);
/*     */     } catch (Exception e) {
/* 270 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/* 272 */       if ((indicatorWrapper != null) && (indicatorWrapper.shouldBeShownOnSubWin()))
/* 273 */         cleanupSubViews(indicatorWrapper);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteIndicators(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/* 280 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 281 */       Integer subWindowId = this.guiRefresher.getSubChartViewIdFor(indicatorWrapper.getId());
/* 282 */       if (subWindowId != null) {
/* 283 */         ((SubDrawingsManagerImpl)this.drawingsManagerContainer.getSubDrawingsManagers(subWindowId.intValue()).get(Integer.valueOf(indicatorWrapper.getId()))).removeAllDrawings();
/*     */       }
/*     */     }
/* 286 */     this.indicatorsContainer.deleteIndicators(indicatorWrappers);
/*     */   }
/*     */ 
/*     */   private void checkAndChangeCandleType(DataType.DataPresentationType newCandleType)
/*     */   {
/* 292 */     if (getChartState().getCandleType().equals(newCandleType)) {
/* 293 */       return;
/*     */     }
/* 295 */     getChartState().setCandleType(newCandleType);
/* 296 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().candleTypeChanged(newCandleType);
/*     */   }
/*     */ 
/*     */   public void setLineType(DataType.DataPresentationType timePeriodAggregationPresentationType)
/*     */   {
/* 301 */     DataType.DataPresentationType newCandleType = timePeriodAggregationPresentationType;
/*     */ 
/* 303 */     checkAndChangeCandleType(newCandleType);
/*     */ 
/* 305 */     if (DataType.DataPresentationType.TABLE.equals(newCandleType)) {
/* 306 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */     }
/*     */     else {
/* 309 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/* 310 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkAndChangeTickType(DataType.DataPresentationType newTickType) {
/* 315 */     if (getChartState().getTickType().equals(newTickType)) {
/* 316 */       return;
/*     */     }
/* 318 */     getChartState().setTickType(newTickType);
/* 319 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().tickTypeChanged(newTickType);
/*     */   }
/*     */ 
/*     */   public void setTickType(DataType.DataPresentationType ticksPresentationType)
/*     */   {
/* 324 */     DataType.DataPresentationType newTickType = ticksPresentationType;
/*     */ 
/* 326 */     checkAndChangeTickType(newTickType);
/*     */ 
/* 328 */     if (DataType.DataPresentationType.TABLE.equals(newTickType)) {
/* 329 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */     }
/*     */     else {
/* 332 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */ 
/* 334 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMouseCursorVisible(boolean isMouseCursorVisible)
/*     */   {
/* 340 */     getChartState().setMouseCursorVisible(isMouseCursorVisible);
/* 341 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().mouseCursorVisibilityChanged(isMouseCursorVisible);
/* 342 */     this.guiRefresher.refreshAllContent();
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period selectedPeriod)
/*     */   {
/* 349 */     setPeriod(selectedPeriod, true);
/*     */   }
/*     */ 
/*     */   private void setPeriod(Period selectedPeriod, boolean needRefresh) {
/* 353 */     Period oldPeriod = getChartState().getPeriod();
/* 354 */     if (oldPeriod == selectedPeriod) {
/* 355 */       return;
/*     */     }
/*     */ 
/* 358 */     this.mainOperationManager.setPeriod(selectedPeriod);
/* 359 */     this.dataManager.setPeriod(oldPeriod, selectedPeriod);
/* 360 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().periodChanged(selectedPeriod);
/*     */ 
/* 362 */     if (needRefresh)
/* 363 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide newOfferSide)
/*     */   {
/* 369 */     if (!this.chartState.getOfferSide().equals(newOfferSide)) {
/* 370 */       this.chartWrapper.offerSideChanged(newOfferSide);
/* 371 */       this.mainOperationManager.setOfferSide(newOfferSide);
/* 372 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().offerSideChanged(newOfferSide);
/*     */ 
/* 374 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setVerticalChartMovementEnabled(boolean isVerticalChartMovementEnabled)
/*     */   {
/* 380 */     this.mainOperationManager.setVerticalChartMovementEnabled(isVerticalChartMovementEnabled);
/* 381 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().verticalChartMovementChanged(isVerticalChartMovementEnabled);
/* 382 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void shiftChartToFront()
/*     */   {
/* 387 */     this.mainOperationManager.shiftChartToFront();
/* 388 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void shiftChart(int unitsCount)
/*     */   {
/* 393 */     this.mainOperationManager.moveTimeFrame(unitsCount);
/* 394 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void setCustomRange(int unitsBefore, long time, int unitsAfter)
/*     */   {
/* 399 */     this.mainOperationManager.setCustomRange(unitsBefore, time, unitsAfter);
/*     */   }
/*     */ 
/*     */   public void moveTimeFrame(int draggedXFrom, int draggedXTo, int draggedYFrom, int draggedYTo, boolean isNewDragging)
/*     */   {
/* 404 */     this.mainOperationManager.moveTimeFrame(draggedXFrom, draggedXTo, draggedYFrom, draggedYTo, isNewDragging);
/*     */   }
/*     */ 
/*     */   public void moveTimeFrameSilent(int draggedXFrom, int draggedXTo, int draggedYFrom, int draggedYTo, boolean isNewDragging)
/*     */   {
/* 409 */     this.mainOperationManager.moveTimeFrameSilent(draggedXFrom, draggedXTo, draggedYFrom, draggedYTo, isNewDragging);
/* 410 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void shiftChartSilent(int unitsCount)
/*     */   {
/* 415 */     this.mainOperationManager.moveTimeFrameSilent(unitsCount);
/* 416 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void zoomIn()
/*     */   {
/* 421 */     this.mainOperationManager.zoomIn();
/* 422 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void zoomOut()
/*     */   {
/* 427 */     this.mainOperationManager.zoomOut();
/* 428 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void startZoomingToArea()
/*     */   {
/* 433 */     this.mainOperationManager.startZoomingToArea();
/* 434 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public void addProgressListener(ProgressListener progressListener)
/*     */   {
/* 441 */     this.dataManager.addProgressListener(progressListener);
/*     */   }
/*     */ 
/*     */   public void addDisabledEnableListener(DisableEnableListener disableEnableListener)
/*     */   {
/* 446 */     this.chartSystemListenerManager.getDisableEnableListenersRegistry().registerListener(disableEnableListener);
/*     */   }
/*     */ 
/*     */   public void addChartsActionListener(DDSChartsActionListener ddsChartsActionListener)
/*     */   {
/* 451 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().registerListener(ddsChartsActionListener);
/*     */   }
/*     */ 
/*     */   public void addChartModeChangeListener(ChartModeChangeListener chartModeChangeListener)
/*     */   {
/* 456 */     this.chartSystemListenerManager.getChartModeChangeListenersRegistry().addListener(chartModeChangeListener);
/*     */   }
/*     */ 
/*     */   public void startLoadingData(boolean autoShiftActive, int chartShiftInPx)
/*     */   {
/* 461 */     if (chartShiftInPx != -1) {
/* 462 */       getChartState().setChartShiftHandlerCoordinate(chartShiftInPx);
/*     */     }
/* 464 */     getChartState().setChartShiftActive(autoShiftActive);
/*     */ 
/* 466 */     this.dataManager.start();
/* 467 */     this.ordersController.start();
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 472 */     this.dataManager.dispose();
/* 473 */     this.ordersController.dispose();
/*     */   }
/*     */ 
/*     */   public void refreshContent()
/*     */   {
/* 478 */     this.guiRefresher.refreshAllContent();
/*     */   }
/*     */ 
/*     */   public void saveWorkspaceImageToFile(File file)
/*     */   {
/* 483 */     if (file == null) {
/* 484 */       return;
/*     */     }
/*     */ 
/* 487 */     JComponent activeChartWindow = this.guiRefresher.getChartsContainer();
/* 488 */     BufferedImage image = getWorkspaceImage();
/*     */     try
/*     */     {
/* 491 */       SnapshotHelper.saveImageToAFile(image, file);
/*     */     } catch (Exception e) {
/* 493 */       JOptionPane.showMessageDialog(activeChartWindow, e.getMessage(), "Failed to save chart to the image", 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void saveWorkspaceImageToClipboard()
/*     */   {
/* 499 */     JComponent activeChartWindow = this.guiRefresher.getChartsContainer();
/* 500 */     BufferedImage image = SnapshotHelper.writeToImage(activeChartWindow);
/* 501 */     SnapshotHelper.saveImageToClipboard(image, activeChartWindow);
/*     */   }
/*     */ 
/*     */   public void printWorkspaceImage()
/*     */   {
/* 506 */     JComponent activeChartWindow = this.guiRefresher.getChartsContainer();
/* 507 */     PrintUtilities.printComponent(activeChartWindow);
/*     */   }
/*     */ 
/*     */   public BufferedImage getWorkspaceImage()
/*     */   {
/* 515 */     JComponent activeChartWindow = this.guiRefresher.getChartsContainer();
/* 516 */     return SnapshotHelper.writeToImage(activeChartWindow);
/*     */   }
/*     */ 
/*     */   public void selectCustomRange(List<JForexPeriod> allowedPeriods)
/*     */   {
/* 521 */     CustomRange customRange = new CustomRange(new JForexPeriod(this.dataManager.getDataType(), this.dataManager.getPeriod(), this.dataManager.getPriceRange(), this.dataManager.getReversalAmount(), this.dataManager.getTickBarSize()), getTime(), this.geometryCalculator.getDataUnitsCount(), 0);
/*     */ 
/* 528 */     new CustomRangeDialog(this.guiRefresher.getChartsContainer(), customRange, new ActionListener(customRange)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 534 */         ChartController.this.mainOperationManager.setCustomRange(this.val$customRange);
/*     */       }
/*     */     }
/*     */     , this.dataManager, allowedPeriods);
/*     */   }
/*     */ 
/*     */   public List<IndicatorWrapper> createAddEditIndicatorsDialog()
/*     */   {
/* 544 */     AddIndicatorDialog indicatorDialog = new AddIndicatorDialog((JFrame)getJFrame(), (IDataManagerAndIndicatorsContainer)this.dataManager, this.guiRefresher, getChartState().getPeriod(), getChartState().getDataType());
/*     */ 
/* 552 */     List indicators = indicatorDialog.getIndicators();
/* 553 */     indicatorDialog.dispose();
/* 554 */     return indicators;
/*     */   }
/*     */ 
/*     */   public JPopupMenu createPopupMenuForDrawings(IChartObject drawing, Component component, Point locationOnScreen)
/*     */   {
/* 559 */     return this.popupManagerForDrawings.createPopup((ChartObject)drawing, this.guiRefresher, locationOnScreen);
/*     */   }
/*     */ 
/*     */   private Component getJFrame()
/*     */   {
/* 565 */     Component parent = this.guiRefresher.getMainContainer().getParent();
/* 566 */     while ((parent != null) && (!(parent instanceof JFrame))) {
/* 567 */       parent = parent.getParent();
/*     */     }
/* 569 */     return parent;
/*     */   }
/*     */ 
/*     */   private void cleanupSubViews(IndicatorWrapper indicatorWrapper) {
/* 573 */     Integer subWindowId = this.guiRefresher.getSubChartViewIdFor(indicatorWrapper.getId());
/* 574 */     LinkedHashMap subDrawingsManagers = this.drawingsManagerContainer.getSubDrawingsManagers(subWindowId.intValue());
/* 575 */     if (subDrawingsManagers != null) {
/* 576 */       SubDrawingsManagerImpl drawingsManager = (SubDrawingsManagerImpl)subDrawingsManagers.get(Integer.valueOf(indicatorWrapper.getId()));
/* 577 */       if (drawingsManager != null) {
/* 578 */         drawingsManager.removeAllDrawings();
/*     */       }
/*     */     }
/* 581 */     this.guiRefresher.deleteSubIndicatorFromSubChartView(subWindowId.intValue(), indicatorWrapper);
/* 582 */     if (this.guiRefresher.isSubViewEmpty(subWindowId))
/* 583 */       this.guiRefresher.deleteSubChartView(subWindowId);
/*     */     else
/* 585 */       this.guiRefresher.refreshSubContentBySubViewId(subWindowId.intValue());
/*     */   }
/*     */ 
/*     */   public ChartState getChartState()
/*     */   {
/* 592 */     return this.chartState;
/*     */   }
/*     */ 
/*     */   public long getTime()
/*     */   {
/* 597 */     return this.dataManager.getTime();
/*     */   }
/*     */ 
/*     */   public double getMinPrice()
/*     */   {
/* 602 */     return this.mainOperationManager.getMainOperationManager().getChartMinPrice();
/*     */   }
/*     */ 
/*     */   public double getMaxPrice()
/*     */   {
/* 607 */     return this.mainOperationManager.getMainOperationManager().getChartMaxPrice();
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument selectedInstrument)
/*     */   {
/* 612 */     Instrument oldInstrumen = getChartState().getInstrument();
/* 613 */     if (selectedInstrument != oldInstrumen)
/*     */     {
/* 615 */       getIChart().setInstrument(selectedInstrument);
/* 616 */       getChartState().setInstrument(selectedInstrument);
/* 617 */       this.dataManager.setInstrument(oldInstrumen, selectedInstrument);
/* 618 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().instrumentChanged(selectedInstrument);
/*     */ 
/* 620 */       this.ordersController.changeInstrument(selectedInstrument);
/*     */ 
/* 622 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   public GeometryCalculator getGeometryCalculator() {
/* 627 */     return this.geometryCalculator;
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getMainChartDrawings()
/*     */   {
/* 633 */     return this.drawingsManagerContainer.getMainDrawingsManager().getChartObjects();
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getSubChartDrawings(int subPanelId, int indicatorId)
/*     */   {
/* 638 */     LinkedHashMap subDrawingsManagers = this.drawingsManagerContainer.getSubDrawingsManagers(subPanelId);
/* 639 */     if (subDrawingsManagers == null) {
/* 640 */       return null;
/*     */     }
/* 642 */     SubDrawingsManagerImpl subDrawingsManagerImpl = (SubDrawingsManagerImpl)subDrawingsManagers.get(Integer.valueOf(indicatorId));
/* 643 */     if (subDrawingsManagerImpl == null) {
/* 644 */       return null;
/*     */     }
/* 646 */     List chartObjects = subDrawingsManagerImpl.getChartObjects();
/* 647 */     return chartObjects;
/*     */   }
/*     */ 
/*     */   public void saveChartTableDataToFile(File file)
/*     */   {
/* 652 */     if (file == null) {
/* 653 */       return;
/*     */     }
/*     */ 
/* 656 */     DataTablePresentationAbstractJTable table = this.guiRefresher.getCurrentChartDataTable();
/* 657 */     TableModel tableModel = table.getModel();
/*     */     try
/*     */     {
/* 660 */       Writer out = new BufferedWriter(new FileWriter(file));
/* 661 */       for (int y = 0; y < tableModel.getRowCount(); y++) {
/* 662 */         for (int x = 0; x < tableModel.getColumnCount(); x++) {
/* 663 */           Object value = tableModel.getValueAt(y, x);
/* 664 */           if ((value instanceof Date)) {
/* 665 */             String formatedData = this.dateFormat.format(Long.valueOf(((Date)value).getTime()));
/* 666 */             out.write(formatedData);
/*     */           } else {
/* 668 */             out.write(String.valueOf(value));
/*     */           }
/*     */ 
/* 671 */           if (x < tableModel.getColumnCount() - 1) {
/* 672 */             out.write(",");
/*     */           }
/*     */         }
/* 675 */         out.write("\r\n");
/*     */       }
/*     */ 
/* 678 */       out.close();
/*     */     } catch (IOException e) {
/* 680 */       LOGGER.error(e.getMessage(), e);
/* 681 */       SwingUtilities.invokeLater(new Runnable(e)
/*     */       {
/*     */         public void run() {
/* 684 */           LocalizedMessageHelper.showErrorMessage(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.save.data", new Object[] { this.val$e.getMessage() }));
/*     */         }
/*     */       });
/* 687 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void applyQuickTableDataFilter(String pattern)
/*     */   {
/* 693 */     this.guiRefresher.getPriceRangeTablePresentationManager().applyQuickFilter(pattern);
/* 694 */     this.guiRefresher.getCandleTablePresentationManager().applyQuickFilter(pattern);
/* 695 */     this.guiRefresher.getTickTablePresentationManager().applyQuickFilter(pattern);
/* 696 */     this.guiRefresher.getPointAndFigureTablePresentationManager().applyQuickFilter(pattern);
/* 697 */     this.guiRefresher.getTickBarTablePresentationManager().applyQuickFilter(pattern);
/* 698 */     this.guiRefresher.getRenkoTablePresentationManager().applyQuickFilter(pattern);
/*     */   }
/*     */ 
/*     */   private void switchTableChartMode(DataType newDataType) {
/* 702 */     switch (3.$SwitchMap$com$dukascopy$api$DataType[newDataType.ordinal()]) {
/*     */     case 1:
/* 704 */       if (DataType.DataPresentationType.TABLE.equals(getChartState().getTickType()))
/* 705 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */       else {
/* 707 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */       }
/* 709 */       break;
/*     */     case 2:
/* 712 */       if (DataType.DataPresentationType.TABLE.equals(getChartState().getCandleType()))
/* 713 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */       else {
/* 715 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */       }
/* 717 */       break;
/*     */     case 3:
/* 720 */       if (DataType.DataPresentationType.TABLE.equals(getChartState().getPriceRangesPresentationType()))
/* 721 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */       else {
/* 723 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */       }
/* 725 */       break;
/*     */     case 4:
/* 728 */       if (DataType.DataPresentationType.TABLE.equals(getChartState().getPointAndFigurePresentationType()))
/* 729 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */       else {
/* 731 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */       }
/* 733 */       break;
/*     */     case 5:
/* 736 */       if (DataType.DataPresentationType.TABLE.equals(getChartState().getTickBarPresentationType()))
/* 737 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */       else {
/* 739 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */       }
/* 741 */       break;
/*     */     case 6:
/* 744 */       if (DataType.DataPresentationType.TABLE.equals(getChartState().getRenkoPresentationType()))
/* 745 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */       else {
/* 747 */         this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */       }
/* 749 */       break;
/*     */     default:
/* 751 */       throw new IllegalArgumentException("Unsupported Data Type : " + newDataType);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void changePriceRange(PriceRange priceRange)
/*     */   {
/* 757 */     changePriceRange(priceRange, true);
/*     */   }
/*     */ 
/*     */   private void changePriceRange(PriceRange priceRange, boolean needRefresh) {
/* 761 */     PriceRange oldPriceRange = getChartState().getPriceRange();
/* 762 */     if (oldPriceRange != priceRange) {
/* 763 */       this.dataManager.priceRangeChanged(priceRange);
/*     */ 
/* 765 */       getChartState().setPriceRange(priceRange);
/* 766 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().priceRangeChanged(priceRange);
/*     */ 
/* 768 */       if (needRefresh)
/* 769 */         refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkAndChangePriceRangePresentationType(DataType.DataPresentationType priceRangePresentationType)
/*     */   {
/* 775 */     if (getChartState().getPriceRangesPresentationType().equals(priceRangePresentationType)) {
/* 776 */       return;
/*     */     }
/* 778 */     getChartState().setPriceRangesPresentationType(priceRangePresentationType);
/* 779 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().priceRangesPresentationTypeChanged(priceRangePresentationType);
/*     */   }
/*     */ 
/*     */   public void changePriceRangePresentationType(DataType.DataPresentationType priceRangePresentationType)
/*     */   {
/* 784 */     checkAndChangePriceRangePresentationType(priceRangePresentationType);
/*     */ 
/* 786 */     if (DataType.DataPresentationType.TABLE.equals(priceRangePresentationType)) {
/* 787 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */     } else {
/* 789 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */ 
/* 791 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOrderLineVisible(String orderId, boolean visible)
/*     */   {
/* 797 */     this.ordersController.setOrderLineVisible(orderId, visible);
/*     */   }
/*     */ 
/*     */   public void changeJForexPeriod(JForexPeriod jForexPeriod)
/*     */   {
/* 802 */     if ((getChartState().getPeriod() != jForexPeriod.getPeriod()) || (getChartState().getPriceRange() != jForexPeriod.getPriceRange()) || (getChartState().getReversalAmount() != jForexPeriod.getReversalAmount()) || (getChartState().getDataType() != jForexPeriod.getDataType()) || (getChartState().getTickBarSize() != jForexPeriod.getTickBarSize()))
/*     */     {
/* 810 */       DataType oldDataType = getChartState().getDataType();
/* 811 */       this.dataManager.setJForexPeriod(jForexPeriod);
/* 812 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().jForexPeriodChanged(jForexPeriod);
/*     */ 
/* 814 */       if (!oldDataType.equals(jForexPeriod.getDataType())) {
/* 815 */         this.chartSystemListenerManager.getChartsActionListenerRegistry().dataTypeChanged(jForexPeriod.getDataType());
/*     */       }
/*     */ 
/* 818 */       switchTableChartMode(jForexPeriod.getDataType());
/*     */ 
/* 820 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reactivate()
/*     */   {
/* 826 */     this.dataManager.reactivate();
/*     */   }
/*     */ 
/*     */   public void changeReversalAmount(ReversalAmount reversalAmount)
/*     */   {
/* 831 */     changeReversalAmount(reversalAmount, true);
/*     */   }
/*     */ 
/*     */   private void changeReversalAmount(ReversalAmount reversalAmount, boolean needRefresh) {
/* 835 */     ReversalAmount oldReversalAmount = getChartState().getReversalAmount();
/* 836 */     if (oldReversalAmount != reversalAmount) {
/* 837 */       this.dataManager.reversalAmountChanged(reversalAmount);
/*     */ 
/* 839 */       getChartState().setReversalAmount(reversalAmount);
/* 840 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().reversalAmountChanged(reversalAmount);
/*     */ 
/* 842 */       if (needRefresh)
/* 843 */         refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void refreshAllContentAndPutFocusOnMainChartView()
/*     */   {
/* 849 */     refreshContent();
/* 850 */     this.guiRefresher.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   private void checkAndChangePointAndFigurePresentationType(DataType.DataPresentationType pointAndFigurePresentationType) {
/* 854 */     if (getChartState().getPointAndFigurePresentationType().equals(pointAndFigurePresentationType)) {
/* 855 */       return;
/*     */     }
/* 857 */     getChartState().setPointAndFigurePresentationType(pointAndFigurePresentationType);
/* 858 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().pointAndFigurePresentationTypeChanged(pointAndFigurePresentationType);
/*     */   }
/*     */ 
/*     */   public void changePointAndFigurePresentationType(DataType.DataPresentationType pointAndFigurePresentationType)
/*     */   {
/* 863 */     checkAndChangePointAndFigurePresentationType(pointAndFigurePresentationType);
/*     */ 
/* 865 */     if (DataType.DataPresentationType.TABLE.equals(pointAndFigurePresentationType)) {
/* 866 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */     } else {
/* 868 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */ 
/* 870 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkAndChangeTickBarPresentationType(DataType.DataPresentationType tickBarPresentationType) {
/* 875 */     if (getChartState().getTickBarPresentationType().equals(tickBarPresentationType)) {
/* 876 */       return;
/*     */     }
/* 878 */     getChartState().setTickBarPresentationType(tickBarPresentationType);
/* 879 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().tickBarPresentationTypeChanged(tickBarPresentationType);
/*     */   }
/*     */ 
/*     */   public void changeTickBarPresentationType(DataType.DataPresentationType tickBarPresentationType)
/*     */   {
/* 885 */     checkAndChangeTickBarPresentationType(tickBarPresentationType);
/*     */ 
/* 887 */     if (DataType.DataPresentationType.TABLE.equals(tickBarPresentationType)) {
/* 888 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */     } else {
/* 890 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */ 
/* 892 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void changeRenkoPresentationType(DataType.DataPresentationType renkoPresentationType)
/*     */   {
/* 898 */     checkAndChangeRenkoPresentationType(renkoPresentationType);
/*     */ 
/* 900 */     if (DataType.DataPresentationType.TABLE.equals(renkoPresentationType)) {
/* 901 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.TABLE);
/*     */     } else {
/* 903 */       this.chartSystemListenerManager.getChartModeChangeListenersRegistry().changeMode(ChartModeChangeListener.ChartMode.CHART);
/*     */ 
/* 905 */       refreshAllContentAndPutFocusOnMainChartView();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkAndChangeRenkoPresentationType(DataType.DataPresentationType renkoPresentationType) {
/* 910 */     if (getChartState().getRenkoPresentationType().equals(renkoPresentationType)) {
/* 911 */       return;
/*     */     }
/* 913 */     getChartState().setRenkoPresentationType(renkoPresentationType);
/* 914 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().renkoPresentationTypeChanged(renkoPresentationType);
/*     */   }
/*     */ 
/*     */   public IChartObject getDrawingByKey(String key)
/*     */   {
/* 919 */     IChartObject chartObjectForKey = this.drawingsManagerContainer.getMainDrawingsManager().getDrawingByKey(key);
/* 920 */     if (chartObjectForKey != null) {
/* 921 */       return chartObjectForKey;
/*     */     }
/*     */ 
/* 924 */     List subManagers = this.drawingsManagerContainer.getSubDrawingManagers();
/*     */ 
/* 926 */     if (subManagers != null) {
/* 927 */       for (SubDrawingsManagerImpl subManager : subManagers) {
/* 928 */         chartObjectForKey = subManager.getDrawingByKey(key);
/* 929 */         if (chartObjectForKey != null) {
/* 930 */           return chartObjectForKey;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 935 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTheme(ITheme theme)
/*     */   {
/* 940 */     this.chartState.setTheme(theme);
/*     */   }
/*     */ 
/*     */   public ITheme getTheme()
/*     */   {
/* 945 */     return this.chartState.getTheme();
/*     */   }
/*     */ 
/*     */   public void setVerticalAxisScale(double minPrice, double maxPrice) {
/* 949 */     this.mainOperationManager.setVerticalAxisScale(minPrice, maxPrice);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ChartController
 * JD-Core Version:    0.6.0
 */