/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ 
/*     */ class TermVectorsReader
/*     */   implements Cloneable, Closeable
/*     */ {
/*     */   static final int FORMAT_VERSION = 2;
/*     */   static final int FORMAT_VERSION2 = 3;
/*     */   static final int FORMAT_UTF8_LENGTH_IN_BYTES = 4;
/*     */   static final int FORMAT_CURRENT = 4;
/*     */   static final int FORMAT_SIZE = 4;
/*     */   static final byte STORE_POSITIONS_WITH_TERMVECTOR = 1;
/*     */   static final byte STORE_OFFSET_WITH_TERMVECTOR = 2;
/*     */   private FieldInfos fieldInfos;
/*     */   private IndexInput tvx;
/*     */   private IndexInput tvd;
/*     */   private IndexInput tvf;
/*     */   private int size;
/*     */   private int numTotalDocs;
/*     */   private int docStoreOffset;
/*     */   private final int format;
/*     */ 
/*     */   TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/*  67 */     this(d, segment, fieldInfos, 1024);
/*     */   }
/*     */ 
/*     */   TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos, int readBufferSize) throws CorruptIndexException, IOException
/*     */   {
/*  72 */     this(d, segment, fieldInfos, readBufferSize, -1, 0);
/*     */   }
/*     */ 
/*     */   TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos, int readBufferSize, int docStoreOffset, int size) throws CorruptIndexException, IOException
/*     */   {
/*  77 */     boolean success = false;
/*     */     try
/*     */     {
/*  80 */       String idxName = IndexFileNames.segmentFileName(segment, "tvx");
/*  81 */       this.tvx = d.openInput(idxName, readBufferSize);
/*  82 */       this.format = checkValidFormat(this.tvx);
/*  83 */       this.tvd = d.openInput(IndexFileNames.segmentFileName(segment, "tvd"), readBufferSize);
/*  84 */       int tvdFormat = checkValidFormat(this.tvd);
/*  85 */       this.tvf = d.openInput(IndexFileNames.segmentFileName(segment, "tvf"), readBufferSize);
/*  86 */       int tvfFormat = checkValidFormat(this.tvf);
/*     */ 
/*  88 */       assert (this.format == tvdFormat);
/*  89 */       assert (this.format == tvfFormat);
/*     */ 
/*  91 */       if (this.format >= 3) {
/*  92 */         this.numTotalDocs = (int)(this.tvx.length() >> 4);
/*     */       } else {
/*  94 */         assert ((this.tvx.length() - 4L) % 8L == 0L);
/*  95 */         this.numTotalDocs = (int)(this.tvx.length() >> 3);
/*     */       }
/*     */ 
/*  98 */       if (-1 == docStoreOffset) {
/*  99 */         this.docStoreOffset = 0;
/* 100 */         this.size = this.numTotalDocs;
/* 101 */         if ((!$assertionsDisabled) && (size != 0) && (this.numTotalDocs != size)) throw new AssertionError(); 
/*     */       }
/*     */       else {
/* 103 */         this.docStoreOffset = docStoreOffset;
/* 104 */         this.size = size;
/*     */ 
/* 107 */         assert (this.numTotalDocs >= size + docStoreOffset) : ("numTotalDocs=" + this.numTotalDocs + " size=" + size + " docStoreOffset=" + docStoreOffset);
/*     */       }
/*     */ 
/* 110 */       this.fieldInfos = fieldInfos;
/* 111 */       success = true;
/*     */     }
/*     */     finally
/*     */     {
/* 118 */       if (!success)
/* 119 */         close();
/*     */     }
/*     */   }
/*     */ 
/*     */   IndexInput getTvdStream()
/*     */   {
/* 126 */     return this.tvd;
/*     */   }
/*     */ 
/*     */   IndexInput getTvfStream()
/*     */   {
/* 131 */     return this.tvf;
/*     */   }
/*     */ 
/*     */   private final void seekTvx(int docNum) throws IOException {
/* 135 */     if (this.format < 3)
/* 136 */       this.tvx.seek((docNum + this.docStoreOffset) * 8L + 4L);
/*     */     else
/* 138 */       this.tvx.seek((docNum + this.docStoreOffset) * 16L + 4L);
/*     */   }
/*     */ 
/*     */   boolean canReadRawDocs() {
/* 142 */     return this.format >= 4;
/*     */   }
/*     */ 
/*     */   final void rawDocs(int[] tvdLengths, int[] tvfLengths, int startDocID, int numDocs)
/*     */     throws IOException
/*     */   {
/* 153 */     if (this.tvx == null) {
/* 154 */       Arrays.fill(tvdLengths, 0);
/* 155 */       Arrays.fill(tvfLengths, 0);
/* 156 */       return;
/*     */     }
/*     */ 
/* 161 */     if (this.format < 3) {
/* 162 */       throw new IllegalStateException("cannot read raw docs with older term vector formats");
/*     */     }
/* 164 */     seekTvx(startDocID);
/*     */ 
/* 166 */     long tvdPosition = this.tvx.readLong();
/* 167 */     this.tvd.seek(tvdPosition);
/*     */ 
/* 169 */     long tvfPosition = this.tvx.readLong();
/* 170 */     this.tvf.seek(tvfPosition);
/*     */ 
/* 172 */     long lastTvdPosition = tvdPosition;
/* 173 */     long lastTvfPosition = tvfPosition;
/*     */ 
/* 175 */     int count = 0;
/* 176 */     while (count < numDocs) {
/* 177 */       int docID = this.docStoreOffset + startDocID + count + 1;
/* 178 */       assert (docID <= this.numTotalDocs);
/* 179 */       if (docID < this.numTotalDocs) {
/* 180 */         tvdPosition = this.tvx.readLong();
/* 181 */         tvfPosition = this.tvx.readLong();
/*     */       } else {
/* 183 */         tvdPosition = this.tvd.length();
/* 184 */         tvfPosition = this.tvf.length();
/* 185 */         assert (count == numDocs - 1);
/*     */       }
/* 187 */       tvdLengths[count] = (int)(tvdPosition - lastTvdPosition);
/* 188 */       tvfLengths[count] = (int)(tvfPosition - lastTvfPosition);
/* 189 */       count++;
/* 190 */       lastTvdPosition = tvdPosition;
/* 191 */       lastTvfPosition = tvfPosition;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int checkValidFormat(IndexInput in) throws CorruptIndexException, IOException
/*     */   {
/* 197 */     int format = in.readInt();
/* 198 */     if (format > 4) {
/* 199 */       throw new CorruptIndexException("Incompatible format version: " + format + " expected " + 4 + " or less");
/*     */     }
/*     */ 
/* 202 */     return format;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 206 */     IOUtils.close(new Closeable[] { this.tvx, this.tvd, this.tvf });
/*     */   }
/*     */ 
/*     */   int size()
/*     */   {
/* 214 */     return this.size;
/*     */   }
/*     */ 
/*     */   public void get(int docNum, String field, TermVectorMapper mapper) throws IOException {
/* 218 */     if (this.tvx != null) {
/* 219 */       int fieldNumber = this.fieldInfos.fieldNumber(field);
/*     */ 
/* 224 */       seekTvx(docNum);
/*     */ 
/* 226 */       long tvdPosition = this.tvx.readLong();
/*     */ 
/* 228 */       this.tvd.seek(tvdPosition);
/* 229 */       int fieldCount = this.tvd.readVInt();
/*     */ 
/* 234 */       int number = 0;
/* 235 */       int found = -1;
/* 236 */       for (int i = 0; i < fieldCount; i++) {
/* 237 */         if (this.format >= 2)
/* 238 */           number = this.tvd.readVInt();
/*     */         else {
/* 240 */           number += this.tvd.readVInt();
/*     */         }
/* 242 */         if (number == fieldNumber) {
/* 243 */           found = i;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 248 */       if (found != -1)
/*     */       {
/*     */         long position;
/*     */         long position;
/* 251 */         if (this.format >= 3)
/* 252 */           position = this.tvx.readLong();
/*     */         else
/* 254 */           position = this.tvd.readVLong();
/* 255 */         for (int i = 1; i <= found; i++) {
/* 256 */           position += this.tvd.readVLong();
/*     */         }
/* 258 */         mapper.setDocumentNumber(docNum);
/* 259 */         readTermVector(field, position, mapper);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   TermFreqVector get(int docNum, String field)
/*     */     throws IOException
/*     */   {
/* 279 */     ParallelArrayTermVectorMapper mapper = new ParallelArrayTermVectorMapper();
/* 280 */     get(docNum, field, mapper);
/*     */ 
/* 282 */     return mapper.materializeVector();
/*     */   }
/*     */ 
/*     */   private final String[] readFields(int fieldCount)
/*     */     throws IOException
/*     */   {
/* 288 */     int number = 0;
/* 289 */     String[] fields = new String[fieldCount];
/*     */ 
/* 291 */     for (int i = 0; i < fieldCount; i++) {
/* 292 */       if (this.format >= 2)
/* 293 */         number = this.tvd.readVInt();
/*     */       else {
/* 295 */         number += this.tvd.readVInt();
/*     */       }
/* 297 */       fields[i] = this.fieldInfos.fieldName(number);
/*     */     }
/*     */ 
/* 300 */     return fields;
/*     */   }
/*     */ 
/*     */   private final long[] readTvfPointers(int fieldCount)
/*     */     throws IOException
/*     */   {
/*     */     long position;
/*     */     long position;
/* 308 */     if (this.format >= 3)
/* 309 */       position = this.tvx.readLong();
/*     */     else {
/* 311 */       position = this.tvd.readVLong();
/*     */     }
/* 313 */     long[] tvfPointers = new long[fieldCount];
/* 314 */     tvfPointers[0] = position;
/*     */ 
/* 316 */     for (int i = 1; i < fieldCount; i++) {
/* 317 */       position += this.tvd.readVLong();
/* 318 */       tvfPointers[i] = position;
/*     */     }
/*     */ 
/* 321 */     return tvfPointers;
/*     */   }
/*     */ 
/*     */   TermFreqVector[] get(int docNum)
/*     */     throws IOException
/*     */   {
/* 332 */     TermFreqVector[] result = null;
/* 333 */     if (this.tvx != null)
/*     */     {
/* 335 */       seekTvx(docNum);
/* 336 */       long tvdPosition = this.tvx.readLong();
/*     */ 
/* 338 */       this.tvd.seek(tvdPosition);
/* 339 */       int fieldCount = this.tvd.readVInt();
/*     */ 
/* 342 */       if (fieldCount != 0) {
/* 343 */         String[] fields = readFields(fieldCount);
/* 344 */         long[] tvfPointers = readTvfPointers(fieldCount);
/* 345 */         result = readTermVectors(docNum, fields, tvfPointers);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 350 */     return result;
/*     */   }
/*     */ 
/*     */   public void get(int docNumber, TermVectorMapper mapper) throws IOException
/*     */   {
/* 355 */     if (this.tvx != null)
/*     */     {
/* 358 */       seekTvx(docNumber);
/* 359 */       long tvdPosition = this.tvx.readLong();
/*     */ 
/* 361 */       this.tvd.seek(tvdPosition);
/* 362 */       int fieldCount = this.tvd.readVInt();
/*     */ 
/* 365 */       if (fieldCount != 0) {
/* 366 */         String[] fields = readFields(fieldCount);
/* 367 */         long[] tvfPointers = readTvfPointers(fieldCount);
/* 368 */         mapper.setDocumentNumber(docNumber);
/* 369 */         readTermVectors(fields, tvfPointers, mapper);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private SegmentTermVector[] readTermVectors(int docNum, String[] fields, long[] tvfPointers)
/*     */     throws IOException
/*     */   {
/* 379 */     SegmentTermVector[] res = new SegmentTermVector[fields.length];
/* 380 */     for (int i = 0; i < fields.length; i++) {
/* 381 */       ParallelArrayTermVectorMapper mapper = new ParallelArrayTermVectorMapper();
/* 382 */       mapper.setDocumentNumber(docNum);
/* 383 */       readTermVector(fields[i], tvfPointers[i], mapper);
/* 384 */       res[i] = ((SegmentTermVector)mapper.materializeVector());
/*     */     }
/* 386 */     return res;
/*     */   }
/*     */ 
/*     */   private void readTermVectors(String[] fields, long[] tvfPointers, TermVectorMapper mapper) throws IOException
/*     */   {
/* 391 */     for (int i = 0; i < fields.length; i++)
/* 392 */       readTermVector(fields[i], tvfPointers[i], mapper);
/*     */   }
/*     */ 
/*     */   private void readTermVector(String field, long tvfPointer, TermVectorMapper mapper)
/*     */     throws IOException
/*     */   {
/* 409 */     this.tvf.seek(tvfPointer);
/*     */ 
/* 411 */     int numTerms = this.tvf.readVInt();
/*     */ 
/* 414 */     if (numTerms == 0)
/* 415 */       return;
/*     */     boolean storeOffsets;
/*     */     boolean storePositions;
/*     */     boolean storeOffsets;
/* 420 */     if (this.format >= 2) {
/* 421 */       byte bits = this.tvf.readByte();
/* 422 */       boolean storePositions = (bits & 0x1) != 0;
/* 423 */       storeOffsets = (bits & 0x2) != 0;
/*     */     }
/*     */     else {
/* 426 */       this.tvf.readVInt();
/* 427 */       storePositions = false;
/* 428 */       storeOffsets = false;
/*     */     }
/* 430 */     mapper.setExpectations(field, numTerms, storeOffsets, storePositions);
/* 431 */     int start = 0;
/* 432 */     int deltaLength = 0;
/* 433 */     int totalLength = 0;
/*     */ 
/* 436 */     boolean preUTF8 = this.format < 4;
/*     */     byte[] byteBuffer;
/*     */     char[] charBuffer;
/*     */     byte[] byteBuffer;
/* 439 */     if (preUTF8) {
/* 440 */       char[] charBuffer = new char[10];
/* 441 */       byteBuffer = null;
/*     */     } else {
/* 443 */       charBuffer = null;
/* 444 */       byteBuffer = new byte[20];
/*     */     }
/*     */ 
/* 447 */     for (int i = 0; i < numTerms; i++) {
/* 448 */       start = this.tvf.readVInt();
/* 449 */       deltaLength = this.tvf.readVInt();
/* 450 */       totalLength = start + deltaLength;
/*     */       String term;
/*     */       String term;
/* 454 */       if (preUTF8)
/*     */       {
/* 456 */         if (charBuffer.length < totalLength) {
/* 457 */           charBuffer = ArrayUtil.grow(charBuffer, totalLength);
/*     */         }
/* 459 */         this.tvf.readChars(charBuffer, start, deltaLength);
/* 460 */         term = new String(charBuffer, 0, totalLength);
/*     */       }
/*     */       else {
/* 463 */         if (byteBuffer.length < totalLength) {
/* 464 */           byteBuffer = ArrayUtil.grow(byteBuffer, totalLength);
/*     */         }
/* 466 */         this.tvf.readBytes(byteBuffer, start, deltaLength);
/* 467 */         term = new String(byteBuffer, 0, totalLength, "UTF-8");
/*     */       }
/* 469 */       int freq = this.tvf.readVInt();
/* 470 */       int[] positions = null;
/* 471 */       if (storePositions)
/*     */       {
/* 473 */         if (!mapper.isIgnoringPositions()) {
/* 474 */           positions = new int[freq];
/* 475 */           int prevPosition = 0;
/* 476 */           for (int j = 0; j < freq; j++)
/*     */           {
/* 478 */             positions[j] = (prevPosition + this.tvf.readVInt());
/* 479 */             prevPosition = positions[j];
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 484 */           for (int j = 0; j < freq; j++)
/*     */           {
/* 486 */             this.tvf.readVInt();
/*     */           }
/*     */         }
/*     */       }
/* 490 */       TermVectorOffsetInfo[] offsets = null;
/* 491 */       if (storeOffsets)
/*     */       {
/* 493 */         if (!mapper.isIgnoringOffsets()) {
/* 494 */           offsets = new TermVectorOffsetInfo[freq];
/* 495 */           int prevOffset = 0;
/* 496 */           for (int j = 0; j < freq; j++) {
/* 497 */             int startOffset = prevOffset + this.tvf.readVInt();
/* 498 */             int endOffset = startOffset + this.tvf.readVInt();
/* 499 */             offsets[j] = new TermVectorOffsetInfo(startOffset, endOffset);
/* 500 */             prevOffset = endOffset;
/*     */           }
/*     */         } else {
/* 503 */           for (int j = 0; j < freq; j++) {
/* 504 */             this.tvf.readVInt();
/* 505 */             this.tvf.readVInt();
/*     */           }
/*     */         }
/*     */       }
/* 509 */       mapper.map(term, freq, offsets, positions);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object clone()
/*     */     throws CloneNotSupportedException
/*     */   {
/* 516 */     TermVectorsReader clone = (TermVectorsReader)super.clone();
/*     */ 
/* 520 */     if ((this.tvx != null) && (this.tvd != null) && (this.tvf != null)) {
/* 521 */       clone.tvx = ((IndexInput)this.tvx.clone());
/* 522 */       clone.tvd = ((IndexInput)this.tvd.clone());
/* 523 */       clone.tvf = ((IndexInput)this.tvf.clone());
/*     */     }
/*     */ 
/* 526 */     return clone;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorsReader
 * JD-Core Version:    0.6.0
 */