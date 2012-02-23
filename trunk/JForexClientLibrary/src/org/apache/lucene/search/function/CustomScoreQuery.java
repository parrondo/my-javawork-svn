/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.ComplexExplanation;
/*     */ import org.apache.lucene.search.Explanation;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.Scorer;
/*     */ import org.apache.lucene.search.Searcher;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.search.Weight;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class CustomScoreQuery extends Query
/*     */ {
/*     */   private Query subQuery;
/*     */   private ValueSourceQuery[] valSrcQueries;
/*  51 */   private boolean strict = false;
/*     */ 
/*     */   public CustomScoreQuery(Query subQuery)
/*     */   {
/*  58 */     this(subQuery, new ValueSourceQuery[0]);
/*     */   }
/*     */ 
/*     */   public CustomScoreQuery(Query subQuery, ValueSourceQuery valSrcQuery)
/*     */   {
/*  70 */     this(subQuery, valSrcQuery != null ? new ValueSourceQuery[] { valSrcQuery } : new ValueSourceQuery[0]);
/*     */   }
/*     */ 
/*     */   public CustomScoreQuery(Query subQuery, ValueSourceQuery[] valSrcQueries)
/*     */   {
/*  83 */     this.subQuery = subQuery;
/*  84 */     this.valSrcQueries = (valSrcQueries != null ? valSrcQueries : new ValueSourceQuery[0]);
/*     */ 
/*  86 */     if (subQuery == null) throw new IllegalArgumentException("<subquery> must not be null!");
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  92 */     CustomScoreQuery clone = null;
/*     */ 
/*  94 */     Query sq = this.subQuery.rewrite(reader);
/*  95 */     if (sq != this.subQuery) {
/*  96 */       clone = (CustomScoreQuery)clone();
/*  97 */       clone.subQuery = sq;
/*     */     }
/*     */ 
/* 100 */     for (int i = 0; i < this.valSrcQueries.length; i++) {
/* 101 */       ValueSourceQuery v = (ValueSourceQuery)this.valSrcQueries[i].rewrite(reader);
/* 102 */       if (v != this.valSrcQueries[i]) {
/* 103 */         if (clone == null) clone = (CustomScoreQuery)clone();
/* 104 */         clone.valSrcQueries[i] = v;
/*     */       }
/*     */     }
/*     */ 
/* 108 */     return clone == null ? this : clone;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 114 */     this.subQuery.extractTerms(terms);
/* 115 */     for (int i = 0; i < this.valSrcQueries.length; i++)
/* 116 */       this.valSrcQueries[i].extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 123 */     CustomScoreQuery clone = (CustomScoreQuery)super.clone();
/* 124 */     clone.subQuery = ((Query)this.subQuery.clone());
/* 125 */     clone.valSrcQueries = new ValueSourceQuery[this.valSrcQueries.length];
/* 126 */     for (int i = 0; i < this.valSrcQueries.length; i++) {
/* 127 */       clone.valSrcQueries[i] = ((ValueSourceQuery)this.valSrcQueries[i].clone());
/*     */     }
/* 129 */     return clone;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 135 */     StringBuilder sb = new StringBuilder(name()).append("(");
/* 136 */     sb.append(this.subQuery.toString(field));
/* 137 */     for (int i = 0; i < this.valSrcQueries.length; i++) {
/* 138 */       sb.append(", ").append(this.valSrcQueries[i].toString(field));
/*     */     }
/* 140 */     sb.append(")");
/* 141 */     sb.append(this.strict ? " STRICT" : "");
/* 142 */     return sb.toString() + ToStringUtils.boost(getBoost());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 148 */     if (this == o)
/* 149 */       return true;
/* 150 */     if (!super.equals(o))
/* 151 */       return false;
/* 152 */     if (getClass() != o.getClass()) {
/* 153 */       return false;
/*     */     }
/* 155 */     CustomScoreQuery other = (CustomScoreQuery)o;
/* 156 */     if ((getBoost() != other.getBoost()) || (!this.subQuery.equals(other.subQuery)) || (this.strict != other.strict) || (this.valSrcQueries.length != other.valSrcQueries.length))
/*     */     {
/* 160 */       return false;
/*     */     }
/* 162 */     return Arrays.equals(this.valSrcQueries, other.valSrcQueries);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 168 */     return getClass().hashCode() + this.subQuery.hashCode() + Arrays.hashCode(this.valSrcQueries) ^ Float.floatToIntBits(getBoost()) ^ (this.strict ? 1234 : 4321);
/*     */   }
/*     */ 
/*     */   protected CustomScoreProvider getCustomScoreProvider(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 179 */     return new CustomScoreProvider(reader);
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 354 */     return new CustomWeight(searcher);
/*     */   }
/*     */ 
/*     */   public boolean isStrict()
/*     */   {
/* 367 */     return this.strict;
/*     */   }
/*     */ 
/*     */   public void setStrict(boolean strict)
/*     */   {
/* 376 */     this.strict = strict;
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/* 383 */     return "custom";
/*     */   }
/*     */ 
/*     */   private class CustomScorer extends Scorer
/*     */   {
/*     */     private final float qWeight;
/*     */     private Scorer subQueryScorer;
/*     */     private Scorer[] valSrcScorers;
/*     */     private final CustomScoreProvider provider;
/*     */     private float[] vScores;
/*     */ 
/*     */     private CustomScorer(Similarity similarity, IndexReader reader, CustomScoreQuery.CustomWeight w, Scorer subQueryScorer, Scorer[] valSrcScorers)
/*     */       throws IOException
/*     */     {
/* 307 */       super(w);
/* 308 */       this.qWeight = w.getValue();
/* 309 */       this.subQueryScorer = subQueryScorer;
/* 310 */       this.valSrcScorers = valSrcScorers;
/* 311 */       this.vScores = new float[valSrcScorers.length];
/* 312 */       this.provider = CustomScoreQuery.this.getCustomScoreProvider(reader);
/*     */     }
/*     */ 
/*     */     public int nextDoc() throws IOException
/*     */     {
/* 317 */       int doc = this.subQueryScorer.nextDoc();
/* 318 */       if (doc != 2147483647) {
/* 319 */         for (int i = 0; i < this.valSrcScorers.length; i++) {
/* 320 */           this.valSrcScorers[i].advance(doc);
/*     */         }
/*     */       }
/* 323 */       return doc;
/*     */     }
/*     */ 
/*     */     public int docID()
/*     */     {
/* 328 */       return this.subQueryScorer.docID();
/*     */     }
/*     */ 
/*     */     public float score()
/*     */       throws IOException
/*     */     {
/* 334 */       for (int i = 0; i < this.valSrcScorers.length; i++) {
/* 335 */         this.vScores[i] = this.valSrcScorers[i].score();
/*     */       }
/* 337 */       return this.qWeight * this.provider.customScore(this.subQueryScorer.docID(), this.subQueryScorer.score(), this.vScores);
/*     */     }
/*     */ 
/*     */     public int advance(int target) throws IOException
/*     */     {
/* 342 */       int doc = this.subQueryScorer.advance(target);
/* 343 */       if (doc != 2147483647) {
/* 344 */         for (int i = 0; i < this.valSrcScorers.length; i++) {
/* 345 */           this.valSrcScorers[i].advance(doc);
/*     */         }
/*     */       }
/* 348 */       return doc;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CustomWeight extends Weight
/*     */   {
/*     */     Similarity similarity;
/*     */     Weight subQueryWeight;
/*     */     Weight[] valSrcWeights;
/*     */     boolean qStrict;
/*     */ 
/*     */     public CustomWeight(Searcher searcher)
/*     */       throws IOException
/*     */     {
/* 191 */       this.similarity = CustomScoreQuery.this.getSimilarity(searcher);
/* 192 */       this.subQueryWeight = CustomScoreQuery.this.subQuery.createWeight(searcher);
/* 193 */       this.valSrcWeights = new Weight[CustomScoreQuery.this.valSrcQueries.length];
/* 194 */       for (int i = 0; i < CustomScoreQuery.this.valSrcQueries.length; i++) {
/* 195 */         this.valSrcWeights[i] = CustomScoreQuery.access$100(CustomScoreQuery.this)[i].createWeight(searcher);
/*     */       }
/* 197 */       this.qStrict = CustomScoreQuery.this.strict;
/*     */     }
/*     */ 
/*     */     public Query getQuery()
/*     */     {
/* 203 */       return CustomScoreQuery.this;
/*     */     }
/*     */ 
/*     */     public float getValue()
/*     */     {
/* 209 */       return CustomScoreQuery.this.getBoost();
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights()
/*     */       throws IOException
/*     */     {
/* 215 */       float sum = this.subQueryWeight.sumOfSquaredWeights();
/* 216 */       for (int i = 0; i < this.valSrcWeights.length; i++) {
/* 217 */         if (this.qStrict)
/* 218 */           this.valSrcWeights[i].sumOfSquaredWeights();
/*     */         else {
/* 220 */           sum += this.valSrcWeights[i].sumOfSquaredWeights();
/*     */         }
/*     */       }
/* 223 */       sum *= CustomScoreQuery.this.getBoost() * CustomScoreQuery.this.getBoost();
/* 224 */       return sum;
/*     */     }
/*     */ 
/*     */     public void normalize(float norm)
/*     */     {
/* 230 */       norm *= CustomScoreQuery.this.getBoost();
/* 231 */       this.subQueryWeight.normalize(norm);
/* 232 */       for (int i = 0; i < this.valSrcWeights.length; i++)
/* 233 */         if (this.qStrict)
/* 234 */           this.valSrcWeights[i].normalize(1.0F);
/*     */         else
/* 236 */           this.valSrcWeights[i].normalize(norm);
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer)
/*     */       throws IOException
/*     */     {
/* 248 */       Scorer subQueryScorer = this.subQueryWeight.scorer(reader, true, false);
/* 249 */       if (subQueryScorer == null) {
/* 250 */         return null;
/*     */       }
/* 252 */       Scorer[] valSrcScorers = new Scorer[this.valSrcWeights.length];
/* 253 */       for (int i = 0; i < valSrcScorers.length; i++) {
/* 254 */         valSrcScorers[i] = this.valSrcWeights[i].scorer(reader, true, topScorer);
/*     */       }
/* 256 */       return new CustomScoreQuery.CustomScorer(CustomScoreQuery.this, this.similarity, reader, this, subQueryScorer, valSrcScorers, null);
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc) throws IOException
/*     */     {
/* 261 */       Explanation explain = doExplain(reader, doc);
/* 262 */       return explain == null ? new Explanation(0.0F, "no matching docs") : explain;
/*     */     }
/*     */ 
/*     */     private Explanation doExplain(IndexReader reader, int doc) throws IOException {
/* 266 */       Explanation subQueryExpl = this.subQueryWeight.explain(reader, doc);
/* 267 */       if (!subQueryExpl.isMatch()) {
/* 268 */         return subQueryExpl;
/*     */       }
/*     */ 
/* 271 */       Explanation[] valSrcExpls = new Explanation[this.valSrcWeights.length];
/* 272 */       for (int i = 0; i < this.valSrcWeights.length; i++) {
/* 273 */         valSrcExpls[i] = this.valSrcWeights[i].explain(reader, doc);
/*     */       }
/* 275 */       Explanation customExp = CustomScoreQuery.this.getCustomScoreProvider(reader).customExplain(doc, subQueryExpl, valSrcExpls);
/* 276 */       float sc = getValue() * customExp.getValue();
/* 277 */       Explanation res = new ComplexExplanation(true, sc, CustomScoreQuery.this.toString() + ", product of:");
/*     */ 
/* 279 */       res.addDetail(customExp);
/* 280 */       res.addDetail(new Explanation(getValue(), "queryBoost"));
/* 281 */       return res;
/*     */     }
/*     */ 
/*     */     public boolean scoresDocsOutOfOrder()
/*     */     {
/* 286 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.CustomScoreQuery
 * JD-Core Version:    0.6.0
 */