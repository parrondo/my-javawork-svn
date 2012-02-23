/*     */ package com.dukascopy.charts.view.drawingstrategies.sub;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class SubAxisYPanelMovableLabelDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDrawingStrategy
/*     */ {
/*  25 */   final DecimalFormat formatter = new DecimalFormat("0.######");
/*     */   final SubIndicatorGroup subIndicatorGroup;
/*     */   final ChartState chartState;
/*     */   final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   final SubValueToYMapper subValueToYMapper;
/*     */ 
/*     */   public SubAxisYPanelMovableLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup, ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, SubValueToYMapper subValueToYMapper)
/*     */   {
/*  38 */     this.subIndicatorGroup = subIndicatorGroup;
/*  39 */     this.chartState = chartState;
/*  40 */     this.dataSequenceProvider = dataSequenceProvider;
/*  41 */     this.subValueToYMapper = subValueToYMapper;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent) {
/*  45 */     if (!this.chartState.isMouseCursorOnWindow(this.subIndicatorGroup.getSubWindowId())) {
/*  46 */       return;
/*     */     }
/*     */ 
/*  49 */     Color color = g.getColor();
/*  50 */     Font font = g.getFont();
/*     */ 
/*  52 */     drawMouseCursorLabel(g, jComponent, this.chartState.getMouseCursorPoint().y);
/*     */ 
/*  54 */     g.setColor(color);
/*  55 */     g.setFont(font);
/*     */   }
/*     */ 
/*     */   public void drawMouseCursorLabel(Graphics g, JComponent jComponent, int y) {
/*  59 */     List indicatorWrappers = this.subIndicatorGroup.getSubIndicators();
/*  60 */     if (indicatorWrappers.isEmpty()) {
/*  61 */       return;
/*     */     }
/*  63 */     double value = this.subValueToYMapper.get(Integer.valueOf(this.subIndicatorGroup.getBasicSubIndicator().getId())).vy(y);
/*  64 */     drawBackground(g, jComponent, y);
/*  65 */     drawValue(g, jComponent, this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND), value, y);
/*     */   }
/*     */ 
/*     */   void drawValue(Graphics g, JComponent jComponent, Color foreground, double value, int y) {
/*  69 */     SubAxisYFormatterUtils.setup(this.formatter, value);
/*  70 */     drawText(g, jComponent, foreground, this.formatter.format(value), y);
/*     */   }
/*     */ 
/*     */   void drawText(Graphics g, JComponent jComponent, String text, int y) {
/*  74 */     drawText(g, jComponent, this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND), text, y);
/*     */   }
/*     */ 
/*     */   void drawText(Graphics g, JComponent jComponent, Color color, String text, int y) {
/*  78 */     g.setColor(color);
/*  79 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*     */ 
/*  81 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  82 */     int textWidth = fontMetrics.stringWidth(text);
/*  83 */     int fontHeight = fontMetrics.getHeight();
/*     */ 
/*  85 */     g.drawString(text, jComponent.getWidth() / 2 - textWidth / 2, y + fontHeight / 2 - 2);
/*     */   }
/*     */ 
/*     */   void drawBackground(Graphics g, JComponent jComponent, int y)
/*     */   {
/*  93 */     Color foreground = this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/*  94 */     Color background = this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND);
/*  95 */     drawBackground(g, jComponent, foreground, background, y);
/*     */   }
/*     */ 
/*     */   void drawBackground(Graphics g, JComponent jComponent, Color foreground, Color background, int y) {
/*  99 */     int fontHeight = g.getFontMetrics().getHeight();
/*     */ 
/* 101 */     g.setColor(background);
/* 102 */     g.fillRect(1, y - fontHeight / 2, jComponent.getWidth() - 3, fontHeight);
/*     */ 
/* 109 */     g.setColor(foreground);
/* 110 */     g.drawRect(1, y - fontHeight / 2, jComponent.getWidth() - 3, fontHeight);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelMovableLabelDrawingStrategy
 * JD-Core Version:    0.6.0
 */