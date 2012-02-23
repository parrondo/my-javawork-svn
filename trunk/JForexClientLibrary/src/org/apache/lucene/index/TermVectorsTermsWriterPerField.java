/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.store.RAMOutputStream;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.UnicodeUtil;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*     */ 
/*     */ final class TermVectorsTermsWriterPerField extends TermsHashConsumerPerField
/*     */ {
/*     */   final TermVectorsTermsWriterPerThread perThread;
/*     */   final TermsHashPerField termsHashPerField;
/*     */   final TermVectorsTermsWriter termsWriter;
/*     */   final FieldInfo fieldInfo;
/*     */   final DocumentsWriter.DocState docState;
/*     */   final FieldInvertState fieldState;
/*     */   boolean doVectors;
/*     */   boolean doVectorPositions;
/*     */   boolean doVectorOffsets;
/*     */   int maxNumPostings;
/*  42 */   OffsetAttribute offsetAttribute = null;
/*     */ 
/*     */   public TermVectorsTermsWriterPerField(TermsHashPerField termsHashPerField, TermVectorsTermsWriterPerThread perThread, FieldInfo fieldInfo) {
/*  45 */     this.termsHashPerField = termsHashPerField;
/*  46 */     this.perThread = perThread;
/*  47 */     this.termsWriter = perThread.termsWriter;
/*  48 */     this.fieldInfo = fieldInfo;
/*  49 */     this.docState = termsHashPerField.docState;
/*  50 */     this.fieldState = termsHashPerField.fieldState;
/*     */   }
/*     */ 
/*     */   int getStreamCount()
/*     */   {
/*  55 */     return 2;
/*     */   }
/*     */ 
/*     */   boolean start(Fieldable[] fields, int count)
/*     */   {
/*  60 */     this.doVectors = false;
/*  61 */     this.doVectorPositions = false;
/*  62 */     this.doVectorOffsets = false;
/*     */ 
/*  64 */     for (int i = 0; i < count; i++) {
/*  65 */       Fieldable field = fields[i];
/*  66 */       if ((field.isIndexed()) && (field.isTermVectorStored())) {
/*  67 */         this.doVectors = true;
/*  68 */         this.doVectorPositions |= field.isStorePositionWithTermVector();
/*  69 */         this.doVectorOffsets |= field.isStoreOffsetWithTermVector();
/*     */       }
/*     */     }
/*     */ 
/*  73 */     if (this.doVectors) {
/*  74 */       if (this.perThread.doc == null) {
/*  75 */         this.perThread.doc = this.termsWriter.getPerDoc();
/*  76 */         this.perThread.doc.docID = this.docState.docID;
/*  77 */         assert (this.perThread.doc.numVectorFields == 0);
/*  78 */         assert (0L == this.perThread.doc.perDocTvf.length());
/*  79 */         assert (0L == this.perThread.doc.perDocTvf.getFilePointer());
/*     */       }
/*     */ 
/*  82 */       assert (this.perThread.doc.docID == this.docState.docID);
/*     */ 
/*  84 */       if (this.termsHashPerField.numPostings != 0)
/*     */       {
/*  88 */         this.termsHashPerField.reset();
/*  89 */         this.perThread.termsHashPerThread.reset(false);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  96 */     return this.doVectors;
/*     */   }
/*     */ 
/*     */   public void abort()
/*     */   {
/*     */   }
/*     */ 
/*     */   void finish()
/*     */     throws IOException
/*     */   {
/* 108 */     assert (this.docState.testPoint("TermVectorsTermsWriterPerField.finish start"));
/*     */ 
/* 110 */     int numPostings = this.termsHashPerField.numPostings;
/*     */ 
/* 112 */     assert (numPostings >= 0);
/*     */ 
/* 114 */     if ((!this.doVectors) || (numPostings == 0)) {
/* 115 */       return;
/*     */     }
/* 117 */     if (numPostings > this.maxNumPostings) {
/* 118 */       this.maxNumPostings = numPostings;
/*     */     }
/* 120 */     IndexOutput tvf = this.perThread.doc.perDocTvf;
/*     */ 
/* 126 */     assert (this.fieldInfo.storeTermVector);
/* 127 */     assert (this.perThread.vectorFieldsInOrder(this.fieldInfo));
/*     */ 
/* 129 */     this.perThread.doc.addField(this.termsHashPerField.fieldInfo.number);
/* 130 */     TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
/*     */ 
/* 132 */     int[] termIDs = this.termsHashPerField.sortPostings();
/*     */ 
/* 134 */     tvf.writeVInt(numPostings);
/* 135 */     byte bits = 0;
/* 136 */     if (this.doVectorPositions)
/* 137 */       bits = (byte)(bits | 0x1);
/* 138 */     if (this.doVectorOffsets)
/* 139 */       bits = (byte)(bits | 0x2);
/* 140 */     tvf.writeByte(bits);
/*     */ 
/* 142 */     int encoderUpto = 0;
/* 143 */     int lastTermBytesCount = 0;
/*     */ 
/* 145 */     ByteSliceReader reader = this.perThread.vectorSliceReader;
/* 146 */     char[][] charBuffers = this.perThread.termsHashPerThread.charPool.buffers;
/* 147 */     for (int j = 0; j < numPostings; j++) {
/* 148 */       int termID = termIDs[j];
/* 149 */       int freq = postings.freqs[termID];
/*     */ 
/* 151 */       char[] text2 = charBuffers[(postings.textStarts[termID] >> 14)];
/* 152 */       int start2 = postings.textStarts[termID] & 0x3FFF;
/*     */ 
/* 156 */       UnicodeUtil.UTF8Result utf8Result = this.perThread.utf8Results[encoderUpto];
/*     */ 
/* 159 */       UnicodeUtil.UTF16toUTF8(text2, start2, utf8Result);
/* 160 */       int termBytesCount = utf8Result.length;
/*     */ 
/* 165 */       int prefix = 0;
/* 166 */       if (j > 0) {
/* 167 */         byte[] lastTermBytes = this.perThread.utf8Results[(1 - encoderUpto)].result;
/* 168 */         byte[] termBytes = this.perThread.utf8Results[encoderUpto].result;
/* 169 */         while ((prefix < lastTermBytesCount) && (prefix < termBytesCount) && 
/* 170 */           (lastTermBytes[prefix] == termBytes[prefix]))
/*     */         {
/* 172 */           prefix++;
/*     */         }
/*     */       }
/* 175 */       encoderUpto = 1 - encoderUpto;
/* 176 */       lastTermBytesCount = termBytesCount;
/*     */ 
/* 178 */       int suffix = termBytesCount - prefix;
/* 179 */       tvf.writeVInt(prefix);
/* 180 */       tvf.writeVInt(suffix);
/* 181 */       tvf.writeBytes(utf8Result.result, prefix, suffix);
/* 182 */       tvf.writeVInt(freq);
/*     */ 
/* 184 */       if (this.doVectorPositions) {
/* 185 */         this.termsHashPerField.initReader(reader, termID, 0);
/* 186 */         reader.writeTo(tvf);
/*     */       }
/*     */ 
/* 189 */       if (this.doVectorOffsets) {
/* 190 */         this.termsHashPerField.initReader(reader, termID, 1);
/* 191 */         reader.writeTo(tvf);
/*     */       }
/*     */     }
/*     */ 
/* 195 */     this.termsHashPerField.reset();
/*     */ 
/* 202 */     this.perThread.termsHashPerThread.reset(false);
/*     */   }
/*     */ 
/*     */   void shrinkHash() {
/* 206 */     this.termsHashPerField.shrinkHash(this.maxNumPostings);
/* 207 */     this.maxNumPostings = 0;
/*     */   }
/*     */ 
/*     */   void start(Fieldable f)
/*     */   {
/* 212 */     if (this.doVectorOffsets)
/* 213 */       this.offsetAttribute = ((OffsetAttribute)this.fieldState.attributeSource.addAttribute(OffsetAttribute.class));
/*     */     else
/* 215 */       this.offsetAttribute = null;
/*     */   }
/*     */ 
/*     */   void newTerm(int termID)
/*     */   {
/* 222 */     assert (this.docState.testPoint("TermVectorsTermsWriterPerField.newTerm start"));
/*     */ 
/* 224 */     TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
/*     */ 
/* 226 */     postings.freqs[termID] = 1;
/*     */ 
/* 228 */     if (this.doVectorOffsets) {
/* 229 */       int startOffset = this.fieldState.offset + this.offsetAttribute.startOffset();
/* 230 */       int endOffset = this.fieldState.offset + this.offsetAttribute.endOffset();
/*     */ 
/* 232 */       this.termsHashPerField.writeVInt(1, startOffset);
/* 233 */       this.termsHashPerField.writeVInt(1, endOffset - startOffset);
/* 234 */       postings.lastOffsets[termID] = endOffset;
/*     */     }
/*     */ 
/* 237 */     if (this.doVectorPositions) {
/* 238 */       this.termsHashPerField.writeVInt(0, this.fieldState.position);
/* 239 */       postings.lastPositions[termID] = this.fieldState.position;
/*     */     }
/*     */   }
/*     */ 
/*     */   void addTerm(int termID)
/*     */   {
/* 246 */     assert (this.docState.testPoint("TermVectorsTermsWriterPerField.addTerm start"));
/*     */ 
/* 248 */     TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
/*     */ 
/* 250 */     postings.freqs[termID] += 1;
/*     */ 
/* 252 */     if (this.doVectorOffsets) {
/* 253 */       int startOffset = this.fieldState.offset + this.offsetAttribute.startOffset();
/* 254 */       int endOffset = this.fieldState.offset + this.offsetAttribute.endOffset();
/*     */ 
/* 256 */       this.termsHashPerField.writeVInt(1, startOffset - postings.lastOffsets[termID]);
/* 257 */       this.termsHashPerField.writeVInt(1, endOffset - startOffset);
/* 258 */       postings.lastOffsets[termID] = endOffset;
/*     */     }
/*     */ 
/* 261 */     if (this.doVectorPositions) {
/* 262 */       this.termsHashPerField.writeVInt(0, this.fieldState.position - postings.lastPositions[termID]);
/* 263 */       postings.lastPositions[termID] = this.fieldState.position;
/*     */     }
/*     */   }
/*     */ 
/*     */   void skippingLongTerm()
/*     */   {
/*     */   }
/*     */ 
/*     */   ParallelPostingsArray createPostingsArray(int size) {
/* 272 */     return new TermVectorsPostingsArray(size); } 
/*     */   static final class TermVectorsPostingsArray extends ParallelPostingsArray { int[] freqs;
/*     */     int[] lastOffsets;
/*     */     int[] lastPositions;
/*     */ 
/* 277 */     public TermVectorsPostingsArray(int size) { super();
/* 278 */       this.freqs = new int[size];
/* 279 */       this.lastOffsets = new int[size];
/* 280 */       this.lastPositions = new int[size];
/*     */     }
/*     */ 
/*     */     ParallelPostingsArray newInstance(int size)
/*     */     {
/* 289 */       return new TermVectorsPostingsArray(size);
/*     */     }
/*     */ 
/*     */     void copyTo(ParallelPostingsArray toArray, int numToCopy)
/*     */     {
/* 294 */       assert ((toArray instanceof TermVectorsPostingsArray));
/* 295 */       TermVectorsPostingsArray to = (TermVectorsPostingsArray)toArray;
/*     */ 
/* 297 */       super.copyTo(toArray, numToCopy);
/*     */ 
/* 299 */       System.arraycopy(this.freqs, 0, to.freqs, 0, this.size);
/* 300 */       System.arraycopy(this.lastOffsets, 0, to.lastOffsets, 0, this.size);
/* 301 */       System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, this.size);
/*     */     }
/*     */ 
/*     */     int bytesPerPosting()
/*     */     {
/* 306 */       return super.bytesPerPosting() + 12;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorsTermsWriterPerField
 * JD-Core Version:    0.6.0
 */