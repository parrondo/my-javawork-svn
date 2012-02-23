/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.PriorityQueue;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ public abstract class TopTermsRewrite<Q extends Query> extends TermCollectingRewrite<Q>
/*     */ {
/*     */   private final int size;
/*     */ 
/*     */   public TopTermsRewrite(int size)
/*     */   {
/*  43 */     this.size = size;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/*  48 */     return this.size;
/*     */   }
/*     */ 
/*     */   protected abstract int getMaxSize();
/*     */ 
/*     */   public Q rewrite(IndexReader reader, MultiTermQuery query) throws IOException
/*     */   {
/*  56 */     int maxSize = Math.min(this.size, getMaxSize());
/*  57 */     PriorityQueue stQueue = new PriorityQueue();
/*  58 */     collectTerms(reader, query, new TermCollectingRewrite.TermCollector(stQueue, maxSize)
/*     */     {
/*  73 */       private TopTermsRewrite.ScoreTerm st = new TopTermsRewrite.ScoreTerm(null);
/*     */ 
/*     */       public boolean collect(Term t, float boost)
/*     */       {
/*  61 */         if ((this.val$stQueue.size() >= this.val$maxSize) && (boost <= ((TopTermsRewrite.ScoreTerm)this.val$stQueue.peek()).boost)) {
/*  62 */           return true;
/*     */         }
/*  64 */         this.st.term = t;
/*  65 */         this.st.boost = boost;
/*  66 */         this.val$stQueue.offer(this.st);
/*     */ 
/*  68 */         this.st = (this.val$stQueue.size() > this.val$maxSize ? (TopTermsRewrite.ScoreTerm)this.val$stQueue.poll() : new TopTermsRewrite.ScoreTerm(null));
/*  69 */         return true;
/*     */       }
/*     */     });
/*  76 */     Query q = getTopLevelQuery();
/*  77 */     for (ScoreTerm st : stQueue) {
/*  78 */       addClause(q, st.term, query.getBoost() * st.boost);
/*     */     }
/*  80 */     query.incTotalNumberOfTerms(stQueue.size());
/*     */ 
/*  82 */     return q;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  87 */     return 31 * this.size;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  92 */     if (this == obj) return true;
/*  93 */     if (obj == null) return false;
/*  94 */     if (getClass() != obj.getClass()) return false;
/*  95 */     TopTermsRewrite other = (TopTermsRewrite)obj;
/*  96 */     return this.size == other.size;
/*     */   }
/*     */   private static class ScoreTerm implements Comparable<ScoreTerm> {
/*     */     public Term term;
/*     */     public float boost;
/*     */ 
/*     */     public int compareTo(ScoreTerm other) {
/* 105 */       if (this.boost == other.boost) {
/* 106 */         return other.term.compareTo(this.term);
/*     */       }
/* 108 */       return Float.compare(this.boost, other.boost);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TopTermsRewrite
 * JD-Core Version:    0.6.0
 */