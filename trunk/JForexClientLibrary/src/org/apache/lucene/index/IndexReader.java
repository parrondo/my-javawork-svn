/*      */ package org.apache.lucene.index;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.util.Collection;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import org.apache.lucene.document.Document;
/*      */ import org.apache.lucene.document.FieldSelector;
/*      */ import org.apache.lucene.search.Similarity;
/*      */ import org.apache.lucene.store.AlreadyClosedException;
/*      */ import org.apache.lucene.store.Directory;
/*      */ import org.apache.lucene.store.FSDirectory;
/*      */ import org.apache.lucene.store.IndexInput;
/*      */ import org.apache.lucene.store.LockObtainFailedException;
/*      */ import org.apache.lucene.util.ArrayUtil;
/*      */ 
/*      */ public abstract class IndexReader
/*      */   implements Cloneable, Closeable
/*      */ {
/*      */   protected volatile Collection<ReaderFinishedListener> readerFinishedListeners;
/*      */   private boolean closed;
/*      */   protected boolean hasChanges;
/*  170 */   private final AtomicInteger refCount = new AtomicInteger();
/*      */ 
/*  172 */   static int DEFAULT_TERMS_INDEX_DIVISOR = 1;
/*      */ 
/*      */   public void addReaderFinishedListener(ReaderFinishedListener listener)
/*      */   {
/*  112 */     this.readerFinishedListeners.add(listener);
/*      */   }
/*      */ 
/*      */   public void removeReaderFinishedListener(ReaderFinishedListener listener)
/*      */   {
/*  119 */     this.readerFinishedListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   protected void notifyReaderFinishedListeners()
/*      */   {
/*  125 */     if (this.readerFinishedListeners != null)
/*  126 */       for (ReaderFinishedListener listener : this.readerFinishedListeners)
/*  127 */         listener.finished(this);
/*      */   }
/*      */ 
/*      */   protected void readerFinished()
/*      */   {
/*  133 */     notifyReaderFinishedListeners();
/*      */   }
/*      */ 
/*      */   public int getRefCount()
/*      */   {
/*  176 */     return this.refCount.get();
/*      */   }
/*      */ 
/*      */   public void incRef()
/*      */   {
/*  194 */     ensureOpen();
/*  195 */     this.refCount.incrementAndGet();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  201 */     StringBuilder buffer = new StringBuilder();
/*  202 */     if (this.hasChanges) {
/*  203 */       buffer.append('*');
/*      */     }
/*  205 */     buffer.append(getClass().getSimpleName());
/*  206 */     buffer.append('(');
/*  207 */     IndexReader[] subReaders = getSequentialSubReaders();
/*  208 */     if ((subReaders != null) && (subReaders.length > 0)) {
/*  209 */       buffer.append(subReaders[0]);
/*  210 */       for (int i = 1; i < subReaders.length; i++) {
/*  211 */         buffer.append(" ").append(subReaders[i]);
/*      */       }
/*      */     }
/*  214 */     buffer.append(')');
/*  215 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   public void decRef()
/*      */     throws IOException
/*      */   {
/*  230 */     ensureOpen();
/*  231 */     if (this.refCount.getAndDecrement() == 1) {
/*  232 */       boolean success = false;
/*      */       try {
/*  234 */         commit();
/*  235 */         doClose();
/*  236 */         success = true;
/*      */       } finally {
/*  238 */         if (!success)
/*      */         {
/*  240 */           this.refCount.incrementAndGet();
/*      */         }
/*      */       }
/*  243 */       readerFinished();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected IndexReader() {
/*  248 */     this.refCount.set(1);
/*      */   }
/*      */ 
/*      */   protected final void ensureOpen()
/*      */     throws AlreadyClosedException
/*      */   {
/*  255 */     if (this.refCount.get() <= 0)
/*  256 */       throw new AlreadyClosedException("this IndexReader is closed");
/*      */   }
/*      */ 
/*      */   public static IndexReader open(Directory directory)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  267 */     return open(directory, null, null, true, DEFAULT_TERMS_INDEX_DIVISOR);
/*      */   }
/*      */ 
/*      */   public static IndexReader open(Directory directory, boolean readOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  281 */     return open(directory, null, null, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
/*      */   }
/*      */ 
/*      */   public static IndexReader open(IndexWriter writer, boolean applyAllDeletes)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  304 */     return writer.getReader(applyAllDeletes);
/*      */   }
/*      */ 
/*      */   public static IndexReader open(IndexCommit commit, boolean readOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  318 */     return open(commit.getDirectory(), null, commit, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
/*      */   }
/*      */ 
/*      */   public static IndexReader open(Directory directory, IndexDeletionPolicy deletionPolicy, boolean readOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  336 */     return open(directory, deletionPolicy, null, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
/*      */   }
/*      */ 
/*      */   public static IndexReader open(Directory directory, IndexDeletionPolicy deletionPolicy, boolean readOnly, int termInfosIndexDivisor)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  364 */     return open(directory, deletionPolicy, null, readOnly, termInfosIndexDivisor);
/*      */   }
/*      */ 
/*      */   public static IndexReader open(IndexCommit commit, IndexDeletionPolicy deletionPolicy, boolean readOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  384 */     return open(commit.getDirectory(), deletionPolicy, commit, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
/*      */   }
/*      */ 
/*      */   public static IndexReader open(IndexCommit commit, IndexDeletionPolicy deletionPolicy, boolean readOnly, int termInfosIndexDivisor)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  417 */     return open(commit.getDirectory(), deletionPolicy, commit, readOnly, termInfosIndexDivisor);
/*      */   }
/*      */ 
/*      */   private static IndexReader open(Directory directory, IndexDeletionPolicy deletionPolicy, IndexCommit commit, boolean readOnly, int termInfosIndexDivisor) throws CorruptIndexException, IOException {
/*  421 */     return DirectoryReader.open(directory, deletionPolicy, commit, readOnly, termInfosIndexDivisor);
/*      */   }
/*      */ 
/*      */   public synchronized IndexReader reopen()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  470 */     throw new UnsupportedOperationException("This reader does not support reopen().");
/*      */   }
/*      */ 
/*      */   public synchronized IndexReader reopen(boolean openReadOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  479 */     throw new UnsupportedOperationException("This reader does not support reopen().");
/*      */   }
/*      */ 
/*      */   public synchronized IndexReader reopen(IndexCommit commit)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  489 */     throw new UnsupportedOperationException("This reader does not support reopen(IndexCommit).");
/*      */   }
/*      */ 
/*      */   public IndexReader reopen(IndexWriter writer, boolean applyAllDeletes)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  561 */     return writer.getReader(applyAllDeletes);
/*      */   }
/*      */ 
/*      */   public synchronized Object clone()
/*      */   {
/*  583 */     throw new UnsupportedOperationException("This reader does not implement clone()");
/*      */   }
/*      */ 
/*      */   public synchronized IndexReader clone(boolean openReadOnly)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  593 */     throw new UnsupportedOperationException("This reader does not implement clone()");
/*      */   }
/*      */ 
/*      */   public Directory directory()
/*      */   {
/*  604 */     ensureOpen();
/*  605 */     throw new UnsupportedOperationException("This reader does not support this method.");
/*      */   }
/*      */ 
/*      */   public static long lastModified(Directory directory2)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  616 */     return ((Long)new SegmentInfos.FindSegmentsFile(directory2, directory2)
/*      */     {
/*      */       public Object doBody(String segmentFileName) throws IOException {
/*  619 */         return Long.valueOf(this.val$directory2.fileModified(segmentFileName));
/*      */       }
/*      */     }
/*  616 */     .run()).longValue();
/*      */   }
/*      */ 
/*      */   public static long getCurrentVersion(Directory directory)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  635 */     return SegmentInfos.readCurrentVersion(directory);
/*      */   }
/*      */ 
/*      */   public static Map<String, String> getCommitUserData(Directory directory)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  653 */     return SegmentInfos.readCurrentUserData(directory);
/*      */   }
/*      */ 
/*      */   public long getVersion()
/*      */   {
/*  681 */     throw new UnsupportedOperationException("This reader does not support this method.");
/*      */   }
/*      */ 
/*      */   public Map<String, String> getCommitUserData()
/*      */   {
/*  693 */     throw new UnsupportedOperationException("This reader does not support this method.");
/*      */   }
/*      */ 
/*      */   public boolean isCurrent()
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  725 */     throw new UnsupportedOperationException("This reader does not support this method.");
/*      */   }
/*      */ 
/*      */   public boolean isOptimized()
/*      */   {
/*  735 */     throw new UnsupportedOperationException("This reader does not support this method.");
/*      */   }
/*      */ 
/*      */   public abstract TermFreqVector[] getTermFreqVectors(int paramInt)
/*      */     throws IOException;
/*      */ 
/*      */   public abstract TermFreqVector getTermFreqVector(int paramInt, String paramString)
/*      */     throws IOException;
/*      */ 
/*      */   public abstract void getTermFreqVector(int paramInt, String paramString, TermVectorMapper paramTermVectorMapper)
/*      */     throws IOException;
/*      */ 
/*      */   public abstract void getTermFreqVector(int paramInt, TermVectorMapper paramTermVectorMapper)
/*      */     throws IOException;
/*      */ 
/*      */   public static boolean indexExists(Directory directory)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  801 */       new SegmentInfos().read(directory);
/*  802 */       return true; } catch (IOException ioe) {
/*      */     }
/*  804 */     return false;
/*      */   }
/*      */ 
/*      */   public abstract int numDocs();
/*      */ 
/*      */   public abstract int maxDoc();
/*      */ 
/*      */   public int numDeletedDocs()
/*      */   {
/*  819 */     return maxDoc() - numDocs();
/*      */   }
/*      */ 
/*      */   public Document document(int n)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  836 */     ensureOpen();
/*  837 */     if ((n < 0) || (n >= maxDoc())) {
/*  838 */       throw new IllegalArgumentException("docID must be >= 0 and < maxDoc=" + maxDoc() + " (got docID=" + n + ")");
/*      */     }
/*  840 */     return document(n, null);
/*      */   }
/*      */ 
/*      */   public abstract Document document(int paramInt, FieldSelector paramFieldSelector)
/*      */     throws CorruptIndexException, IOException;
/*      */ 
/*      */   public abstract boolean isDeleted(int paramInt);
/*      */ 
/*      */   public abstract boolean hasDeletions();
/*      */ 
/*      */   public boolean hasNorms(String field)
/*      */     throws IOException
/*      */   {
/*  886 */     ensureOpen();
/*  887 */     return norms(field) != null;
/*      */   }
/*      */ 
/*      */   public abstract byte[] norms(String paramString)
/*      */     throws IOException;
/*      */ 
/*      */   public abstract void norms(String paramString, byte[] paramArrayOfByte, int paramInt)
/*      */     throws IOException;
/*      */ 
/*      */   public synchronized void setNorm(int doc, String field, byte value)
/*      */     throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/*  928 */     ensureOpen();
/*  929 */     acquireWriteLock();
/*  930 */     this.hasChanges = true;
/*  931 */     doSetNorm(doc, field, value);
/*      */   }
/*      */ 
/*      */   protected abstract void doSetNorm(int paramInt, String paramString, byte paramByte)
/*      */     throws CorruptIndexException, IOException;
/*      */ 
/*      */   @Deprecated
/*      */   public void setNorm(int doc, String field, float value)
/*      */     throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/*  958 */     ensureOpen();
/*  959 */     setNorm(doc, field, Similarity.getDefault().encodeNormValue(value));
/*      */   }
/*      */ 
/*      */   public abstract TermEnum terms()
/*      */     throws IOException;
/*      */ 
/*      */   public abstract TermEnum terms(Term paramTerm)
/*      */     throws IOException;
/*      */ 
/*      */   public abstract int docFreq(Term paramTerm)
/*      */     throws IOException;
/*      */ 
/*      */   public TermDocs termDocs(Term term)
/*      */     throws IOException
/*      */   {
/* 1000 */     ensureOpen();
/* 1001 */     TermDocs termDocs = termDocs();
/* 1002 */     termDocs.seek(term);
/* 1003 */     return termDocs;
/*      */   }
/*      */ 
/*      */   public abstract TermDocs termDocs()
/*      */     throws IOException;
/*      */ 
/*      */   public TermPositions termPositions(Term term)
/*      */     throws IOException
/*      */   {
/* 1034 */     ensureOpen();
/* 1035 */     TermPositions termPositions = termPositions();
/* 1036 */     termPositions.seek(term);
/* 1037 */     return termPositions;
/*      */   }
/*      */ 
/*      */   public abstract TermPositions termPositions()
/*      */     throws IOException;
/*      */ 
/*      */   public synchronized void deleteDocument(int docNum)
/*      */     throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/* 1063 */     ensureOpen();
/* 1064 */     acquireWriteLock();
/* 1065 */     this.hasChanges = true;
/* 1066 */     doDelete(docNum);
/*      */   }
/*      */ 
/*      */   protected abstract void doDelete(int paramInt)
/*      */     throws CorruptIndexException, IOException;
/*      */ 
/*      */   public int deleteDocuments(Term term)
/*      */     throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/* 1094 */     ensureOpen();
/* 1095 */     TermDocs docs = termDocs(term);
/* 1096 */     if (docs == null) return 0;
/* 1097 */     int n = 0;
/*      */     try {
/* 1099 */       while (docs.next()) {
/* 1100 */         deleteDocument(docs.doc());
/* 1101 */         n++;
/*      */       }
/*      */     } finally {
/* 1104 */       docs.close();
/*      */     }
/* 1106 */     return n;
/*      */   }
/*      */ 
/*      */   public synchronized void undeleteAll()
/*      */     throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException
/*      */   {
/* 1129 */     ensureOpen();
/* 1130 */     acquireWriteLock();
/* 1131 */     this.hasChanges = true;
/* 1132 */     doUndeleteAll();
/*      */   }
/*      */ 
/*      */   protected abstract void doUndeleteAll()
/*      */     throws CorruptIndexException, IOException;
/*      */ 
/*      */   protected synchronized void acquireWriteLock()
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   public final synchronized void flush()
/*      */     throws IOException
/*      */   {
/* 1149 */     ensureOpen();
/* 1150 */     commit();
/*      */   }
/*      */ 
/*      */   public final synchronized void flush(Map<String, String> commitUserData)
/*      */     throws IOException
/*      */   {
/* 1161 */     ensureOpen();
/* 1162 */     commit(commitUserData);
/*      */   }
/*      */ 
/*      */   protected final synchronized void commit()
/*      */     throws IOException
/*      */   {
/* 1175 */     commit(null);
/*      */   }
/*      */ 
/*      */   public final synchronized void commit(Map<String, String> commitUserData)
/*      */     throws IOException
/*      */   {
/* 1188 */     if (this.hasChanges) {
/* 1189 */       doCommit(commitUserData);
/*      */     }
/* 1191 */     this.hasChanges = false;
/*      */   }
/*      */ 
/*      */   protected abstract void doCommit(Map<String, String> paramMap)
/*      */     throws IOException;
/*      */ 
/*      */   public final synchronized void close()
/*      */     throws IOException
/*      */   {
/* 1204 */     if (!this.closed) {
/* 1205 */       decRef();
/* 1206 */       this.closed = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract void doClose()
/*      */     throws IOException;
/*      */ 
/*      */   public abstract Collection<String> getFieldNames(FieldOption paramFieldOption);
/*      */ 
/*      */   public IndexCommit getIndexCommit()
/*      */     throws IOException
/*      */   {
/* 1232 */     throw new UnsupportedOperationException("This reader does not support this method.");
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1243 */     String filename = null;
/* 1244 */     boolean extract = false;
/*      */ 
/* 1246 */     for (int i = 0; i < args.length; i++) {
/* 1247 */       if (args[i].equals("-extract"))
/* 1248 */         extract = true;
/* 1249 */       else if (filename == null) {
/* 1250 */         filename = args[i];
/*      */       }
/*      */     }
/*      */ 
/* 1254 */     if (filename == null) {
/* 1255 */       System.out.println("Usage: org.apache.lucene.index.IndexReader [-extract] <cfsfile>");
/* 1256 */       return;
/*      */     }
/*      */ 
/* 1259 */     Directory dir = null;
/* 1260 */     CompoundFileReader cfr = null;
/*      */     try
/*      */     {
/* 1263 */       File file = new File(filename);
/* 1264 */       String dirname = file.getAbsoluteFile().getParent();
/* 1265 */       filename = file.getName();
/* 1266 */       dir = FSDirectory.open(new File(dirname));
/* 1267 */       cfr = new CompoundFileReader(dir, filename);
/*      */ 
/* 1269 */       String[] files = cfr.listAll();
/* 1270 */       ArrayUtil.mergeSort(files);
/*      */ 
/* 1272 */       for (int i = 0; i < files.length; i++) {
/* 1273 */         long len = cfr.fileLength(files[i]);
/*      */ 
/* 1275 */         if (extract) {
/* 1276 */           System.out.println("extract " + files[i] + " with " + len + " bytes to local directory...");
/* 1277 */           IndexInput ii = cfr.openInput(files[i]);
/*      */ 
/* 1279 */           FileOutputStream f = new FileOutputStream(files[i]);
/*      */ 
/* 1282 */           byte[] buffer = new byte[1024];
/* 1283 */           int chunk = buffer.length;
/* 1284 */           while (len > 0L) {
/* 1285 */             int bufLen = (int)Math.min(chunk, len);
/* 1286 */             ii.readBytes(buffer, 0, bufLen);
/* 1287 */             f.write(buffer, 0, bufLen);
/* 1288 */             len -= bufLen;
/*      */           }
/*      */ 
/* 1291 */           f.close();
/* 1292 */           ii.close();
/*      */         }
/*      */         else {
/* 1295 */           System.out.println(files[i] + ": " + len + " bytes");
/*      */         }
/*      */       }
/*      */     } catch (IOException ioe) {
/* 1298 */       ioe.printStackTrace();
/*      */     }
/*      */     finally {
/*      */       try {
/* 1302 */         if (dir != null)
/* 1303 */           dir.close();
/* 1304 */         if (cfr != null)
/* 1305 */           cfr.close();
/*      */       }
/*      */       catch (IOException ioe) {
/* 1308 */         ioe.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Collection<IndexCommit> listCommits(Directory dir)
/*      */     throws IOException
/*      */   {
/* 1329 */     return DirectoryReader.listCommits(dir);
/*      */   }
/*      */ 
/*      */   public IndexReader[] getSequentialSubReaders()
/*      */   {
/* 1348 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getCoreCacheKey()
/*      */   {
/* 1353 */     return this;
/*      */   }
/*      */ 
/*      */   public Object getDeletesCacheKey()
/*      */   {
/* 1359 */     return this;
/*      */   }
/*      */ 
/*      */   public long getUniqueTermCount()
/*      */     throws IOException
/*      */   {
/* 1375 */     throw new UnsupportedOperationException("this reader does not implement getUniqueTermCount()");
/*      */   }
/*      */ 
/*      */   public int getTermInfosIndexDivisor()
/*      */   {
/* 1384 */     throw new UnsupportedOperationException("This reader does not support this method.");
/*      */   }
/*      */ 
/*      */   public static enum FieldOption
/*      */   {
/*  142 */     ALL, 
/*      */ 
/*  144 */     INDEXED, 
/*      */ 
/*  146 */     STORES_PAYLOADS, 
/*      */ 
/*  148 */     OMIT_TERM_FREQ_AND_POSITIONS, 
/*      */ 
/*  150 */     OMIT_POSITIONS, 
/*      */ 
/*  152 */     UNINDEXED, 
/*      */ 
/*  154 */     INDEXED_WITH_TERMVECTOR, 
/*      */ 
/*  156 */     INDEXED_NO_TERMVECTOR, 
/*      */ 
/*  158 */     TERMVECTOR, 
/*      */ 
/*  160 */     TERMVECTOR_WITH_POSITION, 
/*      */ 
/*  162 */     TERMVECTOR_WITH_OFFSET, 
/*      */ 
/*  164 */     TERMVECTOR_WITH_POSITION_OFFSET;
/*      */   }
/*      */ 
/*      */   public static abstract interface ReaderFinishedListener
/*      */   {
/*      */     public abstract void finished(IndexReader paramIndexReader);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexReader
 * JD-Core Version:    0.6.0
 */