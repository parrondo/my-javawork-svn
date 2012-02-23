/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*    */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.dds2.greed.connection.GreedClientListener;
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*    */ import java.math.BigDecimal;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Calendar;
/*    */ import java.util.Currency;
/*    */ import java.util.List;
/*    */ import java.util.TimeZone;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class FakeTickOnWeekendsAction extends AppActionEvent
/*    */ {
/* 32 */   private static final Logger LOGGER = LoggerFactory.getLogger(FakeTickOnWeekendsAction.class);
/*    */   private TickData lastTick;
/*    */   private boolean loadedSuccessfully;
/*    */   private Exception loadingException;
/*    */   private Instrument instrument;
/*    */ 
/*    */   public FakeTickOnWeekendsAction(Object source, Instrument instrument)
/*    */   {
/* 40 */     super(source, false, false);
/* 41 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 47 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 48 */     calendar.setFirstDayOfWeek(2);
/* 49 */     calendar.set(7, 6);
/* 50 */     calendar.set(11, 21);
/* 51 */     calendar.set(12, 0);
/* 52 */     calendar.set(13, 0);
/* 53 */     calendar.set(14, 0);
/* 54 */     long weekendStart = calendar.getTimeInMillis();
/* 55 */     calendar.set(7, 1);
/* 56 */     calendar.set(11, 22);
/* 57 */     long weekendEnd = calendar.getTimeInMillis();
/* 58 */     long currentTime = System.currentTimeMillis();
/* 59 */     if ((currentTime > weekendStart) && (currentTime < weekendEnd))
/*    */     {
/*    */       try {
/* 62 */         Thread.sleep(10000L);
/*    */       } catch (InterruptedException e) {
/* 64 */         LOGGER.error(e.getMessage(), e);
/*    */       }
/*    */ 
/* 67 */       this.lastTick = FeedDataProvider.getDefaultInstance().getLastTick(this.instrument);
/* 68 */       if (this.lastTick == null)
/*    */       {
/*    */         try {
/* 71 */           FeedDataProvider.getDefaultInstance().loadTicksDataSynched(this.instrument, weekendStart - 600000L, weekendStart + 4500000L, new LiveFeedListener()
/*    */           {
/*    */             public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/* 74 */               FakeTickOnWeekendsAction.access$002(FakeTickOnWeekendsAction.this, new TickData(time, ask, bid, askVol, bidVol));
/*    */             }
/*    */ 
/*    */             public void newCandle(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */             {
/*    */             }
/*    */           }
/*    */           , new LoadingProgressListener()
/*    */           {
/*    */             public void dataLoaded(long start, long end, long currentPosition, String information)
/*    */             {
/*    */             }
/*    */ 
/*    */             public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*    */             {
/* 87 */               FakeTickOnWeekendsAction.access$102(FakeTickOnWeekendsAction.this, allDataLoaded);
/* 88 */               FakeTickOnWeekendsAction.access$202(FakeTickOnWeekendsAction.this, e);
/*    */             }
/*    */ 
/*    */             public boolean stopJob()
/*    */             {
/* 93 */               return false;
/*    */             } } );
/*    */         } catch (DataCacheException e) {
/* 97 */           this.loadedSuccessfully = false;
/* 98 */           this.loadingException = e;
/*    */         }
/* 100 */         if ((this.loadedSuccessfully) && (this.lastTick != null))
/*    */         {
/* 102 */           if (FeedDataProvider.getDefaultInstance().isSubscribedToInstrument(this.instrument))
/*    */           {
/* 104 */             TickData maybeLastTick = FeedDataProvider.getDefaultInstance().getLastTick(this.instrument);
/* 105 */             if (maybeLastTick == null)
/*    */             {
/* 107 */               String currencyPrimary = this.instrument.getPrimaryCurrency().getCurrencyCode();
/* 108 */               String currencySecondary = this.instrument.getSecondaryCurrency().getCurrencyCode();
/* 109 */               List bids = new ArrayList(1);
/* 110 */               List asks = new ArrayList(1);
/* 111 */               bids.add(new CurrencyOffer(currencyPrimary, currencySecondary, com.dukascopy.transport.common.model.type.OfferSide.BID, new Money(BigDecimal.valueOf(this.lastTick.bidVol), this.instrument.getPrimaryCurrency()), new Money(BigDecimal.valueOf(this.lastTick.bid), this.instrument.getPrimaryCurrency())));
/*    */ 
/* 115 */               asks.add(new CurrencyOffer(currencyPrimary, currencySecondary, com.dukascopy.transport.common.model.type.OfferSide.ASK, new Money(BigDecimal.valueOf(this.lastTick.askVol), this.instrument.getPrimaryCurrency()), new Money(BigDecimal.valueOf(this.lastTick.ask), this.instrument.getPrimaryCurrency())));
/*    */ 
/* 119 */               CurrencyMarket currencyMarket = new CurrencyMarket(currencyPrimary, currencySecondary, bids, asks);
/* 120 */               currencyMarket.setCreationTimestamp(Long.valueOf(this.lastTick.getTime()));
/* 121 */               currencyMarket.setIsBackup(true);
/* 122 */               GreedClientListener.getInstance().feedbackMessageReceived(null, currencyMarket);
/*    */             }
/*    */           }
/*    */         } else {
/* 126 */           LOGGER.error("Error while loading last tick for instrument [" + this.instrument + "]");
/* 127 */           if (this.loadingException != null)
/* 128 */             LOGGER.error(this.loadingException.getMessage(), this.loadingException);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.FakeTickOnWeekendsAction
 * JD-Core Version:    0.6.0
 */