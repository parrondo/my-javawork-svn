/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.dialogs.customrange.CustomRange;
/*     */ import com.dukascopy.charts.drawings.NewDrawingsCoordinator;
/*     */ import com.dukascopy.charts.indicators.IndicatorsManagerImpl;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsActionListener;
/*     */ import com.dukascopy.charts.mappers.value.ValueFrame;
/*     */ import com.dukascopy.charts.orders.OrdersManagerImpl;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class MainOperationManager
/*     */   implements IMainOperationManager
/*     */ {
/*  23 */   private static final Logger LOGGER = LoggerFactory.getLogger(MainOperationManager.class);
/*     */   private final ChartState chartState;
/*     */   private final GuiRefresher guiRefresher;
/*     */   private final IndicatorsManagerImpl indicatorsManagerImpl;
/*     */   private final NewDrawingsCoordinator newDrawingsCoordinator;
/*     */   private final MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState;
/*     */   private final DDSChartsActionListener chartsActionListenerRegistry;
/*     */   private final IOperationManagerStrategy tickOperationManagerStrategy;
/*     */   private final IOperationManagerStrategy candleOperationManagerStrategy;
/*     */   private final IOperationManagerStrategy priceRangeOperationManagerStrategy;
/*     */   private final IOperationManagerStrategy pointAndFigureOperationManagerStrategy;
/*     */   private final IOperationManagerStrategy tickBarOperationManagerStrategy;
/*     */   private final IOperationManagerStrategy renkoOperationManagerStrategy;
/*     */   private final IDataManager dataManager;
/*     */   private final OrdersManagerImpl ordersManagerImpl;
/*     */   private final ValueFrame valueFrame;
/*  46 */   private final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  47 */   private final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");
/*     */ 
/*     */   MainOperationManager(ChartState chartState, GuiRefresher guiRefresher, IndicatorsManagerImpl indicatorsManagerImpl, NewDrawingsCoordinator newDrawingsCoordinator, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, DDSChartsActionListener chartsActionListenerRegistry, IOperationManagerStrategy candleOperationManagerStrategy, IOperationManagerStrategy tickOperationManagerStrategy, IOperationManagerStrategy priceRangeOperationManagerStrategy, IOperationManagerStrategy pointAndFigureOperationManagerStrategy, IOperationManagerStrategy tickBarOperationManagerStrategy, IOperationManagerStrategy renkoOperationManagerStrategy, IDataManager dataManager, OrdersManagerImpl ordersManagerImpl, ValueFrame valueFrame)
/*     */   {
/*  66 */     this.guiRefresher = guiRefresher;
/*  67 */     this.indicatorsManagerImpl = indicatorsManagerImpl;
/*  68 */     this.newDrawingsCoordinator = newDrawingsCoordinator;
/*  69 */     this.chartsActionListenerRegistry = chartsActionListenerRegistry;
/*  70 */     this.chartState = chartState;
/*  71 */     this.mouseControllerMetaDrawingsState = mouseControllerMetaDrawingsState;
/*     */ 
/*  73 */     this.candleOperationManagerStrategy = candleOperationManagerStrategy;
/*  74 */     this.tickOperationManagerStrategy = tickOperationManagerStrategy;
/*  75 */     this.priceRangeOperationManagerStrategy = priceRangeOperationManagerStrategy;
/*  76 */     this.pointAndFigureOperationManagerStrategy = pointAndFigureOperationManagerStrategy;
/*  77 */     this.tickBarOperationManagerStrategy = tickBarOperationManagerStrategy;
/*  78 */     this.renkoOperationManagerStrategy = renkoOperationManagerStrategy;
/*     */ 
/*  80 */     this.dataManager = dataManager;
/*  81 */     this.ordersManagerImpl = ordersManagerImpl;
/*     */ 
/*  83 */     this.valueFrame = valueFrame;
/*     */   }
/*     */ 
/*     */   public void moveTimeFrame(int draggedFromPx, int draggedToPx, int draggedYFrom, int draggedYTo, boolean isNewDragging)
/*     */   {
/*  88 */     moveTimeFrame(draggedFromPx, draggedToPx, draggedYFrom, draggedYTo, isNewDragging, true);
/*     */   }
/*     */ 
/*     */   public void moveTimeFrameSilent(int draggedFromPx, int draggedToPx, int draggedYFrom, int draggedYTo, boolean isNewDragging)
/*     */   {
/*  93 */     moveTimeFrame(draggedFromPx, draggedToPx, draggedYFrom, draggedYTo, isNewDragging, false);
/*     */   }
/*     */ 
/*     */   private void moveTimeFrame(int draggedFromPx, int draggedToPx, int draggedYFrom, int draggedYTo, boolean isNewDragging, boolean callListeners) {
/*     */     try {
/*  98 */       this.chartState.setChartShiftActive(false);
/*  99 */       boolean hasMoved = getMainOperationManager().moveValueFrame(draggedYFrom, draggedYTo);
/* 100 */       hasMoved |= getMainOperationManager().moveTimeFrame(draggedFromPx, draggedToPx, isNewDragging);
/* 101 */       if (hasMoved) {
/* 102 */         this.guiRefresher.refreshAllContent();
/* 103 */         if (callListeners) {
/* 104 */           this.chartsActionListenerRegistry.timeFrameMoved(this.chartState.isChartShiftActive());
/* 105 */           this.chartsActionListenerRegistry.timeFrameMoved(draggedFromPx, draggedToPx, draggedYFrom, draggedYTo, isNewDragging);
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 109 */       LOGGER.error("Failed to move time frame due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void moveTimeFrame(int unitsMoved)
/*     */   {
/* 115 */     moveTimeFrame(unitsMoved, true);
/*     */   }
/*     */ 
/*     */   public void moveTimeFrameSilent(int unitsMoved)
/*     */   {
/* 121 */     moveTimeFrame(unitsMoved, false);
/*     */   }
/*     */ 
/*     */   private void moveTimeFrame(int unitsMoved, boolean callListeners) {
/*     */     try {
/* 126 */       this.chartState.setChartShiftActive(false);
/* 127 */       boolean hasMoved = getMainOperationManager().moveTimeFrame(unitsMoved);
/* 128 */       if (hasMoved) {
/* 129 */         this.guiRefresher.refreshAllContent();
/* 130 */         if (callListeners)
/* 131 */           this.chartsActionListenerRegistry.timeFrameMoved(this.chartState.isChartShiftActive(), unitsMoved);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 135 */       LOGGER.error("Failed to move time frame due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCustomRange(CustomRange customRange)
/*     */   {
/* 141 */     setCustomRange(customRange.getPeriod(), customRange.getBefore(), customRange.getTime(), customRange.getAfter());
/*     */   }
/*     */ 
/*     */   public void setCustomRange(JForexPeriod newPeriod, int unitsBefore, long time, int unitsAfter)
/*     */   {
/* 146 */     Period oldPeriod = this.chartState.getPeriod();
/* 147 */     DataType oldDataType = this.chartState.getDataType();
/* 148 */     if ((!oldDataType.equals(newPeriod.getDataType())) || (!oldPeriod.equals(newPeriod.getPeriod())))
/*     */     {
/* 150 */       if (!oldDataType.equals(newPeriod.getDataType())) {
/* 151 */         this.dataManager.changeDataType(oldDataType, newPeriod.getDataType());
/* 152 */         this.chartsActionListenerRegistry.dataTypeChanged(newPeriod.getDataType());
/*     */       }
/* 154 */       else if (!oldPeriod.equals(newPeriod.getPeriod())) {
/* 155 */         setPeriod(newPeriod.getPeriod());
/* 156 */         this.dataManager.setPeriod(oldPeriod, newPeriod.getPeriod());
/* 157 */         this.chartsActionListenerRegistry.periodChanged(newPeriod.getPeriod());
/*     */       }
/*     */ 
/* 160 */       this.chartsActionListenerRegistry.jForexPeriodChanged(newPeriod);
/*     */     }
/* 162 */     setCustomRange(unitsBefore, time, unitsAfter);
/*     */   }
/*     */ 
/*     */   public void setCustomRange(int unitsBefore, long time, int unitsAfter)
/*     */   {
/* 167 */     if (LOGGER.isDebugEnabled()) {
/* 168 */       this.dateFormat.setTimeZone(this.GMT_TIME_ZONE);
/* 169 */       LOGGER.debug("time = " + this.dateFormat.format(Long.valueOf(time)) + " unitsBefore = " + unitsBefore + " unitsAfter = " + unitsAfter);
/*     */     }
/*     */     try
/*     */     {
/* 173 */       this.newDrawingsCoordinator.unselectDrawingToBeEditedAndExitEditingMode();
/* 174 */       this.chartState.setChartShiftActive(false);
/* 175 */       getMainOperationManager().setCustomRange(unitsBefore, time, unitsAfter);
/* 176 */       this.chartsActionListenerRegistry.timeFrameMoved(false);
/* 177 */       this.guiRefresher.refreshAllContent();
/* 178 */       this.guiRefresher.setFocusToMainChartView();
/*     */     } catch (Exception e) {
/* 180 */       LOGGER.error("Failed to set custom range due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void moveTimeFrame(long startTime, long endTime)
/*     */   {
/*     */     try {
/* 187 */       this.chartState.setChartShiftActive(false);
/* 188 */       getMainOperationManager().moveTimeFrame(startTime, endTime);
/* 189 */       this.guiRefresher.refreshAllContent();
/*     */     } catch (Exception e) {
/* 191 */       LOGGER.error("Failed to move time frame due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void scaleTimeFrame(int draggingDiffPx)
/*     */   {
/*     */     try {
/* 198 */       this.newDrawingsCoordinator.unselectDrawingToBeEditedAndExitEditingMode();
/* 199 */       boolean scaled = getMainOperationManager().scaleTimeFrame(draggingDiffPx);
/* 200 */       if (scaled) {
/* 201 */         this.guiRefresher.refreshAllContent();
/* 202 */         this.chartsActionListenerRegistry.timeFrameMoved(this.chartState.isChartShiftActive());
/*     */       }
/* 204 */       performZoomButtonsState(scaled, draggingDiffPx);
/*     */     } catch (Exception e) {
/* 206 */       LOGGER.error("Failed to scale time frame due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void performZoomButtonsState(boolean scaled, int draggingDiffPx) {
/* 211 */     if (draggingDiffPx < 0) {
/* 212 */       this.chartsActionListenerRegistry.zoomInEnabled(true);
/* 213 */       this.chartsActionListenerRegistry.zoomOutEnabled(scaled);
/*     */     }
/* 215 */     else if (draggingDiffPx > 0) {
/* 216 */       this.chartsActionListenerRegistry.zoomInEnabled(scaled);
/* 217 */       this.chartsActionListenerRegistry.zoomOutEnabled(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void zoomIn()
/*     */   {
/*     */     try {
/* 224 */       boolean zoomedIn = getMainOperationManager().zoomIn();
/* 225 */       if (zoomedIn) {
/* 226 */         this.guiRefresher.refreshAllContent();
/* 227 */         this.chartsActionListenerRegistry.timeFrameMoved(this.chartState.isChartShiftActive());
/* 228 */         this.chartsActionListenerRegistry.zoomOutEnabled(true);
/*     */       }
/* 230 */       this.chartsActionListenerRegistry.zoomInEnabled(zoomedIn);
/*     */     }
/*     */     catch (Exception e) {
/* 233 */       LOGGER.error("Failed to zoom in due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void zoomOut()
/*     */   {
/*     */     try {
/* 240 */       boolean zoomedOut = getMainOperationManager().zoomOut();
/* 241 */       if (zoomedOut) {
/* 242 */         this.guiRefresher.refreshAllContent();
/* 243 */         this.chartsActionListenerRegistry.timeFrameMoved(this.chartState.isChartShiftActive());
/* 244 */         this.chartsActionListenerRegistry.zoomInEnabled(true);
/*     */       }
/* 246 */       this.chartsActionListenerRegistry.zoomOutEnabled(zoomedOut);
/*     */     } catch (Exception e) {
/* 248 */       LOGGER.error("Failed to zoom out due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void zoomToArea(int leftX, int leftY, int rightX, int rightY)
/*     */   {
/*     */     try {
/* 255 */       getMainOperationManager().zoomToArea(leftX, leftY, rightX, rightY);
/* 256 */       this.chartsActionListenerRegistry.timeFrameMoved(this.chartState.isChartShiftActive());
/* 257 */       this.chartsActionListenerRegistry.zoomOutEnabled(true);
/* 258 */       this.guiRefresher.refreshAllContent();
/*     */     } catch (Exception e) {
/* 260 */       LOGGER.error("Failed to zoom to area due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void scaleMainChartViewIn(int height)
/*     */   {
/*     */     try
/*     */     {
/* 268 */       getMainOperationManager().scaleMainValueFrame(true, height);
/* 269 */       this.guiRefresher.refreshMainContent();
/*     */     } catch (Exception e) {
/* 271 */       LOGGER.error("Failed to scale main chart view in due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void scaleMainChartViewOut(int height)
/*     */   {
/*     */     try {
/* 278 */       getMainOperationManager().scaleMainValueFrame(false, height);
/* 279 */       this.guiRefresher.refreshMainContent();
/*     */     } catch (Exception e) {
/* 281 */       LOGGER.error("Failed to scale main chart view out due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void scaleSubChartViewIn(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/*     */     try
/*     */     {
/* 291 */       getMainOperationManager().scaleSubValueFrame(subIndicatorGroup, true);
/* 292 */       this.guiRefresher.refreshSubContentBySubViewId(subIndicatorGroup.getSubWindowId());
/*     */     } catch (Exception e) {
/* 294 */       LOGGER.error("Failed to scale sub chart view in due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void scaleSubChartViewOut(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/*     */     try
/*     */     {
/* 304 */       getMainOperationManager().scaleSubValueFrame(subIndicatorGroup, false);
/* 305 */       this.guiRefresher.refreshSubContentBySubViewId(subIndicatorGroup.getSubWindowId());
/*     */     } catch (Exception e) {
/* 307 */       LOGGER.error("Failed to scale sub chart view out due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period newPeriod)
/*     */   {
/* 313 */     if (this.chartState.getPeriod() == newPeriod) {
/* 314 */       return;
/*     */     }
/* 316 */     this.chartState.setPeriod(newPeriod);
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide newOfferSide)
/*     */   {
/*     */     try {
/* 322 */       this.chartState.setOfferSide(newOfferSide);
/* 323 */       getMainOperationManager().offerSideChanged(newOfferSide);
/*     */     } catch (Exception e) {
/* 325 */       LOGGER.error("Failed to set new offer side due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startZoomingToArea()
/*     */   {
/*     */     try {
/* 332 */       this.newDrawingsCoordinator.unselectDrawingToBeEditedAndExitEditingMode();
/* 333 */       this.chartState.setChartShiftActive(false);
/* 334 */       this.mouseControllerMetaDrawingsState.setIsZoomingToArea(true);
/*     */     } catch (Exception e) {
/* 336 */       LOGGER.error("Zooming to area action cancelled due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startDrawing(IChart.Type drawingType)
/*     */   {
/*     */     try {
/* 343 */       this.indicatorsManagerImpl.unseletSelectedIndicator();
/* 344 */       this.ordersManagerImpl.unselectSeletedOrders();
/* 345 */       this.newDrawingsCoordinator.startDrawing(drawingType);
/* 346 */       this.chartState.setChartShiftActive(false);
/* 347 */       this.chartsActionListenerRegistry.timeFrameMoved(false);
/* 348 */       this.guiRefresher.refreshAllContent();
/*     */     } catch (Exception e) {
/* 350 */       LOGGER.error("Drawing action cancelled: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shiftChartToFront()
/*     */   {
/*     */     try {
/* 357 */       this.chartState.setChartShiftActive(true);
/* 358 */       getMainOperationManager().shiftChartToFront();
/* 359 */       this.chartsActionListenerRegistry.timeFrameMoved(true);
/* 360 */       this.guiRefresher.refreshAllContent();
/*     */     } catch (Exception e) {
/* 362 */       LOGGER.error("Failed to shift chart to front due to: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setVerticalChartMovementEnabled(boolean verticalChartMovementEnabled)
/*     */   {
/* 368 */     this.chartState.setVerticalChartMovementEnabled(verticalChartMovementEnabled);
/* 369 */     if (!verticalChartMovementEnabled) {
/* 370 */       getMainOperationManager().setVerticalChartMovementEnabled(verticalChartMovementEnabled);
/* 371 */       this.guiRefresher.refreshMainContent();
/*     */     }
/*     */   }
/*     */ 
/*     */   public IOperationManagerStrategy getMainOperationManager()
/*     */   {
/* 377 */     DataType dataType = this.chartState.getDataType();
/* 378 */     switch (1.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) { case 1:
/* 379 */       return this.tickOperationManagerStrategy;
/*     */     case 2:
/* 380 */       return this.candleOperationManagerStrategy;
/*     */     case 3:
/* 381 */       return this.priceRangeOperationManagerStrategy;
/*     */     case 4:
/* 382 */       return this.pointAndFigureOperationManagerStrategy;
/*     */     case 5:
/* 383 */       return this.tickBarOperationManagerStrategy;
/*     */     case 6:
/* 384 */       return this.renkoOperationManagerStrategy; }
/* 385 */     throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*     */   }
/*     */ 
/*     */   public void setVerticalAxisScale(double minPriceValue, double maxPriceValue)
/*     */   {
/* 391 */     this.valueFrame.changeMinMax(minPriceValue, maxPriceValue);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MainOperationManager
 * JD-Core Version:    0.6.0
 */