/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ 
/*     */ public class JForexPeriod
/*     */ {
/*     */   private Period period;
/*     */   private DataType dataType;
/*     */   private PriceRange priceRange;
/*     */   private ReversalAmount reversalAmount;
/*     */   private TickBarSize tickBarSize;
/*     */ 
/*     */   public JForexPeriod(DataType dataType, Period period, PriceRange priceRange, ReversalAmount reversalAmount, TickBarSize tickBarSize)
/*     */   {
/*  38 */     this.period = period;
/*  39 */     this.dataType = dataType;
/*  40 */     this.priceRange = priceRange;
/*  41 */     this.reversalAmount = reversalAmount;
/*  42 */     this.tickBarSize = tickBarSize;
/*     */   }
/*     */ 
/*     */   public JForexPeriod(DataType dataType, Period period, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */   {
/*  51 */     this(dataType, period, priceRange, reversalAmount, null);
/*     */   }
/*     */ 
/*     */   public JForexPeriod(DataType dataType, Period period, PriceRange priceRange)
/*     */   {
/*  59 */     this(dataType, period, priceRange, null);
/*     */   }
/*     */ 
/*     */   public JForexPeriod(DataType dataType, Period period)
/*     */   {
/*  68 */     this(dataType, period, null, null);
/*     */   }
/*     */ 
/*     */   public JForexPeriod(Period period) {
/*  72 */     this(null, period, null, null);
/*     */   }
/*     */ 
/*     */   public JForexPeriod(DataType dataType) {
/*  76 */     this(dataType, null, null, null);
/*     */   }
/*     */ 
/*     */   public JForexPeriod(PriceRange priceRange) {
/*  80 */     this(null, null, priceRange, null);
/*     */   }
/*     */ 
/*     */   public JForexPeriod(ReversalAmount reversalAmount) {
/*  84 */     this(null, null, null, reversalAmount);
/*     */   }
/*     */ 
/*     */   public JForexPeriod() {
/*  88 */     this(null, null, null, null);
/*     */   }
/*     */ 
/*     */   public JForexPeriod(DataType dataType, Period period, TickBarSize tickBarSize)
/*     */   {
/*  96 */     this(dataType, period, null, null, tickBarSize);
/*     */   }
/*     */ 
/*     */   public Period getPeriod()
/*     */   {
/* 101 */     return this.period;
/*     */   }
/*     */   public void setPeriod(Period period) {
/* 104 */     this.period = period;
/*     */   }
/*     */   public DataType getDataType() {
/* 107 */     return this.dataType;
/*     */   }
/*     */   public void setDataType(DataType dataType) {
/* 110 */     this.dataType = dataType;
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 114 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public void setPriceRange(PriceRange priceRange) {
/* 118 */     this.priceRange = priceRange;
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount() {
/* 122 */     return this.reversalAmount;
/*     */   }
/*     */ 
/*     */   public void setReversalAmount(ReversalAmount reversalAmount) {
/* 126 */     this.reversalAmount = reversalAmount;
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize() {
/* 130 */     return this.tickBarSize;
/*     */   }
/*     */ 
/*     */   public void setTickBarSize(TickBarSize tickBarSize) {
/* 134 */     this.tickBarSize = tickBarSize;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/* 140 */     if ((object instanceof JForexPeriod)) {
/* 141 */       JForexPeriod dtpw = (JForexPeriod)object;
/* 142 */       if ((dtpw.getDataType() != null) && (dtpw.getPeriod() != null) && (dtpw.getDataType().equals(getDataType())) && (dtpw.getPeriod().equals(getPeriod())))
/*     */       {
/* 148 */         if ((DataType.PRICE_RANGE_AGGREGATION.equals(dtpw.getDataType())) || (DataType.RENKO.equals(dtpw.getDataType())))
/*     */         {
/* 152 */           if ((dtpw.getPriceRange() != null) && (dtpw.getPriceRange().equals(getPriceRange()))) {
/* 153 */             return true;
/*     */           }
/*     */         }
/* 156 */         else if (DataType.POINT_AND_FIGURE.equals(dtpw.getDataType())) {
/* 157 */           if ((dtpw.getPriceRange() != null) && (dtpw.getPriceRange().equals(getPriceRange())) && (dtpw.getReversalAmount() != null) && (dtpw.getReversalAmount().equals(getReversalAmount())))
/*     */           {
/* 163 */             return true;
/*     */           }
/*     */         }
/* 166 */         else if (DataType.TICK_BAR.equals(dtpw.getDataType())) {
/* 167 */           if ((dtpw.getTickBarSize() != null) && (dtpw.getTickBarSize().equals(getTickBarSize()))) {
/* 168 */             return true;
/*     */           }
/*     */         }
/*     */         else {
/* 172 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 176 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 182 */     if ((DataType.PRICE_RANGE_AGGREGATION.equals(getDataType())) || (DataType.RENKO.equals(getDataType())))
/*     */     {
/* 186 */       return getPriceRange() == null ? getDataType().toString() : getPriceRange().getName();
/*     */     }
/* 188 */     if (DataType.POINT_AND_FIGURE.equals(getDataType())) {
/* 189 */       return getPeriod() + "(" + getPriceRange() + "; x" + getReversalAmount() + ")";
/*     */     }
/* 191 */     if ((DataType.TICKS.equals(getDataType())) || (DataType.TIME_PERIOD_AGGREGATION.equals(getDataType())))
/*     */     {
/* 195 */       return getPeriod().toString();
/*     */     }
/* 197 */     if (DataType.TICK_BAR.equals(getDataType())) {
/* 198 */       return getTickBarSize() != null ? " " + String.valueOf(getTickBarSize().getSize()) : "";
/*     */     }
/* 200 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 205 */     int prime = 31;
/* 206 */     int result = 1;
/* 207 */     result = 31 * result + (this.dataType == null ? 0 : this.dataType.hashCode());
/* 208 */     result = 31 * result + (this.period == null ? 0 : this.period.hashCode());
/* 209 */     result = 31 * result + (this.priceRange == null ? 0 : this.priceRange.hashCode());
/* 210 */     result = 31 * result + (this.reversalAmount == null ? 0 : this.reversalAmount.hashCode());
/* 211 */     result = 31 * result + (this.tickBarSize == null ? 0 : this.tickBarSize.hashCode());
/* 212 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.JForexPeriod
 * JD-Core Version:    0.6.0
 */