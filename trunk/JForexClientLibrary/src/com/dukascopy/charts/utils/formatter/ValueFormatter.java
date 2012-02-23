/*     */ package com.dukascopy.charts.utils.formatter;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class ValueFormatter
/*     */ {
/*     */   private final ChartState chartState;
/*  14 */   private final String RULER_FORMATTER = "RULER_FORMATTER";
/*  15 */   private final String MOUSE_FORMATTER = "MOUSE_FORMATTER";
/*  16 */   private final String VOLUME_FORMATTER = "VOLUME_FORMATTER";
/*     */ 
/*  18 */   private final Map<String, Map<Period, Map<Instrument, DukascopyDecimalFormat>>> formatters = new HashMap();
/*     */ 
/*  20 */   private final DukascopyDecimalFormat fiboFormatter = new DukascopyDecimalFormat(2);
/*     */ 
/*     */   public ValueFormatter(ChartState chartState) {
/*  23 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public String formatPrice(double price) {
/*  27 */     return getFormatter("MOUSE_FORMATTER", this.chartState.getPeriod(), this.chartState.getInstrument()).format(price);
/*     */   }
/*     */ 
/*     */   public String formatGridPrice(double price) {
/*  31 */     double doubleVal = price;
/*  32 */     if (doubleVal > 10000.0D) {
/*  33 */       return "";
/*     */     }
/*  35 */     return getFormatter("RULER_FORMATTER", this.chartState.getPeriod(), this.chartState.getInstrument()).format(doubleVal);
/*     */   }
/*     */ 
/*     */   public String formatCandleInProgressPrice(double price)
/*     */   {
/*  40 */     return getFormatter("RULER_FORMATTER", Period.TICK, this.chartState.getInstrument()).format(price);
/*     */   }
/*     */ 
/*     */   public String formatMouseCursorValue(double price) {
/*  44 */     DukascopyDecimalFormat formatter = getFormatter("MOUSE_FORMATTER", this.chartState.getPeriod(), this.chartState.getInstrument());
/*  45 */     double doubleVal = round(price, formatter.getDecimalPlaces());
/*  46 */     if (doubleVal > 10000.0D) {
/*  47 */       return "";
/*     */     }
/*  49 */     return formatter.format(doubleVal);
/*     */   }
/*     */ 
/*     */   public String formatHorizontalLinePrice(double price)
/*     */   {
/*  54 */     return getFormatter("RULER_FORMATTER", Period.TICK, this.chartState.getInstrument()).format(price);
/*     */   }
/*     */ 
/*     */   private double roundValueDiff(double valueDiff) {
/*  58 */     double pipValue = this.chartState.getInstrument().getPipValue();
/*  59 */     return round(valueDiff / pipValue, 2);
/*     */   }
/*     */ 
/*     */   public String formatValueDiff(double valueDiff) {
/*  63 */     return String.valueOf(roundValueDiff(valueDiff));
/*     */   }
/*     */ 
/*     */   public String formatFibo(double fiboLevel) {
/*  67 */     return this.fiboFormatter.format(fiboLevel);
/*     */   }
/*     */ 
/*     */   private double round(double Rval, int Rpl) {
/*  71 */     double result = StratUtils.round(Rval, Rpl);
/*  72 */     return result;
/*     */   }
/*     */ 
/*     */   private DukascopyDecimalFormat getFormatter(String formatterType, Period period, Instrument instrument) {
/*  76 */     Map typedFormatters = (Map)this.formatters.get(formatterType);
/*  77 */     if (typedFormatters == null) {
/*  78 */       typedFormatters = new HashMap();
/*  79 */       this.formatters.put(formatterType, typedFormatters);
/*     */     }
/*     */ 
/*  82 */     Map instruments = (Map)typedFormatters.get(period);
/*  83 */     if (instruments == null) {
/*  84 */       instruments = new HashMap();
/*  85 */       typedFormatters.put(period, instruments);
/*     */     }
/*     */ 
/*  88 */     DukascopyDecimalFormat formatter = (DukascopyDecimalFormat)instruments.get(instrument);
/*  89 */     if (formatter == null) {
/*  90 */       formatter = createNewFormatter(formatterType, period, instrument);
/*  91 */       instruments.put(instrument, formatter);
/*     */     }
/*     */ 
/*  94 */     return formatter;
/*     */   }
/*     */ 
/*     */   private DukascopyDecimalFormat createNewFormatter(String formatterType, Period period, Instrument instrument) {
/*  98 */     if ("RULER_FORMATTER".equals(formatterType)) {
/*  99 */       if (Period.TICK == period) {
/* 100 */         return new DukascopyDecimalFormat(instrument.getPipScale() + 1);
/*     */       }
/*     */ 
/* 103 */       return new DukascopyDecimalFormat(instrument.getPipScale());
/*     */     }
/*     */ 
/* 106 */     if ("MOUSE_FORMATTER".equals(formatterType)) {
/* 107 */       return new DukascopyDecimalFormat(instrument.getPipScale() + 1);
/*     */     }
/* 109 */     if ("VOLUME_FORMATTER".equals(formatterType)) {
/* 110 */       if (Instrument.XAUUSD.equals(instrument)) {
/* 111 */         return new DukascopyDecimalFormat(4);
/*     */       }
/*     */ 
/* 114 */       return new DukascopyDecimalFormat(2);
/*     */     }
/*     */ 
/* 118 */     throw new IllegalArgumentException("Unsupported formatter type - " + formatterType);
/*     */   }
/*     */ 
/*     */   public String formatVolume(double volume)
/*     */   {
/* 123 */     return getFormatter("VOLUME_FORMATTER", this.chartState.getPeriod(), this.chartState.getInstrument()).format(volume);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.formatter.ValueFormatter
 * JD-Core Version:    0.6.0
 */