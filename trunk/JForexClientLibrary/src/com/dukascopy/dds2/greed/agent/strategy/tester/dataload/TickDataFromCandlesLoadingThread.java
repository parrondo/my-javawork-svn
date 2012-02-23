/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.system.ITesterClient.InterpolationMethod;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.CubicSplineInterpolation;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class TickDataFromCandlesLoadingThread extends AbstractDataLoadingThread<TickData>
/*     */ {
/*     */   private final ITesterClient.InterpolationMethod interpolationMethod;
/*     */   private final JForexPeriod candlesJForexPeriod;
/*     */   private final OfferSide candlesOfferSide;
/*     */ 
/*     */   public TickDataFromCandlesLoadingThread(String name, Instrument instrument, JForexPeriod candlesJForexPeriod, OfferSide candlesOfferSide, BlockingQueue<TickData> queue, long from, long to, IFeedDataProvider feedDataProvider, ITesterClient.InterpolationMethod interpolationMethod)
/*     */   {
/*  39 */     super(name, instrument, new JForexPeriod(DataType.TICKS, Period.TICK), null, queue, from, to, feedDataProvider);
/*     */ 
/*  50 */     this.interpolationMethod = interpolationMethod;
/*  51 */     this.candlesJForexPeriod = candlesJForexPeriod;
/*  52 */     this.candlesOfferSide = candlesOfferSide;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  57 */     LiveFeedListener feedListener = new LiveFeedListener()
/*     */     {
/*     */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*  60 */         if (!TickDataFromCandlesLoadingThread.this.isStop())
/*     */         {
/*     */           double spreadInPipsAsk;
/*     */           double spreadInPipsBid;
/*     */           double ask;
/*     */           double bid;
/*  61 */           switch (TickDataFromCandlesLoadingThread.2.$SwitchMap$com$dukascopy$api$system$ITesterClient$InterpolationMethod[TickDataFromCandlesLoadingThread.this.interpolationMethod.ordinal()]) {
/*     */           case 1:
/*  63 */             spreadInPipsAsk = (side == OfferSide.ASK ? 0.0D : 2.0D) * instrument.getPipValue();
/*  64 */             spreadInPipsBid = (side == OfferSide.ASK ? -2.0D : 0.0D) * instrument.getPipValue();
/*  65 */             ask = StratUtils.round(open + spreadInPipsAsk, 5);
/*  66 */             bid = StratUtils.round(open + spreadInPipsBid, 5);
/*  67 */             TickDataFromCandlesLoadingThread.this.putDataToQueue(new TickData(time, ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */ 
/*  70 */             return;
/*     */           case 2:
/*  72 */             spreadInPipsAsk = (side == OfferSide.ASK ? 0.0D : 2.0D) * instrument.getPipValue();
/*  73 */             spreadInPipsBid = (side == OfferSide.ASK ? -2.0D : 0.0D) * instrument.getPipValue();
/*  74 */             ask = StratUtils.round(close + spreadInPipsAsk, 5);
/*  75 */             bid = StratUtils.round(close + spreadInPipsBid, 5);
/*  76 */             TickDataFromCandlesLoadingThread.this.putDataToQueue(new TickData(DataCacheUtils.getNextCandleStartFast(period, time) - 1L, ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */ 
/*  79 */             return;
/*     */           case 3:
/*  81 */             TickDataFromCandlesLoadingThread.this.candle2ITickFourTicks(time, open, high, low, close, vol, instrument, period, side);
/*  82 */             break;
/*     */           case 4:
/*  84 */             TickDataFromCandlesLoadingThread.this.candle2ITickCubicSpline(time, open, high, low, close, vol, instrument, period, side);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */       {
/*     */       }
/*     */     };
/*  93 */     LoadingProgressListener loadingProgressListener = createLoadingProgressListener();
/*     */     try
/*     */     {
/*  96 */       getFeedDataProvider().loadCandlesDataBlockingSynched(getInstrument(), getCandlesJForexPeriod().getPeriod(), getCandlesOfferSide(), getFrom(), getTo(), feedListener, loadingProgressListener);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 106 */       LOGGER.error(e.getLocalizedMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void candle2ITickCubicSpline(long time, double open, double high, double low, double close, double volume, Instrument instrument, Period period, OfferSide side)
/*     */   {
/* 123 */     double spreadInPips = 2.0D;
/*     */ 
/* 125 */     long length = period.getInterval();
/* 126 */     long step = length / 4L;
/*     */ 
/* 128 */     long baseTime = time;
/*     */ 
/* 130 */     long x1 = length;
/* 131 */     long x4 = x1 + length;
/* 132 */     long x2 = x1 + step;
/* 133 */     long x3 = x2 + step;
/*     */ 
/* 135 */     double y1 = open;
/* 136 */     double y2 = open > close ? low : high;
/* 137 */     double y3 = open > close ? high : low;
/* 138 */     double y4 = close;
/*     */ 
/* 140 */     CubicSplineInterpolation cubicSplineInterpolation = new CubicSplineInterpolation(new double[] { x1, x2, x3, x4 }, new double[] { y1, y2, y3, y4 });
/*     */ 
/* 143 */     double pip = instrument.getPipValue();
/*     */ 
/* 145 */     long ticksNumber = ()((high - low) / (pip / 2.0D));
/* 146 */     if (ticksNumber == 0L)
/*     */     {
/* 148 */       ticksNumber = 1L;
/*     */     }
/* 150 */     step = length / ticksNumber;
/* 151 */     double vol = StratUtils.roundHalfEven(volume / ticksNumber, 2);
/*     */ 
/* 153 */     for (int i = 0; i < ticksNumber + 1L; i++) {
/* 154 */       long xx = x1 + step * i;
/*     */       double bid;
/*     */       double bid;
/*     */       double ask;
/* 157 */       if (side == OfferSide.ASK) {
/* 158 */         double ask = StratUtils.round(cubicSplineInterpolation.interpolate(xx), 5);
/* 159 */         bid = StratUtils.round(ask - pip * spreadInPips, 5);
/*     */       } else {
/* 161 */         bid = StratUtils.round(cubicSplineInterpolation.interpolate(xx), 5);
/* 162 */         ask = StratUtils.round(bid + pip * spreadInPips, 5);
/*     */       }
/* 164 */       if (!isStop())
/* 165 */         putDataToQueue(new TickData(baseTime + step * i, ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void candle2ITickFourTicks(long time, double open, double high, double low, double close, double volume, Instrument instrument, Period period, OfferSide side)
/*     */   {
/* 182 */     double spreadInPipsAsk = (side == OfferSide.ASK ? 0.0D : 2.0D) * instrument.getPipValue();
/* 183 */     double spreadInPipsBid = (side == OfferSide.ASK ? -2.0D : 0.0D) * instrument.getPipValue();
/*     */ 
/* 185 */     double length = period.getInterval();
/* 186 */     double step = length / 8.0D;
/*     */ 
/* 189 */     double ask = StratUtils.round(open + spreadInPipsAsk, 5);
/* 190 */     double bid = StratUtils.round(open + spreadInPipsBid, 5);
/* 191 */     double vol = StratUtils.round(volume / 4.0D, 5);
/* 192 */     if (!isStop()) {
/* 193 */       putDataToQueue(new TickData(()(time + step), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */     }
/*     */ 
/* 198 */     double lowhigh = close < open ? high : low;
/* 199 */     ask = StratUtils.round(lowhigh + spreadInPipsAsk, 5);
/* 200 */     bid = StratUtils.round(lowhigh + spreadInPipsBid, 5);
/* 201 */     if (!isStop()) {
/* 202 */       putDataToQueue(new TickData(()(time + step * 3.0D), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */     }
/*     */ 
/* 207 */     lowhigh = close < open ? low : high;
/* 208 */     ask = StratUtils.round(lowhigh + spreadInPipsAsk, 5);
/* 209 */     bid = StratUtils.round(lowhigh + spreadInPipsBid, 5);
/* 210 */     if (!isStop()) {
/* 211 */       putDataToQueue(new TickData(()(time + step * 5.0D), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */     }
/*     */ 
/* 216 */     ask = StratUtils.round(close + spreadInPipsAsk, 5);
/* 217 */     bid = StratUtils.round(close + spreadInPipsBid, 5);
/* 218 */     if (!isStop())
/* 219 */       putDataToQueue(new TickData(()(time + step * 7.0D), ask, bid, vol, vol, new double[] { ask }, new double[] { bid }, new double[] { vol }, new double[] { vol }));
/*     */   }
/*     */ 
/*     */   protected TickData createEmptyBar()
/*     */   {
/* 226 */     return new TickData(-9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, null, null, null, null);
/*     */   }
/*     */ 
/*     */   private JForexPeriod getCandlesJForexPeriod() {
/* 230 */     return this.candlesJForexPeriod;
/*     */   }
/*     */ 
/*     */   private OfferSide getCandlesOfferSide() {
/* 234 */     return this.candlesOfferSide;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TickDataFromCandlesLoadingThread
 * JD-Core Version:    0.6.0
 */