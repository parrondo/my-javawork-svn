/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ 
/*    */ public class ScienceThreadFactory
/*    */   implements ThreadFactory
/*    */ {
/* 13 */   private ClassLoader classLoader = null;
/* 14 */   private String scriptName = null;
/*    */ 
/* 16 */   private long tid = 0L;
/* 17 */   private ScienceThread scienceThread = null;
/*    */ 
/*    */   public ScienceThreadFactory(ClassLoader classLoader, String scriptName) {
/* 20 */     this.classLoader = classLoader;
/* 21 */     this.scriptName = scriptName;
/*    */   }
/*    */ 
/*    */   public ScienceThread getThread() {
/* 25 */     return this.scienceThread;
/*    */   }
/*    */ 
/*    */   public long getThreadId() {
/* 29 */     return this.tid;
/*    */   }
/*    */ 
/*    */   public Thread newThread(Runnable r)
/*    */   {
/* 34 */     this.scienceThread = new ScienceThread(this.classLoader, r);
/* 35 */     this.tid = this.scienceThread.getId();
/* 36 */     this.scienceThread.setName("Strategy " + this.scriptName);
/* 37 */     return this.scienceThread;
/*    */   }
/*    */ 
/*    */   public static class ScienceThread extends Thread {
/* 41 */     private ClassLoader classLoader = null;
/*    */ 
/*    */     public ScienceThread(ClassLoader classLoader, Runnable runnable) {
/* 44 */       super();
/* 45 */       this.classLoader = classLoader;
/*    */     }
/*    */ 
/*    */     public ClassLoader getContextClassLoader() {
/* 49 */       return this.classLoader;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.ScienceThreadFactory
 * JD-Core Version:    0.6.0
 */