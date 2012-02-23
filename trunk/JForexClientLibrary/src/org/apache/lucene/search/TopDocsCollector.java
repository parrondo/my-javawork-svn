/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ public abstract class TopDocsCollector<T extends ScoreDoc> extends Collector
/*     */ {
/*  38 */   protected static final TopDocs EMPTY_TOPDOCS = new TopDocs(0, new ScoreDoc[0], (0.0F / 0.0F));
/*     */   protected PriorityQueue<T> pq;
/*     */   protected int totalHits;
/*     */ 
/*     */   protected TopDocsCollector(PriorityQueue<T> pq)
/*     */   {
/*  52 */     this.pq = pq;
/*     */   }
/*     */ 
/*     */   protected void populateResults(ScoreDoc[] results, int howMany)
/*     */   {
/*  60 */     for (int i = howMany - 1; i >= 0; i--)
/*  61 */       results[i] = ((ScoreDoc)this.pq.pop());
/*     */   }
/*     */ 
/*     */   protected TopDocs newTopDocs(ScoreDoc[] results, int start)
/*     */   {
/*  72 */     return results == null ? EMPTY_TOPDOCS : new TopDocs(this.totalHits, results);
/*     */   }
/*     */ 
/*     */   public int getTotalHits()
/*     */   {
/*  77 */     return this.totalHits;
/*     */   }
/*     */ 
/*     */   public TopDocs topDocs()
/*     */   {
/*  85 */     return topDocs(0, this.totalHits < this.pq.size() ? this.totalHits : this.pq.size());
/*     */   }
/*     */ 
/*     */   public TopDocs topDocs(int start)
/*     */   {
/* 104 */     return topDocs(start, this.totalHits < this.pq.size() ? this.totalHits : this.pq.size());
/*     */   }
/*     */ 
/*     */   public TopDocs topDocs(int start, int howMany)
/*     */   {
/* 126 */     int size = this.totalHits < this.pq.size() ? this.totalHits : this.pq.size();
/*     */ 
/* 130 */     if ((start < 0) || (start >= size) || (howMany <= 0)) {
/* 131 */       return newTopDocs(null, start);
/*     */     }
/*     */ 
/* 135 */     howMany = Math.min(size - start, howMany);
/* 136 */     ScoreDoc[] results = new ScoreDoc[howMany];
/*     */ 
/* 143 */     for (int i = this.pq.size() - start - howMany; i > 0; i--) this.pq.pop();
/*     */ 
/* 146 */     populateResults(results, howMany);
/*     */ 
/* 148 */     return newTopDocs(results, start);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TopDocsCollector
 * JD-Core Version:    0.6.0
 */