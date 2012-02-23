/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ public class TimeLimitingCollector extends Collector
/*     */ {
/*     */   public static final int DEFAULT_RESOLUTION = 20;
/*  43 */   public boolean DEFAULT_GREEDY = false;
/*     */ 
/*  45 */   private static long resolution = 20L;
/*     */ 
/*  47 */   private boolean greedy = this.DEFAULT_GREEDY;
/*     */ 
/* 123 */   private static final TimerThread TIMER_THREAD = new TimerThread(null);
/*     */   private final long t0;
/*     */   private final long timeout;
/*     */   private final Collector collector;
/*     */   private int docBase;
/*     */ 
/*     */   public TimeLimitingCollector(Collector collector, long timeAllowed)
/*     */   {
/* 141 */     this.collector = collector;
/* 142 */     this.t0 = TIMER_THREAD.getMilliseconds();
/* 143 */     this.timeout = (this.t0 + timeAllowed);
/*     */   }
/*     */ 
/*     */   public static long getResolution()
/*     */   {
/* 151 */     return resolution;
/*     */   }
/*     */ 
/*     */   public static void setResolution(long newResolution)
/*     */   {
/* 169 */     resolution = Math.max(newResolution, 5L);
/*     */   }
/*     */ 
/*     */   public boolean isGreedy()
/*     */   {
/* 181 */     return this.greedy;
/*     */   }
/*     */ 
/*     */   public void setGreedy(boolean greedy)
/*     */   {
/* 190 */     this.greedy = greedy;
/*     */   }
/*     */ 
/*     */   public void collect(int doc)
/*     */     throws IOException
/*     */   {
/* 202 */     long time = TIMER_THREAD.getMilliseconds();
/* 203 */     if (this.timeout < time) {
/* 204 */       if (this.greedy)
/*     */       {
/* 206 */         this.collector.collect(doc);
/*     */       }
/*     */ 
/* 209 */       throw new TimeExceededException(this.timeout - this.t0, time - this.t0, this.docBase + doc, null);
/*     */     }
/*     */ 
/* 212 */     this.collector.collect(doc);
/*     */   }
/*     */ 
/*     */   public void setNextReader(IndexReader reader, int base) throws IOException
/*     */   {
/* 217 */     this.collector.setNextReader(reader, base);
/* 218 */     this.docBase = base;
/*     */   }
/*     */ 
/*     */   public void setScorer(Scorer scorer) throws IOException
/*     */   {
/* 223 */     this.collector.setScorer(scorer);
/*     */   }
/*     */ 
/*     */   public boolean acceptsDocsOutOfOrder()
/*     */   {
/* 228 */     return this.collector.acceptsDocsOutOfOrder();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 126 */     TIMER_THREAD.start();
/*     */   }
/*     */ 
/*     */   public static class TimeExceededException extends RuntimeException
/*     */   {
/*     */     private long timeAllowed;
/*     */     private long timeElapsed;
/*     */     private int lastDocCollected;
/*     */ 
/*     */     private TimeExceededException(long timeAllowed, long timeElapsed, int lastDocCollected)
/*     */     {
/* 101 */       super();
/* 102 */       this.timeAllowed = timeAllowed;
/* 103 */       this.timeElapsed = timeElapsed;
/* 104 */       this.lastDocCollected = lastDocCollected;
/*     */     }
/*     */ 
/*     */     public long getTimeAllowed() {
/* 108 */       return this.timeAllowed;
/*     */     }
/*     */ 
/*     */     public long getTimeElapsed() {
/* 112 */       return this.timeElapsed;
/*     */     }
/*     */ 
/*     */     public int getLastDocCollected() {
/* 116 */       return this.lastDocCollected;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class TimerThread extends Thread
/*     */   {
/*  61 */     private volatile long time = 0L;
/*     */ 
/*     */     private TimerThread()
/*     */     {
/*  70 */       super();
/*  71 */       setDaemon(true);
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       while (true)
/*     */       {
/*  78 */         this.time += TimeLimitingCollector.resolution;
/*     */         try {
/*  80 */           Thread.sleep(TimeLimitingCollector.resolution); } catch (InterruptedException ie) {
/*     */         }
/*     */       }
/*  82 */       throw new ThreadInterruptedException(ie);
/*     */     }
/*     */ 
/*     */     public long getMilliseconds()
/*     */     {
/*  91 */       return this.time;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TimeLimitingCollector
 * JD-Core Version:    0.6.0
 */