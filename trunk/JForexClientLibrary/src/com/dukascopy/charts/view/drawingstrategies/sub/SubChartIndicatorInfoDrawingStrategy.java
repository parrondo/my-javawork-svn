/*     */ package com.dukascopy.charts.view.drawingstrategies.sub;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.IndicatorValueFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.awt.font.LineMetrics;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class SubChartIndicatorInfoDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDrawingStrategy
/*     */ {
/*     */   final SubIndicatorGroup subIndicatorGroup;
/*     */   final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   final ITimeToXMapper timeToXMapper;
/*     */   final ChartState chartState;
/*     */   final GeometryCalculator geometryCalculator;
/*  34 */   final IndicatorValueFormatter formatter = new IndicatorValueFormatter();
/*     */ 
/*     */   public SubChartIndicatorInfoDrawingStrategy(SubIndicatorGroup subIndicatorGroup, ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper)
/*     */   {
/*  44 */     this.subIndicatorGroup = subIndicatorGroup;
/*  45 */     this.dataSequenceProvider = dataSequenceProvider;
/*  46 */     this.geometryCalculator = geometryCalculator;
/*  47 */     this.timeToXMapper = timeToXMapper;
/*  48 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent)
/*     */   {
/*  58 */     Color textColor = this.chartState.getTheme().getColor(ITheme.ChartElement.META);
/*  59 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.META));
/*     */ 
/*  62 */     List indicators = this.subIndicatorGroup.getSubIndicators();
/*  63 */     if ((indicators == null) || (indicators.size() < 1))
/*     */     {
/*  65 */       g.setColor(textColor);
/*     */ 
/*  67 */       String indicatorDescription = this.subIndicatorGroup.getName();
/*  68 */       LineMetrics lineMetrics = g.getFontMetrics().getLineMetrics(indicatorDescription, g);
/*     */ 
/*  70 */       g.drawString(indicatorDescription, 20, (int)(10.0F + lineMetrics.getHeight() / 2.0F));
/*     */     }
/*     */     else
/*     */     {
/*  81 */       FontMetrics fontMetrics = g.getFontMetrics();
/*     */ 
/*  83 */       int x = 20;
/*  84 */       int y = 10 + fontMetrics.getHeight() / 2;
/*  85 */       int line_y = y - fontMetrics.getHeight() / 2;
/*     */ 
/*  87 */       for (int i = 0; i < indicators.size(); i++) {
/*  88 */         IndicatorWrapper indicatorWrapper = (IndicatorWrapper)indicators.get(i);
/*  89 */         Color[] colors = indicatorWrapper.getOutputColors();
/*     */ 
/*  91 */         if ((colors == null) || (colors.length < 1))
/*     */         {
/*  93 */           String description = indicatorWrapper.getNameWithParams();
/*  94 */           if (i > 0) {
/*  95 */             description = ", " + description;
/*     */           }
/*  97 */           x = drawString(description, g, textColor, fontMetrics, x, y) + 3;
/*     */         }
/*  99 */         else if (colors.length == 1)
/*     */         {
/* 101 */           String description = indicatorWrapper.getNameWithParams();
/* 102 */           if (i > 0) {
/* 103 */             description = ", " + description;
/*     */           }
/*     */ 
/* 107 */           String currentValue = getCurrentValue(indicatorWrapper, 0);
/* 108 */           if (currentValue != null) {
/* 109 */             description = description + " : " + currentValue;
/*     */           }
/*     */ 
/* 113 */           x = drawString(description, g, textColor, fontMetrics, x, y) + 3;
/* 114 */           OutputParameterInfo.DrawingStyle style = indicatorWrapper.getDrawingStyles()[0];
/* 115 */           x = drawOutputLine(g, colors[0], style, x, line_y) + 3;
/*     */         }
/*     */         else
/*     */         {
/* 119 */           String description = indicatorWrapper.getNameWithParams();
/* 120 */           if (i > 0) {
/* 121 */             description = ", " + description;
/*     */           }
/* 123 */           x = drawString(description + " (", g, textColor, fontMetrics, x, y);
/*     */ 
/* 125 */           for (int j = 0; j < colors.length; j++) {
/* 126 */             OutputParameterInfo output = indicatorWrapper.getIndicator().getOutputParameterInfo(j);
/*     */ 
/* 130 */             String currentValue = getCurrentValue(indicatorWrapper, j);
/*     */             String text;
/*     */             String text;
/* 131 */             if (currentValue != null)
/* 132 */               text = output.getName() + ": " + currentValue;
/*     */             else {
/* 134 */               text = output.getName();
/*     */             }
/*     */ 
/* 141 */             if (j < colors.length - 1) {
/* 142 */               text = text + ", ";
/*     */             }
/*     */ 
/* 145 */             x = drawString(text, g, colors[j], fontMetrics, x, y);
/*     */           }
/* 147 */           x = drawString(")", g, textColor, fontMetrics, x, y) + 3;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getNameWithParams(IndicatorWrapper wrapper)
/*     */   {
/* 159 */     return wrapper.getNameWithParams();
/*     */   }
/*     */ 
/*     */   private String getCurrentValue(IndicatorWrapper wrapper, int outputIdx)
/*     */   {
/* 169 */     int mouseX = this.chartState.getMouseCursorPoint().x;
/*     */ 
/* 172 */     OutputParameterInfo out = wrapper.getIndicator().getOutputParameterInfo(outputIdx);
/*     */ 
/* 175 */     long time = this.timeToXMapper.tx(mouseX);
/*     */ 
/* 178 */     AbstractDataSequence ds = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/* 179 */     Object value = ds.getFormulaValue(wrapper.getId(), outputIdx, time);
/* 180 */     if (value == null) {
/* 181 */       return null;
/*     */     }
/* 183 */     if ((value instanceof Double)) {
/* 184 */       return this.formatter.format(((Double)value).doubleValue());
/*     */     }
/*     */ 
/* 187 */     return value.toString();
/*     */   }
/*     */ 
/*     */   private int drawOutputLine(Graphics g, Color color, OutputParameterInfo.DrawingStyle style, int x, int y)
/*     */   {
/* 196 */     if (style == null) {
/* 197 */       return x;
/*     */     }
/*     */ 
/* 200 */     g.setColor(color);
/*     */ 
/* 202 */     switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$DrawingStyle[style.ordinal()]) {
/*     */     case 1:
/* 204 */       g.drawLine(x, y + 1, x + 1, y + 1);
/* 205 */       g.drawLine(x + 4, y + 1, x + 5, y + 1);
/* 206 */       g.drawLine(x + 8, y + 1, x + 9, y + 1);
/* 207 */       g.drawLine(x + 12, y + 1, x + 13, y + 1);
/* 208 */       return x + 13;
/*     */     case 2:
/* 211 */       g.drawRect(x, y + 1, 2, 1);
/* 212 */       g.drawRect(x + 5, y + 1, 2, 1);
/* 213 */       g.drawRect(x + 10, y + 1, 2, 1);
/* 214 */       return x + 13;
/*     */     case 3:
/* 217 */       g.drawRect(x, y + 1, 2, 1);
/* 218 */       g.drawLine(x + 6, y + 1, x + 7, y + 1);
/* 219 */       g.drawRect(x + 10, y + 1, 2, 1);
/* 220 */       return x + 13;
/*     */     case 4:
/* 223 */       g.drawRect(x, y + 1, 3, 1);
/* 224 */       g.drawLine(x + 7, y + 1, x + 8, y + 1);
/* 225 */       g.drawLine(x + 12, y + 1, x + 13, y + 1);
/* 226 */       return x + 13;
/*     */     }
/*     */ 
/* 229 */     g.drawRect(x, y + 1, 12, 1);
/* 230 */     return x + 13;
/*     */   }
/*     */ 
/*     */   private int drawString(String string, Graphics g, Color color, FontMetrics fm, int x, int y)
/*     */   {
/* 236 */     g.setColor(color);
/* 237 */     g.drawString(string, x, y);
/* 238 */     return x + fm.stringWidth(string);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.sub.SubChartIndicatorInfoDrawingStrategy
 * JD-Core Version:    0.6.0
 */