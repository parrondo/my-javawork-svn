/*     */ package com.dukascopy.transport.common.backoffice.type;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class Position
/*     */ {
/*     */   private int positionId;
/*     */   private PositionState state;
/*     */   private BigDecimal lots;
/*     */   private String instrument;
/*     */   private int quoteId;
/*     */   private boolean isUp;
/*     */   private Date date;
/*     */   private BigDecimal openPrice;
/*     */   private BigDecimal closePrice;
/*     */   private BigDecimal takeProfitPrice;
/*     */   private Integer takeProfitPips;
/*     */   private BigDecimal stopLossPrice;
/*     */   private Integer stopLossPips;
/*     */   private BigDecimal profit;
/*     */   private BigDecimal commissions;
/*     */ 
/*     */   public void setPositionId(int positionId)
/*     */   {
/*  37 */     this.positionId = positionId;
/*     */   }
/*     */ 
/*     */   public int getPositionId() {
/*  41 */     return this.positionId;
/*     */   }
/*     */ 
/*     */   public void setState(PositionState state) {
/*  45 */     this.state = state;
/*     */   }
/*     */ 
/*     */   public PositionState getState() {
/*  49 */     return this.state;
/*     */   }
/*     */ 
/*     */   public void setLots(BigDecimal lots) {
/*  53 */     this.lots = lots;
/*     */   }
/*     */ 
/*     */   public BigDecimal getLots() {
/*  57 */     return this.lots;
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  61 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  65 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setQuoteId(int quoteId) {
/*  69 */     this.quoteId = quoteId;
/*     */   }
/*     */ 
/*     */   public int getQuoteId() {
/*  73 */     return this.quoteId;
/*     */   }
/*     */ 
/*     */   public void setIsUp(boolean isUp) {
/*  77 */     this.isUp = isUp;
/*     */   }
/*     */ 
/*     */   public boolean isUp() {
/*  81 */     return this.isUp;
/*     */   }
/*     */ 
/*     */   public void setDate(Date date) {
/*  85 */     this.date = date;
/*     */   }
/*     */ 
/*     */   public Date getDate() {
/*  89 */     return this.date;
/*     */   }
/*     */ 
/*     */   public void setOpenPrice(BigDecimal openPrice) {
/*  93 */     this.openPrice = openPrice;
/*     */   }
/*     */ 
/*     */   public BigDecimal getOpenPrice() {
/*  97 */     return this.openPrice;
/*     */   }
/*     */ 
/*     */   public void setClosePrice(BigDecimal closePrice) {
/* 101 */     this.closePrice = closePrice;
/*     */   }
/*     */ 
/*     */   public BigDecimal getClosePrice() {
/* 105 */     return this.closePrice;
/*     */   }
/*     */ 
/*     */   public void setTakeProfitPrice(BigDecimal takeProfitPrice) {
/* 109 */     this.takeProfitPrice = takeProfitPrice;
/*     */   }
/*     */ 
/*     */   public BigDecimal getTakeProfitPrice() {
/* 113 */     return this.takeProfitPrice;
/*     */   }
/*     */ 
/*     */   public void setTakeProfitPips(Integer takeProfitPips) {
/* 117 */     this.takeProfitPips = takeProfitPips;
/*     */   }
/*     */ 
/*     */   public Integer getTakeProfitPips() {
/* 121 */     return this.takeProfitPips;
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(BigDecimal stopLossPrice) {
/* 125 */     this.stopLossPrice = stopLossPrice;
/*     */   }
/*     */ 
/*     */   public BigDecimal getStopLossPrice() {
/* 129 */     return this.stopLossPrice;
/*     */   }
/*     */ 
/*     */   public void setStopLossPips(Integer stopLossPips) {
/* 133 */     this.stopLossPips = stopLossPips;
/*     */   }
/*     */ 
/*     */   public Integer getStopLossPips() {
/* 137 */     return this.stopLossPips;
/*     */   }
/*     */ 
/*     */   public void setProfit(BigDecimal profit) {
/* 141 */     this.profit = profit;
/*     */   }
/*     */ 
/*     */   public BigDecimal getProfit() {
/* 145 */     return this.profit;
/*     */   }
/*     */ 
/*     */   public void setCommissions(BigDecimal commissions) {
/* 149 */     this.commissions = commissions;
/*     */   }
/*     */ 
/*     */   public BigDecimal getCommissions() {
/* 153 */     return this.commissions;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 157 */     StringBuffer sb = new StringBuffer();
/* 158 */     sb.append(getState()).append(",");
/* 159 */     sb.append(getLots()).append(",");
/* 160 */     sb.append(getInstrument()).append(",");
/* 161 */     sb.append(isUp() ? "true" : "false").append(",");
/* 162 */     sb.append(getTakeProfitPrice()).append(",");
/* 163 */     sb.append(getStopLossPrice());
/* 164 */     return sb.toString().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 168 */     StringBuffer sb = new StringBuffer("Position={positionId=");
/* 169 */     sb.append(getPositionId()).append(",state=").append(getState()).append(",lots=").append(getLots());
/* 170 */     sb.append(",instrument=").append(getInstrument()).append(",isUp=").append(isUp()).append(",date=").append(this.date);
/* 171 */     sb.append(",openPrice=").append(getOpenPrice()).append(",closePrice=").append(getClosePrice());
/* 172 */     sb.append(",takeProfitPrice=").append(getTakeProfitPrice()).append(",takeProfitPips=").append(getTakeProfitPips());
/* 173 */     sb.append(",stopLossPrice=").append(getStopLossPrice()).append(",stopLossPips=").append(getStopLossPips());
/* 174 */     sb.append(",profit=").append(getProfit()).append(",commissions=").append(getCommissions()).append("}");
/* 175 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Position position) {
/* 179 */     return hashCode() == position.hashCode();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.backoffice.type.Position
 * JD-Core Version:    0.6.0
 */