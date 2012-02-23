/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ public class TopFieldDocs extends TopDocs
/*    */ {
/*    */   public SortField[] fields;
/*    */ 
/*    */   public TopFieldDocs(int totalHits, ScoreDoc[] scoreDocs, SortField[] fields, float maxScore)
/*    */   {
/* 37 */     super(totalHits, scoreDocs, maxScore);
/* 38 */     this.fields = fields;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TopFieldDocs
 * JD-Core Version:    0.6.0
 */