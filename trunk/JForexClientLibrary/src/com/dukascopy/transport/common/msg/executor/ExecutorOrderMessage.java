/*     */ package com.dukascopy.transport.common.msg.executor;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class ExecutorOrderMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "execOrder";
/*     */   public static final String TIME_IN_FORCE = "timeInForce";
/*     */   private static final String ORDER_ID = "orderId";
/*     */   private static final String ROOT_ORDER_ID = "rootId";
/*     */   private static final String INSTRUMENT = "instr";
/*     */   private static final String SIDE = "side";
/*     */   private static final String PRICE = "price";
/*     */   private static final String AMOUNT = "amount";
/*     */   private static final String PARTY_ID = "partyId";
/*     */   public static final String EXECUTOR = "exec";
/*     */   public static final String TRANSACT_TIME = "trans_time";
/*     */ 
/*     */   public ExecutorOrderMessage()
/*     */   {
/*  35 */     setType("execOrder");
/*     */   }
/*     */ 
/*     */   public ExecutorOrderMessage(ProtocolMessage message) {
/*  39 */     super(message);
/*  40 */     setType("execOrder");
/*  41 */     put("timeInForce", message.getString("timeInForce"));
/*  42 */     put("orderId", message.getString("orderId"));
/*  43 */     put("rootId", message.getString("rootId"));
/*  44 */     put("instr", message.getString("instr"));
/*  45 */     put("side", message.getString("side"));
/*  46 */     put("price", message.getString("price"));
/*  47 */     put("amount", message.getString("amount"));
/*  48 */     put("partyId", message.getString("partyId"));
/*  49 */     put("exec", message.getString("exec"));
/*  50 */     put("trans_time", message.getString("trans_time"));
/*     */   }
/*     */ 
/*     */   public TimeInForce getTimeInForce() {
/*  54 */     String tifString = getString("timeInForce");
/*  55 */     if (tifString != null) {
/*  56 */       return TimeInForce.fromString(tifString);
/*     */     }
/*  58 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTimeInForce(TimeInForce tif) {
/*  62 */     if (tif != null)
/*  63 */       put("timeInForce", tif.asString());
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId)
/*     */   {
/*  68 */     if (orderId != null)
/*  69 */       put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/*  74 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setRootOrderId(String rootOrderId) {
/*  78 */     if (rootOrderId != null)
/*  79 */       put("rootId", rootOrderId);
/*     */   }
/*     */ 
/*     */   public String getRootOrderId()
/*     */   {
/*  84 */     return getString("rootId");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  88 */     if (instrument != null)
/*  89 */       put("instr", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/*  94 */     return getString("instr");
/*     */   }
/*     */ 
/*     */   public void setSide(OrderSide side) {
/*  98 */     if (side != null)
/*  99 */       put("side", side.asString());
/*     */   }
/*     */ 
/*     */   public OrderSide getOrderSide()
/*     */   {
/* 104 */     String sideString = getString("side");
/* 105 */     if (sideString != null) {
/* 106 */       return OrderSide.fromString(sideString);
/*     */     }
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPrice(BigDecimal price) {
/* 112 */     if (price != null)
/* 113 */       put("price", price.stripTrailingZeros().toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getPrice()
/*     */   {
/* 118 */     String priceStr = getString("price");
/* 119 */     if (priceStr != null) {
/* 120 */       return new BigDecimal(priceStr);
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAmount(BigDecimal amount) {
/* 126 */     if (amount != null)
/* 127 */       put("amount", amount.stripTrailingZeros().toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount()
/*     */   {
/* 132 */     String amountStr = getString("amount");
/* 133 */     if (amountStr != null) {
/* 134 */       return new BigDecimal(amountStr);
/*     */     }
/* 136 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPartyId(String partyId) {
/* 140 */     if (partyId != null)
/* 141 */       put("partyId", partyId);
/*     */   }
/*     */ 
/*     */   public String getPartyId()
/*     */   {
/* 146 */     return getString("partyId");
/*     */   }
/*     */ 
/*     */   public void setExecutor(String executor) {
/* 150 */     if (executor != null)
/* 151 */       put("exec", executor);
/*     */   }
/*     */ 
/*     */   public String getExecutor()
/*     */   {
/* 156 */     return getString("exec");
/*     */   }
/*     */ 
/*     */   public Date getTransactTime() {
/* 160 */     return getDate("trans_time");
/*     */   }
/*     */ 
/*     */   public void setTransactTime(Date time) {
/* 164 */     putDate("trans_time", time);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.executor.ExecutorOrderMessage
 * JD-Core Version:    0.6.0
 */