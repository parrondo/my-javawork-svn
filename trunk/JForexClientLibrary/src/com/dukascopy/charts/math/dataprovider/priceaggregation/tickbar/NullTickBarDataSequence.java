/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import java.util.Collections;
/*     */ 
/*     */ public class NullTickBarDataSequence extends TickBarDataSequence
/*     */ {
/*     */   public NullTickBarDataSequence()
/*     */   {
/*  15 */     super(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, new TickBarData[0], Collections.emptyMap(), Collections.emptyMap(), false, false);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  30 */     return true;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  35 */     return 0;
/*     */   }
/*     */ 
/*     */   public void calculateMasterDataMinMax()
/*     */   {
/*     */   }
/*     */ 
/*     */   public double getMin()
/*     */   {
/*  45 */     return 4.9E-324D;
/*     */   }
/*     */ 
/*     */   public double getMax()
/*     */   {
/*  50 */     return 1.7976931348623157E+308D;
/*     */   }
/*     */ 
/*     */   public TickBarData[] getData()
/*     */   {
/*  55 */     return new TickBarData[0];
/*     */   }
/*     */ 
/*     */   public long[][] getGaps()
/*     */   {
/*  60 */     return new long[0][];
/*     */   }
/*     */ 
/*     */   public long getFrom()
/*     */   {
/*  65 */     return 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public long getTo()
/*     */   {
/*  70 */     return 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public boolean isLatestDataVisible()
/*     */   {
/*  75 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isIncludesLatestData()
/*     */   {
/*  80 */     return true;
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
/*     */   public double[] getFormulaOutputDouble(int id, int outputNumber)
/*     */   {
/*  95 */     return new double[0];
/*     */   }
/*     */ 
/*     */   public int[] getFormulaOutputInt(int id, int outputNumber)
/*     */   {
/* 100 */     return new int[0];
/*     */   }
/*     */ 
/*     */   public boolean isFormulasMinMaxEmpty(Integer indicatorId)
/*     */   {
/* 105 */     return true;
/*     */   }
/*     */ 
/*     */   public double getFormulasMinFor(Integer indicatorId)
/*     */   {
/* 110 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double getFormulasMaxFor(Integer indicatorId)
/*     */   {
/* 115 */     return 0.0D;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.NullTickBarDataSequence
 * JD-Core Version:    0.6.0
 */