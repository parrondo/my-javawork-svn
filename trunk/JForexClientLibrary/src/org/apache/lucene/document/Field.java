/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import java.io.Serializable;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.index.FieldInfo.IndexOptions;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ public final class Field extends AbstractField
/*     */   implements Fieldable, Serializable
/*     */ {
/*     */   public String stringValue()
/*     */   {
/* 270 */     return (this.fieldsData instanceof String) ? (String)this.fieldsData : null;
/*     */   }
/*     */ 
/*     */   public Reader readerValue()
/*     */   {
/* 275 */     return (this.fieldsData instanceof Reader) ? (Reader)this.fieldsData : null;
/*     */   }
/*     */ 
/*     */   public TokenStream tokenStreamValue() {
/* 279 */     return this.tokenStream;
/*     */   }
/*     */ 
/*     */   public void setValue(String value)
/*     */   {
/* 294 */     if (this.isBinary) {
/* 295 */       throw new IllegalArgumentException("cannot set a String value on a binary field");
/*     */     }
/* 297 */     this.fieldsData = value;
/*     */   }
/*     */ 
/*     */   public void setValue(Reader value)
/*     */   {
/* 302 */     if (this.isBinary) {
/* 303 */       throw new IllegalArgumentException("cannot set a Reader value on a binary field");
/*     */     }
/* 305 */     if (this.isStored) {
/* 306 */       throw new IllegalArgumentException("cannot set a Reader value on a stored field");
/*     */     }
/* 308 */     this.fieldsData = value;
/*     */   }
/*     */ 
/*     */   public void setValue(byte[] value)
/*     */   {
/* 313 */     if (!this.isBinary) {
/* 314 */       throw new IllegalArgumentException("cannot set a byte[] value on a non-binary field");
/*     */     }
/* 316 */     this.fieldsData = value;
/* 317 */     this.binaryLength = value.length;
/* 318 */     this.binaryOffset = 0;
/*     */   }
/*     */ 
/*     */   public void setValue(byte[] value, int offset, int length)
/*     */   {
/* 323 */     if (!this.isBinary) {
/* 324 */       throw new IllegalArgumentException("cannot set a byte[] value on a non-binary field");
/*     */     }
/* 326 */     this.fieldsData = value;
/* 327 */     this.binaryLength = length;
/* 328 */     this.binaryOffset = offset;
/*     */   }
/*     */ 
/*     */   public void setTokenStream(TokenStream tokenStream)
/*     */   {
/* 334 */     this.isIndexed = true;
/* 335 */     this.isTokenized = true;
/* 336 */     this.tokenStream = tokenStream;
/*     */   }
/*     */ 
/*     */   public Field(String name, String value, Store store, Index index)
/*     */   {
/* 352 */     this(name, value, store, index, TermVector.NO);
/*     */   }
/*     */ 
/*     */   public Field(String name, String value, Store store, Index index, TermVector termVector)
/*     */   {
/* 373 */     this(name, true, value, store, index, termVector);
/*     */   }
/*     */ 
/*     */   public Field(String name, boolean internName, String value, Store store, Index index, TermVector termVector)
/*     */   {
/* 395 */     if (name == null)
/* 396 */       throw new NullPointerException("name cannot be null");
/* 397 */     if (value == null)
/* 398 */       throw new NullPointerException("value cannot be null");
/* 399 */     if ((name.length() == 0) && (value.length() == 0))
/* 400 */       throw new IllegalArgumentException("name and value cannot both be empty");
/* 401 */     if ((index == Index.NO) && (store == Store.NO)) {
/* 402 */       throw new IllegalArgumentException("it doesn't make sense to have a field that is neither indexed nor stored");
/*     */     }
/* 404 */     if ((index == Index.NO) && (termVector != TermVector.NO)) {
/* 405 */       throw new IllegalArgumentException("cannot store term vector information for a field that is not indexed");
/*     */     }
/*     */ 
/* 408 */     if (internName) {
/* 409 */       name = StringHelper.intern(name);
/*     */     }
/* 411 */     this.name = name;
/*     */ 
/* 413 */     this.fieldsData = value;
/*     */ 
/* 415 */     this.isStored = store.isStored();
/*     */ 
/* 417 */     this.isIndexed = index.isIndexed();
/* 418 */     this.isTokenized = index.isAnalyzed();
/* 419 */     this.omitNorms = index.omitNorms();
/* 420 */     if (index == Index.NO)
/*     */     {
/* 422 */       this.indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
/*     */     }
/*     */ 
/* 425 */     this.isBinary = false;
/*     */ 
/* 427 */     setStoreTermVector(termVector);
/*     */   }
/*     */ 
/*     */   public Field(String name, Reader reader)
/*     */   {
/* 441 */     this(name, reader, TermVector.NO);
/*     */   }
/*     */ 
/*     */   public Field(String name, Reader reader, TermVector termVector)
/*     */   {
/* 456 */     if (name == null)
/* 457 */       throw new NullPointerException("name cannot be null");
/* 458 */     if (reader == null) {
/* 459 */       throw new NullPointerException("reader cannot be null");
/*     */     }
/* 461 */     this.name = StringHelper.intern(name);
/* 462 */     this.fieldsData = reader;
/*     */ 
/* 464 */     this.isStored = false;
/*     */ 
/* 466 */     this.isIndexed = true;
/* 467 */     this.isTokenized = true;
/*     */ 
/* 469 */     this.isBinary = false;
/*     */ 
/* 471 */     setStoreTermVector(termVector);
/*     */   }
/*     */ 
/*     */   public Field(String name, TokenStream tokenStream)
/*     */   {
/* 486 */     this(name, tokenStream, TermVector.NO);
/*     */   }
/*     */ 
/*     */   public Field(String name, TokenStream tokenStream, TermVector termVector)
/*     */   {
/* 502 */     if (name == null)
/* 503 */       throw new NullPointerException("name cannot be null");
/* 504 */     if (tokenStream == null) {
/* 505 */       throw new NullPointerException("tokenStream cannot be null");
/*     */     }
/* 507 */     this.name = StringHelper.intern(name);
/* 508 */     this.fieldsData = null;
/* 509 */     this.tokenStream = tokenStream;
/*     */ 
/* 511 */     this.isStored = false;
/*     */ 
/* 513 */     this.isIndexed = true;
/* 514 */     this.isTokenized = true;
/*     */ 
/* 516 */     this.isBinary = false;
/*     */ 
/* 518 */     setStoreTermVector(termVector);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Field(String name, byte[] value, Store store)
/*     */   {
/* 533 */     this(name, value, 0, value.length);
/*     */ 
/* 535 */     if (store == Store.NO)
/* 536 */       throw new IllegalArgumentException("binary values can't be unstored");
/*     */   }
/*     */ 
/*     */   public Field(String name, byte[] value)
/*     */   {
/* 547 */     this(name, value, 0, value.length);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Field(String name, byte[] value, int offset, int length, Store store)
/*     */   {
/* 563 */     this(name, value, offset, length);
/*     */ 
/* 565 */     if (store == Store.NO)
/* 566 */       throw new IllegalArgumentException("binary values can't be unstored");
/*     */   }
/*     */ 
/*     */   public Field(String name, byte[] value, int offset, int length)
/*     */   {
/* 580 */     if (name == null)
/* 581 */       throw new IllegalArgumentException("name cannot be null");
/* 582 */     if (value == null) {
/* 583 */       throw new IllegalArgumentException("value cannot be null");
/*     */     }
/* 585 */     this.name = StringHelper.intern(name);
/* 586 */     this.fieldsData = value;
/*     */ 
/* 588 */     this.isStored = true;
/* 589 */     this.isIndexed = false;
/* 590 */     this.isTokenized = false;
/* 591 */     this.indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
/* 592 */     this.omitNorms = true;
/*     */ 
/* 594 */     this.isBinary = true;
/* 595 */     this.binaryLength = length;
/* 596 */     this.binaryOffset = offset;
/*     */ 
/* 598 */     setStoreTermVector(TermVector.NO);
/*     */   }
/*     */ 
/*     */   public static abstract enum TermVector
/*     */   {
/* 176 */     NO, 
/*     */ 
/* 187 */     YES, 
/*     */ 
/* 201 */     WITH_POSITIONS, 
/*     */ 
/* 215 */     WITH_OFFSETS, 
/*     */ 
/* 231 */     WITH_POSITIONS_OFFSETS;
/*     */ 
/*     */     public static TermVector toTermVector(boolean stored, boolean withOffsets, boolean withPositions)
/*     */     {
/* 244 */       if (!stored) {
/* 245 */         return NO;
/*     */       }
/*     */ 
/* 248 */       if (withOffsets) {
/* 249 */         if (withPositions) {
/* 250 */           return WITH_POSITIONS_OFFSETS;
/*     */         }
/* 252 */         return WITH_OFFSETS;
/*     */       }
/*     */ 
/* 255 */       if (withPositions) {
/* 256 */         return WITH_POSITIONS;
/*     */       }
/* 258 */       return YES;
/*     */     }
/*     */ 
/*     */     public abstract boolean isStored();
/*     */ 
/*     */     public abstract boolean withPositions();
/*     */ 
/*     */     public abstract boolean withOffsets();
/*     */   }
/*     */ 
/*     */   public static abstract enum Index
/*     */   {
/*  67 */     NO, 
/*     */ 
/*  79 */     ANALYZED, 
/*     */ 
/*  92 */     NOT_ANALYZED, 
/*     */ 
/* 115 */     NOT_ANALYZED_NO_NORMS, 
/*     */ 
/* 129 */     ANALYZED_NO_NORMS;
/*     */ 
/*     */     public static Index toIndex(boolean indexed, boolean analyzed)
/*     */     {
/* 140 */       return toIndex(indexed, analyzed, false);
/*     */     }
/*     */ 
/*     */     public static Index toIndex(boolean indexed, boolean analyzed, boolean omitNorms)
/*     */     {
/* 147 */       if (!indexed) {
/* 148 */         return NO;
/*     */       }
/*     */ 
/* 152 */       if (!omitNorms) {
/* 153 */         if (analyzed) {
/* 154 */           return ANALYZED;
/*     */         }
/* 156 */         return NOT_ANALYZED;
/*     */       }
/*     */ 
/* 160 */       if (analyzed) {
/* 161 */         return ANALYZED_NO_NORMS;
/*     */       }
/* 163 */       return NOT_ANALYZED_NO_NORMS;
/*     */     }
/*     */ 
/*     */     public abstract boolean isIndexed();
/*     */ 
/*     */     public abstract boolean isAnalyzed();
/*     */ 
/*     */     public abstract boolean omitNorms();
/*     */   }
/*     */ 
/*     */   public static abstract enum Store
/*     */   {
/*  47 */     YES, 
/*     */ 
/*  53 */     NO;
/*     */ 
/*     */     public abstract boolean isStored();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.Field
 * JD-Core Version:    0.6.0
 */