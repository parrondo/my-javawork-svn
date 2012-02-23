/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ public final class DoubleBarrelLRUCache<K extends CloneableKey, V>
/*     */ {
/*     */   private final Map<K, V> cache1;
/*     */   private final Map<K, V> cache2;
/*     */   private final AtomicInteger countdown;
/*     */   private volatile boolean swapped;
/*     */   private final int maxSize;
/*     */ 
/*     */   public DoubleBarrelLRUCache(int maxSize)
/*     */   {
/*  59 */     this.maxSize = maxSize;
/*  60 */     this.countdown = new AtomicInteger(maxSize);
/*  61 */     this.cache1 = new ConcurrentHashMap();
/*  62 */     this.cache2 = new ConcurrentHashMap();
/*     */   }
/*     */ 
/*     */   public V get(K key)
/*     */   {
/*     */     Map secondary;
/*     */     Map primary;
/*     */     Map secondary;
/*  69 */     if (this.swapped) {
/*  70 */       Map primary = this.cache2;
/*  71 */       secondary = this.cache1;
/*     */     } else {
/*  73 */       primary = this.cache1;
/*  74 */       secondary = this.cache2;
/*     */     }
/*     */ 
/*  78 */     Object result = primary.get(key);
/*  79 */     if (result == null)
/*     */     {
/*  81 */       result = secondary.get(key);
/*  82 */       if (result != null)
/*     */       {
/*  84 */         put((CloneableKey)key.clone(), result);
/*     */       }
/*     */     }
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   public void put(K key, V value)
/*     */   {
/*     */     Map secondary;
/*     */     Map primary;
/*     */     Map secondary;
/*  93 */     if (this.swapped) {
/*  94 */       Map primary = this.cache2;
/*  95 */       secondary = this.cache1;
/*     */     } else {
/*  97 */       primary = this.cache1;
/*  98 */       secondary = this.cache2;
/*     */     }
/* 100 */     primary.put(key, value);
/*     */ 
/* 102 */     if (this.countdown.decrementAndGet() == 0)
/*     */     {
/* 112 */       secondary.clear();
/*     */ 
/* 115 */       this.swapped = (!this.swapped);
/*     */ 
/* 118 */       this.countdown.set(this.maxSize);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class CloneableKey
/*     */   {
/*     */     public abstract Object clone();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.DoubleBarrelLRUCache
 * JD-Core Version:    0.6.0
 */