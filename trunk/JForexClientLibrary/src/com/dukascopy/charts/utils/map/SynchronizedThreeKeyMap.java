/*     */ package com.dukascopy.charts.utils.map;
/*     */ 
/*     */ import com.dukascopy.charts.utils.map.entry.IThreeKeyEntry;
/*     */ import com.dukascopy.charts.utils.map.entry.ThreeKeyEntry;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SynchronizedThreeKeyMap<K1, K2, K3, V>
/*     */   implements ISynchronizedThreeKeyMap<K1, K2, K3, V>
/*     */ {
/*  20 */   private final Map<K1, Map<K2, Map<K3, V>>> map = new HashMap();
/*     */ 
/*     */   public V get(K1 key1, K2 key2, K3 key3)
/*     */   {
/*  24 */     synchronized (this) {
/*  25 */       Map key1Map = (Map)this.map.get(key1);
/*  26 */       if (key1Map != null) {
/*  27 */         Map key2Map = (Map)key1Map.get(key2);
/*  28 */         if (key2Map != null) {
/*  29 */           return key2Map.get(key3);
/*     */         }
/*     */       }
/*  32 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void put(K1 key1, K2 key2, K3 key3, V value)
/*     */   {
/*  38 */     synchronized (this) {
/*  39 */       Map key1Map = (Map)this.map.get(key1);
/*  40 */       if (key1Map == null) {
/*  41 */         key1Map = new HashMap();
/*  42 */         this.map.put(key1, key1Map);
/*     */       }
/*     */ 
/*  45 */       Map key2Map = (Map)key1Map.get(key2);
/*  46 */       if (key2Map == null) {
/*  47 */         key2Map = new HashMap();
/*  48 */         key1Map.put(key2, key2Map);
/*     */       }
/*     */ 
/*  51 */       key2Map.put(key3, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<K2, Map<K3, V>> getSubMap(K1 key)
/*     */   {
/*  57 */     synchronized (this) {
/*  58 */       if (this.map.containsKey(key)) {
/*  59 */         Map result = new HashMap();
/*  60 */         result.putAll((Map)this.map.get(key));
/*  61 */         return result;
/*     */       }
/*  63 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<K3, V> getSubMap(K1 key1, K2 key2)
/*     */   {
/*  69 */     synchronized (this) {
/*  70 */       Map key1Map = (Map)this.map.get(key1);
/*  71 */       if (key1Map != null) {
/*  72 */         Map key2Map = (Map)key1Map.get(key2);
/*  73 */         if (key2Map != null) {
/*  74 */           Map result = new HashMap();
/*  75 */           result.putAll(key2Map);
/*  76 */           return result;
/*     */         }
/*     */       }
/*  79 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public V remove(K1 key1, K2 key2, K3 key3)
/*     */   {
/*  85 */     synchronized (this) {
/*  86 */       Map key1Map = (Map)this.map.get(key1);
/*  87 */       if (key1Map != null) {
/*  88 */         Map key2Map = (Map)key1Map.get(key2);
/*  89 */         if (key2Map != null) {
/*  90 */           return key2Map.remove(key3);
/*     */         }
/*     */       }
/*  93 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IThreeKeyEntry<K1, K2, K3, V>> entrySet()
/*     */   {
/*  99 */     synchronized (this) {
/* 100 */       Set result = entrySet(null, null);
/* 101 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IThreeKeyEntry<K1, K2, K3, V>> entrySet(K1 key1)
/*     */   {
/* 107 */     synchronized (this) {
/* 108 */       Set result = entrySet(key1, null);
/* 109 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IThreeKeyEntry<K1, K2, K3, V>> entrySet(K1 key1, K2 key2)
/*     */   {
/* 115 */     synchronized (this) {
/* 116 */       Set result = new HashSet();
/* 117 */       for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { k1 = i$.next();
/*     */ 
/* 119 */         if ((key1 != null) && (!key1.equals(k1)))
/*     */         {
/*     */           continue;
/*     */         }
/* 123 */         key1Map = (Map)this.map.get(k1);
/* 124 */         if (key1Map != null)
/* 125 */           for (i$ = key1Map.keySet().iterator(); i$.hasNext(); ) { k2 = i$.next();
/*     */ 
/* 127 */             if ((key2 != null) && (!key2.equals(k2)))
/*     */             {
/*     */               continue;
/*     */             }
/* 131 */             key2Map = (Map)key1Map.get(k2);
/* 132 */             if (key2Map != null)
/* 133 */               for (i$ = key2Map.keySet().iterator(); i$.hasNext(); ) { Object k3 = i$.next();
/*     */ 
/* 135 */                 Object value = key2Map.get(k3);
/* 136 */                 result.add(new ThreeKeyEntry(k1, k2, k3, value));
/*     */               }
/*     */           }
/*     */       }
/*     */       Object k1;
/*     */       Map key1Map;
/*     */       Iterator i$;
/*     */       Object k2;
/*     */       Map key2Map;
/*     */       Iterator i$;
/* 142 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean contains(K1 key1, K2 key2, K3 key3)
/*     */   {
/* 148 */     synchronized (this) {
/* 149 */       Object value = get(key1, key2, key3);
/* 150 */       boolean result = value != null;
/* 151 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 157 */     synchronized (this) {
/* 158 */       Set records = entrySet();
/* 159 */       return records.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 165 */     synchronized (this) {
/* 166 */       for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { Object k1 = i$.next();
/* 167 */         Map key1Map = (Map)this.map.get(k1);
/* 168 */         if (key1Map != null) {
/* 169 */           for (Iterator i$ = key1Map.keySet().iterator(); i$.hasNext(); ) { Object k2 = i$.next();
/* 170 */             Map key2Map = (Map)key1Map.get(k2);
/* 171 */             if (key2Map != null) {
/* 172 */               key2Map.clear();
/*     */             }
/*     */           }
/* 175 */           key1Map.clear();
/*     */         }
/*     */       }
/* 178 */       this.map.clear();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.SynchronizedThreeKeyMap
 * JD-Core Version:    0.6.0
 */