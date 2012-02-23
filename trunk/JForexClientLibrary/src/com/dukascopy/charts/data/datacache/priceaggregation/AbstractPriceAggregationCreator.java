/*     */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public abstract class AbstractPriceAggregationCreator<D extends AbstractPriceAggregationData, SD extends Data, L extends IPriceAggregationLiveFeedListener<D>>
/*     */   implements IPriceAggregationCreator<D, SD, L>
/*     */ {
/*  22 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*     */   private List<L> priceAggregationLiveFeedListener;
/*     */   protected D[] result;
/*     */   protected int lastElementIndex;
/*     */   private final int desiredBarsCount;
/*     */   private D lastFiredData;
/*     */   private final boolean liveCreation;
/*     */   private final boolean directOrder;
/*     */   private final Instrument instrument;
/*     */   private final OfferSide offerSide;
/*     */ 
/*     */   public AbstractPriceAggregationCreator(Instrument instrument, OfferSide offerSide, int desiredBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  45 */     this(instrument, offerSide, null, desiredBarsCount, liveCreation, directOrder);
/*     */   }
/*     */ 
/*     */   public AbstractPriceAggregationCreator(Instrument instrument, OfferSide offerSide, L listener, int desiredBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  63 */     this.instrument = instrument;
/*  64 */     this.offerSide = offerSide;
/*  65 */     this.priceAggregationLiveFeedListener = new ArrayList();
/*  66 */     this.desiredBarsCount = desiredBarsCount;
/*  67 */     this.liveCreation = liveCreation;
/*  68 */     this.directOrder = directOrder;
/*     */ 
/*  70 */     if (listener != null)
/*  71 */       addListener(listener);
/*     */   }
/*     */ 
/*     */   protected List<L> getPriceRangeLiveFeedListeners()
/*     */   {
/*  77 */     return this.priceAggregationLiveFeedListener;
/*     */   }
/*     */ 
/*     */   public void addListener(L listener)
/*     */   {
/*  82 */     getPriceRangeLiveFeedListeners().add(listener);
/*     */   }
/*     */ 
/*     */   public void removeListener(L listener)
/*     */   {
/*  87 */     getPriceRangeLiveFeedListeners().remove(listener);
/*     */   }
/*     */ 
/*     */   public boolean contains(L listener)
/*     */   {
/*  92 */     return getPriceRangeLiveFeedListeners().contains(listener);
/*     */   }
/*     */ 
/*     */   public void fireNewBarCreated(D data)
/*     */   {
/*  97 */     this.lastFiredData = data;
/*     */ 
/*  99 */     if (isLiveCreation())
/* 100 */       for (IPriceAggregationLiveFeedListener listener : getPriceRangeLiveFeedListeners())
/* 101 */         listener.newPriceData(data);
/*     */   }
/*     */ 
/*     */   public D getFirstData()
/*     */   {
/* 109 */     if (getLastElementIndex() > -1) {
/* 110 */       return getResult()[0];
/*     */     }
/* 112 */     return null;
/*     */   }
/*     */ 
/*     */   public D getLastData()
/*     */   {
/* 117 */     if ((getResult() != null) && (getLastElementIndex() > -1) && (getLastElementIndex() < getResult().length))
/*     */     {
/* 122 */       return getResult()[getLastElementIndex()];
/*     */     }
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */   public D[] getResult()
/*     */   {
/* 129 */     return this.result;
/*     */   }
/*     */ 
/*     */   public int getLastElementIndex()
/*     */   {
/* 134 */     return this.lastElementIndex;
/*     */   }
/*     */ 
/*     */   public int getDesiredDatasCount()
/*     */   {
/* 139 */     return this.desiredBarsCount;
/*     */   }
/*     */ 
/*     */   public int getLoadedElementsNumber()
/*     */   {
/* 144 */     return getLastElementIndex() + 1;
/*     */   }
/*     */ 
/*     */   protected double round(double value) {
/* 148 */     return StratUtils.round(value, 10);
/*     */   }
/*     */ 
/*     */   public D getLastFiredData()
/*     */   {
/* 153 */     return this.lastFiredData;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/* 157 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide() {
/* 161 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   protected double getPrice(TickData tickData) {
/* 165 */     if (tickData == null) {
/* 166 */       return 4.9E-324D;
/*     */     }
/*     */ 
/* 169 */     if (OfferSide.ASK.equals(this.offerSide)) {
/* 170 */       return tickData.getAsk();
/*     */     }
/* 172 */     if (OfferSide.BID.equals(this.offerSide)) {
/* 173 */       return tickData.getBid();
/*     */     }
/*     */ 
/* 176 */     return 4.9E-324D;
/*     */   }
/*     */ 
/*     */   protected double getVolume(TickData tickData) {
/* 180 */     if (tickData == null) {
/* 181 */       return 4.9E-324D;
/*     */     }
/*     */ 
/* 184 */     if (OfferSide.ASK.equals(this.offerSide)) {
/* 185 */       return tickData.getAskVolume();
/*     */     }
/* 187 */     if (OfferSide.BID.equals(this.offerSide)) {
/* 188 */       return tickData.getBidVolume();
/*     */     }
/*     */ 
/* 191 */     return 4.9E-324D;
/*     */   }
/*     */ 
/*     */   public boolean isLiveCreation() {
/* 195 */     return this.liveCreation;
/*     */   }
/*     */ 
/*     */   public boolean isDirectOrder() {
/* 199 */     return this.directOrder;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  24 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationCreator
 * JD-Core Version:    0.6.0
 */