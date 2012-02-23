/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.FixedBitSet;
/*     */ 
/*     */ public class CachingWrapperFilter extends Filter
/*     */ {
/*     */   Filter filter;
/*     */   protected final FilterCache<DocIdSet> cache;
/*     */   int hitCount;
/*     */   int missCount;
/*     */ 
/*     */   public CachingWrapperFilter(Filter filter)
/*     */   {
/* 138 */     this(filter, DeletesMode.IGNORE);
/*     */   }
/*     */ 
/*     */   public CachingWrapperFilter(Filter filter, DeletesMode deletesMode)
/*     */   {
/* 150 */     this.filter = filter;
/* 151 */     this.cache = new FilterCache(deletesMode)
/*     */     {
/*     */       public DocIdSet mergeDeletes(IndexReader r, DocIdSet docIdSet) {
/* 154 */         return new FilteredDocIdSet(docIdSet, r)
/*     */         {
/*     */           protected boolean match(int docID) {
/* 157 */             return !this.val$r.isDeleted(docID);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected DocIdSet docIdSetToCache(DocIdSet docIdSet, IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 171 */     if (docIdSet == null)
/*     */     {
/* 173 */       return DocIdSet.EMPTY_DOCIDSET;
/* 174 */     }if (docIdSet.isCacheable()) {
/* 175 */       return docIdSet;
/*     */     }
/* 177 */     DocIdSetIterator it = docIdSet.iterator();
/*     */ 
/* 181 */     if (it == null) {
/* 182 */       return DocIdSet.EMPTY_DOCIDSET;
/*     */     }
/* 184 */     FixedBitSet bits = new FixedBitSet(reader.maxDoc());
/* 185 */     bits.or(it);
/* 186 */     return bits;
/*     */   }
/*     */ 
/*     */   public DocIdSet getDocIdSet(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 197 */     Object coreKey = reader.getCoreCacheKey();
/* 198 */     Object delCoreKey = reader.hasDeletions() ? reader.getDeletesCacheKey() : coreKey;
/*     */ 
/* 200 */     DocIdSet docIdSet = (DocIdSet)this.cache.get(reader, coreKey, delCoreKey);
/* 201 */     if (docIdSet != null) {
/* 202 */       this.hitCount += 1;
/* 203 */       return docIdSet;
/*     */     }
/*     */ 
/* 206 */     this.missCount += 1;
/*     */ 
/* 209 */     docIdSet = docIdSetToCache(this.filter.getDocIdSet(reader), reader);
/*     */ 
/* 211 */     if (docIdSet != null) {
/* 212 */       this.cache.put(coreKey, delCoreKey, docIdSet);
/*     */     }
/*     */ 
/* 215 */     return docIdSet;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 220 */     return "CachingWrapperFilter(" + this.filter + ")";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 225 */     if (!(o instanceof CachingWrapperFilter)) return false;
/* 226 */     return this.filter.equals(((CachingWrapperFilter)o).filter);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 231 */     return this.filter.hashCode() ^ 0x1117BF25;
/*     */   }
/*     */ 
/*     */   static abstract class FilterCache<T>
/*     */     implements Serializable
/*     */   {
/*     */     transient Map<Object, T> cache;
/*     */     private final CachingWrapperFilter.DeletesMode deletesMode;
/*     */ 
/*     */     public FilterCache(CachingWrapperFilter.DeletesMode deletesMode)
/*     */     {
/*  78 */       this.deletesMode = deletesMode;
/*     */     }
/*     */ 
/*     */     public synchronized T get(IndexReader reader, Object coreKey, Object delCoreKey)
/*     */       throws IOException
/*     */     {
/*  84 */       if (this.cache == null)
/*  85 */         this.cache = new WeakHashMap();
/*     */       Object value;
/*     */       Object value;
/*  88 */       if (this.deletesMode == CachingWrapperFilter.DeletesMode.IGNORE)
/*     */       {
/*  90 */         value = this.cache.get(coreKey);
/*     */       }
/*     */       else
/*     */       {
/*     */         Object value;
/*  91 */         if (this.deletesMode == CachingWrapperFilter.DeletesMode.RECACHE)
/*     */         {
/*  93 */           value = this.cache.get(delCoreKey);
/*     */         } else {
/*  95 */           assert (this.deletesMode == CachingWrapperFilter.DeletesMode.DYNAMIC);
/*     */ 
/*  98 */           value = this.cache.get(delCoreKey);
/*     */ 
/* 100 */           if (value == null)
/*     */           {
/* 103 */             value = this.cache.get(coreKey);
/* 104 */             if ((value != null) && (reader.hasDeletions())) {
/* 105 */               value = mergeDeletes(reader, value);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 110 */       return value;
/*     */     }
/*     */     protected abstract T mergeDeletes(IndexReader paramIndexReader, T paramT);
/*     */ 
/*     */     public synchronized void put(Object coreKey, Object delCoreKey, T value) {
/* 116 */       if (this.deletesMode == CachingWrapperFilter.DeletesMode.IGNORE) {
/* 117 */         this.cache.put(coreKey, value);
/* 118 */       } else if (this.deletesMode == CachingWrapperFilter.DeletesMode.RECACHE) {
/* 119 */         this.cache.put(delCoreKey, value);
/*     */       } else {
/* 121 */         this.cache.put(coreKey, value);
/* 122 */         this.cache.put(delCoreKey, value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum DeletesMode
/*     */   {
/*  62 */     IGNORE, RECACHE, DYNAMIC;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.CachingWrapperFilter
 * JD-Core Version:    0.6.0
 */