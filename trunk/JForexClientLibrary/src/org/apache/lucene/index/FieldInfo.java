/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ public final class FieldInfo
/*     */ {
/*     */   final String name;
/*     */   final int number;
/*     */   boolean isIndexed;
/*     */   boolean storeTermVector;
/*     */   boolean storeOffsetWithTermVector;
/*     */   boolean storePositionWithTermVector;
/*     */   public boolean omitNorms;
/*     */   public IndexOptions indexOptions;
/*     */   boolean storePayloads;
/*     */ 
/*     */   FieldInfo(String na, boolean tk, int nu, boolean storeTermVector, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector, boolean omitNorms, boolean storePayloads, IndexOptions indexOptions)
/*     */   {
/*  53 */     this.name = na;
/*  54 */     this.isIndexed = tk;
/*  55 */     this.number = nu;
/*  56 */     if (this.isIndexed) {
/*  57 */       this.storeTermVector = storeTermVector;
/*  58 */       this.storeOffsetWithTermVector = storeOffsetWithTermVector;
/*  59 */       this.storePositionWithTermVector = storePositionWithTermVector;
/*  60 */       this.storePayloads = storePayloads;
/*  61 */       this.omitNorms = omitNorms;
/*  62 */       this.indexOptions = indexOptions;
/*     */     } else {
/*  64 */       this.storeTermVector = false;
/*  65 */       this.storeOffsetWithTermVector = false;
/*  66 */       this.storePositionWithTermVector = false;
/*  67 */       this.storePayloads = false;
/*  68 */       this.omitNorms = true;
/*  69 */       this.indexOptions = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
/*     */     }
/*  71 */     assert ((indexOptions == IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) || (!storePayloads));
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  76 */     return new FieldInfo(this.name, this.isIndexed, this.number, this.storeTermVector, this.storePositionWithTermVector, this.storeOffsetWithTermVector, this.omitNorms, this.storePayloads, this.indexOptions);
/*     */   }
/*     */ 
/*     */   void update(boolean isIndexed, boolean storeTermVector, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector, boolean omitNorms, boolean storePayloads, IndexOptions indexOptions)
/*     */   {
/*  83 */     if (this.isIndexed != isIndexed) {
/*  84 */       this.isIndexed = true;
/*     */     }
/*  86 */     if (isIndexed) {
/*  87 */       if (this.storeTermVector != storeTermVector) {
/*  88 */         this.storeTermVector = true;
/*     */       }
/*  90 */       if (this.storePositionWithTermVector != storePositionWithTermVector) {
/*  91 */         this.storePositionWithTermVector = true;
/*     */       }
/*  93 */       if (this.storeOffsetWithTermVector != storeOffsetWithTermVector) {
/*  94 */         this.storeOffsetWithTermVector = true;
/*     */       }
/*  96 */       if (this.storePayloads != storePayloads) {
/*  97 */         this.storePayloads = true;
/*     */       }
/*  99 */       if (this.omitNorms != omitNorms) {
/* 100 */         this.omitNorms = false;
/*     */       }
/* 102 */       if (this.indexOptions != indexOptions)
/*     */       {
/* 104 */         this.indexOptions = (this.indexOptions.compareTo(indexOptions) < 0 ? this.indexOptions : indexOptions);
/* 105 */         this.storePayloads = false;
/*     */       }
/*     */     }
/* 108 */     assert ((this.indexOptions == IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) || (!this.storePayloads));
/*     */   }
/*     */ 
/*     */   public static enum IndexOptions
/*     */   {
/*  43 */     DOCS_ONLY, 
/*     */ 
/*  45 */     DOCS_AND_FREQS, 
/*     */ 
/*  47 */     DOCS_AND_FREQS_AND_POSITIONS;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FieldInfo
 * JD-Core Version:    0.6.0
 */