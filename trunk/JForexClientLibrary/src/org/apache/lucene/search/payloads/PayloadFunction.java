/*    */ package org.apache.lucene.search.payloads;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.lucene.search.Explanation;
/*    */ 
/*    */ public abstract class PayloadFunction
/*    */   implements Serializable
/*    */ {
/*    */   public abstract float currentScore(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2);
/*    */ 
/*    */   public abstract float docScore(int paramInt1, String paramString, int paramInt2, float paramFloat);
/*    */ 
/*    */   public Explanation explain(int docId, int numPayloadsSeen, float payloadScore)
/*    */   {
/* 60 */     Explanation result = new Explanation();
/* 61 */     result.setDescription("Unimpl Payload Function Explain");
/* 62 */     result.setValue(1.0F);
/* 63 */     return result;
/*    */   }
/*    */ 
/*    */   public abstract int hashCode();
/*    */ 
/*    */   public abstract boolean equals(Object paramObject);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.payloads.PayloadFunction
 * JD-Core Version:    0.6.0
 */