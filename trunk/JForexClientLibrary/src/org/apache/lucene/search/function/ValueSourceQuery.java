/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermDocs;
/*     */ import org.apache.lucene.search.ComplexExplanation;
/*     */ import org.apache.lucene.search.Explanation;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.Scorer;
/*     */ import org.apache.lucene.search.Searcher;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.search.Weight;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class ValueSourceQuery extends Query
/*     */ {
/*     */   ValueSource valSrc;
/*     */ 
/*     */   public ValueSourceQuery(ValueSource valSrc)
/*     */   {
/*  50 */     this.valSrc = valSrc;
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  56 */     return this;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */   {
/* 166 */     return new ValueSourceWeight(searcher);
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 171 */     return this.valSrc.toString() + ToStringUtils.boost(getBoost());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 177 */     if (this == o)
/* 178 */       return true;
/* 179 */     if (!super.equals(o))
/* 180 */       return false;
/* 181 */     if (getClass() != o.getClass()) {
/* 182 */       return false;
/*     */     }
/* 184 */     ValueSourceQuery other = (ValueSourceQuery)o;
/* 185 */     return (getBoost() == other.getBoost()) && (this.valSrc.equals(other.valSrc));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 192 */     return getClass().hashCode() + this.valSrc.hashCode() ^ Float.floatToIntBits(getBoost());
/*     */   }
/*     */ 
/*     */   private class ValueSourceScorer extends Scorer
/*     */   {
/*     */     private final float qWeight;
/*     */     private final DocValues vals;
/*     */     private final TermDocs termDocs;
/* 131 */     private int doc = -1;
/*     */ 
/*     */     private ValueSourceScorer(Similarity similarity, IndexReader reader, ValueSourceQuery.ValueSourceWeight w) throws IOException
/*     */     {
/* 135 */       super(w);
/* 136 */       this.qWeight = w.getValue();
/*     */ 
/* 138 */       this.vals = ValueSourceQuery.this.valSrc.getValues(reader);
/* 139 */       this.termDocs = reader.termDocs(null);
/*     */     }
/*     */ 
/*     */     public int nextDoc() throws IOException
/*     */     {
/* 144 */       return this.doc = this.termDocs.next() ? this.termDocs.doc() : 2147483647;
/*     */     }
/*     */ 
/*     */     public int docID()
/*     */     {
/* 149 */       return this.doc;
/*     */     }
/*     */ 
/*     */     public int advance(int target) throws IOException
/*     */     {
/* 154 */       return this.doc = this.termDocs.skipTo(target) ? this.termDocs.doc() : 2147483647;
/*     */     }
/*     */ 
/*     */     public float score()
/*     */       throws IOException
/*     */     {
/* 160 */       return this.qWeight * this.vals.floatVal(this.termDocs.doc());
/*     */     }
/*     */   }
/*     */ 
/*     */   class ValueSourceWeight extends Weight
/*     */   {
/*     */     Similarity similarity;
/*     */     float queryNorm;
/*     */     float queryWeight;
/*     */ 
/*     */     public ValueSourceWeight(Searcher searcher)
/*     */     {
/*  71 */       this.similarity = ValueSourceQuery.this.getSimilarity(searcher);
/*     */     }
/*     */ 
/*     */     public Query getQuery()
/*     */     {
/*  77 */       return ValueSourceQuery.this;
/*     */     }
/*     */ 
/*     */     public float getValue()
/*     */     {
/*  83 */       return this.queryWeight;
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights()
/*     */       throws IOException
/*     */     {
/*  89 */       this.queryWeight = ValueSourceQuery.this.getBoost();
/*  90 */       return this.queryWeight * this.queryWeight;
/*     */     }
/*     */ 
/*     */     public void normalize(float norm)
/*     */     {
/*  96 */       this.queryNorm = norm;
/*  97 */       this.queryWeight *= this.queryNorm;
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
/*     */     {
/* 102 */       return new ValueSourceQuery.ValueSourceScorer(ValueSourceQuery.this, this.similarity, reader, this, null);
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc)
/*     */       throws IOException
/*     */     {
/* 108 */       DocValues vals = ValueSourceQuery.this.valSrc.getValues(reader);
/* 109 */       float sc = this.queryWeight * vals.floatVal(doc);
/*     */ 
/* 111 */       Explanation result = new ComplexExplanation(true, sc, ValueSourceQuery.this.toString() + ", product of:");
/*     */ 
/* 114 */       result.addDetail(vals.explain(doc));
/* 115 */       result.addDetail(new Explanation(ValueSourceQuery.this.getBoost(), "boost"));
/* 116 */       result.addDetail(new Explanation(this.queryNorm, "queryNorm"));
/* 117 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.ValueSourceQuery
 * JD-Core Version:    0.6.0
 */