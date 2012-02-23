/*     */ package com.dukascopy.charts.math.dataprovider;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*     */ import com.dukascopy.charts.data.datacache.OrdersListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrdersDataProvider
/*     */   implements IOrdersDataProvider
/*     */ {
/*  27 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersDataProvider.class);
/*     */ 
/*  29 */   private static final OrderHistoricalData[] NO_ORDERS = new OrderHistoricalData[0];
/*     */ 
/*  31 */   private List<OrdersDataChangeListener> dataChangeListeners = Collections.synchronizedList(new ArrayList());
/*     */   private Instrument instrument;
/*  34 */   private long loadedFrom = 9223372036854775807L;
/*  35 */   private long loadedTo = -9223372036854775808L;
/*  36 */   private boolean autoshift = false;
/*  37 */   private OrderHistoricalData[] loadedData = NO_ORDERS;
/*     */   private long maxIntervalInMilliseconds;
/*  39 */   private List<OrderHistoricalData> ordersCachingVariable = new ArrayList();
/*     */   protected DataCacheRequestData dataCacheRequestData;
/*     */   private IFeedDataProvider feedDataProvider;
/*     */   private OrdersListener firstDataListener;
/*     */   private LiveFeedListener ticksListener;
/*     */ 
/*     */   public OrdersDataProvider(Instrument instrument, long maxIntervalInMilliseconds, IFeedDataProvider feedDataProvider)
/*     */   {
/*  49 */     this.instrument = instrument;
/*  50 */     this.maxIntervalInMilliseconds = maxIntervalInMilliseconds;
/*  51 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/*  56 */     this.firstDataListener = new Object() {
/*     */       public void newOrder(Instrument instrument, OrderHistoricalData orderData) {
/*  58 */         synchronized (OrdersDataProvider.this) {
/*  59 */           if ((OrdersDataProvider.this.dataCacheRequestData != null) && (OrdersDataProvider.this.dataCacheRequestData.autoshift)) {
/*  60 */             OrdersDataProvider.this.dataCacheRequestData.cancel = true;
/*  61 */             OrdersDataProvider.this.requestHistoryData(OrdersDataProvider.this.dataCacheRequestData.from, OrdersDataProvider.this.dataCacheRequestData.to, OrdersDataProvider.this.dataCacheRequestData.from, OrdersDataProvider.this.dataCacheRequestData.to, OrdersDataProvider.this.dataCacheRequestData.autoshift);
/*     */           }
/*  63 */           if ((OrdersDataProvider.this.autoshift) || (orderData.getHistoryStart() <= OrdersDataProvider.this.loadedTo) || (orderData.getHistoryEnd() <= OrdersDataProvider.this.loadedTo)) {
/*  64 */             OrderHistoricalData[] newLoadedData = new OrderHistoricalData[OrdersDataProvider.this.loadedData.length + 1];
/*  65 */             System.arraycopy(OrdersDataProvider.this.loadedData, 0, newLoadedData, 0, OrdersDataProvider.this.loadedData.length);
/*  66 */             newLoadedData[OrdersDataProvider.this.loadedData.length] = orderData;
/*  67 */             OrdersDataProvider.access$302(OrdersDataProvider.this, newLoadedData);
/*  68 */             if ((OrdersDataProvider.this.autoshift) && (OrdersDataProvider.this.loadedTo < orderData.getHistoryEnd())) {
/*  69 */               OrdersDataProvider.access$202(OrdersDataProvider.this, orderData.getHistoryEnd());
/*     */             }
/*  71 */             OrdersDataProvider.this.fireOrdersChanged(orderData.getHistoryStart(), orderData.getHistoryEnd());
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       public void orderChange(Instrument instrument, OrderHistoricalData orderData) {
/*  77 */         synchronized (OrdersDataProvider.this)
/*     */         {
/*  79 */           for (int i = OrdersDataProvider.this.loadedData.length - 1; i >= 0; i--) {
/*  80 */             OrderHistoricalData orderToCheck = OrdersDataProvider.this.loadedData[i];
/*  81 */             if (orderToCheck.getOrderGroupId().equals(orderData.getOrderGroupId())) {
/*  82 */               if ((orderData.isClosed()) && (!orderData.isOpened()))
/*     */               {
/*  84 */                 OrderHistoricalData[] newLoadedData = new OrderHistoricalData[OrdersDataProvider.this.loadedData.length - 1];
/*  85 */                 System.arraycopy(OrdersDataProvider.this.loadedData, 0, newLoadedData, 0, i);
/*  86 */                 System.arraycopy(OrdersDataProvider.this.loadedData, i + 1, newLoadedData, i, OrdersDataProvider.this.loadedData.length - i - 1);
/*  87 */                 OrdersDataProvider.access$302(OrdersDataProvider.this, newLoadedData);
/*     */               } else {
/*  89 */                 OrdersDataProvider.this.loadedData[i] = orderData;
/*     */               }
/*  91 */               if ((orderData.getHistoryStart() == 9223372036854775807L) || (orderToCheck.getHistoryStart() == 9223372036854775807L)) {
/*  92 */                 OrdersDataProvider.this.fireOrdersChanged(); break;
/*     */               }
/*  94 */               OrdersDataProvider.this.fireOrdersChanged(orderData.getHistoryStart(), orderData.getHistoryEnd());
/*     */ 
/*  96 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       public void orderMerge(Instrument instrument, OrderHistoricalData resultingOrder, List<OrderHistoricalData> mergedOrdersData)
/*     */       {
/* 104 */         synchronized (OrdersDataProvider.this) {
/* 105 */           if ((OrdersDataProvider.this.dataCacheRequestData != null) && (OrdersDataProvider.this.dataCacheRequestData.autoshift)) {
/* 106 */             OrdersDataProvider.this.dataCacheRequestData.cancel = true;
/* 107 */             OrdersDataProvider.this.requestHistoryData(OrdersDataProvider.this.dataCacheRequestData.from, OrdersDataProvider.this.dataCacheRequestData.to, OrdersDataProvider.this.dataCacheRequestData.from, OrdersDataProvider.this.dataCacheRequestData.to, OrdersDataProvider.this.dataCacheRequestData.autoshift);
/*     */           }
/* 109 */           if ((resultingOrder.getHistoryStart() <= OrdersDataProvider.this.loadedTo) || (OrdersDataProvider.this.autoshift)) {
/* 110 */             boolean found = false;
/* 111 */             for (int i = OrdersDataProvider.this.loadedData.length - 1; i >= 0; i--) {
/* 112 */               OrderHistoricalData orderToCheck = OrdersDataProvider.this.loadedData[i];
/* 113 */               if (orderToCheck.getOrderGroupId().equals(resultingOrder.getOrderGroupId())) {
/* 114 */                 OrdersDataProvider.this.loadedData[i] = resultingOrder;
/* 115 */                 found = true;
/* 116 */                 break;
/*     */               }
/*     */             }
/* 119 */             if (!found) {
/* 120 */               OrderHistoricalData[] newLoadedData = new OrderHistoricalData[OrdersDataProvider.this.loadedData.length + 1];
/* 121 */               System.arraycopy(OrdersDataProvider.this.loadedData, 0, newLoadedData, 0, OrdersDataProvider.this.loadedData.length);
/* 122 */               newLoadedData[OrdersDataProvider.this.loadedData.length] = resultingOrder;
/* 123 */               OrdersDataProvider.access$302(OrdersDataProvider.this, newLoadedData);
/* 124 */               if (OrdersDataProvider.this.loadedTo < resultingOrder.getHistoryEnd()) {
/* 125 */                 OrdersDataProvider.access$202(OrdersDataProvider.this, resultingOrder.getHistoryEnd());
/*     */               }
/*     */             }
/*     */           }
/* 129 */           for (OrderHistoricalData mergedOrder : mergedOrdersData) {
/* 130 */             boolean found = false;
/* 131 */             for (int i = OrdersDataProvider.this.loadedData.length - 1; i >= 0; i--) {
/* 132 */               OrderHistoricalData orderToCheck = OrdersDataProvider.this.loadedData[i];
/* 133 */               if (orderToCheck.getOrderGroupId().equals(mergedOrder.getOrderGroupId())) {
/* 134 */                 OrdersDataProvider.this.loadedData[i] = mergedOrder;
/* 135 */                 found = true;
/* 136 */                 break;
/*     */               }
/*     */             }
/* 139 */             if (!found) {
/* 140 */               OrderHistoricalData[] newLoadedData = new OrderHistoricalData[OrdersDataProvider.this.loadedData.length + 1];
/* 141 */               System.arraycopy(OrdersDataProvider.this.loadedData, 0, newLoadedData, 0, OrdersDataProvider.this.loadedData.length);
/* 142 */               newLoadedData[OrdersDataProvider.this.loadedData.length] = resultingOrder;
/* 143 */               OrdersDataProvider.access$302(OrdersDataProvider.this, newLoadedData);
/* 144 */               if (OrdersDataProvider.this.loadedTo < resultingOrder.getHistoryEnd()) {
/* 145 */                 OrdersDataProvider.access$202(OrdersDataProvider.this, resultingOrder.getHistoryEnd());
/*     */               }
/*     */             }
/*     */           }
/* 149 */           OrdersDataProvider.this.fireOrdersChanged();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void ordersInvalidated(Instrument instrument) {
/* 154 */         synchronized (OrdersDataProvider.this) {
/* 155 */           if ((OrdersDataProvider.this.loadedFrom != 9223372036854775807L) && (OrdersDataProvider.this.loadedTo != -9223372036854775808L))
/* 156 */             OrdersDataProvider.this.requestHistoryData(OrdersDataProvider.this.loadedTo - OrdersDataProvider.this.loadedFrom > OrdersDataProvider.this.maxIntervalInMilliseconds ? OrdersDataProvider.this.loadedTo - OrdersDataProvider.this.maxIntervalInMilliseconds : OrdersDataProvider.this.loadedFrom, OrdersDataProvider.this.loadedTo, OrdersDataProvider.this.loadedFrom, OrdersDataProvider.this.loadedTo, OrdersDataProvider.this.autoshift);
/*     */         }
/*     */       }
/*     */     };
/* 162 */     this.feedDataProvider.subscribeToOrdersNotifications(this.instrument, this.firstDataListener);
/*     */ 
/* 164 */     this.ticksListener = new Object()
/*     */     {
/*     */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*     */       }
/*     */ 
/*     */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/* 170 */         synchronized (OrdersDataProvider.this) {
/* 171 */           if ((OrdersDataProvider.this.autoshift) && (OrdersDataProvider.this.loadedTo < time))
/* 172 */             OrdersDataProvider.access$202(OrdersDataProvider.this, time);
/*     */         }
/*     */       }
/*     */     };
/* 177 */     this.feedDataProvider.subscribeToLiveFeed(this.instrument, this.ticksListener);
/*     */   }
/*     */ 
/*     */   public synchronized OrderHistoricalData[] getOrdersData(long from, long to, boolean autoshift)
/*     */   {
/* 186 */     boolean onlyOpenOrders = false;
/* 187 */     if (to - from > this.maxIntervalInMilliseconds) {
/* 188 */       onlyOpenOrders = true;
/*     */     }
/* 190 */     if (from > to) {
/* 191 */       throw new IllegalArgumentException(new StringBuilder().append("Requested interval has from time [").append(from).append("] bigger than to time [").append(to).append("]").toString());
/*     */     }
/* 193 */     long currentTime = this.feedDataProvider.getCurrentTime();
/* 194 */     if ((currentTime != -9223372036854775808L) && (to > currentTime + 10000L)) {
/* 195 */       to = currentTime;
/* 196 */       if (from > to) {
/* 197 */         return new OrderHistoricalData[0];
/*     */       }
/*     */     }
/* 200 */     if (onlyOpenOrders)
/* 201 */       return getRequestedOrders(from, to, onlyOpenOrders, autoshift);
/* 202 */     if ((from < this.loadedFrom) || (to < this.loadedFrom) || ((!this.autoshift) && ((from > this.loadedTo) || (to > this.loadedTo))))
/*     */     {
/* 204 */       long requestFrom = from - (to - from) * 3L;
/* 205 */       long requestTo = to + (to - from) * 3L;
/* 206 */       if (requestTo - requestFrom > this.maxIntervalInMilliseconds) {
/* 207 */         requestFrom = from - (this.maxIntervalInMilliseconds - (to - from) / 2L);
/* 208 */         requestTo = to + (this.maxIntervalInMilliseconds - (to - from) / 2L);
/*     */       }
/*     */ 
/* 211 */       requestHistoryData(requestFrom, requestTo, from, to, autoshift);
/* 212 */       return getRequestedOrders(from, to, onlyOpenOrders, autoshift);
/*     */     }
/* 214 */     if (this.dataCacheRequestData == null) {
/* 215 */       this.autoshift = autoshift;
/*     */     }
/* 217 */     return getRequestedOrders(from, to, onlyOpenOrders, autoshift);
/*     */   }
/*     */ 
/*     */   public void addOrdersDataChangeListener(OrdersDataChangeListener dataChangeListener)
/*     */   {
/* 222 */     this.dataChangeListeners.add(dataChangeListener);
/*     */   }
/*     */ 
/*     */   public void removeOrdersDataChangeListener(OrdersDataChangeListener dataChangeListener) {
/* 226 */     this.dataChangeListeners.remove(dataChangeListener);
/*     */   }
/*     */ 
/*     */   private OrderHistoricalData[] getRequestedOrders(long from, long to, boolean onlyOpenOrders, boolean autoshift) {
/* 230 */     long currentTime = this.feedDataProvider.getCurrentTime(this.instrument);
/* 231 */     this.ordersCachingVariable.clear();
/* 232 */     for (OrderHistoricalData orderData : this.loadedData) {
/* 233 */       if (((orderData.getHistoryEnd() < from) || (orderData.getHistoryStart() > to)) && (orderData.getHistoryStart() != 9223372036854775807L) && ((orderData.isClosed()) || (!orderData.isOpened()) || ((orderData.getEntryOrder().getStopLossPrice().compareTo(BigDecimal.ZERO) < 0) && (orderData.getEntryOrder().getTakeProfitPrice().compareTo(BigDecimal.ZERO) < 0))) && (((!autoshift) && (to != currentTime)) || (orderData.getHistoryStart() <= to))) {
/*     */         continue;
/*     */       }
/* 236 */       if ((!onlyOpenOrders) || (orderData.getHistoryStart() == 9223372036854775807L) || ((orderData.isOpened()) && (!orderData.isClosed()))) {
/* 237 */         this.ordersCachingVariable.add(orderData);
/*     */       }
/*     */     }
/*     */ 
/* 241 */     if (LOGGER.isTraceEnabled()) {
/* 242 */       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
/* 243 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 244 */       LOGGER.trace(new StringBuilder().append("Returning [").append(this.ordersCachingVariable.size()).append("] orders as the result to the request from [").append(from).append("] to [").append(to).append("]").toString());
/*     */     }
/* 246 */     return (OrderHistoricalData[])this.ordersCachingVariable.toArray(new OrderHistoricalData[this.ordersCachingVariable.size()]);
/*     */   }
/*     */ 
/*     */   private void requestHistoryData(long from, long to, long requestedFrom, long requestedTo, boolean autoshift)
/*     */   {
/* 256 */     if ((this.dataCacheRequestData != null) && (!this.dataCacheRequestData.cancel))
/*     */     {
/* 258 */       if ((requestedFrom >= this.dataCacheRequestData.from) && ((this.dataCacheRequestData.autoshift) || (requestedTo <= this.dataCacheRequestData.to))) {
/* 259 */         return;
/*     */       }
/*     */ 
/* 262 */       if (LOGGER.isDebugEnabled()) {
/* 263 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 264 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 265 */         LOGGER.debug(new StringBuilder().append("Canceling orders request for instrument [").append(this.instrument).append("] from [").append(dateFormat.format(Long.valueOf(this.dataCacheRequestData.from))).append("], to [").append(dateFormat.format(Long.valueOf(this.dataCacheRequestData.to))).append("]").toString());
/*     */       }
/*     */ 
/* 268 */       this.dataCacheRequestData.cancel = true;
/* 269 */       this.dataCacheRequestData = null;
/*     */     }
/* 271 */     fireLoadingStarted();
/* 272 */     this.dataCacheRequestData = new DataCacheRequestData();
/* 273 */     this.dataCacheRequestData.from = from;
/* 274 */     this.dataCacheRequestData.to = to;
/* 275 */     this.dataCacheRequestData.autoshift = autoshift;
/* 276 */     this.dataCacheRequestData.cancel = false;
/* 277 */     if (this.feedDataProvider != null)
/*     */       try {
/* 279 */         LoadDataProgressListener loadDataProgressListener = new LoadDataProgressListener(this.dataCacheRequestData);
/* 280 */         if (LOGGER.isDebugEnabled()) {
/* 281 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 282 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 283 */           LOGGER.debug(new StringBuilder().append("Requesting orders for instrument [").append(this.instrument).append("], from [").append(dateFormat.format(Long.valueOf(this.dataCacheRequestData.from))).append("], to [").append(dateFormat.format(Long.valueOf(this.dataCacheRequestData.to))).append("]").toString());
/*     */         }
/*     */ 
/* 286 */         this.feedDataProvider.loadOrdersHistoricalData(this.instrument, from, to, new LoadDataListener(this.dataCacheRequestData), loadDataProgressListener);
/* 287 */         return;
/*     */       } catch (DataCacheException e) {
/* 289 */         LOGGER.error(e.getMessage(), e);
/* 290 */         return;
/*     */       }
/*     */   }
/*     */ 
/*     */   protected synchronized void dataLoaded(boolean allDataLoaded, DataCacheRequestData requestData, Exception e)
/*     */   {
/* 296 */     if (LOGGER.isDebugEnabled()) {
/* 297 */       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 298 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 299 */       LOGGER.debug(new StringBuilder().append("dataLoaded, instrument [").append(this.instrument).append("] from [").append(dateFormat.format(Long.valueOf(requestData.from))).append("], to [").append(dateFormat.format(Long.valueOf(requestData.to))).append("], canceled [").append(requestData.cancel).append("], size [").append(requestData.dataLoaded == null ? "null" : Integer.valueOf(requestData.dataLoaded.size())).append("]").toString());
/*     */     }
/*     */ 
/* 303 */     if (this.dataCacheRequestData == requestData) {
/* 304 */       this.dataCacheRequestData = null;
/* 305 */       fireLoadingFinished();
/*     */     }
/* 307 */     if ((!requestData.cancel) && (allDataLoaded)) {
/* 308 */       this.loadedFrom = requestData.from;
/* 309 */       this.loadedTo = requestData.to;
/* 310 */       this.autoshift = true;
/* 311 */       this.loadedData = ((OrderHistoricalData[])requestData.dataLoaded.toArray(new OrderHistoricalData[requestData.dataLoaded.size()]));
/* 312 */       fireOrdersChanged();
/* 313 */     } else if ((!allDataLoaded) && (e != null)) {
/* 314 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOrdersChanged() {
/* 319 */     OrdersDataChangeListener[] listeners = (OrdersDataChangeListener[])this.dataChangeListeners.toArray(new OrdersDataChangeListener[this.dataChangeListeners.size()]);
/* 320 */     for (OrdersDataChangeListener listener : listeners)
/* 321 */       listener.dataChanged(this.instrument, this.loadedFrom, this.loadedTo);
/*     */   }
/*     */ 
/*     */   private void fireOrdersChanged(long from, long to)
/*     */   {
/* 326 */     OrdersDataChangeListener[] listeners = (OrdersDataChangeListener[])this.dataChangeListeners.toArray(new OrdersDataChangeListener[this.dataChangeListeners.size()]);
/* 327 */     for (OrdersDataChangeListener listener : listeners)
/* 328 */       listener.dataChanged(this.instrument, from, to);
/*     */   }
/*     */ 
/*     */   protected void fireLoadingStarted()
/*     */   {
/* 333 */     if (LOGGER.isTraceEnabled()) {
/* 334 */       LOGGER.trace(new StringBuilder().append("fireLoadingStarted, instrument [").append(this.instrument).append("]").toString());
/*     */     }
/* 336 */     OrdersDataChangeListener[] listeners = (OrdersDataChangeListener[])this.dataChangeListeners.toArray(new OrdersDataChangeListener[this.dataChangeListeners.size()]);
/* 337 */     for (OrdersDataChangeListener listener : listeners)
/* 338 */       listener.loadingStarted(this.instrument);
/*     */   }
/*     */ 
/*     */   protected void fireLoadingFinished()
/*     */   {
/* 343 */     if (LOGGER.isTraceEnabled()) {
/* 344 */       LOGGER.trace(new StringBuilder().append("fireLoadingFinished, instrument [").append(this.instrument).append("]").toString());
/*     */     }
/* 346 */     OrdersDataChangeListener[] listeners = (OrdersDataChangeListener[])this.dataChangeListeners.toArray(new OrdersDataChangeListener[this.dataChangeListeners.size()]);
/* 347 */     for (OrdersDataChangeListener listener : listeners)
/* 348 */       listener.loadingFinished(this.instrument);
/*     */   }
/*     */ 
/*     */   public synchronized void dispose()
/*     */   {
/* 354 */     stop();
/* 355 */     this.dataChangeListeners.clear();
/*     */   }
/*     */ 
/*     */   private synchronized void stop() {
/* 359 */     if (this.firstDataListener != null) {
/* 360 */       this.feedDataProvider.unsubscribeFromOrdersNotifications(this.instrument, this.firstDataListener);
/*     */     }
/* 362 */     if (this.ticksListener != null)
/* 363 */       this.feedDataProvider.unsubscribeFromLiveFeed(this.instrument, this.ticksListener);
/*     */   }
/*     */ 
/*     */   public void changeInstrument(Instrument instrument)
/*     */   {
/* 369 */     stop();
/* 370 */     reset();
/*     */ 
/* 372 */     this.instrument = instrument;
/*     */ 
/* 374 */     start();
/* 375 */     fireOrdersChanged();
/*     */   }
/*     */ 
/*     */   private void reset() {
/* 379 */     this.loadedFrom = 9223372036854775807L;
/* 380 */     this.loadedTo = -9223372036854775808L;
/* 381 */     this.loadedData = NO_ORDERS;
/* 382 */     this.ordersCachingVariable = new ArrayList();
/* 383 */     this.autoshift = false;
/*     */   }
/*     */ 
/*     */   protected class LoadDataProgressListener
/*     */     implements LoadingProgressListener
/*     */   {
/*     */     private OrdersDataProvider.DataCacheRequestData dataCacheRequestData;
/*     */     private boolean done;
/*     */ 
/*     */     public LoadDataProgressListener(OrdersDataProvider.DataCacheRequestData dataCacheRequestData)
/*     */     {
/* 445 */       this.dataCacheRequestData = dataCacheRequestData;
/*     */     }
/*     */ 
/*     */     public boolean done() {
/* 449 */       return this.done;
/*     */     }
/*     */ 
/*     */     public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*     */     }
/*     */ 
/*     */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/* 456 */       this.done = true;
/* 457 */       OrdersDataProvider.this.dataLoaded(allDataLoaded, this.dataCacheRequestData, e);
/*     */     }
/*     */ 
/*     */     public boolean stopJob() {
/* 461 */       return this.dataCacheRequestData.cancel;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class LoadDataListener
/*     */     implements OrdersListener
/*     */   {
/*     */     private OrdersDataProvider.DataCacheRequestData dataCacheRequestData;
/* 406 */     long prevTime = -9223372036854775808L;
/*     */ 
/*     */     public LoadDataListener(OrdersDataProvider.DataCacheRequestData dataCacheRequestData) {
/* 409 */       this.dataCacheRequestData = dataCacheRequestData;
/*     */     }
/*     */ 
/*     */     public void newOrder(Instrument instrument, OrderHistoricalData orderData) {
/* 413 */       if (!this.dataCacheRequestData.cancel) {
/* 414 */         if (this.prevTime > orderData.getHistoryStart()) {
/* 415 */           throw new RuntimeException("Received orders from data cache are not in ascending order");
/*     */         }
/* 417 */         this.dataCacheRequestData.dataLoaded.add(orderData);
/*     */ 
/* 419 */         this.prevTime = orderData.getHistoryStart();
/*     */       }
/*     */       else {
/* 422 */         this.dataCacheRequestData.dataLoaded = null;
/* 423 */         this.dataCacheRequestData.progressListener = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void orderChange(Instrument instrument, OrderHistoricalData orderData)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void orderMerge(Instrument instrument, OrderHistoricalData resultingOrderData, List<OrderHistoricalData> mergedOrdersData)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void ordersInvalidated(Instrument instrument)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class DataCacheRequestData
/*     */   {
/*     */     public long from;
/*     */     public long to;
/*     */     public boolean autoshift;
/*     */     public LoadingProgressListener progressListener;
/*     */     public boolean cancel;
/* 392 */     public List<OrderHistoricalData> dataLoaded = new ArrayList();
/*     */ 
/*     */     public String toString() {
/* 395 */       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/* 396 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 397 */       StringBuilder stamp = new StringBuilder();
/* 398 */       stamp.append(format.format(Long.valueOf(this.from))).append(" - ");
/* 399 */       stamp.append(format.format(Long.valueOf(this.to))).append(" loadedSize - ").append(this.dataLoaded.size());
/* 400 */       return stamp.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.OrdersDataProvider
 * JD-Core Version:    0.6.0
 */