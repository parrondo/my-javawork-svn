/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ import org.apache.lucene.index.Term;
/*    */ 
/*    */ abstract class TermCollectingRewrite<Q extends Query> extends MultiTermQuery.RewriteMethod
/*    */ {
/*    */   protected abstract Q getTopLevelQuery()
/*    */     throws IOException;
/*    */ 
/*    */   protected abstract void addClause(Q paramQ, Term paramTerm, float paramFloat)
/*    */     throws IOException;
/*    */ 
/*    */   protected final void collectTerms(IndexReader reader, MultiTermQuery query, TermCollector collector)
/*    */     throws IOException
/*    */   {
/* 34 */     FilteredTermEnum enumerator = query.getEnum(reader);
/*    */     try {
/*    */       do {
/* 37 */         Term t = enumerator.term();
/* 38 */         if ((t == null) || (!collector.collect(t, enumerator.difference()))) break;
/*    */       }
/* 40 */       while (enumerator.next());
/*    */     } finally {
/* 42 */       enumerator.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   protected static abstract interface TermCollector
/*    */   {
/*    */     public abstract boolean collect(Term paramTerm, float paramFloat)
/*    */       throws IOException;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TermCollectingRewrite
 * JD-Core Version:    0.6.0
 */