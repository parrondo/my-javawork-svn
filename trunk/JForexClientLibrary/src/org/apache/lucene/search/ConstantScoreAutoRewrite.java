/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ class ConstantScoreAutoRewrite extends TermCollectingRewrite<BooleanQuery>
/*     */ {
/*  31 */   public static int DEFAULT_TERM_COUNT_CUTOFF = 350;
/*     */ 
/*  35 */   public static double DEFAULT_DOC_COUNT_PERCENT = 0.1D;
/*     */   private int termCountCutoff;
/*     */   private double docCountPercent;
/*     */ 
/*     */   ConstantScoreAutoRewrite()
/*     */   {
/*  37 */     this.termCountCutoff = DEFAULT_TERM_COUNT_CUTOFF;
/*  38 */     this.docCountPercent = DEFAULT_DOC_COUNT_PERCENT;
/*     */   }
/*     */ 
/*     */   public void setTermCountCutoff(int count)
/*     */   {
/*  44 */     this.termCountCutoff = count;
/*     */   }
/*     */ 
/*     */   public int getTermCountCutoff()
/*     */   {
/*  49 */     return this.termCountCutoff;
/*     */   }
/*     */ 
/*     */   public void setDocCountPercent(double percent)
/*     */   {
/*  58 */     this.docCountPercent = percent;
/*     */   }
/*     */ 
/*     */   public double getDocCountPercent()
/*     */   {
/*  63 */     return this.docCountPercent;
/*     */   }
/*     */ 
/*     */   protected BooleanQuery getTopLevelQuery()
/*     */   {
/*  68 */     return new BooleanQuery(true);
/*     */   }
/*     */ 
/*     */   protected void addClause(BooleanQuery topLevel, Term term, float boost)
/*     */   {
/*  73 */     topLevel.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader, MultiTermQuery query)
/*     */     throws IOException
/*     */   {
/*  83 */     int docCountCutoff = (int)(this.docCountPercent / 100.0D * reader.maxDoc());
/*  84 */     int termCountLimit = Math.min(BooleanQuery.getMaxClauseCount(), this.termCountCutoff);
/*     */ 
/*  86 */     CutOffTermCollector col = new CutOffTermCollector(reader, docCountCutoff, termCountLimit);
/*  87 */     collectTerms(reader, query, col);
/*     */ 
/*  89 */     if (col.hasCutOff)
/*  90 */       return MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE.rewrite(reader, query);
/*     */     Query result;
/*     */     Query result;
/*  93 */     if (col.pendingTerms.isEmpty()) {
/*  94 */       result = getTopLevelQuery();
/*     */     } else {
/*  96 */       BooleanQuery bq = getTopLevelQuery();
/*  97 */       for (Term term : col.pendingTerms) {
/*  98 */         addClause(bq, term, 1.0F);
/*     */       }
/*     */ 
/* 101 */       result = new ConstantScoreQuery(bq);
/* 102 */       result.setBoost(query.getBoost());
/*     */     }
/* 104 */     query.incTotalNumberOfTerms(col.pendingTerms.size());
/* 105 */     return result;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 140 */     int prime = 1279;
/* 141 */     return (int)(1279 * this.termCountCutoff + Double.doubleToLongBits(this.docCountPercent));
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 146 */     if (this == obj)
/* 147 */       return true;
/* 148 */     if (obj == null)
/* 149 */       return false;
/* 150 */     if (getClass() != obj.getClass()) {
/* 151 */       return false;
/*     */     }
/* 153 */     ConstantScoreAutoRewrite other = (ConstantScoreAutoRewrite)obj;
/* 154 */     if (other.termCountCutoff != this.termCountCutoff) {
/* 155 */       return false;
/*     */     }
/*     */ 
/* 159 */     return Double.doubleToLongBits(other.docCountPercent) == Double.doubleToLongBits(this.docCountPercent);
/*     */   }
/*     */ 
/*     */   private static final class CutOffTermCollector
/*     */     implements TermCollectingRewrite.TermCollector
/*     */   {
/* 130 */     int docVisitCount = 0;
/* 131 */     boolean hasCutOff = false;
/*     */     final IndexReader reader;
/*     */     final int docCountCutoff;
/*     */     final int termCountLimit;
/* 135 */     final ArrayList<Term> pendingTerms = new ArrayList();
/*     */ 
/*     */     CutOffTermCollector(IndexReader reader, int docCountCutoff, int termCountLimit)
/*     */     {
/* 111 */       this.reader = reader;
/* 112 */       this.docCountCutoff = docCountCutoff;
/* 113 */       this.termCountLimit = termCountLimit;
/*     */     }
/*     */ 
/*     */     public boolean collect(Term t, float boost) throws IOException {
/* 117 */       this.pendingTerms.add(t);
/*     */ 
/* 122 */       this.docVisitCount += this.reader.docFreq(t);
/* 123 */       if ((this.pendingTerms.size() >= this.termCountLimit) || (this.docVisitCount >= this.docCountCutoff)) {
/* 124 */         this.hasCutOff = true;
/* 125 */         return false;
/*     */       }
/* 127 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ConstantScoreAutoRewrite
 * JD-Core Version:    0.6.0
 */