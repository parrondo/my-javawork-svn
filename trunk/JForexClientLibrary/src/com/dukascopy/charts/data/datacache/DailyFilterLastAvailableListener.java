/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ import java.util.Calendar;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class DailyFilterLastAvailableListener
/*    */   implements LiveFeedListener
/*    */ {
/*    */   private static ThreadLocal<Calendar> calendars;
/*    */   private LiveFeedListener candleListener;
/*    */   private boolean process;
/*    */   private CandleData mondayCandle;
/*    */   private CandleData sundayCandle;
/*    */ 
/*    */   public DailyFilterLastAvailableListener(LiveFeedListener candleListener)
/*    */   {
/* 31 */     this.candleListener = candleListener;
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/* 36 */     this.candleListener.newTick(instrument, time, ask, bid, askVol, bidVol);
/*    */   }
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/* 41 */     if (period == Period.DAILY_SKIP_SUNDAY)
/*    */     {
/* 43 */       Calendar calendar = (Calendar)calendars.get();
/* 44 */       calendar.setTimeInMillis(time);
/* 45 */       if (calendar.get(7) == 1) {
/* 46 */         this.process = true;
/* 47 */       } else if (this.process)
/*    */       {
/* 49 */         assert (calendar.get(7) == 7);
/* 50 */         this.process = false;
/*    */ 
/* 52 */         this.candleListener.newCandle(instrument, period, side, time + 86400000L, close, close, close, close, 0.0D);
/*    */ 
/* 54 */         this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*    */       } else {
/* 56 */         this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*    */       }
/* 58 */     } else if (period == Period.DAILY_SUNDAY_IN_MONDAY)
/*    */     {
/* 60 */       Calendar calendar = (Calendar)calendars.get();
/* 61 */       calendar.setTimeInMillis(time);
/* 62 */       if (calendar.get(7) == 2) {
/* 63 */         this.process = true;
/* 64 */         this.mondayCandle = new CandleData(time, open, close, low, high, vol);
/* 65 */         this.sundayCandle = null;
/* 66 */       } else if (this.process) {
/* 67 */         if (calendar.get(7) == 1) {
/* 68 */           assert (this.sundayCandle == null);
/* 69 */           this.sundayCandle = new CandleData(time, open, close, low, high, vol);
/*    */         }
/*    */         else {
/* 72 */           assert (calendar.get(7) == 7);
/* 73 */           this.process = false;
/*    */ 
/* 75 */           this.mondayCandle.open = this.sundayCandle.open;
/* 76 */           this.mondayCandle.high = Math.max(this.mondayCandle.high, this.sundayCandle.high);
/* 77 */           this.mondayCandle.low = Math.min(this.mondayCandle.low, this.sundayCandle.low);
/* 78 */           this.mondayCandle.vol = StratUtils.roundHalfEven(this.mondayCandle.vol + this.sundayCandle.vol, 7);
/* 79 */           this.candleListener.newCandle(instrument, period, side, time + 172800000L, this.mondayCandle.open, this.mondayCandle.close, this.mondayCandle.low, this.mondayCandle.high, this.mondayCandle.vol);
/*    */ 
/* 82 */           this.candleListener.newCandle(instrument, period, side, time + 86400000L, close, close, close, close, 0.0D);
/*    */ 
/* 84 */           this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*    */         }
/*    */       } else {
/* 87 */         this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*    */       }
/*    */     } else {
/* 90 */       this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*    */     }
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 19 */     calendars = new ThreadLocal() {
/*    */       protected Calendar initialValue() {
/* 21 */         return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.DailyFilterLastAvailableListener
 * JD-Core Version:    0.6.0
 */