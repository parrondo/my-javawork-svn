/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.TimedData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ @Deprecated
/*     */ public class DataLoadingThread extends Thread
/*     */ {
/*  31 */   private static final Logger LOGGER = LoggerFactory.getLogger(DataLoadingThread.class);
/*     */   private final Instrument instrument;
/*     */   private final Period period;
/*     */   private final OfferSide side;
/*     */   private final BlockingQueue<TimedData> queue;
/*     */   private boolean stop;
/*     */   private final long from;
/*     */   private final long to;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */   private Period selectedPeriod;
/*     */   private OfferSide selectedOfferSide;
/*     */ 
/*     */   public DataLoadingThread(Instrument instrument, BlockingQueue<TimedData> queue, String name, long from, long to, IFeedDataProvider feedDataProvider)
/*     */   {
/*  46 */     super(name);
/*  47 */     this.instrument = instrument;
/*  48 */     this.period = Period.TICK;
/*  49 */     this.side = null;
/*  50 */     this.queue = queue;
/*     */ 
/*  52 */     this.from = from;
/*  53 */     this.to = to;
/*  54 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public DataLoadingThread(Instrument instrument, Period period, OfferSide side, BlockingQueue<TimedData> queue, String name, long from, long to, IFeedDataProvider feedDataProvider) {
/*  58 */     super(name);
/*  59 */     this.instrument = instrument;
/*  60 */     this.period = period;
/*  61 */     this.side = side;
/*  62 */     this.queue = queue;
/*     */ 
/*  64 */     this.from = from;
/*  65 */     this.to = to;
/*  66 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  71 */     LiveFeedListener feedListener = new LiveFeedListener()
/*     */     {
/*     */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */       {
/* 121 */         if (!DataLoadingThread.this.stop)
/*     */           try {
/* 123 */             DataLoadingThread.this.queue.put(new TickData(time, ask, bid, askVol, bidVol, new double[] { ask }, new double[] { bid }, new double[] { askVol }, new double[] { bidVol }));
/*     */           }
/*     */           catch (InterruptedException e)
/*     */           {
/*     */           }
/*     */       }
/*     */     };
/* 131 */     LoadingProgressListener loadingProgressListener = new LoadingProgressListener() {
/*     */       public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex) {
/* 136 */         if (allDataLoaded)
/*     */           try {
/* 138 */             DataLoadingThread.this.queue.put(new TickData(-9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, null, null, null, null));
/*     */           }
/*     */           catch (InterruptedException e) {
/*     */           }
/* 142 */         else if (ex != null)
/* 143 */           DataLoadingThread.LOGGER.error(ex.getMessage(), ex);
/*     */       }
/*     */ 
/*     */       public boolean stopJob()
/*     */       {
/* 148 */         return DataLoadingThread.this.stop;
/*     */       } } ;
/*     */     try {
/* 152 */       if (this.period == Period.TICK) {
/* 153 */         if (this.selectedPeriod == Period.TICK)
/* 154 */           this.feedDataProvider.loadTicksDataBlockingSynched(this.instrument, this.from, this.to, feedListener, loadingProgressListener);
/*     */         else
/* 156 */           this.feedDataProvider.loadCandlesDataBlockingSynched(this.instrument, this.selectedPeriod, this.selectedOfferSide, this.from, this.to, feedListener, loadingProgressListener);
/*     */       }
/*     */       else
/*     */       {
/* 160 */         long firstCandle = DataCacheUtils.getCandleStartFast(this.period, this.from);
/* 161 */         long lastCandle = DataCacheUtils.getPreviousCandleStartFast(this.period, DataCacheUtils.getCandleStartFast(this.period, this.to));
/* 162 */         if (firstCandle <= lastCandle)
/* 163 */           this.feedDataProvider.loadCandlesDataBlockingSynched(this.instrument, this.period, this.side, firstCandle, lastCandle, feedListener, loadingProgressListener);
/*     */         else
/*     */           try
/*     */           {
/* 167 */             this.queue.put(new TickData(-9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, null, null, null, null));
/*     */           }
/*     */           catch (InterruptedException e) {
/*     */           }
/*     */       }
/*     */     }
/*     */     catch (DataCacheException e) {
/* 174 */       LOGGER.error(e.getMessage(), e);
/*     */       try {
/* 176 */         this.queue.put(new TickData(-9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, null, null, null, null));
/*     */       }
/*     */       catch (InterruptedException e1) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/* 184 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public Period getPeriod() {
/* 188 */     return this.period;
/*     */   }
/*     */ 
/*     */   public OfferSide getSide() {
/* 192 */     return this.side;
/*     */   }
/*     */ 
/*     */   public BlockingQueue<TimedData> getQueue() {
/* 196 */     return this.queue;
/*     */   }
/*     */ 
/*     */   public void stopThread() {
/* 200 */     this.stop = true;
/* 201 */     while (this.queue.poll() != null);
/*     */   }
/*     */ 
/*     */   private void candle2ITickCubicSpline(long time, double open, double high, double low, double close, double volume, Instrument instrument, Period period, OfferSide side, BlockingQueue<TimedData> queue) {
/* 207 */     double spreadInPips = 2.0D;
/*     */ 
/* 209 */     long length = period.getInterval();
/* 210 */     long step = length / 4L;
/*     */ 
/* 212 */     long baseTime = time;
/*     */ 
/* 214 */     long x1 = length;
/* 215 */     long x4 = x1 + length;
/* 216 */     long x2 = x1 + step;
/* 217 */     long x3 = x2 + step;
/*     */ 
/* 219 */     double y1 = open;
/* 220 */     double y2 = open > close ? low : high;
/* 221 */     double y3 = open > close ? high : low;
/* 222 */     double y4 = close;
/*     */ 
/* 224 */     CubicSplineInterpolation cubicSplineInterpolation = new CubicSplineInterpolation(new double[] { x1, x2, x3, x4 }, new double[] { y1, y2, y3, y4 });
/*     */ 
/* 227 */     double pip = instrument.getPipValue();
/*     */ 
/* 229 */     long ticksNumber = ()((high - low) / (pip / 2.0D));
/*     */ 
/* 231 */     if (ticksNumber == 0L)
/*     */     {
/* 233 */       ticksNumber = 1L;
/*     */     }
/* 235 */     step = length / ticksNumber;
/* 236 */     double vol = StratUtils.roundHalfEven(volume / ticksNumber, 2);
/*     */ 
/* 238 */     for (int i = 0; i < ticksNumber + 1L; i++) {
/* 239 */       long xx = x1 + step * i;
/*     */       double bid;
/*     */       double bid;
/*     */       double ask;
/* 242 */       if (side == OfferSide.ASK) {
/* 243 */         double ask = StratUtils.round(cubicSplineInterpolation.interpolate(xx), 5);
/* 244 */         bid = StratUtils.round(ask - pip * spreadInPips, 5);
/*     */       } else {
/* 246 */         bid = StratUtils.round(cubicSplineInterpolation.interpolate(xx), 5);
/* 247 */         ask = StratUtils.round(bid + pip * spreadInPips, 5);
/*     */       }
/* 249 */       if (this.stop) continue;
/*     */       try {
/* 251 */         queue.put(new TickData(baseTime + step * i, ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void candle2ITickFourTicks(long time, double open, double high, double low, double close, double volume, Instrument instrument, Period period, OfferSide side, BlockingQueue<TimedData> queue)
/*     */   {
/* 262 */     double spreadInPipsAsk = (side == OfferSide.ASK ? 0.0D : 2.0D) * instrument.getPipValue();
/* 263 */     double spreadInPipsBid = (side == OfferSide.ASK ? -2.0D : 0.0D) * instrument.getPipValue();
/*     */ 
/* 265 */     double length = period.getInterval();
/* 266 */     double step = length / 8.0D;
/*     */ 
/* 269 */     double ask = StratUtils.round(open + spreadInPipsAsk, 5);
/* 270 */     double bid = StratUtils.round(open + spreadInPipsBid, 5);
/* 271 */     double vol = StratUtils.round(volume / 4.0D, 5);
/* 272 */     if (!this.stop) {
/*     */       try {
/* 274 */         queue.put(new TickData(()(time + step), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 282 */     double lowhigh = close < open ? high : low;
/* 283 */     ask = StratUtils.round(lowhigh + spreadInPipsAsk, 5);
/* 284 */     bid = StratUtils.round(lowhigh + spreadInPipsBid, 5);
/* 285 */     if (!this.stop) {
/*     */       try {
/* 287 */         queue.put(new TickData(()(time + step * 3.0D), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 295 */     lowhigh = close < open ? low : high;
/* 296 */     ask = StratUtils.round(lowhigh + spreadInPipsAsk, 5);
/* 297 */     bid = StratUtils.round(lowhigh + spreadInPipsBid, 5);
/* 298 */     if (!this.stop) {
/*     */       try {
/* 300 */         queue.put(new TickData(()(time + step * 5.0D), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 308 */     ask = StratUtils.round(close + spreadInPipsAsk, 5);
/* 309 */     bid = StratUtils.round(close + spreadInPipsBid, 5);
/* 310 */     if (!this.stop)
/*     */       try {
/* 312 */         queue.put(new TickData(()(time + step * 7.0D), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setSelectedPeriod(Period selectedPeriod)
/*     */   {
/* 321 */     this.selectedPeriod = selectedPeriod;
/*     */   }
/*     */ 
/*     */   public void setSelectedOfferSide(OfferSide selectedOfferSide) {
/* 325 */     this.selectedOfferSide = selectedOfferSide;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.DataLoadingThread
 * JD-Core Version:    0.6.0
 */