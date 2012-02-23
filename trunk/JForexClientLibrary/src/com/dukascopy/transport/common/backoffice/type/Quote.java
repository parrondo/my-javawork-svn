/*    */ package com.dukascopy.transport.common.backoffice.type;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class Quote
/*    */ {
/*    */   private int quoteId;
/*    */   private String instrument;
/*    */   private BigDecimal bid;
/*    */   private BigDecimal ask;
/*    */   private BigDecimal pipPrice;
/*    */   private String tradeStatus;
/*    */   private int type;
/*    */ 
/*    */   public void setQuoteId(int quoteId)
/*    */   {
/* 28 */     this.quoteId = quoteId;
/*    */   }
/*    */ 
/*    */   public int getQuoteId() {
/* 32 */     return this.quoteId;
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 36 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public String getInstrument() {
/* 40 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public void setBid(BigDecimal bid) {
/* 44 */     this.bid = bid;
/*    */   }
/*    */ 
/*    */   public BigDecimal getBid() {
/* 48 */     return this.bid;
/*    */   }
/*    */ 
/*    */   public void setAsk(BigDecimal ask) {
/* 52 */     this.ask = ask;
/*    */   }
/*    */ 
/*    */   public BigDecimal getAsk() {
/* 56 */     return this.ask;
/*    */   }
/*    */ 
/*    */   public void setPipPrice(BigDecimal pipPrice) {
/* 60 */     this.pipPrice = pipPrice;
/*    */   }
/*    */ 
/*    */   public BigDecimal getPipPrice() {
/* 64 */     return this.pipPrice;
/*    */   }
/*    */ 
/*    */   public void setTradeStatus(String tradeStatus) {
/* 68 */     this.tradeStatus = tradeStatus;
/*    */   }
/*    */ 
/*    */   public String getTradeStatus() {
/* 72 */     return this.tradeStatus;
/*    */   }
/*    */ 
/*    */   public void setType(int type) {
/* 76 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public int getType() {
/* 80 */     return this.type;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 84 */     StringBuffer sb = new StringBuffer("Quote={quoteId=");
/* 85 */     sb.append(getQuoteId()).append(",instrument=").append(getInstrument());
/* 86 */     sb.append(",bid=").append(getBid());
/* 87 */     sb.append(",ask=").append(getAsk());
/* 88 */     sb.append(",pipPrice=").append(getPipPrice());
/* 89 */     sb.append(",tradeStatus=").append(getTradeStatus());
/* 90 */     sb.append(",type=").append(getType());
/* 91 */     sb.append("}");
/* 92 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.backoffice.type.Quote
 * JD-Core Version:    0.6.0
 */