/*      */ package com.dukascopy.api;
/*      */ 
/*      */ import com.dukascopy.api.connector.AbstractConnectorIndicatorImpl;
/*      */ import com.dukascopy.api.connector.IConnector;
/*      */ import com.dukascopy.api.connector.helpers.ColorHelpers;
/*      */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*      */ import com.dukascopy.api.indicators.IIndicatorContext;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.IndicatorResult;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo.Type;
/*      */ import com.dukascopy.api.indicators.IntegerListDescription;
/*      */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*      */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OptInputParameterInfo.Type;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*      */ import com.dukascopy.charts.data.datacache.nulls.NullIBar;
/*      */ import com.dukascopy.dds2.greed.connector.impl.JFToolBox;
/*      */ import java.awt.Color;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.ExecutorService;
/*      */ import java.util.concurrent.SynchronousQueue;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ 
/*      */ public class ConnectorIndicator extends AbstractConnectorIndicatorImpl
/*      */ {
/*      */   volatile boolean isCalculating;
/*      */   volatile boolean isUpdatingOutputs;
/*      */   ExecutorService executorService;
/*      */   String[] bufferLabels;
/*      */   Field[] indexBufferFields;
/*      */   int[] shifts;
/*      */   int indicator_buffers;
/*      */   int indocator_counter;
/*      */   int digits;
/*      */   private double scaleMin;
/*      */   private double scaleMax;
/*      */   private boolean recalculateScale;
/*      */   private boolean isInputParameter;
/*      */   private IndicatorInfo indicatorInfo;
/*      */   private InputParameterInfo[] inputParameterInfos;
/*      */   private OptInputParameterInfo[] optInputParameterInfos;
/*      */   private OutputParameterInfo[] outputParameterInfos;
/*      */   private IndicatorOutputStyle[] outputStyles;
/*      */   private IBar[][] inputs;
/*      */   private int currentBars;
/*      */   private int timePeriod;
/*      */   private double[][] outputs;
/*      */   public double instrPips;
/*      */   private ArrayList<ConnectorIndicatorLevel> indicatorLevels;
/*      */ 
/*      */   public ConnectorIndicator()
/*      */   {
/*   24 */     this.isCalculating = false;
/*   25 */     this.isUpdatingOutputs = false;
/*      */ 
/*   28 */     this.executorService = null;
/*      */ 
/*   41 */     this.indicator_buffers = 0;
/*   42 */     this.indocator_counter = 0;
/*   43 */     this.digits = 4;
/*   44 */     this.scaleMin = 0.0D;
/*   45 */     this.scaleMax = 0.0D;
/*   46 */     this.recalculateScale = true;
/*   47 */     this.isInputParameter = false;
/*      */ 
/*   54 */     this.inputs = new IBar[1][];
/*   55 */     this.currentBars = 0;
/*   56 */     this.timePeriod = 2;
/*   57 */     this.outputs = ((double[][])null);
/*      */ 
/*   59 */     this.instrPips = 0.0D;
/*      */ 
/*   62 */     this.indicatorLevels = new ArrayList();
/*      */   }
/*   64 */   public ArrayList<ConnectorIndicatorLevel> getLevels() { return this.indicatorLevels;
/*      */   }
/*      */ 
/*      */   protected IBar[] Bars(Number start, Number count)
/*      */     throws JFException
/*      */   {
/*   70 */     if (count.intValue() < 0) {
/*   71 */       throw new JFException("Bars count < 0");
/*      */     }
/*      */ 
/*   78 */     IBar[] bars = new IBar[count.intValue()];
/*   79 */     System.arraycopy(this.inputs[0], start.intValue(), bars, 0, count.intValue());
/*   80 */     return bars;
/*      */   }
/*      */ 
/*      */   protected IBar[] Bars() throws JFException
/*      */   {
/*   85 */     return Bars(Integer.valueOf(0), Integer.valueOf(this.inputs[0].length));
/*      */   }
/*      */ 
/*      */   protected IBar Bar(Number shift) throws JFException
/*      */   {
/*   90 */     IBar bar = null;
/*      */ 
/*   92 */     if (shift.intValue() < 0) {
/*   93 */       shift = Integer.valueOf(0);
/*      */     }
/*   95 */     if (shift.intValue() > this.inputs[0].length - 1) {
/*   96 */       shift = Integer.valueOf(this.inputs[0].length - 1);
/*      */     }
/*      */ 
/*   99 */     if ((this.inputs != null) && (this.inputs.length > 0) && (this.inputs[0] != null) && (shift.intValue() > -1) && (shift.intValue() < this.inputs[0].length))
/*  100 */       bar = this.inputs[0][shift.intValue()];
/*      */     else {
/*  102 */       bar = new NullIBar();
/*      */     }
/*      */ 
/*  105 */     return bar;
/*      */   }
/*      */ 
/*      */   protected IBar Bar() throws JFException {
/*  109 */     return this.inputs[0][0];
/*      */   }
/*      */ 
/*      */   protected double Open(Number shift) throws JFException
/*      */   {
/*  114 */     return Bar(shift).getOpen();
/*      */   }
/*      */ 
/*      */   protected double Close(Number shift) throws JFException
/*      */   {
/*  119 */     return Bar(shift).getClose();
/*      */   }
/*      */ 
/*      */   protected double High(Number shift) throws JFException {
/*  123 */     return Bar(shift).getHigh();
/*      */   }
/*      */ 
/*      */   protected double Low(Number shift) throws JFException
/*      */   {
/*  128 */     return Bar(shift).getLow();
/*      */   }
/*      */ 
/*      */   protected double Volume(Number shift) throws JFException {
/*  132 */     return Bar(shift).getVolume();
/*      */   }
/*      */ 
/*      */   protected long Time(Number shift) throws JFException
/*      */   {
/*  137 */     return Bar(shift).getTime();
/*      */   }
/*      */ 
/*      */   public int iHighest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  147 */     int rc = -1;
/*      */     try {
/*  149 */       IBar[] bars = Bars();
/*  150 */       double highValue = 4.9E-324D;
/*  151 */       for (int i = 0; i < count.intValue(); i++) {
/*  152 */         int index = i + start.intValue();
/*  153 */         if (index < 0) {
/*  154 */           index = 0;
/*      */         }
/*  156 */         if (index > bars.length - 1) {
/*  157 */           index = bars.length - 1;
/*  158 */           i = count.intValue();
/*      */         }
/*  160 */         IBar bar = bars[index];
/*  161 */         double value = 0.0D;
/*  162 */         switch (type.intValue()) {
/*      */         case 0:
/*  164 */           value = bar.getOpen();
/*  165 */           break;
/*      */         case 1:
/*  167 */           value = bar.getLow();
/*  168 */           break;
/*      */         case 2:
/*  170 */           value = bar.getHigh();
/*  171 */           break;
/*      */         case 3:
/*  173 */           value = bar.getClose();
/*  174 */           break;
/*      */         case 4:
/*  176 */           value = bar.getVolume();
/*  177 */           break;
/*      */         case 5:
/*  179 */           value = bar.getTime() / 1000L;
/*  180 */           break;
/*      */         default:
/*  182 */           value = bar.getOpen();
/*      */         }
/*      */ 
/*  185 */         if (value > highValue) {
/*  186 */           rc = index;
/*  187 */           highValue = value;
/*      */         }
/*      */       }
/*      */     } catch (JFException e) {
/*  191 */       throw e;
/*      */     }
/*  193 */     return rc;
/*      */   }
/*      */ 
/*      */   protected int iHighest(Object symbol, Number timeframe, Number type, Number count, Number start)
/*      */     throws JFException
/*      */   {
/*  199 */     return iHighest(symbol, timeframe, Integer.valueOf(type.intValue()), Integer.valueOf(count.intValue()), Integer.valueOf(start.intValue()), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected int Highest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  207 */     return iHighest(symbol, timeframe, Integer.valueOf(type.intValue()), Integer.valueOf(count.intValue()), Integer.valueOf(start.intValue()), offerSide);
/*      */   }
/*      */ 
/*      */   protected int Highest(Object symbol, Number timeframe, Number type, Number count, Number start)
/*      */     throws JFException
/*      */   {
/*  215 */     return iHighest(symbol, timeframe, Integer.valueOf(type.intValue()), Integer.valueOf(count.intValue()), Integer.valueOf(start.intValue()), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   public int iLowest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  228 */     int rc = -1;
/*      */     try {
/*  230 */       IBar[] bars = Bars();
/*      */ 
/*  233 */       double lowValue = 1.7976931348623157E+308D;
/*  234 */       for (int i = 0; i < count.intValue(); i++) {
/*  235 */         int index = i + start.intValue();
/*  236 */         if (index < 0) {
/*  237 */           index = 0;
/*      */         }
/*  239 */         if (index > bars.length - 1) {
/*  240 */           index = bars.length - 1;
/*  241 */           i = count.intValue();
/*      */         }
/*  243 */         IBar bar = bars[index];
/*      */ 
/*  245 */         double value = 0.0D;
/*  246 */         switch (type.intValue()) {
/*      */         case 0:
/*  248 */           value = bar.getOpen();
/*  249 */           break;
/*      */         case 1:
/*  251 */           value = bar.getLow();
/*  252 */           break;
/*      */         case 2:
/*  254 */           value = bar.getHigh();
/*  255 */           break;
/*      */         case 3:
/*  257 */           value = bar.getClose();
/*  258 */           break;
/*      */         case 4:
/*  260 */           value = bar.getVolume();
/*  261 */           break;
/*      */         case 5:
/*  263 */           value = bar.getTime() / 1000L;
/*  264 */           break;
/*      */         default:
/*  266 */           value = bar.getOpen();
/*      */         }
/*      */ 
/*  269 */         if (value < lowValue) {
/*  270 */           rc = index;
/*  271 */           lowValue = value;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/*  277 */       throw e;
/*      */     }
/*  279 */     return rc;
/*      */   }
/*      */ 
/*      */   protected int iLowest(Object symbol, Number timeframe, Number type, Number count, Number start) throws JFException
/*      */   {
/*  284 */     return iLowest(symbol, timeframe, Integer.valueOf(type.intValue()), Integer.valueOf(count.intValue()), Integer.valueOf(start.intValue()), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected int Lowest(Object symbol, Number timeframe, Number type, Number count, Number start)
/*      */     throws JFException
/*      */   {
/*  292 */     return iLowest(symbol, timeframe, Integer.valueOf(type.intValue()), Integer.valueOf(count.intValue()), Integer.valueOf(start.intValue()), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected int Lowest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  300 */     return iLowest(symbol, timeframe, Integer.valueOf(type.intValue()), Integer.valueOf(count.intValue()), Integer.valueOf(start.intValue()), offerSide);
/*      */   }
/*      */ 
/*      */   public int iBarShift(Object symbol, Number timeframe, Number time, Boolean exact)
/*      */     throws JFException
/*      */   {
/*  314 */     int indexer = 0;
/*      */     try {
/*  316 */       IBar bar = null;
/*      */       while (true) {
/*  318 */         bar = Bar(Integer.valueOf(indexer));
/*  319 */         if ((bar == null) || (bar.getTime() <= time.longValue() * 1000L))
/*      */           break;
/*  321 */         indexer++;
/*      */       }
/*  323 */       if ((bar == null) && (indexer > 0)) {
/*  324 */         indexer--;
/*  325 */         bar = Bar(Integer.valueOf(indexer));
/*      */       }
/*  327 */       if (bar == null)
/*  328 */         return -1;
/*  329 */       if (exact.booleanValue()) {
/*  330 */         if (bar.getTime() == time.longValue() * 1000L)
/*  331 */           return indexer;
/*  332 */         return -1;
/*      */       }
/*  334 */       return indexer;
/*      */     } catch (JFException e) {
/*      */     }
/*  337 */     throw e;
/*      */   }
/*      */ 
/*      */   protected int iBarShift(Object symbol, Number timeframe, Number time, Number exact)
/*      */     throws JFException
/*      */   {
/*  343 */     return iBarShift(symbol, timeframe, time, Boolean.valueOf(Bool(exact)));
/*      */   }
/*      */ 
/*      */   protected int iBarShift(Object symbol, Number timeframe, Number time)
/*      */     throws JFException
/*      */   {
/*  349 */     return iBarShift(symbol, timeframe, time, Boolean.valueOf(true));
/*      */   }
/*      */ 
/*      */   private Color getOutputParameterInfoColor(int index)
/*      */   {
/*      */     try {
/*  355 */       return ColorHelpers.colorFromString(this.properties.getProperty("indicator_color" + String.valueOf(index), "Blue"));
/*      */     }
/*      */     catch (JFException e) {
/*  358 */       e.printStackTrace();
/*      */     }
/*  360 */     return Blue;
/*      */   }
/*      */ 
/*      */   private OutputParameterInfo.DrawingStyle getOutputParameterInfoDrawingStyle(int index) {
/*  364 */     return OutputParameterInfo.DrawingStyle.LINE;
/*      */   }
/*      */ 
/*      */   public void onStart(IIndicatorContext context)
/*      */   {
/*  369 */     if (!this.isInitialized) {
/*      */       try {
/*  371 */         this.isInitialized = true;
/*  372 */         this.executorService = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MICROSECONDS, new SynchronousQueue(true), new ThreadPoolExecutor.CallerRunsPolicy());
/*      */ 
/*  377 */         setContext(context);
/*  378 */         setConnector(getConnectorInstance());
/*  379 */         getConnector().setIndicator(this);
/*      */ 
/*  381 */         initProperties();
/*  382 */         this.indicator_buffers = this.properties.getProperty("indicator_buffers", 1);
/*  383 */         if (this.indicator_buffers > 0) {
/*  384 */           IndicatorBuffers(this.indicator_buffers);
/*      */         }
/*  386 */         this.outputs = new double[this.indicator_buffers][];
/*  387 */         this.outputStyles = new IndicatorOutputStyle[this.indicator_buffers];
/*  388 */         this.digits = getCurrentInstrument().getPipScale();
/*  389 */         this.instrPips = getCurrentInstrument().getPipValue();
/*  390 */         this.Point = this.instrPips;
/*      */ 
/*  393 */         this.scaleMin = this.properties.getProperty("indicator_minimum", -1.0D);
/*  394 */         this.scaleMax = this.properties.getProperty("indicator_maximum", 1.0D);
/*      */ 
/*  396 */         if ((this.properties.getProperty("indicator_minimum") != null) && (this.properties.getProperty("indicator_maximum") != null)) {
/*  397 */           this.recalculateScale = false;
/*      */         }
/*      */ 
/*  400 */         init();
/*  401 */         OnInit();
/*      */ 
/*  403 */         super.onStart(context);
/*      */ 
/*  405 */         if (this.inputParameterInfos == null) {
/*  406 */           this.inputParameterInfos = addInputParameterInfo();
/*      */         }
/*      */ 
/*  409 */         if (this.optInputParameterInfos == null) {
/*  410 */           this.optInputParameterInfos = addOptInputParameters();
/*      */         }
/*      */ 
/*  413 */         this.outputParameterInfos = new OutputParameterInfo[this.indicator_buffers];
/*      */ 
/*  416 */         for (int i = 1; i <= this.indicator_buffers; i++) {
/*  417 */           int index = i;
/*      */ 
/*  419 */           IndicatorOutputStyle outputStyle = this.outputStyles.length > index - 1 ? this.outputStyles[(index - 1)] : null;
/*      */ 
/*  421 */           this.outputParameterInfos[(index - 1)] = new OutputParameterInfo(this.indicatorInfo.getName() + index, OutputParameterInfo.Type.DOUBLE, outputStyle != null ? this.outputStyles[(index - 1)].drawingType : OutputParameterInfo.DrawingStyle.LINE, index, outputStyle)
/*      */           {
/*      */           };
/*      */         }
/*      */ 
/*  431 */         while ((getConnector() == null) && (getConnector().getChartSelectedInstrument() == null)) {
/*      */           try {
/*  433 */             wait(500L);
/*      */           }
/*      */           catch (InterruptedException e) {
/*  436 */             e.printStackTrace();
/*      */           }
/*      */         }
/*  439 */         setBox(new JFToolBox(context, getConnector()));
/*      */       } catch (JFException e) {
/*  441 */         e.printStackTrace();
/*      */       }
/*  443 */       this.isInitialized = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void calculateImpl(int startIndex, int endIndex) {
/*  448 */     long start = System.currentTimeMillis();
/*  449 */     long end = System.currentTimeMillis();
/*      */     try {
/*  451 */       start = System.currentTimeMillis();
/*  452 */       synchronized (this) {
/*  453 */         getConnector().onInit(getContext());
/*  454 */         setStartIndex(startIndex < 0 ? 0 : startIndex);
/*  455 */         setEndIndex(endIndex);
/*  456 */         start();
/*  457 */         end = System.currentTimeMillis();
/*      */       }
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/*  462 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private IndicatorResult fillOutputImpl(int startIndex, int endIndex) {
/*  467 */     int i = 0;
/*  468 */     long start = System.currentTimeMillis();
/*      */ 
/*  472 */     int outputArrayLength = endIndex;
/*      */ 
/*  482 */     for (i = 0; i < this.indicator_buffers; i++) {
/*  483 */       if (this.indexBufferFields[i] != null) {
/*  484 */         Field field = this.indexBufferFields[i];
/*  485 */         double[] outputBuffer = null;
/*      */         try {
/*  487 */           if (field != null)
/*  488 */             outputBuffer = (double[])(double[])field.get(this);
/*      */         }
/*      */         catch (IllegalArgumentException e)
/*      */         {
/*  492 */           e.printStackTrace();
/*      */         }
/*      */         catch (IllegalAccessException e) {
/*  495 */           e.printStackTrace();
/*      */         }
/*      */ 
/*  502 */         int iz = 1; for (int j = startIndex + 1; j <= outputArrayLength; j++) {
/*  503 */           if ((outputBuffer == null) || (j >= outputArrayLength) || (iz >= this.outputs[i].length) || (outputBuffer[(j - 1)] == 0.0D) || (outputBuffer[(j - 1)] == 0.0D) || (outputBuffer[(j - 1)] == (0.0D / 0.0D)))
/*      */           {
/*      */             try
/*      */             {
/*  516 */               outputBuffer[(j - 1)] = (0.0D / 0.0D);
/*      */             }
/*      */             catch (Exception ex)
/*      */             {
/*      */             }
/*      */           }
/*  502 */           iz++;
/*      */         }
/*      */ 
/*  523 */         System.arraycopy(outputBuffer, 0, this.outputs[i], 0, this.outputs[i].length);
/*  524 */         if (i == 0) {
/*  525 */           endIndex = iz;
/*      */         }
/*      */       }
/*      */     }
/*  529 */     long end = System.currentTimeMillis();
/*      */ 
/*  536 */     end = System.currentTimeMillis();
/*      */ 
/*  538 */     return new IndicatorResult(startIndex - 1 > 0 ? startIndex - 1 : 0, endIndex);
/*      */   }
/*      */ 
/*      */   public IndicatorResult calculate_thread(int startIndex, int endIndex)
/*      */   {
/*  543 */     if (this.isCalculating) {
/*  544 */       this.executorService.shutdownNow();
/*  545 */       this.isCalculating = false;
/*      */     }
/*      */ 
/*  548 */     System.out.println("calculate start.");
/*  549 */     setStartIndex(startIndex);
/*  550 */     setEndIndex(endIndex);
/*      */     try
/*      */     {
/*  553 */       ITick tick = getConnector().getIHistory().getLastTick(getCurrentInstrument());
/*  554 */       setLastTickTime(tick.getTime() / 1000L);
/*      */     }
/*      */     catch (JFException e) {
/*  557 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  560 */     this.Bars = endIndex;
/*  561 */     this.digits = getCurrentInstrument().getPipScale();
/*  562 */     this.instrPips = getCurrentInstrument().getPipValue();
/*  563 */     this.Point = this.instrPips;
/*      */ 
/*  566 */     int j = 0;
/*  567 */     if ((isInitialized()) && (!isBufferFieldsEmpty()))
/*      */     {
/*  569 */       long st = System.currentTimeMillis();
/*  570 */       if (startIndex - getLookback() < 0) {
/*  571 */         startIndex -= startIndex - getLookback();
/*      */       }
/*  573 */       if (startIndex > endIndex) {
/*  574 */         return new IndicatorResult(0, 0);
/*      */       }
/*      */ 
/*  577 */       for (int i = 0; i < this.indexBufferFields.length; i++) {
/*      */         try {
/*  579 */           int arrayLength = Math.max(endIndex, 9501);
/*  580 */           if (this.indexBufferFields[i] != null)
/*  581 */             this.indexBufferFields[i].set(this, new double[arrayLength]);
/*      */         }
/*      */         catch (IllegalArgumentException e)
/*      */         {
/*  585 */           e.printStackTrace();
/*      */         }
/*      */         catch (IllegalAccessException e) {
/*  588 */           e.printStackTrace();
/*      */         }
/*      */       }
/*  591 */       System.out.println("Start calc thread.");
/*  592 */       IndicatorCalculation indicatorCalculation = new IndicatorCalculation();
/*  593 */       this.executorService.submit(indicatorCalculation);
/*  594 */       this.isCalculating = true;
/*      */ 
/*  596 */       while (this.isCalculating) {
/*      */         try {
/*  598 */           Thread.sleep(5L);
/*      */         }
/*      */         catch (InterruptedException e) {
/*  601 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*  605 */       System.out.println("End calc thread.");
/*  606 */       this.executorService.shutdownNow();
/*  607 */       startIndex = getStartIndex();
/*  608 */       endIndex = getEndIndex();
/*  609 */       IndicatorResult indicatorResult = fillOutputImpl(startIndex, endIndex);
/*      */ 
/*  611 */       return indicatorResult;
/*      */     }
/*      */ 
/*  614 */     int size = 0;
/*  615 */     for (int i = 0; i < this.indicator_buffers; i++) {
/*  616 */       size += getLookback() + getLookforward();
/*  617 */       this.outputs[i] = new double[endIndex + 1];
/*  618 */       Arrays.fill(this.outputs[i], (0.0D / 0.0D));
/*      */     }
/*      */ 
/*  621 */     startIndex = 1;
/*  622 */     endIndex++;
/*      */ 
/*  625 */     return new IndicatorResult(startIndex, endIndex);
/*      */   }
/*      */ 
/*      */   public IndicatorResult calculate(int startIndex, int endIndex)
/*      */   {
/*  633 */     setStartIndex(startIndex);
/*  634 */     setEndIndex(endIndex);
/*      */     try
/*      */     {
/*  639 */       ITick tick = getConnector().getIHistory().getLastTick(this.currentInstrument);
/*  640 */       setLastTickTime(tick.getTime() / 1000L);
/*      */     }
/*      */     catch (JFException e) {
/*  643 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  649 */     long start = System.currentTimeMillis();
/*  650 */     this.Bars = endIndex;
/*  651 */     this.digits = getCurrentInstrument().getPipScale();
/*  652 */     this.instrPips = getCurrentInstrument().getPipValue();
/*  653 */     this.Point = this.instrPips;
/*      */ 
/*  656 */     int j = 0;
/*  657 */     if ((isInitialized()) && (!isBufferFieldsEmpty()))
/*      */     {
/*  659 */       long st = System.currentTimeMillis();
/*  660 */       if (startIndex - getLookback() < 0) {
/*  661 */         startIndex -= startIndex - getLookback();
/*      */       }
/*  663 */       if (startIndex > endIndex) {
/*  664 */         return new IndicatorResult(0, 0);
/*      */       }
/*      */ 
/*  667 */       for (int i = 0; i < this.indexBufferFields.length; i++) {
/*      */         try {
/*  669 */           int arrayLength = Math.max(endIndex, 9501);
/*  670 */           if (this.indexBufferFields[i] != null)
/*  671 */             this.indexBufferFields[i].set(this, new double[arrayLength]);
/*      */         }
/*      */         catch (IllegalArgumentException e)
/*      */         {
/*  675 */           e.printStackTrace();
/*      */         }
/*      */         catch (IllegalAccessException e) {
/*  678 */           e.printStackTrace();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  683 */       calculateImpl(getStartIndex() < 0 ? 0 : getStartIndex(), getEndIndex());
/*      */ 
/*  686 */       IndicatorResult indicatorResult = fillOutputImpl(getStartIndex(), getEndIndex());
/*      */ 
/*  689 */       return indicatorResult;
/*      */     }
/*      */ 
/*  692 */     int size = 0;
/*  693 */     for (int i = 0; i < this.indicator_buffers; i++) {
/*  694 */       size += getLookback() + getLookforward();
/*  695 */       this.outputs[i] = new double[endIndex + 1];
/*  696 */       Arrays.fill(this.outputs[i], (0.0D / 0.0D));
/*      */     }
/*      */ 
/*  699 */     startIndex = 1;
/*  700 */     endIndex++;
/*      */ 
/*  703 */     return new IndicatorResult(startIndex < 0 ? 0 : startIndex, endIndex);
/*      */   }
/*      */ 
/*      */   public IndicatorInfo getIndicatorInfo() {
/*  707 */     return this.indicatorInfo;
/*      */   }
/*      */ 
/*      */   public InputParameterInfo getInputParameterInfo(int index) {
/*  711 */     if (index <= this.inputParameterInfos.length) {
/*  712 */       return this.inputParameterInfos[index];
/*      */     }
/*  714 */     return null;
/*      */   }
/*      */ 
/*      */   private boolean isOverChart()
/*      */   {
/*  725 */     boolean overChart = true;
/*      */ 
/*  727 */     if (this.properties.getProperty("indicator_chart_window") != null)
/*  728 */       overChart = this.properties.getProperty("indicator_chart_window", true);
/*  729 */     else if (this.properties.getProperty("indicator_separate_window") != null) {
/*  730 */       overChart = this.properties.getProperty("indicator_separate_window", true) != true;
/*      */     }
/*  732 */     return overChart;
/*      */   }
/*      */ 
/*      */   public int getLookback() {
/*  736 */     int max = 1;
/*      */ 
/*  760 */     return max;
/*      */   }
/*      */ 
/*      */   public int getLookforward() {
/*  764 */     return 0;
/*      */   }
/*      */ 
/*      */   public OptInputParameterInfo getOptInputParameterInfo(int index) {
/*  768 */     if (index <= this.optInputParameterInfos.length) {
/*  769 */       return this.optInputParameterInfos[index];
/*      */     }
/*  771 */     return null;
/*      */   }
/*      */ 
/*      */   public void setOptInputParameter(int index, Object value) {
/*  775 */     if (index < this.optInputParameterInfos.length) {
/*  776 */       Field field = getConfigurableByIndex(index);
/*      */       try {
/*  778 */         if (field.getType().getName().equalsIgnoreCase("boolean")) {
/*  779 */           Boolean b = new Boolean(value.toString());
/*  780 */           field.setBoolean(this, b.booleanValue());
/*  781 */         } else if (field.getType() != Color.class)
/*      */         {
/*  783 */           if (field.getType() == String.class)
/*  784 */             field.set(this, String.valueOf(value));
/*      */           else
/*  786 */             field.set(this, value);
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException e) {
/*  790 */         e.printStackTrace();
/*      */       }
/*      */       catch (IllegalAccessException e) {
/*  793 */         e.printStackTrace();
/*      */       }
/*      */     } else {
/*  796 */       setInitialized(true);
/*  797 */       throw new ArrayIndexOutOfBoundsException(index);
/*      */     }
/*      */   }
/*      */ 
/*      */   public OutputParameterInfo getOutputParameterInfo(int index) {
/*  802 */     if ((this.outputParameterInfos != null) && (index < this.outputParameterInfos.length)) {
/*  803 */       return this.outputParameterInfos[index];
/*      */     }
/*  805 */     return null;
/*      */   }
/*      */ 
/*      */   public void setInputParameter(int index, Object array) {
/*  809 */     this.inputs[index] = ((IBar[])(IBar[])array);
/*  810 */     this.isInputParameter = true;
/*      */   }
/*      */ 
/*      */   public Object getInputParameter(int index)
/*      */   {
/*  815 */     if ((this.inputs != null) && (index < this.inputs.length)) {
/*  816 */       return this.inputs[index];
/*      */     }
/*  818 */     return null;
/*      */   }
/*      */ 
/*      */   public void setOutputParameter(int index, Object array) {
/*  822 */     this.outputs[index] = ((double[])(double[])array);
/*      */   }
/*      */ 
/*      */   protected void initProperties()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected int init()
/*      */     throws JFException
/*      */   {
/*  832 */     return 0;
/*      */   }
/*      */ 
/*      */   private Field getConfigurableByIndex(int index) {
/*  836 */     Field returnField = null;
/*  837 */     Field[] fields = getClass().getFields();
/*  838 */     int i = 0;
/*  839 */     for (Field field : fields) {
/*  840 */       Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*  841 */       if (configurable != null)
/*  842 */         if (i < index) {
/*  843 */           i++;
/*      */         }
/*      */         else {
/*  846 */           returnField = field;
/*  847 */           break;
/*      */         }
/*      */     }
/*  850 */     return returnField;
/*      */   }
/*      */ 
/*      */   protected Field getIndicatorBuffers(Object object)
/*      */   {
/*  855 */     Field[] fields = getClass().getFields();
/*  856 */     Field resultField = null;
/*  857 */     IndicatorBuffer indicatorBuffer = null;
/*  858 */     for (Field field : fields) {
/*  859 */       indicatorBuffer = (IndicatorBuffer)field.getAnnotation(IndicatorBuffer.class);
/*  860 */       if (indicatorBuffer != null) {
/*  861 */         Object fieldValue = null;
/*      */         try {
/*  863 */           fieldValue = field.get(this);
/*  864 */           if (fieldValue == object) {
/*  865 */             resultField = field;
/*  866 */             break;
/*      */           }
/*      */         }
/*      */         catch (IllegalArgumentException e) {
/*      */         }
/*      */         catch (IllegalAccessException e) {
/*      */         }
/*      */       }
/*      */     }
/*  875 */     return resultField;
/*      */   }
/*      */ 
/*      */   protected Field getIndicatorBuffers(Object object, int index)
/*      */   {
/*  880 */     Field[] fields = getClass().getFields();
/*  881 */     Field resultField = null;
/*  882 */     IndicatorBuffer indicatorBuffer = null;
/*  883 */     int i = 0;
/*  884 */     for (Field field : fields) {
/*  885 */       indicatorBuffer = (IndicatorBuffer)field.getAnnotation(IndicatorBuffer.class);
/*  886 */       if ((indicatorBuffer != null) && 
/*  887 */         (i == index)) {
/*  888 */         resultField = field;
/*  889 */         break;
/*      */       }
/*      */ 
/*  892 */       i++;
/*      */     }
/*  894 */     return resultField;
/*      */   }
/*      */ 
/*      */   protected Field getBufferFieldByName(String name)
/*      */   {
/*  899 */     Field[] fields = getClass().getFields();
/*  900 */     Field resultField = null;
/*  901 */     IndicatorBuffer indicatorBuffer = null;
/*  902 */     for (Field field : fields) {
/*  903 */       indicatorBuffer = (IndicatorBuffer)field.getAnnotation(IndicatorBuffer.class);
/*  904 */       if ((indicatorBuffer == null) || 
/*  905 */         (!field.getName().equals(name))) continue;
/*  906 */       resultField = field;
/*  907 */       break;
/*      */     }
/*      */ 
/*  911 */     return resultField;
/*      */   }
/*      */ 
/*      */   protected InputParameterInfo[] addInputParameterInfo() {
/*  915 */     InputParameterInfo[] result = new InputParameterInfo[1];
/*  916 */     InputParameterInfo item = new InputParameterInfo("Input data", InputParameterInfo.Type.BAR);
/*  917 */     result[0] = item;
/*  918 */     return result;
/*      */   }
/*      */ 
/*      */   protected OptInputParameterInfo[] addOptInputParameters() throws JFException
/*      */   {
/*  923 */     List optList = new ArrayList();
/*  924 */     Field[] fields = getClass().getFields();
/*  925 */     for (Field field : fields) {
/*  926 */       Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*  927 */       if (configurable != null) {
/*  928 */         Object fieldValue = null;
/*      */         try {
/*  930 */           fieldValue = field.get(this);
/*      */         } catch (IllegalArgumentException e) {
/*  932 */           throw new JFException(e);
/*      */         } catch (IllegalAccessException e) {
/*  934 */           throw new JFException(e);
/*      */         }
/*  936 */         OptInputParameterInfo data = null;
/*      */ 
/*  938 */         if ((fieldValue instanceof Integer)) {
/*  939 */           Integer value = null;
/*  940 */           value = (Integer)fieldValue;
/*  941 */           int min = -(Math.abs(value.intValue()) + 100);
/*  942 */           int max = Math.abs(value.intValue()) + 200;
/*  943 */           data = new OptInputParameterInfo(field.getName(), OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(value.intValue(), min, max, 1));
/*      */         }
/*  946 */         else if ((fieldValue instanceof Boolean)) {
/*  947 */           Boolean booleanValue = (Boolean)fieldValue;
/*  948 */           int defaultValue = booleanValue.booleanValue() == true ? 0 : 1;
/*  949 */           data = new OptInputParameterInfo(field.getName(), OptInputParameterInfo.Type.OTHER, new IntegerListDescription(defaultValue, new int[] { 0, 1 }, new String[] { "true", "false" }));
/*      */         }
/*      */         else
/*      */         {
/*      */           Color colorValue;
/*  950 */           if ((fieldValue instanceof Color)) {
/*  951 */             colorValue = (Color)fieldValue;
/*      */           }
/*  953 */           else if ((fieldValue instanceof Double)) {
/*  954 */             Double value = (Double)fieldValue;
/*  955 */             int min = -(Math.abs(value.intValue()) + 100);
/*  956 */             int max = Math.abs(value.intValue()) + 200;
/*  957 */             data = new OptInputParameterInfo(field.getName(), OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(value.doubleValue(), min, max, 0.01D, 1));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  962 */         if (data != null) {
/*  963 */           optList.add(data);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  968 */     if (this.indicatorInfo == null) {
/*  969 */       int numberOfOutputs = 0;
/*  970 */       int numberOfInputs = 0;
/*  971 */       int numberOfOptionalInputs = 0;
/*  972 */       if (this.indexBufferFields != null) {
/*  973 */         numberOfOutputs = this.indexBufferFields.length;
/*      */       }
/*      */ 
/*  976 */       if (this.inputParameterInfos != null) {
/*  977 */         numberOfInputs = this.inputParameterInfos.length;
/*      */       } else {
/*  979 */         this.inputParameterInfos = addInputParameterInfo();
/*  980 */         numberOfInputs = this.inputParameterInfos.length;
/*      */       }
/*      */ 
/*  983 */       if (this.optInputParameterInfos != null) {
/*  984 */         numberOfOptionalInputs = this.optInputParameterInfos.length;
/*      */       }
/*      */ 
/*  987 */       String indicatorName = getClass().getSimpleName();
/*  988 */       boolean overChart = isOverChart();
/*      */ 
/*  990 */       this.indicatorInfo = new IndicatorInfo(indicatorName, indicatorName + " MT4 Custom indicator", getDefaultRecognitionGroup(), overChart, false, true, numberOfInputs, numberOfOptionalInputs, numberOfOutputs)
/*      */       {
/*      */       };
/*      */     }
/*      */ 
/*  997 */     this.indicatorInfo.setNumberOfOptionalInputs(optList.size());
/*  998 */     return (OptInputParameterInfo[])optList.toArray(new OptInputParameterInfo[optList.size()]);
/*      */   }
/*      */ 
/*      */   protected int deinit() throws JFException {
/* 1002 */     return 0;
/*      */   }
/*      */ 
/*      */   protected int OnInit() {
/* 1006 */     this.instrPips = getCurrentInstrument().getPipValue();
/* 1007 */     this.Point = this.instrPips;
/* 1008 */     return 0;
/*      */   }
/*      */ 
/*      */   protected int OnDeinit() {
/* 1012 */     return 0;
/*      */   }
/*      */ 
/*      */   protected int OnStart() {
/* 1016 */     return 0;
/*      */   }
/*      */   protected int start() throws JFException {
/* 1019 */     return 0;
/*      */   }
/*      */ 
/*      */   public void IndicatorBuffers(int count)
/*      */   {
/* 1037 */     if ((count < 0) && (count > 7)) {
/* 1038 */       throw new RuntimeException("This function not allowed");
/*      */     }
/*      */ 
/* 1041 */     this.indexBufferFields = new Field[count];
/* 1042 */     this.bufferLabels = new String[count];
/* 1043 */     this.shifts = new int[count];
/* 1044 */     this.indicator_buffers = count;
/* 1045 */     this.outputs = new double[this.indicator_buffers][];
/*      */   }
/*      */ 
/*      */   public int IndicatorCounted()
/*      */   {
/* 1060 */     return 1;
/*      */   }
/*      */ 
/*      */   public void IndicatorDigits(int digits)
/*      */   {
/* 1072 */     this.digits = digits;
/*      */   }
/*      */ 
/*      */   public void IndicatorDigits(double digits)
/*      */   {
/* 1085 */     this.digits = (int)digits;
/*      */   }
/*      */ 
/*      */   public void IndicatorShortName(String name) {
/* 1089 */     if (this.indicatorInfo == null) {
/* 1090 */       int numberOfOutputs = 0;
/* 1091 */       int numberOfInputs = 0;
/* 1092 */       int numberOfOptionalInputs = 0;
/* 1093 */       if (this.indexBufferFields != null) {
/* 1094 */         numberOfOutputs = this.indexBufferFields.length;
/*      */       }
/* 1096 */       if (this.inputParameterInfos != null) {
/* 1097 */         numberOfInputs = this.inputParameterInfos.length;
/*      */       } else {
/* 1099 */         this.inputParameterInfos = addInputParameterInfo();
/* 1100 */         numberOfInputs = this.inputParameterInfos.length;
/*      */       }
/* 1102 */       if (this.optInputParameterInfos != null) {
/* 1103 */         numberOfOptionalInputs = this.optInputParameterInfos.length;
/*      */       }
/*      */ 
/* 1106 */       boolean overChart = isOverChart();
/*      */ 
/* 1108 */       this.indicatorInfo = new IndicatorInfo(name, name + " MT4 Custom indicator", getDefaultRecognitionGroup(), overChart, false, true, numberOfInputs, numberOfOptionalInputs, numberOfOutputs)
/*      */       {
/*      */       };
/*      */     }
/*      */     else
/*      */     {
/* 1117 */       this.indicatorInfo.setName(name);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SetIndexArrow(int index, int code)
/*      */   {
/* 1130 */     if ((index < 0) && (index > 7))
/* 1131 */       throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean SetIndexBuffer(int index, double[] array)
/*      */   {
/* 1150 */     boolean result = false;
/* 1151 */     if ((index < 0) && (index > 7)) {
/* 1152 */       throw new RuntimeException("This function not allowed");
/*      */     }
/*      */ 
/* 1155 */     Field field = getIndicatorBuffers(array);
/* 1156 */     if (field == null) {
/* 1157 */       throw new RuntimeException("Field not found.");
/*      */     }
/* 1159 */     if ((this.indexBufferFields != null) && (index > -1) && (index < this.indexBufferFields.length)) {
/* 1160 */       this.indexBufferFields[index] = field;
/*      */ 
/* 1162 */       result = true;
/*      */     }
/* 1164 */     return result;
/*      */   }
/*      */ 
/*      */   public void SetIndexDrawBegin(Number index, Number begin)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void SetIndexDrawBegin(Number index, double[] s3Buffer)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void SetIndexEmptyValue(int index, double value)
/*      */   {
/* 1212 */     if ((index < 0) && (index > 7)) {
/* 1213 */       throw new RuntimeException("This function not allowed");
/*      */     }
/* 1215 */     double[] array = (double[])this.outputs[index];
/* 1216 */     if (array != null)
/*      */     {
/* 1220 */       Arrays.fill(array, value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void SetIndexLabel(int index, String text)
/*      */   {
/* 1236 */     if ((index < 0) && (index > 7)) {
/* 1237 */       throw new RuntimeException("This function not allowed");
/*      */     }
/* 1239 */     this.bufferLabels[index] = text;
/*      */   }
/*      */ 
/*      */   public void SetIndexShift(int index, int shift)
/*      */   {
/* 1254 */     if ((index < 0) && (index > 7)) {
/* 1255 */       throw new RuntimeException("This function not allowed");
/*      */     }
/* 1257 */     this.shifts[index] = shift;
/*      */   }
/*      */ 
/*      */   protected void SetIndexStyle(Number index, Number type, Number style, Number width, Color clr)
/*      */     throws JFException
/*      */   {
/* 1282 */     SetIndexStyle(Integer.valueOf(index.intValue()), Integer.valueOf(type.intValue()), Integer.valueOf(style.intValue()), Integer.valueOf(width.intValue()), Integer.valueOf(-1));
/*      */   }
/*      */   protected void SetIndexStyle(Number index, Number type, Number style, Number width, Number clr) throws JFException {
/* 1285 */     if ((index.intValue() < 0) && (index.intValue() > 7)) {
/* 1286 */       throw new RuntimeException("This function not allowed");
/*      */     }
/*      */ 
/* 1289 */     if (this.outputParameterInfos == null)
/*      */     {
/* 1291 */       this.outputParameterInfos = new OutputParameterInfo[this.indicator_buffers];
/*      */     }
/*      */ 
/* 1295 */     this.outputStyles[index.intValue()] = new IndicatorOutputStyle(type, style, width, null);
/*      */   }
/*      */ 
/*      */   protected void SetIndexStyle(Number index, Number type, Number style, Number width) throws JFException {
/* 1299 */     SetIndexStyle(Integer.valueOf(index.intValue()), Integer.valueOf(type.intValue()), Integer.valueOf(style.intValue()), Integer.valueOf(width.intValue()), Integer.valueOf(-1));
/*      */   }
/*      */   protected void SetIndexStyle(Number index, Number type, Number style) throws JFException {
/* 1302 */     SetIndexStyle(Integer.valueOf(index.intValue()), Integer.valueOf(type.intValue()), Integer.valueOf(style.intValue()), Integer.valueOf(-1), Integer.valueOf(-1));
/*      */   }
/*      */   public void SetIndexStyle(Number index, Boolean type) throws JFException {
/* 1305 */     SetIndexStyle(Integer.valueOf(index.intValue()), Integer.valueOf(toInt(type)), Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
/*      */   }
/*      */ 
/*      */   public void SetIndexStyle(Number index, Number type) throws JFException {
/* 1309 */     SetIndexStyle(Integer.valueOf(index.intValue()), Integer.valueOf(type.intValue()), Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
/*      */   }
/*      */ 
/*      */   public void SetLevelStyle(int draw_style, int line_width, long clr)
/*      */   {
/* 1328 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public void SetLevelValue(int level, double value)
/*      */   {
/* 1341 */     this.indicatorLevels.add(new ConnectorIndicatorLevel(level, value));
/*      */   }
/*      */ 
/*      */   protected String getDefaultRecognitionGroup()
/*      */   {
/* 1349 */     return "Connector Strategies";
/*      */   }
/*      */ 
/*      */   protected int getNumberOfOutputs() {
/* 1353 */     return getNumberOfOutputsForOneSubindicator();
/*      */   }
/*      */ 
/*      */   protected int getNumberOfOutputsForOneSubindicator() {
/* 1357 */     if (this.indicator_buffers < 1) {
/* 1358 */       this.indicator_buffers = this.properties.getProperty("indicator_buffers", 1);
/*      */     }
/* 1360 */     return this.indicator_buffers;
/*      */   }
/*      */ 
/*      */   protected OptInputParameterInfo[] getOptInputParameterInfos() {
/* 1364 */     return this.optInputParameterInfos;
/*      */   }
/*      */ 
/*      */   public Instrument getCurrentInstrument()
/*      */   {
/* 1388 */     synchronized (this.currentInstrument) {
/* 1389 */       return this.currentInstrument;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCurrentInstrument(Instrument currentInstrument) {
/* 1394 */     synchronized (this.currentInstrument) {
/* 1395 */       this.currentInstrument = currentInstrument;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isBufferFieldsEmpty() {
/* 1400 */     boolean result = true;
/* 1401 */     if ((this.indexBufferFields != null) && (this.indexBufferFields.length > 0)) {
/* 1402 */       for (int i = 0; i < this.indexBufferFields.length; i++) {
/* 1403 */         if (this.indexBufferFields[i] == null) {
/* 1404 */           result = false;
/* 1405 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1409 */     return !result;
/*      */   }
/*      */ 
/*      */   class IndicatorOutputStyle
/*      */   {
/*      */     public final OutputParameterInfo.DrawingStyle drawingType;
/*      */     public final int width;
/*      */     public final Color color;
/*      */ 
/*      */     public IndicatorOutputStyle(Number index, Number type, Number style, Number width, Color clr)
/*      */     {
/* 1529 */       if (type.intValue() == 2) {
/* 1530 */         this.drawingType = OutputParameterInfo.DrawingStyle.HISTOGRAM;
/*      */       }
/* 1532 */       else if (type.intValue() == 3) {
/* 1533 */         this.drawingType = OutputParameterInfo.DrawingStyle.DOTS;
/*      */       }
/*      */       else {
/* 1536 */         switch (style.intValue()) { case 0:
/* 1537 */           this.drawingType = OutputParameterInfo.DrawingStyle.LINE; break;
/*      */         case 1:
/* 1538 */           this.drawingType = OutputParameterInfo.DrawingStyle.DASH_LINE; break;
/*      */         case 2:
/* 1539 */           this.drawingType = OutputParameterInfo.DrawingStyle.DOT_LINE; break;
/*      */         case 3:
/* 1540 */           this.drawingType = OutputParameterInfo.DrawingStyle.DASHDOT_LINE; break;
/*      */         case 4:
/* 1541 */           this.drawingType = OutputParameterInfo.DrawingStyle.DASHDOTDOT_LINE; break;
/*      */         default:
/* 1542 */           this.drawingType = OutputParameterInfo.DrawingStyle.LINE;
/*      */         }
/*      */       }
/*      */ 
/* 1546 */       this.width = width.intValue();
/* 1547 */       this.color = clr;
/*      */     }
/*      */ 
/*      */     public IndicatorOutputStyle(Number type, Number style, Number width, Color clr)
/*      */     {
/* 1552 */       this(Integer.valueOf(0), type, style, width, clr);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class ConnectorIndicatorLevel
/*      */   {
/*      */     private int index;
/*      */     private double value;
/*      */ 
/*      */     public int getIndex()
/*      */     {
/* 1498 */       return this.index;
/*      */     }
/*      */     public void setIndex(int index) {
/* 1501 */       this.index = index;
/*      */     }
/*      */     public double getValue() {
/* 1504 */       return this.value;
/*      */     }
/*      */     public void setValue(double value) {
/* 1507 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public ConnectorIndicatorLevel(int index, double value)
/*      */     {
/* 1512 */       this.index = index;
/* 1513 */       this.value = value;
/*      */     }
/*      */   }
/*      */ 
/*      */   class IndicatorCalculation
/*      */     implements Runnable
/*      */   {
/*      */     IndicatorCalculation()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1433 */       ConnectorIndicator.this.calculateImpl(ConnectorIndicator.this.getStartIndex() < 0 ? 0 : ConnectorIndicator.this.getStartIndex(), ConnectorIndicator.this.getEndIndex());
/* 1434 */       ConnectorIndicator.this.isCalculating = false;
/*      */     }
/*      */ 
/*      */     public void run1()
/*      */     {
/*      */       try {
/* 1440 */         ITick tick = ConnectorIndicator.this.getConnector().getIHistory().getLastTick(ConnectorIndicator.this.getCurrentInstrument());
/* 1441 */         ConnectorIndicator.this.setLastTickTime(tick.getTime() / 1000L);
/*      */       }
/*      */       catch (JFException e) {
/* 1444 */         e.printStackTrace();
/*      */       }
/*      */ 
/* 1449 */       long start = System.currentTimeMillis();
/* 1450 */       ConnectorIndicator.access$202(ConnectorIndicator.this, ConnectorIndicator.this.getEndIndex());
/* 1451 */       ConnectorIndicator.this.digits = ConnectorIndicator.this.getCurrentInstrument().getPipScale();
/* 1452 */       ConnectorIndicator.this.instrPips = ConnectorIndicator.this.getCurrentInstrument().getPipValue();
/* 1453 */       ConnectorIndicator.access$302(ConnectorIndicator.this, ConnectorIndicator.this.instrPips);
/* 1454 */       int j = 0;
/* 1455 */       if ((ConnectorIndicator.this.isInitialized()) && (!ConnectorIndicator.this.isBufferFieldsEmpty()))
/*      */       {
/* 1457 */         long st = System.currentTimeMillis();
/* 1458 */         if (ConnectorIndicator.this.getStartIndex() - ConnectorIndicator.this.getLookback() < 0) {
/* 1459 */           ConnectorIndicator.this.setStartIndex(ConnectorIndicator.this.getStartIndex() - (ConnectorIndicator.this.getStartIndex() - ConnectorIndicator.this.getLookback()));
/*      */         }
/* 1461 */         if (ConnectorIndicator.this.getStartIndex() > ConnectorIndicator.this.getEndIndex())
/*      */         {
/* 1463 */           ConnectorIndicator.this.setStartIndex(0);
/* 1464 */           ConnectorIndicator.this.setEndIndex(0);
/*      */         }
/*      */         else {
/* 1467 */           for (int i = 0; i < ConnectorIndicator.this.indexBufferFields.length; i++) {
/*      */             try {
/* 1469 */               int arrayLength = Math.max(ConnectorIndicator.this.getEndIndex(), 9501);
/* 1470 */               if (ConnectorIndicator.this.indexBufferFields[i] != null)
/* 1471 */                 ConnectorIndicator.this.indexBufferFields[i].set(this, new double[arrayLength]);
/*      */             }
/*      */             catch (IllegalArgumentException e)
/*      */             {
/* 1475 */               e.printStackTrace();
/*      */             }
/*      */             catch (IllegalAccessException e) {
/* 1478 */               e.printStackTrace();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1483 */           ConnectorIndicator.this.calculateImpl(ConnectorIndicator.this.getStartIndex() < 0 ? 0 : ConnectorIndicator.this.getStartIndex(), ConnectorIndicator.this.getEndIndex());
/* 1484 */           IndicatorResult indicatorResult = ConnectorIndicator.this.fillOutputImpl(ConnectorIndicator.this.getStartIndex(), ConnectorIndicator.this.getEndIndex());
/*      */ 
/* 1486 */           ConnectorIndicator.this.setStartIndex(indicatorResult.getFirstValueIndex());
/* 1487 */           ConnectorIndicator.this.setEndIndex(indicatorResult.getLastValueIndex());
/*      */         }
/*      */       }
/* 1490 */       ConnectorIndicator.this.isCalculating = false;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.ConnectorIndicator
 * JD-Core Version:    0.6.0
 */