/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MainDataOperationManager
/*     */   implements IDataOperationManager
/*     */ {
/*  12 */   private static final Logger LOGGER = LoggerFactory.getLogger(MainDataOperationManager.class);
/*     */ 
/*  14 */   private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*     */   private final ChartState chartState;
/*     */   private final IDataOperationManagerStrategy tickDataOperationManagerStrategy;
/*     */   private final IDataOperationManagerStrategy candleDataOperationManagerStrategy;
/*     */   private final IDataOperationManagerStrategy priceRangeDataOperationManagerStrategy;
/*     */   private final IDataOperationManagerStrategy pointAndFigureDataOperationManagerStrategy;
/*     */   private final IDataOperationManagerStrategy tickBarDataOperationManagerStrategy;
/*     */   private final IDataOperationManagerStrategy renkoDataOperationManagerStrategy;
/*     */ 
/*     */   public MainDataOperationManager(ChartState chartState, IDataOperationManagerStrategy tickDataOperationManagerStrategy, IDataOperationManagerStrategy candleDataOperationManagerStrategy, IDataOperationManagerStrategy priceRangeDataOperationManagerStrategy, IDataOperationManagerStrategy pointAndFigureDataOperationManagerStrategy, IDataOperationManagerStrategy tickBarDataOperationManagerStrategy, IDataOperationManagerStrategy renkoDataOperationManagerStrategy)
/*     */   {
/*  36 */     this.chartState = chartState;
/*  37 */     this.tickDataOperationManagerStrategy = tickDataOperationManagerStrategy;
/*  38 */     this.candleDataOperationManagerStrategy = candleDataOperationManagerStrategy;
/*  39 */     this.priceRangeDataOperationManagerStrategy = priceRangeDataOperationManagerStrategy;
/*  40 */     this.pointAndFigureDataOperationManagerStrategy = pointAndFigureDataOperationManagerStrategy;
/*  41 */     this.tickBarDataOperationManagerStrategy = tickBarDataOperationManagerStrategy;
/*  42 */     this.renkoDataOperationManagerStrategy = renkoDataOperationManagerStrategy;
/*     */   }
/*     */ 
/*     */   public void ordersChanged(long from, long to)
/*     */   {
/*  47 */     if (LOGGER.isTraceEnabled()) {
/*  48 */       LOGGER.trace("orders data set changed(" + DATE_FORMATTER.format(Long.valueOf(from)) + ", " + DATE_FORMATTER.format(Long.valueOf(to)) + ")");
/*     */     }
/*     */ 
/*  51 */     getOperationManager().orderChanged(from, to);
/*     */   }
/*     */ 
/*     */   public boolean dataChanged(long from, long to)
/*     */   {
/*  56 */     boolean shouldBeRefreshed = getOperationManager().dataChanged(from, to);
/*  57 */     if ((shouldBeRefreshed) && 
/*  58 */       (LOGGER.isTraceEnabled())) {
/*  59 */       LOGGER.trace("main data set changed: " + this.chartState.getInstrument() + ", " + this.chartState.getPeriod() + ", " + this.chartState.getOfferSide() + " (" + DATE_FORMATTER.format(Long.valueOf(from)) + ", " + DATE_FORMATTER.format(Long.valueOf(to)) + ")");
/*     */     }
/*     */ 
/*  62 */     return shouldBeRefreshed;
/*     */   }
/*     */ 
/*     */   public void indicatorAdded(int indicatorId)
/*     */   {
/*  67 */     if (LOGGER.isTraceEnabled()) {
/*  68 */       LOGGER.trace("main indicator added: id = " + indicatorId);
/*     */     }
/*  70 */     getOperationManager().mainIndicatorAdded(indicatorId);
/*     */   }
/*     */ 
/*     */   public void indicatorEdited(int indicatorId)
/*     */   {
/*  75 */     if (LOGGER.isTraceEnabled()) {
/*  76 */       LOGGER.trace("main indicator edited: id = " + indicatorId);
/*     */     }
/*  78 */     getOperationManager().mainIndicatorEdited(indicatorId);
/*     */   }
/*     */ 
/*     */   public void subIndicatorAdded(int indicatorId)
/*     */   {
/*  83 */     if (LOGGER.isTraceEnabled()) {
/*  84 */       LOGGER.trace("sub indicator added: id = " + indicatorId);
/*     */     }
/*  86 */     getOperationManager().subIndicatorAdded(indicatorId);
/*     */   }
/*     */ 
/*     */   public void subIndicatorEdited(int indicatorId)
/*     */   {
/*  91 */     if (LOGGER.isTraceEnabled()) {
/*  92 */       LOGGER.trace("sub indicator edited: id = " + indicatorId);
/*     */     }
/*  94 */     getOperationManager().subIndicatorEdited(indicatorId);
/*     */   }
/*     */ 
/*     */   private IDataOperationManagerStrategy getOperationManager() {
/*  98 */     DataType dataType = this.chartState.getDataType();
/*  99 */     switch (1.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) { case 1:
/* 100 */       return this.tickDataOperationManagerStrategy;
/*     */     case 2:
/* 101 */       return this.candleDataOperationManagerStrategy;
/*     */     case 3:
/* 102 */       return this.priceRangeDataOperationManagerStrategy;
/*     */     case 4:
/* 103 */       return this.pointAndFigureDataOperationManagerStrategy;
/*     */     case 5:
/* 104 */       return this.tickBarDataOperationManagerStrategy;
/*     */     case 6:
/* 105 */       return this.renkoDataOperationManagerStrategy; }
/* 106 */     throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  16 */     DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MainDataOperationManager
 * JD-Core Version:    0.6.0
 */