/*    */ package com.dukascopy.charts.data.datacache.customperiod.tick;
/*    */ 
/*    */ import com.dukascopy.api.Filter;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
/*    */ 
/*    */ public class FlowCustomPeriodFromTicksCreator extends CustomPeriodFromTicksCreator
/*    */ {
/*    */   public FlowCustomPeriodFromTicksCreator(Instrument instrument, OfferSide offerSide, Period desiredPeriod, Filter filter, boolean inverseOrder, Long desiredFirstDataTime, Double firstDataValue, IFilterManager filterManager)
/*    */   {
/* 29 */     super(instrument, offerSide, 1, desiredPeriod, filter, inverseOrder, desiredFirstDataTime, firstDataValue, filterManager);
/*    */   }
/*    */ 
/*    */   protected void addCompletedCandle(CandleData candleData)
/*    */   {
/* 44 */     fireNewCandle(candleData);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.tick.FlowCustomPeriodFromTicksCreator
 * JD-Core Version:    0.6.0
 */