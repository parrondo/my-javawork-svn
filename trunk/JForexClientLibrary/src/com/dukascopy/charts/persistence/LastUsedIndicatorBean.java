/*     */ package com.dukascopy.charts.persistence;
/*     */ 
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import java.awt.Color;
/*     */ import java.util.List;
/*     */ 
/*     */ public class LastUsedIndicatorBean extends IndicatorBean
/*     */   implements Comparable<LastUsedIndicatorBean>
/*     */ {
/*     */   private long lastUsedTime;
/*     */ 
/*     */   public LastUsedIndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/*  36 */     this(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, null, valuesOnChart, showOutputs, opacityAlphas, drawingStyles, lineWidths, outputShifts, System.currentTimeMillis(), chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */   }
/*     */ 
/*     */   public LastUsedIndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, long lastUsedTime, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/*  71 */     this(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, null, null, null, null, drawingStyles, lineWidths, outputShifts, lastUsedTime, chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */   }
/*     */ 
/*     */   public LastUsedIndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/* 110 */     this(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, valuesOnChart, showOutputs, opacityAlphas, drawingStyles, lineWidths, outputShifts, System.currentTimeMillis(), chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */   }
/*     */ 
/*     */   public LastUsedIndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, long lastUsedTime, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/* 146 */     this(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, null, null, null, drawingStyles, lineWidths, outputShifts, lastUsedTime, chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */   }
/*     */ 
/*     */   public LastUsedIndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, long lastUsedTime, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/* 185 */     super(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, valuesOnChart, showOutputs, opacityAlphas, drawingStyles, lineWidths, outputShifts, Integer.valueOf(-1), chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */ 
/* 203 */     this.lastUsedTime = lastUsedTime;
/*     */   }
/*     */ 
/*     */   public long getLastUsedTime() {
/* 207 */     return this.lastUsedTime;
/*     */   }
/*     */ 
/*     */   public void setLastUsedTime(long lastUsedTime) {
/* 211 */     this.lastUsedTime = lastUsedTime;
/*     */   }
/*     */ 
/*     */   public int compareTo(LastUsedIndicatorBean bean)
/*     */   {
/* 216 */     return this.lastUsedTime == bean.lastUsedTime ? 0 : this.lastUsedTime < bean.lastUsedTime ? 1 : -1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.LastUsedIndicatorBean
 * JD-Core Version:    0.6.0
 */