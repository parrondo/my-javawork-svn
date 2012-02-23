/*    */ package org.apache.lucene.search.payloads;
/*    */ 
/*    */ import org.apache.lucene.search.Explanation;
/*    */ 
/*    */ public class AveragePayloadFunction extends PayloadFunction
/*    */ {
/*    */   public float currentScore(int docId, String field, int start, int end, int numPayloadsSeen, float currentScore, float currentPayloadScore)
/*    */   {
/* 32 */     return currentPayloadScore + currentScore;
/*    */   }
/*    */ 
/*    */   public float docScore(int docId, String field, int numPayloadsSeen, float payloadScore)
/*    */   {
/* 37 */     return numPayloadsSeen > 0 ? payloadScore / numPayloadsSeen : 1.0F;
/*    */   }
/*    */ 
/*    */   public Explanation explain(int doc, int numPayloadsSeen, float payloadScore) {
/* 41 */     Explanation payloadBoost = new Explanation();
/* 42 */     float avgPayloadScore = numPayloadsSeen > 0 ? payloadScore / numPayloadsSeen : 1.0F;
/* 43 */     payloadBoost.setValue(avgPayloadScore);
/* 44 */     payloadBoost.setDescription("AveragePayloadFunction(...)");
/* 45 */     return payloadBoost;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 50 */     int prime = 31;
/* 51 */     int result = 1;
/* 52 */     result = 31 * result + getClass().hashCode();
/* 53 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 58 */     if (this == obj)
/* 59 */       return true;
/* 60 */     if (obj == null) {
/* 61 */       return false;
/*    */     }
/* 63 */     return getClass() == obj.getClass();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.payloads.AveragePayloadFunction
 * JD-Core Version:    0.6.0
 */