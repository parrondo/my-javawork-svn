/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IConsole;
/*     */ import com.dukascopy.api.IHistory;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.feed.FeedDescriptor;
/*     */ import com.dukascopy.api.feed.IFeedDescriptor;
/*     */ import com.dukascopy.api.indicators.IIndicatorContext;
/*     */ import com.dukascopy.api.indicators.IIndicatorsProvider;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.dds2.greed.agent.indicator.AccountProvider;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ 
/*     */ public class IndicatorContext
/*     */   implements IIndicatorContext
/*     */ {
/*     */   private IConsole console;
/*     */   private History history;
/*     */   private IFeedDescriptor feedDescriptor;
/*     */ 
/*     */   public IndicatorContext(INotificationUtils notificationUtils, History history)
/*     */   {
/*  29 */     this.history = history;
/*  30 */     this.console = new NotificationConsoleImpl(notificationUtils);
/*     */   }
/*     */ 
/*     */   public IAccount getAccount() {
/*  34 */     return AccountProvider.getAccount();
/*     */   }
/*     */ 
/*     */   public IHistory getHistory() {
/*  38 */     return this.history;
/*     */   }
/*     */ 
/*     */   public IConsole getConsole() {
/*  42 */     return this.console;
/*     */   }
/*     */ 
/*     */   public IIndicatorsProvider getIndicatorsProvider() {
/*  46 */     return IndicatorsProvider.getInstance();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setChartInfo(Instrument instrument, Period period, OfferSide offerSide)
/*     */   {
/*  57 */     this.feedDescriptor = new FeedDescriptor();
/*     */ 
/*  59 */     if (Period.TICK.equals(period)) {
/*  60 */       this.feedDescriptor.setDataType(DataType.TICKS);
/*     */     }
/*     */     else {
/*  63 */       this.feedDescriptor.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*     */     }
/*     */ 
/*  66 */     this.feedDescriptor.setInstrument(instrument);
/*  67 */     this.feedDescriptor.setPeriod(period);
/*  68 */     this.feedDescriptor.setOfferSide(offerSide);
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/*  72 */     Instrument result = getFeedDescriptor() == null ? null : getFeedDescriptor().getInstrument();
/*     */ 
/*  75 */     return result;
/*     */   }
/*     */ 
/*     */   public Period getPeriod() {
/*  79 */     Period result = getFeedDescriptor() == null ? null : getFeedDescriptor().getPeriod();
/*     */ 
/*  82 */     return result;
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide() {
/*  86 */     OfferSide result = getFeedDescriptor() == null ? null : getFeedDescriptor().getOfferSide();
/*     */ 
/*  89 */     return result;
/*     */   }
/*     */ 
/*     */   public IFeedDescriptor getFeedDescriptor()
/*     */   {
/*  94 */     return this.feedDescriptor;
/*     */   }
/*     */ 
/*     */   public void setFeedDescriptor(IFeedDescriptor feedDescriptor) {
/*  98 */     this.feedDescriptor = feedDescriptor;
/*     */   }
/*     */ 
/*     */   public void resetFeedDescriptor() {
/* 102 */     this.feedDescriptor = new FeedDescriptor();
/* 103 */     this.feedDescriptor.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.IndicatorContext
 * JD-Core Version:    0.6.0
 */