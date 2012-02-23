/*     */ package com.dukascopy.charts.utils.map;
/*     */ 
/*     */ import com.dukascopy.charts.utils.map.entry.ITwoKeyEntry;
/*     */ import com.dukascopy.charts.utils.map.entry.TwoKeyEntry;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SynchronizedTwoKeyMap<K1, K2, V>
/*     */   implements ISynchronizedTwoKeyMap<K1, K2, V>
/*     */ {
/*  20 */   private final Map<K1, Map<K2, V>> map = new HashMap();
/*     */ 
/*     */   public V get(K1 key1, K2 key2)
/*     */   {
/*  24 */     synchronized (this) {
/*  25 */       Map key1Map = (Map)this.map.get(key1);
/*  26 */       if (key1Map != null) {
/*  27 */         return key1Map.get(key2);
/*     */       }
/*  29 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void put(K1 key1, K2 key2, V value)
/*     */   {
/*  35 */     synchronized (this) {
/*  36 */       Map key1Map = (Map)this.map.get(key1);
/*  37 */       if (key1Map == null) {
/*  38 */         key1Map = new HashMap();
/*  39 */         this.map.put(key1, key1Map);
/*     */       }
/*     */ 
/*  42 */       key1Map.put(key2, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<K2, V> getSubMap(K1 key)
/*     */   {
/*  48 */     synchronized (this) {
/*  49 */       if (this.map.containsKey(key)) {
/*  50 */         Map result = new HashMap();
/*  51 */         result.putAll((Map)this.map.get(key));
/*  52 */         return result;
/*     */       }
/*  54 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public V remove(K1 key1, K2 key2)
/*     */   {
/*  60 */     synchronized (this) {
/*  61 */       Map key1Map = (Map)this.map.get(key1);
/*  62 */       if (key1Map != null) {
/*  63 */         return key1Map.remove(key2);
/*     */       }
/*  65 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<ITwoKeyEntry<K1, K2, V>> entrySet()
/*     */   {
/*  71 */     synchronized (this) {
/*  72 */       Set result = entrySet(null);
/*  73 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<ITwoKeyEntry<K1, K2, V>> entrySet(K1 key1)
/*     */   {
/*  79 */     synchronized (this) {
/*  80 */       Set result = new HashSet();
/*  81 */       for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { k1 = i$.next();
/*     */ 
/*  83 */         if ((key1 != null) && (!key1.equals(k1)))
/*     */         {
/*     */           continue;
/*     */         }
/*  87 */         key1Map = (Map)this.map.get(k1);
/*  88 */         if (key1Map != null)
/*  89 */           for (i$ = key1Map.keySet().iterator(); i$.hasNext(); ) { Object k2 = i$.next();
/*  90 */             Object value = key1Map.get(k2);
/*     */ 
/*  92 */             result.add(new TwoKeyEntry(k1, k2, value));
/*     */           }
/*     */       }
/*     */       Object k1;
/*     */       Map key1Map;
/*     */       Iterator i$;
/*  96 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean contains(K1 key1, K2 key2)
/*     */   {
/* 102 */     synchronized (this) {
/* 103 */       Object value = get(key1, key2);
/* 104 */       boolean result = value != null;
/* 105 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 111 */     synchronized (this) {
/* 112 */       Set records = entrySet();
/* 113 */       return records.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 119 */     synchronized (this) {
/* 120 */       for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { Object k1 = i$.next();
/* 121 */         Map key1Map = (Map)this.map.get(k1);
/* 122 */         if (key1Map != null) {
/* 123 */           key1Map.clear();
/*     */         }
/*     */       }
/* 126 */       this.map.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 132 */     synchronized (this) {
/* 133 */       return this.map.isEmpty();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeValue(V value)
/*     */   {
/*     */     Iterator i$;
/*     */     Map key1Map;
/*     */     Iterator i$;
/* 139 */     synchronized (this) {
/* 140 */       for (i$ = this.map.keySet().iterator(); i$.hasNext(); ) { Object k1 = i$.next();
/* 141 */         key1Map = (Map)this.map.get(k1);
/* 142 */         if (key1Map != null)
/* 143 */           for (i$ = key1Map.keySet().iterator(); i$.hasNext(); ) { Object k2 = i$.next();
/* 144 */             Object v = key1Map.get(k2);
/* 145 */             if ((v != null) && (v.equals(value)))
/* 146 */               key1Map.remove(k2);
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ISynchronizedTwoKeyMap<K1, K2, V> copy()
/*     */   {
/* 156 */     ISynchronizedTwoKeyMap copy = new SynchronizedTwoKeyMap();
/* 157 */     for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { k1 = i$.next();
/* 158 */       key1Map = (Map)this.map.get(k1);
/* 159 */       if (key1Map != null)
/* 160 */         for (i$ = key1Map.keySet().iterator(); i$.hasNext(); ) { Object k2 = i$.next();
/* 161 */           Object value = key1Map.get(k2);
/* 162 */           copy.put(k1, k2, value);
/*     */         }
/*     */     }
/*     */     Object k1;
/*     */     Map key1Map;
/*     */     Iterator i$;
/* 166 */     return copy;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.SynchronizedTwoKeyMap
 * JD-Core Version:    0.6.0
 */