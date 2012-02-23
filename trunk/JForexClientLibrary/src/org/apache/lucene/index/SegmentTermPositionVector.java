/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ class SegmentTermPositionVector extends SegmentTermVector
/*    */   implements TermPositionVector
/*    */ {
/*    */   protected int[][] positions;
/*    */   protected TermVectorOffsetInfo[][] offsets;
/* 23 */   public static final int[] EMPTY_TERM_POS = new int[0];
/*    */ 
/*    */   public SegmentTermPositionVector(String field, String[] terms, int[] termFreqs, int[][] positions, TermVectorOffsetInfo[][] offsets) {
/* 26 */     super(field, terms, termFreqs);
/* 27 */     this.offsets = offsets;
/* 28 */     this.positions = positions;
/*    */   }
/*    */ 
/*    */   public TermVectorOffsetInfo[] getOffsets(int index)
/*    */   {
/* 39 */     TermVectorOffsetInfo[] result = TermVectorOffsetInfo.EMPTY_OFFSET_INFO;
/* 40 */     if (this.offsets == null)
/* 41 */       return null;
/* 42 */     if ((index >= 0) && (index < this.offsets.length))
/*    */     {
/* 44 */       result = this.offsets[index];
/*    */     }
/* 46 */     return result;
/*    */   }
/*    */ 
/*    */   public int[] getTermPositions(int index)
/*    */   {
/* 55 */     int[] result = EMPTY_TERM_POS;
/* 56 */     if (this.positions == null)
/* 57 */       return null;
/* 58 */     if ((index >= 0) && (index < this.positions.length))
/*    */     {
/* 60 */       result = this.positions[index];
/*    */     }
/*    */ 
/* 63 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentTermPositionVector
 * JD-Core Version:    0.6.0
 */