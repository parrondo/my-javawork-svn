/*      */ package com.dukascopy.api.impl;
/*      */ 
/*      */ import com.dukascopy.api.ConnectorIndicator;
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.IBar;
/*      */ import com.dukascopy.api.IIndicators;
/*      */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*      */ import com.dukascopy.api.IIndicators.MaType;
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.feed.FeedDescriptor;
/*      */ import com.dukascopy.api.feed.IFeedDescriptor;
/*      */ import com.dukascopy.api.feed.IPointAndFigure;
/*      */ import com.dukascopy.api.feed.IRangeBar;
/*      */ import com.dukascopy.api.feed.IRenkoBar;
/*      */ import com.dukascopy.api.feed.ITickBar;
/*      */ import com.dukascopy.api.impl.util.ComponentDownloader;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.IndicatorResult;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo.Type;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*      */ import com.dukascopy.charts.data.datacache.CandleData;
/*      */ import com.dukascopy.charts.data.datacache.Data;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.IntraPeriodCandleData;
/*      */ import com.dukascopy.charts.data.datacache.IntraperiodCandlesGenerator;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.IndicatorData;
/*      */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*      */ import com.dukascopy.charts.math.dataprovider.IDataSequence;
/*      */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.IndicatorHelper;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class Indicators
/*      */   implements IIndicators
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   private static final int UNSTABLE_PERIOD_LOOKBACK = 100;
/*      */   protected History history;
/*   67 */   protected final Map<String, IndicatorHolder> cachedIndicators = Collections.synchronizedMap(new LinkedHashMap(50, 0.75F, true) {
/*      */     protected boolean removeEldestEntry(Map.Entry<String, IndicatorHolder> eldest) {
/*   69 */       return size() > 15;
/*      */     }
/*      */   });
/*      */ 
/*   73 */   private final Map<String, Class<? extends IIndicator>> customIndicators = Collections.synchronizedMap(new LinkedHashMap());
/*      */ 
/* 5960 */   protected Period dailyFilterPeriod = Period.DAILY;
/*      */ 
/*      */   public Indicators(History history)
/*      */   {
/*   76 */     this.history = history;
/*      */   }
/*      */ 
/*      */   public Collection<String> getAllNames() {
/*   80 */     Collection result = new LinkedHashSet();
/*   81 */     if (!this.customIndicators.isEmpty()) {
/*   82 */       result.addAll(this.customIndicators.keySet());
/*      */     }
/*   84 */     result.addAll(IndicatorsProvider.getInstance().getAllNames());
/*   85 */     return result;
/*      */   }
/*      */ 
/*      */   public Collection<String> getGroups() {
/*   89 */     Collection result = new LinkedHashSet();
/*   90 */     if (!this.customIndicators.isEmpty()) {
/*   91 */       for (String indicatorName : this.customIndicators.keySet()) {
/*      */         try {
/*   93 */           IndicatorHolder cachedIndicator = getCachedIndicator(indicatorName);
/*   94 */           result.add(cachedIndicator.getIndicator().getIndicatorInfo().getName());
/*   95 */           cacheIndicator(indicatorName, cachedIndicator);
/*      */         } catch (Exception ex) {
/*   97 */           LOGGER.error("Error while getting cached indicator : " + indicatorName, ex);
/*      */         }
/*      */       }
/*      */     }
/*  101 */     result.addAll(IndicatorsProvider.getInstance().getGroups());
/*  102 */     return result;
/*      */   }
/*      */ 
/*      */   public IIndicator getIndicator(String name) {
/*      */     try {
/*  107 */       IIndicator indicator = getCustomIndicator(name);
/*  108 */       if (indicator != null)
/*  109 */         return indicator;
/*      */     }
/*      */     catch (Exception ex) {
/*  112 */       LOGGER.error("Error while getting custom indicator : " + name, ex);
/*      */     }
/*  114 */     IndicatorHolder holder = IndicatorsProvider.getInstance().getIndicatorHolder(name);
/*  115 */     if (holder != null) {
/*  116 */       IndicatorsProvider.getInstance().saveIndicatorHolder(holder);
/*  117 */       return holder.getIndicator();
/*      */     }
/*      */ 
/*  120 */     return null;
/*      */   }
/*      */ 
/*      */   public Collection<String> getNames(String groupName)
/*      */   {
/*  125 */     Collection result = new LinkedHashSet();
/*  126 */     if (!this.customIndicators.isEmpty()) {
/*  127 */       for (String indicatorName : this.customIndicators.keySet()) {
/*      */         try {
/*  129 */           IndicatorHolder cachedIndicator = getCachedIndicator(indicatorName);
/*  130 */           IndicatorInfo indicatorInfo = cachedIndicator.getIndicator().getIndicatorInfo();
/*  131 */           if (indicatorInfo.getGroupName().equalsIgnoreCase(groupName)) {
/*  132 */             result.add(indicatorInfo.getName());
/*      */           }
/*  134 */           cacheIndicator(indicatorName, cachedIndicator);
/*      */         } catch (Exception ex) {
/*  136 */           LOGGER.error("Error while getting cached indicator : " + indicatorName, ex);
/*      */         }
/*      */       }
/*      */     }
/*  140 */     return IndicatorsProvider.getInstance().getNames(groupName);
/*      */   }
/*      */ 
/*      */   public double acos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/*  144 */     return ((Double)function(instrument, period, side, "ACOS", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] acos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/*  148 */     return (double[])(double[])function(instrument, period, side, "ACOS", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] acos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  152 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ACOS", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] acos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/*  156 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ACOS", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ac(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, int shift) throws JFException {
/*  160 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, shift);
/*  161 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] ac(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, long from, long to) throws JFException {
/*  165 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, from, to);
/*  166 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] ac(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  170 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  171 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] ac(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, Filter filter, long from, long to) throws JFException {
/*  175 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, filter, from, to);
/*  176 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double ad(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  180 */     return ((Double)function(instrument, period, side, "AD", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ad(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  184 */     return (double[])(double[])function(instrument, period, side, "AD", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ad(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  188 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "AD", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ad(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  192 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "AD", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double add(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int shift) throws JFException {
/*  196 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "ADD", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] add(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, long from, long to) throws JFException {
/*  200 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "ADD", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] add(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  204 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "ADD", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] add(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, long from, long to) throws JFException {
/*  208 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "ADD", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double adOsc(Instrument instrument, Period period, OfferSide side, int fastPeriod, int slowPeriod, int shift) throws JFException {
/*  212 */     return ((Double)function(instrument, period, side, "ADOSC", null, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] adOsc(Instrument instrument, Period period, OfferSide side, int fastPeriod, int slowPeriod, long from, long to) throws JFException {
/*  216 */     return (double[])(double[])function(instrument, period, side, "ADOSC", null, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] adOsc(Instrument instrument, Period period, OfferSide side, int fastPeriod, int slowPeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  220 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ADOSC", null, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] adOsc(Instrument instrument, Period period, OfferSide side, int fastPeriod, int slowPeriod, Filter filter, long from, long to) throws JFException {
/*  224 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ADOSC", null, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double adx(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  228 */     return ((Double)function(instrument, period, side, "ADX", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] adx(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  232 */     return (double[])(double[])function(instrument, period, side, "ADX", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] adx(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  236 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ADX", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] adx(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  240 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ADX", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double adxr(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  244 */     return ((Double)function(instrument, period, side, "ADXR", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] adxr(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  248 */     return (double[])(double[])function(instrument, period, side, "ADXR", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] adxr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  252 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ADXR", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] adxr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  256 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ADX", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] alligator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, int shift) throws JFException {
/*  260 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ALLIGATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, shift);
/*  261 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] alligator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, Filter filter, long from, long to) throws JFException {
/*  265 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ALLIGATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, filter, from, to);
/*  266 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] alligator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  270 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ALLIGATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  271 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] alligator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, long from, long to) throws JFException {
/*  275 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ALLIGATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, from, to);
/*  276 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double apo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, int shift) throws JFException {
/*  280 */     return ((Double)function(instrument, period, side, "APO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] apo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/*  284 */     return (double[])(double[])function(instrument, period, side, "APO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] apo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  288 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "APO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] apo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/*  292 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "APO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] aroon(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  296 */     Object[] ret = function(instrument, period, side, "AROON", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/*  297 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] aroon(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  301 */     Object[] ret = function(instrument, period, side, "AROON", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/*  302 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] aroon(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  306 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AROON", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  307 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] aroon(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  311 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AROON", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/*  312 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double aroonOsc(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  316 */     return ((Double)function(instrument, period, side, "AROONOSC", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] aroonOsc(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  320 */     return (double[])(double[])function(instrument, period, side, "AROONOSC", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] aroonOsc(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  324 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "AROONOSC", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] aroonOsc(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  328 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "AROONOSC", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double asin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/*  332 */     return ((Double)function(instrument, period, side, "ASIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] asin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/*  336 */     return (double[])(double[])function(instrument, period, side, "ASIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] asin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  340 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ASIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] asin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/*  344 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ASIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double atan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/*  348 */     return ((Double)function(instrument, period, side, "ATAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] atan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/*  352 */     return (double[])(double[])function(instrument, period, side, "ATAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] atan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  356 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ATAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] atan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/*  360 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ATAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double atr(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  364 */     return ((Double)function(instrument, period, side, "ATR", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] atr(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  368 */     return (double[])(double[])function(instrument, period, side, "ATR", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] atr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  372 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ATR", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] atr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  376 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ATR", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double avgPrice(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  380 */     return ((Double)function(instrument, period, side, "AVGPRICE", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] avgPrice(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  384 */     return (double[])(double[])function(instrument, period, side, "AVGPRICE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] avgPrice(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  388 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "AVGPRICE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] avgPrice(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  392 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "AVGPRICE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] awesome(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fasterMaTimePeriod, IIndicators.MaType fasterMaType, int slowerMaTimePeriod, IIndicators.MaType slowerMaType, int shift) throws JFException {
/*  396 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AWESOME", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fasterMaTimePeriod), Integer.valueOf(fasterMaType.ordinal()), Integer.valueOf(slowerMaTimePeriod), Integer.valueOf(slowerMaType.ordinal()) }, shift);
/*  397 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] awesome(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fasterMaTimePeriod, IIndicators.MaType fasterMaType, int slowerMaTimePeriod, IIndicators.MaType slowerMaType, long from, long to) throws JFException {
/*  401 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AWESOME", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fasterMaTimePeriod), Integer.valueOf(fasterMaType.ordinal()), Integer.valueOf(slowerMaTimePeriod), Integer.valueOf(slowerMaType.ordinal()) }, from, to);
/*  402 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] awesome(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fasterMaTimePeriod, IIndicators.MaType fasterMaType, int slowerMaTimePeriod, IIndicators.MaType slowerMaType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  406 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AWESOME", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fasterMaTimePeriod), Integer.valueOf(fasterMaType.ordinal()), Integer.valueOf(slowerMaTimePeriod), Integer.valueOf(slowerMaType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  407 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] awesome(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fasterMaTimePeriod, IIndicators.MaType fasterMaType, int slowerMaTimePeriod, IIndicators.MaType slowerMaType, Filter filter, long from, long to) throws JFException {
/*  411 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "AWESOME", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fasterMaTimePeriod), Integer.valueOf(fasterMaType.ordinal()), Integer.valueOf(slowerMaTimePeriod), Integer.valueOf(slowerMaType.ordinal()) }, filter, from, to);
/*  412 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[] bbands(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDevUp, double nbDevDn, IIndicators.MaType maType, int shift) throws JFException {
/*  416 */     Object[] ret = function(instrument, period, side, "BBANDS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDevUp), Double.valueOf(nbDevDn), Integer.valueOf(maType.ordinal()) }, shift);
/*  417 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] bbands(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDevUp, double nbDevDn, IIndicators.MaType maType, long from, long to) throws JFException {
/*  421 */     Object[] ret = function(instrument, period, side, "BBANDS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDevUp), Double.valueOf(nbDevDn), Integer.valueOf(maType.ordinal()) }, from, to);
/*  422 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] bbands(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDevUp, double nbDevDn, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  426 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BBANDS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDevUp), Double.valueOf(nbDevDn), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  427 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] bbands(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDevUp, double nbDevDn, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/*  431 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BBANDS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDevUp), Double.valueOf(nbDevDn), Integer.valueOf(maType.ordinal()) }, filter, from, to);
/*  432 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double beta(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, int shift) throws JFException {
/*  436 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "BETA", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] beta(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, long from, long to) throws JFException {
/*  440 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "BETA", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] beta(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  444 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "BETA", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] beta(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  448 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "BETA", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double bear(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  452 */     return ((Double)function(instrument, period, side, "BEARP", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] bear(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  456 */     return (double[])(double[])function(instrument, period, side, "BEARP", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] bear(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  460 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "BEARP", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] bear(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  464 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "BEARP", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double bull(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  468 */     return ((Double)function(instrument, period, side, "BULLP", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] bull(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  472 */     return (double[])(double[])function(instrument, period, side, "BULLP", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] bull(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  476 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "BULLP", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] bull(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  480 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "BULLP", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] bwmfi(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  484 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BWMFI", null, new Object[0], shift);
/*  485 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] bwmfi(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  489 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BWMFI", null, new Object[0], from, to);
/*  490 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3] };
/*      */   }
/*      */ 
/*      */   public double[][] bwmfi(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  494 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BWMFI", null, new Object[0], filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  495 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3] };
/*      */   }
/*      */ 
/*      */   public double[][] bwmfi(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  499 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BWMFI", null, new Object[0], filter, from, to);
/*  500 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3] };
/*      */   }
/*      */ 
/*      */   public double bop(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  504 */     return ((Double)function(instrument, period, side, "BOP", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] bop(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  508 */     return (double[])(double[])function(instrument, period, side, "BOP", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] bop(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  512 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "BOP", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] bop(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  516 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "BOP", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double butterworthFilter(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/*  520 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BUTTERWORTH", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift);
/*  521 */     return ((Double)ret[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] butterworthFilter(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/*  525 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BUTTERWORTH", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/*  526 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] butterworthFilter(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  530 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "BUTTERWORTH", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  531 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] butterworthFilter(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  535 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "BUTTERWORTH", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] camPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  539 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "CAMPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/*  540 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue(), ((Double)ret[6]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] camPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  544 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "CAMPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/*  545 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] camPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  549 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "CAMPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*  550 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] camPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  554 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "CAMPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/*  555 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double cci(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/*  559 */     return ((Double)function(instrument, period, side, "CCI", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] cci(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/*  563 */     return (double[])(double[])function(instrument, period, side, "CCI", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] cci(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  567 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CCI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] cci(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/*  571 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CCI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdl2Crows(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  575 */     return ((Integer)function(instrument, period, side, "CDL2CROWS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdl2Crows(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  579 */     return (int[])(int[])function(instrument, period, side, "CDL2CROWS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl2Crows(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  583 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL2CROWS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl2Crows(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  587 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL2CROWS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdl3BlackCrows(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  591 */     return ((Integer)function(instrument, period, side, "CDL3BLACKCROWS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdl3BlackCrows(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  595 */     return (int[])(int[])function(instrument, period, side, "CDL3BLACKCROWS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3BlackCrows(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  599 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3BLACKCROWS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3BlackCrows(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  603 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3BLACKCROWS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdl3Inside(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  607 */     return ((Integer)function(instrument, period, side, "CDL3INSIDE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdl3Inside(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  611 */     return (int[])(int[])function(instrument, period, side, "CDL3INSIDE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3Inside(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  615 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3INSIDE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3Inside(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  619 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3INSIDE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdl3LineStrike(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  623 */     return ((Integer)function(instrument, period, side, "CDL3LINESTRIKE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdl3LineStrike(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  627 */     return (int[])(int[])function(instrument, period, side, "CDL3LINESTRIKE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3LineStrike(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  631 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3LINESTRIKE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3LineStrike(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  635 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3LINESTRIKE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdl3Outside(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  639 */     return ((Integer)function(instrument, period, side, "CDL3OUTSIDE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdl3Outside(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  643 */     return (int[])(int[])function(instrument, period, side, "CDL3OUTSIDE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3Outside(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  647 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3OUTSIDE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3Outside(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  651 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3OUTSIDE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdl3StarsInSouth(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  655 */     return ((Integer)function(instrument, period, side, "CDL3STARSINSOUTH", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdl3StarsInSouth(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  659 */     return (int[])(int[])function(instrument, period, side, "CDL3STARSINSOUTH", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3StarsInSouth(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  663 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3STARSINSOUTH", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3StarsInSouth(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  667 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3STARSINSOUTH", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdl3WhiteSoldiers(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  671 */     return ((Integer)function(instrument, period, side, "CDL3WHITESOLDIERS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdl3WhiteSoldiers(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  675 */     return (int[])(int[])function(instrument, period, side, "CDL3WHITESOLDIERS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3WhiteSoldiers(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  679 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3WHITESOLDIERS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdl3WhiteSoldiers(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  683 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDL3WHITESOLDIERS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlAbandonedBaby(Instrument instrument, Period period, OfferSide side, double penetration, int shift) throws JFException {
/*  687 */     return ((Integer)function(instrument, period, side, "CDLABANDONEDBABY", null, new Object[] { Double.valueOf(penetration) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlAbandonedBaby(Instrument instrument, Period period, OfferSide side, double penetration, long from, long to) throws JFException {
/*  691 */     return (int[])(int[])function(instrument, period, side, "CDLABANDONEDBABY", null, new Object[] { Double.valueOf(penetration) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlAbandonedBaby(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  695 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLABANDONEDBABY", null, new Object[] { Double.valueOf(penetration) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlAbandonedBaby(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, long from, long to) throws JFException {
/*  699 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLABANDONEDBABY", null, new Object[] { Double.valueOf(penetration) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlAdvanceBlock(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  703 */     return ((Integer)function(instrument, period, side, "CDLADVANCEBLOCK", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlAdvanceBlock(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  707 */     return (int[])(int[])function(instrument, period, side, "CDLADVANCEBLOCK", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlAdvanceBlock(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  711 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLADVANCEBLOCK", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlAdvanceBlock(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  715 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLADVANCEBLOCK", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlBeltHold(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  719 */     return ((Integer)function(instrument, period, side, "CDLBELTHOLD", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlBeltHold(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  723 */     return (int[])(int[])function(instrument, period, side, "CDLBELTHOLD", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlBeltHold(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  727 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLBELTHOLD", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlBeltHold(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  731 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLBELTHOLD", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlBreakAway(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  735 */     return ((Integer)function(instrument, period, side, "CDLBREAKAWAY", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlBreakAway(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  739 */     return (int[])(int[])function(instrument, period, side, "CDLBREAKAWAY", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlBreakAway(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  743 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLBREAKAWAY", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlBreakAway(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  747 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLBREAKAWAY", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlClosingMarubozu(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  751 */     return ((Integer)function(instrument, period, side, "CDLCLOSINGMARUBOZU", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlClosingMarubozu(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  755 */     return (int[])(int[])function(instrument, period, side, "CDLCLOSINGMARUBOZU", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlClosingMarubozu(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  759 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLCLOSINGMARUBOZU", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlClosingMarubozu(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  763 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLCLOSINGMARUBOZU", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlConcealBabySwall(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  767 */     return ((Integer)function(instrument, period, side, "CDLCONCEALBABYSWALL", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlConcealBabySwall(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  771 */     return (int[])(int[])function(instrument, period, side, "CDLCONCEALBABYSWALL", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlConcealBabySwall(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  775 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLCONCEALBABYSWALL", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlConcealBabySwall(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  779 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLCONCEALBABYSWALL", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlCounterattack(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  783 */     return ((Integer)function(instrument, period, side, "CDLCOUNTERATTACK", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlCounterattack(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  787 */     return (int[])(int[])function(instrument, period, side, "CDLCOUNTERATTACK", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlCounterattack(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  791 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLCOUNTERATTACK", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlCounterattack(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  795 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLCOUNTERATTACK", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlDarkCloudCover(Instrument instrument, Period period, OfferSide side, double penetration, int shift) throws JFException {
/*  799 */     return ((Integer)function(instrument, period, side, "CDLDARKCLOUDCOVER", null, new Object[] { Double.valueOf(penetration) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlDarkCloudCover(Instrument instrument, Period period, OfferSide side, double penetration, long from, long to) throws JFException {
/*  803 */     return (int[])(int[])function(instrument, period, side, "CDLDARKCLOUDCOVER", null, new Object[] { Double.valueOf(penetration) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDarkCloudCover(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  807 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDARKCLOUDCOVER", null, new Object[] { Double.valueOf(penetration) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDarkCloudCover(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, long from, long to) throws JFException {
/*  811 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDARKCLOUDCOVER", null, new Object[] { Double.valueOf(penetration) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlDoji(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  815 */     return ((Integer)function(instrument, period, side, "CDLDOJI", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlDoji(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  819 */     return (int[])(int[])function(instrument, period, side, "CDLDOJI", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDoji(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  823 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDOJI", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDoji(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  827 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDOJI", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlDojiStar(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  831 */     return ((Integer)function(instrument, period, side, "CDLDOJISTAR", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlDojiStar(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  835 */     return (int[])(int[])function(instrument, period, side, "CDLDOJISTAR", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDojiStar(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  839 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDOJISTAR", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDojiStar(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  843 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDOJISTAR", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlDragonflyDoji(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  847 */     return ((Integer)function(instrument, period, side, "CDLDRAGONFLYDOJI", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlDragonflyDoji(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  851 */     return (int[])(int[])function(instrument, period, side, "CDLDRAGONFLYDOJI", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDragonflyDoji(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  855 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDRAGONFLYDOJI", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlDragonflyDoji(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  859 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLDRAGONFLYDOJI", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlEngulfing(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  863 */     return ((Integer)function(instrument, period, side, "CDLENGULFING", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlEngulfing(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  867 */     return (int[])(int[])function(instrument, period, side, "CDLENGULFING", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlEngulfing(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  871 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLENGULFING", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlEngulfing(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  875 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLENGULFING", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlEveningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, int shift) throws JFException {
/*  879 */     return ((Integer)function(instrument, period, side, "CDLEVENINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlEveningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, long from, long to) throws JFException {
/*  883 */     return (int[])(int[])function(instrument, period, side, "CDLEVENINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlEveningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  887 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLEVENINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlEveningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, long from, long to) throws JFException {
/*  891 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLEVENINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlEveningStar(Instrument instrument, Period period, OfferSide side, double penetration, int shift) throws JFException {
/*  895 */     return ((Integer)function(instrument, period, side, "CDLEVENINGSTAR", null, new Object[] { Double.valueOf(penetration) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlEveningStar(Instrument instrument, Period period, OfferSide side, double penetration, long from, long to) throws JFException {
/*  899 */     return (int[])(int[])function(instrument, period, side, "CDLEVENINGSTAR", null, new Object[] { Double.valueOf(penetration) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlEveningStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  903 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLEVENINGSTAR", null, new Object[] { Double.valueOf(penetration) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlEveningStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, long from, long to) throws JFException {
/*  907 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLEVENINGSTAR", null, new Object[] { Double.valueOf(penetration) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlGapSideSideWhite(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  911 */     return ((Integer)function(instrument, period, side, "CDLGAPSIDESIDEWHITE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlGapSideSideWhite(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  915 */     return (int[])(int[])function(instrument, period, side, "CDLGAPSIDESIDEWHITE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlGapSideSideWhite(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  919 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLGAPSIDESIDEWHITE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlGapSideSideWhite(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  923 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLGAPSIDESIDEWHITE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlGravestoneDoji(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  927 */     return ((Integer)function(instrument, period, side, "CDLGRAVESTONEDOJI", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlGravestoneDoji(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  931 */     return (int[])(int[])function(instrument, period, side, "CDLGRAVESTONEDOJI", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlGravestoneDoji(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  935 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLGRAVESTONEDOJI", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlGravestoneDoji(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  939 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLGRAVESTONEDOJI", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHammer(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  943 */     return ((Integer)function(instrument, period, side, "CDLHAMMER", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHammer(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  947 */     return (int[])(int[])function(instrument, period, side, "CDLHAMMER", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHammer(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  951 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHAMMER", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHammer(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  955 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHAMMER", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHangingMan(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  959 */     return ((Integer)function(instrument, period, side, "CDLHANGINGMAN", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHangingMan(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  963 */     return (int[])(int[])function(instrument, period, side, "CDLHANGINGMAN", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHangingMan(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  967 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHANGINGMAN", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHangingMan(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  971 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHANGINGMAN", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHarami(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  975 */     return ((Integer)function(instrument, period, side, "CDLHARAMI", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHarami(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  979 */     return (int[])(int[])function(instrument, period, side, "CDLHARAMI", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHarami(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  983 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHARAMI", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHarami(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/*  987 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHARAMI", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHaramiCross(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  991 */     return ((Integer)function(instrument, period, side, "CDLHARAMICROSS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHaramiCross(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/*  995 */     return (int[])(int[])function(instrument, period, side, "CDLHARAMICROSS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHaramiCross(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/*  999 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHARAMICROSS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHaramiCross(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1003 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHARAMICROSS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHighWave(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1007 */     return ((Integer)function(instrument, period, side, "CDLHIGHWAVE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHighWave(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1011 */     return (int[])(int[])function(instrument, period, side, "CDLHIGHWAVE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHighWave(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1015 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHIGHWAVE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHighWave(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1019 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHIGHWAVE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHikkake(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1023 */     return ((Integer)function(instrument, period, side, "CDLHIKKAKE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHikkake(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1027 */     return (int[])(int[])function(instrument, period, side, "CDLHIKKAKE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHikkake(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1031 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHIKKAKE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHikkake(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1035 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHIKKAKE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHikkakeMod(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1039 */     return ((Integer)function(instrument, period, side, "CDLHIKKAKEMOD", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHikkakeMod(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1043 */     return (int[])(int[])function(instrument, period, side, "CDLHIKKAKEMOD", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHikkakeMod(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1047 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHIKKAKEMOD", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHikkakeMod(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1051 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHIKKAKEMOD", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlHomingPigeon(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1055 */     return ((Integer)function(instrument, period, side, "CDLHOMINGPIGEON", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlHomingPigeon(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1059 */     return (int[])(int[])function(instrument, period, side, "CDLHOMINGPIGEON", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHomingPigeon(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1063 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHOMINGPIGEON", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlHomingPigeon(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1067 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLHOMINGPIGEON", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlIdentical3Crows(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1071 */     return ((Integer)function(instrument, period, side, "CDLIDENTICAL3CROWS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlIdentical3Crows(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1075 */     return (int[])(int[])function(instrument, period, side, "CDLIDENTICAL3CROWS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlIdentical3Crows(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1079 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLIDENTICAL3CROWS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlIdentical3Crows(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1083 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLIDENTICAL3CROWS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlInNeck(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1087 */     return ((Integer)function(instrument, period, side, "CDLINNECK", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlInNeck(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1091 */     return (int[])(int[])function(instrument, period, side, "CDLINNECK", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlInNeck(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1095 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLINNECK", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlInNeck(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1099 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLINNECK", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlInvertedHammer(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1103 */     return ((Integer)function(instrument, period, side, "CDLINVERTEDHAMMER", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlInvertedHammer(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1107 */     return (int[])(int[])function(instrument, period, side, "CDLINVERTEDHAMMER", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlInvertedHammer(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1111 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLINVERTEDHAMMER", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlInvertedHammer(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1115 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLINVERTEDHAMMER", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlKicking(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1119 */     return ((Integer)function(instrument, period, side, "CDLKICKING", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlKicking(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1123 */     return (int[])(int[])function(instrument, period, side, "CDLKICKING", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlKicking(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1127 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLKICKING", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlKicking(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1131 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLKICKING", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlKickingByLength(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1135 */     return ((Integer)function(instrument, period, side, "CDLKICKINGBYLENGTH", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlKickingByLength(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1139 */     return (int[])(int[])function(instrument, period, side, "CDLKICKINGBYLENGTH", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlKickingByLength(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1143 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLKICKINGBYLENGTH", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlKickingByLength(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1147 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLKICKINGBYLENGTH", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlLadderBotton(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1151 */     return ((Integer)function(instrument, period, side, "CDLLADDERBOTTOM", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlLadderBotton(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1155 */     return (int[])(int[])function(instrument, period, side, "CDLLADDERBOTTOM", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLadderBotton(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1159 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLADDERBOTTOM", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLadderBotton(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1163 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLADDERBOTTOM", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlLadderBottom(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1167 */     return ((Integer)function(instrument, period, side, "CDLLADDERBOTTOM", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlLadderBottom(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1171 */     return (int[])(int[])function(instrument, period, side, "CDLLADDERBOTTOM", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLadderBottom(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1175 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLADDERBOTTOM", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLadderBottom(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1179 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLADDERBOTTOM", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlLongLeggedDoji(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1183 */     return ((Integer)function(instrument, period, side, "CDLLONGLEGGEDDOJI", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlLongLeggedDoji(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1187 */     return (int[])(int[])function(instrument, period, side, "CDLLONGLEGGEDDOJI", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLongLeggedDoji(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1191 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLONGLEGGEDDOJI", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLongLeggedDoji(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1195 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLONGLEGGEDDOJI", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlLongLine(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1199 */     return ((Integer)function(instrument, period, side, "CDLLONGLINE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlLongLine(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1203 */     return (int[])(int[])function(instrument, period, side, "CDLLONGLINE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLongLine(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1207 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLONGLINE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlLongLine(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1211 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLLONGLINE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlMarubozu(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1215 */     return ((Integer)function(instrument, period, side, "CDLMARUBOZU", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlMarubozu(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1219 */     return (int[])(int[])function(instrument, period, side, "CDLMARUBOZU", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMarubozu(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1223 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMARUBOZU", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMarubozu(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1227 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMARUBOZU", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlMatchingLow(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1231 */     return ((Integer)function(instrument, period, side, "CDLMATCHINGLOW", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlMatchingLow(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1235 */     return (int[])(int[])function(instrument, period, side, "CDLMATCHINGLOW", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMatchingLow(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1239 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMATCHINGLOW", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMatchingLow(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1243 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMATCHINGLOW", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlMathold(Instrument instrument, Period period, OfferSide side, double penetration, int shift) throws JFException {
/* 1247 */     return ((Integer)function(instrument, period, side, "CDLMATHOLD", null, new Object[] { Double.valueOf(penetration) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlMathold(Instrument instrument, Period period, OfferSide side, double penetration, long from, long to) throws JFException {
/* 1251 */     return (int[])(int[])function(instrument, period, side, "CDLMATHOLD", null, new Object[] { Double.valueOf(penetration) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMathold(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1255 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMATHOLD", null, new Object[] { Double.valueOf(penetration) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMathold(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, long from, long to) throws JFException {
/* 1259 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMATHOLD", null, new Object[] { Double.valueOf(penetration) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlMorningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, int shift) throws JFException {
/* 1263 */     return ((Integer)function(instrument, period, side, "CDLMORNINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlMorningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, long from, long to) throws JFException {
/* 1267 */     return (int[])(int[])function(instrument, period, side, "CDLMORNINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMorningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1271 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMORNINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMorningDojiStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, long from, long to) throws JFException {
/* 1275 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMORNINGDOJISTAR", null, new Object[] { Double.valueOf(penetration) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlMorningStar(Instrument instrument, Period period, OfferSide side, double penetration, int shift) throws JFException {
/* 1279 */     return ((Integer)function(instrument, period, side, "CDLMORNINGSTAR", null, new Object[] { Double.valueOf(penetration) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlMorningStar(Instrument instrument, Period period, OfferSide side, double penetration, long from, long to) throws JFException {
/* 1283 */     return (int[])(int[])function(instrument, period, side, "CDLMORNINGSTAR", null, new Object[] { Double.valueOf(penetration) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMorningStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1287 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMORNINGSTAR", null, new Object[] { Double.valueOf(penetration) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlMorningStar(Instrument instrument, Period period, OfferSide side, double penetration, Filter filter, long from, long to) throws JFException {
/* 1291 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLMORNINGSTAR", null, new Object[] { Double.valueOf(penetration) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlOnNeck(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1295 */     return ((Integer)function(instrument, period, side, "CDLONNECK", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlOnNeck(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1299 */     return (int[])(int[])function(instrument, period, side, "CDLONNECK", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlOnNeck(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1303 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLONNECK", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlOnNeck(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1307 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLONNECK", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlPiercing(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1311 */     return ((Integer)function(instrument, period, side, "CDLPIERCING", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlPiercing(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1315 */     return (int[])(int[])function(instrument, period, side, "CDLPIERCING", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlPiercing(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1319 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLPIERCING", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlPiercing(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1323 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLPIERCING", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlRickshawMan(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1327 */     return ((Integer)function(instrument, period, side, "CDLRICKSHAWMAN", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlRickshawMan(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1331 */     return (int[])(int[])function(instrument, period, side, "CDLRICKSHAWMAN", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlRickshawMan(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1335 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLRICKSHAWMAN", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlRickshawMan(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1339 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLRICKSHAWMAN", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlRiseFall3Methods(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1343 */     return ((Integer)function(instrument, period, side, "CDLRISEFALL3METHODS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlRiseFall3Methods(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1347 */     return (int[])(int[])function(instrument, period, side, "CDLRISEFALL3METHODS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlRiseFall3Methods(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1351 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLRISEFALL3METHODS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlRiseFall3Methods(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1355 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLRISEFALL3METHODS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlSeparatingLines(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1359 */     return ((Integer)function(instrument, period, side, "CDLSEPARATINGLINES", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlSeparatingLines(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1363 */     return (int[])(int[])function(instrument, period, side, "CDLSEPARATINGLINES", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlSeparatingLines(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1367 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSEPARATINGLINES", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlSeparatingLines(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1371 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSEPARATINGLINES", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlShootingStar(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1375 */     return ((Integer)function(instrument, period, side, "CDLSHOOTINGSTAR", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlShootingStar(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1379 */     return (int[])(int[])function(instrument, period, side, "CDLSHOOTINGSTAR", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlShootingStar(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1383 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSHOOTINGSTAR", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlShootingStar(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1387 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSHOOTINGSTAR", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlShortLine(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1391 */     return ((Integer)function(instrument, period, side, "CDLSHORTLINE", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlShortLine(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1395 */     return (int[])(int[])function(instrument, period, side, "CDLSHORTLINE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlShortLine(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1399 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSHORTLINE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlShortLine(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1403 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSHORTLINE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlSpinningTop(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1407 */     return ((Integer)function(instrument, period, side, "CDLSPINNINGTOP", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlSpinningTop(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1411 */     return (int[])(int[])function(instrument, period, side, "CDLSPINNINGTOP", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlSpinningTop(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1415 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSPINNINGTOP", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlSpinningTop(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1419 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSPINNINGTOP", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlStalledPattern(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1423 */     return ((Integer)function(instrument, period, side, "CDLSTALLEDPATTERN", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlStalledPattern(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1427 */     return (int[])(int[])function(instrument, period, side, "CDLSTALLEDPATTERN", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlStalledPattern(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1431 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSTALLEDPATTERN", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlStalledPattern(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1435 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSTALLEDPATTERN", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlStickSandwich(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1439 */     return ((Integer)function(instrument, period, side, "CDLSTICKSANDWICH", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlStickSandwich(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1443 */     return (int[])(int[])function(instrument, period, side, "CDLSTICKSANDWICH", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlStickSandwich(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1447 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSTICKSANDWICH", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlStickSandwich(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1451 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLSTICKSANDWICH", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlTakuri(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1455 */     return ((Integer)function(instrument, period, side, "CDLTAKURI", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlTakuri(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1459 */     return (int[])(int[])function(instrument, period, side, "CDLTAKURI", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlTakuri(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1463 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTAKURI", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlTakuri(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1467 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTAKURI", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlTasukiGap(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1471 */     return ((Integer)function(instrument, period, side, "CDLTASUKIGAP", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlTasukiGap(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1475 */     return (int[])(int[])function(instrument, period, side, "CDLTASUKIGAP", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlTasukiGap(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1479 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTASUKIGAP", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlTasukiGap(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1483 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTASUKIGAP", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlThrusting(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1487 */     return ((Integer)function(instrument, period, side, "CDLTHRUSTING", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlThrusting(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1491 */     return (int[])(int[])function(instrument, period, side, "CDLTHRUSTING", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlThrusting(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1495 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTHRUSTING", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlThrusting(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1499 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTHRUSTING", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlTristar(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1503 */     return ((Integer)function(instrument, period, side, "CDLTRISTAR", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlTristar(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1507 */     return (int[])(int[])function(instrument, period, side, "CDLTRISTAR", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlTristar(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1511 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTRISTAR", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlTristar(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1515 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLTRISTAR", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlUnique3River(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1519 */     return ((Integer)function(instrument, period, side, "CDLUNIQUE3RIVER", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlUnique3River(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1523 */     return (int[])(int[])function(instrument, period, side, "CDLUNIQUE3RIVER", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlUnique3River(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1527 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLUNIQUE3RIVER", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlUnique3River(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1531 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLUNIQUE3RIVER", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlUpsideGap2Crows(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1535 */     return ((Integer)function(instrument, period, side, "CDLUPSIDEGAP2CROWS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlUpsideGap2Crows(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1539 */     return (int[])(int[])function(instrument, period, side, "CDLUPSIDEGAP2CROWS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlUpsideGap2Crows(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1543 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLUPSIDEGAP2CROWS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlUpsideGap2Crows(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1547 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLUPSIDEGAP2CROWS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int cdlXsideGap3Methods(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1551 */     return ((Integer)function(instrument, period, side, "CDLXSIDEGAP3METHODS", null, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] cdlXsideGap3Methods(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1555 */     return (int[])(int[])function(instrument, period, side, "CDLXSIDEGAP3METHODS", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlXsideGap3Methods(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1559 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLXSIDEGAP3METHODS", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] cdlXsideGap3Methods(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1563 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CDLXSIDEGAP3METHODS", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ceil(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 1567 */     return ((Double)function(instrument, period, side, "CEIL", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ceil(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 1571 */     return (double[])(double[])function(instrument, period, side, "CEIL", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ceil(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1575 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CEIL", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ceil(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 1579 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CEIL", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double cmo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 1583 */     return ((Double)function(instrument, period, side, "CMO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] cmo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 1587 */     return (double[])(double[])function(instrument, period, side, "CMO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] cmo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1591 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CMO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] cmo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1595 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "CMO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] cog(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int smoothPeriod, IIndicators.MaType maType, int shift) throws JFException {
/* 1599 */     Object[] res = function(instrument, period, side, "COG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(smoothPeriod), Integer.valueOf(maType.ordinal()) }, shift);
/* 1600 */     return new double[] { ((Double)res[0]).doubleValue(), ((Double)res[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] cog(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int smoothPeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/* 1604 */     Object[] res = function(instrument, period, side, "COG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(smoothPeriod), Integer.valueOf(maType.ordinal()) }, from, to);
/* 1605 */     return new double[][] { (double[])(double[])res[0], (double[])(double[])res[1] };
/*      */   }
/*      */ 
/*      */   public double[][] cog(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int smoothPeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1609 */     Object[] res = calculateIndicator(instrument, period, new OfferSide[] { side }, "COG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(smoothPeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1610 */     return new double[][] { (double[])(double[])res[0], (double[])(double[])res[1] };
/*      */   }
/*      */ 
/*      */   public double[][] cog(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int smoothPeriod, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/* 1614 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "COG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(smoothPeriod), Integer.valueOf(maType.ordinal()) }, filter, from, to);
/* 1615 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double correl(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, int shift) throws JFException {
/* 1619 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "CORREL", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] correl(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, long from, long to) throws JFException {
/* 1623 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "CORREL", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] correl(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1627 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "CORREL", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] correl(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1631 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "CORREL", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double cos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 1635 */     return ((Double)function(instrument, period, side, "COS", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] cos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 1639 */     return (double[])(double[])function(instrument, period, side, "COS", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] cos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1643 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "COS", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] cos(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 1647 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "COS", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double cosh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 1651 */     return ((Double)function(instrument, period, side, "COSH", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] cosh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 1655 */     return (double[])(double[])function(instrument, period, side, "COSH", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] cosh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1659 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "COSH", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] cosh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 1663 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "COSH", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double dema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 1667 */     return ((Double)function(instrument, period, side, "DEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] dema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 1671 */     return (double[])(double[])function(instrument, period, side, "DEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] dema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1675 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "DEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] dema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1679 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "DEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double div(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int shift) throws JFException
/*      */   {
/* 1684 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "DIV", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] div(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, long from, long to) throws JFException
/*      */   {
/* 1689 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "DIV", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] div(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 1694 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "DIV", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] div(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, long from, long to) throws JFException
/*      */   {
/* 1699 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "DIV", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] dmi(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 1703 */     Object[] res = function(instrument, period, side, "DMI", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 1704 */     return new double[] { ((Double)res[0]).doubleValue(), ((Double)res[1]).doubleValue(), ((Double)res[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] dmi(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 1708 */     Object[] res = function(instrument, period, side, "DMI", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 1709 */     return new double[][] { (double[])(double[])res[0], (double[])(double[])res[1], (double[])(double[])res[2] };
/*      */   }
/*      */ 
/*      */   public double[][] dmi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1713 */     Object[] res = calculateIndicator(instrument, period, new OfferSide[] { side }, "DMI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1714 */     return new double[][] { (double[])(double[])res[0], (double[])(double[])res[1], (double[])(double[])res[2] };
/*      */   }
/*      */ 
/*      */   public double[][] dmi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1718 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "DMI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 1719 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[] donchian(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 1723 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "DONCHIANCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 1724 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] donchian(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 1728 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "DONCHIANCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 1729 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] donchian(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1733 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "DONCHIANCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1734 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] donchian(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1738 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "DONCHIANCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 1739 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double dx(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 1743 */     return ((Double)function(instrument, period, side, "DX", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] dx(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 1747 */     return (double[])(double[])function(instrument, period, side, "DX", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] dx(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1751 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "DX", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] dx(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1755 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "DX", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 1759 */     return ((Double)function(instrument, period, side, "EMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 1763 */     return (double[])(double[])function(instrument, period, side, "EMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1767 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "EMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1771 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "EMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] emaEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, int shift) throws JFException {
/* 1775 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "EMAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, shift);
/* 1776 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] emaEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, long from, long to) throws JFException {
/* 1780 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "EMAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, from, to);
/* 1781 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] emaEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1785 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "EMAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1786 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] emaEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, Filter filter, long from, long to) throws JFException {
/* 1790 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "EMAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, filter, from, to);
/* 1791 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double exp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 1795 */     return ((Double)function(instrument, period, side, "EXP", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] exp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 1799 */     return (double[])(double[])function(instrument, period, side, "EXP", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] exp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 1804 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "EXP", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] exp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 1808 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "EXP", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double floor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 1812 */     return ((Double)function(instrument, period, side, "FLOOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] floor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 1816 */     return (double[])(double[])function(instrument, period, side, "FLOOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] floor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 1821 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "FLOOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] floor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 1825 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "FLOOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double force(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, int shift) throws JFException {
/* 1829 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FORCEI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, shift);
/* 1830 */     return ((Double)ret[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] force(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/* 1834 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FORCEI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, from, to);
/* 1835 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] force(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter)
/*      */     throws JFException
/*      */   {
/* 1842 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FORCEI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1843 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] force(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/* 1847 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FORCEI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] fractal(Instrument instrument, Period period, OfferSide side, int barsOnSides, int shift) throws JFException {
/* 1851 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTAL", null, new Object[] { Integer.valueOf(barsOnSides) }, shift);
/* 1852 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] fractal(Instrument instrument, Period period, OfferSide side, int barsOnSides, long from, long to) throws JFException {
/* 1856 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTAL", null, new Object[] { Integer.valueOf(barsOnSides) }, from, to);
/* 1857 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] fractal(Instrument instrument, Period period, OfferSide side, int barsOnSides, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1861 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTAL", null, new Object[] { Integer.valueOf(barsOnSides) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1862 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] fractal(Instrument instrument, Period period, OfferSide side, int barsOnSides, Filter filter, long from, long to) throws JFException {
/* 1866 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTAL", null, new Object[] { Integer.valueOf(barsOnSides) }, filter, from, to);
/* 1867 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[] fractalLines(Instrument instrument, Period period, OfferSide side, int barsOnSides, int shift) throws JFException {
/* 1871 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTALLINES", null, new Object[] { Integer.valueOf(barsOnSides) }, shift);
/* 1872 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] fractalLines(Instrument instrument, Period period, OfferSide side, int barsOnSides, long from, long to) throws JFException {
/* 1876 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTALLINES", null, new Object[] { Integer.valueOf(barsOnSides) }, from, to);
/* 1877 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] fractalLines(Instrument instrument, Period period, OfferSide side, int barsOnSides, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1881 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTALLINES", null, new Object[] { Integer.valueOf(barsOnSides) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1882 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] fractalLines(Instrument instrument, Period period, OfferSide side, int barsOnSides, Filter filter, long from, long to) throws JFException {
/* 1886 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "FRACTALLINES", null, new Object[] { Integer.valueOf(barsOnSides) }, filter, from, to);
/* 1887 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[] fibPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 1891 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FIBPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 1892 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue(), ((Double)ret[6]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] fibPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 1896 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FIBPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 1897 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] fibPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1901 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FIBPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1902 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] fibPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1906 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "FIBPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 1907 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[] gator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, int shift) throws JFException {
/* 1911 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "GATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, shift);
/* 1912 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] gator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, long from, long to) throws JFException {
/* 1916 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "GATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, from, to);
/* 1917 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] gator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1921 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "GATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1922 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] gator(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int jawTimePeriod, int teethTimePeriod, int lipsTimePeriod, Filter filter, long from, long to) throws JFException {
/* 1926 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "GATOR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(jawTimePeriod), Integer.valueOf(teethTimePeriod), Integer.valueOf(lipsTimePeriod) }, filter, from, to);
/* 1927 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[] heikenAshi(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1931 */     Object ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HeikinAshi", null, null, shift)[0];
/* 1932 */     return (double[])(double[])ret;
/*      */   }
/*      */ 
/*      */   public double[][] heikenAshi(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1936 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HeikinAshi", null, null, from, to);
/* 1937 */     double[][] retD = new double[((Object[])(Object[])ret[0]).length][];
/* 1938 */     System.arraycopy(ret[0], 0, retD, 0, retD.length);
/* 1939 */     return retD;
/*      */   }
/*      */ 
/*      */   public double[][] heikenAshi(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1943 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HeikinAshi", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1944 */     double[][] retD = new double[((Object[])(Object[])ret[0]).length][];
/* 1945 */     System.arraycopy(ret[0], 0, retD, 0, retD.length);
/* 1946 */     return retD;
/*      */   }
/*      */ 
/*      */   public double[] heikinAshi(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 1950 */     Object ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HeikinAshi", null, null, shift)[0];
/* 1951 */     return (double[])(double[])ret;
/*      */   }
/*      */ 
/*      */   public double[][] heikinAshi(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 1955 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HeikinAshi", null, null, from, to);
/* 1956 */     double[][] retD = new double[((Object[])(Object[])ret[0]).length][];
/* 1957 */     System.arraycopy(ret[0], 0, retD, 0, retD.length);
/* 1958 */     return retD;
/*      */   }
/*      */ 
/*      */   public double[][] heikinAshi(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1962 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HeikinAshi", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 1963 */     double[][] retD = new double[((Object[])(Object[])ret[0]).length][];
/* 1964 */     System.arraycopy(ret[0], 0, retD, 0, retD.length);
/* 1965 */     return retD;
/*      */   }
/*      */ 
/*      */   public double[][] heikinAshi(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 1969 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HeikinAshi", null, null, filter, from, to);
/* 1970 */     double[][] retD = new double[((Object[])(Object[])ret[0]).length][];
/* 1971 */     System.arraycopy(ret[0], 0, retD, 0, retD.length);
/* 1972 */     return retD;
/*      */   }
/*      */ 
/*      */   public double hma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 1976 */     return ((Double)function(instrument, period, side, "HMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] hma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 1980 */     return (double[])(double[])function(instrument, period, side, "HMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] hma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 1984 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] hma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 1988 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ht_dcperiod(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 1992 */     return ((Double)function(instrument, period, side, "HT_DCPERIOD", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ht_dcperiod(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 1996 */     return (double[])(double[])function(instrument, period, side, "HT_DCPERIOD", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ht_dcperiod(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2001 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_DCPERIOD", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ht_dcperiod(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2005 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_DCPERIOD", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ht_dcphase(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 2009 */     return ((Double)function(instrument, period, side, "HT_DCPHASE", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ht_dcphase(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 2013 */     return (double[])(double[])function(instrument, period, side, "HT_DCPHASE", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ht_dcphase(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2018 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_DCPHASE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ht_dcphase(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2022 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_DCPHASE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ht_phasor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 2026 */     Object[] ret = function(instrument, period, side, "HT_PHASOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift);
/* 2027 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] ht_phasor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 2031 */     Object[] ret = function(instrument, period, side, "HT_PHASOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to);
/* 2032 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] ht_phasor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2037 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_PHASOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2038 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] ht_phasor(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2042 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_PHASOR", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to);
/* 2043 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[] ht_sine(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 2047 */     Object[] ret = function(instrument, period, side, "HT_SINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift);
/* 2048 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] ht_sine(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 2052 */     Object[] ret = function(instrument, period, side, "HT_SINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to);
/* 2053 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] ht_sine(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2058 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_SINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2059 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] ht_sine(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2063 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_SINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to);
/* 2064 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double ht_trendline(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 2068 */     return ((Double)function(instrument, period, side, "HT_TRENDLINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ht_trendline(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 2072 */     return (double[])(double[])function(instrument, period, side, "HT_TRENDLINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ht_trendline(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2077 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_TRENDLINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ht_trendline(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2081 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_TRENDLINE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int ht_trendmode(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 2085 */     return ((Integer)function(instrument, period, side, "HT_TRENDMODE", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] ht_trendmode(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 2089 */     return (int[])(int[])function(instrument, period, side, "HT_TRENDMODE", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] ht_trendmode(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2094 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_TRENDMODE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] ht_trendmode(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2098 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "HT_TRENDMODE", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ichimoku(Instrument instrument, Period period, OfferSide side, int tenkan, int kijun, int senkou, int shift) throws JFException {
/* 2102 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ICHIMOKU", null, new Object[] { Integer.valueOf(tenkan), Integer.valueOf(kijun), Integer.valueOf(senkou) }, shift);
/* 2103 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), Double.valueOf(((double[])(double[])ret[5])[0]).doubleValue(), Double.valueOf(((double[])(double[])ret[5])[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] ichimoku(Instrument instrument, Period period, OfferSide side, int tenkan, int kijun, int senkou, long from, long to) throws JFException {
/* 2107 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ICHIMOKU", null, new Object[] { Integer.valueOf(tenkan), Integer.valueOf(kijun), Integer.valueOf(senkou) }, from, to);
/* 2108 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])((Object[])(Object[])ret[5])[0], (double[])(double[])((Object[])(Object[])ret[5])[0] };
/*      */   }
/*      */ 
/*      */   public double[][] ichimoku(Instrument instrument, Period period, OfferSide side, int tenkan, int kijun, int senkou, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2112 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ICHIMOKU", null, new Object[] { Integer.valueOf(tenkan), Integer.valueOf(kijun), Integer.valueOf(senkou) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2113 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])((Object[])(Object[])ret[5])[0], (double[])(double[])((Object[])(Object[])ret[5])[0] };
/*      */   }
/*      */ 
/*      */   public double[][] ichimoku(Instrument instrument, Period period, OfferSide side, int tenkan, int kijun, int senkou, Filter filter, long from, long to) throws JFException {
/* 2117 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "ICHIMOKU", null, new Object[] { Integer.valueOf(tenkan), Integer.valueOf(kijun), Integer.valueOf(senkou) }, filter, from, to);
/* 2118 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])((Object[])(Object[])ret[5])[0], (double[])(double[])((Object[])(Object[])ret[5])[0] };
/*      */   }
/*      */ 
/*      */   public double kairi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, int shift) throws JFException {
/* 2122 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "KAIRI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, shift);
/* 2123 */     return ((Double)ret[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] kairi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/* 2127 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "KAIRI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, from, to);
/* 2128 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] kairi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2132 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "KAIRI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2133 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] kairi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/* 2137 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side, side }, "KAIRI", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double kama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2141 */     return ((Double)function(instrument, period, side, "KAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] kama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2145 */     return (double[])(double[])function(instrument, period, side, "KAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] kama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2149 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "KAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] kama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2153 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "KAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] keltner(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2157 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "KELTNER", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 2158 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] keltner(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2162 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "KELTNER", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 2163 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] keltner(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2167 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "KELTNER", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2168 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] keltner(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2172 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "KELTNER", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 2173 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double lasacs1(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int ma, double gamma, int lookback, int shift) throws JFException {
/* 2177 */     return ((Double)function(instrument, period, side, "LAGACS1", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(ma), Double.valueOf(gamma), Integer.valueOf(lookback) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] lasacs1(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int ma, double gamma, int lookback, long from, long to) throws JFException {
/* 2181 */     return (double[])(double[])function(instrument, period, side, "LAGACS1", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(ma), Double.valueOf(gamma), Integer.valueOf(lookback) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] lasacs1(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int ma, double gamma, int lookback, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2185 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LAGACS1", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(ma), Double.valueOf(gamma), Integer.valueOf(lookback) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double lagACS1(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int ma, double gamma, int lookback, int shift) throws JFException {
/* 2189 */     return ((Double)function(instrument, period, side, "LAGACS1", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(ma), Double.valueOf(gamma), Integer.valueOf(lookback) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] lagACS1(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int ma, double gamma, int lookback, long from, long to) throws JFException {
/* 2193 */     return (double[])(double[])function(instrument, period, side, "LAGACS1", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(ma), Double.valueOf(gamma), Integer.valueOf(lookback) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] lagACS1(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int ma, double gamma, int lookback, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2197 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LAGACS1", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(ma), Double.valueOf(gamma), Integer.valueOf(lookback) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] lagACS1(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int ma, double gamma, int lookback, Filter filter, long from, long to) throws JFException {
/* 2201 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LAGACS1", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(ma), Double.valueOf(gamma), Integer.valueOf(lookback) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double linearReg(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2205 */     return ((Double)function(instrument, period, side, "LINEARREG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] linearReg(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2209 */     return (double[])(double[])function(instrument, period, side, "LINEARREG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearReg(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2213 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearReg(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2217 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double linearRegAngle(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2221 */     return ((Double)function(instrument, period, side, "LINEARREG_ANGLE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] linearRegAngle(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2225 */     return (double[])(double[])function(instrument, period, side, "LINEARREG_ANGLE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearRegAngle(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2229 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG_ANGLE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearRegAngle(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2233 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG_ANGLE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double linearRegIntercept(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2237 */     return ((Double)function(instrument, period, side, "LINEARREG_INTERCEPT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] linearRegIntercept(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2241 */     return (double[])(double[])function(instrument, period, side, "LINEARREG_INTERCEPT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearRegIntercept(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2245 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG_INTERCEPT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearRegIntercept(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2249 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG_INTERCEPT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double linearRegSlope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2253 */     return ((Double)function(instrument, period, side, "LINEARREG_SLOPE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] linearRegSlope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2257 */     return (double[])(double[])function(instrument, period, side, "LINEARREG_SLOPE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearRegSlope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2261 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG_SLOPE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] linearRegSlope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2265 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LINEARREG_SLOPE", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ln(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 2269 */     return ((Double)function(instrument, period, side, "LN", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ln(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 2273 */     return (double[])(double[])function(instrument, period, side, "LN", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ln(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2277 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ln(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2281 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double log10(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 2285 */     return ((Double)function(instrument, period, side, "LOG10", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] log10(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 2289 */     return (double[])(double[])function(instrument, period, side, "LOG10", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] log10(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2293 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LOG10", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] log10(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 2297 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LOG10", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double lwma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2301 */     return ((Double)function(instrument, period, side, "LWMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] lwma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2305 */     return (double[])(double[])function(instrument, period, side, "LWMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] lwma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2309 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LWMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] lwma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2313 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "LWMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, int shift) throws JFException {
/* 2317 */     return ((Double)function(instrument, period, side, "MA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/* 2321 */     return (double[])(double[])function(instrument, period, side, "MA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2325 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/* 2329 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(maType.ordinal()) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] macd(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, int signalPeriod, int shift) throws JFException {
/* 2333 */     Object[] ret = function(instrument, period, side, "MACD", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(signalPeriod) }, shift);
/* 2334 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] macd(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, int signalPeriod, long from, long to) throws JFException {
/* 2338 */     Object[] ret = function(instrument, period, side, "MACD", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(signalPeriod) }, from, to);
/* 2339 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] macd(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, int signalPeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2343 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MACD", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(signalPeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2344 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] macd(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, int signalPeriod, Filter filter, long from, long to) throws JFException {
/* 2348 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MACD", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(signalPeriod) }, filter, from, to);
/* 2349 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[] macdExt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, IIndicators.MaType fastMaType, int slowPeriod, IIndicators.MaType slowMaType, int signalPeriod, IIndicators.MaType signalMaType, int shift) throws JFException {
/* 2353 */     Object[] ret = function(instrument, period, side, "MACDEXT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(fastMaType.ordinal()), Integer.valueOf(slowPeriod), Integer.valueOf(slowMaType.ordinal()), Integer.valueOf(signalPeriod), Integer.valueOf(signalMaType.ordinal()) }, shift);
/* 2354 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] macdExt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, IIndicators.MaType fastMaType, int slowPeriod, IIndicators.MaType slowMaType, int signalPeriod, IIndicators.MaType signalMaType, long from, long to) throws JFException {
/* 2358 */     Object[] ret = function(instrument, period, side, "MACDEXT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(fastMaType.ordinal()), Integer.valueOf(slowPeriod), Integer.valueOf(slowMaType.ordinal()), Integer.valueOf(signalPeriod), Integer.valueOf(signalMaType.ordinal()) }, from, to);
/* 2359 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] macdExt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, IIndicators.MaType fastMaType, int slowPeriod, IIndicators.MaType slowMaType, int signalPeriod, IIndicators.MaType signalMaType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2363 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MACDEXT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(fastMaType.ordinal()), Integer.valueOf(slowPeriod), Integer.valueOf(slowMaType.ordinal()), Integer.valueOf(signalPeriod), Integer.valueOf(signalMaType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2364 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] macdExt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, IIndicators.MaType fastMaType, int slowPeriod, IIndicators.MaType slowMaType, int signalPeriod, IIndicators.MaType signalMaType, Filter filter, long from, long to) throws JFException {
/* 2368 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MACDEXT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(fastMaType.ordinal()), Integer.valueOf(slowPeriod), Integer.valueOf(slowMaType.ordinal()), Integer.valueOf(signalPeriod), Integer.valueOf(signalMaType.ordinal()) }, filter, from, to);
/* 2369 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[] macdFix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int signalPeriod, int shift) throws JFException {
/* 2373 */     Object[] ret = function(instrument, period, side, "MACDFIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(signalPeriod) }, shift);
/* 2374 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] macdFix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int signalPeriod, long from, long to) throws JFException {
/* 2378 */     Object[] ret = function(instrument, period, side, "MACDFIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(signalPeriod) }, from, to);
/* 2379 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] macdFix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int signalPeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2383 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MACDFIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(signalPeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2384 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] macdFix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int signalPeriod, Filter filter, long from, long to) throws JFException {
/* 2388 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MACDFIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(signalPeriod) }, filter, from, to);
/* 2389 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[] maEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, int shift) throws JFException {
/* 2393 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, shift);
/* 2394 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] maEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, long from, long to) throws JFException {
/* 2398 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, from, to);
/* 2399 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] maEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2403 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2404 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] maEnvelope(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double deviation, Filter filter, long from, long to) throws JFException {
/* 2408 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MAEnvelope", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, filter, from, to);
/* 2409 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[] mama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, double fastLimit, double slowLimit, int shift) throws JFException {
/* 2413 */     Object[] ret = function(instrument, period, side, "MAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Double.valueOf(fastLimit), Double.valueOf(slowLimit) }, shift);
/* 2414 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] mama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, double fastLimit, double slowLimit, long from, long to) throws JFException {
/* 2418 */     Object[] ret = function(instrument, period, side, "MAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Double.valueOf(fastLimit), Double.valueOf(slowLimit) }, from, to);
/* 2419 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] mama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, double fastLimit, double slowLimit, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2423 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Double.valueOf(fastLimit), Double.valueOf(slowLimit) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2424 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] mama(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, double fastLimit, double slowLimit, Filter filter, long from, long to) throws JFException {
/* 2428 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MAMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Double.valueOf(fastLimit), Double.valueOf(slowLimit) }, filter, from, to);
/* 2429 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public Object[] wsmTime(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 2433 */     return function(instrument, period, side, "WSMTIME", null, null, shift);
/*      */   }
/*      */ 
/*      */   public Object[] wsmTime(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 2437 */     return function(instrument, period, side, "WSMTIME", null, null, from, to);
/*      */   }
/*      */ 
/*      */   public Object[] wsmTime(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2441 */     return calculateIndicator(instrument, period, new OfferSide[] { side }, "WSMTIME", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*      */   }
/*      */ 
/*      */   public Object[] wsmTime(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 2445 */     return calculateIndicator(instrument, period, new OfferSide[] { side }, "WSMTIME", null, null, filter, from, to);
/*      */   }
/*      */ 
/*      */   public double mavp(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int minPeriod, int maxPeriod, IIndicators.MaType maType, int shift) throws JFException {
/* 2449 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MAVP", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(minPeriod), Integer.valueOf(maxPeriod), Integer.valueOf(maType.ordinal()) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] mavp(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int minPeriod, int maxPeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/* 2453 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MAVP", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(minPeriod), Integer.valueOf(maxPeriod), Integer.valueOf(maType.ordinal()) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] mavp(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int minPeriod, int maxPeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2457 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MAVP", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(minPeriod), Integer.valueOf(maxPeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] mavp(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int minPeriod, int maxPeriod, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/* 2461 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MAVP", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, new Object[] { Integer.valueOf(minPeriod), Integer.valueOf(maxPeriod), Integer.valueOf(maType.ordinal()) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double max(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2465 */     return ((Double)function(instrument, period, side, "MAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] max(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2469 */     return (double[])(double[])function(instrument, period, side, "MAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] max(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2473 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] max(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2477 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int maxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2481 */     return ((Integer)function(instrument, period, side, "MAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] maxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2485 */     return (int[])(int[])function(instrument, period, side, "MAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] maxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2489 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] maxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2493 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double medPrice(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 2497 */     return ((Double)function(instrument, period, side, "MEDPRICE", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] medPrice(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 2501 */     return (double[])(double[])function(instrument, period, side, "MEDPRICE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] medPrice(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2505 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MEDPRICE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] medPrice(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 2509 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MEDPRICE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double mfi(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2513 */     return ((Double)function(instrument, period, side, "MFI", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] mfi(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2517 */     return (double[])(double[])function(instrument, period, side, "MFI", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] mfi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2521 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MFI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] mfi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2525 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MFI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double midPoint(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2529 */     return ((Double)function(instrument, period, side, "MIDPOINT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] midPoint(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2533 */     return (double[])(double[])function(instrument, period, side, "MIDPOINT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] midPoint(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2537 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MIDPOINT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] midPoint(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2541 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MIDPOINT", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double midPrice(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2545 */     return ((Double)function(instrument, period, side, "MIDPRICE", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] midPrice(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2549 */     return (double[])(double[])function(instrument, period, side, "MIDPRICE", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] midPrice(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2553 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MIDPRICE", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] midPrice(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2557 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MIDPRICE", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double min(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2561 */     return ((Double)function(instrument, period, side, "MIN", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] min(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2565 */     return (double[])(double[])function(instrument, period, side, "MIN", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] min(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2569 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MIN", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] min(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2573 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MIN", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int minIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2577 */     return ((Integer)function(instrument, period, side, "MININDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).intValue();
/*      */   }
/*      */ 
/*      */   public int[] minIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2581 */     return (int[])(int[])function(instrument, period, side, "MININDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public int[] minIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2585 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MININDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public int[] minIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2589 */     return (int[])(int[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MININDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] minMax(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2593 */     Object[] ret = function(instrument, period, side, "MINMAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 2594 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] minMax(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2598 */     Object[] ret = function(instrument, period, side, "MINMAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 2599 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] minMax(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2603 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MINMAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2604 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] minMax(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2608 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MINMAX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 2609 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public int[] minMaxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2613 */     Object[] ret = function(instrument, period, side, "MINMAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 2614 */     return new int[] { ((Integer)ret[0]).intValue(), ((Integer)ret[1]).intValue() };
/*      */   }
/*      */ 
/*      */   public int[][] minMaxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2618 */     Object[] ret = function(instrument, period, side, "MINMAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 2619 */     return new int[][] { (int[])(int[])ret[0], (int[])(int[])ret[1] };
/*      */   }
/*      */ 
/*      */   public int[][] minMaxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2623 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MINMAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2624 */     return new int[][] { (int[])(int[])ret[0], (int[])(int[])ret[1] };
/*      */   }
/*      */ 
/*      */   public int[][] minMaxIndex(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2628 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MINMAXINDEX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 2629 */     return new int[][] { (int[])(int[])ret[0], (int[])(int[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double minusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2633 */     return ((Double)function(instrument, period, side, "MINUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] minusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2637 */     return (double[])(double[])function(instrument, period, side, "MINUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] minusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2641 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MINUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] minusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2645 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MINUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double minusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2649 */     return ((Double)function(instrument, period, side, "MINUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] minusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2653 */     return (double[])(double[])function(instrument, period, side, "MINUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] minusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2657 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MINUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] minusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2661 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MINUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double mom(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2665 */     return ((Double)function(instrument, period, side, "MOM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] mom(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2669 */     return (double[])(double[])function(instrument, period, side, "MOM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] mom(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2673 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MOM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] mom(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2677 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "MOM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double mult(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int shift) throws JFException
/*      */   {
/* 2682 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MULT", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] mult(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, long from, long to) throws JFException
/*      */   {
/* 2687 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MULT", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] mult(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2692 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MULT", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] mult(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, long from, long to) throws JFException
/*      */   {
/* 2697 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "MULT", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] murrey(Instrument instrument, Period period, OfferSide side, int nPeriod, int timePeriod, int stepBack, int shift) throws JFException {
/* 2701 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MURRCH", null, new Object[] { Integer.valueOf(nPeriod), Integer.valueOf(timePeriod), Integer.valueOf(stepBack) }, shift);
/* 2702 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue(), ((Double)ret[6]).doubleValue(), ((Double)ret[7]).doubleValue(), ((Double)ret[8]).doubleValue(), ((Double)ret[9]).doubleValue(), ((Double)ret[10]).doubleValue(), ((Double)ret[11]).doubleValue(), ((Double)ret[12]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] murrey(Instrument instrument, Period period, OfferSide side, int nPeriod, int timePeriod, int stepBack, long from, long to) throws JFException {
/* 2706 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MURRCH", null, new Object[] { Integer.valueOf(nPeriod), Integer.valueOf(timePeriod), Integer.valueOf(stepBack) }, from, to);
/* 2707 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6], (double[])(double[])ret[7], (double[])(double[])ret[8], (double[])(double[])ret[9], (double[])(double[])ret[10], (double[])(double[])ret[11], (double[])(double[])ret[12] };
/*      */   }
/*      */ 
/*      */   public double[][] murrey(Instrument instrument, Period period, OfferSide side, int nPeriod, int timePeriod, int stepBack, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2711 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MURRCH", null, new Object[] { Integer.valueOf(nPeriod), Integer.valueOf(timePeriod), Integer.valueOf(stepBack) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2712 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6], (double[])(double[])ret[7], (double[])(double[])ret[8], (double[])(double[])ret[9], (double[])(double[])ret[10], (double[])(double[])ret[11], (double[])(double[])ret[12] };
/*      */   }
/*      */ 
/*      */   public double[][] murrey(Instrument instrument, Period period, OfferSide side, int nPeriod, int timePeriod, int stepBack, Filter filter, long from, long to) throws JFException {
/* 2716 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "MURRCH", null, new Object[] { Integer.valueOf(nPeriod), Integer.valueOf(timePeriod), Integer.valueOf(stepBack) }, filter, from, to);
/* 2717 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6], (double[])(double[])ret[7], (double[])(double[])ret[8], (double[])(double[])ret[9], (double[])(double[])ret[10], (double[])(double[])ret[11], (double[])(double[])ret[12] };
/*      */   }
/*      */ 
/*      */   public double natr(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2721 */     return ((Double)function(instrument, period, side, "NATR", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] natr(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2725 */     return (double[])(double[])function(instrument, period, side, "NATR", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] natr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2729 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "NATR", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] natr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2733 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "NATR", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double obv(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, OfferSide sideForPriceV, int shift) throws JFException
/*      */   {
/* 2738 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side, sideForPriceV }, "OBV", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] obv(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, OfferSide sideForPriceV, long from, long to) throws JFException
/*      */   {
/* 2743 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side, sideForPriceV }, "OBV", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] obv(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, OfferSide sideForPriceV, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2748 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side, sideForPriceV }, "OBV", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] obv(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, OfferSide sideForPriceV, Filter filter, long from, long to) throws JFException {
/* 2752 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side, sideForPriceV }, "OBV", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double osma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fast_ema_period, int slow_ema_period, int signal_period, int shift) throws JFException {
/* 2756 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "OsMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fast_ema_period), Integer.valueOf(slow_ema_period), Integer.valueOf(signal_period) }, shift);
/* 2757 */     return ((Double)ret[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] osma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fast_ema_period, int slow_ema_period, int signal_period, long from, long to) throws JFException {
/* 2761 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "OsMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fast_ema_period), Integer.valueOf(slow_ema_period), Integer.valueOf(signal_period) }, from, to);
/* 2762 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] osma(Instrument instrument, Period period, OfferSide side, int fast_ema_period, int slow_ema_period, int signal_period, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 2767 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "OsMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fast_ema_period), Integer.valueOf(slow_ema_period), Integer.valueOf(signal_period) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2768 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] osma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fast_ema_period, int slow_ema_period, int signal_period, Filter filter, long from, long to) throws JFException {
/* 2772 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "OsMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fast_ema_period), Integer.valueOf(slow_ema_period), Integer.valueOf(signal_period) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, boolean showHistoricalLevels, int shift) throws JFException {
/* 2776 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(showHistoricalLevels) }, shift);
/* 2777 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue(), ((Double)ret[6]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, boolean showHistoricalLevels, long from, long to) throws JFException {
/* 2781 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(showHistoricalLevels) }, from, to);
/* 2782 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, boolean showHistoricalLevels, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2786 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(showHistoricalLevels) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2787 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, boolean showHistoricalLevels, Filter filter, long from, long to) throws JFException {
/* 2791 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(showHistoricalLevels) }, filter, from, to);
/* 2792 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2796 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(true) }, shift);
/* 2797 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue(), ((Double)ret[6]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2801 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(true) }, from, to);
/* 2802 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2806 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(true) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 2807 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] pivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2811 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "PIVOT", null, new Object[] { Integer.valueOf(timePeriod), Boolean.valueOf(true) }, filter, from, to);
/* 2812 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double plusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2816 */     return ((Double)function(instrument, period, side, "PLUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] plusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2820 */     return (double[])(double[])function(instrument, period, side, "PLUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] plusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2824 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PLUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] plusDi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2828 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PLUS_DI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double plusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2832 */     return ((Double)function(instrument, period, side, "PLUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] plusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2836 */     return (double[])(double[])function(instrument, period, side, "PLUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] plusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2840 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PLUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] plusDm(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2844 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PLUS_DM", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ppo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, int shift) throws JFException {
/* 2848 */     return ((Double)function(instrument, period, side, "PPO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ppo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/* 2852 */     return (double[])(double[])function(instrument, period, side, "PPO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ppo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2856 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PPO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ppo(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, Filter filter, long from, long to) throws JFException {
/* 2860 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PPO", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double prchannel(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, int shift) throws JFException {
/* 2864 */     return ((Double)function(instrument, period, side, "PCHANNEL", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] prchannel(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, long from, long to) throws JFException {
/* 2868 */     return (double[])(double[])function(instrument, period, side, "PCHANNEL", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] prchannel(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int fastPeriod, int slowPeriod, IIndicators.MaType maType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2872 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PCHANNEL", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(fastPeriod), Integer.valueOf(slowPeriod), Integer.valueOf(maType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double prchannel(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 2876 */     return ((Double)function(instrument, period, side, "PCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] prchannel(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 2880 */     return (double[])(double[])function(instrument, period, side, "PCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] prchannel(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2884 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] prchannel(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2888 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "PCHANNEL", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double rci(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2892 */     return ((Double)function(instrument, period, side, "RCI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] rci(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2896 */     return (double[])(double[])function(instrument, period, side, "RCI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] rci(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2900 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "RCI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] rci(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2904 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "RCI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double rmi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int momentumPeriod, int shift) throws JFException {
/* 2908 */     return ((Double)function(instrument, period, side, "RMI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(momentumPeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] rmi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int momentumPeriod, long from, long to) throws JFException {
/* 2912 */     return (double[])(double[])function(instrument, period, side, "RMI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(momentumPeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] rmi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int momentumPeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2916 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "RMI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(momentumPeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] rmi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int momentumPeriod, Filter filter, long from, long to) throws JFException {
/* 2920 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "RMI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(momentumPeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double roc(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2924 */     return ((Double)function(instrument, period, side, "ROC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] roc(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2928 */     return (double[])(double[])function(instrument, period, side, "ROC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] roc(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2932 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] roc(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2936 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROC", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double rocp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2940 */     return ((Double)function(instrument, period, side, "ROCP", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] rocp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2944 */     return (double[])(double[])function(instrument, period, side, "ROCP", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] rocp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2948 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROCP", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] rocp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2952 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROCP", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double rocr(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2956 */     return ((Double)function(instrument, period, side, "ROCR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] rocr(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2960 */     return (double[])(double[])function(instrument, period, side, "ROCR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] rocr(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2964 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROCR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] rocr(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2968 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROCR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double rocr100(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2972 */     return ((Double)function(instrument, period, side, "ROCR100", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] rocr100(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2976 */     return (double[])(double[])function(instrument, period, side, "ROCR100", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] rocr100(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2980 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROCR100", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] rocr100(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 2984 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ROCR100", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double rsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 2988 */     return ((Double)function(instrument, period, side, "RSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] rsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 2992 */     return (double[])(double[])function(instrument, period, side, "RSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] rsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 2996 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "RSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] rsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3000 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "RSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] rvi(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 3004 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "RVI", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 3005 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] rvi(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 3009 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "RVI", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 3010 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] rvi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3014 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "RVI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3015 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] rvi(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3019 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "RVI", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 3020 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double sar(Instrument instrument, Period period, OfferSide side, double acceleration, double maximum, int shift) throws JFException {
/* 3024 */     double value = sarExt(instrument, period, side, 0.0D, 0.0D, acceleration, acceleration, maximum, acceleration, acceleration, maximum, shift);
/* 3025 */     if (value < 0.0D) {
/* 3026 */       return -value;
/*      */     }
/* 3028 */     return value;
/*      */   }
/*      */ 
/*      */   public double[] sar(Instrument instrument, Period period, OfferSide side, double acceleration, double maximum, long from, long to) throws JFException
/*      */   {
/* 3033 */     double[] values = sarExt(instrument, period, side, 0.0D, 0.0D, acceleration, acceleration, maximum, acceleration, acceleration, maximum, from, to);
/* 3034 */     for (int i = 0; i < values.length; i++) {
/* 3035 */       double value = values[i];
/* 3036 */       if (value < 0.0D) {
/* 3037 */         values[i] = (-value);
/*      */       }
/*      */     }
/* 3040 */     return values;
/*      */   }
/*      */ 
/*      */   public double[] sar(Instrument instrument, Period period, OfferSide side, double acceleration, double maximum, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3044 */     double[] values = sarExt(instrument, period, side, 0.0D, 0.0D, acceleration, acceleration, maximum, acceleration, acceleration, maximum, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3045 */     for (int i = 0; i < values.length; i++) {
/* 3046 */       double value = values[i];
/* 3047 */       if (value < 0.0D) {
/* 3048 */         values[i] = (-value);
/*      */       }
/*      */     }
/* 3051 */     return values;
/*      */   }
/*      */ 
/*      */   public double sarExt(Instrument instrument, Period period, OfferSide side, double startValue, double offsetOnReverse, double accelerationInitLong, double accelerationLong, double accelerationMaxLong, double accelerationInitShort, double accelerationShort, double accelerationMaxShort, int shift) throws JFException {
/*      */     try {
/* 3056 */       checkNotTick(period);
/* 3057 */       checkSide(period, new OfferSide[] { side });
/*      */ 
/* 3059 */       IndicatorHolder indicatorHolder = getCachedIndicator("SAREXT");
/*      */       try {
/* 3061 */         IIndicator indicator = indicatorHolder.getIndicator();
/* 3062 */         setOptParams(indicator, new Object[] { Double.valueOf(startValue), Double.valueOf(offsetOnReverse), Double.valueOf(accelerationInitLong), Double.valueOf(accelerationLong), Double.valueOf(accelerationMaxLong), Double.valueOf(accelerationInitShort), Double.valueOf(accelerationShort), Double.valueOf(accelerationMaxShort) });
/*      */         int indicatorLookback;
/* 3066 */         int lookback = indicatorLookback = indicator.getLookback();
/* 3067 */         boolean hasPeriodChange = false;
/* 3068 */         int previousLength = 0;
/* 3069 */         double[] doubles = new double[0];
/* 3070 */         IndicatorResult result = null;
/*      */         do
/*      */         {
/* 3073 */           lookback += (lookback * 4 < 100 ? 100 : lookback * 4);
/* 3074 */           inputData = (double[][])(double[][])getInputData(instrument, period, side, InputParameterInfo.Type.PRICE, null, shift, lookback, 0);
/* 3075 */           if (inputData == null) {
/* 3076 */             double d = (0.0D / 0.0D);
/*      */             return d;
/*      */           }
/* 3078 */           int length = inputData[0].length;
/* 3079 */           if (length <= previousLength)
/*      */           {
/*      */             break;
/*      */           }
/* 3083 */           previousLength = length;
/* 3084 */           indicator.setInputParameter(0, inputData);
/* 3085 */           doubles = new double[length - indicatorLookback];
/* 3086 */           indicator.setOutputParameter(0, doubles);
/* 3087 */           result = indicator.calculate(0, length - 1);
/* 3088 */           if (result.getNumberOfElements() == 0)
/*      */           {
/*      */             break;
/*      */           }
/* 3092 */           boolean positive = doubles[0] > 0.0D;
/* 3093 */           for (int i = 1; i < result.getNumberOfElements(); i++)
/* 3094 */             if (doubles[i] > 0.0D != positive) {
/* 3095 */               hasPeriodChange = true;
/* 3096 */               break;
/*      */             }
/*      */         }
/* 3099 */         while (!hasPeriodChange);
/* 3100 */         double[][] inputData = doubles[(result.getNumberOfElements() - 1)];
/*      */         return inputData; } finally { cacheIndicator("SAREXT", indicatorHolder); }
/*      */     }
/*      */     catch (JFException e) {
/* 3105 */       throw e;
/*      */     } catch (TaLibException e) {
/* 3107 */       Throwable t = e.getCause();
/* 3108 */       throw new JFException(t);
/*      */     } catch (RuntimeException e) {
/* 3110 */       throw e; } catch (Exception e) {
/*      */     }
/* 3112 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public double[] sarExt(Instrument instrument, Period period, OfferSide side, double startValue, double offsetOnReverse, double accelerationInitLong, double accelerationLong, double accelerationMaxLong, double accelerationInitShort, double accelerationShort, double accelerationMaxShort, long from, long to) throws JFException
/*      */   {
/*      */     try {
/* 3118 */       checkSide(period, new OfferSide[] { side });
/* 3119 */       checkIntervalValid(period, from, to);
/*      */ 
/* 3121 */       IndicatorHolder indicatorHolder = getCachedIndicator("SAREXT");
/*      */       try {
/* 3123 */         IIndicator indicator = indicatorHolder.getIndicator();
/* 3124 */         setOptParams(indicator, new Object[] { Double.valueOf(startValue), Double.valueOf(offsetOnReverse), Double.valueOf(accelerationInitLong), Double.valueOf(accelerationLong), Double.valueOf(accelerationMaxLong), Double.valueOf(accelerationInitShort), Double.valueOf(accelerationShort), Double.valueOf(accelerationMaxShort) });
/*      */         int indicatorLookback;
/* 3128 */         int lookback = indicatorLookback = indicator.getLookback();
/* 3129 */         boolean hasPeriodChange = false;
/* 3130 */         int previousLength = 0;
/* 3131 */         double[] doubles = new double[0];
/* 3132 */         IndicatorResult result = null;
/*      */         do
/*      */         {
/* 3135 */           lookback += (lookback * 4 < 100 ? 100 : lookback * 4);
/* 3136 */           double[][] inputData = (double[][])(double[][])getInputData(instrument, period, side, InputParameterInfo.Type.PRICE, null, from, to, lookback, 0, false, 0);
/* 3137 */           int length = inputData[0].length;
/* 3138 */           if (length <= previousLength)
/*      */           {
/*      */             break;
/*      */           }
/* 3142 */           previousLength = length;
/* 3143 */           indicator.setInputParameter(0, inputData);
/* 3144 */           doubles = new double[length - indicatorLookback];
/* 3145 */           indicator.setOutputParameter(0, doubles);
/* 3146 */           result = indicator.calculate(0, length - 1);
/* 3147 */           if (result.getNumberOfElements() == 0)
/*      */           {
/*      */             break;
/*      */           }
/* 3151 */           positive = doubles[0] > 0.0D;
/* 3152 */           for (int i = 1; i < result.getNumberOfElements(); i++)
/* 3153 */             if (doubles[i] > 0.0D != positive) {
/* 3154 */               hasPeriodChange = true;
/* 3155 */               break;
/*      */             }
/*      */         }
/* 3158 */         while (!hasPeriodChange);
/*      */ 
/* 3160 */         int outCount = DataCacheUtils.getCandlesCountBetweenFast(period == Period.TICK ? Period.ONE_SEC : period, from, to) > result.getNumberOfElements() ? result.getNumberOfElements() : DataCacheUtils.getCandlesCountBetweenFast(period == Period.TICK ? Period.ONE_SEC : period, from, to);
/*      */ 
/* 3162 */         double[] retDoubles = new double[outCount];
/* 3163 */         System.arraycopy(doubles, result.getNumberOfElements() - outCount, retDoubles, 0, outCount);
/* 3164 */         boolean positive = retDoubles;
/*      */         return positive; } finally { cacheIndicator("SAREXT", indicatorHolder); }
/*      */     }
/*      */     catch (JFException e) {
/* 3169 */       throw e;
/*      */     } catch (TaLibException e) {
/* 3171 */       Throwable t = e.getCause();
/* 3172 */       throw new JFException(t);
/*      */     } catch (RuntimeException e) {
/* 3174 */       throw e; } catch (Exception e) {
/*      */     }
/* 3176 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public double[] sarExt(Instrument instrument, Period period, OfferSide side, double startValue, double offsetOnReverse, double accelerationInitLong, double accelerationLong, double accelerationMaxLong, double accelerationInitShort, double accelerationShort, double accelerationMaxShort, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter)
/*      */     throws JFException
/*      */   {
/*      */     try
/*      */     {
/* 3186 */       checkNotTick(period);
/* 3187 */       checkSide(period, side);
/* 3188 */       checkIntervalValid(numberOfCandlesBefore, numberOfCandlesAfter);
/*      */ 
/* 3190 */       IndicatorHolder indicatorHolder = getCachedIndicator("SAREXT");
/*      */       try {
/* 3192 */         IIndicator indicator = indicatorHolder.getIndicator();
/* 3193 */         setOptParams(indicator, new Object[] { Double.valueOf(startValue), Double.valueOf(offsetOnReverse), Double.valueOf(accelerationInitLong), Double.valueOf(accelerationLong), Double.valueOf(accelerationMaxLong), Double.valueOf(accelerationInitShort), Double.valueOf(accelerationShort), Double.valueOf(accelerationMaxShort) });
/*      */         int indicatorLookback;
/* 3197 */         int lookback = indicatorLookback = indicator.getLookback();
/* 3198 */         boolean hasPeriodChange = false;
/* 3199 */         int previousLength = 0;
/* 3200 */         double[] doubles = new double[0];
/* 3201 */         IndicatorResult result = null;
/*      */         do
/*      */         {
/* 3204 */           lookback += (lookback * 4 < 100 ? 100 : lookback * 4);
/* 3205 */           double[][] inputData = (double[][])(double[][])getInputData(instrument, period, side, InputParameterInfo.Type.PRICE, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter, lookback, 0, 0);
/* 3206 */           int length = inputData[0].length;
/* 3207 */           if (length <= previousLength)
/*      */           {
/*      */             break;
/*      */           }
/* 3211 */           previousLength = length;
/* 3212 */           indicator.setInputParameter(0, inputData);
/* 3213 */           doubles = new double[length - indicatorLookback];
/* 3214 */           indicator.setOutputParameter(0, doubles);
/* 3215 */           result = indicator.calculate(0, length - 1);
/* 3216 */           if (result.getNumberOfElements() == 0)
/*      */           {
/*      */             break;
/*      */           }
/* 3220 */           positive = doubles[0] > 0.0D;
/* 3221 */           for (int i = 1; i < result.getNumberOfElements(); i++)
/* 3222 */             if (doubles[i] > 0.0D != positive) {
/* 3223 */               hasPeriodChange = true;
/* 3224 */               break;
/*      */             }
/*      */         }
/* 3227 */         while (!hasPeriodChange);
/*      */ 
/* 3229 */         int outCount = numberOfCandlesBefore + numberOfCandlesAfter > result.getNumberOfElements() ? result.getNumberOfElements() : numberOfCandlesBefore + numberOfCandlesAfter;
/*      */ 
/* 3231 */         double[] retDoubles = new double[outCount];
/* 3232 */         System.arraycopy(doubles, result.getNumberOfElements() - outCount, retDoubles, 0, outCount);
/* 3233 */         boolean positive = retDoubles;
/*      */         return positive; } finally { cacheIndicator("SAREXT", indicatorHolder); }
/*      */     }
/*      */     catch (JFException e) {
/* 3238 */       throw e;
/*      */     } catch (TaLibException e) {
/* 3240 */       Throwable t = e.getCause();
/* 3241 */       throw new JFException(t);
/*      */     } catch (RuntimeException e) {
/* 3243 */       throw e; } catch (Exception e) {
/*      */     }
/* 3245 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public double sin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException
/*      */   {
/* 3250 */     return ((Double)function(instrument, period, side, "SIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] sin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 3254 */     return (double[])(double[])function(instrument, period, side, "SIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] sin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3258 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] sin(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 3262 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SIN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double sinh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 3266 */     return ((Double)function(instrument, period, side, "SINH", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] sinh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 3270 */     return (double[])(double[])function(instrument, period, side, "SINH", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] sinh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 3275 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SINH", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] sinh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 3279 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SINH", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double sma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3283 */     return ((Double)function(instrument, period, side, "SMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] sma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3287 */     return (double[])(double[])function(instrument, period, side, "SMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] sma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3291 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] sma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3295 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] smi(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, int slowDPeriod, int smoothingPeriod, int shift) throws JFException {
/* 3299 */     Object[] ret = function(instrument, period, side, "SMI", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowDPeriod), Integer.valueOf(smoothingPeriod) }, shift);
/* 3300 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] smi(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, int slowDPeriod, int smoothingPeriod, long from, long to) throws JFException {
/* 3304 */     Object[] ret = function(instrument, period, side, "SMI", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowDPeriod), Integer.valueOf(smoothingPeriod) }, from, to);
/* 3305 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] smi(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, int slowDPeriod, int smoothingPeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3309 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "SMI", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowDPeriod), Integer.valueOf(smoothingPeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3310 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] smi(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, int slowDPeriod, int smoothingPeriod, Filter filter, long from, long to) throws JFException {
/* 3314 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "SMI", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowDPeriod), Integer.valueOf(smoothingPeriod) }, filter, from, to);
/* 3315 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double smma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3319 */     return ((Double)function(instrument, period, side, "SMMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] smma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3323 */     return (double[])(double[])function(instrument, period, side, "SMMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] smma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3327 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SMMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] smma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3331 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SMMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double sqrt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 3335 */     return ((Double)function(instrument, period, side, "SQRT", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] sqrt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 3339 */     return (double[])(double[])function(instrument, period, side, "SQRT", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] sqrt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 3344 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SQRT", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] sqrt(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 3348 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SQRT", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double stdDev(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, int shift) throws JFException {
/* 3352 */     return ((Double)function(instrument, period, side, "STDDEV", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] stdDev(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, long from, long to) throws JFException {
/* 3356 */     return (double[])(double[])function(instrument, period, side, "STDDEV", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] stdDev(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3360 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "STDDEV", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] stdDev(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, Filter filter, long from, long to) throws JFException {
/* 3364 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "STDDEV", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] stoch(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, IIndicators.MaType slowKMaType, int slowDPeriod, IIndicators.MaType slowDMaType, int shift) throws JFException {
/* 3368 */     Object[] ret = function(instrument, period, side, "STOCH", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowKMaType.ordinal()), Integer.valueOf(slowDPeriod), Integer.valueOf(slowDMaType.ordinal()) }, shift);
/* 3369 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] stoch(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, IIndicators.MaType slowKMaType, int slowDPeriod, IIndicators.MaType slowDMaType, long from, long to) throws JFException {
/* 3373 */     Object[] ret = function(instrument, period, side, "STOCH", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowKMaType.ordinal()), Integer.valueOf(slowDPeriod), Integer.valueOf(slowDMaType.ordinal()) }, from, to);
/* 3374 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] stoch(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, IIndicators.MaType slowKMaType, int slowDPeriod, IIndicators.MaType slowDMaType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3378 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "STOCH", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowKMaType.ordinal()), Integer.valueOf(slowDPeriod), Integer.valueOf(slowDMaType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3379 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] stoch(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int slowKPeriod, IIndicators.MaType slowKMaType, int slowDPeriod, IIndicators.MaType slowDMaType, Filter filter, long from, long to) throws JFException {
/* 3383 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "STOCH", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(slowKPeriod), Integer.valueOf(slowKMaType.ordinal()), Integer.valueOf(slowDPeriod), Integer.valueOf(slowDMaType.ordinal()) }, filter, from, to);
/* 3384 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[] stochF(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, int shift) throws JFException {
/* 3388 */     Object[] ret = function(instrument, period, side, "STOCHF", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, shift);
/* 3389 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] stochF(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, long from, long to) throws JFException {
/* 3393 */     Object[] ret = function(instrument, period, side, "STOCHF", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, from, to);
/* 3394 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] stochF(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3398 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "STOCHF", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3399 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] stochF(Instrument instrument, Period period, OfferSide side, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, Filter filter, long from, long to) throws JFException {
/* 3403 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "STOCHF", null, new Object[] { Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, filter, from, to);
/* 3404 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[] stochRsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, int shift) throws JFException {
/* 3408 */     Object[] ret = function(instrument, period, side, "STOCHRSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, shift);
/* 3409 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] stochRsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, long from, long to) throws JFException {
/* 3413 */     Object[] ret = function(instrument, period, side, "STOCHRSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, from, to);
/* 3414 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] stochRsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3418 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "STOCHRSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3419 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] stochRsi(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int fastKPeriod, int fastDPeriod, IIndicators.MaType fastDMaType, Filter filter, long from, long to) throws JFException {
/* 3423 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "STOCHRSI", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Integer.valueOf(fastKPeriod), Integer.valueOf(fastDPeriod), Integer.valueOf(fastDMaType.ordinal()) }, filter, from, to);
/* 3424 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double sub(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, int shift) throws JFException {
/* 3428 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "SUB", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] sub(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, long from, long to) throws JFException {
/* 3432 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "SUB", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] sub(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3436 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "SUB", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] sub(Instrument instrument, Period period, OfferSide side1, IIndicators.AppliedPrice appliedPrice1, OfferSide side2, IIndicators.AppliedPrice appliedPrice2, Filter filter, long from, long to) throws JFException {
/* 3440 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side1, side2 }, "SUB", new IIndicators.AppliedPrice[] { appliedPrice1, appliedPrice2 }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double sum(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3444 */     return ((Double)function(instrument, period, side, "SUM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] sum(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3448 */     return (double[])(double[])function(instrument, period, side, "SUM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] sum(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3452 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SUM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] sum(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3456 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "SUM", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] supportResistance(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 3460 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "S&R", null, null, shift);
/* 3461 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] supportResistance(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 3465 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "S&R", null, null, from, to);
/* 3466 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] supportResistance(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3470 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "S&R", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3471 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double[][] supportResistance(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 3475 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "S&R", null, null, filter, from, to);
/* 3476 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double t3(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double factor, int shift) throws JFException
/*      */   {
/* 3481 */     return ((Double)function(instrument, period, side, "T3", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(factor) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] t3(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double factor, long from, long to) throws JFException {
/* 3485 */     return (double[])(double[])function(instrument, period, side, "T3", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(factor) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] t3(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double factor, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 3490 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "T3", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(factor) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] t3(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double factor, Filter filter, long from, long to) throws JFException {
/* 3494 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "T3", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(factor) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double tan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 3498 */     return ((Double)function(instrument, period, side, "TAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] tan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 3502 */     return (double[])(double[])function(instrument, period, side, "TAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] tan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 3507 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] tan(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 3511 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TAN", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double tanh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 3515 */     return ((Double)function(instrument, period, side, "TANH", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] tanh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, long from, long to) throws JFException {
/* 3519 */     return (double[])(double[])function(instrument, period, side, "TANH", new IIndicators.AppliedPrice[] { appliedPrice }, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] tanh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 3524 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TANH", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] tanh(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to) throws JFException {
/* 3528 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TANH", new IIndicators.AppliedPrice[] { appliedPrice }, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] tbp(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 3532 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "THRUSTBAR", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift);
/* 3533 */     return new double[] { ((Integer)ret[0]).intValue(), ((Integer)ret[1]).intValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[] tbop(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int shift) throws JFException {
/* 3537 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "THRUSTOUTSIDEBAR", new IIndicators.AppliedPrice[] { appliedPrice }, null, shift);
/* 3538 */     return new double[] { ((Integer)ret[0]).intValue(), ((Integer)ret[1]).intValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[] td_i(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 3542 */     Object[] ret = function(instrument, period, side, "TD_I", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 3543 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] td_i(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 3547 */     Object[] ret = function(instrument, period, side, "TD_I", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 3548 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] td_i(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3552 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "TD_I", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3553 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public double[][] td_i(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3557 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "TD_I", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 3558 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2] };
/*      */   }
/*      */ 
/*      */   public int[] td_s(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3562 */     Object[] ret = function(instrument, period, side, "TD_S", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 3563 */     return new int[] { ((Integer)ret[0]).intValue(), ((Integer)ret[1]).intValue() };
/*      */   }
/*      */ 
/*      */   public int[][] td_s(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3567 */     Object[] ret = function(instrument, period, side, "TD_S", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 3568 */     return new int[][] { (int[])(int[])ret[0], (int[])(int[])ret[1] };
/*      */   }
/*      */ 
/*      */   public int[][] td_s(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3572 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "TD_S", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3573 */     return new int[][] { (int[])(int[])ret[0], (int[])(int[])ret[1] };
/*      */   }
/*      */ 
/*      */   public int[][] td_s(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3577 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "TD_S", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 3578 */     return new int[][] { (int[])(int[])ret[0], (int[])(int[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double tema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3582 */     return ((Double)function(instrument, period, side, "TEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] tema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3586 */     return (double[])(double[])function(instrument, period, side, "TEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] tema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3590 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] tema(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3594 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TEMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double trange(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 3598 */     return ((Double)function(instrument, period, side, "TRANGE", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] trange(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 3602 */     return (double[])(double[])function(instrument, period, side, "TRANGE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] trange(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3606 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TRANGE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] trange(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 3610 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TRANGE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] trendEnv(Instrument instrument, Period period, OfferSide side, int timePeriod, double deviation, int shift) throws JFException {
/* 3614 */     Object[] res = function(instrument, period, side, "TrendEnvelopes", null, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, shift);
/* 3615 */     return new double[] { ((Double)res[0]).doubleValue(), ((Double)res[1]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] trendEnv(Instrument instrument, Period period, OfferSide side, int timePeriod, double deviation, long from, long to) throws JFException {
/* 3619 */     Object[] res = function(instrument, period, side, "TrendEnvelopes", null, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, from, to);
/* 3620 */     return new double[][] { (double[])(double[])res[0], (double[])(double[])res[1] };
/*      */   }
/*      */ 
/*      */   public double[][] trendEnv(Instrument instrument, Period period, OfferSide side, int timePeriod, double deviation, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3624 */     Object[] res = calculateIndicator(instrument, period, new OfferSide[] { side }, "TrendEnvelopes", null, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3625 */     return new double[][] { (double[])(double[])res[0], (double[])(double[])res[1] };
/*      */   }
/*      */ 
/*      */   public double[][] trendEnv(Instrument instrument, Period period, OfferSide side, int timePeriod, double deviation, Filter filter, long from, long to) throws JFException {
/* 3629 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "TrendEnvelopes", null, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(deviation) }, filter, from, to);
/* 3630 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1] };
/*      */   }
/*      */ 
/*      */   public double trima(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException
/*      */   {
/* 3635 */     return ((Double)function(instrument, period, side, "TRIMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] trima(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3639 */     return (double[])(double[])function(instrument, period, side, "TRIMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] trima(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/* 3644 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TRIMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] trima(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3648 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TRIMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double trix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3652 */     return ((Double)function(instrument, period, side, "TRIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] trix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3656 */     return (double[])(double[])function(instrument, period, side, "TRIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] trix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3660 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TRIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] trix(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3664 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TRIX", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double tsf(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3668 */     return ((Double)function(instrument, period, side, "TSF", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] tsf(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3672 */     return (double[])(double[])function(instrument, period, side, "TSF", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] tsf(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3676 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TSF", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] tsf(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3680 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TSF", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double tvs(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3684 */     return ((Double)function(instrument, period, side, "TVS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] tvs(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3688 */     return (double[])(double[])function(instrument, period, side, "TVS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] tvs(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3692 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TVS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] tvs(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3696 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TVS", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double typPrice(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 3700 */     return ((Double)function(instrument, period, side, "TYPPRICE", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] typPrice(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 3704 */     return (double[])(double[])function(instrument, period, side, "TYPPRICE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] typPrice(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3708 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TYPPRICE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] typPrice(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 3712 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "TYPPRICE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double ultOsc(Instrument instrument, Period period, OfferSide side, int timePeriod1, int timePeriod2, int timePeriod3, int shift) throws JFException {
/* 3716 */     return ((Double)function(instrument, period, side, "ULTOSC", null, new Object[] { Integer.valueOf(timePeriod1), Integer.valueOf(timePeriod2), Integer.valueOf(timePeriod3) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] ultOsc(Instrument instrument, Period period, OfferSide side, int timePeriod1, int timePeriod2, int timePeriod3, long from, long to) throws JFException {
/* 3720 */     return (double[])(double[])function(instrument, period, side, "ULTOSC", null, new Object[] { Integer.valueOf(timePeriod1), Integer.valueOf(timePeriod2), Integer.valueOf(timePeriod3) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] ultOsc(Instrument instrument, Period period, OfferSide side, int timePeriod1, int timePeriod2, int timePeriod3, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3724 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ULTOSC", null, new Object[] { Integer.valueOf(timePeriod1), Integer.valueOf(timePeriod2), Integer.valueOf(timePeriod3) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] ultOsc(Instrument instrument, Period period, OfferSide side, int timePeriod1, int timePeriod2, int timePeriod3, Filter filter, long from, long to) throws JFException {
/* 3728 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ULTOSC", null, new Object[] { Integer.valueOf(timePeriod1), Integer.valueOf(timePeriod2), Integer.valueOf(timePeriod3) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double var(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, int shift) throws JFException {
/* 3732 */     return ((Double)function(instrument, period, side, "VAR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] var(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, long from, long to) throws JFException {
/* 3736 */     return (double[])(double[])function(instrument, period, side, "VAR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] var(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3740 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "VAR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] var(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, double nbDev, Filter filter, long from, long to) throws JFException {
/* 3744 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "VAR", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod), Double.valueOf(nbDev) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double volume(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 3748 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "Volume", null, null, shift);
/* 3749 */     return ((Double)ret[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] volume(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 3753 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "Volume", null, null, from, to);
/* 3754 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] volume(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3758 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side }, "Volume", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3759 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] volume(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 3763 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "Volume", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double volumeWAP(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3767 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "VolumeWAP", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 3768 */     return ((Double)ret[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] volumeWAP(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3772 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "VolumeWAP", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 3773 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] volumeWAP(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3777 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "VolumeWAP", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3778 */     return (double[])(double[])ret[0];
/*      */   }
/*      */ 
/*      */   public double[] volumeWAP(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3782 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side, side }, "VolumeWAP", new IIndicators.AppliedPrice[] { appliedPrice, appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double waddahAttar(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 3786 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side }, "WADDAHAT", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] waddahAttar(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 3790 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WADDAHAT", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] waddahAttar(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3794 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WADDAHAT", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] waddahAttar(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 3798 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WADDAHAT", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double wclPrice(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/* 3802 */     return ((Double)function(instrument, period, side, "WCLPRICE", null, null, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] wclPrice(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException {
/* 3806 */     return (double[])(double[])function(instrument, period, side, "WCLPRICE", null, null, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] wclPrice(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3810 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WCLPRICE", null, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] wclPrice(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException {
/* 3814 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WCLPRICE", null, null, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double willr(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 3818 */     return ((Double)function(instrument, period, side, "WILLR", null, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] willr(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 3822 */     return (double[])(double[])function(instrument, period, side, "WILLR", null, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] willr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3826 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WILLR", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] willr(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3830 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WILLR", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double wma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, int shift) throws JFException {
/* 3834 */     return ((Double)function(instrument, period, side, "WMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] wma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, long from, long to) throws JFException {
/* 3838 */     return (double[])(double[])function(instrument, period, side, "WMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] wma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3842 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] wma(Instrument instrument, Period period, OfferSide side, IIndicators.AppliedPrice appliedPrice, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3846 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "WMA", new IIndicators.AppliedPrice[] { appliedPrice }, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] woodPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, int shift) throws JFException {
/* 3850 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "WOODPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, shift);
/* 3851 */     return new double[] { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue(), ((Double)ret[2]).doubleValue(), ((Double)ret[3]).doubleValue(), ((Double)ret[4]).doubleValue(), ((Double)ret[5]).doubleValue(), ((Double)ret[6]).doubleValue() };
/*      */   }
/*      */ 
/*      */   public double[][] woodPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, long from, long to) throws JFException {
/* 3855 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "WOODPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, from, to);
/* 3856 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] woodPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3860 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "WOODPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/* 3861 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double[][] woodPivot(Instrument instrument, Period period, OfferSide side, int timePeriod, Filter filter, long from, long to) throws JFException {
/* 3865 */     Object[] ret = calculateIndicator(instrument, period, new OfferSide[] { side, side }, "WOODPIVOT", null, new Object[] { Integer.valueOf(timePeriod) }, filter, from, to);
/* 3866 */     return new double[][] { (double[])(double[])ret[0], (double[])(double[])ret[1], (double[])(double[])ret[2], (double[])(double[])ret[3], (double[])(double[])ret[4], (double[])(double[])ret[5], (double[])(double[])ret[6] };
/*      */   }
/*      */ 
/*      */   public double zigzag(Instrument instrument, Period period, OfferSide side, int extDepth, int extDeviation, int extBackstep, int shift) throws JFException {
/* 3870 */     return ((Double)calculateIndicator(instrument, period, new OfferSide[] { side }, "ZIGZAG", null, new Object[] { Integer.valueOf(extDepth), Integer.valueOf(extDeviation), Integer.valueOf(extBackstep) }, shift)[0]).doubleValue();
/*      */   }
/*      */ 
/*      */   public double[] zigzag(Instrument instrument, Period period, OfferSide side, int extDepth, int extDeviation, int extBackstep, long from, long to) throws JFException {
/* 3874 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ZIGZAG", null, new Object[] { Integer.valueOf(extDepth), Integer.valueOf(extDeviation), Integer.valueOf(extBackstep) }, from, to)[0];
/*      */   }
/*      */ 
/*      */   public double[] zigzag(Instrument instrument, Period period, OfferSide side, int extDepth, int extDeviation, int extBackstep, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException {
/* 3878 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ZIGZAG", null, new Object[] { Integer.valueOf(extDepth), Integer.valueOf(extDeviation), Integer.valueOf(extBackstep) }, filter, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */   }
/*      */ 
/*      */   public double[] zigzag(Instrument instrument, Period period, OfferSide side, int extDepth, int extDeviation, int extBackstep, Filter filter, long from, long to) throws JFException {
/* 3882 */     return (double[])(double[])calculateIndicator(instrument, period, new OfferSide[] { side }, "ZIGZAG", null, new Object[] { Integer.valueOf(extDepth), Integer.valueOf(extDeviation), Integer.valueOf(extBackstep) }, filter, from, to)[0];
/*      */   }
/*      */ 
/*      */   protected IndicatorHolder getCachedIndicator(String name)
/*      */     throws JFException
/*      */   {
/* 3888 */     IndicatorHolder indicatorHolder = (IndicatorHolder)this.cachedIndicators.remove(name);
/* 3889 */     if (indicatorHolder == null) {
/* 3890 */       indicatorHolder = getCustomIndicatorHolder(name);
/* 3891 */       if (indicatorHolder == null) {
/* 3892 */         indicatorHolder = IndicatorsProvider.getInstance().getIndicatorHolder(name);
/*      */       }
/*      */     }
/* 3895 */     return indicatorHolder;
/*      */   }
/*      */ 
/*      */   private IIndicator getCustomIndicator(String customIndicatorName) throws JFException {
/* 3899 */     if (this.customIndicators.containsKey(customIndicatorName)) {
/* 3900 */       IIndicator custom = null;
/*      */       try {
/* 3902 */         custom = (IIndicator)((Class)this.customIndicators.get(customIndicatorName)).newInstance();
/*      */       } catch (Throwable th) {
/* 3904 */         throw new JFException("Error while creating custom indicator [" + customIndicatorName + "] instance");
/*      */       }
/*      */       try {
/* 3907 */         custom.onStart(IndicatorHelper.createIndicatorContext());
/* 3908 */         return custom;
/*      */       } catch (Throwable th) {
/* 3910 */         throw new JFException("Exception in onStart method");
/*      */       }
/*      */     }
/* 3913 */     return null;
/*      */   }
/*      */ 
/*      */   private IndicatorHolder getCustomIndicatorHolder(String customIndicatorName) throws JFException {
/* 3917 */     IIndicator indicator = getCustomIndicator(customIndicatorName);
/* 3918 */     if (indicator != null) {
/* 3919 */       return new IndicatorHolder(indicator, IndicatorHelper.createIndicatorContext());
/*      */     }
/*      */ 
/* 3925 */     return null;
/*      */   }
/*      */ 
/*      */   protected void cacheIndicator(String name, IndicatorHolder indicator) {
/* 3929 */     this.cachedIndicators.put(name, indicator);
/*      */   }
/*      */ 
/*      */   private Object[] function(Instrument instrument, Period period, OfferSide side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, int shift) throws JFException
/*      */   {
/* 3934 */     return calculateIndicator(instrument, period, new OfferSide[] { side }, functionName, inputTypes, optParams, shift);
/*      */   }
/*      */ 
/*      */   public Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, int shift) throws JFException
/*      */   {
/*      */     try {
/* 3940 */       IndicatorHolder indicatorHolder = getCachedIndicator(functionName);
/*      */       try {
/* 3942 */         checkNotTick(period);
/* 3943 */         checkSide(period, side);
/* 3944 */         checkShiftPositive(shift);
/* 3945 */         if (indicatorHolder == null) {
/* 3946 */           throw new JFException("Indicator with name " + functionName + " was not found");
/*      */         }
/* 3948 */         int[] lookSides = calculateLookbackLookforward(indicatorHolder.getIndicator(), optParams);
/* 3949 */         Object[] arrayOfObject = calculateIndicator(instrument, period, side, inputTypes, shift, indicatorHolder, lookSides[0], lookSides[1], lookSides[2]);
/*      */         return arrayOfObject;
/*      */       }
/*      */       finally
/*      */       {
/* 3951 */         if (indicatorHolder != null) {
/* 3952 */           indicatorHolder.getIndicatorContext().resetFeedDescriptor();
/* 3953 */           cacheIndicator(functionName, indicatorHolder);
/*      */         }
/*      */       }
/*      */     } catch (JFException e) {
/* 3957 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 3959 */       throw e; } catch (Exception e) {
/*      */     }
/* 3961 */     throw new JFException(e);
/*      */   }
/*      */   protected int[] calculateLookbackLookforward(IIndicator indicator, Object[] optParams) throws JFException {
/* 3966 */     setOptParams(indicator, optParams);
/*      */     int indicatorLookback;
/*      */     int lookback;
/*      */     try { lookback = indicatorLookback = indicator.getLookback();
/*      */     } catch (Throwable t) {
/* 3972 */       LOGGER.error(t.getMessage(), t);
/* 3973 */       String error = StrategyWrapper.representError(indicator, t);
/* 3974 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 3975 */       throw new JFException(t);
/*      */     }int lookforward;
/*      */     try {
/* 3979 */       lookforward = indicator.getLookforward();
/*      */     } catch (AbstractMethodError e) {
/* 3981 */       lookforward = 0;
/*      */     } catch (Throwable t) {
/* 3983 */       LOGGER.error(t.getMessage(), t);
/* 3984 */       String error = StrategyWrapper.representError(indicator, t);
/* 3985 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 3986 */       throw new JFException(t);
/*      */     }
/* 3988 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 3989 */     if (indicatorInfo.isUnstablePeriod())
/*      */     {
/* 3991 */       lookback += (lookback * 4 < 100 ? 100 : lookback * 4);
/*      */     }
/* 3993 */     return new int[] { indicatorLookback, lookback, lookforward };
/*      */   }
/*      */ 
/*      */   private FeedDescriptor createFeedDescriptor(Period period, Instrument instrument, OfferSide offerSide) {
/* 3997 */     FeedDescriptor feedDescriptor = new FeedDescriptor();
/*      */ 
/* 3999 */     if (Period.TICK.equals(period)) {
/* 4000 */       feedDescriptor.setDataType(DataType.TICKS);
/*      */     }
/*      */     else {
/* 4003 */       feedDescriptor.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/*      */ 
/* 4006 */     feedDescriptor.setInstrument(instrument);
/* 4007 */     feedDescriptor.setPeriod(period);
/* 4008 */     feedDescriptor.setOfferSide(offerSide);
/*      */ 
/* 4010 */     return feedDescriptor;
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, IIndicators.AppliedPrice[] inputTypes, int shift, IndicatorHolder indicatorHolder, int indicatorLookback, int lookback, int lookforward) throws JFException, DataCacheException {
/* 4014 */     int inputLength = -2147483648;
/* 4015 */     IIndicator indicator = indicatorHolder.getIndicator();
/* 4016 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 4017 */     IndicatorContext indicatorContext = indicatorHolder.getIndicatorContext();
/* 4018 */     FeedDescriptor feedDescriptor = createFeedDescriptor(period, instrument, indicatorInfo.getNumberOfInputs() > 0 ? side[0] : OfferSide.BID);
/* 4019 */     indicatorContext.setFeedDescriptor(feedDescriptor);
/*      */ 
/* 4021 */     int i = 0; for (int j = indicatorInfo.getNumberOfInputs(); i < j; i++) {
/* 4022 */       InputParameterInfo info = indicator.getInputParameterInfo(i);
/* 4023 */       Instrument currentInstrument = info.getInstrument() == null ? instrument : info.getInstrument();
/* 4024 */       Period currentPeriod = info.getPeriod() == null ? period : info.getPeriod();
/* 4025 */       OfferSide currentSide = info.getOfferSide() == null ? side[i] : info.getOfferSide();
/*      */       Object inputData;
/*      */       Object inputData;
/* 4027 */       if ((info.getInstrument() == null) && (info.getPeriod() == null)) {
/* 4028 */         inputData = getInputData(currentInstrument, currentPeriod, currentSide, info.getType(), info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null, shift, lookback, lookforward);
/*      */       }
/*      */       else {
/* 4031 */         if (period.getInterval() > currentPeriod.getInterval()) {
/* 4032 */           throw new JFException("Indicator [" + indicatorInfo.getName() + "] cannot be run with periods longer than " + currentPeriod);
/*      */         }
/* 4034 */         int currentShift = calculateShift(instrument, currentPeriod, period, shift);
/*      */ 
/* 4036 */         int currentLookforward = currentShift >= lookforward ? lookforward : currentShift < 0 ? 0 : currentShift;
/* 4037 */         inputData = getInputData(currentInstrument, currentPeriod, currentSide, info.getType(), info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null, currentShift, lookback, currentLookforward);
/*      */       }
/*      */ 
/* 4040 */       if (inputData == null)
/*      */       {
/* 4042 */         Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4043 */         int k = 0; for (int l = indicatorInfo.getNumberOfOutputs(); k < l; k++) {
/* 4044 */           OutputParameterInfo outInfo = indicator.getOutputParameterInfo(k);
/* 4045 */           switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outInfo.getType().ordinal()]) {
/*      */           case 1:
/* 4047 */             ret[k] = Double.valueOf((0.0D / 0.0D));
/* 4048 */             break;
/*      */           case 2:
/* 4050 */             ret[k] = Integer.valueOf(-2147483648);
/* 4051 */             break;
/*      */           case 3:
/* 4053 */             ret[k] = null;
/*      */           }
/*      */         }
/*      */ 
/* 4057 */         return ret;
/*      */       }
/* 4059 */       if ((info.getInstrument() == null) && (info.getPeriod() == null)) {
/* 4060 */         if (info.getType() == InputParameterInfo.Type.PRICE)
/* 4061 */           inputLength = ((double[][])(double[][])inputData)[0].length;
/* 4062 */         else if (info.getType() == InputParameterInfo.Type.DOUBLE)
/* 4063 */           inputLength = ((double[])(double[])inputData).length;
/* 4064 */         else if (info.getType() == InputParameterInfo.Type.BAR)
/* 4065 */           inputLength = ((IBar[])(IBar[])inputData).length;
/*      */       }
/*      */       try
/*      */       {
/* 4069 */         indicator.setInputParameter(i, inputData);
/*      */       } catch (Throwable t) {
/* 4071 */         LOGGER.error(t.getMessage(), t);
/* 4072 */         String error = StrategyWrapper.representError(indicator, t);
/* 4073 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4074 */         throw new JFException(t);
/*      */       }
/*      */     }
/* 4077 */     if (inputLength == -2147483648) {
/* 4078 */       if (indicatorInfo.getNumberOfInputs() == 0) {
/* 4079 */         inputLength = 0;
/*      */       } else {
/* 4081 */         Object inputData = getInputData(instrument, period, OfferSide.BID, InputParameterInfo.Type.BAR, null, shift, lookback, lookforward);
/* 4082 */         inputLength = ((IBar[])(IBar[])inputData).length;
/*      */       }
/*      */     }
/* 4085 */     return calculateIndicator(indicator, indicatorLookback, lookback, lookforward, inputLength);
/*      */   }
/*      */ 
/*      */   protected int calculateShift(Instrument instrument, Period currentPeriod, Period period, int shift) throws JFException {
/* 4089 */     if (period == currentPeriod) {
/* 4090 */       return shift;
/*      */     }
/* 4092 */     if (shift == 0) {
/* 4093 */       return 0;
/*      */     }
/* 4095 */     long currentBarStartTime = this.history.getStartTimeOfCurrentBar(instrument, period);
/* 4096 */     long requestedBarStartTime = DataCacheUtils.getTimeForNCandlesBackFast(period, DataCacheUtils.getPreviousCandleStartFast(period, currentBarStartTime), shift);
/* 4097 */     currentBarStartTime = this.history.getStartTimeOfCurrentBar(instrument, currentPeriod);
/* 4098 */     requestedBarStartTime = DataCacheUtils.getCandleStartFast(currentPeriod, requestedBarStartTime);
/* 4099 */     if (requestedBarStartTime <= currentBarStartTime) {
/* 4100 */       return DataCacheUtils.getCandlesCountBetweenFast(currentPeriod, requestedBarStartTime, currentBarStartTime) - 1;
/*      */     }
/* 4102 */     return -(DataCacheUtils.getCandlesCountBetweenFast(currentPeriod, currentBarStartTime, requestedBarStartTime) - 1);
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(IIndicator indicator, int indicatorLookback, int lookback, int lookforward, int inputLength)
/*      */     throws JFException
/*      */   {
/* 4108 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*      */ 
/* 4110 */     if (inputLength <= indicatorLookback + lookforward) {
/* 4111 */       LOGGER.warn("There is no enough data to calculate value");
/* 4112 */       Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4113 */       int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4114 */         OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 4115 */         switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */         case 1:
/* 4117 */           ret[i] = Double.valueOf((0.0D / 0.0D));
/* 4118 */           break;
/*      */         case 2:
/* 4120 */           ret[i] = Integer.valueOf(-2147483648);
/* 4121 */           break;
/*      */         case 3:
/* 4123 */           ret[i] = null;
/*      */         }
/*      */       }
/*      */ 
/* 4127 */       return ret;
/*      */     }
/*      */ 
/* 4130 */     Object[] outputs = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4131 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4132 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 4133 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 4135 */         double[] doubles = new double[inputLength - (indicatorLookback + lookforward)];
/* 4136 */         outputs[i] = doubles;
/*      */         try {
/* 4138 */           indicator.setOutputParameter(i, doubles);
/*      */         } catch (Throwable t) {
/* 4140 */           LOGGER.error(t.getMessage(), t);
/* 4141 */           String error = StrategyWrapper.representError(indicator, t);
/* 4142 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4143 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 2:
/* 4147 */         int[] ints = new int[inputLength - (indicatorLookback + lookforward)];
/* 4148 */         outputs[i] = ints;
/*      */         try {
/* 4150 */           indicator.setOutputParameter(i, ints);
/*      */         } catch (Throwable t) {
/* 4152 */           LOGGER.error(t.getMessage(), t);
/* 4153 */           String error = StrategyWrapper.representError(indicator, t);
/* 4154 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4155 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 3:
/* 4159 */         Object[] objects = new Object[inputLength - (indicatorLookback + lookforward)];
/* 4160 */         outputs[i] = objects;
/*      */         try {
/* 4162 */           indicator.setOutputParameter(i, objects);
/*      */         } catch (Throwable t) {
/* 4164 */           LOGGER.error(t.getMessage(), t);
/* 4165 */           String error = StrategyWrapper.representError(indicator, t);
/* 4166 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4167 */           throw new JFException(t);
/*      */         }
/*      */       }
/*      */     }
/*      */     IndicatorResult result;
/*      */     try {
/* 4174 */       result = indicator.calculate(0, inputLength - 1);
/*      */     } catch (TaLibException e) {
/* 4176 */       Throwable t = e.getCause();
/* 4177 */       LOGGER.error(t.getMessage(), t);
/* 4178 */       String error = StrategyWrapper.representError(indicator, t);
/* 4179 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4180 */       throw new JFException(t);
/*      */     } catch (Throwable t) {
/* 4182 */       LOGGER.error(t.getMessage(), t);
/* 4183 */       String error = StrategyWrapper.representError(indicator, t);
/* 4184 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4185 */       throw new JFException(t);
/*      */     }
/* 4187 */     if ((result.getLastValueIndex() == -2147483648) && (result.getNumberOfElements() != 0)) {
/* 4188 */       if (lookforward != 0) {
/* 4189 */         String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned result without lastValueIndex set. This is only allowed when lookforward is equals to zero";
/*      */ 
/* 4191 */         LOGGER.error(error);
/* 4192 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 4193 */         throw new JFException(error);
/*      */       }
/* 4195 */       result.setLastValueIndex(inputLength - 1);
/*      */     }
/*      */ 
/* 4198 */     if ((result.getLastValueIndex() + 1 - result.getFirstValueIndex() < inputLength - lookback - lookforward) || (result.getNumberOfElements() < inputLength - lookback - lookforward)) {
/* 4199 */       String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned less values than expected";
/* 4200 */       LOGGER.error(error);
/* 4201 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 4202 */       throw new JFException(error);
/*      */     }
/* 4204 */     Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4205 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4206 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 4207 */       if (result.getNumberOfElements() == 0) {
/* 4208 */         throw new JFException("Indicator didn't return any value");
/*      */       }
/* 4210 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 4212 */         double[] doubles = (double[])(double[])outputs[i];
/* 4213 */         ret[i] = Double.valueOf(doubles[(result.getNumberOfElements() - 1)]);
/* 4214 */         break;
/*      */       case 2:
/* 4216 */         int[] ints = (int[])(int[])outputs[i];
/* 4217 */         ret[i] = Integer.valueOf(ints[(result.getNumberOfElements() - 1)]);
/* 4218 */         break;
/*      */       case 3:
/* 4220 */         Object[] objects = (Object[])(Object[])outputs[i];
/* 4221 */         ret[i] = objects[(result.getNumberOfElements() - 1)];
/*      */       }
/*      */     }
/*      */ 
/* 4225 */     return ret;
/*      */   }
/*      */ 
/*      */   private Object[] function(Instrument instrument, Period period, OfferSide side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, long from, long to) throws JFException
/*      */   {
/* 4230 */     return calculateIndicator(instrument, period, new OfferSide[] { side }, functionName, inputTypes, optParams, from, to);
/*      */   }
/*      */ 
/*      */   public Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, long from, long to) throws JFException
/*      */   {
/*      */     try {
/* 4236 */       checkSide(period, side);
/* 4237 */       checkIntervalValid(period, from, to);
/* 4238 */       IndicatorHolder indicatorHolder = getCachedIndicator(functionName);
/* 4239 */       if (indicatorHolder == null)
/* 4240 */         throw new JFException("Indicator with name " + functionName + " was not found");
/*      */       try
/*      */       {
/* 4243 */         setOptParams(indicatorHolder.getIndicator(), optParams);
/* 4244 */         int[] lookSides = calculateLookbackLookforward(indicatorHolder.getIndicator(), optParams);
/* 4245 */         Object[] arrayOfObject = calculateIndicator(instrument, period, side, inputTypes, from, to, indicatorHolder, lookSides[0], lookSides[1], lookSides[2]);
/*      */         return arrayOfObject;
/*      */       }
/*      */       finally
/*      */       {
/* 4247 */         indicatorHolder.getIndicatorContext().resetFeedDescriptor();
/* 4248 */         cacheIndicator(functionName, indicatorHolder);
/*      */       }
/*      */     } catch (JFException e) {
/* 4251 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 4253 */       throw e; } catch (Exception e) {
/*      */     }
/* 4255 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, IIndicators.AppliedPrice[] inputTypes, long from, long to, IndicatorHolder indicatorHolder, int indicatorLookback, int lookback, int lookforward)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 4262 */     IIndicator indicator = indicatorHolder.getIndicator();
/* 4263 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 4264 */     IndicatorContext indicatorContext = indicatorHolder.getIndicatorContext();
/* 4265 */     indicatorContext.setChartInfo(instrument, period, indicatorInfo.getNumberOfInputs() > 0 ? side[0] : OfferSide.BID);
/* 4266 */     int length = -2147483648;
/* 4267 */     int i = 0; for (int j = indicatorInfo.getNumberOfInputs(); i < j; i++) {
/* 4268 */       InputParameterInfo info = indicator.getInputParameterInfo(i);
/* 4269 */       OfferSide currentSide = info.getOfferSide() == null ? side[i] : info.getOfferSide();
/* 4270 */       Period currentPeriod = info.getPeriod() == null ? period : info.getPeriod();
/* 4271 */       Instrument currentInstrument = info.getInstrument() == null ? instrument : info.getInstrument();
/* 4272 */       if (period.getInterval() > currentPeriod.getInterval()) {
/* 4273 */         throw new JFException("Indicator [" + indicatorInfo.getName() + "] cannot be run with periods longer than " + currentPeriod);
/*      */       }
/* 4275 */       int flats = 0;
/* 4276 */       long currentTo = to;
/* 4277 */       boolean addCurrentCandle = false;
/* 4278 */       if (instrument != currentInstrument) {
/* 4279 */         long lastCandleTime = DataCacheUtils.getPreviousCandleStartFast(period, this.history.getStartTimeOfCurrentBar(instrument, period));
/* 4280 */         if (currentTo > lastCandleTime) {
/* 4281 */           currentTo = lastCandleTime;
/*      */         }
/* 4283 */         currentTo = DataCacheUtils.getCandleStartFast(currentPeriod, currentTo);
/* 4284 */         long lastFormedCandleTime = DataCacheUtils.getPreviousCandleStartFast(currentPeriod, this.history.getStartTimeOfCurrentBar(currentInstrument, currentPeriod));
/* 4285 */         if (currentTo > lastFormedCandleTime) {
/* 4286 */           addCurrentCandle = true;
/*      */         }
/* 4288 */         lastCandleTime = this.history.getStartTimeOfCurrentBar(currentInstrument, currentPeriod);
/* 4289 */         if (currentTo > lastCandleTime) {
/* 4290 */           flats = DataCacheUtils.getCandlesCountBetweenFast(currentPeriod, lastCandleTime, currentTo) - 1;
/*      */         }
/* 4292 */         if (currentTo > lastFormedCandleTime) {
/* 4293 */           currentTo = lastFormedCandleTime;
/*      */         }
/*      */       }
/* 4296 */       Object inputData = getInputData(currentInstrument, currentPeriod, currentSide, info.getType(), info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null, from, currentTo, lookback, lookforward, addCurrentCandle, flats);
/*      */ 
/* 4299 */       if ((info.getInstrument() == null) && (info.getPeriod() == null)) {
/* 4300 */         if (info.getType() == InputParameterInfo.Type.PRICE)
/* 4301 */           length = ((double[][])(double[][])inputData)[0].length;
/* 4302 */         else if (info.getType() == InputParameterInfo.Type.DOUBLE)
/* 4303 */           length = ((double[])(double[])inputData).length;
/* 4304 */         else if (info.getType() == InputParameterInfo.Type.BAR)
/* 4305 */           length = ((IBar[])(IBar[])inputData).length;
/*      */       }
/*      */       try
/*      */       {
/* 4309 */         indicator.setInputParameter(i, inputData);
/*      */       } catch (Throwable t) {
/* 4311 */         LOGGER.error(t.getMessage(), t);
/* 4312 */         String error = StrategyWrapper.representError(indicator, t);
/* 4313 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4314 */         throw new JFException(t);
/*      */       }
/*      */     }
/* 4317 */     if (length == -2147483648) {
/* 4318 */       if (indicatorInfo.getNumberOfInputs() == 0) {
/* 4319 */         length = 0;
/*      */       } else {
/* 4321 */         Object inputData = getInputData(instrument, period, OfferSide.BID, InputParameterInfo.Type.BAR, null, from, to, lookback, lookforward, false, 0);
/*      */ 
/* 4323 */         length = ((IBar[])(IBar[])inputData).length;
/*      */       }
/*      */     }
/* 4326 */     return calculateIndicator(period, from, to, indicator, indicatorLookback, lookback, lookforward, length);
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(Period period, long from, long to, IIndicator indicator, int indicatorLookback, int lookback, int lookforward, int inputLength) throws JFException {
/* 4330 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*      */ 
/* 4332 */     if (inputLength <= indicatorLookback + lookforward) {
/* 4333 */       LOGGER.warn("There is no enough data to calculate values");
/* 4334 */       Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4335 */       int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4336 */         OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 4337 */         switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */         case 1:
/* 4339 */           ret[i] = new double[0];
/* 4340 */           break;
/*      */         case 2:
/* 4342 */           ret[i] = new int[0];
/* 4343 */           break;
/*      */         case 3:
/* 4345 */           ret[i] = new Object[0];
/*      */         }
/*      */       }
/*      */ 
/* 4349 */       return ret;
/*      */     }
/*      */ 
/* 4352 */     Object[] outputs = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4353 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4354 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 4355 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 4357 */         double[] doubles = new double[inputLength - (indicatorLookback + lookforward)];
/* 4358 */         outputs[i] = doubles;
/*      */         try {
/* 4360 */           indicator.setOutputParameter(i, doubles);
/*      */         } catch (Throwable t) {
/* 4362 */           LOGGER.error(t.getMessage(), t);
/* 4363 */           String error = StrategyWrapper.representError(indicator, t);
/* 4364 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4365 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 2:
/* 4369 */         int[] ints = new int[inputLength - (indicatorLookback + lookforward)];
/* 4370 */         outputs[i] = ints;
/*      */         try {
/* 4372 */           indicator.setOutputParameter(i, ints);
/*      */         } catch (Throwable t) {
/* 4374 */           LOGGER.error(t.getMessage(), t);
/* 4375 */           String error = StrategyWrapper.representError(indicator, t);
/* 4376 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4377 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 3:
/* 4381 */         Object[] objects = new Object[inputLength - (indicatorLookback + lookforward)];
/* 4382 */         outputs[i] = objects;
/*      */         try {
/* 4384 */           indicator.setOutputParameter(i, objects);
/*      */         } catch (Throwable t) {
/* 4386 */           LOGGER.error(t.getMessage(), t);
/* 4387 */           String error = StrategyWrapper.representError(indicator, t);
/* 4388 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4389 */           throw new JFException(t);
/*      */         }
/*      */       }
/*      */     }
/*      */     IndicatorResult result;
/*      */     try {
/* 4396 */       result = indicator.calculate(0, inputLength - 1);
/*      */     } catch (TaLibException e) {
/* 4398 */       Throwable t = e.getCause();
/* 4399 */       LOGGER.error(t.getMessage(), t);
/* 4400 */       String error = StrategyWrapper.representError(indicator, t);
/* 4401 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4402 */       throw new JFException(t);
/*      */     } catch (Throwable t) {
/* 4404 */       LOGGER.error(t.getMessage(), t);
/* 4405 */       String error = StrategyWrapper.representError(indicator, t);
/* 4406 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4407 */       throw new JFException(t);
/*      */     }
/* 4409 */     if ((result.getLastValueIndex() == -2147483648) && (result.getNumberOfElements() != 0)) {
/* 4410 */       if (lookforward != 0) {
/* 4411 */         String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned result without lastValueIndex set. This is only allowed when lookforward is equals to zero";
/*      */ 
/* 4413 */         LOGGER.error(error);
/* 4414 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 4415 */         throw new JFException(error);
/*      */       }
/* 4417 */       result.setLastValueIndex(inputLength - 1);
/*      */     }
/*      */ 
/* 4420 */     if ((result.getLastValueIndex() + 1 - result.getFirstValueIndex() < inputLength - lookback - lookforward) || (result.getNumberOfElements() < inputLength - lookback - lookforward)) {
/* 4421 */       String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned less values than expected";
/* 4422 */       LOGGER.error(error);
/* 4423 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 4424 */       throw new JFException(error);
/*      */     }
/* 4426 */     Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4427 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4428 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/*      */       int outCount;
/*      */       int outCount;
/* 4431 */       if (period != null) {
/* 4432 */         outCount = DataCacheUtils.getCandlesCountBetweenFast(period == Period.TICK ? Period.ONE_SEC : period, from, to) > result.getNumberOfElements() ? result.getNumberOfElements() : DataCacheUtils.getCandlesCountBetweenFast(period == Period.TICK ? Period.ONE_SEC : period, from, to);
/*      */       }
/*      */       else
/*      */       {
/* 4436 */         outCount = inputLength - lookback - lookforward;
/*      */       }
/*      */ 
/* 4439 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 4441 */         double[] doubles = (double[])(double[])outputs[i];
/* 4442 */         double[] retDoubles = new double[outCount];
/* 4443 */         System.arraycopy(doubles, result.getNumberOfElements() - outCount, retDoubles, 0, outCount);
/* 4444 */         ret[i] = retDoubles;
/* 4445 */         break;
/*      */       case 2:
/* 4447 */         int[] ints = (int[])(int[])outputs[i];
/* 4448 */         int[] retInts = new int[outCount];
/* 4449 */         System.arraycopy(ints, result.getNumberOfElements() - outCount, retInts, 0, outCount);
/* 4450 */         ret[i] = retInts;
/* 4451 */         break;
/*      */       case 3:
/* 4453 */         Object[] objects = (Object[])(Object[])outputs[i];
/* 4454 */         Object[] retObjs = new Object[outCount];
/* 4455 */         System.arraycopy(objects, result.getNumberOfElements() - outCount, retObjs, 0, outCount);
/* 4456 */         ret[i] = retObjs;
/*      */       }
/*      */     }
/*      */ 
/* 4460 */     return ret;
/*      */   }
/*      */ 
/*      */   public Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter)
/*      */     throws JFException
/*      */   {
/* 4476 */     RateDataIndicatorCalculationResultWraper wraper = calculateIndicatorReturnSourceData(instrument, period, side, functionName, inputTypes, optParams, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*      */ 
/* 4488 */     return wraper.getIndicatorCalculationResult();
/*      */   }
/*      */ 
/*      */   public static int calculateLookback(IIndicator indicator) {
/* 4492 */     return calculateLookback(indicator, indicator.getLookback());
/*      */   }
/*      */ 
/*      */   private static int calculateLookback(IIndicator indicator, int initialLookback) {
/* 4496 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 4497 */     if (indicatorInfo.isUnstablePeriod())
/*      */     {
/* 4499 */       initialLookback += (initialLookback * 4 < 100 ? 100 : initialLookback * 4);
/*      */     }
/* 4501 */     return initialLookback;
/*      */   }
/*      */ 
/*      */   public RateDataIndicatorCalculationResultWraper calculateIndicatorReturnSourceData(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter)
/*      */     throws JFException
/*      */   {
/* 4533 */     RateDataIndicatorCalculationResultWraper calculationResultWraper = new RateDataIndicatorCalculationResultWraper();
/*      */     try
/*      */     {
/* 4536 */       checkNotTick(period);
/* 4537 */       checkSide(period, side);
/* 4538 */       checkIntervalValid(numberOfCandlesBefore, numberOfCandlesAfter);
/* 4539 */       IndicatorHolder indicatorHolder = getCachedIndicator(functionName);
/*      */       try {
/* 4541 */         if (indicatorHolder == null) {
/* 4542 */           throw new JFException("Indicator with name " + functionName + " was not found");
/*      */         }
/* 4544 */         IIndicator indicator = indicatorHolder.getIndicator();
/* 4545 */         IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*      */ 
/* 4547 */         int[] lookBackLookForward = calculateLookbackLookforward(indicator, optParams);
/* 4548 */         int lookBack = lookBackLookForward[1];
/* 4549 */         int lookforward = lookBackLookForward[2];
/* 4550 */         int indicatorLookback = lookBackLookForward[0];
/*      */ 
/* 4552 */         IndicatorContext indicatorContext = indicatorHolder.getIndicatorContext();
/* 4553 */         indicatorContext.setChartInfo(instrument, period, indicatorInfo.getNumberOfInputs() > 0 ? side[0] : OfferSide.BID);
/* 4554 */         int length = -2147483648;
/* 4555 */         int i = 0; for (int j = indicatorInfo.getNumberOfInputs(); i < j; i++) {
/* 4556 */           InputParameterInfo info = indicator.getInputParameterInfo(i);
/*      */           Object inputData;
/*      */           List sourceBars;
/*      */           Object inputData;
/* 4559 */           if ((info.getInstrument() == null) && (info.getPeriod() == null)) {
/* 4560 */             InputParameterInfo.Type inputType = info.getType();
/* 4561 */             IIndicators.AppliedPrice appliedPrice = info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null;
/*      */ 
/* 4563 */             List sourceBars = getInputBars(instrument, period, side[i], inputType, appliedPrice, filter, numberOfCandlesBefore, time, numberOfCandlesAfter, lookBack, lookforward, 0);
/* 4564 */             inputData = barsToReal(sourceBars, inputType, appliedPrice);
/*      */           } else {
/* 4566 */             OfferSide currentSide = info.getOfferSide() == null ? side[i] : info.getOfferSide();
/* 4567 */             Instrument currentInstrument = info.getInstrument() == null ? instrument : info.getInstrument();
/* 4568 */             Period currentPeriod = info.getPeriod() == null ? period : info.getPeriod();
/* 4569 */             if (period.getInterval() > currentPeriod.getInterval())
/* 4570 */               throw new JFException("Indicator [" + indicatorInfo.getName() + "] cannot be run with periods longer than " + currentPeriod);
/*      */             List sourceBars;
/* 4572 */             if (currentPeriod != period)
/*      */             {
/* 4574 */               IBar[] bars = (IBar[])(IBar[])getInputData(instrument, period, side[i], InputParameterInfo.Type.BAR, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter, 0, 0, 0);
/*      */               Object inputData;
/* 4575 */               if ((bars != null) && (bars.length > 0)) {
/* 4576 */                 long currentFrom = DataCacheUtils.getCandleStartFast(currentPeriod, bars[0].getTime());
/* 4577 */                 long currentTo = DataCacheUtils.getCandleStartFast(currentPeriod, bars[(bars.length - 1)].getTime());
/* 4578 */                 int before = DataCacheUtils.getCandlesCountBetweenFast(currentPeriod, currentFrom, currentTo);
/* 4579 */                 long currentBarTime = this.history.getStartTimeOfCurrentBar(currentInstrument, currentPeriod);
/* 4580 */                 int flats = 0;
/* 4581 */                 if (currentTo > currentBarTime) {
/* 4582 */                   flats = DataCacheUtils.getCandlesCountBetweenFast(currentPeriod, currentBarTime, currentTo) - 1;
/* 4583 */                   currentTo = currentBarTime;
/*      */                 }
/*      */ 
/* 4586 */                 InputParameterInfo.Type inputType = info.getType();
/* 4587 */                 IIndicators.AppliedPrice appliedPrice = info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null;
/*      */ 
/* 4589 */                 List sourceBars = getInputBars(currentInstrument, currentPeriod, currentSide, info.getType(), appliedPrice, filter, before, currentTo, 0, lookBack - flats < 0 ? 0 : lookBack - flats, lookforward, flats);
/*      */ 
/* 4592 */                 inputData = barsToReal(sourceBars, inputType, appliedPrice);
/*      */               } else {
/* 4594 */                 Object inputData = bars;
/* 4595 */                 sourceBars = Arrays.asList(bars);
/*      */               }
/*      */             } else {
/* 4598 */               long currentBarTime = this.history.getStartTimeOfCurrentBar(currentInstrument, period);
/* 4599 */               int flats = 0;
/* 4600 */               long currentTo = time;
/* 4601 */               if (currentTo > currentBarTime) {
/* 4602 */                 flats = DataCacheUtils.getCandlesCountBetweenFast(currentPeriod, currentBarTime, currentTo) - 1;
/* 4603 */                 currentTo = currentBarTime;
/*      */               }
/*      */ 
/* 4606 */               InputParameterInfo.Type inputType = info.getType();
/* 4607 */               IIndicators.AppliedPrice appliedPrice = info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null;
/*      */ 
/* 4609 */               sourceBars = getInputBars(currentInstrument, currentPeriod, currentSide, inputType, appliedPrice, filter, numberOfCandlesBefore, currentTo, numberOfCandlesAfter, lookBack - flats < 0 ? 0 : lookBack - flats, flats > 0 ? 0 : lookforward, flats);
/*      */ 
/* 4611 */               inputData = barsToReal(sourceBars, inputType, appliedPrice);
/*      */             }
/*      */           }
/* 4614 */           if ((info.getInstrument() == null) && (info.getPeriod() == null)) {
/* 4615 */             if (info.getType() == InputParameterInfo.Type.PRICE)
/* 4616 */               length = ((double[][])(double[][])inputData)[0].length;
/* 4617 */             else if (info.getType() == InputParameterInfo.Type.DOUBLE)
/* 4618 */               length = ((double[])(double[])inputData).length;
/* 4619 */             else if (info.getType() == InputParameterInfo.Type.BAR)
/* 4620 */               length = ((IBar[])(IBar[])inputData).length;
/*      */           }
/*      */           try
/*      */           {
/* 4624 */             indicator.setInputParameter(i, inputData);
/* 4625 */             calculationResultWraper.setSourceData(sourceBars);
/*      */           } catch (Throwable t) {
/* 4627 */             LOGGER.error(t.getMessage(), t);
/* 4628 */             String error = StrategyWrapper.representError(indicator, t);
/* 4629 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4630 */             throw new JFException(t);
/*      */           }
/*      */         }
/* 4633 */         if (length == -2147483648) {
/* 4634 */           if (indicatorInfo.getNumberOfInputs() == 0) {
/* 4635 */             length = 0;
/*      */           } else {
/* 4637 */             Object inputData = getInputData(instrument, period, OfferSide.BID, InputParameterInfo.Type.BAR, null, filter, numberOfCandlesBefore, time, numberOfCandlesAfter, lookBack, lookforward, 0);
/*      */ 
/* 4639 */             length = ((IBar[])(IBar[])inputData).length;
/*      */           }
/*      */         }
/*      */ 
/* 4643 */         Object[] ret = calculateIndicator(numberOfCandlesBefore, time, numberOfCandlesAfter, indicator, indicatorLookback, indicatorLookback, lookforward, length);
/*      */ 
/* 4645 */         calculationResultWraper.setIndicatorCalculationResult(ret);
/* 4646 */         j = calculationResultWraper;
/*      */         return j; } finally { cacheIndicator(functionName, indicatorHolder); }
/*      */     }
/*      */     catch (JFException e) {
/* 4651 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 4653 */       throw e; } catch (Exception e) {
/*      */     }
/* 4655 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(int numberOfCandlesBefore, long time, int numberOfCandlesAfter, IIndicator indicator, int indicatorLookback, int lookback, int lookforward, int inputLength)
/*      */     throws JFException
/*      */   {
/* 4669 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 4670 */     if (inputLength < numberOfCandlesBefore + numberOfCandlesAfter + indicatorLookback + lookforward) {
/* 4671 */       LOGGER.warn("There is no enough data to calculate values");
/* 4672 */       Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4673 */       int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4674 */         OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 4675 */         switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */         case 1:
/* 4677 */           ret[i] = new double[0];
/* 4678 */           break;
/*      */         case 2:
/* 4680 */           ret[i] = new int[0];
/* 4681 */           break;
/*      */         case 3:
/* 4683 */           ret[i] = new Object[0];
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4688 */       return ret;
/*      */     }
/* 4690 */     Object[] outputs = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4691 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4692 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 4693 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 4695 */         double[] doubles = new double[inputLength - (indicatorLookback + lookforward)];
/* 4696 */         outputs[i] = doubles;
/*      */         try {
/* 4698 */           indicator.setOutputParameter(i, doubles);
/*      */         } catch (Throwable t) {
/* 4700 */           LOGGER.error(t.getMessage(), t);
/* 4701 */           String error = StrategyWrapper.representError(indicator, t);
/* 4702 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4703 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 2:
/* 4707 */         int[] ints = new int[inputLength - (indicatorLookback + lookforward)];
/* 4708 */         outputs[i] = ints;
/*      */         try {
/* 4710 */           indicator.setOutputParameter(i, ints);
/*      */         } catch (Throwable t) {
/* 4712 */           LOGGER.error(t.getMessage(), t);
/* 4713 */           String error = StrategyWrapper.representError(indicator, t);
/* 4714 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4715 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 3:
/* 4719 */         Object[] objects = new Object[inputLength - (indicatorLookback + lookforward)];
/* 4720 */         outputs[i] = objects;
/*      */         try {
/* 4722 */           indicator.setOutputParameter(i, objects);
/*      */         } catch (Throwable t) {
/* 4724 */           LOGGER.error(t.getMessage(), t);
/* 4725 */           String error = StrategyWrapper.representError(indicator, t);
/* 4726 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4727 */           throw new JFException(t);
/*      */         }
/*      */       }
/*      */     }
/*      */     IndicatorResult result;
/*      */     try {
/* 4734 */       result = indicator.calculate(0, inputLength - 1);
/*      */     } catch (TaLibException e) {
/* 4736 */       Throwable t = e.getCause();
/* 4737 */       LOGGER.error(t.getMessage(), t);
/* 4738 */       String error = StrategyWrapper.representError(indicator, t);
/* 4739 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4740 */       throw new JFException(t);
/*      */     } catch (Throwable t) {
/* 4742 */       LOGGER.error(t.getMessage(), t);
/* 4743 */       String error = StrategyWrapper.representError(indicator, t);
/* 4744 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4745 */       throw new JFException(t);
/*      */     }
/* 4747 */     if ((result.getLastValueIndex() == -2147483648) && (result.getNumberOfElements() != 0)) {
/* 4748 */       if (lookforward != 0) {
/* 4749 */         String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned result without lastValueIndex set. This is only allowed when lookforward is equals to zero";
/*      */ 
/* 4751 */         LOGGER.error(error);
/* 4752 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 4753 */         throw new JFException(error);
/*      */       }
/* 4755 */       result.setLastValueIndex(inputLength - 1);
/*      */     }
/*      */ 
/* 4758 */     if ((result.getLastValueIndex() + 1 - result.getFirstValueIndex() < inputLength - lookback - lookforward) || (result.getNumberOfElements() < inputLength - lookback - lookforward)) {
/* 4759 */       String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned less values than expected";
/* 4760 */       LOGGER.error(error);
/* 4761 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 4762 */       throw new JFException(error);
/*      */     }
/* 4764 */     Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 4765 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 4766 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/*      */       int outCount;
/* 4767 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 4769 */         double[] doubles = (double[])(double[])outputs[i];
/* 4770 */         outCount = numberOfCandlesBefore + numberOfCandlesAfter > result.getNumberOfElements() ? result.getNumberOfElements() : numberOfCandlesBefore + numberOfCandlesAfter;
/*      */ 
/* 4772 */         double[] retDoubles = new double[outCount];
/* 4773 */         System.arraycopy(doubles, result.getNumberOfElements() - outCount, retDoubles, 0, outCount);
/* 4774 */         ret[i] = retDoubles;
/* 4775 */         break;
/*      */       case 2:
/* 4777 */         int[] ints = (int[])(int[])outputs[i];
/* 4778 */         outCount = numberOfCandlesBefore + numberOfCandlesAfter > result.getNumberOfElements() ? result.getNumberOfElements() : numberOfCandlesBefore + numberOfCandlesAfter;
/*      */ 
/* 4780 */         int[] retInts = new int[outCount];
/* 4781 */         System.arraycopy(ints, result.getNumberOfElements() - outCount, retInts, 0, outCount);
/* 4782 */         ret[i] = retInts;
/* 4783 */         break;
/*      */       case 3:
/* 4785 */         Object[] objects = (Object[])(Object[])outputs[i];
/* 4786 */         outCount = numberOfCandlesBefore + numberOfCandlesAfter > result.getNumberOfElements() ? result.getNumberOfElements() : numberOfCandlesBefore + numberOfCandlesAfter;
/*      */ 
/* 4788 */         Object[] retObjs = new Object[outCount];
/* 4789 */         System.arraycopy(objects, result.getNumberOfElements() - outCount, retObjs, 0, outCount);
/* 4790 */         ret[i] = retObjs;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4795 */     return ret;
/*      */   }
/*      */ 
/*      */   protected void setOptParams(IIndicator indicator, Object[] optParams) throws JFException {
/* 4799 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 4800 */     int i = 0; for (int j = indicatorInfo.getNumberOfOptionalInputs(); i < j; i++)
/*      */       try {
/* 4802 */         indicator.setOptInputParameter(i, optParams[i]);
/*      */       } catch (Throwable t) {
/* 4804 */         LOGGER.error(t.getMessage(), t);
/* 4805 */         String error = StrategyWrapper.representError(indicator, t);
/* 4806 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 4807 */         throw new JFException(t);
/*      */       }
/*      */   }
/*      */ 
/*      */   Object getInputData(Instrument instrument, Period period, OfferSide side, InputParameterInfo.Type inputType, IIndicators.AppliedPrice appliedPrice, int shift, int lookback, int lookforward)
/*      */     throws DataCacheException, JFException
/*      */   {
/* 4814 */     if ((lookback == 0) && (lookforward == 0))
/*      */     {
/*      */       IBar bar;
/* 4817 */       if (shift < 0) {
/* 4818 */         IBar bar = this.history.getCurrentBar(instrument, period, side);
/* 4819 */         if (bar == null) {
/* 4820 */           return null;
/*      */         }
/* 4822 */         double price = bar.getClose();
/* 4823 */         bar = new CandleData(DataCacheUtils.getTimeForNCandlesForwardFast(period, bar.getTime(), -shift + 1), price, price, price, price, 0.0D);
/*      */       } else {
/* 4825 */         bar = this.history.getBar(instrument, period, side, shift);
/* 4826 */         if (bar == null) {
/* 4827 */           return null;
/*      */         }
/*      */       }
/* 4830 */       List bars = new ArrayList(1);
/* 4831 */       bars.add(bar);
/* 4832 */       return barsToReal(bars, inputType, appliedPrice);
/* 4833 */     }if (lookback == 0) {
/* 4834 */       int flats = shift < 0 ? -shift : 0;
/* 4835 */       shift = shift < 0 ? 0 : shift;
/* 4836 */       IBar currentBar = null;
/* 4837 */       long currentBarStartTime = this.history.getStartTimeOfCurrentBar(instrument, period);
/* 4838 */       long requestedBarStartTime = DataCacheUtils.getTimeForNCandlesBackFast(period, DataCacheUtils.getPreviousCandleStart(period, currentBarStartTime), shift);
/* 4839 */       int count = DataCacheUtils.getCandlesCountBetweenFast(period, requestedBarStartTime, currentBarStartTime) - 1;
/*      */       long lookforwardStartTime;
/* 4841 */       if (count > lookforward) {
/* 4842 */         lookforwardStartTime = DataCacheUtils.getTimeForNCandlesForwardFast(period, requestedBarStartTime, count + 1);
/* 4843 */       } else if (count == lookforward) {
/* 4844 */         long lookforwardStartTime = DataCacheUtils.getTimeForNCandlesForwardFast(period, requestedBarStartTime, count);
/* 4845 */         currentBar = this.history.getCurrentBar(instrument, period, side);
/*      */       }
/*      */       else {
/* 4848 */         return null;
/*      */       }
/*      */       long lookforwardStartTime;
/* 4850 */       List bars = this.history.getBars(instrument, period, side, requestedBarStartTime, lookforwardStartTime);
/* 4851 */       if (currentBar != null) {
/* 4852 */         bars.add(currentBar);
/*      */       }
/* 4854 */       if ((flats > 0) && (!bars.isEmpty()))
/*      */       {
/* 4857 */         IBar lastCandle = (IBar)bars.get(bars.size() - 1);
/* 4858 */         double price = lastCandle.getClose();
/* 4859 */         int i = 0; for (long time = DataCacheUtils.getNextCandleStartFast(period, lastCandle.getTime()); i < flats; time = DataCacheUtils.getNextCandleStartFast(period, time)) {
/* 4860 */           bars.add(new CandleData(time, price, price, price, price, 0.0D));
/*      */ 
/* 4859 */           i++;
/*      */         }
/*      */       }
/*      */ 
/* 4863 */       return barsToReal(bars, inputType, appliedPrice);
/*      */     }
/* 4865 */     int flats = shift < 0 ? -shift : 0;
/* 4866 */     shift = shift < 0 ? 0 : shift;
/* 4867 */     IBar currentBar = null;
/* 4868 */     if (shift == 0) {
/* 4869 */       if (lookforward > 0)
/*      */       {
/* 4871 */         return null;
/*      */       }
/* 4873 */       shift = 1;
/* 4874 */       lookback--;
/* 4875 */       currentBar = this.history.getCurrentBar(instrument, period, side);
/* 4876 */       if (currentBar == null) {
/* 4877 */         return null;
/*      */       }
/*      */     }
/* 4880 */     long currentBarStartTime = this.history.getStartTimeOfCurrentBar(instrument, period);
/* 4881 */     long requestedBarStartTime = DataCacheUtils.getTimeForNCandlesBackFast(period, DataCacheUtils.getPreviousCandleStart(period, currentBarStartTime), shift);
/* 4882 */     long lookbackStartTime = DataCacheUtils.getTimeForNCandlesBackFast(period, requestedBarStartTime, lookback + 1);
/* 4883 */     if (lookforward > 0) {
/* 4884 */       int count = DataCacheUtils.getCandlesCountBetweenFast(period, requestedBarStartTime, currentBarStartTime) - 1;
/* 4885 */       if (count > lookforward) {
/* 4886 */         requestedBarStartTime = DataCacheUtils.getTimeForNCandlesForwardFast(period, requestedBarStartTime, lookforward + 1);
/* 4887 */       } else if (count == lookforward) {
/* 4888 */         requestedBarStartTime = DataCacheUtils.getTimeForNCandlesForwardFast(period, requestedBarStartTime, count);
/* 4889 */         currentBar = this.history.getCurrentBar(instrument, period, side);
/*      */       }
/*      */       else {
/* 4892 */         return null;
/*      */       }
/*      */     }
/* 4895 */     List bars = this.history.getBars(instrument, period, side, lookbackStartTime, requestedBarStartTime);
/* 4896 */     if (currentBar != null) {
/* 4897 */       bars.add(currentBar);
/*      */     }
/* 4899 */     if ((flats > 0) && (!bars.isEmpty()))
/*      */     {
/* 4902 */       IBar lastCandle = (IBar)bars.get(bars.size() - 1);
/* 4903 */       double price = lastCandle.getClose();
/* 4904 */       int i = 0; for (long time = DataCacheUtils.getNextCandleStartFast(period, lastCandle.getTime()); i < flats; time = DataCacheUtils.getNextCandleStartFast(period, time)) {
/* 4905 */         bars.add(new CandleData(time, price, price, price, price, 0.0D));
/*      */ 
/* 4904 */         i++;
/*      */       }
/*      */     }
/*      */ 
/* 4908 */     return barsToReal(bars, inputType, appliedPrice);
/*      */   }
/*      */ 
/*      */   Object getInputData(Instrument instrument, Period period, OfferSide side, InputParameterInfo.Type inputType, IIndicators.AppliedPrice appliedPrice, long from, long to, int lookback, int lookforward, boolean addCurrentCandle, int addFlatsAtTheEnd)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 4916 */     if (period == Period.TICK) {
/* 4917 */       from = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, from);
/* 4918 */       from -= lookback * 1000;
/* 4919 */       long candleTo = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, to);
/* 4920 */       long timeOfLastTick = this.history.getTimeOfLastTick(instrument);
/* 4921 */       if (lookforward > 0) {
/* 4922 */         long lastCandle = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, timeOfLastTick);
/* 4923 */         int count = DataCacheUtils.getCandlesCountBetweenFast(Period.ONE_SEC, candleTo, lastCandle) - 1;
/* 4924 */         count = count > lookforward ? lookforward : count;
/* 4925 */         to = DataCacheUtils.getTimeForNCandlesForwardFast(Period.ONE_SEC, candleTo, count + 1);
/* 4926 */         candleTo = to;
/* 4927 */         if (candleTo + 999L <= timeOfLastTick)
/* 4928 */           to = candleTo + 999L;
/* 4929 */         else if (candleTo <= timeOfLastTick)
/* 4930 */           to = timeOfLastTick;
/*      */       }
/* 4932 */       else if (candleTo + 999L <= timeOfLastTick) {
/* 4933 */         to = candleTo + 999L;
/*      */       }
/* 4935 */       List ticks = this.history.getTicks(instrument, from, to);
/* 4936 */       List bars = new ArrayList((int)((to - from) / 1000L + 1L));
/* 4937 */       if ((ticks.isEmpty()) || (DataCacheUtils.getCandleStartFast(Period.ONE_SEC, ((ITick)ticks.get(0)).getTime()) != from)) {
/* 4938 */         ITick tickBefore = this.history.getLastTickBefore(instrument, from - 1L);
/* 4939 */         double price = side == OfferSide.ASK ? tickBefore.getAsk() : tickBefore.getBid();
/* 4940 */         long timeOfLastFlatCandle = ticks.isEmpty() ? DataCacheUtils.getCandleStartFast(Period.ONE_SEC, to) : DataCacheUtils.getPreviousCandleStartFast(Period.ONE_SEC, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, ((ITick)ticks.get(0)).getTime()));
/*      */ 
/* 4942 */         for (long time = from; time <= timeOfLastFlatCandle; time += 1000L) {
/* 4943 */           bars.add(new CandleData(time, price, price, price, price, 0.0D));
/*      */         }
/*      */       }
/* 4946 */       createCandlesFromTicks(ticks, bars, side);
/* 4947 */       return barsToReal(bars, inputType, appliedPrice);
/*      */     }
/* 4949 */     from = DataCacheUtils.getCandleStartFast(period, from);
/* 4950 */     long lookbackStartTime = DataCacheUtils.getTimeForNCandlesBackFast(period, from, lookback + 1);
/* 4951 */     if (lookforward > 0) {
/* 4952 */       to = DataCacheUtils.getCandleStartFast(period, to);
/* 4953 */       long lastCandle = DataCacheUtils.getPreviousCandleStartFast(period, this.history.getStartTimeOfCurrentBar(instrument, period));
/* 4954 */       int count = DataCacheUtils.getCandlesCountBetweenFast(period, to, lastCandle) - 1;
/* 4955 */       count = count > lookforward ? lookforward : count;
/* 4956 */       to = DataCacheUtils.getTimeForNCandlesForwardFast(period, to, count + 1);
/*      */     }
/* 4958 */     List bars = this.history.getBars(instrument, period, side, lookbackStartTime, to);
/* 4959 */     if (addCurrentCandle) {
/* 4960 */       IBar currentBar = this.history.getCurrentBar(instrument, period, side);
/* 4961 */       bars.add(currentBar);
/*      */     }
/* 4963 */     if ((addFlatsAtTheEnd > 0) && (!bars.isEmpty()))
/*      */     {
/* 4966 */       IBar lastCandle = (IBar)bars.get(bars.size() - 1);
/* 4967 */       double price = lastCandle.getClose();
/* 4968 */       int i = 0; for (long lastTime = DataCacheUtils.getNextCandleStartFast(period, lastCandle.getTime()); i < addFlatsAtTheEnd; lastTime = DataCacheUtils.getNextCandleStartFast(period, lastTime)) {
/* 4969 */         bars.add(new CandleData(lastTime, price, price, price, price, 0.0D));
/*      */ 
/* 4968 */         i++;
/*      */       }
/*      */     }
/*      */ 
/* 4972 */     return barsToReal(bars, inputType, appliedPrice);
/*      */   }
/*      */ 
/*      */   Object getInputData(Instrument instrument, Period period, OfferSide side, InputParameterInfo.Type inputType, IIndicators.AppliedPrice appliedPrice, Filter filter, int before, long time, int after, int lookback, int lookforward, int addFlatsAtTheEnd)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 4980 */     List bars = getInputBars(instrument, period, side, inputType, appliedPrice, filter, before, time, after, lookback, lookforward, addFlatsAtTheEnd);
/* 4981 */     return barsToReal(bars, inputType, appliedPrice);
/*      */   }
/*      */ 
/*      */   private List<IBar> getInputBars(Instrument instrument, Period period, OfferSide side, InputParameterInfo.Type inputType, IIndicators.AppliedPrice appliedPrice, Filter filter, int before, long time, int after, int lookback, int lookforward, int addFlatsAtTheEnd)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 4999 */     time = DataCacheUtils.getCandleStartFast(period, time);
/* 5000 */     before += lookback;
/* 5001 */     after += lookforward;
/* 5002 */     List bars = this.history.getBars(instrument, period, side, filter, before, time, after);
/* 5003 */     if ((addFlatsAtTheEnd > 0) && (!bars.isEmpty()))
/*      */     {
/* 5006 */       IBar lastCandle = (IBar)bars.get(bars.size() - 1);
/* 5007 */       double price = lastCandle.getClose();
/* 5008 */       int i = 0; for (long lastTime = DataCacheUtils.getNextCandleStartFast(period, lastCandle.getTime()); i < addFlatsAtTheEnd; lastTime = DataCacheUtils.getNextCandleStartFast(period, lastTime)) {
/* 5009 */         bars.add(new CandleData(lastTime, price, price, price, price, 0.0D));
/*      */ 
/* 5008 */         i++;
/*      */       }
/*      */     }
/*      */ 
/* 5012 */     return bars;
/*      */   }
/*      */ 
/*      */   protected void checkSide(Period period, OfferSide[] side) throws JFException {
/* 5016 */     if (period == Period.TICK) {
/* 5017 */       return;
/*      */     }
/* 5019 */     checkSide(side);
/*      */   }
/*      */ 
/*      */   protected void checkSide(OfferSide[] side) throws JFException {
/* 5023 */     if (side == null) {
/* 5024 */       throw new JFException("Side parameter cannot be null");
/*      */     }
/* 5026 */     for (OfferSide offerSide : side)
/* 5027 */       if (offerSide == null)
/* 5028 */         throw new JFException("Side parameter cannot be null");
/*      */   }
/*      */ 
/*      */   private void checkSide(Period period, OfferSide side)
/*      */     throws JFException
/*      */   {
/* 5035 */     if (period == Period.TICK) {
/* 5036 */       return;
/*      */     }
/* 5038 */     if (side == null)
/* 5039 */       throw new JFException("Side parameter cannot be null");
/*      */   }
/*      */ 
/*      */   protected void checkIntervalValid(Period period, long from, long to) throws JFException
/*      */   {
/* 5044 */     this.history.validateIntervalByPeriod(period, from, to);
/*      */   }
/*      */ 
/*      */   private void checkIntervalValid(int before, int after) throws JFException {
/* 5048 */     if ((before <= 0) && (after <= 0))
/* 5049 */       throw new IllegalArgumentException("Negative or zero number of candles requested");
/*      */   }
/*      */ 
/*      */   protected void checkNotTick(Period period) throws JFException
/*      */   {
/* 5054 */     if (period == Period.TICK)
/* 5055 */       throw new JFException("Functions with shift parameter doesn't support ticks");
/*      */   }
/*      */ 
/*      */   protected void checkShiftPositive(int shift) throws JFException
/*      */   {
/* 5060 */     if (shift < 0)
/* 5061 */       throw new JFException("Shift parameter must not be negative");
/*      */   }
/*      */ 
/*      */   protected <B extends IBar> Object barsToReal(List<B> bars, InputParameterInfo.Type inputType, IIndicators.AppliedPrice appliedPrice) throws JFException
/*      */   {
/* 5066 */     int i = 0;
/* 5067 */     switch (19.$SwitchMap$com$dukascopy$api$indicators$InputParameterInfo$Type[inputType.ordinal()]) {
/*      */     case 1:
/* 5069 */       double[] retDouble = new double[bars.size()];
/* 5070 */       for (IBar bar : bars) {
/* 5071 */         retDouble[i] = barToReal(bar, appliedPrice);
/* 5072 */         i++;
/*      */       }
/* 5074 */       return retDouble;
/*      */     case 2:
/* 5076 */       double[][] retPrice = new double[5][bars.size()];
/* 5077 */       for (IBar bar : bars) {
/* 5078 */         retPrice[0][i] = barToReal(bar, IIndicators.AppliedPrice.OPEN);
/* 5079 */         retPrice[1][i] = barToReal(bar, IIndicators.AppliedPrice.CLOSE);
/* 5080 */         retPrice[2][i] = barToReal(bar, IIndicators.AppliedPrice.HIGH);
/* 5081 */         retPrice[3][i] = barToReal(bar, IIndicators.AppliedPrice.LOW);
/* 5082 */         retPrice[4][i] = barToReal(bar, IIndicators.AppliedPrice.VOLUME);
/* 5083 */         i++;
/*      */       }
/* 5085 */       return retPrice;
/*      */     case 3:
/* 5087 */       return bars.toArray(new IBar[bars.size()]);
/*      */     }
/* 5089 */     throw new RuntimeException("Unexpected input type [" + inputType + "]");
/*      */   }
/*      */ 
/*      */   private double barToReal(IBar bar, IIndicators.AppliedPrice appliedPrice) throws JFException
/*      */   {
/* 5094 */     switch (19.$SwitchMap$com$dukascopy$api$IIndicators$AppliedPrice[appliedPrice.ordinal()]) {
/*      */     case 1:
/* 5096 */       return bar.getClose();
/*      */     case 2:
/* 5098 */       return bar.getHigh();
/*      */     case 3:
/* 5100 */       return bar.getLow();
/*      */     case 4:
/* 5102 */       return (bar.getHigh() + bar.getLow()) / 2.0D;
/*      */     case 5:
/* 5104 */       return bar.getOpen();
/*      */     case 6:
/* 5106 */       return (bar.getHigh() + bar.getLow() + bar.getClose()) / 3.0D;
/*      */     case 7:
/* 5108 */       return (bar.getHigh() + bar.getLow() + bar.getClose() + bar.getClose()) / 4.0D;
/*      */     case 8:
/* 5110 */       return bar.getTime();
/*      */     case 9:
/* 5112 */       return bar.getVolume();
/*      */     }
/* 5114 */     throw new JFException("Parameter should not be ASK, BID, ASK_VOLUME or BID_VOLUME for bars");
/*      */   }
/*      */ 
/*      */   private void createCandlesFromTicks(List<ITick> timeData, List<IBar> tickCandles, OfferSide side)
/*      */   {
/* 5119 */     if (timeData.isEmpty()) {
/* 5120 */       return;
/*      */     }
/* 5122 */     IntraPeriodCandleData[][] generatedCandles = new IntraPeriodCandleData[Period.values().length][];
/* 5123 */     generatedCandles[Period.ONE_SEC.ordinal()] = new IntraPeriodCandleData[0];
/* 5124 */     List formedCandles = new ArrayList();
/* 5125 */     for (ITick tickData : timeData) {
/* 5126 */       IntraperiodCandlesGenerator.addTickToCandles(tickData.getTime(), tickData.getAsk(), tickData.getBid(), tickData.getAskVolume(), tickData.getBidVolume(), null, generatedCandles, formedCandles, null);
/*      */     }
/*      */ 
/* 5139 */     long nextCandleTime = DataCacheUtils.getNextCandleStartFast(Period.ONE_SEC, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, ((ITick)timeData.get(timeData.size() - 1)).getTime()));
/* 5140 */     IntraperiodCandlesGenerator.addTickToCandles(nextCandleTime, 0.0D, 0.0D, 0.0D, 0.0D, null, generatedCandles, formedCandles, null);
/*      */ 
/* 5152 */     for (Object[] formedCandlesData : formedCandles)
/*      */     {
/*      */       IntraPeriodCandleData candle;
/*      */       IntraPeriodCandleData candle;
/* 5154 */       if (side == OfferSide.ASK)
/* 5155 */         candle = (IntraPeriodCandleData)formedCandlesData[1];
/*      */       else {
/* 5157 */         candle = (IntraPeriodCandleData)formedCandlesData[2];
/*      */       }
/* 5159 */       IBar bar = new CandleData(candle.time, candle.open, candle.close, candle.low, candle.high, candle.vol);
/* 5160 */       tickCandles.add(bar);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerCustomIndicator(File compiledCustomIndcatorFile) throws JFException
/*      */   {
/*      */     try {
/* 5167 */       AccessController.doPrivileged(new PrivilegedExceptionAction(compiledCustomIndcatorFile) {
/*      */         public Object run() throws Exception {
/* 5169 */           Indicators.this.registerCustomIndicatorSecured(this.val$compiledCustomIndcatorFile);
/* 5170 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 5173 */       Exception ex = e.getException();
/* 5174 */       if ((ex instanceof JFException))
/* 5175 */         throw ((JFException)ex);
/* 5176 */       if ((ex instanceof RuntimeException)) {
/* 5177 */         throw ((RuntimeException)ex);
/*      */       }
/* 5179 */       LOGGER.error(ex.getMessage(), ex);
/* 5180 */       throw new JFException(ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerCustomIndicator(Class<? extends IIndicator> indicatorClass) throws JFException
/*      */   {
/*      */     try
/*      */     {
/* 5188 */       AccessController.doPrivileged(new PrivilegedExceptionAction(indicatorClass) {
/*      */         public Object run() throws Exception {
/* 5190 */           Indicators.this.registerCustomIndicatorSecured(this.val$indicatorClass);
/* 5191 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException e) {
/* 5196 */       Exception ex = e.getException();
/* 5197 */       if ((ex instanceof JFException))
/* 5198 */         throw ((JFException)ex);
/* 5199 */       if ((ex instanceof RuntimeException)) {
/* 5200 */         throw ((RuntimeException)ex);
/*      */       }
/* 5202 */       LOGGER.error(ex.getMessage(), ex);
/* 5203 */       throw new JFException(ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerDownloadableIndicator(String id, String name) throws JFException {
/* 5212 */     ComponentDownloader downloader = ComponentDownloader.getInstance();
/*      */     byte[] srcCode;
/*      */     try {
/* 5215 */       srcCode = downloader.getFileSource(id, null, "http://arizona.rix.dukascopy.com:8080/strategystorageserver/getCompiledFile?id=");
/*      */     } catch (IOException e) {
/* 5217 */       throw new JFException("Unable to download component " + name, e);
/*      */     }IIndicator indicator;
/*      */     try {
/* 5221 */       JFXPack jfxPack = JFXPack.loadFromPack(srcCode);
/* 5222 */       indicator = (IIndicator)jfxPack.getTarget();
/*      */     } catch (Exception e) {
/* 5224 */       throw new JFException("Unable to init component " + name, e);
/*      */     }
/* 5226 */     registerCustomIndicator(indicator.getClass());
/*      */   }
/*      */ 
/*      */   private void registerCustomIndicatorSecured(File compiledCustomIndicatorFile) throws JFException {
/* 5230 */     if (!compiledCustomIndicatorFile.exists()) {
/* 5231 */       throw new JFException("Indicator file doesn't exists");
/*      */     }
/* 5233 */     IndicatorsProvider indicatorsProvider = IndicatorsProvider.getInstance();
/* 5234 */     CustIndicatorWrapper indicatorWrapper = new CustIndicatorWrapper();
/* 5235 */     indicatorWrapper.setBinaryFile(compiledCustomIndicatorFile);
/* 5236 */     if (indicatorsProvider.enableIndicator(indicatorWrapper, NotificationUtilsProvider.getNotificationUtils()) == null)
/* 5237 */       throw new JFException("Cannot register indicator");
/*      */   }
/*      */ 
/*      */   private void registerCustomIndicatorSecured(Class<? extends IIndicator> indicatorClass) throws JFException
/*      */   {
/* 5242 */     if (indicatorClass == null) {
/* 5243 */       throw new JFException("Indicator class is null");
/*      */     }
/*      */ 
/* 5246 */     IIndicator indicator = null;
/*      */     try {
/* 5248 */       indicator = (IIndicator)indicatorClass.newInstance();
/*      */     } catch (Exception ex) {
/* 5250 */       throw new JFException("Cannot create indicator [" + indicatorClass + "] instance.");
/*      */     }
/* 5252 */     IndicatorContext indicatorContext = IndicatorHelper.createIndicatorContext();
/*      */     try {
/* 5254 */       indicator.onStart(indicatorContext);
/*      */     }
/*      */     catch (Throwable th) {
/* 5257 */       throw new JFException("Exception in onStart method");
/*      */     }
/* 5259 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 5260 */     if (indicatorInfo == null) {
/* 5261 */       throw new JFException("Indicator info is null");
/*      */     }
/*      */ 
/* 5264 */     String indicatorName = indicatorInfo.getName();
/* 5265 */     if ((indicatorName != null) && (!indicatorName.trim().isEmpty())) {
/* 5266 */       this.customIndicators.put(indicatorName, indicatorClass);
/* 5267 */       cacheIndicator(indicatorName, new IndicatorHolder(indicator, indicatorContext));
/*      */     }
/*      */     else
/*      */     {
/* 5272 */       throw new JFException("Indicator name is empty");
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, Filter filter, long from, long to)
/*      */     throws JFException
/*      */   {
/*      */     try
/*      */     {
/* 5289 */       checkSide(period, side);
/* 5290 */       checkIntervalValid(period, from, to);
/* 5291 */       IndicatorHolder indicatorHolder = getCachedIndicator(functionName);
/*      */       try {
/* 5293 */         if (indicatorHolder == null) {
/* 5294 */           throw new JFException("Indicator with name " + functionName + " was not found");
/*      */         }
/* 5296 */         IIndicator indicator = indicatorHolder.getIndicator();
/* 5297 */         int[] lookBackLookForward = calculateLookbackLookforward(indicator, optParams);
/* 5298 */         IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*      */ 
/* 5300 */         int indicatorLookback = lookBackLookForward[0];
/* 5301 */         int lookBack = lookBackLookForward[1];
/* 5302 */         int lookforward = lookBackLookForward[2];
/*      */ 
/* 5304 */         IndicatorContext indicatorContext = indicatorHolder.getIndicatorContext();
/* 5305 */         FeedDescriptor feedDescriptor = createFeedDescriptor(period, instrument, indicatorInfo.getNumberOfInputs() > 0 ? side[0] : OfferSide.BID);
/* 5306 */         indicatorContext.setFeedDescriptor(feedDescriptor);
/* 5307 */         int length = -2147483648;
/*      */ 
/* 5309 */         int i = 0; for (int j = indicatorInfo.getNumberOfInputs(); i < j; i++) {
/* 5310 */           InputParameterInfo info = indicator.getInputParameterInfo(i);
/*      */ 
/* 5312 */           InputParameterInfo.Type inputType = info.getType();
/* 5313 */           IIndicators.AppliedPrice appliedPrice = info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null;
/* 5314 */           OfferSide currentSide = info.getOfferSide() == null ? side[i] : info.getOfferSide();
/* 5315 */           Instrument currentInstrument = info.getInstrument() == null ? instrument : info.getInstrument();
/*      */ 
/* 5317 */           Period currentPeriod = info.getPeriod() == null ? period : info.getPeriod();
/* 5318 */           if (period.getInterval() > currentPeriod.getInterval()) {
/* 5319 */             throw new JFException("Indicator [" + indicatorInfo.getName() + "] cannot be run with periods longer than " + currentPeriod);
/*      */           }
/*      */ 
/* 5322 */           long currentTo = to;
/* 5323 */           int flats = 0;
/*      */ 
/* 5325 */           if (instrument != currentInstrument) {
/* 5326 */             long lastCandleTime = DataCacheUtils.getPreviousCandleStartFast(period, this.history.getStartTimeOfCurrentBar(instrument, period));
/* 5327 */             if (currentTo > lastCandleTime) {
/* 5328 */               currentTo = lastCandleTime;
/*      */             }
/* 5330 */             currentTo = DataCacheUtils.getCandleStartFast(currentPeriod, currentTo);
/* 5331 */             long lastFormedCandleTime = DataCacheUtils.getPreviousCandleStartFast(currentPeriod, this.history.getStartTimeOfCurrentBar(currentInstrument, currentPeriod));
/*      */ 
/* 5333 */             lastCandleTime = this.history.getStartTimeOfCurrentBar(currentInstrument, currentPeriod);
/*      */ 
/* 5335 */             if (currentTo > lastCandleTime) {
/* 5336 */               flats = DataCacheUtils.getCandlesCountBetweenFast(currentPeriod, lastCandleTime, currentTo) - 1;
/*      */             }
/* 5338 */             if (currentTo > lastFormedCandleTime) {
/* 5339 */               currentTo = lastFormedCandleTime;
/*      */             }
/*      */           }
/* 5342 */           Object inputData = getInputData(currentInstrument, currentPeriod, currentSide, inputType, appliedPrice, filter, from, currentTo, lookBack, lookforward, flats);
/*      */ 
/* 5345 */           if ((info.getInstrument() == null) && (info.getPeriod() == null)) {
/* 5346 */             if (info.getType() == InputParameterInfo.Type.PRICE)
/* 5347 */               length = ((double[][])(double[][])inputData)[0].length;
/* 5348 */             else if (info.getType() == InputParameterInfo.Type.DOUBLE)
/* 5349 */               length = ((double[])(double[])inputData).length;
/* 5350 */             else if (info.getType() == InputParameterInfo.Type.BAR)
/* 5351 */               length = ((IBar[])(IBar[])inputData).length;
/*      */           }
/*      */           try
/*      */           {
/* 5355 */             indicator.setInputParameter(i, inputData);
/*      */           } catch (Throwable t) {
/* 5357 */             LOGGER.error(t.getMessage(), t);
/* 5358 */             String error = StrategyWrapper.representError(indicator, t);
/* 5359 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 5360 */             throw new JFException(t);
/*      */           }
/*      */         }
/* 5363 */         if (length == -2147483648) {
/* 5364 */           if (indicatorInfo.getNumberOfInputs() == 0) {
/* 5365 */             length = 0;
/*      */           } else {
/* 5367 */             inputData = getInputData(instrument, period, OfferSide.BID, InputParameterInfo.Type.BAR, null, filter, from, to, lookBack, lookforward, 0);
/*      */ 
/* 5369 */             length = ((IBar[])(IBar[])inputData).length;
/*      */           }
/*      */         }
/*      */ 
/* 5373 */         Object inputData = calculateIndicator(period, from, to, filter, indicator, indicatorLookback, lookBack, lookforward, length);
/*      */         return inputData;
/*      */       }
/*      */       finally
/*      */       {
/* 5375 */         indicatorHolder.getIndicatorContext().resetFeedDescriptor();
/* 5376 */         cacheIndicator(functionName, indicatorHolder);
/*      */       }
/*      */     } catch (JFException e) {
/* 5379 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 5381 */       throw e; } catch (Exception e) {
/*      */     }
/* 5383 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   Object getInputData(Instrument instrument, Period period, OfferSide side, InputParameterInfo.Type inputType, IIndicators.AppliedPrice appliedPrice, Filter filter, long from, long to, int lookback, int lookforward, int addFlatsAtTheEnd)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 5401 */     if (period.equals(Period.TICK)) {
/* 5402 */       return getInputData(instrument, period, side, inputType, appliedPrice, from, to, lookback, lookforward, false, addFlatsAtTheEnd);
/*      */     }
/* 5404 */     List bars = this.history.getBars(instrument, period, side, filter, from, to);
/* 5405 */     if (!bars.isEmpty()) {
/* 5406 */       if (lookback > 0) {
/* 5407 */         List lookBackBars = this.history.getBars(instrument, period, side, filter, lookback + 1, ((IBar)bars.get(0)).getTime(), 0);
/*      */ 
/* 5409 */         if (!lookBackBars.isEmpty()) {
/* 5410 */           lookBackBars.remove(lookBackBars.size() - 1);
/*      */         }
/*      */ 
/* 5413 */         bars.addAll(0, lookBackBars);
/*      */       }
/* 5415 */       if (lookforward > 0) {
/* 5416 */         List lookForwardBars = this.history.getBars(instrument, period, side, filter, 0, ((IBar)bars.get(bars.size() - 1)).getTime(), lookforward + 1);
/*      */ 
/* 5418 */         if (!lookForwardBars.isEmpty()) {
/* 5419 */           lookForwardBars.remove(0);
/*      */         }
/* 5421 */         bars.addAll(lookForwardBars);
/*      */       }
/*      */ 
/* 5424 */       if (addFlatsAtTheEnd > 0) {
/* 5425 */         addFlatBars(bars, addFlatsAtTheEnd, period);
/*      */       }
/*      */     }
/*      */ 
/* 5429 */     return barsToReal(bars, inputType, appliedPrice);
/*      */   }
/*      */ 
/*      */   private List<IBar> addFlatBars(List<IBar> bars, int flatsNumber, Period period)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 5435 */     IBar lastCandle = (IBar)bars.get(bars.size() - 1);
/* 5436 */     double price = lastCandle.getClose();
/* 5437 */     int i = 0; long lastTime = DataCacheUtils.getNextCandleStartFast(period, lastCandle.getTime());
/* 5438 */     for (; i < flatsNumber; lastTime = DataCacheUtils.getNextCandleStartFast(period, lastTime))
/*      */     {
/* 5440 */       bars.add(new CandleData(lastTime, price, price, price, price, 0.0D));
/*      */ 
/* 5438 */       i++;
/*      */     }
/*      */ 
/* 5443 */     return bars;
/*      */   }
/*      */ 
/*      */   private static Object[] initIndicatorOutputs(IIndicator indicator, int indicatorLookBack, int lookForward, int inputLength) throws JFException {
/* 5447 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 5448 */     Object[] outputs = new Object[indicatorInfo.getNumberOfOutputs()];
/* 5449 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 5450 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 5451 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 5453 */         double[] doubles = new double[inputLength - (indicatorLookBack + lookForward)];
/* 5454 */         outputs[i] = doubles;
/*      */         try {
/* 5456 */           indicator.setOutputParameter(i, doubles);
/*      */         } catch (Throwable t) {
/* 5458 */           LOGGER.error(t.getMessage(), t);
/* 5459 */           String error = StrategyWrapper.representError(indicator, t);
/* 5460 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 5461 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 2:
/* 5465 */         int[] ints = new int[inputLength - (indicatorLookBack + lookForward)];
/* 5466 */         outputs[i] = ints;
/*      */         try {
/* 5468 */           indicator.setOutputParameter(i, ints);
/*      */         } catch (Throwable t) {
/* 5470 */           LOGGER.error(t.getMessage(), t);
/* 5471 */           String error = StrategyWrapper.representError(indicator, t);
/* 5472 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 5473 */           throw new JFException(t);
/*      */         }
/*      */ 
/*      */       case 3:
/* 5477 */         Object[] objects = new Object[inputLength - (indicatorLookBack + lookForward)];
/* 5478 */         outputs[i] = objects;
/*      */         try {
/* 5480 */           indicator.setOutputParameter(i, objects);
/*      */         } catch (Throwable t) {
/* 5482 */           LOGGER.error(t.getMessage(), t);
/* 5483 */           String error = StrategyWrapper.representError(indicator, t);
/* 5484 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 5485 */           throw new JFException(t);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 5490 */     return outputs;
/*      */   }
/*      */   private static IndicatorResult calculateAndCheck(IIndicator indicator, int inputLength, int lookback, int lookforward) throws JFException {
/*      */     IndicatorResult result;
/*      */     try {
/* 5496 */       result = indicator.calculate(0, inputLength - 1);
/*      */     } catch (TaLibException e) {
/* 5498 */       Throwable t = e.getCause();
/* 5499 */       LOGGER.error(t.getMessage(), t);
/* 5500 */       String error = StrategyWrapper.representError(indicator, t);
/* 5501 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 5502 */       throw new JFException(t);
/*      */     } catch (Throwable t) {
/* 5504 */       LOGGER.error(t.getMessage(), t);
/* 5505 */       String error = StrategyWrapper.representError(indicator, t);
/* 5506 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 5507 */       throw new JFException(t);
/*      */     }
/* 5509 */     if ((result.getLastValueIndex() == -2147483648) && (result.getNumberOfElements() != 0)) {
/* 5510 */       if (lookforward != 0) {
/* 5511 */         String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned result without lastValueIndex set. This is only allowed when lookforward is equals to zero";
/*      */ 
/* 5513 */         LOGGER.error(error);
/* 5514 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 5515 */         throw new JFException(error);
/*      */       }
/* 5517 */       result.setLastValueIndex(inputLength - 1);
/*      */     }
/*      */ 
/* 5520 */     if ((result.getLastValueIndex() + 1 - result.getFirstValueIndex() < inputLength - lookback - lookforward) || (result.getNumberOfElements() < inputLength - lookback - lookforward)) {
/* 5521 */       String error = "calculate() method of indicator [" + indicator.getIndicatorInfo().getName() + "] returned less values than expected";
/* 5522 */       LOGGER.error(error);
/* 5523 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/* 5524 */       throw new JFException(error);
/*      */     }
/* 5526 */     return result;
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(Period period, long from, long to, Filter filter, IIndicator indicator, int indicatorLookback, int lookback, int lookforward, int inputLength) throws JFException {
/* 5530 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*      */ 
/* 5532 */     if (inputLength <= indicatorLookback + lookforward) {
/* 5533 */       LOGGER.warn("There is no enough data to calculate values");
/* 5534 */       Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 5535 */       int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 5536 */         OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 5537 */         switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */         case 1:
/* 5539 */           ret[i] = new double[0];
/* 5540 */           break;
/*      */         case 2:
/* 5542 */           ret[i] = new int[0];
/* 5543 */           break;
/*      */         case 3:
/* 5545 */           ret[i] = new Object[0];
/*      */         }
/*      */       }
/*      */ 
/* 5549 */       return ret;
/*      */     }
/*      */ 
/* 5552 */     Object[] outputs = initIndicatorOutputs(indicator, indicatorLookback, lookforward, inputLength);
/* 5553 */     IndicatorResult result = calculateAndCheck(indicator, inputLength, lookback, lookforward);
/*      */ 
/* 5555 */     Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 5556 */     int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 5557 */       OutputParameterInfo info = indicator.getOutputParameterInfo(i);
/* 5558 */       int outCount = (result.getNumberOfElements() < inputLength - lookback - lookforward) || (inputLength - lookback - lookforward < 0) ? result.getNumberOfElements() : inputLength - lookback - lookforward;
/*      */ 
/* 5561 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[info.getType().ordinal()]) {
/*      */       case 1:
/* 5563 */         double[] doubles = (double[])(double[])outputs[i];
/* 5564 */         double[] retDoubles = new double[outCount];
/* 5565 */         System.arraycopy(doubles, result.getNumberOfElements() - outCount, retDoubles, 0, outCount);
/* 5566 */         ret[i] = retDoubles;
/* 5567 */         break;
/*      */       case 2:
/* 5569 */         int[] ints = (int[])(int[])outputs[i];
/* 5570 */         int[] retInts = new int[outCount];
/* 5571 */         System.arraycopy(ints, result.getNumberOfElements() - outCount, retInts, 0, outCount);
/* 5572 */         ret[i] = retInts;
/* 5573 */         break;
/*      */       case 3:
/* 5575 */         Object[] objects = (Object[])(Object[])outputs[i];
/* 5576 */         Object[] retObjs = new Object[outCount];
/* 5577 */         System.arraycopy(objects, result.getNumberOfElements() - outCount, retObjs, 0, outCount);
/* 5578 */         ret[i] = retObjs;
/*      */       }
/*      */     }
/*      */ 
/* 5582 */     return ret;
/*      */   }
/*      */ 
/*      */   private Period noFilterPeriod(Period period)
/*      */   {
/* 5590 */     if (period == null) {
/* 5591 */       return null;
/*      */     }
/* 5593 */     if ((period == Period.DAILY_SKIP_SUNDAY) || (period == Period.DAILY_SUNDAY_IN_MONDAY))
/*      */     {
/* 5595 */       return Period.DAILY;
/*      */     }
/* 5597 */     return period;
/*      */   }
/*      */ 
/*      */   private Period dailyFilterPeriod(Period period)
/*      */   {
/* 5603 */     if ((period == Period.DAILY) || (period == Period.DAILY_SKIP_SUNDAY) || (period == Period.DAILY_SUNDAY_IN_MONDAY))
/*      */     {
/* 5605 */       return period;
/*      */     }
/* 5607 */     return null;
/*      */   }
/*      */ 
/*      */   private Object getIndicatorInputData(InputParameterInfo inputParameterInfo, IIndicators.AppliedPrice appliedPrice, CandleData[] timeData)
/*      */   {
/* 5612 */     int dataSize = timeData.length;
/*      */     int k;
/* 5613 */     switch (19.$SwitchMap$com$dukascopy$api$indicators$InputParameterInfo$Type[inputParameterInfo.getType().ordinal()]) {
/*      */     case 2:
/* 5615 */       double[] open = new double[dataSize];
/* 5616 */       double[] high = new double[dataSize];
/* 5617 */       double[] low = new double[dataSize];
/* 5618 */       double[] close = new double[dataSize];
/* 5619 */       double[] volume = new double[dataSize];
/* 5620 */       k = 0;
/* 5621 */       for (CandleData candle : timeData) {
/* 5622 */         open[k] = candle.open;
/* 5623 */         high[k] = candle.high;
/* 5624 */         low[k] = candle.low;
/* 5625 */         close[k] = candle.close;
/* 5626 */         volume[k] = candle.vol;
/* 5627 */         k++;
/*      */       }
/* 5629 */       return new double[][] { open, close, high, low, volume };
/*      */     case 1:
/* 5631 */       double[] data = new double[dataSize];
/* 5632 */       switch (19.$SwitchMap$com$dukascopy$api$IIndicators$AppliedPrice[appliedPrice.ordinal()]) {
/*      */       case 1:
/* 5634 */         k = 0;
/* 5635 */         for (CandleData candle : timeData) {
/* 5636 */           data[k] = candle.close;
/* 5637 */           k++;
/*      */         }
/* 5639 */         break;
/*      */       case 2:
/* 5641 */         k = 0;
/* 5642 */         for (CandleData candle : timeData) {
/* 5643 */           data[k] = candle.high;
/* 5644 */           k++;
/*      */         }
/* 5646 */         break;
/*      */       case 3:
/* 5648 */         k = 0;
/* 5649 */         for (CandleData candle : timeData) {
/* 5650 */           data[k] = candle.low;
/* 5651 */           k++;
/*      */         }
/* 5653 */         break;
/*      */       case 5:
/* 5655 */         k = 0;
/* 5656 */         for (CandleData candle : timeData) {
/* 5657 */           data[k] = candle.open;
/* 5658 */           k++;
/*      */         }
/* 5660 */         break;
/*      */       case 4:
/* 5662 */         k = 0;
/* 5663 */         for (CandleData candle : timeData) {
/* 5664 */           data[k] = ((candle.high + candle.low) / 2.0D);
/* 5665 */           k++;
/*      */         }
/* 5667 */         break;
/*      */       case 6:
/* 5669 */         k = 0;
/* 5670 */         for (CandleData candle : timeData) {
/* 5671 */           data[k] = ((candle.high + candle.low + candle.close) / 3.0D);
/* 5672 */           k++;
/*      */         }
/* 5674 */         break;
/*      */       case 7:
/* 5676 */         k = 0;
/* 5677 */         for (CandleData candle : timeData) {
/* 5678 */           data[k] = ((candle.high + candle.low + candle.close + candle.close) / 4.0D);
/* 5679 */           k++;
/*      */         }
/* 5681 */         break;
/*      */       case 8:
/* 5683 */         k = 0;
/* 5684 */         for (CandleData candle : timeData) {
/* 5685 */           data[k] = candle.time;
/* 5686 */           k++;
/*      */         }
/* 5688 */         break;
/*      */       case 9:
/* 5690 */         k = 0;
/* 5691 */         for (CandleData candle : timeData) {
/* 5692 */           data[k] = candle.vol;
/* 5693 */           k++;
/*      */         }
/*      */       }
/*      */ 
/* 5697 */       return data;
/*      */     case 3:
/* 5699 */       return timeData;
/*      */     }
/* 5701 */     if (!$assertionsDisabled) throw new AssertionError("shouldn't be here");
/* 5702 */     return null;
/*      */   }
/*      */ 
/*      */   protected final CandleData[] putDataInListFromToIndexes(int from, int to, CandleData[] buffer)
/*      */   {
/* 5707 */     CandleData[] data = new CandleData[to - from + 1];
/* 5708 */     System.arraycopy(buffer, from, data, 0, to - from + 1);
/* 5709 */     return data;
/*      */   }
/*      */ 
/*      */   private double[][][] calculateIndicatorInputs(Instrument instrument, Period period, OfferSide side, Collection<AbstractDataProvider.IndicatorData> formulasToRecalculate, CandleData[] timeDataAsk, CandleData[] timeDataBid, double[][][] doubleInputs, boolean isTicksDataType)
/*      */   {
/* 5716 */     double[][][] priceInput = (double[][][])null;
/* 5717 */     for (AbstractDataProvider.IndicatorData formulaData : formulasToRecalculate) {
/* 5718 */       if (formulaData.disabledIndicator) {
/*      */         continue;
/*      */       }
/* 5721 */       IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/* 5722 */       OfferSide[] tickOfferSides = formulaData.indicatorWrapper.getOfferSidesForTicks();
/* 5723 */       IIndicators.AppliedPrice[] appliedPrices = formulaData.indicatorWrapper.getAppliedPricesForCandles();
/* 5724 */       if ((timeDataAsk == null) && (timeDataBid == null))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 5732 */       if ((indicator instanceof ConnectorIndicator)) {
/* 5733 */         ((ConnectorIndicator)indicator).setCurrentInstrument(instrument);
/* 5734 */         ((ConnectorIndicator)indicator).setCurrentPeriod(period);
/*      */       }
/*      */ 
/* 5737 */       int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfInputs(); i < j; i++) {
/* 5738 */         InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/* 5739 */         if ((inputParameterInfo.getOfferSide() != null) || (inputParameterInfo.getPeriod() != null) || (inputParameterInfo.getInstrument() != null)) { if (inputParameterInfo.getOfferSide() != null) if (inputParameterInfo.getOfferSide() != (isTicksDataType ? tickOfferSides[i] : side)) continue; if (((inputParameterInfo.getPeriod() != null) && ((noFilterPeriod(inputParameterInfo.getPeriod()) != period) || (dailyFilterPeriod(inputParameterInfo.getPeriod()) != dailyFilterPeriod(this.dailyFilterPeriod)))) || ((inputParameterInfo.getInstrument() != null) && (inputParameterInfo.getInstrument() != instrument)))
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 5746 */         switch (inputParameterInfo.getType()) {
/*      */         case PRICE:
/* 5748 */           if ((priceInput != null) && ((!isTicksDataType) || (priceInput[tickOfferSides[i].ordinal()] != null))) continue;
/* 5749 */           if (priceInput == null) {
/* 5750 */             priceInput = new double[2][][];
/*      */           }
/* 5752 */           priceInput[(!isTicksDataType ? 0 : tickOfferSides[i].ordinal())] = ((double[][])(double[][])getIndicatorInputData(inputParameterInfo, appliedPrices[i], (!isTicksDataType) || (tickOfferSides[i] == OfferSide.ASK) ? timeDataAsk : timeDataBid)); break;
/*      */         case DOUBLE:
/* 5758 */           if (doubleInputs[tickOfferSides[i].ordinal()][appliedPrices[i].ordinal()] != null) continue;
/* 5759 */           doubleInputs[tickOfferSides[i].ordinal()][appliedPrices[i].ordinal()] = ((double[])(double[])getIndicatorInputData(inputParameterInfo, appliedPrices[i], (!isTicksDataType) || (tickOfferSides[i] == OfferSide.ASK) ? timeDataAsk : timeDataBid));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5768 */     return priceInput;
/*      */   }
/*      */ 
/*      */   protected void copyToIndicatorOutput(int from, int to, int recalculateStart, Object outputData, int firstValueIndex, int numberOfElements, Object inputData, OutputParameterInfo.Type type, int lastIndex, int bufferLength)
/*      */   {
/*      */     int toSkip;
/*      */     int toSkip;
/* 5785 */     if (recalculateStart <= from)
/* 5786 */       toSkip = from - recalculateStart;
/*      */     else {
/* 5788 */       toSkip = bufferLength - recalculateStart + from;
/*      */     }
/*      */ 
/* 5793 */     int valuesNA = firstValueIndex - toSkip;
/*      */     int outputBufferCounter;
/*      */     int inputBufferCounter;
/* 5796 */     if (valuesNA > 0) {
/* 5797 */       int inputBufferCounter = 0;
/*      */       int outputBufferCounter;
/* 5798 */       if (lastIndex - from + 1 >= valuesNA)
/* 5799 */         outputBufferCounter = from + valuesNA;
/*      */       else
/* 5801 */         throw new RuntimeException("Cannot happen");
/*      */       int outputBufferCounter;
/* 5803 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[type.ordinal()]) {
/*      */       case 2:
/* 5805 */         int[] outputDataInt = (int[])(int[])outputData;
/* 5806 */         Arrays.fill(outputDataInt, from, outputBufferCounter, -2147483648);
/* 5807 */         break;
/*      */       case 1:
/* 5809 */         double[] outputDataDouble = (double[])(double[])outputData;
/* 5810 */         Arrays.fill(outputDataDouble, from, outputBufferCounter, (0.0D / 0.0D));
/* 5811 */         break;
/*      */       case 3:
/* 5813 */         Object[] outputDataObject = (Object[])(Object[])outputData;
/* 5814 */         Arrays.fill(outputDataObject, from, outputBufferCounter, null);
/*      */       }
/*      */     }
/*      */     else {
/* 5818 */       outputBufferCounter = from;
/* 5819 */       inputBufferCounter = toSkip - firstValueIndex;
/* 5820 */       numberOfElements -= inputBufferCounter;
/*      */     }
/*      */ 
/* 5824 */     if (lastIndex - outputBufferCounter + 1 >= numberOfElements) {
/* 5825 */       System.arraycopy(inputData, inputBufferCounter, outputData, outputBufferCounter, numberOfElements);
/*      */ 
/* 5827 */       if (type.equals(OutputParameterInfo.Type.DOUBLE))
/*      */       {
/* 5833 */         double[] outputs = (double[])(double[])outputData;
/* 5834 */         if (outputs != null) {
/* 5835 */           int nanCount = 0;
/* 5836 */           int k = outputBufferCounter;
/*      */ 
/* 5838 */           while ((k > -1) && (Double.isNaN(outputs[k]))) {
/* 5839 */             nanCount++;
/* 5840 */             k--;
/*      */           }
/*      */ 
/* 5844 */           if ((nanCount > 0) && (inputBufferCounter >= nanCount) && (outputBufferCounter >= nanCount))
/* 5845 */             System.arraycopy(inputData, inputBufferCounter - nanCount, outputData, outputBufferCounter - nanCount, nanCount);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 5850 */       throw new RuntimeException("Cannot happen");
/*      */     }
/*      */ 
/* 5855 */     if (to - (outputBufferCounter + numberOfElements) + 1 > 0)
/* 5856 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[type.ordinal()]) {
/*      */       case 2:
/* 5858 */         int[] outputDataInt = (int[])(int[])outputData;
/* 5859 */         Arrays.fill(outputDataInt, outputBufferCounter + numberOfElements, to + 1, -2147483648);
/* 5860 */         break;
/*      */       case 1:
/* 5862 */         double[] outputDataDouble = (double[])(double[])outputData;
/* 5863 */         Arrays.fill(outputDataDouble, outputBufferCounter + numberOfElements, to + 1, (0.0D / 0.0D));
/* 5864 */         break;
/*      */       case 3:
/* 5866 */         Object[] outputDataObject = (Object[])(Object[])outputData;
/* 5867 */         Arrays.fill(outputDataObject, outputBufferCounter + numberOfElements, to + 1, null);
/*      */       }
/*      */   }
/*      */ 
/*      */   private boolean populateIndicatorInputFromDataProvider(Period period, AbstractDataProvider.IndicatorData indicatorData, int inputIndex, long from, long to, int finalLookback, int finalLookforward, int maxNumberOfCandles, int bufferSizeMultiplier)
/*      */   {
/* 5877 */     IIndicator indicator = indicatorData.indicatorWrapper.getIndicator();
/* 5878 */     assert (indicatorData.inputDataProviders[inputIndex] != null) : "Input data provider is null";
/* 5879 */     Period inputPeriod = indicatorData.inputPeriods[inputIndex] == null ? period : indicatorData.inputPeriods[inputIndex];
/* 5880 */     Period inputCandlePeriod = inputPeriod == Period.TICK ? Period.ONE_SEC : inputPeriod;
/*      */ 
/* 5882 */     long latestTo = to = DataCacheUtils.getCandleStartFast(inputCandlePeriod, to);
/*      */ 
/* 5884 */     from = DataCacheUtils.getCandleStartFast(inputCandlePeriod, from);
/*      */ 
/* 5886 */     long latestDataTime = indicatorData.inputDataProviders[inputIndex].getLastLoadedDataTime();
/*      */ 
/* 5888 */     if (latestDataTime == -9223372036854775808L) {
/* 5889 */       LOGGER.debug("WARN: Indicator data provider doesn't have any data");
/* 5890 */       return false;
/*      */     }
/* 5892 */     if (to > latestDataTime) {
/* 5893 */       to = DataCacheUtils.getCandleStartFast(inputCandlePeriod, latestDataTime);
/*      */     }
/* 5895 */     if (from > to) {
/* 5896 */       return false;
/*      */     }
/* 5898 */     int candlesBefore = DataCacheUtils.getCandlesCountBetweenFast(inputCandlePeriod, from, to) + finalLookback;
/* 5899 */     if (candlesBefore == 0) {
/* 5900 */       LOGGER.debug("WARN: Nothing to request from indicator data provider");
/* 5901 */       return false;
/*      */     }
/* 5903 */     if (candlesBefore + finalLookforward > maxNumberOfCandles * bufferSizeMultiplier) {
/* 5904 */       candlesBefore = maxNumberOfCandles * bufferSizeMultiplier - finalLookforward;
/*      */     }
/*      */ 
/* 5907 */     IDataSequence dataSequence = indicatorData.inputDataProviders[inputIndex].getDataSequence(candlesBefore, to, finalLookforward);
/*      */     CandleData[] data;
/* 5909 */     if (inputPeriod == Period.TICK) {
/* 5910 */       TickDataSequence tickSequence = (TickDataSequence)dataSequence;
/* 5911 */       OfferSide offerSide = indicator.getInputParameterInfo(inputIndex).getOfferSide();
/* 5912 */       OfferSide[] tickOfferSides = indicatorData.indicatorWrapper.getOfferSidesForTicks();
/*      */       CandleData[] data;
/*      */       CandleData[] data;
/* 5913 */       if (((offerSide != null) && (offerSide == OfferSide.ASK)) || ((offerSide == null) && (tickOfferSides[inputIndex] == OfferSide.ASK)))
/* 5914 */         data = tickSequence.getOneSecCandlesAsk();
/*      */       else {
/* 5916 */         data = tickSequence.getOneSecCandlesBid();
/*      */       }
/* 5918 */       if ((tickSequence.getOneSecExtraBefore() > 0) || (tickSequence.getOneSecExtraAfter() > 0)) {
/* 5919 */         CandleData[] newData = new CandleData[data.length - tickSequence.getOneSecExtraBefore() - tickSequence.getOneSecExtraAfter()];
/* 5920 */         System.arraycopy(data, tickSequence.getOneSecExtraBefore(), newData, 0, newData.length);
/* 5921 */         data = newData;
/*      */       }
/*      */     } else {
/* 5924 */       CandleDataSequence candleSequence = (CandleDataSequence)dataSequence;
/* 5925 */       data = (CandleData[])candleSequence.getData();
/* 5926 */       if ((dataSequence.getExtraBefore() > 0) || (dataSequence.getExtraAfter() > 0)) {
/* 5927 */         CandleData[] newData = new CandleData[data.length - dataSequence.getExtraBefore() - dataSequence.getExtraAfter()];
/* 5928 */         System.arraycopy(data, dataSequence.getExtraBefore(), newData, 0, newData.length);
/* 5929 */         data = newData;
/*      */       }
/*      */     }
/* 5932 */     if (data.length == 0) {
/* 5933 */       indicator.setInputParameter(inputIndex, getIndicatorInputData(indicatorData.indicatorWrapper.getIndicator().getInputParameterInfo(inputIndex), indicatorData.indicatorWrapper.getAppliedPricesForCandles()[inputIndex], new CandleData[0]));
/*      */ 
/* 5935 */       return true;
/*      */     }
/* 5937 */     int flats = 0;
/* 5938 */     if (latestTo > to)
/* 5939 */       flats = DataCacheUtils.getCandlesCountBetweenFast(inputCandlePeriod, to, latestTo) - 1;
/*      */     CandleData[] correctData;
/* 5942 */     if (flats > 0) {
/* 5943 */       CandleData[] correctData = new CandleData[data.length + flats];
/* 5944 */       System.arraycopy(data, 0, correctData, 0, data.length);
/*      */ 
/* 5947 */       CandleData lastCandle = correctData[(data.length - 1)];
/* 5948 */       int i = 1; long time = DataCacheUtils.getNextCandleStartFast(inputCandlePeriod, lastCandle.time);
/* 5949 */       for (; i <= flats; time = DataCacheUtils.getNextCandleStartFast(inputCandlePeriod, time)) {
/* 5950 */         correctData[(data.length - 1 + i)] = new CandleData(time, lastCandle.close, lastCandle.close, lastCandle.close, lastCandle.close, 0.0D);
/*      */ 
/* 5949 */         i++;
/*      */       }
/*      */     }
/*      */     else {
/* 5953 */       correctData = data;
/*      */     }
/*      */ 
/* 5956 */     indicator.setInputParameter(inputIndex, getIndicatorInputData(indicatorData.indicatorWrapper.getIndicator().getInputParameterInfo(inputIndex), indicatorData.indicatorWrapper.getAppliedPricesForCandles()[inputIndex], correctData));
/*      */ 
/* 5958 */     return true;
/*      */   }
/*      */ 
/*      */   public void calculateIndicators(Instrument instrument, Period period, OfferSide side, Filter filter, int from, int to, Collection<AbstractDataProvider.IndicatorData> formulasToRecalculate, int lastIndex, CandleData[] bufferAsk, CandleData[] bufferBid, boolean isTicksDataType, int maxNumberOfCandles, int bufferSizeMultiplier, Data firstData, AbstractDataProvider<?, ?> parentProvider)
/*      */   {
/*      */     int finalLookback;
/*      */     int finalLookforward;
/*      */     int recalculateStart;
/*      */     CandleData[] timeDataAsk;
/*      */     CandleData[] timeDataBid;
/*      */     long lastInputTime;
/*      */     double[][][] doubleInputs;
/*      */     double[][][] priceInput;
/* 5979 */     label1356: synchronized (formulasToRecalculate) {
/* 5980 */       if ((firstData == null) || (lastIndex == -1))
/*      */       {
/* 5982 */         return;
/*      */       }
/*      */ 
/* 5986 */       finalLookback = 0;
/* 5987 */       finalLookforward = 0;
/* 5988 */       boolean needAsk = false;
/* 5989 */       boolean needBid = false;
/*      */ 
/* 5992 */       for (AbstractDataProvider.IndicatorData formulaData : formulasToRecalculate) {
/* 5993 */         if (formulaData.disabledIndicator)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/* 5998 */         IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/* 5999 */         int lookback = formulaData.lookback;
/* 6000 */         int lookforward = formulaData.lookforward;
/* 6001 */         if (indicator.getIndicatorInfo().isUnstablePeriod())
/*      */         {
/* 6004 */           lookback += (lookback * 4 < 100 ? 100 : lookback * 4);
/*      */         }
/* 6006 */         if (finalLookback < lookback) {
/* 6007 */           finalLookback = lookback;
/*      */         }
/* 6009 */         if (finalLookforward < lookforward) {
/* 6010 */           finalLookforward = lookforward;
/*      */         }
/*      */ 
/* 6013 */         if ((isTicksDataType) && ((!needBid) || (!needAsk))) {
/* 6014 */           int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfInputs(); i < j; i++) {
/* 6015 */             side = formulaData.indicatorWrapper.getOfferSidesForTicks()[i];
/* 6016 */             if (side == OfferSide.ASK)
/* 6017 */               needAsk = true;
/*      */             else {
/* 6019 */               needBid = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 6025 */       if (to != lastIndex)
/*      */       {
/* 6028 */         to = lastIndex - to > finalLookback ? to + finalLookback : lastIndex;
/*      */       }
/* 6030 */       if ((finalLookforward != 0) && (from != 0))
/*      */       {
/* 6033 */         from = from > finalLookforward ? from - finalLookforward : 0;
/*      */       }
/*      */ 
/* 6036 */       recalculateStart = from > finalLookback ? from - finalLookback : 0;
/* 6037 */       int recalculateEnd = lastIndex - to > finalLookforward ? to + finalLookforward : lastIndex;
/*      */ 
/* 6039 */       timeDataAsk = null;
/* 6040 */       timeDataBid = null;
/* 6041 */       if (isTicksDataType) {
/* 6042 */         if (needAsk) {
/* 6043 */           timeDataAsk = putDataInListFromToIndexes(recalculateStart, recalculateEnd, bufferAsk);
/*      */         }
/* 6045 */         if (needBid)
/* 6046 */           timeDataBid = putDataInListFromToIndexes(recalculateStart, recalculateEnd, bufferBid);
/*      */       }
/*      */       else {
/* 6049 */         timeDataAsk = putDataInListFromToIndexes(recalculateStart, recalculateEnd, bufferAsk);
/*      */       }
/*      */       long lastInputTime;
/* 6053 */       if (timeDataAsk != null) {
/* 6054 */         lastInputTime = timeDataAsk[(timeDataAsk.length - 1)].time;
/*      */       }
/*      */       else {
/* 6057 */         lastInputTime = -1L;
/*      */       }
/* 6059 */       if (timeDataBid != null) {
/* 6060 */         long time = timeDataBid[(timeDataBid.length - 1)].time;
/* 6061 */         if (time > lastInputTime) {
/* 6062 */           lastInputTime = time;
/*      */         }
/*      */       }
/*      */ 
/* 6066 */       doubleInputs = new double[OfferSide.values().length][IIndicators.AppliedPrice.values().length];
/* 6067 */       priceInput = calculateIndicatorInputs(instrument, period, side, formulasToRecalculate, timeDataAsk, timeDataBid, doubleInputs, isTicksDataType);
/*      */ 
/* 6070 */       for (AbstractDataProvider.IndicatorData formulaData : formulasToRecalculate) {
/* 6071 */         if (formulaData.disabledIndicator)
/*      */         {
/*      */           continue;
/*      */         }
/* 6075 */         IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/* 6076 */         IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 6077 */         OfferSide[] tickOfferSides = formulaData.indicatorWrapper.getOfferSidesForTicks();
/* 6078 */         IIndicators.AppliedPrice[] appliedPrices = formulaData.indicatorWrapper.getAppliedPricesForCandles();
/*      */ 
/* 6080 */         if ((timeDataAsk == null) && (timeDataBid == null))
/*      */         {
/*      */           continue;
/*      */         }
/* 6084 */         IndicatorContext indicatorContext = formulaData.indicatorWrapper.getIndicatorHolder().getIndicatorContext();
/* 6085 */         FeedDescriptor descriptor = createFeedDescriptor(period, instrument, indicatorInfo.getNumberOfInputs() > 0 ? side : isTicksDataType ? tickOfferSides[0] : OfferSide.BID);
/*      */ 
/* 6087 */         descriptor.setFilter(filter);
/* 6088 */         indicatorContext.setFeedDescriptor(descriptor);
/*      */         try
/*      */         {
/* 6091 */           int dataSize = timeDataAsk == null ? timeDataBid.length : timeDataAsk.length;
/*      */ 
/* 6093 */           int i = 0; for (int j = indicatorInfo.getNumberOfInputs(); ; i++) { if (i >= j) break label1356; InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/* 6095 */             if ((inputParameterInfo.getOfferSide() != null) || (inputParameterInfo.getPeriod() != null) || (inputParameterInfo.getInstrument() != null)) { if (inputParameterInfo.getOfferSide() != null) {
/* 6095 */                 if (inputParameterInfo.getOfferSide() != (isTicksDataType ? tickOfferSides[i] : side));
/* 6095 */               } else if (((inputParameterInfo.getPeriod() != null) && ((noFilterPeriod(inputParameterInfo.getPeriod()) != period) || (dailyFilterPeriod(inputParameterInfo.getPeriod()) != dailyFilterPeriod(this.dailyFilterPeriod)))) || ((inputParameterInfo.getInstrument() != null) && (inputParameterInfo.getInstrument() != instrument)));
/*      */             }
/*      */             else
/*      */             {
/* 6103 */               switch (19.$SwitchMap$com$dukascopy$api$indicators$InputParameterInfo$Type[inputParameterInfo.getType().ordinal()]) {
/*      */               case 2:
/*      */                 try {
/* 6106 */                   indicator.setInputParameter(i, !isTicksDataType ? priceInput[0] : priceInput[tickOfferSides[i].ordinal()]);
/*      */                 }
/*      */                 catch (Throwable t) {
/* 6109 */                   LOGGER.error(t.getMessage(), t);
/* 6110 */                   String error = StrategyWrapper.representError(indicator, t);
/* 6111 */                   NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */                 }
/*      */ 
/* 6390 */                 indicatorContext.resetFeedDescriptor(); break;
/*      */               case 1:
/*      */                 try
/*      */                 {
/* 6117 */                   indicator.setInputParameter(i, !isTicksDataType ? doubleInputs[0][appliedPrices[i].ordinal()] : doubleInputs[tickOfferSides[i].ordinal()][appliedPrices[i].ordinal()]);
/*      */                 }
/*      */                 catch (Throwable t) {
/* 6120 */                   LOGGER.error(t.getMessage(), t);
/* 6121 */                   String error = StrategyWrapper.representError(indicator, t);
/* 6122 */                   NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */                 }
/*      */ 
/* 6390 */                 indicatorContext.resetFeedDescriptor(); break;
/*      */               case 3:
/*      */                 try
/*      */                 {
/* 6128 */                   indicator.setInputParameter(i, (!isTicksDataType) || (tickOfferSides[i] == OfferSide.ASK) ? timeDataAsk : timeDataBid);
/*      */                 } catch (Throwable t) {
/* 6130 */                   LOGGER.error(t.getMessage(), t);
/* 6131 */                   String error = StrategyWrapper.representError(indicator, t);
/* 6132 */                   NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */                 }
/*      */ 
/* 6390 */                 indicatorContext.resetFeedDescriptor(); break;
/*      */               default:
/* 6135 */                 break;
/*      */               }
/*      */             }
/* 6138 */             if (!parentProvider.isActive())
/*      */             {
/* 6390 */               indicatorContext.resetFeedDescriptor(); return;
/*      */             }
/* 6141 */             if (populateIndicatorInputFromDataProvider(period, formulaData, i, bufferAsk[from].time, bufferAsk[to].time, finalLookback, finalLookforward, maxNumberOfCandles, bufferSizeMultiplier))
/*      */               continue;
/* 6143 */             int k = 0; for (int n = indicatorInfo.getNumberOfOutputs(); k < n; k++) {
/* 6144 */               OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(k);
/* 6145 */               switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */               case 2:
/* 6147 */                 Arrays.fill(formulaData.getOutputDataInt()[k], from, to + 1, -2147483648);
/* 6148 */                 break;
/*      */               case 1:
/* 6150 */                 Arrays.fill(formulaData.getOutputDataDouble()[k], from, to + 1, (0.0D / 0.0D));
/* 6151 */                 break;
/*      */               case 3:
/* 6153 */                 Arrays.fill(formulaData.getOutputDataObject()[k], from, to + 1, null);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 6390 */             indicatorContext.resetFeedDescriptor(); break;
/*      */           }
/* 6163 */           Object[] outArrays = new Object[indicatorInfo.getNumberOfOutputs()];
/*      */           IndicatorResult result;
/*      */           IndicatorResult result;
/* 6165 */           if (dataSize <= formulaData.lookback + formulaData.lookforward) {
/* 6166 */             result = new IndicatorResult(0, 0, 0);
/*      */           }
/*      */           else {
/* 6169 */             int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); ; i++) { if (i >= j) break label1792; OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(i);
/* 6171 */               switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */               case 2:
/* 6173 */                 int[] arrayInt = new int[dataSize - (formulaData.lookback + formulaData.lookforward)];
/*      */                 try {
/* 6175 */                   indicator.setOutputParameter(i, arrayInt);
/*      */                 } catch (Throwable t) {
/* 6177 */                   LOGGER.error(t.getMessage(), t);
/* 6178 */                   String error = StrategyWrapper.representError(indicator, t);
/* 6179 */                   NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */                 }
/*      */ 
/* 6390 */                 indicatorContext.resetFeedDescriptor(); break;
/*      */ 
/* 6182 */                 outArrays[i] = arrayInt;
/* 6183 */                 break;
/*      */               case 1:
/* 6185 */                 double[] arrayDouble = new double[dataSize - (formulaData.lookback + formulaData.lookforward)];
/*      */                 try {
/* 6187 */                   indicator.setOutputParameter(i, arrayDouble);
/*      */                 } catch (Throwable t) {
/* 6189 */                   LOGGER.error(t.getMessage(), t);
/* 6190 */                   String error = StrategyWrapper.representError(indicator, t);
/* 6191 */                   NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */                 }
/*      */ 
/* 6390 */                 indicatorContext.resetFeedDescriptor(); break;
/*      */ 
/* 6194 */                 outArrays[i] = arrayDouble;
/* 6195 */                 break;
/*      */               case 3:
/* 6197 */                 Object[] arrayObject = new Object[dataSize - (formulaData.lookback + formulaData.lookforward)];
/*      */                 try {
/* 6199 */                   indicator.setOutputParameter(i, arrayObject);
/*      */                 } catch (Throwable t) {
/* 6201 */                   LOGGER.error(t.getMessage(), t);
/* 6202 */                   String error = StrategyWrapper.representError(indicator, t);
/* 6203 */                   NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */                 }
/*      */ 
/* 6390 */                 indicatorContext.resetFeedDescriptor(); break;
/*      */ 
/* 6206 */                 outArrays[i] = arrayObject;
/*      */               }
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/* 6212 */               result = indicator.calculate(0, dataSize - 1);
/*      */             } catch (TaLibException e) {
/* 6214 */               Throwable t = e.getCause();
/* 6215 */               LOGGER.error(t.getMessage(), t);
/* 6216 */               String error = StrategyWrapper.representError(indicator, t);
/* 6217 */               NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */ 
/* 6390 */               indicatorContext.resetFeedDescriptor(); continue;
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/* 6220 */               LOGGER.error(t.getMessage(), t);
/* 6221 */               String error = StrategyWrapper.representError(indicator, t);
/* 6222 */               NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/*      */             }
/*      */ 
/* 6390 */             indicatorContext.resetFeedDescriptor(); continue;
/*      */ 
/* 6226 */             if (result.getNumberOfElements() < dataSize - formulaData.lookback - formulaData.lookforward) {
/* 6227 */               String error = "calculate() method of indicator [" + indicatorInfo.getName() + "] returned less values than expected. Requested from-to [0]-[" + (dataSize - 1) + "], input array size [" + dataSize + "], returned first calculated index [" + result.getFirstValueIndex() + "], number of calculated values [" + result.getNumberOfElements() + "], lookback [" + formulaData.lookback + "], lookforward [" + formulaData.lookforward + "], expected number of elements is [" + (dataSize - formulaData.lookback - formulaData.lookforward) + "]";
/*      */ 
/* 6244 */               LOGGER.error(error);
/* 6245 */               NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/*      */ 
/* 6390 */               indicatorContext.resetFeedDescriptor(); continue;
/*      */             }
/* 6248 */             if (result.getFirstValueIndex() + result.getNumberOfElements() > dataSize) {
/* 6249 */               String error = "calculate() method of indicator [" + indicatorInfo.getName() + "] returned incorrect values. Requested from-to [0]-[" + (dataSize - 1) + "], input array size [" + dataSize + "], returned first calculated index [" + result.getFirstValueIndex() + "], number of calculated values [" + result.getNumberOfElements() + "], lookback [" + formulaData.lookback + "], lookforward [" + formulaData.lookforward + "], first index + number of elements cannot be > input array size";
/*      */ 
/* 6264 */               LOGGER.error(error);
/* 6265 */               NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/*      */ 
/* 6390 */               indicatorContext.resetFeedDescriptor(); continue;
/*      */             }
/* 6268 */             if ((result.getLastValueIndex() == -2147483648) && (result.getNumberOfElements() != 0)) {
/* 6269 */               if (formulaData.lookforward != 0) {
/* 6270 */                 String error = "calculate() method of indicator [" + indicatorInfo.getName() + "] returned result without lastValueIndex set. This is only allowed when lookforward is equals to zero";
/*      */ 
/* 6273 */                 LOGGER.error(error);
/* 6274 */                 NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/*      */ 
/* 6390 */                 indicatorContext.resetFeedDescriptor(); continue;
/*      */               }
/* 6277 */               result.setLastValueIndex(dataSize - 1);
/*      */             }
/*      */ 
/* 6280 */             if (result.getLastValueIndex() + 1 - result.getFirstValueIndex() < dataSize - formulaData.lookback - formulaData.lookforward)
/*      */             {
/* 6283 */               String error = "calculate() method of indicator [" + indicatorInfo.getName() + "] returned incorrect first value and last value indexes. Requested from-to [0]-[" + (dataSize - 1) + "], input array size [" + dataSize + "], returned first calculated index [" + result.getFirstValueIndex() + "], number of calculated values [" + result.getNumberOfElements() + "], last calculated index (set to max index by default) [" + result.getLastValueIndex() + "], lookback [" + formulaData.lookback + "], lookforward [" + formulaData.lookforward + "]";
/*      */ 
/* 6297 */               LOGGER.error(error);
/* 6298 */               NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, true);
/*      */ 
/* 6390 */               indicatorContext.resetFeedDescriptor(); continue;
/*      */             }
/*      */           }
/* 6303 */           int firstValueIndex = result.getFirstValueIndex();
/* 6304 */           int numberOfElements = result.getNumberOfElements();
/*      */ 
/* 6308 */           if ((indicator instanceof ConnectorIndicator)) {
/* 6309 */             firstValueIndex = result.getFirstValueIndex() < 0 ? 0 : result.getFirstValueIndex();
/* 6310 */             numberOfElements = result.getNumberOfElements() < result.getLastValueIndex() - firstValueIndex ? result.getNumberOfElements() : result.getLastValueIndex() - firstValueIndex;
/*      */           }
/*      */ 
/* 6315 */           synchronized (formulaData)
/*      */           {
/* 6317 */             if ((lastInputTime > 0L) && (formulaData.getLastTime() != lastInputTime)) {
/* 6318 */               formulaData.setLastTime(lastInputTime);
/* 6319 */               formulaData.lastValues = new Object[indicatorInfo.getNumberOfOutputs()];
/*      */             }
/*      */ 
/* 6322 */             int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 6323 */               OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(i);
/* 6324 */               switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */               case 2:
/* 6326 */                 if (numberOfElements == 0)
/* 6327 */                   Arrays.fill(formulaData.getOutputDataInt()[i], from, to + 1, -2147483648);
/*      */                 else {
/* 6329 */                   copyToIndicatorOutput(from, to, recalculateStart, formulaData.getOutputDataInt()[i], firstValueIndex, numberOfElements, outArrays[i], outputParameterInfo.getType(), lastIndex, bufferAsk.length);
/*      */                 }
/*      */ 
/* 6342 */                 if (formulaData.getLastTime() != lastInputTime) continue;
/* 6343 */                 formulaData.lastValues[i] = Integer.valueOf(formulaData.getOutputDataInt()[i][lastIndex]); break;
/*      */               case 1:
/* 6347 */                 if (numberOfElements == 0)
/* 6348 */                   Arrays.fill(formulaData.getOutputDataDouble()[i], from, to + 1, (0.0D / 0.0D));
/*      */                 else {
/* 6350 */                   copyToIndicatorOutput(from, to, recalculateStart, formulaData.getOutputDataDouble()[i], firstValueIndex, numberOfElements, outArrays[i], outputParameterInfo.getType(), lastIndex, bufferAsk.length);
/*      */                 }
/*      */ 
/* 6363 */                 if (formulaData.getLastTime() != lastInputTime) continue;
/* 6364 */                 formulaData.lastValues[i] = Double.valueOf(formulaData.getOutputDataDouble()[i][lastIndex]); break;
/*      */               case 3:
/* 6368 */                 if (numberOfElements == 0)
/* 6369 */                   Arrays.fill(formulaData.getOutputDataObject()[i], from, to + 1, null);
/*      */                 else {
/* 6371 */                   copyToIndicatorOutput(from, to, recalculateStart, formulaData.getOutputDataObject()[i], firstValueIndex, numberOfElements, outArrays[i], outputParameterInfo.getType(), lastIndex, bufferAsk.length);
/*      */                 }
/*      */ 
/* 6381 */                 if (formulaData.getLastTime() != lastInputTime) continue;
/* 6382 */                 formulaData.lastValues[i] = formulaData.getOutputDataObject()[i][lastIndex];
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/* 6390 */           indicatorContext.resetFeedDescriptor();
/*      */         }
/*      */       }
/*      */     }
/* 6394 */     label1792:
/*      */   }
/*      */ 
/*      */   public Object[] calculateIndicator(IFeedDescriptor feedDescriptor, OfferSide[] offerSides, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, long from, long to)
/*      */     throws JFException
/*      */   {
/* 6406 */     if (feedDescriptor == null) {
/* 6407 */       throw new JFException("FeedDescriptor is null");
/*      */     }
/* 6409 */     if (feedDescriptor.getInstrument() == null) {
/* 6410 */       throw new JFException("Instrument is null");
/*      */     }
/*      */ 
/* 6413 */     if ((DataType.TICKS.equals(feedDescriptor.getDataType())) || (DataType.TIME_PERIOD_AGGREGATION.equals(feedDescriptor.getDataType())))
/*      */     {
/* 6417 */       return calculateIndicator(feedDescriptor.getInstrument(), feedDescriptor.getPeriod(), offerSides, functionName, inputTypes, optParams, from, to);
/*      */     }
/*      */     try
/*      */     {
/* 6421 */       checkSide(offerSides);
/* 6422 */       this.history.validateTimeInterval(feedDescriptor.getInstrument(), from, to);
/* 6423 */       IndicatorHolder indicatorHolder = getCachedIndicator(functionName);
/* 6424 */       if (indicatorHolder == null)
/* 6425 */         throw new JFException("Indicator with name " + functionName + " was not found");
/*      */       try
/*      */       {
/* 6428 */         setOptParams(indicatorHolder.getIndicator(), optParams);
/* 6429 */         int[] lookSides = calculateLookbackLookforward(indicatorHolder.getIndicator(), optParams);
/*      */ 
/* 6431 */         Object[] arrayOfObject = calculateIndicator(feedDescriptor, offerSides, inputTypes, from, to, indicatorHolder, lookSides[0], lookSides[1], lookSides[2]);
/*      */         return arrayOfObject;
/*      */       }
/*      */       finally
/*      */       {
/* 6433 */         indicatorHolder.getIndicatorContext().setFeedDescriptor(null);
/* 6434 */         cacheIndicator(functionName, indicatorHolder);
/*      */       }
/*      */     } catch (JFException e) {
/* 6437 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 6439 */       throw e; } catch (Exception e) {
/*      */     }
/* 6441 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(IFeedDescriptor feedDescriptor, OfferSide[] offerSides, IIndicators.AppliedPrice[] inputTypes, long from, long to, IndicatorHolder indicatorHolder, int indicatorLookback, int lookBack, int lookForward)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 6456 */     int inputLength = feedDataToIndicator(new BarsLoadable(feedDescriptor, from, to, lookBack, lookForward)
/*      */     {
/*      */       public List<IBar> load(IFeedDescriptor fd) throws JFException
/*      */       {
/* 6460 */         return Indicators.this.loadBarsFromTo(this.val$feedDescriptor, this.val$from, this.val$to, this.val$lookBack, this.val$lookForward);
/*      */       }
/*      */     }
/*      */     , feedDescriptor, offerSides, inputTypes, indicatorHolder, indicatorLookback, lookBack, lookForward);
/*      */ 
/* 6472 */     IIndicator indicator = indicatorHolder.getIndicator();
/*      */ 
/* 6474 */     if (inputLength <= 0)
/*      */     {
/* 6476 */       return getEmptyIndicatorCalculationResult(indicator);
/*      */     }
/*      */ 
/* 6479 */     return calculateIndicator(null, from, to, indicator, indicatorLookback, lookBack, lookForward, inputLength);
/*      */   }
/*      */ 
/*      */   private List<IBar> loadBarsFromTo(IFeedDescriptor feedDescriptor, long from, long to, int lookBack, int lookForward)
/*      */     throws JFException
/*      */   {
/* 6490 */     DataType dataType = feedDescriptor.getDataType();
/*      */     List converedResult;
/* 6493 */     if (DataType.PRICE_RANGE_AGGREGATION.equals(dataType)) {
/* 6494 */       List result = loadBarsFromTo(new BarsLoadable(feedDescriptor, from, to)
/*      */       {
/*      */         public List<IRangeBar> load(IFeedDescriptor fd) throws JFException
/*      */         {
/* 6498 */           return Indicators.this.history.getRangeBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), this.val$from, this.val$to);
/*      */         }
/*      */       }
/*      */       , new BarsLoadable(feedDescriptor, lookBack, from)
/*      */       {
/*      */         public List<IRangeBar> load(IFeedDescriptor fd)
/*      */           throws JFException
/*      */         {
/* 6504 */           return Indicators.this.history.getRangeBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), this.val$lookBack, DataCacheUtils.getPreviousPriceAggregationBarStart(this.val$from), 0);
/*      */         }
/*      */       }
/*      */       , new BarsLoadable(feedDescriptor, to, lookForward)
/*      */       {
/*      */         public List<IRangeBar> load(IFeedDescriptor fd)
/*      */           throws JFException
/*      */         {
/* 6510 */           return Indicators.this.history.getRangeBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), 0, DataCacheUtils.getNextPriceAggregationBarStart(this.val$to), this.val$lookForward);
/*      */         }
/*      */       }
/*      */       , lookBack, lookForward);
/*      */ 
/* 6516 */       converedResult = new ArrayList(result);
/*      */     }
/*      */     else
/*      */     {
/*      */       List converedResult;
/* 6518 */       if (DataType.POINT_AND_FIGURE.equals(dataType)) {
/* 6519 */         List result = loadBarsFromTo(new BarsLoadable(feedDescriptor, from, to)
/*      */         {
/*      */           public List<IPointAndFigure> load(IFeedDescriptor fd) throws JFException
/*      */           {
/* 6523 */             return Indicators.this.history.getPointAndFigures(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), this.val$feedDescriptor.getReversalAmount(), this.val$from, this.val$to);
/*      */           }
/*      */         }
/*      */         , new BarsLoadable(feedDescriptor, lookBack, from)
/*      */         {
/*      */           public List<IPointAndFigure> load(IFeedDescriptor fd)
/*      */             throws JFException
/*      */           {
/* 6529 */             return Indicators.this.history.getPointAndFigures(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), this.val$feedDescriptor.getReversalAmount(), this.val$lookBack, DataCacheUtils.getPreviousPriceAggregationBarStart(this.val$from), 0);
/*      */           }
/*      */         }
/*      */         , new BarsLoadable(feedDescriptor, to, lookForward)
/*      */         {
/*      */           public List<IPointAndFigure> load(IFeedDescriptor fd)
/*      */             throws JFException
/*      */           {
/* 6535 */             return Indicators.this.history.getPointAndFigures(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), this.val$feedDescriptor.getReversalAmount(), 0, DataCacheUtils.getNextPriceAggregationBarStart(this.val$to), this.val$lookForward);
/*      */           }
/*      */         }
/*      */         , lookBack, lookForward);
/*      */ 
/* 6541 */         converedResult = new ArrayList(result);
/*      */       }
/*      */       else
/*      */       {
/*      */         List converedResult;
/* 6543 */         if (DataType.TICK_BAR.equals(dataType)) {
/* 6544 */           List result = loadBarsFromTo(new BarsLoadable(feedDescriptor, from, to)
/*      */           {
/*      */             public List<ITickBar> load(IFeedDescriptor fd) throws JFException
/*      */             {
/* 6548 */               return Indicators.this.history.getTickBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getTickBarSize(), this.val$from, this.val$to);
/*      */             }
/*      */           }
/*      */           , new BarsLoadable(feedDescriptor, lookBack, from)
/*      */           {
/*      */             public List<ITickBar> load(IFeedDescriptor fd)
/*      */               throws JFException
/*      */             {
/* 6554 */               return Indicators.this.history.getTickBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getTickBarSize(), this.val$lookBack, DataCacheUtils.getPreviousPriceAggregationBarStart(this.val$from), 0);
/*      */             }
/*      */           }
/*      */           , new BarsLoadable(feedDescriptor, to, lookForward)
/*      */           {
/*      */             public List<ITickBar> load(IFeedDescriptor fd)
/*      */               throws JFException
/*      */             {
/* 6560 */               return Indicators.this.history.getTickBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getTickBarSize(), 0, DataCacheUtils.getNextPriceAggregationBarStart(this.val$to), this.val$lookForward);
/*      */             }
/*      */           }
/*      */           , lookBack, lookForward);
/*      */ 
/* 6566 */           converedResult = new ArrayList(result);
/*      */         }
/*      */         else
/*      */         {
/*      */           List converedResult;
/* 6568 */           if (DataType.RENKO.equals(dataType)) {
/* 6569 */             List result = loadBarsFromTo(new BarsLoadable(feedDescriptor, from, to)
/*      */             {
/*      */               public List<IRenkoBar> load(IFeedDescriptor fd) throws JFException
/*      */               {
/* 6573 */                 return Indicators.this.history.getRenkoBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), this.val$from, this.val$to);
/*      */               }
/*      */             }
/*      */             , new BarsLoadable(feedDescriptor, lookBack, from)
/*      */             {
/*      */               public List<IRenkoBar> load(IFeedDescriptor fd)
/*      */                 throws JFException
/*      */               {
/* 6579 */                 return Indicators.this.history.getRenkoBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), this.val$lookBack, DataCacheUtils.getPreviousPriceAggregationBarStart(this.val$from), 0);
/*      */               }
/*      */             }
/*      */             , new BarsLoadable(feedDescriptor, to, lookForward)
/*      */             {
/*      */               public List<IRenkoBar> load(IFeedDescriptor fd)
/*      */                 throws JFException
/*      */               {
/* 6585 */                 return Indicators.this.history.getRenkoBars(this.val$feedDescriptor.getInstrument(), this.val$feedDescriptor.getOfferSide(), this.val$feedDescriptor.getPriceRange(), 0, DataCacheUtils.getNextPriceAggregationBarStart(this.val$to), this.val$lookForward);
/*      */               }
/*      */             }
/*      */             , lookBack, lookForward);
/*      */ 
/* 6591 */             converedResult = new ArrayList(result);
/*      */           }
/*      */           else
/*      */           {
/* 6595 */             throw new JFException("Unsupported data type [" + dataType + "]");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     List converedResult;
/* 6598 */     return converedResult;
/*      */   }
/*      */ 
/*      */   private <T extends IBar> List<T> loadBarsFromTo(BarsLoadable<T> fromToLoadable, BarsLoadable<T> lookBackLoadable, BarsLoadable<T> lookForwardLoadable, int lookBack, int lookForward)
/*      */     throws JFException
/*      */   {
/* 6612 */     List rangeBars = fromToLoadable.load(null);
/* 6613 */     List lookBackBars = null;
/* 6614 */     List lookForwardBars = null;
/*      */ 
/* 6616 */     if (lookBack > 0) {
/* 6617 */       lookBackBars = lookBackLoadable.load(null);
/*      */     }
/*      */ 
/* 6620 */     if (lookForward > 0) {
/* 6621 */       lookForwardBars = lookForwardLoadable.load(null);
/*      */     }
/*      */ 
/* 6624 */     List result = new ArrayList();
/*      */ 
/* 6626 */     if (lookBackBars != null) {
/* 6627 */       result.addAll(lookBackBars);
/*      */     }
/*      */ 
/* 6630 */     result.addAll(rangeBars);
/*      */ 
/* 6632 */     if (lookForwardBars != null) {
/* 6633 */       result.addAll(lookForwardBars);
/*      */     }
/*      */ 
/* 6636 */     return result;
/*      */   }
/*      */ 
/*      */   public Object[] calculateIndicator(IFeedDescriptor feedDescriptor, OfferSide[] offerSides, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, int shift)
/*      */     throws JFException
/*      */   {
/* 6648 */     if (feedDescriptor == null) {
/* 6649 */       throw new JFException("FeedDescriptor is null");
/*      */     }
/*      */ 
/* 6652 */     if ((DataType.TICKS.equals(feedDescriptor.getDataType())) || (DataType.TIME_PERIOD_AGGREGATION.equals(feedDescriptor.getDataType())))
/*      */     {
/* 6656 */       return calculateIndicator(feedDescriptor.getInstrument(), feedDescriptor.getPeriod(), offerSides, functionName, inputTypes, optParams, shift);
/*      */     }
/*      */     try
/*      */     {
/* 6660 */       IndicatorHolder indicatorHolder = getCachedIndicator(functionName);
/*      */       try {
/* 6662 */         checkSide(offerSides);
/* 6663 */         checkShiftPositive(shift);
/* 6664 */         if (indicatorHolder == null) {
/* 6665 */           throw new JFException("Indicator with name " + functionName + " was not found");
/*      */         }
/* 6667 */         int[] lookSides = calculateLookbackLookforward(indicatorHolder.getIndicator(), optParams);
/* 6668 */         Object[] arrayOfObject = calculateIndicator(feedDescriptor, offerSides, inputTypes, shift, indicatorHolder, lookSides[0], lookSides[1], lookSides[2]);
/*      */         return arrayOfObject;
/*      */       }
/*      */       finally
/*      */       {
/* 6670 */         if (indicatorHolder != null) {
/* 6671 */           indicatorHolder.getIndicatorContext().setFeedDescriptor(null);
/* 6672 */           cacheIndicator(functionName, indicatorHolder);
/*      */         }
/*      */       }
/*      */     } catch (JFException e) {
/* 6676 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 6678 */       throw e; } catch (Exception e) {
/*      */     }
/* 6680 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   protected Object[] calculateIndicator(IFeedDescriptor feedDescriptor, OfferSide[] offerSides, IIndicators.AppliedPrice[] inputTypes, int shift, IndicatorHolder indicatorHolder, int indicatorLookback, int lookback, int lookforward)
/*      */     throws JFException, DataCacheException
/*      */   {
/* 6695 */     int inputLength = feedDataToIndicator(new BarsLoadable(shift, lookback, lookforward)
/*      */     {
/*      */       public List<IBar> load(IFeedDescriptor feedDescriptor) throws JFException
/*      */       {
/* 6699 */         return Indicators.this.loadBarsByShift(feedDescriptor, this.val$shift, this.val$lookback, this.val$lookforward);
/*      */       }
/*      */     }
/*      */     , feedDescriptor, offerSides, inputTypes, indicatorHolder, indicatorLookback, lookback, lookforward);
/*      */ 
/* 6711 */     IIndicator indicator = indicatorHolder.getIndicator();
/*      */ 
/* 6713 */     if (inputLength <= 0)
/*      */     {
/* 6715 */       return getEmptyIndicatorCalculationResult(indicator);
/*      */     }
/*      */ 
/* 6719 */     return calculateIndicator(indicator, indicatorLookback, lookback, lookforward, inputLength);
/*      */   }
/*      */ 
/*      */   private List<IBar> loadBarsByShift(IFeedDescriptor feedDescriptor, int shift, int lookback, int lookforward)
/*      */     throws JFException
/*      */   {
/* 6729 */     if (lookforward > shift) {
/* 6730 */       return null;
/*      */     }
/*      */ 
/* 6733 */     int finalShift = shift - lookforward;
/* 6734 */     IBar shiftedBar = null;
/*      */ 
/* 6736 */     if (DataType.PRICE_RANGE_AGGREGATION.equals(feedDescriptor.getDataType())) {
/* 6737 */       shiftedBar = this.history.getRangeBar(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), finalShift);
/*      */     }
/* 6739 */     else if (DataType.POINT_AND_FIGURE.equals(feedDescriptor.getDataType())) {
/* 6740 */       shiftedBar = this.history.getPointAndFigure(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), feedDescriptor.getReversalAmount(), finalShift);
/*      */     }
/* 6742 */     else if (DataType.TICK_BAR.equals(feedDescriptor.getDataType())) {
/* 6743 */       shiftedBar = this.history.getTickBar(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getTickBarSize(), finalShift);
/*      */     }
/* 6745 */     else if (DataType.RENKO.equals(feedDescriptor.getDataType())) {
/* 6746 */       shiftedBar = this.history.getRenkoBar(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), finalShift);
/*      */     }
/*      */     else {
/* 6749 */       throw new JFException("Unsupported data type [" + feedDescriptor.getDataType() + "]");
/*      */     }
/*      */ 
/* 6752 */     if (shiftedBar == null) {
/* 6753 */       return null;
/*      */     }
/*      */ 
/* 6756 */     List result = new ArrayList();
/*      */ 
/* 6758 */     if (lookback + lookforward <= 0) {
/* 6759 */       result.add(shiftedBar);
/*      */     }
/*      */     else {
/* 6762 */       long time = shiftedBar.getTime();
/* 6763 */       int beforeCount = lookback + lookforward + 1;
/*      */ 
/* 6765 */       if (DataType.PRICE_RANGE_AGGREGATION.equals(feedDescriptor.getDataType())) {
/* 6766 */         List bars = this.history.getRangeBars(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), beforeCount, time, 0);
/* 6767 */         result.addAll(bars);
/*      */       }
/* 6769 */       else if (DataType.POINT_AND_FIGURE.equals(feedDescriptor.getDataType())) {
/* 6770 */         List bars = this.history.getPointAndFigures(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), feedDescriptor.getReversalAmount(), beforeCount, time, 0);
/* 6771 */         result.addAll(bars);
/*      */       }
/* 6773 */       else if (DataType.TICK_BAR.equals(feedDescriptor.getDataType())) {
/* 6774 */         List bars = this.history.getTickBars(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getTickBarSize(), beforeCount, time, 0);
/* 6775 */         result.addAll(bars);
/*      */       }
/*      */     }
/*      */ 
/* 6779 */     return result.isEmpty() ? null : result;
/*      */   }
/*      */ 
/*      */   public Object[] calculateIndicator(IFeedDescriptor feedDescriptor, OfferSide[] offerSides, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*      */     throws JFException
/*      */   {
/* 6794 */     if (feedDescriptor == null) {
/* 6795 */       throw new JFException("FeedDescriptor is null");
/*      */     }
/*      */ 
/* 6798 */     if (feedDescriptor.getInstrument() == null) {
/* 6799 */       throw new JFException("Instrument is null");
/*      */     }
/*      */ 
/* 6802 */     if ((DataType.TICKS.equals(feedDescriptor.getDataType())) || (DataType.TIME_PERIOD_AGGREGATION.equals(feedDescriptor.getDataType())))
/*      */     {
/* 6806 */       return calculateIndicator(feedDescriptor.getInstrument(), feedDescriptor.getPeriod(), offerSides, functionName, inputTypes, optParams, Filter.NO_FILTER, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 6811 */       IndicatorHolder indicatorHolder = getCachedIndicator(functionName);
/*      */       try {
/* 6813 */         checkSide(offerSides);
/* 6814 */         this.history.validateBeforeTimeAfter(feedDescriptor.getInstrument(), numberOfBarsBefore, time, numberOfBarsAfter);
/* 6815 */         if (indicatorHolder == null) {
/* 6816 */           throw new JFException("Indicator with name " + functionName + " was not found");
/*      */         }
/* 6818 */         int[] lookSides = calculateLookbackLookforward(indicatorHolder.getIndicator(), optParams);
/* 6819 */         Object[] arrayOfObject = calculateIndicator(feedDescriptor, offerSides, inputTypes, numberOfBarsBefore, time, numberOfBarsAfter, indicatorHolder, lookSides[0], lookSides[1], lookSides[2]);
/*      */         return arrayOfObject;
/*      */       }
/*      */       finally
/*      */       {
/* 6821 */         if (indicatorHolder != null) {
/* 6822 */           indicatorHolder.getIndicatorContext().setFeedDescriptor(null);
/* 6823 */           cacheIndicator(functionName, indicatorHolder);
/*      */         }
/*      */       }
/*      */     } catch (JFException e) {
/* 6827 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 6829 */       throw e; } catch (Exception e) {
/*      */     }
/* 6831 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   private Object[] calculateIndicator(IFeedDescriptor feedDescriptor, OfferSide[] offerSides, IIndicators.AppliedPrice[] inputTypes, int numberOfBarsBefore, long time, int numberOfBarsAfter, IndicatorHolder indicatorHolder, int indicatorLookback, int lookback, int lookforward)
/*      */     throws JFException
/*      */   {
/* 6848 */     int inputLength = feedDataToIndicator(new BarsLoadable(numberOfBarsBefore, time, numberOfBarsAfter, lookback, lookforward)
/*      */     {
/*      */       public List<IBar> load(IFeedDescriptor feedDescriptor) throws JFException
/*      */       {
/* 6852 */         return Indicators.this.loadBarsBeforeTimeAfter(feedDescriptor, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, this.val$lookback, this.val$lookforward);
/*      */       }
/*      */     }
/*      */     , feedDescriptor, offerSides, inputTypes, indicatorHolder, indicatorLookback, lookback, lookforward);
/*      */ 
/* 6864 */     IIndicator indicator = indicatorHolder.getIndicator();
/*      */ 
/* 6866 */     if (inputLength <= 0)
/*      */     {
/* 6868 */       return getEmptyIndicatorCalculationResult(indicator);
/*      */     }
/*      */ 
/* 6871 */     return calculateIndicator(numberOfBarsBefore, time, numberOfBarsAfter, indicator, indicatorLookback, lookback, lookforward, inputLength);
/*      */   }
/*      */ 
/*      */   private List<IBar> loadBarsBeforeTimeAfter(IFeedDescriptor feedDescriptor, int numberOfBarsBefore, long time, int numberOfBarsAfter, int lookback, int lookforward)
/*      */     throws JFException
/*      */   {
/* 6883 */     List data = new ArrayList();
/*      */ 
/* 6885 */     int beforeCount = numberOfBarsBefore + lookback;
/* 6886 */     int afterCount = numberOfBarsAfter + lookforward;
/*      */ 
/* 6888 */     if (DataType.PRICE_RANGE_AGGREGATION.equals(feedDescriptor.getDataType())) {
/* 6889 */       List bars = this.history.getRangeBars(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), beforeCount, time, afterCount);
/* 6890 */       data.addAll(bars);
/*      */     }
/* 6892 */     else if (DataType.POINT_AND_FIGURE.equals(feedDescriptor.getDataType())) {
/* 6893 */       List bars = this.history.getPointAndFigures(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), feedDescriptor.getReversalAmount(), beforeCount, time, afterCount);
/* 6894 */       data.addAll(bars);
/*      */     }
/* 6896 */     else if (DataType.TICK_BAR.equals(feedDescriptor.getDataType())) {
/* 6897 */       List bars = this.history.getTickBars(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getTickBarSize(), beforeCount, time, afterCount);
/* 6898 */       data.addAll(bars);
/*      */     }
/* 6900 */     else if (DataType.RENKO.equals(feedDescriptor.getDataType())) {
/* 6901 */       List bars = this.history.getRenkoBars(feedDescriptor.getInstrument(), feedDescriptor.getOfferSide(), feedDescriptor.getPriceRange(), beforeCount, time, afterCount);
/* 6902 */       data.addAll(bars);
/*      */     }
/*      */     else {
/* 6905 */       throw new JFException("Unsupported data type [" + feedDescriptor.getDataType() + "]");
/*      */     }
/*      */ 
/* 6908 */     List result = data.size() < beforeCount + afterCount ? null : data;
/* 6909 */     return result;
/*      */   }
/*      */ 
/*      */   private Object[] getEmptyIndicatorCalculationResult(IIndicator indicator) {
/* 6913 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*      */ 
/* 6915 */     Object[] ret = new Object[indicatorInfo.getNumberOfOutputs()];
/* 6916 */     int k = 0; for (int l = indicatorInfo.getNumberOfOutputs(); k < l; k++) {
/* 6917 */       OutputParameterInfo outInfo = indicator.getOutputParameterInfo(k);
/* 6918 */       switch (19.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outInfo.getType().ordinal()]) {
/*      */       case 1:
/* 6920 */         ret[k] = Double.valueOf((0.0D / 0.0D));
/* 6921 */         break;
/*      */       case 2:
/* 6923 */         ret[k] = Integer.valueOf(-2147483648);
/* 6924 */         break;
/*      */       case 3:
/* 6926 */         ret[k] = null;
/*      */       }
/*      */     }
/*      */ 
/* 6930 */     return ret;
/*      */   }
/*      */ 
/*      */   private int feedDataToIndicator(BarsLoadable<IBar> loadable, IFeedDescriptor feedDescriptor, OfferSide[] offerSides, IIndicators.AppliedPrice[] inputTypes, IndicatorHolder indicatorHolder, int indicatorLookback, int lookback, int lookforward)
/*      */     throws JFException
/*      */   {
/* 6944 */     if (feedDescriptor == null) {
/* 6945 */       throw new JFException("FeedDescriptor is null");
/*      */     }
/*      */ 
/* 6948 */     int inputLength = -2147483648;
/* 6949 */     IIndicator indicator = indicatorHolder.getIndicator();
/* 6950 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 6951 */     IndicatorContext indicatorContext = indicatorHolder.getIndicatorContext();
/*      */ 
/* 6953 */     OfferSide offerSide = indicatorInfo.getNumberOfInputs() > 0 ? offerSides[0] : OfferSide.BID;
/* 6954 */     feedDescriptor.setOfferSide(offerSide);
/* 6955 */     indicatorContext.setFeedDescriptor(feedDescriptor);
/*      */ 
/* 6957 */     int i = 0; for (int j = indicatorInfo.getNumberOfInputs(); i < j; i++) {
/* 6958 */       InputParameterInfo info = indicator.getInputParameterInfo(i);
/*      */ 
/* 6960 */       if ((info.getOfferSide() != null) || (info.getInstrument() != null) || (info.getPeriod() != null)) {
/* 6961 */         throw new JFException("Indicator [" + indicatorInfo.getName() + "] cannot be calculated for data type " + feedDescriptor.getDataType());
/*      */       }
/*      */ 
/* 6964 */       OfferSide currentOfferSide = offerSides[i];
/*      */ 
/* 6966 */       IFeedDescriptor fd = new FeedDescriptor(feedDescriptor);
/* 6967 */       fd.setOfferSide(currentOfferSide);
/*      */ 
/* 6969 */       List bars = loadable.load(fd);
/* 6970 */       Object inputData = null;
/*      */ 
/* 6972 */       if ((bars == null) || (bars.isEmpty())) {
/* 6973 */         return 0;
/*      */       }
/*      */ 
/* 6976 */       inputLength = bars.size();
/* 6977 */       inputData = barsToReal(bars, info.getType(), info.getType() == InputParameterInfo.Type.DOUBLE ? inputTypes[i] : null);
/*      */       try
/*      */       {
/* 6981 */         indicator.setInputParameter(i, inputData);
/*      */       } catch (Throwable t) {
/* 6983 */         LOGGER.error(t.getMessage(), t);
/* 6984 */         String error = StrategyWrapper.representError(indicator, t);
/* 6985 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Error in indicator: " + error, t, true);
/* 6986 */         throw new JFException(t);
/*      */       }
/*      */     }
/*      */ 
/* 6990 */     return inputLength;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   62 */     LOGGER = LoggerFactory.getLogger(Indicators.class);
/*      */   }
/*      */ 
/*      */   private static abstract interface BarsLoadable<T>
/*      */   {
/*      */     public abstract List<T> load(IFeedDescriptor paramIFeedDescriptor)
/*      */       throws JFException;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.Indicators
 * JD-Core Version:    0.6.0
 */