/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class MergePositionsMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "mergePositions";
/*     */   public static final String LIST = "list";
/*     */   public static final String NEW_ORD_GR_ID = "new_id";
/*     */   public static final String EXTERNAL_SYS_ID = "extSysId";
/*     */   public static final String STRATEGY_ID = "strategyId";
/*     */   public static final String DELIMETER = ";";
/*     */ 
/*     */   public MergePositionsMessage()
/*     */   {
/*  29 */     setType("mergePositions");
/*     */   }
/*     */ 
/*     */   public MergePositionsMessage(ProtocolMessage message) {
/*  33 */     super(message);
/*  34 */     setType("mergePositions");
/*  35 */     put("list", message.getString("list"));
/*  36 */     put("new_id", message.getString("new_id"));
/*  37 */     put("extSysId", message.getString("extSysId"));
/*  38 */     put("strategyId", message.getString("strategyId"));
/*     */   }
/*     */ 
/*     */   public MergePositionsMessage(String s) {
/*  42 */     setType("mergePositions");
/*  43 */     put("list", s);
/*     */   }
/*     */ 
/*     */   public MergePositionsMessage(String messageTextRepresentation, boolean workaroundValue) throws ParseException {
/*  47 */     super(messageTextRepresentation);
/*     */   }
/*     */ 
/*     */   public String getNewOrderGroupId()
/*     */   {
/*  56 */     return getString("new_id");
/*     */   }
/*     */ 
/*     */   public void setNewOrderGroupId(String orderGroupId)
/*     */   {
/*  65 */     put("new_id", orderGroupId);
/*     */   }
/*     */ 
/*     */   public void setPositions(String s) {
/*  69 */     put("list", s);
/*     */   }
/*     */ 
/*     */   public void setPositions(Collection<String> positions) {
/*  73 */     StringBuffer sb = new StringBuffer();
/*  74 */     for (String position : positions) {
/*  75 */       if (0 < sb.length()) sb.append(";");
/*  76 */       sb.append(position);
/*     */     }
/*  78 */     put("list", sb.toString());
/*     */   }
/*     */ 
/*     */   public String getPositionsString() {
/*  82 */     return (String)get("list");
/*     */   }
/*     */ 
/*     */   public Collection<String> getPositionsList() {
/*  86 */     Collection positions = new ArrayList();
/*  87 */     String positionString = getString("list");
/*  88 */     if (positionString == null) {
/*  89 */       return Collections.EMPTY_LIST;
/*     */     }
/*  91 */     StringTokenizer tokenizer = new StringTokenizer(positionString, ";");
/*  92 */     while (tokenizer.hasMoreTokens()) {
/*  93 */       positions.add(tokenizer.nextToken());
/*     */     }
/*  95 */     return positions;
/*     */   }
/*     */ 
/*     */   public String getExternalSysId()
/*     */   {
/* 104 */     return getString("extSysId");
/*     */   }
/*     */ 
/*     */   public void setExternalSysId(String extId)
/*     */   {
/* 113 */     put("extSysId", extId);
/*     */   }
/*     */ 
/*     */   public String getStrategyId()
/*     */   {
/* 122 */     return getString("strategyId");
/*     */   }
/*     */ 
/*     */   public void setStrategySysId(String strategyId)
/*     */   {
/* 131 */     put("strategyId", strategyId);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.MergePositionsMessage
 * JD-Core Version:    0.6.0
 */