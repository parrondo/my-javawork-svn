/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.search.FieldCache;
/*     */ import org.apache.lucene.search.FieldCache.CacheEntry;
/*     */ import org.apache.lucene.search.FieldCache.CreationPlaceholder;
/*     */ 
/*     */ public final class FieldCacheSanityChecker
/*     */ {
/*  54 */   private RamUsageEstimator ramCalc = null;
/*     */ 
/*     */   public void setRamUsageEstimator(RamUsageEstimator r)
/*     */   {
/*  63 */     this.ramCalc = r;
/*     */   }
/*     */ 
/*     */   public static Insanity[] checkSanity(FieldCache cache)
/*     */   {
/*  72 */     return checkSanity(cache.getCacheEntries());
/*     */   }
/*     */ 
/*     */   public static Insanity[] checkSanity(FieldCache.CacheEntry[] cacheEntries)
/*     */   {
/*  81 */     FieldCacheSanityChecker sanityChecker = new FieldCacheSanityChecker();
/*     */ 
/*  83 */     sanityChecker.setRamUsageEstimator(new RamUsageEstimator(false));
/*  84 */     return sanityChecker.check(cacheEntries);
/*     */   }
/*     */ 
/*     */   public Insanity[] check(FieldCache.CacheEntry[] cacheEntries)
/*     */   {
/*  96 */     if ((null == cacheEntries) || (0 == cacheEntries.length)) {
/*  97 */       return new Insanity[0];
/*     */     }
/*  99 */     if (null != this.ramCalc) {
/* 100 */       for (int i = 0; i < cacheEntries.length; i++) {
/* 101 */         cacheEntries[i].estimateSize(this.ramCalc);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 109 */     MapOfSets valIdToItems = new MapOfSets(new HashMap(17));
/*     */ 
/* 111 */     MapOfSets readerFieldToValIds = new MapOfSets(new HashMap(17));
/*     */ 
/* 115 */     Set valMismatchKeys = new HashSet();
/*     */ 
/* 118 */     for (int i = 0; i < cacheEntries.length; i++) {
/* 119 */       FieldCache.CacheEntry item = cacheEntries[i];
/* 120 */       Object val = item.getValue();
/*     */ 
/* 122 */       if ((val instanceof FieldCache.CreationPlaceholder)) {
/*     */         continue;
/*     */       }
/* 125 */       ReaderField rf = new ReaderField(item.getReaderKey(), item.getFieldName());
/*     */ 
/* 128 */       Integer valId = Integer.valueOf(System.identityHashCode(val));
/*     */ 
/* 131 */       valIdToItems.put(valId, item);
/* 132 */       if (1 < readerFieldToValIds.put(rf, valId)) {
/* 133 */         valMismatchKeys.add(rf);
/*     */       }
/*     */     }
/*     */ 
/* 137 */     List insanity = new ArrayList(valMismatchKeys.size() * 3);
/*     */ 
/* 139 */     insanity.addAll(checkValueMismatch(valIdToItems, readerFieldToValIds, valMismatchKeys));
/*     */ 
/* 142 */     insanity.addAll(checkSubreaders(valIdToItems, readerFieldToValIds));
/*     */ 
/* 145 */     return (Insanity[])insanity.toArray(new Insanity[insanity.size()]);
/*     */   }
/*     */ 
/*     */   private Collection<Insanity> checkValueMismatch(MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems, MapOfSets<ReaderField, Integer> readerFieldToValIds, Set<ReaderField> valMismatchKeys)
/*     */   {
/* 159 */     List insanity = new ArrayList(valMismatchKeys.size() * 3);
/*     */     Map rfMap;
/*     */     Map valMap;
/* 161 */     if (!valMismatchKeys.isEmpty())
/*     */     {
/* 164 */       rfMap = readerFieldToValIds.getMap();
/* 165 */       valMap = valIdToItems.getMap();
/* 166 */       for (ReaderField rf : valMismatchKeys) {
/* 167 */         List badEntries = new ArrayList(valMismatchKeys.size() * 2);
/* 168 */         for (Integer value : (Set)rfMap.get(rf)) {
/* 169 */           for (FieldCache.CacheEntry cacheEntry : (Set)valMap.get(value)) {
/* 170 */             badEntries.add(cacheEntry);
/*     */           }
/*     */         }
/*     */ 
/* 174 */         FieldCache.CacheEntry[] badness = new FieldCache.CacheEntry[badEntries.size()];
/* 175 */         badness = (FieldCache.CacheEntry[])badEntries.toArray(badness);
/*     */ 
/* 177 */         insanity.add(new Insanity(InsanityType.VALUEMISMATCH, "Multiple distinct value objects for " + rf.toString(), badness));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 182 */     return insanity;
/*     */   }
/*     */ 
/*     */   private Collection<Insanity> checkSubreaders(MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems, MapOfSets<ReaderField, Integer> readerFieldToValIds)
/*     */   {
/* 196 */     List insanity = new ArrayList(23);
/*     */ 
/* 198 */     Map badChildren = new HashMap(17);
/* 199 */     MapOfSets badKids = new MapOfSets(badChildren);
/*     */ 
/* 201 */     Map viToItemSets = valIdToItems.getMap();
/* 202 */     Map rfToValIdSets = readerFieldToValIds.getMap();
/*     */ 
/* 204 */     Set seen = new HashSet(17);
/*     */ 
/* 206 */     Set readerFields = rfToValIdSets.keySet();
/* 207 */     for (ReaderField rf : readerFields)
/*     */     {
/* 209 */       if (seen.contains(rf))
/*     */         continue;
/* 211 */       List kids = getAllDecendentReaderKeys(rf.readerKey);
/* 212 */       for (Iterator i$ = kids.iterator(); i$.hasNext(); ) { Object kidKey = i$.next();
/* 213 */         ReaderField kid = new ReaderField(kidKey, rf.fieldName);
/*     */ 
/* 215 */         if (badChildren.containsKey(kid))
/*     */         {
/* 218 */           badKids.put(rf, kid);
/* 219 */           badKids.putAll(rf, (Collection)badChildren.get(kid));
/* 220 */           badChildren.remove(kid);
/*     */         }
/* 222 */         else if (rfToValIdSets.containsKey(kid))
/*     */         {
/* 224 */           badKids.put(rf, kid);
/*     */         }
/* 226 */         seen.add(kid);
/*     */       }
/* 228 */       seen.add(rf);
/*     */     }
/*     */ 
/* 232 */     for (ReaderField parent : badChildren.keySet()) {
/* 233 */       Set kids = (Set)badChildren.get(parent);
/*     */ 
/* 235 */       List badEntries = new ArrayList(kids.size() * 2);
/*     */ 
/* 239 */       for (Integer value : (Set)rfToValIdSets.get(parent)) {
/* 240 */         badEntries.addAll((Collection)viToItemSets.get(value));
/*     */       }
/*     */ 
/* 245 */       for (ReaderField kid : kids) {
/* 246 */         for (Integer value : (Set)rfToValIdSets.get(kid)) {
/* 247 */           badEntries.addAll((Collection)viToItemSets.get(value));
/*     */         }
/*     */       }
/*     */ 
/* 251 */       FieldCache.CacheEntry[] badness = new FieldCache.CacheEntry[badEntries.size()];
/* 252 */       badness = (FieldCache.CacheEntry[])badEntries.toArray(badness);
/*     */ 
/* 254 */       insanity.add(new Insanity(InsanityType.SUBREADER, "Found caches for decendents of " + parent.toString(), badness));
/*     */     }
/*     */ 
/* 260 */     return insanity;
/*     */   }
/*     */ 
/*     */   private List<Object> getAllDecendentReaderKeys(Object seed)
/*     */   {
/* 270 */     List all = new ArrayList(17);
/* 271 */     all.add(seed);
/* 272 */     for (int i = 0; i < all.size(); i++) {
/* 273 */       Object obj = all.get(i);
/* 274 */       if ((obj instanceof IndexReader)) {
/* 275 */         IndexReader[] subs = ((IndexReader)obj).getSequentialSubReaders();
/* 276 */         for (int j = 0; (null != subs) && (j < subs.length); j++) {
/* 277 */           all.add(subs[j].getCoreCacheKey());
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 283 */     return all.subList(1, all.size());
/*     */   }
/*     */ 
/*     */   public static final class InsanityType
/*     */   {
/*     */     private final String label;
/* 393 */     public static final InsanityType SUBREADER = new InsanityType("SUBREADER");
/*     */ 
/* 411 */     public static final InsanityType VALUEMISMATCH = new InsanityType("VALUEMISMATCH");
/*     */ 
/* 419 */     public static final InsanityType EXPECTED = new InsanityType("EXPECTED");
/*     */ 
/*     */     private InsanityType(String label)
/*     */     {
/* 384 */       this.label = label;
/*     */     }
/*     */     public String toString() {
/* 387 */       return this.label;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Insanity
/*     */   {
/*     */     private final FieldCacheSanityChecker.InsanityType type;
/*     */     private final String msg;
/*     */     private final FieldCache.CacheEntry[] entries;
/*     */ 
/*     */     public Insanity(FieldCacheSanityChecker.InsanityType type, String msg, FieldCache.CacheEntry[] entries)
/*     */     {
/* 324 */       if (null == type) {
/* 325 */         throw new IllegalArgumentException("Insanity requires non-null InsanityType");
/*     */       }
/*     */ 
/* 328 */       if ((null == entries) || (0 == entries.length)) {
/* 329 */         throw new IllegalArgumentException("Insanity requires non-null/non-empty CacheEntry[]");
/*     */       }
/*     */ 
/* 332 */       this.type = type;
/* 333 */       this.msg = msg;
/* 334 */       this.entries = entries;
/*     */     }
/*     */ 
/*     */     public FieldCacheSanityChecker.InsanityType getType()
/*     */     {
/* 340 */       return this.type;
/*     */     }
/*     */ 
/*     */     public String getMsg() {
/* 344 */       return this.msg;
/*     */     }
/*     */ 
/*     */     public FieldCache.CacheEntry[] getCacheEntries() {
/* 348 */       return this.entries;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 356 */       StringBuilder buf = new StringBuilder();
/* 357 */       buf.append(getType()).append(": ");
/*     */ 
/* 359 */       String m = getMsg();
/* 360 */       if (null != m) buf.append(m);
/*     */ 
/* 362 */       buf.append('\n');
/*     */ 
/* 364 */       FieldCache.CacheEntry[] ce = getCacheEntries();
/* 365 */       for (int i = 0; i < ce.length; i++) {
/* 366 */         buf.append('\t').append(ce[i].toString()).append('\n');
/*     */       }
/*     */ 
/* 369 */       return buf.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ReaderField
/*     */   {
/*     */     public final Object readerKey;
/*     */     public final String fieldName;
/*     */ 
/*     */     public ReaderField(Object readerKey, String fieldName)
/*     */     {
/* 293 */       this.readerKey = readerKey;
/* 294 */       this.fieldName = fieldName;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 298 */       return System.identityHashCode(this.readerKey) * this.fieldName.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object that) {
/* 302 */       if (!(that instanceof ReaderField)) return false;
/*     */ 
/* 304 */       ReaderField other = (ReaderField)that;
/* 305 */       return (this.readerKey == other.readerKey) && (this.fieldName.equals(other.fieldName));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 310 */       return this.readerKey.toString() + "+" + this.fieldName;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.FieldCacheSanityChecker
 * JD-Core Version:    0.6.0
 */