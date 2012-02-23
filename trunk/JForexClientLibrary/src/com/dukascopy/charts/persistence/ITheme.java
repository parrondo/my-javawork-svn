/*     */ package com.dukascopy.charts.persistence;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ 
/*     */ public abstract interface ITheme extends Cloneable
/*     */ {
/*     */   public abstract void setName(String paramString);
/*     */ 
/*     */   public abstract String getName();
/*     */ 
/*     */   public abstract void setColor(ChartElement paramChartElement, Color paramColor);
/*     */ 
/*     */   public abstract Color getColor(ChartElement paramChartElement);
/*     */ 
/*     */   public abstract void setFont(TextElement paramTextElement, Font paramFont);
/*     */ 
/*     */   public abstract Font getFont(TextElement paramTextElement);
/*     */ 
/*     */   public abstract void setStroke(StrokeElement paramStrokeElement, BasicStroke paramBasicStroke);
/*     */ 
/*     */   public abstract BasicStroke getStroke(StrokeElement paramStrokeElement);
/*     */ 
/*     */   public abstract ITheme clone();
/*     */ 
/*     */   public static enum StrokeElement
/*     */   {
/*  94 */     DEFAULT("Default"), 
/*  95 */     GRID_STROKE("Grid Stroke"), 
/*  96 */     LAST_CANDLE_TRACKING_LINE_STROKE("Last Price Tracking Line Stroke");
/*     */ 
/*     */     public static final BasicStroke BASIC_STROKE;
/*     */     private String description;
/*     */ 
/*     */     private StrokeElement(String description)
/*     */     {
/* 110 */       this.description = description;
/*     */     }
/*     */ 
/*     */     public String getDescription() {
/* 114 */       return this.description;
/*     */     }
/*     */ 
/*     */     static
/*     */     {
/*  98 */       BASIC_STROKE = new BasicStroke(1.0F, 0, 2, 0.0F, null, 0.0F);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum TextElement
/*     */   {
/*  76 */     DEFAULT("Default"), 
/*  77 */     AXIS("Axis labels"), 
/*  78 */     OHLC("OHLC"), 
/*  79 */     META("Meta info"), 
/*  80 */     ORDER("Order info");
/*     */ 
/*     */     private String description;
/*     */ 
/*  85 */     private TextElement(String description) { this.description = description; }
/*     */ 
/*     */     public String getDescription()
/*     */     {
/*  89 */       return this.description;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum ChartElement
/*     */   {
/*  13 */     DEFAULT("Default Color"), 
/*  14 */     BACKGROUND("Background"), 
/*     */ 
/*  16 */     GRID("Grid"), 
/*  17 */     DRAWING("Drawing"), 
/*  18 */     OUTLINE("Outline"), 
/*  19 */     OHLC("OHLC"), 
/*  20 */     OHLC_BACKGROUND("OHLC Background"), 
/*  21 */     META("Meta Info"), 
/*  22 */     LAST_CANDLE_TRACKING_LINE("Last Price Tracking Line Color"), 
/*     */ 
/*  24 */     BID("Bid"), 
/*  25 */     ASK("Ask"), 
/*  26 */     NEUTRAL_BAR("Neutral bar"), 
/*  27 */     LINE_UP("Up"), 
/*  28 */     LINE_DOWN("Down"), 
/*  29 */     BAR_UP("Bar up"), 
/*  30 */     BAR_DOWN("Bar down"), 
/*  31 */     DOJI_CANDLE("Doji candle"), 
/*  32 */     CANDLE_BEAR("Bear candle"), 
/*  33 */     CANDLE_BEAR_BORDER("Bear candle border"), 
/*  34 */     CANDLE_BULL("Bull candle"), 
/*  35 */     CANDLE_BULL_BORDER("Bull candle border"), 
/*     */ 
/*  37 */     HT_BALANCE("Balance"), 
/*  38 */     HT_EQUITY("Equity"), 
/*  39 */     HT_PROFIT_LOSS("P/L"), 
/*     */ 
/*  41 */     AXIS_LABEL_FOREGROUND("Label Foreground"), 
/*  42 */     AXIS_LABEL_BACKGROUND("Label Background"), 
/*  43 */     AXIS_LABEL_BACKGROUND_ASK("Ask Label Background"), 
/*  44 */     AXIS_LABEL_BACKGROUND_BID("Bid Label Background"), 
/*  45 */     AXIS_PANEL_BACKGROUND("Axis Panel Background"), 
/*  46 */     AXIS_PANEL_FOREGROUND("Axis Panel Foreground"), 
/*     */ 
/*  48 */     ORDER_TRACKING_LINE("Order Tracking Line"), 
/*  49 */     ORDER_LONG_POSITION_TRACKING_LINE("Long Position Tracking Line"), 
/*  50 */     ORDER_SHORT_POSITION_TRACKING_LINE("Short Position Tracking Line"), 
/*  51 */     ORDER_OPEN_SELL("Open Short"), 
/*  52 */     ORDER_CLOSE_SELL("Close Short"), 
/*  53 */     ORDER_OPEN_BUY("Open Long"), 
/*  54 */     ORDER_CLOSE_BUY("Close Long"), 
/*  55 */     PERIOD_SEPARATORS("Period Separators"), 
/*     */ 
/*  57 */     TEXT("Text"), 
/*  58 */     ODD_ROW("Odd Row"), 
/*  59 */     EVEN_ROW("Even Row"), 
/*  60 */     CANDLE_BEAR_ROW("Candle Bear Row"), 
/*  61 */     CANDLE_BULL_ROW("Candle Bull Row");
/*     */ 
/*     */     private String description;
/*     */ 
/*     */     private ChartElement(String description) {
/*  67 */       this.description = description;
/*     */     }
/*     */ 
/*     */     public String getDescription() {
/*  71 */       return this.description;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.ITheme
 * JD-Core Version:    0.6.0
 */