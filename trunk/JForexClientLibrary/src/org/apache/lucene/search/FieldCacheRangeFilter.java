/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.TermDocs;
/*     */ import org.apache.lucene.util.NumericUtils;
/*     */ 
/*     */ public abstract class FieldCacheRangeFilter<T> extends Filter
/*     */ {
/*     */   final String field;
/*     */   final FieldCache.Parser parser;
/*     */   final T lowerVal;
/*     */   final T upperVal;
/*     */   final boolean includeLower;
/*     */   final boolean includeUpper;
/*     */ 
/*     */   private FieldCacheRangeFilter(String field, FieldCache.Parser parser, T lowerVal, T upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/*  64 */     this.field = field;
/*  65 */     this.parser = parser;
/*  66 */     this.lowerVal = lowerVal;
/*  67 */     this.upperVal = upperVal;
/*  68 */     this.includeLower = includeLower;
/*  69 */     this.includeUpper = includeUpper;
/*     */   }
/*     */ 
/*     */   public abstract DocIdSet getDocIdSet(IndexReader paramIndexReader)
/*     */     throws IOException;
/*     */ 
/*     */   public static FieldCacheRangeFilter<String> newStringRange(String field, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/*  82 */     return new FieldCacheRangeFilter(field, null, lowerVal, upperVal, includeLower, includeUpper)
/*     */     {
/*     */       public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
/*  85 */         FieldCache.StringIndex fcsi = FieldCache.DEFAULT.getStringIndex(reader, this.field);
/*  86 */         int lowerPoint = fcsi.binarySearchLookup((String)this.lowerVal);
/*  87 */         int upperPoint = fcsi.binarySearchLookup((String)this.upperVal);
/*     */         int inclusiveLowerPoint;
/*     */         int inclusiveLowerPoint;
/*  95 */         if (lowerPoint == 0) {
/*  96 */           assert (this.lowerVal == null);
/*  97 */           inclusiveLowerPoint = 1;
/*     */         }
/*     */         else
/*     */         {
/*     */           int inclusiveLowerPoint;
/*  98 */           if ((this.includeLower) && (lowerPoint > 0)) {
/*  99 */             inclusiveLowerPoint = lowerPoint;
/*     */           }
/*     */           else
/*     */           {
/*     */             int inclusiveLowerPoint;
/* 100 */             if (lowerPoint > 0)
/* 101 */               inclusiveLowerPoint = lowerPoint + 1;
/*     */             else
/* 103 */               inclusiveLowerPoint = Math.max(1, -lowerPoint - 1);
/*     */           }
/*     */         }
/*     */         int inclusiveUpperPoint;
/*     */         int inclusiveUpperPoint;
/* 106 */         if (upperPoint == 0) {
/* 107 */           assert (this.upperVal == null);
/* 108 */           inclusiveUpperPoint = 2147483647;
/*     */         }
/*     */         else
/*     */         {
/*     */           int inclusiveUpperPoint;
/* 109 */           if ((this.includeUpper) && (upperPoint > 0)) {
/* 110 */             inclusiveUpperPoint = upperPoint;
/*     */           }
/*     */           else
/*     */           {
/*     */             int inclusiveUpperPoint;
/* 111 */             if (upperPoint > 0)
/* 112 */               inclusiveUpperPoint = upperPoint - 1;
/*     */             else
/* 114 */               inclusiveUpperPoint = -upperPoint - 2;
/*     */           }
/*     */         }
/* 117 */         if ((inclusiveUpperPoint <= 0) || (inclusiveLowerPoint > inclusiveUpperPoint)) {
/* 118 */           return DocIdSet.EMPTY_DOCIDSET;
/*     */         }
/* 120 */         assert ((inclusiveLowerPoint > 0) && (inclusiveUpperPoint > 0));
/*     */ 
/* 124 */         return new FieldCacheRangeFilter.FieldCacheDocIdSet(reader, false, fcsi, inclusiveLowerPoint, inclusiveUpperPoint)
/*     */         {
/*     */           final boolean matchDoc(int doc) {
/* 127 */             return (this.val$fcsi.order[doc] >= this.val$inclusiveLowerPoint) && (this.val$fcsi.order[doc] <= this.val$inclusiveUpperPoint);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Byte> newByteRange(String field, Byte lowerVal, Byte upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 140 */     return newByteRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Byte> newByteRange(String field, FieldCache.ByteParser parser, Byte lowerVal, Byte upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 149 */     return new FieldCacheRangeFilter(field, parser, lowerVal, upperVal, includeLower, includeUpper)
/*     */     {
/*     */       public DocIdSet getDocIdSet(IndexReader reader)
/*     */         throws IOException
/*     */       {
/*     */         byte inclusiveLowerPoint;
/*     */         byte inclusiveLowerPoint;
/* 153 */         if (this.lowerVal != null) {
/* 154 */           byte i = ((Byte)this.lowerVal).byteValue();
/* 155 */           if ((!this.includeLower) && (i == 127))
/* 156 */             return DocIdSet.EMPTY_DOCIDSET;
/* 157 */           inclusiveLowerPoint = (byte)(this.includeLower ? i : i + 1);
/*     */         } else {
/* 159 */           inclusiveLowerPoint = -128;
/*     */         }
/*     */         byte inclusiveUpperPoint;
/*     */         byte inclusiveUpperPoint;
/* 161 */         if (this.upperVal != null) {
/* 162 */           byte i = ((Byte)this.upperVal).byteValue();
/* 163 */           if ((!this.includeUpper) && (i == -128))
/* 164 */             return DocIdSet.EMPTY_DOCIDSET;
/* 165 */           inclusiveUpperPoint = (byte)(this.includeUpper ? i : i - 1);
/*     */         } else {
/* 167 */           inclusiveUpperPoint = 127;
/*     */         }
/*     */ 
/* 170 */         if (inclusiveLowerPoint > inclusiveUpperPoint) {
/* 171 */           return DocIdSet.EMPTY_DOCIDSET;
/*     */         }
/* 173 */         byte[] values = FieldCache.DEFAULT.getBytes(reader, this.field, (FieldCache.ByteParser)this.parser);
/*     */ 
/* 175 */         return new FieldCacheRangeFilter.FieldCacheDocIdSet(reader, (inclusiveLowerPoint <= 0) && (inclusiveUpperPoint >= 0), values, inclusiveLowerPoint, inclusiveUpperPoint)
/*     */         {
/*     */           boolean matchDoc(int doc) {
/* 178 */             return (this.val$values[doc] >= this.val$inclusiveLowerPoint) && (this.val$values[doc] <= this.val$inclusiveUpperPoint);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Short> newShortRange(String field, Short lowerVal, Short upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 191 */     return newShortRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Short> newShortRange(String field, FieldCache.ShortParser parser, Short lowerVal, Short upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 200 */     return new FieldCacheRangeFilter(field, parser, lowerVal, upperVal, includeLower, includeUpper)
/*     */     {
/*     */       public DocIdSet getDocIdSet(IndexReader reader)
/*     */         throws IOException
/*     */       {
/*     */         short inclusiveLowerPoint;
/*     */         short inclusiveLowerPoint;
/* 204 */         if (this.lowerVal != null) {
/* 205 */           short i = ((Short)this.lowerVal).shortValue();
/* 206 */           if ((!this.includeLower) && (i == 32767))
/* 207 */             return DocIdSet.EMPTY_DOCIDSET;
/* 208 */           inclusiveLowerPoint = (short)(this.includeLower ? i : i + 1);
/*     */         } else {
/* 210 */           inclusiveLowerPoint = -32768;
/*     */         }
/*     */         short inclusiveUpperPoint;
/*     */         short inclusiveUpperPoint;
/* 212 */         if (this.upperVal != null) {
/* 213 */           short i = ((Short)this.upperVal).shortValue();
/* 214 */           if ((!this.includeUpper) && (i == -32768))
/* 215 */             return DocIdSet.EMPTY_DOCIDSET;
/* 216 */           inclusiveUpperPoint = (short)(this.includeUpper ? i : i - 1);
/*     */         } else {
/* 218 */           inclusiveUpperPoint = 32767;
/*     */         }
/*     */ 
/* 221 */         if (inclusiveLowerPoint > inclusiveUpperPoint) {
/* 222 */           return DocIdSet.EMPTY_DOCIDSET;
/*     */         }
/* 224 */         short[] values = FieldCache.DEFAULT.getShorts(reader, this.field, (FieldCache.ShortParser)this.parser);
/*     */ 
/* 226 */         return new FieldCacheRangeFilter.FieldCacheDocIdSet(reader, (inclusiveLowerPoint <= 0) && (inclusiveUpperPoint >= 0), values, inclusiveLowerPoint, inclusiveUpperPoint)
/*     */         {
/*     */           boolean matchDoc(int doc) {
/* 229 */             return (this.val$values[doc] >= this.val$inclusiveLowerPoint) && (this.val$values[doc] <= this.val$inclusiveUpperPoint);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Integer> newIntRange(String field, Integer lowerVal, Integer upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 242 */     return newIntRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Integer> newIntRange(String field, FieldCache.IntParser parser, Integer lowerVal, Integer upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 251 */     return new FieldCacheRangeFilter(field, parser, lowerVal, upperVal, includeLower, includeUpper)
/*     */     {
/*     */       public DocIdSet getDocIdSet(IndexReader reader)
/*     */         throws IOException
/*     */       {
/*     */         int inclusiveLowerPoint;
/*     */         int inclusiveLowerPoint;
/* 255 */         if (this.lowerVal != null) {
/* 256 */           int i = ((Integer)this.lowerVal).intValue();
/* 257 */           if ((!this.includeLower) && (i == 2147483647))
/* 258 */             return DocIdSet.EMPTY_DOCIDSET;
/* 259 */           inclusiveLowerPoint = this.includeLower ? i : i + 1;
/*     */         } else {
/* 261 */           inclusiveLowerPoint = -2147483648;
/*     */         }
/*     */         int inclusiveUpperPoint;
/*     */         int inclusiveUpperPoint;
/* 263 */         if (this.upperVal != null) {
/* 264 */           int i = ((Integer)this.upperVal).intValue();
/* 265 */           if ((!this.includeUpper) && (i == -2147483648))
/* 266 */             return DocIdSet.EMPTY_DOCIDSET;
/* 267 */           inclusiveUpperPoint = this.includeUpper ? i : i - 1;
/*     */         } else {
/* 269 */           inclusiveUpperPoint = 2147483647;
/*     */         }
/*     */ 
/* 272 */         if (inclusiveLowerPoint > inclusiveUpperPoint) {
/* 273 */           return DocIdSet.EMPTY_DOCIDSET;
/*     */         }
/* 275 */         int[] values = FieldCache.DEFAULT.getInts(reader, this.field, (FieldCache.IntParser)this.parser);
/*     */ 
/* 277 */         return new FieldCacheRangeFilter.FieldCacheDocIdSet(reader, (inclusiveLowerPoint <= 0) && (inclusiveUpperPoint >= 0), values, inclusiveLowerPoint, inclusiveUpperPoint)
/*     */         {
/*     */           boolean matchDoc(int doc) {
/* 280 */             return (this.val$values[doc] >= this.val$inclusiveLowerPoint) && (this.val$values[doc] <= this.val$inclusiveUpperPoint);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Long> newLongRange(String field, Long lowerVal, Long upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 293 */     return newLongRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Long> newLongRange(String field, FieldCache.LongParser parser, Long lowerVal, Long upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 302 */     return new FieldCacheRangeFilter(field, parser, lowerVal, upperVal, includeLower, includeUpper)
/*     */     {
/*     */       public DocIdSet getDocIdSet(IndexReader reader)
/*     */         throws IOException
/*     */       {
/*     */         long inclusiveLowerPoint;
/*     */         long inclusiveLowerPoint;
/* 306 */         if (this.lowerVal != null) {
/* 307 */           long i = ((Long)this.lowerVal).longValue();
/* 308 */           if ((!this.includeLower) && (i == 9223372036854775807L))
/* 309 */             return DocIdSet.EMPTY_DOCIDSET;
/* 310 */           inclusiveLowerPoint = this.includeLower ? i : i + 1L;
/*     */         } else {
/* 312 */           inclusiveLowerPoint = -9223372036854775808L;
/*     */         }
/*     */         long inclusiveUpperPoint;
/*     */         long inclusiveUpperPoint;
/* 314 */         if (this.upperVal != null) {
/* 315 */           long i = ((Long)this.upperVal).longValue();
/* 316 */           if ((!this.includeUpper) && (i == -9223372036854775808L))
/* 317 */             return DocIdSet.EMPTY_DOCIDSET;
/* 318 */           inclusiveUpperPoint = this.includeUpper ? i : i - 1L;
/*     */         } else {
/* 320 */           inclusiveUpperPoint = 9223372036854775807L;
/*     */         }
/*     */ 
/* 323 */         if (inclusiveLowerPoint > inclusiveUpperPoint) {
/* 324 */           return DocIdSet.EMPTY_DOCIDSET;
/*     */         }
/* 326 */         long[] values = FieldCache.DEFAULT.getLongs(reader, this.field, (FieldCache.LongParser)this.parser);
/*     */ 
/* 328 */         return new FieldCacheRangeFilter.FieldCacheDocIdSet(reader, (inclusiveLowerPoint <= 0L) && (inclusiveUpperPoint >= 0L), values, inclusiveLowerPoint, inclusiveUpperPoint)
/*     */         {
/*     */           boolean matchDoc(int doc) {
/* 331 */             return (this.val$values[doc] >= this.val$inclusiveLowerPoint) && (this.val$values[doc] <= this.val$inclusiveUpperPoint);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Float> newFloatRange(String field, Float lowerVal, Float upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 344 */     return newFloatRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Float> newFloatRange(String field, FieldCache.FloatParser parser, Float lowerVal, Float upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 353 */     return new FieldCacheRangeFilter(field, parser, lowerVal, upperVal, includeLower, includeUpper)
/*     */     {
/*     */       public DocIdSet getDocIdSet(IndexReader reader)
/*     */         throws IOException
/*     */       {
/*     */         float inclusiveLowerPoint;
/*     */         float inclusiveLowerPoint;
/* 359 */         if (this.lowerVal != null) {
/* 360 */           float f = ((Float)this.lowerVal).floatValue();
/* 361 */           if ((!this.includeUpper) && (f > 0.0F) && (Float.isInfinite(f)))
/* 362 */             return DocIdSet.EMPTY_DOCIDSET;
/* 363 */           int i = NumericUtils.floatToSortableInt(f);
/* 364 */           inclusiveLowerPoint = NumericUtils.sortableIntToFloat(this.includeLower ? i : i + 1);
/*     */         } else {
/* 366 */           inclusiveLowerPoint = (1.0F / -1.0F);
/*     */         }
/*     */         float inclusiveUpperPoint;
/*     */         float inclusiveUpperPoint;
/* 368 */         if (this.upperVal != null) {
/* 369 */           float f = ((Float)this.upperVal).floatValue();
/* 370 */           if ((!this.includeUpper) && (f < 0.0F) && (Float.isInfinite(f)))
/* 371 */             return DocIdSet.EMPTY_DOCIDSET;
/* 372 */           int i = NumericUtils.floatToSortableInt(f);
/* 373 */           inclusiveUpperPoint = NumericUtils.sortableIntToFloat(this.includeUpper ? i : i - 1);
/*     */         } else {
/* 375 */           inclusiveUpperPoint = (1.0F / 1.0F);
/*     */         }
/*     */ 
/* 378 */         if (inclusiveLowerPoint > inclusiveUpperPoint) {
/* 379 */           return DocIdSet.EMPTY_DOCIDSET;
/*     */         }
/* 381 */         float[] values = FieldCache.DEFAULT.getFloats(reader, this.field, (FieldCache.FloatParser)this.parser);
/*     */ 
/* 383 */         return new FieldCacheRangeFilter.FieldCacheDocIdSet(reader, (inclusiveLowerPoint <= 0.0F) && (inclusiveUpperPoint >= 0.0F), values, inclusiveLowerPoint, inclusiveUpperPoint)
/*     */         {
/*     */           boolean matchDoc(int doc) {
/* 386 */             return (this.val$values[doc] >= this.val$inclusiveLowerPoint) && (this.val$values[doc] <= this.val$inclusiveUpperPoint);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Double> newDoubleRange(String field, Double lowerVal, Double upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 399 */     return newDoubleRange(field, null, lowerVal, upperVal, includeLower, includeUpper);
/*     */   }
/*     */ 
/*     */   public static FieldCacheRangeFilter<Double> newDoubleRange(String field, FieldCache.DoubleParser parser, Double lowerVal, Double upperVal, boolean includeLower, boolean includeUpper)
/*     */   {
/* 408 */     return new FieldCacheRangeFilter(field, parser, lowerVal, upperVal, includeLower, includeUpper)
/*     */     {
/*     */       public DocIdSet getDocIdSet(IndexReader reader)
/*     */         throws IOException
/*     */       {
/*     */         double inclusiveLowerPoint;
/*     */         double inclusiveLowerPoint;
/* 414 */         if (this.lowerVal != null) {
/* 415 */           double f = ((Double)this.lowerVal).doubleValue();
/* 416 */           if ((!this.includeUpper) && (f > 0.0D) && (Double.isInfinite(f)))
/* 417 */             return DocIdSet.EMPTY_DOCIDSET;
/* 418 */           long i = NumericUtils.doubleToSortableLong(f);
/* 419 */           inclusiveLowerPoint = NumericUtils.sortableLongToDouble(this.includeLower ? i : i + 1L);
/*     */         } else {
/* 421 */           inclusiveLowerPoint = (-1.0D / 0.0D);
/*     */         }
/*     */         double inclusiveUpperPoint;
/*     */         double inclusiveUpperPoint;
/* 423 */         if (this.upperVal != null) {
/* 424 */           double f = ((Double)this.upperVal).doubleValue();
/* 425 */           if ((!this.includeUpper) && (f < 0.0D) && (Double.isInfinite(f)))
/* 426 */             return DocIdSet.EMPTY_DOCIDSET;
/* 427 */           long i = NumericUtils.doubleToSortableLong(f);
/* 428 */           inclusiveUpperPoint = NumericUtils.sortableLongToDouble(this.includeUpper ? i : i - 1L);
/*     */         } else {
/* 430 */           inclusiveUpperPoint = (1.0D / 0.0D);
/*     */         }
/*     */ 
/* 433 */         if (inclusiveLowerPoint > inclusiveUpperPoint) {
/* 434 */           return DocIdSet.EMPTY_DOCIDSET;
/*     */         }
/* 436 */         double[] values = FieldCache.DEFAULT.getDoubles(reader, this.field, (FieldCache.DoubleParser)this.parser);
/*     */ 
/* 438 */         return new FieldCacheRangeFilter.FieldCacheDocIdSet(reader, (inclusiveLowerPoint <= 0.0D) && (inclusiveUpperPoint >= 0.0D), values, inclusiveLowerPoint, inclusiveUpperPoint)
/*     */         {
/*     */           boolean matchDoc(int doc) {
/* 441 */             return (this.val$values[doc] >= this.val$inclusiveLowerPoint) && (this.val$values[doc] <= this.val$inclusiveUpperPoint);
/*     */           } } ;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public final String toString() {
/* 450 */     StringBuilder sb = new StringBuilder(this.field).append(":");
/* 451 */     return (this.includeLower ? '[' : '{') + (this.lowerVal == null ? "*" : this.lowerVal.toString()) + " TO " + (this.upperVal == null ? "*" : this.upperVal.toString()) + (this.includeUpper ? ']' : '}');
/*     */   }
/*     */ 
/*     */   public final boolean equals(Object o)
/*     */   {
/* 461 */     if (this == o) return true;
/* 462 */     if (!(o instanceof FieldCacheRangeFilter)) return false;
/* 463 */     FieldCacheRangeFilter other = (FieldCacheRangeFilter)o;
/*     */ 
/* 465 */     if ((!this.field.equals(other.field)) || (this.includeLower != other.includeLower) || (this.includeUpper != other.includeUpper))
/*     */     {
/* 468 */       return false;
/* 469 */     }if (this.lowerVal != null ? !this.lowerVal.equals(other.lowerVal) : other.lowerVal != null) return false;
/* 470 */     if (this.upperVal != null ? !this.upperVal.equals(other.upperVal) : other.upperVal != null) return false;
/* 471 */     return this.parser != null ? this.parser.equals(other.parser) : other.parser == null;
/*     */   }
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 477 */     int h = this.field.hashCode();
/* 478 */     h ^= (this.lowerVal != null ? this.lowerVal.hashCode() : 550356204);
/* 479 */     h = h << 1 | h >>> 31;
/* 480 */     h ^= (this.upperVal != null ? this.upperVal.hashCode() : -1674416163);
/* 481 */     h ^= (this.parser != null ? this.parser.hashCode() : -1572457324);
/* 482 */     h ^= (this.includeLower ? 1549299360 : -365038026) ^ (this.includeUpper ? 1721088258 : 1948649653);
/* 483 */     return h;
/*     */   }
/*     */ 
/*     */   public String getField() {
/* 487 */     return this.field;
/*     */   }
/*     */   public boolean includesLower() {
/* 490 */     return this.includeLower;
/*     */   }
/*     */   public boolean includesUpper() {
/* 493 */     return this.includeUpper;
/*     */   }
/*     */   public T getLowerVal() {
/* 496 */     return this.lowerVal;
/*     */   }
/*     */   public T getUpperVal() {
/* 499 */     return this.upperVal;
/*     */   }
/*     */   public FieldCache.Parser getParser() {
/* 502 */     return this.parser;
/*     */   }
/*     */   static abstract class FieldCacheDocIdSet extends DocIdSet { private final IndexReader reader;
/*     */     private boolean mayUseTermDocs;
/*     */ 
/* 509 */     FieldCacheDocIdSet(IndexReader reader, boolean mayUseTermDocs) { this.reader = reader;
/* 510 */       this.mayUseTermDocs = mayUseTermDocs;
/*     */     }
/*     */ 
/*     */     abstract boolean matchDoc(int paramInt)
/*     */       throws ArrayIndexOutOfBoundsException;
/*     */ 
/*     */     public boolean isCacheable()
/*     */     {
/* 519 */       return (!this.mayUseTermDocs) || (!this.reader.hasDeletions());
/*     */     }
/*     */ 
/*     */     public DocIdSetIterator iterator()
/*     */       throws IOException
/*     */     {
/*     */       TermDocs termDocs;
/* 529 */       synchronized (this.reader) {
/* 530 */         termDocs = isCacheable() ? null : this.reader.termDocs(null);
/*     */       }
/* 532 */       if (termDocs != null)
/*     */       {
/* 534 */         return new DocIdSetIterator(termDocs) {
/* 535 */           private int doc = -1;
/*     */ 
/*     */           public int docID()
/*     */           {
/* 539 */             return this.doc;
/*     */           }
/*     */ 
/*     */           public int nextDoc() throws IOException
/*     */           {
/*     */             do
/* 545 */               if (!this.val$termDocs.next())
/* 546 */                 return this.doc = 2147483647;
/* 547 */             while (!FieldCacheRangeFilter.FieldCacheDocIdSet.this.matchDoc(this.doc = this.val$termDocs.doc()));
/* 548 */             return this.doc;
/*     */           }
/*     */ 
/*     */           public int advance(int target) throws IOException
/*     */           {
/* 553 */             if (!this.val$termDocs.skipTo(target))
/* 554 */               return this.doc = 2147483647;
/* 555 */             while (!FieldCacheRangeFilter.FieldCacheDocIdSet.this.matchDoc(this.doc = this.val$termDocs.doc())) {
/* 556 */               if (!this.val$termDocs.next())
/* 557 */                 return this.doc = 2147483647;
/*     */             }
/* 559 */             return this.doc;
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/* 565 */       return new DocIdSetIterator() {
/* 566 */         private int doc = -1;
/*     */ 
/*     */         public int docID()
/*     */         {
/* 570 */           return this.doc;
/*     */         }
/*     */ 
/*     */         public int nextDoc()
/*     */         {
/*     */           try {
/*     */             do
/* 577 */               this.doc += 1;
/* 578 */             while (!FieldCacheRangeFilter.FieldCacheDocIdSet.this.matchDoc(this.doc));
/* 579 */             return this.doc; } catch (ArrayIndexOutOfBoundsException e) {
/*     */           }
/* 581 */           return this.doc = 2147483647;
/*     */         }
/*     */ 
/*     */         public int advance(int target)
/*     */         {
/*     */           try
/*     */           {
/* 588 */             this.doc = target;
/* 589 */             while (!FieldCacheRangeFilter.FieldCacheDocIdSet.this.matchDoc(this.doc)) {
/* 590 */               this.doc += 1;
/*     */             }
/* 592 */             return this.doc; } catch (ArrayIndexOutOfBoundsException e) {
/*     */           }
/* 594 */           return this.doc = 2147483647;
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldCacheRangeFilter
 * JD-Core Version:    0.6.0
 */