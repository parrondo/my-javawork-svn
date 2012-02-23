/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ 
/*     */ public final class FieldInvertState
/*     */ {
/*     */   int position;
/*     */   int length;
/*     */   int numOverlap;
/*     */   int offset;
/*     */   int maxTermFrequency;
/*     */   int uniqueTermCount;
/*     */   float boost;
/*     */   AttributeSource attributeSource;
/*     */ 
/*     */   public FieldInvertState()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FieldInvertState(int position, int length, int numOverlap, int offset, float boost)
/*     */   {
/*  42 */     this.position = position;
/*  43 */     this.length = length;
/*  44 */     this.numOverlap = numOverlap;
/*  45 */     this.offset = offset;
/*  46 */     this.boost = boost;
/*     */   }
/*     */ 
/*     */   void reset(float docBoost)
/*     */   {
/*  54 */     this.position = 0;
/*  55 */     this.length = 0;
/*  56 */     this.numOverlap = 0;
/*  57 */     this.offset = 0;
/*  58 */     this.maxTermFrequency = 0;
/*  59 */     this.uniqueTermCount = 0;
/*  60 */     this.boost = docBoost;
/*  61 */     this.attributeSource = null;
/*     */   }
/*     */ 
/*     */   public int getPosition()
/*     */   {
/*  69 */     return this.position;
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  77 */     return this.length;
/*     */   }
/*     */ 
/*     */   public void setLength(int length) {
/*  81 */     this.length = length;
/*     */   }
/*     */ 
/*     */   public int getNumOverlap()
/*     */   {
/*  89 */     return this.numOverlap;
/*     */   }
/*     */ 
/*     */   public void setNumOverlap(int numOverlap) {
/*  93 */     this.numOverlap = numOverlap;
/*     */   }
/*     */ 
/*     */   public int getOffset()
/*     */   {
/* 101 */     return this.offset;
/*     */   }
/*     */ 
/*     */   public float getBoost()
/*     */   {
/* 111 */     return this.boost;
/*     */   }
/*     */ 
/*     */   public void setBoost(float boost) {
/* 115 */     this.boost = boost;
/*     */   }
/*     */ 
/*     */   public int getMaxTermFrequency()
/*     */   {
/* 124 */     return this.maxTermFrequency;
/*     */   }
/*     */ 
/*     */   public int getUniqueTermCount()
/*     */   {
/* 131 */     return this.uniqueTermCount;
/*     */   }
/*     */ 
/*     */   public AttributeSource getAttributeSource() {
/* 135 */     return this.attributeSource;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FieldInvertState
 * JD-Core Version:    0.6.0
 */