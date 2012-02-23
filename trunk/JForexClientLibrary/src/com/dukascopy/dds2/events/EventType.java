/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class EventType
/*     */ {
/*     */   private String name;
/*     */   private String tableName;
/*     */   private static final String EVENT_TABLE = "events";
/*     */   private static final String CLIENT_EVENT_TABLE = "events_client";
/*     */   private static final String LOGIN_EVENT_TABLE = "events_login";
/*     */   private static final String NOTIFICATION_EVENT_TABLE = "events_notify";
/*     */   private static final String SUBMIT_ORDER_EVENT_TABLE = "events_os";
/*     */   private static final String ORDER_STATE_CHANGE_EVENT_TABLE = "events_osc";
/*     */   private static final String CHANGE_CONDITION_EVENT_TABLE = "events_cc";
/*     */   private static final String ORDER_RELATED_EVENT_TABLE = "events_ore";
/*     */   private static final String TRADE_EVENT_TABLE = "events_trade";
/*  37 */   public static final EventType GENERIC = new EventType("GENERIC", "events");
/*     */ 
/*  42 */   public static final EventType CLIENT = new EventType("CLIENT", "events_client");
/*     */ 
/*  47 */   public static final EventType LOGIN = new EventType("LOGIN", "events_login");
/*     */ 
/*  52 */   public static final EventType NOTIFICATION = new EventType("NOTIFICATION", "events_notify");
/*     */ 
/*  57 */   public static final EventType SUBMIT_ORDER = new EventType("SUBMIT_ORDER", "events_os");
/*     */ 
/*  62 */   public static final EventType ORDER_STATE_CHANGE = new EventType("ORDER_STATE_CHANGE", "events_osc");
/*     */ 
/*  67 */   public static final EventType CHANGE_CONDITION = new EventType("CHANGE_CONDITION", "events_cc");
/*     */ 
/*  72 */   public static final EventType ORDER_RELATED = new EventType("ORDER_RELATED", "events_ore");
/*     */ 
/*  77 */   public static final EventType TRADE = new EventType("TRADE", "events_trade");
/*     */ 
/*  79 */   private static final Map<Class, EventType> types = new HashMap();
/*     */ 
/*     */   public EventType(String name, String tableName)
/*     */   {
/*  94 */     this.name = name;
/*  95 */     this.tableName = tableName;
/*     */   }
/*     */ 
/*     */   private String getName()
/*     */   {
/* 104 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getTableName()
/*     */   {
/* 113 */     return this.tableName;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 118 */     return this.name;
/*     */   }
/*     */ 
/*     */   public static EventType fromString(String name)
/*     */   {
/* 129 */     if (CLIENT.getName().equalsIgnoreCase(name))
/* 130 */       return CLIENT;
/* 131 */     if (LOGIN.getName().equalsIgnoreCase(name))
/* 132 */       return LOGIN;
/* 133 */     if (NOTIFICATION.getName().equalsIgnoreCase(name))
/* 134 */       return NOTIFICATION;
/* 135 */     if (SUBMIT_ORDER.getName().equalsIgnoreCase(name))
/* 136 */       return SUBMIT_ORDER;
/* 137 */     if (CHANGE_CONDITION.getName().equalsIgnoreCase(name))
/* 138 */       return CHANGE_CONDITION;
/* 139 */     if (ORDER_STATE_CHANGE.getName().equalsIgnoreCase(name))
/* 140 */       return ORDER_STATE_CHANGE;
/* 141 */     if (ORDER_RELATED.getName().equalsIgnoreCase(name))
/* 142 */       return ORDER_RELATED;
/* 143 */     if (TRADE.getName().equalsIgnoreCase(name)) {
/* 144 */       return TRADE;
/*     */     }
/* 146 */     throw new IllegalArgumentException("Unsupported or unknown event type: " + name);
/*     */   }
/*     */ 
/*     */   public static EventType getInstance(Class clazz)
/*     */   {
/* 157 */     return (EventType)types.get(clazz);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  82 */     types.put(ClientEvent.class, CLIENT);
/*  83 */     types.put(LoginEvent.class, LOGIN);
/*  84 */     types.put(NotificationEvent.class, NOTIFICATION);
/*  85 */     types.put(SubmitOrderEvent.class, SUBMIT_ORDER);
/*  86 */     types.put(OrderStateChangeEvent.class, ORDER_STATE_CHANGE);
/*  87 */     types.put(ChangeOrderConditionEvent.class, CHANGE_CONDITION);
/*  88 */     types.put(OrderRelatedEvent.class, ORDER_RELATED);
/*  89 */     types.put(TradeEvent.class, TRADE);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.EventType
 * JD-Core Version:    0.6.0
 */