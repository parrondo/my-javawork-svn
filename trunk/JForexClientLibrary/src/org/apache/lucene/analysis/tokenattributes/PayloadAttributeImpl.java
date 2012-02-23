/*    */ package org.apache.lucene.analysis.tokenattributes;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.lucene.index.Payload;
/*    */ import org.apache.lucene.util.AttributeImpl;
/*    */ 
/*    */ public class PayloadAttributeImpl extends AttributeImpl
/*    */   implements PayloadAttribute, Cloneable, Serializable
/*    */ {
/*    */   private Payload payload;
/*    */ 
/*    */   public PayloadAttributeImpl()
/*    */   {
/*    */   }
/*    */ 
/*    */   public PayloadAttributeImpl(Payload payload)
/*    */   {
/* 40 */     this.payload = payload;
/*    */   }
/*    */ 
/*    */   public Payload getPayload()
/*    */   {
/* 47 */     return this.payload;
/*    */   }
/*    */ 
/*    */   public void setPayload(Payload payload)
/*    */   {
/* 54 */     this.payload = payload;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 59 */     this.payload = null;
/*    */   }
/*    */ 
/*    */   public Object clone()
/*    */   {
/* 64 */     PayloadAttributeImpl clone = (PayloadAttributeImpl)super.clone();
/* 65 */     if (this.payload != null) {
/* 66 */       clone.payload = ((Payload)this.payload.clone());
/*    */     }
/* 68 */     return clone;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 73 */     if (other == this) {
/* 74 */       return true;
/*    */     }
/*    */ 
/* 77 */     if ((other instanceof Serializable)) {
/* 78 */       PayloadAttributeImpl o = (PayloadAttributeImpl)other;
/* 79 */       if ((o.payload == null) || (this.payload == null)) {
/* 80 */         return (o.payload == null) && (this.payload == null);
/*    */       }
/*    */ 
/* 83 */       return o.payload.equals(this.payload);
/*    */     }
/*    */ 
/* 86 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 91 */     return this.payload == null ? 0 : this.payload.hashCode();
/*    */   }
/*    */ 
/*    */   public void copyTo(AttributeImpl target)
/*    */   {
/* 96 */     PayloadAttribute t = (Serializable)target;
/* 97 */     t.setPayload(this.payload == null ? null : (Payload)this.payload.clone());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.PayloadAttributeImpl
 * JD-Core Version:    0.6.0
 */