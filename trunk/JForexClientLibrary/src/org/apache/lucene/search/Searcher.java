/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.index.CorruptIndexException;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class Searcher
/*     */   implements Searchable
/*     */ {
/* 141 */   private Similarity similarity = Similarity.getDefault();
/*     */ 
/*     */   public TopFieldDocs search(Query query, Filter filter, int n, Sort sort)
/*     */     throws IOException
/*     */   {
/*  53 */     return search(createNormalizedWeight(query), filter, n, sort);
/*     */   }
/*     */ 
/*     */   public TopFieldDocs search(Query query, int n, Sort sort)
/*     */     throws IOException
/*     */   {
/*  66 */     return search(createNormalizedWeight(query), null, n, sort);
/*     */   }
/*     */ 
/*     */   public void search(Query query, Collector results)
/*     */     throws IOException
/*     */   {
/*  84 */     search(createNormalizedWeight(query), null, results);
/*     */   }
/*     */ 
/*     */   public void search(Query query, Filter filter, Collector results)
/*     */     throws IOException
/*     */   {
/* 105 */     search(createNormalizedWeight(query), filter, results);
/*     */   }
/*     */ 
/*     */   public TopDocs search(Query query, Filter filter, int n)
/*     */     throws IOException
/*     */   {
/* 115 */     return search(createNormalizedWeight(query), filter, n);
/*     */   }
/*     */ 
/*     */   public TopDocs search(Query query, int n)
/*     */     throws IOException
/*     */   {
/* 125 */     return search(query, null, n);
/*     */   }
/*     */ 
/*     */   public Explanation explain(Query query, int doc)
/*     */     throws IOException
/*     */   {
/* 137 */     return explain(createNormalizedWeight(query), doc);
/*     */   }
/*     */ 
/*     */   public void setSimilarity(Similarity similarity)
/*     */   {
/* 148 */     this.similarity = similarity;
/*     */   }
/*     */ 
/*     */   public Similarity getSimilarity()
/*     */   {
/* 156 */     return this.similarity;
/*     */   }
/*     */ 
/*     */   public Weight createNormalizedWeight(Query query)
/*     */     throws IOException
/*     */   {
/* 167 */     query = rewrite(query);
/* 168 */     Weight weight = query.createWeight(this);
/* 169 */     float sum = weight.sumOfSquaredWeights();
/*     */ 
/* 171 */     float norm = query.getSimilarity(this).queryNorm(sum);
/* 172 */     if ((Float.isInfinite(norm)) || (Float.isNaN(norm)))
/* 173 */       norm = 1.0F;
/* 174 */     weight.normalize(norm);
/* 175 */     return weight;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected final Weight createWeight(Query query)
/*     */     throws IOException
/*     */   {
/* 188 */     return createNormalizedWeight(query);
/*     */   }
/*     */ 
/*     */   public int[] docFreqs(Term[] terms) throws IOException
/*     */   {
/* 193 */     int[] result = new int[terms.length];
/* 194 */     for (int i = 0; i < terms.length; i++) {
/* 195 */       result[i] = docFreq(terms[i]);
/*     */     }
/* 197 */     return result;
/*     */   }
/*     */ 
/*     */   public abstract void search(Weight paramWeight, Filter paramFilter, Collector paramCollector)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract void close()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract int docFreq(Term paramTerm)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract int maxDoc()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract TopDocs search(Weight paramWeight, Filter paramFilter, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract Document doc(int paramInt)
/*     */     throws CorruptIndexException, IOException;
/*     */ 
/*     */   public abstract Document doc(int paramInt, FieldSelector paramFieldSelector)
/*     */     throws CorruptIndexException, IOException;
/*     */ 
/*     */   public abstract Query rewrite(Query paramQuery)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract Explanation explain(Weight paramWeight, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract TopFieldDocs search(Weight paramWeight, Filter paramFilter, int paramInt, Sort paramSort)
/*     */     throws IOException;
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Searcher
 * JD-Core Version:    0.6.0
 */