/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TradeEvent extends Event
/*     */ {
/*     */   public static final String ACCOUNT = "account";
/*     */   public static final String ORDER_ID = "order_id";
/*     */   public static final String TRADE_ID = "trade_id";
/*     */   public static final String AMOUNT_PRIMARY = "amount_pri";
/*     */   public static final String PRICE = "price";
/*     */   public static final String AMOUNT_SECONDARY = "amount_sec";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String SIDE = "side";
/*     */   public static final String EXEC_ID = "exec_id";
/*     */   private String account;
/*     */   private String orderId;
/*     */   private String tradeId;
/*     */   private BigDecimal amountPrimary;
/*     */   private BigDecimal price;
/*     */   private BigDecimal amountSecondary;
/*     */   private String instruemnt;
/*     */   private String side;
/*     */   private String execId;
/*     */ 
/*     */   public String getInstruemnt()
/*     */   {
/*  44 */     return this.instruemnt;
/*     */   }
/*     */ 
/*     */   public String getSide() {
/*  48 */     return this.side;
/*     */   }
/*     */ 
/*     */   public String getExecId() {
/*  52 */     return this.execId;
/*     */   }
/*     */ 
/*     */   public TradeEvent(String account, String orderId, String tradeId, BigDecimal amountPrimary, BigDecimal price, BigDecimal amountSecondary)
/*     */   {
/*  64 */     this(account, orderId, tradeId, amountPrimary, price, amountSecondary, null, null, null);
/*     */   }
/*     */   public TradeEvent(String orderId, String tradeId, BigDecimal amountPrimary, BigDecimal price, BigDecimal amountSecondary) {
/*  67 */     this(null, orderId, tradeId, amountPrimary, price, amountSecondary, null, null, null);
/*     */   }
/*     */ 
/*     */   public TradeEvent(String orderId, String tradeId, BigDecimal amountPrimary, BigDecimal price, BigDecimal amountSecondary, String instruemnt, String side, String execId) {
/*  71 */     this(null, orderId, tradeId, amountPrimary, price, amountSecondary, instruemnt, side, execId);
/*     */   }
/*     */ 
/*     */   public TradeEvent(String account, String orderId, String tradeId, BigDecimal amountPrimary, BigDecimal price, BigDecimal amountSecondary, String instruemnt, String side, String execId)
/*     */   {
/*  76 */     this.account = account;
/*  77 */     this.orderId = orderId;
/*  78 */     this.tradeId = tradeId;
/*  79 */     this.amountPrimary = amountPrimary;
/*  80 */     this.price = price;
/*  81 */     this.amountSecondary = amountSecondary;
/*  82 */     this.instruemnt = instruemnt;
/*  83 */     this.side = side;
/*  84 */     this.execId = execId;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getAttributes()
/*     */   {
/*  89 */     Map attributes = new HashMap();
/*  90 */     attributes.put("account", this.account);
/*  91 */     attributes.put("order_id", this.orderId);
/*  92 */     attributes.put("trade_id", this.tradeId);
/*  93 */     attributes.put("amount_pri", this.amountPrimary);
/*  94 */     attributes.put("price", this.price);
/*  95 */     attributes.put("amount_sec", this.amountSecondary);
/*  96 */     if (this.instruemnt != null) {
/*  97 */       attributes.put("instrument", this.instruemnt);
/*  98 */       attributes.put("side", this.side);
/*  99 */       attributes.put("exec_id", this.execId);
/*     */     }
/* 101 */     return attributes;
/*     */   }
/*     */ 
/*     */   public String getAccount() {
/* 105 */     return this.account;
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmountPrimary() {
/* 109 */     return this.amountPrimary;
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmountSecondary() {
/* 113 */     return this.amountSecondary;
/*     */   }
/*     */ 
/*     */   public String getOrderId() {
/* 117 */     return this.orderId;
/*     */   }
/*     */ 
/*     */   public BigDecimal getPrice() {
/* 121 */     return this.price;
/*     */   }
/*     */ 
/*     */   public String getTradeId() {
/* 125 */     return this.tradeId;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.TradeEvent
 * JD-Core Version:    0.6.0
 */