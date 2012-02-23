/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import org.apache.lucene.util.PriorityQueue;
/*    */ 
/*    */ final class HitQueue extends PriorityQueue<ScoreDoc>
/*    */ {
/*    */   private boolean prePopulate;
/*    */ 
/*    */   HitQueue(int size, boolean prePopulate)
/*    */   {
/* 66 */     this.prePopulate = prePopulate;
/* 67 */     initialize(size);
/*    */   }
/*    */ 
/*    */   protected ScoreDoc getSentinelObject()
/*    */   {
/* 76 */     return !this.prePopulate ? null : new ScoreDoc(2147483647, (1.0F / -1.0F));
/*    */   }
/*    */ 
/*    */   protected final boolean lessThan(ScoreDoc hitA, ScoreDoc hitB)
/*    */   {
/* 81 */     if (hitA.score == hitB.score) {
/* 82 */       return hitA.doc > hitB.doc;
/*    */     }
/* 84 */     return hitA.score < hitB.score;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.HitQueue
 * JD-Core Version:    0.6.0
 */