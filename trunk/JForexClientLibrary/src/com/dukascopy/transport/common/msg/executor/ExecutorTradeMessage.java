/*     */ package com.dukascopy.transport.common.msg.executor;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class ExecutorTradeMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "execTrade";
/*     */   private static final String DEAL_TYPE = "dealType";
/*     */   private static final String ROOT_ID = "rootId";
/*     */   private static final String ORDER_ID = "orderId";
/*     */   private static final String TRADE_ID = "tradeId";
/*     */   private static final String EXECUTOR_ID = "execId";
/*     */   private static final String BROKER_ID = "brokId";
/*     */   private static final String PRIMEBROKER_ID = "pbId";
/*     */   private static final String INSTRUMENT = "instr";
/*     */   private static final String SIDE = "side";
/*     */   private static final String EXECUTION_PRICE = "price";
/*     */   private static final String EXECUTED_AMOUNT = "amount";
/*     */   private static final String SECONDARY_AMOUNT = "secAmount";
/*     */   private static final String COUNTERPARTY = "counterparty";
/*     */   private static final String VALUE_DATE = "valueDate";
/*     */   private static final String TRADE_DATE = "tradeDate";
/*     */   private static final String TRANSACT_TIME = "transactTime";
/*     */   private static final String USER_ID = "userId";
/*     */ 
/*     */   public ExecutorTradeMessage()
/*     */   {
/*  50 */     setType("execTrade");
/*     */   }
/*     */ 
/*     */   public ExecutorTradeMessage(ProtocolMessage message) {
/*  54 */     super(message);
/*  55 */     setType("execTrade");
/*  56 */     put("dealType", message.getString("dealType"));
/*  57 */     put("rootId", message.getString("rootId"));
/*  58 */     put("orderId", message.getString("orderId"));
/*  59 */     put("tradeId", message.getString("tradeId"));
/*  60 */     put("execId", message.getString("execId"));
/*  61 */     put("brokId", message.getString("brokId"));
/*  62 */     put("pbId", message.getString("pbId"));
/*  63 */     put("instr", message.getString("instr"));
/*  64 */     put("side", message.getString("side"));
/*  65 */     put("price", message.getString("price"));
/*  66 */     put("amount", message.getString("amount"));
/*  67 */     put("secAmount", message.getString("secAmount"));
/*  68 */     put("counterparty", message.getString("counterparty"));
/*  69 */     put("valueDate", message.getString("valueDate"));
/*  70 */     put("tradeDate", message.getString("tradeDate"));
/*  71 */     put("transactTime", message.getString("transactTime"));
/*  72 */     put("userId", message.getString("userId"));
/*     */   }
/*     */ 
/*     */   public void setDealType(DealType dealType) {
/*  76 */     if (dealType != null)
/*  77 */       put("dealType", dealType.toString());
/*     */   }
/*     */ 
/*     */   public DealType getDealType()
/*     */   {
/*  82 */     String type = getString("dealType");
/*  83 */     if (type != null) {
/*  84 */       return DealType.valueOf(type);
/*     */     }
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public void setRootOrderId(String rootId) {
/*  90 */     if (rootId != null)
/*  91 */       put("rootId", rootId);
/*     */   }
/*     */ 
/*     */   public String getRootOrderId()
/*     */   {
/*  96 */     return getString("rootId");
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId) {
/* 100 */     if (orderId != null)
/* 101 */       put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/* 106 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setTradeId(String tradeId) {
/* 110 */     if (tradeId != null)
/* 111 */       put("tradeId", tradeId);
/*     */   }
/*     */ 
/*     */   public String getTradeId()
/*     */   {
/* 116 */     return getString("tradeId");
/*     */   }
/*     */ 
/*     */   public void setExecutorId(String executorId) {
/* 120 */     if (executorId != null)
/* 121 */       put("execId", executorId);
/*     */   }
/*     */ 
/*     */   public String getExecutorId()
/*     */   {
/* 126 */     return getString("execId");
/*     */   }
/*     */ 
/*     */   public void setBrokerId(String brokerId) {
/* 130 */     if (brokerId != null)
/* 131 */       put("brokId", brokerId);
/*     */   }
/*     */ 
/*     */   public String getBrokerId()
/*     */   {
/* 136 */     return getString("brokId");
/*     */   }
/*     */ 
/*     */   public void setPrimeBrokerId(String primeBrokerId) {
/* 140 */     if (primeBrokerId != null)
/* 141 */       put("pbId", primeBrokerId);
/*     */   }
/*     */ 
/*     */   public String getPrimeBrokerId()
/*     */   {
/* 146 */     return getString("pbId");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 150 */     if (instrument != null)
/* 151 */       put("instr", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 156 */     return getString("instr");
/*     */   }
/*     */ 
/*     */   public void setSide(OrderSide side) {
/* 160 */     if (side != null)
/* 161 */       put("side", side.asString());
/*     */   }
/*     */ 
/*     */   public OrderSide getOrderSide()
/*     */   {
/* 166 */     String sideString = getString("side");
/* 167 */     if (sideString != null) {
/* 168 */       return OrderSide.fromString(sideString);
/*     */     }
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   public void setExecutionPrice(BigDecimal executionPrice) {
/* 174 */     if (executionPrice != null)
/* 175 */       put("price", executionPrice.stripTrailingZeros().toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getExecutionPrice()
/*     */   {
/* 180 */     String priceStr = getString("price");
/* 181 */     if (priceStr != null) {
/* 182 */       return new BigDecimal(priceStr);
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   public void setExecutedAmount(BigDecimal amount) {
/* 188 */     if (amount != null)
/* 189 */       put("amount", amount.stripTrailingZeros().toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getExecutedAmount()
/*     */   {
/* 194 */     String amountStr = getString("amount");
/* 195 */     if (amountStr != null) {
/* 196 */       return new BigDecimal(amountStr);
/*     */     }
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSecondaryAmount(BigDecimal amount) {
/* 202 */     if (amount != null)
/* 203 */       put("secAmount", amount.stripTrailingZeros().toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getSecondaryAmount()
/*     */   {
/* 208 */     String amountStr = getString("secAmount");
/* 209 */     if (amountStr != null) {
/* 210 */       return new BigDecimal(amountStr);
/*     */     }
/* 212 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCounterparty(String counterparty) {
/* 216 */     if (counterparty != null)
/* 217 */       put("counterparty", counterparty);
/*     */   }
/*     */ 
/*     */   public String getCounterparty()
/*     */   {
/* 222 */     return getString("counterparty");
/*     */   }
/*     */ 
/*     */   public void setValueDate(Date valueDate) {
/* 226 */     putDate("valueDate", valueDate);
/*     */   }
/*     */ 
/*     */   public Date getValueDate() {
/* 230 */     return getDate("valueDate");
/*     */   }
/*     */ 
/*     */   public void setTradeDate(Date tradeDate) {
/* 234 */     putDate("tradeDate", tradeDate);
/*     */   }
/*     */ 
/*     */   public Date getTradeDate() {
/* 238 */     return getDate("tradeDate");
/*     */   }
/*     */ 
/*     */   public void setTransactTime(Date transactTime) {
/* 242 */     putDate("transactTime", transactTime);
/*     */   }
/*     */ 
/*     */   public Date getTransactTime() {
/* 246 */     return getDate("transactTime");
/*     */   }
/*     */ 
/*     */   public void setUserId(String userId) {
/* 250 */     if (userId != null)
/* 251 */       put("userId", userId);
/*     */   }
/*     */ 
/*     */   public String getUserId()
/*     */   {
/* 256 */     return getString("userId");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.executor.ExecutorTradeMessage
 * JD-Core Version:    0.6.0
 */