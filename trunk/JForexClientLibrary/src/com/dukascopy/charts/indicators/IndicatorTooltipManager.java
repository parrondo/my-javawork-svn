/*     */ package com.dukascopy.charts.indicators;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.Point;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class IndicatorTooltipManager
/*     */ {
/*     */   private final ITimeToXMapper timeToXMapper;
/*     */   private final IValueToYMapper mainValueToYMapper;
/*     */   private final FormattersManager formattersManager;
/*     */   private final ChartState chartState;
/*     */   private final Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders;
/*     */ 
/*     */   public IndicatorTooltipManager(ITimeToXMapper timeToXMapper, IValueToYMapper mainValueToYMapper, FormattersManager formattersManager, ChartState chartState, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders)
/*     */   {
/*  40 */     this.timeToXMapper = timeToXMapper;
/*  41 */     this.mainValueToYMapper = mainValueToYMapper;
/*     */ 
/*  43 */     this.formattersManager = formattersManager;
/*     */ 
/*  45 */     this.chartState = chartState;
/*     */ 
/*  47 */     this.allDataSequenceProviders = allDataSequenceProviders;
/*     */   }
/*     */ 
/*     */   public TooltipData getIndicatorTooltipAtPoint(Point point, IndicatorWrapper indicatorWrapper, int outpIdx) {
/*  51 */     long time = this.timeToXMapper.tx(point.x);
/*  52 */     AbstractDataSequence ds = (AbstractDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(this.chartState.getDataType())).getDataSequence();
/*  53 */     Object value = ds.getFormulaValue(indicatorWrapper.getId(), outpIdx, time);
/*  54 */     if (!(value instanceof Double)) {
/*  55 */       return null;
/*     */     }
/*  57 */     double price = ((Double)value).doubleValue();
/*     */ 
/*  59 */     Boolean downtrend = ds.isFormulaDowntrendAt(indicatorWrapper.getId(), outpIdx, time);
/*     */ 
/*  61 */     int x = this.timeToXMapper.xt(time);
/*  62 */     int y = this.mainValueToYMapper.yv(price);
/*     */ 
/*  64 */     String indName = indicatorWrapper.getNameWithParams();
/*  65 */     String strTime = this.formattersManager.getDateFormatter().formatTime(time);
/*  66 */     String strPrice = this.formattersManager.getValueFormatter().formatPrice(price);
/*     */ 
/*  68 */     Color handlerColor = Boolean.TRUE.equals(downtrend) ? indicatorWrapper.getOutputColors2()[outpIdx] : indicatorWrapper.getOutputColors()[outpIdx];
/*     */ 
/*  70 */     Color fontColor = this.chartState.getTheme().getColor(ITheme.ChartElement.DEFAULT);
/*  71 */     Color borderColor = this.chartState.getTheme().getColor(ITheme.ChartElement.GRID);
/*  72 */     Color backgroundColor = this.chartState.getTheme().getColor(ITheme.ChartElement.BACKGROUND);
/*     */ 
/*  74 */     return new TooltipData(indName, strTime, strPrice, x, y, handlerColor, fontColor, borderColor, backgroundColor, indicatorWrapper.getLineWidths()[outpIdx]);
/*     */   }
/*     */ 
/*     */   public class TooltipData
/*     */   {
/*     */     private final String indName;
/*     */     private final String time;
/*     */     private final String price;
/*     */     private final int x;
/*     */     private final int y;
/*     */     private final Color handlerColor;
/*     */     private final Color fontColor;
/*     */     private final Color borderColor;
/*     */     private final Color backgroundColor;
/*     */     private final int lineWidth;
/*     */ 
/*     */     public TooltipData(String indName, String time, String price, int x, int y, Color handlerColor, Color fontColor, Color borderColor, Color backgroundColor, int lineWidth)
/*     */     {
/* 111 */       this.indName = indName;
/* 112 */       this.time = time;
/* 113 */       this.price = price;
/* 114 */       this.x = x;
/* 115 */       this.y = y;
/* 116 */       this.handlerColor = handlerColor;
/* 117 */       this.fontColor = fontColor;
/* 118 */       this.borderColor = borderColor;
/* 119 */       this.backgroundColor = backgroundColor;
/* 120 */       this.lineWidth = lineWidth;
/*     */     }
/*     */ 
/*     */     public String getIndName() {
/* 124 */       return this.indName;
/*     */     }
/*     */     public String getTime() {
/* 127 */       return this.time;
/*     */     }
/*     */     public String getPrice() {
/* 130 */       return this.price;
/*     */     }
/*     */     public int getX() {
/* 133 */       return this.x;
/*     */     }
/*     */     public int getY() {
/* 136 */       return this.y;
/*     */     }
/*     */     public Color getHandlerColor() {
/* 139 */       return this.handlerColor;
/*     */     }
/*     */     public Color getFontColor() {
/* 142 */       return this.fontColor;
/*     */     }
/*     */     public Color getBorderColor() {
/* 145 */       return this.borderColor;
/*     */     }
/*     */     public Color getBackgroundColor() {
/* 148 */       return this.backgroundColor;
/*     */     }
/*     */     public int getLineWidth() {
/* 151 */       return this.lineWidth;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.indicators.IndicatorTooltipManager
 * JD-Core Version:    0.6.0
 */