/*      */ package org.apache.lucene.index;
/*      */ 
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import org.apache.lucene.document.Document;
/*      */ import org.apache.lucene.document.FieldSelector;
/*      */ import org.apache.lucene.search.Similarity;
/*      */ import org.apache.lucene.store.Directory;
/*      */ import org.apache.lucene.store.Lock;
/*      */ import org.apache.lucene.store.LockObtainFailedException;
/*      */ import org.apache.lucene.util.MapBackedSet;
/*      */ 
/*      */ class DirectoryReader extends IndexReader
/*      */   implements Cloneable
/*      */ {
/*      */   protected Directory directory;
/*      */   protected boolean readOnly;
/*      */   IndexWriter writer;
/*      */   private IndexDeletionPolicy deletionPolicy;
/*      */   private Lock writeLock;
/*      */   private final SegmentInfos segmentInfos;
/*      */   private boolean stale;
/*      */   private final int termInfosIndexDivisor;
/*      */   private boolean rollbackHasChanges;
/*      */   private SegmentReader[] subReaders;
/*      */   private int[] starts;
/*   61 */   private Map<String, byte[]> normsCache = new HashMap();
/*   62 */   private int maxDoc = 0;
/*   63 */   private int numDocs = -1;
/*   64 */   private boolean hasDeletions = false;
/*      */   private long maxIndexVersion;
/*      */   private final boolean applyAllDeletes;
/*      */ 
/*      */   static IndexReader open(Directory directory, IndexDeletionPolicy deletionPolicy, IndexCommit commit, boolean readOnly, int termInfosIndexDivisor)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*   75 */     return (IndexReader)new SegmentInfos.FindSegmentsFile(directory, readOnly, deletionPolicy, termInfosIndexDivisor)
/*      */     {
/*      */       protected Object doBody(String segmentFileName) throws CorruptIndexException, IOException {
/*   78 */         SegmentInfos infos = new SegmentInfos();
/*   79 */         infos.read(this.directory, segmentFileName);
/*   80 */         if (this.val$readOnly) {
/*   81 */           return new ReadOnlyDirectoryReader(this.directory, infos, this.val$deletionPolicy, this.val$termInfosIndexDivisor, null);
/*      */         }
/*   83 */         return new DirectoryReader(this.directory, infos, this.val$deletionPolicy, false, this.val$termInfosIndexDivisor, null);
/*      */       }
/*      */     }
/*   75 */     .run(commit);
/*      */   }
/*      */ 
/*      */   DirectoryReader(Directory directory, SegmentInfos sis, IndexDeletionPolicy deletionPolicy, boolean readOnly, int termInfosIndexDivisor, Collection<IndexReader.ReaderFinishedListener> readerFinishedListeners)
/*      */     throws IOException
/*      */   {
/*   91 */     this.directory = directory;
/*   92 */     this.readOnly = readOnly;
/*   93 */     this.segmentInfos = sis;
/*   94 */     this.deletionPolicy = deletionPolicy;
/*   95 */     this.termInfosIndexDivisor = termInfosIndexDivisor;
/*      */ 
/*   97 */     if (readerFinishedListeners == null)
/*   98 */       this.readerFinishedListeners = new MapBackedSet(new ConcurrentHashMap());
/*      */     else {
/*  100 */       this.readerFinishedListeners = readerFinishedListeners;
/*      */     }
/*  102 */     this.applyAllDeletes = false;
/*      */ 
/*  109 */     SegmentReader[] readers = new SegmentReader[sis.size()];
/*  110 */     for (int i = sis.size() - 1; i >= 0; i--) {
/*  111 */       boolean success = false;
/*      */       try {
/*  113 */         readers[i] = SegmentReader.get(readOnly, sis.info(i), termInfosIndexDivisor);
/*  114 */         readers[i].readerFinishedListeners = this.readerFinishedListeners;
/*  115 */         success = true;
/*      */       } finally {
/*  117 */         if (!success)
/*      */         {
/*  119 */           for (i++; i < sis.size(); i++) {
/*      */             try {
/*  121 */               readers[i].close();
/*      */             }
/*      */             catch (Throwable ignore)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  130 */     initialize(readers);
/*      */   }
/*      */ 
/*      */   DirectoryReader(IndexWriter writer, SegmentInfos infos, int termInfosIndexDivisor, boolean applyAllDeletes) throws IOException
/*      */   {
/*  135 */     this.directory = writer.getDirectory();
/*  136 */     this.readOnly = true;
/*  137 */     this.applyAllDeletes = applyAllDeletes;
/*      */ 
/*  139 */     this.termInfosIndexDivisor = termInfosIndexDivisor;
/*  140 */     this.readerFinishedListeners = writer.getReaderFinishedListeners();
/*      */ 
/*  145 */     int numSegments = infos.size();
/*      */ 
/*  147 */     List readers = new ArrayList();
/*  148 */     Directory dir = writer.getDirectory();
/*      */ 
/*  150 */     this.segmentInfos = ((SegmentInfos)infos.clone());
/*  151 */     int infosUpto = 0;
/*  152 */     for (int i = 0; i < numSegments; i++) {
/*  153 */       boolean success = false;
/*      */       try {
/*  155 */         SegmentInfo info = infos.info(i);
/*  156 */         assert (info.dir == dir);
/*  157 */         SegmentReader reader = writer.readerPool.getReadOnlyClone(info, true, termInfosIndexDivisor);
/*  158 */         if ((reader.numDocs() > 0) || (writer.getKeepFullyDeletedSegments())) {
/*  159 */           reader.readerFinishedListeners = this.readerFinishedListeners;
/*  160 */           readers.add(reader);
/*  161 */           infosUpto++;
/*      */         } else {
/*  163 */           reader.close();
/*  164 */           this.segmentInfos.remove(infosUpto);
/*      */         }
/*  166 */         success = true;
/*      */       } finally {
/*  168 */         if (!success)
/*      */         {
/*  170 */           for (SegmentReader reader : readers) {
/*      */             try {
/*  172 */               reader.close();
/*      */             }
/*      */             catch (Throwable ignore)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  181 */     this.writer = writer;
/*      */ 
/*  183 */     initialize((SegmentReader[])readers.toArray(new SegmentReader[readers.size()]));
/*      */   }
/*      */ 
/*      */   DirectoryReader(Directory directory, SegmentInfos infos, SegmentReader[] oldReaders, int[] oldStarts, Map<String, byte[]> oldNormsCache, boolean readOnly, boolean doClone, int termInfosIndexDivisor, Collection<IndexReader.ReaderFinishedListener> readerFinishedListeners)
/*      */     throws IOException
/*      */   {
/*  190 */     this.directory = directory;
/*  191 */     this.readOnly = readOnly;
/*  192 */     this.segmentInfos = infos;
/*  193 */     this.termInfosIndexDivisor = termInfosIndexDivisor;
/*  194 */     assert (readerFinishedListeners != null);
/*  195 */     this.readerFinishedListeners = readerFinishedListeners;
/*  196 */     this.applyAllDeletes = false;
/*      */ 
/*  200 */     Map segmentReaders = new HashMap();
/*      */ 
/*  202 */     if (oldReaders != null)
/*      */     {
/*  204 */       for (int i = 0; i < oldReaders.length; i++) {
/*  205 */         segmentReaders.put(oldReaders[i].getSegmentName(), Integer.valueOf(i));
/*      */       }
/*      */     }
/*      */ 
/*  209 */     SegmentReader[] newReaders = new SegmentReader[infos.size()];
/*      */ 
/*  213 */     boolean[] readerShared = new boolean[infos.size()];
/*      */ 
/*  215 */     for (int i = infos.size() - 1; i >= 0; i--)
/*      */     {
/*  217 */       Integer oldReaderIndex = (Integer)segmentReaders.get(infos.info(i).name);
/*  218 */       if (oldReaderIndex == null)
/*      */       {
/*  220 */         newReaders[i] = null;
/*      */       }
/*      */       else {
/*  223 */         newReaders[i] = oldReaders[oldReaderIndex.intValue()];
/*      */       }
/*      */ 
/*  226 */       boolean success = false;
/*      */       try
/*      */       {
/*      */         SegmentReader newReader;
/*  229 */         if ((newReaders[i] == null) || (infos.info(i).getUseCompoundFile() != newReaders[i].getSegmentInfo().getUseCompoundFile()))
/*      */         {
/*  232 */           assert (!doClone);
/*      */ 
/*  235 */           SegmentReader newReader = SegmentReader.get(readOnly, infos.info(i), termInfosIndexDivisor);
/*  236 */           newReader.readerFinishedListeners = readerFinishedListeners;
/*      */         } else {
/*  238 */           newReader = newReaders[i].reopenSegment(infos.info(i), doClone, readOnly);
/*  239 */           assert (newReader.readerFinishedListeners == readerFinishedListeners);
/*      */         }
/*  241 */         if (newReader == newReaders[i])
/*      */         {
/*  244 */           readerShared[i] = true;
/*  245 */           newReader.incRef();
/*      */         } else {
/*  247 */           readerShared[i] = false;
/*  248 */           newReaders[i] = newReader;
/*      */         }
/*  250 */         success = true;
/*      */       } finally {
/*  252 */         if (!success) {
/*  253 */           for (i++; i < infos.size(); i++) {
/*  254 */             if (newReaders[i] == null) continue;
/*      */             try {
/*  256 */               if (readerShared[i] == 0)
/*      */               {
/*  259 */                 newReaders[i].close();
/*      */               }
/*      */               else
/*      */               {
/*  263 */                 newReaders[i].decRef();
/*      */               }
/*      */             }
/*      */             catch (IOException ignore)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  275 */     initialize(newReaders);
/*      */ 
/*  278 */     if (oldNormsCache != null)
/*  279 */       for (Map.Entry entry : oldNormsCache.entrySet()) {
/*  280 */         String field = (String)entry.getKey();
/*  281 */         if (!hasNorms(field))
/*      */         {
/*      */           continue;
/*      */         }
/*  285 */         byte[] oldBytes = (byte[])entry.getValue();
/*      */ 
/*  287 */         byte[] bytes = new byte[maxDoc()];
/*      */ 
/*  289 */         for (int i = 0; i < this.subReaders.length; i++) {
/*  290 */           Integer oldReaderIndex = (Integer)segmentReaders.get(this.subReaders[i].getSegmentName());
/*      */ 
/*  293 */           if ((oldReaderIndex != null) && ((oldReaders[oldReaderIndex.intValue()] == this.subReaders[i]) || (oldReaders[oldReaderIndex.intValue()].norms.get(field) == this.subReaders[i].norms.get(field))))
/*      */           {
/*  299 */             System.arraycopy(oldBytes, oldStarts[oldReaderIndex.intValue()], bytes, this.starts[i], this.starts[(i + 1)] - this.starts[i]);
/*      */           }
/*  301 */           else this.subReaders[i].norms(field, bytes, this.starts[i]);
/*      */ 
/*      */         }
/*      */ 
/*  305 */         this.normsCache.put(field, bytes);
/*      */       }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  313 */     StringBuilder buffer = new StringBuilder();
/*  314 */     if (this.hasChanges) {
/*  315 */       buffer.append("*");
/*      */     }
/*  317 */     buffer.append(getClass().getSimpleName());
/*  318 */     buffer.append('(');
/*  319 */     String segmentsFile = this.segmentInfos.getCurrentSegmentFileName();
/*  320 */     if (segmentsFile != null) {
/*  321 */       buffer.append(segmentsFile);
/*      */     }
/*  323 */     if (this.writer != null) {
/*  324 */       buffer.append(":nrt");
/*      */     }
/*  326 */     for (int i = 0; i < this.subReaders.length; i++) {
/*  327 */       buffer.append(' ');
/*  328 */       buffer.append(this.subReaders[i]);
/*      */     }
/*  330 */     buffer.append(')');
/*  331 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   private void initialize(SegmentReader[] subReaders) throws IOException {
/*  335 */     this.subReaders = subReaders;
/*  336 */     this.starts = new int[subReaders.length + 1];
/*  337 */     for (int i = 0; i < subReaders.length; i++) {
/*  338 */       this.starts[i] = this.maxDoc;
/*  339 */       this.maxDoc += subReaders[i].maxDoc();
/*      */ 
/*  341 */       if (subReaders[i].hasDeletions())
/*  342 */         this.hasDeletions = true;
/*      */     }
/*  344 */     this.starts[subReaders.length] = this.maxDoc;
/*      */ 
/*  346 */     if (!this.readOnly)
/*  347 */       this.maxIndexVersion = SegmentInfos.readCurrentVersion(this.directory);
/*      */   }
/*      */ 
/*      */   public final synchronized Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  354 */       return clone(this.readOnly); } catch (Exception ex) {
/*      */     }
/*  356 */     throw new RuntimeException(ex);
/*      */   }
/*      */ 
/*      */   public final synchronized IndexReader clone(boolean openReadOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  362 */     DirectoryReader newReader = doReopen((SegmentInfos)this.segmentInfos.clone(), true, openReadOnly);
/*      */ 
/*  364 */     if (this != newReader) {
/*  365 */       newReader.deletionPolicy = this.deletionPolicy;
/*      */     }
/*  367 */     newReader.writer = this.writer;
/*      */ 
/*  370 */     if ((!openReadOnly) && (this.writeLock != null))
/*      */     {
/*  372 */       assert (this.writer == null);
/*  373 */       newReader.writeLock = this.writeLock;
/*  374 */       newReader.hasChanges = this.hasChanges;
/*  375 */       newReader.hasDeletions = this.hasDeletions;
/*  376 */       this.writeLock = null;
/*  377 */       this.hasChanges = false;
/*      */     }
/*  379 */     assert (newReader.readerFinishedListeners != null);
/*      */ 
/*  381 */     return newReader;
/*      */   }
/*      */ 
/*      */   public final IndexReader reopen()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  387 */     return doReopen(this.readOnly, null);
/*      */   }
/*      */ 
/*      */   public final IndexReader reopen(boolean openReadOnly) throws CorruptIndexException, IOException
/*      */   {
/*  392 */     return doReopen(openReadOnly, null);
/*      */   }
/*      */ 
/*      */   public final IndexReader reopen(IndexCommit commit) throws CorruptIndexException, IOException
/*      */   {
/*  397 */     return doReopen(true, commit);
/*      */   }
/*      */ 
/*      */   private final IndexReader doReopenFromWriter(boolean openReadOnly, IndexCommit commit) throws CorruptIndexException, IOException {
/*  401 */     assert (this.readOnly);
/*      */ 
/*  403 */     if (!openReadOnly) {
/*  404 */       throw new IllegalArgumentException("a reader obtained from IndexWriter.getReader() can only be reopened with openReadOnly=true (got false)");
/*      */     }
/*      */ 
/*  407 */     if (commit != null) {
/*  408 */       throw new IllegalArgumentException("a reader obtained from IndexWriter.getReader() cannot currently accept a commit");
/*      */     }
/*      */ 
/*  414 */     IndexReader reader = this.writer.getReader(this.applyAllDeletes);
/*  415 */     reader.readerFinishedListeners = this.readerFinishedListeners;
/*  416 */     return reader;
/*      */   }
/*      */ 
/*      */   private IndexReader doReopen(boolean openReadOnly, IndexCommit commit) throws CorruptIndexException, IOException {
/*  420 */     ensureOpen();
/*      */ 
/*  422 */     assert ((commit == null) || (openReadOnly));
/*      */ 
/*  426 */     if (this.writer != null) {
/*  427 */       return doReopenFromWriter(openReadOnly, commit);
/*      */     }
/*  429 */     return doReopenNoWriter(openReadOnly, commit);
/*      */   }
/*      */ 
/*      */   private synchronized IndexReader doReopenNoWriter(boolean openReadOnly, IndexCommit commit)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  435 */     if (commit == null) {
/*  436 */       if (this.hasChanges)
/*      */       {
/*  438 */         assert (!this.readOnly);
/*      */ 
/*  440 */         assert (this.writeLock != null);
/*      */ 
/*  443 */         assert (isCurrent());
/*      */ 
/*  445 */         if (openReadOnly) {
/*  446 */           return clone(openReadOnly);
/*      */         }
/*  448 */         return this;
/*      */       }
/*  450 */       if (isCurrent()) {
/*  451 */         if (openReadOnly != this.readOnly)
/*      */         {
/*  453 */           return clone(openReadOnly);
/*      */         }
/*  455 */         return this;
/*      */       }
/*      */     }
/*      */     else {
/*  459 */       if (this.directory != commit.getDirectory())
/*  460 */         throw new IOException("the specified commit does not match the specified Directory");
/*  461 */       if ((this.segmentInfos != null) && (commit.getSegmentsFileName().equals(this.segmentInfos.getCurrentSegmentFileName()))) {
/*  462 */         if (this.readOnly != openReadOnly)
/*      */         {
/*  464 */           return clone(openReadOnly);
/*      */         }
/*  466 */         return this;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  471 */     return (IndexReader)new SegmentInfos.FindSegmentsFile(this.directory, openReadOnly)
/*      */     {
/*      */       protected Object doBody(String segmentFileName) throws CorruptIndexException, IOException {
/*  474 */         SegmentInfos infos = new SegmentInfos();
/*  475 */         infos.read(this.directory, segmentFileName);
/*  476 */         return DirectoryReader.this.doReopen(infos, false, this.val$openReadOnly);
/*      */       }
/*      */     }
/*  471 */     .run(commit);
/*      */   }
/*      */ 
/*      */   private synchronized DirectoryReader doReopen(SegmentInfos infos, boolean doClone, boolean openReadOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*      */     DirectoryReader reader;
/*      */     DirectoryReader reader;
/*  483 */     if (openReadOnly)
/*  484 */       reader = new ReadOnlyDirectoryReader(this.directory, infos, this.subReaders, this.starts, this.normsCache, doClone, this.termInfosIndexDivisor, this.readerFinishedListeners);
/*      */     else {
/*  486 */       reader = new DirectoryReader(this.directory, infos, this.subReaders, this.starts, this.normsCache, false, doClone, this.termInfosIndexDivisor, this.readerFinishedListeners);
/*      */     }
/*  488 */     return reader;
/*      */   }
/*      */ 
/*      */   public long getVersion()
/*      */   {
/*  494 */     ensureOpen();
/*  495 */     return this.segmentInfos.getVersion();
/*      */   }
/*      */ 
/*      */   public TermFreqVector[] getTermFreqVectors(int n) throws IOException
/*      */   {
/*  500 */     ensureOpen();
/*  501 */     int i = readerIndex(n);
/*  502 */     return this.subReaders[i].getTermFreqVectors(n - this.starts[i]);
/*      */   }
/*      */ 
/*      */   public TermFreqVector getTermFreqVector(int n, String field)
/*      */     throws IOException
/*      */   {
/*  508 */     ensureOpen();
/*  509 */     int i = readerIndex(n);
/*  510 */     return this.subReaders[i].getTermFreqVector(n - this.starts[i], field);
/*      */   }
/*      */ 
/*      */   public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper)
/*      */     throws IOException
/*      */   {
/*  516 */     ensureOpen();
/*  517 */     int i = readerIndex(docNumber);
/*  518 */     this.subReaders[i].getTermFreqVector(docNumber - this.starts[i], field, mapper);
/*      */   }
/*      */ 
/*      */   public void getTermFreqVector(int docNumber, TermVectorMapper mapper) throws IOException
/*      */   {
/*  523 */     ensureOpen();
/*  524 */     int i = readerIndex(docNumber);
/*  525 */     this.subReaders[i].getTermFreqVector(docNumber - this.starts[i], mapper);
/*      */   }
/*      */ 
/*      */   public boolean isOptimized()
/*      */   {
/*  534 */     ensureOpen();
/*  535 */     return (this.segmentInfos.size() == 1) && (!hasDeletions());
/*      */   }
/*      */ 
/*      */   public int numDocs()
/*      */   {
/*  544 */     if (this.numDocs == -1) {
/*  545 */       int n = 0;
/*  546 */       for (int i = 0; i < this.subReaders.length; i++)
/*  547 */         n += this.subReaders[i].numDocs();
/*  548 */       this.numDocs = n;
/*      */     }
/*  550 */     return this.numDocs;
/*      */   }
/*      */ 
/*      */   public int maxDoc()
/*      */   {
/*  556 */     return this.maxDoc;
/*      */   }
/*      */ 
/*      */   public Document document(int n, FieldSelector fieldSelector)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  562 */     ensureOpen();
/*  563 */     int i = readerIndex(n);
/*  564 */     return this.subReaders[i].document(n - this.starts[i], fieldSelector);
/*      */   }
/*      */ 
/*      */   public boolean isDeleted(int n)
/*      */   {
/*  570 */     int i = readerIndex(n);
/*  571 */     return this.subReaders[i].isDeleted(n - this.starts[i]);
/*      */   }
/*      */ 
/*      */   public boolean hasDeletions()
/*      */   {
/*  577 */     return this.hasDeletions;
/*      */   }
/*      */ 
/*      */   protected void doDelete(int n) throws CorruptIndexException, IOException
/*      */   {
/*  582 */     this.numDocs = -1;
/*  583 */     int i = readerIndex(n);
/*  584 */     this.subReaders[i].deleteDocument(n - this.starts[i]);
/*  585 */     this.hasDeletions = true;
/*      */   }
/*      */ 
/*      */   protected void doUndeleteAll() throws CorruptIndexException, IOException
/*      */   {
/*  590 */     for (int i = 0; i < this.subReaders.length; i++) {
/*  591 */       this.subReaders[i].undeleteAll();
/*      */     }
/*  593 */     this.hasDeletions = false;
/*  594 */     this.numDocs = -1;
/*      */   }
/*      */ 
/*      */   private int readerIndex(int n) {
/*  598 */     return readerIndex(n, this.starts, this.subReaders.length);
/*      */   }
/*      */ 
/*      */   static final int readerIndex(int n, int[] starts, int numSubReaders) {
/*  602 */     int lo = 0;
/*  603 */     int hi = numSubReaders - 1;
/*      */ 
/*  605 */     while (hi >= lo) {
/*  606 */       int mid = lo + hi >>> 1;
/*  607 */       int midValue = starts[mid];
/*  608 */       if (n < midValue) {
/*  609 */         hi = mid - 1;
/*  610 */       } else if (n > midValue) {
/*  611 */         lo = mid + 1;
/*      */       } else {
/*  613 */         while ((mid + 1 < numSubReaders) && (starts[(mid + 1)] == midValue)) {
/*  614 */           mid++;
/*      */         }
/*  616 */         return mid;
/*      */       }
/*      */     }
/*  619 */     return hi;
/*      */   }
/*      */ 
/*      */   public boolean hasNorms(String field) throws IOException
/*      */   {
/*  624 */     ensureOpen();
/*  625 */     for (int i = 0; i < this.subReaders.length; i++) {
/*  626 */       if (this.subReaders[i].hasNorms(field)) return true;
/*      */     }
/*  628 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized byte[] norms(String field) throws IOException
/*      */   {
/*  633 */     ensureOpen();
/*  634 */     byte[] bytes = (byte[])this.normsCache.get(field);
/*  635 */     if (bytes != null)
/*  636 */       return bytes;
/*  637 */     if (!hasNorms(field)) {
/*  638 */       return null;
/*      */     }
/*  640 */     bytes = new byte[maxDoc()];
/*  641 */     for (int i = 0; i < this.subReaders.length; i++)
/*  642 */       this.subReaders[i].norms(field, bytes, this.starts[i]);
/*  643 */     this.normsCache.put(field, bytes);
/*  644 */     return bytes;
/*      */   }
/*      */ 
/*      */   public synchronized void norms(String field, byte[] result, int offset)
/*      */     throws IOException
/*      */   {
/*  650 */     ensureOpen();
/*  651 */     byte[] bytes = (byte[])this.normsCache.get(field);
/*  652 */     if ((bytes == null) && (!hasNorms(field)))
/*  653 */       Arrays.fill(result, offset, result.length, Similarity.getDefault().encodeNormValue(1.0F));
/*  654 */     else if (bytes != null)
/*  655 */       System.arraycopy(bytes, 0, result, offset, maxDoc());
/*      */     else
/*  657 */       for (int i = 0; i < this.subReaders.length; i++)
/*  658 */         this.subReaders[i].norms(field, result, offset + this.starts[i]);
/*      */   }
/*      */ 
/*      */   protected void doSetNorm(int n, String field, byte value)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  666 */     synchronized (this.normsCache) {
/*  667 */       this.normsCache.remove(field);
/*      */     }
/*  669 */     int i = readerIndex(n);
/*  670 */     this.subReaders[i].setNorm(n - this.starts[i], field, value);
/*      */   }
/*      */ 
/*      */   public TermEnum terms() throws IOException
/*      */   {
/*  675 */     ensureOpen();
/*  676 */     if (this.subReaders.length == 1)
/*      */     {
/*  678 */       return this.subReaders[0].terms();
/*      */     }
/*  680 */     return new MultiTermEnum(this, this.subReaders, this.starts, null);
/*      */   }
/*      */ 
/*      */   public TermEnum terms(Term term)
/*      */     throws IOException
/*      */   {
/*  686 */     ensureOpen();
/*  687 */     if (this.subReaders.length == 1)
/*      */     {
/*  689 */       return this.subReaders[0].terms(term);
/*      */     }
/*  691 */     return new MultiTermEnum(this, this.subReaders, this.starts, term);
/*      */   }
/*      */ 
/*      */   public int docFreq(Term t)
/*      */     throws IOException
/*      */   {
/*  697 */     ensureOpen();
/*  698 */     int total = 0;
/*  699 */     for (int i = 0; i < this.subReaders.length; i++)
/*  700 */       total += this.subReaders[i].docFreq(t);
/*  701 */     return total;
/*      */   }
/*      */ 
/*      */   public TermDocs termDocs() throws IOException
/*      */   {
/*  706 */     ensureOpen();
/*  707 */     if (this.subReaders.length == 1)
/*      */     {
/*  709 */       return this.subReaders[0].termDocs();
/*      */     }
/*  711 */     return new MultiTermDocs(this, this.subReaders, this.starts);
/*      */   }
/*      */ 
/*      */   public TermDocs termDocs(Term term)
/*      */     throws IOException
/*      */   {
/*  717 */     ensureOpen();
/*  718 */     if (this.subReaders.length == 1)
/*      */     {
/*  720 */       return this.subReaders[0].termDocs(term);
/*      */     }
/*  722 */     return super.termDocs(term);
/*      */   }
/*      */ 
/*      */   public TermPositions termPositions()
/*      */     throws IOException
/*      */   {
/*  728 */     ensureOpen();
/*  729 */     if (this.subReaders.length == 1)
/*      */     {
/*  731 */       return this.subReaders[0].termPositions();
/*      */     }
/*  733 */     return new MultiTermPositions(this, this.subReaders, this.starts);
/*      */   }
/*      */ 
/*      */   protected void acquireWriteLock()
/*      */     throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/*  751 */     if (this.readOnly)
/*      */     {
/*  755 */       ReadOnlySegmentReader.noWrite();
/*      */     }
/*      */ 
/*  758 */     if (this.segmentInfos != null) {
/*  759 */       ensureOpen();
/*  760 */       if (this.stale) {
/*  761 */         throw new StaleReaderException("IndexReader out of date and no longer valid for delete, undelete, or setNorm operations");
/*      */       }
/*  763 */       if (this.writeLock == null) {
/*  764 */         Lock writeLock = this.directory.makeLock("write.lock");
/*  765 */         if (!writeLock.obtain(IndexWriterConfig.WRITE_LOCK_TIMEOUT))
/*  766 */           throw new LockObtainFailedException("Index locked for write: " + writeLock);
/*  767 */         this.writeLock = writeLock;
/*      */ 
/*  772 */         if (SegmentInfos.readCurrentVersion(this.directory) > this.maxIndexVersion) {
/*  773 */           this.stale = true;
/*  774 */           this.writeLock.release();
/*  775 */           this.writeLock = null;
/*  776 */           throw new StaleReaderException("IndexReader out of date and no longer valid for delete, undelete, or setNorm operations");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void doCommit(Map<String, String> commitUserData)
/*      */     throws IOException
/*      */   {
/*  792 */     if (this.hasChanges) {
/*  793 */       this.segmentInfos.setUserData(commitUserData);
/*      */ 
/*  796 */       IndexFileDeleter deleter = new IndexFileDeleter(this.directory, this.deletionPolicy == null ? new KeepOnlyLastCommitDeletionPolicy() : this.deletionPolicy, this.segmentInfos, null, null);
/*      */ 
/*  799 */       this.segmentInfos.updateGeneration(deleter.getLastSegmentInfos());
/*  800 */       this.segmentInfos.changed();
/*      */ 
/*  804 */       startCommit();
/*      */ 
/*  806 */       List rollbackSegments = this.segmentInfos.createBackupSegmentInfos(false);
/*      */ 
/*  808 */       boolean success = false;
/*      */       try {
/*  810 */         for (int i = 0; i < this.subReaders.length; i++) {
/*  811 */           this.subReaders[i].commit();
/*      */         }
/*      */ 
/*  815 */         this.segmentInfos.pruneDeletedSegments();
/*      */ 
/*  818 */         this.directory.sync(this.segmentInfos.files(this.directory, false));
/*  819 */         this.segmentInfos.commit(this.directory);
/*  820 */         success = true;
/*      */       }
/*      */       finally {
/*  823 */         if (!success)
/*      */         {
/*  830 */           rollbackCommit();
/*      */ 
/*  835 */           deleter.refresh();
/*      */ 
/*  838 */           this.segmentInfos.rollbackSegmentInfos(rollbackSegments);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  844 */       deleter.checkpoint(this.segmentInfos, true);
/*  845 */       deleter.close();
/*      */ 
/*  847 */       this.maxIndexVersion = this.segmentInfos.getVersion();
/*      */ 
/*  849 */       if (this.writeLock != null) {
/*  850 */         this.writeLock.release();
/*  851 */         this.writeLock = null;
/*      */       }
/*      */     }
/*  854 */     this.hasChanges = false;
/*      */   }
/*      */ 
/*      */   void startCommit() {
/*  858 */     this.rollbackHasChanges = this.hasChanges;
/*  859 */     for (int i = 0; i < this.subReaders.length; i++)
/*  860 */       this.subReaders[i].startCommit();
/*      */   }
/*      */ 
/*      */   void rollbackCommit()
/*      */   {
/*  865 */     this.hasChanges = this.rollbackHasChanges;
/*  866 */     for (int i = 0; i < this.subReaders.length; i++)
/*  867 */       this.subReaders[i].rollbackCommit();
/*      */   }
/*      */ 
/*      */   public Map<String, String> getCommitUserData()
/*      */   {
/*  873 */     ensureOpen();
/*  874 */     return this.segmentInfos.getUserData();
/*      */   }
/*      */ 
/*      */   public boolean isCurrent() throws CorruptIndexException, IOException
/*      */   {
/*  879 */     ensureOpen();
/*  880 */     if ((this.writer == null) || (this.writer.isClosed()))
/*      */     {
/*  882 */       return SegmentInfos.readCurrentVersion(this.directory) == this.segmentInfos.getVersion();
/*      */     }
/*  884 */     return this.writer.nrtIsCurrent(this.segmentInfos);
/*      */   }
/*      */ 
/*      */   protected synchronized void doClose()
/*      */     throws IOException
/*      */   {
/*  890 */     IOException ioe = null;
/*  891 */     this.normsCache = null;
/*  892 */     for (int i = 0; i < this.subReaders.length; i++) {
/*      */       try
/*      */       {
/*  895 */         this.subReaders[i].decRef();
/*      */       } catch (IOException e) {
/*  897 */         if (ioe != null) continue; ioe = e;
/*      */       }
/*      */     }
/*      */ 
/*  901 */     if (this.writer != null)
/*      */     {
/*  904 */       this.writer.deleteUnusedFiles();
/*      */     }
/*      */ 
/*  908 */     if (ioe != null) throw ioe;
/*      */   }
/*      */ 
/*      */   public Collection<String> getFieldNames(IndexReader.FieldOption fieldNames)
/*      */   {
/*  913 */     ensureOpen();
/*  914 */     return getFieldNames(fieldNames, this.subReaders);
/*      */   }
/*      */ 
/*      */   static Collection<String> getFieldNames(IndexReader.FieldOption fieldNames, IndexReader[] subReaders)
/*      */   {
/*  919 */     Set fieldSet = new HashSet();
/*  920 */     for (IndexReader reader : subReaders) {
/*  921 */       Collection names = reader.getFieldNames(fieldNames);
/*  922 */       fieldSet.addAll(names);
/*      */     }
/*  924 */     return fieldSet;
/*      */   }
/*      */ 
/*      */   public IndexReader[] getSequentialSubReaders()
/*      */   {
/*  929 */     return this.subReaders;
/*      */   }
/*      */ 
/*      */   public Directory directory()
/*      */   {
/*  938 */     return this.directory;
/*      */   }
/*      */ 
/*      */   public int getTermInfosIndexDivisor()
/*      */   {
/*  943 */     return this.termInfosIndexDivisor;
/*      */   }
/*      */ 
/*      */   public IndexCommit getIndexCommit()
/*      */     throws IOException
/*      */   {
/*  953 */     return new ReaderCommit(this.segmentInfos, this.directory);
/*      */   }
/*      */ 
/*      */   public static Collection<IndexCommit> listCommits(Directory dir) throws IOException
/*      */   {
/*  958 */     String[] files = dir.listAll();
/*      */ 
/*  960 */     List commits = new ArrayList();
/*      */ 
/*  962 */     SegmentInfos latest = new SegmentInfos();
/*  963 */     latest.read(dir);
/*  964 */     long currentGen = latest.getGeneration();
/*      */ 
/*  966 */     commits.add(new ReaderCommit(latest, dir));
/*      */ 
/*  968 */     for (int i = 0; i < files.length; i++)
/*      */     {
/*  970 */       String fileName = files[i];
/*      */ 
/*  972 */       if ((!fileName.startsWith("segments")) || (fileName.equals("segments.gen")) || (SegmentInfos.generationFromSegmentsFileName(fileName) >= currentGen))
/*      */       {
/*      */         continue;
/*      */       }
/*  976 */       SegmentInfos sis = new SegmentInfos();
/*      */       try
/*      */       {
/*  980 */         sis.read(dir, fileName);
/*      */       }
/*      */       catch (FileNotFoundException fnfe)
/*      */       {
/*  989 */         sis = null;
/*      */       }
/*      */ 
/*  992 */       if (sis != null) {
/*  993 */         commits.add(new ReaderCommit(sis, dir));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  998 */     Collections.sort(commits);
/*      */ 
/* 1000 */     return commits;
/*      */   }
/*      */ 
/*      */   static class MultiTermPositions extends DirectoryReader.MultiTermDocs
/*      */     implements TermPositions
/*      */   {
/*      */     public MultiTermPositions(IndexReader topReader, IndexReader[] r, int[] s)
/*      */     {
/* 1312 */       super(r, s);
/*      */     }
/*      */ 
/*      */     protected TermDocs termDocs(IndexReader reader) throws IOException
/*      */     {
/* 1317 */       return reader.termPositions();
/*      */     }
/*      */ 
/*      */     public int nextPosition() throws IOException {
/* 1321 */       return ((TermPositions)this.current).nextPosition();
/*      */     }
/*      */ 
/*      */     public int getPayloadLength() {
/* 1325 */       return ((TermPositions)this.current).getPayloadLength();
/*      */     }
/*      */ 
/*      */     public byte[] getPayload(byte[] data, int offset) throws IOException {
/* 1329 */       return ((TermPositions)this.current).getPayload(data, offset);
/*      */     }
/*      */ 
/*      */     public boolean isPayloadAvailable()
/*      */     {
/* 1335 */       return ((TermPositions)this.current).isPayloadAvailable();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class MultiTermDocs
/*      */     implements TermDocs
/*      */   {
/*      */     IndexReader topReader;
/*      */     protected IndexReader[] readers;
/*      */     protected int[] starts;
/*      */     protected Term term;
/* 1165 */     protected int base = 0;
/* 1166 */     protected int pointer = 0;
/*      */     private TermDocs[] readerTermDocs;
/*      */     protected TermDocs current;
/*      */     private DirectoryReader.MultiTermEnum tenum;
/*      */     int matchingSegmentPos;
/*      */     SegmentMergeInfo smi;
/*      */ 
/*      */     public MultiTermDocs(IndexReader topReader, IndexReader[] r, int[] s)
/*      */     {
/* 1176 */       this.topReader = topReader;
/* 1177 */       this.readers = r;
/* 1178 */       this.starts = s;
/*      */ 
/* 1180 */       this.readerTermDocs = new TermDocs[r.length];
/*      */     }
/*      */ 
/*      */     public int doc() {
/* 1184 */       return this.base + this.current.doc();
/*      */     }
/*      */     public int freq() {
/* 1187 */       return this.current.freq();
/*      */     }
/*      */ 
/*      */     public void seek(Term term) {
/* 1191 */       this.term = term;
/* 1192 */       this.base = 0;
/* 1193 */       this.pointer = 0;
/* 1194 */       this.current = null;
/* 1195 */       this.tenum = null;
/* 1196 */       this.smi = null;
/* 1197 */       this.matchingSegmentPos = 0;
/*      */     }
/*      */ 
/*      */     public void seek(TermEnum termEnum) throws IOException {
/* 1201 */       seek(termEnum.term());
/* 1202 */       if ((termEnum instanceof DirectoryReader.MultiTermEnum)) {
/* 1203 */         this.tenum = ((DirectoryReader.MultiTermEnum)termEnum);
/* 1204 */         if (this.topReader != this.tenum.topReader)
/* 1205 */           this.tenum = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean next() throws IOException {
/*      */       while (true) {
/* 1211 */         if ((this.current != null) && (this.current.next())) {
/* 1212 */           return true;
/*      */         }
/* 1214 */         if (this.pointer >= this.readers.length) break;
/* 1215 */         if (this.tenum != null) {
/* 1216 */           this.smi = this.tenum.matchingSegments[(this.matchingSegmentPos++)];
/* 1217 */           if (this.smi == null) {
/* 1218 */             this.pointer = this.readers.length;
/* 1219 */             return false;
/*      */           }
/* 1221 */           this.pointer = this.smi.ord;
/*      */         }
/* 1223 */         this.base = this.starts[this.pointer];
/* 1224 */         this.current = termDocs(this.pointer++);
/*      */       }
/* 1226 */       return false;
/*      */     }
/*      */ 
/*      */     public int read(int[] docs, int[] freqs)
/*      */       throws IOException
/*      */     {
/*      */       while (true)
/*      */       {
/* 1234 */         if (this.current == null) {
/* 1235 */           if (this.pointer < this.readers.length) {
/* 1236 */             if (this.tenum != null) {
/* 1237 */               this.smi = this.tenum.matchingSegments[(this.matchingSegmentPos++)];
/* 1238 */               if (this.smi == null) {
/* 1239 */                 this.pointer = this.readers.length;
/* 1240 */                 return 0;
/*      */               }
/* 1242 */               this.pointer = this.smi.ord;
/*      */             }
/* 1244 */             this.base = this.starts[this.pointer];
/* 1245 */             this.current = termDocs(this.pointer++); continue;
/*      */           }
/* 1247 */           return 0;
/*      */         }
/*      */ 
/* 1250 */         int end = this.current.read(docs, freqs);
/* 1251 */         if (end == 0) {
/* 1252 */           this.current = null;
/*      */         } else {
/* 1254 */           int b = this.base;
/* 1255 */           for (int i = 0; i < end; i++)
/* 1256 */             docs[i] += b;
/* 1257 */           return end;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean skipTo(int target) throws IOException
/*      */     {
/*      */       while (true) {
/* 1265 */         if ((this.current != null) && (this.current.skipTo(target - this.base)))
/* 1266 */           return true;
/* 1267 */         if (this.pointer >= this.readers.length) break;
/* 1268 */         if (this.tenum != null) {
/* 1269 */           SegmentMergeInfo smi = this.tenum.matchingSegments[(this.matchingSegmentPos++)];
/* 1270 */           if (smi == null) {
/* 1271 */             this.pointer = this.readers.length;
/* 1272 */             return false;
/*      */           }
/* 1274 */           this.pointer = smi.ord;
/*      */         }
/* 1276 */         this.base = this.starts[this.pointer];
/* 1277 */         this.current = termDocs(this.pointer++);
/*      */       }
/* 1279 */       return false;
/*      */     }
/*      */ 
/*      */     private TermDocs termDocs(int i) throws IOException
/*      */     {
/* 1284 */       TermDocs result = this.readerTermDocs[i];
/* 1285 */       if (result == null)
/* 1286 */         result = this.readerTermDocs[i] =  = termDocs(this.readers[i]);
/* 1287 */       if (this.smi != null) {
/* 1288 */         assert (this.smi.ord == i);
/* 1289 */         assert (this.smi.termEnum.term().equals(this.term));
/* 1290 */         result.seek(this.smi.termEnum);
/*      */       } else {
/* 1292 */         result.seek(this.term);
/*      */       }
/* 1294 */       return result;
/*      */     }
/*      */ 
/*      */     protected TermDocs termDocs(IndexReader reader) throws IOException
/*      */     {
/* 1299 */       return this.term == null ? reader.termDocs(null) : reader.termDocs();
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/* 1303 */       for (int i = 0; i < this.readerTermDocs.length; i++)
/* 1304 */         if (this.readerTermDocs[i] != null)
/* 1305 */           this.readerTermDocs[i].close();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class MultiTermEnum extends TermEnum
/*      */   {
/*      */     IndexReader topReader;
/*      */     private SegmentMergeQueue queue;
/*      */     private Term term;
/*      */     private int docFreq;
/*      */     final SegmentMergeInfo[] matchingSegments;
/*      */ 
/*      */     public MultiTermEnum(IndexReader topReader, IndexReader[] readers, int[] starts, Term t)
/*      */       throws IOException
/*      */     {
/* 1083 */       this.topReader = topReader;
/* 1084 */       this.queue = new SegmentMergeQueue(readers.length);
/* 1085 */       this.matchingSegments = new SegmentMergeInfo[readers.length + 1];
/* 1086 */       for (int i = 0; i < readers.length; i++) {
/* 1087 */         IndexReader reader = readers[i];
/*      */         TermEnum termEnum;
/*      */         TermEnum termEnum;
/* 1090 */         if (t != null)
/* 1091 */           termEnum = reader.terms(t);
/*      */         else {
/* 1093 */           termEnum = reader.terms();
/*      */         }
/* 1095 */         SegmentMergeInfo smi = new SegmentMergeInfo(starts[i], termEnum, reader);
/* 1096 */         smi.ord = i;
/* 1097 */         if (t == null ? smi.next() : termEnum.term() != null)
/* 1098 */           this.queue.add(smi);
/*      */         else {
/* 1100 */           smi.close();
/*      */         }
/*      */       }
/* 1103 */       if ((t != null) && (this.queue.size() > 0))
/* 1104 */         next();
/*      */     }
/*      */ 
/*      */     public boolean next()
/*      */       throws IOException
/*      */     {
/* 1110 */       for (int i = 0; i < this.matchingSegments.length; i++) {
/* 1111 */         SegmentMergeInfo smi = this.matchingSegments[i];
/* 1112 */         if (smi == null) break;
/* 1113 */         if (smi.next())
/* 1114 */           this.queue.add(smi);
/*      */         else {
/* 1116 */           smi.close();
/*      */         }
/*      */       }
/* 1119 */       int numMatchingSegments = 0;
/* 1120 */       this.matchingSegments[0] = null;
/*      */ 
/* 1122 */       SegmentMergeInfo top = (SegmentMergeInfo)this.queue.top();
/*      */ 
/* 1124 */       if (top == null) {
/* 1125 */         this.term = null;
/* 1126 */         return false;
/*      */       }
/*      */ 
/* 1129 */       this.term = top.term;
/* 1130 */       this.docFreq = 0;
/*      */ 
/* 1132 */       while ((top != null) && (this.term.compareTo(top.term) == 0)) {
/* 1133 */         this.matchingSegments[(numMatchingSegments++)] = top;
/* 1134 */         this.queue.pop();
/* 1135 */         this.docFreq += top.termEnum.docFreq();
/* 1136 */         top = (SegmentMergeInfo)this.queue.top();
/*      */       }
/*      */ 
/* 1139 */       this.matchingSegments[numMatchingSegments] = null;
/* 1140 */       return true;
/*      */     }
/*      */ 
/*      */     public Term term()
/*      */     {
/* 1145 */       return this.term;
/*      */     }
/*      */ 
/*      */     public int docFreq()
/*      */     {
/* 1150 */       return this.docFreq;
/*      */     }
/*      */ 
/*      */     public void close() throws IOException
/*      */     {
/* 1155 */       this.queue.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class ReaderCommit extends IndexCommit
/*      */   {
/*      */     private String segmentsFileName;
/*      */     Collection<String> files;
/*      */     Directory dir;
/*      */     long generation;
/*      */     long version;
/*      */     final boolean isOptimized;
/*      */     final Map<String, String> userData;
/*      */ 
/*      */     ReaderCommit(SegmentInfos infos, Directory dir)
/*      */       throws IOException
/*      */     {
/* 1013 */       this.segmentsFileName = infos.getCurrentSegmentFileName();
/* 1014 */       this.dir = dir;
/* 1015 */       this.userData = infos.getUserData();
/* 1016 */       this.files = Collections.unmodifiableCollection(infos.files(dir, true));
/* 1017 */       this.version = infos.getVersion();
/* 1018 */       this.generation = infos.getGeneration();
/* 1019 */       this.isOptimized = ((infos.size() == 1) && (!infos.info(0).hasDeletions()));
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1024 */       return "DirectoryReader.ReaderCommit(" + this.segmentsFileName + ")";
/*      */     }
/*      */ 
/*      */     public boolean isOptimized()
/*      */     {
/* 1029 */       return this.isOptimized;
/*      */     }
/*      */ 
/*      */     public String getSegmentsFileName()
/*      */     {
/* 1034 */       return this.segmentsFileName;
/*      */     }
/*      */ 
/*      */     public Collection<String> getFileNames()
/*      */     {
/* 1039 */       return this.files;
/*      */     }
/*      */ 
/*      */     public Directory getDirectory()
/*      */     {
/* 1044 */       return this.dir;
/*      */     }
/*      */ 
/*      */     public long getVersion()
/*      */     {
/* 1049 */       return this.version;
/*      */     }
/*      */ 
/*      */     public long getGeneration()
/*      */     {
/* 1054 */       return this.generation;
/*      */     }
/*      */ 
/*      */     public boolean isDeleted()
/*      */     {
/* 1059 */       return false;
/*      */     }
/*      */ 
/*      */     public Map<String, String> getUserData()
/*      */     {
/* 1064 */       return this.userData;
/*      */     }
/*      */ 
/*      */     public void delete()
/*      */     {
/* 1069 */       throw new UnsupportedOperationException("This IndexCommit does not support deletions");
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DirectoryReader
 * JD-Core Version:    0.6.0
 */