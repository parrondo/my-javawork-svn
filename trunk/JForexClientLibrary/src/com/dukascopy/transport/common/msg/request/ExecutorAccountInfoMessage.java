/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import java.io.PrintStream;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class ExecutorAccountInfoMessage extends AccountInfoMessage
/*     */ {
/*  22 */   private List<OrderGroupMessage> cachedGroups = new ArrayList();
/*     */   public static final String TYPE = "mmAccountInfo";
/*     */   public static final String RELOAD_TYPE = "reloadType";
/*     */   public static final String GROUPS = "groups";
/*     */ 
/*     */   public ExecutorAccountInfoMessage()
/*     */   {
/*  37 */     setType("mmAccountInfo");
/*     */   }
/*     */ 
/*     */   public ExecutorAccountInfoMessage(ProtocolMessage message)
/*     */   {
/*  47 */     super(message);
/*  48 */     setType("mmAccountInfo");
/*  49 */     put("groups", message.get("groups"));
/*  50 */     put("reloadType", message.get("reloadType"));
/*     */   }
/*     */ 
/*     */   public void setReloadType(String userId) {
/*  54 */     put("reloadType", userId);
/*     */   }
/*     */ 
/*     */   public String getReloadType() {
/*  58 */     return getString("reloadType");
/*     */   }
/*     */ 
/*     */   public ExecutorAccountInfoMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  69 */     super(s);
/*  70 */     setType("mmAccountInfo");
/*     */   }
/*     */ 
/*     */   public void setGroups(List<OrderGroupMessage> groups)
/*     */   {
/*  80 */     JSONArray groupsArray = new JSONArray();
/*  81 */     this.cachedGroups.clear();
/*  82 */     for (OrderGroupMessage group : groups) {
/*  83 */       groupsArray.put(group);
/*  84 */       this.cachedGroups.add(group);
/*     */     }
/*  86 */     put("groups", groupsArray);
/*     */   }
/*     */ 
/*     */   public List<OrderGroupMessage> getGroups()
/*     */   {
/*  95 */     if (!this.cachedGroups.isEmpty()) {
/*  96 */       return new ArrayList(this.cachedGroups);
/*     */     }
/*  98 */     List orders = new ArrayList();
/*  99 */     JSONArray groupsArray = null;
/*     */     try {
/* 101 */       groupsArray = getJSONArray("groups");
/* 102 */       if (groupsArray != null)
/* 103 */         for (int i = 0; i < groupsArray.length(); i++)
/* 104 */           orders.add((OrderGroupMessage)ProtocolMessage.parse(groupsArray.getString(i)));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 108 */       System.out.println("******** " + groupsArray);
/*     */     }
/* 110 */     return orders;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ExecutorAccountInfoMessage
 * JD-Core Version:    0.6.0
 */