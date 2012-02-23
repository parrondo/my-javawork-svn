/*      */ package org.apache.lucene.index;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import org.apache.lucene.analysis.Analyzer;
/*      */ import org.apache.lucene.document.Document;
/*      */ import org.apache.lucene.search.Query;
/*      */ import org.apache.lucene.search.Similarity;
/*      */ import org.apache.lucene.store.AlreadyClosedException;
/*      */ import org.apache.lucene.store.Directory;
/*      */ import org.apache.lucene.store.Lock;
/*      */ import org.apache.lucene.store.LockObtainFailedException;
/*      */ import org.apache.lucene.util.Constants;
/*      */ import org.apache.lucene.util.MapBackedSet;
/*      */ import org.apache.lucene.util.StringHelper;
/*      */ import org.apache.lucene.util.ThreadInterruptedException;
/*      */ import org.apache.lucene.util.TwoPhaseCommit;
/*      */ import org.apache.lucene.util.Version;
/*      */ 
/*      */ public class IndexWriter
/*      */   implements Closeable, TwoPhaseCommit
/*      */ {
/*      */ 
/*      */   @Deprecated
/*      */   public static long WRITE_LOCK_TIMEOUT;
/*      */   private long writeLockTimeout;
/*      */   public static final String WRITE_LOCK_NAME = "write.lock";
/*      */ 
/*      */   @Deprecated
/*      */   public static final int DISABLE_AUTO_FLUSH = -1;
/*      */ 
/*      */   @Deprecated
/*      */   public static final int DEFAULT_MAX_BUFFERED_DOCS = -1;
/*      */ 
/*      */   @Deprecated
/*      */   public static final double DEFAULT_RAM_BUFFER_SIZE_MB = 16.0D;
/*      */ 
/*      */   @Deprecated
/*      */   public static final int DEFAULT_MAX_BUFFERED_DELETE_TERMS = -1;
/*      */ 
/*      */   @Deprecated
/*      */   public static final int DEFAULT_MAX_FIELD_LENGTH;
/*      */ 
/*      */   @Deprecated
/*      */   public static final int DEFAULT_TERM_INDEX_INTERVAL = 128;
/*      */   public static final int MAX_TERM_LENGTH = 16383;
/*      */   private static final int MERGE_READ_BUFFER_SIZE = 4096;
/*      */   private static final AtomicInteger MESSAGE_ID;
/*  268 */   private int messageID = MESSAGE_ID.getAndIncrement();
/*      */   private volatile boolean hitOOM;
/*      */   private final Directory directory;
/*      */   private final Analyzer analyzer;
/*  275 */   private Similarity similarity = Similarity.getDefault();
/*      */   private volatile long changeCount;
/*      */   private long lastCommitChangeCount;
/*      */   private List<SegmentInfo> rollbackSegments;
/*      */   volatile SegmentInfos pendingCommit;
/*      */   volatile long pendingCommitChangeCount;
/*  285 */   final SegmentInfos segmentInfos = new SegmentInfos();
/*      */   private DocumentsWriter docWriter;
/*      */   private IndexFileDeleter deleter;
/*  290 */   private Map<SegmentInfo, Boolean> segmentsToOptimize = new HashMap();
/*      */   private int optimizeMaxNumSegments;
/*      */   private Lock writeLock;
/*      */   private boolean closed;
/*      */   private boolean closing;
/*  300 */   private HashSet<SegmentInfo> mergingSegments = new HashSet();
/*      */   private MergePolicy mergePolicy;
/*      */   private MergeScheduler mergeScheduler;
/*  305 */   private LinkedList<MergePolicy.OneMerge> pendingMerges = new LinkedList();
/*  306 */   private Set<MergePolicy.OneMerge> runningMerges = new HashSet();
/*  307 */   private List<MergePolicy.OneMerge> mergeExceptions = new ArrayList();
/*      */   private long mergeGen;
/*      */   private boolean stopMerges;
/*  311 */   private final AtomicInteger flushCount = new AtomicInteger();
/*  312 */   private final AtomicInteger flushDeletesCount = new AtomicInteger();
/*      */ 
/*  314 */   final ReaderPool readerPool = new ReaderPool();
/*      */   final BufferedDeletesStream bufferedDeletesStream;
/*      */   private volatile boolean poolReaders;
/*      */   private final IndexWriterConfig config;
/*      */   private PayloadProcessorProvider payloadProcessorProvider;
/*      */   boolean anyNonBulkMerges;
/*  473 */   private final Collection<IndexReader.ReaderFinishedListener> readerFinishedListeners = new MapBackedSet(new ConcurrentHashMap());
/*      */ 
/*      */   @Deprecated
/* 1993 */   private int maxFieldLength = DEFAULT_MAX_FIELD_LENGTH;
/*      */   private PrintStream infoStream;
/*      */   private static PrintStream defaultInfoStream;
/* 3434 */   private final Object commitLock = new Object();
/*      */   private boolean keepFullyDeletedSegments;
/* 4937 */   final FlushControl flushControl = new FlushControl();
/*      */ 
/*      */   @Deprecated
/*      */   public IndexReader getReader()
/*      */     throws IOException
/*      */   {
/*  402 */     return getReader(this.config.getReaderTermsIndexDivisor(), true);
/*      */   }
/*      */ 
/*      */   IndexReader getReader(boolean applyAllDeletes) throws IOException {
/*  406 */     return getReader(this.config.getReaderTermsIndexDivisor(), applyAllDeletes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public IndexReader getReader(int termInfosIndexDivisor)
/*      */     throws IOException
/*      */   {
/*  435 */     return getReader(termInfosIndexDivisor, true);
/*      */   }
/*      */ 
/*      */   IndexReader getReader(int termInfosIndexDivisor, boolean applyAllDeletes) throws IOException {
/*  439 */     ensureOpen();
/*      */ 
/*  441 */     long tStart = System.currentTimeMillis();
/*      */ 
/*  443 */     if (this.infoStream != null) {
/*  444 */       message("flush at getReader");
/*      */     }
/*      */ 
/*  450 */     this.poolReaders = true;
/*      */     IndexReader r;
/*  456 */     synchronized (this) {
/*  457 */       flush(false, applyAllDeletes);
/*  458 */       r = new ReadOnlyDirectoryReader(this, this.segmentInfos, termInfosIndexDivisor, applyAllDeletes);
/*  459 */       if (this.infoStream != null) {
/*  460 */         message("return reader version=" + r.getVersion() + " reader=" + r);
/*      */       }
/*      */     }
/*      */ 
/*  464 */     maybeMerge();
/*      */ 
/*  466 */     if (this.infoStream != null) {
/*  467 */       message("getReader took " + (System.currentTimeMillis() - tStart) + " msec");
/*      */     }
/*  469 */     return r;
/*      */   }
/*      */ 
/*      */   Collection<IndexReader.ReaderFinishedListener> getReaderFinishedListeners()
/*      */     throws IOException
/*      */   {
/*  476 */     return this.readerFinishedListeners;
/*      */   }
/*      */ 
/*      */   public int numDeletedDocs(SegmentInfo info)
/*      */     throws IOException
/*      */   {
/*  758 */     SegmentReader reader = this.readerPool.getIfExists(info);
/*      */     try {
/*  760 */       if (reader != null) {
/*  761 */         i = reader.numDeletedDocs();
/*      */         return i;
/*      */       }
/*  763 */       int i = info.getDelCount();
/*      */       return i;
/*      */     }
/*      */     finally
/*      */     {
/*  766 */       if (reader != null)
/*  767 */         this.readerPool.release(reader); 
/*  767 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   protected final void ensureOpen(boolean includePendingClose)
/*      */     throws AlreadyClosedException
/*      */   {
/*  779 */     if ((this.closed) || ((includePendingClose) && (this.closing)))
/*  780 */       throw new AlreadyClosedException("this IndexWriter is closed");
/*      */   }
/*      */ 
/*      */   protected final void ensureOpen() throws AlreadyClosedException
/*      */   {
/*  785 */     ensureOpen(true);
/*      */   }
/*      */ 
/*      */   public void message(String message)
/*      */   {
/*  794 */     if (this.infoStream != null)
/*  795 */       this.infoStream.println("IW " + this.messageID + " [" + new Date() + "; " + Thread.currentThread().getName() + "]: " + message);
/*      */   }
/*      */ 
/*      */   private LogMergePolicy getLogMergePolicy()
/*      */   {
/*  803 */     if ((this.mergePolicy instanceof LogMergePolicy)) {
/*  804 */       return (LogMergePolicy)this.mergePolicy;
/*      */     }
/*  806 */     throw new IllegalArgumentException("this method can only be called when the merge policy is the default LogMergePolicy");
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean getUseCompoundFile()
/*      */   {
/*  826 */     return getLogMergePolicy().getUseCompoundFile();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setUseCompoundFile(boolean value)
/*      */   {
/*  845 */     getLogMergePolicy().setUseCompoundFile(value);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setSimilarity(Similarity similarity)
/*      */   {
/*  855 */     ensureOpen();
/*  856 */     this.similarity = similarity;
/*  857 */     this.docWriter.setSimilarity(similarity);
/*      */ 
/*  860 */     this.config.setSimilarity(similarity);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Similarity getSimilarity()
/*      */   {
/*  870 */     ensureOpen();
/*  871 */     return this.similarity;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setTermIndexInterval(int interval)
/*      */   {
/*  898 */     ensureOpen();
/*  899 */     this.config.setTermIndexInterval(interval);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getTermIndexInterval()
/*      */   {
/*  910 */     ensureOpen(false);
/*  911 */     return this.config.getTermIndexInterval();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public IndexWriter(Directory d, Analyzer a, boolean create, MaxFieldLength mfl)
/*      */     throws CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/*  940 */     this(d, new IndexWriterConfig(Version.LUCENE_31, a).setOpenMode(create ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND));
/*      */ 
/*  942 */     setMaxFieldLength(mfl.getLimit());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public IndexWriter(Directory d, Analyzer a, MaxFieldLength mfl)
/*      */     throws CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/*  967 */     this(d, new IndexWriterConfig(Version.LUCENE_31, a));
/*  968 */     setMaxFieldLength(mfl.getLimit());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public IndexWriter(Directory d, Analyzer a, IndexDeletionPolicy deletionPolicy, MaxFieldLength mfl)
/*      */     throws CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/*  993 */     this(d, new IndexWriterConfig(Version.LUCENE_31, a).setIndexDeletionPolicy(deletionPolicy));
/*  994 */     setMaxFieldLength(mfl.getLimit());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public IndexWriter(Directory d, Analyzer a, boolean create, IndexDeletionPolicy deletionPolicy, MaxFieldLength mfl)
/*      */     throws CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/* 1025 */     this(d, new IndexWriterConfig(Version.LUCENE_31, a).setOpenMode(create ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND).setIndexDeletionPolicy(deletionPolicy));
/*      */ 
/* 1027 */     setMaxFieldLength(mfl.getLimit());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public IndexWriter(Directory d, Analyzer a, IndexDeletionPolicy deletionPolicy, MaxFieldLength mfl, IndexCommit commit)
/*      */     throws CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/* 1066 */     this(d, new IndexWriterConfig(Version.LUCENE_31, a).setOpenMode(IndexWriterConfig.OpenMode.APPEND).setIndexDeletionPolicy(deletionPolicy).setIndexCommit(commit));
/*      */ 
/* 1068 */     setMaxFieldLength(mfl.getLimit());
/*      */   }
/*      */ 
/*      */   public IndexWriter(Directory d, IndexWriterConfig conf)
/*      */     throws CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/* 1097 */     this.config = ((IndexWriterConfig)conf.clone());
/* 1098 */     this.directory = d;
/* 1099 */     this.analyzer = conf.getAnalyzer();
/* 1100 */     this.infoStream = defaultInfoStream;
/* 1101 */     this.writeLockTimeout = conf.getWriteLockTimeout();
/* 1102 */     this.similarity = conf.getSimilarity();
/* 1103 */     this.mergePolicy = conf.getMergePolicy();
/* 1104 */     this.mergePolicy.setIndexWriter(this);
/* 1105 */     this.mergeScheduler = conf.getMergeScheduler();
/* 1106 */     this.bufferedDeletesStream = new BufferedDeletesStream(this.messageID);
/* 1107 */     this.bufferedDeletesStream.setInfoStream(this.infoStream);
/* 1108 */     this.poolReaders = conf.getReaderPooling();
/*      */ 
/* 1110 */     this.writeLock = this.directory.makeLock("write.lock");
/*      */ 
/* 1112 */     if (!this.writeLock.obtain(this.writeLockTimeout)) {
/* 1113 */       throw new LockObtainFailedException("Index locked for write: " + this.writeLock);
/*      */     }
/* 1115 */     IndexWriterConfig.OpenMode mode = conf.getOpenMode();
/*      */     boolean create;
/*      */     boolean create;
/* 1117 */     if (mode == IndexWriterConfig.OpenMode.CREATE) {
/* 1118 */       create = true;
/*      */     }
/*      */     else
/*      */     {
/*      */       boolean create;
/* 1119 */       if (mode == IndexWriterConfig.OpenMode.APPEND) {
/* 1120 */         create = false;
/*      */       }
/*      */       else {
/* 1123 */         create = !IndexReader.indexExists(this.directory);
/*      */       }
/*      */     }
/* 1126 */     boolean success = false;
/*      */     try
/*      */     {
/* 1135 */       if (create)
/*      */       {
/*      */         try
/*      */         {
/* 1141 */           this.segmentInfos.read(this.directory);
/* 1142 */           this.segmentInfos.clear();
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*      */         }
/*      */ 
/* 1149 */         this.changeCount += 1L;
/* 1150 */         this.segmentInfos.changed();
/*      */       } else {
/* 1152 */         this.segmentInfos.read(this.directory);
/*      */ 
/* 1154 */         IndexCommit commit = conf.getIndexCommit();
/* 1155 */         if (commit != null)
/*      */         {
/* 1161 */           if (commit.getDirectory() != this.directory)
/* 1162 */             throw new IllegalArgumentException("IndexCommit's directory doesn't match my directory");
/* 1163 */           SegmentInfos oldInfos = new SegmentInfos();
/* 1164 */           oldInfos.read(this.directory, commit.getSegmentsFileName());
/* 1165 */           this.segmentInfos.replace(oldInfos);
/* 1166 */           this.changeCount += 1L;
/* 1167 */           this.segmentInfos.changed();
/* 1168 */           if (this.infoStream != null) {
/* 1169 */             message("init: loaded commit \"" + commit.getSegmentsFileName() + "\"");
/*      */           }
/*      */         }
/*      */       }
/* 1173 */       this.rollbackSegments = this.segmentInfos.createBackupSegmentInfos(true);
/*      */ 
/* 1175 */       this.docWriter = new DocumentsWriter(this.config, this.directory, this, getCurrentFieldInfos(), this.bufferedDeletesStream);
/* 1176 */       this.docWriter.setInfoStream(this.infoStream);
/* 1177 */       this.docWriter.setMaxFieldLength(this.maxFieldLength);
/*      */ 
/* 1181 */       synchronized (this) {
/* 1182 */         this.deleter = new IndexFileDeleter(this.directory, conf.getIndexDeletionPolicy(), this.segmentInfos, this.infoStream, this);
/*      */       }
/*      */ 
/* 1188 */       if (this.deleter.startingCommitDeleted)
/*      */       {
/* 1193 */         this.changeCount += 1L;
/* 1194 */         this.segmentInfos.changed();
/*      */       }
/*      */ 
/* 1197 */       if (this.infoStream != null) {
/* 1198 */         messageState();
/*      */       }
/*      */ 
/* 1201 */       success = true;
/*      */     }
/*      */     finally {
/* 1204 */       if (!success) {
/* 1205 */         if (this.infoStream != null)
/* 1206 */           message("init: hit exception on init; releasing write lock");
/*      */         try
/*      */         {
/* 1209 */           this.writeLock.release();
/*      */         }
/*      */         catch (Throwable t) {
/*      */         }
/* 1213 */         this.writeLock = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private FieldInfos getFieldInfos(SegmentInfo info) throws IOException {
/* 1219 */     Directory cfsDir = null;
/*      */     try {
/* 1221 */       if (info.getUseCompoundFile())
/* 1222 */         cfsDir = new CompoundFileReader(this.directory, IndexFileNames.segmentFileName(info.name, "cfs"));
/*      */       else {
/* 1224 */         cfsDir = this.directory;
/*      */       }
/* 1226 */       FieldInfos localFieldInfos = new FieldInfos(cfsDir, IndexFileNames.segmentFileName(info.name, "fnm"));
/*      */       return localFieldInfos;
/*      */     }
/*      */     finally
/*      */     {
/* 1228 */       if ((info.getUseCompoundFile()) && (cfsDir != null))
/* 1229 */         cfsDir.close(); 
/* 1229 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   private FieldInfos getCurrentFieldInfos()
/*      */     throws IOException
/*      */   {
/*      */     FieldInfos fieldInfos;
/*      */     FieldInfos fieldInfos;
/* 1236 */     if (this.segmentInfos.size() > 0)
/*      */     {
/*      */       FieldInfos fieldInfos;
/* 1237 */       if (this.segmentInfos.getFormat() > -9)
/*      */       {
/* 1240 */         fieldInfos = new FieldInfos();
/* 1241 */         for (SegmentInfo info : this.segmentInfos) {
/* 1242 */           FieldInfos segFieldInfos = getFieldInfos(info);
/* 1243 */           int fieldCount = segFieldInfos.size();
/* 1244 */           for (int fieldNumber = 0; fieldNumber < fieldCount; fieldNumber++) {
/* 1245 */             fieldInfos.add(segFieldInfos.fieldInfo(fieldNumber));
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1251 */         fieldInfos = getFieldInfos(this.segmentInfos.info(this.segmentInfos.size() - 1));
/*      */       }
/*      */     } else {
/* 1254 */       fieldInfos = new FieldInfos();
/*      */     }
/* 1256 */     return fieldInfos;
/*      */   }
/*      */ 
/*      */   public IndexWriterConfig getConfig()
/*      */   {
/* 1271 */     return this.config;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMergePolicy(MergePolicy mp)
/*      */   {
/* 1281 */     ensureOpen();
/* 1282 */     if (mp == null) {
/* 1283 */       throw new NullPointerException("MergePolicy must be non-null");
/*      */     }
/* 1285 */     if (this.mergePolicy != mp)
/* 1286 */       this.mergePolicy.close();
/* 1287 */     this.mergePolicy = mp;
/* 1288 */     this.mergePolicy.setIndexWriter(this);
/* 1289 */     pushMaxBufferedDocs();
/* 1290 */     if (this.infoStream != null) {
/* 1291 */       message("setMergePolicy " + mp);
/*      */     }
/*      */ 
/* 1294 */     this.config.setMergePolicy(mp);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public MergePolicy getMergePolicy()
/*      */   {
/* 1305 */     ensureOpen();
/* 1306 */     return this.mergePolicy;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public synchronized void setMergeScheduler(MergeScheduler mergeScheduler)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 1315 */     ensureOpen();
/* 1316 */     if (mergeScheduler == null) {
/* 1317 */       throw new NullPointerException("MergeScheduler must be non-null");
/*      */     }
/* 1319 */     if (this.mergeScheduler != mergeScheduler) {
/* 1320 */       finishMerges(true);
/* 1321 */       this.mergeScheduler.close();
/*      */     }
/* 1323 */     this.mergeScheduler = mergeScheduler;
/* 1324 */     if (this.infoStream != null) {
/* 1325 */       message("setMergeScheduler " + mergeScheduler);
/*      */     }
/*      */ 
/* 1328 */     this.config.setMergeScheduler(mergeScheduler);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public MergeScheduler getMergeScheduler()
/*      */   {
/* 1339 */     ensureOpen();
/* 1340 */     return this.mergeScheduler;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMaxMergeDocs(int maxMergeDocs)
/*      */   {
/* 1366 */     getLogMergePolicy().setMaxMergeDocs(maxMergeDocs);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getMaxMergeDocs()
/*      */   {
/* 1383 */     return getLogMergePolicy().getMaxMergeDocs();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMaxFieldLength(int maxFieldLength)
/*      */   {
/* 1410 */     ensureOpen();
/* 1411 */     this.maxFieldLength = maxFieldLength;
/* 1412 */     this.docWriter.setMaxFieldLength(maxFieldLength);
/* 1413 */     if (this.infoStream != null)
/* 1414 */       message("setMaxFieldLength " + maxFieldLength);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getMaxFieldLength()
/*      */   {
/* 1425 */     ensureOpen();
/* 1426 */     return this.maxFieldLength;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setReaderTermsIndexDivisor(int divisor)
/*      */   {
/* 1435 */     ensureOpen();
/* 1436 */     this.config.setReaderTermsIndexDivisor(divisor);
/* 1437 */     if (this.infoStream != null)
/* 1438 */       message("setReaderTermsIndexDivisor " + divisor);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getReaderTermsIndexDivisor()
/*      */   {
/* 1448 */     ensureOpen();
/* 1449 */     return this.config.getReaderTermsIndexDivisor();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMaxBufferedDocs(int maxBufferedDocs)
/*      */   {
/* 1474 */     ensureOpen();
/* 1475 */     pushMaxBufferedDocs();
/* 1476 */     if (this.infoStream != null) {
/* 1477 */       message("setMaxBufferedDocs " + maxBufferedDocs);
/*      */     }
/*      */ 
/* 1481 */     this.config.setMaxBufferedDocs(maxBufferedDocs);
/*      */   }
/*      */ 
/*      */   private void pushMaxBufferedDocs()
/*      */   {
/* 1490 */     if (this.config.getMaxBufferedDocs() != -1) {
/* 1491 */       MergePolicy mp = this.mergePolicy;
/* 1492 */       if ((mp instanceof LogDocMergePolicy)) {
/* 1493 */         LogDocMergePolicy lmp = (LogDocMergePolicy)mp;
/* 1494 */         int maxBufferedDocs = this.config.getMaxBufferedDocs();
/* 1495 */         if (lmp.getMinMergeDocs() != maxBufferedDocs) {
/* 1496 */           if (this.infoStream != null)
/* 1497 */             message("now push maxBufferedDocs " + maxBufferedDocs + " to LogDocMergePolicy");
/* 1498 */           lmp.setMinMergeDocs(maxBufferedDocs);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getMaxBufferedDocs()
/*      */   {
/* 1512 */     ensureOpen();
/* 1513 */     return this.config.getMaxBufferedDocs();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setRAMBufferSizeMB(double mb)
/*      */   {
/* 1557 */     if (this.infoStream != null) {
/* 1558 */       message("setRAMBufferSizeMB " + mb);
/*      */     }
/*      */ 
/* 1562 */     this.config.setRAMBufferSizeMB(mb);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public double getRAMBufferSizeMB()
/*      */   {
/* 1571 */     return this.config.getRAMBufferSizeMB();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms)
/*      */   {
/* 1589 */     ensureOpen();
/* 1590 */     if (this.infoStream != null) {
/* 1591 */       message("setMaxBufferedDeleteTerms " + maxBufferedDeleteTerms);
/*      */     }
/*      */ 
/* 1594 */     this.config.setMaxBufferedDeleteTerms(maxBufferedDeleteTerms);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getMaxBufferedDeleteTerms()
/*      */   {
/* 1605 */     ensureOpen();
/* 1606 */     return this.config.getMaxBufferedDeleteTerms();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMergeFactor(int mergeFactor)
/*      */   {
/* 1627 */     getLogMergePolicy().setMergeFactor(mergeFactor);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getMergeFactor()
/*      */   {
/* 1645 */     return getLogMergePolicy().getMergeFactor();
/*      */   }
/*      */ 
/*      */   public static void setDefaultInfoStream(PrintStream infoStream)
/*      */   {
/* 1653 */     defaultInfoStream = infoStream;
/*      */   }
/*      */ 
/*      */   public static PrintStream getDefaultInfoStream()
/*      */   {
/* 1662 */     return defaultInfoStream;
/*      */   }
/*      */ 
/*      */   public void setInfoStream(PrintStream infoStream)
/*      */     throws IOException
/*      */   {
/* 1670 */     ensureOpen();
/* 1671 */     this.infoStream = infoStream;
/* 1672 */     this.docWriter.setInfoStream(infoStream);
/* 1673 */     this.deleter.setInfoStream(infoStream);
/* 1674 */     this.bufferedDeletesStream.setInfoStream(infoStream);
/* 1675 */     if (infoStream != null)
/* 1676 */       messageState();
/*      */   }
/*      */ 
/*      */   private void messageState() throws IOException {
/* 1680 */     message("\ndir=" + this.directory + "\n" + "index=" + segString() + "\n" + "version=" + Constants.LUCENE_VERSION + "\n" + this.config.toString());
/*      */   }
/*      */ 
/*      */   public PrintStream getInfoStream()
/*      */   {
/* 1691 */     ensureOpen();
/* 1692 */     return this.infoStream;
/*      */   }
/*      */ 
/*      */   public boolean verbose()
/*      */   {
/* 1697 */     return this.infoStream != null;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setWriteLockTimeout(long writeLockTimeout)
/*      */   {
/* 1707 */     ensureOpen();
/* 1708 */     this.writeLockTimeout = writeLockTimeout;
/*      */ 
/* 1711 */     this.config.setWriteLockTimeout(writeLockTimeout);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public long getWriteLockTimeout()
/*      */   {
/* 1721 */     ensureOpen();
/* 1722 */     return this.writeLockTimeout;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static void setDefaultWriteLockTimeout(long writeLockTimeout)
/*      */   {
/* 1732 */     IndexWriterConfig.setDefaultWriteLockTimeout(writeLockTimeout);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static long getDefaultWriteLockTimeout()
/*      */   {
/* 1743 */     return IndexWriterConfig.getDefaultWriteLockTimeout();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 1789 */     close(true);
/*      */   }
/*      */ 
/*      */   public void close(boolean waitForMerges)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 1818 */     if (shouldClose())
/*      */     {
/* 1822 */       if (this.hitOOM)
/* 1823 */         rollbackInternal();
/*      */       else
/* 1825 */         closeInternal(waitForMerges);
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized boolean shouldClose()
/*      */   {
/* 1834 */     while (!this.closed) {
/* 1835 */       if (!this.closing) {
/* 1836 */         this.closing = true;
/* 1837 */         return true;
/*      */       }
/*      */ 
/* 1842 */       doWait();
/*      */     }
/*      */ 
/* 1845 */     return false;
/*      */   }
/*      */ 
/*      */   private void closeInternal(boolean waitForMerges) throws CorruptIndexException, IOException
/*      */   {
/*      */     try
/*      */     {
/* 1852 */       if (this.infoStream != null) {
/* 1853 */         message("now flush at close waitForMerges=" + waitForMerges);
/*      */       }
/*      */ 
/* 1856 */       this.docWriter.close();
/*      */ 
/* 1860 */       if (!this.hitOOM) {
/* 1861 */         flush(waitForMerges, true);
/*      */       }
/*      */ 
/* 1864 */       if (waitForMerges)
/*      */       {
/* 1867 */         this.mergeScheduler.merge(this);
/*      */       }
/* 1869 */       this.mergePolicy.close();
/*      */ 
/* 1871 */       synchronized (this) {
/* 1872 */         finishMerges(waitForMerges);
/* 1873 */         this.stopMerges = true;
/*      */       }
/*      */ 
/* 1876 */       this.mergeScheduler.close();
/*      */ 
/* 1878 */       if (this.infoStream != null) {
/* 1879 */         message("now call final commit()");
/*      */       }
/* 1881 */       if (!this.hitOOM) {
/* 1882 */         commitInternal(null);
/*      */       }
/*      */ 
/* 1885 */       if (this.infoStream != null) {
/* 1886 */         message("at close: " + segString());
/*      */       }
/* 1888 */       synchronized (this) {
/* 1889 */         this.readerPool.close();
/* 1890 */         this.docWriter = null;
/* 1891 */         this.deleter.close();
/*      */       }
/*      */ 
/* 1894 */       if (this.writeLock != null) {
/* 1895 */         this.writeLock.release();
/* 1896 */         this.writeLock = null;
/*      */       }
/* 1898 */       synchronized (this) {
/* 1899 */         this.closed = true;
/*      */       }
/*      */     } catch (OutOfMemoryError oom) {
/* 1902 */       handleOOM(oom, "closeInternal");
/*      */     } finally {
/* 1904 */       synchronized (this) {
/* 1905 */         this.closing = false;
/* 1906 */         notifyAll();
/* 1907 */         if ((!this.closed) && 
/* 1908 */           (this.infoStream != null))
/* 1909 */           message("hit exception while closing");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Directory getDirectory()
/*      */   {
/* 1918 */     ensureOpen(false);
/* 1919 */     return this.directory;
/*      */   }
/*      */ 
/*      */   public Analyzer getAnalyzer()
/*      */   {
/* 1924 */     ensureOpen();
/* 1925 */     return this.analyzer;
/*      */   }
/*      */ 
/*      */   public synchronized int maxDoc()
/*      */   {
/*      */     int count;
/*      */     int count;
/* 1934 */     if (this.docWriter != null)
/* 1935 */       count = this.docWriter.getNumDocs();
/*      */     else {
/* 1937 */       count = 0;
/*      */     }
/* 1939 */     count += this.segmentInfos.totalDocCount();
/* 1940 */     return count;
/*      */   }
/*      */ 
/*      */   public synchronized int numDocs()
/*      */     throws IOException
/*      */   {
/*      */     int count;
/*      */     int count;
/* 1951 */     if (this.docWriter != null)
/* 1952 */       count = this.docWriter.getNumDocs();
/*      */     else {
/* 1954 */       count = 0;
/*      */     }
/* 1956 */     for (SegmentInfo info : this.segmentInfos) {
/* 1957 */       count += info.docCount - numDeletedDocs(info);
/*      */     }
/* 1959 */     return count;
/*      */   }
/*      */ 
/*      */   public synchronized boolean hasDeletions() throws IOException {
/* 1963 */     ensureOpen();
/* 1964 */     if (this.bufferedDeletesStream.any()) {
/* 1965 */       return true;
/*      */     }
/* 1967 */     if (this.docWriter.anyDeletions()) {
/* 1968 */       return true;
/*      */     }
/* 1970 */     for (SegmentInfo info : this.segmentInfos) {
/* 1971 */       if (info.hasDeletions()) {
/* 1972 */         return true;
/*      */       }
/*      */     }
/* 1975 */     return false;
/*      */   }
/*      */ 
/*      */   public void addDocument(Document doc)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2041 */     addDocument(doc, this.analyzer);
/*      */   }
/*      */ 
/*      */   public void addDocument(Document doc, Analyzer analyzer)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2062 */     ensureOpen();
/* 2063 */     boolean doFlush = false;
/* 2064 */     boolean success = false;
/*      */     try {
/*      */       try {
/* 2067 */         doFlush = this.docWriter.updateDocument(doc, analyzer, null);
/* 2068 */         success = true;
/*      */       } finally {
/* 2070 */         if ((!success) && (this.infoStream != null))
/* 2071 */           message("hit exception adding document");
/*      */       }
/* 2073 */       if (doFlush)
/* 2074 */         flush(true, false);
/*      */     } catch (OutOfMemoryError oom) {
/* 2076 */       handleOOM(oom, "addDocument");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addDocuments(Collection<Document> docs)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2116 */     addDocuments(docs, this.analyzer);
/*      */   }
/*      */ 
/*      */   public void addDocuments(Collection<Document> docs, Analyzer analyzer)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2132 */     updateDocuments(null, docs, analyzer);
/*      */   }
/*      */ 
/*      */   public void updateDocuments(Term delTerm, Collection<Document> docs)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2150 */     updateDocuments(delTerm, docs, this.analyzer);
/*      */   }
/*      */ 
/*      */   public void updateDocuments(Term delTerm, Collection<Document> docs, Analyzer analyzer)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2169 */     ensureOpen();
/*      */     try {
/* 2171 */       boolean success = false;
/* 2172 */       boolean doFlush = false;
/*      */       try {
/* 2174 */         doFlush = this.docWriter.updateDocuments(docs, analyzer, delTerm);
/* 2175 */         success = true;
/*      */       } finally {
/* 2177 */         if ((!success) && (this.infoStream != null)) {
/* 2178 */           message("hit exception updating document");
/*      */         }
/*      */       }
/* 2181 */       if (doFlush)
/* 2182 */         flush(true, false);
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 2185 */       handleOOM(oom, "updateDocuments");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteDocuments(Term term)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2201 */     ensureOpen();
/*      */     try {
/* 2203 */       if (this.docWriter.deleteTerm(term, false))
/* 2204 */         flush(true, false);
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 2207 */       handleOOM(oom, "deleteDocuments(Term)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteDocuments(Term[] terms)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2225 */     ensureOpen();
/*      */     try {
/* 2227 */       if (this.docWriter.deleteTerms(terms))
/* 2228 */         flush(true, false);
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 2231 */       handleOOM(oom, "deleteDocuments(Term..)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteDocuments(Query query)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2247 */     ensureOpen();
/*      */     try {
/* 2249 */       if (this.docWriter.deleteQuery(query))
/* 2250 */         flush(true, false);
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 2253 */       handleOOM(oom, "deleteDocuments(Query)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteDocuments(Query[] queries)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2271 */     ensureOpen();
/*      */     try {
/* 2273 */       if (this.docWriter.deleteQueries(queries))
/* 2274 */         flush(true, false);
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 2277 */       handleOOM(oom, "deleteDocuments(Query..)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateDocument(Term term, Document doc)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2299 */     ensureOpen();
/* 2300 */     updateDocument(term, doc, getAnalyzer());
/*      */   }
/*      */ 
/*      */   public void updateDocument(Term term, Document doc, Analyzer analyzer)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2323 */     ensureOpen();
/*      */     try {
/* 2325 */       boolean doFlush = false;
/* 2326 */       boolean success = false;
/*      */       try {
/* 2328 */         doFlush = this.docWriter.updateDocument(doc, analyzer, term);
/* 2329 */         success = true;
/*      */       } finally {
/* 2331 */         if ((!success) && (this.infoStream != null))
/* 2332 */           message("hit exception updating document");
/*      */       }
/* 2334 */       if (doFlush)
/* 2335 */         flush(true, false);
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 2338 */       handleOOM(oom, "updateDocument");
/*      */     }
/*      */   }
/*      */ 
/*      */   final synchronized int getSegmentCount()
/*      */   {
/* 2344 */     return this.segmentInfos.size();
/*      */   }
/*      */ 
/*      */   final synchronized int getNumBufferedDocuments()
/*      */   {
/* 2349 */     return this.docWriter.getNumDocs();
/*      */   }
/*      */ 
/*      */   final synchronized int getDocCount(int i)
/*      */   {
/* 2354 */     if ((i >= 0) && (i < this.segmentInfos.size())) {
/* 2355 */       return this.segmentInfos.info(i).docCount;
/*      */     }
/* 2357 */     return -1;
/*      */   }
/*      */ 
/*      */   final int getFlushCount()
/*      */   {
/* 2363 */     return this.flushCount.get();
/*      */   }
/*      */ 
/*      */   final int getFlushDeletesCount()
/*      */   {
/* 2368 */     return this.flushDeletesCount.get();
/*      */   }
/*      */ 
/*      */   final String newSegmentName()
/*      */   {
/* 2374 */     synchronized (this.segmentInfos)
/*      */     {
/* 2380 */       this.changeCount += 1L;
/* 2381 */       this.segmentInfos.changed();
/* 2382 */       return "_" + Integer.toString(this.segmentInfos.counter++, 36);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void optimize()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2456 */     optimize(true);
/*      */   }
/*      */ 
/*      */   public void optimize(int maxNumSegments)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2472 */     optimize(maxNumSegments, true);
/*      */   }
/*      */ 
/*      */   public void optimize(boolean doWait)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2486 */     optimize(1, doWait);
/*      */   }
/*      */ 
/*      */   public void optimize(int maxNumSegments, boolean doWait)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2500 */     ensureOpen();
/*      */ 
/* 2502 */     if (maxNumSegments < 1) {
/* 2503 */       throw new IllegalArgumentException("maxNumSegments must be >= 1; got " + maxNumSegments);
/*      */     }
/* 2505 */     if (this.infoStream != null) {
/* 2506 */       message("optimize: index now " + segString());
/* 2507 */       message("now flush at optimize");
/*      */     }
/*      */ 
/* 2510 */     flush(true, true);
/*      */ 
/* 2512 */     synchronized (this) {
/* 2513 */       resetMergeExceptions();
/* 2514 */       this.segmentsToOptimize.clear();
/* 2515 */       for (SegmentInfo info : this.segmentInfos) {
/* 2516 */         this.segmentsToOptimize.put(info, Boolean.TRUE);
/*      */       }
/* 2518 */       this.optimizeMaxNumSegments = maxNumSegments;
/*      */ 
/* 2522 */       for (MergePolicy.OneMerge merge : this.pendingMerges) {
/* 2523 */         merge.optimize = true;
/* 2524 */         merge.maxNumSegmentsOptimize = maxNumSegments;
/* 2525 */         this.segmentsToOptimize.put(merge.info, Boolean.TRUE);
/*      */       }
/*      */ 
/* 2528 */       for (MergePolicy.OneMerge merge : this.runningMerges) {
/* 2529 */         merge.optimize = true;
/* 2530 */         merge.maxNumSegmentsOptimize = maxNumSegments;
/* 2531 */         this.segmentsToOptimize.put(merge.info, Boolean.TRUE);
/*      */       }
/*      */     }
/*      */ 
/* 2535 */     maybeMerge(maxNumSegments, true);
/*      */ 
/* 2537 */     if (doWait) {
/* 2538 */       synchronized (this)
/*      */       {
/*      */         while (true) {
/* 2541 */           if (this.hitOOM) {
/* 2542 */             throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot complete optimize");
/*      */           }
/*      */ 
/* 2545 */           if (this.mergeExceptions.size() > 0)
/*      */           {
/* 2548 */             int size = this.mergeExceptions.size();
/* 2549 */             for (int i = 0; i < size; i++) {
/* 2550 */               MergePolicy.OneMerge merge = (MergePolicy.OneMerge)this.mergeExceptions.get(i);
/* 2551 */               if (merge.optimize) {
/* 2552 */                 IOException err = new IOException("background merge hit exception: " + merge.segString(this.directory));
/* 2553 */                 Throwable t = merge.getException();
/* 2554 */                 if (t != null)
/* 2555 */                   err.initCause(t);
/* 2556 */                 throw err;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2561 */           if (!optimizeMergesPending()) break;
/* 2562 */           doWait();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2572 */       ensureOpen();
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized boolean optimizeMergesPending()
/*      */   {
/* 2583 */     for (MergePolicy.OneMerge merge : this.pendingMerges) {
/* 2584 */       if (merge.optimize) {
/* 2585 */         return true;
/*      */       }
/*      */     }
/* 2588 */     for (MergePolicy.OneMerge merge : this.runningMerges) {
/* 2589 */       if (merge.optimize) {
/* 2590 */         return true;
/*      */       }
/*      */     }
/* 2593 */     return false;
/*      */   }
/*      */ 
/*      */   public void expungeDeletes(boolean doWait)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2613 */     ensureOpen();
/*      */ 
/* 2615 */     flush(true, true);
/*      */ 
/* 2617 */     if (this.infoStream != null)
/* 2618 */       message("expungeDeletes: index now " + segString());
/*      */     MergePolicy.MergeSpecification spec;
/* 2622 */     synchronized (this) {
/* 2623 */       spec = this.mergePolicy.findMergesToExpungeDeletes(this.segmentInfos);
/* 2624 */       if (spec != null) {
/* 2625 */         int numMerges = spec.merges.size();
/* 2626 */         for (int i = 0; i < numMerges; i++) {
/* 2627 */           registerMerge((MergePolicy.OneMerge)spec.merges.get(i));
/*      */         }
/*      */       }
/*      */     }
/* 2631 */     this.mergeScheduler.merge(this);
/*      */ 
/* 2633 */     if ((spec != null) && (doWait)) {
/* 2634 */       int numMerges = spec.merges.size();
/* 2635 */       synchronized (this) {
/* 2636 */         boolean running = true;
/* 2637 */         while (running)
/*      */         {
/* 2639 */           if (this.hitOOM) {
/* 2640 */             throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot complete expungeDeletes");
/*      */           }
/*      */ 
/* 2646 */           running = false;
/* 2647 */           for (int i = 0; i < numMerges; i++) {
/* 2648 */             MergePolicy.OneMerge merge = (MergePolicy.OneMerge)spec.merges.get(i);
/* 2649 */             if ((this.pendingMerges.contains(merge)) || (this.runningMerges.contains(merge)))
/* 2650 */               running = true;
/* 2651 */             Throwable t = merge.getException();
/* 2652 */             if (t != null) {
/* 2653 */               IOException ioe = new IOException("background merge hit exception: " + merge.segString(this.directory));
/* 2654 */               ioe.initCause(t);
/* 2655 */               throw ioe;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2660 */           if (running)
/* 2661 */             doWait();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void expungeDeletes()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2698 */     expungeDeletes(true);
/*      */   }
/*      */ 
/*      */   public final void maybeMerge()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 2716 */     maybeMerge(false);
/*      */   }
/*      */ 
/*      */   private final void maybeMerge(boolean optimize) throws CorruptIndexException, IOException {
/* 2720 */     maybeMerge(1, optimize);
/*      */   }
/*      */ 
/*      */   private final void maybeMerge(int maxNumSegmentsOptimize, boolean optimize) throws CorruptIndexException, IOException {
/* 2724 */     updatePendingMerges(maxNumSegmentsOptimize, optimize);
/* 2725 */     this.mergeScheduler.merge(this);
/*      */   }
/*      */ 
/*      */   private synchronized void updatePendingMerges(int maxNumSegmentsOptimize, boolean optimize) throws CorruptIndexException, IOException
/*      */   {
/* 2730 */     assert ((!optimize) || (maxNumSegmentsOptimize > 0));
/*      */ 
/* 2732 */     if (this.stopMerges) {
/* 2733 */       return;
/*      */     }
/*      */ 
/* 2737 */     if (this.hitOOM)
/* 2738 */       return;
/*      */     MergePolicy.MergeSpecification spec;
/* 2742 */     if (optimize) {
/* 2743 */       MergePolicy.MergeSpecification spec = this.mergePolicy.findMergesForOptimize(this.segmentInfos, maxNumSegmentsOptimize, Collections.unmodifiableMap(this.segmentsToOptimize));
/* 2744 */       if (spec != null) {
/* 2745 */         int numMerges = spec.merges.size();
/* 2746 */         for (int i = 0; i < numMerges; i++) {
/* 2747 */           MergePolicy.OneMerge merge = (MergePolicy.OneMerge)spec.merges.get(i);
/* 2748 */           merge.optimize = true;
/* 2749 */           merge.maxNumSegmentsOptimize = maxNumSegmentsOptimize;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2754 */       spec = this.mergePolicy.findMerges(this.segmentInfos);
/*      */     }
/*      */ 
/* 2757 */     if (spec != null) {
/* 2758 */       int numMerges = spec.merges.size();
/* 2759 */       for (int i = 0; i < numMerges; i++)
/* 2760 */         registerMerge((MergePolicy.OneMerge)spec.merges.get(i));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized Collection<SegmentInfo> getMergingSegments()
/*      */   {
/* 2774 */     return this.mergingSegments;
/*      */   }
/*      */ 
/*      */   public synchronized MergePolicy.OneMerge getNextMerge()
/*      */   {
/* 2784 */     if (this.pendingMerges.size() == 0) {
/* 2785 */       return null;
/*      */     }
/*      */ 
/* 2788 */     MergePolicy.OneMerge merge = (MergePolicy.OneMerge)this.pendingMerges.removeFirst();
/* 2789 */     this.runningMerges.add(merge);
/* 2790 */     return merge;
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws IOException
/*      */   {
/* 2806 */     ensureOpen();
/*      */ 
/* 2809 */     if (shouldClose())
/* 2810 */       rollbackInternal();
/*      */   }
/*      */ 
/*      */   private void rollbackInternal() throws IOException
/*      */   {
/* 2815 */     boolean success = false;
/*      */ 
/* 2817 */     if (this.infoStream != null) {
/* 2818 */       message("rollback");
/*      */     }
/*      */     try
/*      */     {
/* 2822 */       synchronized (this) {
/* 2823 */         finishMerges(false);
/* 2824 */         this.stopMerges = true;
/*      */       }
/*      */ 
/* 2827 */       if (this.infoStream != null) {
/* 2828 */         message("rollback: done finish merges");
/*      */       }
/*      */ 
/* 2834 */       this.mergePolicy.close();
/* 2835 */       this.mergeScheduler.close();
/*      */ 
/* 2837 */       this.bufferedDeletesStream.clear();
/*      */ 
/* 2839 */       synchronized (this)
/*      */       {
/* 2841 */         if (this.pendingCommit != null) {
/* 2842 */           this.pendingCommit.rollbackCommit(this.directory);
/* 2843 */           this.deleter.decRef(this.pendingCommit);
/* 2844 */           this.pendingCommit = null;
/* 2845 */           notifyAll();
/*      */         }
/*      */ 
/* 2853 */         this.segmentInfos.rollbackSegmentInfos(this.rollbackSegments);
/* 2854 */         if (this.infoStream != null) {
/* 2855 */           message("rollback: infos=" + segString(this.segmentInfos));
/*      */         }
/*      */ 
/* 2858 */         this.docWriter.abort();
/*      */ 
/* 2860 */         assert (testPoint("rollback before checkpoint"));
/*      */ 
/* 2864 */         this.deleter.checkpoint(this.segmentInfos, false);
/* 2865 */         this.deleter.refresh();
/*      */       }
/*      */ 
/* 2869 */       this.readerPool.clear(null);
/*      */ 
/* 2871 */       this.lastCommitChangeCount = this.changeCount;
/*      */ 
/* 2873 */       success = true;
/*      */     } catch (OutOfMemoryError oom) {
/* 2875 */       handleOOM(oom, "rollbackInternal");
/*      */     } finally {
/* 2877 */       synchronized (this) {
/* 2878 */         if (!success) {
/* 2879 */           this.closing = false;
/* 2880 */           notifyAll();
/* 2881 */           if (this.infoStream != null) {
/* 2882 */             message("hit exception during rollback");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2887 */     closeInternal(false);
/*      */   }
/*      */ 
/*      */   public synchronized void deleteAll()
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/* 2910 */       finishMerges(false);
/*      */ 
/* 2913 */       this.docWriter.abort();
/*      */ 
/* 2916 */       this.segmentInfos.clear();
/*      */ 
/* 2919 */       this.deleter.checkpoint(this.segmentInfos, false);
/* 2920 */       this.deleter.refresh();
/*      */ 
/* 2923 */       this.readerPool.dropAll();
/*      */ 
/* 2926 */       this.changeCount += 1L;
/* 2927 */       this.segmentInfos.changed();
/*      */     } catch (OutOfMemoryError oom) {
/* 2929 */       handleOOM(oom, "deleteAll");
/*      */     } finally {
/* 2931 */       if (this.infoStream != null)
/* 2932 */         message("hit exception during deleteAll");
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void finishMerges(boolean waitForMerges) throws IOException
/*      */   {
/* 2938 */     if (!waitForMerges)
/*      */     {
/* 2940 */       this.stopMerges = true;
/*      */ 
/* 2943 */       for (MergePolicy.OneMerge merge : this.pendingMerges) {
/* 2944 */         if (this.infoStream != null)
/* 2945 */           message("now abort pending merge " + merge.segString(this.directory));
/* 2946 */         merge.abort();
/* 2947 */         mergeFinish(merge);
/*      */       }
/* 2949 */       this.pendingMerges.clear();
/*      */ 
/* 2951 */       for (MergePolicy.OneMerge merge : this.runningMerges) {
/* 2952 */         if (this.infoStream != null)
/* 2953 */           message("now abort running merge " + merge.segString(this.directory));
/* 2954 */         merge.abort();
/*      */       }
/*      */ 
/* 2962 */       while (this.runningMerges.size() > 0) {
/* 2963 */         if (this.infoStream != null)
/* 2964 */           message("now wait for " + this.runningMerges.size() + " running merge to abort");
/* 2965 */         doWait();
/*      */       }
/*      */ 
/* 2968 */       this.stopMerges = false;
/* 2969 */       notifyAll();
/*      */ 
/* 2971 */       assert (0 == this.mergingSegments.size());
/*      */ 
/* 2973 */       if (this.infoStream != null) {
/* 2974 */         message("all running merges have aborted");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2982 */       waitForMerges();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void waitForMerges()
/*      */   {
/* 2993 */     if (this.infoStream != null) {
/* 2994 */       message("waitForMerges");
/*      */     }
/* 2996 */     while ((this.pendingMerges.size() > 0) || (this.runningMerges.size() > 0)) {
/* 2997 */       doWait();
/*      */     }
/*      */ 
/* 3001 */     assert (0 == this.mergingSegments.size());
/*      */ 
/* 3003 */     if (this.infoStream != null)
/* 3004 */       message("waitForMerges done");
/*      */   }
/*      */ 
/*      */   synchronized void checkpoint()
/*      */     throws IOException
/*      */   {
/* 3014 */     this.changeCount += 1L;
/* 3015 */     this.segmentInfos.changed();
/* 3016 */     this.deleter.checkpoint(this.segmentInfos, false);
/*      */   }
/*      */ 
/*      */   private synchronized void resetMergeExceptions() {
/* 3020 */     this.mergeExceptions = new ArrayList();
/* 3021 */     this.mergeGen += 1L;
/*      */   }
/*      */ 
/*      */   private void noDupDirs(Directory[] dirs) {
/* 3025 */     HashSet dups = new HashSet();
/* 3026 */     for (Directory dir : dirs) {
/* 3027 */       if (dups.contains(dir))
/* 3028 */         throw new IllegalArgumentException("Directory " + dir + " appears more than once");
/* 3029 */       if (dir == this.directory)
/* 3030 */         throw new IllegalArgumentException("Cannot add directory to itself");
/* 3031 */       dups.add(dir);
/*      */     }
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void addIndexesNoOptimize(Directory[] dirs)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3041 */     addIndexes(dirs);
/*      */   }
/*      */ 
/*      */   public void addIndexes(IndexReader[] readers)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3074 */     ensureOpen();
/*      */     try
/*      */     {
/* 3077 */       if (this.infoStream != null)
/* 3078 */         message("flush at addIndexes(IndexReader...)");
/* 3079 */       flush(false, true);
/*      */ 
/* 3081 */       String mergedName = newSegmentName();
/*      */ 
/* 3084 */       SegmentMerger merger = new SegmentMerger(this.directory, this.config.getTermIndexInterval(), mergedName, null, this.payloadProcessorProvider, (FieldInfos)this.docWriter.getFieldInfos().clone());
/*      */ 
/* 3088 */       for (IndexReader reader : readers) {
/* 3089 */         merger.add(reader);
/*      */       }
/* 3091 */       int docCount = merger.merge();
/*      */ 
/* 3093 */       SegmentInfo info = new SegmentInfo(mergedName, docCount, this.directory, false, true, merger.fieldInfos().hasProx(), merger.fieldInfos().hasVectors());
/*      */ 
/* 3097 */       setDiagnostics(info, "addIndexes(IndexReader...)");
/*      */       boolean useCompoundFile;
/* 3100 */       synchronized (this) {
/* 3101 */         if (this.stopMerges) {
/* 3102 */           this.deleter.deleteNewFiles(info.files());
/* 3103 */           return;
/*      */         }
/* 3105 */         ensureOpen();
/* 3106 */         useCompoundFile = this.mergePolicy.useCompoundFile(this.segmentInfos, info);
/*      */       }
/*      */ 
/* 3110 */       if (useCompoundFile) {
/* 3111 */         merger.createCompoundFile(mergedName + ".cfs", info);
/*      */ 
/* 3115 */         synchronized (this) {
/* 3116 */           this.deleter.deleteNewFiles(info.files());
/*      */         }
/* 3118 */         info.setUseCompoundFile(true);
/*      */       }
/*      */ 
/* 3122 */       synchronized (this) {
/* 3123 */         if (this.stopMerges) {
/* 3124 */           this.deleter.deleteNewFiles(info.files());
/* 3125 */           return;
/*      */         }
/* 3127 */         ensureOpen();
/* 3128 */         this.segmentInfos.add(info);
/* 3129 */         checkpoint();
/*      */       }
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 3133 */       handleOOM(oom, "addIndexes(IndexReader...)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addIndexes(Directory[] dirs)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3186 */     ensureOpen();
/*      */ 
/* 3188 */     noDupDirs(dirs);
/*      */     try
/*      */     {
/* 3191 */       if (this.infoStream != null)
/* 3192 */         message("flush at addIndexes(Directory...)");
/* 3193 */       flush(false, true);
/*      */ 
/* 3195 */       int docCount = 0;
/* 3196 */       List infos = new ArrayList();
/* 3197 */       Comparator versionComparator = StringHelper.getVersionComparator();
/*      */       Set dsFilesCopied;
/*      */       Map dsNames;
/* 3198 */       for (Directory dir : dirs) {
/* 3199 */         if (this.infoStream != null) {
/* 3200 */           message("addIndexes: process directory " + dir);
/*      */         }
/* 3202 */         SegmentInfos sis = new SegmentInfos();
/* 3203 */         sis.read(dir);
/* 3204 */         dsFilesCopied = new HashSet();
/* 3205 */         dsNames = new HashMap();
/* 3206 */         for (SegmentInfo info : sis) {
/* 3207 */           assert (!infos.contains(info)) : ("dup info dir=" + info.dir + " name=" + info.name);
/*      */ 
/* 3209 */           docCount += info.docCount;
/* 3210 */           String newSegName = newSegmentName();
/* 3211 */           String dsName = info.getDocStoreSegment();
/*      */ 
/* 3213 */           if (this.infoStream != null)
/* 3214 */             message("addIndexes: process segment origName=" + info.name + " newName=" + newSegName + " dsName=" + dsName + " info=" + info);
/*      */           boolean createCFS;
/* 3220 */           synchronized (this) {
/* 3221 */             createCFS = (!info.getUseCompoundFile()) && (this.mergePolicy.useCompoundFile(this.segmentInfos, info)) && (versionComparator.compare(info.getVersion(), "3.1") >= 0);
/*      */           }
/*      */ 
/* 3227 */           if (createCFS)
/* 3228 */             copySegmentIntoCFS(info, newSegName);
/*      */           else {
/* 3230 */             copySegmentAsIs(info, newSegName, dsNames, dsFilesCopied);
/*      */           }
/* 3232 */           infos.add(info);
/*      */         }
/*      */       }
/*      */ 
/* 3236 */       synchronized (this) {
/* 3237 */         ensureOpen();
/* 3238 */         this.segmentInfos.addAll(infos);
/* 3239 */         checkpoint();
/*      */       }
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 3243 */       handleOOM(oom, "addIndexes(Directory...)");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void copySegmentIntoCFS(SegmentInfo info, String segName) throws IOException
/*      */   {
/* 3249 */     String segFileName = IndexFileNames.segmentFileName(segName, "cfs");
/* 3250 */     Collection files = info.files();
/* 3251 */     CompoundFileWriter cfsWriter = new CompoundFileWriter(this.directory, segFileName);
/* 3252 */     for (String file : files) {
/* 3253 */       String newFileName = segName + IndexFileNames.stripSegmentName(file);
/* 3254 */       if ((!IndexFileNames.matchesExtension(file, "del")) && (!IndexFileNames.isSeparateNormsFile(file)))
/*      */       {
/* 3256 */         cfsWriter.addFile(file, info.dir);
/*      */       } else {
/* 3258 */         assert (!this.directory.fileExists(newFileName)) : ("file \"" + newFileName + "\" already exists");
/* 3259 */         info.dir.copy(this.directory, file, newFileName);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3264 */     cfsWriter.close();
/*      */ 
/* 3266 */     info.dir = this.directory;
/* 3267 */     info.name = segName;
/* 3268 */     info.setUseCompoundFile(true);
/*      */   }
/*      */ 
/*      */   private void copySegmentAsIs(SegmentInfo info, String segName, Map<String, String> dsNames, Set<String> dsFilesCopied)
/*      */     throws IOException
/*      */   {
/* 3282 */     String dsName = info.getDocStoreSegment();
/*      */     String newDsName;
/*      */     String newDsName;
/* 3284 */     if (dsName != null)
/*      */     {
/*      */       String newDsName;
/* 3285 */       if (dsNames.containsKey(dsName)) {
/* 3286 */         newDsName = (String)dsNames.get(dsName);
/*      */       } else {
/* 3288 */         dsNames.put(dsName, segName);
/* 3289 */         newDsName = segName;
/*      */       }
/*      */     } else {
/* 3292 */       newDsName = segName;
/*      */     }
/*      */ 
/* 3296 */     for (String file : info.files())
/*      */     {
/*      */       String newFileName;
/* 3298 */       if (IndexFileNames.isDocStoreFile(file)) {
/* 3299 */         String newFileName = newDsName + IndexFileNames.stripSegmentName(file);
/* 3300 */         if (dsFilesCopied.contains(newFileName)) {
/*      */           continue;
/*      */         }
/* 3303 */         dsFilesCopied.add(newFileName);
/*      */       } else {
/* 3305 */         newFileName = segName + IndexFileNames.stripSegmentName(file);
/*      */       }
/*      */ 
/* 3308 */       assert (!this.directory.fileExists(newFileName)) : ("file \"" + newFileName + "\" already exists");
/* 3309 */       info.dir.copy(this.directory, file, newFileName);
/*      */     }
/*      */ 
/* 3312 */     info.setDocStore(info.getDocStoreOffset(), newDsName, info.getDocStoreIsCompoundFile());
/* 3313 */     info.dir = this.directory;
/* 3314 */     info.name = segName;
/*      */   }
/*      */ 
/*      */   protected void doAfterFlush()
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void doBeforeFlush()
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   public final void prepareCommit()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3338 */     ensureOpen();
/* 3339 */     prepareCommit(null);
/*      */   }
/*      */ 
/*      */   public final void prepareCommit(Map<String, String> commitUserData)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3375 */     if (this.hitOOM) {
/* 3376 */       throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot commit");
/*      */     }
/*      */ 
/* 3380 */     if (this.pendingCommit != null) {
/* 3381 */       throw new IllegalStateException("prepareCommit was already called with no corresponding call to commit");
/*      */     }
/*      */ 
/* 3384 */     if (this.infoStream != null) {
/* 3385 */       message("prepareCommit: flush");
/*      */     }
/* 3387 */     ensureOpen(false);
/* 3388 */     boolean anySegmentsFlushed = false;
/* 3389 */     SegmentInfos toCommit = null;
/* 3390 */     boolean success = false;
/*      */     try {
/*      */       try {
/* 3393 */         synchronized (this) {
/* 3394 */           anySegmentsFlushed = doFlush(true);
/* 3395 */           this.readerPool.commit(this.segmentInfos);
/* 3396 */           toCommit = (SegmentInfos)this.segmentInfos.clone();
/* 3397 */           this.pendingCommitChangeCount = this.changeCount;
/*      */ 
/* 3403 */           this.deleter.incRef(toCommit, false);
/*      */         }
/* 3405 */         success = true;
/*      */       } finally {
/* 3407 */         if ((!success) && (this.infoStream != null)) {
/* 3408 */           message("hit exception during prepareCommit");
/*      */         }
/* 3410 */         doAfterFlush();
/*      */       }
/*      */     } catch (OutOfMemoryError oom) {
/* 3413 */       handleOOM(oom, "prepareCommit");
/*      */     }
/*      */ 
/* 3416 */     success = false;
/*      */     try {
/* 3418 */       if (anySegmentsFlushed) {
/* 3419 */         maybeMerge();
/*      */       }
/* 3421 */       success = true;
/*      */     } finally {
/* 3423 */       if (!success) {
/* 3424 */         synchronized (this) {
/* 3425 */           this.deleter.decRef(toCommit);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3430 */     startCommit(toCommit, commitUserData);
/*      */   }
/*      */ 
/*      */   public final void commit()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3467 */     commit(null);
/*      */   }
/*      */ 
/*      */   public final void commit(Map<String, String> commitUserData)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3481 */     ensureOpen();
/*      */ 
/* 3483 */     commitInternal(commitUserData);
/*      */   }
/*      */ 
/*      */   private final void commitInternal(Map<String, String> commitUserData) throws CorruptIndexException, IOException
/*      */   {
/* 3488 */     if (this.infoStream != null) {
/* 3489 */       message("commit: start");
/*      */     }
/*      */ 
/* 3492 */     synchronized (this.commitLock) {
/* 3493 */       if (this.infoStream != null) {
/* 3494 */         message("commit: enter lock");
/*      */       }
/*      */ 
/* 3497 */       if (this.pendingCommit == null) {
/* 3498 */         if (this.infoStream != null) {
/* 3499 */           message("commit: now prepare");
/*      */         }
/* 3501 */         prepareCommit(commitUserData);
/* 3502 */       } else if (this.infoStream != null) {
/* 3503 */         message("commit: already prepared");
/*      */       }
/*      */ 
/* 3506 */       finishCommit();
/*      */     }
/*      */   }
/*      */ 
/*      */   private final synchronized void finishCommit() throws CorruptIndexException, IOException
/*      */   {
/* 3512 */     if (this.pendingCommit != null) {
/*      */       try {
/* 3514 */         if (this.infoStream != null)
/* 3515 */           message("commit: pendingCommit != null");
/* 3516 */         this.pendingCommit.finishCommit(this.directory);
/* 3517 */         if (this.infoStream != null)
/* 3518 */           message("commit: wrote segments file \"" + this.pendingCommit.getCurrentSegmentFileName() + "\"");
/* 3519 */         this.lastCommitChangeCount = this.pendingCommitChangeCount;
/* 3520 */         this.segmentInfos.updateGeneration(this.pendingCommit);
/* 3521 */         this.segmentInfos.setUserData(this.pendingCommit.getUserData());
/* 3522 */         this.rollbackSegments = this.pendingCommit.createBackupSegmentInfos(true);
/* 3523 */         this.deleter.checkpoint(this.pendingCommit, true);
/*      */       }
/*      */       finally {
/* 3526 */         this.deleter.decRef(this.pendingCommit);
/* 3527 */         this.pendingCommit = null;
/* 3528 */         notifyAll();
/*      */       }
/*      */     }
/* 3531 */     else if (this.infoStream != null) {
/* 3532 */       message("commit: pendingCommit == null; skip");
/*      */     }
/*      */ 
/* 3535 */     if (this.infoStream != null)
/* 3536 */       message("commit: done");
/*      */   }
/*      */ 
/*      */   protected final void flush(boolean triggerMerge, boolean flushDocStores, boolean flushDeletes)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3544 */     flush(triggerMerge, flushDeletes);
/*      */   }
/*      */ 
/*      */   protected final void flush(boolean triggerMerge, boolean applyAllDeletes)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3564 */     ensureOpen(false);
/* 3565 */     if ((doFlush(applyAllDeletes)) && (triggerMerge))
/* 3566 */       maybeMerge();
/*      */   }
/*      */ 
/*      */   private synchronized boolean doFlush(boolean applyAllDeletes)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3575 */     if (this.hitOOM) {
/* 3576 */       throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot flush");
/*      */     }
/*      */ 
/* 3579 */     doBeforeFlush();
/*      */ 
/* 3581 */     assert (testPoint("startDoFlush"));
/*      */ 
/* 3589 */     this.flushControl.setFlushPendingNoWait("explicit flush");
/*      */ 
/* 3591 */     boolean success = false;
/*      */     try
/*      */     {
/* 3595 */       if (this.infoStream != null) {
/* 3596 */         message("  start flush: applyAllDeletes=" + applyAllDeletes);
/* 3597 */         message("  index before flush " + segString());
/*      */       }
/*      */ 
/* 3600 */       SegmentInfo newSegment = this.docWriter.flush(this, this.deleter, this.mergePolicy, this.segmentInfos);
/* 3601 */       if (newSegment != null) {
/* 3602 */         setDiagnostics(newSegment, "flush");
/* 3603 */         this.segmentInfos.add(newSegment);
/* 3604 */         checkpoint();
/*      */       }
/*      */ 
/* 3607 */       if (!applyAllDeletes)
/*      */       {
/* 3612 */         if ((this.flushControl.getFlushDeletes()) || ((this.config.getRAMBufferSizeMB() != -1.0D) && (this.bufferedDeletesStream.bytesUsed() > 1048576.0D * this.config.getRAMBufferSizeMB() / 2.0D)))
/*      */         {
/* 3615 */           applyAllDeletes = true;
/* 3616 */           if (this.infoStream != null) {
/* 3617 */             message("force apply deletes bytesUsed=" + this.bufferedDeletesStream.bytesUsed() + " vs ramBuffer=" + 1048576.0D * this.config.getRAMBufferSizeMB());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3622 */       if (applyAllDeletes) {
/* 3623 */         if (this.infoStream != null) {
/* 3624 */           message("apply all deletes during flush");
/*      */         }
/*      */ 
/* 3627 */         this.flushDeletesCount.incrementAndGet();
/* 3628 */         result = this.bufferedDeletesStream.applyDeletes(this.readerPool, this.segmentInfos.asList());
/*      */ 
/* 3630 */         if (result.anyDeletes) {
/* 3631 */           checkpoint();
/*      */         }
/* 3633 */         if ((!this.keepFullyDeletedSegments) && (result.allDeleted != null)) {
/* 3634 */           if (this.infoStream != null) {
/* 3635 */             message("drop 100% deleted segments: " + result.allDeleted);
/*      */           }
/* 3637 */           for (SegmentInfo info : result.allDeleted)
/*      */           {
/* 3642 */             if (!this.mergingSegments.contains(info)) {
/* 3643 */               this.segmentInfos.remove(info);
/* 3644 */               if (this.readerPool != null) {
/* 3645 */                 this.readerPool.drop(info);
/*      */               }
/*      */             }
/*      */           }
/* 3649 */           checkpoint();
/*      */         }
/* 3651 */         this.bufferedDeletesStream.prune(this.segmentInfos);
/*      */ 
/* 3653 */         assert (!this.bufferedDeletesStream.any());
/* 3654 */         this.flushControl.clearDeletes();
/* 3655 */       } else if (this.infoStream != null) {
/* 3656 */         message("don't apply deletes now delTermCount=" + this.bufferedDeletesStream.numTerms() + " bytesUsed=" + this.bufferedDeletesStream.bytesUsed());
/*      */       }
/*      */ 
/* 3660 */       doAfterFlush();
/* 3661 */       this.flushCount.incrementAndGet();
/*      */ 
/* 3663 */       success = true;
/*      */ 
/* 3665 */       result = newSegment != null ? 1 : 0;
/*      */       return result;
/*      */     }
/*      */     catch (OutOfMemoryError oom)
/*      */     {
/* 3668 */       handleOOM(oom, "doFlush");
/*      */ 
/* 3670 */       BufferedDeletesStream.ApplyDeletesResult result = 0;
/*      */       return result;
/*      */     }
/*      */     finally
/*      */     {
/* 3672 */       this.flushControl.clearFlushPending();
/* 3673 */       if ((!success) && (this.infoStream != null))
/* 3674 */         message("hit exception during flush"); 
/* 3674 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public final long ramSizeInBytes()
/*      */   {
/* 3682 */     ensureOpen();
/* 3683 */     return this.docWriter.bytesUsed() + this.bufferedDeletesStream.bytesUsed();
/*      */   }
/*      */ 
/*      */   public final synchronized int numRamDocs()
/*      */   {
/* 3689 */     ensureOpen();
/* 3690 */     return this.docWriter.getNumDocs();
/*      */   }
/*      */ 
/*      */   private void ensureValidMerge(MergePolicy.OneMerge merge) throws IOException {
/* 3694 */     for (SegmentInfo info : merge.segments)
/* 3695 */       if (!this.segmentInfos.contains(info))
/* 3696 */         throw new MergePolicy.MergeException("MergePolicy selected a segment (" + info.name + ") that is not in the current index " + segString(), this.directory);
/*      */   }
/*      */ 
/*      */   private synchronized void commitMergedDeletes(MergePolicy.OneMerge merge, SegmentReader mergedReader)
/*      */     throws IOException
/*      */   {
/* 3712 */     assert (testPoint("startCommitMergeDeletes"));
/*      */ 
/* 3714 */     List sourceSegments = merge.segments;
/*      */ 
/* 3716 */     if (this.infoStream != null) {
/* 3717 */       message("commitMergeDeletes " + merge.segString(this.directory));
/*      */     }
/*      */ 
/* 3721 */     int docUpto = 0;
/* 3722 */     int delCount = 0;
/* 3723 */     long minGen = 9223372036854775807L;
/*      */ 
/* 3725 */     for (int i = 0; i < sourceSegments.size(); i++) {
/* 3726 */       SegmentInfo info = (SegmentInfo)sourceSegments.get(i);
/* 3727 */       minGen = Math.min(info.getBufferedDeletesGen(), minGen);
/* 3728 */       int docCount = info.docCount;
/* 3729 */       SegmentReader previousReader = (SegmentReader)merge.readerClones.get(i);
/* 3730 */       if (previousReader == null)
/*      */       {
/*      */         continue;
/*      */       }
/* 3734 */       SegmentReader currentReader = (SegmentReader)merge.readers.get(i);
/* 3735 */       if (previousReader.hasDeletions())
/*      */       {
/* 3744 */         if (currentReader.numDeletedDocs() > previousReader.numDeletedDocs())
/*      */         {
/* 3748 */           for (int j = 0; j < docCount; j++) {
/* 3749 */             if (previousReader.isDeleted(j)) {
/* 3750 */               if (($assertionsDisabled) || (currentReader.isDeleted(j))) continue; throw new AssertionError();
/*      */             }
/* 3752 */             if (currentReader.isDeleted(j)) {
/* 3753 */               mergedReader.doDelete(docUpto);
/* 3754 */               delCount++;
/*      */             }
/* 3756 */             docUpto++;
/*      */           }
/*      */         }
/*      */         else
/* 3760 */           docUpto += docCount - previousReader.numDeletedDocs();
/*      */       }
/* 3762 */       else if (currentReader.hasDeletions())
/*      */       {
/* 3765 */         for (int j = 0; j < docCount; j++) {
/* 3766 */           if (currentReader.isDeleted(j)) {
/* 3767 */             mergedReader.doDelete(docUpto);
/* 3768 */             delCount++;
/*      */           }
/* 3770 */           docUpto++;
/*      */         }
/*      */       }
/*      */       else {
/* 3774 */         docUpto += info.docCount;
/*      */       }
/*      */     }
/* 3777 */     assert (mergedReader.numDeletedDocs() == delCount);
/*      */ 
/* 3779 */     mergedReader.hasChanges = (delCount > 0);
/*      */ 
/* 3786 */     assert ((!mergedReader.hasChanges) || (minGen > mergedReader.getSegmentInfo().getBufferedDeletesGen()));
/*      */ 
/* 3788 */     mergedReader.getSegmentInfo().setBufferedDeletesGen(minGen);
/*      */   }
/*      */ 
/*      */   private synchronized boolean commitMerge(MergePolicy.OneMerge merge, SegmentReader mergedReader) throws IOException
/*      */   {
/* 3793 */     assert (testPoint("startCommitMerge"));
/*      */ 
/* 3795 */     if (this.hitOOM) {
/* 3796 */       throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot complete merge");
/*      */     }
/*      */ 
/* 3799 */     if (this.infoStream != null) {
/* 3800 */       message("commitMerge: " + merge.segString(this.directory) + " index=" + segString());
/*      */     }
/* 3802 */     assert (merge.registerDone);
/*      */ 
/* 3810 */     if (merge.isAborted()) {
/* 3811 */       if (this.infoStream != null)
/* 3812 */         message("commitMerge: skipping merge " + merge.segString(this.directory) + ": it was aborted");
/* 3813 */       return false;
/*      */     }
/*      */ 
/* 3816 */     commitMergedDeletes(merge, mergedReader);
/*      */ 
/* 3823 */     assert (!this.segmentInfos.contains(merge.info));
/*      */ 
/* 3825 */     boolean allDeleted = mergedReader.numDocs() == 0;
/*      */ 
/* 3827 */     if ((this.infoStream != null) && (allDeleted)) {
/* 3828 */       message("merged segment " + merge.info + " is 100% deleted" + (this.keepFullyDeletedSegments ? "" : "; skipping insert"));
/*      */     }
/*      */ 
/* 3831 */     boolean dropSegment = (allDeleted) && (!this.keepFullyDeletedSegments);
/* 3832 */     this.segmentInfos.applyMergeChanges(merge, dropSegment);
/*      */ 
/* 3834 */     if (dropSegment) {
/* 3835 */       this.readerPool.drop(merge.info);
/*      */     }
/*      */ 
/* 3838 */     if (this.infoStream != null) {
/* 3839 */       message("after commit: " + segString());
/*      */     }
/*      */ 
/* 3842 */     closeMergeReaders(merge, false);
/*      */ 
/* 3846 */     checkpoint();
/*      */ 
/* 3851 */     this.readerPool.clear(merge.segments);
/*      */ 
/* 3853 */     if (merge.optimize)
/*      */     {
/* 3855 */       if (!this.segmentsToOptimize.containsKey(merge.info)) {
/* 3856 */         this.segmentsToOptimize.put(merge.info, Boolean.FALSE);
/*      */       }
/*      */     }
/*      */ 
/* 3860 */     return true;
/*      */   }
/*      */ 
/*      */   private final void handleMergeException(Throwable t, MergePolicy.OneMerge merge) throws IOException
/*      */   {
/* 3865 */     if (this.infoStream != null) {
/* 3866 */       message("handleMergeException: merge=" + merge.segString(this.directory) + " exc=" + t);
/*      */     }
/*      */ 
/* 3872 */     merge.setException(t);
/* 3873 */     addMergeException(merge);
/*      */ 
/* 3875 */     if ((t instanceof MergePolicy.MergeAbortedException))
/*      */     {
/* 3882 */       if (merge.isExternal)
/* 3883 */         throw ((MergePolicy.MergeAbortedException)t); 
/*      */     } else {
/* 3884 */       if ((t instanceof IOException))
/* 3885 */         throw ((IOException)t);
/* 3886 */       if ((t instanceof RuntimeException))
/* 3887 */         throw ((RuntimeException)t);
/* 3888 */       if ((t instanceof Error)) {
/* 3889 */         throw ((Error)t);
/*      */       }
/*      */ 
/* 3892 */       throw new RuntimeException(t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void merge(MergePolicy.OneMerge merge)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 3904 */     boolean success = false;
/*      */ 
/* 3906 */     long t0 = System.currentTimeMillis();
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/* 3912 */         mergeInit(merge);
/*      */ 
/* 3914 */         if (this.infoStream != null) {
/* 3915 */           message("now merge\n  merge=" + merge.segString(this.directory) + "\n  merge=" + merge + "\n  index=" + segString());
/*      */         }
/* 3917 */         mergeMiddle(merge);
/* 3918 */         mergeSuccess(merge);
/* 3919 */         success = true;
/*      */       } catch (Throwable t) {
/* 3921 */         handleMergeException(t, merge);
/*      */       }
/*      */       finally {
/* 3924 */         jsr 6; } synchronized (this) {
/* 3925 */         mergeFinish(merge);
/*      */ 
/* 3927 */         if (!success) {
/* 3928 */           if (this.infoStream != null)
/* 3929 */             message("hit exception during merge");
/* 3930 */           if ((merge.info != null) && (!this.segmentInfos.contains(merge.info))) {
/* 3931 */             this.deleter.refresh(merge.info.name);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3937 */         if ((success) && (!merge.isAborted()) && ((merge.optimize) || ((!this.closed) && (!this.closing))))
/* 3938 */           updatePendingMerges(merge.maxNumSegmentsOptimize, merge.optimize);
/*      */       }
/* 3940 */       ret;
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/* 3943 */       handleOOM(oom, "merge");
/*      */     }
/* 3945 */     if ((this.infoStream != null) && (merge.info != null))
/* 3946 */       message("merge time " + (System.currentTimeMillis() - t0) + " msec for " + merge.info.docCount + " docs");
/*      */   }
/*      */ 
/*      */   void mergeSuccess(MergePolicy.OneMerge merge)
/*      */   {
/*      */   }
/*      */ 
/*      */   final synchronized boolean registerMerge(MergePolicy.OneMerge merge)
/*      */     throws MergePolicy.MergeAbortedException, IOException
/*      */   {
/* 3963 */     if (merge.registerDone) {
/* 3964 */       return true;
/*      */     }
/* 3966 */     if (this.stopMerges) {
/* 3967 */       merge.abort();
/* 3968 */       throw new MergePolicy.MergeAbortedException("merge is aborted: " + merge.segString(this.directory));
/*      */     }
/*      */ 
/* 3971 */     boolean isExternal = false;
/* 3972 */     for (SegmentInfo info : merge.segments) {
/* 3973 */       if (this.mergingSegments.contains(info)) {
/* 3974 */         return false;
/*      */       }
/* 3976 */       if (!this.segmentInfos.contains(info)) {
/* 3977 */         return false;
/*      */       }
/* 3979 */       if (info.dir != this.directory) {
/* 3980 */         isExternal = true;
/*      */       }
/* 3982 */       if (this.segmentsToOptimize.containsKey(info)) {
/* 3983 */         merge.optimize = true;
/* 3984 */         merge.maxNumSegmentsOptimize = this.optimizeMaxNumSegments;
/*      */       }
/*      */     }
/*      */ 
/* 3988 */     ensureValidMerge(merge);
/*      */ 
/* 3990 */     this.pendingMerges.add(merge);
/*      */ 
/* 3992 */     if (this.infoStream != null) {
/* 3993 */       message("add merge to pendingMerges: " + merge.segString(this.directory) + " [total " + this.pendingMerges.size() + " pending]");
/*      */     }
/* 3995 */     merge.mergeGen = this.mergeGen;
/* 3996 */     merge.isExternal = isExternal;
/*      */ 
/* 4002 */     message("registerMerge merging=" + this.mergingSegments);
/* 4003 */     for (SegmentInfo info : merge.segments) {
/* 4004 */       message("registerMerge info=" + info);
/* 4005 */       this.mergingSegments.add(info);
/*      */     }
/*      */ 
/* 4009 */     merge.registerDone = true;
/* 4010 */     return true;
/*      */   }
/*      */ 
/*      */   final synchronized void mergeInit(MergePolicy.OneMerge merge)
/*      */     throws IOException
/*      */   {
/* 4016 */     boolean success = false;
/*      */     try {
/* 4018 */       _mergeInit(merge);
/* 4019 */       success = true;
/*      */     } finally {
/* 4021 */       if (!success) {
/* 4022 */         if (this.infoStream != null) {
/* 4023 */           message("hit exception in mergeInit");
/*      */         }
/* 4025 */         mergeFinish(merge);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void _mergeInit(MergePolicy.OneMerge merge) throws IOException
/*      */   {
/* 4032 */     assert (testPoint("startMergeInit"));
/*      */ 
/* 4034 */     assert (merge.registerDone);
/* 4035 */     assert ((!merge.optimize) || (merge.maxNumSegmentsOptimize > 0));
/*      */ 
/* 4037 */     if (this.hitOOM) {
/* 4038 */       throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot merge");
/*      */     }
/*      */ 
/* 4044 */     if (merge.info != null)
/*      */     {
/* 4046 */       return;
/*      */     }
/* 4048 */     if (merge.isAborted()) {
/* 4049 */       return;
/*      */     }
/* 4051 */     boolean hasVectors = false;
/* 4052 */     for (SegmentInfo sourceSegment : merge.segments) {
/* 4053 */       if (sourceSegment.getHasVectors()) {
/* 4054 */         hasVectors = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4061 */     merge.info = new SegmentInfo(newSegmentName(), 0, this.directory, false, true, false, hasVectors);
/*      */ 
/* 4064 */     BufferedDeletesStream.ApplyDeletesResult result = this.bufferedDeletesStream.applyDeletes(this.readerPool, merge.segments);
/*      */ 
/* 4066 */     if (result.anyDeletes) {
/* 4067 */       checkpoint();
/*      */     }
/*      */ 
/* 4070 */     if ((!this.keepFullyDeletedSegments) && (result.allDeleted != null)) {
/* 4071 */       if (this.infoStream != null) {
/* 4072 */         message("drop 100% deleted segments: " + result.allDeleted);
/*      */       }
/* 4074 */       for (SegmentInfo info : result.allDeleted) {
/* 4075 */         this.segmentInfos.remove(info);
/* 4076 */         if (merge.segments.contains(info)) {
/* 4077 */           this.mergingSegments.remove(info);
/* 4078 */           merge.segments.remove(info);
/*      */         }
/*      */       }
/* 4081 */       if (this.readerPool != null) {
/* 4082 */         this.readerPool.drop(result.allDeleted);
/*      */       }
/* 4084 */       checkpoint();
/*      */     }
/*      */ 
/* 4087 */     merge.info.setBufferedDeletesGen(result.gen);
/*      */ 
/* 4090 */     this.bufferedDeletesStream.prune(this.segmentInfos);
/*      */ 
/* 4092 */     Map details = new HashMap();
/* 4093 */     details.put("optimize", Boolean.toString(merge.optimize));
/* 4094 */     details.put("mergeFactor", Integer.toString(merge.segments.size()));
/* 4095 */     setDiagnostics(merge.info, "merge", details);
/*      */ 
/* 4097 */     if (this.infoStream != null) {
/* 4098 */       message("merge seg=" + merge.info.name);
/*      */     }
/*      */ 
/* 4101 */     assert (merge.estimatedMergeBytes == 0L);
/* 4102 */     for (SegmentInfo info : merge.segments) {
/* 4103 */       if (info.docCount > 0) {
/* 4104 */         int delCount = numDeletedDocs(info);
/* 4105 */         assert (delCount <= info.docCount);
/* 4106 */         double delRatio = delCount / info.docCount;
/*      */         MergePolicy.OneMerge tmp585_584 = merge; tmp585_584.estimatedMergeBytes = ()(tmp585_584.estimatedMergeBytes + info.sizeInBytes(true) * (1.0D - delRatio));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4119 */     this.mergingSegments.add(merge.info);
/*      */   }
/*      */ 
/*      */   private void setDiagnostics(SegmentInfo info, String source) {
/* 4123 */     setDiagnostics(info, source, null);
/*      */   }
/*      */ 
/*      */   private void setDiagnostics(SegmentInfo info, String source, Map<String, String> details) {
/* 4127 */     Map diagnostics = new HashMap();
/* 4128 */     diagnostics.put("source", source);
/* 4129 */     diagnostics.put("lucene.version", Constants.LUCENE_VERSION);
/* 4130 */     diagnostics.put("os", Constants.OS_NAME);
/* 4131 */     diagnostics.put("os.arch", Constants.OS_ARCH);
/* 4132 */     diagnostics.put("os.version", Constants.OS_VERSION);
/* 4133 */     diagnostics.put("java.version", Constants.JAVA_VERSION);
/* 4134 */     diagnostics.put("java.vendor", Constants.JAVA_VENDOR);
/* 4135 */     if (details != null) {
/* 4136 */       diagnostics.putAll(details);
/*      */     }
/* 4138 */     info.setDiagnostics(diagnostics);
/*      */   }
/*      */ 
/*      */   final synchronized void mergeFinish(MergePolicy.OneMerge merge)
/*      */     throws IOException
/*      */   {
/* 4147 */     notifyAll();
/*      */ 
/* 4151 */     if (merge.registerDone) {
/* 4152 */       List sourceSegments = merge.segments;
/* 4153 */       for (SegmentInfo info : sourceSegments) {
/* 4154 */         this.mergingSegments.remove(info);
/*      */       }
/*      */ 
/* 4158 */       this.mergingSegments.remove(merge.info);
/* 4159 */       merge.registerDone = false;
/*      */     }
/*      */ 
/* 4162 */     this.runningMerges.remove(merge);
/*      */   }
/*      */ 
/*      */   private final synchronized void closeMergeReaders(MergePolicy.OneMerge merge, boolean suppressExceptions) throws IOException {
/* 4166 */     int numSegments = merge.readers.size();
/* 4167 */     Throwable th = null;
/*      */ 
/* 4169 */     boolean anyChanges = false;
/* 4170 */     boolean drop = !suppressExceptions;
/* 4171 */     for (int i = 0; i < numSegments; i++) {
/* 4172 */       if (merge.readers.get(i) != null) {
/*      */         try {
/* 4174 */           anyChanges |= this.readerPool.release((SegmentReader)merge.readers.get(i), drop);
/*      */         } catch (Throwable t) {
/* 4176 */           if (th == null) {
/* 4177 */             th = t;
/*      */           }
/*      */         }
/* 4180 */         merge.readers.set(i, null);
/*      */       }
/*      */ 
/* 4183 */       if ((i >= merge.readerClones.size()) || (merge.readerClones.get(i) == null)) continue;
/*      */       try {
/* 4185 */         ((SegmentReader)merge.readerClones.get(i)).close();
/*      */       } catch (Throwable t) {
/* 4187 */         if (th == null) {
/* 4188 */           th = t;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4193 */       assert (((SegmentReader)merge.readerClones.get(i)).getRefCount() == 0) : ("refCount should be 0 but is " + ((SegmentReader)merge.readerClones.get(i)).getRefCount());
/* 4194 */       merge.readerClones.set(i, null);
/*      */     }
/*      */ 
/* 4198 */     if ((suppressExceptions) && (anyChanges)) {
/* 4199 */       checkpoint();
/*      */     }
/*      */ 
/* 4203 */     if ((!suppressExceptions) && (th != null)) {
/* 4204 */       if ((th instanceof IOException)) throw ((IOException)th);
/* 4205 */       if ((th instanceof RuntimeException)) throw ((RuntimeException)th);
/* 4206 */       if ((th instanceof Error)) throw ((Error)th);
/* 4207 */       throw new RuntimeException(th);
/*      */     }
/*      */   }
/*      */ 
/*      */   private final int mergeMiddle(MergePolicy.OneMerge merge)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/* 4217 */     merge.checkAborted(this.directory);
/*      */ 
/* 4219 */     String mergedName = merge.info.name;
/*      */ 
/* 4221 */     int mergedDocCount = 0;
/*      */ 
/* 4223 */     List sourceSegments = merge.segments;
/*      */ 
/* 4225 */     SegmentMerger merger = new SegmentMerger(this.directory, this.config.getTermIndexInterval(), mergedName, merge, this.payloadProcessorProvider, (FieldInfos)this.docWriter.getFieldInfos().clone());
/*      */ 
/* 4229 */     if (this.infoStream != null) {
/* 4230 */       message("merging " + merge.segString(this.directory) + " mergeVectors=" + merge.info.getHasVectors());
/*      */     }
/*      */ 
/* 4233 */     merge.readers = new ArrayList();
/* 4234 */     merge.readerClones = new ArrayList();
/*      */ 
/* 4238 */     boolean success = false;
/*      */     try {
/* 4240 */       int totDocCount = 0;
/* 4241 */       int segUpto = 0;
/* 4242 */       while (segUpto < sourceSegments.size())
/*      */       {
/* 4244 */         SegmentInfo info = (SegmentInfo)sourceSegments.get(segUpto);
/*      */ 
/* 4248 */         SegmentReader reader = this.readerPool.get(info, true, 4096, -1);
/*      */ 
/* 4251 */         merge.readers.add(reader);
/*      */ 
/* 4256 */         SegmentReader clone = (SegmentReader)reader.clone(true);
/* 4257 */         merge.readerClones.add(clone);
/*      */ 
/* 4259 */         if (clone.numDocs() > 0) {
/* 4260 */           merger.add(clone);
/* 4261 */           totDocCount += clone.numDocs();
/*      */         }
/* 4263 */         segUpto++;
/*      */       }
/*      */ 
/* 4266 */       if (this.infoStream != null) {
/* 4267 */         message("merge: total " + totDocCount + " docs");
/*      */       }
/*      */ 
/* 4270 */       merge.checkAborted(this.directory);
/*      */ 
/* 4273 */       mergedDocCount = merge.info.docCount = merger.merge();
/*      */ 
/* 4276 */       merge.info.setHasVectors(merger.fieldInfos().hasVectors());
/*      */ 
/* 4278 */       assert (mergedDocCount == totDocCount);
/*      */ 
/* 4280 */       if (this.infoStream != null) {
/* 4281 */         message("merge store matchedCount=" + merger.getMatchedSubReaderCount() + " vs " + merge.readers.size());
/*      */       }
/*      */ 
/* 4284 */       this.anyNonBulkMerges |= merger.getAnyNonBulkMerges();
/*      */ 
/* 4286 */       assert (mergedDocCount == totDocCount) : ("mergedDocCount=" + mergedDocCount + " vs " + totDocCount);
/*      */ 
/* 4291 */       merge.info.setHasProx(merger.fieldInfos().hasProx());
/*      */       boolean useCompoundFile;
/* 4294 */       synchronized (this) {
/* 4295 */         useCompoundFile = this.mergePolicy.useCompoundFile(this.segmentInfos, merge.info);
/*      */       }
/*      */       String compoundFileName;
/* 4298 */       if (useCompoundFile)
/*      */       {
/* 4300 */         success = false;
/* 4301 */         compoundFileName = IndexFileNames.segmentFileName(mergedName, "cfs");
/*      */         try
/*      */         {
/* 4304 */           if (this.infoStream != null) {
/* 4305 */             message("create compound file " + compoundFileName);
/*      */           }
/* 4307 */           merger.createCompoundFile(compoundFileName, merge.info);
/* 4308 */           success = true;
/*      */         } catch (IOException ioe) {
/* 4310 */           synchronized (this) {
/* 4311 */             if (!merge.isAborted())
/*      */             {
/* 4316 */               handleMergeException(ioe, merge);
/*      */             }
/*      */           }
/*      */         } catch (Throwable t) {
/* 4320 */           handleMergeException(t, merge);
/*      */         } finally {
/* 4322 */           if (!success) {
/* 4323 */             if (this.infoStream != null) {
/* 4324 */               message("hit exception creating compound file during merge");
/*      */             }
/*      */ 
/* 4327 */             synchronized (this) {
/* 4328 */               this.deleter.deleteFile(compoundFileName);
/* 4329 */               this.deleter.deleteNewFiles(merge.info.files());
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 4334 */         success = false;
/*      */       }
/*      */       IndexReaderWarmer mergedSegmentWarmer;
/*      */       int termsIndexDivisor;
/*      */       boolean loadDocStores;
/*      */       int termsIndexDivisor;
/*      */       boolean loadDocStores;
/*      */       SegmentReader mergedReader;
/*      */       int j;
/* 4336 */       synchronized (this)
/*      */       {
/* 4340 */         this.deleter.deleteNewFiles(merge.info.files());
/*      */ 
/* 4342 */         if (merge.isAborted()) {
/* 4343 */           if (this.infoStream != null) {
/* 4344 */             message("abort merge after building CFS");
/*      */           }
/* 4346 */           this.deleter.deleteFile(compoundFileName);
/* 4347 */           int i = 0; jsr 257; return i;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4399 */       success = true;
/*      */     }
/*      */     finally
/*      */     {
/* 4404 */       if (!success) {
/* 4405 */         closeMergeReaders(merge, true);
/*      */       }
/*      */     }
/*      */ 
/* 4409 */     return mergedDocCount;
/*      */   }
/*      */ 
/*      */   synchronized void addMergeException(MergePolicy.OneMerge merge) {
/* 4413 */     assert (merge.getException() != null);
/* 4414 */     if ((!this.mergeExceptions.contains(merge)) && (this.mergeGen == merge.mergeGen))
/* 4415 */       this.mergeExceptions.add(merge);
/*      */   }
/*      */ 
/*      */   final int getBufferedDeleteTermsSize()
/*      */   {
/* 4420 */     return this.docWriter.getPendingDeletes().terms.size();
/*      */   }
/*      */ 
/*      */   final int getNumBufferedDeleteTerms()
/*      */   {
/* 4425 */     return this.docWriter.getPendingDeletes().numTermDeletes.get();
/*      */   }
/*      */ 
/*      */   synchronized SegmentInfo newestSegment()
/*      */   {
/* 4430 */     return this.segmentInfos.size() > 0 ? this.segmentInfos.info(this.segmentInfos.size() - 1) : null;
/*      */   }
/*      */ 
/*      */   public synchronized String segString() throws IOException
/*      */   {
/* 4435 */     return segString(this.segmentInfos);
/*      */   }
/*      */ 
/*      */   public synchronized String segString(Iterable<SegmentInfo> infos) throws IOException
/*      */   {
/* 4440 */     StringBuilder buffer = new StringBuilder();
/* 4441 */     for (SegmentInfo s : infos) {
/* 4442 */       if (buffer.length() > 0) {
/* 4443 */         buffer.append(' ');
/*      */       }
/* 4445 */       buffer.append(segString(s));
/*      */     }
/* 4447 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   public synchronized String segString(SegmentInfo info) throws IOException
/*      */   {
/* 4452 */     StringBuilder buffer = new StringBuilder();
/* 4453 */     SegmentReader reader = this.readerPool.getIfExists(info);
/*      */     try {
/* 4455 */       if (reader != null) {
/* 4456 */         buffer.append(reader.toString());
/*      */       } else {
/* 4458 */         buffer.append(info.toString(this.directory, 0));
/* 4459 */         if (info.dir != this.directory)
/* 4460 */           buffer.append("**");
/*      */       }
/*      */     }
/*      */     finally {
/* 4464 */       if (reader != null) {
/* 4465 */         this.readerPool.release(reader);
/*      */       }
/*      */     }
/* 4468 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   private synchronized void doWait()
/*      */   {
/*      */     try
/*      */     {
/* 4479 */       wait(1000L);
/*      */     } catch (InterruptedException ie) {
/* 4481 */       throw new ThreadInterruptedException(ie);
/*      */     }
/*      */   }
/*      */ 
/*      */   void keepFullyDeletedSegments()
/*      */   {
/* 4491 */     this.keepFullyDeletedSegments = true;
/*      */   }
/*      */ 
/*      */   boolean getKeepFullyDeletedSegments() {
/* 4495 */     return this.keepFullyDeletedSegments;
/*      */   }
/*      */ 
/*      */   private boolean filesExist(SegmentInfos toSync) throws IOException
/*      */   {
/* 4500 */     Collection files = toSync.files(this.directory, false);
/* 4501 */     for (String fileName : files) {
/* 4502 */       assert (this.directory.fileExists(fileName)) : ("file " + fileName + " does not exist");
/*      */ 
/* 4508 */       assert (this.deleter.exists(fileName)) : ("IndexFileDeleter doesn't know about file " + fileName);
/*      */     }
/* 4510 */     return true;
/*      */   }
/*      */ 
/*      */   private void startCommit(SegmentInfos toSync, Map<String, String> commitUserData)
/*      */     throws IOException
/*      */   {
/* 4520 */     assert (testPoint("startStartCommit"));
/* 4521 */     assert (this.pendingCommit == null);
/*      */ 
/* 4523 */     if (this.hitOOM) {
/* 4524 */       throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot commit");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 4529 */       if (this.infoStream != null) {
/* 4530 */         message("startCommit(): start");
/*      */       }
/*      */ 
/* 4533 */       synchronized (this)
/*      */       {
/* 4535 */         assert (this.lastCommitChangeCount <= this.changeCount);
/*      */ 
/* 4537 */         if (this.pendingCommitChangeCount == this.lastCommitChangeCount) {
/* 4538 */           if (this.infoStream != null) {
/* 4539 */             message("  skip startCommit(): no changes pending");
/*      */           }
/* 4541 */           this.deleter.decRef(toSync);
/* 4542 */           return;
/*      */         }
/*      */ 
/* 4549 */         if (this.infoStream != null) {
/* 4550 */           message("startCommit index=" + segString(toSync) + " changeCount=" + this.changeCount);
/*      */         }
/* 4552 */         assert (filesExist(toSync));
/*      */ 
/* 4554 */         if (commitUserData != null) {
/* 4555 */           toSync.setUserData(commitUserData);
/*      */         }
/*      */       }
/*      */ 
/* 4559 */       assert (testPoint("midStartCommit"));
/*      */ 
/* 4561 */       boolean pendingCommitSet = false;
/*      */       try
/*      */       {
/* 4566 */         this.directory.sync(toSync.files(this.directory, false));
/*      */ 
/* 4568 */         assert (testPoint("midStartCommit2"));
/*      */ 
/* 4570 */         synchronized (this)
/*      */         {
/* 4572 */           assert (this.pendingCommit == null);
/*      */ 
/* 4574 */           assert (this.segmentInfos.getGeneration() == toSync.getGeneration());
/*      */ 
/* 4579 */           toSync.prepareCommit(this.directory);
/* 4580 */           pendingCommitSet = true;
/* 4581 */           this.pendingCommit = toSync;
/*      */         }
/*      */ 
/* 4584 */         if (this.infoStream != null) {
/* 4585 */           message("done all syncs");
/*      */         }
/*      */ 
/* 4588 */         if ((!$assertionsDisabled) && (!testPoint("midStartCommitSuccess"))) throw new AssertionError(); 
/*      */       }
/*      */       finally
/*      */       {
/* 4591 */         synchronized (this)
/*      */         {
/* 4597 */           this.segmentInfos.updateGeneration(toSync);
/*      */ 
/* 4599 */           if (!pendingCommitSet) {
/* 4600 */             if (this.infoStream != null) {
/* 4601 */               message("hit exception committing segments file");
/*      */             }
/*      */ 
/* 4604 */             this.deleter.decRef(toSync);
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (OutOfMemoryError oom) {
/* 4609 */       handleOOM(oom, "startCommit");
/*      */     }
/* 4611 */     assert (testPoint("finishStartCommit"));
/*      */   }
/*      */ 
/*      */   public static boolean isLocked(Directory directory)
/*      */     throws IOException
/*      */   {
/* 4621 */     return directory.makeLock("write.lock").isLocked();
/*      */   }
/*      */ 
/*      */   public static void unlock(Directory directory)
/*      */     throws IOException
/*      */   {
/* 4632 */     directory.makeLock("write.lock").release();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMergedSegmentWarmer(IndexReaderWarmer warmer)
/*      */   {
/* 4715 */     this.config.setMergedSegmentWarmer(warmer);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public IndexReaderWarmer getMergedSegmentWarmer()
/*      */   {
/* 4725 */     return this.config.getMergedSegmentWarmer();
/*      */   }
/*      */ 
/*      */   private void handleOOM(OutOfMemoryError oom, String location) {
/* 4729 */     if (this.infoStream != null) {
/* 4730 */       message("hit OutOfMemoryError inside " + location);
/*      */     }
/* 4732 */     this.hitOOM = true;
/* 4733 */     throw oom;
/*      */   }
/*      */ 
/*      */   boolean testPoint(String name)
/*      */   {
/* 4748 */     return true;
/*      */   }
/*      */ 
/*      */   synchronized boolean nrtIsCurrent(SegmentInfos infos)
/*      */   {
/* 4753 */     return (infos.version == this.segmentInfos.version) && (!this.docWriter.anyChanges()) && (!this.bufferedDeletesStream.any());
/*      */   }
/*      */ 
/*      */   synchronized boolean isClosed() {
/* 4757 */     return this.closed;
/*      */   }
/*      */ 
/*      */   public synchronized void deleteUnusedFiles()
/*      */     throws IOException
/*      */   {
/* 4786 */     this.deleter.deletePendingFiles();
/* 4787 */     this.deleter.revisitPolicy();
/*      */   }
/*      */ 
/*      */   public void setPayloadProcessorProvider(PayloadProcessorProvider pcp)
/*      */   {
/* 4809 */     this.payloadProcessorProvider = pcp;
/*      */   }
/*      */ 
/*      */   public PayloadProcessorProvider getPayloadProcessorProvider()
/*      */   {
/* 4817 */     return this.payloadProcessorProvider;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  195 */     WRITE_LOCK_TIMEOUT = IndexWriterConfig.WRITE_LOCK_TIMEOUT;
/*      */ 
/*  241 */     DEFAULT_MAX_FIELD_LENGTH = MaxFieldLength.UNLIMITED.getLimit();
/*      */ 
/*  267 */     MESSAGE_ID = new AtomicInteger();
/*      */   }
/*      */ 
/*      */   final class FlushControl
/*      */   {
/*      */     private boolean flushPending;
/*      */     private boolean flushDeletes;
/*      */     private int delCount;
/*      */     private int docCount;
/*      */     private boolean flushing;
/*      */ 
/*      */     FlushControl()
/*      */     {
/*      */     }
/*      */ 
/*      */     private synchronized boolean setFlushPending(String reason, boolean doWait)
/*      */     {
/* 4830 */       if ((this.flushPending) || (this.flushing)) {
/* 4831 */         while ((doWait) && (
/* 4832 */           (this.flushPending) || (this.flushing))) {
/*      */           try {
/* 4834 */             wait();
/*      */           } catch (InterruptedException ie) {
/* 4836 */             throw new ThreadInterruptedException(ie);
/*      */           }
/*      */         }
/*      */ 
/* 4840 */         return false;
/*      */       }
/* 4842 */       if (IndexWriter.this.infoStream != null) {
/* 4843 */         IndexWriter.this.message("now trigger flush reason=" + reason);
/*      */       }
/* 4845 */       this.flushPending = true;
/* 4846 */       return this.flushPending;
/*      */     }
/*      */ 
/*      */     public synchronized void setFlushPendingNoWait(String reason)
/*      */     {
/* 4851 */       setFlushPending(reason, false);
/*      */     }
/*      */ 
/*      */     public synchronized boolean getFlushPending() {
/* 4855 */       return this.flushPending;
/*      */     }
/*      */ 
/*      */     public synchronized boolean getFlushDeletes() {
/* 4859 */       return this.flushDeletes;
/*      */     }
/*      */ 
/*      */     public synchronized void clearFlushPending() {
/* 4863 */       if (IndexWriter.this.infoStream != null) {
/* 4864 */         IndexWriter.this.message("clearFlushPending");
/*      */       }
/* 4866 */       this.flushPending = false;
/* 4867 */       this.flushDeletes = false;
/* 4868 */       this.docCount = 0;
/* 4869 */       notifyAll();
/*      */     }
/*      */ 
/*      */     public synchronized void clearDeletes() {
/* 4873 */       this.delCount = 0;
/*      */     }
/*      */ 
/*      */     public synchronized boolean waitUpdate(int docInc, int delInc) {
/* 4877 */       return waitUpdate(docInc, delInc, false);
/*      */     }
/*      */ 
/*      */     public synchronized boolean waitUpdate(int docInc, int delInc, boolean skipWait) {
/* 4881 */       while (this.flushPending) {
/*      */         try {
/* 4883 */           wait();
/*      */         } catch (InterruptedException ie) {
/* 4885 */           throw new ThreadInterruptedException(ie);
/*      */         }
/*      */       }
/*      */ 
/* 4889 */       this.docCount += docInc;
/* 4890 */       this.delCount += delInc;
/*      */ 
/* 4895 */       if (skipWait) {
/* 4896 */         return false;
/*      */       }
/*      */ 
/* 4899 */       int maxBufferedDocs = IndexWriter.this.config.getMaxBufferedDocs();
/* 4900 */       if ((maxBufferedDocs != -1) && (this.docCount >= maxBufferedDocs))
/*      */       {
/* 4902 */         return setFlushPending("maxBufferedDocs", true);
/*      */       }
/*      */ 
/* 4905 */       int maxBufferedDeleteTerms = IndexWriter.this.config.getMaxBufferedDeleteTerms();
/* 4906 */       if ((maxBufferedDeleteTerms != -1) && (this.delCount >= maxBufferedDeleteTerms))
/*      */       {
/* 4908 */         this.flushDeletes = true;
/* 4909 */         return setFlushPending("maxBufferedDeleteTerms", true);
/*      */       }
/*      */ 
/* 4912 */       return flushByRAMUsage("add delete/doc");
/*      */     }
/*      */ 
/*      */     public synchronized boolean flushByRAMUsage(String reason) {
/* 4916 */       double ramBufferSizeMB = IndexWriter.this.config.getRAMBufferSizeMB();
/* 4917 */       if (ramBufferSizeMB != -1.0D) {
/* 4918 */         long limit = ()(ramBufferSizeMB * 1024.0D * 1024.0D);
/* 4919 */         long used = IndexWriter.this.bufferedDeletesStream.bytesUsed() + IndexWriter.this.docWriter.bytesUsed();
/* 4920 */         if (used >= limit)
/*      */         {
/* 4925 */           IndexWriter.this.docWriter.balanceRAM();
/*      */ 
/* 4927 */           used = IndexWriter.this.bufferedDeletesStream.bytesUsed() + IndexWriter.this.docWriter.bytesUsed();
/* 4928 */           if (used >= limit) {
/* 4929 */             return setFlushPending("ram full: " + reason, false);
/*      */           }
/*      */         }
/*      */       }
/* 4933 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class IndexReaderWarmer
/*      */   {
/*      */     public abstract void warm(IndexReader paramIndexReader)
/*      */       throws IOException;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static final class MaxFieldLength
/*      */   {
/*      */     private int limit;
/*      */     private String name;
/* 4679 */     public static final MaxFieldLength UNLIMITED = new MaxFieldLength("UNLIMITED", 2147483647);
/*      */ 
/* 4686 */     public static final MaxFieldLength LIMITED = new MaxFieldLength("LIMITED", 10000);
/*      */ 
/*      */     private MaxFieldLength(String name, int limit)
/*      */     {
/* 4655 */       this.name = name;
/* 4656 */       this.limit = limit;
/*      */     }
/*      */ 
/*      */     public MaxFieldLength(int limit)
/*      */     {
/* 4665 */       this("User-specified", limit);
/*      */     }
/*      */ 
/*      */     public int getLimit() {
/* 4669 */       return this.limit;
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 4675 */       return this.name + ":" + this.limit;
/*      */     }
/*      */   }
/*      */ 
/*      */   class ReaderPool
/*      */   {
/*  488 */     private final Map<SegmentInfo, SegmentReader> readerMap = new HashMap();
/*      */ 
/*      */     ReaderPool() {
/*      */     }
/*  492 */     synchronized void clear(List<SegmentInfo> infos) throws IOException { if (infos == null) {
/*  493 */         for (Map.Entry ent : this.readerMap.entrySet())
/*  494 */           ((SegmentReader)ent.getValue()).hasChanges = false;
/*      */       }
/*      */       else
/*  497 */         for (SegmentInfo info : infos) {
/*  498 */           SegmentReader r = (SegmentReader)this.readerMap.get(info);
/*  499 */           if (r != null)
/*  500 */             r.hasChanges = false;
/*      */         }
/*      */     }
/*      */ 
/*      */     public synchronized boolean infoIsLive(SegmentInfo info)
/*      */     {
/*  508 */       int idx = IndexWriter.this.segmentInfos.indexOf(info);
/*  509 */       assert (idx != -1) : ("info=" + info + " isn't in pool");
/*  510 */       assert (IndexWriter.this.segmentInfos.info(idx) == info) : ("info=" + info + " doesn't match live info in segmentInfos");
/*  511 */       return true;
/*      */     }
/*      */ 
/*      */     public synchronized SegmentInfo mapToLive(SegmentInfo info) {
/*  515 */       int idx = IndexWriter.this.segmentInfos.indexOf(info);
/*  516 */       if (idx != -1) {
/*  517 */         info = IndexWriter.this.segmentInfos.info(idx);
/*      */       }
/*  519 */       return info;
/*      */     }
/*      */ 
/*      */     public synchronized boolean release(SegmentReader sr)
/*      */       throws IOException
/*      */     {
/*  532 */       return release(sr, false);
/*      */     }
/*      */ 
/*      */     public synchronized boolean release(SegmentReader sr, boolean drop)
/*      */       throws IOException
/*      */     {
/*  546 */       boolean pooled = this.readerMap.containsKey(sr.getSegmentInfo());
/*      */ 
/*  548 */       assert ((!pooled) || (this.readerMap.get(sr.getSegmentInfo()) == sr));
/*      */ 
/*  552 */       sr.decRef();
/*      */ 
/*  554 */       if ((pooled) && ((drop) || ((!IndexWriter.this.poolReaders) && (sr.getRefCount() == 1))))
/*      */       {
/*  558 */         assert ((!sr.hasChanges) || (Thread.holdsLock(IndexWriter.this)));
/*      */ 
/*  563 */         sr.hasChanges &= !drop;
/*      */ 
/*  565 */         boolean hasChanges = sr.hasChanges;
/*      */ 
/*  569 */         sr.close();
/*      */ 
/*  573 */         this.readerMap.remove(sr.getSegmentInfo());
/*      */ 
/*  575 */         return hasChanges;
/*      */       }
/*      */ 
/*  578 */       return false;
/*      */     }
/*      */ 
/*      */     public synchronized void drop(List<SegmentInfo> infos) throws IOException {
/*  582 */       for (SegmentInfo info : infos)
/*  583 */         drop(info);
/*      */     }
/*      */ 
/*      */     public synchronized void drop(SegmentInfo info) throws IOException
/*      */     {
/*  588 */       SegmentReader sr = (SegmentReader)this.readerMap.get(info);
/*  589 */       if (sr != null) {
/*  590 */         sr.hasChanges = false;
/*  591 */         this.readerMap.remove(info);
/*  592 */         sr.close();
/*      */       }
/*      */     }
/*      */ 
/*      */     public synchronized void dropAll() throws IOException {
/*  597 */       for (SegmentReader reader : this.readerMap.values()) {
/*  598 */         reader.hasChanges = false;
/*      */ 
/*  603 */         reader.decRef();
/*      */       }
/*  605 */       this.readerMap.clear();
/*      */     }
/*      */ 
/*      */     synchronized void close()
/*      */       throws IOException
/*      */     {
/*  613 */       assert (Thread.holdsLock(IndexWriter.this));
/*      */ 
/*  615 */       for (Map.Entry ent : this.readerMap.entrySet())
/*      */       {
/*  617 */         SegmentReader sr = (SegmentReader)ent.getValue();
/*  618 */         if (sr.hasChanges) {
/*  619 */           assert (infoIsLive(sr.getSegmentInfo()));
/*  620 */           sr.doCommit(null);
/*      */ 
/*  625 */           IndexWriter.this.deleter.checkpoint(IndexWriter.this.segmentInfos, false);
/*      */         }
/*      */ 
/*  632 */         sr.decRef();
/*      */       }
/*      */ 
/*  635 */       this.readerMap.clear();
/*      */     }
/*      */ 
/*      */     synchronized void commit(SegmentInfos infos)
/*      */       throws IOException
/*      */     {
/*  646 */       assert (Thread.holdsLock(IndexWriter.this));
/*      */ 
/*  648 */       for (SegmentInfo info : infos)
/*      */       {
/*  650 */         SegmentReader sr = (SegmentReader)this.readerMap.get(info);
/*  651 */         if ((sr != null) && (sr.hasChanges)) {
/*  652 */           assert (infoIsLive(info));
/*  653 */           sr.doCommit(null);
/*      */ 
/*  657 */           IndexWriter.this.deleter.checkpoint(IndexWriter.this.segmentInfos, false);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public synchronized SegmentReader getReadOnlyClone(SegmentInfo info, boolean doOpenStores, int termInfosIndexDivisor)
/*      */       throws IOException
/*      */     {
/*  668 */       SegmentReader sr = get(info, doOpenStores, 1024, termInfosIndexDivisor);
/*      */       try {
/*  670 */         SegmentReader localSegmentReader1 = (SegmentReader)sr.clone(true);
/*      */         return localSegmentReader1; } finally { sr.decRef(); } throw localObject;
/*      */     }
/*      */ 
/*      */     public synchronized SegmentReader get(SegmentInfo info, boolean doOpenStores)
/*      */       throws IOException
/*      */     {
/*  685 */       return get(info, doOpenStores, 1024, IndexWriter.this.config.getReaderTermsIndexDivisor());
/*      */     }
/*      */ 
/*      */     public synchronized SegmentReader get(SegmentInfo info, boolean doOpenStores, int readBufferSize, int termsIndexDivisor)
/*      */       throws IOException
/*      */     {
/*  701 */       if (IndexWriter.this.poolReaders) {
/*  702 */         readBufferSize = 1024;
/*      */       }
/*      */ 
/*  705 */       SegmentReader sr = (SegmentReader)this.readerMap.get(info);
/*  706 */       if (sr == null)
/*      */       {
/*  710 */         sr = SegmentReader.get(false, info.dir, info, readBufferSize, doOpenStores, termsIndexDivisor);
/*  711 */         sr.readerFinishedListeners = IndexWriter.this.readerFinishedListeners;
/*      */ 
/*  713 */         if (info.dir == IndexWriter.this.directory)
/*      */         {
/*  715 */           this.readerMap.put(info, sr);
/*      */         }
/*      */       } else {
/*  718 */         if (doOpenStores) {
/*  719 */           sr.openDocStores();
/*      */         }
/*  721 */         if ((termsIndexDivisor != -1) && (!sr.termsIndexLoaded()))
/*      */         {
/*  728 */           sr.loadTermsIndex(termsIndexDivisor);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  733 */       if (info.dir == IndexWriter.this.directory)
/*      */       {
/*  735 */         sr.incRef();
/*      */       }
/*  737 */       return sr;
/*      */     }
/*      */ 
/*      */     public synchronized SegmentReader getIfExists(SegmentInfo info) throws IOException
/*      */     {
/*  742 */       SegmentReader sr = (SegmentReader)this.readerMap.get(info);
/*  743 */       if (sr != null) {
/*  744 */         sr.incRef();
/*      */       }
/*  746 */       return sr;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexWriter
 * JD-Core Version:    0.6.0
 */