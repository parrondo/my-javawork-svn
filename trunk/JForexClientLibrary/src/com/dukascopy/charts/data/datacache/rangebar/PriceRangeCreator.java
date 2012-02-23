/*     */ package com.dukascopy.charts.data.datacache.rangebar;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractFantomablePriceAggregationCreator;
/*     */ 
/*     */ public class PriceRangeCreator extends AbstractFantomablePriceAggregationCreator<PriceRangeData, TickData, IPriceRangeLiveFeedListener>
/*     */   implements IPriceRangeCreator
/*     */ {
/*     */   private PriceRange priceRange;
/*     */   private int loadedPriceRangeCount;
/*     */   private double range;
/*     */ 
/*     */   public PriceRangeCreator(Instrument instrument, PriceRange priceRange, OfferSide offerSide, int desiredPriceRangesCount, boolean liveCreation, boolean directOrder, IPriceRangeLiveFeedListener priceRangeLiveFeedListener)
/*     */   {
/*  32 */     super(instrument, offerSide, priceRangeLiveFeedListener, desiredPriceRangesCount, liveCreation, directOrder);
/*     */ 
/*  40 */     this.priceRange = priceRange;
/*     */ 
/*  42 */     reset();
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  46 */     resetResulArray();
/*  47 */     this.lastElementIndex = -1;
/*  48 */     this.loadedPriceRangeCount = 0;
/*  49 */     this.range = getRangeValue(getPriceRange(), getInstrument());
/*     */   }
/*     */ 
/*     */   protected void resetResulArray() {
/*  53 */     this.result = new PriceRangeData[getDesiredDatasCount()];
/*     */   }
/*     */ 
/*     */   public boolean checkPriceMatch(PriceRangeData currentPriceRange, double price, double range) {
/*  57 */     return (round(Math.abs(currentPriceRange.getLow() - price)) <= range) && (round(Math.abs(currentPriceRange.getHigh() - price)) <= range);
/*     */   }
/*     */ 
/*     */   private boolean canContinueCurrentDataConstruction(PriceRangeData currentPriceRange, double price, long nextTickTime) {
/*  61 */     boolean canContinueByPrice = checkPriceMatch(currentPriceRange, price, getRange());
/*  62 */     if (!canContinueByPrice) {
/*  63 */       return false;
/*     */     }
/*  65 */     return DataCacheUtils.isTheSameTradingSession(currentPriceRange.getTime(), nextTickTime);
/*     */   }
/*     */ 
/*     */   public boolean canContinueCurrentDataConstruction(PriceRangeData currentPriceRange, TickData tickData) {
/*  69 */     if (currentPriceRange == null) {
/*  70 */       return false;
/*     */     }
/*  72 */     double price = getPrice(tickData);
/*  73 */     return canContinueCurrentDataConstruction(currentPriceRange, price, tickData.getTime());
/*     */   }
/*     */ 
/*     */   public boolean analyse(TickData tickData)
/*     */   {
/*  78 */     if (tickData == null) {
/*  79 */       return false;
/*     */     }
/*     */ 
/*  82 */     PriceRangeData currentPriceRange = getLastElementIndex() < 0 ? null : ((PriceRangeData[])this.result)[getLastElementIndex()];
/*     */ 
/*  84 */     double price = getPrice(tickData);
/*  85 */     double volume = getVolume(tickData);
/*  86 */     long time = tickData.getTime();
/*     */ 
/*  88 */     if (currentPriceRange == null)
/*     */     {
/*  92 */       currentPriceRange = createPriceRange(price, volume, time);
/*  93 */       setLastElementIndex(0);
/*  94 */       ((PriceRangeData[])this.result)[getLastElementIndex()] = currentPriceRange;
/*     */     }
/*  96 */     else if (canContinueCurrentDataConstruction(currentPriceRange, price, time))
/*     */     {
/* 100 */       currentPriceRange = continuePriceRange(currentPriceRange, price, volume, time);
/*     */     }
/*     */     else
/*     */     {
/* 107 */       PriceRangeData[] phantomPriceRanges = (PriceRangeData[])checkAndCreatePhantomBars(currentPriceRange, tickData, getRange());
/*     */ 
/* 109 */       for (PriceRangeData prd : phantomPriceRanges) {
/* 110 */         if (prd != currentPriceRange)
/*     */         {
/* 114 */           setLastElementIndex(getLastElementIndex() + 1);
/* 115 */           ((PriceRangeData[])this.result)[getLastElementIndex()] = prd;
/*     */         }
/*     */ 
/* 118 */         this.loadedPriceRangeCount += 1;
/* 119 */         fireNewBarCreated(prd);
/*     */ 
/* 121 */         if (isAllDesiredDataLoaded())
/*     */         {
/* 125 */           return true;
/*     */         }
/*     */       }
/*     */ 
/* 129 */       currentPriceRange = createPriceRange(price, volume, time);
/*     */ 
/* 131 */       setLastElementIndex(getLastElementIndex() + 1);
/* 132 */       ((PriceRangeData[])this.result)[getLastElementIndex()] = currentPriceRange;
/*     */     }
/*     */ 
/* 135 */     return false;
/*     */   }
/*     */ 
/*     */   private double getRangeValue(PriceRange priceRange, Instrument instrument)
/*     */   {
/* 140 */     double result = instrument.getPipValue() * priceRange.getPipCount();
/* 141 */     return round(result);
/*     */   }
/*     */ 
/*     */   private PriceRangeData continuePriceRange(PriceRangeData priceRange, double price, double volume, long time)
/*     */   {
/* 150 */     return continueBar(priceRange, price, new Double(volume), new Long(time), 1L);
/*     */   }
/*     */ 
/*     */   protected PriceRangeData continueBar(PriceRangeData priceRange, double price, Double volume, Long time, long incTicksCount)
/*     */   {
/* 161 */     if (price > priceRange.getHigh()) {
/* 162 */       priceRange.setHigh(price);
/*     */     }
/* 164 */     if (price < priceRange.getLow()) {
/* 165 */       priceRange.setLow(price);
/*     */     }
/*     */ 
/* 168 */     if (volume != null) {
/* 169 */       priceRange.setVolume(round(priceRange.getVolume() + volume.doubleValue()));
/*     */     }
/*     */ 
/* 172 */     priceRange.setFormedElementsCount(priceRange.getFormedElementsCount() + incTicksCount);
/*     */ 
/* 174 */     if (isDirectOrder()) {
/* 175 */       if (time != null) {
/* 176 */         priceRange.setEndTime(time.longValue());
/*     */       }
/* 178 */       priceRange.setClose(price);
/*     */     }
/*     */     else {
/* 181 */       if (time != null) {
/* 182 */         priceRange.time = time.longValue();
/* 183 */         priceRange.setTime(time.longValue());
/*     */       }
/* 185 */       priceRange.setOpen(price);
/*     */     }
/*     */ 
/* 189 */     return priceRange;
/*     */   }
/*     */ 
/*     */   private PriceRangeData createPriceRange(double price, double volume, long time)
/*     */   {
/* 197 */     long ticksCount = 1L;
/*     */ 
/* 199 */     return createBar(price, volume, time, ticksCount, null);
/*     */   }
/*     */ 
/*     */   protected PriceRangeData createBar(double price, double volume, long time, long ticksCount, PriceRangeData previousBar)
/*     */   {
/* 211 */     PriceRangeData priceRange = new PriceRangeData();
/*     */ 
/* 213 */     priceRange.setOpen(price);
/* 214 */     priceRange.setHigh(price);
/* 215 */     priceRange.setLow(price);
/* 216 */     priceRange.setClose(price);
/* 217 */     priceRange.setVolume(volume);
/* 218 */     priceRange.setTime(time);
/* 219 */     priceRange.setEndTime(time);
/* 220 */     priceRange.setFormedElementsCount(ticksCount);
/*     */ 
/* 222 */     return priceRange;
/*     */   }
/*     */ 
/*     */   public boolean isAllDesiredDataLoaded()
/*     */   {
/* 229 */     return getLoadedPriceRangeCount() >= getDesiredDatasCount();
/*     */   }
/*     */ 
/*     */   public int getLoadedPriceRangeCount() {
/* 233 */     return this.loadedPriceRangeCount;
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 237 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   protected void setLastElementIndex(int lastElementIndex) {
/* 241 */     this.lastElementIndex = lastElementIndex;
/*     */   }
/*     */ 
/*     */   protected void setLoadedPriceRangeCount(int loadedPriceRangeCount) {
/* 245 */     this.loadedPriceRangeCount = loadedPriceRangeCount;
/*     */   }
/*     */ 
/*     */   public double getRange() {
/* 249 */     return this.range;
/*     */   }
/*     */ 
/*     */   public void setupLastData(PriceRangeData data)
/*     */   {
/* 254 */     ((PriceRangeData[])getResult())[getLastElementIndex()] = data;
/*     */   }
/*     */ 
/*     */   public PriceRangeData getLastCompletedData()
/*     */   {
/* 259 */     return (PriceRangeData)getLastData();
/*     */   }
/*     */ 
/*     */   protected PriceRangeData[] createArray(PriceRangeData bar)
/*     */   {
/* 264 */     return new PriceRangeData[] { bar };
/*     */   }
/*     */ 
/*     */   protected PriceRangeData[] createArray(int size)
/*     */   {
/* 269 */     return new PriceRangeData[size];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.PriceRangeCreator
 * JD-Core Version:    0.6.0
 */