/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class IndexWriterConfig
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int DEFAULT_TERM_INDEX_INTERVAL = 128;
/*     */   public static final int DISABLE_AUTO_FLUSH = -1;
/*     */   public static final int DEFAULT_MAX_BUFFERED_DELETE_TERMS = -1;
/*     */   public static final int DEFAULT_MAX_BUFFERED_DOCS = -1;
/*     */   public static final double DEFAULT_RAM_BUFFER_SIZE_MB = 16.0D;
/*  81 */   public static long WRITE_LOCK_TIMEOUT = 1000L;
/*     */   public static final int DEFAULT_MAX_THREAD_STATES = 8;
/*     */   public static final boolean DEFAULT_READER_POOLING = false;
/*  93 */   public static final int DEFAULT_READER_TERMS_INDEX_DIVISOR = IndexReader.DEFAULT_TERMS_INDEX_DIVISOR;
/*     */   private final Analyzer analyzer;
/*     */   private volatile IndexDeletionPolicy delPolicy;
/*     */   private volatile IndexCommit commit;
/*     */   private volatile OpenMode openMode;
/*     */   private volatile Similarity similarity;
/*     */   private volatile int termIndexInterval;
/*     */   private volatile MergeScheduler mergeScheduler;
/*     */   private volatile long writeLockTimeout;
/*     */   private volatile int maxBufferedDeleteTerms;
/*     */   private volatile double ramBufferSizeMB;
/*     */   private volatile int maxBufferedDocs;
/*     */   private volatile DocumentsWriter.IndexingChain indexingChain;
/*     */   private volatile IndexWriter.IndexReaderWarmer mergedSegmentWarmer;
/*     */   private volatile MergePolicy mergePolicy;
/*     */   private volatile int maxThreadStates;
/*     */   private volatile boolean readerPooling;
/*     */   private volatile int readerTermsIndexDivisor;
/*     */   private Version matchVersion;
/*     */ 
/*     */   public static void setDefaultWriteLockTimeout(long writeLockTimeout)
/*     */   {
/* 100 */     WRITE_LOCK_TIMEOUT = writeLockTimeout;
/*     */   }
/*     */ 
/*     */   public static long getDefaultWriteLockTimeout()
/*     */   {
/* 110 */     return WRITE_LOCK_TIMEOUT;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig(Version matchVersion, Analyzer analyzer)
/*     */   {
/* 146 */     this.matchVersion = matchVersion;
/* 147 */     this.analyzer = analyzer;
/* 148 */     this.delPolicy = new KeepOnlyLastCommitDeletionPolicy();
/* 149 */     this.commit = null;
/* 150 */     this.openMode = OpenMode.CREATE_OR_APPEND;
/* 151 */     this.similarity = Similarity.getDefault();
/* 152 */     this.termIndexInterval = 128;
/* 153 */     this.mergeScheduler = new ConcurrentMergeScheduler();
/* 154 */     this.writeLockTimeout = WRITE_LOCK_TIMEOUT;
/* 155 */     this.maxBufferedDeleteTerms = -1;
/* 156 */     this.ramBufferSizeMB = 16.0D;
/* 157 */     this.maxBufferedDocs = -1;
/* 158 */     this.indexingChain = DocumentsWriter.defaultIndexingChain;
/* 159 */     this.mergedSegmentWarmer = null;
/* 160 */     if (matchVersion.onOrAfter(Version.LUCENE_32))
/* 161 */       this.mergePolicy = new TieredMergePolicy();
/*     */     else {
/* 163 */       this.mergePolicy = new LogByteSizeMergePolicy();
/*     */     }
/* 165 */     this.maxThreadStates = 8;
/* 166 */     this.readerPooling = false;
/* 167 */     this.readerTermsIndexDivisor = DEFAULT_READER_TERMS_INDEX_DIVISOR;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 175 */       return super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/*     */     }
/* 178 */     throw new RuntimeException(e);
/*     */   }
/*     */ 
/*     */   public Analyzer getAnalyzer()
/*     */   {
/* 184 */     return this.analyzer;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setOpenMode(OpenMode openMode)
/*     */   {
/* 191 */     this.openMode = openMode;
/* 192 */     return this;
/*     */   }
/*     */ 
/*     */   public OpenMode getOpenMode()
/*     */   {
/* 197 */     return this.openMode;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setIndexDeletionPolicy(IndexDeletionPolicy delPolicy)
/*     */   {
/* 218 */     this.delPolicy = (delPolicy == null ? new KeepOnlyLastCommitDeletionPolicy() : delPolicy);
/* 219 */     return this;
/*     */   }
/*     */ 
/*     */   public IndexDeletionPolicy getIndexDeletionPolicy()
/*     */   {
/* 228 */     return this.delPolicy;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setIndexCommit(IndexCommit commit)
/*     */   {
/* 237 */     this.commit = commit;
/* 238 */     return this;
/*     */   }
/*     */ 
/*     */   public IndexCommit getIndexCommit()
/*     */   {
/* 247 */     return this.commit;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setSimilarity(Similarity similarity)
/*     */   {
/* 260 */     this.similarity = (similarity == null ? Similarity.getDefault() : similarity);
/* 261 */     return this;
/*     */   }
/*     */ 
/*     */   public Similarity getSimilarity()
/*     */   {
/* 270 */     return this.similarity;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setTermIndexInterval(int interval)
/*     */   {
/* 298 */     this.termIndexInterval = interval;
/* 299 */     return this;
/*     */   }
/*     */ 
/*     */   public int getTermIndexInterval()
/*     */   {
/* 308 */     return this.termIndexInterval;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setMergeScheduler(MergeScheduler mergeScheduler)
/*     */   {
/* 320 */     this.mergeScheduler = (mergeScheduler == null ? new ConcurrentMergeScheduler() : mergeScheduler);
/* 321 */     return this;
/*     */   }
/*     */ 
/*     */   public MergeScheduler getMergeScheduler()
/*     */   {
/* 329 */     return this.mergeScheduler;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setWriteLockTimeout(long writeLockTimeout)
/*     */   {
/* 339 */     this.writeLockTimeout = writeLockTimeout;
/* 340 */     return this;
/*     */   }
/*     */ 
/*     */   public long getWriteLockTimeout()
/*     */   {
/* 349 */     return this.writeLockTimeout;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms)
/*     */   {
/* 368 */     if ((maxBufferedDeleteTerms != -1) && (maxBufferedDeleteTerms < 1))
/*     */     {
/* 370 */       throw new IllegalArgumentException("maxBufferedDeleteTerms must at least be 1 when enabled");
/*     */     }
/* 372 */     this.maxBufferedDeleteTerms = maxBufferedDeleteTerms;
/* 373 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMaxBufferedDeleteTerms()
/*     */   {
/* 383 */     return this.maxBufferedDeleteTerms;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setRAMBufferSizeMB(double ramBufferSizeMB)
/*     */   {
/* 426 */     if (ramBufferSizeMB > 2048.0D) {
/* 427 */       throw new IllegalArgumentException("ramBufferSize " + ramBufferSizeMB + " is too large; should be comfortably less than 2048");
/*     */     }
/*     */ 
/* 430 */     if ((ramBufferSizeMB != -1.0D) && (ramBufferSizeMB <= 0.0D)) {
/* 431 */       throw new IllegalArgumentException("ramBufferSize should be > 0.0 MB when enabled");
/*     */     }
/* 433 */     if ((ramBufferSizeMB == -1.0D) && (this.maxBufferedDocs == -1)) {
/* 434 */       throw new IllegalArgumentException("at least one of ramBufferSize and maxBufferedDocs must be enabled");
/*     */     }
/* 436 */     this.ramBufferSizeMB = ramBufferSizeMB;
/* 437 */     return this;
/*     */   }
/*     */ 
/*     */   public double getRAMBufferSizeMB()
/*     */   {
/* 442 */     return this.ramBufferSizeMB;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setMaxBufferedDocs(int maxBufferedDocs)
/*     */   {
/* 470 */     if ((maxBufferedDocs != -1) && (maxBufferedDocs < 2)) {
/* 471 */       throw new IllegalArgumentException("maxBufferedDocs must at least be 2 when enabled");
/*     */     }
/* 473 */     if ((maxBufferedDocs == -1) && (this.ramBufferSizeMB == -1.0D))
/*     */     {
/* 475 */       throw new IllegalArgumentException("at least one of ramBufferSize and maxBufferedDocs must be enabled");
/*     */     }
/* 477 */     this.maxBufferedDocs = maxBufferedDocs;
/* 478 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMaxBufferedDocs()
/*     */   {
/* 488 */     return this.maxBufferedDocs;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setMergedSegmentWarmer(IndexWriter.IndexReaderWarmer mergeSegmentWarmer)
/*     */   {
/* 495 */     this.mergedSegmentWarmer = mergeSegmentWarmer;
/* 496 */     return this;
/*     */   }
/*     */ 
/*     */   public IndexWriter.IndexReaderWarmer getMergedSegmentWarmer()
/*     */   {
/* 501 */     return this.mergedSegmentWarmer;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setMergePolicy(MergePolicy mergePolicy)
/*     */   {
/* 513 */     this.mergePolicy = (mergePolicy == null ? new LogByteSizeMergePolicy() : mergePolicy);
/* 514 */     return this;
/*     */   }
/*     */ 
/*     */   public MergePolicy getMergePolicy()
/*     */   {
/* 523 */     return this.mergePolicy;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setMaxThreadStates(int maxThreadStates)
/*     */   {
/* 534 */     this.maxThreadStates = (maxThreadStates < 1 ? 8 : maxThreadStates);
/* 535 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMaxThreadStates()
/*     */   {
/* 541 */     return this.maxThreadStates;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setReaderPooling(boolean readerPooling)
/*     */   {
/* 555 */     this.readerPooling = readerPooling;
/* 556 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean getReaderPooling()
/*     */   {
/* 562 */     return this.readerPooling;
/*     */   }
/*     */ 
/*     */   IndexWriterConfig setIndexingChain(DocumentsWriter.IndexingChain indexingChain)
/*     */   {
/* 569 */     this.indexingChain = (indexingChain == null ? DocumentsWriter.defaultIndexingChain : indexingChain);
/* 570 */     return this;
/*     */   }
/*     */ 
/*     */   DocumentsWriter.IndexingChain getIndexingChain()
/*     */   {
/* 575 */     return this.indexingChain;
/*     */   }
/*     */ 
/*     */   public IndexWriterConfig setReaderTermsIndexDivisor(int divisor)
/*     */   {
/* 589 */     if ((divisor <= 0) && (divisor != -1)) {
/* 590 */       throw new IllegalArgumentException("divisor must be >= 1, or -1 (got " + divisor + ")");
/*     */     }
/* 592 */     this.readerTermsIndexDivisor = divisor;
/* 593 */     return this;
/*     */   }
/*     */ 
/*     */   public int getReaderTermsIndexDivisor()
/*     */   {
/* 598 */     return this.readerTermsIndexDivisor;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 603 */     StringBuilder sb = new StringBuilder();
/* 604 */     sb.append("matchVersion=").append(this.matchVersion).append("\n");
/* 605 */     sb.append("analyzer=").append(this.analyzer == null ? "null" : this.analyzer.getClass().getName()).append("\n");
/* 606 */     sb.append("delPolicy=").append(this.delPolicy.getClass().getName()).append("\n");
/* 607 */     sb.append("commit=").append(this.commit == null ? "null" : this.commit).append("\n");
/* 608 */     sb.append("openMode=").append(this.openMode).append("\n");
/* 609 */     sb.append("similarity=").append(this.similarity.getClass().getName()).append("\n");
/* 610 */     sb.append("termIndexInterval=").append(this.termIndexInterval).append("\n");
/* 611 */     sb.append("mergeScheduler=").append(this.mergeScheduler.getClass().getName()).append("\n");
/* 612 */     sb.append("default WRITE_LOCK_TIMEOUT=").append(WRITE_LOCK_TIMEOUT).append("\n");
/* 613 */     sb.append("writeLockTimeout=").append(this.writeLockTimeout).append("\n");
/* 614 */     sb.append("maxBufferedDeleteTerms=").append(this.maxBufferedDeleteTerms).append("\n");
/* 615 */     sb.append("ramBufferSizeMB=").append(this.ramBufferSizeMB).append("\n");
/* 616 */     sb.append("maxBufferedDocs=").append(this.maxBufferedDocs).append("\n");
/* 617 */     sb.append("mergedSegmentWarmer=").append(this.mergedSegmentWarmer).append("\n");
/* 618 */     sb.append("mergePolicy=").append(this.mergePolicy).append("\n");
/* 619 */     sb.append("maxThreadStates=").append(this.maxThreadStates).append("\n");
/* 620 */     sb.append("readerPooling=").append(this.readerPooling).append("\n");
/* 621 */     sb.append("readerTermsIndexDivisor=").append(this.readerTermsIndexDivisor).append("\n");
/* 622 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static enum OpenMode
/*     */   {
/*  56 */     CREATE, APPEND, CREATE_OR_APPEND;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexWriterConfig
 * JD-Core Version:    0.6.0
 */