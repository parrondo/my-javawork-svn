/*     */ package com.dukascopy.charts.wrapper;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.drawings.IChartObjectFactory;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class DelegatableChartWrapper
/*     */   implements IChart
/*     */ {
/*     */   private final IChart delegate;
/*     */   private final String callerId;
/*     */ 
/*     */   public DelegatableChartWrapper(IChart delegate, String callerId)
/*     */   {
/*  46 */     this.delegate = delegate;
/*  47 */     this.callerId = callerId;
/*     */   }
/*     */ 
/*     */   public IChart getDelegate() {
/*  51 */     return this.delegate;
/*     */   }
/*     */ 
/*     */   public String getCallerId() {
/*  55 */     return this.callerId;
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator) {
/*  59 */     this.delegate.addIndicator(indicator);
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams) {
/*  63 */     this.delegate.addIndicator(indicator, optParams);
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] outputDrawingStyles, int[] outputWidths)
/*     */   {
/*  73 */     this.delegate.addIndicator(indicator, optParams, outputColors, outputDrawingStyles, outputWidths);
/*     */   }
/*     */ 
/*     */   public void addSubIndicator(Integer subChartId, IIndicator indicator)
/*     */   {
/*  83 */     this.delegate.addSubIndicator(subChartId, indicator);
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToMainChart(T object) {
/*  87 */     String previousCallerId = ((AbstractChartWrapper)this.delegate).getOwnerId(object);
/*     */     try {
/*  89 */       ((AbstractChartWrapper)this.delegate).setOwnerId(object, this.callerId);
/*  90 */       this.delegate.addToMainChart(object);
/*     */     } catch (RuntimeException e) {
/*  92 */       ((AbstractChartWrapper)this.delegate).setOwnerId(object, previousCallerId);
/*  93 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToMainChartUnlocked(T object) {
/*  98 */     String previousCallerId = ((AbstractChartWrapper)this.delegate).getOwnerId(object);
/*     */     try {
/* 100 */       ((AbstractChartWrapper)this.delegate).setOwnerId(object, this.callerId);
/* 101 */       this.delegate.addToMainChartUnlocked(object);
/*     */     } catch (RuntimeException e) {
/* 103 */       ((AbstractChartWrapper)this.delegate).setOwnerId(object, previousCallerId);
/* 104 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToSubChart(Integer subChartId, int indicatorId, T object)
/*     */   {
/* 113 */     String previousCallerId = ((AbstractChartWrapper)this.delegate).getOwnerId(object);
/*     */     try {
/* 115 */       ((AbstractChartWrapper)this.delegate).setOwnerId(object, this.callerId);
/* 116 */       this.delegate.addToSubChart(subChartId, indicatorId, object);
/*     */     }
/*     */     catch (RuntimeException e)
/*     */     {
/* 122 */       ((AbstractChartWrapper)this.delegate).setOwnerId(object, previousCallerId);
/* 123 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void comment(String comment) {
/* 128 */     this.delegate.comment(comment);
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/* 141 */     return this.delegate.draw(key, type, time1, price1, time2, price2, time3, price3);
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/* 161 */     return this.delegate.draw(key, type, time1, price1, time2, price2);
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1)
/*     */   {
/* 177 */     return this.delegate.draw(key, type, time1, price1);
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/* 195 */     return this.delegate.drawUnlocked(key, type, time1, price1, time2, price2, time3, price3);
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/* 215 */     return this.delegate.drawUnlocked(key, type, time1, price1, time2, price2);
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1)
/*     */   {
/* 231 */     return this.delegate.drawUnlocked(key, type, time1, price1);
/*     */   }
/*     */ 
/*     */   public IChartObject get(String key)
/*     */   {
/* 240 */     return this.delegate.get(key);
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getAll() {
/* 244 */     return this.delegate.getAll();
/*     */   }
/*     */ 
/*     */   public int getBarsCount() {
/* 248 */     return this.delegate.getBarsCount();
/*     */   }
/*     */ 
/*     */   public IChartObjectFactory getChartObjectFactory() {
/* 252 */     return this.delegate.getChartObjectFactory();
/*     */   }
/*     */ 
/*     */   public Color getCommentColor() {
/* 256 */     return this.delegate.getCommentColor();
/*     */   }
/*     */ 
/*     */   public Font getCommentFont() {
/* 260 */     return this.delegate.getCommentFont();
/*     */   }
/*     */ 
/*     */   public int getCommentHorizontalPosition() {
/* 264 */     return this.delegate.getCommentHorizontalPosition();
/*     */   }
/*     */ 
/*     */   public int getCommentVerticalPosition() {
/* 268 */     return this.delegate.getCommentVerticalPosition();
/*     */   }
/*     */ 
/*     */   public DataType getDataType() {
/* 272 */     return this.delegate.getDataType();
/*     */   }
/*     */ 
/*     */   public List<IIndicator> getIndicators() {
/* 276 */     return this.delegate.getIndicators();
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/* 280 */     return this.delegate.getInstrument();
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 284 */     return this.delegate.getPriceRange();
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount() {
/* 288 */     return this.delegate.getReversalAmount();
/*     */   }
/*     */ 
/*     */   public OfferSide getSelectedOfferSide() {
/* 292 */     return this.delegate.getSelectedOfferSide();
/*     */   }
/*     */ 
/*     */   public Period getSelectedPeriod() {
/* 296 */     return this.delegate.getSelectedPeriod();
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize() {
/* 300 */     return this.delegate.getTickBarSize();
/*     */   }
/*     */ 
/*     */   public Boolean isChartObjectUnlocked(IChartObject chartObject) {
/* 304 */     return this.delegate.isChartObjectUnlocked(chartObject);
/*     */   }
/*     */ 
/*     */   public void move(IChartObject objectToMove, long newTime, double newPrice)
/*     */   {
/* 312 */     this.delegate.move(objectToMove, newTime, newPrice);
/*     */   }
/*     */ 
/*     */   public void move(String chartObjectKey, long newTime, double newPrice)
/*     */   {
/* 324 */     this.delegate.move(chartObjectKey, newTime, newPrice);
/*     */   }
/*     */ 
/*     */   public double priceMax(int index)
/*     */   {
/* 332 */     return this.delegate.priceMax(index);
/*     */   }
/*     */ 
/*     */   public double priceMin(int index) {
/* 336 */     return this.delegate.priceMin(index);
/*     */   }
/*     */ 
/*     */   public IChartObject remove(String key) {
/* 340 */     return this.delegate.remove(key);
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject) {
/* 344 */     this.delegate.remove(chartObject);
/*     */   }
/*     */ 
/*     */   public void removeAll() {
/* 348 */     this.delegate.removeAll();
/*     */   }
/*     */ 
/*     */   public void removeIndicator(IIndicator indicator) {
/* 352 */     this.delegate.removeIndicator(indicator);
/*     */   }
/*     */ 
/*     */   public void repaint() {
/* 356 */     this.delegate.repaint();
/*     */   }
/*     */ 
/*     */   public void setCommentColor(Color color) {
/* 360 */     this.delegate.setCommentColor(color);
/*     */   }
/*     */ 
/*     */   public void setCommentFont(Font font) {
/* 364 */     this.delegate.setCommentFont(font);
/*     */   }
/*     */ 
/*     */   public void setCommentHorizontalPosition(int position) {
/* 368 */     this.delegate.setCommentHorizontalPosition(position);
/*     */   }
/*     */ 
/*     */   public void setCommentVerticalPosition(int position) {
/* 372 */     this.delegate.setCommentVerticalPosition(position);
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument) {
/* 376 */     this.delegate.setInstrument(instrument);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 380 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   public int windowsTotal() {
/* 384 */     return this.delegate.windowsTotal();
/*     */   }
/*     */ 
/*     */   public Iterator<IChartObject> iterator() {
/* 388 */     return this.delegate.iterator();
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getStrategyChartObjects()
/*     */   {
/* 393 */     List objects = getAll();
/* 394 */     List result = new ArrayList();
/* 395 */     if ((objects != null) && (this.callerId != null)) {
/* 396 */       for (IChartObject object : objects) {
/* 397 */         String chartObjectOwnerId = ((AbstractChartWrapper)this.delegate).getOwnerId(object);
/* 398 */         if (this.callerId.equals(chartObjectOwnerId)) {
/* 399 */           result.add(object);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 404 */     return result;
/*     */   }
/*     */ 
/*     */   public List<IChartObject> remove(List<IChartObject> chartObjects)
/*     */   {
/* 409 */     return this.delegate.remove(chartObjects);
/*     */   }
/*     */ 
/*     */   public BufferedImage getImage()
/*     */   {
/* 417 */     return this.delegate.getImage();
/*     */   }
/*     */ 
/*     */   public void setVerticalAxisScale(double minPriceValue, double maxPriceValue)
/*     */   {
/* 422 */     this.delegate.setVerticalAxisScale(minPriceValue, maxPriceValue);
/*     */   }
/*     */ 
/*     */   public void setVerticalAutoscale(boolean autoscale)
/*     */   {
/* 427 */     this.delegate.setVerticalAutoscale(autoscale);
/*     */   }
/*     */ 
/*     */   public void setDataPresentationType(DataType.DataPresentationType presentationType)
/*     */   {
/* 432 */     this.delegate.setDataPresentationType(presentationType);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.wrapper.DelegatableChartWrapper
 * JD-Core Version:    0.6.0
 */