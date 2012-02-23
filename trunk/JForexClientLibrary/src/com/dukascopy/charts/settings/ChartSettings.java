/*     */ package com.dukascopy.charts.settings;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class ChartSettings
/*     */ {
/*     */   public static final boolean DEFAULT_CHART_TRADING = true;
/*  94 */   private static final Map<Option, Object> defaultOptions = new EnumMap() { } ;
/*     */ 
/* 119 */   private static final Map<Option, Object> options = new EnumMap() { } ;
/*     */ 
/*     */   public static void resetOptions()
/*     */   {
/* 124 */     options.clear();
/* 125 */     options.putAll(defaultOptions);
/*     */   }
/*     */ 
/*     */   public static void set(Option option, Object value) {
/* 129 */     options.put(option, value);
/*     */   }
/*     */ 
/*     */   public static Object get(Option option) {
/* 133 */     return options.get(option);
/*     */   }
/*     */ 
/*     */   public static boolean getBoolean(Option option) {
/* 137 */     return ((Boolean)get(option)).booleanValue();
/*     */   }
/*     */ 
/*     */   public static String toString(Option option) {
/* 141 */     Object value = options.get(option);
/*     */ 
/* 143 */     if (value == null) {
/* 144 */       return null;
/*     */     }
/*     */ 
/* 147 */     return value.toString();
/*     */   }
/*     */ 
/*     */   public static Object valueOf(Option option, String text) {
/* 151 */     switch (3.$SwitchMap$com$dukascopy$charts$settings$ChartSettings$Option[option.ordinal()]) {
/*     */     case 1:
/* 153 */       return Filter.valueOf(text);
/*     */     case 2:
/* 155 */       return DailyFilter.valueOf(text);
/*     */     case 3:
/*     */       try {
/* 158 */         return GridType.valueOf(text);
/*     */       } catch (Exception ex) {
/* 160 */         return Boolean.valueOf(text).booleanValue() ? GridType.PIP : GridType.NONE;
/*     */       }
/*     */     case 4:
/* 163 */       return LineConstructionMethod.valueOf(text);
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/* 171 */       return Integer.valueOf(text);
/*     */     }
/* 173 */     return Boolean.valueOf(text);
/*     */   }
/*     */ 
/*     */   public static enum LineConstructionMethod
/*     */   {
/*  56 */     OPEN("line.construction.method.open"), 
/*  57 */     HIGH("line.construction.method.high"), 
/*  58 */     LOW("line.construction.method.low"), 
/*  59 */     CLOSE("line.construction.method.close"), 
/*  60 */     AVERAGE_HIGH_LOW("line.construction.method.average.high.low"), 
/*  61 */     AVERAGE_OPEN_CLOSE("line.construction.method.average.open.close");
/*     */ 
/*     */     private final String captionKey;
/*     */ 
/*  66 */     private LineConstructionMethod(String captionKey) { this.captionKey = captionKey;
/*     */     }
/*     */ 
/*     */     public double getY(double open, double close, double high, double low)
/*     */     {
/*  75 */       switch (ChartSettings.3.$SwitchMap$com$dukascopy$charts$settings$ChartSettings$LineConstructionMethod[ordinal()]) {
/*     */       case 1:
/*  77 */         return open;
/*     */       case 2:
/*  78 */         return close;
/*     */       case 3:
/*  79 */         return high;
/*     */       case 4:
/*  80 */         return low;
/*     */       case 5:
/*  82 */         return (high + low) / 2.0D;
/*     */       case 6:
/*  83 */         return (open + close) / 2.0D;
/*     */       }
/*  85 */       throw new IllegalArgumentException("Unsupported line construction method - " + this);
/*     */     }
/*     */ 
/*     */     public String getCaptionKey()
/*     */     {
/*  90 */       return this.captionKey;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum Option
/*     */   {
/*  30 */     FILTER, 
/*  31 */     DAILYFILTER, 
/*  32 */     GRID, 
/*  33 */     GRID_SIZE, 
/*  34 */     LAST_CANDLE_TRACKING, 
/*  35 */     THROUGHOUT_LAST_CANDLE_TRACKING, 
/*  36 */     PERIOD_SEPARATORS, 
/*  37 */     CANDLE_CANVAS, 
/*  38 */     ENTRY_ORDERS, 
/*  39 */     STOP_ORDERS, 
/*  40 */     OPEN_POSITIONS, 
/*  41 */     CLOSED_ORDERS, 
/*  42 */     POSITIONS_LABELS, 
/*  43 */     TRADING, 
/*  44 */     RANDOW_DRAWINGS_COLOR, 
/*  45 */     LINE_CONSTRUCTION_METHOD, 
/*  46 */     POINT_AND_FIGURE_GRID_CONSTRUCTION, 
/*  47 */     DRAWING_SEQUENCE_INDICATORS, 
/*  48 */     DRAWING_SEQUENCE_GRID, 
/*  49 */     DRAWING_SEQUENCE_ORDERS, 
/*  50 */     DRAWING_SEQUENCE_PERIOD_SEPARATORS, 
/*  51 */     DRAWING_SEQUENCE_DRAWINGS, 
/*  52 */     DRAWING_SEQUENCE_CANDLES;
/*     */   }
/*     */ 
/*     */   public static enum DailyFilter
/*     */   {
/*  24 */     NONE, 
/*  25 */     SUNDAY_IN_MONDAY, 
/*  26 */     SKIP_SUNDAY;
/*     */   }
/*     */ 
/*     */   public static enum GridType
/*     */   {
/*  18 */     NONE, 
/*  19 */     STATIC, 
/*  20 */     PIP;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.settings.ChartSettings
 * JD-Core Version:    0.6.0
 */