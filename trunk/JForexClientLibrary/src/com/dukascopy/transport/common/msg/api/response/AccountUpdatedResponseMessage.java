/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class AccountUpdatedResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "account";
/*    */ 
/*    */   public AccountUpdatedResponseMessage()
/*    */   {
/* 22 */     setType("account");
/*    */   }
/*    */ 
/*    */   public AccountUpdatedResponseMessage(ProtocolMessage message)
/*    */   {
/* 31 */     super(message);
/*    */ 
/* 33 */     setType("account");
/*    */ 
/* 35 */     setBalance(message.getBigDecimal("balance"));
/* 36 */     setUsedMargin(message.getBigDecimal("usedMargin"));
/* 37 */     setMarginCall(message.getBool("marginCall"));
/* 38 */     setEquity(message.getBigDecimal("equity"));
/* 39 */     setAccountName(message.getString("acountName"));
/* 40 */     setLeverage(message.getInteger("leverage"));
/* 41 */     setLotSize(message.getBigDecimal("lotSize"));
/*    */   }
/*    */ 
/*    */   public void setAccountName(String name) {
/* 45 */     put("acountName", name);
/*    */   }
/*    */ 
/*    */   public String getAccountNamey() {
/* 49 */     return getString("acountName");
/*    */   }
/*    */ 
/*    */   public BigDecimal getEquity() {
/* 53 */     return getBigDecimal("equity");
/*    */   }
/*    */ 
/*    */   public void setEquity(BigDecimal equity) {
/* 57 */     put("equity", equity);
/*    */   }
/*    */ 
/*    */   public Integer getLeverage() {
/* 61 */     return getInteger("leverage");
/*    */   }
/*    */ 
/*    */   public void setLeverage(Integer leverage) {
/* 65 */     put("leverage", leverage);
/*    */   }
/*    */ 
/*    */   public BigDecimal getLotSize() {
/* 69 */     return getBigDecimal("lotSize");
/*    */   }
/*    */ 
/*    */   public void setLotSize(BigDecimal lotSize) {
/* 73 */     put("lotSize", lotSize);
/*    */   }
/*    */ 
/*    */   public void setBalance(BigDecimal balance) {
/* 77 */     put("balance", balance);
/*    */   }
/*    */ 
/*    */   public BigDecimal getBalance() {
/* 81 */     return getBigDecimal("balance");
/*    */   }
/*    */ 
/*    */   public void setUsedMargin(BigDecimal usedMargin) {
/* 85 */     put("usedMargin", usedMargin);
/*    */   }
/*    */ 
/*    */   public BigDecimal getUsedMargin() {
/* 89 */     return getBigDecimal("usedMargin");
/*    */   }
/*    */ 
/*    */   public void setMarginCall(Boolean marginCall) {
/* 93 */     put("marginCall", marginCall);
/*    */   }
/*    */ 
/*    */   public Boolean getMarginCall() {
/* 97 */     return getBool("marginCall");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.AccountUpdatedResponseMessage
 * JD-Core Version:    0.6.0
 */