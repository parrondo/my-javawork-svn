/*     */ package com.dukascopy.charts.mappers;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ 
/*     */ public class Mapper
/*     */   implements IMapper
/*     */ {
/*     */   private final ITimeToXMapper timeToXMapper;
/*     */   private final IValueToYMapper valueToYMapper;
/*     */ 
/*     */   public Mapper(ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper)
/*     */   {
/*  14 */     this.timeToXMapper = timeToXMapper;
/*  15 */     this.valueToYMapper = valueToYMapper;
/*     */   }
/*     */ 
/*     */   public long tx(int x)
/*     */   {
/*  22 */     return this.timeToXMapper.tx(x);
/*     */   }
/*     */ 
/*     */   public int xt(long time)
/*     */   {
/*  27 */     return this.timeToXMapper.xt(time);
/*     */   }
/*     */ 
/*     */   public boolean isXOutOfRange(int x)
/*     */   {
/*  32 */     return this.timeToXMapper.isXOutOfRange(x);
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/*  37 */     return this.timeToXMapper.getWidth();
/*     */   }
/*     */ 
/*     */   public int getBarWidth()
/*     */   {
/*  42 */     return this.timeToXMapper.getBarWidth();
/*     */   }
/*     */ 
/*     */   public long getInterval()
/*     */   {
/*  47 */     return this.timeToXMapper.getInterval();
/*     */   }
/*     */ 
/*     */   public double vy(int y)
/*     */   {
/*  54 */     return this.valueToYMapper.vy(y);
/*     */   }
/*     */ 
/*     */   public int yv(double value)
/*     */   {
/*  59 */     return this.valueToYMapper.yv(value);
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/*  64 */     return this.valueToYMapper.getHeight();
/*     */   }
/*     */ 
/*     */   public boolean isYOutOfRange(int y)
/*     */   {
/*  69 */     return this.valueToYMapper.isYOutOfRange(y);
/*     */   }
/*     */ 
/*     */   public float getValuesInOnePixel()
/*     */   {
/*  74 */     return this.valueToYMapper.getValuesInOnePixel();
/*     */   }
/*     */ 
/*     */   public void computeGeometry(int paneHeight)
/*     */   {
/*  79 */     this.valueToYMapper.computeGeometry(paneHeight);
/*     */   }
/*     */ 
/*     */   public void computeGeometry(double min, double max) {
/*  83 */     this.valueToYMapper.computeGeometry(min, max);
/*     */   }
/*     */ 
/*     */   public void computeGeometry() {
/*  87 */     this.valueToYMapper.computeGeometry();
/*     */   }
/*     */ 
/*     */   public void setPadding(double padding) {
/*  91 */     this.valueToYMapper.setPadding(padding);
/*     */   }
/*     */ 
/*     */   public double getPadding() {
/*  95 */     return this.valueToYMapper.getPadding();
/*     */   }
/*     */ 
/*     */   public Period getPeriod()
/*     */   {
/* 100 */     return this.timeToXMapper.getPeriod();
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 105 */     return this.valueToYMapper.getInstrument();
/*     */   }
/*     */ 
/*     */   public ITimeToXMapper getTimeToXMapper() {
/* 109 */     return this.timeToXMapper;
/*     */   }
/*     */ 
/*     */   public IValueToYMapper getValueToYMapper() {
/* 113 */     return this.valueToYMapper;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.Mapper
 * JD-Core Version:    0.6.0
 */