/*      */ package org.apache.lucene.index;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import org.apache.lucene.analysis.Analyzer;
/*      */ import org.apache.lucene.document.Document;
/*      */ import org.apache.lucene.search.Query;
/*      */ import org.apache.lucene.search.Similarity;
/*      */ import org.apache.lucene.store.AlreadyClosedException;
/*      */ import org.apache.lucene.store.Directory;
/*      */ import org.apache.lucene.store.RAMFile;
/*      */ import org.apache.lucene.util.ArrayUtil;
/*      */ import org.apache.lucene.util.BitVector;
/*      */ import org.apache.lucene.util.RamUsageEstimator;
/*      */ import org.apache.lucene.util.ThreadInterruptedException;
/*      */ 
/*      */ final class DocumentsWriter
/*      */ {
/*  111 */   final AtomicLong bytesUsed = new AtomicLong(0L);
/*      */   IndexWriter writer;
/*      */   Directory directory;
/*      */   String segment;
/*      */   private int nextDocID;
/*      */   private int numDocs;
/*  122 */   private DocumentsWriterThreadState[] threadStates = new DocumentsWriterThreadState[0];
/*  123 */   private final HashMap<Thread, DocumentsWriterThreadState> threadBindings = new HashMap();
/*      */   boolean bufferIsFull;
/*      */   private boolean aborting;
/*      */   PrintStream infoStream;
/*  129 */   int maxFieldLength = IndexWriter.DEFAULT_MAX_FIELD_LENGTH;
/*      */   Similarity similarity;
/*      */   private final int maxThreadStates;
/*  137 */   private BufferedDeletes pendingDeletes = new BufferedDeletes();
/*      */   static final IndexingChain defaultIndexingChain;
/*      */   final DocConsumer consumer;
/*      */   private final IndexWriterConfig config;
/*      */   private boolean closed;
/*      */   private final FieldInfos fieldInfos;
/*      */   private final BufferedDeletesStream bufferedDeletesStream;
/*      */   private final IndexWriter.FlushControl flushControl;
/* 1082 */   final SkipDocWriter skipDocWriter = new SkipDocWriter(null);
/*      */ 
/* 1084 */   NumberFormat nf = NumberFormat.getInstance();
/*      */   static final int BYTE_BLOCK_SHIFT = 15;
/*      */   static final int BYTE_BLOCK_SIZE = 32768;
/*      */   static final int BYTE_BLOCK_MASK = 32767;
/*      */   static final int BYTE_BLOCK_NOT_MASK = -32768;
/*      */   static final int INT_BLOCK_SHIFT = 13;
/*      */   static final int INT_BLOCK_SIZE = 8192;
/*      */   static final int INT_BLOCK_MASK = 8191;
/* 1147 */   private List<int[]> freeIntBlocks = new ArrayList();
/*      */ 
/* 1178 */   ByteBlockAllocator byteBlockAllocator = new ByteBlockAllocator(32768);
/*      */   static final int PER_DOC_BLOCK_SIZE = 1024;
/* 1182 */   final ByteBlockAllocator perDocAllocator = new ByteBlockAllocator(1024);
/*      */   static final int CHAR_BLOCK_SHIFT = 14;
/*      */   static final int CHAR_BLOCK_SIZE = 16384;
/*      */   static final int CHAR_BLOCK_MASK = 16383;
/*      */   static final int MAX_TERM_LENGTH = 16383;
/* 1193 */   private ArrayList<char[]> freeCharBlocks = new ArrayList();
/*      */ 
/* 1343 */   final WaitQueue waitQueue = new WaitQueue();
/*      */ 
/*      */   PerDocBuffer newPerDocBuffer()
/*      */   {
/*  181 */     return new PerDocBuffer();
/*      */   }
/*      */ 
/*      */   DocumentsWriter(IndexWriterConfig config, Directory directory, IndexWriter writer, FieldInfos fieldInfos, BufferedDeletesStream bufferedDeletesStream)
/*      */     throws IOException
/*      */   {
/*  273 */     this.directory = directory;
/*  274 */     this.writer = writer;
/*  275 */     this.similarity = config.getSimilarity();
/*  276 */     this.maxThreadStates = config.getMaxThreadStates();
/*  277 */     this.fieldInfos = fieldInfos;
/*  278 */     this.bufferedDeletesStream = bufferedDeletesStream;
/*  279 */     this.flushControl = writer.flushControl;
/*      */ 
/*  281 */     this.consumer = config.getIndexingChain().getChain(this);
/*  282 */     this.config = config;
/*      */   }
/*      */ 
/*      */   synchronized void deleteDocID(int docIDUpto)
/*      */   {
/*  288 */     this.pendingDeletes.addDocID(docIDUpto);
/*      */   }
/*      */ 
/*      */   boolean deleteQueries(Query[] queries)
/*      */   {
/*  301 */     boolean doFlush = this.flushControl.waitUpdate(0, queries.length);
/*  302 */     synchronized (this) {
/*  303 */       for (Query query : queries) {
/*  304 */         this.pendingDeletes.addQuery(query, this.numDocs);
/*      */       }
/*      */     }
/*  307 */     return doFlush;
/*      */   }
/*      */ 
/*      */   boolean deleteQuery(Query query) {
/*  311 */     boolean doFlush = this.flushControl.waitUpdate(0, 1);
/*  312 */     synchronized (this) {
/*  313 */       this.pendingDeletes.addQuery(query, this.numDocs);
/*      */     }
/*  315 */     return doFlush;
/*      */   }
/*      */ 
/*      */   boolean deleteTerms(Term[] terms) {
/*  319 */     boolean doFlush = this.flushControl.waitUpdate(0, terms.length);
/*  320 */     synchronized (this) {
/*  321 */       for (Term term : terms) {
/*  322 */         this.pendingDeletes.addTerm(term, this.numDocs);
/*      */       }
/*      */     }
/*  325 */     return doFlush;
/*      */   }
/*      */ 
/*      */   boolean deleteTerm(Term term, boolean skipWait)
/*      */   {
/*  332 */     boolean doFlush = this.flushControl.waitUpdate(0, 1, skipWait);
/*  333 */     synchronized (this) {
/*  334 */       this.pendingDeletes.addTerm(term, this.numDocs);
/*      */     }
/*  336 */     return doFlush;
/*      */   }
/*      */ 
/*      */   public FieldInfos getFieldInfos() {
/*  340 */     return this.fieldInfos;
/*      */   }
/*      */ 
/*      */   synchronized void setInfoStream(PrintStream infoStream)
/*      */   {
/*  346 */     this.infoStream = infoStream;
/*  347 */     for (int i = 0; i < this.threadStates.length; i++)
/*  348 */       this.threadStates[i].docState.infoStream = infoStream;
/*      */   }
/*      */ 
/*      */   synchronized void setMaxFieldLength(int maxFieldLength)
/*      */   {
/*  353 */     this.maxFieldLength = maxFieldLength;
/*  354 */     for (int i = 0; i < this.threadStates.length; i++)
/*  355 */       this.threadStates[i].docState.maxFieldLength = maxFieldLength;
/*      */   }
/*      */ 
/*      */   synchronized void setSimilarity(Similarity similarity)
/*      */   {
/*  360 */     this.similarity = similarity;
/*  361 */     for (int i = 0; i < this.threadStates.length; i++)
/*  362 */       this.threadStates[i].docState.similarity = similarity;
/*      */   }
/*      */ 
/*      */   synchronized String getSegment()
/*      */   {
/*  368 */     return this.segment;
/*      */   }
/*      */ 
/*      */   synchronized int getNumDocs()
/*      */   {
/*  373 */     return this.numDocs;
/*      */   }
/*      */ 
/*      */   void message(String message) {
/*  377 */     if (this.infoStream != null)
/*  378 */       this.writer.message("DW: " + message);
/*      */   }
/*      */ 
/*      */   synchronized void setAborting()
/*      */   {
/*  383 */     if (this.infoStream != null) {
/*  384 */       message("setAborting");
/*      */     }
/*  386 */     this.aborting = true;
/*      */   }
/*      */ 
/*      */   synchronized void abort()
/*      */     throws IOException
/*      */   {
/*  394 */     if (this.infoStream != null) {
/*  395 */       message("docWriter: abort");
/*      */     }
/*      */ 
/*  398 */     boolean success = false;
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/*  404 */         this.waitQueue.abort();
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */       }
/*      */       try
/*      */       {
/*  411 */         waitIdle();
/*      */       } finally {
/*  413 */         if (this.infoStream != null) {
/*  414 */           message("docWriter: abort waitIdle done");
/*      */         }
/*      */ 
/*  417 */         assert (0 == this.waitQueue.numWaiting) : ("waitQueue.numWaiting=" + this.waitQueue.numWaiting);
/*  418 */         this.waitQueue.waitingBytes = 0L;
/*      */ 
/*  420 */         this.pendingDeletes.clear();
/*      */ 
/*  422 */         for (DocumentsWriterThreadState threadState : this.threadStates)
/*      */           try {
/*  424 */             threadState.consumer.abort();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/*      */         try {
/*  430 */           this.consumer.abort();
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*  435 */         doAfterFlush();
/*      */       }
/*      */ 
/*  438 */       success = true;
/*      */     } finally {
/*  440 */       this.aborting = false;
/*  441 */       notifyAll();
/*  442 */       if (this.infoStream != null)
/*  443 */         message("docWriter: done abort; success=" + success);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doAfterFlush()
/*      */     throws IOException
/*      */   {
/*  451 */     assert (allThreadsIdle());
/*  452 */     this.threadBindings.clear();
/*  453 */     this.waitQueue.reset();
/*  454 */     this.segment = null;
/*  455 */     this.numDocs = 0;
/*  456 */     this.nextDocID = 0;
/*  457 */     this.bufferIsFull = false;
/*  458 */     for (int i = 0; i < this.threadStates.length; i++)
/*  459 */       this.threadStates[i].doAfterFlush();
/*      */   }
/*      */ 
/*      */   private synchronized boolean allThreadsIdle()
/*      */   {
/*  464 */     for (int i = 0; i < this.threadStates.length; i++) {
/*  465 */       if (!this.threadStates[i].isIdle) {
/*  466 */         return false;
/*      */       }
/*      */     }
/*  469 */     return true;
/*      */   }
/*      */ 
/*      */   synchronized boolean anyChanges() {
/*  473 */     return (this.numDocs != 0) || (this.pendingDeletes.any());
/*      */   }
/*      */ 
/*      */   public BufferedDeletes getPendingDeletes()
/*      */   {
/*  478 */     return this.pendingDeletes;
/*      */   }
/*      */ 
/*      */   private void pushDeletes(SegmentInfo newSegment, SegmentInfos segmentInfos)
/*      */   {
/*  483 */     long delGen = this.bufferedDeletesStream.getNextGen();
/*  484 */     if (this.pendingDeletes.any()) {
/*  485 */       if ((segmentInfos.size() > 0) || (newSegment != null)) {
/*  486 */         FrozenBufferedDeletes packet = new FrozenBufferedDeletes(this.pendingDeletes, delGen);
/*  487 */         if (this.infoStream != null) {
/*  488 */           message("flush: push buffered deletes startSize=" + this.pendingDeletes.bytesUsed.get() + " frozenSize=" + packet.bytesUsed);
/*      */         }
/*  490 */         this.bufferedDeletesStream.push(packet);
/*  491 */         if (this.infoStream != null) {
/*  492 */           message("flush: delGen=" + packet.gen);
/*      */         }
/*  494 */         if (newSegment != null) {
/*  495 */           newSegment.setBufferedDeletesGen(packet.gen);
/*      */         }
/*      */       }
/*  498 */       else if (this.infoStream != null) {
/*  499 */         message("flush: drop buffered deletes: no segments");
/*      */       }
/*      */ 
/*  505 */       this.pendingDeletes.clear();
/*  506 */     } else if (newSegment != null) {
/*  507 */       newSegment.setBufferedDeletesGen(delGen);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean anyDeletions() {
/*  512 */     return this.pendingDeletes.any();
/*      */   }
/*      */ 
/*      */   synchronized SegmentInfo flush(IndexWriter writer, IndexFileDeleter deleter, MergePolicy mergePolicy, SegmentInfos segmentInfos)
/*      */     throws IOException
/*      */   {
/*  519 */     long startTime = System.currentTimeMillis();
/*      */ 
/*  522 */     assert (Thread.holdsLock(writer));
/*      */ 
/*  524 */     waitIdle();
/*      */ 
/*  526 */     if (this.numDocs == 0)
/*      */     {
/*  528 */       if (this.infoStream != null) {
/*  529 */         message("flush: no docs; skipping");
/*      */       }
/*      */ 
/*  532 */       pushDeletes(null, segmentInfos);
/*  533 */       return null;
/*      */     }
/*      */ 
/*  536 */     if (this.aborting) {
/*  537 */       if (this.infoStream != null) {
/*  538 */         message("flush: skip because aborting is set");
/*      */       }
/*  540 */       return null;
/*      */     }
/*      */ 
/*  543 */     boolean success = false;
/*      */     SegmentInfo newSegment;
/*      */     try
/*      */     {
/*  549 */       assert (this.nextDocID == this.numDocs) : ("nextDocID=" + this.nextDocID + " numDocs=" + this.numDocs);
/*  550 */       assert (this.waitQueue.numWaiting == 0) : ("numWaiting=" + this.waitQueue.numWaiting);
/*  551 */       assert (this.waitQueue.waitingBytes == 0L);
/*      */ 
/*  553 */       if (this.infoStream != null) {
/*  554 */         message("flush postings as segment " + this.segment + " numDocs=" + this.numDocs);
/*      */       }
/*      */ 
/*  557 */       SegmentWriteState flushState = new SegmentWriteState(this.infoStream, this.directory, this.segment, this.fieldInfos, this.numDocs, writer.getConfig().getTermIndexInterval(), this.pendingDeletes);
/*      */ 
/*  563 */       if (this.pendingDeletes.docIDs.size() > 0) {
/*  564 */         flushState.deletedDocs = new BitVector(this.numDocs);
/*  565 */         for (Iterator i$ = this.pendingDeletes.docIDs.iterator(); i$.hasNext(); ) { int delDocID = ((Integer)i$.next()).intValue();
/*  566 */           flushState.deletedDocs.set(delDocID);
/*      */         }
/*  568 */         this.pendingDeletes.bytesUsed.addAndGet(-this.pendingDeletes.docIDs.size() * BufferedDeletes.BYTES_PER_DEL_DOCID);
/*  569 */         this.pendingDeletes.docIDs.clear();
/*      */       }
/*      */ 
/*  572 */       newSegment = new SegmentInfo(this.segment, this.numDocs, this.directory, false, true, this.fieldInfos.hasProx(), false);
/*      */ 
/*  574 */       Collection threads = new HashSet();
/*  575 */       for (DocumentsWriterThreadState threadState : this.threadStates) {
/*  576 */         threads.add(threadState.consumer);
/*      */       }
/*      */ 
/*  579 */       double startMBUsed = bytesUsed() / 1024.0D / 1024.0D;
/*      */ 
/*  581 */       this.consumer.flush(threads, flushState);
/*      */ 
/*  583 */       newSegment.setHasVectors(flushState.hasVectors);
/*      */ 
/*  585 */       if (this.infoStream != null) {
/*  586 */         message("new segment has " + (flushState.hasVectors ? "vectors" : "no vectors"));
/*  587 */         if (flushState.deletedDocs != null) {
/*  588 */           message("new segment has " + flushState.deletedDocs.count() + " deleted docs");
/*      */         }
/*  590 */         message("flushedFiles=" + newSegment.files());
/*      */       }
/*      */ 
/*  593 */       if (mergePolicy.useCompoundFile(segmentInfos, newSegment)) {
/*  594 */         String cfsFileName = IndexFileNames.segmentFileName(this.segment, "cfs");
/*      */ 
/*  596 */         if (this.infoStream != null) {
/*  597 */           message("flush: create compound file \"" + cfsFileName + "\"");
/*      */         }
/*      */ 
/*  600 */         CompoundFileWriter cfsWriter = new CompoundFileWriter(this.directory, cfsFileName);
/*  601 */         for (String fileName : newSegment.files()) {
/*  602 */           cfsWriter.addFile(fileName);
/*      */         }
/*  604 */         cfsWriter.close();
/*  605 */         deleter.deleteNewFiles(newSegment.files());
/*  606 */         newSegment.setUseCompoundFile(true);
/*      */       }
/*      */ 
/*  611 */       if (flushState.deletedDocs != null) {
/*  612 */         int delCount = flushState.deletedDocs.count();
/*  613 */         assert (delCount > 0);
/*  614 */         newSegment.setDelCount(delCount);
/*  615 */         newSegment.advanceDelGen();
/*  616 */         String delFileName = newSegment.getDelFileName();
/*  617 */         if (this.infoStream != null) {
/*  618 */           message("flush: write " + delCount + " deletes to " + delFileName);
/*      */         }
/*  620 */         boolean success2 = false;
/*      */         try
/*      */         {
/*  627 */           flushState.deletedDocs.write(this.directory, delFileName);
/*  628 */           success2 = true;
/*      */         } finally {
/*  630 */           if (!success2) {
/*      */             try {
/*  632 */               this.directory.deleteFile(delFileName);
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  641 */       if (this.infoStream != null) {
/*  642 */         message("flush: segment=" + newSegment);
/*  643 */         double newSegmentSizeNoStore = newSegment.sizeInBytes(false) / 1024.0D / 1024.0D;
/*  644 */         double newSegmentSize = newSegment.sizeInBytes(true) / 1024.0D / 1024.0D;
/*  645 */         message("  ramUsed=" + this.nf.format(startMBUsed) + " MB" + " newFlushedSize=" + this.nf.format(newSegmentSize) + " MB" + " (" + this.nf.format(newSegmentSizeNoStore) + " MB w/o doc stores)" + " docs/MB=" + this.nf.format(this.numDocs / newSegmentSize) + " new/old=" + this.nf.format(100.0D * newSegmentSizeNoStore / startMBUsed) + "%");
/*      */       }
/*      */ 
/*  652 */       success = true;
/*      */     } finally {
/*  654 */       notifyAll();
/*  655 */       if (!success) {
/*  656 */         if (this.segment != null) {
/*  657 */           deleter.refresh(this.segment);
/*      */         }
/*  659 */         abort();
/*      */       }
/*      */     }
/*      */ 
/*  663 */     doAfterFlush();
/*      */ 
/*  666 */     pushDeletes(newSegment, segmentInfos);
/*  667 */     if (this.infoStream != null) {
/*  668 */       message("flush time " + (System.currentTimeMillis() - startTime) + " msec");
/*      */     }
/*      */ 
/*  671 */     return newSegment;
/*      */   }
/*      */ 
/*      */   synchronized void close() {
/*  675 */     this.closed = true;
/*  676 */     notifyAll();
/*      */   }
/*      */ 
/*      */   synchronized DocumentsWriterThreadState getThreadState(Term delTerm, int docCount)
/*      */     throws IOException
/*      */   {
/*  686 */     Thread currentThread = Thread.currentThread();
/*  687 */     assert (!Thread.holdsLock(this.writer));
/*      */ 
/*  692 */     DocumentsWriterThreadState state = (DocumentsWriterThreadState)this.threadBindings.get(currentThread);
/*  693 */     if (state == null)
/*      */     {
/*  697 */       DocumentsWriterThreadState minThreadState = null;
/*  698 */       for (int i = 0; i < this.threadStates.length; i++) {
/*  699 */         DocumentsWriterThreadState ts = this.threadStates[i];
/*  700 */         if ((minThreadState == null) || (ts.numThreads < minThreadState.numThreads)) {
/*  701 */           minThreadState = ts;
/*      */         }
/*      */       }
/*  704 */       if ((minThreadState != null) && ((minThreadState.numThreads == 0) || (this.threadStates.length >= this.maxThreadStates))) {
/*  705 */         state = minThreadState;
/*  706 */         state.numThreads += 1;
/*      */       }
/*      */       else {
/*  709 */         DocumentsWriterThreadState[] newArray = new DocumentsWriterThreadState[1 + this.threadStates.length];
/*  710 */         if (this.threadStates.length > 0) {
/*  711 */           System.arraycopy(this.threadStates, 0, newArray, 0, this.threadStates.length);
/*      */         }
/*  713 */         state = newArray[this.threadStates.length] =  = new DocumentsWriterThreadState(this);
/*  714 */         this.threadStates = newArray;
/*      */       }
/*  716 */       this.threadBindings.put(currentThread, state);
/*      */     }
/*      */ 
/*  722 */     waitReady(state);
/*      */ 
/*  726 */     if (this.segment == null) {
/*  727 */       this.segment = this.writer.newSegmentName();
/*  728 */       assert (this.numDocs == 0);
/*      */     }
/*      */ 
/*  731 */     state.docState.docID = this.nextDocID;
/*  732 */     this.nextDocID += docCount;
/*      */ 
/*  734 */     if (delTerm != null) {
/*  735 */       this.pendingDeletes.addTerm(delTerm, state.docState.docID);
/*      */     }
/*      */ 
/*  738 */     this.numDocs += docCount;
/*  739 */     state.isIdle = false;
/*  740 */     return state;
/*      */   }
/*      */ 
/*      */   boolean addDocument(Document doc, Analyzer analyzer) throws CorruptIndexException, IOException {
/*  744 */     return updateDocument(doc, analyzer, null);
/*      */   }
/*      */ 
/*      */   boolean updateDocument(Document doc, Analyzer analyzer, Term delTerm)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  751 */     boolean doFlush = this.flushControl.waitUpdate(1, delTerm != null ? 1 : 0);
/*      */ 
/*  754 */     DocumentsWriterThreadState state = getThreadState(delTerm, 1);
/*      */ 
/*  756 */     DocState docState = state.docState;
/*  757 */     docState.doc = doc;
/*  758 */     docState.analyzer = analyzer;
/*      */ 
/*  760 */     boolean success = false;
/*      */     try
/*      */     {
/*      */       DocWriter perDoc;
/*      */       try {
/*  766 */         perDoc = state.consumer.processDocument();
/*      */       } finally {
/*  768 */         docState.clear();
/*      */       }
/*      */ 
/*  772 */       finishDocument(state, perDoc);
/*      */ 
/*  774 */       success = true;
/*      */     } finally {
/*  776 */       if (success) {
/*      */         break label276;
/*      */       }
/*  780 */       if (doFlush) {
/*  781 */         this.flushControl.clearFlushPending();
/*      */       }
/*      */ 
/*  784 */       if (this.infoStream == null);
/*      */     }
/*      */ 
/*  788 */     synchronized (this)
/*      */     {
/*  790 */       state.isIdle = true;
/*  791 */       notifyAll();
/*      */ 
/*  793 */       if (this.aborting) {
/*  794 */         abort();
/*      */       } else {
/*  796 */         this.skipDocWriter.docID = docState.docID;
/*  797 */         boolean success2 = false;
/*      */         try {
/*  799 */           this.waitQueue.add(this.skipDocWriter);
/*  800 */           success2 = true;
/*      */         } finally {
/*  802 */           if (!success2) {
/*  803 */             abort();
/*  804 */             return false;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  812 */         deleteDocID(state.docState.docID);
/*      */       }
/*      */     }
/*  814 */     label276: ret;
/*      */ 
/*  818 */     doFlush |= this.flushControl.flushByRAMUsage("new document");
/*      */ 
/*  820 */     return doFlush; } 
/*      */   // ERROR //
/*      */   boolean updateDocuments(Collection<Document> docs, Analyzer analyzer, Term delTerm) throws CorruptIndexException, IOException { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 44	org/apache/lucene/index/DocumentsWriter:flushControl	Lorg/apache/lucene/index/IndexWriter$FlushControl;
/*      */     //   4: aload_1
/*      */     //   5: invokeinterface 220 1 0
/*      */     //   10: aload_3
/*      */     //   11: ifnull +7 -> 18
/*      */     //   14: iconst_1
/*      */     //   15: goto +4 -> 19
/*      */     //   18: iconst_0
/*      */     //   19: invokevirtual 49	org/apache/lucene/index/IndexWriter$FlushControl:waitUpdate	(II)Z
/*      */     //   22: istore 4
/*      */     //   24: aload_1
/*      */     //   25: invokeinterface 220 1 0
/*      */     //   30: istore 5
/*      */     //   32: aload_0
/*      */     //   33: aconst_null
/*      */     //   34: iload 5
/*      */     //   36: invokevirtual 207	org/apache/lucene/index/DocumentsWriter:getThreadState	(Lorg/apache/lucene/index/Term;I)Lorg/apache/lucene/index/DocumentsWriterThreadState;
/*      */     //   39: astore 6
/*      */     //   41: aload 6
/*      */     //   43: getfield 55	org/apache/lucene/index/DocumentsWriterThreadState:docState	Lorg/apache/lucene/index/DocumentsWriter$DocState;
/*      */     //   46: astore 7
/*      */     //   48: aload 7
/*      */     //   50: getfield 205	org/apache/lucene/index/DocumentsWriter$DocState:docID	I
/*      */     //   53: istore 8
/*      */     //   55: iload 8
/*      */     //   57: istore 9
/*      */     //   59: aload_1
/*      */     //   60: invokeinterface 221 1 0
/*      */     //   65: astore 10
/*      */     //   67: aload 10
/*      */     //   69: invokeinterface 132 1 0
/*      */     //   74: ifeq +426 -> 500
/*      */     //   77: aload 10
/*      */     //   79: invokeinterface 133 1 0
/*      */     //   84: checkcast 222	org/apache/lucene/document/Document
/*      */     //   87: astore 11
/*      */     //   89: aload 7
/*      */     //   91: aload 11
/*      */     //   93: putfield 208	org/apache/lucene/index/DocumentsWriter$DocState:doc	Lorg/apache/lucene/document/Document;
/*      */     //   96: aload 7
/*      */     //   98: aload_2
/*      */     //   99: putfield 209	org/apache/lucene/index/DocumentsWriter$DocState:analyzer	Lorg/apache/lucene/analysis/Analyzer;
/*      */     //   102: aload 7
/*      */     //   104: iload 9
/*      */     //   106: iinc 9 1
/*      */     //   109: putfield 205	org/apache/lucene/index/DocumentsWriter$DocState:docID	I
/*      */     //   112: iconst_0
/*      */     //   113: istore 12
/*      */     //   115: aload 6
/*      */     //   117: getfield 82	org/apache/lucene/index/DocumentsWriterThreadState:consumer	Lorg/apache/lucene/index/DocConsumerPerThread;
/*      */     //   120: invokevirtual 210	org/apache/lucene/index/DocConsumerPerThread:processDocument	()Lorg/apache/lucene/index/DocumentsWriter$DocWriter;
/*      */     //   123: astore 13
/*      */     //   125: jsr +14 -> 139
/*      */     //   128: goto +20 -> 148
/*      */     //   131: astore 14
/*      */     //   133: jsr +6 -> 139
/*      */     //   136: aload 14
/*      */     //   138: athrow
/*      */     //   139: astore 15
/*      */     //   141: aload 7
/*      */     //   143: invokevirtual 211	org/apache/lucene/index/DocumentsWriter$DocState:clear	()V
/*      */     //   146: ret 15
/*      */     //   148: aload_0
/*      */     //   149: invokevirtual 223	org/apache/lucene/index/DocumentsWriter:balanceRAM	()V
/*      */     //   152: aload_0
/*      */     //   153: dup
/*      */     //   154: astore 14
/*      */     //   156: monitorenter
/*      */     //   157: aload_0
/*      */     //   158: getfield 68	org/apache/lucene/index/DocumentsWriter:aborting	Z
/*      */     //   161: ifeq +12 -> 173
/*      */     //   164: aload 14
/*      */     //   166: monitorexit
/*      */     //   167: jsr +111 -> 278
/*      */     //   170: goto +330 -> 500
/*      */     //   173: getstatic 74	org/apache/lucene/index/DocumentsWriter:$assertionsDisabled	Z
/*      */     //   176: ifne +29 -> 205
/*      */     //   179: aload 13
/*      */     //   181: ifnull +24 -> 205
/*      */     //   184: aload 13
/*      */     //   186: getfield 224	org/apache/lucene/index/DocumentsWriter$DocWriter:docID	I
/*      */     //   189: aload 7
/*      */     //   191: getfield 205	org/apache/lucene/index/DocumentsWriter$DocState:docID	I
/*      */     //   194: if_icmpeq +11 -> 205
/*      */     //   197: new 76	java/lang/AssertionError
/*      */     //   200: dup
/*      */     //   201: invokespecial 90	java/lang/AssertionError:<init>	()V
/*      */     //   204: athrow
/*      */     //   205: aload 13
/*      */     //   207: ifnull +16 -> 223
/*      */     //   210: aload_0
/*      */     //   211: getfield 34	org/apache/lucene/index/DocumentsWriter:waitQueue	Lorg/apache/lucene/index/DocumentsWriter$WaitQueue;
/*      */     //   214: aload 13
/*      */     //   216: invokevirtual 216	org/apache/lucene/index/DocumentsWriter$WaitQueue:add	(Lorg/apache/lucene/index/DocumentsWriter$DocWriter;)Z
/*      */     //   219: pop
/*      */     //   220: goto +27 -> 247
/*      */     //   223: aload_0
/*      */     //   224: getfield 20	org/apache/lucene/index/DocumentsWriter:skipDocWriter	Lorg/apache/lucene/index/DocumentsWriter$SkipDocWriter;
/*      */     //   227: aload 7
/*      */     //   229: getfield 205	org/apache/lucene/index/DocumentsWriter$DocState:docID	I
/*      */     //   232: putfield 215	org/apache/lucene/index/DocumentsWriter$SkipDocWriter:docID	I
/*      */     //   235: aload_0
/*      */     //   236: getfield 34	org/apache/lucene/index/DocumentsWriter:waitQueue	Lorg/apache/lucene/index/DocumentsWriter$WaitQueue;
/*      */     //   239: aload_0
/*      */     //   240: getfield 20	org/apache/lucene/index/DocumentsWriter:skipDocWriter	Lorg/apache/lucene/index/DocumentsWriter$SkipDocWriter;
/*      */     //   243: invokevirtual 216	org/apache/lucene/index/DocumentsWriter$WaitQueue:add	(Lorg/apache/lucene/index/DocumentsWriter$DocWriter;)Z
/*      */     //   246: pop
/*      */     //   247: aload 14
/*      */     //   249: monitorexit
/*      */     //   250: goto +11 -> 261
/*      */     //   253: astore 16
/*      */     //   255: aload 14
/*      */     //   257: monitorexit
/*      */     //   258: aload 16
/*      */     //   260: athrow
/*      */     //   261: iconst_1
/*      */     //   262: istore 12
/*      */     //   264: jsr +14 -> 278
/*      */     //   267: goto +230 -> 497
/*      */     //   270: astore 17
/*      */     //   272: jsr +6 -> 278
/*      */     //   275: aload 17
/*      */     //   277: athrow
/*      */     //   278: astore 18
/*      */     //   280: iload 12
/*      */     //   282: ifne +213 -> 495
/*      */     //   285: iload 4
/*      */     //   287: ifeq +16 -> 303
/*      */     //   290: aload_0
/*      */     //   291: ldc 225
/*      */     //   293: invokevirtual 67	org/apache/lucene/index/DocumentsWriter:message	(Ljava/lang/String;)V
/*      */     //   296: aload_0
/*      */     //   297: getfield 44	org/apache/lucene/index/DocumentsWriter:flushControl	Lorg/apache/lucene/index/IndexWriter$FlushControl;
/*      */     //   300: invokevirtual 213	org/apache/lucene/index/IndexWriter$FlushControl:clearFlushPending	()V
/*      */     //   303: aload_0
/*      */     //   304: getfield 54	org/apache/lucene/index/DocumentsWriter:infoStream	Ljava/io/PrintStream;
/*      */     //   307: ifnull +29 -> 336
/*      */     //   310: aload_0
/*      */     //   311: new 60	java/lang/StringBuilder
/*      */     //   314: dup
/*      */     //   315: invokespecial 61	java/lang/StringBuilder:<init>	()V
/*      */     //   318: ldc 226
/*      */     //   320: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   323: aload_0
/*      */     //   324: getfield 68	org/apache/lucene/index/DocumentsWriter:aborting	Z
/*      */     //   327: invokevirtual 88	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   330: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   333: invokevirtual 67	org/apache/lucene/index/DocumentsWriter:message	(Ljava/lang/String;)V
/*      */     //   336: aload_0
/*      */     //   337: dup
/*      */     //   338: astore 19
/*      */     //   340: monitorenter
/*      */     //   341: aload 6
/*      */     //   343: iconst_1
/*      */     //   344: putfield 96	org/apache/lucene/index/DocumentsWriterThreadState:isIdle	Z
/*      */     //   347: aload_0
/*      */     //   348: invokevirtual 86	java/lang/Object:notifyAll	()V
/*      */     //   351: aload_0
/*      */     //   352: getfield 68	org/apache/lucene/index/DocumentsWriter:aborting	Z
/*      */     //   355: ifeq +10 -> 365
/*      */     //   358: aload_0
/*      */     //   359: invokevirtual 193	org/apache/lucene/index/DocumentsWriter:abort	()V
/*      */     //   362: goto +119 -> 481
/*      */     //   365: iload 8
/*      */     //   367: iload 5
/*      */     //   369: iadd
/*      */     //   370: istore 20
/*      */     //   372: aload 7
/*      */     //   374: getfield 205	org/apache/lucene/index/DocumentsWriter$DocState:docID	I
/*      */     //   377: istore 9
/*      */     //   379: iload 9
/*      */     //   381: iload 20
/*      */     //   383: if_icmpge +68 -> 451
/*      */     //   386: aload_0
/*      */     //   387: getfield 20	org/apache/lucene/index/DocumentsWriter:skipDocWriter	Lorg/apache/lucene/index/DocumentsWriter$SkipDocWriter;
/*      */     //   390: iload 9
/*      */     //   392: iinc 9 1
/*      */     //   395: putfield 215	org/apache/lucene/index/DocumentsWriter$SkipDocWriter:docID	I
/*      */     //   398: iconst_0
/*      */     //   399: istore 21
/*      */     //   401: aload_0
/*      */     //   402: getfield 34	org/apache/lucene/index/DocumentsWriter:waitQueue	Lorg/apache/lucene/index/DocumentsWriter$WaitQueue;
/*      */     //   405: aload_0
/*      */     //   406: getfield 20	org/apache/lucene/index/DocumentsWriter:skipDocWriter	Lorg/apache/lucene/index/DocumentsWriter$SkipDocWriter;
/*      */     //   409: invokevirtual 216	org/apache/lucene/index/DocumentsWriter$WaitQueue:add	(Lorg/apache/lucene/index/DocumentsWriter$DocWriter;)Z
/*      */     //   412: pop
/*      */     //   413: iconst_1
/*      */     //   414: istore 21
/*      */     //   416: jsr +14 -> 430
/*      */     //   419: goto +29 -> 448
/*      */     //   422: astore 22
/*      */     //   424: jsr +6 -> 430
/*      */     //   427: aload 22
/*      */     //   429: athrow
/*      */     //   430: astore 23
/*      */     //   432: iload 21
/*      */     //   434: ifne +12 -> 446
/*      */     //   437: aload_0
/*      */     //   438: invokevirtual 193	org/apache/lucene/index/DocumentsWriter:abort	()V
/*      */     //   441: iconst_0
/*      */     //   442: aload 19
/*      */     //   444: monitorexit
/*      */     //   445: ireturn
/*      */     //   446: ret 23
/*      */     //   448: goto -69 -> 379
/*      */     //   451: iload 8
/*      */     //   453: istore 9
/*      */     //   455: iload 9
/*      */     //   457: iload 8
/*      */     //   459: aload_1
/*      */     //   460: invokeinterface 220 1 0
/*      */     //   465: iadd
/*      */     //   466: if_icmpge +15 -> 481
/*      */     //   469: aload_0
/*      */     //   470: iload 9
/*      */     //   472: iinc 9 1
/*      */     //   475: invokevirtual 217	org/apache/lucene/index/DocumentsWriter:deleteDocID	(I)V
/*      */     //   478: goto -23 -> 455
/*      */     //   481: aload 19
/*      */     //   483: monitorexit
/*      */     //   484: goto +11 -> 495
/*      */     //   487: astore 24
/*      */     //   489: aload 19
/*      */     //   491: monitorexit
/*      */     //   492: aload 24
/*      */     //   494: athrow
/*      */     //   495: ret 18
/*      */     //   497: goto -430 -> 67
/*      */     //   500: aload_0
/*      */     //   501: dup
/*      */     //   502: astore 10
/*      */     //   504: monitorenter
/*      */     //   505: aload_0
/*      */     //   506: getfield 34	org/apache/lucene/index/DocumentsWriter:waitQueue	Lorg/apache/lucene/index/DocumentsWriter$WaitQueue;
/*      */     //   509: invokevirtual 227	org/apache/lucene/index/DocumentsWriter$WaitQueue:doPause	()Z
/*      */     //   512: ifeq +7 -> 519
/*      */     //   515: aload_0
/*      */     //   516: invokevirtual 228	org/apache/lucene/index/DocumentsWriter:waitForWaitQueue	()V
/*      */     //   519: aload_0
/*      */     //   520: getfield 68	org/apache/lucene/index/DocumentsWriter:aborting	Z
/*      */     //   523: ifeq +40 -> 563
/*      */     //   526: aload 6
/*      */     //   528: iconst_1
/*      */     //   529: putfield 96	org/apache/lucene/index/DocumentsWriterThreadState:isIdle	Z
/*      */     //   532: aload_0
/*      */     //   533: invokevirtual 86	java/lang/Object:notifyAll	()V
/*      */     //   536: aload_0
/*      */     //   537: invokevirtual 193	org/apache/lucene/index/DocumentsWriter:abort	()V
/*      */     //   540: iload 4
/*      */     //   542: ifeq +16 -> 558
/*      */     //   545: aload_0
/*      */     //   546: ldc 225
/*      */     //   548: invokevirtual 67	org/apache/lucene/index/DocumentsWriter:message	(Ljava/lang/String;)V
/*      */     //   551: aload_0
/*      */     //   552: getfield 44	org/apache/lucene/index/DocumentsWriter:flushControl	Lorg/apache/lucene/index/IndexWriter$FlushControl;
/*      */     //   555: invokevirtual 213	org/apache/lucene/index/IndexWriter$FlushControl:clearFlushPending	()V
/*      */     //   558: iconst_0
/*      */     //   559: aload 10
/*      */     //   561: monitorexit
/*      */     //   562: ireturn
/*      */     //   563: aload_3
/*      */     //   564: ifnull +13 -> 577
/*      */     //   567: aload_0
/*      */     //   568: getfield 17	org/apache/lucene/index/DocumentsWriter:pendingDeletes	Lorg/apache/lucene/index/BufferedDeletes;
/*      */     //   571: aload_3
/*      */     //   572: iload 8
/*      */     //   574: invokevirtual 52	org/apache/lucene/index/BufferedDeletes:addTerm	(Lorg/apache/lucene/index/Term;I)V
/*      */     //   577: aload 6
/*      */     //   579: iconst_1
/*      */     //   580: putfield 96	org/apache/lucene/index/DocumentsWriterThreadState:isIdle	Z
/*      */     //   583: aload_0
/*      */     //   584: invokevirtual 86	java/lang/Object:notifyAll	()V
/*      */     //   587: aload 10
/*      */     //   589: monitorexit
/*      */     //   590: goto +11 -> 601
/*      */     //   593: astore 25
/*      */     //   595: aload 10
/*      */     //   597: monitorexit
/*      */     //   598: aload 25
/*      */     //   600: athrow
/*      */     //   601: iload 4
/*      */     //   603: aload_0
/*      */     //   604: getfield 44	org/apache/lucene/index/DocumentsWriter:flushControl	Lorg/apache/lucene/index/IndexWriter$FlushControl;
/*      */     //   607: ldc 218
/*      */     //   609: invokevirtual 219	org/apache/lucene/index/IndexWriter$FlushControl:flushByRAMUsage	(Ljava/lang/String;)Z
/*      */     //   612: ior
/*      */     //   613: istore 4
/*      */     //   615: iload 4
/*      */     //   617: ireturn
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   115	128	131	finally
/*      */     //   131	136	131	finally
/*      */     //   157	167	253	finally
/*      */     //   173	250	253	finally
/*      */     //   253	258	253	finally
/*      */     //   115	170	270	finally
/*      */     //   173	267	270	finally
/*      */     //   270	275	270	finally
/*      */     //   401	419	422	finally
/*      */     //   422	427	422	finally
/*      */     //   341	445	487	finally
/*      */     //   446	484	487	finally
/*      */     //   487	492	487	finally
/*      */     //   505	562	593	finally
/*      */     //   563	590	593	finally
/*      */     //   593	598	593	finally } 
/*  984 */   public synchronized void waitIdle() { while (!allThreadsIdle())
/*      */       try {
/*  986 */         wait();
/*      */       } catch (InterruptedException ie) {
/*  988 */         throw new ThreadInterruptedException(ie);
/*      */       }
/*      */   }
/*      */ 
/*      */   synchronized void waitReady(DocumentsWriterThreadState state)
/*      */   {
/*  994 */     while ((!this.closed) && ((!state.isIdle) || (this.aborting))) {
/*      */       try {
/*  996 */         wait();
/*      */       } catch (InterruptedException ie) {
/*  998 */         throw new ThreadInterruptedException(ie);
/*      */       }
/*      */     }
/*      */ 
/* 1002 */     if (this.closed)
/* 1003 */       throw new AlreadyClosedException("this IndexWriter is closed");
/*      */   }
/*      */ 
/*      */   private void finishDocument(DocumentsWriterThreadState perThread, DocWriter docWriter)
/*      */     throws IOException
/*      */   {
/* 1013 */     balanceRAM();
/*      */ 
/* 1015 */     synchronized (this)
/*      */     {
/* 1017 */       assert ((docWriter == null) || (docWriter.docID == perThread.docState.docID));
/*      */ 
/* 1019 */       if (this.aborting)
/*      */       {
/* 1025 */         if (docWriter != null)
/*      */           try {
/* 1027 */             docWriter.abort();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 1032 */         perThread.isIdle = true;
/*      */ 
/* 1035 */         notifyAll();
/*      */ 
/* 1037 */         return;
/*      */       }
/*      */       boolean doPause;
/*      */       boolean doPause;
/* 1042 */       if (docWriter != null) {
/* 1043 */         doPause = this.waitQueue.add(docWriter);
/*      */       } else {
/* 1045 */         this.skipDocWriter.docID = perThread.docState.docID;
/* 1046 */         doPause = this.waitQueue.add(this.skipDocWriter);
/*      */       }
/*      */ 
/* 1049 */       if (doPause) {
/* 1050 */         waitForWaitQueue();
/*      */       }
/*      */ 
/* 1053 */       perThread.isIdle = true;
/*      */ 
/* 1056 */       notifyAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   synchronized void waitForWaitQueue() {
/*      */     do
/*      */       try {
/* 1063 */         wait();
/*      */       } catch (InterruptedException ie) {
/* 1065 */         throw new ThreadInterruptedException(ie);
/*      */       }
/* 1067 */     while (!this.waitQueue.doResume());
/*      */   }
/*      */ 
/*      */   synchronized int[] getIntBlock()
/*      */   {
/* 1151 */     int size = this.freeIntBlocks.size();
/*      */     int[] b;
/* 1153 */     if (0 == size) {
/* 1154 */       int[] b = new int[8192];
/* 1155 */       this.bytesUsed.addAndGet(32768L);
/*      */     } else {
/* 1157 */       b = (int[])this.freeIntBlocks.remove(size - 1);
/*      */     }
/* 1159 */     return b;
/*      */   }
/*      */ 
/*      */   synchronized void bytesUsed(long numBytes) {
/* 1163 */     this.bytesUsed.addAndGet(numBytes);
/*      */   }
/*      */ 
/*      */   long bytesUsed() {
/* 1167 */     return this.bytesUsed.get() + this.pendingDeletes.bytesUsed.get();
/*      */   }
/*      */ 
/*      */   synchronized void recycleIntBlocks(int[][] blocks, int start, int end)
/*      */   {
/* 1172 */     for (int i = start; i < end; i++) {
/* 1173 */       this.freeIntBlocks.add(blocks[i]);
/* 1174 */       blocks[i] = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   synchronized char[] getCharBlock()
/*      */   {
/* 1197 */     int size = this.freeCharBlocks.size();
/*      */     char[] c;
/*      */     char[] c;
/* 1199 */     if (0 == size) {
/* 1200 */       this.bytesUsed.addAndGet(32768L);
/* 1201 */       c = new char[16384];
/*      */     } else {
/* 1203 */       c = (char[])this.freeCharBlocks.remove(size - 1);
/*      */     }
/*      */ 
/* 1208 */     return c;
/*      */   }
/*      */ 
/*      */   synchronized void recycleCharBlocks(char[][] blocks, int numBlocks)
/*      */   {
/* 1213 */     for (int i = 0; i < numBlocks; i++) {
/* 1214 */       this.freeCharBlocks.add(blocks[i]);
/* 1215 */       blocks[i] = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   String toMB(long v) {
/* 1220 */     return this.nf.format(v / 1024.0D / 1024.0D);
/*      */   }
/*      */ 
/*      */   void balanceRAM()
/*      */   {
/* 1241 */     long deletesRAMUsed = this.bufferedDeletesStream.bytesUsed();
/*      */ 
/* 1244 */     double mb = this.config.getRAMBufferSizeMB();
/*      */     long ramBufferSize;
/*      */     long ramBufferSize;
/* 1245 */     if (mb == -1.0D)
/* 1246 */       ramBufferSize = -1L;
/*      */     else
/* 1248 */       ramBufferSize = ()(mb * 1024.0D * 1024.0D);
/*      */     boolean doBalance;
/* 1251 */     synchronized (this) {
/* 1252 */       if ((ramBufferSize == -1L) || (this.bufferIsFull)) {
/* 1253 */         return;
/*      */       }
/*      */ 
/* 1256 */       doBalance = bytesUsed() + deletesRAMUsed >= ramBufferSize;
/*      */     }
/*      */ 
/* 1259 */     if (doBalance)
/*      */     {
/* 1261 */       if (this.infoStream != null) {
/* 1262 */         message("  RAM: balance allocations: usedMB=" + toMB(bytesUsed()) + " vs trigger=" + toMB(ramBufferSize) + " deletesMB=" + toMB(deletesRAMUsed) + " byteBlockFree=" + toMB(this.byteBlockAllocator.freeByteBlocks.size() * 32768) + " perDocFree=" + toMB(this.perDocAllocator.freeByteBlocks.size() * 1024) + " charBlockFree=" + toMB(this.freeCharBlocks.size() * 16384 * 2));
/*      */       }
/*      */ 
/* 1270 */       long startBytesUsed = bytesUsed() + deletesRAMUsed;
/*      */ 
/* 1272 */       int iter = 0;
/*      */ 
/* 1278 */       boolean any = true;
/*      */ 
/* 1280 */       long freeLevel = ()(0.95D * ramBufferSize);
/*      */ 
/* 1282 */       while (bytesUsed() + deletesRAMUsed > freeLevel)
/*      */       {
/* 1284 */         synchronized (this) {
/* 1285 */           if ((0 == this.perDocAllocator.freeByteBlocks.size()) && (0 == this.byteBlockAllocator.freeByteBlocks.size()) && (0 == this.freeCharBlocks.size()) && (0 == this.freeIntBlocks.size()) && (!any))
/*      */           {
/* 1291 */             this.bufferIsFull = (bytesUsed() + deletesRAMUsed > ramBufferSize);
/* 1292 */             if (this.infoStream != null) {
/* 1293 */               if (bytesUsed() + deletesRAMUsed > ramBufferSize)
/* 1294 */                 message("    nothing to free; set bufferIsFull");
/*      */               else {
/* 1296 */                 message("    nothing to free");
/*      */               }
/*      */             }
/* 1299 */             break;
/*      */           }
/*      */ 
/* 1302 */           if ((0 == iter % 5) && (this.byteBlockAllocator.freeByteBlocks.size() > 0)) {
/* 1303 */             this.byteBlockAllocator.freeByteBlocks.remove(this.byteBlockAllocator.freeByteBlocks.size() - 1);
/* 1304 */             this.bytesUsed.addAndGet(-32768L);
/*      */           }
/*      */ 
/* 1307 */           if ((1 == iter % 5) && (this.freeCharBlocks.size() > 0)) {
/* 1308 */             this.freeCharBlocks.remove(this.freeCharBlocks.size() - 1);
/* 1309 */             this.bytesUsed.addAndGet(-32768L);
/*      */           }
/*      */ 
/* 1312 */           if ((2 == iter % 5) && (this.freeIntBlocks.size() > 0)) {
/* 1313 */             this.freeIntBlocks.remove(this.freeIntBlocks.size() - 1);
/* 1314 */             this.bytesUsed.addAndGet(-32768L);
/*      */           }
/*      */ 
/* 1317 */           if ((3 == iter % 5) && (this.perDocAllocator.freeByteBlocks.size() > 0))
/*      */           {
/* 1319 */             for (int i = 0; i < 32; i++) {
/* 1320 */               this.perDocAllocator.freeByteBlocks.remove(this.perDocAllocator.freeByteBlocks.size() - 1);
/* 1321 */               this.bytesUsed.addAndGet(-1024L);
/* 1322 */               if (this.perDocAllocator.freeByteBlocks.size() == 0)
/*      */               {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1329 */         if ((4 == iter % 5) && (any))
/*      */         {
/* 1331 */           any = this.consumer.freeRAM();
/*      */         }
/*      */ 
/* 1334 */         iter++;
/*      */       }
/*      */ 
/* 1337 */       if (this.infoStream != null)
/* 1338 */         message("    after free: freedMB=" + this.nf.format((startBytesUsed - bytesUsed() - deletesRAMUsed) / 1024.0D / 1024.0D) + " usedMB=" + this.nf.format((bytesUsed() + deletesRAMUsed) / 1024.0D / 1024.0D));
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  224 */     defaultIndexingChain = new IndexingChain()
/*      */     {
/*      */       DocConsumer getChain(DocumentsWriter documentsWriter)
/*      */       {
/*  248 */         TermsHashConsumer termVectorsWriter = new TermVectorsTermsWriter(documentsWriter);
/*  249 */         TermsHashConsumer freqProxWriter = new FreqProxTermsWriter();
/*      */ 
/*  251 */         InvertedDocConsumer termsHash = new TermsHash(documentsWriter, true, freqProxWriter, new TermsHash(documentsWriter, false, termVectorsWriter, null));
/*      */ 
/*  253 */         NormsWriter normsWriter = new NormsWriter();
/*  254 */         DocInverter docInverter = new DocInverter(termsHash, normsWriter);
/*  255 */         return new DocFieldProcessor(documentsWriter, docInverter);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private class WaitQueue
/*      */   {
/*      */     DocumentsWriter.DocWriter[] waiting;
/*      */     int nextWriteDocID;
/*      */     int nextWriteLoc;
/*      */     int numWaiting;
/*      */     long waitingBytes;
/*      */ 
/*      */     public WaitQueue()
/*      */     {
/* 1353 */       this.waiting = new DocumentsWriter.DocWriter[10];
/*      */     }
/*      */ 
/*      */     synchronized void reset()
/*      */     {
/* 1358 */       assert (this.numWaiting == 0);
/* 1359 */       assert (this.waitingBytes == 0L);
/* 1360 */       this.nextWriteDocID = 0;
/*      */     }
/*      */ 
/*      */     synchronized boolean doResume() {
/* 1364 */       double mb = DocumentsWriter.this.config.getRAMBufferSizeMB();
/*      */       long waitQueueResumeBytes;
/*      */       long waitQueueResumeBytes;
/* 1366 */       if (mb == -1.0D)
/* 1367 */         waitQueueResumeBytes = 2097152L;
/*      */       else {
/* 1369 */         waitQueueResumeBytes = ()(mb * 1024.0D * 1024.0D * 0.05D);
/*      */       }
/* 1371 */       return this.waitingBytes <= waitQueueResumeBytes;
/*      */     }
/*      */ 
/*      */     synchronized boolean doPause() {
/* 1375 */       double mb = DocumentsWriter.this.config.getRAMBufferSizeMB();
/*      */       long waitQueuePauseBytes;
/*      */       long waitQueuePauseBytes;
/* 1377 */       if (mb == -1.0D)
/* 1378 */         waitQueuePauseBytes = 4194304L;
/*      */       else {
/* 1380 */         waitQueuePauseBytes = ()(mb * 1024.0D * 1024.0D * 0.1D);
/*      */       }
/* 1382 */       return this.waitingBytes > waitQueuePauseBytes;
/*      */     }
/*      */ 
/*      */     synchronized void abort() {
/* 1386 */       int count = 0;
/* 1387 */       for (int i = 0; i < this.waiting.length; i++) {
/* 1388 */         DocumentsWriter.DocWriter doc = this.waiting[i];
/* 1389 */         if (doc != null) {
/* 1390 */           doc.abort();
/* 1391 */           this.waiting[i] = null;
/* 1392 */           count++;
/*      */         }
/*      */       }
/* 1395 */       this.waitingBytes = 0L;
/* 1396 */       assert (count == this.numWaiting);
/* 1397 */       this.numWaiting = 0;
/*      */     }
/*      */ 
/*      */     private void writeDocument(DocumentsWriter.DocWriter doc) throws IOException {
/* 1401 */       assert ((doc == DocumentsWriter.this.skipDocWriter) || (this.nextWriteDocID == doc.docID));
/* 1402 */       boolean success = false;
/*      */       try {
/* 1404 */         doc.finish();
/* 1405 */         this.nextWriteDocID += 1;
/* 1406 */         this.nextWriteLoc += 1;
/* 1407 */         assert (this.nextWriteLoc <= this.waiting.length);
/* 1408 */         if (this.nextWriteLoc == this.waiting.length) {
/* 1409 */           this.nextWriteLoc = 0;
/*      */         }
/* 1411 */         success = true;
/*      */       } finally {
/* 1413 */         if (!success)
/* 1414 */           DocumentsWriter.this.setAborting();
/*      */       }
/*      */     }
/*      */ 
/*      */     public synchronized boolean add(DocumentsWriter.DocWriter doc)
/*      */       throws IOException
/*      */     {
/* 1421 */       assert (doc.docID >= this.nextWriteDocID);
/*      */ 
/* 1423 */       if (doc.docID == this.nextWriteDocID) {
/* 1424 */         writeDocument(doc);
/*      */         while (true) {
/* 1426 */           doc = this.waiting[this.nextWriteLoc];
/* 1427 */           if (doc == null) break;
/* 1428 */           this.numWaiting -= 1;
/* 1429 */           this.waiting[this.nextWriteLoc] = null;
/* 1430 */           this.waitingBytes -= doc.sizeInBytes();
/* 1431 */           writeDocument(doc);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1444 */       int gap = doc.docID - this.nextWriteDocID;
/* 1445 */       if (gap >= this.waiting.length)
/*      */       {
/* 1447 */         DocumentsWriter.DocWriter[] newArray = new DocumentsWriter.DocWriter[ArrayUtil.oversize(gap, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/* 1448 */         assert (this.nextWriteLoc >= 0);
/* 1449 */         System.arraycopy(this.waiting, this.nextWriteLoc, newArray, 0, this.waiting.length - this.nextWriteLoc);
/* 1450 */         System.arraycopy(this.waiting, 0, newArray, this.waiting.length - this.nextWriteLoc, this.nextWriteLoc);
/* 1451 */         this.nextWriteLoc = 0;
/* 1452 */         this.waiting = newArray;
/* 1453 */         gap = doc.docID - this.nextWriteDocID;
/*      */       }
/*      */ 
/* 1456 */       int loc = this.nextWriteLoc + gap;
/* 1457 */       if (loc >= this.waiting.length) {
/* 1458 */         loc -= this.waiting.length;
/*      */       }
/*      */ 
/* 1462 */       assert (loc < this.waiting.length);
/*      */ 
/* 1465 */       assert (this.waiting[loc] == null);
/* 1466 */       this.waiting[loc] = doc;
/* 1467 */       this.numWaiting += 1;
/* 1468 */       this.waitingBytes += doc.sizeInBytes();
/*      */ 
/* 1471 */       return doPause();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ByteBlockAllocator extends ByteBlockPool.Allocator
/*      */   {
/*      */     final int blockSize;
/* 1100 */     ArrayList<byte[]> freeByteBlocks = new ArrayList();
/*      */ 
/*      */     ByteBlockAllocator(int blockSize)
/*      */     {
/* 1097 */       this.blockSize = blockSize;
/*      */     }
/*      */ 
/*      */     byte[] getByteBlock()
/*      */     {
/* 1105 */       synchronized (DocumentsWriter.this) {
/* 1106 */         int size = this.freeByteBlocks.size();
/*      */         byte[] b;
/* 1108 */         if (0 == size) {
/* 1109 */           byte[] b = new byte[this.blockSize];
/* 1110 */           DocumentsWriter.this.bytesUsed.addAndGet(this.blockSize);
/*      */         } else {
/* 1112 */           b = (byte[])this.freeByteBlocks.remove(size - 1);
/* 1113 */         }return b;
/*      */       }
/*      */     }
/*      */ 
/*      */     void recycleByteBlocks(byte[][] blocks, int start, int end)
/*      */     {
/* 1121 */       synchronized (DocumentsWriter.this) {
/* 1122 */         for (int i = start; i < end; i++) {
/* 1123 */           this.freeByteBlocks.add(blocks[i]);
/* 1124 */           blocks[i] = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void recycleByteBlocks(List<byte[]> blocks)
/*      */     {
/* 1131 */       synchronized (DocumentsWriter.this) {
/* 1132 */         int size = blocks.size();
/* 1133 */         for (int i = 0; i < size; i++) {
/* 1134 */           this.freeByteBlocks.add(blocks.get(i));
/* 1135 */           blocks.set(i, null);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SkipDocWriter extends DocumentsWriter.DocWriter
/*      */   {
/*      */     void finish()
/*      */     {
/*      */     }
/*      */ 
/*      */     void abort()
/*      */     {
/*      */     }
/*      */ 
/*      */     long sizeInBytes()
/*      */     {
/* 1079 */       return 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class IndexingChain
/*      */   {
/*      */     abstract DocConsumer getChain(DocumentsWriter paramDocumentsWriter);
/*      */   }
/*      */ 
/*      */   class PerDocBuffer extends RAMFile
/*      */   {
/*      */     PerDocBuffer()
/*      */     {
/*      */     }
/*      */ 
/*      */     protected byte[] newBuffer(int size)
/*      */     {
/*  194 */       assert (size == 1024);
/*  195 */       return DocumentsWriter.this.perDocAllocator.getByteBlock();
/*      */     }
/*      */ 
/*      */     synchronized void recycle()
/*      */     {
/*  202 */       if (this.buffers.size() > 0) {
/*  203 */         setLength(0L);
/*      */ 
/*  206 */         DocumentsWriter.this.perDocAllocator.recycleByteBlocks(this.buffers);
/*  207 */         this.buffers.clear();
/*  208 */         this.sizeInBytes = 0L;
/*      */ 
/*  210 */         assert (numBuffers() == 0);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class DocWriter
/*      */   {
/*      */     DocWriter next;
/*      */     int docID;
/*      */ 
/*      */     abstract void finish()
/*      */       throws IOException;
/*      */ 
/*      */     abstract void abort();
/*      */ 
/*      */     abstract long sizeInBytes();
/*      */ 
/*      */     void setNext(DocWriter next)
/*      */     {
/*  173 */       this.next = next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class DocState
/*      */   {
/*      */     DocumentsWriter docWriter;
/*      */     Analyzer analyzer;
/*      */     int maxFieldLength;
/*      */     PrintStream infoStream;
/*      */     Similarity similarity;
/*      */     int docID;
/*      */     Document doc;
/*      */     String maxTermPrefix;
/*      */ 
/*      */     public boolean testPoint(String name)
/*      */     {
/*  151 */       return this.docWriter.writer.testPoint(name);
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/*  157 */       this.doc = null;
/*  158 */       this.analyzer = null;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocumentsWriter
 * JD-Core Version:    0.6.0
 */