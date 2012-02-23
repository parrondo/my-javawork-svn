/*     */ package com.dukascopy.charts.math.dataprovider;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import java.util.Collections;
/*     */ 
/*     */ public class NullCandleDataSequence extends CandleDataSequence
/*     */ {
/*     */   public NullCandleDataSequence()
/*     */   {
/*  12 */     super(Period.TEN_SECS, System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, new CandleData[0], new long[0][], Collections.emptyMap(), Collections.emptyMap(), false, false);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  29 */     return true;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  34 */     return 0;
/*     */   }
/*     */ 
/*     */   public void calculateMasterDataMinMax()
/*     */   {
/*     */   }
/*     */ 
/*     */   public double getMin()
/*     */   {
/*  44 */     return 4.9E-324D;
/*     */   }
/*     */ 
/*     */   public double getMax()
/*     */   {
/*  49 */     return 1.7976931348623157E+308D;
/*     */   }
/*     */ 
/*     */   public CandleData[] getData()
/*     */   {
/*  54 */     return new CandleData[0];
/*     */   }
/*     */ 
/*     */   public CandleData getCandleInProgress() {
/*  58 */     return null;
/*     */   }
/*     */ 
/*     */   public CandleData getBeforeFirst() {
/*  62 */     return null;
/*     */   }
/*     */ 
/*     */   public CandleData getAfterLast() {
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public long[][] getGaps()
/*     */   {
/*  71 */     return new long[0][];
/*     */   }
/*     */ 
/*     */   public long getFrom()
/*     */   {
/*  77 */     return 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public long getTo()
/*     */   {
/*  82 */     return 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public boolean isLatestDataVisible()
/*     */   {
/*  87 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isIncludesLatestData()
/*     */   {
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */   protected void calculateMinMax()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void calculateSlaveDataMinMax()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isDataAvailable()
/*     */   {
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   public double[] getFormulaOutputDouble(int id, int outputNumber)
/*     */   {
/* 111 */     return new double[0];
/*     */   }
/*     */ 
/*     */   public int[] getFormulaOutputInt(int id, int outputNumber)
/*     */   {
/* 116 */     return new int[0];
/*     */   }
/*     */ 
/*     */   public boolean isFormulasMinMaxEmpty(Integer indicatorId)
/*     */   {
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */   public double getFormulasMinFor(Integer indicatorId)
/*     */   {
/* 126 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double getFormulasMaxFor(Integer indicatorId)
/*     */   {
/* 131 */     return 0.0D;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.NullCandleDataSequence
 * JD-Core Version:    0.6.0
 */