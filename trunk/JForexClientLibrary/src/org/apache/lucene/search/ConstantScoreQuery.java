/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class ConstantScoreQuery extends Query
/*     */ {
/*     */   protected final Filter filter;
/*     */   protected final Query query;
/*     */ 
/*     */   public ConstantScoreQuery(Query query)
/*     */   {
/*  45 */     if (query == null)
/*  46 */       throw new NullPointerException("Query may not be null");
/*  47 */     this.filter = null;
/*  48 */     this.query = query;
/*     */   }
/*     */ 
/*     */   public ConstantScoreQuery(Filter filter)
/*     */   {
/*  58 */     if (filter == null)
/*  59 */       throw new NullPointerException("Filter may not be null");
/*  60 */     this.filter = filter;
/*  61 */     this.query = null;
/*     */   }
/*     */ 
/*     */   public Filter getFilter()
/*     */   {
/*  66 */     return this.filter;
/*     */   }
/*     */ 
/*     */   public Query getQuery()
/*     */   {
/*  71 */     return this.query;
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException
/*     */   {
/*  76 */     if (this.query != null) {
/*  77 */       Query rewritten = this.query.rewrite(reader);
/*  78 */       if (rewritten != this.query) {
/*  79 */         rewritten = new ConstantScoreQuery(rewritten);
/*  80 */         rewritten.setBoost(getBoost());
/*  81 */         return rewritten;
/*     */       }
/*     */     }
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/*  93 */     if (this.query != null)
/*  94 */       this.query.extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 260 */     return new ConstantWeight(searcher);
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 265 */     return "ConstantScore(" + (this.query == null ? this.filter.toString() : this.query.toString(field)) + ')' + ToStringUtils.boost(getBoost());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 274 */     if (this == o) return true;
/* 275 */     if (!super.equals(o))
/* 276 */       return false;
/* 277 */     if ((o instanceof ConstantScoreQuery)) {
/* 278 */       ConstantScoreQuery other = (ConstantScoreQuery)o;
/* 279 */       return (this.filter == null ? other.filter == null : this.filter.equals(other.filter)) && (this.query == null ? other.query == null : this.query.equals(other.query));
/*     */     }
/*     */ 
/* 283 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 288 */     return 31 * super.hashCode() + (this.query == null ? this.filter : this.query).hashCode();
/*     */   }
/*     */ 
/*     */   protected class ConstantScorer extends Scorer
/*     */   {
/*     */     final DocIdSetIterator docIdSetIterator;
/*     */     final float theScore;
/*     */ 
/*     */     public ConstantScorer(Similarity similarity, DocIdSetIterator docIdSetIterator, Weight w)
/*     */       throws IOException
/*     */     {
/* 184 */       super(w);
/* 185 */       this.theScore = w.getValue();
/* 186 */       this.docIdSetIterator = docIdSetIterator;
/*     */     }
/*     */ 
/*     */     public int nextDoc() throws IOException
/*     */     {
/* 191 */       return this.docIdSetIterator.nextDoc();
/*     */     }
/*     */ 
/*     */     public int docID()
/*     */     {
/* 196 */       return this.docIdSetIterator.docID();
/*     */     }
/*     */ 
/*     */     public float score() throws IOException
/*     */     {
/* 201 */       return this.theScore;
/*     */     }
/*     */ 
/*     */     public int advance(int target) throws IOException
/*     */     {
/* 206 */       return this.docIdSetIterator.advance(target);
/*     */     }
/*     */ 
/*     */     private Collector wrapCollector(Collector collector) {
/* 210 */       return new Collector(collector)
/*     */       {
/*     */         public void setScorer(Scorer scorer) throws IOException
/*     */         {
/* 214 */           this.val$collector.setScorer(new ConstantScoreQuery.ConstantScorer(ConstantScoreQuery.this, ConstantScoreQuery.ConstantScorer.this.getSimilarity(), scorer, ConstantScoreQuery.ConstantScorer.this.weight));
/*     */         }
/*     */ 
/*     */         public void collect(int doc)
/*     */           throws IOException
/*     */         {
/* 220 */           this.val$collector.collect(doc);
/*     */         }
/*     */ 
/*     */         public void setNextReader(IndexReader reader, int docBase) throws IOException
/*     */         {
/* 225 */           this.val$collector.setNextReader(reader, docBase);
/*     */         }
/*     */ 
/*     */         public boolean acceptsDocsOutOfOrder()
/*     */         {
/* 230 */           return this.val$collector.acceptsDocsOutOfOrder();
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public void score(Collector collector) throws IOException
/*     */     {
/* 238 */       if ((this.docIdSetIterator instanceof Scorer))
/* 239 */         ((Scorer)this.docIdSetIterator).score(wrapCollector(collector));
/*     */       else
/* 241 */         super.score(collector);
/*     */     }
/*     */ 
/*     */     protected boolean score(Collector collector, int max, int firstDocID)
/*     */       throws IOException
/*     */     {
/* 250 */       if ((this.docIdSetIterator instanceof Scorer)) {
/* 251 */         return ((Scorer)this.docIdSetIterator).score(wrapCollector(collector), max, firstDocID);
/*     */       }
/* 253 */       return super.score(collector, max, firstDocID);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class ConstantWeight extends Weight
/*     */   {
/*     */     private final Weight innerWeight;
/*     */     private final Similarity similarity;
/*     */     private float queryNorm;
/*     */     private float queryWeight;
/*     */ 
/*     */     public ConstantWeight(Searcher searcher)
/*     */       throws IOException
/*     */     {
/* 104 */       this.similarity = ConstantScoreQuery.this.getSimilarity(searcher);
/* 105 */       this.innerWeight = (ConstantScoreQuery.this.query == null ? null : ConstantScoreQuery.this.query.createWeight(searcher));
/*     */     }
/*     */ 
/*     */     public Query getQuery()
/*     */     {
/* 110 */       return ConstantScoreQuery.this;
/*     */     }
/*     */ 
/*     */     public float getValue()
/*     */     {
/* 115 */       return this.queryWeight;
/*     */     }
/*     */ 
/*     */     public float sumOfSquaredWeights()
/*     */       throws IOException
/*     */     {
/* 121 */       if (this.innerWeight != null) this.innerWeight.sumOfSquaredWeights();
/* 122 */       this.queryWeight = ConstantScoreQuery.this.getBoost();
/* 123 */       return this.queryWeight * this.queryWeight;
/*     */     }
/*     */ 
/*     */     public void normalize(float norm)
/*     */     {
/* 128 */       this.queryNorm = norm;
/* 129 */       this.queryWeight *= this.queryNorm;
/*     */ 
/* 131 */       if (this.innerWeight != null) this.innerWeight.normalize(norm);
/*     */     }
/*     */ 
/*     */     public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer)
/*     */       throws IOException
/*     */     {
/*     */       DocIdSetIterator disi;
/*     */       DocIdSetIterator disi;
/* 137 */       if (ConstantScoreQuery.this.filter != null) {
/* 138 */         assert (ConstantScoreQuery.this.query == null);
/* 139 */         DocIdSet dis = ConstantScoreQuery.this.filter.getDocIdSet(reader);
/* 140 */         if (dis == null)
/* 141 */           return null;
/* 142 */         disi = dis.iterator();
/*     */       } else {
/* 144 */         assert ((ConstantScoreQuery.this.query != null) && (this.innerWeight != null));
/* 145 */         disi = this.innerWeight.scorer(reader, scoreDocsInOrder, topScorer);
/*     */       }
/*     */ 
/* 148 */       if (disi == null)
/* 149 */         return null;
/* 150 */       return new ConstantScoreQuery.ConstantScorer(ConstantScoreQuery.this, this.similarity, disi, this);
/*     */     }
/*     */ 
/*     */     public boolean scoresDocsOutOfOrder()
/*     */     {
/* 155 */       return this.innerWeight != null ? this.innerWeight.scoresDocsOutOfOrder() : false;
/*     */     }
/*     */ 
/*     */     public Explanation explain(IndexReader reader, int doc) throws IOException
/*     */     {
/* 160 */       Scorer cs = scorer(reader, true, false);
/* 161 */       boolean exists = (cs != null) && (cs.advance(doc) == doc);
/*     */ 
/* 163 */       ComplexExplanation result = new ComplexExplanation();
/* 164 */       if (exists) {
/* 165 */         result.setDescription(ConstantScoreQuery.this.toString() + ", product of:");
/* 166 */         result.setValue(this.queryWeight);
/* 167 */         result.setMatch(Boolean.TRUE);
/* 168 */         result.addDetail(new Explanation(ConstantScoreQuery.this.getBoost(), "boost"));
/* 169 */         result.addDetail(new Explanation(this.queryNorm, "queryNorm"));
/*     */       } else {
/* 171 */         result.setDescription(ConstantScoreQuery.this.toString() + " doesn't match id " + doc);
/* 172 */         result.setValue(0.0F);
/* 173 */         result.setMatch(Boolean.FALSE);
/*     */       }
/* 175 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ConstantScoreQuery
 * JD-Core Version:    0.6.0
 */