/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.search.Similarity;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.util.BitVector;
/*     */ import org.apache.lucene.util.CloseableThreadLocal;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ public class SegmentReader extends IndexReader
/*     */   implements Cloneable
/*     */ {
/*     */   protected boolean readOnly;
/*     */   private SegmentInfo si;
/*     */   private int readBufferSize;
/*  52 */   CloseableThreadLocal<FieldsReader> fieldsReaderLocal = new FieldsReaderLocal(null);
/*  53 */   CloseableThreadLocal<TermVectorsReader> termVectorsLocal = new CloseableThreadLocal();
/*     */ 
/*  55 */   BitVector deletedDocs = null;
/*  56 */   AtomicInteger deletedDocsRef = null;
/*  57 */   private boolean deletedDocsDirty = false;
/*  58 */   private boolean normsDirty = false;
/*     */   private int pendingDeleteCount;
/*  64 */   private boolean rollbackHasChanges = false;
/*  65 */   private boolean rollbackDeletedDocsDirty = false;
/*  66 */   private boolean rollbackNormsDirty = false;
/*     */   private SegmentInfo rollbackSegmentInfo;
/*     */   private int rollbackPendingDeleteCount;
/*     */   IndexInput singleNormStream;
/*     */   AtomicInteger singleNormRef;
/*     */   SegmentCoreReaders core;
/*  86 */   Map<String, SegmentNorms> norms = new HashMap();
/*     */ 
/*     */   public static SegmentReader get(boolean readOnly, SegmentInfo si, int termInfosIndexDivisor)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/*  93 */     return get(readOnly, si.dir, si, 1024, true, termInfosIndexDivisor);
/*     */   }
/*     */ 
/*     */   public static SegmentReader get(boolean readOnly, Directory dir, SegmentInfo si, int readBufferSize, boolean doOpenStores, int termInfosIndexDivisor)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 107 */     SegmentReader instance = readOnly ? new ReadOnlySegmentReader() : new SegmentReader();
/* 108 */     instance.readOnly = readOnly;
/* 109 */     instance.si = si;
/* 110 */     instance.readBufferSize = readBufferSize;
/*     */ 
/* 112 */     boolean success = false;
/*     */     try
/*     */     {
/* 115 */       instance.core = new SegmentCoreReaders(instance, dir, si, readBufferSize, termInfosIndexDivisor);
/* 116 */       if (doOpenStores) {
/* 117 */         instance.core.openDocStores(si);
/*     */       }
/* 119 */       instance.loadDeletedDocs();
/* 120 */       instance.openNorms(instance.core.cfsDir, readBufferSize);
/* 121 */       success = true;
/*     */     }
/*     */     finally
/*     */     {
/* 129 */       if (!success) {
/* 130 */         instance.doClose();
/*     */       }
/*     */     }
/* 133 */     return instance;
/*     */   }
/*     */ 
/*     */   void openDocStores() throws IOException {
/* 137 */     this.core.openDocStores(this.si);
/*     */   }
/*     */ 
/*     */   private boolean checkDeletedCounts() throws IOException {
/* 141 */     int recomputedCount = this.deletedDocs.getRecomputedCount();
/*     */ 
/* 143 */     assert (this.deletedDocs.count() == recomputedCount) : ("deleted count=" + this.deletedDocs.count() + " vs recomputed count=" + recomputedCount);
/*     */ 
/* 146 */     assert (this.si.getDelCount() == recomputedCount) : ("delete count mismatch: info=" + this.si.getDelCount() + " vs BitVector=" + recomputedCount);
/*     */ 
/* 151 */     assert (this.si.getDelCount() <= maxDoc()) : ("delete count mismatch: " + recomputedCount + ") exceeds max doc (" + maxDoc() + ") for segment " + this.si.name);
/*     */ 
/* 153 */     return true;
/*     */   }
/*     */ 
/*     */   private void loadDeletedDocs() throws IOException
/*     */   {
/* 158 */     if (hasDeletions(this.si)) {
/* 159 */       this.deletedDocs = new BitVector(directory(), this.si.getDelFileName());
/* 160 */       this.deletedDocsRef = new AtomicInteger(1);
/* 161 */       assert (checkDeletedCounts());
/* 162 */       if (this.deletedDocs.size() != this.si.docCount)
/* 163 */         throw new CorruptIndexException("document count mismatch: deleted docs count " + this.deletedDocs.size() + " vs segment doc count " + this.si.docCount + " segment=" + this.si.name);
/*     */     }
/*     */     else {
/* 166 */       assert (this.si.getDelCount() == 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected byte[] cloneNormBytes(byte[] bytes)
/*     */   {
/* 175 */     byte[] cloneBytes = new byte[bytes.length];
/* 176 */     System.arraycopy(bytes, 0, cloneBytes, 0, bytes.length);
/* 177 */     return cloneBytes;
/*     */   }
/*     */ 
/*     */   protected BitVector cloneDeletedDocs(BitVector bv)
/*     */   {
/* 186 */     return (BitVector)bv.clone();
/*     */   }
/*     */ 
/*     */   public final synchronized Object clone()
/*     */   {
/*     */     try {
/* 192 */       return clone(this.readOnly); } catch (Exception ex) {
/*     */     }
/* 194 */     throw new RuntimeException(ex);
/*     */   }
/*     */ 
/*     */   public final synchronized IndexReader clone(boolean openReadOnly)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 200 */     return reopenSegment(this.si, true, openReadOnly);
/*     */   }
/*     */ 
/*     */   public synchronized IndexReader reopen()
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 206 */     return reopenSegment(this.si, false, this.readOnly);
/*     */   }
/*     */ 
/*     */   public synchronized IndexReader reopen(boolean openReadOnly)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 212 */     return reopenSegment(this.si, false, openReadOnly);
/*     */   }
/*     */ 
/*     */   synchronized SegmentReader reopenSegment(SegmentInfo si, boolean doClone, boolean openReadOnly) throws CorruptIndexException, IOException {
/* 216 */     boolean deletionsUpToDate = (this.si.hasDeletions() == si.hasDeletions()) && ((!si.hasDeletions()) || (this.si.getDelFileName().equals(si.getDelFileName())));
/*     */ 
/* 218 */     boolean normsUpToDate = true;
/*     */ 
/* 220 */     boolean[] fieldNormsChanged = new boolean[this.core.fieldInfos.size()];
/* 221 */     int fieldCount = this.core.fieldInfos.size();
/* 222 */     for (int i = 0; i < fieldCount; i++) {
/* 223 */       if (!this.si.getNormFileName(i).equals(si.getNormFileName(i))) {
/* 224 */         normsUpToDate = false;
/* 225 */         fieldNormsChanged[i] = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 231 */     if ((normsUpToDate) && (deletionsUpToDate) && (!doClone) && (openReadOnly) && (this.readOnly)) {
/* 232 */       return this;
/*     */     }
/*     */ 
/* 237 */     assert ((!doClone) || ((normsUpToDate) && (deletionsUpToDate)));
/*     */ 
/* 240 */     SegmentReader clone = openReadOnly ? new ReadOnlySegmentReader() : new SegmentReader();
/*     */ 
/* 242 */     boolean success = false;
/*     */     try {
/* 244 */       this.core.incRef();
/* 245 */       clone.core = this.core;
/* 246 */       clone.readOnly = openReadOnly;
/* 247 */       clone.si = si;
/* 248 */       clone.readBufferSize = this.readBufferSize;
/* 249 */       clone.pendingDeleteCount = this.pendingDeleteCount;
/* 250 */       clone.readerFinishedListeners = this.readerFinishedListeners;
/*     */ 
/* 252 */       if ((!openReadOnly) && (this.hasChanges))
/*     */       {
/* 254 */         clone.deletedDocsDirty = this.deletedDocsDirty;
/* 255 */         clone.normsDirty = this.normsDirty;
/* 256 */         clone.hasChanges = this.hasChanges;
/* 257 */         this.hasChanges = false;
/*     */       }
/*     */ 
/* 260 */       if (doClone) {
/* 261 */         if (this.deletedDocs != null) {
/* 262 */           this.deletedDocsRef.incrementAndGet();
/* 263 */           clone.deletedDocs = this.deletedDocs;
/* 264 */           clone.deletedDocsRef = this.deletedDocsRef;
/*     */         }
/*     */       }
/* 267 */       else if (!deletionsUpToDate)
/*     */       {
/* 269 */         assert (clone.deletedDocs == null);
/* 270 */         clone.loadDeletedDocs();
/* 271 */       } else if (this.deletedDocs != null) {
/* 272 */         this.deletedDocsRef.incrementAndGet();
/* 273 */         clone.deletedDocs = this.deletedDocs;
/* 274 */         clone.deletedDocsRef = this.deletedDocsRef;
/*     */       }
/*     */ 
/* 278 */       clone.norms = new HashMap();
/*     */ 
/* 281 */       for (int i = 0; i < fieldNormsChanged.length; i++)
/*     */       {
/* 284 */         if ((doClone) || (fieldNormsChanged[i] == 0)) {
/* 285 */           String curField = this.core.fieldInfos.fieldInfo(i).name;
/* 286 */           SegmentNorms norm = (SegmentNorms)this.norms.get(curField);
/* 287 */           if (norm != null) {
/* 288 */             clone.norms.put(curField, (SegmentNorms)norm.clone());
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 294 */       clone.openNorms(si.getUseCompoundFile() ? this.core.getCFSReader() : directory(), this.readBufferSize);
/*     */ 
/* 296 */       success = true;
/*     */     } finally {
/* 298 */       if (!success)
/*     */       {
/* 301 */         clone.decRef();
/*     */       }
/*     */     }
/*     */ 
/* 305 */     return clone;
/*     */   }
/*     */ 
/*     */   protected void doCommit(Map<String, String> commitUserData) throws IOException
/*     */   {
/* 310 */     if (this.hasChanges) {
/* 311 */       startCommit();
/* 312 */       boolean success = false;
/*     */       try {
/* 314 */         commitChanges(commitUserData);
/* 315 */         success = true;
/*     */       } finally {
/* 317 */         if (!success)
/* 318 */           rollbackCommit();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void commitChanges(Map<String, String> commitUserData) throws IOException
/*     */   {
/* 325 */     if (this.deletedDocsDirty) {
/* 326 */       this.si.advanceDelGen();
/*     */ 
/* 328 */       assert (this.deletedDocs.size() == this.si.docCount);
/*     */ 
/* 333 */       String delFileName = this.si.getDelFileName();
/* 334 */       boolean success = false;
/*     */       try {
/* 336 */         this.deletedDocs.write(directory(), delFileName);
/* 337 */         success = true;
/*     */       } finally {
/* 339 */         if (!success) {
/*     */           try {
/* 341 */             directory().deleteFile(delFileName);
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 349 */       this.si.setDelCount(this.si.getDelCount() + this.pendingDeleteCount);
/* 350 */       this.pendingDeleteCount = 0;
/* 351 */       assert (this.deletedDocs.count() == this.si.getDelCount()) : ("delete count mismatch during commit: info=" + this.si.getDelCount() + " vs BitVector=" + this.deletedDocs.count());
/*     */     } else {
/* 353 */       assert (this.pendingDeleteCount == 0);
/*     */     }
/*     */ 
/* 356 */     if (this.normsDirty) {
/* 357 */       this.si.setNumFields(this.core.fieldInfos.size());
/* 358 */       for (SegmentNorms norm : this.norms.values()) {
/* 359 */         if (norm.dirty) {
/* 360 */           norm.reWrite(this.si);
/*     */         }
/*     */       }
/*     */     }
/* 364 */     this.deletedDocsDirty = false;
/* 365 */     this.normsDirty = false;
/* 366 */     this.hasChanges = false;
/*     */   }
/*     */ 
/*     */   FieldsReader getFieldsReader() {
/* 370 */     return (FieldsReader)this.fieldsReaderLocal.get();
/*     */   }
/*     */ 
/*     */   protected void doClose() throws IOException
/*     */   {
/* 375 */     this.termVectorsLocal.close();
/* 376 */     this.fieldsReaderLocal.close();
/*     */ 
/* 378 */     if (this.deletedDocs != null) {
/* 379 */       this.deletedDocsRef.decrementAndGet();
/*     */ 
/* 381 */       this.deletedDocs = null;
/*     */     }
/*     */ 
/* 384 */     for (SegmentNorms norm : this.norms.values()) {
/* 385 */       norm.decRef();
/*     */     }
/* 387 */     if (this.core != null)
/* 388 */       this.core.decRef();
/*     */   }
/*     */ 
/*     */   static boolean hasDeletions(SegmentInfo si)
/*     */     throws IOException
/*     */   {
/* 394 */     return si.hasDeletions();
/*     */   }
/*     */ 
/*     */   public boolean hasDeletions()
/*     */   {
/* 400 */     return this.deletedDocs != null;
/*     */   }
/*     */ 
/*     */   static boolean usesCompoundFile(SegmentInfo si) throws IOException {
/* 404 */     return si.getUseCompoundFile();
/*     */   }
/*     */ 
/*     */   static boolean hasSeparateNorms(SegmentInfo si) throws IOException {
/* 408 */     return si.hasSeparateNorms();
/*     */   }
/*     */ 
/*     */   protected void doDelete(int docNum)
/*     */   {
/* 413 */     if (this.deletedDocs == null) {
/* 414 */       this.deletedDocs = new BitVector(maxDoc());
/* 415 */       this.deletedDocsRef = new AtomicInteger(1);
/*     */     }
/*     */ 
/* 420 */     if (this.deletedDocsRef.get() > 1) {
/* 421 */       AtomicInteger oldRef = this.deletedDocsRef;
/* 422 */       this.deletedDocs = cloneDeletedDocs(this.deletedDocs);
/* 423 */       this.deletedDocsRef = new AtomicInteger(1);
/* 424 */       oldRef.decrementAndGet();
/*     */     }
/* 426 */     this.deletedDocsDirty = true;
/* 427 */     if (!this.deletedDocs.getAndSet(docNum))
/* 428 */       this.pendingDeleteCount += 1;
/*     */   }
/*     */ 
/*     */   protected void doUndeleteAll()
/*     */   {
/* 434 */     this.deletedDocsDirty = false;
/* 435 */     if (this.deletedDocs != null) {
/* 436 */       assert (this.deletedDocsRef != null);
/* 437 */       this.deletedDocsRef.decrementAndGet();
/* 438 */       this.deletedDocs = null;
/* 439 */       this.deletedDocsRef = null;
/* 440 */       this.pendingDeleteCount = 0;
/* 441 */       this.si.clearDelGen();
/* 442 */       this.si.setDelCount(0);
/*     */     } else {
/* 444 */       assert (this.deletedDocsRef == null);
/* 445 */       assert (this.pendingDeleteCount == 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   List<String> files() throws IOException {
/* 450 */     return new ArrayList(this.si.files());
/*     */   }
/*     */ 
/*     */   public TermEnum terms()
/*     */   {
/* 455 */     ensureOpen();
/* 456 */     return this.core.getTermsReader().terms();
/*     */   }
/*     */ 
/*     */   public TermEnum terms(Term t) throws IOException
/*     */   {
/* 461 */     ensureOpen();
/* 462 */     return this.core.getTermsReader().terms(t);
/*     */   }
/*     */ 
/*     */   FieldInfos fieldInfos() {
/* 466 */     return this.core.fieldInfos;
/*     */   }
/*     */ 
/*     */   public Document document(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException
/*     */   {
/* 471 */     ensureOpen();
/* 472 */     if ((n < 0) || (n >= maxDoc())) {
/* 473 */       throw new IllegalArgumentException("docID must be >= 0 and < maxDoc=" + maxDoc() + " (got docID=" + n + ")");
/*     */     }
/* 475 */     return getFieldsReader().doc(n, fieldSelector);
/*     */   }
/*     */ 
/*     */   public synchronized boolean isDeleted(int n)
/*     */   {
/* 480 */     return (this.deletedDocs != null) && (this.deletedDocs.get(n));
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs(Term term) throws IOException
/*     */   {
/* 485 */     if (term == null) {
/* 486 */       return new AllTermDocs(this);
/*     */     }
/* 488 */     return super.termDocs(term);
/*     */   }
/*     */ 
/*     */   public TermDocs termDocs()
/*     */     throws IOException
/*     */   {
/* 494 */     ensureOpen();
/* 495 */     return new SegmentTermDocs(this);
/*     */   }
/*     */ 
/*     */   public TermPositions termPositions() throws IOException
/*     */   {
/* 500 */     ensureOpen();
/* 501 */     return new SegmentTermPositions(this);
/*     */   }
/*     */ 
/*     */   public int docFreq(Term t) throws IOException
/*     */   {
/* 506 */     ensureOpen();
/* 507 */     TermInfo ti = this.core.getTermsReader().get(t);
/* 508 */     if (ti != null) {
/* 509 */       return ti.docFreq;
/*     */     }
/* 511 */     return 0;
/*     */   }
/*     */ 
/*     */   public int numDocs()
/*     */   {
/* 517 */     int n = maxDoc();
/* 518 */     if (this.deletedDocs != null)
/* 519 */       n -= this.deletedDocs.count();
/* 520 */     return n;
/*     */   }
/*     */ 
/*     */   public int maxDoc()
/*     */   {
/* 526 */     return this.si.docCount;
/*     */   }
/*     */ 
/*     */   public Collection<String> getFieldNames(IndexReader.FieldOption fieldOption)
/*     */   {
/* 534 */     ensureOpen();
/*     */ 
/* 536 */     Set fieldSet = new HashSet();
/* 537 */     for (int i = 0; i < this.core.fieldInfos.size(); i++) {
/* 538 */       FieldInfo fi = this.core.fieldInfos.fieldInfo(i);
/* 539 */       if (fieldOption == IndexReader.FieldOption.ALL) {
/* 540 */         fieldSet.add(fi.name);
/*     */       }
/* 542 */       else if ((!fi.isIndexed) && (fieldOption == IndexReader.FieldOption.UNINDEXED)) {
/* 543 */         fieldSet.add(fi.name);
/*     */       }
/* 545 */       else if ((fi.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) && (fieldOption == IndexReader.FieldOption.OMIT_TERM_FREQ_AND_POSITIONS)) {
/* 546 */         fieldSet.add(fi.name);
/*     */       }
/* 548 */       else if ((fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS) && (fieldOption == IndexReader.FieldOption.OMIT_POSITIONS)) {
/* 549 */         fieldSet.add(fi.name);
/*     */       }
/* 551 */       else if ((fi.storePayloads) && (fieldOption == IndexReader.FieldOption.STORES_PAYLOADS)) {
/* 552 */         fieldSet.add(fi.name);
/*     */       }
/* 554 */       else if ((fi.isIndexed) && (fieldOption == IndexReader.FieldOption.INDEXED)) {
/* 555 */         fieldSet.add(fi.name);
/*     */       }
/* 557 */       else if ((fi.isIndexed) && (!fi.storeTermVector) && (fieldOption == IndexReader.FieldOption.INDEXED_NO_TERMVECTOR)) {
/* 558 */         fieldSet.add(fi.name);
/*     */       }
/* 560 */       else if ((fi.storeTermVector == true) && (!fi.storePositionWithTermVector) && (!fi.storeOffsetWithTermVector) && (fieldOption == IndexReader.FieldOption.TERMVECTOR))
/*     */       {
/* 564 */         fieldSet.add(fi.name);
/*     */       }
/* 566 */       else if ((fi.isIndexed) && (fi.storeTermVector) && (fieldOption == IndexReader.FieldOption.INDEXED_WITH_TERMVECTOR)) {
/* 567 */         fieldSet.add(fi.name);
/*     */       }
/* 569 */       else if ((fi.storePositionWithTermVector) && (!fi.storeOffsetWithTermVector) && (fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_POSITION)) {
/* 570 */         fieldSet.add(fi.name);
/*     */       }
/* 572 */       else if ((fi.storeOffsetWithTermVector) && (!fi.storePositionWithTermVector) && (fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_OFFSET)) {
/* 573 */         fieldSet.add(fi.name);
/*     */       } else {
/* 575 */         if ((!fi.storeOffsetWithTermVector) || (!fi.storePositionWithTermVector) || (fieldOption != IndexReader.FieldOption.TERMVECTOR_WITH_POSITION_OFFSET))
/*     */           continue;
/* 577 */         fieldSet.add(fi.name);
/*     */       }
/*     */     }
/* 580 */     return fieldSet;
/*     */   }
/*     */ 
/*     */   public boolean hasNorms(String field)
/*     */   {
/* 585 */     ensureOpen();
/* 586 */     return this.norms.containsKey(field);
/*     */   }
/*     */ 
/*     */   public byte[] norms(String field) throws IOException
/*     */   {
/* 591 */     ensureOpen();
/* 592 */     SegmentNorms norm = (SegmentNorms)this.norms.get(field);
/* 593 */     if (norm == null)
/*     */     {
/* 595 */       return null;
/*     */     }
/* 597 */     return norm.bytes();
/*     */   }
/*     */ 
/*     */   protected void doSetNorm(int doc, String field, byte value)
/*     */     throws IOException
/*     */   {
/* 603 */     SegmentNorms norm = (SegmentNorms)this.norms.get(field);
/* 604 */     if (norm == null)
/*     */     {
/* 606 */       throw new IllegalStateException("Cannot setNorm for field " + field + ": norms were omitted");
/*     */     }
/*     */ 
/* 609 */     this.normsDirty = true;
/* 610 */     norm.copyOnWrite()[doc] = value;
/*     */   }
/*     */ 
/*     */   public synchronized void norms(String field, byte[] bytes, int offset)
/*     */     throws IOException
/*     */   {
/* 618 */     ensureOpen();
/* 619 */     SegmentNorms norm = (SegmentNorms)this.norms.get(field);
/* 620 */     if (norm == null) {
/* 621 */       Arrays.fill(bytes, offset, bytes.length, Similarity.getDefault().encodeNormValue(1.0F));
/* 622 */       return;
/*     */     }
/*     */ 
/* 625 */     norm.bytes(bytes, offset, maxDoc());
/*     */   }
/*     */ 
/*     */   int getPostingsSkipInterval()
/*     */   {
/* 631 */     return this.core.getTermsReader().getSkipInterval();
/*     */   }
/*     */ 
/*     */   private void openNorms(Directory cfsDir, int readBufferSize) throws IOException {
/* 635 */     long nextNormSeek = SegmentNorms.NORMS_HEADER.length;
/* 636 */     int maxDoc = maxDoc();
/* 637 */     for (int i = 0; i < this.core.fieldInfos.size(); i++) {
/* 638 */       FieldInfo fi = this.core.fieldInfos.fieldInfo(i);
/* 639 */       if (this.norms.containsKey(fi.name))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 644 */       if ((fi.isIndexed) && (!fi.omitNorms)) {
/* 645 */         Directory d = directory();
/* 646 */         String fileName = this.si.getNormFileName(fi.number);
/* 647 */         if (!this.si.hasSeparateNorms(fi.number)) {
/* 648 */           d = cfsDir;
/*     */         }
/*     */ 
/* 652 */         boolean singleNormFile = IndexFileNames.matchesExtension(fileName, "nrm");
/* 653 */         IndexInput normInput = null;
/*     */         long normSeek;
/* 656 */         if (singleNormFile) {
/* 657 */           long normSeek = nextNormSeek;
/* 658 */           if (this.singleNormStream == null) {
/* 659 */             this.singleNormStream = d.openInput(fileName, readBufferSize);
/* 660 */             this.singleNormRef = new AtomicInteger(1);
/*     */           } else {
/* 662 */             this.singleNormRef.incrementAndGet();
/*     */           }
/*     */ 
/* 667 */           normInput = this.singleNormStream;
/*     */         } else {
/* 669 */           normInput = d.openInput(fileName);
/*     */ 
/* 674 */           String version = this.si.getVersion();
/* 675 */           boolean isUnversioned = ((version == null) || (StringHelper.getVersionComparator().compare(version, "3.2") < 0)) && (normInput.length() == maxDoc());
/*     */           long normSeek;
/* 678 */           if (isUnversioned)
/* 679 */             normSeek = 0L;
/*     */           else {
/* 681 */             normSeek = SegmentNorms.NORMS_HEADER.length;
/*     */           }
/*     */         }
/*     */ 
/* 685 */         this.norms.put(fi.name, new SegmentNorms(normInput, fi.number, normSeek, this));
/* 686 */         nextNormSeek += maxDoc;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean termsIndexLoaded() {
/* 692 */     return this.core.termsIndexIsLoaded();
/*     */   }
/*     */ 
/*     */   void loadTermsIndex(int termsIndexDivisor)
/*     */     throws IOException
/*     */   {
/* 701 */     this.core.loadTermsIndex(this.si, termsIndexDivisor);
/*     */   }
/*     */ 
/*     */   boolean normsClosed()
/*     */   {
/* 706 */     if (this.singleNormStream != null) {
/* 707 */       return false;
/*     */     }
/* 709 */     for (SegmentNorms norm : this.norms.values()) {
/* 710 */       if (norm.refCount > 0) {
/* 711 */         return false;
/*     */       }
/*     */     }
/* 714 */     return true;
/*     */   }
/*     */ 
/*     */   boolean normsClosed(String field)
/*     */   {
/* 719 */     return ((SegmentNorms)this.norms.get(field)).refCount == 0;
/*     */   }
/*     */ 
/*     */   TermVectorsReader getTermVectorsReader()
/*     */   {
/* 727 */     TermVectorsReader tvReader = (TermVectorsReader)this.termVectorsLocal.get();
/* 728 */     if (tvReader == null) {
/* 729 */       TermVectorsReader orig = this.core.getTermVectorsReaderOrig();
/* 730 */       if (orig == null)
/* 731 */         return null;
/*     */       try
/*     */       {
/* 734 */         tvReader = (TermVectorsReader)orig.clone();
/*     */       } catch (CloneNotSupportedException cnse) {
/* 736 */         return null;
/*     */       }
/*     */ 
/* 739 */       this.termVectorsLocal.set(tvReader);
/*     */     }
/* 741 */     return tvReader;
/*     */   }
/*     */ 
/*     */   TermVectorsReader getTermVectorsReaderOrig() {
/* 745 */     return this.core.getTermVectorsReaderOrig();
/*     */   }
/*     */ 
/*     */   public TermFreqVector getTermFreqVector(int docNumber, String field)
/*     */     throws IOException
/*     */   {
/* 757 */     ensureOpen();
/* 758 */     FieldInfo fi = this.core.fieldInfos.fieldInfo(field);
/* 759 */     if ((fi == null) || (!fi.storeTermVector)) {
/* 760 */       return null;
/*     */     }
/* 762 */     TermVectorsReader termVectorsReader = getTermVectorsReader();
/* 763 */     if (termVectorsReader == null) {
/* 764 */       return null;
/*     */     }
/* 766 */     return termVectorsReader.get(docNumber, field);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 772 */     ensureOpen();
/* 773 */     FieldInfo fi = this.core.fieldInfos.fieldInfo(field);
/* 774 */     if ((fi == null) || (!fi.storeTermVector)) {
/* 775 */       return;
/*     */     }
/* 777 */     TermVectorsReader termVectorsReader = getTermVectorsReader();
/* 778 */     if (termVectorsReader == null) {
/* 779 */       return;
/*     */     }
/*     */ 
/* 783 */     termVectorsReader.get(docNumber, field, mapper);
/*     */   }
/*     */ 
/*     */   public void getTermFreqVector(int docNumber, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 789 */     ensureOpen();
/*     */ 
/* 791 */     TermVectorsReader termVectorsReader = getTermVectorsReader();
/* 792 */     if (termVectorsReader == null) {
/* 793 */       return;
/*     */     }
/* 795 */     termVectorsReader.get(docNumber, mapper);
/*     */   }
/*     */ 
/*     */   public TermFreqVector[] getTermFreqVectors(int docNumber)
/*     */     throws IOException
/*     */   {
/* 807 */     ensureOpen();
/*     */ 
/* 809 */     TermVectorsReader termVectorsReader = getTermVectorsReader();
/* 810 */     if (termVectorsReader == null) {
/* 811 */       return null;
/*     */     }
/* 813 */     return termVectorsReader.get(docNumber);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 819 */     StringBuilder buffer = new StringBuilder();
/* 820 */     if (this.hasChanges) {
/* 821 */       buffer.append('*');
/*     */     }
/* 823 */     buffer.append(this.si.toString(this.core.dir, this.pendingDeleteCount));
/* 824 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public String getSegmentName()
/*     */   {
/* 831 */     return this.core.segment;
/*     */   }
/*     */ 
/*     */   SegmentInfo getSegmentInfo()
/*     */   {
/* 838 */     return this.si;
/*     */   }
/*     */ 
/*     */   void setSegmentInfo(SegmentInfo info) {
/* 842 */     this.si = info;
/*     */   }
/*     */ 
/*     */   void startCommit() {
/* 846 */     this.rollbackSegmentInfo = ((SegmentInfo)this.si.clone());
/* 847 */     this.rollbackHasChanges = this.hasChanges;
/* 848 */     this.rollbackDeletedDocsDirty = this.deletedDocsDirty;
/* 849 */     this.rollbackNormsDirty = this.normsDirty;
/* 850 */     this.rollbackPendingDeleteCount = this.pendingDeleteCount;
/* 851 */     for (SegmentNorms norm : this.norms.values())
/* 852 */       norm.rollbackDirty = norm.dirty;
/*     */   }
/*     */ 
/*     */   void rollbackCommit()
/*     */   {
/* 857 */     this.si.reset(this.rollbackSegmentInfo);
/* 858 */     this.hasChanges = this.rollbackHasChanges;
/* 859 */     this.deletedDocsDirty = this.rollbackDeletedDocsDirty;
/* 860 */     this.normsDirty = this.rollbackNormsDirty;
/* 861 */     this.pendingDeleteCount = this.rollbackPendingDeleteCount;
/* 862 */     for (SegmentNorms norm : this.norms.values())
/* 863 */       norm.dirty = norm.rollbackDirty;
/*     */   }
/*     */ 
/*     */   public Directory directory()
/*     */   {
/* 873 */     return this.core.dir;
/*     */   }
/*     */ 
/*     */   public final Object getCoreCacheKey()
/*     */   {
/* 881 */     return this.core.freqStream;
/*     */   }
/*     */ 
/*     */   public Object getDeletesCacheKey()
/*     */   {
/* 886 */     return this.deletedDocs;
/*     */   }
/*     */ 
/*     */   public long getUniqueTermCount()
/*     */   {
/* 891 */     return this.core.getTermsReader().size();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   static SegmentReader getOnlySegmentReader(Directory dir)
/*     */     throws IOException
/*     */   {
/* 903 */     return getOnlySegmentReader(IndexReader.open(dir, false));
/*     */   }
/*     */ 
/*     */   static SegmentReader getOnlySegmentReader(IndexReader reader) {
/* 907 */     if ((reader instanceof SegmentReader)) {
/* 908 */       return (SegmentReader)reader;
/*     */     }
/* 910 */     if ((reader instanceof DirectoryReader)) {
/* 911 */       IndexReader[] subReaders = reader.getSequentialSubReaders();
/* 912 */       if (subReaders.length != 1) {
/* 913 */         throw new IllegalArgumentException(reader + " has " + subReaders.length + " segments instead of exactly one");
/*     */       }
/* 915 */       return (SegmentReader)subReaders[0];
/*     */     }
/*     */ 
/* 918 */     throw new IllegalArgumentException(reader + " is not a SegmentReader or a single-segment DirectoryReader");
/*     */   }
/*     */ 
/*     */   public int getTermInfosIndexDivisor()
/*     */   {
/* 923 */     return this.core.termsIndexDivisor;
/*     */   }
/*     */ 
/*     */   protected void readerFinished()
/*     */   {
/*     */   }
/*     */ 
/*     */   private class FieldsReaderLocal extends CloseableThreadLocal<FieldsReader>
/*     */   {
/*     */     private FieldsReaderLocal()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected FieldsReader initialValue()
/*     */     {
/*  82 */       return (FieldsReader)SegmentReader.this.core.getFieldsReaderOrig().clone();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentReader
 * JD-Core Version:    0.6.0
 */