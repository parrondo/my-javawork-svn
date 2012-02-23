/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.RAMOutputStream;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ final class StoredFieldsWriter
/*     */ {
/*     */   FieldsWriter fieldsWriter;
/*     */   final DocumentsWriter docWriter;
/*     */   final FieldInfos fieldInfos;
/*     */   int lastDocID;
/*  33 */   PerDoc[] docFreeList = new PerDoc[1];
/*     */   int freeCount;
/*     */   int allocCount;
/*     */ 
/*     */   public StoredFieldsWriter(DocumentsWriter docWriter, FieldInfos fieldInfos)
/*     */   {
/*  37 */     this.docWriter = docWriter;
/*  38 */     this.fieldInfos = fieldInfos;
/*     */   }
/*     */ 
/*     */   public StoredFieldsWriterPerThread addThread(DocumentsWriter.DocState docState) throws IOException {
/*  42 */     return new StoredFieldsWriterPerThread(docState, this);
/*     */   }
/*     */ 
/*     */   public synchronized void flush(SegmentWriteState state) throws IOException {
/*  46 */     if (state.numDocs > this.lastDocID) {
/*  47 */       initFieldsWriter();
/*  48 */       fill(state.numDocs);
/*     */     }
/*     */ 
/*  51 */     if (this.fieldsWriter != null) {
/*  52 */       this.fieldsWriter.close();
/*  53 */       this.fieldsWriter = null;
/*  54 */       this.lastDocID = 0;
/*     */ 
/*  56 */       String fieldsIdxName = IndexFileNames.segmentFileName(state.segmentName, "fdx");
/*  57 */       if (4L + state.numDocs * 8L != state.directory.fileLength(fieldsIdxName))
/*  58 */         throw new RuntimeException("after flush: fdx size mismatch: " + state.numDocs + " docs vs " + state.directory.fileLength(fieldsIdxName) + " length in bytes of " + fieldsIdxName + " file exists?=" + state.directory.fileExists(fieldsIdxName));
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void initFieldsWriter() throws IOException
/*     */   {
/*  64 */     if (this.fieldsWriter == null) {
/*  65 */       this.fieldsWriter = new FieldsWriter(this.docWriter.directory, this.docWriter.getSegment(), this.fieldInfos);
/*  66 */       this.lastDocID = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized PerDoc getPerDoc()
/*     */   {
/*  73 */     if (this.freeCount == 0) {
/*  74 */       this.allocCount += 1;
/*  75 */       if (this.allocCount > this.docFreeList.length)
/*     */       {
/*  79 */         assert (this.allocCount == 1 + this.docFreeList.length);
/*  80 */         this.docFreeList = new PerDoc[ArrayUtil.oversize(this.allocCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
/*     */       }
/*  82 */       return new PerDoc();
/*     */     }
/*  84 */     return this.docFreeList[(--this.freeCount)];
/*     */   }
/*     */ 
/*     */   synchronized void abort()
/*     */   {
/*  89 */     if (this.fieldsWriter != null) {
/*  90 */       this.fieldsWriter.abort();
/*  91 */       this.fieldsWriter = null;
/*  92 */       this.lastDocID = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   void fill(int docID)
/*     */     throws IOException
/*     */   {
/* 100 */     while (this.lastDocID < docID) {
/* 101 */       this.fieldsWriter.skipDocument();
/* 102 */       this.lastDocID += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void finishDocument(PerDoc perDoc) throws IOException {
/* 107 */     assert (this.docWriter.writer.testPoint("StoredFieldsWriter.finishDocument start"));
/* 108 */     initFieldsWriter();
/*     */ 
/* 110 */     fill(perDoc.docID);
/*     */ 
/* 113 */     this.fieldsWriter.flushDocument(perDoc.numStoredFields, perDoc.fdt);
/* 114 */     this.lastDocID += 1;
/* 115 */     perDoc.reset();
/* 116 */     free(perDoc);
/* 117 */     assert (this.docWriter.writer.testPoint("StoredFieldsWriter.finishDocument end"));
/*     */   }
/*     */ 
/*     */   synchronized void free(PerDoc perDoc) {
/* 121 */     assert (this.freeCount < this.docFreeList.length);
/* 122 */     assert (0 == perDoc.numStoredFields);
/* 123 */     assert (0L == perDoc.fdt.length());
/* 124 */     assert (0L == perDoc.fdt.getFilePointer());
/* 125 */     this.docFreeList[(this.freeCount++)] = perDoc; } 
/* 129 */   class PerDoc extends DocumentsWriter.DocWriter { final DocumentsWriter.PerDocBuffer buffer = StoredFieldsWriter.this.docWriter.newPerDocBuffer();
/* 130 */     RAMOutputStream fdt = new RAMOutputStream(this.buffer);
/*     */     int numStoredFields;
/*     */ 
/*     */     PerDoc() {  }
/*     */ 
/* 134 */     void reset() { this.fdt.reset();
/* 135 */       this.buffer.recycle();
/* 136 */       this.numStoredFields = 0;
/*     */     }
/*     */ 
/*     */     void abort()
/*     */     {
/* 141 */       reset();
/* 142 */       StoredFieldsWriter.this.free(this);
/*     */     }
/*     */ 
/*     */     public long sizeInBytes()
/*     */     {
/* 147 */       return this.buffer.getSizeInBytes();
/*     */     }
/*     */ 
/*     */     public void finish() throws IOException
/*     */     {
/* 152 */       StoredFieldsWriter.this.finishDocument(this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.StoredFieldsWriter
 * JD-Core Version:    0.6.0
 */