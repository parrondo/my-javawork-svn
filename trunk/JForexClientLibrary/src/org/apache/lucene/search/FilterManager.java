/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeSet;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ @Deprecated
/*     */ public class FilterManager
/*     */ {
/*     */   protected static FilterManager manager;
/*     */   protected static final int DEFAULT_CACHE_CLEAN_SIZE = 100;
/*     */   protected static final long DEFAULT_CACHE_SLEEP_TIME = 600000L;
/*     */   protected Map<Integer, FilterItem> cache;
/*     */   protected int cacheCleanSize;
/*     */   protected long cleanSleepTime;
/*     */   protected FilterCleaner filterCleaner;
/*     */ 
/*     */   public static synchronized FilterManager getInstance()
/*     */   {
/*  64 */     if (manager == null) {
/*  65 */       manager = new FilterManager();
/*     */     }
/*  67 */     return manager;
/*     */   }
/*     */ 
/*     */   protected FilterManager()
/*     */   {
/*  74 */     this.cache = new HashMap();
/*  75 */     this.cacheCleanSize = 100;
/*  76 */     this.cleanSleepTime = 600000L;
/*     */ 
/*  78 */     this.filterCleaner = new FilterCleaner();
/*  79 */     Thread fcThread = new Thread(this.filterCleaner);
/*     */ 
/*  81 */     fcThread.setDaemon(true);
/*  82 */     fcThread.start();
/*     */   }
/*     */ 
/*     */   public void setCacheSize(int cacheCleanSize)
/*     */   {
/*  90 */     this.cacheCleanSize = cacheCleanSize;
/*     */   }
/*     */ 
/*     */   public void setCleanThreadSleepTime(long cleanSleepTime)
/*     */   {
/*  98 */     this.cleanSleepTime = cleanSleepTime;
/*     */   }
/*     */ 
/*     */   public Filter getFilter(Filter filter)
/*     */   {
/* 110 */     synchronized (this.cache) {
/* 111 */       FilterItem fi = null;
/* 112 */       fi = (FilterItem)this.cache.get(Integer.valueOf(filter.hashCode()));
/* 113 */       if (fi != null) {
/* 114 */         fi.timestamp = new Date().getTime();
/* 115 */         return fi.filter;
/*     */       }
/* 117 */       this.cache.put(Integer.valueOf(filter.hashCode()), new FilterItem(filter));
/* 118 */       return filter;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class FilterCleaner implements Runnable {
/* 154 */     private boolean running = true;
/*     */     private TreeSet<Map.Entry<Integer, FilterManager.FilterItem>> sortedFilterItems;
/*     */ 
/*     */     public FilterCleaner() {
/* 158 */       this.sortedFilterItems = new TreeSet(new Comparator(FilterManager.this) {
/*     */         public int compare(Map.Entry<Integer, FilterManager.FilterItem> a, Map.Entry<Integer, FilterManager.FilterItem> b) {
/* 160 */           FilterManager.FilterItem fia = (FilterManager.FilterItem)a.getValue();
/* 161 */           FilterManager.FilterItem fib = (FilterManager.FilterItem)b.getValue();
/* 162 */           if (fia.timestamp == fib.timestamp) {
/* 163 */             return 0;
/*     */           }
/*     */ 
/* 166 */           if (fia.timestamp < fib.timestamp) {
/* 167 */             return -1;
/*     */           }
/*     */ 
/* 170 */           return 1;
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public void run() {
/* 177 */       while (this.running)
/*     */       {
/* 181 */         if (FilterManager.this.cache.size() > FilterManager.this.cacheCleanSize)
/*     */         {
/* 183 */           this.sortedFilterItems.clear();
/* 184 */           synchronized (FilterManager.this.cache) {
/* 185 */             this.sortedFilterItems.addAll(FilterManager.this.cache.entrySet());
/* 186 */             Iterator it = this.sortedFilterItems.iterator();
/* 187 */             int numToDelete = (int)((FilterManager.this.cache.size() - FilterManager.this.cacheCleanSize) * 1.5D);
/* 188 */             int counter = 0;
/*     */ 
/* 190 */             while ((it.hasNext()) && (counter++ < numToDelete)) {
/* 191 */               Map.Entry entry = (Map.Entry)it.next();
/* 192 */               FilterManager.this.cache.remove(entry.getKey());
/*     */             }
/*     */           }
/*     */ 
/* 196 */           this.sortedFilterItems.clear();
/*     */         }
/*     */         try
/*     */         {
/* 200 */           Thread.sleep(FilterManager.this.cleanSleepTime);
/*     */         } catch (InterruptedException ie) {
/* 202 */           throw new ThreadInterruptedException(ie);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class FilterItem
/*     */   {
/*     */     public Filter filter;
/*     */     public long timestamp;
/*     */ 
/*     */     public FilterItem(Filter filter)
/*     */     {
/* 132 */       this.filter = filter;
/* 133 */       this.timestamp = new Date().getTime();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FilterManager
 * JD-Core Version:    0.6.0
 */