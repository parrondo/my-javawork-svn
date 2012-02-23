/*     */ package com.dukascopy.charts.math.dataprovider;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractDataSequence<DataClass extends Data>
/*     */   implements IDataSequence<DataClass>
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSequence.class);
/*     */ 
/*  30 */   protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat() {  } ;
/*     */   protected final Period period;
/*     */   protected final DataClass[] data;
/*  36 */   protected double min = 1.0D;
/*  37 */   protected double max = 2.0D;
/*     */   protected long[][] gaps;
/*     */   protected final long from;
/*     */   protected final long to;
/*     */   protected final Map<Integer, Object[]> formulaOutputs;
/*     */   protected final Map<Integer, IndicatorWrapper> indicators;
/*     */   protected final Map<Integer, FormulasMinMax> formulaOutputsMinMax;
/*  45 */   protected final double[] emptyFormulasOutputs = new double[0];
/*     */   private boolean minMaxCalculated;
/*     */   private final boolean latestDataVisible;
/*     */   private final boolean includesLatestData;
/*     */   protected final int extraBefore;
/*     */   protected final int extraAfter;
/*     */   protected static final String LINE_SEPARATOR = "\n";
/*     */ 
/*  63 */   public AbstractDataSequence(Period period, long from, long to, int extraBefore, int extraAfter, DataClass[] data, long[][] gaps, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData) { this.period = period;
/*  64 */     this.from = from;
/*  65 */     this.to = to;
/*  66 */     this.data = data;
/*  67 */     this.gaps = gaps;
/*  68 */     this.formulaOutputs = formulaOutputs;
/*  69 */     this.indicators = indicators;
/*  70 */     this.latestDataVisible = latestDataVisible;
/*  71 */     this.includesLatestData = includesLatestData;
/*  72 */     this.extraBefore = extraBefore;
/*  73 */     this.extraAfter = extraAfter;
/*     */ 
/*  75 */     if (formulaOutputs != null)
/*  76 */       this.formulaOutputsMinMax = new HashMap((int)(formulaOutputs.size() / 0.75D + 1.0D), 0.75F);
/*     */     else
/*  78 */       this.formulaOutputsMinMax = null;
/*     */   }
/*     */ 
/*     */   public double getMin()
/*     */   {
/*  84 */     return this.min;
/*     */   }
/*     */ 
/*     */   public double getMax() {
/*  88 */     return this.max;
/*     */   }
/*     */ 
/*     */   public long[][] getGaps() {
/*  92 */     return this.gaps;
/*     */   }
/*     */ 
/*     */   public long getFrom() {
/*  96 */     return this.from;
/*     */   }
/*     */ 
/*     */   public long getTo() {
/* 100 */     return this.to;
/*     */   }
/*     */ 
/*     */   public DataClass[] getData() {
/* 104 */     return this.data;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 108 */     return this.data.length == 0;
/*     */   }
/*     */ 
/*     */   public DataClass getData(int dataUnitIndex) {
/* 112 */     return getData(dataUnitIndex, false);
/*     */   }
/*     */ 
/*     */   public DataClass getData(int dataUnitIndex, boolean extra) {
/* 116 */     if ((dataUnitIndex < 0) || (dataUnitIndex >= size(extra))) {
/* 117 */       return null;
/*     */     }
/* 119 */     if (extra) {
/* 120 */       return getData()[dataUnitIndex];
/*     */     }
/* 122 */     return getData()[(getExtraBefore() + dataUnitIndex)];
/*     */   }
/*     */ 
/*     */   public DataClass getData(long dataTime)
/*     */   {
/* 128 */     Data tmpData = new Data(dataTime)
/*     */     {
/*     */       public void toBytes(int version, long firstChunkCandle, double pipValue, byte[] buff, int off) {
/*     */       }
/*     */ 
/*     */       public int getBytesCount(int version) {
/* 134 */         return 0;
/*     */       }
/*     */     };
/* 137 */     int index = Arrays.binarySearch(this.data, tmpData, new Comparator() {
/*     */       public int compare(Data o1, Data o2) {
/* 139 */         return (int)(o1.time - o2.time);
/*     */       }
/*     */     });
/* 142 */     if (index > 0) {
/* 143 */       return this.data[index];
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   public DataClass getLastData()
/*     */   {
/* 150 */     if (isEmpty()) {
/* 151 */       return null;
/*     */     }
/*     */ 
/* 154 */     return getData(size() - 1);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 158 */     return size(false);
/*     */   }
/*     */ 
/*     */   public int size(boolean extra) {
/* 162 */     if (extra) {
/* 163 */       return getData().length;
/*     */     }
/* 165 */     return getData().length - getExtraBefore() - getExtraAfter();
/*     */   }
/*     */ 
/*     */   public boolean isLatestDataVisible()
/*     */   {
/* 170 */     return this.latestDataVisible;
/*     */   }
/*     */ 
/*     */   public boolean isIncludesLatestData() {
/* 174 */     return this.includesLatestData;
/*     */   }
/*     */ 
/*     */   protected void calculateMinMax() {
/* 178 */     if (this.minMaxCalculated) {
/* 179 */       return;
/*     */     }
/* 181 */     calculateMasterDataMinMax();
/* 182 */     calculateSlaveDataMinMax();
/* 183 */     this.minMaxCalculated = true;
/*     */   }
/*     */   public abstract void calculateMasterDataMinMax();
/*     */ 
/*     */   protected void calculateSlaveDataMinMax() {
/* 189 */     if (this.formulaOutputs == null) {
/* 190 */       return;
/*     */     }
/* 192 */     for (Integer indicatorId : this.formulaOutputs.keySet())
/* 193 */       calculateSingleForumulaMinMax(indicatorId);
/*     */   }
/*     */ 
/*     */   private void calculateSingleForumulaMinMax(Integer indicatorId)
/*     */   {
/* 198 */     Object[] formulas = (Object[])this.formulaOutputs.get(indicatorId);
/* 199 */     if ((formulas[0] instanceof int[])) {
/* 200 */       int[] firstValue = (int[])(int[])formulas[0];
/* 201 */       if (firstValue.length == 0)
/* 202 */         return;
/*     */     }
/* 204 */     else if ((formulas[0] instanceof double[])) {
/* 205 */       double[] firstValue = (double[])(double[])formulas[0];
/* 206 */       if (firstValue.length == 0) {
/* 207 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 211 */     IndicatorWrapper indicatorWrapper = (IndicatorWrapper)this.indicators.get(indicatorId);
/* 212 */     boolean isHistogramUsed = false;
/* 213 */     double curMin = (0.0D / 0.0D);
/* 214 */     double curMax = (0.0D / 0.0D);
/* 215 */     for (int i = 0; i < formulas.length; i++) {
/* 216 */       if (indicatorWrapper.getMinMaxMethod() != null) {
/* 217 */         double[] minMax = new double[0];
/*     */         try {
/* 219 */           minMax = (double[])(double[])indicatorWrapper.getMinMaxMethod().invoke(indicatorWrapper.getIndicator(), new Object[] { Integer.valueOf(i), formulas[i], Integer.valueOf(getFormulaExtraBefore() > 0 ? getFormulaExtraBefore() : 0), Integer.valueOf(Array.getLength(formulas[i]) - (getFormulaExtraAfter() > 0 ? getFormulaExtraAfter() : 0) - 1) });
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 223 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Exception in getMinMax method: ").append(StrategyWrapper.representError(indicatorWrapper.getIndicator(), e)).toString(), e, true);
/*     */         }
/* 225 */         if ((minMax == null) || (minMax.length < 2)) {
/*     */           continue;
/*     */         }
/* 228 */         if (curMin != curMin)
/* 229 */           curMin = minMax[0];
/* 230 */         else if (minMax[0] == minMax[0]) {
/* 231 */           curMin = curMin > minMax[0] ? minMax[0] : curMin;
/*     */         }
/* 233 */         if (curMax != curMax)
/* 234 */           curMax = minMax[1];
/* 235 */         else if (minMax[1] == minMax[1])
/* 236 */           curMax = curMax > minMax[1] ? curMax : minMax[1];
/*     */       }
/*     */       else
/*     */       {
/* 240 */         if (indicatorWrapper.getDrawingStyles()[i] == OutputParameterInfo.DrawingStyle.NONE) {
/*     */           continue;
/*     */         }
/* 243 */         if ((formulas[i] instanceof int[])) {
/* 244 */           int[] intValues = (int[])(int[])formulas[i];
/* 245 */           FormulasMinMax formulasMinMax = calculateIntegerMinMax(intValues);
/* 246 */           if (curMin == -2147483648.0D)
/* 247 */             curMin = formulasMinMax.min;
/* 248 */           else if (formulasMinMax.min != -2147483648.0D) {
/* 249 */             curMin = curMin > formulasMinMax.min ? formulasMinMax.min : curMin;
/*     */           }
/* 251 */           if (curMax == -2147483648.0D)
/* 252 */             curMax = formulasMinMax.max;
/* 253 */           else if (formulasMinMax.max != -2147483648.0D)
/* 254 */             curMax = curMax > formulasMinMax.max ? curMax : formulasMinMax.max;
/*     */         }
/* 256 */         else if ((formulas[i] instanceof double[])) {
/* 257 */           double[] doubleValues = (double[])(double[])formulas[i];
/* 258 */           FormulasMinMax formulasMinMax = calculateDoubleMinMax(doubleValues);
/* 259 */           if (curMin != curMin)
/* 260 */             curMin = formulasMinMax.min;
/* 261 */           else if (formulasMinMax.min == formulasMinMax.min) {
/* 262 */             curMin = curMin > formulasMinMax.min ? formulasMinMax.min : curMin;
/*     */           }
/* 264 */           if (curMax != curMax)
/* 265 */             curMax = formulasMinMax.max;
/* 266 */           else if (formulasMinMax.max == formulasMinMax.max) {
/* 267 */             curMax = curMax > formulasMinMax.max ? curMax : formulasMinMax.max;
/*     */           }
/*     */         }
/*     */       }
/* 271 */       if (indicatorWrapper.getDrawingStyles()[i] == OutputParameterInfo.DrawingStyle.HISTOGRAM) {
/* 272 */         isHistogramUsed = true;
/*     */       }
/*     */     }
/*     */ 
/* 276 */     if ((isHistogramUsed) && (curMin > 0.0D)) {
/* 277 */       curMin = 0.0D;
/*     */     }
/* 279 */     if (curMin != curMin) {
/* 280 */       curMin = 0.0D;
/*     */     }
/* 282 */     if (curMax != curMax) {
/* 283 */       curMax = 0.0D;
/*     */     }
/* 285 */     if (curMin == curMax) {
/* 286 */       curMin = 1.0D;
/* 287 */       curMax = 2.0D;
/*     */     }
/* 289 */     this.formulaOutputsMinMax.put(indicatorId, new FormulasMinMax(curMin, curMax));
/*     */   }
/*     */ 
/*     */   private FormulasMinMax calculateIntegerMinMax(int[] intValues) {
/* 293 */     int curMin = -2147483648;
/* 294 */     int curMax = -2147483648;
/*     */ 
/* 296 */     int i = getFormulaExtraBefore() > 0 ? getFormulaExtraBefore() - 1 : 0;
/* 297 */     for (int j = intValues.length - (getFormulaExtraAfter() > 0 ? getFormulaExtraAfter() - 1 : 0); i < j; i++) {
/* 298 */       int curValue = intValues[i];
/* 299 */       if (curValue == -2147483648) {
/*     */         continue;
/*     */       }
/* 302 */       if (curMin == -2147483648) {
/* 303 */         curMin = curValue;
/*     */       }
/* 305 */       if (curMax == -2147483648) {
/* 306 */         curMax = curValue;
/*     */       }
/* 308 */       curMin = curMin > curValue ? curValue : curMin;
/* 309 */       curMax = curMax > curValue ? curMax : curValue;
/*     */     }
/* 311 */     if (curMin == curMax) {
/* 312 */       curMin = 1;
/* 313 */       curMax = 2;
/*     */     }
/* 315 */     return new FormulasMinMax(curMin, curMax);
/*     */   }
/*     */ 
/*     */   private FormulasMinMax calculateDoubleMinMax(double[] doubleValues) {
/* 319 */     double curMin = (0.0D / 0.0D);
/* 320 */     double curMax = (0.0D / 0.0D);
/*     */ 
/* 322 */     int i = getFormulaExtraBefore() > 0 ? getFormulaExtraBefore() - 1 : 0;
/* 323 */     for (int j = doubleValues.length - (getFormulaExtraAfter() > 0 ? getFormulaExtraAfter() - 1 : 0); i < j; i++) {
/* 324 */       double curValue = doubleValues[i];
/*     */ 
/* 326 */       if (curValue != curValue) {
/*     */         continue;
/*     */       }
/* 329 */       if (curMin != curMin) {
/* 330 */         curMin = curValue;
/*     */       }
/* 332 */       if (curMax != curMax) {
/* 333 */         curMax = curValue;
/*     */       }
/* 335 */       curMin = curMin > curValue ? curValue : curMin;
/* 336 */       curMax = curMax > curValue ? curMax : curValue;
/*     */     }
/* 338 */     return new FormulasMinMax(curMin, curMax);
/*     */   }
/*     */ 
/*     */   protected int getFormulaExtraBefore() {
/* 342 */     return this.extraBefore;
/*     */   }
/*     */ 
/*     */   protected int getFormulaExtraAfter() {
/* 346 */     return this.extraAfter;
/*     */   }
/*     */ 
/*     */   public int getExtraBefore() {
/* 350 */     return this.extraBefore;
/*     */   }
/*     */ 
/*     */   public int getExtraAfter() {
/* 354 */     return this.extraAfter;
/*     */   }
/*     */ 
/*     */   public double[] getFormulaOutputDouble(int id, int outputNumber)
/*     */   {
/* 360 */     if (this.formulaOutputs == null) {
/* 361 */       return this.emptyFormulasOutputs;
/*     */     }
/* 363 */     Object[] objects = (Object[])this.formulaOutputs.get(Integer.valueOf(id));
/* 364 */     if (objects == null) {
/* 365 */       return this.emptyFormulasOutputs;
/*     */     }
/* 367 */     return (double[])(double[])objects[outputNumber];
/*     */   }
/*     */ 
/*     */   public int[] getFormulaOutputInt(int id, int outputNumber) {
/* 371 */     if (this.formulaOutputs == null) {
/* 372 */       return new int[0];
/*     */     }
/* 374 */     Object[] formulaOutput = (Object[])this.formulaOutputs.get(Integer.valueOf(id));
/* 375 */     if (formulaOutput == null) {
/* 376 */       return new int[0];
/*     */     }
/* 378 */     return (int[])(int[])formulaOutput[outputNumber];
/*     */   }
/*     */ 
/*     */   public Object[] getFormulaOutputs(int id) {
/* 382 */     if (this.formulaOutputs == null) {
/* 383 */       return null;
/*     */     }
/* 385 */     return (Object[])this.formulaOutputs.get(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   public boolean isFormulasMinMaxEmpty(Integer indicatorId) {
/* 389 */     return (this.formulaOutputsMinMax == null) || (this.formulaOutputsMinMax.get(indicatorId) == null);
/*     */   }
/*     */ 
/*     */   public double getFormulasMinFor(Integer indicatorId) {
/* 393 */     if (this.formulaOutputsMinMax == null) {
/* 394 */       return 0.0D;
/*     */     }
/* 396 */     FormulasMinMax formulasMinMax = (FormulasMinMax)this.formulaOutputsMinMax.get(indicatorId);
/* 397 */     if (formulasMinMax == null) {
/* 398 */       return 0.0D;
/*     */     }
/* 400 */     return formulasMinMax.min;
/*     */   }
/*     */ 
/*     */   public double getFormulasMaxFor(Integer indicatorId) {
/* 404 */     if (this.formulaOutputsMinMax == null) {
/* 405 */       return 0.0D;
/*     */     }
/* 407 */     FormulasMinMax formulasMinMax = (FormulasMinMax)this.formulaOutputsMinMax.get(indicatorId);
/* 408 */     if (formulasMinMax == null) {
/* 409 */       return 0.0D;
/*     */     }
/* 411 */     return formulasMinMax.max;
/*     */   }
/*     */ 
/*     */   public int indexOf(long time) {
/* 415 */     if (this.data.length == 0) {
/* 416 */       return -1;
/*     */     }
/*     */ 
/* 419 */     if (time < this.data[0].time) {
/* 420 */       int diff = (int)((this.data[0].time - time) / this.period.getInterval());
/* 421 */       return diff - this.extraBefore;
/*     */     }
/*     */ 
/* 424 */     if (time > this.data[(this.data.length - 1)].time) {
/* 425 */       int diff = (int)((time - this.data[(this.data.length - 1)].time) / this.period.getInterval());
/* 426 */       return diff + this.extraAfter;
/*     */     }
/*     */ 
/* 429 */     for (int i = 0; i < this.data.length; i++) {
/* 430 */       if (time <= this.data[i].time) {
/* 431 */         return i - this.extraBefore;
/*     */       }
/*     */     }
/*     */ 
/* 435 */     return -1;
/*     */   }
/*     */ 
/*     */   protected int getIndicatorDataIndex(long time)
/*     */   {
/* 445 */     if ((time < getFrom()) || (time > getTo())) {
/* 446 */       return -1;
/*     */     }
/*     */ 
/* 449 */     return TimeDataUtils.strictTimeIndex(this.data, time);
/*     */   }
/*     */ 
/*     */   public Boolean isFormulaDowntrendAt(int indicatorId, int outputIdx, long time)
/*     */   {
/* 454 */     if ((time < getFrom()) || (time > getTo())) {
/* 455 */       return null;
/*     */     }
/*     */ 
/* 458 */     if (this.indicators == null)
/*     */     {
/* 461 */       return null;
/*     */     }
/* 463 */     IndicatorWrapper wrapper = (IndicatorWrapper)this.indicators.get(Integer.valueOf(indicatorId));
/* 464 */     if (wrapper == null) {
/* 465 */       return null;
/*     */     }
/* 467 */     IIndicator indicator = wrapper.getIndicator();
/* 468 */     OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(outputIdx);
/*     */ 
/* 470 */     int index = getIndicatorDataIndex(time);
/* 471 */     index -= outputParameterInfo.getShift();
/* 472 */     if ((index < 0) || (index >= this.data.length))
/* 473 */       return null;
/* 474 */     if (index == 0) {
/* 475 */       index = 1;
/*     */     }
/*     */     try
/*     */     {
/* 479 */       switch (4.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*     */       case 1:
/* 481 */         int[] formulaData = (int[])(int[])((Object[])this.formulaOutputs.get(Integer.valueOf(wrapper.getId())))[outputIdx];
/* 482 */         return Boolean.valueOf(formulaData[index] < formulaData[(index - 1)]);
/*     */       case 2:
/* 486 */         double[] formulaData = (double[])(double[])((Object[])this.formulaOutputs.get(Integer.valueOf(wrapper.getId())))[outputIdx];
/* 487 */         double value1 = formulaData[(index - 1)];
/* 488 */         double value2 = formulaData[index];
/* 489 */         if ((value1 != value1) || (value2 != value2))
/*     */         {
/* 491 */           return null;
/*     */         }
/* 493 */         return Boolean.valueOf(value2 < value1);
/*     */       case 3:
/* 498 */         return null;
/*     */       }
/*     */ 
/* 502 */       return null;
/*     */     }
/*     */     catch (IndexOutOfBoundsException ex)
/*     */     {
/* 507 */       LOGGER.debug("Error getting formula value.", ex);
/* 508 */     }return null;
/*     */   }
/*     */ 
/*     */   public Object getFormulaValue(int indicatorId, int outputIdx, long time)
/*     */   {
/* 514 */     if ((time < getFrom()) || (time > getTo())) {
/* 515 */       return null;
/*     */     }
/*     */ 
/* 518 */     if (this.indicators == null)
/*     */     {
/* 521 */       return null;
/*     */     }
/* 523 */     IndicatorWrapper wrapper = (IndicatorWrapper)this.indicators.get(Integer.valueOf(indicatorId));
/* 524 */     if (wrapper == null) {
/* 525 */       return null;
/*     */     }
/* 527 */     IIndicator indicator = wrapper.getIndicator();
/* 528 */     OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(outputIdx);
/*     */ 
/* 530 */     int index = getIndicatorDataIndex(time);
/* 531 */     index -= outputParameterInfo.getShift();
/* 532 */     if ((index < 0) || (index >= this.data.length)) {
/* 533 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 537 */       switch (4.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*     */       case 1:
/* 539 */         int[] formulaData = (int[])(int[])((Object[])this.formulaOutputs.get(Integer.valueOf(wrapper.getId())))[outputIdx];
/* 540 */         return Integer.valueOf(formulaData[index]);
/*     */       case 2:
/* 544 */         double[] formulaData = (double[])(double[])((Object[])this.formulaOutputs.get(Integer.valueOf(wrapper.getId())))[outputIdx];
/* 545 */         double value = formulaData[index];
/* 546 */         if (value != value)
/*     */         {
/* 548 */           return null;
/*     */         }
/* 550 */         return Double.valueOf(value);
/*     */       case 3:
/* 555 */         Object[] formulaData = (Object[])(Object[])((Object[])this.formulaOutputs.get(Integer.valueOf(wrapper.getId())))[outputIdx];
/* 556 */         return formulaData[index];
/*     */       }
/*     */ 
/* 560 */       return null;
/*     */     }
/*     */     catch (IndexOutOfBoundsException ex)
/*     */     {
/* 565 */       LOGGER.debug("Error getting formula value.", ex);
/* 566 */     }return null;
/*     */   }
/*     */ 
/*     */   public boolean intersects(long from, long to)
/*     */   {
/* 571 */     return (to >= this.from) && (this.to >= from);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 588 */     StringBuilder buffer = new StringBuilder();
/*     */ 
/* 590 */     buffer.append("from=").append(DATE_FORMATTER.format(Long.valueOf(this.from))).append(", ");
/* 591 */     buffer.append("to=").append(DATE_FORMATTER.format(Long.valueOf(this.to))).append(", ");
/*     */ 
/* 593 */     if (this.formulaOutputs != null) {
/* 594 */       buffer.append("formulaOutputs.size()=").append(this.formulaOutputs.size()).append(", ");
/*     */     }
/*     */ 
/* 597 */     buffer.append("latestDataVisible=").append(this.latestDataVisible).append(", ");
/* 598 */     buffer.append("extraBefore=").append(this.extraBefore).append(", ");
/* 599 */     buffer.append("extraAfter=").append(this.extraAfter);
/*     */ 
/* 601 */     buffer.append("\n");
/*     */ 
/* 603 */     buffer.append("data.length=").append(this.data.length).append(", ");
/* 604 */     if (this.data.length > 0) {
/* 605 */       buffer.append("data[0].time=").append(DATE_FORMATTER.format(Long.valueOf(this.data[0].time))).append(", ");
/*     */     }
/* 607 */     if (this.data.length > 1) {
/* 608 */       buffer.append("data[last].time=").append(DATE_FORMATTER.format(Long.valueOf(this.data[(this.data.length - 1)].time))).append(", ");
/*     */     }
/*     */ 
/* 611 */     buffer.append("\n");
/*     */ 
/* 613 */     buffer.append("min=").append(this.min).append(", ");
/* 614 */     buffer.append("max=").append(this.max).append(", ");
/*     */ 
/* 616 */     if (this.gaps != null)
/* 617 */       buffer.append("gaps.length=").append(this.gaps.length);
/*     */     else {
/* 619 */       buffer.append("gaps=null");
/*     */     }
/*     */ 
/* 622 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   protected static class FormulasMinMax
/*     */   {
/*     */     final double min;
/*     */     final double max;
/*     */ 
/*     */     public FormulasMinMax(double curMin, double curMax)
/*     */     {
/* 581 */       this.min = curMin;
/* 582 */       this.max = curMax;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.AbstractDataSequence
 * JD-Core Version:    0.6.0
 */