/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermDocs;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class MatchAllDocsQuery extends Query
/*     */ {
/*     */   private final String normsField;
/*     */ 
/*     */   public MatchAllDocsQuery()
/*     */   {
/*  35 */     this(null);
/*     */   }
/*     */ 
/*     */   public MatchAllDocsQuery(String normsField)
/*     */   {
/*  44 */     this.normsField = normsField;
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */   {
/* 140 */     return new MatchAllDocsWeight(searcher);
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 149 */     StringBuilder buffer = new StringBuilder();
/* 150 */     buffer.append("*:*");
/* 151 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 152 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 157 */     if (!(o instanceof MatchAllDocsQuery))
/* 158 */       return false;
/* 159 */     MatchAllDocsQuery other = (MatchAllDocsQuery)o;
/* 160 */     return getBoost() == other.getBoost();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 165 */     return Float.floatToIntBits(getBoost()) ^ 0x1AA71190;
/*     */   }
/*     */ 
/*     */   private class MatchAllDocsWeight extends Weight
/*     */   {
/*     */     private Similarity similarity;
/*     */     private float queryWeight;
/*     */     private float queryNorm;
/*     */ 
/*     */     public MatchAllDocsWeight(Searcher searcher)
/*     */     {
/*  88 */       this.similarity = searcher.getSimilarity();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  93 */       return "weight(" + MatchAllDocsQuery.this + ")";
/*     */     }
/*     */ 
/*     */     public Query getQuery()
/*     */     {
/*  98 */       return MatchAllDocsQuery.this;
/*     */     }
/*     */ 
/*     */     public float getValue()
/*     */     {
/* 103 */       return this.queryWeight;
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights()
/*     */     {
/* 108 */       this.queryWeight = MatchAllDocsQuery.this.getBoost();
/* 109 */       return this.queryWeight * this.queryWeight;
/*     */     }
/*     */ 
/*     */     public void normalize(float queryNorm)
/*     */     {
/* 114 */       this.queryNorm = queryNorm;
/* 115 */       this.queryWeight *= this.queryNorm;
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
/*     */     {
/* 120 */       return new MatchAllDocsQuery.MatchAllScorer(MatchAllDocsQuery.this, reader, this.similarity, this, MatchAllDocsQuery.this.normsField != null ? reader.norms(MatchAllDocsQuery.this.normsField) : null);
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc)
/*     */     {
/* 127 */       Explanation queryExpl = new ComplexExplanation(true, getValue(), "MatchAllDocsQuery, product of:");
/*     */ 
/* 129 */       if (MatchAllDocsQuery.this.getBoost() != 1.0F) {
/* 130 */         queryExpl.addDetail(new Explanation(MatchAllDocsQuery.this.getBoost(), "boost"));
/*     */       }
/* 132 */       queryExpl.addDetail(new Explanation(this.queryNorm, "queryNorm"));
/*     */ 
/* 134 */       return queryExpl;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MatchAllScorer extends Scorer
/*     */   {
/*     */     final TermDocs termDocs;
/*     */     final float score;
/*     */     final byte[] norms;
/*  51 */     private int doc = -1;
/*     */ 
/*     */     MatchAllScorer(IndexReader reader, Similarity similarity, Weight w, byte[] norms) throws IOException
/*     */     {
/*  55 */       super(w);
/*  56 */       this.termDocs = reader.termDocs(null);
/*  57 */       this.score = w.getValue();
/*  58 */       this.norms = norms;
/*     */     }
/*     */ 
/*     */     public int docID()
/*     */     {
/*  63 */       return this.doc;
/*     */     }
/*     */ 
/*     */     public int nextDoc() throws IOException
/*     */     {
/*  68 */       return this.doc = this.termDocs.next() ? this.termDocs.doc() : 2147483647;
/*     */     }
/*     */ 
/*     */     public float score()
/*     */     {
/*  73 */       return this.norms == null ? this.score : this.score * getSimilarity().decodeNormValue(this.norms[docID()]);
/*     */     }
/*     */ 
/*     */     public int advance(int target) throws IOException
/*     */     {
/*  78 */       return this.doc = this.termDocs.skipTo(target) ? this.termDocs.doc() : 2147483647;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.MatchAllDocsQuery
 * JD-Core Version:    0.6.0
 */