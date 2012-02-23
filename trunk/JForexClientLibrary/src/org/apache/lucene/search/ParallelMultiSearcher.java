/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.CompletionService;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorCompletionService;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.NamedThreadFactory;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ @Deprecated
/*     */ public class ParallelMultiSearcher extends MultiSearcher
/*     */ {
/*     */   private final ExecutorService executor;
/*     */   private final Searchable[] searchables;
/*     */   private final int[] starts;
/*     */ 
/*     */   public ParallelMultiSearcher(Searchable[] searchables)
/*     */     throws IOException
/*     */   {
/*  57 */     this(Executors.newCachedThreadPool(new NamedThreadFactory(ParallelMultiSearcher.class.getSimpleName())), searchables);
/*     */   }
/*     */ 
/*     */   public ParallelMultiSearcher(ExecutorService executor, Searchable[] searchables)
/*     */     throws IOException
/*     */   {
/*  64 */     super(searchables);
/*  65 */     this.searchables = searchables;
/*  66 */     this.starts = getStarts();
/*  67 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */   public int docFreq(Term term)
/*     */     throws IOException
/*     */   {
/*  75 */     ExecutionHelper runner = new ExecutionHelper(this.executor);
/*  76 */     for (int i = 0; i < this.searchables.length; i++) {
/*  77 */       Searchable searchable = this.searchables[i];
/*  78 */       runner.submit(new Callable(searchable, term) {
/*     */         public Integer call() throws IOException {
/*  80 */           return Integer.valueOf(this.val$searchable.docFreq(this.val$term));
/*     */         } } );
/*     */     }
/*  84 */     int docFreq = 0;
/*  85 */     for (Integer num : runner) {
/*  86 */       docFreq += num.intValue();
/*     */     }
/*  88 */     return docFreq;
/*     */   }
/*     */ 
/*     */   public TopDocs search(Weight weight, Filter filter, int nDocs)
/*     */     throws IOException
/*     */   {
/*  98 */     HitQueue hq = new HitQueue(nDocs, false);
/*  99 */     Lock lock = new ReentrantLock();
/* 100 */     ExecutionHelper runner = new ExecutionHelper(this.executor);
/*     */ 
/* 102 */     for (int i = 0; i < this.searchables.length; i++) {
/* 103 */       runner.submit(new MultiSearcher.MultiSearcherCallableNoSort(lock, this.searchables[i], weight, filter, nDocs, hq, i, this.starts));
/*     */     }
/*     */ 
/* 107 */     int totalHits = 0;
/* 108 */     float maxScore = (1.0F / -1.0F);
/* 109 */     for (TopDocs topDocs : runner) {
/* 110 */       totalHits += topDocs.totalHits;
/* 111 */       maxScore = Math.max(maxScore, topDocs.getMaxScore());
/*     */     }
/*     */ 
/* 114 */     ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
/* 115 */     for (int i = hq.size() - 1; i >= 0; i--) {
/* 116 */       scoreDocs[i] = ((ScoreDoc)hq.pop());
/*     */     }
/* 118 */     return new TopDocs(totalHits, scoreDocs, maxScore);
/*     */   }
/*     */ 
/*     */   public TopFieldDocs search(Weight weight, Filter filter, int nDocs, Sort sort)
/*     */     throws IOException
/*     */   {
/* 128 */     if (sort == null) throw new NullPointerException();
/*     */ 
/* 130 */     FieldDocSortedHitQueue hq = new FieldDocSortedHitQueue(nDocs);
/* 131 */     Lock lock = new ReentrantLock();
/* 132 */     ExecutionHelper runner = new ExecutionHelper(this.executor);
/* 133 */     for (int i = 0; i < this.searchables.length; i++) {
/* 134 */       runner.submit(new MultiSearcher.MultiSearcherCallableWithSort(lock, this.searchables[i], weight, filter, nDocs, hq, sort, i, this.starts));
/*     */     }
/*     */ 
/* 137 */     int totalHits = 0;
/* 138 */     float maxScore = (1.0F / -1.0F);
/* 139 */     for (TopFieldDocs topFieldDocs : runner) {
/* 140 */       totalHits += topFieldDocs.totalHits;
/* 141 */       maxScore = Math.max(maxScore, topFieldDocs.getMaxScore());
/*     */     }
/* 143 */     ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
/* 144 */     for (int i = hq.size() - 1; i >= 0; i--) {
/* 145 */       scoreDocs[i] = ((ScoreDoc)hq.pop());
/*     */     }
/* 147 */     return new TopFieldDocs(totalHits, scoreDocs, hq.getFields(), maxScore);
/*     */   }
/*     */ 
/*     */   public void search(Weight weight, Filter filter, Collector collector)
/*     */     throws IOException
/*     */   {
/* 169 */     for (int i = 0; i < this.searchables.length; i++)
/*     */     {
/* 171 */       int start = this.starts[i];
/*     */ 
/* 173 */       Collector hc = new Collector(collector, start)
/*     */       {
/*     */         public void setScorer(Scorer scorer) throws IOException {
/* 176 */           this.val$collector.setScorer(scorer);
/*     */         }
/*     */ 
/*     */         public void collect(int doc) throws IOException
/*     */         {
/* 181 */           this.val$collector.collect(doc);
/*     */         }
/*     */ 
/*     */         public void setNextReader(IndexReader reader, int docBase) throws IOException
/*     */         {
/* 186 */           this.val$collector.setNextReader(reader, this.val$start + docBase);
/*     */         }
/*     */ 
/*     */         public boolean acceptsDocsOutOfOrder()
/*     */         {
/* 191 */           return this.val$collector.acceptsDocsOutOfOrder();
/*     */         }
/*     */       };
/* 195 */       this.searchables[i].search(weight, filter, hc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 201 */     this.executor.shutdown();
/* 202 */     super.close();
/*     */   }
/*     */ 
/*     */   HashMap<Term, Integer> createDocFrequencyMap(Set<Term> terms) throws IOException
/*     */   {
/* 207 */     Term[] allTermsArray = (Term[])terms.toArray(new Term[terms.size()]);
/* 208 */     int[] aggregatedDocFreqs = new int[terms.size()];
/* 209 */     ExecutionHelper runner = new ExecutionHelper(this.executor);
/* 210 */     for (Searchable searchable : this.searchables) {
/* 211 */       runner.submit(new DocumentFrequencyCallable(searchable, allTermsArray));
/*     */     }
/*     */ 
/* 214 */     int docFreqLen = aggregatedDocFreqs.length;
/* 215 */     for (int[] docFreqs : runner) {
/* 216 */       for (int i = 0; i < docFreqLen; i++) {
/* 217 */         aggregatedDocFreqs[i] += docFreqs[i];
/*     */       }
/*     */     }
/*     */ 
/* 221 */     HashMap dfMap = new HashMap();
/* 222 */     for (int i = 0; i < allTermsArray.length; i++) {
/* 223 */       dfMap.put(allTermsArray[i], Integer.valueOf(aggregatedDocFreqs[i]));
/*     */     }
/* 225 */     return dfMap;
/*     */   }
/*     */ 
/*     */   private static final class ExecutionHelper<T>
/*     */     implements Iterator<T>, Iterable<T>
/*     */   {
/*     */     private final CompletionService<T> service;
/*     */     private int numTasks;
/*     */ 
/*     */     ExecutionHelper(Executor executor)
/*     */     {
/* 258 */       this.service = new ExecutorCompletionService(executor);
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/* 262 */       return this.numTasks > 0;
/*     */     }
/*     */ 
/*     */     public void submit(Callable<T> task) {
/* 266 */       this.service.submit(task);
/* 267 */       this.numTasks += 1;
/*     */     }
/*     */ 
/*     */     public T next() {
/* 271 */       if (!hasNext())
/* 272 */         throw new NoSuchElementException();
/*     */       try {
/* 274 */         localObject1 = this.service.take().get();
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */         Object localObject1;
/* 276 */         throw new ThreadInterruptedException(e);
/*     */       } catch (ExecutionException e) {
/* 278 */         throw new RuntimeException(e);
/*     */       } finally {
/* 280 */         this.numTasks -= 1;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 285 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Iterator<T> iterator()
/*     */     {
/* 290 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class DocumentFrequencyCallable
/*     */     implements Callable<int[]>
/*     */   {
/*     */     private final Searchable searchable;
/*     */     private final Term[] terms;
/*     */ 
/*     */     public DocumentFrequencyCallable(Searchable searchable, Term[] terms)
/*     */     {
/* 237 */       this.searchable = searchable;
/* 238 */       this.terms = terms;
/*     */     }
/*     */ 
/*     */     public int[] call() throws Exception {
/* 242 */       return this.searchable.docFreqs(this.terms);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ParallelMultiSearcher
 * JD-Core Version:    0.6.0
 */