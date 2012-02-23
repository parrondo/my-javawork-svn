/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ 
/*    */ public class QueryWrapperFilter extends Filter
/*    */ {
/*    */   private Query query;
/*    */ 
/*    */   public QueryWrapperFilter(Query query)
/*    */   {
/* 42 */     this.query = query;
/*    */   }
/*    */ 
/*    */   public DocIdSet getDocIdSet(IndexReader reader) throws IOException
/*    */   {
/* 47 */     Weight weight = new IndexSearcher(reader).createNormalizedWeight(this.query);
/* 48 */     return new DocIdSet(weight, reader)
/*    */     {
/*    */       public DocIdSetIterator iterator() throws IOException {
/* 51 */         return this.val$weight.scorer(this.val$reader, true, false);
/*    */       }
/*    */       public boolean isCacheable() {
/* 54 */         return false;
/*    */       } } ;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 60 */     return "QueryWrapperFilter(" + this.query + ")";
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 65 */     if (!(o instanceof QueryWrapperFilter))
/* 66 */       return false;
/* 67 */     return this.query.equals(((QueryWrapperFilter)o).query);
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 72 */     return this.query.hashCode() ^ 0x923F64B9;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.QueryWrapperFilter
 * JD-Core Version:    0.6.0
 */