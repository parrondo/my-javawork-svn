/*     */ package com.dukascopy.charts.view.drawingstrategies.main;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.GridType;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.GridUtil;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import javax.swing.JComponent;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MainChartGridDrawingStrategy
/*     */   implements IDrawingStrategy
/*     */ {
/*  33 */   private static final Logger LOGGER = LoggerFactory.getLogger(MainChartGridDrawingStrategy.class);
/*     */   protected final ChartState chartState;
/*     */   protected final IValueToYMapper valueToYMapper;
/*  38 */   protected final GeneralPath path = new GeneralPath();
/*     */ 
/*     */   public MainChartGridDrawingStrategy(ChartState chartState, IValueToYMapper valueToYMapper)
/*     */   {
/*  44 */     this.chartState = chartState;
/*  45 */     this.valueToYMapper = valueToYMapper;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent component)
/*     */   {
/*  50 */     Color color = g.getColor();
/*     */ 
/*  52 */     int width = component.getWidth();
/*  53 */     int height = component.getHeight();
/*     */ 
/*  55 */     ChartSettings.GridType gridType = (ChartSettings.GridType)ChartSettings.get(ChartSettings.Option.GRID);
/*  56 */     if (gridType != ChartSettings.GridType.NONE) {
/*  57 */       drawGrid((Graphics2D)g, width, height, gridType);
/*     */     }
/*     */ 
/*  60 */     drawFrame((Graphics2D)g, width, height);
/*     */ 
/*  62 */     g.setColor(color);
/*     */   }
/*     */ 
/*     */   private void drawGrid(Graphics2D g2d, int width, int height, ChartSettings.GridType gridType) {
/*  66 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.GRID));
/*  67 */     this.path.reset();
/*     */ 
/*  69 */     BasicStroke gridStroke = this.chartState.getTheme().getStroke(ITheme.StrokeElement.GRID_STROKE);
/*     */ 
/*  72 */     for (int x = width - getGridIncrementInPixels(gridType) - 1; x > 0; x -= getGridIncrementInPixels(gridType)) {
/*  73 */       GraphicHelper.drawVerticalDashedLine(this.path, x, 0.0D, height, gridStroke);
/*     */     }
/*     */ 
/*  77 */     if (gridType == ChartSettings.GridType.STATIC) {
/*  78 */       int gridSize = ((Integer)ChartSettings.get(ChartSettings.Option.GRID_SIZE)).intValue();
/*  79 */       for (int y = height - gridSize; y > 0; y -= gridSize)
/*  80 */         GraphicHelper.drawHorizontalDashedLine(this.path, y, 0.0D, width, gridStroke);
/*     */     }
/*     */     else {
/*  83 */       double gridSize = getGridSize(gridType);
/*  84 */       double max = this.valueToYMapper.vy(0);
/*  85 */       double min = this.valueToYMapper.vy(height);
/*     */ 
/*  87 */       if ((!GridUtil.isValid(min)) || (!GridUtil.isValid(max))) {
/*  88 */         return;
/*     */       }
/*     */ 
/*  91 */       Instrument instrument = this.chartState.getInstrument();
/*  92 */       double pipValue = instrument.getPipValue();
/*  93 */       int scale = GridUtil.calculateScale(this.valueToYMapper.getValuesInOnePixel(), instrument, gridSize);
/*     */ 
/*  95 */       int iterations = 0;
/*  96 */       for (double value = GridUtil.calculateNearest(instrument, max, gridSize); value >= min; value -= pipValue * gridSize * scale) {
/*  97 */         int y = this.valueToYMapper.yv(value);
/*     */ 
/*  99 */         if ((y >= 0) && (y < height)) {
/* 100 */           GraphicHelper.drawHorizontalDashedLine(this.path, y, 0.0D, width, gridStroke);
/*     */         }
/*     */ 
/* 104 */         if (iterations++ > 1000) {
/* 105 */           LOGGER.debug("Lock detected");
/* 106 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 111 */     g2d.draw(this.path);
/*     */   }
/*     */ 
/*     */   protected int getGridIncrementInPixels(ChartSettings.GridType gridType) {
/* 115 */     return 30;
/*     */   }
/*     */ 
/*     */   protected double getGridSize(ChartSettings.GridType gridType) {
/* 119 */     return GridUtil.getGridSize(this.chartState);
/*     */   }
/*     */ 
/*     */   protected void drawFrame(Graphics2D g2d, int width, int height) {
/* 123 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.OUTLINE));
/*     */ 
/* 125 */     this.path.reset();
/* 126 */     this.path.append(new Rectangle2D.Float(0.0F, 0.0F, width - 1, height - 1), false);
/*     */ 
/* 128 */     g2d.draw(this.path);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.main.MainChartGridDrawingStrategy
 * JD-Core Version:    0.6.0
 */