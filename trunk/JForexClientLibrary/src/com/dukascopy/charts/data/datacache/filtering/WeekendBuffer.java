/*     */ package com.dukascopy.charts.data.datacache.filtering;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class WeekendBuffer
/*     */ {
/*  20 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*     */   private TimeInterval[] buffer;
/*     */   private long from;
/*     */   private long to;
/*     */   private static final long ONE_DAY_INTERVAL = 86400000L;
/*     */   private static final long ONE_WEEK_INTERVAL = 604800000L;
/*     */ 
/*     */   public WeekendBuffer()
/*     */   {
/*  34 */     clear();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  41 */     this.from = 9223372036854775807L;
/*  42 */     this.to = -9223372036854775808L;
/*  43 */     this.buffer = new TimeInterval[0];
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  47 */     return size() <= 0;
/*     */   }
/*     */ 
/*     */   public int size() {
/*  51 */     return this.buffer.length;
/*     */   }
/*     */ 
/*     */   public boolean coversTime(long time) {
/*  55 */     boolean result = (this.from <= time) && (time <= this.to);
/*  56 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean coversInterval(long from, long to) {
/*  60 */     boolean result = (this.from <= from) && (to <= this.to);
/*  61 */     return result;
/*     */   }
/*     */ 
/*     */   public void set(List<TimeInterval> weekends) {
/*  65 */     if ((weekends == null) || (weekends.isEmpty())) {
/*  66 */       return;
/*     */     }
/*     */ 
/*  69 */     clear();
/*  70 */     this.buffer = ((TimeInterval[])weekends.toArray(new TimeInterval[weekends.size()]));
/*     */ 
/*  72 */     for (TimeInterval weekend : this.buffer) {
/*  73 */       if (this.from > weekend.getStart()) {
/*  74 */         this.from = weekend.getStart();
/*     */       }
/*     */ 
/*  77 */       if (this.to < weekend.getEnd())
/*  78 */         this.to = weekend.getEnd();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isWeekendTime(long time)
/*     */   {
/*  92 */     boolean result = false;
/*  93 */     TimeInterval appropriateWeekend = getWeekend(time);
/*     */ 
/*  95 */     if (appropriateWeekend != null) {
/*  96 */       result = appropriateWeekend.isInIntervalForWeekends(time);
/*     */     }
/*     */     else {
/*  99 */       for (TimeInterval weekend : this.buffer) {
/* 100 */         if (weekend.isInIntervalForWeekends(time)) {
/* 101 */           result = true;
/* 102 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 107 */     return result;
/*     */   }
/*     */ 
/*     */   public TimeInterval getWeekend(long time)
/*     */   {
/* 114 */     long fromWeekNumber = this.from / 604800000L;
/* 115 */     long currentWeekNumber = time / 604800000L;
/* 116 */     int index = (int)(currentWeekNumber - fromWeekNumber);
/*     */ 
/* 118 */     TimeInterval weekend = null;
/* 119 */     if ((0 <= index) && (index < this.buffer.length)) {
/* 120 */       weekend = this.buffer[index];
/*     */     }
/*     */ 
/* 123 */     return weekend;
/*     */   }
/*     */ 
/*     */   public long getFrom() {
/* 127 */     return this.from;
/*     */   }
/*     */ 
/*     */   public long getTo() {
/* 131 */     return this.to;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  22 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.filtering.WeekendBuffer
 * JD-Core Version:    0.6.0
 */