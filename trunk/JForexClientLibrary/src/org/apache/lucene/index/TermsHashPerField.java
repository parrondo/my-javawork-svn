/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.SorterTemplate;
/*     */ 
/*     */ final class TermsHashPerField extends InvertedDocConsumerPerField
/*     */ {
/*     */   final TermsHashConsumerPerField consumer;
/*     */   final TermsHashPerField nextPerField;
/*     */   final TermsHashPerThread perThread;
/*     */   final DocumentsWriter.DocState docState;
/*     */   final FieldInvertState fieldState;
/*     */   CharTermAttribute termAtt;
/*     */   final CharBlockPool charPool;
/*     */   final IntBlockPool intPool;
/*     */   final ByteBlockPool bytePool;
/*     */   final int streamCount;
/*     */   final int numPostingInt;
/*     */   final FieldInfo fieldInfo;
/*     */   boolean postingsCompacted;
/*     */   int numPostings;
/*  51 */   private int postingsHashSize = 4;
/*  52 */   private int postingsHashHalfSize = this.postingsHashSize / 2;
/*  53 */   private int postingsHashMask = this.postingsHashSize - 1;
/*     */   private int[] postingsHash;
/*     */   ParallelPostingsArray postingsArray;
/*     */   private boolean doCall;
/*     */   private boolean doNextCall;
/*     */   int[] intUptos;
/*     */   int intUptoStart;
/*     */ 
/*     */   public TermsHashPerField(DocInverterPerField docInverterPerField, TermsHashPerThread perThread, TermsHashPerThread nextPerThread, FieldInfo fieldInfo)
/*     */   {
/*  59 */     this.perThread = perThread;
/*  60 */     this.intPool = perThread.intPool;
/*  61 */     this.charPool = perThread.charPool;
/*  62 */     this.bytePool = perThread.bytePool;
/*  63 */     this.docState = perThread.docState;
/*     */ 
/*  65 */     this.postingsHash = new int[this.postingsHashSize];
/*  66 */     Arrays.fill(this.postingsHash, -1);
/*  67 */     bytesUsed(this.postingsHashSize * 4);
/*     */ 
/*  69 */     this.fieldState = docInverterPerField.fieldState;
/*  70 */     this.consumer = perThread.consumer.addField(this, fieldInfo);
/*  71 */     initPostingsArray();
/*     */ 
/*  73 */     this.streamCount = this.consumer.getStreamCount();
/*  74 */     this.numPostingInt = (2 * this.streamCount);
/*  75 */     this.fieldInfo = fieldInfo;
/*  76 */     if (nextPerThread != null)
/*  77 */       this.nextPerField = ((TermsHashPerField)nextPerThread.addField(docInverterPerField, fieldInfo));
/*     */     else
/*  79 */       this.nextPerField = null;
/*     */   }
/*     */ 
/*     */   private void initPostingsArray() {
/*  83 */     this.postingsArray = this.consumer.createPostingsArray(2);
/*  84 */     bytesUsed(this.postingsArray.size * this.postingsArray.bytesPerPosting());
/*     */   }
/*     */ 
/*     */   private void bytesUsed(long size)
/*     */   {
/*  89 */     if (this.perThread.termsHash.trackAllocations)
/*  90 */       this.perThread.termsHash.docWriter.bytesUsed(size);
/*     */   }
/*     */ 
/*     */   void shrinkHash(int targetSize)
/*     */   {
/*  95 */     assert ((this.postingsCompacted) || (this.numPostings == 0));
/*     */ 
/*  97 */     int newSize = 4;
/*  98 */     if (4 != this.postingsHash.length) {
/*  99 */       long previousSize = this.postingsHash.length;
/* 100 */       this.postingsHash = new int[4];
/* 101 */       bytesUsed((4L - previousSize) * 4L);
/* 102 */       Arrays.fill(this.postingsHash, -1);
/* 103 */       this.postingsHashSize = 4;
/* 104 */       this.postingsHashHalfSize = 2;
/* 105 */       this.postingsHashMask = 3;
/*     */     }
/*     */ 
/* 109 */     if (this.postingsArray != null) {
/* 110 */       bytesUsed(-this.postingsArray.bytesPerPosting() * this.postingsArray.size);
/* 111 */       this.postingsArray = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 116 */     if (!this.postingsCompacted)
/* 117 */       compactPostings();
/* 118 */     assert (this.numPostings <= this.postingsHash.length);
/* 119 */     if (this.numPostings > 0) {
/* 120 */       Arrays.fill(this.postingsHash, 0, this.numPostings, -1);
/* 121 */       this.numPostings = 0;
/*     */     }
/* 123 */     this.postingsCompacted = false;
/* 124 */     if (this.nextPerField != null)
/* 125 */       this.nextPerField.reset();
/*     */   }
/*     */ 
/*     */   public synchronized void abort()
/*     */   {
/* 130 */     reset();
/* 131 */     if (this.nextPerField != null)
/* 132 */       this.nextPerField.abort();
/*     */   }
/*     */ 
/*     */   private final void growParallelPostingsArray() {
/* 136 */     int oldSize = this.postingsArray.size;
/* 137 */     this.postingsArray = this.postingsArray.grow();
/* 138 */     bytesUsed(this.postingsArray.bytesPerPosting() * (this.postingsArray.size - oldSize));
/*     */   }
/*     */ 
/*     */   public void initReader(ByteSliceReader reader, int termID, int stream) {
/* 142 */     assert (stream < this.streamCount);
/* 143 */     int intStart = this.postingsArray.intStarts[termID];
/* 144 */     int[] ints = this.intPool.buffers[(intStart >> 13)];
/* 145 */     int upto = intStart & 0x1FFF;
/* 146 */     reader.init(this.bytePool, this.postingsArray.byteStarts[termID] + stream * ByteBlockPool.FIRST_LEVEL_SIZE, ints[(upto + stream)]);
/*     */   }
/*     */ 
/*     */   private void compactPostings()
/*     */   {
/* 152 */     int upto = 0;
/* 153 */     for (int i = 0; i < this.postingsHashSize; i++) {
/* 154 */       if (this.postingsHash[i] != -1) {
/* 155 */         if (upto < i) {
/* 156 */           this.postingsHash[upto] = this.postingsHash[i];
/* 157 */           this.postingsHash[i] = -1;
/*     */         }
/* 159 */         upto++;
/*     */       }
/*     */     }
/*     */ 
/* 163 */     assert (upto == this.numPostings) : ("upto=" + upto + " numPostings=" + this.numPostings);
/* 164 */     this.postingsCompacted = true;
/*     */   }
/*     */ 
/*     */   public int[] sortPostings()
/*     */   {
/* 169 */     compactPostings();
/* 170 */     int[] postingsHash = this.postingsHash;
/* 171 */     new SorterTemplate(postingsHash) { private int pivotTerm;
/*     */       private int pivotBufPos;
/*     */       private char[] pivotBuf;
/*     */ 
/* 174 */       protected void swap(int i, int j) { int o = this.val$postingsHash[i];
/* 175 */         this.val$postingsHash[i] = this.val$postingsHash[j];
/* 176 */         this.val$postingsHash[j] = o;
/*     */       }
/*     */ 
/*     */       protected int compare(int i, int j)
/*     */       {
/* 181 */         int term1 = this.val$postingsHash[i]; int term2 = this.val$postingsHash[j];
/* 182 */         if (term1 == term2)
/* 183 */           return 0;
/* 184 */         int textStart1 = TermsHashPerField.this.postingsArray.textStarts[term1];
/* 185 */         int textStart2 = TermsHashPerField.this.postingsArray.textStarts[term2];
/* 186 */         char[] text1 = TermsHashPerField.this.charPool.buffers[(textStart1 >> 14)];
/* 187 */         int pos1 = textStart1 & 0x3FFF;
/* 188 */         char[] text2 = TermsHashPerField.this.charPool.buffers[(textStart2 >> 14)];
/* 189 */         int pos2 = textStart2 & 0x3FFF;
/* 190 */         return comparePostings(text1, pos1, text2, pos2);
/*     */       }
/*     */ 
/*     */       protected void setPivot(int i)
/*     */       {
/* 195 */         this.pivotTerm = this.val$postingsHash[i];
/* 196 */         int textStart = TermsHashPerField.this.postingsArray.textStarts[this.pivotTerm];
/* 197 */         this.pivotBuf = TermsHashPerField.this.charPool.buffers[(textStart >> 14)];
/* 198 */         this.pivotBufPos = (textStart & 0x3FFF);
/*     */       }
/*     */ 
/*     */       protected int comparePivot(int j)
/*     */       {
/* 203 */         int term = this.val$postingsHash[j];
/* 204 */         if (this.pivotTerm == term)
/* 205 */           return 0;
/* 206 */         int textStart = TermsHashPerField.this.postingsArray.textStarts[term];
/* 207 */         char[] text = TermsHashPerField.this.charPool.buffers[(textStart >> 14)];
/* 208 */         int pos = textStart & 0x3FFF;
/* 209 */         return comparePostings(this.pivotBuf, this.pivotBufPos, text, pos);
/*     */       }
/*     */ 
/*     */       private int comparePostings(char[] text1, int pos1, char[] text2, int pos2)
/*     */       {
/* 218 */         assert ((text1 != text2) || (pos1 != pos2));
/*     */         while (true)
/*     */         {
/* 221 */           char c1 = text1[(pos1++)];
/* 222 */           char c2 = text2[(pos2++)];
/* 223 */           if (c1 != c2) {
/* 224 */             if (65535 == c2)
/* 225 */               return 1;
/* 226 */             if (65535 == c1) {
/* 227 */               return -1;
/*     */             }
/* 229 */             return c1 - c2;
/*     */           }
/*     */ 
/* 233 */           assert (c1 != 65535);
/*     */         }
/*     */       }
/*     */     }
/* 171 */     .quickSort(0, this.numPostings - 1);
/*     */ 
/* 237 */     return postingsHash;
/*     */   }
/*     */ 
/*     */   private boolean postingEquals(int termID, char[] tokenText, int tokenTextLen)
/*     */   {
/* 243 */     int textStart = this.postingsArray.textStarts[termID];
/*     */ 
/* 245 */     char[] text = this.perThread.charPool.buffers[(textStart >> 14)];
/* 246 */     assert (text != null);
/* 247 */     int pos = textStart & 0x3FFF;
/*     */ 
/* 249 */     int tokenPos = 0;
/* 250 */     for (; tokenPos < tokenTextLen; tokenPos++) {
/* 251 */       if (tokenText[tokenPos] != text[pos])
/* 252 */         return false;
/* 250 */       pos++;
/*     */     }
/*     */ 
/* 253 */     return 65535 == text[pos];
/*     */   }
/*     */ 
/*     */   void start(Fieldable f)
/*     */   {
/* 261 */     this.termAtt = ((CharTermAttribute)this.fieldState.attributeSource.addAttribute(CharTermAttribute.class));
/* 262 */     this.consumer.start(f);
/* 263 */     if (this.nextPerField != null)
/* 264 */       this.nextPerField.start(f);
/*     */   }
/*     */ 
/*     */   boolean start(Fieldable[] fields, int count)
/*     */     throws IOException
/*     */   {
/* 270 */     this.doCall = this.consumer.start(fields, count);
/* 271 */     if (this.postingsArray == null) {
/* 272 */       initPostingsArray();
/*     */     }
/*     */ 
/* 275 */     if (this.nextPerField != null)
/* 276 */       this.doNextCall = this.nextPerField.start(fields, count);
/* 277 */     return (this.doCall) || (this.doNextCall);
/*     */   }
/*     */ 
/*     */   public void add(int textStart)
/*     */     throws IOException
/*     */   {
/* 284 */     int code = textStart;
/*     */ 
/* 286 */     int hashPos = code & this.postingsHashMask;
/*     */ 
/* 288 */     assert (!this.postingsCompacted);
/*     */ 
/* 291 */     int termID = this.postingsHash[hashPos];
/*     */ 
/* 293 */     if ((termID != -1) && (this.postingsArray.textStarts[termID] != textStart))
/*     */     {
/* 296 */       int inc = (code >> 8) + code | 0x1;
/*     */       do {
/* 298 */         code += inc;
/* 299 */         hashPos = code & this.postingsHashMask;
/* 300 */         termID = this.postingsHash[hashPos];
/* 301 */       }while ((termID != -1) && (this.postingsArray.textStarts[termID] != textStart));
/*     */     }
/*     */ 
/* 304 */     if (termID == -1)
/*     */     {
/* 310 */       termID = this.numPostings++;
/* 311 */       if (termID >= this.postingsArray.size) {
/* 312 */         growParallelPostingsArray();
/*     */       }
/*     */ 
/* 315 */       assert (termID >= 0);
/*     */ 
/* 317 */       this.postingsArray.textStarts[termID] = textStart;
/*     */ 
/* 319 */       assert (this.postingsHash[hashPos] == -1);
/* 320 */       this.postingsHash[hashPos] = termID;
/*     */ 
/* 322 */       if (this.numPostings == this.postingsHashHalfSize) {
/* 323 */         rehashPostings(2 * this.postingsHashSize);
/*     */       }
/*     */ 
/* 326 */       if (this.numPostingInt + this.intPool.intUpto > 8192) {
/* 327 */         this.intPool.nextBuffer();
/*     */       }
/* 329 */       if (32768 - this.bytePool.byteUpto < this.numPostingInt * ByteBlockPool.FIRST_LEVEL_SIZE) {
/* 330 */         this.bytePool.nextBuffer();
/*     */       }
/* 332 */       this.intUptos = this.intPool.buffer;
/* 333 */       this.intUptoStart = this.intPool.intUpto;
/* 334 */       this.intPool.intUpto += this.streamCount;
/*     */ 
/* 336 */       this.postingsArray.intStarts[termID] = (this.intUptoStart + this.intPool.intOffset);
/*     */ 
/* 338 */       for (int i = 0; i < this.streamCount; i++) {
/* 339 */         int upto = this.bytePool.newSlice(ByteBlockPool.FIRST_LEVEL_SIZE);
/* 340 */         this.intUptos[(this.intUptoStart + i)] = (upto + this.bytePool.byteOffset);
/*     */       }
/* 342 */       this.postingsArray.byteStarts[termID] = this.intUptos[this.intUptoStart];
/*     */ 
/* 344 */       this.consumer.newTerm(termID);
/*     */     }
/*     */     else {
/* 347 */       int intStart = this.postingsArray.intStarts[termID];
/* 348 */       this.intUptos = this.intPool.buffers[(intStart >> 13)];
/* 349 */       this.intUptoStart = (intStart & 0x1FFF);
/* 350 */       this.consumer.addTerm(termID);
/*     */     }
/*     */   }
/*     */ 
/*     */   void add()
/*     */     throws IOException
/*     */   {
/* 358 */     assert (!this.postingsCompacted);
/*     */ 
/* 364 */     char[] tokenText = this.termAtt.buffer();
/* 365 */     int tokenTextLen = this.termAtt.length();
/*     */ 
/* 368 */     int downto = tokenTextLen;
/* 369 */     int code = 0;
/* 370 */     while (downto > 0) {
/* 371 */       downto--; char ch = tokenText[downto];
/*     */ 
/* 373 */       if ((ch >= 56320) && (ch <= 57343)) {
/* 374 */         if (0 == downto)
/*     */         {
/* 376 */           ch = tokenText[downto] = 65533;
/*     */         } else {
/* 378 */           char ch2 = tokenText[(downto - 1)];
/* 379 */           if ((ch2 >= 55296) && (ch2 <= 56319))
/*     */           {
/* 382 */             code = (code * 31 + ch) * 31 + ch2;
/* 383 */             downto--;
/* 384 */             continue;
/*     */           }
/*     */ 
/* 387 */           ch = tokenText[downto] = 65533;
/*     */         }
/*     */       }
/* 390 */       else if ((ch >= 55296) && ((ch <= 56319) || (ch == 65535)))
/*     */       {
/* 393 */         ch = tokenText[downto] = 65533;
/*     */       }
/*     */ 
/* 396 */       code = code * 31 + ch;
/*     */     }
/*     */ 
/* 399 */     int hashPos = code & this.postingsHashMask;
/*     */ 
/* 402 */     int termID = this.postingsHash[hashPos];
/*     */ 
/* 404 */     if ((termID != -1) && (!postingEquals(termID, tokenText, tokenTextLen)))
/*     */     {
/* 407 */       int inc = (code >> 8) + code | 0x1;
/*     */       do {
/* 409 */         code += inc;
/* 410 */         hashPos = code & this.postingsHashMask;
/* 411 */         termID = this.postingsHash[hashPos];
/* 412 */       }while ((termID != -1) && (!postingEquals(termID, tokenText, tokenTextLen)));
/*     */     }
/*     */ 
/* 415 */     if (termID == -1)
/*     */     {
/* 419 */       int textLen1 = 1 + tokenTextLen;
/* 420 */       if (textLen1 + this.charPool.charUpto > 16384) {
/* 421 */         if (textLen1 > 16384)
/*     */         {
/* 428 */           if (this.docState.maxTermPrefix == null) {
/* 429 */             this.docState.maxTermPrefix = new String(tokenText, 0, 30);
/*     */           }
/* 431 */           this.consumer.skippingLongTerm();
/* 432 */           return;
/*     */         }
/* 434 */         this.charPool.nextBuffer();
/*     */       }
/*     */ 
/* 438 */       termID = this.numPostings++;
/* 439 */       if (termID >= this.postingsArray.size) {
/* 440 */         growParallelPostingsArray();
/*     */       }
/*     */ 
/* 443 */       assert (termID != -1);
/*     */ 
/* 445 */       char[] text = this.charPool.buffer;
/* 446 */       int textUpto = this.charPool.charUpto;
/* 447 */       this.postingsArray.textStarts[termID] = (textUpto + this.charPool.charOffset);
/* 448 */       this.charPool.charUpto += textLen1;
/* 449 */       System.arraycopy(tokenText, 0, text, textUpto, tokenTextLen);
/* 450 */       text[(textUpto + tokenTextLen)] = 65535;
/*     */ 
/* 452 */       assert (this.postingsHash[hashPos] == -1);
/* 453 */       this.postingsHash[hashPos] = termID;
/*     */ 
/* 455 */       if (this.numPostings == this.postingsHashHalfSize) {
/* 456 */         rehashPostings(2 * this.postingsHashSize);
/* 457 */         bytesUsed(2 * this.numPostings * 4);
/*     */       }
/*     */ 
/* 461 */       if (this.numPostingInt + this.intPool.intUpto > 8192) {
/* 462 */         this.intPool.nextBuffer();
/*     */       }
/* 464 */       if (32768 - this.bytePool.byteUpto < this.numPostingInt * ByteBlockPool.FIRST_LEVEL_SIZE) {
/* 465 */         this.bytePool.nextBuffer();
/*     */       }
/* 467 */       this.intUptos = this.intPool.buffer;
/* 468 */       this.intUptoStart = this.intPool.intUpto;
/* 469 */       this.intPool.intUpto += this.streamCount;
/*     */ 
/* 471 */       this.postingsArray.intStarts[termID] = (this.intUptoStart + this.intPool.intOffset);
/*     */ 
/* 473 */       for (int i = 0; i < this.streamCount; i++) {
/* 474 */         int upto = this.bytePool.newSlice(ByteBlockPool.FIRST_LEVEL_SIZE);
/* 475 */         this.intUptos[(this.intUptoStart + i)] = (upto + this.bytePool.byteOffset);
/*     */       }
/* 477 */       this.postingsArray.byteStarts[termID] = this.intUptos[this.intUptoStart];
/*     */ 
/* 479 */       this.consumer.newTerm(termID);
/*     */     }
/*     */     else {
/* 482 */       int intStart = this.postingsArray.intStarts[termID];
/* 483 */       this.intUptos = this.intPool.buffers[(intStart >> 13)];
/* 484 */       this.intUptoStart = (intStart & 0x1FFF);
/* 485 */       this.consumer.addTerm(termID);
/*     */     }
/*     */ 
/* 488 */     if (this.doNextCall)
/* 489 */       this.nextPerField.add(this.postingsArray.textStarts[termID]);
/*     */   }
/*     */ 
/*     */   void writeByte(int stream, byte b)
/*     */   {
/* 496 */     int upto = this.intUptos[(this.intUptoStart + stream)];
/* 497 */     byte[] bytes = this.bytePool.buffers[(upto >> 15)];
/* 498 */     assert (bytes != null);
/* 499 */     int offset = upto & 0x7FFF;
/* 500 */     if (bytes[offset] != 0)
/*     */     {
/* 502 */       offset = this.bytePool.allocSlice(bytes, offset);
/* 503 */       bytes = this.bytePool.buffer;
/* 504 */       this.intUptos[(this.intUptoStart + stream)] = (offset + this.bytePool.byteOffset);
/*     */     }
/* 506 */     bytes[offset] = b;
/* 507 */     this.intUptos[(this.intUptoStart + stream)] += 1;
/*     */   }
/*     */ 
/*     */   public void writeBytes(int stream, byte[] b, int offset, int len)
/*     */   {
/* 512 */     int end = offset + len;
/* 513 */     for (int i = offset; i < end; i++)
/* 514 */       writeByte(stream, b[i]);
/*     */   }
/*     */ 
/*     */   void writeVInt(int stream, int i) {
/* 518 */     assert (stream < this.streamCount);
/* 519 */     while ((i & 0xFFFFFF80) != 0) {
/* 520 */       writeByte(stream, (byte)(i & 0x7F | 0x80));
/* 521 */       i >>>= 7;
/*     */     }
/* 523 */     writeByte(stream, (byte)i);
/*     */   }
/*     */ 
/*     */   void finish() throws IOException
/*     */   {
/*     */     try {
/* 529 */       this.consumer.finish();
/*     */     } finally {
/* 531 */       if (this.nextPerField != null)
/* 532 */         this.nextPerField.finish();
/*     */     }
/*     */   }
/*     */ 
/*     */   void rehashPostings(int newSize)
/*     */   {
/* 541 */     int newMask = newSize - 1;
/*     */ 
/* 543 */     int[] newHash = new int[newSize];
/* 544 */     Arrays.fill(newHash, -1);
/* 545 */     for (int i = 0; i < this.postingsHashSize; i++) {
/* 546 */       int termID = this.postingsHash[i];
/* 547 */       if (termID == -1)
/*     */         continue;
/*     */       int code;
/* 549 */       if (this.perThread.primary) {
/* 550 */         int textStart = this.postingsArray.textStarts[termID];
/* 551 */         int start = textStart & 0x3FFF;
/* 552 */         char[] text = this.charPool.buffers[(textStart >> 14)];
/* 553 */         int pos = start;
/* 554 */         while (text[pos] != 65535)
/* 555 */           pos++;
/* 556 */         int code = 0;
/* 557 */         while (pos > start) {
/* 558 */           pos--; code = code * 31 + text[pos];
/*     */         }
/*     */       } else {
/* 560 */         code = this.postingsArray.textStarts[termID];
/*     */       }
/* 562 */       int hashPos = code & newMask;
/* 563 */       assert (hashPos >= 0);
/* 564 */       if (newHash[hashPos] != -1) {
/* 565 */         int inc = (code >> 8) + code | 0x1;
/*     */         do {
/* 567 */           code += inc;
/* 568 */           hashPos = code & newMask;
/* 569 */         }while (newHash[hashPos] != -1);
/*     */       }
/* 571 */       newHash[hashPos] = termID;
/*     */     }
/*     */ 
/* 575 */     this.postingsHashMask = newMask;
/* 576 */     this.postingsHash = newHash;
/*     */ 
/* 578 */     this.postingsHashSize = newSize;
/* 579 */     this.postingsHashHalfSize = (newSize >> 1);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermsHashPerField
 * JD-Core Version:    0.6.0
 */