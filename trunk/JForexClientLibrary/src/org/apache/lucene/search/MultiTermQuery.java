/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ public abstract class MultiTermQuery extends Query
/*     */ {
/*  62 */   protected RewriteMethod rewriteMethod = CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
/*  63 */   transient int numberOfTerms = 0;
/*     */ 
/*  82 */   public static final RewriteMethod CONSTANT_SCORE_FILTER_REWRITE = new RewriteMethod()
/*     */   {
/*     */     public Query rewrite(IndexReader reader, MultiTermQuery query) {
/*  85 */       Query result = new ConstantScoreQuery(new MultiTermQueryWrapperFilter(query));
/*  86 */       result.setBoost(query.getBoost());
/*  87 */       return result;
/*     */     }
/*     */ 
/*     */     protected Object readResolve()
/*     */     {
/*  92 */       return MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE;
/*     */     }
/*  82 */   };
/*     */ 
/* 109 */   public static final RewriteMethod SCORING_BOOLEAN_QUERY_REWRITE = ScoringRewrite.SCORING_BOOLEAN_QUERY_REWRITE;
/*     */ 
/* 121 */   public static final RewriteMethod CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE = ScoringRewrite.CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE;
/*     */ 
/* 229 */   public static final RewriteMethod CONSTANT_SCORE_AUTO_REWRITE_DEFAULT = new ConstantScoreAutoRewrite()
/*     */   {
/*     */     public void setTermCountCutoff(int count) {
/* 232 */       throw new UnsupportedOperationException("Please create a private instance");
/*     */     }
/*     */ 
/*     */     public void setDocCountPercent(double percent)
/*     */     {
/* 237 */       throw new UnsupportedOperationException("Please create a private instance");
/*     */     }
/*     */ 
/*     */     protected Object readResolve()
/*     */     {
/* 242 */       return MultiTermQuery.CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
/*     */     }
/* 229 */   };
/*     */ 
/*     */   protected abstract FilteredTermEnum getEnum(IndexReader paramIndexReader)
/*     */     throws IOException;
/*     */ 
/*     */   public int getTotalNumberOfTerms()
/*     */   {
/* 273 */     return this.numberOfTerms;
/*     */   }
/*     */ 
/*     */   public void clearTotalNumberOfTerms()
/*     */   {
/* 282 */     this.numberOfTerms = 0;
/*     */   }
/*     */ 
/*     */   protected void incTotalNumberOfTerms(int inc) {
/* 286 */     this.numberOfTerms += inc;
/*     */   }
/*     */ 
/*     */   public final Query rewrite(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 296 */     return this.rewriteMethod.rewrite(reader, this);
/*     */   }
/*     */ 
/*     */   public RewriteMethod getRewriteMethod()
/*     */   {
/* 303 */     return this.rewriteMethod;
/*     */   }
/*     */ 
/*     */   public void setRewriteMethod(RewriteMethod method)
/*     */   {
/* 311 */     this.rewriteMethod = method;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 316 */     int prime = 31;
/* 317 */     int result = 1;
/* 318 */     result = 31 * result + Float.floatToIntBits(getBoost());
/* 319 */     result = 31 * result;
/* 320 */     result += this.rewriteMethod.hashCode();
/* 321 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 326 */     if (this == obj)
/* 327 */       return true;
/* 328 */     if (obj == null)
/* 329 */       return false;
/* 330 */     if (getClass() != obj.getClass())
/* 331 */       return false;
/* 332 */     MultiTermQuery other = (MultiTermQuery)obj;
/* 333 */     if (Float.floatToIntBits(getBoost()) != Float.floatToIntBits(other.getBoost())) {
/* 334 */       return false;
/*     */     }
/* 336 */     return this.rewriteMethod.equals(other.rewriteMethod);
/*     */   }
/*     */ 
/*     */   public static class ConstantScoreAutoRewrite extends ConstantScoreAutoRewrite
/*     */   {
/*     */   }
/*     */ 
/*     */   public static final class TopTermsBoostOnlyBooleanQueryRewrite extends TopTermsRewrite<BooleanQuery>
/*     */   {
/*     */     public TopTermsBoostOnlyBooleanQueryRewrite(int size)
/*     */     {
/* 186 */       super();
/*     */     }
/*     */ 
/*     */     protected int getMaxSize()
/*     */     {
/* 191 */       return BooleanQuery.getMaxClauseCount();
/*     */     }
/*     */ 
/*     */     protected BooleanQuery getTopLevelQuery()
/*     */     {
/* 196 */       return new BooleanQuery(true);
/*     */     }
/*     */ 
/*     */     protected void addClause(BooleanQuery topLevel, Term term, float boost)
/*     */     {
/* 201 */       Query q = new ConstantScoreQuery(new TermQuery(term));
/* 202 */       q.setBoost(boost);
/* 203 */       topLevel.add(q, BooleanClause.Occur.SHOULD);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class TopTermsScoringBooleanQueryRewrite extends TopTermsRewrite<BooleanQuery>
/*     */   {
/*     */     public TopTermsScoringBooleanQueryRewrite(int size)
/*     */     {
/* 145 */       super();
/*     */     }
/*     */ 
/*     */     protected int getMaxSize()
/*     */     {
/* 150 */       return BooleanQuery.getMaxClauseCount();
/*     */     }
/*     */ 
/*     */     protected BooleanQuery getTopLevelQuery()
/*     */     {
/* 155 */       return new BooleanQuery(true);
/*     */     }
/*     */ 
/*     */     protected void addClause(BooleanQuery topLevel, Term term, float boost)
/*     */     {
/* 160 */       TermQuery tq = new TermQuery(term);
/* 161 */       tq.setBoost(boost);
/* 162 */       topLevel.add(tq, BooleanClause.Occur.SHOULD);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class RewriteMethod
/*     */     implements Serializable
/*     */   {
/*     */     public abstract Query rewrite(IndexReader paramIndexReader, MultiTermQuery paramMultiTermQuery)
/*     */       throws IOException;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.MultiTermQuery
 * JD-Core Version:    0.6.0
 */