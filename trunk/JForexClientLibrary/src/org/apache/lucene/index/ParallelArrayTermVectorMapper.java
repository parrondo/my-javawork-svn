/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ class ParallelArrayTermVectorMapper extends TermVectorMapper
/*     */ {
/*     */   private String[] terms;
/*     */   private int[] termFreqs;
/*     */   private int[][] positions;
/*     */   private TermVectorOffsetInfo[][] offsets;
/*     */   private int currentPosition;
/*     */   private boolean storingOffsets;
/*     */   private boolean storingPositions;
/*     */   private String field;
/*     */ 
/*     */   public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions)
/*     */   {
/* 548 */     this.field = field;
/* 549 */     this.terms = new String[numTerms];
/* 550 */     this.termFreqs = new int[numTerms];
/* 551 */     this.storingOffsets = storeOffsets;
/* 552 */     this.storingPositions = storePositions;
/* 553 */     if (storePositions)
/* 554 */       this.positions = new int[numTerms][];
/* 555 */     if (storeOffsets)
/* 556 */       this.offsets = new TermVectorOffsetInfo[numTerms][];
/*     */   }
/*     */ 
/*     */   public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions)
/*     */   {
/* 561 */     this.terms[this.currentPosition] = term;
/* 562 */     this.termFreqs[this.currentPosition] = frequency;
/* 563 */     if (this.storingOffsets)
/*     */     {
/* 565 */       this.offsets[this.currentPosition] = offsets;
/*     */     }
/* 567 */     if (this.storingPositions)
/*     */     {
/* 569 */       this.positions[this.currentPosition] = positions;
/*     */     }
/* 571 */     this.currentPosition += 1;
/*     */   }
/*     */ 
/*     */   public TermFreqVector materializeVector()
/*     */   {
/* 579 */     SegmentTermVector tv = null;
/* 580 */     if ((this.field != null) && (this.terms != null)) {
/* 581 */       if ((this.storingPositions) || (this.storingOffsets))
/* 582 */         tv = new SegmentTermPositionVector(this.field, this.terms, this.termFreqs, this.positions, this.offsets);
/*     */       else {
/* 584 */         tv = new SegmentTermVector(this.field, this.terms, this.termFreqs);
/*     */       }
/*     */     }
/* 587 */     return tv;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ParallelArrayTermVectorMapper
 * JD-Core Version:    0.6.0
 */