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
/*     */ public class IndicatorBean
/*     */ {
/*     */   private final int id;
/*     */   private final String name;
/*     */   private final OfferSide[] sidesForTicks;
/*     */   private final IIndicators.AppliedPrice[] appliedPricesForCandles;
/*     */   private final Object[] optParams;
/*     */   private final Color[] outputColors;
/*     */   private final Color[] outputColors2;
/*     */   private final boolean[] showValuesOnChart;
/*     */   private final boolean[] showOutputs;
/*     */   private final OutputParameterInfo.DrawingStyle[] drawingStyles;
/*     */   private final int[] lineWidths;
/*     */   private final float[] opacityAlphas;
/*     */   private final Integer subPanelId;
/*     */   private List<IChartObject> chartObjects;
/*     */   private final int[] outputShifts;
/*     */   private List<LevelInfo> levelInfoList;
/*     */   private final boolean recalculateOnNewCandleOnly;
/*     */ 
/*     */   public IndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subChartId, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/*  48 */     this(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, null, null, null, drawingStyles, lineWidths, outputShifts, subChartId, chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */   }
/*     */ 
/*     */   public IndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subChartId, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/*  85 */     this(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, null, drawingStyles, lineWidths, outputShifts, subChartId, chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */   }
/*     */ 
/*     */   public IndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subChartId, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/* 119 */     this(id, name, sidesForTicks, appliedPricesForCandles, optParams, outputColors, outputColors2, null, null, null, drawingStyles, lineWidths, outputShifts, subChartId, chartObjects, levelInfoList, recalculateOnNewCandleOnly);
/*     */   }
/*     */ 
/*     */   public IndicatorBean(int id, String name, OfferSide[] sidesForTicks, IIndicators.AppliedPrice[] appliedPricesForCandles, Object[] optParams, Color[] outputColors, Color[] outputColors2, boolean[] valuesOnChart, boolean[] showOutputs, float[] opacityAlphas, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, int[] outputShifts, Integer subChartId, List<IChartObject> chartObjects, List<LevelInfo> levelInfoList, boolean recalculateOnNewCandleOnly)
/*     */   {
/* 158 */     this.id = id;
/* 159 */     this.name = name;
/* 160 */     this.sidesForTicks = (sidesForTicks == null ? new OfferSide[0] : sidesForTicks);
/* 161 */     this.appliedPricesForCandles = (appliedPricesForCandles == null ? new IIndicators.AppliedPrice[0] : appliedPricesForCandles);
/* 162 */     this.optParams = (optParams == null ? new Object[0] : optParams);
/* 163 */     this.outputColors = (outputColors == null ? new Color[0] : outputColors);
/* 164 */     this.outputColors2 = (outputColors2 == null ? this.outputColors : outputColors2);
/* 165 */     this.showValuesOnChart = (valuesOnChart == null ? new boolean[this.outputColors.length] : valuesOnChart);
/* 166 */     this.showOutputs = (showOutputs == null ? new boolean[this.outputColors.length] : showOutputs);
/* 167 */     this.opacityAlphas = (null == opacityAlphas ? new float[this.outputColors.length] : opacityAlphas);
/* 168 */     this.drawingStyles = drawingStyles;
/* 169 */     this.lineWidths = lineWidths;
/* 170 */     this.outputShifts = outputShifts;
/* 171 */     this.subPanelId = subChartId;
/* 172 */     this.chartObjects = chartObjects;
/* 173 */     this.levelInfoList = levelInfoList;
/* 174 */     this.recalculateOnNewCandleOnly = recalculateOnNewCandleOnly;
/*     */   }
/*     */ 
/*     */   public Integer getId() {
/* 178 */     return Integer.valueOf(this.id);
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 182 */     return this.name;
/*     */   }
/*     */ 
/*     */   public OfferSide[] getSidesForTicks() {
/* 186 */     return this.sidesForTicks;
/*     */   }
/*     */ 
/*     */   public IIndicators.AppliedPrice[] getAppliedPricesForCandles() {
/* 190 */     return this.appliedPricesForCandles;
/*     */   }
/*     */ 
/*     */   public Object[] getOptParams() {
/* 194 */     return this.optParams;
/*     */   }
/*     */ 
/*     */   public Color[] getOutputColors() {
/* 198 */     return this.outputColors;
/*     */   }
/*     */ 
/*     */   public Color[] getOutputColors2() {
/* 202 */     return this.outputColors2;
/*     */   }
/*     */ 
/*     */   public boolean[] getShowValuesOnChart() {
/* 206 */     return this.showValuesOnChart;
/*     */   }
/*     */ 
/*     */   public boolean[] getShowOutputs() {
/* 210 */     return this.showOutputs;
/*     */   }
/*     */ 
/*     */   public float[] getOpacityAlphas() {
/* 214 */     return this.opacityAlphas;
/*     */   }
/*     */ 
/*     */   public OutputParameterInfo.DrawingStyle[] getDrawingStyles() {
/* 218 */     return this.drawingStyles;
/*     */   }
/*     */ 
/*     */   public int[] getLineWidths() {
/* 222 */     return this.lineWidths;
/*     */   }
/*     */ 
/*     */   public int[] getOutputShifts() {
/* 226 */     return this.outputShifts;
/*     */   }
/*     */ 
/*     */   public boolean isRecalculateOnNewCandleOnly() {
/* 230 */     return this.recalculateOnNewCandleOnly;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 235 */     StringBuilder stringBuilder = new StringBuilder();
/*     */ 
/* 237 */     String lineSeparator = System.getProperty("line.separator");
/* 238 */     stringBuilder.append("id=").append(this.id).append(lineSeparator);
/* 239 */     stringBuilder.append("name=").append(this.name).append(lineSeparator);
/*     */ 
/* 241 */     stringBuilder.append("sidesForTicks=");
/* 242 */     for (OfferSide sidesForTick : this.sidesForTicks) {
/* 243 */       stringBuilder.append(sidesForTick).append(", ");
/*     */     }
/* 245 */     stringBuilder.append(lineSeparator);
/*     */ 
/* 247 */     stringBuilder.append("appliedPricesForCandles=");
/* 248 */     for (IIndicators.AppliedPrice appliedPricesForCandle : this.appliedPricesForCandles) {
/* 249 */       stringBuilder.append(appliedPricesForCandle).append(", ");
/*     */     }
/* 251 */     stringBuilder.append(lineSeparator);
/*     */ 
/* 253 */     stringBuilder.append("optParams=");
/* 254 */     for (Object param : this.optParams) {
/* 255 */       stringBuilder.append(param).append(", ");
/*     */     }
/* 257 */     stringBuilder.append(lineSeparator);
/*     */ 
/* 259 */     stringBuilder.append("outputColors=");
/* 260 */     for (Color outputColor : this.outputColors) {
/* 261 */       stringBuilder.append(outputColor).append(", ");
/*     */     }
/* 263 */     stringBuilder.append(lineSeparator);
/*     */ 
/* 265 */     stringBuilder.append("outputColors2=");
/* 266 */     for (Color outputColor : this.outputColors2) {
/* 267 */       stringBuilder.append(outputColor).append(", ");
/*     */     }
/* 269 */     stringBuilder.append(lineSeparator);
/*     */ 
/* 271 */     if (this.drawingStyles != null) {
/* 272 */       stringBuilder.append("drawingStyles=");
/* 273 */       for (OutputParameterInfo.DrawingStyle drawingStyle : this.drawingStyles) {
/* 274 */         stringBuilder.append(drawingStyle).append(", ");
/*     */       }
/* 276 */       stringBuilder.append(lineSeparator);
/*     */     }
/*     */ 
/* 279 */     if (this.lineWidths != null) {
/* 280 */       stringBuilder.append("lineWidths=");
/* 281 */       for (int lineWidth : this.lineWidths) {
/* 282 */         stringBuilder.append(lineWidth).append(", ");
/*     */       }
/* 284 */       stringBuilder.append(lineSeparator);
/*     */     }
/*     */ 
/* 287 */     if (this.outputShifts != null) {
/* 288 */       stringBuilder.append("outputShifts=");
/* 289 */       for (int shift : this.outputShifts) {
/* 290 */         stringBuilder.append(shift).append(", ");
/*     */       }
/* 292 */       stringBuilder.append(lineSeparator);
/*     */     }
/*     */ 
/* 295 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   public Integer getSubPanelId() {
/* 299 */     return this.subPanelId;
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getChartObjects() {
/* 303 */     return this.chartObjects;
/*     */   }
/*     */ 
/*     */   public void setChartObjects(List<IChartObject> chartObjects) {
/* 307 */     this.chartObjects = chartObjects;
/*     */   }
/*     */ 
/*     */   public List<LevelInfo> getLevelInfoList()
/*     */   {
/* 314 */     return this.levelInfoList;
/*     */   }
/*     */ 
/*     */   public void setLevelInfoList(List<LevelInfo> levelInfoList)
/*     */   {
/* 321 */     this.levelInfoList = levelInfoList;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.IndicatorBean
 * JD-Core Version:    0.6.0
 */