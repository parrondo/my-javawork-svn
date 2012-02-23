/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.impl.TimedData;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractDataLoadingThread<TD extends TimedData> extends Thread
/*     */   implements IDataLoadingThread<TD>
/*     */ {
/*  21 */   protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataLoadingThread.class);
/*     */   private final Instrument instrument;
/*     */   private final JForexPeriod jForexPeriod;
/*     */   private final OfferSide offerSide;
/*     */   private final BlockingQueue<TD> queue;
/*     */   private final long from;
/*     */   private final long to;
/*     */   private final IFeedDataProvider feedDataProvider;
/*  31 */   private boolean stop = false;
/*     */ 
/*     */   public AbstractDataLoadingThread(String name, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BlockingQueue<TD> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*     */   {
/*  43 */     super(name);
/*  44 */     this.instrument = instrument;
/*  45 */     this.offerSide = offerSide;
/*  46 */     this.queue = queue;
/*  47 */     this.from = from;
/*  48 */     this.to = to;
/*  49 */     this.feedDataProvider = feedDataProvider;
/*  50 */     this.jForexPeriod = jForexPeriod;
/*     */   }
/*     */ 
/*     */   public abstract void run();
/*     */ 
/*     */   protected abstract TD createEmptyBar();
/*     */ 
/*     */   protected LoadingProgressListener createLoadingProgressListener() {
/*  60 */     return new LoadingProgressListener()
/*     */     {
/*     */       public void dataLoaded(long start, long end, long currentPosition, String information) {
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/*  66 */         if (allDataLoaded)
/*  67 */           AbstractDataLoadingThread.this.putDataToQueue(AbstractDataLoadingThread.this.createEmptyBar());
/*  68 */         else if (e != null)
/*  69 */           AbstractDataLoadingThread.LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */ 
/*     */       public boolean stopJob()
/*     */       {
/*  74 */         return AbstractDataLoadingThread.this.isStop();
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   protected void putDataToQueue(TD[] datas) {
/*  80 */     if (datas != null)
/*  81 */       for (TimedData data : datas) {
/*  82 */         if (isStop()) {
/*     */           break;
/*     */         }
/*  85 */         putDataToQueue(data);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void putDataToQueue(TD data)
/*     */   {
/*     */     try {
/*  92 */       getQueue().put(data);
/*     */     } catch (InterruptedException e) {
/*  94 */       LOGGER.error(e.getLocalizedMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stopThread()
/*     */   {
/* 100 */     this.stop = true;
/* 101 */     while (this.queue.poll() != null);
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 109 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public JForexPeriod getJForexPeriod()
/*     */   {
/* 114 */     return this.jForexPeriod;
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide()
/*     */   {
/* 119 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   public BlockingQueue<TD> getQueue()
/*     */   {
/* 124 */     return this.queue;
/*     */   }
/*     */ 
/*     */   public IFeedDataProvider getFeedDataProvider() {
/* 128 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   public long getFrom() {
/* 132 */     return this.from;
/*     */   }
/*     */ 
/*     */   public long getTo() {
/* 136 */     return this.to;
/*     */   }
/*     */ 
/*     */   public boolean isStop() {
/* 140 */     return this.stop;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.AbstractDataLoadingThread
 * JD-Core Version:    0.6.0
 */