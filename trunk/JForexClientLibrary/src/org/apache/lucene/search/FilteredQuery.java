/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class FilteredQuery extends Query
/*     */ {
/*     */   Query query;
/*     */   Filter filter;
/*     */ 
/*     */   public FilteredQuery(Query query, Filter filter)
/*     */   {
/*  53 */     this.query = query;
/*  54 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/*  63 */     Weight weight = this.query.createWeight(searcher);
/*  64 */     Similarity similarity = this.query.getSimilarity(searcher);
/*  65 */     return new Weight(weight, similarity) {
/*     */       private float value;
/*     */ 
/*     */       public float getValue() {
/*  70 */         return this.value;
/*     */       }
/*     */ 
/*     */       public float sumOfSquaredWeights() throws IOException {
/*  74 */         return this.val$weight.sumOfSquaredWeights() * FilteredQuery.this.getBoost() * FilteredQuery.this.getBoost();
/*     */       }
/*     */ 
/*     */       public void normalize(float v)
/*     */       {
/*  79 */         this.val$weight.normalize(v);
/*  80 */         this.value = (this.val$weight.getValue() * FilteredQuery.this.getBoost());
/*     */       }
/*     */ 
/*     */       public Explanation explain(IndexReader ir, int i) throws IOException
/*     */       {
/*  85 */         Explanation inner = this.val$weight.explain(ir, i);
/*  86 */         if (FilteredQuery.this.getBoost() != 1.0F) {
/*  87 */           Explanation preBoost = inner;
/*  88 */           inner = new Explanation(inner.getValue() * FilteredQuery.this.getBoost(), "product of:");
/*  89 */           inner.addDetail(new Explanation(FilteredQuery.this.getBoost(), "boost"));
/*  90 */           inner.addDetail(preBoost);
/*     */         }
/*  92 */         Filter f = FilteredQuery.this.filter;
/*  93 */         DocIdSet docIdSet = f.getDocIdSet(ir);
/*  94 */         DocIdSetIterator docIdSetIterator = docIdSet == null ? DocIdSet.EMPTY_DOCIDSET.iterator() : docIdSet.iterator();
/*  95 */         if (docIdSetIterator == null) {
/*  96 */           docIdSetIterator = DocIdSet.EMPTY_DOCIDSET.iterator();
/*     */         }
/*  98 */         if (docIdSetIterator.advance(i) == i) {
/*  99 */           return inner;
/*     */         }
/* 101 */         Explanation result = new Explanation(0.0F, "failure to match filter: " + f.toString());
/*     */ 
/* 103 */         result.addDetail(inner);
/* 104 */         return result;
/*     */       }
/*     */ 
/*     */       public Query getQuery()
/*     */       {
/* 110 */         return FilteredQuery.this;
/*     */       }
/*     */ 
/*     */       public Scorer scorer(IndexReader indexReader, boolean scoreDocsInOrder, boolean topScorer)
/*     */         throws IOException
/*     */       {
/* 116 */         Scorer scorer = this.val$weight.scorer(indexReader, true, false);
/* 117 */         if (scorer == null) {
/* 118 */           return null;
/*     */         }
/* 120 */         DocIdSet docIdSet = FilteredQuery.this.filter.getDocIdSet(indexReader);
/* 121 */         if (docIdSet == null) {
/* 122 */           return null;
/*     */         }
/* 124 */         DocIdSetIterator docIdSetIterator = docIdSet.iterator();
/* 125 */         if (docIdSetIterator == null) {
/* 126 */           return null;
/*     */         }
/*     */ 
/* 129 */         return new Scorer(this.val$similarity, this, scorer, docIdSetIterator)
/*     */         {
/* 131 */           private int doc = -1;
/*     */ 
/*     */           private int advanceToCommon(int scorerDoc, int disiDoc) throws IOException {
/* 134 */             while (scorerDoc != disiDoc) {
/* 135 */               if (scorerDoc < disiDoc) {
/* 136 */                 scorerDoc = this.val$scorer.advance(disiDoc); continue;
/*     */               }
/* 138 */               disiDoc = this.val$docIdSetIterator.advance(scorerDoc);
/*     */             }
/*     */ 
/* 141 */             return scorerDoc;
/*     */           }
/*     */ 
/*     */           public int nextDoc()
/*     */             throws IOException
/*     */           {
/*     */             int disiDoc;
/*     */             int scorerDoc;
/* 147 */             return this.doc = ((disiDoc = this.val$docIdSetIterator.nextDoc()) != 2147483647) && ((scorerDoc = this.val$scorer.nextDoc()) != 2147483647) && (advanceToCommon(scorerDoc, disiDoc) != 2147483647) ? this.val$scorer.docID() : 2147483647;
/*     */           }
/*     */ 
/*     */           public int docID()
/*     */           {
/* 153 */             return this.doc;
/*     */           }
/*     */ 
/*     */           public int advance(int target)
/*     */             throws IOException
/*     */           {
/*     */             int disiDoc;
/*     */             int scorerDoc;
/* 158 */             return this.doc = ((disiDoc = this.val$docIdSetIterator.advance(target)) != 2147483647) && ((scorerDoc = this.val$scorer.advance(disiDoc)) != 2147483647) && (advanceToCommon(scorerDoc, disiDoc) != 2147483647) ? this.val$scorer.docID() : 2147483647;
/*     */           }
/*     */ 
/*     */           public float score()
/*     */             throws IOException
/*     */           {
/* 164 */             return FilteredQuery.this.getBoost() * this.val$scorer.score();
/*     */           } } ;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException {
/* 173 */     Query rewritten = this.query.rewrite(reader);
/* 174 */     if (rewritten != this.query) {
/* 175 */       FilteredQuery clone = (FilteredQuery)clone();
/* 176 */       clone.query = rewritten;
/* 177 */       return clone;
/*     */     }
/* 179 */     return this;
/*     */   }
/*     */ 
/*     */   public Query getQuery()
/*     */   {
/* 184 */     return this.query;
/*     */   }
/*     */ 
/*     */   public Filter getFilter() {
/* 188 */     return this.filter;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 194 */     getQuery().extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public String toString(String s)
/*     */   {
/* 200 */     StringBuilder buffer = new StringBuilder();
/* 201 */     buffer.append("filtered(");
/* 202 */     buffer.append(this.query.toString(s));
/* 203 */     buffer.append(")->");
/* 204 */     buffer.append(this.filter);
/* 205 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 206 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 212 */     if ((o instanceof FilteredQuery)) {
/* 213 */       FilteredQuery fq = (FilteredQuery)o;
/* 214 */       return (this.query.equals(fq.query)) && (this.filter.equals(fq.filter)) && (getBoost() == fq.getBoost());
/*     */     }
/* 216 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 222 */     return this.query.hashCode() ^ this.filter.hashCode() + Float.floatToRawIntBits(getBoost());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FilteredQuery
 * JD-Core Version:    0.6.0
 */