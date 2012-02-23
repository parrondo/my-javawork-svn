/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermDocs;
/*     */ import org.apache.lucene.util.ReaderUtil.Gather;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class TermQuery extends Query
/*     */ {
/*     */   private Term term;
/*     */ 
/*     */   public TermQuery(Term t)
/*     */   {
/* 190 */     this.term = t;
/*     */   }
/*     */ 
/*     */   public Term getTerm() {
/* 194 */     return this.term;
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher) throws IOException {
/* 198 */     return new TermWeight(searcher);
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 203 */     terms.add(getTerm());
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 209 */     StringBuilder buffer = new StringBuilder();
/* 210 */     if (!this.term.field().equals(field)) {
/* 211 */       buffer.append(this.term.field());
/* 212 */       buffer.append(":");
/*     */     }
/* 214 */     buffer.append(this.term.text());
/* 215 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 216 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 222 */     if (!(o instanceof TermQuery))
/* 223 */       return false;
/* 224 */     TermQuery other = (TermQuery)o;
/* 225 */     return (getBoost() == other.getBoost()) && (this.term.equals(other.term));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 232 */     return Float.floatToIntBits(getBoost()) ^ this.term.hashCode();
/*     */   }
/*     */ 
/*     */   private class TermWeight extends Weight
/*     */   {
/*     */     private final Similarity similarity;
/*     */     private float value;
/*     */     private float idf;
/*     */     private float queryNorm;
/*     */     private float queryWeight;
/*     */     private Explanation.IDFExplanation idfExp;
/*     */     private final Set<Integer> hash;
/*     */ 
/*     */     public TermWeight(Searcher searcher)
/*     */       throws IOException
/*     */     {
/*  48 */       this.similarity = TermQuery.this.getSimilarity(searcher);
/*     */       IndexReader ir;
/*  49 */       if ((searcher instanceof IndexSearcher)) {
/*  50 */         this.hash = new HashSet();
/*  51 */         ir = ((IndexSearcher)searcher).getIndexReader();
/*  52 */         int[] dfSum = new int[1];
/*  53 */         new ReaderUtil.Gather(ir, TermQuery.this, dfSum)
/*     */         {
/*     */           protected void add(int base, IndexReader r) throws IOException {
/*  56 */             int df = r.docFreq(TermQuery.this.term);
/*  57 */             this.val$dfSum[0] += df;
/*  58 */             if (df > 0)
/*  59 */               TermQuery.TermWeight.this.hash.add(Integer.valueOf(r.hashCode()));
/*     */           }
/*     */         }
/*  53 */         .run();
/*     */ 
/*  64 */         this.idfExp = this.similarity.idfExplain(TermQuery.this.term, searcher, dfSum[0]);
/*     */       } else {
/*  66 */         this.idfExp = this.similarity.idfExplain(TermQuery.this.term, searcher);
/*  67 */         this.hash = null;
/*     */       }
/*     */ 
/*  70 */       this.idf = this.idfExp.getIdf();
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  74 */       return "weight(" + TermQuery.this + ")";
/*     */     }
/*     */     public Query getQuery() {
/*  77 */       return TermQuery.this;
/*     */     }
/*     */     public float getValue() {
/*  80 */       return this.value;
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights() {
/*  84 */       this.queryWeight = (this.idf * TermQuery.this.getBoost());
/*  85 */       return this.queryWeight * this.queryWeight;
/*     */     }
/*     */ 
/*     */     public void normalize(float queryNorm)
/*     */     {
/*  90 */       this.queryNorm = queryNorm;
/*  91 */       this.queryWeight *= queryNorm;
/*  92 */       this.value = (this.queryWeight * this.idf);
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
/*     */     {
/*  97 */       if ((this.hash != null) && (!this.hash.contains(Integer.valueOf(reader.hashCode())))) {
/*  98 */         return null;
/*     */       }
/*     */ 
/* 101 */       TermDocs termDocs = reader.termDocs(TermQuery.this.term);
/*     */ 
/* 103 */       if (termDocs == null) {
/* 104 */         return null;
/*     */       }
/* 106 */       return new TermScorer(this, termDocs, this.similarity, reader.norms(TermQuery.this.term.field()));
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc)
/*     */       throws IOException
/*     */     {
/* 113 */       ComplexExplanation result = new ComplexExplanation();
/* 114 */       result.setDescription("weight(" + getQuery() + " in " + doc + "), product of:");
/*     */ 
/* 116 */       Explanation expl = new Explanation(this.idf, this.idfExp.explain());
/*     */ 
/* 119 */       Explanation queryExpl = new Explanation();
/* 120 */       queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");
/*     */ 
/* 122 */       Explanation boostExpl = new Explanation(TermQuery.this.getBoost(), "boost");
/* 123 */       if (TermQuery.this.getBoost() != 1.0F)
/* 124 */         queryExpl.addDetail(boostExpl);
/* 125 */       queryExpl.addDetail(expl);
/*     */ 
/* 127 */       Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
/* 128 */       queryExpl.addDetail(queryNormExpl);
/*     */ 
/* 130 */       queryExpl.setValue(boostExpl.getValue() * expl.getValue() * queryNormExpl.getValue());
/*     */ 
/* 134 */       result.addDetail(queryExpl);
/*     */ 
/* 137 */       String field = TermQuery.this.term.field();
/* 138 */       ComplexExplanation fieldExpl = new ComplexExplanation();
/* 139 */       fieldExpl.setDescription("fieldWeight(" + TermQuery.this.term + " in " + doc + "), product of:");
/*     */ 
/* 142 */       Explanation tfExplanation = new Explanation();
/* 143 */       int tf = 0;
/* 144 */       TermDocs termDocs = reader.termDocs(TermQuery.this.term);
/* 145 */       if (termDocs != null) {
/*     */         try {
/* 147 */           if ((termDocs.skipTo(doc)) && (termDocs.doc() == doc))
/* 148 */             tf = termDocs.freq();
/*     */         }
/*     */         finally {
/* 151 */           termDocs.close();
/*     */         }
/* 153 */         tfExplanation.setValue(this.similarity.tf(tf));
/* 154 */         tfExplanation.setDescription("tf(termFreq(" + TermQuery.this.term + ")=" + tf + ")");
/*     */       } else {
/* 156 */         tfExplanation.setValue(0.0F);
/* 157 */         tfExplanation.setDescription("no matching term");
/*     */       }
/* 159 */       fieldExpl.addDetail(tfExplanation);
/* 160 */       fieldExpl.addDetail(expl);
/*     */ 
/* 162 */       Explanation fieldNormExpl = new Explanation();
/* 163 */       byte[] fieldNorms = reader.norms(field);
/* 164 */       float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0F;
/*     */ 
/* 166 */       fieldNormExpl.setValue(fieldNorm);
/* 167 */       fieldNormExpl.setDescription("fieldNorm(field=" + field + ", doc=" + doc + ")");
/* 168 */       fieldExpl.addDetail(fieldNormExpl);
/*     */ 
/* 170 */       fieldExpl.setMatch(Boolean.valueOf(tfExplanation.isMatch()));
/* 171 */       fieldExpl.setValue(tfExplanation.getValue() * expl.getValue() * fieldNormExpl.getValue());
/*     */ 
/* 175 */       result.addDetail(fieldExpl);
/* 176 */       result.setMatch(fieldExpl.getMatch());
/*     */ 
/* 179 */       result.setValue(queryExpl.getValue() * fieldExpl.getValue());
/*     */ 
/* 181 */       if (queryExpl.getValue() == 1.0F) {
/* 182 */         return fieldExpl;
/*     */       }
/* 184 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TermQuery
 * JD-Core Version:    0.6.0
 */