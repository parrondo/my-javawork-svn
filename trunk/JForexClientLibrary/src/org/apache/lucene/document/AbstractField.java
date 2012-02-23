/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.index.FieldInfo.IndexOptions;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ public abstract class AbstractField
/*     */   implements Fieldable
/*     */ {
/*  33 */   protected String name = "body";
/*  34 */   protected boolean storeTermVector = false;
/*  35 */   protected boolean storeOffsetWithTermVector = false;
/*  36 */   protected boolean storePositionWithTermVector = false;
/*  37 */   protected boolean omitNorms = false;
/*  38 */   protected boolean isStored = false;
/*  39 */   protected boolean isIndexed = true;
/*  40 */   protected boolean isTokenized = true;
/*  41 */   protected boolean isBinary = false;
/*  42 */   protected boolean lazy = false;
/*  43 */   protected FieldInfo.IndexOptions indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
/*  44 */   protected float boost = 1.0F;
/*     */ 
/*  46 */   protected Object fieldsData = null;
/*     */   protected TokenStream tokenStream;
/*     */   protected int binaryLength;
/*     */   protected int binaryOffset;
/*     */ 
/*     */   protected AbstractField()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected AbstractField(String name, Field.Store store, Field.Index index, Field.TermVector termVector)
/*     */   {
/*  58 */     if (name == null)
/*  59 */       throw new NullPointerException("name cannot be null");
/*  60 */     this.name = StringHelper.intern(name);
/*     */ 
/*  62 */     this.isStored = store.isStored();
/*  63 */     this.isIndexed = index.isIndexed();
/*  64 */     this.isTokenized = index.isAnalyzed();
/*  65 */     this.omitNorms = index.omitNorms();
/*     */ 
/*  67 */     this.isBinary = false;
/*     */ 
/*  69 */     setStoreTermVector(termVector);
/*     */   }
/*     */ 
/*     */   public void setBoost(float boost)
/*     */   {
/*  95 */     this.boost = boost;
/*     */   }
/*     */ 
/*     */   public float getBoost()
/*     */   {
/* 110 */     return this.boost;
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/* 116 */     return this.name;
/*     */   }
/*     */   protected void setStoreTermVector(Field.TermVector termVector) {
/* 119 */     this.storeTermVector = termVector.isStored();
/* 120 */     this.storePositionWithTermVector = termVector.withPositions();
/* 121 */     this.storeOffsetWithTermVector = termVector.withOffsets();
/*     */   }
/*     */ 
/*     */   public final boolean isStored()
/*     */   {
/* 127 */     return this.isStored;
/*     */   }
/*     */ 
/*     */   public final boolean isIndexed() {
/* 131 */     return this.isIndexed;
/*     */   }
/*     */ 
/*     */   public final boolean isTokenized()
/*     */   {
/* 136 */     return this.isTokenized;
/*     */   }
/*     */ 
/*     */   public final boolean isTermVectorStored()
/*     */   {
/* 146 */     return this.storeTermVector;
/*     */   }
/*     */ 
/*     */   public boolean isStoreOffsetWithTermVector()
/*     */   {
/* 153 */     return this.storeOffsetWithTermVector;
/*     */   }
/*     */ 
/*     */   public boolean isStorePositionWithTermVector()
/*     */   {
/* 160 */     return this.storePositionWithTermVector;
/*     */   }
/*     */ 
/*     */   public final boolean isBinary()
/*     */   {
/* 165 */     return this.isBinary;
/*     */   }
/*     */ 
/*     */   public byte[] getBinaryValue()
/*     */   {
/* 177 */     return getBinaryValue(null);
/*     */   }
/*     */ 
/*     */   public byte[] getBinaryValue(byte[] result) {
/* 181 */     if ((this.isBinary) || ((this.fieldsData instanceof byte[]))) {
/* 182 */       return (byte[])(byte[])this.fieldsData;
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   public int getBinaryLength()
/*     */   {
/* 193 */     if (this.isBinary)
/* 194 */       return this.binaryLength;
/* 195 */     if ((this.fieldsData instanceof byte[])) {
/* 196 */       return ((byte[])(byte[])this.fieldsData).length;
/*     */     }
/* 198 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getBinaryOffset()
/*     */   {
/* 207 */     return this.binaryOffset;
/*     */   }
/*     */ 
/*     */   public boolean getOmitNorms() {
/* 211 */     return this.omitNorms;
/*     */   }
/* 215 */   @Deprecated
/*     */   public boolean getOmitTermFreqAndPositions() { return this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY; }
/*     */ 
/*     */   public FieldInfo.IndexOptions getIndexOptions() {
/* 218 */     return this.indexOptions;
/*     */   }
/*     */ 
/*     */   public void setOmitNorms(boolean omitNorms)
/*     */   {
/* 225 */     this.omitNorms = omitNorms;
/*     */   }
/*     */   @Deprecated
/*     */   public void setOmitTermFreqAndPositions(boolean omitTermFreqAndPositions) {
/* 230 */     if (omitTermFreqAndPositions)
/* 231 */       this.indexOptions = FieldInfo.IndexOptions.DOCS_ONLY;
/*     */     else
/* 233 */       this.indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
/*     */   }
/*     */ 
/*     */   public void setIndexOptions(FieldInfo.IndexOptions indexOptions)
/*     */   {
/* 248 */     this.indexOptions = indexOptions;
/*     */   }
/*     */   public boolean isLazy() {
/* 251 */     return this.lazy;
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 257 */     StringBuilder result = new StringBuilder();
/* 258 */     if (this.isStored) {
/* 259 */       result.append("stored");
/*     */     }
/* 261 */     if (this.isIndexed) {
/* 262 */       if (result.length() > 0)
/* 263 */         result.append(",");
/* 264 */       result.append("indexed");
/*     */     }
/* 266 */     if (this.isTokenized) {
/* 267 */       if (result.length() > 0)
/* 268 */         result.append(",");
/* 269 */       result.append("tokenized");
/*     */     }
/* 271 */     if (this.storeTermVector) {
/* 272 */       if (result.length() > 0)
/* 273 */         result.append(",");
/* 274 */       result.append("termVector");
/*     */     }
/* 276 */     if (this.storeOffsetWithTermVector) {
/* 277 */       if (result.length() > 0)
/* 278 */         result.append(",");
/* 279 */       result.append("termVectorOffsets");
/*     */     }
/* 281 */     if (this.storePositionWithTermVector) {
/* 282 */       if (result.length() > 0)
/* 283 */         result.append(",");
/* 284 */       result.append("termVectorPosition");
/*     */     }
/* 286 */     if (this.isBinary) {
/* 287 */       if (result.length() > 0)
/* 288 */         result.append(",");
/* 289 */       result.append("binary");
/*     */     }
/* 291 */     if (this.omitNorms) {
/* 292 */       result.append(",omitNorms");
/*     */     }
/* 294 */     if (this.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
/* 295 */       result.append(",indexOptions=");
/* 296 */       result.append(this.indexOptions);
/*     */     }
/* 298 */     if (this.lazy) {
/* 299 */       result.append(",lazy");
/*     */     }
/* 301 */     result.append('<');
/* 302 */     result.append(this.name);
/* 303 */     result.append(':');
/*     */ 
/* 305 */     if ((this.fieldsData != null) && (!this.lazy)) {
/* 306 */       result.append(this.fieldsData);
/*     */     }
/*     */ 
/* 309 */     result.append('>');
/* 310 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.AbstractField
 * JD-Core Version:    0.6.0
 */