/*    */ package org.apache.lucene.analysis.tokenattributes;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.lucene.util.AttributeImpl;
/*    */ 
/*    */ public class OffsetAttributeImpl extends AttributeImpl
/*    */   implements OffsetAttribute, Cloneable, Serializable
/*    */ {
/*    */   private int startOffset;
/*    */   private int endOffset;
/*    */ 
/*    */   public int startOffset()
/*    */   {
/* 38 */     return this.startOffset;
/*    */   }
/*    */ 
/*    */   public void setOffset(int startOffset, int endOffset)
/*    */   {
/* 45 */     this.startOffset = startOffset;
/* 46 */     this.endOffset = endOffset;
/*    */   }
/*    */ 
/*    */   public int endOffset()
/*    */   {
/* 54 */     return this.endOffset;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 60 */     this.startOffset = 0;
/* 61 */     this.endOffset = 0;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 66 */     if (other == this) {
/* 67 */       return true;
/*    */     }
/*    */ 
/* 70 */     if ((other instanceof OffsetAttributeImpl)) {
/* 71 */       OffsetAttributeImpl o = (OffsetAttributeImpl)other;
/* 72 */       return (o.startOffset == this.startOffset) && (o.endOffset == this.endOffset);
/*    */     }
/*    */ 
/* 75 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 80 */     int code = this.startOffset;
/* 81 */     code = code * 31 + this.endOffset;
/* 82 */     return code;
/*    */   }
/*    */ 
/*    */   public void copyTo(AttributeImpl target)
/*    */   {
/* 87 */     OffsetAttribute t = (Serializable)target;
/* 88 */     t.setOffset(this.startOffset, this.endOffset);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.OffsetAttributeImpl
 * JD-Core Version:    0.6.0
 */