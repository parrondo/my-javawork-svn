/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.ComplexExplanation;
/*     */ import org.apache.lucene.search.Explanation;
/*     */ import org.apache.lucene.search.Explanation.IDFExplanation;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.Scorer;
/*     */ import org.apache.lucene.search.Searcher;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.search.Weight;
/*     */ 
/*     */ public class SpanWeight extends Weight
/*     */ {
/*     */   protected Similarity similarity;
/*     */   protected float value;
/*     */   protected float idf;
/*     */   protected float queryNorm;
/*     */   protected float queryWeight;
/*     */   protected Set<Term> terms;
/*     */   protected SpanQuery query;
/*     */   private Explanation.IDFExplanation idfExp;
/*     */ 
/*     */   public SpanWeight(SpanQuery query, Searcher searcher)
/*     */     throws IOException
/*     */   {
/*  45 */     this.similarity = query.getSimilarity(searcher);
/*  46 */     this.query = query;
/*     */ 
/*  48 */     this.terms = new HashSet();
/*  49 */     query.extractTerms(this.terms);
/*     */ 
/*  51 */     this.idfExp = this.similarity.idfExplain(this.terms, searcher);
/*  52 */     this.idf = this.idfExp.getIdf();
/*     */   }
/*     */ 
/*     */   public Query getQuery() {
/*  56 */     return this.query;
/*     */   }
/*     */   public float getValue() {
/*  59 */     return this.value;
/*     */   }
/*     */ 
/*     */   public float sumOfSquaredWeights() throws IOException {
/*  63 */     this.queryWeight = (this.idf * this.query.getBoost());
/*  64 */     return this.queryWeight * this.queryWeight;
/*     */   }
/*     */ 
/*     */   public void normalize(float queryNorm)
/*     */   {
/*  69 */     this.queryNorm = queryNorm;
/*  70 */     this.queryWeight *= queryNorm;
/*  71 */     this.value = (this.queryWeight * this.idf);
/*     */   }
/*     */ 
/*     */   public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
/*     */   {
/*  76 */     return new SpanScorer(this.query.getSpans(reader), this, this.similarity, reader.norms(this.query.getField()));
/*     */   }
/*     */ 
/*     */   public Explanation explain(IndexReader reader, int doc)
/*     */     throws IOException
/*     */   {
/*  84 */     ComplexExplanation result = new ComplexExplanation();
/*  85 */     result.setDescription("weight(" + getQuery() + " in " + doc + "), product of:");
/*  86 */     String field = ((SpanQuery)getQuery()).getField();
/*     */ 
/*  88 */     Explanation idfExpl = new Explanation(this.idf, "idf(" + field + ": " + this.idfExp.explain() + ")");
/*     */ 
/*  92 */     Explanation queryExpl = new Explanation();
/*  93 */     queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");
/*     */ 
/*  95 */     Explanation boostExpl = new Explanation(getQuery().getBoost(), "boost");
/*  96 */     if (getQuery().getBoost() != 1.0F)
/*  97 */       queryExpl.addDetail(boostExpl);
/*  98 */     queryExpl.addDetail(idfExpl);
/*     */ 
/* 100 */     Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
/* 101 */     queryExpl.addDetail(queryNormExpl);
/*     */ 
/* 103 */     queryExpl.setValue(boostExpl.getValue() * idfExpl.getValue() * queryNormExpl.getValue());
/*     */ 
/* 107 */     result.addDetail(queryExpl);
/*     */ 
/* 110 */     ComplexExplanation fieldExpl = new ComplexExplanation();
/* 111 */     fieldExpl.setDescription("fieldWeight(" + field + ":" + this.query.toString(field) + " in " + doc + "), product of:");
/*     */ 
/* 114 */     Explanation tfExpl = ((SpanScorer)scorer(reader, true, false)).explain(doc);
/* 115 */     fieldExpl.addDetail(tfExpl);
/* 116 */     fieldExpl.addDetail(idfExpl);
/*     */ 
/* 118 */     Explanation fieldNormExpl = new Explanation();
/* 119 */     byte[] fieldNorms = reader.norms(field);
/* 120 */     float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0F;
/*     */ 
/* 122 */     fieldNormExpl.setValue(fieldNorm);
/* 123 */     fieldNormExpl.setDescription("fieldNorm(field=" + field + ", doc=" + doc + ")");
/* 124 */     fieldExpl.addDetail(fieldNormExpl);
/*     */ 
/* 126 */     fieldExpl.setMatch(Boolean.valueOf(tfExpl.isMatch()));
/* 127 */     fieldExpl.setValue(tfExpl.getValue() * idfExpl.getValue() * fieldNormExpl.getValue());
/*     */ 
/* 131 */     result.addDetail(fieldExpl);
/* 132 */     result.setMatch(fieldExpl.getMatch());
/*     */ 
/* 135 */     result.setValue(queryExpl.getValue() * fieldExpl.getValue());
/*     */ 
/* 137 */     if (queryExpl.getValue() == 1.0F) {
/* 138 */       return fieldExpl;
/*     */     }
/* 140 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanWeight
 * JD-Core Version:    0.6.0
 */