/*     */ package com.dukascopy.charts.mappers;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ 
/*     */ public class NullMapper
/*     */   implements IMapper
/*     */ {
/*     */   public long tx(int x)
/*     */   {
/*  11 */     return -1L;
/*     */   }
/*     */ 
/*     */   public int xt(long time)
/*     */   {
/*  16 */     return -1;
/*     */   }
/*     */ 
/*     */   public double vy(int y)
/*     */   {
/*  21 */     return -1.0D;
/*     */   }
/*     */ 
/*     */   public int yv(double value)
/*     */   {
/*  26 */     return -1;
/*     */   }
/*     */ 
/*     */   public double subVy(IndicatorWrapper indicatorWrapper, int y)
/*     */   {
/*  31 */     return -1.0D;
/*     */   }
/*     */ 
/*     */   public long getInterval()
/*     */   {
/*  36 */     return -1L;
/*     */   }
/*     */ 
/*     */   public int getBarWidth() {
/*  40 */     return 1;
/*     */   }
/*     */ 
/*     */   public float getValuesInOnePixel()
/*     */   {
/*  45 */     return -1.0F;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/*  50 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/*  55 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean isXOutOfRange(int x)
/*     */   {
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isYOutOfRange(int y)
/*     */   {
/*  65 */     return true;
/*     */   }
/*     */ 
/*     */   public void computeGeometry(int paneHeight)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void computeGeometry(double min, double max)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void computeGeometry()
/*     */   {
/*     */   }
/*     */ 
/*     */   public double getPadding()
/*     */   {
/*  89 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public void setPadding(double padding)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Period getPeriod()
/*     */   {
/* 101 */     return null;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 107 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.NullMapper
 * JD-Core Version:    0.6.0
 */