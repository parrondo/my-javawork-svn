/*     */ package com.dukascopy.charts.view.drawingstrategies.main;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public abstract class MainAxisYPanelMovableLabelDrawingStrategyAbstract
/*     */   implements IDrawingStrategy
/*     */ {
/*     */   private final ChartState chartState;
/*     */   private final ValueFormatter valueFormatter;
/*     */   private final IValueToYMapper valueToYMapper;
/*     */ 
/*     */   protected MainAxisYPanelMovableLabelDrawingStrategyAbstract(ChartState chartState, ValueFormatter valueFormatter, IValueToYMapper valueToYMapper)
/*     */   {
/*  30 */     this.valueFormatter = valueFormatter;
/*  31 */     this.chartState = chartState;
/*  32 */     this.valueToYMapper = valueToYMapper;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent) {
/*  36 */     Color color = g.getColor();
/*  37 */     Font font = g.getFont();
/*     */ 
/*  39 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*     */ 
/*  41 */     if (ChartSettings.getBoolean(ChartSettings.Option.LAST_CANDLE_TRACKING)) {
/*  42 */       drawDataInProgressMark(g, jComponent);
/*     */     }
/*  44 */     if (getChartState().isMouseCursorOnWindow(-1)) {
/*  45 */       drawMouseCursorMark(g, jComponent, getChartState().getMouseCursorPoint().y);
/*     */     }
/*     */ 
/*  48 */     g.setColor(color);
/*  49 */     g.setFont(font);
/*     */   }
/*     */ 
/*     */   protected void drawDataInProgressMark(Graphics g, JComponent jComponent)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void drawMouseCursorMark(Graphics g, JComponent jComponent, int curMouseY) {
/*  57 */     drawBackground(g, jComponent, this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND), this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND), curMouseY);
/*     */ 
/*  64 */     drawText(g, jComponent, curMouseY, getValueFormatter().formatMouseCursorValue(getValueToYMapper().vy(curMouseY)));
/*     */   }
/*     */ 
/*     */   protected void drawText(Graphics g, JComponent jComponent, int yOfValue, String text)
/*     */   {
/*  70 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  71 */     int textWidth = fontMetrics.stringWidth(text);
/*  72 */     drawText(g, jComponent, yOfValue, jComponent.getWidth() / 2 - textWidth / 2, text);
/*     */   }
/*     */ 
/*     */   protected void drawText(Graphics g, JComponent jComponent, int yOfValue, int xValue, String text) {
/*  76 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  77 */     int fontHeight = fontMetrics.getHeight();
/*  78 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND));
/*  79 */     g.drawString(text, xValue, yOfValue + fontHeight / 2 - 2);
/*     */   }
/*     */ 
/*     */   protected void drawBackground(Graphics g, JComponent jComponent, Color foregroundColor, Color backgroundColor, int y)
/*     */   {
/*  87 */     int fontHeight = g.getFontMetrics().getHeight();
/*  88 */     if ((y < -fontHeight) || (y > jComponent.getHeight() + fontHeight)) {
/*  89 */       return;
/*     */     }
/*  91 */     g.setColor(backgroundColor);
/*  92 */     g.fillRect(1, y - fontHeight / 2, jComponent.getWidth() - 3, fontHeight);
/*     */ 
/*  98 */     g.setColor(foregroundColor);
/*  99 */     g.drawRect(1, y - fontHeight / 2, jComponent.getWidth() - 3, fontHeight);
/*     */   }
/*     */ 
/*     */   public ChartState getChartState()
/*     */   {
/* 108 */     return this.chartState;
/*     */   }
/*     */ 
/*     */   public ValueFormatter getValueFormatter() {
/* 112 */     return this.valueFormatter;
/*     */   }
/*     */ 
/*     */   public IValueToYMapper getValueToYMapper() {
/* 116 */     return this.valueToYMapper;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelMovableLabelDrawingStrategyAbstract
 * JD-Core Version:    0.6.0
 */