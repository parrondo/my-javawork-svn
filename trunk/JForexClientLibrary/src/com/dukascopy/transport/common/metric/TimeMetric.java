/*    */ package com.dukascopy.transport.common.metric;
/*    */ 
/*    */ public class TimeMetric extends Metric
/*    */ {
/*    */   private long beginTimeMillis;
/*    */   private long endTimeMillis;
/*    */ 
/*    */   public TimeMetric()
/*    */   {
/*    */   }
/*    */ 
/*    */   public TimeMetric(long beginTimeMillis)
/*    */   {
/* 28 */     setBeginTimeMillis(beginTimeMillis);
/*    */   }
/*    */ 
/*    */   public TimeMetric(long beginTimeMillis, long endTimeMillis)
/*    */   {
/* 40 */     setBeginTimeMillis(beginTimeMillis);
/* 41 */     setEndTimeMillis(endTimeMillis);
/*    */   }
/*    */ 
/*    */   public long getDeltaTimeMillis()
/*    */   {
/* 50 */     return getEndTimeMillis() - getBeginTimeMillis();
/*    */   }
/*    */ 
/*    */   public void setBeginTimeMillis(long beginTimeMillis) {
/* 54 */     this.beginTimeMillis = beginTimeMillis;
/*    */   }
/*    */ 
/*    */   public long getBeginTimeMillis() {
/* 58 */     return this.beginTimeMillis;
/*    */   }
/*    */ 
/*    */   public void setEndTimeMillis(long endTimeMillis) {
/* 62 */     this.endTimeMillis = endTimeMillis;
/*    */   }
/*    */ 
/*    */   public long getEndTimeMillis() {
/* 66 */     return this.endTimeMillis;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 70 */     StringBuffer sb = new StringBuffer("TimeMetric{");
/* 71 */     sb.append("group=").append(getGroup());
/* 72 */     sb.append(",beginTimeMillis=").append(getBeginTimeMillis());
/* 73 */     sb.append(",endTimeMillis=").append(getEndTimeMillis());
/* 74 */     sb.append(",deltaTimeMillis=").append(getDeltaTimeMillis());
/* 75 */     sb.append("}");
/* 76 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.metric.TimeMetric
 * JD-Core Version:    0.6.0
 */