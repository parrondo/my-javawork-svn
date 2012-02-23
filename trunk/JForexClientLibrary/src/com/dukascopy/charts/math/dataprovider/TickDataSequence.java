/*     */ package com.dukascopy.charts.math.dataprovider;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TickDataSequence extends AbstractDataSequence<TickData>
/*     */ {
/*     */   private final int oneSecExtraBefore;
/*     */   private final int oneSecExtraAfter;
/*     */   private final CandleData[] oneSecCandlesAsk;
/*     */   private final CandleData[] oneSecCandlesBid;
/*     */ 
/*     */   public TickDataSequence(long from, long to, int extraBefore, int extraAfter, TickData[] data, long[][] gaps, int oneSecExtraBefore, int oneSecExtraAfter, CandleData[] oneSecCandlesAsk, CandleData[] oneSecCandlesBid, Map<Integer, IndicatorWrapper> indicatorsMap, Map<Integer, Object[]> formulaOutputs, boolean latestDataVisible, boolean includesLatestData)
/*     */   {
/*  36 */     super(Period.TICK, from, to, extraBefore, extraAfter, data, gaps, formulaOutputs, indicatorsMap, latestDataVisible, includesLatestData);
/*     */ 
/*  50 */     this.oneSecExtraBefore = oneSecExtraBefore;
/*  51 */     this.oneSecExtraAfter = oneSecExtraAfter;
/*  52 */     this.oneSecCandlesAsk = oneSecCandlesAsk;
/*  53 */     this.oneSecCandlesBid = oneSecCandlesBid;
/*  54 */     calculateMinMax();
/*     */   }
/*     */ 
/*     */   public void calculateMasterDataMinMax()
/*     */   {
/*  60 */     if ((this.oneSecCandlesAsk == null) || (this.oneSecCandlesAsk.length == 0)) {
/*  61 */       return;
/*     */     }
/*     */ 
/*  64 */     int minMaxIndexStart = this.oneSecExtraBefore > 0 ? this.oneSecExtraBefore - 1 : 0;
/*  65 */     int minMaxIndexEnd = this.oneSecCandlesAsk.length - (this.oneSecExtraAfter > 0 ? this.oneSecExtraAfter - 1 : 0);
/*  66 */     this.min = this.oneSecCandlesBid[minMaxIndexStart].low;
/*  67 */     this.max = this.oneSecCandlesAsk[minMaxIndexStart].high;
/*     */ 
/*  69 */     for (int i = minMaxIndexStart; i < minMaxIndexEnd; i++) {
/*  70 */       double high = this.oneSecCandlesAsk[i].high;
/*  71 */       double low = this.oneSecCandlesBid[i].low;
/*  72 */       this.max = (this.max > high ? this.max : high);
/*  73 */       this.min = (this.min > low ? low : this.min);
/*     */     }
/*     */ 
/*  76 */     if (this.max == this.min)
/*  77 */       this.max += 1.0D;
/*     */   }
/*     */ 
/*     */   public Object[] getFormulaOutputs(int id)
/*     */   {
/*  83 */     if (this.formulaOutputs == null) {
/*  84 */       return new Object[0][];
/*     */     }
/*  86 */     return (Object[])this.formulaOutputs.get(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   protected int getFormulaExtraBefore()
/*     */   {
/*  91 */     return this.oneSecExtraBefore;
/*     */   }
/*     */ 
/*     */   protected int getFormulaExtraAfter()
/*     */   {
/*  96 */     return this.oneSecExtraAfter;
/*     */   }
/*     */ 
/*     */   public int getOneSecExtraBefore() {
/* 100 */     return this.oneSecExtraBefore;
/*     */   }
/*     */ 
/*     */   public int getOneSecExtraAfter() {
/* 104 */     return this.oneSecExtraAfter;
/*     */   }
/*     */ 
/*     */   public CandleData[] getOneSecCandlesAsk() {
/* 108 */     return this.oneSecCandlesAsk;
/*     */   }
/*     */ 
/*     */   public CandleData[] getOneSecCandlesBid() {
/* 112 */     return this.oneSecCandlesBid;
/*     */   }
/*     */ 
/*     */   protected int getIndicatorDataIndex(long time)
/*     */   {
/* 121 */     int i = 0; for (int k = this.oneSecCandlesAsk.length; i < k; i++) {
/* 122 */       if (time <= this.oneSecCandlesAsk[i].time) {
/* 123 */         return i;
/*     */       }
/*     */     }
/* 126 */     return -1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.TickDataSequence
 * JD-Core Version:    0.6.0
 */