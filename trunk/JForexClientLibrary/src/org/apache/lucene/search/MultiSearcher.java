/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.index.CorruptIndexException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.DummyConcurrentLock;
/*     */ import org.apache.lucene.util.ReaderUtil;
/*     */ 
/*     */ @Deprecated
/*     */ public class MultiSearcher extends Searcher
/*     */ {
/*     */   private Searchable[] searchables;
/*     */   private int[] starts;
/* 137 */   private int maxDoc = 0;
/*     */ 
/*     */   public MultiSearcher(Searchable[] searchables) throws IOException
/*     */   {
/* 141 */     this.searchables = searchables;
/*     */ 
/* 143 */     this.starts = new int[searchables.length + 1];
/* 144 */     for (int i = 0; i < searchables.length; i++) {
/* 145 */       this.starts[i] = this.maxDoc;
/* 146 */       this.maxDoc += searchables[i].maxDoc();
/*     */     }
/* 148 */     this.starts[searchables.length] = this.maxDoc;
/*     */   }
/*     */ 
/*     */   public Searchable[] getSearchables()
/*     */   {
/* 153 */     return this.searchables;
/*     */   }
/*     */ 
/*     */   protected int[] getStarts() {
/* 157 */     return this.starts;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 163 */     for (int i = 0; i < this.searchables.length; i++)
/* 164 */       this.searchables[i].close();
/*     */   }
/*     */ 
/*     */   public int docFreq(Term term) throws IOException
/*     */   {
/* 169 */     int docFreq = 0;
/* 170 */     for (int i = 0; i < this.searchables.length; i++)
/* 171 */       docFreq += this.searchables[i].docFreq(term);
/* 172 */     return docFreq;
/*     */   }
/*     */ 
/*     */   public Document doc(int n)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 178 */     int i = subSearcher(n);
/* 179 */     return this.searchables[i].doc(n - this.starts[i]);
/*     */   }
/*     */ 
/*     */   public Document doc(int n, FieldSelector fieldSelector)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 185 */     int i = subSearcher(n);
/* 186 */     return this.searchables[i].doc(n - this.starts[i], fieldSelector);
/*     */   }
/*     */ 
/*     */   public int subSearcher(int n)
/*     */   {
/* 192 */     return ReaderUtil.subIndex(n, this.starts);
/*     */   }
/*     */ 
/*     */   public int subDoc(int n)
/*     */   {
/* 198 */     return n - this.starts[subSearcher(n)];
/*     */   }
/*     */ 
/*     */   public int maxDoc() throws IOException
/*     */   {
/* 203 */     return this.maxDoc;
/*     */   }
/*     */ 
/*     */   public TopDocs search(Weight weight, Filter filter, int nDocs)
/*     */     throws IOException
/*     */   {
/* 210 */     nDocs = Math.min(nDocs, maxDoc());
/* 211 */     HitQueue hq = new HitQueue(nDocs, false);
/* 212 */     int totalHits = 0;
/*     */ 
/* 214 */     for (int i = 0; i < this.searchables.length; i++) {
/* 215 */       TopDocs docs = new MultiSearcherCallableNoSort(DummyConcurrentLock.INSTANCE, this.searchables[i], weight, filter, nDocs, hq, i, this.starts).call();
/*     */ 
/* 217 */       totalHits += docs.totalHits;
/*     */     }
/*     */ 
/* 220 */     ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
/* 221 */     for (int i = hq.size() - 1; i >= 0; i--) {
/* 222 */       scoreDocs[i] = ((ScoreDoc)hq.pop());
/*     */     }
/* 224 */     float maxScore = totalHits == 0 ? (1.0F / -1.0F) : scoreDocs[0].score;
/*     */ 
/* 226 */     return new TopDocs(totalHits, scoreDocs, maxScore);
/*     */   }
/*     */ 
/*     */   public TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort) throws IOException
/*     */   {
/* 231 */     n = Math.min(n, maxDoc());
/* 232 */     FieldDocSortedHitQueue hq = new FieldDocSortedHitQueue(n);
/* 233 */     int totalHits = 0;
/*     */ 
/* 235 */     float maxScore = (1.0F / -1.0F);
/*     */ 
/* 237 */     for (int i = 0; i < this.searchables.length; i++) {
/* 238 */       TopFieldDocs docs = new MultiSearcherCallableWithSort(DummyConcurrentLock.INSTANCE, this.searchables[i], weight, filter, n, hq, sort, i, this.starts).call();
/*     */ 
/* 240 */       totalHits += docs.totalHits;
/* 241 */       maxScore = Math.max(maxScore, docs.getMaxScore());
/*     */     }
/*     */ 
/* 244 */     ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
/* 245 */     for (int i = hq.size() - 1; i >= 0; i--) {
/* 246 */       scoreDocs[i] = ((ScoreDoc)hq.pop());
/*     */     }
/* 248 */     return new TopFieldDocs(totalHits, scoreDocs, hq.getFields(), maxScore);
/*     */   }
/*     */ 
/*     */   public void search(Weight weight, Filter filter, Collector collector)
/*     */     throws IOException
/*     */   {
/* 255 */     for (int i = 0; i < this.searchables.length; i++)
/*     */     {
/* 257 */       int start = this.starts[i];
/*     */ 
/* 259 */       Collector hc = new Collector(collector, start)
/*     */       {
/*     */         public void setScorer(Scorer scorer) throws IOException {
/* 262 */           this.val$collector.setScorer(scorer);
/*     */         }
/*     */ 
/*     */         public void collect(int doc) throws IOException {
/* 266 */           this.val$collector.collect(doc);
/*     */         }
/*     */ 
/*     */         public void setNextReader(IndexReader reader, int docBase) throws IOException {
/* 270 */           this.val$collector.setNextReader(reader, this.val$start + docBase);
/*     */         }
/*     */ 
/*     */         public boolean acceptsDocsOutOfOrder() {
/* 274 */           return this.val$collector.acceptsDocsOutOfOrder();
/*     */         }
/*     */       };
/* 278 */       this.searchables[i].search(weight, filter, hc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Query rewrite(Query original) throws IOException
/*     */   {
/* 284 */     Query[] queries = new Query[this.searchables.length];
/* 285 */     for (int i = 0; i < this.searchables.length; i++) {
/* 286 */       queries[i] = this.searchables[i].rewrite(original);
/*     */     }
/* 288 */     return queries[0].combine(queries);
/*     */   }
/*     */ 
/*     */   public Explanation explain(Weight weight, int doc) throws IOException
/*     */   {
/* 293 */     int i = subSearcher(doc);
/* 294 */     return this.searchables[i].explain(weight, doc - this.starts[i]);
/*     */   }
/*     */ 
/*     */   public Weight createNormalizedWeight(Query original)
/*     */     throws IOException
/*     */   {
/* 315 */     Query rewrittenQuery = rewrite(original);
/*     */ 
/* 318 */     Set terms = new HashSet();
/* 319 */     rewrittenQuery.extractTerms(terms);
/*     */ 
/* 322 */     Map dfMap = createDocFrequencyMap(terms);
/*     */ 
/* 325 */     int numDocs = maxDoc();
/* 326 */     CachedDfSource cacheSim = new CachedDfSource(dfMap, numDocs, getSimilarity());
/*     */ 
/* 328 */     return cacheSim.createNormalizedWeight(rewrittenQuery);
/*     */   }
/*     */ 
/*     */   Map<Term, Integer> createDocFrequencyMap(Set<Term> terms)
/*     */     throws IOException
/*     */   {
/* 339 */     Term[] allTermsArray = (Term[])terms.toArray(new Term[terms.size()]);
/* 340 */     int[] aggregatedDfs = new int[allTermsArray.length];
/* 341 */     for (Searchable searchable : this.searchables) {
/* 342 */       int[] dfs = searchable.docFreqs(allTermsArray);
/* 343 */       for (int j = 0; j < aggregatedDfs.length; j++) {
/* 344 */         aggregatedDfs[j] += dfs[j];
/*     */       }
/*     */     }
/* 347 */     HashMap dfMap = new HashMap();
/* 348 */     for (int i = 0; i < allTermsArray.length; i++) {
/* 349 */       dfMap.put(allTermsArray[i], Integer.valueOf(aggregatedDfs[i]));
/*     */     }
/* 351 */     return dfMap;
/*     */   }
/*     */ 
/*     */   static final class MultiSearcherCallableWithSort
/*     */     implements Callable<TopFieldDocs>
/*     */   {
/*     */     private final Lock lock;
/*     */     private final Searchable searchable;
/*     */     private final Weight weight;
/*     */     private final Filter filter;
/*     */     private final int nDocs;
/*     */     private final int i;
/*     */     private final FieldDocSortedHitQueue hq;
/*     */     private final int[] starts;
/*     */     private final Sort sort;
/*     */ 
/*     */     public MultiSearcherCallableWithSort(Lock lock, Searchable searchable, Weight weight, Filter filter, int nDocs, FieldDocSortedHitQueue hq, Sort sort, int i, int[] starts)
/*     */     {
/* 416 */       this.lock = lock;
/* 417 */       this.searchable = searchable;
/* 418 */       this.weight = weight;
/* 419 */       this.filter = filter;
/* 420 */       this.nDocs = nDocs;
/* 421 */       this.hq = hq;
/* 422 */       this.i = i;
/* 423 */       this.starts = starts;
/* 424 */       this.sort = sort;
/*     */     }
/*     */ 
/*     */     public TopFieldDocs call() throws IOException {
/* 428 */       TopFieldDocs docs = this.searchable.search(this.weight, this.filter, this.nDocs, this.sort);
/*     */ 
/* 432 */       for (int j = 0; j < docs.fields.length; j++) {
/* 433 */         if (docs.fields[j].getType() != 1)
/*     */           continue;
/* 435 */         for (int j2 = 0; j2 < docs.scoreDocs.length; j2++) {
/* 436 */           FieldDoc fd = (FieldDoc)docs.scoreDocs[j2];
/* 437 */           fd.fields[j] = Integer.valueOf(((Integer)fd.fields[j]).intValue() + this.starts[this.i]);
/*     */         }
/* 439 */         break;
/*     */       }
/*     */ 
/* 443 */       this.lock.lock();
/*     */       try {
/* 445 */         this.hq.setFields(docs.fields);
/*     */       } finally {
/* 447 */         this.lock.unlock();
/*     */       }
/*     */ 
/* 450 */       ScoreDoc[] scoreDocs = docs.scoreDocs;
/* 451 */       for (int j = 0; j < scoreDocs.length; j++) {
/* 452 */         FieldDoc fieldDoc = (FieldDoc)scoreDocs[j];
/* 453 */         fieldDoc.doc += this.starts[this.i];
/*     */ 
/* 455 */         this.lock.lock();
/*     */         try {
/* 457 */           if (fieldDoc == this.hq.insertWithOverflow(fieldDoc))
/* 458 */             jsr 20;
/*     */         } finally {
/* 460 */           this.lock.unlock();
/*     */         }
/*     */       }
/* 463 */       return docs;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class MultiSearcherCallableNoSort
/*     */     implements Callable<TopDocs>
/*     */   {
/*     */     private final Lock lock;
/*     */     private final Searchable searchable;
/*     */     private final Weight weight;
/*     */     private final Filter filter;
/*     */     private final int nDocs;
/*     */     private final int i;
/*     */     private final HitQueue hq;
/*     */     private final int[] starts;
/*     */ 
/*     */     public MultiSearcherCallableNoSort(Lock lock, Searchable searchable, Weight weight, Filter filter, int nDocs, HitQueue hq, int i, int[] starts)
/*     */     {
/* 370 */       this.lock = lock;
/* 371 */       this.searchable = searchable;
/* 372 */       this.weight = weight;
/* 373 */       this.filter = filter;
/* 374 */       this.nDocs = nDocs;
/* 375 */       this.hq = hq;
/* 376 */       this.i = i;
/* 377 */       this.starts = starts;
/*     */     }
/*     */ 
/*     */     public TopDocs call() throws IOException {
/* 381 */       TopDocs docs = this.searchable.search(this.weight, this.filter, this.nDocs);
/* 382 */       ScoreDoc[] scoreDocs = docs.scoreDocs;
/* 383 */       for (int j = 0; j < scoreDocs.length; j++) {
/* 384 */         ScoreDoc scoreDoc = scoreDocs[j];
/* 385 */         scoreDoc.doc += this.starts[this.i];
/*     */ 
/* 387 */         this.lock.lock();
/*     */         try {
/* 389 */           if (scoreDoc == this.hq.insertWithOverflow(scoreDoc))
/* 390 */             jsr 20;
/*     */         } finally {
/* 392 */           this.lock.unlock();
/*     */         }
/*     */       }
/* 395 */       return docs;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CachedDfSource extends Searcher
/*     */   {
/*     */     private final Map<Term, Integer> dfMap;
/*     */     private final int maxDoc;
/*     */ 
/*     */     public CachedDfSource(Map<Term, Integer> dfMap, int maxDoc, Similarity similarity)
/*     */     {
/*  59 */       this.dfMap = dfMap;
/*  60 */       this.maxDoc = maxDoc;
/*  61 */       setSimilarity(similarity);
/*     */     }
/*     */ 
/*     */     public int docFreq(Term term) {
/*     */       int df;
/*     */       try {
/*  68 */         df = ((Integer)this.dfMap.get(term)).intValue();
/*     */       } catch (NullPointerException e) {
/*  70 */         throw new IllegalArgumentException("df for term " + term.text() + " not available");
/*     */       }
/*     */ 
/*  73 */       return df;
/*     */     }
/*     */ 
/*     */     public int[] docFreqs(Term[] terms)
/*     */     {
/*  78 */       int[] result = new int[terms.length];
/*  79 */       for (int i = 0; i < terms.length; i++) {
/*  80 */         result[i] = docFreq(terms[i]);
/*     */       }
/*  82 */       return result;
/*     */     }
/*     */ 
/*     */     public int maxDoc()
/*     */     {
/*  87 */       return this.maxDoc;
/*     */     }
/*     */ 
/*     */     public Query rewrite(Query query)
/*     */     {
/*  96 */       return query;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */     {
/* 101 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Document doc(int i)
/*     */     {
/* 106 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Document doc(int i, FieldSelector fieldSelector)
/*     */     {
/* 111 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Explanation explain(Weight weight, int doc)
/*     */     {
/* 116 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void search(Weight weight, Filter filter, Collector results)
/*     */     {
/* 121 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public TopDocs search(Weight weight, Filter filter, int n)
/*     */     {
/* 126 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort)
/*     */     {
/* 131 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.MultiSearcher
 * JD-Core Version:    0.6.0
 */