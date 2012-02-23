/*    */ package com.dukascopy.charts.mappers.value;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class SubValueToYMapper
/*    */ {
/*    */   private final double DEFAULT_PADDING;
/*    */   private final ChartState chartState;
/* 14 */   final Map<Integer, IValueToYMapper> valueToYMappers = new HashMap();
/*    */ 
/*    */   public SubValueToYMapper(ChartState chartState, double default_padding) {
/* 17 */     this.chartState = chartState;
/* 18 */     this.DEFAULT_PADDING = default_padding;
/*    */   }
/*    */ 
/*    */   public void computeGeometry(Integer indicatorId, int height)
/*    */   {
/* 23 */     ((IValueToYMapper)this.valueToYMappers.get(indicatorId)).computeGeometry(height);
/*    */   }
/*    */ 
/*    */   public void subComponentAdded(Integer indicatorId, int previousSubHeight) {
/* 27 */     this.valueToYMappers.put(indicatorId, new ValueToYMapper(this.chartState, new ValueFrame(1.0D, 2.0D), this.DEFAULT_PADDING, previousSubHeight));
/*    */   }
/*    */ 
/*    */   public int subComponentDeleted(Integer indicatorId) {
/* 31 */     IValueToYMapper mapper = (IValueToYMapper)this.valueToYMappers.remove(indicatorId);
/* 32 */     if (mapper != null) {
/* 33 */       return mapper.getHeight();
/*    */     }
/* 35 */     return 100;
/*    */   }
/*    */ 
/*    */   public IValueToYMapper get(Integer indicatorId)
/*    */   {
/* 40 */     return (IValueToYMapper)this.valueToYMappers.get(indicatorId);
/*    */   }
/*    */ 
/*    */   public Set<Integer> keySet() {
/* 44 */     return this.valueToYMappers.keySet();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.value.SubValueToYMapper
 * JD-Core Version:    0.6.0
 */