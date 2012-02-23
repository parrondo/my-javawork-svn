/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ class ReqOptSumScorer extends Scorer
/*    */ {
/*    */   private Scorer reqScorer;
/*    */   private Scorer optScorer;
/*    */ 
/*    */   public ReqOptSumScorer(Scorer reqScorer, Scorer optScorer)
/*    */   {
/* 41 */     super(reqScorer.weight);
/* 42 */     this.reqScorer = reqScorer;
/* 43 */     this.optScorer = optScorer;
/*    */   }
/*    */ 
/*    */   public int nextDoc() throws IOException
/*    */   {
/* 48 */     return this.reqScorer.nextDoc();
/*    */   }
/*    */ 
/*    */   public int advance(int target) throws IOException
/*    */   {
/* 53 */     return this.reqScorer.advance(target);
/*    */   }
/*    */ 
/*    */   public int docID()
/*    */   {
/* 58 */     return this.reqScorer.docID();
/*    */   }
/*    */ 
/*    */   public float score()
/*    */     throws IOException
/*    */   {
/* 68 */     int curDoc = this.reqScorer.docID();
/* 69 */     float reqScore = this.reqScorer.score();
/* 70 */     if (this.optScorer == null) {
/* 71 */       return reqScore;
/*    */     }
/*    */ 
/* 74 */     int optScorerDoc = this.optScorer.docID();
/* 75 */     if ((optScorerDoc < curDoc) && ((optScorerDoc = this.optScorer.advance(curDoc)) == 2147483647)) {
/* 76 */       this.optScorer = null;
/* 77 */       return reqScore;
/*    */     }
/*    */ 
/* 80 */     return optScorerDoc == curDoc ? reqScore + this.optScorer.score() : reqScore;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ReqOptSumScorer
 * JD-Core Version:    0.6.0
 */