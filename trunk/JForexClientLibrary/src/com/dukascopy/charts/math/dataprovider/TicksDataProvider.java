/*      */ package com.dukascopy.charts.math.dataprovider;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.feed.FeedDescriptor;
/*      */ import com.dukascopy.api.feed.IFeedDescriptor;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.charts.data.datacache.CandleData;
/*      */ import com.dukascopy.charts.data.datacache.Data;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.TickData;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.TimeZone;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class TicksDataProvider extends AbstractDataProvider<TickData, TickDataSequence>
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   private static final int MIN_SHIFT = 200;
/*      */   private static final int MIN_INCREASE = 200;
/*      */   protected TickData[] buffer;
/*   49 */   protected int lastIndex = -1;
/*   50 */   protected long[][] gaps = new long[0][];
/*      */   protected CandleData[] oneSecBufferAsk;
/*      */   protected CandleData[] oneSecBufferBid;
/*   53 */   protected int oneSecLastIndex = -1;
/*      */   protected long loadedNumberOfSeconds;
/*      */   protected long loadedToInSeconds;
/*      */   protected DataCacheRequestData dataCacheRequestData;
/*      */   private volatile boolean requestAtFirstTick;
/*      */   private LiveFeedListener firstDataListener;
/*   67 */   private final Period ALIGNMENT_PERIOD = Period.ONE_SEC;
/*      */ 
/*      */   public TicksDataProvider(Instrument instrument, int maxNumberOfCandles, int bufferSizeMultiplier, boolean requestAtFirstTick, Filter filter, IFeedDataProvider feedDataProvider)
/*      */   {
/*   77 */     super(instrument, Period.TICK, null, maxNumberOfCandles, bufferSizeMultiplier, filter, feedDataProvider);
/*      */ 
/*   87 */     LOGGER.debug("Creating data provider for instrument [" + instrument + "], period [" + this.period + "], maxNumberOfCandles [" + maxNumberOfCandles + "]");
/*      */ 
/*   89 */     this.requestAtFirstTick = requestAtFirstTick;
/*   90 */     this.buffer = new TickData[maxNumberOfCandles * bufferSizeMultiplier * 2];
/*   91 */     this.oneSecBufferAsk = new CandleData[maxNumberOfCandles * bufferSizeMultiplier];
/*   92 */     this.oneSecBufferBid = new CandleData[maxNumberOfCandles * bufferSizeMultiplier];
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*   98 */     this.firstDataListener = new Object()
/*      */     {
/*      */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */       {
/*  106 */         if ((TicksDataProvider.this.requestAtFirstTick) && (TicksDataProvider.this.parentDataProvider != null))
/*  107 */           synchronized (TicksDataProvider.this.parentDataProvider) {
/*  108 */             TicksDataProvider.this.processNewTick(instrument, time, ask, bid, askVol, bidVol);
/*      */           }
/*      */         else
/*  111 */           TicksDataProvider.this.processNewTick(instrument, time, ask, bid, askVol, bidVol);
/*      */       }
/*      */     };
/*  116 */     TickData lastTick = this.feedDataProvider.getLastTick(this.instrument);
/*  117 */     if (lastTick != null) {
/*  118 */       processNewTick(this.instrument, lastTick.time, lastTick.ask, lastTick.bid, lastTick.askVol, lastTick.bidVol);
/*      */     }
/*  120 */     this.feedDataProvider.subscribeToLiveFeed(this.instrument, this.firstDataListener);
/*  121 */     this.cacheDataUpdatedListener = new AbstractDataProvider.CacheDataUpdatedListener(this);
/*  122 */     this.feedDataProvider.addCacheDataUpdatedListener(this.instrument, this.cacheDataUpdatedListener);
/*      */   }
/*      */ 
/*      */   private void processNewTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */   {
/*  127 */     boolean added = false;
/*      */     Data oldFirstData;
/*  128 */     synchronized (this) {
/*  129 */       boolean add = true;
/*  130 */       if ((this.firstData != null) && (((TickData)this.firstData).time > time)) {
/*  131 */         Exception notThrownException = new Exception("[" + (((TickData)this.firstData).time - time) + "] Received tick has older time than previous tick, ignoring");
/*      */ 
/*  133 */         LOGGER.error(notThrownException.getMessage(), notThrownException);
/*  134 */         return;
/*      */       }
/*  136 */       TickData tickData = new TickData(time, ask, bid, askVol, bidVol);
/*  137 */       if ((this.filter != Filter.NO_FILTER) && (this.period.getInterval() <= Period.DAILY.getInterval()))
/*      */       {
/*  139 */         this.cal.setFirstDayOfWeek(2);
/*  140 */         this.cal.setTimeInMillis(time);
/*  141 */         this.cal.set(7, 6);
/*  142 */         this.cal.set(11, 22);
/*  143 */         this.cal.set(12, 0);
/*  144 */         this.cal.set(13, 0);
/*  145 */         this.cal.set(14, 0);
/*  146 */         long weekendStart = this.cal.getTimeInMillis();
/*  147 */         this.cal.set(7, 1);
/*  148 */         this.cal.set(11, 21);
/*  149 */         long weekendEnd = this.cal.getTimeInMillis();
/*  150 */         if ((time >= weekendStart) && (time <= weekendEnd))
/*      */         {
/*  152 */           add = false;
/*      */         }
/*      */       }
/*  155 */       oldFirstData = this.firstData;
/*  156 */       if (add)
/*      */       {
/*  158 */         oldFirstData = this.firstData;
/*  159 */         this.firstData = tickData;
/*      */ 
/*  161 */         added = addFirstDataIfNeeded(oldFirstData == null ? -9223372036854775808L : oldFirstData.time);
/*      */       }
/*  163 */       if ((oldFirstData == null) && (this.requestAtFirstTick)) {
/*  164 */         this.requestAtFirstTick = false;
/*      */ 
/*  166 */         int numberOfSeconds = this.maxNumberOfCandles * this.bufferSizeMultiplier;
/*  167 */         long to = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, time);
/*  168 */         if (this.feedDataProvider != null) {
/*  169 */           DataCacheRequestData requestData = new DataCacheRequestData();
/*  170 */           requestData.numberOfCandlesBefore = 0;
/*  171 */           requestData.numberOfCandlesAfter = 0;
/*  172 */           requestData.time = to;
/*  173 */           requestData.mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/*  174 */           requestData.cancel = false;
/*  175 */           if (assertionsEnabled()) {
/*  176 */             requestData.requestState = new HashMap();
/*  177 */             requestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/*  178 */             TickData[] bufferCopy = new TickData[this.buffer.length];
/*  179 */             System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/*  180 */             requestData.requestState.put("buffer", bufferCopy);
/*  181 */             requestData.requestState.put("firstData", this.firstData);
/*  182 */             requestData.requestState.put("oneSecLastIndex", Integer.valueOf(this.oneSecLastIndex));
/*  183 */             CandleData[] oneSecBufferAskCopy = new CandleData[this.oneSecBufferAsk.length];
/*  184 */             System.arraycopy(this.oneSecBufferAsk, 0, oneSecBufferAskCopy, 0, this.oneSecBufferAsk.length);
/*  185 */             requestData.requestState.put("oneSecBufferAsk", oneSecBufferAskCopy);
/*  186 */             CandleData[] oneSecBufferBidCopy = new CandleData[this.oneSecBufferBid.length];
/*  187 */             System.arraycopy(this.oneSecBufferBid, 0, oneSecBufferBidCopy, 0, this.oneSecBufferBid.length);
/*  188 */             requestData.requestState.put("oneSecBufferBid", oneSecBufferBidCopy);
/*      */           }
/*      */           try {
/*  191 */             AbstractDataProvider.LoadDataProgressListener loadDataProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, requestData);
/*  192 */             if (LOGGER.isDebugEnabled()) {
/*  193 */               SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  194 */               dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  195 */               LOGGER.debug("Requesting last available ticks for instrument [" + instrument + "] number of seconds [" + numberOfSeconds + "] to [" + dateFormat.format(new Date(to)) + "]");
/*      */             }
/*      */ 
/*  198 */             this.feedDataProvider.loadLastAvailableNumberOfTicksDataSynched(instrument, numberOfSeconds, to, this.filter, new LoadDataListener(requestData, true), loadDataProgressListener);
/*      */           }
/*      */           catch (DataCacheException e) {
/*  201 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*  203 */           if (this.oneSecLastIndex != -1) {
/*  204 */             numberOfSeconds -= this.oneSecLastIndex;
/*  205 */             this.loadedNumberOfSeconds = (this.oneSecLastIndex + 1);
/*  206 */             to = this.oneSecBufferAsk[0].time;
/*      */           }
/*      */         }
/*  209 */         if ((this.oneSecLastIndex == -1) || (this.oneSecLastIndex + 1 < numberOfSeconds)) {
/*  210 */           requestHistoryData(numberOfSeconds, 0, to, AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING, this.maxNumberOfCandles * this.bufferSizeMultiplier, DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, time), 0);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  216 */     if (added) {
/*  217 */       long dataChangedFrom = oldFirstData == null ? ((TickData)this.firstData).time : oldFirstData.time;
/*  218 */       long dataChangedTo = ((TickData)this.firstData).time;
/*  219 */       if (sparceIndicatorAttached()) {
/*  220 */         dataChangedFrom = this.buffer[0].time;
/*  221 */         dataChangedTo = this.buffer[this.lastIndex].time;
/*  222 */       } else if (this.formulasMinShift != 0) {
/*  223 */         dataChangedFrom = DataCacheUtils.getTimeForNCandlesBackFast(this.ALIGNMENT_PERIOD, dataChangedFrom, -this.formulasMinShift + 1);
/*      */       }
/*  225 */       fireDataChanged(dataChangedFrom, dataChangedTo, true, false);
/*      */     }
/*      */ 
/*  228 */     fireLastKnownDataChanged(new TickData(time, ask, bid, askVol, bidVol));
/*      */   }
/*      */ 
/*      */   public synchronized TickDataSequence getDataSequence(int numberOfSecondsBefore, long to, int numberOfSecondsAfter)
/*      */   {
/*  233 */     if ((this.parentDataProvider == null) && (numberOfSecondsBefore + numberOfSecondsAfter > this.maxNumberOfCandles)) {
/*  234 */       throw new IllegalArgumentException("Requested items count: " + (numberOfSecondsBefore + numberOfSecondsAfter) + " is bigger than maxNumberOfCandles[" + this.maxNumberOfCandles + "] specified in constructor");
/*      */     }
/*      */ 
/*  237 */     if ((numberOfSecondsBefore < 0) || (numberOfSecondsAfter < 0) || (numberOfSecondsBefore + numberOfSecondsAfter == 0)) {
/*  238 */       throw new IllegalArgumentException("Negative or zero number of candles requested [" + numberOfSecondsBefore + "],[" + numberOfSecondsAfter + "]");
/*      */     }
/*  240 */     if (!this.active) {
/*  241 */       throw new IllegalStateException("DataProvider is not active, activate it first");
/*      */     }
/*  243 */     if (assertionsEnabled()) {
/*  244 */       if ((to == -9223372036854775808L) || (to > getLatestDataTime())) {
/*  245 */         throw new IllegalStateException("DataProvider is not ready yet, waiting for first tick");
/*      */       }
/*  247 */       if (to != DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, to)) {
/*  248 */         SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  249 */         df.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  250 */         throw new IllegalStateException("to time [" + df.format(Long.valueOf(to)) + "] is not a start of a candle with period [" + this.ALIGNMENT_PERIOD + "]");
/*      */       }
/*      */     }
/*      */ 
/*  254 */     int[] oneSecIntervals = { -2147483648, -2147483648, 0, 0 };
/*  255 */     boolean oneSecDataExists = calculateInterval(numberOfSecondsBefore, to, numberOfSecondsAfter, oneSecIntervals, this.oneSecBufferAsk, this.oneSecLastIndex);
/*      */     CandleData[] oneSecTimeDataAsk;
/*      */     CandleData[] oneSecTimeDataBid;
/*  258 */     if (oneSecDataExists) {
/*  259 */       CandleData[] oneSecTimeDataAsk = new CandleData[oneSecIntervals[1] - oneSecIntervals[0] + 1];
/*  260 */       CandleData[] oneSecTimeDataBid = new CandleData[oneSecIntervals[1] - oneSecIntervals[0] + 1];
/*  261 */       System.arraycopy(this.oneSecBufferAsk, oneSecIntervals[0], oneSecTimeDataAsk, 0, oneSecIntervals[1] - oneSecIntervals[0] + 1);
/*  262 */       System.arraycopy(this.oneSecBufferBid, oneSecIntervals[0], oneSecTimeDataBid, 0, oneSecIntervals[1] - oneSecIntervals[0] + 1);
/*      */     } else {
/*  264 */       oneSecTimeDataAsk = new CandleData[0];
/*  265 */       oneSecTimeDataBid = new CandleData[0];
/*      */     }
/*  267 */     int[] intervals = { -2147483648, -2147483648, 0, 0 };
/*      */     TickData[] timeData;
/*  269 */     if ((oneSecDataExists) && (calculateTicksInterval(oneSecIntervals, intervals))) {
/*  270 */       TickData[] timeData = new TickData[intervals[1] - intervals[0] + 1];
/*  271 */       System.arraycopy(this.buffer, intervals[0], timeData, 0, intervals[1] - intervals[0] + 1);
/*      */     } else {
/*  273 */       timeData = new TickData[0];
/*      */     }
/*  275 */     Map formulaOutputs = null;
/*  276 */     Map indicators = null;
/*  277 */     for (Map.Entry entry : this.formulas.entrySet()) {
/*  278 */       AbstractDataProvider.IndicatorData formulaData = (AbstractDataProvider.IndicatorData)entry.getValue();
/*  279 */       if (formulaData.disabledIndicator) {
/*      */         continue;
/*      */       }
/*  282 */       IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/*  283 */       if (formulaOutputs == null) {
/*  284 */         formulaOutputs = new HashMap();
/*  285 */         indicators = new HashMap();
/*      */       }
/*  287 */       Integer indicatorId = (Integer)entry.getKey();
/*  288 */       indicators.put(indicatorId, formulaData.indicatorWrapper);
/*  289 */       Object[] outputs = new Object[formulaData.getOutputDataInt().length];
/*  290 */       formulaOutputs.put(indicatorId, outputs);
/*  291 */       for (int i = 0; i < outputs.length; i++) {
/*  292 */         switch (2.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[indicator.getOutputParameterInfo(i).getType().ordinal()]) {
/*      */         case 1:
/*  294 */           if (oneSecDataExists) {
/*  295 */             outputs[i] = new int[oneSecIntervals[1] - oneSecIntervals[0] + 1];
/*      */ 
/*  297 */             System.arraycopy(formulaData.getOutputDataInt()[i], oneSecIntervals[0], outputs[i], 0, oneSecIntervals[1] - oneSecIntervals[0] + 1);
/*      */           } else {
/*  299 */             outputs[i] = new int[0];
/*      */           }
/*  301 */           break;
/*      */         case 2:
/*  303 */           if (oneSecDataExists) {
/*  304 */             outputs[i] = new double[oneSecIntervals[1] - oneSecIntervals[0] + 1];
/*      */ 
/*  306 */             System.arraycopy(formulaData.getOutputDataDouble()[i], oneSecIntervals[0], outputs[i], 0, oneSecIntervals[1] - oneSecIntervals[0] + 1);
/*      */           } else {
/*  308 */             outputs[i] = new double[0];
/*      */           }
/*  310 */           break;
/*      */         case 3:
/*  312 */           if (oneSecDataExists) {
/*  313 */             outputs[i] = new Object[oneSecIntervals[1] - oneSecIntervals[0] + 1];
/*      */ 
/*  315 */             System.arraycopy(formulaData.getOutputDataObject()[i], oneSecIntervals[0], outputs[i], 0, oneSecIntervals[1] - oneSecIntervals[0] + 1);
/*      */           } else {
/*  317 */             outputs[i] = new Object[0];
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  322 */       if (formulaData.inputDataProviders != null) {
/*  323 */         for (AbstractDataProvider indicatorDataProvider : formulaData.inputDataProviders) {
/*  324 */           if (indicatorDataProvider != null) {
/*  325 */             Period indicatorPeriod = indicatorDataProvider.period == Period.TICK ? this.ALIGNMENT_PERIOD : indicatorDataProvider.period;
/*  326 */             long indicatorTo = DataCacheUtils.getCandleStartFast(indicatorPeriod, to);
/*  327 */             int indicatorBefore = 1;
/*  328 */             int indicatorAfter = numberOfSecondsAfter > 0 ? 1 : 0;
/*  329 */             if (oneSecDataExists) {
/*  330 */               long indicatorFrom = DataCacheUtils.getCandleStartFast(indicatorPeriod, oneSecTimeDataAsk[oneSecIntervals[2]].time);
/*  331 */               indicatorBefore = DataCacheUtils.getCandlesCountBetweenFast(indicatorPeriod, indicatorFrom, indicatorTo);
/*  332 */               if (indicatorBefore < 0) {
/*  333 */                 indicatorBefore = 1;
/*      */               }
/*  335 */               long indicatorAfterTo = DataCacheUtils.getCandleStartFast(indicatorPeriod, oneSecTimeDataAsk[(oneSecTimeDataAsk.length - oneSecIntervals[3] - 1)].time);
/*  336 */               indicatorAfter = DataCacheUtils.getCandlesCountBetweenFast(indicatorPeriod, DataCacheUtils.getNextCandleStartFast(indicatorPeriod, indicatorTo), indicatorAfterTo);
/*  337 */               if (indicatorAfter < 0) {
/*  338 */                 indicatorAfter = 0;
/*      */               }
/*      */             }
/*      */ 
/*  342 */             synchronized (indicatorDataProvider) {
/*  343 */               indicatorDataProvider.doHistoryRequests(indicatorBefore, indicatorTo, indicatorAfter);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  349 */     if (this.parentDataProvider == null)
/*      */     {
/*  351 */       doHistoryRequests(numberOfSecondsBefore, to, numberOfSecondsAfter);
/*      */     }
/*      */ 
/*  361 */     return new TickDataSequence(!oneSecDataExists ? to : oneSecTimeDataAsk[oneSecIntervals[2]].time, !oneSecDataExists ? to : oneSecTimeDataAsk[(oneSecTimeDataAsk.length - oneSecIntervals[3] - 1)].time, intervals[2], intervals[3], timeData, this.gaps, oneSecIntervals[2], oneSecIntervals[3], oneSecTimeDataAsk, oneSecTimeDataBid, indicators, formulaOutputs, (timeData.length > 0) && (this.firstData != null) && (intervals[3] == 0) && (timeData[(timeData.length - 1)].time == ((TickData)this.firstData).time), (timeData.length > 0) && (this.firstData != null) && (timeData[(timeData.length - 1)].time == ((TickData)this.firstData).time));
/*      */   }
/*      */ 
/*      */   protected final boolean calculateTicksInterval(int[] oneSecIntervals, int[] intervals)
/*      */   {
/*  377 */     if (this.lastIndex == -1) {
/*  378 */       return false;
/*      */     }
/*  380 */     int startIndex = findStart(this.oneSecBufferAsk[(oneSecIntervals[0] + oneSecIntervals[2])].time, 0, this.lastIndex, this.buffer);
/*  381 */     long endTime = this.oneSecBufferAsk[(oneSecIntervals[1] - oneSecIntervals[3])].time + 999L;
/*  382 */     int endIndex = findStart(endTime, 0, this.lastIndex, this.buffer);
/*  383 */     if (endIndex > this.lastIndex) {
/*  384 */       endIndex = this.lastIndex;
/*      */     }
/*  386 */     if (this.buffer[endIndex].time > endTime) {
/*  387 */       endIndex--;
/*      */     }
/*  389 */     if (startIndex > 0)
/*      */     {
/*  391 */       startIndex--;
/*  392 */       intervals[2] = 1;
/*      */     }
/*  394 */     if (endIndex < this.lastIndex)
/*      */     {
/*  396 */       endIndex++;
/*  397 */       intervals[3] = 1;
/*      */     }
/*  399 */     if ((startIndex <= endIndex) && (startIndex >= 0) && (endIndex <= this.lastIndex)) {
/*  400 */       intervals[0] = startIndex;
/*  401 */       intervals[1] = endIndex;
/*  402 */       return true;
/*      */     }
/*  404 */     return false;
/*      */   }
/*      */ 
/*      */   public void setFilter(Filter filter)
/*      */   {
/*  410 */     setParams(this.instrument, this.period, filter);
/*      */   }
/*      */ 
/*      */   public void setPeriod(Period period)
/*      */   {
/*  415 */     setParams(this.instrument, period, this.filter);
/*      */   }
/*      */ 
/*      */   public void setOfferSide(OfferSide offerSide)
/*      */   {
/*  420 */     throw new RuntimeException("Cannot change offer side for ticks");
/*      */   }
/*      */ 
/*      */   public Filter getFilter()
/*      */   {
/*  425 */     return this.filter;
/*      */   }
/*      */ 
/*      */   public OfferSide getOfferSide()
/*      */   {
/*  430 */     return null;
/*      */   }
/*      */ 
/*      */   public void setInstrument(Instrument instrument)
/*      */   {
/*  435 */     setParams(instrument, this.period, this.filter);
/*      */   }
/*      */ 
/*      */   public void setParams(Instrument instrument, Period period, Filter filter)
/*      */   {
/*  443 */     if (LOGGER.isDebugEnabled()) {
/*  444 */       LOGGER.debug("Setting filter " + filter + " for [" + instrument + "] [" + period + "] provider");
/*      */     }
/*      */ 
/*  447 */     if (this.parentDataProvider != null)
/*  448 */       synchronized (this.parentDataProvider) {
/*  449 */         synchronized (this) {
/*  450 */           setPeriodAndFilterSynchronized(instrument, period, filter);
/*      */         }
/*      */       }
/*      */     else
/*  454 */       synchronized (this) {
/*  455 */         setPeriodAndFilterSynchronized(instrument, period, filter);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void setPeriodAndFilterSynchronized(Instrument instrument, Period period, Filter filter)
/*      */   {
/*  461 */     if (period != Period.TICK) {
/*  462 */       throw new IllegalArgumentException("Incorrect period set for ticks provider");
/*      */     }
/*  464 */     if ((filter != Filter.NO_FILTER) && (filter != Filter.WEEKENDS)) {
/*  465 */       throw new IllegalArgumentException("Incorrect filter set for ticks provider");
/*      */     }
/*  467 */     if (this.dataCacheRequestData != null) {
/*  468 */       this.dataCacheRequestData.cancel = true;
/*  469 */       if (LOGGER.isDebugEnabled()) {
/*  470 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  471 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  472 */         LOGGER.debug("Canceling request for instrument [" + instrument + "], period [" + period + "], numberOfCandlesBefore [" + this.dataCacheRequestData.numberOfCandlesBefore + "], numberOfCandlesAfter [" + this.dataCacheRequestData.numberOfCandlesAfter + "] time [" + dateFormat.format(new Date(this.dataCacheRequestData.time)) + "] as a result for request to change instrument/period/filter");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  477 */     this.feedDataProvider.unsubscribeFromLiveFeed(this.instrument, this.firstDataListener);
/*      */ 
/*  479 */     this.filter = filter;
/*  480 */     this.instrument = instrument;
/*  481 */     this.firstData = null;
/*  482 */     this.lastIndex = -1;
/*  483 */     this.oneSecLastIndex = -1;
/*  484 */     this.loadedNumberOfSeconds = 0L;
/*  485 */     this.loadedToInSeconds = -9223372036854775808L;
/*  486 */     for (AbstractDataProvider.IndicatorData indicatorData : this.formulas.values()) {
/*  487 */       IIndicator indicator = indicatorData.indicatorWrapper.getIndicator();
/*  488 */       int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfInputs(); i < j; i++) {
/*  489 */         InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/*  490 */         if ((inputParameterInfo.getOfferSide() == null) && (inputParameterInfo.getPeriod() == null) && (inputParameterInfo.getInstrument() == null))
/*      */           continue;
/*  492 */         initIndicatorInputs(indicatorData);
/*  493 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  497 */     this.feedDataProvider.subscribeToLiveFeed(this.instrument, this.firstDataListener);
/*  498 */     long firstTickTime = getLatestDataTime();
/*  499 */     if (firstTickTime != -9223372036854775808L) {
/*  500 */       if (this.requestAtFirstTick) {
/*  501 */         this.requestAtFirstTick = false;
/*      */       }
/*      */ 
/*  506 */       int numberOfSeconds = this.maxNumberOfCandles * this.bufferSizeMultiplier;
/*  507 */       long to = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, firstTickTime);
/*      */ 
/*  509 */       if (this.feedDataProvider != null) {
/*  510 */         DataCacheRequestData requestData = new DataCacheRequestData();
/*  511 */         requestData.numberOfCandlesBefore = 0;
/*  512 */         requestData.numberOfCandlesAfter = 0;
/*  513 */         requestData.time = to;
/*  514 */         requestData.mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/*  515 */         requestData.cancel = false;
/*  516 */         if (assertionsEnabled()) {
/*  517 */           requestData.requestState = new HashMap();
/*  518 */           requestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/*  519 */           TickData[] bufferCopy = new TickData[this.buffer.length];
/*  520 */           System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/*  521 */           requestData.requestState.put("buffer", bufferCopy);
/*  522 */           requestData.requestState.put("firstData", this.firstData);
/*  523 */           requestData.requestState.put("oneSecLastIndex", Integer.valueOf(this.oneSecLastIndex));
/*  524 */           CandleData[] oneSecBufferAskCopy = new CandleData[this.oneSecBufferAsk.length];
/*  525 */           System.arraycopy(this.oneSecBufferAsk, 0, oneSecBufferAskCopy, 0, this.oneSecBufferAsk.length);
/*  526 */           requestData.requestState.put("oneSecBufferAsk", oneSecBufferAskCopy);
/*  527 */           CandleData[] oneSecBufferBidCopy = new CandleData[this.oneSecBufferBid.length];
/*  528 */           System.arraycopy(this.oneSecBufferBid, 0, oneSecBufferBidCopy, 0, this.oneSecBufferBid.length);
/*  529 */           requestData.requestState.put("oneSecBufferBid", oneSecBufferBidCopy);
/*      */         }
/*      */         try {
/*  532 */           AbstractDataProvider.LoadDataProgressListener loadDataProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, requestData);
/*  533 */           if (LOGGER.isDebugEnabled()) {
/*  534 */             SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  535 */             dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  536 */             LOGGER.debug("Requesting last available ticks for instrument [" + instrument + "] number of seconds [" + numberOfSeconds + "] to [" + dateFormat.format(new Date(to)) + "]");
/*      */           }
/*      */ 
/*  539 */           this.feedDataProvider.loadLastAvailableNumberOfTicksDataSynched(instrument, numberOfSeconds, to, filter, new LoadDataListener(requestData, true), loadDataProgressListener);
/*      */         }
/*      */         catch (DataCacheException e) {
/*  542 */           LOGGER.error(e.getMessage(), e);
/*      */         }
/*  544 */         if (this.oneSecLastIndex != -1) {
/*  545 */           numberOfSeconds -= this.oneSecLastIndex;
/*  546 */           this.loadedNumberOfSeconds = (this.oneSecLastIndex + 1);
/*  547 */           to = this.oneSecBufferAsk[0].time;
/*      */         }
/*      */       }
/*  550 */       if ((this.oneSecLastIndex == -1) || (this.oneSecLastIndex + 1 < numberOfSeconds))
/*  551 */         requestHistoryData(numberOfSeconds, 0, to, AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING, this.maxNumberOfCandles * this.bufferSizeMultiplier, DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, firstTickTime), 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void initIndicatorDataOutputBuffers(AbstractDataProvider.IndicatorData formulaData)
/*      */   {
/*  559 */     IndicatorInfo indicatorInfo = formulaData.indicatorWrapper.getIndicator().getIndicatorInfo();
/*  560 */     for (int i = 0; i < indicatorInfo.getNumberOfOutputs(); i++) {
/*  561 */       OutputParameterInfo outputParameterInfo = formulaData.indicatorWrapper.getIndicator().getOutputParameterInfo(i);
/*  562 */       switch (2.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */       case 1:
/*  564 */         formulaData.getOutputDataInt()[i] = new int[this.oneSecBufferAsk.length];
/*  565 */         break;
/*      */       case 2:
/*  567 */         formulaData.getOutputDataDouble()[i] = new double[this.oneSecBufferAsk.length];
/*  568 */         break;
/*      */       case 3:
/*  570 */         formulaData.getOutputDataObject()[i] = new Object[this.buffer.length];
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators()
/*      */   {
/*  578 */     recalculateIndicators(0, this.oneSecLastIndex);
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicator(AbstractDataProvider.IndicatorData indicatorData, boolean latestData, boolean sameCandle)
/*      */   {
/*  583 */     Collection indicators = new ArrayList(1);
/*  584 */     indicators.add(indicatorData);
/*  585 */     if (latestData) {
/*  586 */       recalculateIndicators(this.oneSecLastIndex, this.oneSecLastIndex, indicators, this.oneSecLastIndex, this.oneSecBufferAsk, this.oneSecBufferBid);
/*      */     }
/*      */     else
/*  589 */       recalculateIndicators(0, this.oneSecLastIndex, indicators, this.oneSecLastIndex, this.oneSecBufferAsk, this.oneSecBufferBid);
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators(int from, int to)
/*      */   {
/*  594 */     if (!this.active) {
/*  595 */       return;
/*      */     }
/*  597 */     boolean split = false;
/*  598 */     for (AbstractDataProvider.IndicatorData formulaData : this.formulas.values()) {
/*  599 */       if (formulaData.indicatorWrapper.getIndicator().getIndicatorInfo().isRecalculateAll()) {
/*  600 */         split = true;
/*  601 */         break;
/*      */       }
/*      */     }
/*  604 */     if (split) {
/*  605 */       Collection recalculateAllFormulas = new ArrayList(this.formulas.size());
/*  606 */       Collection restOfTheFormulas = new ArrayList(this.formulas.size());
/*  607 */       for (AbstractDataProvider.IndicatorData formulaData : this.formulas.values()) {
/*  608 */         if (formulaData.indicatorWrapper.getIndicator().getIndicatorInfo().isRecalculateAll())
/*  609 */           recalculateAllFormulas.add(formulaData);
/*      */         else {
/*  611 */           restOfTheFormulas.add(formulaData);
/*      */         }
/*      */       }
/*  614 */       if (!recalculateAllFormulas.isEmpty()) {
/*  615 */         recalculateIndicators(0, this.oneSecLastIndex, recalculateAllFormulas, this.oneSecLastIndex, this.oneSecBufferAsk, this.oneSecBufferBid);
/*      */       }
/*  617 */       if (!restOfTheFormulas.isEmpty()) {
/*  618 */         recalculateIndicators(from, to, restOfTheFormulas, this.oneSecLastIndex, this.oneSecBufferAsk, this.oneSecBufferBid);
/*      */       }
/*      */     }
/*  621 */     else if (!this.formulas.isEmpty()) {
/*  622 */       recalculateIndicators(from, to, this.formulas.values(), this.oneSecLastIndex, this.oneSecBufferAsk, this.oneSecBufferBid);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean loadingNeeded(int numberOfCandlesBefore, long to, int numberOfCandlesAfter)
/*      */   {
/*  628 */     if (this.oneSecLastIndex == -1) {
/*  629 */       return (numberOfCandlesBefore > 0) || (numberOfCandlesAfter > 0);
/*      */     }
/*      */ 
/*  632 */     if (to <= this.loadedToInSeconds) {
/*  633 */       int numberOfCandlesBetweenTimes = DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, to, this.loadedToInSeconds) - 1;
/*  634 */       if ((numberOfCandlesAfter <= numberOfCandlesBetweenTimes) && (numberOfCandlesBefore < this.loadedNumberOfSeconds - numberOfCandlesBetweenTimes))
/*      */       {
/*  636 */         return false;
/*      */       }
/*      */     }
/*      */ 
/*  640 */     if (this.firstData != null) {
/*  641 */       if (to > ((TickData)this.firstData).time)
/*      */       {
/*  643 */         to = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time);
/*  644 */         numberOfCandlesAfter = 0;
/*      */       }
/*  646 */       int maxCandlesAfter = DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, to), DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time));
/*  647 */       if (numberOfCandlesAfter > maxCandlesAfter) {
/*  648 */         numberOfCandlesAfter = maxCandlesAfter;
/*      */       }
/*      */     }
/*      */ 
/*  652 */     int numberOfCandlesBetweenStartAndTo = 0;
/*  653 */     int numberOfCandlesBetweenToAndEnd = 0;
/*      */ 
/*  656 */     int ei = this.oneSecLastIndex;
/*      */ 
/*  658 */     int toIndex = findStart(to, 0, ei, this.oneSecBufferAsk);
/*  659 */     numberOfCandlesBetweenToAndEnd += ei - toIndex;
/*  660 */     if ((toIndex >= 0) && (toIndex <= ei) && (this.oneSecBufferAsk[0].time == to)) {
/*  661 */       toIndex++;
/*      */     }
/*      */ 
/*  664 */     numberOfCandlesBetweenStartAndTo = (int)(numberOfCandlesBetweenStartAndTo + (toIndex + 1 + (this.oneSecLastIndex + 1 - this.loadedNumberOfSeconds)));
/*      */ 
/*  666 */     int safeCandlesAmount = (this.maxNumberOfCandles * this.bufferSizeMultiplier - this.maxNumberOfCandles) / 4;
/*  667 */     if ((this.firstData != null) && ((this.lastIndex == -1) || (this.buffer[this.lastIndex].time != ((TickData)this.firstData).time)))
/*      */     {
/*  669 */       if (numberOfCandlesBetweenToAndEnd < safeCandlesAmount + numberOfCandlesAfter) {
/*  670 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  674 */     return numberOfCandlesBetweenStartAndTo < safeCandlesAmount + numberOfCandlesBefore;
/*      */   }
/*      */ 
/*      */   public AbstractDataProvider<TickData, TickDataSequence>.LoadDataProgressListener doHistoryRequests(int numOfCandlesBefore, long reqTime, int numOfCandlesAfter)
/*      */   {
/*  680 */     if (loadingNeeded(numOfCandlesBefore, reqTime, numOfCandlesAfter))
/*      */     {
/*  683 */       int numberOfCandlesBefore = this.maxNumberOfCandles * this.bufferSizeMultiplier / 2 + (numOfCandlesBefore + numOfCandlesAfter) / 2;
/*  684 */       int numberOfCandlesAfter = this.maxNumberOfCandles * this.bufferSizeMultiplier / 2 - (numOfCandlesBefore + numOfCandlesAfter) / 2;
/*  685 */       AbstractDataProvider.RequestMode mode = AbstractDataProvider.RequestMode.OVERWRITE;
/*  686 */       long time = reqTime;
/*      */ 
/*  688 */       long firstDataTime = -9223372036854775808L;
/*  689 */       if (this.firstData != null) {
/*  690 */         firstDataTime = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time);
/*      */       }
/*      */ 
/*  693 */       if (firstDataTime != -9223372036854775808L) {
/*  694 */         if (time >= firstDataTime)
/*      */         {
/*  696 */           time = firstDataTime;
/*  697 */           numberOfCandlesAfter = 0;
/*  698 */           numberOfCandlesBefore = this.maxNumberOfCandles * this.bufferSizeMultiplier;
/*  699 */           mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/*      */ 
/*  701 */           if ((this.lastIndex != -1) && (this.firstData != null) && (this.buffer[this.lastIndex].time != ((TickData)this.firstData).time)) {
/*  702 */             this.lastIndex = 0;
/*  703 */             this.oneSecLastIndex = 0;
/*  704 */             this.buffer[this.lastIndex] = ((TickData)this.firstData);
/*  705 */             this.oneSecBufferAsk[this.oneSecLastIndex] = new CandleData();
/*  706 */             this.oneSecBufferBid[this.oneSecLastIndex] = new CandleData();
/*  707 */             initCandleWithTick(this.oneSecBufferAsk[this.oneSecLastIndex], (TickData)this.firstData, OfferSide.ASK);
/*  708 */             initCandleWithTick(this.oneSecBufferBid[this.oneSecLastIndex], (TickData)this.firstData, OfferSide.BID);
/*  709 */             this.gaps = new long[0][];
/*  710 */             this.loadedNumberOfSeconds = 1L;
/*  711 */             this.loadedToInSeconds = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time);
/*  712 */             numberOfCandlesBefore--;
/*  713 */             recalculateIndicators();
/*  714 */             checkConsistency();
/*  715 */           } else if ((this.lastIndex != -1) && (this.firstData != null) && (this.buffer[this.lastIndex].time == ((TickData)this.firstData).time))
/*      */           {
/*  718 */             time = this.oneSecBufferAsk[0].time;
/*  719 */             numberOfCandlesBefore -= this.oneSecLastIndex + 1;
/*  720 */             if ((numberOfCandlesBefore == 0) && (assertionsEnabled()))
/*  721 */               throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */           }
/*      */         }
/*      */         else {
/*  725 */           long expectedLastCandleTime = DataCacheUtils.getTimeForNCandlesForwardFast(this.ALIGNMENT_PERIOD, time, numberOfCandlesAfter + 1);
/*  726 */           if (expectedLastCandleTime > firstDataTime) {
/*  727 */             int numberOfCandlesBetween = DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, firstDataTime, expectedLastCandleTime) - 1;
/*  728 */             numberOfCandlesAfter -= numberOfCandlesBetween;
/*  729 */             numberOfCandlesBefore += numberOfCandlesBetween;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  734 */       return requestHistoryData(numberOfCandlesBefore, numberOfCandlesAfter, time, mode, numOfCandlesBefore, reqTime, numOfCandlesAfter);
/*  735 */     }if (this.dataCacheRequestData != null) {
/*  736 */       if (LOGGER.isDebugEnabled()) {
/*  737 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  738 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  739 */         LOGGER.debug("Canceling request for instrument [" + this.instrument + "], period [" + this.period + "], number of" + " candles before [" + this.dataCacheRequestData.numberOfCandlesBefore + "] number of candles after [" + this.dataCacheRequestData.numberOfCandlesAfter + "] to [" + dateFormat.format(new Date(this.dataCacheRequestData.time)) + "] as a result to request for [" + numOfCandlesBefore + "] candles before time [" + dateFormat.format(new Date(reqTime)) + "] and [" + numOfCandlesAfter + "] candles after time, buffer already has the required data");
/*      */       }
/*      */ 
/*  746 */       this.dataCacheRequestData.cancel = true;
/*  747 */       this.dataCacheRequestData = null;
/*  748 */       fireLoadingFinished();
/*      */     }
/*  750 */     return null;
/*      */   }
/*      */ 
/*      */   protected void shiftLeft(int numberOfElements) {
/*  754 */     if (numberOfElements < this.oneSecLastIndex + 1) {
/*  755 */       if ((this.gaps.length > 0) && 
/*  756 */         (this.gaps[0][0] < this.oneSecBufferAsk[numberOfElements].time)) {
/*  757 */         this.gaps = new long[0][];
/*      */       }
/*      */ 
/*  760 */       System.arraycopy(this.oneSecBufferAsk, numberOfElements, this.oneSecBufferAsk, 0, this.oneSecLastIndex + 1 - numberOfElements);
/*  761 */       System.arraycopy(this.oneSecBufferBid, numberOfElements, this.oneSecBufferBid, 0, this.oneSecLastIndex + 1 - numberOfElements);
/*  762 */       int newStartIndex = findStart(this.oneSecBufferAsk[0].time, 0, this.lastIndex, this.buffer);
/*  763 */       System.arraycopy(this.buffer, newStartIndex, this.buffer, 0, this.lastIndex + 1 - newStartIndex);
/*  764 */       for (AbstractDataProvider.IndicatorData indicatorData : this.formulas.values()) {
/*  765 */         IIndicator indicator = indicatorData.indicatorWrapper.getIndicator();
/*  766 */         int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfOutputs(); i < j; i++)
/*      */         {
/*      */           Object array;
/*  768 */           switch (2.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[indicator.getOutputParameterInfo(i).getType().ordinal()]) {
/*      */           case 2:
/*  770 */             array = indicatorData.getOutputDataDouble()[i];
/*  771 */             break;
/*      */           case 1:
/*  773 */             array = indicatorData.getOutputDataInt()[i];
/*  774 */             break;
/*      */           case 3:
/*  776 */             array = indicatorData.getOutputDataObject()[i];
/*  777 */             break;
/*      */           default:
/*  779 */             break;
/*      */           }
/*      */ 
/*  782 */           System.arraycopy(array, numberOfElements, array, 0, this.oneSecLastIndex + 1 - numberOfElements);
/*      */         }
/*      */       }
/*  785 */       this.lastIndex -= newStartIndex;
/*  786 */       this.oneSecLastIndex -= numberOfElements;
/*  787 */       this.loadedNumberOfSeconds = (this.oneSecLastIndex + 1);
/*  788 */       this.loadedToInSeconds = this.oneSecBufferAsk[this.oneSecLastIndex].time;
/*  789 */     } else if (numberOfElements >= this.oneSecLastIndex + 1) {
/*  790 */       this.lastIndex = -1;
/*  791 */       this.oneSecLastIndex = -1;
/*  792 */       this.loadedNumberOfSeconds = 0L;
/*  793 */       this.loadedToInSeconds = -9223372036854775808L;
/*  794 */       this.gaps = new long[0][];
/*      */     }
/*      */   }
/*      */ 
/*      */   protected AbstractDataProvider<TickData, TickDataSequence>.LoadDataProgressListener requestHistoryData(int numberOfCandlesBefore, int numberOfCandlesAfter, long time, AbstractDataProvider.RequestMode mode, int requestedNumberOfCandlesBefore, long requestedTo, int requestedNumberOfCandlesAfter)
/*      */   {
/*  801 */     if ((this.feedDataProvider != null) && (time < this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period)))
/*      */     {
/*  803 */       return null;
/*      */     }
/*      */ 
/*  806 */     if ((mode != AbstractDataProvider.RequestMode.OVERWRITE) && (mode != AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING))
/*      */     {
/*  808 */       throw new RuntimeException("Request mode [" + mode + "] not supported");
/*      */     }
/*      */ 
/*  811 */     if ((mode == AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING) && (this.lastIndex != -1) && (this.firstData != null) && ((time > DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, this.buffer[0].time)) || (time > this.oneSecBufferAsk[0].time) || (time != this.oneSecBufferAsk[0].time)))
/*      */     {
/*  813 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/*      */ 
/*  816 */     if ((this.dataCacheRequestData != null) && (!this.dataCacheRequestData.cancel))
/*      */     {
/*  820 */       if ((this.dataCacheRequestData.time == time) && (this.dataCacheRequestData.numberOfCandlesBefore == numberOfCandlesBefore) && (this.dataCacheRequestData.numberOfCandlesAfter == numberOfCandlesAfter) && (this.dataCacheRequestData.mode == mode))
/*      */       {
/*  823 */         return null;
/*      */       }
/*      */ 
/*  826 */       long firstDataTime = -9223372036854775808L;
/*  827 */       if (this.firstData != null)
/*  828 */         firstDataTime = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time);
/*      */       long expectedBufferTimeEnd;
/*  833 */       if (this.dataCacheRequestData.mode == AbstractDataProvider.RequestMode.OVERWRITE) {
/*  834 */         long expectedBufferTimeStart = DataCacheUtils.getTimeForNCandlesBackFast(this.ALIGNMENT_PERIOD, this.dataCacheRequestData.time, this.dataCacheRequestData.numberOfCandlesBefore);
/*      */ 
/*  836 */         expectedBufferTimeEnd = DataCacheUtils.getTimeForNCandlesForwardFast(this.ALIGNMENT_PERIOD, DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, this.dataCacheRequestData.time), this.dataCacheRequestData.numberOfCandlesAfter);
/*      */       }
/*      */       else
/*      */       {
/*      */         long expectedBufferTimeStart;
/*  838 */         if (this.dataCacheRequestData.mode == AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING) {
/*  839 */           long expectedBufferTimeEnd = firstDataTime;
/*  840 */           expectedBufferTimeStart = DataCacheUtils.getTimeForNCandlesBackFast(this.ALIGNMENT_PERIOD, this.dataCacheRequestData.time, this.dataCacheRequestData.numberOfCandlesBefore + (DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, time, firstDataTime) - 1));
/*      */         }
/*      */         else
/*      */         {
/*  844 */           throw new RuntimeException("Request mode [" + this.dataCacheRequestData.mode + "] not supported");
/*      */         }
/*      */       }
/*      */       long expectedBufferTimeEnd;
/*      */       long expectedBufferTimeStart;
/*  847 */       long requestedFrom = DataCacheUtils.getTimeForNCandlesBackFast(this.ALIGNMENT_PERIOD, requestedTo, requestedNumberOfCandlesBefore + (this.maxNumberOfCandles * this.bufferSizeMultiplier - this.maxNumberOfCandles) / 4);
/*      */ 
/*  849 */       long requestedToCorrected = requestedTo + DataCacheUtils.getTimeForNCandlesForwardFast(this.ALIGNMENT_PERIOD, DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, requestedTo), requestedNumberOfCandlesAfter + (this.maxNumberOfCandles * this.bufferSizeMultiplier - this.maxNumberOfCandles) / 4);
/*      */ 
/*  852 */       if (requestedToCorrected > firstDataTime)
/*      */       {
/*  854 */         requestedToCorrected = firstDataTime;
/*      */       }
/*  856 */       if ((requestedFrom >= expectedBufferTimeStart) && (requestedToCorrected <= expectedBufferTimeEnd)) {
/*  857 */         return null;
/*      */       }
/*      */ 
/*  860 */       if (LOGGER.isDebugEnabled()) {
/*  861 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  862 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  863 */         LOGGER.debug("Canceling request for instrument [" + this.instrument + "], period [" + this.period + "], numberOfCandlesBefore [" + this.dataCacheRequestData.numberOfCandlesBefore + "], numberOfCandlesAfter [" + this.dataCacheRequestData.numberOfCandlesAfter + "] time [" + dateFormat.format(new Date(this.dataCacheRequestData.time)) + "], request no longer contain required data");
/*      */       }
/*      */ 
/*  867 */       this.dataCacheRequestData.cancel = true;
/*  868 */       this.dataCacheRequestData = null;
/*      */     }
/*  870 */     fireLoadingStarted();
/*  871 */     this.dataCacheRequestData = new DataCacheRequestData();
/*  872 */     this.dataCacheRequestData.numberOfCandlesBefore = numberOfCandlesBefore;
/*  873 */     this.dataCacheRequestData.numberOfCandlesAfter = numberOfCandlesAfter;
/*  874 */     this.dataCacheRequestData.time = time;
/*  875 */     this.dataCacheRequestData.mode = mode;
/*  876 */     this.dataCacheRequestData.cancel = false;
/*  877 */     if (assertionsEnabled()) {
/*  878 */       this.dataCacheRequestData.requestState = new HashMap();
/*  879 */       this.dataCacheRequestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/*  880 */       TickData[] bufferCopy = new TickData[this.buffer.length];
/*  881 */       System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/*  882 */       this.dataCacheRequestData.requestState.put("buffer", bufferCopy);
/*  883 */       this.dataCacheRequestData.requestState.put("firstData", this.firstData);
/*  884 */       this.dataCacheRequestData.requestState.put("oneSecLastIndex", Integer.valueOf(this.oneSecLastIndex));
/*  885 */       CandleData[] oneSecBufferAskCopy = new CandleData[this.oneSecBufferAsk.length];
/*  886 */       System.arraycopy(this.oneSecBufferAsk, 0, oneSecBufferAskCopy, 0, this.oneSecBufferAsk.length);
/*  887 */       this.dataCacheRequestData.requestState.put("oneSecBufferAsk", oneSecBufferAskCopy);
/*  888 */       CandleData[] oneSecBufferBidCopy = new CandleData[this.oneSecBufferBid.length];
/*  889 */       System.arraycopy(this.oneSecBufferBid, 0, oneSecBufferBidCopy, 0, this.oneSecBufferBid.length);
/*  890 */       this.dataCacheRequestData.requestState.put("oneSecBufferBid", oneSecBufferBidCopy);
/*      */     }
/*  892 */     if (this.feedDataProvider != null) {
/*      */       try {
/*  894 */         AbstractDataProvider.LoadDataProgressListener loadDataProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, this.dataCacheRequestData);
/*  895 */         if (LOGGER.isDebugEnabled()) {
/*  896 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  897 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  898 */           LOGGER.debug("Requesting ticks for instrument [" + this.instrument + "], period [" + this.period + "],  loading [" + numberOfCandlesBefore + "] seconds before and [" + numberOfCandlesAfter + "] after time [" + dateFormat.format(new Date(time)) + "] as a result to request for [" + requestedNumberOfCandlesBefore + "] seconds before time [" + dateFormat.format(new Date(requestedTo)) + "] and [" + requestedNumberOfCandlesAfter + "] seconds after time");
/*      */         }
/*      */ 
/*  904 */         this.feedDataProvider.loadTicksDataBeforeAfter(this.instrument, numberOfCandlesBefore, numberOfCandlesAfter, time, this.filter, new LoadDataListener(this.dataCacheRequestData, false), loadDataProgressListener);
/*      */ 
/*  906 */         return loadDataProgressListener;
/*      */       } catch (DataCacheException e) {
/*  908 */         LOGGER.error(e.getMessage(), e);
/*  909 */         return null;
/*      */       }
/*      */     }
/*  912 */     return null;
/*      */   }
/*      */ 
/*      */   protected void dataLoaded(boolean allDataLoaded, AbstractDataProvider.AbstractDataCacheRequestData abstractRequestData, Exception e, ISynchronizeIndicators synchronizeIndicators)
/*      */   {
/*  919 */     boolean dataChanged = false;
/*  920 */     long dataChangedFrom = 9223372036854775807L;
/*  921 */     long dataChangedTo = -9223372036854775808L;
/*  922 */     boolean dataChangedFirstData = true;
/*  923 */     synchronized (this) {
/*  924 */       DataCacheRequestData requestData = (DataCacheRequestData)abstractRequestData;
/*  925 */       if (LOGGER.isTraceEnabled()) {
/*  926 */         LOGGER.trace("dataLoaded, period [" + this.period + "]");
/*      */       }
/*  928 */       if (this.dataCacheRequestData == requestData) {
/*  929 */         this.dataCacheRequestData = null;
/*  930 */         fireLoadingFinished();
/*      */       }
/*  932 */       if ((!requestData.cancel) && (allDataLoaded)) {
/*  933 */         if (LOGGER.isDebugEnabled()) {
/*  934 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  935 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  936 */           LOGGER.debug("Loaded [" + requestData.dataLoaded.size() + "] data for instrument [" + this.instrument + "], period [" + this.period + "], requestedCandlesBefore [" + requestData.numberOfCandlesBefore + "], time [" + dateFormat.format(new Date(requestData.time)) + "], requestedCandlesAfter [" + requestData.numberOfCandlesAfter + "]");
/*      */         }
/*      */ 
/*  940 */         if (assertionsEnabled()) {
/*  941 */           requestData.responseState = new HashMap();
/*  942 */           requestData.responseState.put("lastIndex", Integer.valueOf(this.lastIndex));
/*  943 */           TickData[] bufferCopy = new TickData[this.buffer.length];
/*  944 */           System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/*  945 */           requestData.responseState.put("buffer", bufferCopy);
/*  946 */           requestData.responseState.put("firstData", this.firstData);
/*  947 */           requestData.responseState.put("oneSecLastIndex", Integer.valueOf(this.oneSecLastIndex));
/*  948 */           CandleData[] oneSecBufferAskCopy = new CandleData[this.oneSecBufferAsk.length];
/*  949 */           System.arraycopy(this.oneSecBufferAsk, 0, oneSecBufferAskCopy, 0, this.oneSecBufferAsk.length);
/*  950 */           requestData.responseState.put("oneSecBufferAsk", oneSecBufferAskCopy);
/*  951 */           CandleData[] oneSecBufferBidCopy = new CandleData[this.oneSecBufferBid.length];
/*  952 */           System.arraycopy(this.oneSecBufferBid, 0, oneSecBufferBidCopy, 0, this.oneSecBufferBid.length);
/*  953 */           requestData.responseState.put("oneSecBufferBid", oneSecBufferBidCopy);
/*      */         }
/*  955 */         checkLoadedData(requestData);
/*  956 */         if (requestData.mode == AbstractDataProvider.RequestMode.OVERWRITE) {
/*  957 */           if (!requestData.askCandles.isEmpty()) {
/*  958 */             this.lastIndex = -1;
/*  959 */             this.oneSecLastIndex = -1;
/*  960 */             this.gaps = new long[0][];
/*  961 */             for (TickData dataElement : requestData.dataLoaded) {
/*  962 */               addTick(dataElement);
/*      */             }
/*  964 */             int i = 0; for (int j = requestData.askCandles.size(); i < j; i++) {
/*  965 */               addCandles((CandleData)requestData.askCandles.get(i), (CandleData)requestData.bidCandles.get(i));
/*      */             }
/*  967 */             if (requestData.numberOfCandlesAfter > 0) {
/*  968 */               long requestedTimePlusAfterCandles = DataCacheUtils.getTimeForNCandlesForwardFast(this.ALIGNMENT_PERIOD, requestData.time, requestData.numberOfCandlesAfter);
/*  969 */               this.loadedToInSeconds = (this.oneSecBufferAsk[this.oneSecLastIndex].time > requestedTimePlusAfterCandles ? this.oneSecBufferAsk[this.oneSecLastIndex].time : requestedTimePlusAfterCandles);
/*      */             } else {
/*  971 */               this.loadedToInSeconds = requestData.time;
/*      */             }
/*  973 */             this.loadedNumberOfSeconds = (requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter);
/*      */ 
/*  975 */             recalculateIndicators();
/*  976 */             dataChanged = true;
/*  977 */             dataChangedFrom = Math.min(dataChangedFrom, this.oneSecBufferAsk[0].time);
/*  978 */             dataChangedTo = Math.max(dataChangedTo, this.oneSecBufferAsk[this.oneSecLastIndex].time + 999L);
/*  979 */             dataChangedFirstData = false;
/*      */           } else {
/*  981 */             this.lastIndex = -1;
/*  982 */             this.oneSecLastIndex = -1;
/*  983 */             this.gaps = new long[0][];
/*  984 */             this.loadedNumberOfSeconds = (requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter);
/*  985 */             if (requestData.numberOfCandlesBefore == 0) {
/*  986 */               this.loadedToInSeconds = requestData.time;
/*      */             }
/*  988 */             if (requestData.numberOfCandlesAfter > 0) {
/*  989 */               this.loadedToInSeconds = DataCacheUtils.getTimeForNCandlesForwardFast(this.ALIGNMENT_PERIOD, requestData.time, requestData.numberOfCandlesAfter + 1);
/*      */             }
/*  991 */             dataChanged = true;
/*  992 */             dataChangedFrom = Math.min(dataChangedFrom, DataCacheUtils.getTimeForNCandlesBackFast(this.ALIGNMENT_PERIOD, requestData.time, requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter));
/*  993 */             dataChangedTo = Math.max(dataChangedTo, this.loadedToInSeconds + 999L);
/*  994 */             dataChangedFirstData = false;
/*      */           }
/*  996 */         } else if (requestData.mode == AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING) {
/*  997 */           if (!requestData.askCandles.isEmpty()) {
/*  998 */             if (!requestData.dataLoaded.isEmpty()) {
/*  999 */               if (this.firstData == null)
/*      */               {
/* 1001 */                 this.firstData = ((Data)requestData.dataLoaded.remove(requestData.dataLoaded.size() - 1));
/*      */ 
/* 1004 */                 while (((CandleData)requestData.askCandles.get(requestData.askCandles.size() - 1)).time > DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time)) {
/* 1005 */                   int index = requestData.askCandles.size() - 1;
/* 1006 */                   requestData.askCandles.remove(index);
/* 1007 */                   requestData.bidCandles.remove(index);
/*      */                 }
/* 1009 */                 this.lastIndex = 0;
/* 1010 */                 this.oneSecLastIndex = 0;
/* 1011 */                 this.loadedNumberOfSeconds = 1L;
/* 1012 */                 this.loadedToInSeconds = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time);
/* 1013 */                 this.buffer[this.lastIndex] = ((TickData)this.firstData);
/* 1014 */                 this.oneSecBufferAsk[this.oneSecLastIndex] = new CandleData();
/* 1015 */                 this.oneSecBufferBid[this.oneSecLastIndex] = new CandleData();
/* 1016 */                 initCandleWithTick(this.oneSecBufferAsk[this.oneSecLastIndex], (TickData)this.firstData, OfferSide.ASK);
/* 1017 */                 initCandleWithTick(this.oneSecBufferBid[this.oneSecLastIndex], (TickData)this.firstData, OfferSide.BID);
/*      */               }
/*      */ 
/* 1020 */               if (!requestData.dataLoaded.isEmpty()) {
/* 1021 */                 TickData[] oldBuffer = this.buffer;
/* 1022 */                 int oldLastIndex = this.lastIndex;
/* 1023 */                 this.buffer = new TickData[this.buffer.length];
/* 1024 */                 this.lastIndex = -1;
/*      */ 
/* 1027 */                 for (TickData dataElement : requestData.dataLoaded) {
/* 1028 */                   addTick(dataElement);
/*      */                 }
/*      */ 
/* 1032 */                 long timeOfLastLoadedTick = ((TickData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1)).time;
/* 1033 */                 for (int i = 0; i <= oldLastIndex; i++) {
/* 1034 */                   TickData dataElement = oldBuffer[i];
/* 1035 */                   if (timeOfLastLoadedTick < dataElement.time)
/*      */                   {
/*      */                     break;
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/* 1042 */                 for (; i <= oldLastIndex; i++) {
/* 1043 */                   TickData dataElement = oldBuffer[i];
/* 1044 */                   addTick(dataElement);
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 1049 */             CandleData[] oldAskBuffer = this.oneSecBufferAsk;
/* 1050 */             CandleData[] oldBidBuffer = this.oneSecBufferBid;
/* 1051 */             int oldOneSecLastIndex = this.oneSecLastIndex;
/* 1052 */             this.oneSecBufferAsk = new CandleData[this.maxNumberOfCandles * this.bufferSizeMultiplier];
/* 1053 */             this.oneSecBufferBid = new CandleData[this.maxNumberOfCandles * this.bufferSizeMultiplier];
/* 1054 */             this.oneSecLastIndex = -1;
/* 1055 */             this.gaps = new long[0][];
/*      */ 
/* 1058 */             int k = 0; for (int j = requestData.askCandles.size(); k < j; k++) {
/* 1059 */               addCandles((CandleData)requestData.askCandles.get(k), (CandleData)requestData.bidCandles.get(k));
/*      */             }
/*      */ 
/* 1062 */             long timeOfLastLoadedCandle = ((CandleData)requestData.askCandles.get(requestData.askCandles.size() - 1)).time;
/* 1063 */             if (requestData.askCandles.size() > 1)
/*      */             {
/* 1065 */               double flatPriceAsk = ((CandleData)requestData.askCandles.get(requestData.askCandles.size() - 2)).close;
/* 1066 */               double flatPriceBid = ((CandleData)requestData.bidCandles.get(requestData.bidCandles.size() - 2)).close;
/* 1067 */               recreateCandle(this.oneSecBufferAsk[this.oneSecLastIndex], this.oneSecBufferBid[this.oneSecLastIndex], flatPriceAsk, flatPriceBid);
/*      */             }
/*      */ 
/* 1071 */             for (int i = 0; i <= oldOneSecLastIndex; i++) {
/* 1072 */               CandleData dataElement = oldAskBuffer[i];
/* 1073 */               if (timeOfLastLoadedCandle < dataElement.time)
/*      */               {
/*      */                 break;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1080 */             this.loadedNumberOfSeconds = (this.oneSecLastIndex + 1 - i);
/*      */ 
/* 1082 */             for (; i <= oldOneSecLastIndex; i++) {
/* 1083 */               addCandles(oldAskBuffer[i], oldBidBuffer[i]);
/*      */             }
/* 1085 */             this.loadedNumberOfSeconds += oldOneSecLastIndex - i + 1;
/* 1086 */             if (this.loadedNumberOfSeconds > this.oneSecBufferAsk.length) {
/* 1087 */               this.loadedNumberOfSeconds = this.oneSecBufferAsk.length;
/*      */             }
/*      */ 
/* 1090 */             recalculateIndicators(0, this.oneSecLastIndex);
/*      */           } else {
/* 1092 */             this.loadedNumberOfSeconds = (this.loadedNumberOfSeconds + requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter);
/* 1093 */             if (this.loadedNumberOfSeconds > this.oneSecBufferAsk.length) {
/* 1094 */               this.loadedNumberOfSeconds = this.oneSecBufferAsk.length;
/*      */             }
/*      */           }
/* 1097 */           if (!requestData.askCandles.isEmpty()) {
/* 1098 */             dataChanged = true;
/* 1099 */             dataChangedFrom = Math.min(dataChangedFrom, this.oneSecBufferAsk[0].time);
/* 1100 */             dataChangedTo = Math.max(dataChangedTo, this.oneSecBufferAsk[this.oneSecLastIndex].time + 999L);
/* 1101 */             dataChangedFirstData = false;
/*      */           }
/*      */         }
/* 1104 */         else if (!$assertionsDisabled) { throw new AssertionError("unknown request mode");
/*      */         }
/* 1106 */         checkConsistency();
/* 1107 */       } else if ((!allDataLoaded) && (e != null)) {
/* 1108 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/* 1111 */     if (dataChanged) {
/* 1112 */       if ((dataChangedFirstData) && (sparceIndicatorAttached())) {
/* 1113 */         dataChangedFrom = this.buffer[0].time;
/* 1114 */         dataChangedTo = this.buffer[this.lastIndex].time;
/*      */       }
/* 1116 */       fireDataChanged(dataChangedFrom, dataChangedTo, dataChangedFirstData, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void recreateCandle(CandleData askCandle, CandleData bidCandle, double priceAsk, double priceBid) {
/* 1121 */     askCandle.open = priceAsk;
/* 1122 */     askCandle.close = priceAsk;
/* 1123 */     askCandle.low = priceAsk;
/* 1124 */     askCandle.high = priceAsk;
/* 1125 */     askCandle.vol = 0.0D;
/*      */ 
/* 1127 */     bidCandle.open = priceBid;
/* 1128 */     bidCandle.close = priceBid;
/* 1129 */     bidCandle.low = priceBid;
/* 1130 */     bidCandle.high = priceBid;
/* 1131 */     bidCandle.vol = 0.0D;
/* 1132 */     boolean firstTick = true;
/* 1133 */     for (int i = findStart(askCandle.time, 0, this.lastIndex, this.buffer); (i <= this.lastIndex) && 
/* 1134 */       (this.buffer[i].time / 1000L == askCandle.time / 1000L); i++)
/*      */     {
/* 1135 */       if (firstTick) {
/* 1136 */         initCandleWithTick(askCandle, this.buffer[i], OfferSide.ASK);
/* 1137 */         initCandleWithTick(bidCandle, this.buffer[i], OfferSide.BID);
/* 1138 */         firstTick = false;
/*      */       } else {
/* 1140 */         addTickAtTheEnd(askCandle, this.buffer[i], OfferSide.ASK);
/* 1141 */         addTickAtTheEnd(bidCandle, this.buffer[i], OfferSide.BID);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addCandles(CandleData askCandle, CandleData bidCandle)
/*      */   {
/* 1150 */     if (this.oneSecLastIndex >= this.oneSecBufferAsk.length - 1) {
/* 1151 */       shiftLeft(200);
/*      */     }
/* 1153 */     this.oneSecLastIndex += 1;
/* 1154 */     this.oneSecBufferAsk[this.oneSecLastIndex] = askCandle;
/* 1155 */     this.oneSecBufferBid[this.oneSecLastIndex] = bidCandle;
/*      */ 
/* 1157 */     if ((this.oneSecLastIndex > 0) && (this.oneSecBufferAsk[(this.oneSecLastIndex - 1)].time + 1000L != this.oneSecBufferAsk[this.oneSecLastIndex].time))
/*      */     {
/* 1159 */       long firstGapCandleTime = this.oneSecBufferAsk[(this.oneSecLastIndex - 1)].time + 1000L;
/* 1160 */       this.gaps = new long[][] { { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, firstGapCandleTime, this.oneSecBufferAsk[this.oneSecLastIndex].time - 1000L) } };
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkConsistency()
/*      */   {
/* 1166 */     if (assertionsEnabled()) {
/* 1167 */       if (this.oneSecLastIndex == -1)
/*      */       {
/* 1169 */         return;
/*      */       }
/*      */ 
/* 1172 */       if ((this.lastIndex >= this.buffer.length) || (this.oneSecLastIndex >= this.oneSecBufferAsk.length)) {
/* 1173 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/*      */ 
/* 1176 */       if (((this.lastIndex != -1) && ((this.buffer[0].time < this.oneSecBufferAsk[0].time) || (this.buffer[0].time > this.buffer[this.lastIndex].time) || (this.buffer[this.lastIndex].time > this.oneSecBufferAsk[this.oneSecLastIndex].time + 1000L))) || (this.oneSecBufferAsk[0].time > this.oneSecBufferAsk[this.oneSecLastIndex].time))
/*      */       {
/* 1179 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/*      */ 
/* 1182 */       if (this.oneSecBufferAsk[this.oneSecLastIndex].time > DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time)) {
/* 1183 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/*      */ 
/* 1186 */       if (this.loadedNumberOfSeconds > this.maxNumberOfCandles * this.bufferSizeMultiplier) {
/* 1187 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/*      */ 
/* 1190 */       long firstCandleStart = this.oneSecBufferAsk[0].time;
/* 1191 */       checkConsistency(firstCandleStart);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkConsistency(long firstCandleStart) {
/* 1196 */     long prevTime = -9223372036854775808L;
/* 1197 */     long[][] testGaps = new long[0][];
/* 1198 */     long candleStart = firstCandleStart;
/* 1199 */     int start = 0;
/* 1200 */     CandleData candleAsk = new CandleData();
/* 1201 */     CandleData candleBid = new CandleData();
/* 1202 */     for (int i = 0; i <= this.oneSecLastIndex; i++) {
/* 1203 */       CandleData dataElementAsk = this.oneSecBufferAsk[i];
/* 1204 */       CandleData dataElementBid = this.oneSecBufferBid[i];
/* 1205 */       if ((dataElementAsk == null) || (dataElementBid == null) || (dataElementAsk.time != dataElementBid.time))
/*      */       {
/* 1207 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/* 1209 */       long firstGapCandleTime = prevTime + 1000L;
/* 1210 */       if ((prevTime != -9223372036854775808L) && (firstGapCandleTime != dataElementAsk.time))
/*      */       {
/* 1212 */         testGaps = new long[][] { { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, firstGapCandleTime, dataElementAsk.time - 1000L) } };
/*      */       }
/* 1214 */       prevTime = dataElementAsk.time;
/*      */ 
/* 1216 */       if (this.filter == Filter.NO_FILTER) {
/* 1217 */         if (dataElementAsk.time != candleStart) {
/* 1218 */           throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */         }
/* 1220 */         candleStart = DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, candleStart);
/*      */       } else {
/* 1222 */         if (dataElementAsk.time < candleStart) {
/* 1223 */           throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */         }
/* 1225 */         candleStart = DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, dataElementAsk.time);
/*      */       }
/* 1227 */       boolean firstTick = true;
/* 1228 */       for (; (start <= this.lastIndex) && (this.buffer[start].time / 1000L <= dataElementAsk.time / 1000L); start++) {
/* 1229 */         if (this.buffer[start].time / 1000L == dataElementAsk.time / 1000L) {
/* 1230 */           if (firstTick) {
/* 1231 */             initCandleWithTick(candleAsk, this.buffer[start], OfferSide.ASK);
/* 1232 */             initCandleWithTick(candleBid, this.buffer[start], OfferSide.BID);
/* 1233 */             firstTick = false;
/*      */           } else {
/* 1235 */             addTickAtTheEnd(candleAsk, this.buffer[start], OfferSide.ASK);
/* 1236 */             addTickAtTheEnd(candleBid, this.buffer[start], OfferSide.BID);
/*      */           }
/*      */         }
/*      */       }
/* 1240 */       if (firstTick)
/*      */       {
/* 1242 */         candleAsk.time = dataElementAsk.time;
/* 1243 */         candleAsk.open = (candleAsk.close = candleAsk.high = candleAsk.low = candleAsk.close);
/* 1244 */         candleAsk.vol = 0.0D;
/*      */ 
/* 1246 */         candleBid.time = dataElementBid.time;
/* 1247 */         candleBid.open = (candleBid.close = candleBid.high = candleBid.low = candleBid.close);
/* 1248 */         candleBid.vol = 0.0D;
/*      */       }
/* 1250 */       if ((start == 0) || (
/* 1251 */         (dataElementAsk.equals(candleAsk)) && (dataElementBid.equals(candleBid)))) continue;
/* 1252 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/*      */ 
/* 1256 */     if (this.gaps.length != testGaps.length)
/* 1257 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/* 1258 */     if ((this.gaps.length != 0) && (this.gaps.length != 1))
/* 1259 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/* 1260 */     if (this.gaps.length == 1) {
/* 1261 */       if ((this.gaps[0][0] != testGaps[0][0]) || (this.gaps[0][1] != testGaps[0][1])) {
/* 1262 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/* 1264 */       if (this.gaps[0][0] == this.gaps[0][1])
/* 1265 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkLoadedData(DataCacheRequestData requestData)
/*      */   {
/* 1271 */     if ((!assertionsEnabled()) || (requestData.askCandles.isEmpty())) {
/* 1272 */       return;
/*      */     }
/* 1274 */     if (requestData.askCandles.size() != requestData.bidCandles.size()) {
/* 1275 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/* 1277 */     long candleStart = ((CandleData)requestData.askCandles.get(0)).time;
/* 1278 */     int start = 0;
/* 1279 */     CandleData candleAsk = new CandleData();
/* 1280 */     CandleData candleBid = new CandleData();
/* 1281 */     int i = 0; for (int j = requestData.askCandles.size(); i < j; i++) {
/* 1282 */       CandleData dataElementAsk = (CandleData)requestData.askCandles.get(i);
/* 1283 */       CandleData dataElementBid = (CandleData)requestData.bidCandles.get(i);
/* 1284 */       if ((dataElementAsk == null) || (dataElementBid == null) || (dataElementAsk.time != dataElementBid.time))
/*      */       {
/* 1286 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/*      */ 
/* 1289 */       if (this.filter == Filter.NO_FILTER) {
/* 1290 */         if (dataElementAsk.time != candleStart) {
/* 1291 */           throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */         }
/* 1293 */         candleStart = DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, candleStart);
/*      */       } else {
/* 1295 */         if (dataElementAsk.time < candleStart) {
/* 1296 */           throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */         }
/* 1298 */         candleStart = DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, dataElementAsk.time);
/*      */       }
/* 1300 */       boolean firstTick = true;
/* 1301 */       for (int k = requestData.dataLoaded.size(); (start < k) && (((TickData)requestData.dataLoaded.get(start)).time / 1000L <= dataElementAsk.time / 1000L); start++) {
/* 1302 */         TickData tickData = (TickData)requestData.dataLoaded.get(start);
/* 1303 */         if (tickData.time / 1000L == dataElementAsk.time / 1000L) {
/* 1304 */           if (firstTick) {
/* 1305 */             initCandleWithTick(candleAsk, tickData, OfferSide.ASK);
/* 1306 */             initCandleWithTick(candleBid, tickData, OfferSide.BID);
/* 1307 */             firstTick = false;
/*      */           } else {
/* 1309 */             addTickAtTheEnd(candleAsk, tickData, OfferSide.ASK);
/* 1310 */             addTickAtTheEnd(candleBid, tickData, OfferSide.BID);
/*      */           }
/*      */         }
/*      */       }
/* 1314 */       if ((firstTick) || (
/* 1315 */         (dataElementAsk.equals(candleAsk)) && (dataElementBid.equals(candleBid)))) continue;
/* 1316 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean addFirstDataIfNeeded(long oldTime)
/*      */   {
/* 1323 */     if (oldTime == -9223372036854775808L)
/*      */     {
/* 1325 */       this.lastIndex = 0;
/* 1326 */       this.loadedNumberOfSeconds = 1L;
/* 1327 */       this.loadedToInSeconds = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time);
/* 1328 */       this.buffer[this.lastIndex] = ((TickData)this.firstData);
/* 1329 */       this.oneSecLastIndex = 0;
/* 1330 */       this.oneSecBufferAsk[this.oneSecLastIndex] = new CandleData();
/* 1331 */       this.oneSecBufferBid[this.oneSecLastIndex] = new CandleData();
/* 1332 */       initCandleWithTick(this.oneSecBufferAsk[this.oneSecLastIndex], this.buffer[this.lastIndex], OfferSide.ASK);
/* 1333 */       initCandleWithTick(this.oneSecBufferBid[this.oneSecLastIndex], this.buffer[this.lastIndex], OfferSide.BID);
/* 1334 */       this.gaps = new long[0][];
/* 1335 */       recalculateIndicators();
/* 1336 */       checkConsistency();
/* 1337 */       return true;
/* 1338 */     }if ((this.lastIndex != -1) && (this.buffer[this.lastIndex].time == oldTime)) {
/* 1339 */       long lastCandleTime = this.oneSecBufferAsk[this.oneSecLastIndex].time;
/*      */       int recalculateIndexStart;
/*      */       int recalculateIndexStart;
/* 1341 */       if (((TickData)this.firstData).time / 1000L == lastCandleTime / 1000L) {
/* 1342 */         addTickAtTheEnd(this.oneSecBufferAsk[this.oneSecLastIndex], (TickData)this.firstData, OfferSide.ASK);
/* 1343 */         addTickAtTheEnd(this.oneSecBufferBid[this.oneSecLastIndex], (TickData)this.firstData, OfferSide.BID);
/* 1344 */         addTick((TickData)this.firstData);
/* 1345 */         recalculateIndexStart = this.oneSecLastIndex;
/*      */       } else {
/* 1347 */         int addedSecondsCount = 0;
/*      */ 
/* 1350 */         long firstDataCandleTime = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, ((TickData)this.firstData).time);
/* 1351 */         if ((DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, lastCandleTime) < firstDataCandleTime) && (!isWeekendsBetween(lastCandleTime, firstDataCandleTime)))
/*      */         {
/* 1353 */           long nextCandleTime = DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, lastCandleTime);
/* 1354 */           while (nextCandleTime < firstDataCandleTime) {
/* 1355 */             double priceAsk = this.oneSecBufferAsk[this.oneSecLastIndex].close;
/* 1356 */             double priceBid = this.oneSecBufferBid[this.oneSecLastIndex].close;
/* 1357 */             if (this.oneSecLastIndex + 1 >= this.oneSecBufferAsk.length) {
/* 1358 */               shiftLeft(200);
/*      */             }
/* 1360 */             this.oneSecLastIndex += 1;
/* 1361 */             addedSecondsCount++;
/* 1362 */             this.oneSecBufferAsk[this.oneSecLastIndex] = new CandleData(nextCandleTime, priceAsk, priceAsk, priceAsk, priceAsk, 0.0D);
/* 1363 */             this.oneSecBufferBid[this.oneSecLastIndex] = new CandleData(nextCandleTime, priceBid, priceBid, priceBid, priceBid, 0.0D);
/* 1364 */             this.loadedToInSeconds = nextCandleTime;
/* 1365 */             this.loadedNumberOfSeconds = (this.oneSecLastIndex + 1);
/* 1366 */             nextCandleTime = DataCacheUtils.getNextCandleStartFast(this.ALIGNMENT_PERIOD, nextCandleTime);
/*      */           }
/*      */         }
/*      */ 
/* 1370 */         if (this.oneSecLastIndex + 1 >= this.oneSecBufferAsk.length) {
/* 1371 */           shiftLeft(200);
/*      */         }
/* 1373 */         addTick((TickData)this.firstData);
/* 1374 */         this.oneSecLastIndex += 1;
/* 1375 */         addedSecondsCount++;
/* 1376 */         this.oneSecBufferAsk[this.oneSecLastIndex] = new CandleData();
/* 1377 */         this.oneSecBufferBid[this.oneSecLastIndex] = new CandleData();
/* 1378 */         initCandleWithTick(this.oneSecBufferAsk[this.oneSecLastIndex], this.buffer[this.lastIndex], OfferSide.ASK);
/* 1379 */         initCandleWithTick(this.oneSecBufferBid[this.oneSecLastIndex], this.buffer[this.lastIndex], OfferSide.BID);
/* 1380 */         this.loadedToInSeconds = firstDataCandleTime;
/* 1381 */         this.loadedNumberOfSeconds = (this.oneSecLastIndex + 1);
/* 1382 */         long firstGapCandleTime = this.oneSecBufferAsk[(this.oneSecLastIndex - 1)].time + 1000L;
/* 1383 */         if (firstGapCandleTime != this.oneSecBufferAsk[this.oneSecLastIndex].time)
/*      */         {
/* 1385 */           this.gaps = new long[][] { { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, firstGapCandleTime, this.oneSecBufferAsk[this.oneSecLastIndex].time - 1000L) } };
/*      */         }
/*      */         int recalculateIndexStart;
/* 1389 */         if (addedSecondsCount >= this.oneSecLastIndex + 1)
/* 1390 */           recalculateIndexStart = 0;
/*      */         else {
/* 1392 */           recalculateIndexStart = this.oneSecLastIndex + 1 - addedSecondsCount;
/*      */         }
/*      */       }
/* 1395 */       recalculateIndicators(recalculateIndexStart, this.oneSecLastIndex);
/* 1396 */       checkConsistency();
/* 1397 */       return true;
/*      */     }
/* 1399 */     return false;
/*      */   }
/*      */ 
/*      */   private void addTick(TickData firstData) {
/* 1403 */     if (this.lastIndex + 1 >= this.buffer.length) {
/* 1404 */       this.buffer = ((TickData[])Arrays.copyOf(this.buffer, this.buffer.length + 200));
/*      */     }
/* 1406 */     this.buffer[(++this.lastIndex)] = firstData;
/*      */   }
/*      */ 
/*      */   private void addTickAtTheEnd(CandleData candle, TickData chartsTick, OfferSide side) {
/* 1410 */     candle.close = (side == OfferSide.ASK ? chartsTick.ask : chartsTick.bid);
/* 1411 */     candle.high = (candle.high < candle.close ? candle.close : candle.high);
/* 1412 */     candle.low = (candle.low > candle.close ? candle.close : candle.low);
/* 1413 */     candle.vol = StratUtils.round(candle.vol + (side == OfferSide.ASK ? chartsTick.askVol : chartsTick.bidVol), 6);
/*      */   }
/*      */ 
/*      */   private void initCandleWithTick(CandleData candle, TickData chartsTick, OfferSide side) {
/* 1417 */     candle.time = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, chartsTick.time);
/* 1418 */     double price = side == OfferSide.ASK ? chartsTick.ask : chartsTick.bid;
/* 1419 */     candle.open = price;
/* 1420 */     candle.close = price;
/* 1421 */     candle.high = price;
/* 1422 */     candle.low = price;
/* 1423 */     candle.vol = (side == OfferSide.ASK ? chartsTick.askVol : chartsTick.bidVol);
/*      */   }
/*      */ 
/*      */   public synchronized long getLastLoadedDataTime()
/*      */   {
/* 1429 */     if (this.oneSecLastIndex == -1) {
/* 1430 */       return -9223372036854775808L;
/*      */     }
/* 1432 */     return this.oneSecBufferAsk[this.oneSecLastIndex].time;
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/* 1438 */     if (this.firstDataListener != null) {
/* 1439 */       this.feedDataProvider.unsubscribeFromLiveFeed(this.instrument, this.firstDataListener);
/*      */     }
/* 1441 */     if (this.dataCacheRequestData != null) {
/* 1442 */       this.dataCacheRequestData.cancel = true;
/* 1443 */       this.dataCacheRequestData = null;
/*      */     }
/* 1445 */     super.dispose();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1527 */     return "TicksDataProvider(" + this.instrument + ", " + this.period + ")";
/*      */   }
/*      */ 
/*      */   public DataType getDataType()
/*      */   {
/* 1532 */     return DataType.TICKS;
/*      */   }
/*      */ 
/*      */   protected TickData[] getAllBufferedData()
/*      */   {
/* 1537 */     if (this.buffer == null) {
/* 1538 */       return null;
/*      */     }
/* 1540 */     int bufferSize = this.lastIndex + 1;
/* 1541 */     TickData[] copy = new TickData[bufferSize];
/* 1542 */     System.arraycopy(this.buffer, 0, copy, 0, bufferSize);
/* 1543 */     return copy;
/*      */   }
/*      */ 
/*      */   protected TickDataSequence createNullDataSequence()
/*      */   {
/* 1548 */     return new NullTickDataSequence();
/*      */   }
/*      */ 
/*      */   protected TickDataSequence createDataSequence(TickData[] data, boolean includesLatestData)
/*      */   {
/*      */     TickDataSequence result;
/*      */     TickDataSequence result;
/* 1557 */     if ((data == null) || (data.length <= 0)) {
/* 1558 */       result = createNullDataSequence();
/*      */     }
/*      */     else {
/* 1561 */       long from = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, data[0].getTime());
/* 1562 */       long to = DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, data[(data.length - 1)].getTime());
/*      */ 
/* 1564 */       int fromIndex = TimeDataUtils.strictTimeIndex(this.oneSecBufferAsk, from);
/* 1565 */       int toIndex = TimeDataUtils.strictTimeIndex(this.oneSecBufferAsk, to);
/*      */ 
/* 1567 */       CandleData[] oneSecAskCandles = null;
/* 1568 */       CandleData[] oneSecBidCandles = null;
/*      */ 
/* 1570 */       if ((fromIndex > -1) && (fromIndex < this.oneSecBufferAsk.length) && (toIndex > -1) && (toIndex < this.oneSecBufferAsk.length))
/*      */       {
/* 1577 */         int size = toIndex - fromIndex + 1;
/* 1578 */         oneSecAskCandles = new CandleData[size];
/* 1579 */         oneSecBidCandles = new CandleData[size];
/* 1580 */         System.arraycopy(this.oneSecBufferAsk, fromIndex, oneSecAskCandles, 0, oneSecAskCandles.length);
/* 1581 */         System.arraycopy(this.oneSecBufferBid, fromIndex, oneSecBidCandles, 0, oneSecBidCandles.length);
/*      */       }
/*      */ 
/* 1584 */       result = new TickDataSequence(from, to, 0, 0, data, (long[][])null, 0, 0, oneSecAskCandles, oneSecBidCandles, null, null, includesLatestData, includesLatestData);
/*      */     }
/*      */ 
/* 1601 */     return result;
/*      */   }
/*      */ 
/*      */   protected TickData[] createArray(int size)
/*      */   {
/* 1606 */     return new TickData[size];
/*      */   }
/*      */ 
/*      */   protected IFeedDescriptor getFeedDescriptor()
/*      */   {
/* 1611 */     IFeedDescriptor result = new FeedDescriptor();
/* 1612 */     result.setDataType(getDataType());
/* 1613 */     result.setInstrument(getInstrument());
/* 1614 */     result.setPeriod(getPeriod());
/* 1615 */     return result;
/*      */   }
/*      */ 
/*      */   public long getFirstKnownTime()
/*      */   {
/* 1620 */     return this.feedDataProvider.getTimeOfFirstTick(getInstrument());
/*      */   }
/*      */ 
/*      */   protected TickDataSequence doGetDataSequence(long from, long to)
/*      */   {
/* 1625 */     if (from != DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, from)) {
/* 1626 */       throw new IllegalArgumentException(DATE_FORMAT.format(Long.valueOf(from)) + " is not " + this.ALIGNMENT_PERIOD + " candle start");
/*      */     }
/* 1628 */     if (to != DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, to)) {
/* 1629 */       throw new IllegalArgumentException(DATE_FORMAT.format(Long.valueOf(to)) + " is not " + this.ALIGNMENT_PERIOD + " candle start");
/*      */     }
/*      */ 
/* 1632 */     int numberOfSecondsBefore = DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, from, to);
/* 1633 */     TickDataSequence seq = getDataSequence(numberOfSecondsBefore, to, 0);
/* 1634 */     return seq;
/*      */   }
/*      */ 
/*      */   public AbstractDataProvider<TickData, TickDataSequence>.LoadDataProgressListener doHistoryRequests(long from, long to)
/*      */   {
/* 1639 */     if (from != DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, from)) {
/* 1640 */       throw new IllegalArgumentException(DATE_FORMAT.format(Long.valueOf(from)) + " is not " + this.ALIGNMENT_PERIOD + " candle start");
/*      */     }
/* 1642 */     if (to != DataCacheUtils.getCandleStartFast(this.ALIGNMENT_PERIOD, to)) {
/* 1643 */       throw new IllegalArgumentException(DATE_FORMAT.format(Long.valueOf(to)) + " is not " + this.ALIGNMENT_PERIOD + " candle start");
/*      */     }
/*      */ 
/* 1646 */     int numberOfSecondsBefore = DataCacheUtils.getCandlesCountBetweenFast(this.ALIGNMENT_PERIOD, from, to);
/* 1647 */     return doHistoryRequests(numberOfSecondsBefore, to, 0);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   43 */     LOGGER = LoggerFactory.getLogger(TicksDataProvider.class);
/*      */   }
/*      */ 
/*      */   protected static class LoadDataListener
/*      */     implements LiveFeedListener
/*      */   {
/*      */     private TicksDataProvider.DataCacheRequestData dataCacheRequestData;
/* 1466 */     private long prevTime = -9223372036854775808L;
/* 1467 */     private long prevCandleTime = -9223372036854775808L;
/*      */     private boolean fromEnd;
/*      */ 
/*      */     public LoadDataListener(TicksDataProvider.DataCacheRequestData dataCacheRequestData, boolean fromEnd)
/*      */     {
/* 1471 */       this.dataCacheRequestData = dataCacheRequestData;
/* 1472 */       this.fromEnd = fromEnd;
/* 1473 */       if (fromEnd) {
/* 1474 */         this.prevTime = 9223372036854775807L;
/* 1475 */         this.prevCandleTime = 9223372036854775807L;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */     {
/* 1481 */       if (!this.dataCacheRequestData.cancel) {
/* 1482 */         if (((!this.fromEnd) && (this.prevCandleTime > time)) || ((this.fromEnd) && (this.prevCandleTime < time)))
/* 1483 */           throw new RuntimeException("Received candles from data cache are not in ascending order");
/*      */         List candles;
/*      */         List candles;
/* 1486 */         if (side == OfferSide.ASK)
/* 1487 */           candles = this.dataCacheRequestData.askCandles;
/*      */         else {
/* 1489 */           candles = this.dataCacheRequestData.bidCandles;
/*      */         }
/* 1491 */         if (!this.fromEnd)
/* 1492 */           candles.add(new CandleData(time, open, close, low, high, vol));
/*      */         else {
/* 1494 */           candles.add(0, new CandleData(time, open, close, low, high, vol));
/*      */         }
/*      */ 
/* 1497 */         this.prevCandleTime = time;
/*      */       }
/*      */       else {
/* 1500 */         this.dataCacheRequestData.dataLoaded = null;
/* 1501 */         this.dataCacheRequestData.progressListener = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/* 1506 */       if (!this.dataCacheRequestData.cancel) {
/* 1507 */         if (((!this.fromEnd) && (this.prevTime > time)) || ((this.fromEnd) && (this.prevTime < time))) {
/* 1508 */           throw new RuntimeException("Received ticks from data cache are not in ascending order");
/*      */         }
/* 1510 */         if (!this.fromEnd)
/* 1511 */           this.dataCacheRequestData.dataLoaded.add(new TickData(time, ask, bid, askVol, bidVol));
/*      */         else {
/* 1513 */           this.dataCacheRequestData.dataLoaded.add(0, new TickData(time, ask, bid, askVol, bidVol));
/*      */         }
/*      */ 
/* 1516 */         this.prevTime = time;
/*      */       }
/*      */       else {
/* 1519 */         this.dataCacheRequestData.dataLoaded = null;
/* 1520 */         this.dataCacheRequestData.progressListener = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static class DataCacheRequestData extends AbstractDataProvider.AbstractDataCacheRequestData
/*      */   {
/* 1449 */     public List<TickData> dataLoaded = new ArrayList();
/* 1450 */     public List<CandleData> askCandles = new ArrayList();
/* 1451 */     public List<CandleData> bidCandles = new ArrayList();
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1455 */       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/* 1456 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1457 */       StringBuilder stamp = new StringBuilder();
/* 1458 */       stamp.append(this.numberOfCandlesBefore).append(" - ").append(format.format(Long.valueOf(this.time))).append(" - ");
/* 1459 */       stamp.append(this.numberOfCandlesAfter).append(" loadedSize - ").append(this.dataLoaded.size());
/* 1460 */       return stamp.toString();
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.TicksDataProvider
 * JD-Core Version:    0.6.0
 */