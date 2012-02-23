/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.renko;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import java.util.Collections;
/*     */ 
/*     */ public class NullRenkoDataSequence extends RenkoDataSequence
/*     */ {
/*     */   public NullRenkoDataSequence()
/*     */   {
/*  18 */     super(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, new RenkoData[0], Collections.emptyMap(), Collections.emptyMap(), false, false);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  33 */     return true;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  38 */     return 0;
/*     */   }
/*     */ 
/*     */   public void calculateMasterDataMinMax()
/*     */   {
/*     */   }
/*     */ 
/*     */   public double getMin()
/*     */   {
/*  48 */     return 4.9E-324D;
/*     */   }
/*     */ 
/*     */   public double getMax()
/*     */   {
/*  53 */     return 1.7976931348623157E+308D;
/*     */   }
/*     */ 
/*     */   public RenkoData[] getData()
/*     */   {
/*  58 */     return new RenkoData[0];
/*     */   }
/*     */ 
/*     */   public long[][] getGaps()
/*     */   {
/*  63 */     return new long[0][];
/*     */   }
/*     */ 
/*     */   public long getFrom()
/*     */   {
/*  68 */     return 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public long getTo()
/*     */   {
/*  73 */     return 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public boolean isLatestDataVisible()
/*     */   {
/*  78 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isIncludesLatestData()
/*     */   {
/*  83 */     return true;
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
/*  98 */     return new double[0];
/*     */   }
/*     */ 
/*     */   public int[] getFormulaOutputInt(int id, int outputNumber)
/*     */   {
/* 103 */     return new int[0];
/*     */   }
/*     */ 
/*     */   public boolean isFormulasMinMaxEmpty(Integer indicatorId)
/*     */   {
/* 108 */     return true;
/*     */   }
/*     */ 
/*     */   public double getFormulasMinFor(Integer indicatorId)
/*     */   {
/* 113 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double getFormulasMaxFor(Integer indicatorId)
/*     */   {
/* 118 */     return 0.0D;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.renko.NullRenkoDataSequence
 * JD-Core Version:    0.6.0
 */