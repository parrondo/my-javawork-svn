/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ public abstract class ScoringRewrite<Q extends Query> extends TermCollectingRewrite<Q>
/*     */ {
/*  41 */   public static final ScoringRewrite<BooleanQuery> SCORING_BOOLEAN_QUERY_REWRITE = new ScoringRewrite()
/*     */   {
/*     */     protected BooleanQuery getTopLevelQuery() {
/*  44 */       return new BooleanQuery(true);
/*     */     }
/*     */ 
/*     */     protected void addClause(BooleanQuery topLevel, Term term, float boost)
/*     */     {
/*  49 */       TermQuery tq = new TermQuery(term);
/*  50 */       tq.setBoost(boost);
/*  51 */       topLevel.add(tq, BooleanClause.Occur.SHOULD);
/*     */     }
/*     */ 
/*     */     protected Object readResolve()
/*     */     {
/*  56 */       return SCORING_BOOLEAN_QUERY_REWRITE;
/*     */     }
/*  41 */   };
/*     */ 
/*  70 */   public static final MultiTermQuery.RewriteMethod CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE = new MultiTermQuery.RewriteMethod()
/*     */   {
/*     */     public Query rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
/*  73 */       BooleanQuery bq = (BooleanQuery)ScoringRewrite.SCORING_BOOLEAN_QUERY_REWRITE.rewrite(reader, query);
/*     */ 
/*  75 */       if (bq.clauses().isEmpty()) {
/*  76 */         return bq;
/*     */       }
/*  78 */       Query result = new ConstantScoreQuery(bq);
/*  79 */       result.setBoost(query.getBoost());
/*  80 */       return result;
/*     */     }
/*     */ 
/*     */     protected Object readResolve()
/*     */     {
/*  85 */       return ScoringRewrite.CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE;
/*     */     }
/*  70 */   };
/*     */ 
/*     */   public Q rewrite(IndexReader reader, MultiTermQuery query)
/*     */     throws IOException
/*     */   {
/*  91 */     Query result = getTopLevelQuery();
/*  92 */     int[] size = new int[1];
/*  93 */     collectTerms(reader, query, new TermCollectingRewrite.TermCollector(result, query, size) {
/*     */       public boolean collect(Term t, float boost) throws IOException {
/*  95 */         ScoringRewrite.this.addClause(this.val$result, t, this.val$query.getBoost() * boost);
/*  96 */         this.val$size[0] += 1;
/*  97 */         return true;
/*     */       }
/*     */     });
/* 100 */     query.incTotalNumberOfTerms(size[0]);
/* 101 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ScoringRewrite
 * JD-Core Version:    0.6.0
 */