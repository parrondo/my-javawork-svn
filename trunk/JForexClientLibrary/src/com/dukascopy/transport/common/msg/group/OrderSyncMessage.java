/*     */ package com.dukascopy.transport.common.msg.group;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class OrderSyncMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "ordr_sync";
/*     */   public static final String ORDERS_IDS = "ordr_ids";
/*     */   public static final String POSITION_IDS = "pos_ids";
/*     */   public static final String IS_INIT_MESSAGE = "init_msg";
/*     */ 
/*     */   public OrderSyncMessage()
/*     */   {
/*  23 */     setType("ordr_sync");
/*  24 */     put("init_msg", false);
/*     */   }
/*     */ 
/*     */   public OrderSyncMessage(ProtocolMessage message) {
/*  28 */     super(message);
/*  29 */     setType("ordr_sync");
/*  30 */     put("ordr_ids", message.getString("ordr_ids"));
/*  31 */     put("pos_ids", message.getString("pos_ids"));
/*  32 */     put("init_msg", message.getBoolean("init_msg"));
/*     */   }
/*     */ 
/*     */   public Collection<String> getOrderIds() {
/*  36 */     Collection orders = new ArrayList();
/*  37 */     String ordersString = getString("ordr_ids");
/*  38 */     if (ordersString == null)
/*     */     {
/*  40 */       return Collections.EMPTY_LIST;
/*     */     }
/*  42 */     StringTokenizer tokenizer = new StringTokenizer(ordersString, ";");
/*  43 */     while (tokenizer.hasMoreTokens()) {
/*  44 */       orders.add(tokenizer.nextToken());
/*     */     }
/*  46 */     return orders;
/*     */   }
/*     */ 
/*     */   public Collection<String> getPositionIds() {
/*  50 */     Collection positions = new ArrayList();
/*  51 */     String positionString = getString("pos_ids");
/*  52 */     if (positionString == null)
/*     */     {
/*  54 */       return Collections.EMPTY_LIST;
/*     */     }
/*  56 */     StringTokenizer tokenizer = new StringTokenizer(positionString, ";");
/*  57 */     while (tokenizer.hasMoreTokens()) {
/*  58 */       positions.add(tokenizer.nextToken());
/*     */     }
/*  60 */     return positions;
/*     */   }
/*     */ 
/*     */   public String getOrderIdsAsString() {
/*  64 */     String orderIds = getString("ordr_ids");
/*  65 */     return orderIds == null ? "" : orderIds;
/*     */   }
/*     */ 
/*     */   public String getPositionIdsAsString() {
/*  69 */     String positionIds = getString("pos_ids");
/*  70 */     return positionIds == null ? "" : positionIds;
/*     */   }
/*     */ 
/*     */   public void setOrdersIds(String ordersIds) {
/*  74 */     put("ordr_ids", ordersIds);
/*     */   }
/*     */ 
/*     */   public void setPositionIds(String positionIds) {
/*  78 */     put("pos_ids", positionIds);
/*     */   }
/*     */ 
/*     */   public void addOrderId(String orderId) {
/*  82 */     String orderIds = getString("ordr_ids");
/*  83 */     if (orderIds == null) {
/*  84 */       put("ordr_ids", orderId);
/*     */     } else {
/*  86 */       orderIds = orderIds + ";" + orderId;
/*  87 */       put("ordr_ids", orderIds);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addPositionId(String positionId) {
/*  92 */     String positionIds = getString("pos_ids");
/*  93 */     if (positionIds == null) {
/*  94 */       put("pos_ids", positionId);
/*     */     } else {
/*  96 */       positionIds = positionIds + ";" + positionId;
/*  97 */       put("pos_ids", positionIds);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInitMessage(Boolean isInit) {
/* 102 */     put("init_msg", isInit);
/*     */   }
/*     */ 
/*     */   public Boolean isInitMessage() {
/* 106 */     return Boolean.valueOf(getBoolean("init_msg"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.group.OrderSyncMessage
 * JD-Core Version:    0.6.0
 */