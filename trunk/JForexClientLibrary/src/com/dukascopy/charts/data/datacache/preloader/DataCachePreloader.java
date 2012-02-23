/*     */ package com.dukascopy.charts.data.datacache.preloader;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class DataCachePreloader
/*     */   implements IDataCachePreloader
/*     */ {
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */   private static final long TICK_LOADING_INTERVAL = 3600000L;
/*     */ 
/*     */   public DataCachePreloader(IFeedDataProvider feedDataProvider)
/*     */   {
/*  29 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public IFeedDataProvider getFeedDataProvider()
/*     */   {
/*  34 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void loadTicksInCache(List<Instrument> instruments, TimeInterval timeInterval, DataCachePreloadControl preloadControl)
/*     */   {
/*  44 */     List instrumentTimeIntervals = new ArrayList();
/*  45 */     for (Instrument instrument : instruments) {
/*  46 */       InstrumentTimeInterval iti = new InstrumentTimeInterval(instrument, timeInterval.getStart(), timeInterval.getEnd());
/*  47 */       instrumentTimeIntervals.add(iti);
/*     */     }
/*  49 */     loadTicksInCache(instrumentTimeIntervals, preloadControl);
/*     */   }
/*     */ 
/*     */   public void loadTicksInCache(List<InstrumentTimeInterval> instrumentTimeIntervals, DataCachePreloadControl preloadControl)
/*     */   {
/*  58 */     validateParams(instrumentTimeIntervals, preloadControl);
/*     */ 
/*  60 */     Runnable task = new Runnable(preloadControl, instrumentTimeIntervals)
/*     */     {
/*     */       public void run()
/*     */       {
/*  64 */         boolean allDataLoaded = true;
/*     */         try
/*     */         {
/*  67 */           this.val$preloadControl.loadingProcessStarted();
/*     */ 
/*  69 */           totalInterval = DataCachePreloader.this.calculateTotalInterval(this.val$instrumentTimeIntervals);
/*  70 */           totalIntervalAlreadyLoaded = 0.0D;
/*     */ 
/*  72 */           for (InstrumentTimeInterval iti : this.val$instrumentTimeIntervals)
/*     */           {
/*  74 */             if (this.val$preloadControl.isStopped()) { allDataLoaded = false;
/*     */               return;
/*     */             }
/*  79 */             Instrument instrument = iti.getInstrument();
/*  80 */             boolean allInstrumentDataLoaded = true;
/*     */             try
/*     */             {
/*  83 */               this.val$preloadControl.loadingStarted(instrument, iti);
/*     */ 
/*  85 */               long from = iti.getStart();
/*     */               do
/*     */               {
/*  88 */                 if (this.val$preloadControl.isStopped()) {
/*  89 */                   allInstrumentDataLoaded = false;
/*  90 */                   allDataLoaded = false;
/*     */ 
/* 124 */                   this.val$preloadControl.loadingFinished(instrument, iti, allInstrumentDataLoaded);
/*     */ 
/* 128 */                   if (allDataLoaded) {
/* 129 */                     this.val$preloadControl.loadingProcessed(1.0D);
/*     */                   }
/*     */ 
/* 132 */                   this.val$preloadControl.loadingProcessFinished(allDataLoaded); return;
/*     */                 }
/*  94 */                 long to = from + 3600000L;
/*     */ 
/*  96 */                 if (to > iti.getEnd()) {
/*  97 */                   to = iti.getEnd();
/*     */                 }
/*     */                 try
/*     */                 {
/* 101 */                   DataCachePreloader.this.loadTicks(instrument, from, to, this.val$preloadControl);
/*     */                 } catch (Throwable t) {
/* 103 */                   allInstrumentDataLoaded = false;
/* 104 */                   allDataLoaded = false;
/* 105 */                   this.val$preloadControl.exceptionThrown(instrument, new TimeInterval(from, to), t);
/*     */                 }
/*     */ 
/* 108 */                 if (this.val$preloadControl.isStopped()) {
/* 109 */                   allInstrumentDataLoaded = false;
/* 110 */                   allDataLoaded = false;
/*     */ 
/* 124 */                   this.val$preloadControl.loadingFinished(instrument, iti, allInstrumentDataLoaded);
/*     */ 
/* 128 */                   if (allDataLoaded) {
/* 129 */                     this.val$preloadControl.loadingProcessed(1.0D);
/*     */                   }
/*     */ 
/* 132 */                   this.val$preloadControl.loadingProcessFinished(allDataLoaded); return;
/*     */                 }
/* 114 */                 long loadedInterval = to - from;
/* 115 */                 totalIntervalAlreadyLoaded += loadedInterval;
/* 116 */                 double loadedPercent = totalIntervalAlreadyLoaded / totalInterval;
/* 117 */                 this.val$preloadControl.loadingProcessed(loadedPercent);
/*     */ 
/* 119 */                 from = to;
/*     */               }
/* 121 */               while (from < iti.getEnd());
/*     */             }
/*     */             finally {
/* 124 */               this.val$preloadControl.loadingFinished(instrument, iti, allInstrumentDataLoaded);
/*     */             }
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/*     */           double totalInterval;
/*     */           double totalIntervalAlreadyLoaded;
/* 128 */           if (allDataLoaded) {
/* 129 */             this.val$preloadControl.loadingProcessed(1.0D);
/*     */           }
/*     */ 
/* 132 */           this.val$preloadControl.loadingProcessFinished(allDataLoaded);
/*     */         }
/*     */       }
/*     */     };
/* 137 */     if (!preloadControl.isStopped())
/* 138 */       new Thread(task, "DataCachePreloaderThread " + hashCode()).start();
/*     */   }
/*     */ 
/*     */   private void loadTicks(Instrument instrument, long from, long to, DataCachePreloadControl preloadControl)
/*     */   {
/* 148 */     this.feedDataProvider.loadHistoryDataInCacheFromCFGSynched(instrument, from, to, new LoadingProgressListener(preloadControl)
/*     */     {
/*     */       public boolean stopJob()
/*     */       {
/* 155 */         return this.val$preloadControl.isStopped();
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/* 159 */         if (e != null)
/* 160 */           throw new RuntimeException(e);
/*     */       }
/*     */ 
/*     */       public void dataLoaded(long start, long end, long currentPosition, String information)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void validateParams(List<InstrumentTimeInterval> instrumentTimeIntervals, DataCachePreloadControl preloadControl)
/*     */   {
/* 174 */     if ((instrumentTimeIntervals == null) || (preloadControl == null)) {
/* 175 */       throw new NullPointerException();
/*     */     }
/* 177 */     for (InstrumentTimeInterval iti : instrumentTimeIntervals) {
/* 178 */       if ((iti == null) || (iti.getInstrument() == null)) {
/* 179 */         throw new NullPointerException();
/*     */       }
/* 181 */       if (iti.getStart() >= iti.getEnd())
/* 182 */         throw new IllegalArgumentException("Start time " + iti.getFormattedStart() + " >= End time " + iti.getFormattedEnd() + " for instrument " + iti.getInstrument());
/*     */     }
/*     */   }
/*     */ 
/*     */   private long calculateTotalInterval(List<InstrumentTimeInterval> instrumentTimeIntervals)
/*     */   {
/* 193 */     long totalTimeInterval = 0L;
/* 194 */     for (InstrumentTimeInterval iti : instrumentTimeIntervals) {
/* 195 */       totalTimeInterval += iti.getEnd() - iti.getStart();
/*     */     }
/* 197 */     return totalTimeInterval;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.preloader.DataCachePreloader
 * JD-Core Version:    0.6.0
 */