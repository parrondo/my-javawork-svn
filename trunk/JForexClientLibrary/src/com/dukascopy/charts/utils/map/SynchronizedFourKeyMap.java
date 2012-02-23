/*     */ package com.dukascopy.charts.utils.map;
/*     */ 
/*     */ import com.dukascopy.charts.utils.map.entry.FourKeyEntry;
/*     */ import com.dukascopy.charts.utils.map.entry.IFourKeyEntry;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SynchronizedFourKeyMap<K1, K2, K3, K4, V>
/*     */   implements ISynchronizedFourKeyMap<K1, K2, K3, K4, V>
/*     */ {
/*  20 */   private final Map<K1, Map<K2, Map<K3, Map<K4, V>>>> map = new HashMap();
/*     */ 
/*     */   public V get(K1 key1, K2 key2, K3 key3, K4 key4)
/*     */   {
/*  24 */     synchronized (this) {
/*  25 */       Map key1Map = (Map)this.map.get(key1);
/*  26 */       if (key1Map != null) {
/*  27 */         Map key2Map = (Map)key1Map.get(key2);
/*  28 */         if (key2Map != null) {
/*  29 */           Map key3Map = (Map)key2Map.get(key3);
/*  30 */           if (key3Map != null) {
/*  31 */             return key3Map.get(key4);
/*     */           }
/*     */         }
/*     */       }
/*  35 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<K2, Map<K3, Map<K4, V>>> getSubMap(K1 key)
/*     */   {
/*  41 */     synchronized (this)
/*     */     {
/*  43 */       Map key1Map = (Map)this.map.get(key);
/*  44 */       if (key1Map != null) {
/*  45 */         Map result = new HashMap();
/*  46 */         result.putAll(key1Map);
/*  47 */         return result;
/*     */       }
/*     */ 
/*  50 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<K3, Map<K4, V>> getSubMap(K1 key1, K2 key2)
/*     */   {
/*  56 */     synchronized (this)
/*     */     {
/*  58 */       Map key1Map = (Map)this.map.get(key1);
/*  59 */       if (key1Map != null) {
/*  60 */         Map key2Map = (Map)key1Map.get(key2);
/*  61 */         if (key2Map != null) {
/*  62 */           Map result = new HashMap();
/*  63 */           result.putAll(key2Map);
/*  64 */           return result;
/*     */         }
/*     */       }
/*     */ 
/*  68 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<K4, V> getSubMap(K1 key1, K2 key2, K3 key3)
/*     */   {
/*  74 */     synchronized (this) {
/*  75 */       Map key1Map = (Map)this.map.get(key1);
/*  76 */       if (key1Map != null) {
/*  77 */         Map key2Map = (Map)key1Map.get(key2);
/*  78 */         if (key2Map != null) {
/*  79 */           Map key3Map = (Map)key2Map.get(key3);
/*  80 */           if (key3Map != null) {
/*  81 */             Map result = new HashMap();
/*  82 */             result.putAll(key3Map);
/*  83 */             return result;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  88 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void put(K1 key1, K2 key2, K3 key3, K4 key4, V value)
/*     */   {
/*  94 */     synchronized (this) {
/*  95 */       Map key1Map = (Map)this.map.get(key1);
/*  96 */       if (key1Map == null) {
/*  97 */         key1Map = new HashMap();
/*  98 */         this.map.put(key1, key1Map);
/*     */       }
/*     */ 
/* 101 */       Map key2Map = (Map)key1Map.get(key2);
/* 102 */       if (key2Map == null) {
/* 103 */         key2Map = new HashMap();
/* 104 */         key1Map.put(key2, key2Map);
/*     */       }
/*     */ 
/* 107 */       Map key3Map = (Map)key2Map.get(key3);
/* 108 */       if (key3Map == null) {
/* 109 */         key3Map = new HashMap();
/* 110 */         key2Map.put(key3, key3Map);
/*     */       }
/*     */ 
/* 113 */       key3Map.put(key4, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public V remove(K1 key1, K2 key2, K3 key3, K4 key4)
/*     */   {
/* 119 */     synchronized (this) {
/* 120 */       Map key1Map = (Map)this.map.get(key1);
/* 121 */       if (key1Map != null) {
/* 122 */         Map key2Map = (Map)key1Map.get(key2);
/* 123 */         if (key2Map != null) {
/* 124 */           Map key3Map = (Map)key2Map.get(key3);
/* 125 */           if (key3Map != null) {
/* 126 */             return key3Map.remove(key4);
/*     */           }
/*     */         }
/*     */       }
/* 130 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 136 */     synchronized (this) {
/* 137 */       for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { Object k1 = i$.next();
/* 138 */         Map key1Map = (Map)this.map.get(k1);
/* 139 */         if (key1Map != null) {
/* 140 */           for (Iterator i$ = key1Map.keySet().iterator(); i$.hasNext(); ) { Object k2 = i$.next();
/* 141 */             Map key2Map = (Map)key1Map.get(k2);
/* 142 */             if (key2Map != null) {
/* 143 */               for (Iterator i$ = key2Map.keySet().iterator(); i$.hasNext(); ) { Object k3 = i$.next();
/* 144 */                 Map key3Map = (Map)key2Map.get(k3);
/* 145 */                 if (key3Map != null) {
/* 146 */                   key3Map.clear();
/*     */                 }
/*     */               }
/* 149 */               key2Map.clear();
/*     */             }
/*     */           }
/* 152 */           key1Map.clear();
/*     */         }
/*     */       }
/* 155 */       this.map.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean contains(K1 key1, K2 key2, K3 key3, K4 key4)
/*     */   {
/* 161 */     synchronized (this) {
/* 162 */       Object value = get(key1, key2, key3, key4);
/* 163 */       boolean result = value != null;
/* 164 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet()
/*     */   {
/* 170 */     synchronized (this) {
/* 171 */       Set result = entrySet(null, null, null);
/* 172 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet(K1 key1)
/*     */   {
/* 178 */     synchronized (this) {
/* 179 */       Set result = entrySet(key1, null, null);
/* 180 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet(K1 key1, K2 key2)
/*     */   {
/* 186 */     synchronized (this) {
/* 187 */       Set result = entrySet(key1, key2, null);
/* 188 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet(K1 key1, K2 key2, K3 key3)
/*     */   {
/* 194 */     synchronized (this) {
/* 195 */       Set result = new HashSet();
/* 196 */       for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { k1 = i$.next();
/*     */ 
/* 198 */         if ((key1 != null) && (!key1.equals(k1)))
/*     */         {
/*     */           continue;
/*     */         }
/* 202 */         key1Map = (Map)this.map.get(k1);
/* 203 */         if (key1Map != null)
/* 204 */           for (i$ = key1Map.keySet().iterator(); i$.hasNext(); ) { k2 = i$.next();
/*     */ 
/* 206 */             if ((key2 != null) && (!key2.equals(k2)))
/*     */             {
/*     */               continue;
/*     */             }
/* 210 */             key2Map = (Map)key1Map.get(k2);
/* 211 */             if (key2Map != null)
/* 212 */               for (i$ = key2Map.keySet().iterator(); i$.hasNext(); ) { k3 = i$.next();
/*     */ 
/* 214 */                 if ((key3 != null) && (!key3.equals(k3)))
/*     */                 {
/*     */                   continue;
/*     */                 }
/* 218 */                 key3Map = (Map)key2Map.get(k3);
/*     */ 
/* 220 */                 if (key3Map != null)
/* 221 */                   for (i$ = key3Map.keySet().iterator(); i$.hasNext(); ) { Object k4 = i$.next();
/*     */ 
/* 223 */                     Object value = key3Map.get(k4);
/* 224 */                     result.add(new FourKeyEntry(k1, k2, k3, k4, value));
/*     */                   }
/*     */               }
/*     */           }
/*     */       }
/*     */       Object k1;
/*     */       Map key1Map;
/*     */       Iterator i$;
/*     */       Object k2;
/*     */       Map key2Map;
/*     */       Iterator i$;
/*     */       Object k3;
/*     */       Map key3Map;
/*     */       Iterator i$;
/* 232 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 238 */     synchronized (this) {
/* 239 */       Set records = entrySet();
/* 240 */       return records.size();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.SynchronizedFourKeyMap
 * JD-Core Version:    0.6.0
 */