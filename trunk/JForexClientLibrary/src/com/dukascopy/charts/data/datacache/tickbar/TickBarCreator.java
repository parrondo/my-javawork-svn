/*     */ package com.dukascopy.charts.data.datacache.tickbar;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationCreator;
/*     */ 
/*     */ public class TickBarCreator extends AbstractPriceAggregationCreator<TickBarData, TickData, ITickBarLiveFeedListener>
/*     */   implements ITickBarCreator
/*     */ {
/*     */   protected int fullyCreatedBarsCount;
/*     */   private final TickBarSize tickBarSize;
/*     */   protected int currentDataConstructionTicksIncluded;
/*     */   private int ticksInOneBar;
/*     */ 
/*     */   public TickBarCreator(Instrument instrument, TickBarSize tickBarSize, OfferSide offerSide, int desiredBarsCount, boolean directOrder, boolean liveCreation, ITickBarLiveFeedListener listener)
/*     */   {
/*  31 */     super(instrument, offerSide, listener, desiredBarsCount, liveCreation, directOrder);
/*     */ 
/*  39 */     this.tickBarSize = tickBarSize;
/*  40 */     this.ticksInOneBar = tickBarSize.getSize();
/*     */ 
/*  42 */     reset();
/*     */   }
/*     */ 
/*     */   public boolean isAllDesiredDataLoaded()
/*     */   {
/*  47 */     return this.fullyCreatedBarsCount >= getDesiredDatasCount();
/*     */   }
/*     */ 
/*     */   public boolean analyse(TickData data)
/*     */   {
/*  52 */     TickBarData currentBar = null;
/*  53 */     if (this.currentDataConstructionTicksIncluded == 0) {
/*  54 */       currentBar = startNewBar(data);
/*     */     }
/*     */     else {
/*  57 */       currentBar = (TickBarData)getLastData();
/*  58 */       if (canContinueCurrentDataConstruction(currentBar, data)) {
/*  59 */         currentBar = continueCurrentBar(currentBar, data);
/*     */       }
/*     */       else {
/*  62 */         currentBar = finishCurrentBar(currentBar, data);
/*     */ 
/*  64 */         if (isAllDesiredDataLoaded()) {
/*  65 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */   private TickBarData finishCurrentBar(TickBarData currentBar, TickData data)
/*     */   {
/*  76 */     currentBar = continueCurrentBar(currentBar, data);
/*  77 */     this.fullyCreatedBarsCount += 1;
/*     */ 
/*  79 */     fireNewBarCreated(currentBar);
/*     */ 
/*  81 */     currentBar = null;
/*  82 */     this.currentDataConstructionTicksIncluded = 0;
/*  83 */     return currentBar;
/*     */   }
/*     */ 
/*     */   private TickBarData startNewBar(TickData data) {
/*  87 */     this.currentDataConstructionTicksIncluded += 1;
/*     */ 
/*  89 */     double price = getPrice(data);
/*  90 */     double volume = getVolume(data);
/*     */ 
/*  93 */     TickBarData currentBar = new TickBarData();
/*  94 */     currentBar.setOpen(price);
/*  95 */     currentBar.setHigh(price);
/*  96 */     currentBar.setLow(price);
/*  97 */     currentBar.setClose(price);
/*  98 */     currentBar.setTime(data.time);
/*  99 */     currentBar.setEndTime(data.time);
/* 100 */     currentBar.setVolume(volume);
/* 101 */     currentBar.setFormedElementsCount(1L);
/*     */ 
/* 103 */     this.lastElementIndex += 1;
/* 104 */     ((TickBarData[])this.result)[getLastElementIndex()] = currentBar;
/*     */ 
/* 106 */     return currentBar;
/*     */   }
/*     */ 
/*     */   private TickBarData continueCurrentBar(TickBarData currentBar, TickData data)
/*     */   {
/* 113 */     this.currentDataConstructionTicksIncluded += 1;
/*     */ 
/* 115 */     double price = getPrice(data);
/* 116 */     double volume = getVolume(data);
/* 117 */     currentBar.setVolume(round(currentBar.getVolume() + volume));
/* 118 */     currentBar.setFormedElementsCount(currentBar.getFormedElementsCount() + 1L);
/*     */ 
/* 120 */     if (isDirectOrder()) {
/* 121 */       currentBar.setEndTime(data.time);
/* 122 */       currentBar.setClose(price);
/*     */     }
/*     */     else {
/* 125 */       currentBar.setTime(data.time);
/* 126 */       currentBar.setOpen(price);
/*     */     }
/*     */ 
/* 129 */     if (currentBar.getLow() > price) {
/* 130 */       currentBar.setLow(price);
/*     */     }
/* 132 */     else if (currentBar.getHigh() < price) {
/* 133 */       currentBar.setHigh(price);
/*     */     }
/*     */ 
/* 136 */     return currentBar;
/*     */   }
/*     */ 
/*     */   public boolean canContinueCurrentDataConstruction(TickBarData currentData, TickData sourceData)
/*     */   {
/* 141 */     boolean canContinueByTickCount = this.currentDataConstructionTicksIncluded + 1 < this.ticksInOneBar;
/* 142 */     if (!canContinueByTickCount) {
/* 143 */       return false;
/*     */     }
/* 145 */     if (sourceData == null) {
/* 146 */       return false;
/*     */     }
/* 148 */     return DataCacheUtils.isTheSameTradingSession(currentData.getTime(), sourceData.getTime());
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 153 */     resetResulArray();
/* 154 */     this.lastElementIndex = -1;
/* 155 */     this.currentDataConstructionTicksIncluded = 0;
/* 156 */     this.fullyCreatedBarsCount = 0;
/*     */   }
/*     */ 
/*     */   protected void resetResulArray() {
/* 160 */     this.result = new TickBarData[getDesiredDatasCount()];
/*     */   }
/*     */ 
/*     */   public void setupLastData(TickBarData data)
/*     */   {
/* 165 */     ((TickBarData[])getResult())[getLastElementIndex()] = data;
/*     */   }
/*     */ 
/*     */   public TickBarData getLastCompletedData()
/*     */   {
/* 170 */     if (0 < this.fullyCreatedBarsCount) {
/* 171 */       return ((TickBarData[])getResult())[(this.fullyCreatedBarsCount - 1)];
/*     */     }
/* 173 */     return null;
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize() {
/* 177 */     return this.tickBarSize;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.TickBarCreator
 * JD-Core Version:    0.6.0
 */