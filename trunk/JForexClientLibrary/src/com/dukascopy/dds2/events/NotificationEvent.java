/*    */ package com.dukascopy.dds2.events;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class NotificationEvent extends Event
/*    */ {
/*    */   public static final String TYPE = "notification";
/*    */   public static final String MESSAGE_LEVEL = "message_level";
/*    */   public static final String MESSAGE_TEXT = "message";
/*    */   private Level level;
/*    */   private String message;
/*    */ 
/*    */   public NotificationEvent(Level level, String message)
/*    */   {
/* 55 */     setLevel(level);
/* 56 */     setMessage(message);
/*    */   }
/*    */ 
/*    */   public Level getLevel()
/*    */   {
/* 63 */     return this.level;
/*    */   }
/*    */ 
/*    */   public void setLevel(Level level)
/*    */   {
/* 71 */     this.level = level;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 78 */     return this.message;
/*    */   }
/*    */ 
/*    */   public void setMessage(String message)
/*    */   {
/* 86 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public Map<String, Object> getAttributes()
/*    */   {
/* 91 */     Map attributes = new HashMap();
/* 92 */     attributes.put("event_id", Long.valueOf(super.getId()));
/* 93 */     attributes.put("message_level", this.level);
/* 94 */     attributes.put("message", this.message);
/* 95 */     return Collections.unmodifiableMap(attributes);
/*    */   }
/*    */ 
/*    */   public static enum Level
/*    */   {
/* 22 */     ERROR("ERROR"), INFO("INFO"), WARNING("WARNING"), ALERT("ALERT");
/*    */ 
/*    */     private String value;
/*    */ 
/* 27 */     private Level(String value) { this.value = value;
/*    */     }
/*    */ 
/*    */     public String toString()
/*    */     {
/* 32 */       return this.value;
/*    */     }
/*    */ 
/*    */     public static Level fromString(String value) {
/* 36 */       if (ERROR.toString().equalsIgnoreCase(value))
/* 37 */         return ERROR;
/* 38 */       if (INFO.toString().equalsIgnoreCase(value))
/* 39 */         return INFO;
/* 40 */       if (WARNING.toString().equalsIgnoreCase(value))
/* 41 */         return WARNING;
/* 42 */       if (ALERT.toString().equalsIgnoreCase(value)) {
/* 43 */         return ALERT;
/*    */       }
/* 45 */       throw new IllegalArgumentException("Unsupported notification level: " + value);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.NotificationEvent
 * JD-Core Version:    0.6.0
 */