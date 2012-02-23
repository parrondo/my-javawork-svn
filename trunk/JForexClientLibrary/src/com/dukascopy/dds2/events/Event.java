/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class Event
/*     */ {
/*     */   public static final String ID = "id";
/*     */   public static final String EVENT_ID = "event_id";
/*     */   public static final String EVENT_TYPE = "type";
/*     */   public static final String TIMESTAMP = "timestmp";
/*     */   public static final String SERVICE_ID = "service_id";
/*     */   public static final String TARGET_SERVICE_ID = "target_service_id";
/*     */   public static final String COMMENTS = "comments";
/*     */   private long id;
/*     */   private EventType type;
/*  67 */   private Date timestamp = new Date();
/*     */   private String serviceId;
/*     */   private String targetServiceId;
/*     */   private String comments;
/*     */   private Event nextEvent;
/*     */ 
/*     */   protected void setNextEvent(Event event)
/*     */   {
/*  87 */     this.nextEvent = event;
/*     */   }
/*     */ 
/*     */   public Event getNextEvent()
/*     */   {
/*  97 */     return this.nextEvent;
/*     */   }
/*     */ 
/*     */   public Event append(Event event)
/*     */   {
/* 108 */     getLastEvent().setNextEvent(event);
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   protected Event getLastEvent()
/*     */   {
/* 119 */     Event nextEvent = getNextEvent();
/* 120 */     return nextEvent == null ? this : nextEvent.getLastEvent();
/*     */   }
/*     */ 
/*     */   public String getComments()
/*     */   {
/* 127 */     return this.comments;
/*     */   }
/*     */ 
/*     */   public void setComments(String comments)
/*     */   {
/* 134 */     this.comments = comments;
/*     */   }
/*     */ 
/*     */   public long getId()
/*     */   {
/* 141 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(long id)
/*     */   {
/* 148 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getServiceId()
/*     */   {
/* 155 */     return this.serviceId;
/*     */   }
/*     */ 
/*     */   public void setServiceId(String serviceId)
/*     */   {
/* 162 */     this.serviceId = serviceId;
/*     */   }
/*     */ 
/*     */   public String getTargetServiceId()
/*     */   {
/* 169 */     return this.targetServiceId;
/*     */   }
/*     */ 
/*     */   public void setTargetServiceId(String targetServiceId)
/*     */   {
/* 176 */     this.targetServiceId = targetServiceId;
/*     */   }
/*     */ 
/*     */   public Date getTimestamp()
/*     */   {
/* 183 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   public void setTimestamp(Date timestamp)
/*     */   {
/* 190 */     this.timestamp = timestamp;
/*     */   }
/*     */ 
/*     */   public EventType getType()
/*     */   {
/* 197 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(EventType type)
/*     */   {
/* 204 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public abstract Map<String, Object> getAttributes();
/*     */ 
/*     */   public String toString()
/*     */   {
/* 216 */     StringBuffer sb = new StringBuffer();
/* 217 */     sb.append(getClass().getSimpleName() + " {");
/* 218 */     Iterator i = getAttributes().entrySet().iterator();
/* 219 */     while (i.hasNext()) {
/* 220 */       Map.Entry entry = (Map.Entry)i.next();
/* 221 */       sb.append((String)entry.getKey() + "=" + entry.getValue());
/* 222 */       if (i.hasNext()) {
/* 223 */         sb.append(", ");
/*     */       }
/*     */     }
/* 226 */     sb.append("}");
/*     */ 
/* 228 */     Event nextEvent = getNextEvent();
/* 229 */     if (nextEvent != null) {
/* 230 */       sb.append(" >>> " + nextEvent.toString());
/*     */     }
/*     */ 
/* 233 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.Event
 * JD-Core Version:    0.6.0
 */