/*      */ package com.dukascopy.api.connector;
/*      */ 
/*      */ import com.dukascopy.api.IBar;
/*      */ import com.dukascopy.api.IConsole;
/*      */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*      */ import com.dukascopy.api.IIndicators.MaType;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.connector.helpers.RangeHelper;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IIndicatorContext;
/*      */ import com.dukascopy.api.indicators.IIndicatorsProvider;
/*      */ import com.dukascopy.api.indicators.IndicatorResult;
/*      */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*      */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.CommonHelpers;
/*      */ import java.io.PrintStream;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ 
/*      */ public abstract class AbstractConnectorIndicatorImpl extends AbstractConnectorImpl
/*      */   implements IConst, IColor, IIndicator, IWinUser32
/*      */ {
/*   20 */   private static ConcurrentHashMap<String, IIndicator> mapCustomIndicators = null;
/*   21 */   IIndicatorContext context = null;
/*   22 */   int startIndex = 0;
/*   23 */   int endIndex = 0;
/*      */ 
/*      */   public abstract Object getInputParameter(int paramInt);
/*      */ 
/*      */   public void onStart(IIndicatorContext context)
/*      */   {
/*      */     try {
/*   31 */       setContext(context);
/*   32 */       getConnector().onInit(context);
/*      */     }
/*      */     catch (JFException e) {
/*   35 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected IIndicator getIndicatorByName(String name) {
/*   40 */     if (mapCustomIndicators == null) {
/*   41 */       mapCustomIndicators = new ConcurrentHashMap();
/*      */     }
/*   43 */     IIndicator indicator = (IIndicator)mapCustomIndicators.get(name);
/*   44 */     if ((indicator == null) && (getContext() != null) && (getContext().getIndicatorsProvider() != null)) {
/*   45 */       indicator = getContext().getIndicatorsProvider().getIndicator(name);
/*   46 */       mapCustomIndicators.put(name, indicator);
/*      */     }
/*   48 */     return indicator;
/*      */   }
/*      */ 
/*      */   public int ArrayCopySeries(Object array, Number series_index, Object symbol, Number timeframe)
/*      */     throws JFException
/*      */   {
/*   55 */     int size = -1;
/*   56 */     if ((array instanceof double[])) {
/*   57 */       size = ((double[])(double[])array).length;
/*   58 */       IBar[] bars = Bars(Integer.valueOf(0), Integer.valueOf(size));
/*   59 */       double[] src = CommonHelpers.getIndicatorInputData(bars, series_index.intValue());
/*   60 */       System.arraycopy(src, 0, array, 0, size);
/*      */     }
/*   62 */     return size;
/*      */   }
/*      */ 
/*      */   protected int ArrayCopyRates(Object dest_array, Object symbol, Number timeframe) throws JFException
/*      */   {
/*   67 */     int size = -1;
/*   68 */     if ((dest_array instanceof double[][])) {
/*   69 */       double[][] array = (double[][])(double[][])dest_array;
/*   70 */       size = array[0].length;
/*   71 */       int dimentions = array.length;
/*   72 */       IBar[] bars = Bars(Integer.valueOf(0), Integer.valueOf(size));
/*   73 */       double[][] src = CommonHelpers.getIndicatorInputDataReverse(bars);
/*   74 */       for (int i = 0; i < dimentions; i++) {
/*   75 */         System.arraycopy(src[i], 0, array[i], 0, size);
/*      */       }
/*      */     }
/*   78 */     return size;
/*      */   }
/*      */ 
/*      */   public double iMA(Object symbol, Number timeframe, Number timePeriod, Number ma_shift, Number ma_method, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/*  111 */     int startIndex = this.startIndex;
/*  112 */     int endIndex = this.endIndex;
/*      */ 
/*  114 */     double result = (0.0D / 0.0D);
/*  115 */     int numberOfCandlesBefore = shift.intValue() + 1;
/*      */ 
/*  117 */     int numberOfCandlesAfter = 0;
/*  118 */     IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  119 */     IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method.intValue());
/*  120 */     long start = System.currentTimeMillis();
/*      */ 
/*  122 */     IIndicator indicator = getIndicatorByName("MA");
/*      */ 
/*  127 */     indicator.setOptInputParameter(0, Integer.valueOf(timePeriod.intValue()));
/*  128 */     indicator.setOptInputParameter(1, Integer.valueOf(maType.ordinal()));
/*      */ 
/*  130 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), convertedAppliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  131 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  133 */     int lookback = indicator.getLookback();
/*  134 */     double[] values = new double[endIndex];
/*  135 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  137 */     IndicatorResult indicatorResult = indicator.calculate(startIndex, endIndex);
/*  138 */     long end = System.currentTimeMillis();
/*      */ 
/*  141 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length))
/*  142 */       result = values[shift.intValue()];
/*      */     else {
/*  144 */       getContext().getConsole().getErr().print("warning");
/*      */     }
/*      */ 
/*  147 */     return result;
/*      */   }
/*      */ 
/*      */   public double iMAOnArray(double[] array, Number total, Number period, Number ma_shift, Number ma_method, Number shift)
/*      */     throws JFException
/*      */   {
/*  176 */     double result = (0.0D / 0.0D);
/*  177 */     int intTotal = total.intValue();
/*  178 */     IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method.intValue());
/*  179 */     IIndicator indicator = getIndicatorByName("MA");
/*  180 */     indicator.setOptInputParameter(0, Integer.valueOf(period.intValue()));
/*  181 */     indicator.setOptInputParameter(1, Integer.valueOf(maType.ordinal()));
/*  182 */     int lookback = indicator.getLookback();
/*  183 */     indicator.setInputParameter(0, array);
/*  184 */     if (intTotal < 1) {
/*  185 */       intTotal = array.length;
/*      */     }
/*  187 */     double[] outArray = new double[intTotal];
/*  188 */     indicator.setOutputParameter(0, outArray);
/*  189 */     long start = System.currentTimeMillis();
/*  190 */     indicator.calculate(lookback, intTotal - 1);
/*  191 */     long end = System.currentTimeMillis();
/*      */ 
/*  193 */     result = outArray[shift.intValue()];
/*  194 */     return result;
/*      */   }
/*      */ 
/*      */   public double iOBV(Object symbol, Number timeframe, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/*  218 */     int startIndex = this.startIndex;
/*  219 */     int endIndex = this.endIndex;
/*      */ 
/*  221 */     double result = (0.0D / 0.0D);
/*  222 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  223 */     IIndicator indicator = getIndicatorByName("OBV");
/*  224 */     indicator.setOptInputParameter(0, appliedPrice);
/*  225 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  226 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  228 */     double[] values = new double[endIndex];
/*  229 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  231 */     IndicatorResult indicatorResult = indicator.calculate(startIndex, endIndex);
/*  232 */     if ((values != null) && (values.length > 0)) {
/*  233 */       result = values[shift.intValue()];
/*      */     }
/*  235 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAC(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/*  255 */     int startIndex = this.startIndex;
/*  256 */     int endIndex = this.endIndex;
/*  257 */     double result = (0.0D / 0.0D);
/*  258 */     IIndicator indicator = getIndicatorByName("AC");
/*  259 */     indicator.setOptInputParameter(0, Integer.valueOf(timeframe.intValue()));
/*  260 */     indicator.setOptInputParameter(1, Integer.valueOf(34));
/*      */ 
/*  262 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), IIndicators.AppliedPrice.MEDIAN_PRICE, (IBar[])(IBar[])getInputParameter(0));
/*  263 */     indicator.setInputParameter(0, doubleInputs);
/*  264 */     double[] values = new double[endIndex];
/*  265 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  267 */     IndicatorResult indicatorResult = indicator.calculate(startIndex, endIndex);
/*  268 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  269 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  274 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAD(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/*  294 */     int startIndex = this.startIndex;
/*  295 */     int endIndex = this.endIndex;
/*  296 */     double result = (0.0D / 0.0D);
/*  297 */     IIndicator indicator = getIndicatorByName("AD");
/*      */ 
/*  300 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), IIndicators.AppliedPrice.MEDIAN_PRICE, (IBar[])(IBar[])getInputParameter(0));
/*  301 */     indicator.setInputParameter(0, doubleInputs);
/*  302 */     double[] values = new double[endIndex];
/*  303 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  305 */     IndicatorResult indicatorResult = indicator.calculate(startIndex, endIndex);
/*  306 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  307 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  312 */     return result;
/*      */   }
/*      */ 
/*      */   public double iADX(Object symbol, Number timeframe, Number timePeriod, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/*  339 */     double result = (0.0D / 0.0D);
/*      */ 
/*  341 */     IIndicator indicator = getIndicatorByName("ADX");
/*  342 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  343 */     indicator.setOptInputParameter(0, Integer.valueOf(timePeriod.intValue()));
/*      */ 
/*  345 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  346 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  348 */     int lookback = indicator.getLookback();
/*  349 */     double[] values = new double[this.endIndex];
/*  350 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  352 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  353 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  354 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  358 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAlligator(Object symbol, Number timeframe, Number jaw_period, Number jaw_shift, Number teeth_period, Number teeth_shift, Number lips_period, Number lips_shift, Number ma_method, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/*  404 */     double result = (0.0D / 0.0D);
/*  405 */     IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  406 */     IIndicator indicator = getIndicatorByName("ALLIGATOR");
/*  407 */     indicator.setOptInputParameter(0, jaw_period);
/*  408 */     indicator.setOptInputParameter(1, teeth_period);
/*  409 */     indicator.setOptInputParameter(2, lips_period);
/*      */ 
/*  411 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), convertedAppliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  412 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  414 */     int lookback = indicator.getLookback();
/*  415 */     double[] values = new double[this.endIndex];
/*  416 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  418 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  419 */     long end = System.currentTimeMillis();
/*  420 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  421 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  426 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAO(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/*  446 */     double result = (0.0D / 0.0D);
/*  447 */     IIndicator indicator = getIndicatorByName("AWESOME");
/*  448 */     IIndicators.AppliedPrice appliedPrice = IIndicators.AppliedPrice.MEDIAN_PRICE;
/*  449 */     indicator.setOptInputParameter(0, Integer.valueOf(4));
/*  450 */     indicator.setOptInputParameter(1, Integer.valueOf(IIndicators.MaType.SMA.ordinal()));
/*  451 */     indicator.setOptInputParameter(2, Integer.valueOf(10));
/*  452 */     indicator.setOptInputParameter(3, Integer.valueOf(IIndicators.MaType.SMA.ordinal()));
/*      */ 
/*  454 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  455 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  457 */     int lookback = indicator.getLookback();
/*  458 */     double[] values = new double[this.endIndex];
/*  459 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  461 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  462 */     long end = System.currentTimeMillis();
/*  463 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  464 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  468 */     return result;
/*      */   }
/*      */ 
/*      */   public double iATR(Object symbol, Number timeframe, Number timePeriod, Number shift)
/*      */     throws JFException
/*      */   {
/*  491 */     double result = 0.0D;
/*  492 */     IIndicator indicator = getIndicatorByName("ATR");
/*  493 */     indicator.setOptInputParameter(0, Integer.valueOf(timePeriod.intValue()));
/*      */ 
/*  495 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), null, (IBar[])(IBar[])getInputParameter(0));
/*  496 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  498 */     double[] values = new double[this.endIndex];
/*  499 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  501 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  502 */     long end = System.currentTimeMillis();
/*  503 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  504 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  508 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBands(Object symbol, Number timeframe, Number timePeriod, Number deviation, Number bands_shift, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/*  541 */     double result = (0.0D / 0.0D);
/*  542 */     IIndicator indicator = getIndicatorByName("BBANDS");
/*  543 */     indicator.setOptInputParameter(0, Integer.valueOf(timePeriod.intValue()));
/*  544 */     indicator.setOptInputParameter(1, Double.valueOf(deviation.doubleValue()));
/*  545 */     indicator.setOptInputParameter(2, Double.valueOf(deviation.doubleValue() - bands_shift.doubleValue()));
/*  546 */     indicator.setOptInputParameter(3, Integer.valueOf(IIndicators.MaType.SMA.ordinal()));
/*  547 */     double[] res = null;
/*  548 */     IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  549 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), convertedAppliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  550 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  552 */     int lookback = indicator.getLookback();
/*  553 */     double[] values = new double[this.endIndex];
/*  554 */     double[] values1 = new double[this.endIndex];
/*  555 */     double[] values2 = new double[this.endIndex];
/*  556 */     indicator.setOutputParameter(0, values);
/*  557 */     indicator.setOutputParameter(1, values1);
/*  558 */     indicator.setOutputParameter(2, values2);
/*      */ 
/*  560 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  561 */     long end = System.currentTimeMillis();
/*  562 */     if (mode.intValue() == 1) {
/*  563 */       if ((values != null) && (values.length > 2))
/*  564 */         result = values[shift.intValue()];
/*      */     }
/*  566 */     else if (mode.intValue() == 2) {
/*  567 */       if ((values2 != null) && (values2.length > 2))
/*  568 */         result = values2[shift.intValue()];
/*      */     }
/*      */     else {
/*  571 */       CommonHelpers.notSupported();
/*      */     }
/*      */ 
/*  574 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBandsOnArray(double[] array, Number total, Number period, Number deviation, Number bands_shift, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/*  609 */     double result = (0.0D / 0.0D);
/*  610 */     IIndicator indicator = getIndicatorByName("BBANDS");
/*  611 */     indicator.setOptInputParameter(0, period);
/*  612 */     int lookback = indicator.getLookback();
/*  613 */     indicator.setInputParameter(0, array);
/*  614 */     int arraySize = total.intValue();
/*  615 */     if (arraySize < 1) {
/*  616 */       arraySize = array.length;
/*      */     }
/*  618 */     double[] outArray = new double[arraySize];
/*  619 */     indicator.setOutputParameter(0, outArray);
/*  620 */     indicator.calculate(lookback, arraySize - 1);
/*  621 */     result = outArray[shift.intValue()];
/*  622 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBearsPower(Object symbol, Number timeframe, Number timePeriod, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/*  648 */     double result = (0.0D / 0.0D);
/*  649 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  650 */     IIndicator indicator = getIndicatorByName("BEARP");
/*  651 */     indicator.setOptInputParameter(0, timePeriod);
/*  652 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  653 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  655 */     int lookback = indicator.getLookback();
/*  656 */     double[] values = new double[this.endIndex];
/*  657 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  659 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  660 */     long end = System.currentTimeMillis();
/*  661 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  662 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  667 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBullsPower(Object symbol, Number timeframe, Number timePeriod, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/*  693 */     double result = (0.0D / 0.0D);
/*  694 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  695 */     int numberOfCandlesBefore = shift.intValue() + 1;
/*  696 */     long time = Time(shift);
/*  697 */     int numberOfCandlesAfter = 0;
/*      */ 
/*  699 */     IIndicator indicator = getIndicatorByName("BULLP");
/*  700 */     indicator.setOptInputParameter(0, timePeriod);
/*  701 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  702 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  704 */     int lookback = indicator.getLookback();
/*  705 */     double[] values = new double[this.endIndex];
/*  706 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  708 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  709 */     long end = System.currentTimeMillis();
/*  710 */     if ((values != null) && (values.length > 0) && (shift.intValue() < values.length)) {
/*  711 */       result = values[shift.intValue()];
/*      */     }
/*  713 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBWMFI(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/*  734 */     int numberOfCandlesBefore = shift.intValue() + 1;
/*  735 */     long time = Time(shift);
/*  736 */     int numberOfCandlesAfter = 0;
/*  737 */     int timePeriod = 0;
/*  738 */     double result = (0.0D / 0.0D);
/*      */ 
/*  740 */     IIndicator indicator = getIndicatorByName("BWMFI");
/*  741 */     indicator.setOptInputParameter(0, Integer.valueOf(timePeriod));
/*      */ 
/*  743 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), null, (IBar[])(IBar[])getInputParameter(0));
/*  744 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  746 */     int lookback = indicator.getLookback();
/*  747 */     double[] values = new double[this.endIndex];
/*  748 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  750 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  751 */     long end = System.currentTimeMillis();
/*  752 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  753 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  757 */     return result;
/*      */   }
/*      */ 
/*      */   public double iCCI(Object symbol, Number timeframe, Number timePeriod, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/*  784 */     double result = (0.0D / 0.0D);
/*  785 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*      */ 
/*  787 */     IIndicator indicator = getIndicatorByName("CCI");
/*  788 */     indicator.setOptInputParameter(0, timePeriod);
/*      */ 
/*  790 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  791 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  793 */     int lookback = indicator.getLookback();
/*  794 */     double[] values = new double[this.endIndex];
/*  795 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  797 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  798 */     long end = System.currentTimeMillis();
/*  799 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  800 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  804 */     return result;
/*      */   }
/*      */ 
/*      */   public double iCCIOnArray(double[] array, Number total, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/*  829 */     double result = (0.0D / 0.0D);
/*  830 */     IIndicator maInd = getIndicatorByName("CCI");
/*  831 */     maInd.setOptInputParameter(0, period);
/*  832 */     int lookback = maInd.getLookback();
/*  833 */     maInd.setInputParameter(0, array);
/*  834 */     int arraySize = total.intValue();
/*  835 */     if (arraySize < 1) {
/*  836 */       arraySize = array.length;
/*      */     }
/*  838 */     double[] outArray = new double[arraySize];
/*  839 */     maInd.setOutputParameter(0, outArray);
/*  840 */     maInd.calculate(lookback, arraySize - 1);
/*  841 */     result = outArray[shift.intValue()];
/*  842 */     return result;
/*      */   }
/*      */ 
/*      */   public double iDeMarker(Object symbol, Number timeframe, Number timePeriod, Number shift)
/*      */     throws JFException
/*      */   {
/*  864 */     double result = (0.0D / 0.0D);
/*  865 */     IIndicator indicator = getIndicatorByName("DEMA");
/*      */ 
/*  867 */     IIndicators.AppliedPrice appliedPrice = IIndicators.AppliedPrice.MEDIAN_PRICE;
/*  868 */     indicator.setOptInputParameter(0, timePeriod);
/*      */ 
/*  870 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  871 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  873 */     int lookback = indicator.getLookback();
/*  874 */     double[] values = new double[this.endIndex];
/*  875 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  877 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  878 */     long end = System.currentTimeMillis();
/*  879 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  880 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  884 */     return result;
/*      */   }
/*      */ 
/*      */   public double iEnvelopes(Object symbol, Number timeframe, Number ma_period, Number ma_method, Number ma_shift, Number applied_price, Number deviation, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/*  922 */     double result = (0.0D / 0.0D);
/*  923 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*  924 */     IIndicator indicator = getIndicatorByName("MAEnvelope");
/*  925 */     indicator.setOptInputParameter(0, Integer.valueOf(ma_period.intValue()));
/*  926 */     indicator.setOptInputParameter(1, Double.valueOf(deviation.doubleValue()));
/*      */ 
/*  928 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/*  929 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/*  931 */     int lookback = indicator.getLookback();
/*  932 */     double[] values = new double[this.endIndex];
/*  933 */     indicator.setOutputParameter(0, values);
/*      */ 
/*  935 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/*  936 */     long end = System.currentTimeMillis();
/*  937 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/*  938 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/*  943 */     return result;
/*      */   }
/*      */ 
/*      */   public double iEnvelopesOnArray(double[] array, Number total, Number ma_period, Number ma_method, Number ma_shift, Number deviation, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/*  979 */     double rc = (0.0D / 0.0D);
/*  980 */     IIndicator indicator = getIndicatorByName("MAEnvelope");
/*  981 */     indicator.setOptInputParameter(0, Integer.valueOf(ma_period.intValue()));
/*  982 */     indicator.setOptInputParameter(1, Double.valueOf(deviation.doubleValue()));
/*  983 */     int lookback = indicator.getLookback();
/*  984 */     indicator.setInputParameter(0, array);
/*  985 */     int arraySize = total.intValue();
/*  986 */     if (arraySize < 1) {
/*  987 */       arraySize = array.length;
/*      */     }
/*  989 */     double[] outArray = new double[arraySize];
/*  990 */     indicator.setOutputParameter(0, outArray);
/*  991 */     indicator.calculate(lookback, arraySize - 1);
/*  992 */     rc = outArray[shift.intValue()];
/*  993 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iForce(Object symbol, Number timeframe, Number period, Number ma_method, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 1022 */     double result = (0.0D / 0.0D);
/* 1023 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1024 */     long time = Time(shift);
/* 1025 */     int numberOfCandlesAfter = 0;
/* 1026 */     int timePeriod = 0;
/*      */ 
/* 1028 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*      */ 
/* 1030 */     IIndicator indicator = getIndicatorByName("FORCEI");
/* 1031 */     indicator.setOptInputParameter(0, Integer.valueOf(period.intValue()));
/*      */ 
/* 1033 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1034 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1036 */     int lookback = indicator.getLookback();
/* 1037 */     double[] values = new double[this.endIndex];
/* 1038 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1040 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1041 */     long end = System.currentTimeMillis();
/* 1042 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1043 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1047 */     return result;
/*      */   }
/*      */ 
/*      */   public double iFractals(Object symbol, Number timeframe, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1071 */     double result = (0.0D / 0.0D);
/* 1072 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1073 */     long time = Time(shift);
/* 1074 */     int numberOfCandlesAfter = 0;
/* 1075 */     int timePeriod = 0;
/*      */ 
/* 1077 */     IIndicator indicator = getIndicatorByName("FRACTAL");
/* 1078 */     IIndicators.AppliedPrice appliedPrice = null;
/* 1079 */     indicator.setOptInputParameter(0, Integer.valueOf(timePeriod));
/*      */ 
/* 1081 */     double[][] doubleInputs = (double[][])(double[][])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1082 */     indicator.setInputParameter(0, doubleInputs);
/* 1083 */     this.endIndex = 5;
/* 1084 */     int lookback = indicator.getLookback();
/* 1085 */     double[][] values = new double[2][this.endIndex + 1];
/* 1086 */     indicator.setOutputParameter(0, values[0]);
/* 1087 */     indicator.setOutputParameter(1, values[1]);
/*      */ 
/* 1089 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1090 */     long end = System.currentTimeMillis();
/* 1091 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1092 */       result = values[0][shift.intValue()];
/*      */     }
/*      */ 
/* 1096 */     return result;
/*      */   }
/*      */ 
/*      */   public double iGator(Object symbol, Number timeframe, Number jaw_period, Number jaw_shift, Number teeth_period, Number teeth_shift, Number lips_period, Number lips_shift, Number ma_method, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1142 */     double result = (0.0D / 0.0D);
/*      */ 
/* 1144 */     IIndicator indicator = getIndicatorByName("IGATOR");
/* 1145 */     IIndicators.AppliedPrice appliedPrice = IIndicators.AppliedPrice.MEDIAN_PRICE;
/* 1146 */     indicator.setOptInputParameter(0, jaw_period);
/* 1147 */     indicator.setOptInputParameter(1, teeth_period);
/* 1148 */     indicator.setOptInputParameter(2, lips_period);
/*      */ 
/* 1150 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1151 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1153 */     int lookback = indicator.getLookback();
/* 1154 */     double[] values = new double[this.endIndex];
/* 1155 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1157 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1158 */     long end = System.currentTimeMillis();
/* 1159 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1160 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1164 */     return result;
/*      */   }
/*      */ 
/*      */   public double iIchimoku(Object symbol, Number timeframe, Number tenkan_sen, Number kijun_sen, Number senkou_span_b, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1194 */     double result = (0.0D / 0.0D);
/* 1195 */     IIndicator indicator = getIndicatorByName("ICHIMOKU");
/* 1196 */     IIndicators.AppliedPrice appliedPrice = IIndicators.AppliedPrice.OPEN;
/* 1197 */     indicator.setOptInputParameter(0, tenkan_sen);
/* 1198 */     indicator.setOptInputParameter(1, kijun_sen);
/* 1199 */     indicator.setOptInputParameter(2, senkou_span_b);
/*      */ 
/* 1201 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1202 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1204 */     int lookback = indicator.getLookback();
/* 1205 */     double[] values = new double[this.endIndex];
/* 1206 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1208 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1209 */     long end = System.currentTimeMillis();
/* 1210 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1211 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1215 */     return result;
/*      */   }
/*      */ 
/*      */   public double iMACD(Object symbol, Number timeframe, Number fast_ema_period, Number slow_ema_period, Number signal_period, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1240 */     double rc = (0.0D / 0.0D);
/* 1241 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1242 */     long time = Time(shift);
/* 1243 */     int numberOfCandlesAfter = 0;
/* 1244 */     int timePeriod = 0;
/*      */ 
/* 1246 */     double[] res = null;
/* 1247 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/*      */ 
/* 1249 */     IIndicator indicator = getIndicatorByName("MACD");
/* 1250 */     indicator.setOptInputParameter(0, Integer.valueOf(fast_ema_period.intValue()));
/* 1251 */     indicator.setOptInputParameter(1, Integer.valueOf(slow_ema_period.intValue()));
/* 1252 */     indicator.setOptInputParameter(2, Integer.valueOf(signal_period.intValue()));
/*      */ 
/* 1254 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1255 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1257 */     int lookback = indicator.getLookback();
/* 1258 */     double[] values = new double[this.endIndex];
/* 1259 */     double[] values1 = new double[this.endIndex];
/* 1260 */     double[] values2 = new double[this.endIndex];
/* 1261 */     indicator.setOutputParameter(0, values);
/* 1262 */     indicator.setOutputParameter(1, values1);
/* 1263 */     indicator.setOutputParameter(2, values2);
/*      */ 
/* 1265 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1266 */     long end = System.currentTimeMillis();
/*      */ 
/* 1268 */     if (mode.intValue() == 0) {
/* 1269 */       if ((values != null) && (values.length > 1))
/* 1270 */         rc = values[shift.intValue()];
/*      */     }
/* 1272 */     else if (mode.intValue() == 1) {
/* 1273 */       if ((values1 != null) && (values1.length > 1))
/* 1274 */         rc = values1[shift.intValue()];
/*      */     }
/*      */     else {
/* 1277 */       CommonHelpers.notSupported();
/*      */     }
/*      */ 
/* 1280 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iMFI(Object symbol, Number timeframe, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 1305 */     double result = (0.0D / 0.0D);
/* 1306 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1307 */     long time = Time(shift);
/* 1308 */     int numberOfCandlesAfter = 0;
/* 1309 */     int timePeriod = 0;
/* 1310 */     IIndicators.AppliedPrice appliedPrice = null;
/* 1311 */     IIndicator indicator = getIndicatorByName("MFI");
/* 1312 */     indicator.setOptInputParameter(0, period);
/*      */ 
/* 1314 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1315 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1317 */     int lookback = indicator.getLookback();
/* 1318 */     double[] values = new double[this.endIndex];
/* 1319 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1321 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1322 */     long end = System.currentTimeMillis();
/* 1323 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1324 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1329 */     return result;
/*      */   }
/*      */ 
/*      */   public double iMomentum(Object symbol, Number timeframe, Number period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 1353 */     double result = (0.0D / 0.0D);
/* 1354 */     IIndicator indicator = getIndicatorByName("MOM");
/* 1355 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/* 1356 */     indicator.setOptInputParameter(0, period);
/*      */ 
/* 1358 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1359 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1361 */     int lookback = indicator.getLookback();
/* 1362 */     double[] values = new double[this.endIndex];
/* 1363 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1365 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1366 */     long end = System.currentTimeMillis();
/* 1367 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1368 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1372 */     return result;
/*      */   }
/*      */ 
/*      */   public double iMomentumOnArray(double[] array, Number total, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 1397 */     double rc = (0.0D / 0.0D);
/* 1398 */     IIndicator indicator = getIndicatorByName("MOM");
/* 1399 */     indicator.setOptInputParameter(0, period);
/* 1400 */     indicator.setInputParameter(0, array);
/* 1401 */     int lookback = indicator.getLookback();
/* 1402 */     indicator.setInputParameter(0, array);
/* 1403 */     int arraySize = total.intValue();
/* 1404 */     if (arraySize < 1) {
/* 1405 */       arraySize = array.length;
/*      */     }
/* 1407 */     double[] outArray = new double[arraySize];
/* 1408 */     indicator.setOutputParameter(0, outArray);
/* 1409 */     indicator.calculate(lookback, arraySize - 1);
/* 1410 */     rc = outArray[shift.intValue()];
/* 1411 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iOsMA(Object symbol, Number timeframe, Number fast_ema_period, Number slow_ema_period, Number signal_period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 1443 */     double result = (0.0D / 0.0D);
/* 1444 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1445 */     long time = Time(shift);
/* 1446 */     int numberOfCandlesAfter = 0;
/*      */ 
/* 1448 */     IIndicator indicator = getIndicatorByName("OsMA");
/* 1449 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/* 1450 */     indicator.setOptInputParameter(0, fast_ema_period);
/* 1451 */     indicator.setOptInputParameter(1, slow_ema_period);
/* 1452 */     indicator.setOptInputParameter(2, signal_period);
/*      */ 
/* 1454 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1455 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1457 */     int lookback = indicator.getLookback();
/* 1458 */     double[] values = new double[this.endIndex];
/* 1459 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1461 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1462 */     long end = System.currentTimeMillis();
/* 1463 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1464 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1468 */     return result;
/*      */   }
/*      */ 
/*      */   public double iRSI(Object symbol, Number timeframe, Number timePeriod, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 1497 */     double result = (0.0D / 0.0D);
/* 1498 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1499 */     long time = Time(shift);
/* 1500 */     int numberOfCandlesAfter = 0;
/* 1501 */     IIndicator indicator = getIndicatorByName("RSI");
/* 1502 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/* 1503 */     indicator.setOptInputParameter(0, timePeriod);
/*      */ 
/* 1505 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1506 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1508 */     int lookback = indicator.getLookback();
/* 1509 */     double[] values = new double[this.endIndex];
/* 1510 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1512 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1513 */     long end = System.currentTimeMillis();
/* 1514 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1515 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1519 */     return result;
/*      */   }
/*      */ 
/*      */   public double iRSIOnArray(double[] array, Number total, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 1544 */     double result = (0.0D / 0.0D);
/* 1545 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1546 */     int numberOfCandlesAfter = 0;
/* 1547 */     int timePeriod = 0;
/* 1548 */     IIndicator indicator = getIndicatorByName("RSI");
/* 1549 */     indicator.setOptInputParameter(0, period);
/* 1550 */     int lookback = indicator.getLookback();
/* 1551 */     indicator.setInputParameter(0, array);
/* 1552 */     int intTotal = total.intValue();
/* 1553 */     if (intTotal < 1) {
/* 1554 */       intTotal = array.length;
/*      */     }
/* 1556 */     double[] outArray = new double[intTotal];
/* 1557 */     indicator.setOutputParameter(0, outArray);
/* 1558 */     long start = System.currentTimeMillis();
/* 1559 */     indicator.calculate(lookback, intTotal - 1);
/* 1560 */     long end = System.currentTimeMillis();
/* 1561 */     result = outArray[shift.intValue()];
/* 1562 */     return result;
/*      */   }
/*      */ 
/*      */   public double iRVI(Object symbol, Number timeframe, Number period, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1588 */     double result = (0.0D / 0.0D);
/* 1589 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1590 */     long time = Time(shift);
/* 1591 */     int numberOfCandlesAfter = 0;
/* 1592 */     IIndicator indicator = getIndicatorByName("RVI");
/* 1593 */     IIndicators.AppliedPrice appliedPrice = null;
/* 1594 */     indicator.setOptInputParameter(0, period);
/*      */ 
/* 1596 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1597 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1599 */     int lookback = indicator.getLookback();
/* 1600 */     double[] values = new double[this.endIndex];
/* 1601 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1603 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1604 */     long end = System.currentTimeMillis();
/* 1605 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1606 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1610 */     return result;
/*      */   }
/*      */ 
/*      */   public double iStdDev(Object symbol, Number timeframe, Number ma_period, Number ma_shift, Number ma_method, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 1674 */     double result = (0.0D / 0.0D);
/* 1675 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1676 */     long time = Time(shift);
/* 1677 */     int numberOfCandlesAfter = 0;
/* 1678 */     IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price.intValue());
/* 1679 */     IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method.intValue());
/* 1680 */     IIndicator indicator = getIndicatorByName("STDDEV");
/* 1681 */     IIndicators.AppliedPrice appliedPrice = null;
/* 1682 */     indicator.setOptInputParameter(0, Integer.valueOf(ma_period.intValue()));
/* 1683 */     indicator.setOptInputParameter(1, Integer.valueOf(1));
/*      */ 
/* 1685 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), appliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1686 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1688 */     int lookback = indicator.getLookback();
/* 1689 */     double[] values = new double[this.endIndex];
/* 1690 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1692 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1693 */     long end = System.currentTimeMillis();
/* 1694 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1695 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1699 */     return result;
/*      */   }
/*      */ 
/*      */   public double iStdDevOnArray(double[] array, Number total, Number ma_period, Number ma_shift, Number ma_method, Number shift)
/*      */     throws JFException
/*      */   {
/* 1729 */     double rc = (0.0D / 0.0D);
/* 1730 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1731 */     int numberOfCandlesAfter = 0;
/* 1732 */     IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method.intValue());
/* 1733 */     IIndicator maInd = getIndicatorByName("STDDEV");
/* 1734 */     maInd.setOptInputParameter(0, ma_period);
/* 1735 */     maInd.setOptInputParameter(1, ma_shift);
/* 1736 */     maInd.setOptInputParameter(2, ma_method);
/* 1737 */     int lookback = maInd.getLookback();
/*      */ 
/* 1739 */     maInd.setInputParameter(0, array);
/* 1740 */     int arraySize = total.intValue();
/* 1741 */     if (arraySize < 1) {
/* 1742 */       arraySize = array.length;
/*      */     }
/* 1744 */     double[] outArray = new double[arraySize];
/* 1745 */     maInd.setOutputParameter(0, outArray);
/* 1746 */     maInd.calculate(lookback, arraySize - 1);
/* 1747 */     rc = outArray[shift.intValue()];
/* 1748 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iStochastic(Object symbol, Number timeframe, Number kPeriod, Number dPeriod, Number slowing, Number method, Number price_field, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1786 */     double result = (0.0D / 0.0D);
/* 1787 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1788 */     long time = Time(shift);
/* 1789 */     int numberOfCandlesAfter = 0;
/*      */ 
/* 1791 */     IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(price_field.intValue());
/* 1792 */     IIndicators.MaType maType = CommonHelpers.convertMaType(method.intValue());
/* 1793 */     IIndicator indicator = getIndicatorByName("STOCHRSI");
/*      */ 
/* 1796 */     int timePeriod = RangeHelper.getRangeAdjustedValue((IntegerRangeDescription)indicator.getOptInputParameterInfo(0).getDescription(), slowing.intValue());
/*      */ 
/* 1798 */     indicator.setOptInputParameter(0, Integer.valueOf(timePeriod));
/* 1799 */     indicator.setOptInputParameter(1, Integer.valueOf(kPeriod.intValue()));
/* 1800 */     indicator.setOptInputParameter(2, Integer.valueOf(dPeriod.intValue()));
/* 1801 */     indicator.setOptInputParameter(3, Integer.valueOf(maType.ordinal()));
/*      */ 
/* 1803 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), convertedAppliedPrice, (IBar[])(IBar[])getInputParameter(0));
/* 1804 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1806 */     double[] values = new double[this.endIndex];
/* 1807 */     double[] values2 = new double[this.endIndex];
/* 1808 */     indicator.setOutputParameter(0, values);
/* 1809 */     indicator.setOutputParameter(1, values2);
/*      */ 
/* 1812 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1813 */     long end = System.currentTimeMillis();
/* 1814 */     if (mode.intValue() == 0) {
/* 1815 */       if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1816 */         result = values[shift.intValue()];
/*      */       }
/*      */ 
/*      */     }
/* 1821 */     else if (mode.intValue() == 1) {
/* 1822 */       if ((values2 != null) && (values2.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values2.length)) {
/* 1823 */         result = values2[shift.intValue()];
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1829 */       CommonHelpers.notSupported();
/*      */     }
/*      */ 
/* 1832 */     return result;
/*      */   }
/*      */ 
/*      */   private int getRangeCheckedValue(IntegerRangeDescription description, int value)
/*      */   {
/* 1837 */     return description.getMax() < value ? description.getMax() : description.getMin() > value ? description.getMin() : value;
/*      */   }
/*      */ 
/*      */   public double iWPR(Object symbol, Number timeframe, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 1861 */     double result = (0.0D / 0.0D);
/* 1862 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1863 */     long time = Time(shift);
/* 1864 */     int numberOfCandlesAfter = 0;
/*      */ 
/* 1866 */     IIndicator indicator = getIndicatorByName("WILLR");
/* 1867 */     indicator.setOptInputParameter(0, Integer.valueOf(period.intValue()));
/* 1868 */     int lookback = indicator.getLookback();
/*      */ 
/* 1870 */     double[] doubleInputs = (double[])(double[])CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), null, (IBar[])(IBar[])getInputParameter(0));
/* 1871 */     indicator.setInputParameter(0, doubleInputs);
/*      */ 
/* 1873 */     double[] values = new double[this.endIndex];
/* 1874 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1876 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1877 */     long end = System.currentTimeMillis();
/* 1878 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1879 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1883 */     return result;
/*      */   }
/*      */ 
/*      */   protected double iCustom(Object symbol, Number timeframe, String name, String string, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1916 */     return iCustom(symbol, timeframe, name, Integer.valueOf(mode.intValue()), Integer.valueOf(shift.intValue()), new Object[] { string });
/*      */   }
/*      */ 
/*      */   protected double iCustom(Object symbol, Number timeframe, String name, Object[] custom, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 1922 */     return iCustom(symbol, timeframe, name, mode, shift, custom);
/*      */   }
/*      */ 
/*      */   protected double iCustom(Object symbol, Number timeframe, String name) throws JFException
/*      */   {
/* 1927 */     return iCustom(symbol, timeframe, name, Integer.valueOf(0), Integer.valueOf(0), null);
/*      */   }
/*      */ 
/*      */   public double iCustom(Object symbol, Number timeframe, String name, Number mode, Number shift, Object[] custom)
/*      */     throws JFException
/*      */   {
/* 1933 */     double result = (0.0D / 0.0D);
/*      */ 
/* 1936 */     int numberOfCandlesBefore = shift.intValue() + 1;
/* 1937 */     long time = Time(shift);
/* 1938 */     int numberOfCandlesAfter = 0;
/*      */ 
/* 1940 */     IIndicator indicator = getIndicatorByName(name);
/* 1941 */     Object[] optParams = null;
/* 1942 */     if (custom != null) {
/* 1943 */       optParams = new Object[custom.length];
/* 1944 */       for (int i = 0; i < custom.length; i++) {
/* 1945 */         optParams[i] = custom[i];
/* 1946 */         indicator.setOptInputParameter(i, custom[i]);
/*      */       }
/*      */     }
/* 1949 */     int lookback = indicator.getLookback();
/*      */ 
/* 1951 */     Object doubleInputs = CommonHelpers.getIndicatorInputData(indicator.getInputParameterInfo(0), null, (IBar[])(IBar[])getInputParameter(0));
/* 1952 */     indicator.setInputParameter(0, doubleInputs);
/* 1953 */     double[] values = new double[this.endIndex];
/* 1954 */     indicator.setOutputParameter(0, values);
/*      */ 
/* 1956 */     IndicatorResult indicatorResult = indicator.calculate(this.startIndex, this.endIndex);
/* 1957 */     long end = System.currentTimeMillis();
/* 1958 */     if ((values != null) && (values.length > 0) && (shift.intValue() > -1) && (shift.intValue() < values.length)) {
/* 1959 */       result = values[shift.intValue()];
/*      */     }
/*      */ 
/* 1963 */     return result;
/*      */   }
/*      */ 
/*      */   public int iLowest(Object symbol, Number timeframe, Number typeNumber, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/* 1970 */     int rc = -1;
/* 1971 */     int type = typeNumber.intValue();
/* 1972 */     IBar[] bars = Bars(start, count);
/* 1973 */     double lowValue = 1.7976931348623157E+308D;
/* 1974 */     for (int i = 0; i < bars.length; i++)
/*      */     {
/* 1976 */       IBar bar = bars[i];
/*      */ 
/* 1978 */       double value = 0.0D;
/* 1979 */       switch (type) {
/*      */       case 0:
/* 1981 */         value = bar.getOpen();
/* 1982 */         break;
/*      */       case 1:
/* 1984 */         value = bar.getLow();
/* 1985 */         break;
/*      */       case 2:
/* 1987 */         value = bar.getHigh();
/* 1988 */         break;
/*      */       case 3:
/* 1990 */         value = bar.getClose();
/* 1991 */         break;
/*      */       case 4:
/* 1993 */         value = bar.getVolume();
/* 1994 */         break;
/*      */       case 5:
/* 1996 */         value = bar.getTime() / 1000L;
/* 1997 */         break;
/*      */       default:
/* 1999 */         value = bar.getOpen();
/*      */       }
/*      */ 
/* 2002 */       if (value < lowValue) {
/* 2003 */         rc = i + start.intValue();
/* 2004 */         lowValue = value;
/*      */       }
/*      */     }
/*      */ 
/* 2008 */     return rc;
/*      */   }
/*      */ 
/*      */   public int iHighest(Object symbol, Number timeframe, Number typeNumber, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/* 2018 */     int rc = -1;
/* 2019 */     int type = typeNumber.intValue();
/* 2020 */     IBar[] bars = Bars(start, count);
/* 2021 */     double highValue = 4.9E-324D;
/* 2022 */     for (int i = 0; i < bars.length; i++)
/*      */     {
/* 2024 */       IBar bar = bars[i];
/*      */ 
/* 2026 */       double value = 0.0D;
/* 2027 */       switch (type) {
/*      */       case 0:
/* 2029 */         value = bar.getOpen();
/* 2030 */         break;
/*      */       case 1:
/* 2032 */         value = bar.getLow();
/* 2033 */         break;
/*      */       case 2:
/* 2035 */         value = bar.getHigh();
/* 2036 */         break;
/*      */       case 3:
/* 2038 */         value = bar.getClose();
/* 2039 */         break;
/*      */       case 4:
/* 2041 */         value = bar.getVolume();
/* 2042 */         break;
/*      */       case 5:
/* 2044 */         value = bar.getTime() / 1000L;
/* 2045 */         break;
/*      */       default:
/* 2047 */         value = bar.getOpen();
/*      */       }
/*      */ 
/* 2050 */       if (value > highValue) {
/* 2051 */         rc = i + start.intValue();
/* 2052 */         highValue = value;
/*      */       }
/*      */     }
/* 2055 */     return rc; } 
/*      */   protected abstract IBar[] Bars(Number paramNumber1, Number paramNumber2) throws JFException;
/*      */ 
/*      */   protected abstract IBar[] Bars() throws JFException;
/*      */ 
/*      */   protected abstract IBar Bar(Number paramNumber) throws JFException;
/*      */ 
/* 2065 */   public IIndicatorContext getContext() { return this.context; }
/*      */ 
/*      */   public void setContext(IIndicatorContext context)
/*      */   {
/* 2069 */     this.context = context;
/*      */   }
/*      */ 
/*      */   public int getStartIndex() {
/* 2073 */     synchronized (this) {
/* 2074 */       return this.startIndex;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setStartIndex(int startIndex) {
/* 2079 */     synchronized (this) {
/* 2080 */       this.startIndex = startIndex;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getEndIndex() {
/* 2085 */     synchronized (this) {
/* 2086 */       return this.endIndex;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setEndIndex(int endIndex) {
/* 2091 */     synchronized (this) {
/* 2092 */       this.endIndex = endIndex;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.AbstractConnectorIndicatorImpl
 * JD-Core Version:    0.6.0
 */