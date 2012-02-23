/*    */ package org.apache.lucene.analysis.tokenattributes;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.lucene.util.AttributeImpl;
/*    */ 
/*    */ public class FlagsAttributeImpl extends AttributeImpl
/*    */   implements FlagsAttribute, Cloneable, Serializable
/*    */ {
/* 30 */   private int flags = 0;
/*    */ 
/*    */   public int getFlags()
/*    */   {
/* 42 */     return this.flags;
/*    */   }
/*    */ 
/*    */   public void setFlags(int flags)
/*    */   {
/* 49 */     this.flags = flags;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 54 */     this.flags = 0;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 59 */     if (this == other) {
/* 60 */       return true;
/*    */     }
/*    */ 
/* 63 */     if ((other instanceof FlagsAttributeImpl)) {
/* 64 */       return ((FlagsAttributeImpl)other).flags == this.flags;
/*    */     }
/*    */ 
/* 67 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 72 */     return this.flags;
/*    */   }
/*    */ 
/*    */   public void copyTo(AttributeImpl target)
/*    */   {
/* 77 */     FlagsAttribute t = (Serializable)target;
/* 78 */     t.setFlags(this.flags);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.FlagsAttributeImpl
 * JD-Core Version:    0.6.0
 */