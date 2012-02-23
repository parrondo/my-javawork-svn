/*     */ package com.dukascopy.charts.view.drawingstrategies.sub;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.IndicatorValueFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class SubAxisYIndicatorValueLabelDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data> extends SubAxisYPanelMovableLabelDrawingStrategy<DataSequenceClass, DataClass>
/*     */ {
/*     */   private final ITimeToXMapper timeToXMapper;
/*  31 */   private final IndicatorValueFormatter formatter = new IndicatorValueFormatter();
/*     */ 
/*     */   public SubAxisYIndicatorValueLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup, ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, SubValueToYMapper subValueToYMapper, ITimeToXMapper timeToXMapper)
/*     */   {
/*  37 */     super(subIndicatorGroup, chartState, dataSequenceProvider, subValueToYMapper);
/*  38 */     this.timeToXMapper = timeToXMapper;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent)
/*     */   {
/*  44 */     Color color = g.getColor();
/*  45 */     Font font = g.getFont();
/*     */     try
/*     */     {
/*  48 */       g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*     */ 
/*  50 */       drawIndicatorValueLabels(g, jComponent);
/*     */     }
/*     */     finally {
/*  53 */       g.setColor(color);
/*  54 */       g.setFont(font);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawIndicatorValueLabels(Graphics g, JComponent jComponent)
/*     */   {
/*  64 */     for (IndicatorWrapper indicatorWrapper : this.subIndicatorGroup.getSubIndicators())
/*     */     {
/*  66 */       IIndicator indicator = indicatorWrapper.getIndicator();
/*  67 */       IndicatorInfo info = indicator.getIndicatorInfo();
/*  68 */       if (!info.isOverChart()) {
/*  69 */         IValueToYMapper valueToYMapper = this.subValueToYMapper.get(Integer.valueOf(indicatorWrapper.getId()));
/*     */ 
/*  71 */         for (int i = 0; i < info.getNumberOfOutputs(); i++)
/*     */         {
/*  73 */           if (indicatorWrapper.getShowOutputs()[i] == 0)
/*     */           {
/*     */             continue;
/*     */           }
/*  77 */           if (!indicatorWrapper.showValueOnChart(i))
/*     */           {
/*     */             continue;
/*     */           }
/*  81 */           long lastTime = this.dataSequenceProvider.getIndicatorLatestTime(indicatorWrapper.getId());
/*  82 */           int x = this.timeToXMapper.xt(lastTime);
/*  83 */           if (this.timeToXMapper.isXOutOfRange(x))
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/*  88 */           Object value = this.dataSequenceProvider.getIndicatorLatestValue(indicatorWrapper.getId(), i);
/*  89 */           if (!(value instanceof Number))
/*     */             continue;
/*  91 */           double lastValue = ((Number)value).doubleValue();
/*     */ 
/*  93 */           if (!Double.isNaN(lastValue)) {
/*  94 */             int y = valueToYMapper.yv(lastValue);
/*  95 */             if (!valueToYMapper.isYOutOfRange(y)) {
/*  96 */               Color backgroundColor = indicatorWrapper.getOutputColors()[i];
/*  97 */               if (backgroundColor == null) {
/*  98 */                 backgroundColor = this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND);
/*     */               }
/* 100 */               drawBackground(g, jComponent, Color.BLACK, backgroundColor, y);
/*     */ 
/* 102 */               Color foreground = getTextForeground(backgroundColor);
/* 103 */               drawText(g, jComponent, foreground, this.formatter.format(lastValue), y);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Color getTextForeground(Color background)
/*     */   {
/* 119 */     Color original = this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/* 120 */     Color inverted = new Color(original.getRGB() ^ 0xFFFFFF);
/*     */ 
/* 122 */     double diffOrig = Math.abs(getBrightness(original) - getBrightness(background));
/* 123 */     double diffInv = Math.abs(getBrightness(inverted) - getBrightness(background));
/*     */ 
/* 125 */     return diffOrig >= diffInv ? original : inverted;
/*     */   }
/*     */ 
/*     */   protected double getBrightness(Color color)
/*     */   {
/* 134 */     return 0.2125D * color.getRed() + 0.7154D * color.getGreen() + 0.0721D * color.getBlue();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYIndicatorValueLabelDrawingStrategy
 * JD-Core Version:    0.6.0
 */