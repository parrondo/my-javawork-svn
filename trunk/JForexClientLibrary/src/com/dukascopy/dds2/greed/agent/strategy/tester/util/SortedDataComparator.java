/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.util;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.TimedData;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SortedDataComparator
/*    */   implements Comparator<SortedDataItem>
/*    */ {
/*    */   public int compare(SortedDataItem o1, SortedDataItem o2)
/*    */   {
/* 26 */     Period firstThreadPeriod = o2.getJForexPeriod().getPeriod();
/* 27 */     Period secondThreadPeriod = o1.getJForexPeriod().getPeriod();
/*    */ 
/* 31 */     long firstVal = o2.getAskOrBidBarTime();
/*    */ 
/* 33 */     TimedData firstData = o2.getAskBar() == null ? o2.getBidBar() : o2.getAskBar();
/* 34 */     if ((firstData instanceof AbstractPriceAggregationData)) {
/* 35 */       firstVal = ((AbstractPriceAggregationData)firstData).getEndTime();
/*    */     }
/*    */ 
/* 38 */     if (firstThreadPeriod != Period.TICK) {
/* 39 */       firstVal = DataCacheUtils.getNextCandleStartFast(firstThreadPeriod, firstVal);
/*    */     }
/*    */ 
/* 42 */     long secondVal = o1.getAskOrBidBarTime();
/*    */ 
/* 44 */     TimedData secondData = o1.getAskBar() == null ? o1.getBidBar() : o1.getAskBar();
/* 45 */     if ((secondData instanceof AbstractPriceAggregationData)) {
/* 46 */       secondVal = ((AbstractPriceAggregationData)secondData).getEndTime();
/*    */     }
/*    */ 
/* 49 */     if (secondThreadPeriod != Period.TICK) {
/* 50 */       secondVal = DataCacheUtils.getNextCandleStartFast(secondThreadPeriod, secondVal);
/*    */     }
/*    */ 
/* 53 */     return firstVal == secondVal ? 0 : firstVal < secondVal ? -1 : 1;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 60 */     SortedDataItem array1 = new SortedDataItem(null, new JForexPeriod(Period.TEN_SECS), new CandleData(1L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), new CandleData(1L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
/* 61 */     SortedDataItem array2 = new SortedDataItem(null, new JForexPeriod(Period.TEN_MINS), new CandleData(10L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), new CandleData(10L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
/* 62 */     SortedDataItem array3 = new SortedDataItem(null, new JForexPeriod(Period.TEN_SECS), new CandleData(2L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), new CandleData(2L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
/* 63 */     SortedDataItem array4 = new SortedDataItem(null, new JForexPeriod(Period.ONE_HOUR), new CandleData(60L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), new CandleData(60L, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
/*    */ 
/* 65 */     List list = Arrays.asList(new SortedDataItem[] { array1, array2, array3, array4 });
/* 66 */     Collections.sort(list, new SortedDataComparator());
/* 67 */     for (SortedDataItem array : list)
/* 68 */       System.out.println(array.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.util.SortedDataComparator
 * JD-Core Version:    0.6.0
 */