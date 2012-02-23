/*    */ package com.dukascopy.charts.data.datacache.dhl;
/*    */ 
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class HighLow
/*    */ {
/* 15 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
/*    */   private long time;
/*    */   private double high;
/*    */   private double low;
/*    */ 
/*    */   public HighLow()
/*    */   {
/*    */   }
/*    */ 
/*    */   public HighLow(long time, double high, double low)
/*    */   {
/* 29 */     this.time = time;
/* 30 */     this.high = high;
/* 31 */     this.low = low;
/*    */   }
/*    */ 
/*    */   public long getTime() {
/* 35 */     return this.time;
/*    */   }
/*    */ 
/*    */   public void setTime(long time) {
/* 39 */     this.time = time;
/*    */   }
/*    */ 
/*    */   public double getHigh() {
/* 43 */     return this.high;
/*    */   }
/*    */ 
/*    */   public void setHigh(double high) {
/* 47 */     this.high = high;
/*    */   }
/*    */ 
/*    */   public double getLow() {
/* 51 */     return this.low;
/*    */   }
/*    */ 
/*    */   public void setLow(double low) {
/* 55 */     this.low = low;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 60 */     int prime = 31;
/* 61 */     int result = 1;
/*    */ 
/* 63 */     long temp = Double.doubleToLongBits(this.high);
/* 64 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 65 */     temp = Double.doubleToLongBits(this.low);
/* 66 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 67 */     result = 31 * result + (int)(this.time ^ this.time >>> 32);
/* 68 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 73 */     if (this == obj)
/* 74 */       return true;
/* 75 */     if (obj == null)
/* 76 */       return false;
/* 77 */     if (getClass() != obj.getClass())
/* 78 */       return false;
/* 79 */     HighLow other = (HighLow)obj;
/* 80 */     if (Double.doubleToLongBits(this.high) != Double.doubleToLongBits(other.high))
/*    */     {
/* 82 */       return false;
/* 83 */     }if (Double.doubleToLongBits(this.low) != Double.doubleToLongBits(other.low)) {
/* 84 */       return false;
/*    */     }
/* 86 */     return this.time == other.time;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 93 */     return DATE_FORMAT.format(Long.valueOf(this.time)) + ", high = " + this.high + ", low = " + this.low;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 17 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.dhl.HighLow
 * JD-Core Version:    0.6.0
 */