/*    */ package com.dukascopy.dds2.greed.util.event;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public abstract class ApplicationEvent extends EventObject
/*    */ {
/* 11 */   private final long timestamp = System.currentTimeMillis();
/*    */ 
/*    */   public ApplicationEvent(Object source) {
/* 14 */     super(source);
/*    */   }
/*    */ 
/*    */   public long getTimestamp() {
/* 18 */     return this.timestamp;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.event.ApplicationEvent
 * JD-Core Version:    0.6.0
 */