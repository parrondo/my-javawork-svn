/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.NoSuchDirectoryException;
/*     */ import org.apache.lucene.util.CollectionUtil;
/*     */ 
/*     */ final class IndexFileDeleter
/*     */ {
/*     */   private List<String> deletable;
/*  77 */   private Map<String, RefCount> refCounts = new HashMap();
/*     */ 
/*  84 */   private List<CommitPoint> commits = new ArrayList();
/*     */ 
/*  88 */   private List<Collection<String>> lastFiles = new ArrayList();
/*     */ 
/*  91 */   private List<CommitPoint> commitsToDelete = new ArrayList();
/*     */   private PrintStream infoStream;
/*     */   private Directory directory;
/*     */   private IndexDeletionPolicy policy;
/*     */   final boolean startingCommitDeleted;
/*     */   private SegmentInfos lastSegmentInfos;
/*     */   public static boolean VERBOSE_REF_COUNTS;
/*     */   private final IndexWriter writer;
/*     */ 
/*     */   void setInfoStream(PrintStream infoStream)
/*     */   {
/* 108 */     this.infoStream = infoStream;
/* 109 */     if (infoStream != null)
/* 110 */       message("setInfoStream deletionPolicy=" + this.policy);
/*     */   }
/*     */ 
/*     */   private void message(String message)
/*     */   {
/* 115 */     this.infoStream.println("IFD [" + new Date() + "; " + Thread.currentThread().getName() + "]: " + message);
/*     */   }
/*     */ 
/*     */   private boolean locked()
/*     */   {
/* 120 */     return (this.writer == null) || (Thread.holdsLock(this.writer));
/*     */   }
/*     */ 
/*     */   public IndexFileDeleter(Directory directory, IndexDeletionPolicy policy, SegmentInfos segmentInfos, PrintStream infoStream, IndexWriter writer)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 134 */     this.infoStream = infoStream;
/* 135 */     this.writer = writer;
/*     */ 
/* 137 */     String currentSegmentsFile = segmentInfos.getCurrentSegmentFileName();
/*     */ 
/* 139 */     if (infoStream != null) {
/* 140 */       message("init: current segments file is \"" + currentSegmentsFile + "\"; deletionPolicy=" + policy);
/*     */     }
/*     */ 
/* 143 */     this.policy = policy;
/* 144 */     this.directory = directory;
/*     */ 
/* 148 */     long currentGen = segmentInfos.getGeneration();
/* 149 */     IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
/*     */ 
/* 151 */     CommitPoint currentCommitPoint = null;
/* 152 */     String[] files = null;
/*     */     try {
/* 154 */       files = directory.listAll();
/*     */     }
/*     */     catch (NoSuchDirectoryException e) {
/* 157 */       files = new String[0];
/*     */     }
/*     */ 
/* 160 */     for (String fileName : files)
/*     */     {
/* 162 */       if ((!filter.accept(null, fileName)) || (fileName.equals("segments.gen"))) {
/*     */         continue;
/*     */       }
/* 165 */       getRefCount(fileName);
/*     */ 
/* 167 */       if (!fileName.startsWith("segments"))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 172 */       if (infoStream != null) {
/* 173 */         message("init: load commit \"" + fileName + "\"");
/*     */       }
/* 175 */       SegmentInfos sis = new SegmentInfos();
/*     */       try {
/* 177 */         sis.read(directory, fileName);
/*     */       }
/*     */       catch (FileNotFoundException e)
/*     */       {
/* 186 */         if (infoStream != null) {
/* 187 */           message("init: hit FileNotFoundException when loading commit \"" + fileName + "\"; skipping this commit point");
/*     */         }
/* 189 */         sis = null;
/*     */       } catch (IOException e) {
/* 191 */         if (SegmentInfos.generationFromSegmentsFileName(fileName) <= currentGen) {
/* 192 */           throw e;
/*     */         }
/*     */ 
/* 197 */         sis = null;
/*     */       }
/*     */ 
/* 200 */       if (sis != null) {
/* 201 */         CommitPoint commitPoint = new CommitPoint(this.commitsToDelete, directory, sis);
/* 202 */         if (sis.getGeneration() == segmentInfos.getGeneration()) {
/* 203 */           currentCommitPoint = commitPoint;
/*     */         }
/* 205 */         this.commits.add(commitPoint);
/* 206 */         incRef(sis, true);
/*     */ 
/* 208 */         if ((this.lastSegmentInfos == null) || (sis.getGeneration() > this.lastSegmentInfos.getGeneration())) {
/* 209 */           this.lastSegmentInfos = sis;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 216 */     if ((currentCommitPoint == null) && (currentSegmentsFile != null))
/*     */     {
/* 224 */       SegmentInfos sis = new SegmentInfos();
/*     */       try {
/* 226 */         sis.read(directory, currentSegmentsFile);
/*     */       } catch (IOException e) {
/* 228 */         throw new CorruptIndexException("failed to locate current segments_N file");
/*     */       }
/* 230 */       if (infoStream != null) {
/* 231 */         message("forced open of current segments file " + segmentInfos.getCurrentSegmentFileName());
/*     */       }
/* 233 */       currentCommitPoint = new CommitPoint(this.commitsToDelete, directory, sis);
/* 234 */       this.commits.add(currentCommitPoint);
/* 235 */       incRef(sis, true);
/*     */     }
/*     */ 
/* 239 */     CollectionUtil.mergeSort(this.commits);
/*     */ 
/* 244 */     for (Map.Entry entry : this.refCounts.entrySet()) {
/* 245 */       RefCount rc = (RefCount)entry.getValue();
/* 246 */       String fileName = (String)entry.getKey();
/* 247 */       if (0 == rc.count) {
/* 248 */         if (infoStream != null) {
/* 249 */           message("init: removing unreferenced file \"" + fileName + "\"");
/*     */         }
/* 251 */         deleteFile(fileName);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 257 */     if (currentSegmentsFile != null) {
/* 258 */       policy.onInit(this.commits);
/*     */     }
/*     */ 
/* 263 */     checkpoint(segmentInfos, false);
/*     */ 
/* 265 */     this.startingCommitDeleted = (currentCommitPoint == null ? false : currentCommitPoint.isDeleted());
/*     */ 
/* 267 */     deleteCommits();
/*     */   }
/*     */ 
/*     */   public SegmentInfos getLastSegmentInfos() {
/* 271 */     return this.lastSegmentInfos;
/*     */   }
/*     */ 
/*     */   private void deleteCommits()
/*     */     throws IOException
/*     */   {
/* 280 */     int size = this.commitsToDelete.size();
/*     */ 
/* 282 */     if (size > 0)
/*     */     {
/* 286 */       for (int i = 0; i < size; i++) {
/* 287 */         CommitPoint commit = (CommitPoint)this.commitsToDelete.get(i);
/* 288 */         if (this.infoStream != null) {
/* 289 */           message("deleteCommits: now decRef commit \"" + commit.getSegmentsFileName() + "\"");
/*     */         }
/* 291 */         for (String file : commit.files) {
/* 292 */           decRef(file);
/*     */         }
/*     */       }
/* 295 */       this.commitsToDelete.clear();
/*     */ 
/* 298 */       size = this.commits.size();
/* 299 */       int readFrom = 0;
/* 300 */       int writeTo = 0;
/* 301 */       while (readFrom < size) {
/* 302 */         CommitPoint commit = (CommitPoint)this.commits.get(readFrom);
/* 303 */         if (!commit.deleted) {
/* 304 */           if (writeTo != readFrom) {
/* 305 */             this.commits.set(writeTo, this.commits.get(readFrom));
/*     */           }
/* 307 */           writeTo++;
/*     */         }
/* 309 */         readFrom++;
/*     */       }
/*     */ 
/* 312 */       while (size > writeTo) {
/* 313 */         this.commits.remove(size - 1);
/* 314 */         size--;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void refresh(String segmentName)
/*     */     throws IOException
/*     */   {
/* 328 */     assert (locked());
/*     */ 
/* 330 */     String[] files = this.directory.listAll();
/* 331 */     IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
/*     */     String segmentPrefix2;
/*     */     String segmentPrefix1;
/*     */     String segmentPrefix2;
/* 334 */     if (segmentName != null) {
/* 335 */       String segmentPrefix1 = segmentName + ".";
/* 336 */       segmentPrefix2 = segmentName + "_";
/*     */     } else {
/* 338 */       segmentPrefix1 = null;
/* 339 */       segmentPrefix2 = null;
/*     */     }
/*     */ 
/* 342 */     for (int i = 0; i < files.length; i++) {
/* 343 */       String fileName = files[i];
/* 344 */       if ((!filter.accept(null, fileName)) || ((segmentName != null) && (!fileName.startsWith(segmentPrefix1)) && (!fileName.startsWith(segmentPrefix2))) || (this.refCounts.containsKey(fileName)) || (fileName.equals("segments.gen")))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 349 */       if (this.infoStream != null) {
/* 350 */         message("refresh [prefix=" + segmentName + "]: removing newly created unreferenced file \"" + fileName + "\"");
/*     */       }
/* 352 */       deleteFile(fileName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void refresh()
/*     */     throws IOException
/*     */   {
/* 361 */     assert (locked());
/* 362 */     this.deletable = null;
/* 363 */     refresh(null);
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 368 */     assert (locked());
/* 369 */     int size = this.lastFiles.size();
/* 370 */     if (size > 0) {
/* 371 */       for (int i = 0; i < size; i++) {
/* 372 */         decRef((Collection)this.lastFiles.get(i));
/*     */       }
/* 374 */       this.lastFiles.clear();
/*     */     }
/*     */ 
/* 377 */     deletePendingFiles();
/*     */   }
/*     */ 
/*     */   void revisitPolicy()
/*     */     throws IOException
/*     */   {
/* 390 */     assert (locked());
/* 391 */     if (this.infoStream != null) {
/* 392 */       message("now revisitPolicy");
/*     */     }
/*     */ 
/* 395 */     if (this.commits.size() > 0) {
/* 396 */       this.policy.onCommit(this.commits);
/* 397 */       deleteCommits();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deletePendingFiles() throws IOException {
/* 402 */     assert (locked());
/* 403 */     if (this.deletable != null) {
/* 404 */       List oldDeletable = this.deletable;
/* 405 */       this.deletable = null;
/* 406 */       int size = oldDeletable.size();
/* 407 */       for (int i = 0; i < size; i++) {
/* 408 */         if (this.infoStream != null) {
/* 409 */           message("delete pending file " + (String)oldDeletable.get(i));
/*     */         }
/* 411 */         deleteFile((String)oldDeletable.get(i));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkpoint(SegmentInfos segmentInfos, boolean isCommit)
/*     */     throws IOException
/*     */   {
/* 437 */     assert (locked());
/*     */ 
/* 439 */     if (this.infoStream != null) {
/* 440 */       message("now checkpoint \"" + segmentInfos.getCurrentSegmentFileName() + "\" [" + segmentInfos.size() + " segments " + "; isCommit = " + isCommit + "]");
/*     */     }
/*     */ 
/* 445 */     deletePendingFiles();
/*     */ 
/* 448 */     incRef(segmentInfos, isCommit);
/*     */ 
/* 450 */     if (isCommit)
/*     */     {
/* 452 */       this.commits.add(new CommitPoint(this.commitsToDelete, this.directory, segmentInfos));
/*     */ 
/* 455 */       this.policy.onCommit(this.commits);
/*     */ 
/* 458 */       deleteCommits();
/*     */     }
/*     */     else {
/* 461 */       for (Collection lastFile : this.lastFiles) {
/* 462 */         decRef(lastFile);
/*     */       }
/* 464 */       this.lastFiles.clear();
/*     */ 
/* 467 */       this.lastFiles.add(segmentInfos.files(this.directory, false));
/*     */     }
/*     */   }
/*     */ 
/*     */   void incRef(SegmentInfos segmentInfos, boolean isCommit) throws IOException {
/* 472 */     assert (locked());
/*     */ 
/* 475 */     for (String fileName : segmentInfos.files(this.directory, isCommit))
/* 476 */       incRef(fileName);
/*     */   }
/*     */ 
/*     */   void incRef(Collection<String> files) throws IOException
/*     */   {
/* 481 */     assert (locked());
/* 482 */     for (String file : files)
/* 483 */       incRef(file);
/*     */   }
/*     */ 
/*     */   void incRef(String fileName) throws IOException
/*     */   {
/* 488 */     assert (locked());
/* 489 */     RefCount rc = getRefCount(fileName);
/* 490 */     if ((this.infoStream != null) && (VERBOSE_REF_COUNTS)) {
/* 491 */       message("  IncRef \"" + fileName + "\": pre-incr count is " + rc.count);
/*     */     }
/* 493 */     rc.IncRef();
/*     */   }
/*     */ 
/*     */   void decRef(Collection<String> files) throws IOException {
/* 497 */     assert (locked());
/* 498 */     for (String file : files)
/* 499 */       decRef(file);
/*     */   }
/*     */ 
/*     */   void decRef(String fileName) throws IOException
/*     */   {
/* 504 */     assert (locked());
/* 505 */     RefCount rc = getRefCount(fileName);
/* 506 */     if ((this.infoStream != null) && (VERBOSE_REF_COUNTS)) {
/* 507 */       message("  DecRef \"" + fileName + "\": pre-decr count is " + rc.count);
/*     */     }
/* 509 */     if (0 == rc.DecRef())
/*     */     {
/* 512 */       deleteFile(fileName);
/* 513 */       this.refCounts.remove(fileName);
/*     */     }
/*     */   }
/*     */ 
/*     */   void decRef(SegmentInfos segmentInfos) throws IOException {
/* 518 */     assert (locked());
/* 519 */     for (String file : segmentInfos.files(this.directory, false))
/* 520 */       decRef(file);
/*     */   }
/*     */ 
/*     */   public boolean exists(String fileName)
/*     */   {
/* 525 */     assert (locked());
/* 526 */     if (!this.refCounts.containsKey(fileName)) {
/* 527 */       return false;
/*     */     }
/* 529 */     return getRefCount(fileName).count > 0;
/*     */   }
/*     */ 
/*     */   private RefCount getRefCount(String fileName)
/*     */   {
/* 534 */     assert (locked());
/*     */     RefCount rc;
/* 536 */     if (!this.refCounts.containsKey(fileName)) {
/* 537 */       RefCount rc = new RefCount(fileName);
/* 538 */       this.refCounts.put(fileName, rc);
/*     */     } else {
/* 540 */       rc = (RefCount)this.refCounts.get(fileName);
/*     */     }
/* 542 */     return rc;
/*     */   }
/*     */ 
/*     */   void deleteFiles(List<String> files) throws IOException {
/* 546 */     assert (locked());
/* 547 */     for (String file : files)
/* 548 */       deleteFile(file);
/*     */   }
/*     */ 
/*     */   void deleteNewFiles(Collection<String> files)
/*     */     throws IOException
/*     */   {
/* 555 */     assert (locked());
/* 556 */     for (String fileName : files)
/* 557 */       if (!this.refCounts.containsKey(fileName)) {
/* 558 */         if (this.infoStream != null) {
/* 559 */           message("delete new file \"" + fileName + "\"");
/*     */         }
/* 561 */         deleteFile(fileName);
/*     */       }
/*     */   }
/*     */ 
/*     */   void deleteFile(String fileName)
/*     */     throws IOException
/*     */   {
/* 568 */     assert (locked());
/*     */     try {
/* 570 */       if (this.infoStream != null) {
/* 571 */         message("delete \"" + fileName + "\"");
/*     */       }
/* 573 */       this.directory.deleteFile(fileName);
/*     */     } catch (IOException e) {
/* 575 */       if (this.directory.fileExists(fileName))
/*     */       {
/* 584 */         if (this.infoStream != null) {
/* 585 */           message("unable to remove file \"" + fileName + "\": " + e.toString() + "; Will re-try later.");
/*     */         }
/* 587 */         if (this.deletable == null) {
/* 588 */           this.deletable = new ArrayList();
/*     */         }
/* 590 */         this.deletable.add(fileName);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 102 */     VERBOSE_REF_COUNTS = false;
/*     */   }
/*     */ 
/*     */   private static final class CommitPoint extends IndexCommit
/*     */   {
/*     */     Collection<String> files;
/*     */     String segmentsFileName;
/*     */     boolean deleted;
/*     */     Directory directory;
/*     */     Collection<CommitPoint> commitsToDelete;
/*     */     long version;
/*     */     long generation;
/*     */     final boolean isOptimized;
/*     */     final Map<String, String> userData;
/*     */ 
/*     */     public CommitPoint(Collection<CommitPoint> commitsToDelete, Directory directory, SegmentInfos segmentInfos)
/*     */       throws IOException
/*     */     {
/* 644 */       this.directory = directory;
/* 645 */       this.commitsToDelete = commitsToDelete;
/* 646 */       this.userData = segmentInfos.getUserData();
/* 647 */       this.segmentsFileName = segmentInfos.getCurrentSegmentFileName();
/* 648 */       this.version = segmentInfos.getVersion();
/* 649 */       this.generation = segmentInfos.getGeneration();
/* 650 */       this.files = Collections.unmodifiableCollection(segmentInfos.files(directory, true));
/* 651 */       this.isOptimized = ((segmentInfos.size() == 1) && (!segmentInfos.info(0).hasDeletions()));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 656 */       return "IndexFileDeleter.CommitPoint(" + this.segmentsFileName + ")";
/*     */     }
/*     */ 
/*     */     public boolean isOptimized()
/*     */     {
/* 661 */       return this.isOptimized;
/*     */     }
/*     */ 
/*     */     public String getSegmentsFileName()
/*     */     {
/* 666 */       return this.segmentsFileName;
/*     */     }
/*     */ 
/*     */     public Collection<String> getFileNames() throws IOException
/*     */     {
/* 671 */       return this.files;
/*     */     }
/*     */ 
/*     */     public Directory getDirectory()
/*     */     {
/* 676 */       return this.directory;
/*     */     }
/*     */ 
/*     */     public long getVersion()
/*     */     {
/* 681 */       return this.version;
/*     */     }
/*     */ 
/*     */     public long getGeneration()
/*     */     {
/* 686 */       return this.generation;
/*     */     }
/*     */ 
/*     */     public Map<String, String> getUserData()
/*     */     {
/* 691 */       return this.userData;
/*     */     }
/*     */ 
/*     */     public void delete()
/*     */     {
/* 700 */       if (!this.deleted) {
/* 701 */         this.deleted = true;
/* 702 */         this.commitsToDelete.add(this);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isDeleted()
/*     */     {
/* 708 */       return this.deleted;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class RefCount
/*     */   {
/*     */     final String fileName;
/*     */     boolean initDone;
/*     */     int count;
/*     */ 
/*     */     RefCount(String fileName)
/*     */     {
/* 604 */       this.fileName = fileName;
/*     */     }
/*     */ 
/*     */     public int IncRef()
/*     */     {
/* 610 */       if (!this.initDone)
/* 611 */         this.initDone = true;
/*     */       else {
/* 613 */         assert (this.count > 0) : (Thread.currentThread().getName() + ": RefCount is 0 pre-increment for file \"" + this.fileName + "\"");
/*     */       }
/* 615 */       return ++this.count;
/*     */     }
/*     */ 
/*     */     public int DecRef() {
/* 619 */       assert (this.count > 0) : (Thread.currentThread().getName() + ": RefCount is 0 pre-decrement for file \"" + this.fileName + "\"");
/* 620 */       return --this.count;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexFileDeleter
 * JD-Core Version:    0.6.0
 */