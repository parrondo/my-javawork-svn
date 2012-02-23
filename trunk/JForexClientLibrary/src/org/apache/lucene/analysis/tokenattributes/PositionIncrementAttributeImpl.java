/*    */ package org.apache.lucene.analysis.tokenattributes;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.lucene.util.AttributeImpl;
/*    */ 
/*    */ public class PositionIncrementAttributeImpl extends AttributeImpl
/*    */   implements PositionIncrementAttribute, Cloneable, Serializable
/*    */ {
/* 50 */   private int positionIncrement = 1;
/*    */ 
/*    */   public void setPositionIncrement(int positionIncrement)
/*    */   {
/* 57 */     if (positionIncrement < 0) {
/* 58 */       throw new IllegalArgumentException("Increment must be zero or greater: " + positionIncrement);
/*    */     }
/* 60 */     this.positionIncrement = positionIncrement;
/*    */   }
/*    */ 
/*    */   public int getPositionIncrement()
/*    */   {
/* 67 */     return this.positionIncrement;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 72 */     this.positionIncrement = 1;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 77 */     if (other == this) {
/* 78 */       return true;
/*    */     }
/*    */ 
/* 81 */     if ((other instanceof PositionIncrementAttributeImpl)) {
/* 82 */       return this.positionIncrement == ((PositionIncrementAttributeImpl)other).positionIncrement;
/*    */     }
/*    */ 
/* 85 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 90 */     return this.positionIncrement;
/*    */   }
/*    */ 
/*    */   public void copyTo(AttributeImpl target)
/*    */   {
/* 95 */     PositionIncrementAttribute t = (Serializable)target;
/* 96 */     t.setPositionIncrement(this.positionIncrement);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.PositionIncrementAttributeImpl
 * JD-Core Version:    0.6.0
 */