/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ 
/*     */ public class CachingSpanFilter extends SpanFilter
/*     */ {
/*     */   private SpanFilter filter;
/*     */   private final CachingWrapperFilter.FilterCache<SpanFilterResult> cache;
/*     */   int hitCount;
/*     */   int missCount;
/*     */ 
/*     */   public CachingSpanFilter(SpanFilter filter)
/*     */   {
/*  41 */     this(filter, CachingWrapperFilter.DeletesMode.RECACHE);
/*     */   }
/*     */ 
/*     */   public CachingSpanFilter(SpanFilter filter, CachingWrapperFilter.DeletesMode deletesMode)
/*     */   {
/*  49 */     this.filter = filter;
/*  50 */     if (deletesMode == CachingWrapperFilter.DeletesMode.DYNAMIC) {
/*  51 */       throw new IllegalArgumentException("DeletesMode.DYNAMIC is not supported");
/*     */     }
/*  53 */     this.cache = new CachingWrapperFilter.FilterCache(deletesMode)
/*     */     {
/*     */       protected SpanFilterResult mergeDeletes(IndexReader reader, SpanFilterResult value) {
/*  56 */         throw new IllegalStateException("DeletesMode.DYNAMIC is not supported");
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
/*  63 */     SpanFilterResult result = getCachedResult(reader);
/*  64 */     return result != null ? result.getDocIdSet() : null;
/*     */   }
/*     */ 
/*     */   private SpanFilterResult getCachedResult(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  72 */     Object coreKey = reader.getCoreCacheKey();
/*  73 */     Object delCoreKey = reader.hasDeletions() ? reader.getDeletesCacheKey() : coreKey;
/*     */ 
/*  75 */     SpanFilterResult result = (SpanFilterResult)this.cache.get(reader, coreKey, delCoreKey);
/*  76 */     if (result != null) {
/*  77 */       this.hitCount += 1;
/*  78 */       return result;
/*     */     }
/*     */ 
/*  81 */     this.missCount += 1;
/*  82 */     result = this.filter.bitSpans(reader);
/*     */ 
/*  84 */     this.cache.put(coreKey, delCoreKey, result);
/*  85 */     return result;
/*     */   }
/*     */ 
/*     */   public SpanFilterResult bitSpans(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  91 */     return getCachedResult(reader);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  96 */     return "CachingSpanFilter(" + this.filter + ")";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 101 */     if (!(o instanceof CachingSpanFilter)) return false;
/* 102 */     return this.filter.equals(((CachingSpanFilter)o).filter);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 107 */     return this.filter.hashCode() ^ 0x1117BF25;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.CachingSpanFilter
 * JD-Core Version:    0.6.0
 */