/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ public abstract class TopScoreDocCollector extends TopDocsCollector<ScoreDoc>
/*     */ {
/*     */   ScoreDoc pqTop;
/* 130 */   int docBase = 0;
/*     */   Scorer scorer;
/*     */ 
/*     */   public static TopScoreDocCollector create(int numHits, boolean docsScoredInOrder)
/*     */   {
/* 117 */     if (numHits <= 0) {
/* 118 */       throw new IllegalArgumentException("numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
/*     */     }
/*     */ 
/* 121 */     if (docsScoredInOrder) {
/* 122 */       return new InOrderTopScoreDocCollector(numHits, null);
/*     */     }
/* 124 */     return new OutOfOrderTopScoreDocCollector(numHits, null);
/*     */   }
/*     */ 
/*     */   private TopScoreDocCollector(int numHits)
/*     */   {
/* 135 */     super(new HitQueue(numHits, true));
/*     */ 
/* 138 */     this.pqTop = ((ScoreDoc)this.pq.top());
/*     */   }
/*     */ 
/*     */   protected TopDocs newTopDocs(ScoreDoc[] results, int start)
/*     */   {
/* 143 */     if (results == null) {
/* 144 */       return EMPTY_TOPDOCS;
/*     */     }
/*     */ 
/* 151 */     float maxScore = (0.0F / 0.0F);
/* 152 */     if (start == 0) {
/* 153 */       maxScore = results[0].score;
/*     */     } else {
/* 155 */       for (int i = this.pq.size(); i > 1; i--) this.pq.pop();
/* 156 */       maxScore = ((ScoreDoc)this.pq.pop()).score;
/*     */     }
/*     */ 
/* 159 */     return new TopDocs(this.totalHits, results, maxScore);
/*     */   }
/*     */ 
/*     */   public void setNextReader(IndexReader reader, int base)
/*     */   {
/* 164 */     this.docBase = base;
/*     */   }
/*     */ 
/*     */   public void setScorer(Scorer scorer) throws IOException
/*     */   {
/* 169 */     this.scorer = scorer;
/*     */   }
/*     */ 
/*     */   private static class OutOfOrderTopScoreDocCollector extends TopScoreDocCollector
/*     */   {
/*     */     private OutOfOrderTopScoreDocCollector(int numHits)
/*     */     {
/*  74 */       super(null);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/*  79 */       float score = this.scorer.score();
/*     */ 
/*  82 */       assert (!Float.isNaN(score));
/*     */ 
/*  84 */       this.totalHits += 1;
/*  85 */       if (score < this.pqTop.score)
/*     */       {
/*  87 */         return;
/*     */       }
/*  89 */       doc += this.docBase;
/*  90 */       if ((score == this.pqTop.score) && (doc > this.pqTop.doc))
/*     */       {
/*  92 */         return;
/*     */       }
/*  94 */       this.pqTop.doc = doc;
/*  95 */       this.pqTop.score = score;
/*  96 */       this.pqTop = ((ScoreDoc)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 101 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class InOrderTopScoreDocCollector extends TopScoreDocCollector
/*     */   {
/*     */     private InOrderTopScoreDocCollector(int numHits)
/*     */     {
/*  42 */       super(null);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/*  47 */       float score = this.scorer.score();
/*     */ 
/*  50 */       assert (score != (1.0F / -1.0F));
/*  51 */       assert (!Float.isNaN(score));
/*     */ 
/*  53 */       this.totalHits += 1;
/*  54 */       if (score <= this.pqTop.score)
/*     */       {
/*  58 */         return;
/*     */       }
/*  60 */       this.pqTop.doc = (doc + this.docBase);
/*  61 */       this.pqTop.score = score;
/*  62 */       this.pqTop = ((ScoreDoc)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/*  67 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TopScoreDocCollector
 * JD-Core Version:    0.6.0
 */