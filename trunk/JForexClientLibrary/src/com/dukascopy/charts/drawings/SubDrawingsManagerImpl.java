/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.listeners.drawing.DrawingActionListener;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Point;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SubDrawingsManagerImpl extends DrawingsManagerImpl
/*     */   implements ISubDrawingsManager, ISubDrawingsContainer
/*     */ {
/*     */   final int subWindowId;
/*     */   final int indicatorId;
/*     */ 
/*     */   public SubDrawingsManagerImpl(int subWindowId, int indicatorId, IMapper mapper, FormattersManager formattersManager, GuiRefresher guiRefresher, DrawingsFactory drawingsFactory, DrawingsLabelHelper drawingsLabelHelper, DrawingActionListener drawingActionListener, ChartsActionListenerRegistry chartsActionListenerRegistry, ChartState chartState, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders, GeometryCalculator geometryCalculator)
/*     */   {
/*  39 */     super(mapper, formattersManager, guiRefresher, drawingsFactory, drawingsLabelHelper, drawingActionListener, chartsActionListenerRegistry, chartState, allDataSequenceProviders, geometryCalculator);
/*     */ 
/*  52 */     this.subWindowId = subWindowId;
/*  53 */     this.indicatorId = indicatorId;
/*     */   }
/*     */ 
/*     */   public boolean triggerHighlighting(Point point)
/*     */   {
/*  58 */     Collection chartObjects = this.commonChartObjects.values();
/*  59 */     synchronized (this.commonChartObjects) {
/*  60 */       return triggerHighlightingOfDrawings(point, chartObjects);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEdited()
/*     */   {
/*  66 */     if (this.editedChartObject == null) {
/*  67 */       return;
/*     */     }
/*  69 */     this.editedChartObject.setHighlighted(false);
/*  70 */     this.editedChartObject.setUnderEdit(false);
/*     */ 
/*  72 */     this.chartsActionListenerRegistry.drawingChanged(this.editedChartObject);
/*     */ 
/*  74 */     this.editedChartObject = null;
/*  75 */     this.highlightedObject = null;
/*     */   }
/*     */ 
/*     */   protected void selectDrawing(ChartObject chartObject)
/*     */   {
/*  80 */     if (chartObject == null) {
/*  81 */       return;
/*     */     }
/*  83 */     this.highlightedObject = null;
/*  84 */     this.editedChartObject = chartObject;
/*  85 */     this.editedChartObject.setUnderEdit(true);
/*     */ 
/*  87 */     this.chartsActionListenerRegistry.drawingChanged(this.editedChartObject);
/*  88 */     this.drawingActionListener.drawingEditingStarted();
/*  89 */     refreshWindow();
/*     */   }
/*     */ 
/*     */   protected void refreshWindow()
/*     */   {
/*  94 */     this.guiRefresher.refreshSubContentBySubViewId(this.subWindowId);
/*     */   }
/*     */ 
/*     */   protected void repaintWindow()
/*     */   {
/*  99 */     this.guiRefresher.repaintSubContentBySubViewId(this.subWindowId);
/*     */   }
/*     */ 
/*     */   protected void drawingAdded(IChartObject chartObject)
/*     */   {
/* 104 */     this.chartsActionListenerRegistry.drawingAdded(this.indicatorId, chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawingRemoved(IChartObject chartObject)
/*     */   {
/* 109 */     this.chartsActionListenerRegistry.drawingRemoved(this.indicatorId, chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawingsRemoved(List<IChartObject> chartObjects)
/*     */   {
/* 114 */     this.chartsActionListenerRegistry.drawingsRemoved(chartObjects);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.SubDrawingsManagerImpl
 * JD-Core Version:    0.6.0
 */