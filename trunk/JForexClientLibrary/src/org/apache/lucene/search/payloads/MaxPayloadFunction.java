/*    */ package org.apache.lucene.search.payloads;
/*    */ 
/*    */ import org.apache.lucene.search.Explanation;
/*    */ 
/*    */ public class MaxPayloadFunction extends PayloadFunction
/*    */ {
/*    */   public float currentScore(int docId, String field, int start, int end, int numPayloadsSeen, float currentScore, float currentPayloadScore)
/*    */   {
/* 31 */     if (numPayloadsSeen == 0) {
/* 32 */       return currentPayloadScore;
/*    */     }
/* 34 */     return Math.max(currentPayloadScore, currentScore);
/*    */   }
/*    */ 
/*    */   public float docScore(int docId, String field, int numPayloadsSeen, float payloadScore)
/*    */   {
/* 40 */     return numPayloadsSeen > 0 ? payloadScore : 1.0F;
/*    */   }
/*    */ 
/*    */   public Explanation explain(int doc, int numPayloadsSeen, float payloadScore)
/*    */   {
/* 45 */     Explanation expl = new Explanation();
/* 46 */     float maxPayloadScore = numPayloadsSeen > 0 ? payloadScore : 1.0F;
/* 47 */     expl.setValue(maxPayloadScore);
/* 48 */     expl.setDescription("MaxPayloadFunction(...)");
/* 49 */     return expl;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 53 */     int prime = 31;
/* 54 */     int result = 1;
/* 55 */     result = 31 * result + getClass().hashCode();
/* 56 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 61 */     if (this == obj)
/* 62 */       return true;
/* 63 */     if (obj == null) {
/* 64 */       return false;
/*    */     }
/* 66 */     return getClass() == obj.getClass();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.payloads.MaxPayloadFunction
 * JD-Core Version:    0.6.0
 */