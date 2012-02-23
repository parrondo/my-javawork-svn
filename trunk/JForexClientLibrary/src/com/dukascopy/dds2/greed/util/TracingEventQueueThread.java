/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.awt.AWTEvent;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.WeakHashMap;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class TracingEventQueueThread extends Thread
/*    */ {
/* 11 */   private static final Logger LOGGER = LoggerFactory.getLogger(TracingEventQueueThread.class);
/*    */   private Map<AWTEvent, Long> eventTimeMap;
/*    */   private long thresholdDelay;
/*    */ 
/*    */   public TracingEventQueueThread(long thresholdDelay)
/*    */   {
/* 17 */     super("tracing-eqt");
/* 18 */     this.thresholdDelay = thresholdDelay;
/* 19 */     this.eventTimeMap = new WeakHashMap();
/*    */   }
/*    */ 
/*    */   public synchronized void eventDispatched(AWTEvent event) {
/* 23 */     this.eventTimeMap.put(event, Long.valueOf(System.currentTimeMillis()));
/*    */   }
/*    */ 
/*    */   public synchronized void eventProcessed(AWTEvent event) {
/* 27 */     checkEventTime(event, System.currentTimeMillis(), ((Long)this.eventTimeMap.get(event)).longValue());
/*    */ 
/* 29 */     this.eventTimeMap.put(event, null);
/*    */   }
/*    */ 
/*    */   private void checkEventTime(AWTEvent event, long currTime, long startTime) {
/* 33 */     long currProcessingTime = currTime - startTime;
/* 34 */     if (currProcessingTime >= this.thresholdDelay)
/* 35 */       LOGGER.warn("Event [" + event.hashCode() + "] " + event.getClass().getName() + " is taking too much time on EDT (" + currProcessingTime + ")" + " - " + event.getSource());
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     while (true)
/*    */     {
/* 48 */       long currTime = System.currentTimeMillis();
/* 49 */       synchronized (this) {
/* 50 */         for (Map.Entry entry : this.eventTimeMap.entrySet()) {
/* 51 */           AWTEvent event = (AWTEvent)entry.getKey();
/* 52 */           if (entry.getValue() == null) {
/*    */             continue;
/*    */           }
/* 55 */           long startTime = ((Long)entry.getValue()).longValue();
/* 56 */           checkEventTime(event, currTime, startTime);
/*    */         }
/*    */       }
/*    */       try {
/* 60 */         Thread.sleep(100L);
/*    */       }
/*    */       catch (InterruptedException ie)
/*    */       {
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.TracingEventQueueThread
 * JD-Core Version:    0.6.0
 */