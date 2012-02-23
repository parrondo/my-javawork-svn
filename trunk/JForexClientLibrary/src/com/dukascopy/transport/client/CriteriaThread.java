/*     */ package com.dukascopy.transport.client;
/*     */ 
/*     */ import com.dukascopy.transport.client.events.FeedbackMessageReceivedEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CriteriaThread extends Thread
/*     */ {
/*  13 */   private Object lock = new Object();
/*     */ 
/*  15 */   private volatile int threadState = 0;
/*     */   public static final int STATE_WAIT = 0;
/*     */   public static final int STATE_BUSSY = 1;
/*  21 */   private boolean terminating = false;
/*     */ 
/*  23 */   private int maxBufferSize = 10;
/*     */ 
/*  25 */   private List<FeedbackMessageReceivedEvent> threadQueue = new ArrayList();
/*     */ 
/*  27 */   private static final Logger log = LoggerFactory.getLogger(CriteriaThread.class);
/*     */ 
/*     */   public CriteriaThread(String name)
/*     */   {
/*  31 */     super(name);
/*     */   }
/*     */ 
/*     */   public void terminate() {
/*  35 */     synchronized (this.lock) {
/*  36 */       this.terminating = true;
/*  37 */       this.lock.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void monitor() {
/*  42 */     List queue = new ArrayList();
/*  43 */     synchronized (this.threadQueue) {
/*  44 */       queue.addAll(this.threadQueue);
/*     */     }
/*  46 */     for (FeedbackMessageReceivedEvent e : queue)
/*  47 */       if (e.getCreationTime() < System.currentTimeMillis() - 2000L) {
/*  48 */         log.error("Found not fired event, with wait time > 2 sec: " + e.getMessage());
/*     */ 
/*  50 */         StackTraceElement[] stackEl = getStackTrace();
/*  51 */         for (StackTraceElement st : stackEl) {
/*  52 */           log.error(st.toString());
/*     */         }
/*  54 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  66 */     while (!this.terminating)
/*     */       try {
/*  68 */         FeedbackMessageReceivedEvent event = null;
/*  69 */         synchronized (this.threadQueue) {
/*  70 */           if (this.threadQueue.size() > 0) {
/*  71 */             event = (FeedbackMessageReceivedEvent)this.threadQueue.remove(0);
/*     */           }
/*     */         }
/*  74 */         if (event != null) {
/*  75 */           event.run();
/*     */         }
/*  77 */         synchronized (this.threadQueue) {
/*  78 */           if (this.threadQueue.size() < 1) {
/*  79 */             this.threadState = 0;
/*     */           }
/*     */         }
/*     */ 
/*  83 */         synchronized (this.lock) {
/*  84 */           if ((this.threadState == 0) && (!this.terminating))
/*  85 */             this.lock.wait();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/*  89 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public int getThreadState()
/*     */   {
/*  98 */     return this.threadState;
/*     */   }
/*     */ 
/*     */   public synchronized void addPipeEvent(FeedbackMessageReceivedEvent event)
/*     */   {
/* 106 */     boolean added = false;
/* 107 */     while ((!added) && (!this.terminating)) {
/* 108 */       long stTime = System.currentTimeMillis();
/* 109 */       if (this.threadQueue.size() < this.maxBufferSize) {
/* 110 */         synchronized (this.threadQueue) {
/* 111 */           this.threadState = 1;
/* 112 */           this.threadQueue.add(event);
/* 113 */           added = true;
/*     */         }
/* 115 */         synchronized (this.lock) {
/* 116 */           this.lock.notifyAll();
/*     */         }
/*     */       } else {
/*     */         try {
/* 120 */           Thread.sleep(10L);
/*     */         } catch (Exception e) {
/* 122 */           e.printStackTrace();
/*     */         }
/* 124 */         if (stTime < System.currentTimeMillis() - 2000L) {
/* 125 */           stTime = System.currentTimeMillis();
/* 126 */           if (this.threadQueue.size() < 200) {
/* 127 */             log.error("Wait for threadQueue decreasing > 2 sec, queue len: " + this.threadQueue.size() + " (Can't wait any more, it's lock MinaThread. Message enqueued): " + event.getMessage());
/*     */ 
/* 131 */             synchronized (this.threadQueue) {
/* 132 */               this.threadState = 1;
/* 133 */               this.threadQueue.add(event);
/* 134 */               added = true;
/*     */             }
/* 136 */             synchronized (this.lock) {
/* 137 */               this.lock.notifyAll();
/*     */             }
/*     */           } else {
/* 140 */             log.error("Wait for threadQueue decreasing > 2 sec, queue len: " + this.threadQueue.size() + " (Can't wait any more, it's lock MinaThread. Message dropped): " + event.getMessage());
/*     */ 
/* 144 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getQueueSize() {
/* 152 */     synchronized (this.threadQueue) {
/* 153 */       return this.threadQueue.size();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.CriteriaThread
 * JD-Core Version:    0.6.0
 */