/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.EventQueue;
/*    */ 
/*    */ public class TracingEventQueue extends EventQueue
/*    */ {
/*    */   private TracingEventQueueThread tracingThread;
/*    */ 
/*    */   public TracingEventQueue()
/*    */   {
/* 11 */     this.tracingThread = new TracingEventQueueThread(500L);
/* 12 */     this.tracingThread.start();
/*    */   }
/*    */ 
/*    */   protected void dispatchEvent(AWTEvent event)
/*    */   {
/* 19 */     this.tracingThread.eventDispatched(event);
/*    */ 
/* 27 */     super.dispatchEvent(event);
/*    */ 
/* 32 */     this.tracingThread.eventProcessed(event);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.TracingEventQueue
 * JD-Core Version:    0.6.0
 */