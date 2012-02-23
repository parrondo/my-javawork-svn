/*     */ package com.dukascopy.charts.mappers.time;
/*     */ 
/*     */ public class GeometryCalculator
/*     */ {
/*     */   private int dataUnitWidth;
/*     */   private int dataUnitWidthWithoutOverhead;
/*     */   private int paneWidth;
/*     */   private int dataUnitsCount;
/*     */ 
/*     */   public GeometryCalculator()
/*     */   {
/*  14 */     this(-1);
/*     */   }
/*     */ 
/*     */   public GeometryCalculator(int dataUnitWidth)
/*     */   {
/*  24 */     this.dataUnitWidth = (dataUnitWidth > 0 ? dataUnitWidth : getDefaultDataUnitsWidth());
/*  25 */     this.dataUnitWidthWithoutOverhead = getDefaultDataWidthWithoutOverhead(this.dataUnitWidth);
/*     */   }
/*     */ 
/*     */   private int getDefaultDataUnitsWidth() {
/*  29 */     return 5;
/*     */   }
/*     */ 
/*     */   private int getDefaultDataWidthWithoutOverhead(int dataUnitWidth) {
/*  33 */     return dataUnitWidth - 2;
/*     */   }
/*     */ 
/*     */   public int getDataUnitWidthWithoutOverhead() {
/*  37 */     return this.dataUnitWidthWithoutOverhead;
/*     */   }
/*     */ 
/*     */   public int getDataUnitWidth() {
/*  41 */     return this.dataUnitWidth;
/*     */   }
/*     */ 
/*     */   public int getDataUnitsCount() {
/*  45 */     return this.dataUnitsCount;
/*     */   }
/*     */ 
/*     */   public int getPaneWidth() {
/*  49 */     return this.paneWidth;
/*     */   }
/*     */ 
/*     */   public boolean decreaseDataUnitWidth() {
/*  53 */     if (this.dataUnitWidth <= 1) {
/*  54 */       return false;
/*     */     }
/*     */ 
/*  57 */     int newDataUnitsCount = Math.round(this.paneWidth / (this.dataUnitWidth - 2));
/*  58 */     if (newDataUnitsCount >= 2000) {
/*  59 */       return false;
/*     */     }
/*     */ 
/*  62 */     if (this.dataUnitWidth <= 1)
/*  63 */       this.dataUnitWidth = 1;
/*     */     else {
/*  65 */       this.dataUnitWidth -= 2;
/*     */     }
/*     */ 
/*  68 */     this.dataUnitWidthWithoutOverhead = calculateDataUnitWidthWithoutOverhead();
/*  69 */     calculateDataUnitsCount();
/*  70 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean increaseDataUnitWidth() {
/*  74 */     if (this.dataUnitWidth >= 73) {
/*  75 */       return false;
/*     */     }
/*     */ 
/*  78 */     int newDataUnitsCount = Math.round(this.paneWidth / (this.dataUnitWidth + 2));
/*  79 */     if (newDataUnitsCount <= 10) {
/*  80 */       return false;
/*     */     }
/*     */ 
/*  83 */     if (this.dataUnitWidth >= 73.0F)
/*  84 */       this.dataUnitWidth = 73;
/*     */     else {
/*  86 */       this.dataUnitWidth += 2;
/*     */     }
/*     */ 
/*  89 */     this.dataUnitWidthWithoutOverhead = calculateDataUnitWidthWithoutOverhead();
/*  90 */     calculateDataUnitsCount();
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   public void changeCandleWidth(int wheelRotationInPx) {
/*  95 */     if (wheelRotationInPx > 0)
/*  96 */       increaseDataUnitWidth();
/*  97 */     else if (wheelRotationInPx < 0)
/*  98 */       decreaseDataUnitWidth();
/*     */   }
/*     */ 
/*     */   public void setNewCandlesCountAndRecalculateWidth(int newDataUnitsCount)
/*     */   {
/* 103 */     this.dataUnitsCount = recalculate(newDataUnitsCount);
/*     */   }
/*     */ 
/*     */   public int getOptimalCandlesCount(int desiredCandlesCount) {
/* 107 */     int candleWidth = this.paneWidth / desiredCandlesCount;
/* 108 */     if (candleWidth == 0) {
/* 109 */       candleWidth = 1;
/*     */     }
/* 111 */     int optimalCandlesCount = this.paneWidth / candleWidth;
/* 112 */     recalculate(optimalCandlesCount);
/* 113 */     calculateDataUnitsCount();
/* 114 */     return this.dataUnitsCount;
/*     */   }
/*     */ 
/*     */   public int recalculate(int newDataUnitsCount)
/*     */   {
/* 120 */     int newDataUnitWidth = this.paneWidth / newDataUnitsCount;
/*     */ 
/* 122 */     if (newDataUnitWidth < 1) {
/* 123 */       if (this.dataUnitWidth <= 1) {
/* 124 */         return this.dataUnitsCount;
/*     */       }
/* 126 */       newDataUnitWidth = 1;
/* 127 */       newDataUnitsCount = (int)Math.ceil(this.paneWidth / newDataUnitWidth);
/* 128 */     } else if (newDataUnitWidth > 73) {
/* 129 */       if (this.dataUnitWidth >= 73) {
/* 130 */         return this.dataUnitsCount;
/*     */       }
/* 132 */       newDataUnitWidth = 73;
/* 133 */       newDataUnitsCount = (int)Math.ceil(this.paneWidth / newDataUnitWidth);
/*     */     }
/*     */ 
/* 136 */     if (newDataUnitWidth % 2 == 0) {
/* 137 */       newDataUnitWidth--;
/* 138 */       newDataUnitsCount = (int)Math.floor(this.paneWidth / newDataUnitWidth);
/*     */     }
/*     */ 
/* 141 */     this.dataUnitWidth = newDataUnitWidth;
/* 142 */     this.dataUnitWidthWithoutOverhead = calculateDataUnitWidthWithoutOverhead();
/*     */ 
/* 144 */     return newDataUnitsCount;
/*     */   }
/*     */ 
/*     */   public void paneWidthChanged(int paneWidth) {
/* 148 */     this.paneWidth = paneWidth;
/* 149 */     calculateDataUnitsCount();
/*     */   }
/*     */ 
/*     */   public void calculateDataUnitsCount() {
/* 153 */     int newCandlesCount = (int)Math.ceil(this.paneWidth / this.dataUnitWidth);
/* 154 */     if (newCandlesCount > 2000)
/* 155 */       this.dataUnitsCount = recalculate(2000);
/*     */     else
/* 157 */       this.dataUnitsCount = newCandlesCount;
/*     */   }
/*     */ 
/*     */   private int calculateDataUnitWidthWithoutOverhead()
/*     */   {
/* 162 */     return Math.max(1, this.dataUnitWidth - 2 * (1 + this.dataUnitWidth / 10));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 169 */     return "units : " + this.dataUnitsCount + " " + "unit width : " + this.dataUnitWidth + "/" + this.dataUnitWidthWithoutOverhead + " " + "pane width : " + this.paneWidth;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.GeometryCalculator
 * JD-Core Version:    0.6.0
 */