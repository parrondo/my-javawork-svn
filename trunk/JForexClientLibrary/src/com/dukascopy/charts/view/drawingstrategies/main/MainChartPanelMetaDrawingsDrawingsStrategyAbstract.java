/*     */ package com.dukascopy.charts.view.drawingstrategies.main;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Polygon;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public abstract class MainChartPanelMetaDrawingsDrawingsStrategyAbstract
/*     */   implements IDrawingStrategy
/*     */ {
/*     */   private final ValueFormatter valueFormatter;
/*     */   private final ChartState chartState;
/*     */   private final MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState;
/*     */ 
/*     */   public MainChartPanelMetaDrawingsDrawingsStrategyAbstract(ValueFormatter valueFormatter, ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState)
/*     */   {
/*  30 */     this.valueFormatter = valueFormatter;
/*  31 */     this.chartState = chartState;
/*  32 */     this.mouseControllerMetaDrawingsState = mouseControllerMetaDrawingsState;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent) {
/*  36 */     Color color = g.getColor();
/*  37 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.META));
/*     */ 
/*  39 */     if (getMouseControllerMetaDrawingsState().isZoomingToArea()) {
/*  40 */       drawZoomingInArea(g, getMouseControllerMetaDrawingsState().getFirstZoomingToAreaPoint(), getMouseControllerMetaDrawingsState().getSecondZoomingToAreaPoint());
/*     */     }
/*     */ 
/*  43 */     if (getMouseControllerMetaDrawingsState().isMeasuringCandlesLine()) {
/*  44 */       String infoMessage = getInfoMessage(g, jComponent, getMouseControllerMetaDrawingsState().get1MeasuringCandlesLinePoint(), getMouseControllerMetaDrawingsState().get2MeasuringCandlesLinePoint());
/*  45 */       drawInfoMessageAndCrosses(g, jComponent, getMouseControllerMetaDrawingsState().get1MeasuringCandlesLinePoint(), getMouseControllerMetaDrawingsState().get2MeasuringCandlesLinePoint(), infoMessage);
/*     */     }
/*     */ 
/*  48 */     int chartShiftX = jComponent.getWidth() - getChartState().getChartShiftHandlerCoordinate();
/*  49 */     int bottomY = drawChartShiftHandler((Graphics2D)g, chartShiftX);
/*  50 */     if (getMouseControllerMetaDrawingsState().isChartShiftHandlerBeeingShifted()) {
/*  51 */       g.drawLine(chartShiftX, bottomY, chartShiftX, jComponent.getHeight());
/*     */     }
/*     */ 
/*  54 */     g.setColor(color);
/*     */   }
/*     */ 
/*     */   void drawZoomingInArea(Graphics g, Point firstZoomingToAreaPoint, Point secondZoomingToAreaPoint) {
/*  58 */     if ((firstZoomingToAreaPoint == null) || (secondZoomingToAreaPoint == null)) {
/*  59 */       return;
/*     */     }
/*     */ 
/*  62 */     int width = Math.abs(secondZoomingToAreaPoint.x - firstZoomingToAreaPoint.x);
/*  63 */     int height = Math.abs(secondZoomingToAreaPoint.y - firstZoomingToAreaPoint.y);
/*     */ 
/*  65 */     int x = Math.min(firstZoomingToAreaPoint.x, secondZoomingToAreaPoint.x);
/*  66 */     int y = Math.min(firstZoomingToAreaPoint.y, secondZoomingToAreaPoint.y);
/*     */ 
/*  68 */     Color color = this.chartState.getTheme().getColor(ITheme.ChartElement.META);
/*     */ 
/*  70 */     if ((height < 1) && (width < 1))
/*  71 */       g.fillOval(x, y, 1, 1);
/*  72 */     else if (height < 1)
/*  73 */       GraphicHelper.drawSegmentDashedLine(g, x, y, x + width, y, 2.0D, 2.0D, color, x + width, y + height);
/*  74 */     else if (width < 1)
/*  75 */       GraphicHelper.drawSegmentDashedLine(g, x, y, x, y + height, 2.0D, 2.0D, color, x + width, y + height);
/*     */     else
/*  77 */       GraphicHelper.drawDashedRect(g, x, y, width, height, 2.0F, 2.0F, color, x + width, y + height);
/*     */   }
/*     */ 
/*     */   protected abstract String getInfoMessage(Graphics paramGraphics, JComponent paramJComponent, Point paramPoint1, Point paramPoint2);
/*     */ 
/*     */   protected void drawInfoMessageAndCrosses(Graphics g, JComponent jComponent, Point p1, Point p2, String infoMessage)
/*     */   {
/*  85 */     if (p1 != null) {
/*  86 */       g.drawLine(p1.x, 1, p1.x, p1.y - 3);
/*  87 */       g.drawLine(p1.x, p1.y + 3, p1.x, jComponent.getHeight());
/*  88 */       g.drawLine(1, p1.y, p1.x - 3, p1.y);
/*  89 */       g.drawLine(p1.x + 3, p1.y, jComponent.getWidth(), p1.y);
/*     */     }
/*     */ 
/*  92 */     if (p2 != null) {
/*  93 */       g.drawLine(p2.x, 1, p2.x, p2.y - 3);
/*  94 */       g.drawLine(p2.x, p2.y + 3, p2.x, jComponent.getHeight());
/*  95 */       g.drawLine(1, p2.y, p2.x - 3, p2.y);
/*  96 */       g.drawLine(p2.x + 3, p2.y, jComponent.getWidth(), p2.y);
/*     */ 
/*  98 */       if (infoMessage != null) {
/*  99 */         g.drawString(infoMessage, p2.x + 10, p2.y - 2);
/*     */       }
/*     */     }
/*     */ 
/* 103 */     if ((p1 != null) && (p2 != null))
/* 104 */       g.drawLine(p1.x, p1.y, p2.x, p2.y);
/*     */   }
/*     */ 
/*     */   int drawChartShiftHandler(Graphics2D g2, int x1)
/*     */   {
/* 111 */     g2.fillPolygon(new Polygon(x1)
/*     */     {
/*     */     });
/* 116 */     return 6;
/*     */   }
/*     */ 
/*     */   public ValueFormatter getValueFormatter() {
/* 120 */     return this.valueFormatter;
/*     */   }
/*     */ 
/*     */   public ChartState getChartState() {
/* 124 */     return this.chartState;
/*     */   }
/*     */ 
/*     */   public MouseControllerMetaDrawingsState getMouseControllerMetaDrawingsState() {
/* 128 */     return this.mouseControllerMetaDrawingsState;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.main.MainChartPanelMetaDrawingsDrawingsStrategyAbstract
 * JD-Core Version:    0.6.0
 */