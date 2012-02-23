/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.CompletionService;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorCompletionService;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.index.CorruptIndexException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.util.ReaderUtil;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ public class IndexSearcher extends Searcher
/*     */ {
/*     */   IndexReader reader;
/*     */   private boolean closeReader;
/*     */   protected final IndexReader[] subReaders;
/*     */   protected final int[] docStarts;
/*     */   private final ExecutorService executor;
/*     */   protected final IndexSearcher[] subSearchers;
/*     */   private boolean fieldSortDoTrackScores;
/*     */   private boolean fieldSortDoMaxScore;
/*     */ 
/*     */   public IndexSearcher(Directory path)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/*  89 */     this(IndexReader.open(path, true), true, null);
/*     */   }
/*     */ 
/*     */   public IndexSearcher(Directory path, boolean readOnly)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 104 */     this(IndexReader.open(path, readOnly), true, null);
/*     */   }
/*     */ 
/*     */   public IndexSearcher(IndexReader r)
/*     */   {
/* 109 */     this(r, false, null);
/*     */   }
/*     */ 
/*     */   public IndexSearcher(IndexReader r, ExecutorService executor)
/*     */   {
/* 124 */     this(r, false, executor);
/*     */   }
/*     */ 
/*     */   public IndexSearcher(IndexReader reader, IndexReader[] subReaders, int[] docStarts)
/*     */   {
/* 132 */     this.reader = reader;
/* 133 */     this.subReaders = subReaders;
/* 134 */     this.docStarts = docStarts;
/* 135 */     this.closeReader = false;
/* 136 */     this.executor = null;
/* 137 */     this.subSearchers = null;
/*     */   }
/*     */ 
/*     */   public IndexSearcher(IndexReader reader, IndexReader[] subReaders, int[] docStarts, ExecutorService executor)
/*     */   {
/* 154 */     this.reader = reader;
/* 155 */     this.subReaders = subReaders;
/* 156 */     this.docStarts = docStarts;
/* 157 */     if (executor == null) {
/* 158 */       this.subSearchers = null;
/*     */     } else {
/* 160 */       this.subSearchers = new IndexSearcher[subReaders.length];
/* 161 */       for (int i = 0; i < subReaders.length; i++) {
/* 162 */         this.subSearchers[i] = new IndexSearcher(subReaders[i]);
/*     */       }
/*     */     }
/* 165 */     this.closeReader = false;
/* 166 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */   private IndexSearcher(IndexReader r, boolean closeReader, ExecutorService executor) {
/* 170 */     this.reader = r;
/* 171 */     this.executor = executor;
/* 172 */     this.closeReader = closeReader;
/*     */ 
/* 174 */     List subReadersList = new ArrayList();
/* 175 */     gatherSubReaders(subReadersList, this.reader);
/* 176 */     this.subReaders = ((IndexReader[])subReadersList.toArray(new IndexReader[subReadersList.size()]));
/* 177 */     this.docStarts = new int[this.subReaders.length];
/* 178 */     int maxDoc = 0;
/* 179 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 180 */       this.docStarts[i] = maxDoc;
/* 181 */       maxDoc += this.subReaders[i].maxDoc();
/*     */     }
/* 183 */     if (executor == null) {
/* 184 */       this.subSearchers = null;
/*     */     } else {
/* 186 */       this.subSearchers = new IndexSearcher[this.subReaders.length];
/* 187 */       for (int i = 0; i < this.subReaders.length; i++)
/* 188 */         this.subSearchers[i] = new IndexSearcher(this.subReaders[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void gatherSubReaders(List<IndexReader> allSubReaders, IndexReader r)
/*     */   {
/* 194 */     ReaderUtil.gatherSubReaders(allSubReaders, r);
/*     */   }
/*     */ 
/*     */   public IndexReader getIndexReader()
/*     */   {
/* 199 */     return this.reader;
/*     */   }
/*     */ 
/*     */   public IndexReader[] getSubReaders()
/*     */   {
/* 204 */     return this.subReaders;
/*     */   }
/*     */ 
/*     */   public int maxDoc()
/*     */   {
/* 213 */     return this.reader.maxDoc();
/*     */   }
/*     */ 
/*     */   public int docFreq(Term term)
/*     */     throws IOException
/*     */   {
/* 219 */     if (this.executor == null) {
/* 220 */       return this.reader.docFreq(term);
/*     */     }
/* 222 */     ExecutionHelper runner = new ExecutionHelper(this.executor);
/* 223 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 224 */       IndexSearcher searchable = this.subSearchers[i];
/* 225 */       runner.submit(new Callable(searchable, term) {
/*     */         public Integer call() throws IOException {
/* 227 */           return Integer.valueOf(this.val$searchable.docFreq(this.val$term));
/*     */         } } );
/*     */     }
/* 231 */     int docFreq = 0;
/* 232 */     for (Integer num : runner) {
/* 233 */       docFreq += num.intValue();
/*     */     }
/* 235 */     return docFreq;
/*     */   }
/*     */ 
/*     */   public Document doc(int docID)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 242 */     return this.reader.document(docID);
/*     */   }
/*     */ 
/*     */   public Document doc(int docID, FieldSelector fieldSelector)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 248 */     return this.reader.document(docID, fieldSelector);
/*     */   }
/*     */ 
/*     */   public void setSimilarity(Similarity similarity)
/*     */   {
/* 257 */     super.setSimilarity(similarity);
/*     */   }
/*     */ 
/*     */   public Similarity getSimilarity()
/*     */   {
/* 262 */     return super.getSimilarity();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 273 */     if (this.closeReader)
/* 274 */       this.reader.close();
/*     */   }
/*     */ 
/*     */   public TopDocs search(Query query, int n)
/*     */     throws IOException
/*     */   {
/* 286 */     return search(query, null, n);
/*     */   }
/*     */ 
/*     */   public TopDocs search(Query query, Filter filter, int n)
/*     */     throws IOException
/*     */   {
/* 298 */     return search(createNormalizedWeight(query), filter, n);
/*     */   }
/*     */ 
/*     */   public void search(Query query, Filter filter, Collector results)
/*     */     throws IOException
/*     */   {
/* 320 */     search(createNormalizedWeight(query), filter, results);
/*     */   }
/*     */ 
/*     */   public void search(Query query, Collector results)
/*     */     throws IOException
/*     */   {
/* 339 */     search(createNormalizedWeight(query), null, results);
/*     */   }
/*     */ 
/*     */   public TopFieldDocs search(Query query, Filter filter, int n, Sort sort)
/*     */     throws IOException
/*     */   {
/* 356 */     return search(createNormalizedWeight(query), filter, n, sort);
/*     */   }
/*     */ 
/*     */   public TopFieldDocs search(Query query, int n, Sort sort)
/*     */     throws IOException
/*     */   {
/* 370 */     return search(createNormalizedWeight(query), null, n, sort);
/*     */   }
/*     */ 
/*     */   public TopDocs search(Weight weight, Filter filter, int nDocs)
/*     */     throws IOException
/*     */   {
/* 383 */     if (this.executor == null)
/*     */     {
/* 385 */       int limit = this.reader.maxDoc();
/* 386 */       if (limit == 0) {
/* 387 */         limit = 1;
/*     */       }
/* 389 */       nDocs = Math.min(nDocs, limit);
/* 390 */       TopScoreDocCollector collector = TopScoreDocCollector.create(nDocs, !weight.scoresDocsOutOfOrder());
/* 391 */       search(weight, filter, collector);
/* 392 */       return collector.topDocs();
/*     */     }
/* 394 */     HitQueue hq = new HitQueue(nDocs, false);
/* 395 */     Lock lock = new ReentrantLock();
/* 396 */     ExecutionHelper runner = new ExecutionHelper(this.executor);
/*     */ 
/* 398 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 399 */       runner.submit(new MultiSearcherCallableNoSort(lock, this.subSearchers[i], weight, filter, nDocs, hq, this.docStarts[i]));
/*     */     }
/*     */ 
/* 403 */     int totalHits = 0;
/* 404 */     float maxScore = (1.0F / -1.0F);
/* 405 */     for (TopDocs topDocs : runner) {
/* 406 */       if (topDocs.totalHits != 0) {
/* 407 */         totalHits += topDocs.totalHits;
/* 408 */         maxScore = Math.max(maxScore, topDocs.getMaxScore());
/*     */       }
/*     */     }
/*     */ 
/* 412 */     ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
/* 413 */     for (int i = hq.size() - 1; i >= 0; i--) {
/* 414 */       scoreDocs[i] = ((ScoreDoc)hq.pop());
/*     */     }
/* 416 */     return new TopDocs(totalHits, scoreDocs, maxScore);
/*     */   }
/*     */ 
/*     */   public TopFieldDocs search(Weight weight, Filter filter, int nDocs, Sort sort)
/*     */     throws IOException
/*     */   {
/* 433 */     return search(weight, filter, nDocs, sort, true);
/*     */   }
/*     */ 
/*     */   protected TopFieldDocs search(Weight weight, Filter filter, int nDocs, Sort sort, boolean fillFields)
/*     */     throws IOException
/*     */   {
/* 451 */     if (sort == null) throw new NullPointerException();
/*     */ 
/* 453 */     if (this.executor == null)
/*     */     {
/* 455 */       int limit = this.reader.maxDoc();
/* 456 */       if (limit == 0) {
/* 457 */         limit = 1;
/*     */       }
/* 459 */       nDocs = Math.min(nDocs, limit);
/*     */ 
/* 461 */       TopFieldCollector collector = TopFieldCollector.create(sort, nDocs, fillFields, this.fieldSortDoTrackScores, this.fieldSortDoMaxScore, !weight.scoresDocsOutOfOrder());
/*     */ 
/* 463 */       search(weight, filter, collector);
/* 464 */       return (TopFieldDocs)collector.topDocs();
/*     */     }
/* 466 */     TopFieldCollector topCollector = TopFieldCollector.create(sort, nDocs, fillFields, this.fieldSortDoTrackScores, this.fieldSortDoMaxScore, false);
/*     */ 
/* 472 */     Lock lock = new ReentrantLock();
/* 473 */     ExecutionHelper runner = new ExecutionHelper(this.executor);
/* 474 */     for (int i = 0; i < this.subReaders.length; i++) {
/* 475 */       runner.submit(new MultiSearcherCallableWithSort(lock, this.subSearchers[i], weight, filter, nDocs, topCollector, sort, this.docStarts[i]));
/*     */     }
/*     */ 
/* 478 */     int totalHits = 0;
/* 479 */     float maxScore = (1.0F / -1.0F);
/* 480 */     for (TopFieldDocs topFieldDocs : runner) {
/* 481 */       if (topFieldDocs.totalHits != 0) {
/* 482 */         totalHits += topFieldDocs.totalHits;
/* 483 */         maxScore = Math.max(maxScore, topFieldDocs.getMaxScore());
/*     */       }
/*     */     }
/*     */ 
/* 487 */     TopFieldDocs topDocs = (TopFieldDocs)topCollector.topDocs();
/*     */ 
/* 489 */     return new TopFieldDocs(totalHits, topDocs.scoreDocs, topDocs.fields, topDocs.getMaxScore());
/*     */   }
/*     */ 
/*     */   public void search(Weight weight, Filter filter, Collector collector)
/*     */     throws IOException
/*     */   {
/* 521 */     if (filter == null) {
/* 522 */       for (int i = 0; i < this.subReaders.length; i++) {
/* 523 */         collector.setNextReader(this.subReaders[i], this.docStarts[i]);
/* 524 */         Scorer scorer = weight.scorer(this.subReaders[i], !collector.acceptsDocsOutOfOrder(), true);
/* 525 */         if (scorer != null)
/* 526 */           scorer.score(collector);
/*     */       }
/*     */     }
/*     */     else
/* 530 */       for (int i = 0; i < this.subReaders.length; i++) {
/* 531 */         collector.setNextReader(this.subReaders[i], this.docStarts[i]);
/* 532 */         searchWithFilter(this.subReaders[i], weight, filter, collector);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void searchWithFilter(IndexReader reader, Weight weight, Filter filter, Collector collector)
/*     */     throws IOException
/*     */   {
/* 540 */     assert (filter != null);
/*     */ 
/* 542 */     Scorer scorer = weight.scorer(reader, true, false);
/* 543 */     if (scorer == null) {
/* 544 */       return;
/*     */     }
/*     */ 
/* 547 */     int docID = scorer.docID();
/* 548 */     assert ((docID == -1) || (docID == 2147483647));
/*     */ 
/* 551 */     DocIdSet filterDocIdSet = filter.getDocIdSet(reader);
/* 552 */     if (filterDocIdSet == null)
/*     */     {
/* 554 */       return;
/*     */     }
/*     */ 
/* 557 */     DocIdSetIterator filterIter = filterDocIdSet.iterator();
/* 558 */     if (filterIter == null)
/*     */     {
/* 560 */       return;
/*     */     }
/* 562 */     int filterDoc = filterIter.nextDoc();
/* 563 */     int scorerDoc = scorer.advance(filterDoc);
/*     */ 
/* 565 */     collector.setScorer(scorer);
/*     */     while (true) {
/* 567 */       if (scorerDoc == filterDoc)
/*     */       {
/* 569 */         if (scorerDoc == 2147483647) {
/*     */           break;
/*     */         }
/* 572 */         collector.collect(scorerDoc);
/* 573 */         filterDoc = filterIter.nextDoc();
/* 574 */         scorerDoc = scorer.advance(filterDoc); continue;
/* 575 */       }if (scorerDoc > filterDoc) {
/* 576 */         filterDoc = filterIter.advance(scorerDoc); continue;
/*     */       }
/* 578 */       scorerDoc = scorer.advance(filterDoc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Query rewrite(Query original)
/*     */     throws IOException
/*     */   {
/* 588 */     Query query = original;
/* 589 */     for (Query rewrittenQuery = query.rewrite(this.reader); rewrittenQuery != query; )
/*     */     {
/* 591 */       query = rewrittenQuery;
/*     */ 
/* 590 */       rewrittenQuery = query.rewrite(this.reader);
/*     */     }
/*     */ 
/* 593 */     return query;
/*     */   }
/*     */ 
/*     */   public Explanation explain(Query query, int doc)
/*     */     throws IOException
/*     */   {
/* 606 */     return explain(createNormalizedWeight(query), doc);
/*     */   }
/*     */ 
/*     */   public Explanation explain(Weight weight, int doc)
/*     */     throws IOException
/*     */   {
/* 622 */     int n = ReaderUtil.subIndex(doc, this.docStarts);
/* 623 */     int deBasedDoc = doc - this.docStarts[n];
/*     */ 
/* 625 */     return weight.explain(this.subReaders[n], deBasedDoc);
/*     */   }
/*     */ 
/*     */   public void setDefaultFieldSortScoring(boolean doTrackScores, boolean doMaxScore)
/*     */   {
/* 644 */     this.fieldSortDoTrackScores = doTrackScores;
/* 645 */     this.fieldSortDoMaxScore = doMaxScore;
/* 646 */     if (this.subSearchers != null)
/* 647 */       for (IndexSearcher sub : this.subSearchers)
/* 648 */         sub.setDefaultFieldSortScoring(doTrackScores, doMaxScore);
/*     */   }
/*     */ 
/*     */   public Weight createNormalizedWeight(Query query)
/*     */     throws IOException
/*     */   {
/* 661 */     return super.createNormalizedWeight(query);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 853 */     return "IndexSearcher(" + this.reader + ")";
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
/* 815 */       this.service = new ExecutorCompletionService(executor);
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/* 819 */       return this.numTasks > 0;
/*     */     }
/*     */ 
/*     */     public void submit(Callable<T> task) {
/* 823 */       this.service.submit(task);
/* 824 */       this.numTasks += 1;
/*     */     }
/*     */ 
/*     */     public T next() {
/* 828 */       if (!hasNext())
/* 829 */         throw new NoSuchElementException();
/*     */       try {
/* 831 */         localObject1 = this.service.take().get();
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */         Object localObject1;
/* 833 */         throw new ThreadInterruptedException(e);
/*     */       } catch (ExecutionException e) {
/* 835 */         throw new RuntimeException(e);
/*     */       } finally {
/* 837 */         this.numTasks -= 1;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 842 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Iterator<T> iterator()
/*     */     {
/* 847 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class MultiSearcherCallableWithSort
/*     */     implements Callable<TopFieldDocs>
/*     */   {
/*     */     private final Lock lock;
/*     */     private final IndexSearcher searchable;
/*     */     private final Weight weight;
/*     */     private final Filter filter;
/*     */     private final int nDocs;
/*     */     private final TopFieldCollector hq;
/*     */     private final int docBase;
/*     */     private final Sort sort;
/* 768 */     private final FakeScorer fakeScorer = new FakeScorer();
/*     */ 
/*     */     public MultiSearcherCallableWithSort(Lock lock, IndexSearcher searchable, Weight weight, Filter filter, int nDocs, TopFieldCollector hq, Sort sort, int docBase)
/*     */     {
/* 724 */       this.lock = lock;
/* 725 */       this.searchable = searchable;
/* 726 */       this.weight = weight;
/* 727 */       this.filter = filter;
/* 728 */       this.nDocs = nDocs;
/* 729 */       this.hq = hq;
/* 730 */       this.docBase = docBase;
/* 731 */       this.sort = sort;
/*     */     }
/*     */ 
/*     */     public TopFieldDocs call()
/*     */       throws IOException
/*     */     {
/* 771 */       TopFieldDocs docs = this.searchable.search(this.weight, this.filter, this.nDocs, this.sort);
/*     */ 
/* 775 */       for (int j = 0; j < docs.fields.length; j++) {
/* 776 */         if (docs.fields[j].getType() != 1)
/*     */           continue;
/* 778 */         for (int j2 = 0; j2 < docs.scoreDocs.length; j2++) {
/* 779 */           FieldDoc fd = (FieldDoc)docs.scoreDocs[j2];
/* 780 */           fd.fields[j] = Integer.valueOf(((Integer)fd.fields[j]).intValue() + this.docBase);
/*     */         }
/* 782 */         break;
/*     */       }
/*     */ 
/* 786 */       this.lock.lock();
/*     */       try {
/* 788 */         this.hq.setNextReader(this.searchable.getIndexReader(), this.docBase);
/* 789 */         this.hq.setScorer(this.fakeScorer);
/* 790 */         for (ScoreDoc scoreDoc : docs.scoreDocs) {
/* 791 */           this.fakeScorer.doc = scoreDoc.doc;
/* 792 */           this.fakeScorer.score = scoreDoc.score;
/* 793 */           this.hq.collect(scoreDoc.doc);
/*     */         }
/*     */       } finally {
/* 796 */         this.lock.unlock();
/*     */       }
/*     */ 
/* 799 */       return docs;
/*     */     }
/*     */ 
/*     */     private final class FakeScorer extends Scorer
/*     */     {
/*     */       float score;
/*     */       int doc;
/*     */ 
/*     */       public FakeScorer()
/*     */       {
/* 739 */         super(null);
/*     */       }
/*     */ 
/*     */       public int advance(int target)
/*     */       {
/* 744 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public int docID()
/*     */       {
/* 749 */         return this.doc;
/*     */       }
/*     */ 
/*     */       public float freq()
/*     */       {
/* 754 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public int nextDoc()
/*     */       {
/* 759 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public float score()
/*     */       {
/* 764 */         return this.score;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class MultiSearcherCallableNoSort
/*     */     implements Callable<TopDocs>
/*     */   {
/*     */     private final Lock lock;
/*     */     private final IndexSearcher searchable;
/*     */     private final Weight weight;
/*     */     private final Filter filter;
/*     */     private final int nDocs;
/*     */     private final HitQueue hq;
/*     */     private final int docBase;
/*     */ 
/*     */     public MultiSearcherCallableNoSort(Lock lock, IndexSearcher searchable, Weight weight, Filter filter, int nDocs, HitQueue hq, int docBase)
/*     */     {
/* 679 */       this.lock = lock;
/* 680 */       this.searchable = searchable;
/* 681 */       this.weight = weight;
/* 682 */       this.filter = filter;
/* 683 */       this.nDocs = nDocs;
/* 684 */       this.hq = hq;
/* 685 */       this.docBase = docBase;
/*     */     }
/*     */ 
/*     */     public TopDocs call() throws IOException {
/* 689 */       TopDocs docs = this.searchable.search(this.weight, this.filter, this.nDocs);
/* 690 */       ScoreDoc[] scoreDocs = docs.scoreDocs;
/* 691 */       for (int j = 0; j < scoreDocs.length; j++) {
/* 692 */         ScoreDoc scoreDoc = scoreDocs[j];
/* 693 */         scoreDoc.doc += this.docBase;
/*     */ 
/* 695 */         this.lock.lock();
/*     */         try {
/* 697 */           if (scoreDoc == this.hq.insertWithOverflow(scoreDoc))
/* 698 */             jsr 20;
/*     */         } finally {
/* 700 */           this.lock.unlock();
/*     */         }
/*     */       }
/* 703 */       return docs;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.IndexSearcher
 * JD-Core Version:    0.6.0
 */