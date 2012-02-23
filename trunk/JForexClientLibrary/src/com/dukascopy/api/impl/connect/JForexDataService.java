/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IDailyHighLowListener;
/*     */ import com.dukascopy.api.IDataService;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.feed.IFeedDescriptor;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.dhl.IDailyHighLowManager;
/*     */ import com.dukascopy.charts.data.datacache.dhl.IHighLowListener;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JForexDataService
/*     */   implements IDataService
/*     */ {
/*  36 */   private static final Logger LOGGER = LoggerFactory.getLogger(JForexDataService.class);
/*     */   private IFeedDataProvider feedDataProvider;
/*     */   private IDailyHighLowManager dailyHighLowManager;
/*     */ 
/*     */   public JForexDataService(IFeedDataProvider feedDataProvider)
/*     */   {
/*  43 */     if (feedDataProvider == null) {
/*  44 */       throw new IllegalArgumentException("FeedDataProvider must not be null.");
/*     */     }
/*  46 */     this.feedDataProvider = feedDataProvider;
/*  47 */     this.dailyHighLowManager = feedDataProvider.getDailyHighLowManager();
/*     */   }
/*     */ 
/*     */   public void addDailyHighLowListener(Instrument instrument, IDailyHighLowListener listener)
/*     */   {
/*  55 */     if (listener == null) {
/*  56 */       return;
/*     */     }
/*  58 */     this.dailyHighLowManager.addDailyHighLowListener(instrument, new HighLowListener(listener));
/*     */   }
/*     */ 
/*     */   public void removeDailyHighLowListener(IDailyHighLowListener listener)
/*     */   {
/*  66 */     if (listener == null) {
/*  67 */       return;
/*     */     }
/*  69 */     this.dailyHighLowManager.removeDailyHighLowListener(new HighLowListener(listener));
/*     */   }
/*     */ 
/*     */   public Collection<IDailyHighLowListener> getDailyHighLowListeners(Instrument instrument)
/*     */   {
/*  77 */     return getDailyHighLowListeners(this.dailyHighLowManager.getDailyHighLowListeners(instrument));
/*     */   }
/*     */ 
/*     */   public Map<Instrument, Collection<IDailyHighLowListener>> getDailyHighLowListeners()
/*     */   {
/*  85 */     Map highLowListenerMap = this.dailyHighLowManager.getDailyHighLowListeners();
/*  86 */     Map dailyHighLowListenerMap = new HashMap(highLowListenerMap.size());
/*  87 */     for (Map.Entry entry : highLowListenerMap.entrySet()) {
/*  88 */       Instrument instrument = (Instrument)entry.getKey();
/*  89 */       dailyHighLowListenerMap.put(instrument, getDailyHighLowListeners(instrument));
/*     */     }
/*  91 */     return dailyHighLowListenerMap;
/*     */   }
/*     */ 
/*     */   public void removeAllDailyHighLowListeners()
/*     */   {
/*  99 */     this.dailyHighLowManager.removeAllListeners();
/*     */   }
/*     */ 
/*     */   public long getTimeOfFirstCandle(IFeedDescriptor feedDescriptor)
/*     */   {
/* 108 */     long firstTime = 9223372036854775807L;
/* 109 */     if (feedDescriptor == null) {
/* 110 */       LOGGER.error("FeedDescriptor is null");
/* 111 */       return firstTime;
/*     */     }
/* 113 */     Instrument instrument = feedDescriptor.getInstrument();
/* 114 */     Period period = feedDescriptor.getPeriod();
/* 115 */     PriceRange priceRange = feedDescriptor.getPriceRange();
/* 116 */     ReversalAmount reversalAmount = feedDescriptor.getReversalAmount();
/*     */ 
/* 118 */     switch (1.$SwitchMap$com$dukascopy$api$DataType[feedDescriptor.getDataType().ordinal()]) {
/*     */     case 1:
/* 120 */       firstTime = getTimeOfFirstTick(instrument);
/* 121 */       break;
/*     */     case 2:
/* 123 */       firstTime = getTimeOfFirstTickBar(instrument);
/* 124 */       break;
/*     */     case 3:
/* 126 */       firstTime = getTimeOfFirstCandle(instrument, period);
/* 127 */       break;
/*     */     case 4:
/* 129 */       firstTime = getTimeOfFirstRangeBar(instrument, priceRange);
/* 130 */       break;
/*     */     case 5:
/* 132 */       firstTime = getTimeOfFirstPointAndFigure(instrument, priceRange, reversalAmount);
/* 133 */       break;
/*     */     case 6:
/* 135 */       firstTime = getTimeOfFirstRenko(instrument, priceRange);
/* 136 */       break;
/*     */     default:
/* 138 */       LOGGER.error("Illegal DataType: {}", feedDescriptor.getDataType());
/*     */     }
/* 140 */     return firstTime;
/*     */   }
/*     */ 
/*     */   public long getTimeOfFirstTick(Instrument instrument)
/*     */   {
/* 148 */     return this.feedDataProvider.getTimeOfFirstTick(instrument);
/*     */   }
/*     */ 
/*     */   public long getTimeOfFirstTickBar(Instrument instrument)
/*     */   {
/* 158 */     return this.feedDataProvider.getTimeOfFirstBar(instrument, TickBarSize.TWO);
/*     */   }
/*     */ 
/*     */   public long getTimeOfFirstCandle(Instrument instrument, Period period)
/*     */   {
/* 166 */     return this.feedDataProvider.getTimeOfFirstCandle(instrument, period);
/*     */   }
/*     */ 
/*     */   public long getTimeOfFirstRangeBar(Instrument instrument, PriceRange priceRange)
/*     */   {
/* 174 */     return this.feedDataProvider.getTimeOfFirstBar(instrument, priceRange);
/*     */   }
/*     */ 
/*     */   public long getTimeOfFirstPointAndFigure(Instrument instrument, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */   {
/* 182 */     return this.feedDataProvider.getTimeOfFirstBar(instrument, priceRange, reversalAmount);
/*     */   }
/*     */ 
/*     */   public long getTimeOfFirstRenko(Instrument instrument, PriceRange priceRange)
/*     */   {
/* 190 */     return this.feedDataProvider.getTimeOfFirstBar(instrument, priceRange);
/*     */   }
/*     */ 
/*     */   private List<IDailyHighLowListener> getDailyHighLowListeners(List<IHighLowListener> highLowListenerList) {
/* 194 */     List result = new ArrayList(highLowListenerList.size());
/* 195 */     for (IHighLowListener highLowListener : highLowListenerList) {
/* 196 */       if ((highLowListener instanceof HighLowListener)) {
/* 197 */         result.add(((HighLowListener)HighLowListener.class.cast(highLowListener)).dailyHighLowListener);
/*     */       }
/*     */     }
/* 200 */     return result;
/*     */   }
/*     */ 
/*     */   private class HighLowListener
/*     */     implements IHighLowListener
/*     */   {
/*     */     private IDailyHighLowListener dailyHighLowListener;
/*     */ 
/*     */     public HighLowListener(IDailyHighLowListener dailyHighLowListener)
/*     */     {
/* 211 */       this.dailyHighLowListener = dailyHighLowListener;
/*     */     }
/*     */ 
/*     */     public void highUpdated(Instrument instrument, Period period, double high)
/*     */     {
/* 219 */       if (ObjectUtils.isEqual(Period.DAILY, period))
/* 220 */         this.dailyHighLowListener.highUpdated(instrument, high);
/*     */     }
/*     */ 
/*     */     public void lowUpdated(Instrument instrument, Period period, double low)
/*     */     {
/* 229 */       if (ObjectUtils.isEqual(Period.DAILY, period))
/* 230 */         this.dailyHighLowListener.lowUpdated(instrument, low);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 239 */       return ObjectUtils.getHash(this.dailyHighLowListener);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 247 */       return ObjectUtils.isEqual(this.dailyHighLowListener, ((HighLowListener)HighLowListener.class.cast(obj)).dailyHighLowListener);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.JForexDataService
 * JD-Core Version:    0.6.0
 */