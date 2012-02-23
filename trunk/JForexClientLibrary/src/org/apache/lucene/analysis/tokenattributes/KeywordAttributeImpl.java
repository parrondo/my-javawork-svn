/*    */ package org.apache.lucene.analysis.tokenattributes;
/*    */ 
/*    */ import org.apache.lucene.util.AttributeImpl;
/*    */ 
/*    */ public final class KeywordAttributeImpl extends AttributeImpl
/*    */   implements KeywordAttribute
/*    */ {
/*    */   private boolean keyword;
/*    */ 
/*    */   public void clear()
/*    */   {
/* 36 */     this.keyword = false;
/*    */   }
/*    */ 
/*    */   public void copyTo(AttributeImpl target)
/*    */   {
/* 41 */     KeywordAttribute attr = (KeywordAttribute)target;
/* 42 */     attr.setKeyword(this.keyword);
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 47 */     return this.keyword ? 31 : 37;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 52 */     if (this == obj)
/* 53 */       return true;
/* 54 */     if (getClass() != obj.getClass())
/* 55 */       return false;
/* 56 */     KeywordAttributeImpl other = (KeywordAttributeImpl)obj;
/* 57 */     return this.keyword == other.keyword;
/*    */   }
/*    */ 
/*    */   public boolean isKeyword()
/*    */   {
/* 68 */     return this.keyword;
/*    */   }
/*    */ 
/*    */   public void setKeyword(boolean isKeyword)
/*    */   {
/* 79 */     this.keyword = isKeyword;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.KeywordAttributeImpl
 * JD-Core Version:    0.6.0
 */