/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.store.RAMOutputStream;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ final class TermVectorsTermsWriter extends TermsHashConsumer
/*     */ {
/*     */   final DocumentsWriter docWriter;
/*  34 */   PerDoc[] docFreeList = new PerDoc[1];
/*     */   int freeCount;
/*     */   IndexOutput tvx;
/*     */   IndexOutput tvd;
/*     */   IndexOutput tvf;
/*     */   int lastDocID;
/*     */   boolean hasVectors;
/*     */   int allocCount;
/*     */ 
/*     */   public TermVectorsTermsWriter(DocumentsWriter docWriter)
/*     */   {
/*  43 */     this.docWriter = docWriter;
/*     */   }
/*     */ 
/*     */   public TermsHashConsumerPerThread addThread(TermsHashPerThread termsHashPerThread)
/*     */   {
/*  48 */     return new TermVectorsTermsWriterPerThread(termsHashPerThread, this);
/*     */   }
/*     */ 
/*     */   synchronized void flush(Map<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> threadsAndFields, SegmentWriteState state) throws IOException
/*     */   {
/*  53 */     if (this.tvx != null)
/*     */     {
/*  55 */       fill(state.numDocs);
/*  56 */       IOUtils.close(new Closeable[] { this.tvx, this.tvf, this.tvd });
/*  57 */       this.tvx = (this.tvd = this.tvf = null);
/*  58 */       assert (state.segmentName != null);
/*  59 */       String idxName = IndexFileNames.segmentFileName(state.segmentName, "tvx");
/*  60 */       if (4L + state.numDocs * 16L != state.directory.fileLength(idxName)) {
/*  61 */         throw new RuntimeException("after flush: tvx size mismatch: " + state.numDocs + " docs vs " + state.directory.fileLength(idxName) + " length in bytes of " + idxName + " file exists?=" + state.directory.fileExists(idxName));
/*     */       }
/*     */ 
/*  64 */       this.lastDocID = 0;
/*  65 */       state.hasVectors = this.hasVectors;
/*  66 */       this.hasVectors = false;
/*     */     }
/*     */ 
/*  69 */     for (Map.Entry entry : threadsAndFields.entrySet()) {
/*  70 */       for (TermsHashConsumerPerField field : (Collection)entry.getValue()) {
/*  71 */         TermVectorsTermsWriterPerField perField = (TermVectorsTermsWriterPerField)field;
/*  72 */         perField.termsHashPerField.reset();
/*  73 */         perField.shrinkHash();
/*     */       }
/*     */ 
/*  76 */       TermVectorsTermsWriterPerThread perThread = (TermVectorsTermsWriterPerThread)entry.getKey();
/*  77 */       perThread.termsHashPerThread.reset(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized PerDoc getPerDoc()
/*     */   {
/*  84 */     if (this.freeCount == 0) {
/*  85 */       this.allocCount += 1;
/*  86 */       if (this.allocCount > this.docFreeList.length)
/*     */       {
/*  90 */         assert (this.allocCount == 1 + this.docFreeList.length);
/*  91 */         this.docFreeList = new PerDoc[ArrayUtil.oversize(this.allocCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/*     */       }
/*  93 */       return new PerDoc();
/*     */     }
/*  95 */     return this.docFreeList[(--this.freeCount)];
/*     */   }
/*     */ 
/*     */   void fill(int docID)
/*     */     throws IOException
/*     */   {
/* 102 */     if (this.lastDocID < docID) {
/* 103 */       long tvfPosition = this.tvf.getFilePointer();
/* 104 */       while (this.lastDocID < docID) {
/* 105 */         this.tvx.writeLong(this.tvd.getFilePointer());
/* 106 */         this.tvd.writeVInt(0);
/* 107 */         this.tvx.writeLong(tvfPosition);
/* 108 */         this.lastDocID += 1;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void initTermVectorsWriter() throws IOException {
/* 114 */     if (this.tvx == null) {
/* 115 */       boolean success = false;
/*     */       try
/*     */       {
/* 121 */         this.hasVectors = true;
/* 122 */         this.tvx = this.docWriter.directory.createOutput(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvx"));
/* 123 */         this.tvd = this.docWriter.directory.createOutput(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvd"));
/* 124 */         this.tvf = this.docWriter.directory.createOutput(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvf"));
/*     */ 
/* 126 */         this.tvx.writeInt(4);
/* 127 */         this.tvd.writeInt(4);
/* 128 */         this.tvf.writeInt(4);
/* 129 */         success = true;
/*     */       } finally {
/* 131 */         if (!success) {
/* 132 */           IOUtils.closeWhileHandlingException(new Closeable[] { this.tvx, this.tvd, this.tvf });
/*     */         }
/*     */       }
/* 135 */       this.lastDocID = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void finishDocument(PerDoc perDoc) throws IOException
/*     */   {
/* 141 */     assert (this.docWriter.writer.testPoint("TermVectorsTermsWriter.finishDocument start"));
/*     */ 
/* 143 */     initTermVectorsWriter();
/*     */ 
/* 145 */     fill(perDoc.docID);
/*     */ 
/* 148 */     this.tvx.writeLong(this.tvd.getFilePointer());
/* 149 */     this.tvx.writeLong(this.tvf.getFilePointer());
/* 150 */     this.tvd.writeVInt(perDoc.numVectorFields);
/* 151 */     if (perDoc.numVectorFields > 0) {
/* 152 */       for (int i = 0; i < perDoc.numVectorFields; i++) {
/* 153 */         this.tvd.writeVInt(perDoc.fieldNumbers[i]);
/*     */       }
/* 155 */       assert (0L == perDoc.fieldPointers[0]);
/* 156 */       long lastPos = perDoc.fieldPointers[0];
/* 157 */       for (int i = 1; i < perDoc.numVectorFields; i++) {
/* 158 */         long pos = perDoc.fieldPointers[i];
/* 159 */         this.tvd.writeVLong(pos - lastPos);
/* 160 */         lastPos = pos;
/*     */       }
/* 162 */       perDoc.perDocTvf.writeTo(this.tvf);
/* 163 */       perDoc.numVectorFields = 0;
/*     */     }
/*     */ 
/* 166 */     assert (this.lastDocID == perDoc.docID) : ("lastDocID=" + this.lastDocID + " perDoc.docID=" + perDoc.docID);
/*     */ 
/* 168 */     this.lastDocID += 1;
/*     */ 
/* 170 */     perDoc.reset();
/* 171 */     free(perDoc);
/* 172 */     assert (this.docWriter.writer.testPoint("TermVectorsTermsWriter.finishDocument end"));
/*     */   }
/*     */ 
/*     */   public void abort()
/*     */   {
/* 177 */     this.hasVectors = false;
/*     */     try {
/* 179 */       IOUtils.closeWhileHandlingException(new Closeable[] { this.tvx, this.tvd, this.tvf });
/*     */     }
/*     */     catch (IOException e) {
/* 182 */       throw new RuntimeException(e);
/*     */     }
/*     */     try
/*     */     {
/* 186 */       this.docWriter.directory.deleteFile(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvx"));
/*     */     }
/*     */     catch (IOException ignored) {
/*     */     }
/*     */     try {
/* 191 */       this.docWriter.directory.deleteFile(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvd"));
/*     */     }
/*     */     catch (IOException ignored) {
/*     */     }
/*     */     try {
/* 196 */       this.docWriter.directory.deleteFile(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvf"));
/*     */     }
/*     */     catch (IOException ignored) {
/*     */     }
/* 200 */     this.tvx = (this.tvd = this.tvf = null);
/* 201 */     this.lastDocID = 0;
/*     */   }
/*     */ 
/*     */   synchronized void free(PerDoc doc) {
/* 205 */     assert (this.freeCount < this.docFreeList.length);
/* 206 */     this.docFreeList[(this.freeCount++)] = doc;
/*     */   }
/*     */ 
/*     */   class PerDoc extends DocumentsWriter.DocWriter
/*     */   {
/* 211 */     final DocumentsWriter.PerDocBuffer buffer = TermVectorsTermsWriter.this.docWriter.newPerDocBuffer();
/* 212 */     RAMOutputStream perDocTvf = new RAMOutputStream(this.buffer);
/*     */     int numVectorFields;
/* 216 */     int[] fieldNumbers = new int[1];
/* 217 */     long[] fieldPointers = new long[1];
/*     */ 
/*     */     PerDoc() {  }
/*     */ 
/* 220 */     void reset() { this.perDocTvf.reset();
/* 221 */       this.buffer.recycle();
/* 222 */       this.numVectorFields = 0;
/*     */     }
/*     */ 
/*     */     void abort()
/*     */     {
/* 227 */       reset();
/* 228 */       TermVectorsTermsWriter.this.free(this);
/*     */     }
/*     */ 
/*     */     void addField(int fieldNumber) {
/* 232 */       if (this.numVectorFields == this.fieldNumbers.length) {
/* 233 */         this.fieldNumbers = ArrayUtil.grow(this.fieldNumbers);
/*     */       }
/* 235 */       if (this.numVectorFields == this.fieldPointers.length) {
/* 236 */         this.fieldPointers = ArrayUtil.grow(this.fieldPointers);
/*     */       }
/* 238 */       this.fieldNumbers[this.numVectorFields] = fieldNumber;
/* 239 */       this.fieldPointers[this.numVectorFields] = this.perDocTvf.getFilePointer();
/* 240 */       this.numVectorFields += 1;
/*     */     }
/*     */ 
/*     */     public long sizeInBytes()
/*     */     {
/* 245 */       return this.buffer.getSizeInBytes();
/*     */     }
/*     */ 
/*     */     public void finish() throws IOException
/*     */     {
/* 250 */       TermVectorsTermsWriter.this.finishDocument(this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorsTermsWriter
 * JD-Core Version:    0.6.0
 */