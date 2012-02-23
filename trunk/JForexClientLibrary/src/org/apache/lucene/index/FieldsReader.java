/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.zip.DataFormatException;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.document.AbstractField;
/*     */ import org.apache.lucene.document.CompressionTools;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Field;
/*     */ import org.apache.lucene.document.Field.Index;
/*     */ import org.apache.lucene.document.Field.Store;
/*     */ import org.apache.lucene.document.Field.TermVector;
/*     */ import org.apache.lucene.document.FieldSelector;
/*     */ import org.apache.lucene.document.FieldSelectorResult;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.document.NumericField;
/*     */ import org.apache.lucene.store.AlreadyClosedException;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.util.CloseableThreadLocal;
/*     */ import org.apache.lucene.util.IOUtils;
/*     */ 
/*     */ final class FieldsReader
/*     */   implements Cloneable, Closeable
/*     */ {
/*     */   private final FieldInfos fieldInfos;
/*     */   private final IndexInput cloneableFieldsStream;
/*     */   private final IndexInput fieldsStream;
/*     */   private final IndexInput cloneableIndexStream;
/*     */   private final IndexInput indexStream;
/*     */   private int numTotalDocs;
/*     */   private int size;
/*     */   private boolean closed;
/*     */   private final int format;
/*     */   private final int formatSize;
/*     */   private int docStoreOffset;
/*  68 */   private CloseableThreadLocal<IndexInput> fieldsStreamTL = new CloseableThreadLocal();
/*  69 */   private boolean isOriginal = false;
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  78 */     ensureOpen();
/*  79 */     return new FieldsReader(this.fieldInfos, this.numTotalDocs, this.size, this.format, this.formatSize, this.docStoreOffset, this.cloneableFieldsStream, this.cloneableIndexStream);
/*     */   }
/*     */ 
/*     */   static String detectCodeVersion(Directory dir, String segment)
/*     */     throws IOException
/*     */   {
/*  89 */     IndexInput idxStream = dir.openInput(IndexFileNames.segmentFileName(segment, "fdx"), 1024);
/*     */     try {
/*  91 */       int format = idxStream.readInt();
/*  92 */       if (format < 2) {
/*  93 */         str = "2.x";
/*     */         return str;
/*     */       }
/*  95 */       String str = "3.0";
/*     */       return str; } finally { idxStream.close(); } throw localObject;
/*     */   }
/*     */ 
/*     */   private FieldsReader(FieldInfos fieldInfos, int numTotalDocs, int size, int format, int formatSize, int docStoreOffset, IndexInput cloneableFieldsStream, IndexInput cloneableIndexStream)
/*     */   {
/* 105 */     this.fieldInfos = fieldInfos;
/* 106 */     this.numTotalDocs = numTotalDocs;
/* 107 */     this.size = size;
/* 108 */     this.format = format;
/* 109 */     this.formatSize = formatSize;
/* 110 */     this.docStoreOffset = docStoreOffset;
/* 111 */     this.cloneableFieldsStream = cloneableFieldsStream;
/* 112 */     this.cloneableIndexStream = cloneableIndexStream;
/* 113 */     this.fieldsStream = ((IndexInput)cloneableFieldsStream.clone());
/* 114 */     this.indexStream = ((IndexInput)cloneableIndexStream.clone());
/*     */   }
/*     */ 
/*     */   FieldsReader(Directory d, String segment, FieldInfos fn) throws IOException {
/* 118 */     this(d, segment, fn, 1024, -1, 0);
/*     */   }
/*     */ 
/*     */   FieldsReader(Directory d, String segment, FieldInfos fn, int readBufferSize) throws IOException {
/* 122 */     this(d, segment, fn, readBufferSize, -1, 0);
/*     */   }
/*     */ 
/*     */   FieldsReader(Directory d, String segment, FieldInfos fn, int readBufferSize, int docStoreOffset, int size) throws IOException {
/* 126 */     boolean success = false;
/* 127 */     this.isOriginal = true;
/*     */     try {
/* 129 */       this.fieldInfos = fn;
/*     */ 
/* 131 */       this.cloneableFieldsStream = d.openInput(IndexFileNames.segmentFileName(segment, "fdt"), readBufferSize);
/* 132 */       this.cloneableIndexStream = d.openInput(IndexFileNames.segmentFileName(segment, "fdx"), readBufferSize);
/*     */ 
/* 137 */       int firstInt = this.cloneableIndexStream.readInt();
/* 138 */       if (firstInt == 0)
/* 139 */         this.format = 0;
/*     */       else {
/* 141 */         this.format = firstInt;
/*     */       }
/* 143 */       if (this.format > 3) {
/* 144 */         throw new CorruptIndexException("Incompatible format version: " + this.format + " expected " + 3 + " or lower");
/*     */       }
/*     */ 
/* 147 */       if (this.format > 0)
/* 148 */         this.formatSize = 4;
/*     */       else {
/* 150 */         this.formatSize = 0;
/*     */       }
/* 152 */       if (this.format < 1) {
/* 153 */         this.cloneableFieldsStream.setModifiedUTF8StringsMode();
/*     */       }
/* 155 */       this.fieldsStream = ((IndexInput)this.cloneableFieldsStream.clone());
/*     */ 
/* 157 */       long indexSize = this.cloneableIndexStream.length() - this.formatSize;
/*     */ 
/* 159 */       if (docStoreOffset != -1)
/*     */       {
/* 161 */         this.docStoreOffset = docStoreOffset;
/* 162 */         this.size = size;
/*     */ 
/* 166 */         if ((!$assertionsDisabled) && ((int)(indexSize / 8L) < size + this.docStoreOffset)) throw new AssertionError("indexSize=" + indexSize + " size=" + size + " docStoreOffset=" + docStoreOffset); 
/*     */       }
/*     */       else {
/* 168 */         this.docStoreOffset = 0;
/* 169 */         this.size = (int)(indexSize >> 3);
/*     */       }
/*     */ 
/* 172 */       this.indexStream = ((IndexInput)this.cloneableIndexStream.clone());
/* 173 */       this.numTotalDocs = (int)(indexSize >> 3);
/* 174 */       success = true;
/*     */     }
/*     */     finally
/*     */     {
/* 181 */       if (!success)
/* 182 */         close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void ensureOpen()
/*     */     throws AlreadyClosedException
/*     */   {
/* 191 */     if (this.closed)
/* 192 */       throw new AlreadyClosedException("this FieldsReader is closed");
/*     */   }
/*     */ 
/*     */   public final void close()
/*     */     throws IOException
/*     */   {
/* 203 */     if (!this.closed) {
/* 204 */       if (this.isOriginal)
/* 205 */         IOUtils.close(new Closeable[] { this.fieldsStream, this.indexStream, this.fieldsStreamTL, this.cloneableFieldsStream, this.cloneableIndexStream });
/*     */       else {
/* 207 */         IOUtils.close(new Closeable[] { this.fieldsStream, this.indexStream, this.fieldsStreamTL });
/*     */       }
/* 209 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   final int size() {
/* 214 */     return this.size;
/*     */   }
/*     */ 
/*     */   private final void seekIndex(int docID) throws IOException {
/* 218 */     this.indexStream.seek(this.formatSize + (docID + this.docStoreOffset) * 8L);
/*     */   }
/*     */ 
/*     */   boolean canReadRawDocs()
/*     */   {
/* 226 */     return this.format >= 2;
/*     */   }
/*     */ 
/*     */   final Document doc(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
/* 230 */     seekIndex(n);
/* 231 */     long position = this.indexStream.readLong();
/* 232 */     this.fieldsStream.seek(position);
/*     */ 
/* 234 */     Document doc = new Document();
/* 235 */     int numFields = this.fieldsStream.readVInt();
/* 236 */     for (int i = 0; i < numFields; i++) {
/* 237 */       int fieldNumber = this.fieldsStream.readVInt();
/* 238 */       FieldInfo fi = this.fieldInfos.fieldInfo(fieldNumber);
/* 239 */       FieldSelectorResult acceptField = fieldSelector == null ? FieldSelectorResult.LOAD : fieldSelector.accept(fi.name);
/*     */ 
/* 241 */       int bits = this.fieldsStream.readByte() & 0xFF;
/* 242 */       assert (bits <= 63) : ("bits=" + Integer.toHexString(bits));
/*     */ 
/* 244 */       boolean compressed = (bits & 0x4) != 0;
/*     */ 
/* 246 */       assert ((!compressed) || (this.format < 2)) : "compressed fields are only allowed in indexes of version <= 2.9";
/* 247 */       boolean tokenize = (bits & 0x1) != 0;
/* 248 */       boolean binary = (bits & 0x2) != 0;
/* 249 */       int numeric = bits & 0x38;
/*     */ 
/* 251 */       switch (1.$SwitchMap$org$apache$lucene$document$FieldSelectorResult[acceptField.ordinal()]) {
/*     */       case 1:
/* 253 */         addField(doc, fi, binary, compressed, tokenize, numeric);
/* 254 */         break;
/*     */       case 2:
/* 256 */         addField(doc, fi, binary, compressed, tokenize, numeric);
/* 257 */         break;
/*     */       case 3:
/* 259 */         addFieldLazy(doc, fi, binary, compressed, tokenize, true, numeric);
/* 260 */         break;
/*     */       case 4:
/* 262 */         addFieldLazy(doc, fi, binary, compressed, tokenize, false, numeric);
/* 263 */         break;
/*     */       case 5:
/* 265 */         skipFieldBytes(binary, compressed, addFieldSize(doc, fi, binary, compressed, numeric));
/* 266 */         break;
/*     */       case 6:
/* 268 */         addFieldSize(doc, fi, binary, compressed, numeric);
/* 269 */         break;
/*     */       default:
/* 271 */         skipField(binary, compressed, numeric);
/*     */       }
/*     */     }
/*     */ 
/* 275 */     return doc;
/*     */   }
/*     */ 
/*     */   final IndexInput rawDocs(int[] lengths, int startDocID, int numDocs)
/*     */     throws IOException
/*     */   {
/* 283 */     seekIndex(startDocID);
/* 284 */     long startOffset = this.indexStream.readLong();
/* 285 */     long lastOffset = startOffset;
/* 286 */     int count = 0;
/* 287 */     while (count < numDocs)
/*     */     {
/* 289 */       int docID = this.docStoreOffset + startDocID + count + 1;
/* 290 */       assert (docID <= this.numTotalDocs);
/*     */       long offset;
/*     */       long offset;
/* 291 */       if (docID < this.numTotalDocs)
/* 292 */         offset = this.indexStream.readLong();
/*     */       else
/* 294 */         offset = this.fieldsStream.length();
/* 295 */       lengths[(count++)] = (int)(offset - lastOffset);
/* 296 */       lastOffset = offset;
/*     */     }
/*     */ 
/* 299 */     this.fieldsStream.seek(startOffset);
/*     */ 
/* 301 */     return this.fieldsStream;
/*     */   }
/*     */ 
/*     */   private void skipField(boolean binary, boolean compressed, int numeric)
/*     */     throws IOException
/*     */   {
/*     */     int numBytes;
/* 310 */     switch (numeric) {
/*     */     case 0:
/* 312 */       numBytes = this.fieldsStream.readVInt();
/* 313 */       break;
/*     */     case 8:
/*     */     case 24:
/* 316 */       numBytes = 4;
/* 317 */       break;
/*     */     case 16:
/*     */     case 32:
/* 320 */       numBytes = 8;
/* 321 */       break;
/*     */     default:
/* 323 */       throw new FieldReaderException("Invalid numeric type: " + Integer.toHexString(numeric));
/*     */     }
/*     */ 
/* 326 */     skipFieldBytes(binary, compressed, numBytes);
/*     */   }
/*     */ 
/*     */   private void skipFieldBytes(boolean binary, boolean compressed, int toRead) throws IOException {
/* 330 */     if ((this.format >= 1) || (binary) || (compressed)) {
/* 331 */       this.fieldsStream.seek(this.fieldsStream.getFilePointer() + toRead);
/*     */     }
/*     */     else
/* 334 */       this.fieldsStream.skipChars(toRead);
/*     */   }
/*     */ 
/*     */   private NumericField loadNumericField(FieldInfo fi, int numeric) throws IOException
/*     */   {
/* 339 */     assert (numeric != 0);
/* 340 */     switch (numeric) {
/*     */     case 8:
/* 342 */       return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setIntValue(this.fieldsStream.readInt());
/*     */     case 16:
/* 344 */       return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setLongValue(this.fieldsStream.readLong());
/*     */     case 24:
/* 346 */       return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setFloatValue(Float.intBitsToFloat(this.fieldsStream.readInt()));
/*     */     case 32:
/* 348 */       return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setDoubleValue(Double.longBitsToDouble(this.fieldsStream.readLong()));
/*     */     }
/* 350 */     throw new FieldReaderException("Invalid numeric type: " + Integer.toHexString(numeric));
/*     */   }
/*     */ 
/*     */   private void addFieldLazy(Document doc, FieldInfo fi, boolean binary, boolean compressed, boolean tokenize, boolean cacheResult, int numeric)
/*     */     throws IOException
/*     */   {
/*     */     AbstractField f;
/* 356 */     if (binary) {
/* 357 */       int toRead = this.fieldsStream.readVInt();
/* 358 */       long pointer = this.fieldsStream.getFilePointer();
/* 359 */       AbstractField f = new LazyField(fi.name, Field.Store.YES, toRead, pointer, binary, compressed, cacheResult);
/*     */ 
/* 361 */       this.fieldsStream.seek(pointer + toRead);
/*     */     }
/*     */     else
/*     */     {
/*     */       AbstractField f;
/* 362 */       if (numeric != 0) {
/* 363 */         f = loadNumericField(fi, numeric);
/*     */       } else {
/* 365 */         Field.Store store = Field.Store.YES;
/* 366 */         Field.Index index = Field.Index.toIndex(fi.isIndexed, tokenize);
/* 367 */         Field.TermVector termVector = Field.TermVector.toTermVector(fi.storeTermVector, fi.storeOffsetWithTermVector, fi.storePositionWithTermVector);
/*     */ 
/* 369 */         if (compressed) {
/* 370 */           int toRead = this.fieldsStream.readVInt();
/* 371 */           long pointer = this.fieldsStream.getFilePointer();
/* 372 */           AbstractField f = new LazyField(fi.name, store, toRead, pointer, binary, compressed, cacheResult);
/*     */ 
/* 374 */           this.fieldsStream.seek(pointer + toRead);
/*     */         } else {
/* 376 */           int length = this.fieldsStream.readVInt();
/* 377 */           long pointer = this.fieldsStream.getFilePointer();
/*     */ 
/* 379 */           if (this.format >= 1)
/* 380 */             this.fieldsStream.seek(pointer + length);
/*     */           else {
/* 382 */             this.fieldsStream.skipChars(length);
/*     */           }
/* 384 */           f = new LazyField(fi.name, store, index, termVector, length, pointer, binary, compressed, cacheResult);
/*     */         }
/*     */       }
/*     */     }
/* 388 */     f.setOmitNorms(fi.omitNorms);
/* 389 */     f.setIndexOptions(fi.indexOptions);
/* 390 */     doc.add(f);
/*     */   }
/*     */ 
/*     */   private void addField(Document doc, FieldInfo fi, boolean binary, boolean compressed, boolean tokenize, int numeric)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/*     */     AbstractField f;
/*     */     AbstractField f;
/* 397 */     if (binary) {
/* 398 */       int toRead = this.fieldsStream.readVInt();
/* 399 */       byte[] b = new byte[toRead];
/* 400 */       this.fieldsStream.readBytes(b, 0, b.length);
/*     */       AbstractField f;
/* 401 */       if (compressed)
/* 402 */         f = new Field(fi.name, uncompress(b));
/*     */       else
/* 404 */         f = new Field(fi.name, b);
/*     */     }
/*     */     else
/*     */     {
/*     */       AbstractField f;
/* 406 */       if (numeric != 0) {
/* 407 */         f = loadNumericField(fi, numeric);
/*     */       } else {
/* 409 */         Field.Store store = Field.Store.YES;
/* 410 */         Field.Index index = Field.Index.toIndex(fi.isIndexed, tokenize);
/* 411 */         Field.TermVector termVector = Field.TermVector.toTermVector(fi.storeTermVector, fi.storeOffsetWithTermVector, fi.storePositionWithTermVector);
/*     */         AbstractField f;
/* 412 */         if (compressed) {
/* 413 */           int toRead = this.fieldsStream.readVInt();
/* 414 */           byte[] b = new byte[toRead];
/* 415 */           this.fieldsStream.readBytes(b, 0, b.length);
/* 416 */           f = new Field(fi.name, false, new String(uncompress(b), "UTF-8"), store, index, termVector);
/*     */         }
/*     */         else
/*     */         {
/* 423 */           f = new Field(fi.name, false, this.fieldsStream.readString(), store, index, termVector);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 432 */     f.setIndexOptions(fi.indexOptions);
/* 433 */     f.setOmitNorms(fi.omitNorms);
/* 434 */     doc.add(f);
/*     */   }
/*     */ 
/*     */   private int addFieldSize(Document doc, FieldInfo fi, boolean binary, boolean compressed, int numeric)
/*     */     throws IOException
/*     */   {
/*     */     int size;
/*     */     int bytesize;
/* 442 */     switch (numeric) {
/*     */     case 0:
/* 444 */       size = this.fieldsStream.readVInt();
/* 445 */       bytesize = (binary) || (compressed) ? size : 2 * size;
/* 446 */       break;
/*     */     case 8:
/*     */     case 24:
/* 449 */       size = bytesize = 4;
/* 450 */       break;
/*     */     case 16:
/*     */     case 32:
/* 453 */       size = bytesize = 8;
/* 454 */       break;
/*     */     default:
/* 456 */       throw new FieldReaderException("Invalid numeric type: " + Integer.toHexString(numeric));
/*     */     }
/* 458 */     byte[] sizebytes = new byte[4];
/* 459 */     sizebytes[0] = (byte)(bytesize >>> 24);
/* 460 */     sizebytes[1] = (byte)(bytesize >>> 16);
/* 461 */     sizebytes[2] = (byte)(bytesize >>> 8);
/* 462 */     sizebytes[3] = (byte)bytesize;
/* 463 */     doc.add(new Field(fi.name, sizebytes));
/* 464 */     return size;
/*     */   }
/*     */ 
/*     */   private byte[] uncompress(byte[] b)
/*     */     throws CorruptIndexException
/*     */   {
/*     */     CorruptIndexException newException;
/*     */     try
/*     */     {
/* 639 */       return CompressionTools.decompress(b);
/*     */     }
/*     */     catch (DataFormatException e) {
/* 642 */       newException = new CorruptIndexException("field data are in wrong format: " + e.toString());
/* 643 */       newException.initCause(e);
/* 644 */     }throw newException;
/*     */   }
/*     */ 
/*     */   private class LazyField extends AbstractField
/*     */     implements Fieldable
/*     */   {
/*     */     private int toRead;
/*     */     private long pointer;
/*     */ 
/*     */     @Deprecated
/*     */     private boolean isCompressed;
/*     */     private boolean cacheResult;
/*     */ 
/*     */     public LazyField(String name, Field.Store store, int toRead, long pointer, boolean isBinary, boolean isCompressed, boolean cacheResult)
/*     */     {
/* 480 */       super(store, Field.Index.NO, Field.TermVector.NO);
/* 481 */       this.toRead = toRead;
/* 482 */       this.pointer = pointer;
/* 483 */       this.isBinary = isBinary;
/* 484 */       this.cacheResult = cacheResult;
/* 485 */       if (isBinary)
/* 486 */         this.binaryLength = toRead;
/* 487 */       this.lazy = true;
/* 488 */       this.isCompressed = isCompressed;
/*     */     }
/*     */ 
/*     */     public LazyField(String name, Field.Store store, Field.Index index, Field.TermVector termVector, int toRead, long pointer, boolean isBinary, boolean isCompressed, boolean cacheResult) {
/* 492 */       super(store, index, termVector);
/* 493 */       this.toRead = toRead;
/* 494 */       this.pointer = pointer;
/* 495 */       this.isBinary = isBinary;
/* 496 */       this.cacheResult = cacheResult;
/* 497 */       if (isBinary)
/* 498 */         this.binaryLength = toRead;
/* 499 */       this.lazy = true;
/* 500 */       this.isCompressed = isCompressed;
/*     */     }
/*     */ 
/*     */     private IndexInput getFieldStream() {
/* 504 */       IndexInput localFieldsStream = (IndexInput)FieldsReader.this.fieldsStreamTL.get();
/* 505 */       if (localFieldsStream == null) {
/* 506 */         localFieldsStream = (IndexInput)FieldsReader.this.cloneableFieldsStream.clone();
/* 507 */         FieldsReader.this.fieldsStreamTL.set(localFieldsStream);
/*     */       }
/* 509 */       return localFieldsStream;
/*     */     }
/*     */ 
/*     */     public Reader readerValue()
/*     */     {
/* 516 */       FieldsReader.this.ensureOpen();
/* 517 */       return null;
/*     */     }
/*     */ 
/*     */     public TokenStream tokenStreamValue()
/*     */     {
/* 524 */       FieldsReader.this.ensureOpen();
/* 525 */       return null;
/*     */     }
/*     */ 
/*     */     public String stringValue()
/*     */     {
/* 532 */       FieldsReader.this.ensureOpen();
/* 533 */       if (this.isBinary) {
/* 534 */         return null;
/*     */       }
/* 536 */       if (this.fieldsData == null) { IndexInput localFieldsStream = getFieldStream();
/*     */         String value;
/*     */         try { localFieldsStream.seek(this.pointer);
/*     */           String value;
/* 541 */           if (this.isCompressed) {
/* 542 */             byte[] b = new byte[this.toRead];
/* 543 */             localFieldsStream.readBytes(b, 0, b.length);
/* 544 */             value = new String(FieldsReader.this.uncompress(b), "UTF-8");
/*     */           }
/*     */           else
/*     */           {
/*     */             String value;
/* 546 */             if (FieldsReader.this.format >= 1) {
/* 547 */               byte[] bytes = new byte[this.toRead];
/* 548 */               localFieldsStream.readBytes(bytes, 0, this.toRead);
/* 549 */               value = new String(bytes, "UTF-8");
/*     */             }
/*     */             else {
/* 552 */               char[] chars = new char[this.toRead];
/* 553 */               localFieldsStream.readChars(chars, 0, this.toRead);
/* 554 */               value = new String(chars);
/*     */             }
/*     */           }
/*     */         } catch (IOException e) {
/* 558 */           throw new FieldReaderException(e);
/*     */         }
/* 560 */         if (this.cacheResult) {
/* 561 */           this.fieldsData = value;
/*     */         }
/* 563 */         return value;
/*     */       }
/* 565 */       return (String)this.fieldsData;
/*     */     }
/*     */ 
/*     */     public long getPointer()
/*     */     {
/* 572 */       FieldsReader.this.ensureOpen();
/* 573 */       return this.pointer;
/*     */     }
/*     */ 
/*     */     public void setPointer(long pointer) {
/* 577 */       FieldsReader.this.ensureOpen();
/* 578 */       this.pointer = pointer;
/*     */     }
/*     */ 
/*     */     public int getToRead() {
/* 582 */       FieldsReader.this.ensureOpen();
/* 583 */       return this.toRead;
/*     */     }
/*     */ 
/*     */     public void setToRead(int toRead) {
/* 587 */       FieldsReader.this.ensureOpen();
/* 588 */       this.toRead = toRead;
/*     */     }
/*     */ 
/*     */     public byte[] getBinaryValue(byte[] result)
/*     */     {
/* 593 */       FieldsReader.this.ensureOpen();
/*     */ 
/* 595 */       if (this.isBinary) {
/* 596 */         if (this.fieldsData == null)
/*     */         {
/*     */           byte[] b;
/*     */           byte[] b;
/* 600 */           if ((result == null) || (result.length < this.toRead))
/* 601 */             b = new byte[this.toRead];
/*     */           else {
/* 603 */             b = result;
/*     */           }
/* 605 */           IndexInput localFieldsStream = getFieldStream();
/*     */           byte[] value;
/*     */           try {
/* 610 */             localFieldsStream.seek(this.pointer);
/* 611 */             localFieldsStream.readBytes(b, 0, this.toRead);
/*     */             byte[] value;
/* 612 */             if (this.isCompressed == true)
/* 613 */               value = FieldsReader.this.uncompress(b);
/*     */             else
/* 615 */               value = b;
/*     */           }
/*     */           catch (IOException e) {
/* 618 */             throw new FieldReaderException(e);
/*     */           }
/*     */ 
/* 621 */           this.binaryOffset = 0;
/* 622 */           this.binaryLength = this.toRead;
/* 623 */           if (this.cacheResult == true) {
/* 624 */             this.fieldsData = value;
/*     */           }
/* 626 */           return value;
/*     */         }
/* 628 */         return (byte[])(byte[])this.fieldsData;
/*     */       }
/*     */ 
/* 631 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FieldsReader
 * JD-Core Version:    0.6.0
 */