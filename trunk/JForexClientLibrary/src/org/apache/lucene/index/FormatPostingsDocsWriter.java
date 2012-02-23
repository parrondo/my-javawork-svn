/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ import org.apache.lucene.util.UnicodeUtil;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*     */ 
/*     */ final class FormatPostingsDocsWriter extends FormatPostingsDocsConsumer
/*     */   implements Closeable
/*     */ {
/*     */   final IndexOutput out;
/*     */   final FormatPostingsTermsWriter parent;
/*     */   final FormatPostingsPositionsWriter posWriter;
/*     */   final DefaultSkipListWriter skipListWriter;
/*     */   final int skipInterval;
/*     */   final int totalNumDocs;
/*     */   boolean omitTermFreqAndPositions;
/*     */   boolean storePayloads;
/*     */   long freqStart;
/*     */   FieldInfo fieldInfo;
/*     */   int lastDocID;
/*     */   int df;
/* 107 */   private final TermInfo termInfo = new TermInfo();
/* 108 */   final UnicodeUtil.UTF8Result utf8 = new UnicodeUtil.UTF8Result();
/*     */ 
/*     */   FormatPostingsDocsWriter(SegmentWriteState state, FormatPostingsTermsWriter parent)
/*     */     throws IOException
/*     */   {
/*  46 */     this.parent = parent;
/*  47 */     this.out = parent.parent.dir.createOutput(IndexFileNames.segmentFileName(parent.parent.segment, "frq"));
/*  48 */     boolean success = false;
/*     */     try {
/*  50 */       this.totalNumDocs = parent.parent.totalNumDocs;
/*     */ 
/*  53 */       this.skipInterval = parent.parent.termsOut.skipInterval;
/*  54 */       this.skipListWriter = parent.parent.skipListWriter;
/*  55 */       this.skipListWriter.setFreqOutput(this.out);
/*     */ 
/*  57 */       this.posWriter = new FormatPostingsPositionsWriter(state, this);
/*  58 */       success = true;
/*     */     } finally {
/*  60 */       if (!success)
/*  61 */         IOUtils.closeWhileHandlingException(new Closeable[] { this.out });
/*     */     }
/*     */   }
/*     */ 
/*     */   void setField(FieldInfo fieldInfo)
/*     */   {
/*  67 */     this.fieldInfo = fieldInfo;
/*  68 */     this.omitTermFreqAndPositions = (fieldInfo.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY);
/*  69 */     this.storePayloads = fieldInfo.storePayloads;
/*  70 */     this.posWriter.setField(fieldInfo);
/*     */   }
/*     */ 
/*     */   FormatPostingsPositionsConsumer addDoc(int docID, int termDocFreq)
/*     */     throws IOException
/*     */   {
/*  81 */     int delta = docID - this.lastDocID;
/*     */ 
/*  83 */     if ((docID < 0) || ((this.df > 0) && (delta <= 0))) {
/*  84 */       throw new CorruptIndexException("docs out of order (" + docID + " <= " + this.lastDocID + " )");
/*     */     }
/*  86 */     if (++this.df % this.skipInterval == 0)
/*     */     {
/*  88 */       this.skipListWriter.setSkipData(this.lastDocID, this.storePayloads, this.posWriter.lastPayloadLength);
/*  89 */       this.skipListWriter.bufferSkip(this.df);
/*     */     }
/*     */ 
/*  92 */     assert (docID < this.totalNumDocs) : ("docID=" + docID + " totalNumDocs=" + this.totalNumDocs);
/*     */ 
/*  94 */     this.lastDocID = docID;
/*  95 */     if (this.omitTermFreqAndPositions) {
/*  96 */       this.out.writeVInt(delta);
/*  97 */     } else if (1 == termDocFreq) {
/*  98 */       this.out.writeVInt(delta << 1 | 0x1);
/*     */     } else {
/* 100 */       this.out.writeVInt(delta << 1);
/* 101 */       this.out.writeVInt(termDocFreq);
/*     */     }
/*     */ 
/* 104 */     return this.posWriter;
/*     */   }
/*     */ 
/*     */   void finish()
/*     */     throws IOException
/*     */   {
/* 113 */     long skipPointer = this.skipListWriter.writeSkip(this.out);
/*     */ 
/* 117 */     this.termInfo.set(this.df, this.parent.freqStart, this.parent.proxStart, (int)(skipPointer - this.parent.freqStart));
/*     */ 
/* 120 */     UnicodeUtil.UTF16toUTF8(this.parent.currentTerm, this.parent.currentTermStart, this.utf8);
/*     */ 
/* 122 */     if (this.df > 0) {
/* 123 */       this.parent.termsOut.add(this.fieldInfo.number, this.utf8.result, this.utf8.length, this.termInfo);
/*     */     }
/*     */ 
/* 129 */     this.lastDocID = 0;
/* 130 */     this.df = 0;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 134 */     IOUtils.close(new Closeable[] { this.out, this.posWriter });
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsDocsWriter
 * JD-Core Version:    0.6.0
 */