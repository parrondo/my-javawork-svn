/*     */ package com.dukascopy.charts.data.datacache.wrapper;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class TimeInterval
/*     */   implements ITimeInterval
/*     */ {
/*  15 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*     */   private long start;
/*     */   private long end;
/*     */ 
/*     */   public TimeInterval()
/*     */   {
/*  24 */     this(-9223372036854775808L, -9223372036854775808L);
/*     */   }
/*     */ 
/*     */   public TimeInterval(long start, long end) {
/*  28 */     this.start = start;
/*  29 */     this.end = end;
/*     */   }
/*     */ 
/*     */   public long getStart() {
/*  33 */     return this.start;
/*     */   }
/*     */ 
/*     */   public void setStart(long start) {
/*  37 */     this.start = start;
/*     */   }
/*     */ 
/*     */   public long getEnd() {
/*  41 */     return this.end;
/*     */   }
/*     */ 
/*     */   public void setEnd(long end) {
/*  45 */     this.end = end;
/*     */   }
/*     */ 
/*     */   public String getFormattedEnd() {
/*  49 */     return DATE_FORMAT.format(Long.valueOf(getEnd()));
/*     */   }
/*     */ 
/*     */   public String getFormattedStart() {
/*  53 */     return DATE_FORMAT.format(Long.valueOf(getStart()));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  58 */     return getFormattedStart() + " - " + getFormattedEnd();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  63 */     int prime = 31;
/*  64 */     int result = 1;
/*  65 */     result = 31 * result + (int)(this.end ^ this.end >>> 32);
/*  66 */     result = 31 * result + (int)(this.start ^ this.start >>> 32);
/*  67 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  72 */     if (this == obj) {
/*  73 */       return true;
/*     */     }
/*  75 */     if (obj == null) {
/*  76 */       return false;
/*     */     }
/*  78 */     if (getClass() != obj.getClass()) {
/*  79 */       return false;
/*     */     }
/*  81 */     TimeInterval other = (TimeInterval)obj;
/*  82 */     if (this.end != other.end) {
/*  83 */       return false;
/*     */     }
/*     */ 
/*  86 */     return this.start == other.start;
/*     */   }
/*     */ 
/*     */   public boolean isInIntervalForWeekends(long time)
/*     */   {
/*  97 */     boolean value = (getStart() <= time) && (time < getEnd());
/*  98 */     return value;
/*     */   }
/*     */ 
/*     */   public boolean isInInterval(long time)
/*     */   {
/* 103 */     boolean value = (getStart() <= time) && (time <= getEnd());
/* 104 */     return value;
/*     */   }
/*     */ 
/*     */   public boolean intersects(ITimeInterval interval)
/*     */   {
/* 109 */     boolean isInInterval = isInInterval(interval.getStart());
/*     */ 
/* 111 */     if (isInInterval) {
/* 112 */       return true;
/*     */     }
/*     */ 
/* 115 */     isInInterval = isInInterval(interval.getEnd());
/* 116 */     if (isInInterval) {
/* 117 */       return true;
/*     */     }
/*     */ 
/* 120 */     isInInterval = interval.isInInterval(getEnd());
/* 121 */     if (isInInterval) {
/* 122 */       return true;
/*     */     }
/*     */ 
/* 125 */     isInInterval = interval.isInInterval(getStart());
/*     */ 
/* 127 */     return isInInterval;
/*     */   }
/*     */ 
/*     */   public boolean isIntervalTheSame(ITimeInterval interval)
/*     */   {
/* 135 */     return equals(interval);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  17 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.wrapper.TimeInterval
 * JD-Core Version:    0.6.0
 */