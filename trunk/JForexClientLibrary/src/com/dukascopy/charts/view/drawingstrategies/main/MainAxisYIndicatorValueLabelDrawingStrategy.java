/*     */ package com.dukascopy.charts.view.drawingstrategies.main;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class MainAxisYIndicatorValueLabelDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDrawingStrategy
/*     */ {
/*  28 */   final DecimalFormat formatter = new DecimalFormat("0.######");
/*     */   final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   final IValueToYMapper valueToYMapper;
/*     */   final ITimeToXMapper timeToXMapper;
/*     */   final ChartState chartState;
/*     */ 
/*     */   public MainAxisYIndicatorValueLabelDrawingStrategy(AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, IValueToYMapper valueToYMapper, ITimeToXMapper timeToXMapper, ChartState chartState)
/*     */   {
/*  40 */     this.dataSequenceProvider = dataSequenceProvider;
/*  41 */     this.valueToYMapper = valueToYMapper;
/*  42 */     this.timeToXMapper = timeToXMapper;
/*  43 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent)
/*     */   {
/*  48 */     Color color = g.getColor();
/*  49 */     Font font = g.getFont();
/*     */     try
/*     */     {
/*  52 */       g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*  53 */       drawIndicatorValueLabels(g, jComponent);
/*     */     }
/*     */     finally {
/*  56 */       g.setColor(color);
/*  57 */       g.setFont(font);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawIndicatorValueLabels(Graphics g, JComponent jComponent) {
/*  62 */     List indicators = this.dataSequenceProvider.getIndicators();
/*  63 */     for (IndicatorWrapper indicatorWrapper : indicators) {
/*  64 */       IIndicator indicator = indicatorWrapper.getIndicator();
/*  65 */       IndicatorInfo info = indicator.getIndicatorInfo();
/*     */ 
/*  67 */       for (int i = 0; i < info.getNumberOfOutputs(); i++)
/*     */       {
/*  69 */         if (indicatorWrapper.getShowOutputs()[i] == 0)
/*     */         {
/*     */           continue;
/*     */         }
/*  73 */         if (!indicatorWrapper.showValueOnChart(i))
/*     */         {
/*     */           continue;
/*     */         }
/*  77 */         OutputParameterInfo.DrawingStyle style = indicatorWrapper.getDrawingStyles()[i];
/*  78 */         if ((style == OutputParameterInfo.DrawingStyle.LEVEL_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASH_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOTDOT_LINE))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/*  87 */         long lastTime = this.dataSequenceProvider.getIndicatorLatestTime(indicatorWrapper.getId());
/*  88 */         int x = this.timeToXMapper.xt(lastTime);
/*  89 */         if (this.timeToXMapper.isXOutOfRange(x))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/*  94 */         Object value = this.dataSequenceProvider.getIndicatorLatestValue(indicatorWrapper.getId(), i);
/*  95 */         if (!(value instanceof Number))
/*     */           continue;
/*  97 */         double lastValue = ((Number)value).doubleValue();
/*     */ 
/*  99 */         if (!Double.isNaN(lastValue)) {
/* 100 */           int y = this.valueToYMapper.yv(lastValue);
/* 101 */           if (!this.valueToYMapper.isYOutOfRange(y)) {
/* 102 */             Color backgroundColor = indicatorWrapper.getOutputColors()[i];
/* 103 */             if (backgroundColor == null) {
/* 104 */               backgroundColor = this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND);
/*     */             }
/* 106 */             drawBackground(g, jComponent, Color.BLACK, backgroundColor, y);
/*     */ 
/* 108 */             Color foreground = getTextForeground(backgroundColor);
/* 109 */             drawText(g, jComponent, foreground, this.formatter.format(lastValue), y);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void drawBackground(Graphics g, JComponent jComponent, Color foreground, Color background, int y)
/*     */   {
/* 118 */     int fontHeight = g.getFontMetrics().getHeight();
/*     */ 
/* 120 */     g.setColor(background);
/* 121 */     g.fillRect(1, y - fontHeight / 2, jComponent.getWidth() - 3, fontHeight);
/*     */ 
/* 128 */     g.setColor(foreground);
/* 129 */     g.drawRect(1, y - fontHeight / 2, jComponent.getWidth() - 3, fontHeight);
/*     */   }
/*     */ 
/*     */   void drawText(Graphics g, JComponent jComponent, Color color, String text, int y)
/*     */   {
/* 138 */     g.setColor(color);
/* 139 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*     */ 
/* 141 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 142 */     int textWidth = fontMetrics.stringWidth(text);
/* 143 */     int fontHeight = fontMetrics.getHeight();
/*     */ 
/* 145 */     g.drawString(text, jComponent.getWidth() / 2 - textWidth / 2, y + fontHeight / 2 - 2);
/*     */   }
/*     */ 
/*     */   protected Color getTextForeground(Color background)
/*     */   {
/* 159 */     Color original = this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/* 160 */     Color inverted = new Color(original.getRGB() ^ 0xFFFFFF);
/*     */ 
/* 162 */     double diffOrig = Math.abs(getBrightness(original) - getBrightness(background));
/* 163 */     double diffInv = Math.abs(getBrightness(inverted) - getBrightness(background));
/*     */ 
/* 165 */     return diffOrig >= diffInv ? original : inverted;
/*     */   }
/*     */ 
/*     */   protected double getBrightness(Color color)
/*     */   {
/* 174 */     return 0.2125D * color.getRed() + 0.7154D * color.getGreen() + 0.0721D * color.getBlue();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.main.MainAxisYIndicatorValueLabelDrawingStrategy
 * JD-Core Version:    0.6.0
 */