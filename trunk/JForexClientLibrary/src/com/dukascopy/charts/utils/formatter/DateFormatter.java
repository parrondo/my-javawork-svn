/*     */ package com.dukascopy.charts.utils.formatter;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public final class DateFormatter
/*     */ {
/*  14 */   private static final Map<Period, DateFormat> rightFormatters = new HashMap();
/*  15 */   private static final Map<Period, DateFormat> formatters = new HashMap();
/*  16 */   private static final Map<Period, DateFormat> timeMarkerFormatters = new HashMap();
/*     */ 
/*  18 */   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
/*  19 */   private static final SimpleDateFormat hhmmFormat = new SimpleDateFormat("HH:mm");
/*     */   final ChartState chartState;
/*     */ 
/*     */   public DateFormatter(ChartState chartState)
/*     */   {
/*  89 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public String formatRightTime(long timeToFormat) {
/*  93 */     Period period = Period.getBasicPeriodForCustom(this.chartState.getPeriod());
/*  94 */     return ((DateFormat)rightFormatters.get(period)).format(Long.valueOf(timeToFormat));
/*     */   }
/*     */ 
/*     */   public String formatTime(long timeToFormat) {
/*  98 */     Period period = Period.getBasicPeriodForCustom(this.chartState.getPeriod());
/*  99 */     return ((DateFormat)formatters.get(period)).format(Long.valueOf(timeToFormat));
/*     */   }
/*     */ 
/*     */   public String formatTimeMarkerTime(long time) {
/* 103 */     Period period = Period.getBasicPeriodForCustom(this.chartState.getPeriod());
/* 104 */     return ((DateFormat)timeMarkerFormatters.get(period)).format(Long.valueOf(time));
/*     */   }
/*     */ 
/*     */   public String formatTimeWithoutDate(long time) {
/* 108 */     Period period = Period.getBasicPeriodForCustom(this.chartState.getPeriod());
/*     */     String formatedTime;
/*     */     String formatedTime;
/* 110 */     if (period.getInterval() >= Period.ONE_HOUR.getInterval())
/* 111 */       formatedTime = hhmmFormat.format(Long.valueOf(time));
/*     */     else {
/* 113 */       formatedTime = ((DateFormat)formatters.get(period)).format(Long.valueOf(time));
/*     */     }
/* 115 */     return formatedTime;
/*     */   }
/*     */ 
/*     */   public String formatDateWithoutTime(long time) {
/* 119 */     Period period = Period.getBasicPeriodForCustom(this.chartState.getPeriod());
/*     */     String formatedTime;
/*     */     String formatedTime;
/* 121 */     if (period.getInterval() >= Period.ONE_HOUR.getInterval())
/* 122 */       formatedTime = dateFormat.format(Long.valueOf(time));
/*     */     else {
/* 124 */       formatedTime = ((DateFormat)rightFormatters.get(period)).format(Long.valueOf(time));
/*     */     }
/* 126 */     return formatedTime;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  24 */     formatters.put(Period.TICK, new SimpleDateFormat("HH:mm:ss:SSS"));
/*  25 */     formatters.put(Period.TEN_SECS, new SimpleDateFormat("HH:mm:ss"));
/*  26 */     formatters.put(Period.TWENTY_SECS, new SimpleDateFormat("HH:mm:ss"));
/*  27 */     formatters.put(Period.THIRTY_SECS, new SimpleDateFormat("HH:mm:ss"));
/*  28 */     formatters.put(Period.ONE_MIN, new SimpleDateFormat("HH:mm"));
/*  29 */     formatters.put(Period.FIVE_MINS, new SimpleDateFormat("HH:mm"));
/*  30 */     formatters.put(Period.TEN_MINS, new SimpleDateFormat("HH:mm"));
/*  31 */     formatters.put(Period.FIFTEEN_MINS, new SimpleDateFormat("HH:mm"));
/*  32 */     formatters.put(Period.THIRTY_MINS, new SimpleDateFormat("HH:mm dd MMM"));
/*  33 */     formatters.put(Period.ONE_HOUR, new SimpleDateFormat("HH:mm dd MMM"));
/*  34 */     formatters.put(Period.FOUR_HOURS, new SimpleDateFormat("HH dd MMM"));
/*  35 */     formatters.put(Period.DAILY, new SimpleDateFormat("dd MMM"));
/*  36 */     formatters.put(Period.WEEKLY, new SimpleDateFormat("MMM"));
/*  37 */     formatters.put(Period.MONTHLY, new SimpleDateFormat("MMM yyyy"));
/*     */ 
/*  40 */     rightFormatters.put(Period.TICK, new SimpleDateFormat("yyyy-MM-dd"));
/*  41 */     rightFormatters.put(Period.TEN_SECS, new SimpleDateFormat("yyyy-MM-dd"));
/*  42 */     rightFormatters.put(Period.TWENTY_SECS, new SimpleDateFormat("yyyy-MM-dd"));
/*  43 */     rightFormatters.put(Period.THIRTY_SECS, new SimpleDateFormat("yyyy-MM-dd"));
/*  44 */     rightFormatters.put(Period.ONE_MIN, new SimpleDateFormat("yyyy-MM-dd"));
/*  45 */     rightFormatters.put(Period.FIVE_MINS, new SimpleDateFormat("yyyy-MM-dd"));
/*  46 */     rightFormatters.put(Period.TEN_MINS, new SimpleDateFormat("yyyy-MM-dd"));
/*  47 */     rightFormatters.put(Period.FIFTEEN_MINS, new SimpleDateFormat("yyyy-MM-dd"));
/*  48 */     rightFormatters.put(Period.THIRTY_MINS, new SimpleDateFormat("yyyy-MM-dd"));
/*  49 */     rightFormatters.put(Period.ONE_HOUR, new SimpleDateFormat("MMM yyyy"));
/*  50 */     rightFormatters.put(Period.FOUR_HOURS, new SimpleDateFormat("yyyy"));
/*  51 */     rightFormatters.put(Period.DAILY, new SimpleDateFormat("yyyy"));
/*  52 */     rightFormatters.put(Period.WEEKLY, new SimpleDateFormat("yyyy"));
/*  53 */     rightFormatters.put(Period.MONTHLY, new SimpleDateFormat("yyyy"));
/*     */ 
/*  56 */     timeMarkerFormatters.put(Period.TICK, new SimpleDateFormat("HH:mm:ss:SSS yyyy-MM-dd"));
/*  57 */     timeMarkerFormatters.put(Period.TEN_SECS, new SimpleDateFormat("HH:mm:ss yyyy-MM-dd"));
/*  58 */     timeMarkerFormatters.put(Period.TWENTY_SECS, new SimpleDateFormat("HH:mm:ss yyyy-MM-dd"));
/*  59 */     timeMarkerFormatters.put(Period.THIRTY_SECS, new SimpleDateFormat("HH:mm:ss yyyy-MM-dd"));
/*  60 */     timeMarkerFormatters.put(Period.ONE_MIN, new SimpleDateFormat("HH:mm yyyy-MM-dd"));
/*  61 */     timeMarkerFormatters.put(Period.FIVE_MINS, new SimpleDateFormat("HH:mm yyyy-MM-dd"));
/*  62 */     timeMarkerFormatters.put(Period.TEN_MINS, new SimpleDateFormat("HH:mm yyyy-MM-dd"));
/*  63 */     timeMarkerFormatters.put(Period.FIFTEEN_MINS, new SimpleDateFormat("HH:mm yyyy-MM-dd"));
/*  64 */     timeMarkerFormatters.put(Period.THIRTY_MINS, new SimpleDateFormat("HH:mm yyyy-MM-dd"));
/*  65 */     timeMarkerFormatters.put(Period.ONE_HOUR, new SimpleDateFormat("HH:mm yyyy-MM-dd"));
/*  66 */     timeMarkerFormatters.put(Period.FOUR_HOURS, new SimpleDateFormat("HH:mm yyyy-MM-dd"));
/*  67 */     timeMarkerFormatters.put(Period.DAILY, new SimpleDateFormat("yyyy-MM-dd"));
/*  68 */     timeMarkerFormatters.put(Period.WEEKLY, new SimpleDateFormat("yyyy-MM-dd"));
/*  69 */     timeMarkerFormatters.put(Period.MONTHLY, new SimpleDateFormat("yyyy-MM"));
/*     */ 
/*  71 */     TimeZone timeZone = TimeZone.getTimeZone("GMT 0");
/*  72 */     for (DateFormat dateFormat : formatters.values()) {
/*  73 */       dateFormat.setTimeZone(timeZone);
/*     */     }
/*     */ 
/*  76 */     for (DateFormat dateFormat : timeMarkerFormatters.values()) {
/*  77 */       dateFormat.setTimeZone(timeZone);
/*     */     }
/*     */ 
/*  80 */     for (DateFormat dateFormat : rightFormatters.values()) {
/*  81 */       dateFormat.setTimeZone(timeZone);
/*     */     }
/*     */ 
/*  84 */     dateFormat.setTimeZone(timeZone);
/*  85 */     hhmmFormat.setTimeZone(timeZone);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.formatter.DateFormatter
 * JD-Core Version:    0.6.0
 */