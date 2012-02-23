/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ScoreCachingWrappingScorer extends Scorer
/*    */ {
/*    */   private final Scorer scorer;
/* 36 */   private int curDoc = -1;
/*    */   private float curScore;
/*    */ 
/*    */   public ScoreCachingWrappingScorer(Scorer scorer)
/*    */   {
/* 41 */     super(scorer.getSimilarity(), scorer.weight);
/* 42 */     this.scorer = scorer;
/*    */   }
/*    */ 
/*    */   protected boolean score(Collector collector, int max, int firstDocID) throws IOException
/*    */   {
/* 47 */     return this.scorer.score(collector, max, firstDocID);
/*    */   }
/*    */ 
/*    */   public Similarity getSimilarity()
/*    */   {
/* 52 */     return this.scorer.getSimilarity();
/*    */   }
/*    */ 
/*    */   public float score() throws IOException
/*    */   {
/* 57 */     int doc = this.scorer.docID();
/* 58 */     if (doc != this.curDoc) {
/* 59 */       this.curScore = this.scorer.score();
/* 60 */       this.curDoc = doc;
/*    */     }
/*    */ 
/* 63 */     return this.curScore;
/*    */   }
/*    */ 
/*    */   public int docID()
/*    */   {
/* 68 */     return this.scorer.docID();
/*    */   }
/*    */ 
/*    */   public int nextDoc() throws IOException
/*    */   {
/* 73 */     return this.scorer.nextDoc();
/*    */   }
/*    */ 
/*    */   public void score(Collector collector) throws IOException
/*    */   {
/* 78 */     this.scorer.score(collector);
/*    */   }
/*    */ 
/*    */   public int advance(int target) throws IOException
/*    */   {
/* 83 */     return this.scorer.advance(target);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ScoreCachingWrappingScorer
 * JD-Core Version:    0.6.0
 */