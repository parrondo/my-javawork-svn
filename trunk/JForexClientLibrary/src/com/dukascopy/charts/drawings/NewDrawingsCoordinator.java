/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.listeners.drawing.DrawingActionListener;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingsManagerContainer;
/*     */ 
/*     */ public class NewDrawingsCoordinator
/*     */ {
/*     */   private final IDrawingsManagerContainer drawingsManagerContainer;
/*     */   private final DrawingsFactory drawingsFactory;
/*     */   private final DrawingActionListener drawingActionListener;
/*     */   private final GuiRefresher guiRefresher;
/*     */   private final ChartsActionListenerRegistry chartsActionListenerRegistry;
/*     */   private final ChartState chartState;
/*     */   private Object newChartObjectCurrentOwner;
/*  25 */   private boolean isDrawingStatus = false;
/*     */ 
/*     */   public NewDrawingsCoordinator(IDrawingsManagerContainer drawingsManagerContainer, DrawingsFactory drawingsFactory, DrawingActionListener drawingActionListener, GuiRefresher guiRefresher, ChartsActionListenerRegistry chartsActionListenerRegistry, ChartState chartState)
/*     */   {
/*  36 */     this.drawingsManagerContainer = drawingsManagerContainer;
/*  37 */     this.drawingsFactory = drawingsFactory;
/*  38 */     this.drawingActionListener = drawingActionListener;
/*  39 */     this.guiRefresher = guiRefresher;
/*  40 */     this.chartsActionListenerRegistry = chartsActionListenerRegistry;
/*  41 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public void startDrawing(IChart.Type drawingType) {
/*  45 */     unselectDrawingToBeEditedAndExitEditingMode();
/*  46 */     ChartObject chartObject = this.drawingsFactory.createDrawing(drawingType);
/*  47 */     if (chartObject == null) {
/*  48 */       return;
/*     */     }
/*     */ 
/*  51 */     this.chartsActionListenerRegistry.chartObjectCreatedForNewDrawing(chartObject);
/*     */ 
/*  53 */     setNewChartObjectCurrentOwner(null);
/*  54 */     if ((chartObject instanceof AbstractWidgetChartObject)) {
/*  55 */       if ((chartObject instanceof IOhlcChartObject)) {
/*  56 */         chartObject.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.OHLC));
/*  57 */         ((IOhlcChartObject)chartObject).setFillColor(this.chartState.getTheme().getColor(ITheme.ChartElement.OHLC_BACKGROUND));
/*     */       }
/*  59 */       ((MainDrawingsManagerImpl)this.drawingsManagerContainer.getMainDrawingsManager()).addNewDrawing(chartObject);
/*     */     } else {
/*  61 */       chartObject.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.DRAWING));
/*     */ 
/*  63 */       this.drawingsManagerContainer.setNewChartObject(chartObject);
/*  64 */       this.drawingActionListener.drawingStarted(drawingType);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEditedAndExitEditingMode() {
/*  69 */     this.drawingsManagerContainer.unselectDrawingToBeEdited();
/*  70 */     this.drawingActionListener.drawingEditingEnded();
/*  71 */     this.drawingsManagerContainer.setNewChartObject(null);
/*  72 */     this.guiRefresher.refreshAllContent();
/*     */   }
/*     */ 
/*     */   public ChartObject getNewDrawing()
/*     */   {
/*  77 */     return this.drawingsManagerContainer.getNewChartObject();
/*     */   }
/*     */ 
/*     */   public boolean hasNewDrawing() {
/*  81 */     ChartObject newChartObject = getNewDrawing();
/*  82 */     if (newChartObject == null) {
/*  83 */       return false;
/*     */     }
/*     */ 
/*  86 */     return newChartObject.isUnderEdit();
/*     */   }
/*     */ 
/*     */   public boolean hasNewGlobalDrawing()
/*     */   {
/*  91 */     return (hasNewDrawing()) && (getNewDrawing().isGlobal());
/*     */   }
/*     */ 
/*     */   public void resetNewDrawing() {
/*  95 */     this.drawingsManagerContainer.setNewChartObject(null);
/*     */   }
/*     */ 
/*     */   public boolean isDrawingStatus() {
/*  99 */     return this.isDrawingStatus;
/*     */   }
/*     */ 
/*     */   public void setStatusIsDrawing() {
/* 103 */     this.isDrawingStatus = true;
/*     */   }
/*     */ 
/*     */   public Object getNewChartObjectCurrentOwner() {
/* 107 */     return this.newChartObjectCurrentOwner;
/*     */   }
/*     */ 
/*     */   public void setNewChartObjectCurrentOwner(Object newChartObjectCurrentOwner) {
/* 111 */     this.newChartObjectCurrentOwner = newChartObjectCurrentOwner;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.NewDrawingsCoordinator
 * JD-Core Version:    0.6.0
 */