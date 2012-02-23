/*    */ package com.dukascopy.charts.data.datacache.priceaggregation.dataprovider;
/*    */ 
/*    */ import com.dukascopy.api.feed.IPriceAggregationBar;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.InProgressDataLoadingChecker;
/*    */ 
/*    */ public class PriceAggregationUtils
/*    */ {
/*    */   public static <T extends IPriceAggregationBar> boolean checkAndWaitInProgressBarLoaded(InProgressDataLoadingChecker<T> checker)
/*    */   {
/* 16 */     IPriceAggregationBar inProgressData = checker.getInProgressData();
/* 17 */     if (inProgressData == null) {
/* 18 */       boolean isInProgress = checker.isLoadingInProgress();
/* 19 */       if (!isInProgress) {
/* 20 */         inProgressData = checker.getInProgressData();
/* 21 */         if (inProgressData == null)
/*    */         {
/* 25 */           return false;
/*    */         }
/*    */       }
/*    */       else {
/* 29 */         int TRY_COUNT = 300;
/* 30 */         while ((checker.isLoadingInProgress()) && (TRY_COUNT > 0))
/*    */           try {
/* 32 */             Thread.sleep(100L);
/* 33 */             TRY_COUNT--;
/*    */           }
/*    */           catch (InterruptedException e)
/*    */           {
/*    */           }
/* 38 */         if ((checker.isLoadingInProgress()) && (TRY_COUNT <= 0)) {
/* 39 */           return false;
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 44 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.PriceAggregationUtils
 * JD-Core Version:    0.6.0
 */