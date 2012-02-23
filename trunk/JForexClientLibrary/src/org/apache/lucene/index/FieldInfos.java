/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ final class FieldInfos
/*     */ {
/*     */   public static final int FORMAT_PRE = -1;
/*     */   public static final int FORMAT_START = -2;
/*     */   public static final int FORMAT_OMIT_POSITIONS = -3;
/*     */   static final int CURRENT_FORMAT = -3;
/*     */   static final byte IS_INDEXED = 1;
/*     */   static final byte STORE_TERMVECTOR = 2;
/*     */   static final byte STORE_POSITIONS_WITH_TERMVECTOR = 4;
/*     */   static final byte STORE_OFFSET_WITH_TERMVECTOR = 8;
/*     */   static final byte OMIT_NORMS = 16;
/*     */   static final byte STORE_PAYLOADS = 32;
/*     */   static final byte OMIT_TERM_FREQ_AND_POSITIONS = 64;
/*     */   static final byte OMIT_POSITIONS = -128;
/*  60 */   private final ArrayList<FieldInfo> byNumber = new ArrayList();
/*  61 */   private final HashMap<String, FieldInfo> byName = new HashMap();
/*     */   private int format;
/*     */ 
/*     */   FieldInfos()
/*     */   {
/*     */   }
/*     */ 
/*     */   FieldInfos(Directory d, String name)
/*     */     throws IOException
/*     */   {
/*  74 */     IndexInput input = d.openInput(name);
/*     */     try {
/*     */       try {
/*  77 */         read(input, name);
/*     */       } catch (IOException ioe) {
/*  79 */         if (this.format == -1)
/*     */         {
/*  83 */           input.seek(0L);
/*  84 */           input.setModifiedUTF8StringsMode();
/*  85 */           this.byNumber.clear();
/*  86 */           this.byName.clear();
/*     */           try {
/*  88 */             read(input, name);
/*     */           }
/*     */           catch (Throwable t) {
/*  91 */             throw ioe;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*  96 */           throw ioe;
/*     */         }
/*     */       }
/*     */     } finally {
/* 100 */       input.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Object clone()
/*     */   {
/* 109 */     FieldInfos fis = new FieldInfos();
/* 110 */     int numField = this.byNumber.size();
/* 111 */     for (int i = 0; i < numField; i++) {
/* 112 */       FieldInfo fi = (FieldInfo)((FieldInfo)this.byNumber.get(i)).clone();
/* 113 */       fis.byNumber.add(fi);
/* 114 */       fis.byName.put(fi.name, fi);
/*     */     }
/* 116 */     return fis;
/*     */   }
/*     */ 
/*     */   public synchronized void add(Document doc)
/*     */   {
/* 121 */     List fields = doc.getFields();
/* 122 */     for (Fieldable field : fields)
/* 123 */       add(field.name(), field.isIndexed(), field.isTermVectorStored(), field.isStorePositionWithTermVector(), field.isStoreOffsetWithTermVector(), field.getOmitNorms(), false, field.getIndexOptions());
/*     */   }
/*     */ 
/*     */   boolean hasProx()
/*     */   {
/* 130 */     int numFields = this.byNumber.size();
/* 131 */     for (int i = 0; i < numFields; i++) {
/* 132 */       FieldInfo fi = fieldInfo(i);
/* 133 */       if ((fi.isIndexed) && (fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)) {
/* 134 */         return true;
/*     */       }
/*     */     }
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized void addIndexed(Collection<String> names, boolean storeTermVectors, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector)
/*     */   {
/* 150 */     for (String name : names)
/* 151 */       add(name, true, storeTermVectors, storePositionWithTermVector, storeOffsetWithTermVector);
/*     */   }
/*     */ 
/*     */   public synchronized void add(Collection<String> names, boolean isIndexed)
/*     */   {
/* 164 */     for (String name : names)
/* 165 */       add(name, isIndexed);
/*     */   }
/*     */ 
/*     */   public synchronized void add(String name, boolean isIndexed)
/*     */   {
/* 177 */     add(name, isIndexed, false, false, false, false);
/*     */   }
/*     */ 
/*     */   public synchronized void add(String name, boolean isIndexed, boolean storeTermVector)
/*     */   {
/* 188 */     add(name, isIndexed, storeTermVector, false, false, false);
/*     */   }
/*     */ 
/*     */   public synchronized void add(String name, boolean isIndexed, boolean storeTermVector, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector)
/*     */   {
/* 205 */     add(name, isIndexed, storeTermVector, storePositionWithTermVector, storeOffsetWithTermVector, false);
/*     */   }
/*     */ 
/*     */   public synchronized void add(String name, boolean isIndexed, boolean storeTermVector, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector, boolean omitNorms)
/*     */   {
/* 222 */     add(name, isIndexed, storeTermVector, storePositionWithTermVector, storeOffsetWithTermVector, omitNorms, false, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
/*     */   }
/*     */ 
/*     */   public synchronized FieldInfo add(String name, boolean isIndexed, boolean storeTermVector, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector, boolean omitNorms, boolean storePayloads, FieldInfo.IndexOptions indexOptions)
/*     */   {
/* 243 */     FieldInfo fi = fieldInfo(name);
/* 244 */     if (fi == null) {
/* 245 */       return addInternal(name, isIndexed, storeTermVector, storePositionWithTermVector, storeOffsetWithTermVector, omitNorms, storePayloads, indexOptions);
/*     */     }
/* 247 */     fi.update(isIndexed, storeTermVector, storePositionWithTermVector, storeOffsetWithTermVector, omitNorms, storePayloads, indexOptions);
/*     */ 
/* 249 */     assert ((fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) || (!fi.storePayloads));
/* 250 */     return fi;
/*     */   }
/*     */ 
/*     */   public synchronized FieldInfo add(FieldInfo fi) {
/* 254 */     return add(fi.name, fi.isIndexed, fi.storeTermVector, fi.storePositionWithTermVector, fi.storeOffsetWithTermVector, fi.omitNorms, fi.storePayloads, fi.indexOptions);
/*     */   }
/*     */ 
/*     */   private FieldInfo addInternal(String name, boolean isIndexed, boolean storeTermVector, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector, boolean omitNorms, boolean storePayloads, FieldInfo.IndexOptions indexOptions)
/*     */   {
/* 263 */     name = StringHelper.intern(name);
/* 264 */     FieldInfo fi = new FieldInfo(name, isIndexed, this.byNumber.size(), storeTermVector, storePositionWithTermVector, storeOffsetWithTermVector, omitNorms, storePayloads, indexOptions);
/*     */ 
/* 266 */     this.byNumber.add(fi);
/* 267 */     this.byName.put(name, fi);
/* 268 */     return fi;
/*     */   }
/*     */ 
/*     */   public int fieldNumber(String fieldName) {
/* 272 */     FieldInfo fi = fieldInfo(fieldName);
/* 273 */     return fi != null ? fi.number : -1;
/*     */   }
/*     */ 
/*     */   public FieldInfo fieldInfo(String fieldName) {
/* 277 */     return (FieldInfo)this.byName.get(fieldName);
/*     */   }
/*     */ 
/*     */   public String fieldName(int fieldNumber)
/*     */   {
/* 288 */     FieldInfo fi = fieldInfo(fieldNumber);
/* 289 */     return fi != null ? fi.name : "";
/*     */   }
/*     */ 
/*     */   public FieldInfo fieldInfo(int fieldNumber)
/*     */   {
/* 299 */     return fieldNumber >= 0 ? (FieldInfo)this.byNumber.get(fieldNumber) : null;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 303 */     return this.byNumber.size();
/*     */   }
/*     */ 
/*     */   public boolean hasVectors() {
/* 307 */     boolean hasVectors = false;
/* 308 */     for (int i = 0; i < size(); i++) {
/* 309 */       if (fieldInfo(i).storeTermVector) {
/* 310 */         hasVectors = true;
/* 311 */         break;
/*     */       }
/*     */     }
/* 314 */     return hasVectors;
/*     */   }
/*     */ 
/*     */   public void write(Directory d, String name) throws IOException {
/* 318 */     IndexOutput output = d.createOutput(name);
/*     */     try {
/* 320 */       write(output);
/*     */     } finally {
/* 322 */       output.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(IndexOutput output) throws IOException {
/* 327 */     output.writeVInt(-3);
/* 328 */     output.writeVInt(size());
/* 329 */     for (int i = 0; i < size(); i++) {
/* 330 */       FieldInfo fi = fieldInfo(i);
/* 331 */       assert ((fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) || (!fi.storePayloads));
/* 332 */       byte bits = 0;
/* 333 */       if (fi.isIndexed) bits = (byte)(bits | 0x1);
/* 334 */       if (fi.storeTermVector) bits = (byte)(bits | 0x2);
/* 335 */       if (fi.storePositionWithTermVector) bits = (byte)(bits | 0x4);
/* 336 */       if (fi.storeOffsetWithTermVector) bits = (byte)(bits | 0x8);
/* 337 */       if (fi.omitNorms) bits = (byte)(bits | 0x10);
/* 338 */       if (fi.storePayloads) bits = (byte)(bits | 0x20);
/* 339 */       if (fi.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY)
/* 340 */         bits = (byte)(bits | 0x40);
/* 341 */       else if (fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS) {
/* 342 */         bits = (byte)(bits | 0xFFFFFF80);
/*     */       }
/* 344 */       output.writeString(fi.name);
/* 345 */       output.writeByte(bits);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void read(IndexInput input, String fileName) throws IOException {
/* 350 */     int firstInt = input.readVInt();
/*     */ 
/* 352 */     if (firstInt < 0)
/*     */     {
/* 354 */       this.format = firstInt;
/*     */     }
/* 356 */     else this.format = -1;
/*     */ 
/* 359 */     if ((this.format != -1) && (this.format != -2) && (this.format != -3))
/* 360 */       throw new CorruptIndexException("unrecognized format " + this.format + " in file \"" + fileName + "\"");
/*     */     int size;
/*     */     int size;
/* 364 */     if (this.format == -1)
/* 365 */       size = firstInt;
/*     */     else {
/* 367 */       size = input.readVInt();
/*     */     }
/*     */ 
/* 370 */     for (int i = 0; i < size; i++) {
/* 371 */       String name = StringHelper.intern(input.readString());
/* 372 */       byte bits = input.readByte();
/* 373 */       boolean isIndexed = (bits & 0x1) != 0;
/* 374 */       boolean storeTermVector = (bits & 0x2) != 0;
/* 375 */       boolean storePositionsWithTermVector = (bits & 0x4) != 0;
/* 376 */       boolean storeOffsetWithTermVector = (bits & 0x8) != 0;
/* 377 */       boolean omitNorms = (bits & 0x10) != 0;
/* 378 */       boolean storePayloads = (bits & 0x20) != 0;
/*     */       FieldInfo.IndexOptions indexOptions;
/*     */       FieldInfo.IndexOptions indexOptions;
/* 380 */       if ((bits & 0x40) != 0) {
/* 381 */         indexOptions = FieldInfo.IndexOptions.DOCS_ONLY;
/* 382 */       } else if ((bits & 0xFFFFFF80) != 0)
/*     */       {
/*     */         FieldInfo.IndexOptions indexOptions;
/* 383 */         if (this.format <= -3)
/* 384 */           indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS;
/*     */         else
/* 386 */           throw new CorruptIndexException("Corrupt fieldinfos, OMIT_POSITIONS set but format=" + this.format);
/*     */       }
/*     */       else {
/* 389 */         indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
/*     */       }
/*     */ 
/* 395 */       if (indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/* 396 */         storePayloads = false;
/*     */       }
/*     */ 
/* 399 */       addInternal(name, isIndexed, storeTermVector, storePositionsWithTermVector, storeOffsetWithTermVector, omitNorms, storePayloads, indexOptions);
/*     */     }
/*     */ 
/* 402 */     if (input.getFilePointer() != input.length())
/* 403 */       throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FieldInfos
 * JD-Core Version:    0.6.0
 */