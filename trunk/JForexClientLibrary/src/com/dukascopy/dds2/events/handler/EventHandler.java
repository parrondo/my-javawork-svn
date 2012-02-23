/*    */ package com.dukascopy.dds2.events.handler;
/*    */ 
/*    */ import com.dukascopy.dds2.events.Event;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class EventHandler
/*    */ {
/* 16 */   private static final Logger log = LoggerFactory.getLogger(EventHandler.class);
/*    */   private EventWriter writer;
/*    */   private String serviceId;
/*    */ 
/*    */   public EventHandler(EventWriter writer)
/*    */   {
/* 24 */     this.writer = writer;
/*    */   }
/*    */ 
/*    */   public void handle(Event event)
/*    */   {
/* 34 */     if (this.writer != null)
/* 35 */       this.writer.store(event, this);
/*    */   }
/*    */ 
/*    */   public EventWriter getWriter()
/*    */   {
/* 46 */     return this.writer;
/*    */   }
/*    */ 
/*    */   public String getServiceId()
/*    */   {
/* 55 */     return this.serviceId;
/*    */   }
/*    */ 
/*    */   public void setServiceId(String serviceId)
/*    */   {
/* 64 */     this.serviceId = serviceId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.handler.EventHandler
 * JD-Core Version:    0.6.0
 */