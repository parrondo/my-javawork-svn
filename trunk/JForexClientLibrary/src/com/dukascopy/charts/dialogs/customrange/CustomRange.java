/*    */ package com.dukascopy.charts.dialogs.customrange;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class CustomRange
/*    */ {
/* 15 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat() {  } ;
/*    */   private JForexPeriod period;
/*    */   private long time;
/*    */   private int before;
/*    */   private int after;
/*    */ 
/* 23 */   public CustomRange(JForexPeriod period, long time, int before, int after) { this.period = period;
/* 24 */     this.time = time;
/* 25 */     this.before = before;
/* 26 */     this.after = after;
/*    */ 
/* 28 */     if ((time < 0L) || (time > System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1L)))
/* 29 */       this.time = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public JForexPeriod getPeriod()
/*    */   {
/* 34 */     return this.period;
/*    */   }
/*    */ 
/*    */   public void setPeriod(JForexPeriod period) {
/* 38 */     this.period = period;
/*    */   }
/*    */ 
/*    */   public long getTime() {
/* 42 */     return this.time;
/*    */   }
/*    */ 
/*    */   public void setTime(long time) {
/* 46 */     this.time = time;
/*    */   }
/*    */ 
/*    */   public int getBefore() {
/* 50 */     return this.before;
/*    */   }
/*    */ 
/*    */   public void setBefore(int before) {
/* 54 */     this.before = before;
/*    */   }
/*    */ 
/*    */   public int getAfter() {
/* 58 */     return this.after;
/*    */   }
/*    */ 
/*    */   public void setAfter(int after) {
/* 62 */     this.after = after;
/*    */   }
/*    */ 
/*    */   public int getTotal() {
/* 66 */     return this.after + this.before;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 71 */     return "Period : " + this.period.toString() + " Time : " + DATE_FORMAT.format(Long.valueOf(this.time)) + " Before : " + this.before + " After : " + this.after;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.customrange.CustomRange
 * JD-Core Version:    0.6.0
 */