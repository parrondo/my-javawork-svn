/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ 
/*    */ public class PositiveScoresOnlyCollector extends Collector
/*    */ {
/*    */   private final Collector c;
/*    */   private Scorer scorer;
/*    */ 
/*    */   public PositiveScoresOnlyCollector(Collector c)
/*    */   {
/* 35 */     this.c = c;
/*    */   }
/*    */ 
/*    */   public void collect(int doc) throws IOException
/*    */   {
/* 40 */     if (this.scorer.score() > 0.0F)
/* 41 */       this.c.collect(doc);
/*    */   }
/*    */ 
/*    */   public void setNextReader(IndexReader reader, int docBase)
/*    */     throws IOException
/*    */   {
/* 47 */     this.c.setNextReader(reader, docBase);
/*    */   }
/*    */ 
/*    */   public void setScorer(Scorer scorer)
/*    */     throws IOException
/*    */   {
/* 54 */     this.scorer = new ScoreCachingWrappingScorer(scorer);
/* 55 */     this.c.setScorer(this.scorer);
/*    */   }
/*    */ 
/*    */   public boolean acceptsDocsOutOfOrder()
/*    */   {
/* 60 */     return this.c.acceptsDocsOutOfOrder();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PositiveScoresOnlyCollector
 * JD-Core Version:    0.6.0
 */