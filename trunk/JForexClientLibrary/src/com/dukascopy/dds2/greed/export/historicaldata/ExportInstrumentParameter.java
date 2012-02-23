/*     */ package com.dukascopy.dds2.greed.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ 
/*     */ public class ExportInstrumentParameter
/*     */ {
/*  11 */   private Instrument instrument = null;
/*  12 */   private PeriodType periodType = null;
/*  13 */   private CompositePeriod compositePeriod = null;
/*  14 */   private PriceRange priceRange = null;
/*  15 */   private JForexPeriod pointAndFigure = null;
/*  16 */   private OfferSide offerSide = null;
/*  17 */   private Long dateFrom = Long.valueOf(-9223372036854775808L);
/*  18 */   private Long dateTo = Long.valueOf(-9223372036854775808L);
/*  19 */   private ExportFormat exportFormat = null;
/*  20 */   private Filter filter = null;
/*     */ 
/*     */   public ExportInstrumentParameter(Instrument instrument, PeriodType periodType, CompositePeriod compositePeriod, PriceRange priceRange, JForexPeriod pointAndFigure, OfferSide offerSide, Long dateFrom, Long dateTo, ExportFormat exportFormat, Filter filter)
/*     */   {
/*  34 */     this.instrument = instrument;
/*  35 */     this.periodType = periodType;
/*  36 */     this.compositePeriod = compositePeriod;
/*  37 */     this.priceRange = priceRange;
/*  38 */     this.pointAndFigure = pointAndFigure;
/*  39 */     this.offerSide = offerSide;
/*  40 */     this.dateFrom = dateFrom;
/*  41 */     this.dateTo = dateTo;
/*  42 */     this.exportFormat = exportFormat;
/*  43 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/*  47 */     return this.instrument;
/*     */   }
/*     */   public void setInstrument(Instrument instrument) {
/*  50 */     this.instrument = instrument;
/*     */   }
/*     */   public PeriodType getPeriodType() {
/*  53 */     return this.periodType;
/*     */   }
/*     */   public void setPeriodType(PeriodType periodType) {
/*  56 */     this.periodType = periodType;
/*     */   }
/*     */   public CompositePeriod getCompositePeriod() {
/*  59 */     return this.compositePeriod;
/*     */   }
/*     */   public void setCompositePeriod(CompositePeriod compositePeriod) {
/*  62 */     this.compositePeriod = compositePeriod;
/*     */   }
/*     */   public OfferSide getOfferSide() {
/*  65 */     return this.offerSide;
/*     */   }
/*     */   public void setOfferSide(OfferSide offerSide) {
/*  68 */     this.offerSide = offerSide;
/*     */   }
/*     */   public Long getDateFrom() {
/*  71 */     return this.dateFrom;
/*     */   }
/*     */   public void setDateFrom(Long dateFrom) {
/*  74 */     this.dateFrom = dateFrom;
/*     */   }
/*     */   public Long getDateTo() {
/*  77 */     return this.dateTo;
/*     */   }
/*     */   public void setDateTo(Long dateTo) {
/*  80 */     this.dateTo = dateTo;
/*     */   }
/*     */   public ExportFormat getExportFormat() {
/*  83 */     return this.exportFormat;
/*     */   }
/*     */   public void setExportFormat(ExportFormat exportFormat) {
/*  86 */     this.exportFormat = exportFormat;
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/*  90 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public void setPriceRange(PriceRange priceRange) {
/*  94 */     this.priceRange = priceRange;
/*     */   }
/*     */ 
/*     */   public JForexPeriod getPointAndFigure() {
/*  98 */     return this.pointAndFigure;
/*     */   }
/*     */ 
/*     */   public void setPointAndFigure(JForexPeriod pointAndFigure) {
/* 102 */     this.pointAndFigure = pointAndFigure;
/*     */   }
/*     */ 
/*     */   public Filter getFilter() {
/* 106 */     return this.filter;
/*     */   }
/*     */ 
/*     */   public void setFilter(Filter filter) {
/* 110 */     this.filter = filter;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.ExportInstrumentParameter
 * JD-Core Version:    0.6.0
 */