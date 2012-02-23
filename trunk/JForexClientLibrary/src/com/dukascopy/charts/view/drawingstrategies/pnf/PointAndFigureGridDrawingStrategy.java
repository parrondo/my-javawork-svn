/*     */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartGridDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.GridUtil;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class PointAndFigureGridDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data> extends MainChartGridDrawingStrategy
/*     */ {
/*     */   private final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   private final GeometryCalculator geometryCalculator;
/*     */ 
/*     */   public PointAndFigureGridDrawingStrategy(ChartState chartState, IValueToYMapper valueToYMapper, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*     */   {
/*  40 */     super(chartState, valueToYMapper);
/*     */ 
/*  42 */     this.dataSequenceProvider = dataSequenceProvider;
/*  43 */     this.geometryCalculator = geometryCalculator;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent component)
/*     */   {
/*  48 */     Boolean pointAndFigureGrid = (Boolean)ChartSettings.get(ChartSettings.Option.POINT_AND_FIGURE_GRID_CONSTRUCTION);
/*  49 */     Data lastData = ((AbstractDataSequence)this.dataSequenceProvider.getDataSequence()).getLastData();
/*     */ 
/*  51 */     if ((Boolean.TRUE.equals(pointAndFigureGrid)) && (lastData != null)) {
/*  52 */       Graphics2D g2d = (Graphics2D)g;
/*  53 */       g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.GRID));
/*  54 */       this.path.reset();
/*     */ 
/*  56 */       int width = component.getWidth();
/*  57 */       int height = component.getHeight();
/*     */ 
/*  59 */       drawVerticalSeparators(this.path, height);
/*  60 */       drawHorizontalSeparators(this.path, width, height);
/*  61 */       g2d.draw(this.path);
/*     */ 
/*  63 */       drawFrame((Graphics2D)g, width, height);
/*     */     }
/*     */     else {
/*  66 */       super.draw(g, component);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawHorizontalSeparators(GeneralPath path, int width, int height)
/*     */   {
/*  75 */     Instrument instrument = this.chartState.getInstrument();
/*     */ 
/*  77 */     double gridSize = this.chartState.getPriceRange().getPipCount();
/*  78 */     double max = this.valueToYMapper.vy(0);
/*  79 */     double min = this.valueToYMapper.vy(height);
/*     */ 
/*  81 */     if ((!GridUtil.isValid(min)) || (!GridUtil.isValid(max)) || (min <= 0.0D)) {
/*  82 */       return;
/*     */     }
/*     */ 
/*  85 */     double pipValue = instrument.getPipValue();
/*  86 */     int scale = 1;
/*     */ 
/*  88 */     int iterations = 0;
/*     */ 
/*  90 */     BasicStroke gridStroke = this.chartState.getTheme().getStroke(ITheme.StrokeElement.GRID_STROKE);
/*     */ 
/*  92 */     int prevY = -1;
/*  93 */     for (double value = GridUtil.calculateNearest(instrument, max, gridSize); value >= min; value -= pipValue * gridSize * scale) {
/*  94 */       int y = this.valueToYMapper.yv(value);
/*     */ 
/*  96 */       if (y - prevY <= 2)
/*     */       {
/*     */         continue;
/*     */       }
/* 100 */       if ((y >= 0) && (y < height)) {
/* 101 */         GraphicHelper.drawHorizontalDashedLine(path, y, 0.0D, width, gridStroke);
/*     */       }
/*     */ 
/* 105 */       if (iterations++ > 1000) {
/* 106 */         return;
/*     */       }
/*     */ 
/* 109 */       prevY = y;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawVerticalSeparators(GeneralPath path, int height)
/*     */   {
/* 118 */     int interval = this.geometryCalculator.getDataUnitWidth();
/* 119 */     Data lastData = ((AbstractDataSequence)this.dataSequenceProvider.getDataSequence()).getLastData();
/* 120 */     int datasCount = 1 + ((AbstractDataSequence)this.dataSequenceProvider.getDataSequence()).indexOf(lastData.getTime());
/*     */ 
/* 122 */     BasicStroke gridStroke = this.chartState.getTheme().getStroke(ITheme.StrokeElement.GRID_STROKE);
/*     */ 
/* 124 */     for (int i = 0; i < datasCount + 1; i++) {
/* 125 */       if (!canDrawGridInterval(interval, i)) {
/*     */         continue;
/*     */       }
/* 128 */       int x = i * interval;
/*     */ 
/* 130 */       GraphicHelper.drawVerticalDashedLine(path, x, 0.0D, height - 1, gridStroke);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean canDrawGridInterval(int interval, int index)
/*     */   {
/* 140 */     return (interval >= 4) || (index % 9 == 0);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureGridDrawingStrategy
 * JD-Core Version:    0.6.0
 */