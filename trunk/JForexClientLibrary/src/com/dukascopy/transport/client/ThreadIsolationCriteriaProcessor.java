/*     */ package com.dukascopy.transport.client;
/*     */ 
/*     */ import com.dukascopy.transport.client.events.FeedbackMessageReceivedEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ public class ThreadIsolationCriteriaProcessor
/*     */ {
/*  15 */   private Map<Object, CriteriaThread> workingThreads = new HashMap();
/*     */   private IsolationCriteria criteria;
/*  19 */   private boolean terminating = false;
/*     */ 
/*  21 */   private AtomicInteger counter = new AtomicInteger();
/*     */ 
/*     */   public ThreadIsolationCriteriaProcessor(IsolationCriteria criteria)
/*     */   {
/*  25 */     this.criteria = criteria;
/*     */   }
/*     */ 
/*     */   public boolean execute(FeedbackMessageReceivedEvent event) {
/*  29 */     if (!this.terminating) {
/*  30 */       Object checkParameter = this.criteria.getCheckParameter(event.getMessage());
/*     */ 
/*  32 */       if (checkParameter == null) {
/*  33 */         return false;
/*     */       }
/*  35 */       addCriteriaEvent(checkParameter, event);
/*  36 */       return true;
/*     */     }
/*  38 */     return false;
/*     */   }
/*     */ 
/*     */   public int getScheduledCount() {
/*  42 */     int scheduledSize = 0;
/*  43 */     List threads = new ArrayList();
/*  44 */     synchronized (this.workingThreads) {
/*  45 */       threads.addAll(this.workingThreads.values());
/*     */     }
/*  47 */     for (CriteriaThread ct : threads) {
/*  48 */       if (ct != null) {
/*  49 */         scheduledSize += ct.getQueueSize();
/*     */       }
/*     */     }
/*  52 */     return scheduledSize;
/*     */   }
/*     */ 
/*     */   private void addCriteriaEvent(Object o, FeedbackMessageReceivedEvent event)
/*     */   {
/*  57 */     synchronized (this.workingThreads) {
/*  58 */       criteriaThread = (CriteriaThread)this.workingThreads.get(o);
/*  59 */       if (criteriaThread != null) {
/*  60 */         criteriaThread.addPipeEvent(event);
/*  61 */         return;
/*     */       }
/*     */     }
/*  64 */     Set keys = new HashSet();
/*  65 */     synchronized (this.workingThreads) {
/*  66 */       keys.addAll(this.workingThreads.keySet());
/*     */     }
/*  68 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/*  69 */       synchronized (this.workingThreads) {
/*  70 */         CriteriaThread pp = (CriteriaThread)this.workingThreads.get(key);
/*  71 */         if ((pp != null) && (pp.getThreadState() == 0))
/*     */         {
/*  73 */           criteriaThread = (CriteriaThread)this.workingThreads.remove(key);
/*  74 */           this.workingThreads.put(o, criteriaThread);
/*  75 */           criteriaThread.addPipeEvent(event);
/*  76 */           return;
/*     */         }
/*     */       }
/*     */     }
/*  80 */     CriteriaThread criteriaThread = new CriteriaThread("Client listener isolated thread - " + this.counter.incrementAndGet());
/*     */ 
/*  83 */     criteriaThread.start();
/*  84 */     synchronized (this.workingThreads) {
/*  85 */       this.workingThreads.put(o, criteriaThread);
/*  86 */       criteriaThread.addPipeEvent(event);
/*  87 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void terminate() {
/*  92 */     this.terminating = true;
/*  93 */     synchronized (this.workingThreads) {
/*  94 */       for (CriteriaThread p : this.workingThreads.values())
/*  95 */         p.terminate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isTerminating()
/*     */   {
/* 104 */     return this.terminating;
/*     */   }
/*     */ 
/*     */   public void monitor() {
/* 108 */     Map th = new HashMap();
/* 109 */     synchronized (this.workingThreads) {
/* 110 */       th.putAll(this.workingThreads);
/*     */     }
/* 112 */     for (CriteriaThread ct : th.values())
/* 113 */       ct.monitor();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.ThreadIsolationCriteriaProcessor
 * JD-Core Version:    0.6.0
 */