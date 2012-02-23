/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ 
/*    */ public class TotalHitCountCollector extends Collector
/*    */ {
/*    */   private int totalHits;
/*    */ 
/*    */   public int getTotalHits()
/*    */   {
/* 31 */     return this.totalHits;
/*    */   }
/*    */ 
/*    */   public void setScorer(Scorer scorer)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void collect(int doc)
/*    */   {
/* 40 */     this.totalHits += 1;
/*    */   }
/*    */ 
/*    */   public void setNextReader(IndexReader reader, int docBase)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean acceptsDocsOutOfOrder()
/*    */   {
/* 49 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TotalHitCountCollector
 * JD-Core Version:    0.6.0
 */