/*    */ package com.dukascopy.charts.listeners.datachange;
/*    */ 
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ProgressController
/*    */ {
/* 17 */   private static final Logger LOGGER = LoggerFactory.getLogger(ProgressController.class.getName());
/*    */ 
/* 19 */   final Map<String, LoadingStatus> loadingStatuses = Collections.synchronizedMap(new HashMap());
/* 20 */   final Set<ProgressListener> listeners = Collections.synchronizedSet(new HashSet());
/*    */   final ChartState chartState;
/*    */ 
/*    */   public ProgressController(ChartState chartState)
/*    */   {
/* 25 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   public void registerListener(ProgressListener listener) {
/* 29 */     this.listeners.add(listener);
/*    */   }
/*    */ 
/*    */   public void loadingStarted(Period period, OfferSide offerSide) {
/* 33 */     if (LOGGER.isTraceEnabled()) {
/* 34 */       LOGGER.trace(new StringBuilder().append("loading data started @ ").append(period).append(":").append(offerSide).toString());
/*    */     }
/* 36 */     setProgressState(period, offerSide, true);
/* 37 */     notifyListeners(true);
/*    */   }
/*    */ 
/*    */   public void loadingFinished(Period period, OfferSide offerSide) {
/* 41 */     if (LOGGER.isTraceEnabled()) {
/* 42 */       LOGGER.trace(new StringBuilder().append("loading data finished @ ").append(period).append(":").append(offerSide).toString());
/*    */     }
/* 44 */     setProgressState(period, offerSide, false);
/* 45 */     notifyListeners(false);
/*    */   }
/*    */ 
/*    */   public void loadingOrdersStarted()
/*    */   {
/* 51 */     LOGGER.trace("loading orders started");
/* 52 */     setProgress(true, true);
/*    */   }
/*    */ 
/*    */   public void loadingOrdersFinished() {
/* 56 */     LOGGER.trace("loading orders finished");
/* 57 */     if (!getLoadingStatus(this.chartState.getPeriod(), this.chartState.getOfferSide()).getProgress())
/* 58 */       setProgress(false, true);
/*    */   }
/*    */ 
/*    */   void setProgress(boolean isProgressing, boolean isLoadingOrders)
/*    */   {
/* 63 */     for (ProgressListener progressListener : this.listeners)
/* 64 */       progressListener.setProgress(isProgressing, isLoadingOrders);
/*    */   }
/*    */ 
/*    */   void notifyListeners(boolean progressState)
/*    */   {
/* 69 */     setProgress(progressState, false);
/*    */   }
/*    */ 
/*    */   void setProgressState(Period period, OfferSide offerSide, boolean isLoading) {
/* 73 */     if (isLoading)
/* 74 */       getLoadingStatus(period, offerSide).loadingStarted();
/*    */     else
/* 76 */       getLoadingStatus(period, offerSide).loadingFinished();
/*    */   }
/*    */ 
/*    */   LoadingStatus getLoadingStatus(Period period, OfferSide offerSide)
/*    */   {
/* 81 */     StringBuilder key = new StringBuilder(Long.toString(period.getInterval()));
/*    */ 
/* 83 */     if (offerSide != null) {
/* 84 */       key.append(":").append(offerSide.name());
/*    */     }
/*    */ 
/* 87 */     LoadingStatus loadingStatus = (LoadingStatus)this.loadingStatuses.get(key.toString());
/*    */ 
/* 89 */     if (loadingStatus == null) {
/* 90 */       loadingStatus = new LoadingStatus();
/* 91 */       this.loadingStatuses.put(key.toString(), loadingStatus);
/*    */     }
/*    */ 
/* 94 */     return loadingStatus;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.datachange.ProgressController
 * JD-Core Version:    0.6.0
 */