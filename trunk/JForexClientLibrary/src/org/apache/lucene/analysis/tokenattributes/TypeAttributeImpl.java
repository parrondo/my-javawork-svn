/*    */ package org.apache.lucene.analysis.tokenattributes;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.lucene.util.AttributeImpl;
/*    */ 
/*    */ public class TypeAttributeImpl extends AttributeImpl
/*    */   implements TypeAttribute, Cloneable, Serializable
/*    */ {
/*    */   private String type;
/*    */ 
/*    */   public TypeAttributeImpl()
/*    */   {
/* 31 */     this("word");
/*    */   }
/*    */ 
/*    */   public TypeAttributeImpl(String type) {
/* 35 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public String type()
/*    */   {
/* 40 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(String type)
/*    */   {
/* 46 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 51 */     this.type = "word";
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 56 */     if (other == this) {
/* 57 */       return true;
/*    */     }
/*    */ 
/* 60 */     if ((other instanceof TypeAttributeImpl)) {
/* 61 */       TypeAttributeImpl o = (TypeAttributeImpl)other;
/* 62 */       return this.type == null ? false : o.type == null ? true : this.type.equals(o.type);
/*    */     }
/*    */ 
/* 65 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 70 */     return this.type == null ? 0 : this.type.hashCode();
/*    */   }
/*    */ 
/*    */   public void copyTo(AttributeImpl target)
/*    */   {
/* 75 */     TypeAttribute t = (Serializable)target;
/* 76 */     t.setType(this.type);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.TypeAttributeImpl
 * JD-Core Version:    0.6.0
 */