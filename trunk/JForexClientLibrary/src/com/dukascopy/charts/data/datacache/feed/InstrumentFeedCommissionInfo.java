/*    */ package com.dukascopy.charts.data.datacache.feed;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.preloader.InstrumentTimeInterval;
/*    */ 
/*    */ public class InstrumentFeedCommissionInfo extends InstrumentTimeInterval
/*    */   implements IInstrumentFeedCommissionInfo
/*    */ {
/*    */   private double feedCommission;
/*    */   private long priority;
/*    */ 
/*    */   public InstrumentFeedCommissionInfo(Instrument instrument, double feedCommission, long start, long end)
/*    */   {
/* 24 */     this(instrument, feedCommission, start, end, -9223372036854775808L);
/*    */   }
/*    */ 
/*    */   public InstrumentFeedCommissionInfo(Instrument instrument, double feedCommission, long start, long end, long priority)
/*    */   {
/* 40 */     super(instrument, start, end);
/* 41 */     this.feedCommission = feedCommission;
/* 42 */     this.priority = priority;
/*    */   }
/*    */ 
/*    */   public double getFeedCommission()
/*    */   {
/* 47 */     return this.feedCommission;
/*    */   }
/*    */ 
/*    */   public void setFeedCommission(double feedCommission) {
/* 51 */     this.feedCommission = feedCommission;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 56 */     int prime = 31;
/* 57 */     int result = super.hashCode();
/*    */ 
/* 59 */     long temp = Double.doubleToLongBits(this.feedCommission);
/* 60 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 61 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 66 */     if (this == obj)
/* 67 */       return true;
/* 68 */     if (!super.equals(obj))
/* 69 */       return false;
/* 70 */     if (getClass() != obj.getClass())
/* 71 */       return false;
/* 72 */     InstrumentFeedCommissionInfo other = (InstrumentFeedCommissionInfo)obj;
/*    */ 
/* 75 */     return Double.doubleToLongBits(this.feedCommission) == Double.doubleToLongBits(other.feedCommission);
/*    */   }
/*    */ 
/*    */   public long getPriority()
/*    */   {
/* 81 */     return this.priority;
/*    */   }
/*    */ 
/*    */   public void setPriority(long priority)
/*    */   {
/* 86 */     this.priority = priority;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 91 */     return this.feedCommission + " " + super.toString() + " " + this.priority;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.feed.InstrumentFeedCommissionInfo
 * JD-Core Version:    0.6.0
 */