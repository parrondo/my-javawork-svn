/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.data.orders.IOrdersProvider;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoadOrdersAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*  23 */   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*     */   private static final Logger LOGGER;
/*     */   private final String accountId;
/*     */   private final Instrument instrument;
/*     */   private final long from;
/*     */   private final long to;
/*     */   private final OrdersListener listener;
/*     */   private final StackTraceElement[] stackTrace;
/*     */   private final FeedDataProvider feedDataProvider;
/*     */   private boolean localOrdersOnly;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */ 
/*     */   public LoadOrdersAction(FeedDataProvider feedDataProvider, String accountId, Instrument instrument, long from, long to, OrdersListener ordersListener, LoadingProgressListener loadingProgress, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, StackTraceElement[] stackTrace)
/*     */     throws DataCacheException
/*     */   {
/*  43 */     super(loadingProgress);
/*  44 */     this.accountId = accountId;
/*  45 */     this.instrument = instrument;
/*  46 */     this.from = from;
/*  47 */     this.to = to;
/*  48 */     this.listener = ordersListener;
/*  49 */     this.stackTrace = stackTrace;
/*  50 */     this.feedDataProvider = feedDataProvider;
/*  51 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*     */ 
/*  53 */     if ((instrument == null) || (from > to) || (loadingProgress == null) || (ordersListener == null))
/*  54 */       throw new DataCacheException("Wrong parameters: instrument=" + instrument + " / " + from + ", " + to + " / " + loadingProgress + " / " + ordersListener);
/*     */   }
/*     */ 
/*     */   public boolean isLocalOrdersOnly()
/*     */   {
/*  59 */     return this.localOrdersOnly;
/*     */   }
/*     */ 
/*     */   public void setLocalOrdersOnly(boolean localOrdersOnly) {
/*  63 */     this.localOrdersOnly = localOrdersOnly;
/*     */   }
/*     */ 
/*     */   public void run() {
/*  67 */     if (this.loadingProgress.stopJob()) {
/*  68 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/*  69 */       return;
/*     */     }
/*  71 */     this.loadingProgress.dataLoaded(this.from, this.to, this.from, "Downloading data...");
/*     */     try
/*     */     {
/*  74 */       CurvesDataLoader curvesDataLoader = this.feedDataProvider.getCurvesDataLoader();
/*     */ 
/*  77 */       if (LOGGER.isDebugEnabled()) {
/*  78 */         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZ");
/*  79 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  80 */         LOGGER.debug("Loading orders, from [" + dateFormat.format(Long.valueOf(this.from)) + "], to [" + dateFormat.format(Long.valueOf(this.to)) + "]");
/*     */       }
/*  82 */       long currentTime = this.feedDataProvider.getCurrentTime();
/*  83 */       long correctedFrom = this.from;
/*  84 */       long correctedTo = this.to;
/*  85 */       if (correctedFrom < 946684800000L)
/*     */       {
/*  87 */         correctedFrom = 946684800000L;
/*     */       }
/*  89 */       if (currentTime == -9223372036854775808L)
/*     */       {
/*  91 */         throw new DataCacheException("Orders history can't be accessed before first tick arrives into the system");
/*     */       }
/*  93 */       if (correctedTo > currentTime + 500L)
/*  94 */         correctedTo = currentTime + 500L;
/*     */       Collection orderDataList;
/*  96 */       if (!this.localOrdersOnly) {
/*  97 */         if (this.accountId == null) {
/*  98 */           throw new DataCacheException("Not connected, accountId unknown");
/*     */         }
/* 100 */         curvesDataLoader.loadOrders(this.accountId, this.instrument, correctedFrom, correctedTo, this.intraperiodExistsPolicy, this.loadingProgress);
/* 101 */         if (this.loadingProgress.stopJob()) {
/* 102 */           this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 103 */           return;
/*     */         }
/* 105 */         Collection orderDataList = this.feedDataProvider.getLocalCacheManager().readOrdersData(this.accountId, this.instrument, correctedFrom, correctedTo);
/* 106 */         orderDataList.addAll(this.feedDataProvider.getOrdersProvider().getOpenOrdersForInstrument(this.instrument, correctedFrom, correctedTo));
/*     */       } else {
/* 108 */         orderDataList = this.feedDataProvider.getOrdersProvider().getOpenOrdersForInstrument(this.instrument, correctedFrom, correctedTo);
/*     */       }
/*     */ 
/* 111 */       if (this.loadingProgress.stopJob()) {
/* 112 */         this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 113 */         return;
/*     */       }
/* 115 */       if (this.listener != null) {
/* 116 */         List sortedOrderData = new ArrayList(orderDataList);
/* 117 */         Collections.sort(sortedOrderData, new Comparator()
/*     */         {
/*     */           public int compare(OrderHistoricalData o1, OrderHistoricalData o2) {
/* 120 */             if (o1.getHistoryStart() > o2.getHistoryStart())
/* 121 */               return 1;
/* 122 */             if (o1.getHistoryStart() < o2.getHistoryStart()) {
/* 123 */               return -1;
/*     */             }
/* 125 */             return 0;
/*     */           }
/*     */         });
/* 128 */         for (OrderHistoricalData orderData : sortedOrderData) {
/* 129 */           this.listener.newOrder(this.instrument, orderData);
/* 130 */           if (this.loadingProgress.stopJob()) {
/* 131 */             this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 132 */             return;
/*     */           }
/*     */         }
/*     */       }
/* 136 */       this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 137 */       this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/*     */     } catch (Exception e) {
/* 139 */       LOGGER.error(e.getMessage(), e);
/* 140 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, e);
/*     */     } catch (Throwable t) {
/* 142 */       LOGGER.error(t.getMessage(), t);
/* 143 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  26 */     formatter.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */ 
/*  29 */     LOGGER = LoggerFactory.getLogger(LoadOrdersAction.class);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadOrdersAction
 * JD-Core Version:    0.6.0
 */