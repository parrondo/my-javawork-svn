/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.buffer;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class PriceAggregationShiftableBuffer<D extends AbstractPriceAggregationData> extends ShiftableBuffer<D>
/*     */   implements IPriceAggregationShiftableBuffer<D>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public PriceAggregationShiftableBuffer(int maxSize)
/*     */   {
/*  17 */     super(maxSize);
/*     */   }
/*     */ 
/*     */   public boolean addOrReplace(D bar)
/*     */   {
/*  22 */     synchronized (this) {
/*  23 */       if (containsStartTime(bar.getTime())) {
/*  24 */         int index = getStartTimeIndex(bar.getTime());
/*  25 */         set(bar, index);
/*  26 */         return true;
/*     */       }
/*  28 */       if (!containsTime(bar.getTime())) {
/*  29 */         addToEnd(bar);
/*  30 */         return true;
/*     */       }
/*     */ 
/*  33 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean containsStartTime(long time)
/*     */   {
/*  40 */     synchronized (this) {
/*  41 */       int index = getStartTimeIndex(time);
/*  42 */       return index > -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean containsTime(long time)
/*     */   {
/*  48 */     synchronized (this) {
/*  49 */       int index = getTimeIndex(time);
/*  50 */       return index > -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getStartTimeIndex(long time)
/*     */   {
/*  56 */     synchronized (this)
/*     */     {
/*  59 */       int first = 0;
/*  60 */       int upto = getSize();
/*     */ 
/*  62 */       while (first < upto) {
/*  63 */         int mid = (first + upto) / 2;
/*     */ 
/*  65 */         AbstractPriceAggregationData data = (AbstractPriceAggregationData)this.buffer[mid];
/*     */ 
/*  67 */         if (data.time == time) {
/*  68 */           return mid;
/*     */         }
/*  70 */         if (time < data.time) {
/*  71 */           upto = mid;
/*     */         }
/*  73 */         else if (time > data.time) {
/*  74 */           first = mid + 1;
/*     */         }
/*     */         else {
/*  77 */           return mid;
/*     */         }
/*     */       }
/*     */ 
/*  81 */       return -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getTimeIndex(long time)
/*     */   {
/*  88 */     synchronized (this) {
/*  89 */       int size = getSize();
/*  90 */       int first = 0;
/*  91 */       int upto = size;
/*     */ 
/*  93 */       while (first < upto) {
/*  94 */         int mid = (first + upto) / 2;
/*     */ 
/*  96 */         int next = mid + 1;
/*  97 */         int previous = mid - 1;
/*     */ 
/*  99 */         AbstractPriceAggregationData data = (AbstractPriceAggregationData)this.buffer[mid];
/* 100 */         AbstractPriceAggregationData nextData = (next >= 0) && (next < size) ? (AbstractPriceAggregationData)this.buffer[next] : null;
/* 101 */         AbstractPriceAggregationData previousData = (previous >= 0) && (previous < size) ? (AbstractPriceAggregationData)this.buffer[previous] : null;
/*     */ 
/* 103 */         if ((data.time <= time) && (time <= data.endTime)) {
/* 104 */           return mid;
/*     */         }
/* 106 */         if ((nextData != null) && (data.endTime < time) && (time <= nextData.endTime)) {
/* 107 */           return next;
/*     */         }
/* 109 */         if ((previousData != null) && (previousData.time <= time) && (time < data.time)) {
/* 110 */           return previous;
/*     */         }
/* 112 */         if (time < data.time) {
/* 113 */           upto = mid;
/*     */         }
/* 115 */         else if (time > data.time) {
/* 116 */           first = mid + 1;
/*     */         }
/*     */         else {
/* 119 */           return mid;
/*     */         }
/*     */       }
/*     */ 
/* 123 */       return -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<D> getAfterTimeInclude(long time)
/*     */   {
/* 130 */     synchronized (this) {
/* 131 */       int index = getTimeIndex(time);
/* 132 */       if (index > -1) {
/* 133 */         List result = new ArrayList();
/* 134 */         for (int i = index; i < getSize(); i++) {
/* 135 */           AbstractPriceAggregationData data = (AbstractPriceAggregationData)this.buffer[i];
/* 136 */           result.add(data);
/*     */         }
/* 138 */         return result;
/*     */       }
/* 140 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.buffer.PriceAggregationShiftableBuffer
 * JD-Core Version:    0.6.0
 */