/*    */ package com.dukascopy.charts.mappers;
/*    */ 
/*    */ import com.dukascopy.api.DataType;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CompositeTimeToXMapper
/*    */   implements ITimeToXMapper
/*    */ {
/*    */   private final ChartState chartState;
/* 15 */   private final Map<DataType, ITimeToXMapper> mappersMap = new HashMap();
/*    */ 
/*    */   public CompositeTimeToXMapper(ChartState chartState, ITimeToXMapper tickTimeToXMapper, ITimeToXMapper candleTimeToXMapper, ITimeToXMapper priceRangeTimeToXMapper, ITimeToXMapper pointAndFigureTimeToXMapper, ITimeToXMapper tickBarTimeToXMapper, ITimeToXMapper renkoTimeToXMapper)
/*    */   {
/* 26 */     this.chartState = chartState;
/*    */ 
/* 28 */     this.mappersMap.put(DataType.TICKS, tickTimeToXMapper);
/* 29 */     this.mappersMap.put(DataType.TIME_PERIOD_AGGREGATION, candleTimeToXMapper);
/* 30 */     this.mappersMap.put(DataType.PRICE_RANGE_AGGREGATION, priceRangeTimeToXMapper);
/* 31 */     this.mappersMap.put(DataType.POINT_AND_FIGURE, pointAndFigureTimeToXMapper);
/* 32 */     this.mappersMap.put(DataType.TICK_BAR, tickBarTimeToXMapper);
/* 33 */     this.mappersMap.put(DataType.RENKO, renkoTimeToXMapper);
/*    */   }
/*    */ 
/*    */   public long tx(int x)
/*    */   {
/* 38 */     return getMapper().tx(x);
/*    */   }
/*    */ 
/*    */   public int xt(long time)
/*    */   {
/* 43 */     return getMapper().xt(time);
/*    */   }
/*    */ 
/*    */   public int getWidth()
/*    */   {
/* 48 */     return getMapper().getWidth();
/*    */   }
/*    */ 
/*    */   public long getInterval()
/*    */   {
/* 53 */     return getMapper().getInterval();
/*    */   }
/*    */ 
/*    */   public int getBarWidth()
/*    */   {
/* 58 */     return getMapper().getBarWidth();
/*    */   }
/*    */ 
/*    */   public boolean isXOutOfRange(int x)
/*    */   {
/* 63 */     return getMapper().isXOutOfRange(x);
/*    */   }
/*    */ 
/*    */   private ITimeToXMapper getMapper() {
/* 67 */     DataType dataType = this.chartState.getDataType();
/* 68 */     ITimeToXMapper mapper = (ITimeToXMapper)this.mappersMap.get(dataType);
/* 69 */     if (mapper == null) {
/* 70 */       throw new IllegalArgumentException("Unsupported Data Type : " + dataType);
/*    */     }
/* 72 */     return mapper;
/*    */   }
/*    */ 
/*    */   public Period getPeriod()
/*    */   {
/* 77 */     return getMapper().getPeriod();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.CompositeTimeToXMapper
 * JD-Core Version:    0.6.0
 */