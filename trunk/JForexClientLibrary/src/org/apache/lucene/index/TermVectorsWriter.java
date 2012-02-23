/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ import org.apache.lucene.util.UnicodeUtil;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*     */ 
/*     */ final class TermVectorsWriter
/*     */ {
/*  30 */   private IndexOutput tvx = null; private IndexOutput tvd = null; private IndexOutput tvf = null;
/*     */   private FieldInfos fieldInfos;
/*  32 */   final UnicodeUtil.UTF8Result[] utf8Results = { new UnicodeUtil.UTF8Result(), new UnicodeUtil.UTF8Result() };
/*     */ 
/*     */   public TermVectorsWriter(Directory directory, String segment, FieldInfos fieldInfos)
/*     */     throws IOException
/*     */   {
/*  37 */     boolean success = false;
/*     */     try
/*     */     {
/*  40 */       this.tvx = directory.createOutput(IndexFileNames.segmentFileName(segment, "tvx"));
/*  41 */       this.tvx.writeInt(4);
/*  42 */       this.tvd = directory.createOutput(IndexFileNames.segmentFileName(segment, "tvd"));
/*  43 */       this.tvd.writeInt(4);
/*  44 */       this.tvf = directory.createOutput(IndexFileNames.segmentFileName(segment, "tvf"));
/*  45 */       this.tvf.writeInt(4);
/*  46 */       success = true;
/*     */     } finally {
/*  48 */       if (!success) {
/*  49 */         IOUtils.closeWhileHandlingException(new Closeable[] { this.tvx, this.tvd, this.tvf });
/*     */       }
/*     */     }
/*     */ 
/*  53 */     this.fieldInfos = fieldInfos;
/*     */   }
/*     */ 
/*     */   public final void addAllDocVectors(TermFreqVector[] vectors)
/*     */     throws IOException
/*     */   {
/*  65 */     this.tvx.writeLong(this.tvd.getFilePointer());
/*  66 */     this.tvx.writeLong(this.tvf.getFilePointer());
/*     */ 
/*  68 */     if (vectors != null) {
/*  69 */       int numFields = vectors.length;
/*  70 */       this.tvd.writeVInt(numFields);
/*     */ 
/*  72 */       long[] fieldPointers = new long[numFields];
/*     */ 
/*  74 */       for (int i = 0; i < numFields; i++) {
/*  75 */         fieldPointers[i] = this.tvf.getFilePointer();
/*     */ 
/*  77 */         int fieldNumber = this.fieldInfos.fieldNumber(vectors[i].getField());
/*     */ 
/*  80 */         this.tvd.writeVInt(fieldNumber);
/*     */ 
/*  82 */         int numTerms = vectors[i].size();
/*  83 */         this.tvf.writeVInt(numTerms);
/*     */         byte bits;
/*     */         TermPositionVector tpVector;
/*     */         byte bits;
/*     */         boolean storePositions;
/*     */         boolean storeOffsets;
/*  91 */         if ((vectors[i] instanceof TermPositionVector))
/*     */         {
/*  93 */           TermPositionVector tpVector = (TermPositionVector)vectors[i];
/*  94 */           boolean storePositions = (tpVector.size() > 0) && (tpVector.getTermPositions(0) != null);
/*  95 */           boolean storeOffsets = (tpVector.size() > 0) && (tpVector.getOffsets(0) != null);
/*  96 */           bits = (byte)((storePositions ? 1 : 0) + (storeOffsets ? 2 : 0));
/*     */         }
/*     */         else {
/*  99 */           tpVector = null;
/* 100 */           bits = 0;
/* 101 */           storePositions = false;
/* 102 */           storeOffsets = false;
/*     */         }
/*     */ 
/* 105 */         this.tvf.writeVInt(bits);
/*     */ 
/* 107 */         String[] terms = vectors[i].getTerms();
/* 108 */         int[] freqs = vectors[i].getTermFrequencies();
/*     */ 
/* 110 */         int utf8Upto = 0;
/* 111 */         this.utf8Results[1].length = 0;
/*     */ 
/* 113 */         for (int j = 0; j < numTerms; j++)
/*     */         {
/* 115 */           UnicodeUtil.UTF16toUTF8(terms[j], 0, terms[j].length(), this.utf8Results[utf8Upto]);
/*     */ 
/* 117 */           int start = StringHelper.bytesDifference(this.utf8Results[(1 - utf8Upto)].result, this.utf8Results[(1 - utf8Upto)].length, this.utf8Results[utf8Upto].result, this.utf8Results[utf8Upto].length);
/*     */ 
/* 121 */           int length = this.utf8Results[utf8Upto].length - start;
/* 122 */           this.tvf.writeVInt(start);
/* 123 */           this.tvf.writeVInt(length);
/* 124 */           this.tvf.writeBytes(this.utf8Results[utf8Upto].result, start, length);
/* 125 */           utf8Upto = 1 - utf8Upto;
/*     */ 
/* 127 */           int termFreq = freqs[j];
/*     */ 
/* 129 */           this.tvf.writeVInt(termFreq);
/*     */ 
/* 131 */           if (storePositions) {
/* 132 */             int[] positions = tpVector.getTermPositions(j);
/* 133 */             if (positions == null)
/* 134 */               throw new IllegalStateException("Trying to write positions that are null!");
/* 135 */             assert (positions.length == termFreq);
/*     */ 
/* 138 */             int lastPosition = 0;
/* 139 */             for (int k = 0; k < positions.length; k++) {
/* 140 */               int position = positions[k];
/* 141 */               this.tvf.writeVInt(position - lastPosition);
/* 142 */               lastPosition = position;
/*     */             }
/*     */           }
/*     */ 
/* 146 */           if (storeOffsets) {
/* 147 */             TermVectorOffsetInfo[] offsets = tpVector.getOffsets(j);
/* 148 */             if (offsets == null)
/* 149 */               throw new IllegalStateException("Trying to write offsets that are null!");
/* 150 */             assert (offsets.length == termFreq);
/*     */ 
/* 153 */             int lastEndOffset = 0;
/* 154 */             for (int k = 0; k < offsets.length; k++) {
/* 155 */               int startOffset = offsets[k].getStartOffset();
/* 156 */               int endOffset = offsets[k].getEndOffset();
/* 157 */               this.tvf.writeVInt(startOffset - lastEndOffset);
/* 158 */               this.tvf.writeVInt(endOffset - startOffset);
/* 159 */               lastEndOffset = endOffset;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 166 */       if (numFields > 1) {
/* 167 */         long lastFieldPointer = fieldPointers[0];
/* 168 */         for (int i = 1; i < numFields; i++) {
/* 169 */           long fieldPointer = fieldPointers[i];
/* 170 */           this.tvd.writeVLong(fieldPointer - lastFieldPointer);
/* 171 */           lastFieldPointer = fieldPointer;
/*     */         }
/*     */       }
/*     */     } else {
/* 175 */       this.tvd.writeVInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void addRawDocuments(TermVectorsReader reader, int[] tvdLengths, int[] tvfLengths, int numDocs)
/*     */     throws IOException
/*     */   {
/* 184 */     long tvdPosition = this.tvd.getFilePointer();
/* 185 */     long tvfPosition = this.tvf.getFilePointer();
/* 186 */     long tvdStart = tvdPosition;
/* 187 */     long tvfStart = tvfPosition;
/* 188 */     for (int i = 0; i < numDocs; i++) {
/* 189 */       this.tvx.writeLong(tvdPosition);
/* 190 */       tvdPosition += tvdLengths[i];
/* 191 */       this.tvx.writeLong(tvfPosition);
/* 192 */       tvfPosition += tvfLengths[i];
/*     */     }
/* 194 */     this.tvd.copyBytes(reader.getTvdStream(), tvdPosition - tvdStart);
/* 195 */     this.tvf.copyBytes(reader.getTvfStream(), tvfPosition - tvfStart);
/* 196 */     assert (this.tvd.getFilePointer() == tvdPosition);
/* 197 */     assert (this.tvf.getFilePointer() == tvfPosition);
/*     */   }
/*     */ 
/*     */   final void close()
/*     */     throws IOException
/*     */   {
/* 204 */     IOUtils.close(new Closeable[] { this.tvx, this.tvd, this.tvf });
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorsWriter
 * JD-Core Version:    0.6.0
 */