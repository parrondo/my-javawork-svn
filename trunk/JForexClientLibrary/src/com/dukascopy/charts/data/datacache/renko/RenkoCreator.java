/*     */ package com.dukascopy.charts.data.datacache.renko;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractFantomablePriceAggregationCreator;
/*     */ 
/*     */ public class RenkoCreator extends AbstractFantomablePriceAggregationCreator<RenkoData, TickData, IRenkoLiveFeedListener>
/*     */   implements IRenkoCreator
/*     */ {
/*     */   private final PriceRange brickSize;
/*     */   private final boolean directOrder;
/*     */   private int loadedBarsCount;
/*     */   private final double range;
/*     */ 
/*     */   public RenkoCreator(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int desiredBarsCount, boolean directOrder, boolean liveCreation)
/*     */   {
/*  32 */     super(instrument, offerSide, desiredBarsCount, liveCreation, directOrder);
/*     */ 
/*  39 */     this.brickSize = brickSize;
/*  40 */     this.directOrder = directOrder;
/*  41 */     this.range = round(brickSize.getPipCount() * getInstrument().getPipValue());
/*  42 */     reset();
/*     */   }
/*     */ 
/*     */   protected void resetResulArray() {
/*  46 */     this.result = new RenkoData[getDesiredDatasCount()];
/*     */   }
/*     */ 
/*     */   public boolean analyse(TickData tickData)
/*     */   {
/*  52 */     if (tickData == null) {
/*  53 */       return false;
/*     */     }
/*     */ 
/*  56 */     RenkoData currentRenko = getLastElementIndex() < 0 ? null : ((RenkoData[])this.result)[getLastElementIndex()];
/*     */ 
/*  58 */     double price = getPrice(tickData);
/*  59 */     double volume = getVolume(tickData);
/*  60 */     long time = tickData.getTime();
/*     */ 
/*  62 */     if (currentRenko == null)
/*     */     {
/*  66 */       currentRenko = createBar(price, volume, time);
/*  67 */       setLastElementIndex(0);
/*  68 */       ((RenkoData[])this.result)[getLastElementIndex()] = currentRenko;
/*     */     }
/*  70 */     else if (canContinueCurrentDataConstruction(currentRenko, price, time))
/*     */     {
/*  74 */       currentRenko = continueBar(currentRenko, price, volume, time);
/*     */     }
/*     */     else
/*     */     {
/*  81 */       RenkoData nextRenko = currentRenko.getInProgressRenko();
/*     */ 
/*  83 */       RenkoData[] phantomBars = (RenkoData[])checkAndCreatePhantomBars(currentRenko, tickData, getRange());
/*     */ 
/*  85 */       boolean phantomsContainsInProgresBar = phantomsContainsInProgresBar(phantomBars, nextRenko);
/*  86 */       if ((!phantomsContainsInProgresBar) && (nextRenko != null)) {
/*  87 */         addInProgressRenkoValuesToTarget(currentRenko, nextRenko);
/*  88 */         nextRenko = null;
/*     */       }
/*     */ 
/*  91 */       for (int i = 0; i < phantomBars.length; i++) {
/*  92 */         RenkoData renko = phantomBars[i];
/*     */ 
/*  94 */         if (renko != currentRenko) {
/*  95 */           if ((i == 1) && 
/*  96 */             (nextRenko != null) && (renko.getLow() == nextRenko.getLow()) && (renko.getHigh() == nextRenko.getHigh()))
/*     */           {
/* 101 */             addInProgressRenkoValuesToTarget(renko, nextRenko);
/* 102 */             nextRenko = null;
/*     */           }
/*     */ 
/* 108 */           setLastElementIndex(getLastElementIndex() + 1);
/* 109 */           ((RenkoData[])this.result)[getLastElementIndex()] = renko;
/*     */         }
/*     */ 
/* 112 */         boolean canContinue = canContinueCurrentDataConstruction(renko, price, time);
/* 113 */         if (!canContinue) {
/* 114 */           finishRenko(renko);
/*     */         }
/*     */ 
/* 117 */         if (isAllDesiredDataLoaded())
/*     */         {
/* 121 */           return true;
/*     */         }
/*     */       }
/*     */ 
/* 125 */       if (!canContinueCurrentDataConstruction(phantomBars[(phantomBars.length - 1)], price, time)) {
/* 126 */         if (nextRenko != null) {
/* 127 */           currentRenko = nextRenko;
/*     */         }
/*     */         else {
/* 130 */           currentRenko = createBar(price, volume, time, 1L, phantomBars[(phantomBars.length - 1)]);
/*     */         }
/* 132 */         setLastElementIndex(getLastElementIndex() + 1);
/* 133 */         ((RenkoData[])this.result)[getLastElementIndex()] = currentRenko;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean phantomsContainsInProgresBar(RenkoData[] phantomBars, RenkoData bar)
/*     */   {
/* 145 */     if ((bar == null) || (phantomBars == null)) {
/* 146 */       return false;
/*     */     }
/*     */ 
/* 149 */     for (int i = 0; i < phantomBars.length; i++) {
/* 150 */       RenkoData renko = phantomBars[i];
/* 151 */       if (renko == null) {
/*     */         continue;
/*     */       }
/* 154 */       if ((renko.getLow() == bar.getLow()) && (renko.getHigh() == bar.getHigh()))
/*     */       {
/* 158 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 162 */     return false;
/*     */   }
/*     */ 
/*     */   private void addInProgressRenkoValuesToTarget(RenkoData target, RenkoData inProgressRenko) {
/* 166 */     target.setVolume(round(target.getVolume() + inProgressRenko.getVolume()));
/* 167 */     target.setFormedElementsCount(target.getFormedElementsCount() + inProgressRenko.getFormedElementsCount());
/*     */   }
/*     */ 
/*     */   private RenkoData finishRenko(RenkoData renko) {
/* 171 */     this.loadedBarsCount += 1;
/* 172 */     renko.setInProgressBar(null);
/* 173 */     fireNewBarCreated(renko);
/* 174 */     return renko;
/*     */   }
/*     */ 
/*     */   private RenkoData continueBar(RenkoData currentRenko, double price, double volume, long time)
/*     */   {
/* 183 */     currentRenko = continueBar(currentRenko, price, new Double(volume), new Long(time), 1L);
/* 184 */     return currentRenko;
/*     */   }
/*     */ 
/*     */   protected RenkoData continueBar(RenkoData bar, double price, Double volume, Long time, long ticksCount)
/*     */   {
/* 195 */     boolean canContinueCurrent = isPriceInCurrentRenko(bar, price);
/* 196 */     if (canContinueCurrent) {
/* 197 */       if (bar.getInProgressBar() != null) {
/* 198 */         addInProgressRenkoValuesToTarget(bar, bar.getInProgressRenko());
/* 199 */         bar.setInProgressBar(null);
/*     */       }
/* 201 */       updateRenko(bar, price, volume, time, ticksCount);
/*     */     }
/* 204 */     else if (bar.getInProgressBar() == null) {
/* 205 */       RenkoData inProgressRenko = createBar(price, volume.doubleValue(), time.longValue(), ticksCount, bar);
/* 206 */       bar.setInProgressBar(inProgressRenko);
/*     */     }
/*     */     else {
/* 209 */       updateRenko(bar.getInProgressRenko(), price, volume, time, ticksCount);
/*     */     }
/*     */ 
/* 213 */     return bar;
/*     */   }
/*     */ 
/*     */   protected double getPhantomOpenPrice(RenkoData previousBar, double tickPrice, double range)
/*     */   {
/* 222 */     if (previousBar.getHigh() < tickPrice) {
/* 223 */       return previousBar.getHigh();
/*     */     }
/*     */ 
/* 226 */     return previousBar.getLow();
/*     */   }
/*     */ 
/*     */   private void updateRenko(RenkoData currentRenko, double price, Double volume, Long time, long ticksCount)
/*     */   {
/* 237 */     if (isDirectOrder()) {
/* 238 */       currentRenko.setClose(price);
/* 239 */       if (time != null)
/* 240 */         currentRenko.setEndTime(time.longValue());
/*     */     }
/*     */     else
/*     */     {
/* 244 */       currentRenko.setOpen(price);
/* 245 */       if (time != null) {
/* 246 */         currentRenko.setTime(time.longValue());
/*     */       }
/*     */     }
/*     */ 
/* 250 */     if (volume != null) {
/* 251 */       currentRenko.setVolume(round(currentRenko.getVolume() + volume.doubleValue()));
/*     */     }
/*     */ 
/* 254 */     currentRenko.setFormedElementsCount(currentRenko.getFormedElementsCount() + ticksCount);
/*     */   }
/*     */ 
/*     */   private boolean canContinueCurrentDataConstruction(RenkoData currentBar, double nextTickPrice, long nextTickTime)
/*     */   {
/* 262 */     boolean result = isPriceAlreadyFormedNewRenko(currentBar, nextTickPrice);
/* 263 */     if (result) {
/* 264 */       return false;
/*     */     }
/* 266 */     result = DataCacheUtils.isTheSameTradingSession(currentBar.getTime(), nextTickTime);
/* 267 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isPriceAlreadyFormedNewRenko(RenkoData renko, double price) {
/* 271 */     double brickSizeInPrice = getRange();
/*     */ 
/* 273 */     if (price > renko.getHigh()) {
/* 274 */       double highDiff = round(Math.abs(renko.getHigh() - price));
/*     */ 
/* 276 */       if (highDiff >= brickSizeInPrice) {
/* 277 */         return true;
/*     */       }
/*     */     }
/* 280 */     else if (price < renko.getLow()) {
/* 281 */       double lowDiff = round(Math.abs(renko.getLow() - price));
/*     */ 
/* 283 */       if (lowDiff >= brickSizeInPrice) {
/* 284 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 289 */     return false;
/*     */   }
/*     */ 
/*     */   private double getRange() {
/* 293 */     return this.range;
/*     */   }
/*     */ 
/*     */   private boolean isPriceInCurrentRenko(RenkoData renko, double price) {
/* 297 */     boolean result = (renko.getLow() <= price) && (price <= renko.getHigh());
/* 298 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean canContinueCurrentDataConstruction(RenkoData currentData, TickData nextTick)
/*     */   {
/* 306 */     double price = getPrice(nextTick);
/* 307 */     boolean result = canContinueCurrentDataConstruction(currentData, price, currentData.getTime());
/* 308 */     return result;
/*     */   }
/*     */ 
/*     */   private RenkoData createBar(double price, double volume, long time) {
/* 312 */     RenkoData renko = createBar(price, volume, time, 1L, null);
/* 313 */     return renko;
/*     */   }
/*     */ 
/*     */   protected RenkoData createBar(double price, double volume, long time, long ticksCount, RenkoData previousRenko)
/*     */   {
/* 324 */     int upTrend = 0;
/*     */ 
/* 329 */     double brickSizeInPrice = this.brickSize.getPipCount() * getInstrument().getPipValue();
/*     */     double low;
/*     */     double low;
/*     */     double high;
/* 331 */     if (previousRenko != null)
/*     */     {
/*     */       double high;
/* 332 */       if (previousRenko.getHigh() <= price) {
/* 333 */         double low = previousRenko.getHigh();
/* 334 */         high = round(low + brickSizeInPrice);
/*     */       }
/*     */       else {
/* 337 */         double high = previousRenko.getLow();
/* 338 */         low = round(high - brickSizeInPrice);
/*     */       }
/*     */     }
/*     */     else {
/* 342 */       double pricePosition = price / brickSizeInPrice;
/* 343 */       int brickNumber = (int)pricePosition + upTrend;
/*     */ 
/* 345 */       low = round(brickNumber * brickSizeInPrice);
/* 346 */       high = round(low + brickSizeInPrice);
/*     */     }
/*     */ 
/* 349 */     RenkoData renko = new RenkoData(time, time, price, price, low, high, volume, ticksCount);
/*     */ 
/* 351 */     Boolean rising = null;
/* 352 */     if (previousRenko != null) {
/* 353 */       if (previousRenko.getHigh() <= price) {
/* 354 */         rising = Boolean.TRUE;
/*     */       }
/*     */       else {
/* 357 */         rising = Boolean.FALSE;
/*     */       }
/*     */     }
/* 360 */     renko.setRising(rising);
/*     */ 
/* 362 */     return renko;
/*     */   }
/*     */ 
/*     */   public RenkoData getLastCompletedData()
/*     */   {
/* 367 */     return (RenkoData)getLastData();
/*     */   }
/*     */ 
/*     */   public boolean isAllDesiredDataLoaded()
/*     */   {
/* 372 */     return getLoadedElementsNumber() >= getDesiredDatasCount();
/*     */   }
/*     */ 
/*     */   public boolean isDirectOrder()
/*     */   {
/* 377 */     return this.directOrder;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 382 */     resetResulArray();
/* 383 */     this.lastElementIndex = -1;
/* 384 */     this.loadedBarsCount = 0;
/*     */   }
/*     */ 
/*     */   public void setupLastData(RenkoData data)
/*     */   {
/* 389 */     ((RenkoData[])getResult())[getLastElementIndex()] = data;
/*     */   }
/*     */ 
/*     */   public int getLoadedElementsNumber()
/*     */   {
/* 394 */     return this.loadedBarsCount;
/*     */   }
/*     */ 
/*     */   public void setLoadedBarsCount(int loadedBarsCount) {
/* 398 */     this.loadedBarsCount = loadedBarsCount;
/*     */   }
/*     */ 
/*     */   protected void setLastElementIndex(int index) {
/* 402 */     this.lastElementIndex = index;
/*     */   }
/*     */ 
/*     */   protected RenkoData[] createArray(RenkoData bar)
/*     */   {
/* 407 */     return new RenkoData[] { bar };
/*     */   }
/*     */ 
/*     */   protected RenkoData[] createArray(int size)
/*     */   {
/* 412 */     return new RenkoData[size];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.RenkoCreator
 * JD-Core Version:    0.6.0
 */