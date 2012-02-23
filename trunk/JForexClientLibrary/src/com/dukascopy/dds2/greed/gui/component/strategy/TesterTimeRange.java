/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public enum TesterTimeRange
/*     */ {
/*  14 */   CUSTOM_PERIOD_TEMPLATE, 
/*  15 */   INTRADAY, 
/*  16 */   LAST_DAY, 
/*  17 */   LAST_WEEK, 
/*  18 */   LAST_MONTH, 
/*  19 */   LAST_3_MONTHS, 
/*  20 */   LAST_6_MONTHS, 
/*  21 */   LAST_YEAR;
/*     */ 
/*  23 */   private long dateFrom = -9223372036854775808L;
/*  24 */   private long dateTo = -9223372036854775808L;
/*     */ 
/*     */   public long getDateFrom() {
/*  27 */     return this.dateFrom;
/*     */   }
/*     */ 
/*     */   public long getDateTo() {
/*  31 */     return this.dateTo;
/*     */   }
/*     */ 
/*     */   public void setDateFrom(long dateFrom) {
/*  35 */     this.dateFrom = dateFrom;
/*     */   }
/*     */ 
/*     */   public void setDateFrom(Date date) {
/*  39 */     this.dateFrom = getCalendar(date, "GMT").getTimeInMillis();
/*     */   }
/*     */ 
/*     */   public void setDateTo(long dateTo) {
/*  43 */     this.dateTo = dateTo;
/*     */   }
/*     */ 
/*     */   public void setDateTo(Date date) {
/*  47 */     this.dateTo = getCalendar(date, "GMT").getTimeInMillis();
/*     */   }
/*     */ 
/*     */   public void recalculateTimeRange() {
/*  51 */     if (this == CUSTOM_PERIOD_TEMPLATE) {
/*  52 */       return;
/*     */     }
/*     */ 
/*  55 */     recalculateDateFrom();
/*  56 */     recalculateDateTo();
/*     */   }
/*     */ 
/*     */   public void setCustomPeriod(JPanel panel) {
/*  60 */     RangeSelectionDialog dialog = RangeSelectionDialog.createDialog(panel, "dialog.select.range", Long.valueOf(this.dateFrom), Long.valueOf(this.dateTo));
/*  61 */     if (dialog.showModal()) {
/*  62 */       this.dateFrom = dialog.getDateFrom().getTime();
/*  63 */       this.dateTo = dialog.getDateTo().getTime();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void recalculateDateFrom() {
/*  68 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  69 */     cal.set(14, 0);
/*  70 */     cal.set(13, 0);
/*  71 */     cal.set(12, 0);
/*  72 */     cal.set(11, 0);
/*  73 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$strategy$TesterTimeRange[ordinal()]) {
/*     */     case 1:
/*  75 */       this.dateFrom = getTodayStart();
/*  76 */       break;
/*     */     case 2:
/*  78 */       cal.add(5, -1);
/*  79 */       this.dateFrom = cal.getTimeInMillis();
/*  80 */       break;
/*     */     case 3:
/*  82 */       cal.set(7, 2);
/*  83 */       cal.add(3, -1);
/*  84 */       this.dateFrom = cal.getTimeInMillis();
/*  85 */       break;
/*     */     case 4:
/*  87 */       cal.set(5, 1);
/*  88 */       cal.add(2, -1);
/*  89 */       this.dateFrom = cal.getTimeInMillis();
/*  90 */       break;
/*     */     case 5:
/*  92 */       cal.set(5, 1);
/*  93 */       cal.add(2, -3);
/*  94 */       this.dateFrom = cal.getTimeInMillis();
/*  95 */       break;
/*     */     case 6:
/*  97 */       cal.set(5, 1);
/*  98 */       cal.add(2, -6);
/*  99 */       this.dateFrom = cal.getTimeInMillis();
/* 100 */       break;
/*     */     case 7:
/* 102 */       cal.set(5, 1);
/* 103 */       cal.add(2, -12);
/* 104 */       this.dateFrom = cal.getTimeInMillis();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void recalculateDateTo()
/*     */   {
/* 110 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 111 */     cal.set(14, 0);
/* 112 */     cal.set(13, 0);
/* 113 */     cal.set(12, 0);
/* 114 */     cal.set(11, 0);
/* 115 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$strategy$TesterTimeRange[ordinal()]) {
/*     */     case 1:
/* 117 */       IFeedDataProvider feedDataProvider = (IFeedDataProvider)GreedContext.get("feedDataProvider");
/* 118 */       this.dateTo = feedDataProvider.getCurrentTime();
/* 119 */       break;
/*     */     case 2:
/* 121 */       this.dateTo = cal.getTimeInMillis();
/* 122 */       break;
/*     */     case 3:
/* 124 */       cal.set(7, 2);
/* 125 */       this.dateTo = cal.getTimeInMillis();
/* 126 */       break;
/*     */     case 4:
/* 128 */       cal.set(5, 1);
/* 129 */       this.dateTo = cal.getTimeInMillis();
/* 130 */       break;
/*     */     case 5:
/* 132 */       cal.set(5, 1);
/* 133 */       this.dateTo = cal.getTimeInMillis();
/* 134 */       break;
/*     */     case 6:
/* 136 */       cal.set(5, 1);
/* 137 */       this.dateTo = cal.getTimeInMillis();
/* 138 */       break;
/*     */     case 7:
/* 140 */       cal.set(5, 1);
/* 141 */       this.dateTo = cal.getTimeInMillis();
/*     */     }
/*     */   }
/*     */ 
/*     */   private long getTodayStart()
/*     */   {
/* 147 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 148 */     Calendar localCal = Calendar.getInstance();
/*     */ 
/* 151 */     localCal.setTimeInMillis(System.currentTimeMillis());
/* 152 */     int year = localCal.get(1);
/* 153 */     int month = localCal.get(2);
/* 154 */     int day = localCal.get(5);
/*     */ 
/* 156 */     cal.set(14, 0);
/* 157 */     cal.set(13, 0);
/* 158 */     cal.set(12, 0);
/* 159 */     cal.set(11, 0);
/* 160 */     cal.set(5, day);
/* 161 */     cal.set(2, month);
/* 162 */     cal.set(1, year);
/* 163 */     return cal.getTimeInMillis();
/*     */   }
/*     */ 
/*     */   private Calendar getCalendar(Date date, String timeZone) {
/* 167 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
/* 168 */     cal.setTime(date);
/* 169 */     return cal;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterTimeRange
 * JD-Core Version:    0.6.0
 */