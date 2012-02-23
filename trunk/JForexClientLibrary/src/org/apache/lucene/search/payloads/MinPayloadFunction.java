/*    */ package org.apache.lucene.search.payloads;
/*    */ 
/*    */ import org.apache.lucene.search.Explanation;
/*    */ 
/*    */ public class MinPayloadFunction extends PayloadFunction
/*    */ {
/*    */   public float currentScore(int docId, String field, int start, int end, int numPayloadsSeen, float currentScore, float currentPayloadScore)
/*    */   {
/* 29 */     if (numPayloadsSeen == 0) {
/* 30 */       return currentPayloadScore;
/*    */     }
/* 32 */     return Math.min(currentPayloadScore, currentScore);
/*    */   }
/*    */ 
/*    */   public float docScore(int docId, String field, int numPayloadsSeen, float payloadScore)
/*    */   {
/* 38 */     return numPayloadsSeen > 0 ? payloadScore : 1.0F;
/*    */   }
/*    */ 
/*    */   public Explanation explain(int doc, int numPayloadsSeen, float payloadScore)
/*    */   {
/* 43 */     Explanation expl = new Explanation();
/* 44 */     float minPayloadScore = numPayloadsSeen > 0 ? payloadScore : 1.0F;
/* 45 */     expl.setValue(minPayloadScore);
/* 46 */     expl.setDescription("MinPayloadFunction(...)");
/* 47 */     return expl;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 51 */     int prime = 31;
/* 52 */     int result = 1;
/* 53 */     result = 31 * result + getClass().hashCode();
/* 54 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 59 */     if (this == obj)
/* 60 */       return true;
/* 61 */     if (obj == null) {
/* 62 */       return false;
/*    */     }
/* 64 */     return getClass() == obj.getClass();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.payloads.MinPayloadFunction
 * JD-Core Version:    0.6.0
 */