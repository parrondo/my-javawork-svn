/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ public class DisjunctionMaxQuery extends Query
/*     */   implements Iterable<Query>
/*     */ {
/*  45 */   private ArrayList<Query> disjuncts = new ArrayList();
/*     */ 
/*  48 */   private float tieBreakerMultiplier = 0.0F;
/*     */ 
/*     */   public DisjunctionMaxQuery(float tieBreakerMultiplier)
/*     */   {
/*  57 */     this.tieBreakerMultiplier = tieBreakerMultiplier;
/*     */   }
/*     */ 
/*     */   public DisjunctionMaxQuery(Collection<Query> disjuncts, float tieBreakerMultiplier)
/*     */   {
/*  66 */     this.tieBreakerMultiplier = tieBreakerMultiplier;
/*  67 */     add(disjuncts);
/*     */   }
/*     */ 
/*     */   public void add(Query query)
/*     */   {
/*  74 */     this.disjuncts.add(query);
/*     */   }
/*     */ 
/*     */   public void add(Collection<Query> disjuncts)
/*     */   {
/*  81 */     this.disjuncts.addAll(disjuncts);
/*     */   }
/*     */ 
/*     */   public Iterator<Query> iterator()
/*     */   {
/*  86 */     return this.disjuncts.iterator();
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 184 */     return new DisjunctionMaxWeight(searcher);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 192 */     int numDisjunctions = this.disjuncts.size();
/* 193 */     if (numDisjunctions == 1) {
/* 194 */       Query singleton = (Query)this.disjuncts.get(0);
/* 195 */       Query result = singleton.rewrite(reader);
/* 196 */       if (getBoost() != 1.0F) {
/* 197 */         if (result == singleton) result = (Query)result.clone();
/* 198 */         result.setBoost(getBoost() * result.getBoost());
/*     */       }
/* 200 */       return result;
/*     */     }
/* 202 */     DisjunctionMaxQuery clone = null;
/* 203 */     for (int i = 0; i < numDisjunctions; i++) {
/* 204 */       Query clause = (Query)this.disjuncts.get(i);
/* 205 */       Query rewrite = clause.rewrite(reader);
/* 206 */       if (rewrite != clause) {
/* 207 */         if (clone == null) clone = (DisjunctionMaxQuery)clone();
/* 208 */         clone.disjuncts.set(i, rewrite);
/*     */       }
/*     */     }
/* 211 */     if (clone != null) return clone;
/* 212 */     return this;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 219 */     DisjunctionMaxQuery clone = (DisjunctionMaxQuery)super.clone();
/* 220 */     clone.disjuncts = ((ArrayList)this.disjuncts.clone());
/* 221 */     return clone;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 227 */     for (Query query : this.disjuncts)
/* 228 */       query.extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 238 */     StringBuilder buffer = new StringBuilder();
/* 239 */     buffer.append("(");
/* 240 */     int numDisjunctions = this.disjuncts.size();
/* 241 */     for (int i = 0; i < numDisjunctions; i++) {
/* 242 */       Query subquery = (Query)this.disjuncts.get(i);
/* 243 */       if ((subquery instanceof BooleanQuery)) {
/* 244 */         buffer.append("(");
/* 245 */         buffer.append(subquery.toString(field));
/* 246 */         buffer.append(")");
/*     */       } else {
/* 248 */         buffer.append(subquery.toString(field));
/* 249 */       }if (i == numDisjunctions - 1) continue; buffer.append(" | ");
/*     */     }
/* 251 */     buffer.append(")");
/* 252 */     if (this.tieBreakerMultiplier != 0.0F) {
/* 253 */       buffer.append("~");
/* 254 */       buffer.append(this.tieBreakerMultiplier);
/*     */     }
/* 256 */     if (getBoost() != 1.0D) {
/* 257 */       buffer.append("^");
/* 258 */       buffer.append(getBoost());
/*     */     }
/* 260 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 269 */     if (!(o instanceof DisjunctionMaxQuery)) return false;
/* 270 */     DisjunctionMaxQuery other = (DisjunctionMaxQuery)o;
/* 271 */     return (getBoost() == other.getBoost()) && (this.tieBreakerMultiplier == other.tieBreakerMultiplier) && (this.disjuncts.equals(other.disjuncts));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 281 */     return Float.floatToIntBits(getBoost()) + Float.floatToIntBits(this.tieBreakerMultiplier) + this.disjuncts.hashCode();
/*     */   }
/*     */ 
/*     */   protected class DisjunctionMaxWeight extends Weight
/*     */   {
/*     */     protected Similarity similarity;
/* 101 */     protected ArrayList<Weight> weights = new ArrayList();
/*     */ 
/*     */     public DisjunctionMaxWeight(Searcher searcher) throws IOException
/*     */     {
/* 105 */       this.similarity = searcher.getSimilarity();
/* 106 */       for (Query disjunctQuery : DisjunctionMaxQuery.this.disjuncts)
/* 107 */         this.weights.add(disjunctQuery.createWeight(searcher));
/*     */     }
/*     */ 
/*     */     public Query getQuery()
/*     */     {
/* 113 */       return DisjunctionMaxQuery.this;
/*     */     }
/*     */ 
/*     */     public float getValue() {
/* 117 */       return DisjunctionMaxQuery.this.getBoost();
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights() throws IOException
/*     */     {
/* 122 */       float max = 0.0F; float sum = 0.0F;
/* 123 */       for (Weight currentWeight : this.weights) {
/* 124 */         float sub = currentWeight.sumOfSquaredWeights();
/* 125 */         sum += sub;
/* 126 */         max = Math.max(max, sub);
/*     */       }
/*     */ 
/* 129 */       float boost = DisjunctionMaxQuery.this.getBoost();
/* 130 */       return ((sum - max) * DisjunctionMaxQuery.this.tieBreakerMultiplier * DisjunctionMaxQuery.this.tieBreakerMultiplier + max) * boost * boost;
/*     */     }
/*     */ 
/*     */     public void normalize(float norm)
/*     */     {
/* 136 */       norm *= DisjunctionMaxQuery.this.getBoost();
/* 137 */       for (Weight wt : this.weights)
/* 138 */         wt.normalize(norm);
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer)
/*     */       throws IOException
/*     */     {
/* 146 */       Scorer[] scorers = new Scorer[this.weights.size()];
/* 147 */       int idx = 0;
/* 148 */       for (Weight w : this.weights) {
/* 149 */         Scorer subScorer = w.scorer(reader, true, false);
/* 150 */         if ((subScorer != null) && (subScorer.nextDoc() != 2147483647)) {
/* 151 */           scorers[(idx++)] = subScorer;
/*     */         }
/*     */       }
/* 154 */       if (idx == 0) return null;
/* 155 */       DisjunctionMaxScorer result = new DisjunctionMaxScorer(this, DisjunctionMaxQuery.this.tieBreakerMultiplier, this.similarity, scorers, idx);
/* 156 */       return result;
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc)
/*     */       throws IOException
/*     */     {
/* 162 */       if (DisjunctionMaxQuery.this.disjuncts.size() == 1) return ((Weight)this.weights.get(0)).explain(reader, doc);
/* 163 */       ComplexExplanation result = new ComplexExplanation();
/* 164 */       float max = 0.0F; float sum = 0.0F;
/* 165 */       result.setDescription("max plus " + DisjunctionMaxQuery.this.tieBreakerMultiplier + " times others of:");
/* 166 */       for (Weight wt : this.weights) {
/* 167 */         Explanation e = wt.explain(reader, doc);
/* 168 */         if (e.isMatch()) {
/* 169 */           result.setMatch(Boolean.TRUE);
/* 170 */           result.addDetail(e);
/* 171 */           sum += e.getValue();
/* 172 */           max = Math.max(max, e.getValue());
/*     */         }
/*     */       }
/* 175 */       result.setValue(max + (sum - max) * DisjunctionMaxQuery.this.tieBreakerMultiplier);
/* 176 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.DisjunctionMaxQuery
 * JD-Core Version:    0.6.0
 */