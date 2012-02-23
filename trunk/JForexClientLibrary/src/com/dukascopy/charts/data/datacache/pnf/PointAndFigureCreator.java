/*     */ package com.dukascopy.charts.data.datacache.pnf;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationCreator;
/*     */ 
/*     */ public class PointAndFigureCreator extends AbstractPriceAggregationCreator<PointAndFigureData, TickData, IPointAndFigureLiveFeedListener>
/*     */   implements IPointAndFigureCreator
/*     */ {
/*     */   private final PriceRange priceRange;
/*     */   private final ReversalAmount reversalAmount;
/*     */   private final double reversalDistance;
/*     */   private final double boxSize;
/*     */   protected PointAndFigureData currentBoxColumn;
/*     */   private PointAndFigureData previousBoxColumn;
/*     */ 
/*     */   public PointAndFigureCreator(Instrument instrument, PriceRange priceRange, ReversalAmount reversalAmount, OfferSide offerSide, int desiredBoxCount, boolean directOrder, boolean liveCreation, IPointAndFigureLiveFeedListener pointAndFigureLiveFeedListener)
/*     */   {
/*  37 */     super(instrument, offerSide, pointAndFigureLiveFeedListener, desiredBoxCount, liveCreation, directOrder);
/*     */ 
/*  45 */     this.priceRange = priceRange;
/*  46 */     this.reversalAmount = reversalAmount;
/*  47 */     this.boxSize = (priceRange.getPipCount() * instrument.getPipValue());
/*  48 */     this.reversalDistance = (this.boxSize * reversalAmount.getAmount());
/*     */ 
/*  50 */     reset();
/*     */   }
/*     */ 
/*     */   public boolean analyse(TickData data) {
/*  54 */     if (this.currentBoxColumn == null) {
/*  55 */       if (isAllDesiredDataLoaded()) {
/*  56 */         return true;
/*     */       }
/*  58 */       this.currentBoxColumn = startNewBoxColumn(data, this.previousBoxColumn);
/*  59 */       addNewElement(this.currentBoxColumn);
/*     */     }
/*  62 */     else if (canContinueCurrentDataConstruction(this.currentBoxColumn, data)) {
/*  63 */       this.currentBoxColumn = continueCurrentBoxColumn(data, this.currentBoxColumn, this.previousBoxColumn);
/*     */     }
/*     */     else {
/*  66 */       this.previousBoxColumn = finishCurrentBoxColumn(data, this.currentBoxColumn, this.previousBoxColumn);
/*     */ 
/*  68 */       fireNewBarCreated(this.previousBoxColumn);
/*     */ 
/*  70 */       if (isAllDesiredDataLoaded()) {
/*  71 */         return true;
/*     */       }
/*     */ 
/*  74 */       this.currentBoxColumn = startNewBoxColumn(data, this.previousBoxColumn);
/*  75 */       addNewElement(this.currentBoxColumn);
/*     */     }
/*     */ 
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   protected double roundToNearestHighLevel(double high)
/*     */   {
/*  84 */     double boxCount = round(high / getBoxSize());
/*  85 */     if (boxCount == (int)boxCount)
/*     */     {
/*  91 */       return high;
/*     */     }
/*  93 */     int boxCountInt = 1 + (int)boxCount;
/*  94 */     double result = round(boxCountInt * getBoxSize());
/*  95 */     return result;
/*     */   }
/*     */ 
/*     */   protected double roundToNearestLowLevel(double low) {
/*  99 */     double boxCount = round(low / getBoxSize());
/* 100 */     int boxCountInt = (int)boxCount;
/* 101 */     return round(boxCountInt * getBoxSize());
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 106 */     resetResultArray();
/* 107 */     this.lastElementIndex = -1;
/*     */   }
/*     */ 
/*     */   protected void resetResultArray() {
/* 111 */     this.result = new PointAndFigureData[getDesiredDatasCount()];
/*     */   }
/*     */ 
/*     */   protected void addNewElement(PointAndFigureData element) {
/* 115 */     this.lastElementIndex += 1;
/* 116 */     ((PointAndFigureData[])this.result)[getLastElementIndex()] = element;
/*     */   }
/*     */ 
/*     */   public boolean isAllDesiredDataLoaded()
/*     */   {
/* 121 */     return getLastElementIndex() + 1 >= getDesiredDatasCount();
/*     */   }
/*     */ 
/*     */   public double getReversalDistance() {
/* 125 */     return this.reversalDistance;
/*     */   }
/*     */ 
/*     */   private boolean isFlat(PointAndFigureData data) {
/* 129 */     return data.getHigh() == data.getLow();
/*     */   }
/*     */ 
/*     */   protected boolean canContinueCurrentBoxColumn(PointAndFigureData currentBoxColumn, double price, long nextDataTime)
/*     */   {
/* 137 */     boolean isTheSameTradingSession = DataCacheUtils.isTheSameTradingSession(currentBoxColumn.getTime(), nextDataTime);
/* 138 */     if (!isTheSameTradingSession) {
/* 139 */       return false;
/*     */     }
/*     */ 
/* 142 */     if (Boolean.TRUE.equals(currentBoxColumn.isRising())) {
/* 143 */       double diff = round(currentBoxColumn.getHigh() - getReversalDistance());
/* 144 */       if ((diff > price) && (!isFlat(currentBoxColumn)))
/*     */       {
/* 148 */         return false;
/*     */       }
/*     */     }
/* 151 */     else if (Boolean.FALSE.equals(currentBoxColumn.isRising())) {
/* 152 */       double diff = round(currentBoxColumn.getLow() + getReversalDistance());
/* 153 */       if ((diff < price) && (!isFlat(currentBoxColumn)))
/*     */       {
/* 157 */         return false;
/*     */       }
/*     */     }
/* 160 */     return true;
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData continueCurrentBoxColumn(TickData tickData, PointAndFigureData currentBoxColumn, PointAndFigureData previousBoxColumn)
/*     */   {
/* 168 */     long time = tickData.getTime();
/* 169 */     double price = getPrice(tickData);
/* 170 */     double volume = getVolume(tickData);
/*     */ 
/* 172 */     currentBoxColumn.setVolume(round(currentBoxColumn.getVolume() + volume));
/*     */ 
/* 174 */     if (currentBoxColumn.getHigh() < roundToNearestHighLevel(price)) {
/* 175 */       currentBoxColumn.setHigh(roundToNearestHighLevel(price));
/*     */     }
/* 177 */     else if (currentBoxColumn.getLow() > roundToNearestLowLevel(price)) {
/* 178 */       currentBoxColumn.setLow(roundToNearestLowLevel(price));
/*     */     }
/*     */ 
/* 181 */     currentBoxColumn.setFormedElementsCount(currentBoxColumn.getFormedElementsCount() + 1L);
/*     */ 
/* 183 */     if (isDirectOrder()) {
/* 184 */       currentBoxColumn.setEndTime(time);
/* 185 */       currentBoxColumn.setClose(price);
/*     */     }
/*     */     else {
/* 188 */       currentBoxColumn.setTime(time);
/* 189 */       currentBoxColumn.setOpen(price);
/*     */     }
/*     */ 
/* 192 */     if (currentBoxColumn.isRising() == null) {
/* 193 */       currentBoxColumn = setupRising(currentBoxColumn, previousBoxColumn);
/*     */     }
/*     */ 
/* 196 */     return currentBoxColumn;
/*     */   }
/*     */ 
/*     */   private PointAndFigureData setupRising(PointAndFigureData currentBoxColumn, PointAndFigureData previousBoxColumn)
/*     */   {
/* 204 */     if (previousBoxColumn == null) {
/* 205 */       if (currentBoxColumn.getOpen() > currentBoxColumn.getClose()) {
/* 206 */         currentBoxColumn.setRising(new Boolean(false));
/*     */       }
/* 208 */       else if (currentBoxColumn.getOpen() <= currentBoxColumn.getClose()) {
/* 209 */         currentBoxColumn.setRising(new Boolean(true));
/*     */       }
/*     */     }
/*     */     else {
/* 213 */       currentBoxColumn.setRising(new Boolean(!previousBoxColumn.isRising().booleanValue()));
/*     */     }
/*     */ 
/* 216 */     return currentBoxColumn;
/*     */   }
/*     */ 
/*     */   public void setupLastData(PointAndFigureData data)
/*     */   {
/* 221 */     ((PointAndFigureData[])getResult())[getLastElementIndex()] = data;
/*     */   }
/*     */ 
/*     */   protected double getBoxSize() {
/* 225 */     return this.boxSize;
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData finishCurrentBoxColumn(TickData data, PointAndFigureData currentBoxColumn, PointAndFigureData previousBoxColumn)
/*     */   {
/* 233 */     return currentBoxColumn;
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData startNewBoxColumn(TickData data, PointAndFigureData previousBoxColumn)
/*     */   {
/* 240 */     double price = round(getPrice(data));
/* 241 */     double open = 0.0D;
/* 242 */     double high = 0.0D;
/* 243 */     double low = 0.0D;
/* 244 */     double close = 0.0D;
/*     */ 
/* 246 */     open = high = low = close = price;
/*     */ 
/* 248 */     if (previousBoxColumn != null)
/*     */     {
/* 254 */       double previousePrice = 0.0D;
/* 255 */       if (Boolean.TRUE.equals(previousBoxColumn.isRising())) {
/* 256 */         previousePrice = previousBoxColumn.high - getBoxSize();
/* 257 */         high = round(Math.max(price, previousePrice));
/* 258 */         low = roundToNearestLowLevel(low);
/*     */       }
/*     */       else {
/* 261 */         previousePrice = previousBoxColumn.low + getBoxSize();
/* 262 */         low = round(Math.min(price, previousePrice));
/* 263 */         high = roundToNearestHighLevel(high);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 268 */     double volume = getVolume(data);
/* 269 */     long time = data.getTime();
/*     */ 
/* 271 */     PointAndFigureData boxColumn = new PointAndFigureData();
/*     */ 
/* 273 */     boxColumn.setFormedElementsCount(1L);
/* 274 */     boxColumn.setHigh(high);
/* 275 */     boxColumn.setLow(low);
/*     */ 
/* 277 */     boxColumn.setOpen(open);
/* 278 */     boxColumn.setClose(close);
/*     */ 
/* 280 */     boxColumn.setTime(time);
/* 281 */     boxColumn.setEndTime(time);
/* 282 */     boxColumn.setVolume(round(volume));
/*     */ 
/* 284 */     boxColumn = setupRising(boxColumn, previousBoxColumn);
/*     */ 
/* 286 */     return boxColumn;
/*     */   }
/*     */ 
/*     */   public boolean canContinueCurrentDataConstruction(PointAndFigureData currentData, TickData sourceData)
/*     */   {
/* 294 */     double price = getPrice(sourceData);
/* 295 */     long nextDataTime = sourceData.getTime();
/*     */ 
/* 297 */     boolean isTheSameTradingSession = DataCacheUtils.isTheSameTradingSession(currentData.getTime(), nextDataTime);
/* 298 */     if (!isTheSameTradingSession) {
/* 299 */       return false;
/*     */     }
/*     */ 
/* 302 */     if (Boolean.TRUE.equals(currentData.isRising())) {
/* 303 */       double diff = round(currentData.getHigh() - getReversalDistance());
/* 304 */       if ((diff > price) && (!isFlat(currentData)))
/*     */       {
/* 308 */         return false;
/*     */       }
/*     */     }
/* 311 */     else if (Boolean.FALSE.equals(currentData.isRising())) {
/* 312 */       double diff = round(currentData.getLow() + getReversalDistance());
/* 313 */       if ((diff < price) && (!isFlat(currentData)))
/*     */       {
/* 317 */         return false;
/*     */       }
/*     */     }
/* 320 */     return true;
/*     */   }
/*     */ 
/*     */   public PointAndFigureData getLastCompletedData()
/*     */   {
/* 325 */     return (PointAndFigureData)getLastData();
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 329 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount() {
/* 333 */     return this.reversalAmount;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.PointAndFigureCreator
 * JD-Core Version:    0.6.0
 */