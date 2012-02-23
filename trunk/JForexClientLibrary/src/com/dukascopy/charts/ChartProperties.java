/*     */ package com.dukascopy.charts;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public final class ChartProperties
/*     */ {
/*  10 */   public static final long MAX_VISIBLE_TIME_INTERVAL = TimeUnit.HOURS.toMillis(1L);
/*  11 */   public static final long MIN_VISIBLE_TIME_INTERVAL = TimeUnit.SECONDS.toMillis(10L);
/*     */   public static final int MAX_VISIBLE_SEQUENCE_SIZE = 2000;
/*     */   public static final int MIN_VISIBLE_SEQUENCE_SIZE = 10;
/*  16 */   public static final long MAX_VISIBLE_ORDERS_TIME_INTERVAL = 4000L * TimeUnit.HOURS.toMillis(1L);
/*     */   public static final int DEFAULT_AUTOSHIFT_OFFSET_PX = 30;
/*  19 */   public static final Point DEFAULT_MOUSE_CROSS_CURSOR_POINT = new Point(0, 0);
/*     */   public static final boolean DEFAULT_IS_MOUSE_CROSS_CURSOR_VISIBLE = false;
/*     */   public static final boolean DEFAULT_IS_LAST_CANDLE_VISIBLE = true;
/*     */   public static final boolean DEFAULT_IS_GRID_VISIBLE = true;
/*     */   public static final boolean DEFAULT_IS_CHART_SHIFT_ACTIVE = true;
/*     */   public static final boolean DEFAULT_IS_VERTICAL_MOVEMENT_ENABLED = false;
/*     */   public static final long INVALID_TIME_VALUE = -1L;
/*     */   public static final double INVALID_PRICE_VALUE = -1.0D;
/*     */   public static final int BAR_NOT_FOUND_IDX = -1;
/*     */   public static final int ILLEGAL_CANDLES_COUNT = -1;
/*     */   public static final int DIST_BETWEEN_CANDLES = 1;
/*     */   public static final int CANDLE_WIDTH_STEP_PX = 2;
/*     */   public static final int MAX_CANDLE_WIDTH_PX = 73;
/*     */   public static final int MIN_CANDLE_WIDTH_PX = 1;
/*     */   public static final int RANGE_SCROLL_BAR_HEIGHT = 100;
/*     */   public static final int MIN_LEFT_AND_RIGHT_OFFSET_IN_PX = 100;
/*     */   public static final int CLOSE_WIDTH = 10;
/*     */   public static final int GRID_INCREMENT_PX = 30;
/*     */   public static final int GRID_INCREMENT_PY = 30;
/*     */   public static final int HEIGHT_PX = 12;
/*     */   public static final int WIDTH_PX = 51;
/*     */   public static final int DIST_FROM_LEFT_BORDER = 7;
/*     */   public static final int MOVABLE_LABEL_WIDTH = 70;
/*     */   public static final int OFFSET = 4;
/*     */   public static final double MAX_PADDING = 0.45D;
/*     */   public static final double DEFAULT_CHART_PADDING = 0.025D;
/*     */   public static final double DEFAULT_SUBINDICATOR_PADDING = 0.1D;
/*     */   public static final String DEFAULT_STRING_PATETRN = "12345.67890";
/*  67 */   public static final Double MIN_LEVEL_VALUE = Double.valueOf(-1000.0D);
/*  68 */   public static final Double MAX_LEVEL_VALUE = Double.valueOf(1000.0D);
/*  69 */   public static final Double LEVEL_STEP_SIZE = Double.valueOf(1.0D);
/*  70 */   public static final Double NEXT_LEVEL_INCREMENT = Double.valueOf(10.0D);
/*     */   public static final String DEFAULT_PRESET_NAME = "Default";
/*     */ 
/*     */   public static List<Object[]> createDefaultLevelsPercents()
/*     */   {
/*  82 */     return new ArrayList()
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public static List<Object[]> createDefaultLevelsFibo()
/*     */   {
/*  94 */     return new ArrayList()
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public static List<Object[]> createDefaultLevelsFiboProjections()
/*     */   {
/* 105 */     return new ArrayList()
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public static List<Object[]> createDefaultLevelsFiboRetracements()
/*     */   {
/* 114 */     return new ArrayList()
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public static List<Object[]> createDefaultLevelsFiboExtensions()
/*     */   {
/* 128 */     return new ArrayList()
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public static List<Object[]> createDefaultLevelsFiboTimes()
/*     */   {
/* 138 */     return new ArrayList()
/*     */     {
/*     */     };
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.ChartProperties
 * JD-Core Version:    0.6.0
 */