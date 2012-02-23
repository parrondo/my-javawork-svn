/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ChangeOrderConditionEvent extends Event
/*     */ {
/*     */   public static final String EVENT_ID = "event_id";
/*     */   public static final String ORDER_ID = "order_id";
/*     */   public static final String OLD_PRICE_STOP = "old_price_stop";
/*     */   public static final String OLD_PRICE_TRAILING_LIMIT = "old_price_trailing_limit";
/*     */   public static final String OLD_EXEC_TIMEOUT_MILLIS = "old_exec_timeout_millis";
/*     */   public static final String OLD_STOP_DIRECTION = "old_stop_direction";
/*     */   public static final String PRICE_STOP = "price_stop";
/*     */   public static final String PRICE_TRAILING_LIMIT = "price_trailing_limit";
/*     */   public static final String EXEC_TIMEOUT_MILLIS = "exec_timeout_millis";
/*     */   public static final String STOP_DIRECTION = "stop_direction";
/*     */   private String orderId;
/*     */   private BigDecimal oldPriceStop;
/*     */   private BigDecimal oldPriceTrailingLimit;
/*     */   private Long oldExecTimeoutMillis;
/*     */   private StopDirection oldStopDirection;
/*     */   private BigDecimal priceStop;
/*     */   private BigDecimal priceTrailingLimit;
/*     */   private Long execTimeoutMillis;
/*     */   private StopDirection stopDirection;
/*     */ 
/*     */   public ChangeOrderConditionEvent(OrderMessage order)
/*     */   {
/*  88 */     this(order.getOrderId(), order.getPriceStop() == null ? null : order.getPriceStop().getValue(), order.getPriceTrailingLimit() == null ? null : order.getPriceTrailingLimit().getValue(), order.getExecTimeoutMillis(), order.getStopDirection());
/*     */   }
/*     */ 
/*     */   public ChangeOrderConditionEvent(String orderId, BigDecimal priceStop, BigDecimal priceTrailingLimit, Long execTimeoutMillis, StopDirection stopDirection) {
/*  92 */     this(orderId, null, null, null, null, priceStop, priceTrailingLimit, execTimeoutMillis, stopDirection);
/*     */   }
/*     */ 
/*     */   public ChangeOrderConditionEvent(String orderId, BigDecimal oldPriceStop, BigDecimal oldPriceTrailingLimit, Long oldExecTimeoutMillis, StopDirection oldStopDirection, BigDecimal priceStop, BigDecimal priceTrailingLimit, Long execTimeoutMillis, StopDirection stopDirection)
/*     */   {
/*  97 */     this();
/*  98 */     this.orderId = orderId;
/*  99 */     this.oldPriceStop = oldPriceStop;
/* 100 */     this.oldPriceTrailingLimit = oldPriceTrailingLimit;
/* 101 */     this.oldExecTimeoutMillis = oldExecTimeoutMillis;
/* 102 */     this.oldStopDirection = oldStopDirection;
/* 103 */     this.priceStop = priceStop;
/* 104 */     this.priceTrailingLimit = priceTrailingLimit;
/* 105 */     this.execTimeoutMillis = execTimeoutMillis;
/* 106 */     this.stopDirection = stopDirection;
/*     */   }
/*     */ 
/*     */   protected ChangeOrderConditionEvent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ChangeOrderConditionEvent(String orderId) {
/* 114 */     this();
/* 115 */     this.orderId = orderId;
/*     */   }
/*     */ 
/*     */   public Long getExecTimeoutMillis()
/*     */   {
/* 122 */     return this.execTimeoutMillis;
/*     */   }
/*     */ 
/*     */   public void setExecTimeoutMillis(Long execTimeoutMillis)
/*     */   {
/* 130 */     this.execTimeoutMillis = execTimeoutMillis;
/*     */   }
/*     */ 
/*     */   public Long getOldExecTimeoutMillis()
/*     */   {
/* 137 */     return this.oldExecTimeoutMillis;
/*     */   }
/*     */ 
/*     */   public void setOldExecTimeoutMillis(Long oldExecTimeoutMillis)
/*     */   {
/* 145 */     this.oldExecTimeoutMillis = oldExecTimeoutMillis;
/*     */   }
/*     */ 
/*     */   public BigDecimal getOldPriceStop()
/*     */   {
/* 152 */     return this.oldPriceStop;
/*     */   }
/*     */ 
/*     */   public void setOldPriceStop(BigDecimal oldPriceStop)
/*     */   {
/* 160 */     this.oldPriceStop = oldPriceStop;
/*     */   }
/*     */ 
/*     */   public BigDecimal getOldPriceTrailingLimit()
/*     */   {
/* 167 */     return this.oldPriceTrailingLimit;
/*     */   }
/*     */ 
/*     */   public void setOldPriceTrailingLimit(BigDecimal oldPriceTrailingLimit)
/*     */   {
/* 175 */     this.oldPriceTrailingLimit = oldPriceTrailingLimit;
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/* 182 */     return this.orderId;
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId)
/*     */   {
/* 190 */     this.orderId = orderId;
/*     */   }
/*     */ 
/*     */   public BigDecimal getPriceStop()
/*     */   {
/* 197 */     return this.priceStop;
/*     */   }
/*     */ 
/*     */   public void setPriceStop(BigDecimal priceStop)
/*     */   {
/* 205 */     this.priceStop = priceStop;
/*     */   }
/*     */ 
/*     */   public BigDecimal getPriceTrailingLimit()
/*     */   {
/* 212 */     return this.priceTrailingLimit;
/*     */   }
/*     */ 
/*     */   public void setPriceTrailingLimit(BigDecimal priceTrailingLimit)
/*     */   {
/* 220 */     this.priceTrailingLimit = priceTrailingLimit;
/*     */   }
/*     */ 
/*     */   public StopDirection getOldStopDirection() {
/* 224 */     return this.oldStopDirection;
/*     */   }
/*     */ 
/*     */   public void setOldStopDirection(StopDirection oldStopDirection) {
/* 228 */     this.oldStopDirection = oldStopDirection;
/*     */   }
/*     */ 
/*     */   public StopDirection getStopDirection() {
/* 232 */     return this.stopDirection;
/*     */   }
/*     */ 
/*     */   public void setStopDirection(StopDirection stopDirection) {
/* 236 */     this.stopDirection = stopDirection;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getAttributes()
/*     */   {
/* 241 */     Map attributes = new HashMap();
/* 242 */     attributes.put("event_id", Long.valueOf(super.getId()));
/* 243 */     attributes.put("order_id", this.orderId);
/* 244 */     attributes.put("old_price_stop", this.oldPriceStop);
/* 245 */     attributes.put("old_price_trailing_limit", this.oldPriceTrailingLimit);
/* 246 */     attributes.put("old_exec_timeout_millis", this.oldExecTimeoutMillis);
/* 247 */     attributes.put("old_stop_direction", this.oldStopDirection);
/* 248 */     attributes.put("price_stop", this.priceStop);
/* 249 */     attributes.put("price_trailing_limit", this.priceTrailingLimit);
/* 250 */     attributes.put("exec_timeout_millis", this.execTimeoutMillis);
/* 251 */     attributes.put("stop_direction", this.stopDirection);
/* 252 */     return Collections.unmodifiableMap(attributes);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.ChangeOrderConditionEvent
 * JD-Core Version:    0.6.0
 */