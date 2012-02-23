/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.impl.talib.FuncInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.InputParameterInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.IntegerListHolder;
/*     */ import com.dukascopy.api.impl.talib.IntegerRangeHolder;
/*     */ import com.dukascopy.api.impl.talib.OptInputParameterInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.OutputParameterInfoHolder;
/*     */ import com.dukascopy.api.impl.talib.RealListHolder;
/*     */ import com.dukascopy.api.impl.talib.RealRangeHolder;
/*     */ import com.dukascopy.api.indicators.DoubleListDescription;
/*     */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IIndicatorContext;
/*     */ import com.dukascopy.api.indicators.IMinMax;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.IndicatorResult;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo.Type;
/*     */ import com.dukascopy.api.indicators.IntegerListDescription;
/*     */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*     */ import com.dukascopy.api.indicators.OptInputDescription;
/*     */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OptInputParameterInfo.Type;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsFilter;
/*     */ import com.tictactec.ta.lib.MInteger;
/*     */ import com.tictactec.ta.lib.meta.annotation.InputParameterType;
/*     */ import com.tictactec.ta.lib.meta.annotation.OptInputParameterType;
/*     */ import com.tictactec.ta.lib.meta.annotation.OutputParameterType;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TaLibIndicator
/*     */   implements IIndicator, IMinMax
/*     */ {
/*  36 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaLibIndicator.class);
/*     */   private TaLibMetaData taLibMetaData;
/*     */   private IndicatorInfo indicatorInfo;
/*     */   private InputParameterInfo[] inputParamInfos;
/*     */   private OptInputParameterInfo[] optInputParamInfos;
/*     */   private OutputParameterInfo[] outputParamInfos;
/*     */   private double[] fixedMinMax;
/*     */ 
/*     */   public TaLibIndicator(TaLibMetaData taLibMetaData)
/*     */   {
/*  47 */     this.taLibMetaData = taLibMetaData;
/*  48 */     FuncInfoHolder funcInfo = taLibMetaData.getFuncInfo();
/*  49 */     String name = funcInfo.name.toUpperCase();
/*  50 */     String groupName = funcInfo.group;
/*  51 */     this.indicatorInfo = new IndicatorInfo(name, IndicatorsFilter.getTitle(funcInfo.name), groupName, ((funcInfo.flags & 0x1000000) > 0) || ((funcInfo.flags & 0x10000000) > 0), (funcInfo.flags & 0x4000000) > 0, ((funcInfo.flags & 0x8000000) > 0) || (funcInfo.name.startsWith("MACD")) || (funcInfo.name.equals("TRIX")) || (funcInfo.name.equals("MA")) || (funcInfo.name.startsWith("BBANDS")), funcInfo.nbInput, funcInfo.nbOptInput, funcInfo.nbOutput);
/*     */ 
/*  57 */     if ((name.equalsIgnoreCase("SAR")) || (name.equalsIgnoreCase("SAREXT")) || (name.equalsIgnoreCase("AD")) || (name.equalsIgnoreCase("OBV")) || (funcInfo.name.startsWith("MAMA")) || (funcInfo.name.startsWith("DEMA")))
/*     */     {
/*  65 */       this.indicatorInfo.setRecalculateAll(true);
/*     */     }
/*     */ 
/*  68 */     this.inputParamInfos = new InputParameterInfo[this.indicatorInfo.getNumberOfInputs()];
/*  69 */     for (int i = 0; i < this.inputParamInfos.length; i++) {
/*  70 */       InputParameterInfoHolder info = taLibMetaData.getInputParameterInfo(i);
/*     */       InputParameterInfo.Type type;
/*  72 */       switch (1.$SwitchMap$com$tictactec$ta$lib$meta$annotation$InputParameterType[info.type.ordinal()]) {
/*     */       case 1:
/*  74 */         throw new IllegalArgumentException("Ints as input isn't supported");
/*     */       case 2:
/*  76 */         type = InputParameterInfo.Type.PRICE;
/*  77 */         break;
/*     */       case 3:
/*  79 */         type = InputParameterInfo.Type.DOUBLE;
/*  80 */         break;
/*     */       default:
/*  82 */         throw new IllegalArgumentException("Unknown input type");
/*     */       }
/*  84 */       this.inputParamInfos[i] = new InputParameterInfo(renameInputs(info.paramName), type);
/*     */     }
/*     */ 
/*  87 */     this.optInputParamInfos = new OptInputParameterInfo[this.indicatorInfo.getNumberOfOptionalInputs()];
/*  88 */     for (int i = 0; i < this.optInputParamInfos.length; i++) {
/*  89 */       OptInputParameterInfoHolder info = taLibMetaData.getOptInputParameterInfo(i);
/*     */       OptInputDescription description;
/*  91 */       switch (1.$SwitchMap$com$tictactec$ta$lib$meta$annotation$OptInputParameterType[info.type.ordinal()]) {
/*     */       case 1:
/*  93 */         IntegerListHolder list = taLibMetaData.getOptInputIntegerList(i);
/*  94 */         description = new IntegerListDescription(funcInfo.name.startsWith("BBANDS") ? 1 : list.defaultValue, list.value, list.string);
/*  95 */         break;
/*     */       case 2:
/*  97 */         IntegerRangeHolder range = taLibMetaData.getOptInputIntegerRange(i);
/*  98 */         int value = range.defaultValue;
/*  99 */         if (funcInfo.name.startsWith("BBANDS")) {
/* 100 */           value = 20;
/*     */         }
/* 102 */         int min = range.min;
/* 103 */         if ((funcInfo.name.equals("MACD")) && (i == 2)) {
/* 104 */           min = 2;
/*     */         }
/* 106 */         if ((funcInfo.name.equals("MACDFIX")) && (i == 0)) {
/* 107 */           min = 2;
/*     */         }
/* 109 */         if ((funcInfo.name.equals("TRIX")) && (i == 0)) {
/* 110 */           min = 2;
/*     */         }
/* 112 */         if ((funcInfo.name.equals("ULTOSC")) && (i == 2)) {
/* 113 */           min = 2;
/*     */         }
/* 115 */         int increment = range.suggested_increment;
/* 116 */         if (increment == 0) {
/* 117 */           increment = 1;
/*     */         }
/* 119 */         description = new IntegerRangeDescription(value, min, range.max, increment);
/* 120 */         break;
/*     */       case 3:
/* 122 */         RealListHolder rlist = taLibMetaData.getOptInputRealList(i);
/* 123 */         description = new DoubleListDescription(rlist.defaultValue, rlist.value, rlist.string);
/* 124 */         break;
/*     */       case 4:
/* 126 */         RealRangeHolder rrange = taLibMetaData.getOptInputRealRange(i);
/* 127 */         double suggestedIncrement = rrange.suggested_increment;
/* 128 */         if (suggestedIncrement == 0.0D) {
/* 129 */           if (rrange.precision > 0)
/* 130 */             suggestedIncrement = 1 / (10 * rrange.precision);
/*     */           else {
/* 132 */             suggestedIncrement = 1.0D;
/*     */           }
/*     */         }
/* 135 */         description = new DoubleRangeDescription(rrange.defaultValue, rrange.min, rrange.max, suggestedIncrement, rrange.precision);
/* 136 */         break;
/*     */       default:
/* 138 */         throw new IllegalArgumentException("Unknown optional input type");
/*     */       }
/*     */       OptInputParameterInfo.Type type;
/*     */       OptInputParameterInfo.Type type;
/* 141 */       if ((info.flags & 0x100000) > 0) {
/* 142 */         type = OptInputParameterInfo.Type.PERCENT;
/*     */       }
/*     */       else
/*     */       {
/*     */         OptInputParameterInfo.Type type;
/* 143 */         if ((info.flags & 0x200000) > 0) {
/* 144 */           type = OptInputParameterInfo.Type.DEGREE;
/*     */         }
/*     */         else
/*     */         {
/*     */           OptInputParameterInfo.Type type;
/* 145 */           if ((info.flags & 0x400000) > 0)
/* 146 */             type = OptInputParameterInfo.Type.CURRENCY;
/*     */           else
/* 148 */             type = OptInputParameterInfo.Type.OTHER;
/*     */         }
/*     */       }
/* 151 */       this.optInputParamInfos[i] = new OptInputParameterInfo(renameOptInput(info.paramName), type, description);
/*     */     }
/*     */ 
/* 154 */     this.outputParamInfos = new OutputParameterInfo[this.indicatorInfo.getNumberOfOutputs()];
/* 155 */     for (int i = 0; i < this.outputParamInfos.length; i++) {
/* 156 */       OutputParameterInfoHolder info = taLibMetaData.getOutputParameterInfo(i);
/*     */       OutputParameterInfo.Type type;
/* 158 */       switch (info.type) {
/*     */       case TA_Output_Integer:
/* 160 */         type = OutputParameterInfo.Type.INT;
/* 161 */         break;
/*     */       case TA_Output_Real:
/* 163 */         type = OutputParameterInfo.Type.DOUBLE;
/* 164 */         break;
/*     */       default:
/* 166 */         throw new IllegalArgumentException("Unknown output type");
/*     */       }
/* 168 */       int flags = info.flags;
/* 169 */       if ((funcInfo.flags & 0x10000000) > 0)
/*     */       {
/* 171 */         if ((flags & 0x1) > 0) {
/* 172 */           flags ^= 1;
/*     */         }
/* 174 */         if ((type == OutputParameterInfo.Type.INT) && ((flags & 0x40) == 0) && ((flags & 0x20) == 0) && ((flags & 0x80) == 0) && ((flags & 0x10) <= 0) && ((flags & 0x4) <= 0) && ((flags & 0x8) <= 0) && ((flags & 0x2) <= 0) && ((flags & 0x1) <= 0))
/*     */         {
/* 182 */           flags |= 64;
/*     */         }
/*     */       }
/* 185 */       OutputParameterInfo.DrawingStyle drawingStyle = OutputParameterInfo.DrawingStyle.fromFlagValue(flags);
/* 186 */       if (drawingStyle == OutputParameterInfo.DrawingStyle.NONE) {
/* 187 */         drawingStyle = OutputParameterInfo.DrawingStyle.LINE;
/*     */       }
/* 189 */       this.outputParamInfos[i] = new OutputParameterInfo(renameOutput(info.paramName), type, drawingStyle);
/* 190 */       if ((drawingStyle != OutputParameterInfo.DrawingStyle.HISTOGRAM) || ((!name.equals("MACD")) && (!name.equals("MACDEXT")) && (!name.equals("MACDFIX"))))
/*     */         continue;
/* 192 */       this.outputParamInfos[i].setHistogramTwoColor(true);
/*     */     }
/*     */ 
/* 196 */     if ((name.equals("RSI")) || (name.equals("STOCH")) || (name.equals("MFI")))
/* 197 */       this.fixedMinMax = new double[] { 0.0D, 100.0D };
/* 198 */     else if (name.equals("WILLR"))
/* 199 */       this.fixedMinMax = new double[] { -100.0D, 0.0D };
/* 200 */     else if (name.equals("CCI"))
/* 201 */       this.fixedMinMax = new double[] { -500.0D, 500.0D };
/* 202 */     else if ((name.equals("RVI")) || (name.equals("OBV")))
/* 203 */       this.fixedMinMax = new double[] { 0.0D };
/*     */   }
/*     */ 
/*     */   private String renameOutput(String origName)
/*     */   {
/* 208 */     if (origName == null)
/* 209 */       return "";
/* 210 */     if (origName.equalsIgnoreCase("outReal"))
/* 211 */       return "Line";
/* 212 */     if (origName.equalsIgnoreCase("outInPhase"))
/* 213 */       return "Phase";
/* 214 */     if (origName.equalsIgnoreCase("outQuadrature"))
/* 215 */       return "Quadrature";
/* 216 */     if (origName.equalsIgnoreCase("outInteger"))
/* 217 */       return "Integer";
/* 218 */     if (origName.equalsIgnoreCase("outSine"))
/* 219 */       return "Sine";
/* 220 */     if (origName.equalsIgnoreCase("outLeadSine"))
/* 221 */       return "Lead Sine";
/* 222 */     if (origName.equalsIgnoreCase("outMin"))
/* 223 */       return "Min";
/* 224 */     if (origName.equalsIgnoreCase("outMax"))
/* 225 */       return "Max";
/* 226 */     if (origName.equalsIgnoreCase("outMAMA"))
/* 227 */       return "MAMA";
/* 228 */     if (origName.equalsIgnoreCase("outFAMA"))
/* 229 */       return "FAMA";
/* 230 */     if (origName.equalsIgnoreCase("outRealUpperBand"))
/* 231 */       return "Upper Band";
/* 232 */     if (origName.equalsIgnoreCase("outRealMiddleBand"))
/* 233 */       return "Middle Band";
/* 234 */     if (origName.equalsIgnoreCase("outRealLowerBand"))
/* 235 */       return "Lower Band";
/* 236 */     if (origName.equalsIgnoreCase("outMACD"))
/* 237 */       return "MACD";
/* 238 */     if (origName.equalsIgnoreCase("outMACDSignal"))
/* 239 */       return "MACD Signal";
/* 240 */     if (origName.equalsIgnoreCase("outMACDHist"))
/* 241 */       return "MACD Hist";
/* 242 */     if (origName.equalsIgnoreCase("outSlowK"))
/* 243 */       return "Slow %K";
/* 244 */     if (origName.equalsIgnoreCase("outSlowD"))
/* 245 */       return "Slow %D";
/* 246 */     if (origName.equalsIgnoreCase("outFastK"))
/* 247 */       return "Fast %K";
/* 248 */     if (origName.equalsIgnoreCase("outFastD"))
/* 249 */       return "Fast %D";
/* 250 */     if (origName.equalsIgnoreCase("outAroonDown"))
/* 251 */       return "Aroon Down";
/* 252 */     if (origName.equalsIgnoreCase("outAroonUp")) {
/* 253 */       return "Aroon Up";
/*     */     }
/* 255 */     return origName;
/*     */   }
/*     */ 
/*     */   private String renameOptInput(String origName) {
/* 259 */     if (origName == null)
/* 260 */       return "";
/* 261 */     if (origName.equalsIgnoreCase("optInFastPeriod"))
/* 262 */       return "Fast Period";
/* 263 */     if (origName.equalsIgnoreCase("optInSlowPeriod"))
/* 264 */       return "Slow Period";
/* 265 */     if (origName.equalsIgnoreCase("optInTimePeriod"))
/* 266 */       return "Time Period";
/* 267 */     if (origName.equalsIgnoreCase("optInTimePeriod1"))
/* 268 */       return "Time Period 1";
/* 269 */     if (origName.equalsIgnoreCase("optInTimePeriod2"))
/* 270 */       return "Time Period 2";
/* 271 */     if (origName.equalsIgnoreCase("optInTimePeriod3"))
/* 272 */       return "Time Period 3";
/* 273 */     if (origName.equalsIgnoreCase("optInNbDev"))
/* 274 */       return "Nb Dev";
/* 275 */     if (origName.equalsIgnoreCase("optInStartValue"))
/* 276 */       return "Start Value";
/* 277 */     if (origName.equalsIgnoreCase("optInOffsetOnReverse"))
/* 278 */       return "Offset On Reverse";
/* 279 */     if (origName.equalsIgnoreCase("optInAccelerationInitLong"))
/* 280 */       return "Acceleration Init Long";
/* 281 */     if (origName.equalsIgnoreCase("optInAccelerationLong"))
/* 282 */       return "Acceleration Long";
/* 283 */     if (origName.equalsIgnoreCase("optInAccelerationMaxLong"))
/* 284 */       return "Acceleration Max Long";
/* 285 */     if (origName.equalsIgnoreCase("optInAccelerationInitShort"))
/* 286 */       return "Acceleration Init Short";
/* 287 */     if (origName.equalsIgnoreCase("optInAccelerationShort"))
/* 288 */       return "Acceleration Short";
/* 289 */     if (origName.equalsIgnoreCase("optInAccelerationMaxShort"))
/* 290 */       return "Acceleration Max Short";
/* 291 */     if (origName.equalsIgnoreCase("optInFastLimit"))
/* 292 */       return "Fast Limit";
/* 293 */     if (origName.equalsIgnoreCase("optInSlowLimit"))
/* 294 */       return "Slow Limit";
/* 295 */     if (origName.equalsIgnoreCase("optInVFactor"))
/* 296 */       return "V Factor";
/* 297 */     if (origName.equalsIgnoreCase("optInAcceleration"))
/* 298 */       return "Acceleration";
/* 299 */     if (origName.equalsIgnoreCase("optInMaximum"))
/* 300 */       return "Maximum";
/* 301 */     if (origName.equalsIgnoreCase("optInMinPeriod"))
/* 302 */       return "Min Period";
/* 303 */     if (origName.equalsIgnoreCase("optInMaxPeriod"))
/* 304 */       return "Max Period";
/* 305 */     if (origName.equalsIgnoreCase("optInMAType"))
/* 306 */       return "MA Type";
/* 307 */     if (origName.equalsIgnoreCase("optInNbDevUp"))
/* 308 */       return "Nb Dev Up";
/* 309 */     if (origName.equalsIgnoreCase("optInNbDevDn"))
/* 310 */       return "Nb Dev Dn";
/* 311 */     if (origName.equalsIgnoreCase("optInPenetration"))
/* 312 */       return "Penetration";
/* 313 */     if (origName.equalsIgnoreCase("optInFastMAType"))
/* 314 */       return "Fast MAType";
/* 315 */     if (origName.equalsIgnoreCase("optInSlowMAType"))
/* 316 */       return "Slow MAType";
/* 317 */     if (origName.equalsIgnoreCase("optInSignalPeriod"))
/* 318 */       return "Signal Period";
/* 319 */     if (origName.equalsIgnoreCase("optInSignalMAType"))
/* 320 */       return "Signal MAType";
/* 321 */     if (origName.equalsIgnoreCase("optInFastK_Period"))
/* 322 */       return "Fast %K Period";
/* 323 */     if (origName.equalsIgnoreCase("optInFastD_Period"))
/* 324 */       return "Fast %D Period";
/* 325 */     if (origName.equalsIgnoreCase("optInSlowK_Period"))
/* 326 */       return "Slow %K Period";
/* 327 */     if (origName.equalsIgnoreCase("optInSlowD_Period"))
/* 328 */       return "Slow %D Period";
/* 329 */     if (origName.equalsIgnoreCase("optInSlowK_MAType"))
/* 330 */       return "Slow %K MAType";
/* 331 */     if (origName.equalsIgnoreCase("optInSlowD_MAType"))
/* 332 */       return "Slow %D MAType";
/* 333 */     if (origName.equalsIgnoreCase("optInFastK_MAType"))
/* 334 */       return "Fast %K MAType";
/* 335 */     if (origName.equalsIgnoreCase("optInFastD_MAType")) {
/* 336 */       return "Fast %D MAType";
/*     */     }
/* 338 */     return origName;
/*     */   }
/*     */ 
/*     */   private String renameInputs(String origName) {
/* 342 */     if (origName == null)
/* 343 */       return "";
/* 344 */     if (origName.equalsIgnoreCase("inReal"))
/* 345 */       return "Price";
/* 346 */     if (origName.equalsIgnoreCase("inReal0"))
/* 347 */       return "Price 0";
/* 348 */     if (origName.equalsIgnoreCase("inReal1"))
/* 349 */       return "Price 1";
/* 350 */     if (origName.equalsIgnoreCase("inPriceOHLC"))
/* 351 */       return "Price OHLC";
/* 352 */     if (origName.equalsIgnoreCase("inPriceHLCV"))
/* 353 */       return "Price HLCV";
/* 354 */     if (origName.equalsIgnoreCase("inPriceHLC"))
/* 355 */       return "Price HLC";
/* 356 */     if (origName.equalsIgnoreCase("inPriceHL"))
/* 357 */       return "Price HL";
/* 358 */     if (origName.equalsIgnoreCase("inPriceV"))
/* 359 */       return "Price V";
/* 360 */     if (origName.equalsIgnoreCase("inPeriods")) {
/* 361 */       return "Periods";
/*     */     }
/* 363 */     return origName;
/*     */   }
/*     */ 
/*     */   public void onStart(IIndicatorContext context) {
/*     */   }
/*     */ 
/*     */   public IndicatorResult calculate(int startIndex, int endIndex) {
/* 370 */     MInteger outBegIdx = new MInteger();
/* 371 */     MInteger outNbElement = new MInteger();
/*     */     try {
/* 373 */       this.taLibMetaData.callFunc(startIndex, endIndex, outBegIdx, outNbElement);
/*     */     } catch (RuntimeException e) {
/* 375 */       throw e;
/*     */     } catch (Exception e) {
/* 377 */       throw new TaLibException(e);
/*     */     }
/* 379 */     return new IndicatorResult(outBegIdx.value, outNbElement.value);
/*     */   }
/*     */ 
/*     */   public IndicatorInfo getIndicatorInfo() {
/* 383 */     return this.indicatorInfo;
/*     */   }
/*     */ 
/*     */   public InputParameterInfo getInputParameterInfo(int index) {
/* 387 */     return this.inputParamInfos[index];
/*     */   }
/*     */ 
/*     */   public int getLookback() {
/*     */     try {
/* 392 */       return this.taLibMetaData.getLookback();
/*     */     } catch (Exception e) {
/* 394 */       LOGGER.error(e.getMessage(), e);
/* 395 */     }return 0;
/*     */   }
/*     */ 
/*     */   public int getLookforward()
/*     */   {
/* 400 */     return 0;
/*     */   }
/*     */ 
/*     */   public OptInputParameterInfo getOptInputParameterInfo(int index) {
/* 404 */     return this.optInputParamInfos[index];
/*     */   }
/*     */ 
/*     */   public OutputParameterInfo getOutputParameterInfo(int index) {
/* 408 */     return this.outputParamInfos[index];
/*     */   }
/*     */ 
/*     */   public void setInputParameter(int index, Object array) {
/* 412 */     InputParameterInfo inputParamInfo = this.inputParamInfos[index];
/* 413 */     switch (inputParamInfo.getType()) {
/*     */     case DOUBLE:
/* 415 */       this.taLibMetaData.setInputParamReal(index, array);
/* 416 */       break;
/*     */     case PRICE:
/* 418 */       double[][] prices = (double[][])(double[][])array;
/* 419 */       this.taLibMetaData.setInputParamPrice(index, prices[0], prices[2], prices[3], prices[1], prices[4], new double[prices[0].length]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOptInputParameter(int index, Object value)
/*     */   {
/* 425 */     OptInputParameterInfo inputParamInfo = this.optInputParamInfos[index];
/* 426 */     if (((inputParamInfo.getDescription() instanceof IntegerListDescription)) || ((inputParamInfo.getDescription() instanceof IntegerRangeDescription)))
/* 427 */       this.taLibMetaData.setOptInputParamInteger(index, ((Integer)value).intValue());
/* 428 */     else if (((inputParamInfo.getDescription() instanceof DoubleListDescription)) || ((inputParamInfo.getDescription() instanceof DoubleRangeDescription)))
/* 429 */       this.taLibMetaData.setOptInputParamReal(index, ((Double)value).doubleValue());
/*     */   }
/*     */ 
/*     */   public void setOutputParameter(int index, Object array)
/*     */   {
/* 434 */     OutputParameterInfo outputParamInfo = this.outputParamInfos[index];
/* 435 */     switch (outputParamInfo.getType()) {
/*     */     case DOUBLE:
/* 437 */       this.taLibMetaData.setOutputParamReal(index, array);
/* 438 */       break;
/*     */     case INT:
/* 440 */       this.taLibMetaData.setOutputParamInteger(index, array);
/*     */     }
/*     */   }
/*     */ 
/*     */   public double[] getMinMax(int outputIdx, Object values, int firstVisibleValueIndex, int lastVisibleValueIndex)
/*     */   {
/* 447 */     double[] result = null;
/* 448 */     if ((this.fixedMinMax != null) && (this.fixedMinMax.length == 2))
/* 449 */       return this.fixedMinMax;
/* 450 */     if ((values instanceof double[]))
/* 451 */       result = calculateDoubleMinMax((double[])(double[])values, firstVisibleValueIndex, lastVisibleValueIndex);
/* 452 */     else if ((values instanceof int[])) {
/* 453 */       result = calculateIntegerMinMax((int[])(int[])values, firstVisibleValueIndex, lastVisibleValueIndex);
/*     */     }
/*     */ 
/* 456 */     if ((this.fixedMinMax != null) && (this.fixedMinMax.length == 1)) {
/* 457 */       double fixedLevel = this.fixedMinMax[0];
/* 458 */       double min = result[0];
/* 459 */       double max = result[1];
/*     */ 
/* 461 */       if ((fixedLevel > min) && (fixedLevel > max)) {
/* 462 */         result[1] = fixedLevel;
/*     */       }
/* 464 */       else if ((fixedLevel < min) && (fixedLevel < max)) {
/* 465 */         result[0] = fixedLevel;
/*     */       }
/*     */     }
/*     */ 
/* 469 */     return result;
/*     */   }
/*     */ 
/*     */   private double[] calculateIntegerMinMax(int[] intValues, int firstVisibleValueIndex, int lastVisibleValueIndex) {
/* 473 */     double curMin = (0.0D / 0.0D);
/* 474 */     double curMax = (0.0D / 0.0D);
/*     */ 
/* 476 */     int i = firstVisibleValueIndex > 0 ? firstVisibleValueIndex - 1 : 0;
/* 477 */     for (int j = intValues.length - 1 > lastVisibleValueIndex ? lastVisibleValueIndex + 1 : lastVisibleValueIndex; i < j; i++) {
/* 478 */       int curValue = intValues[i];
/* 479 */       if (curValue == -2147483648) {
/*     */         continue;
/*     */       }
/* 482 */       if (curMin == -2147483648.0D) {
/* 483 */         curMin = curValue;
/*     */       }
/* 485 */       if (curMax == -2147483648.0D) {
/* 486 */         curMax = curValue;
/*     */       }
/* 488 */       curMin = curMin > curValue ? curValue : curMin;
/* 489 */       curMax = curValue;
/*     */     }
/* 491 */     if (curMin == curMax) {
/* 492 */       curMin = 1.0D;
/* 493 */       curMax = 2.0D;
/*     */     }
/* 495 */     return new double[] { curMin, curMax };
/*     */   }
/*     */ 
/*     */   private double[] calculateDoubleMinMax(double[] doubleValues, int firstVisibleValueIndex, int lastVisibleValueIndex) {
/* 499 */     double curMin = (0.0D / 0.0D);
/* 500 */     double curMax = (0.0D / 0.0D);
/*     */ 
/* 502 */     int i = firstVisibleValueIndex > 0 ? firstVisibleValueIndex - 1 : 0;
/* 503 */     for (int j = doubleValues.length - 1 > lastVisibleValueIndex ? lastVisibleValueIndex + 1 : lastVisibleValueIndex; i < j; i++) {
/* 504 */       double curValue = doubleValues[i];
/* 505 */       if (Double.isNaN(curValue)) {
/*     */         continue;
/*     */       }
/* 508 */       if (curMin != curMin) {
/* 509 */         curMin = curValue;
/*     */       }
/* 511 */       if (curMax != curMax) {
/* 512 */         curMax = curValue;
/*     */       }
/* 514 */       curMin = curMin > curValue ? curValue : curMin;
/* 515 */       curMax = curMax > curValue ? curMax : curValue;
/*     */     }
/* 517 */     return new double[] { curMin, curMax };
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.TaLibIndicator
 * JD-Core Version:    0.6.0
 */