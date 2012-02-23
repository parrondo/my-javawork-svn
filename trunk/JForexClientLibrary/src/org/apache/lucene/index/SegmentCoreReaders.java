/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ 
/*     */ final class SegmentCoreReaders
/*     */ {
/*  37 */   private final AtomicInteger ref = new AtomicInteger(1);
/*     */   final String segment;
/*     */   final FieldInfos fieldInfos;
/*     */   final IndexInput freqStream;
/*     */   final IndexInput proxStream;
/*     */   final TermInfosReader tisNoIndex;
/*     */   final Directory dir;
/*     */   final Directory cfsDir;
/*     */   final int readBufferSize;
/*     */   final int termsIndexDivisor;
/*     */   private final SegmentReader owner;
/*     */   TermInfosReader tis;
/*     */   FieldsReader fieldsReaderOrig;
/*     */   TermVectorsReader termVectorsReaderOrig;
/*     */   CompoundFileReader cfsReader;
/*     */   CompoundFileReader storeCFSReader;
/*     */ 
/*     */   SegmentCoreReaders(SegmentReader owner, Directory dir, SegmentInfo si, int readBufferSize, int termsIndexDivisor)
/*     */     throws IOException
/*     */   {
/*  59 */     this.segment = si.name;
/*  60 */     this.readBufferSize = readBufferSize;
/*  61 */     this.dir = dir;
/*     */ 
/*  63 */     boolean success = false;
/*     */     try
/*     */     {
/*  66 */       Directory dir0 = dir;
/*  67 */       if (si.getUseCompoundFile()) {
/*  68 */         this.cfsReader = new CompoundFileReader(dir, IndexFileNames.segmentFileName(this.segment, "cfs"), readBufferSize);
/*  69 */         dir0 = this.cfsReader;
/*     */       }
/*  71 */       this.cfsDir = dir0;
/*     */ 
/*  73 */       this.fieldInfos = new FieldInfos(this.cfsDir, IndexFileNames.segmentFileName(this.segment, "fnm"));
/*     */ 
/*  75 */       this.termsIndexDivisor = termsIndexDivisor;
/*  76 */       TermInfosReader reader = new TermInfosReader(this.cfsDir, this.segment, this.fieldInfos, readBufferSize, termsIndexDivisor);
/*  77 */       if (termsIndexDivisor == -1) {
/*  78 */         this.tisNoIndex = reader;
/*     */       } else {
/*  80 */         this.tis = reader;
/*  81 */         this.tisNoIndex = null;
/*     */       }
/*     */ 
/*  86 */       this.freqStream = this.cfsDir.openInput(IndexFileNames.segmentFileName(this.segment, "frq"), readBufferSize);
/*     */ 
/*  88 */       if (this.fieldInfos.hasProx())
/*  89 */         this.proxStream = this.cfsDir.openInput(IndexFileNames.segmentFileName(this.segment, "prx"), readBufferSize);
/*     */       else {
/*  91 */         this.proxStream = null;
/*     */       }
/*  93 */       success = true;
/*     */     } finally {
/*  95 */       if (!success) {
/*  96 */         decRef();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 104 */     this.owner = owner;
/*     */   }
/*     */ 
/*     */   synchronized TermVectorsReader getTermVectorsReaderOrig() {
/* 108 */     return this.termVectorsReaderOrig;
/*     */   }
/*     */ 
/*     */   synchronized FieldsReader getFieldsReaderOrig() {
/* 112 */     return this.fieldsReaderOrig;
/*     */   }
/*     */ 
/*     */   synchronized void incRef() {
/* 116 */     this.ref.incrementAndGet();
/*     */   }
/*     */ 
/*     */   synchronized Directory getCFSReader() {
/* 120 */     return this.cfsReader;
/*     */   }
/*     */ 
/*     */   synchronized TermInfosReader getTermsReader() {
/* 124 */     if (this.tis != null) {
/* 125 */       return this.tis;
/*     */     }
/* 127 */     return this.tisNoIndex;
/*     */   }
/*     */ 
/*     */   synchronized boolean termsIndexIsLoaded()
/*     */   {
/* 132 */     return this.tis != null;
/*     */   }
/*     */ 
/*     */   synchronized void loadTermsIndex(SegmentInfo si, int termsIndexDivisor)
/*     */     throws IOException
/*     */   {
/* 141 */     if (this.tis == null)
/*     */     {
/*     */       Directory dir0;
/*     */       Directory dir0;
/* 143 */       if (si.getUseCompoundFile())
/*     */       {
/* 148 */         if (this.cfsReader == null) {
/* 149 */           this.cfsReader = new CompoundFileReader(this.dir, IndexFileNames.segmentFileName(this.segment, "cfs"), this.readBufferSize);
/*     */         }
/* 151 */         dir0 = this.cfsReader;
/*     */       } else {
/* 153 */         dir0 = this.dir;
/*     */       }
/*     */ 
/* 156 */       this.tis = new TermInfosReader(dir0, this.segment, this.fieldInfos, this.readBufferSize, termsIndexDivisor);
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void decRef() throws IOException
/*     */   {
/* 162 */     if (this.ref.decrementAndGet() == 0) {
/* 163 */       IOUtils.close(new Closeable[] { this.tis, this.tisNoIndex, this.freqStream, this.proxStream, this.termVectorsReaderOrig, this.fieldsReaderOrig, this.cfsReader, this.storeCFSReader });
/*     */ 
/* 165 */       this.tis = null;
/*     */ 
/* 167 */       if (this.owner != null)
/* 168 */         this.owner.notifyReaderFinishedListeners();
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void openDocStores(SegmentInfo si)
/*     */     throws IOException
/*     */   {
/* 175 */     assert (si.name.equals(this.segment));
/*     */ 
/* 177 */     if (this.fieldsReaderOrig == null)
/*     */     {
/*     */       Directory storeDir;
/* 179 */       if (si.getDocStoreOffset() != -1) {
/* 180 */         if (si.getDocStoreIsCompoundFile()) {
/* 181 */           assert (this.storeCFSReader == null);
/* 182 */           this.storeCFSReader = new CompoundFileReader(this.dir, IndexFileNames.segmentFileName(si.getDocStoreSegment(), "cfx"), this.readBufferSize);
/*     */ 
/* 185 */           Directory storeDir = this.storeCFSReader;
/* 186 */           if ((!$assertionsDisabled) && (storeDir == null)) throw new AssertionError(); 
/*     */         }
/*     */         else {
/* 188 */           Directory storeDir = this.dir;
/* 189 */           if ((!$assertionsDisabled) && (storeDir == null)) throw new AssertionError(); 
/*     */         }
/*     */       }
/* 191 */       else if (si.getUseCompoundFile())
/*     */       {
/* 195 */         if (this.cfsReader == null) {
/* 196 */           this.cfsReader = new CompoundFileReader(this.dir, IndexFileNames.segmentFileName(this.segment, "cfs"), this.readBufferSize);
/*     */         }
/* 198 */         Directory storeDir = this.cfsReader;
/* 199 */         if ((!$assertionsDisabled) && (storeDir == null)) throw new AssertionError(); 
/*     */       }
/*     */       else {
/* 201 */         storeDir = this.dir;
/* 202 */         assert (storeDir != null);
/*     */       }
/*     */       String storesSegment;
/*     */       String storesSegment;
/* 206 */       if (si.getDocStoreOffset() != -1)
/* 207 */         storesSegment = si.getDocStoreSegment();
/*     */       else {
/* 209 */         storesSegment = this.segment;
/*     */       }
/*     */ 
/* 212 */       this.fieldsReaderOrig = new FieldsReader(storeDir, storesSegment, this.fieldInfos, this.readBufferSize, si.getDocStoreOffset(), si.docCount);
/*     */ 
/* 216 */       if ((si.getDocStoreOffset() == -1) && (this.fieldsReaderOrig.size() != si.docCount)) {
/* 217 */         throw new CorruptIndexException("doc counts differ for segment " + this.segment + ": fieldsReader shows " + this.fieldsReaderOrig.size() + " but segmentInfo shows " + si.docCount);
/*     */       }
/*     */ 
/* 220 */       if (si.getHasVectors())
/* 221 */         this.termVectorsReaderOrig = new TermVectorsReader(storeDir, storesSegment, this.fieldInfos, this.readBufferSize, si.getDocStoreOffset(), si.docCount);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 228 */     return "SegmentCoreReader(owner=" + this.owner + ")";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentCoreReaders
 * JD-Core Version:    0.6.0
 */