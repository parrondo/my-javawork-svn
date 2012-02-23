/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ public abstract class TermVectorMapper
/*    */ {
/*    */   private boolean ignoringPositions;
/*    */   private boolean ignoringOffsets;
/*    */ 
/*    */   protected TermVectorMapper()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected TermVectorMapper(boolean ignoringPositions, boolean ignoringOffsets)
/*    */   {
/* 43 */     this.ignoringPositions = ignoringPositions;
/* 44 */     this.ignoringOffsets = ignoringOffsets;
/*    */   }
/*    */ 
/*    */   public abstract void setExpectations(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
/*    */ 
/*    */   public abstract void map(String paramString, int paramInt, TermVectorOffsetInfo[] paramArrayOfTermVectorOffsetInfo, int[] paramArrayOfInt);
/*    */ 
/*    */   public boolean isIgnoringPositions()
/*    */   {
/* 75 */     return this.ignoringPositions;
/*    */   }
/*    */ 
/*    */   public boolean isIgnoringOffsets()
/*    */   {
/* 85 */     return this.ignoringOffsets;
/*    */   }
/*    */ 
/*    */   public void setDocumentNumber(int documentNumber)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorMapper
 * JD-Core Version:    0.6.0
 */