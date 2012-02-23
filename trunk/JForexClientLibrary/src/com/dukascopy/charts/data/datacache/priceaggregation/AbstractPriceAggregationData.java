/*     */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.feed.IPriceAggregationBar;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public abstract class AbstractPriceAggregationData extends CandleData
/*     */   implements IPriceAggregationBar
/*     */ {
/*  15 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
/*     */   public long endTime;
/*     */   public long formedElementsCount;
/*     */ 
/*     */   public AbstractPriceAggregationData()
/*     */   {
/*     */   }
/*     */ 
/*     */   public AbstractPriceAggregationData(long time, long endTime, double open, double close, double low, double high, double vol, long formedElementsCount)
/*     */   {
/*  41 */     super(time, open, close, low, high, vol);
/*  42 */     this.endTime = endTime;
/*  43 */     this.formedElementsCount = formedElementsCount;
/*     */   }
/*     */ 
/*     */   public long getEndTime()
/*     */   {
/*  48 */     return this.endTime;
/*     */   }
/*     */   public void setEndTime(long endTime) {
/*  51 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  57 */     StringBuilder stamp = new StringBuilder();
/*     */ 
/*  59 */     stamp.append("StartTime: ").append(DATE_FORMAT.format(new Long(getTime()))).append(" EndTime: ").append(DATE_FORMAT.format(new Long(getEndTime()))).append(" O: ").append(this.open).append(" C: ").append(this.close).append(" H: ").append(this.high).append(" L: ").append(this.low).append(" V: ").append(this.vol).append(" FEC: ").append(this.formedElementsCount);
/*     */ 
/*  77 */     return stamp.toString();
/*     */   }
/*     */ 
/*     */   public long getFormedElementsCount()
/*     */   {
/*  82 */     return this.formedElementsCount;
/*     */   }
/*     */ 
/*     */   public void setFormedElementsCount(long formedElementsCount) {
/*  86 */     this.formedElementsCount = formedElementsCount;
/*     */   }
/*     */ 
/*     */   public AbstractPriceAggregationData clone()
/*     */   {
/*  91 */     return (AbstractPriceAggregationData)super.clone();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  96 */     int prime = 31;
/*  97 */     int result = super.hashCode();
/*  98 */     result = 31 * result + (int)(this.endTime ^ this.endTime >>> 32);
/*  99 */     result = 31 * result + (int)(this.formedElementsCount ^ this.formedElementsCount >>> 32);
/* 100 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 105 */     if (this == obj) {
/* 106 */       return true;
/*     */     }
/* 108 */     if (!super.equals(obj)) {
/* 109 */       return false;
/*     */     }
/* 111 */     if (getClass() != obj.getClass()) {
/* 112 */       return false;
/*     */     }
/* 114 */     AbstractPriceAggregationData other = (AbstractPriceAggregationData)obj;
/* 115 */     if (this.endTime != other.endTime) {
/* 116 */       return false;
/*     */     }
/*     */ 
/* 119 */     return this.formedElementsCount == other.formedElementsCount;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  17 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData
 * JD-Core Version:    0.6.0
 */