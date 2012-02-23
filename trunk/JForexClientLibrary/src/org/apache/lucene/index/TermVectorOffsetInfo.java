/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class TermVectorOffsetInfo
/*    */   implements Serializable
/*    */ {
/* 31 */   public static final transient TermVectorOffsetInfo[] EMPTY_OFFSET_INFO = new TermVectorOffsetInfo[0];
/*    */   private int startOffset;
/*    */   private int endOffset;
/*    */ 
/*    */   public TermVectorOffsetInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   public TermVectorOffsetInfo(int startOffset, int endOffset)
/*    */   {
/* 39 */     this.endOffset = endOffset;
/* 40 */     this.startOffset = startOffset;
/*    */   }
/*    */ 
/*    */   public int getEndOffset()
/*    */   {
/* 48 */     return this.endOffset;
/*    */   }
/*    */ 
/*    */   public void setEndOffset(int endOffset) {
/* 52 */     this.endOffset = endOffset;
/*    */   }
/*    */ 
/*    */   public int getStartOffset()
/*    */   {
/* 61 */     return this.startOffset;
/*    */   }
/*    */ 
/*    */   public void setStartOffset(int startOffset) {
/* 65 */     this.startOffset = startOffset;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 75 */     if (this == o) return true;
/* 76 */     if (!(o instanceof TermVectorOffsetInfo)) return false;
/*    */ 
/* 78 */     TermVectorOffsetInfo termVectorOffsetInfo = (TermVectorOffsetInfo)o;
/*    */ 
/* 80 */     if (this.endOffset != termVectorOffsetInfo.endOffset) return false;
/* 81 */     return this.startOffset == termVectorOffsetInfo.startOffset;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 89 */     int result = this.startOffset;
/* 90 */     result = 29 * result + this.endOffset;
/* 91 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorOffsetInfo
 * JD-Core Version:    0.6.0
 */