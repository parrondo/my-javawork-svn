/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import org.apache.lucene.index.FieldInvertState;
/*    */ 
/*    */ @Deprecated
/*    */ public class SimilarityDelegator extends Similarity
/*    */ {
/*    */   private Similarity delegee;
/*    */ 
/*    */   public SimilarityDelegator(Similarity delegee)
/*    */   {
/* 37 */     this.delegee = delegee;
/*    */   }
/*    */ 
/*    */   public float computeNorm(String fieldName, FieldInvertState state)
/*    */   {
/* 42 */     return this.delegee.computeNorm(fieldName, state);
/*    */   }
/*    */ 
/*    */   public float queryNorm(float sumOfSquaredWeights)
/*    */   {
/* 47 */     return this.delegee.queryNorm(sumOfSquaredWeights);
/*    */   }
/*    */ 
/*    */   public float tf(float freq)
/*    */   {
/* 52 */     return this.delegee.tf(freq);
/*    */   }
/*    */ 
/*    */   public float sloppyFreq(int distance)
/*    */   {
/* 57 */     return this.delegee.sloppyFreq(distance);
/*    */   }
/*    */ 
/*    */   public float idf(int docFreq, int numDocs)
/*    */   {
/* 62 */     return this.delegee.idf(docFreq, numDocs);
/*    */   }
/*    */ 
/*    */   public float coord(int overlap, int maxOverlap)
/*    */   {
/* 67 */     return this.delegee.coord(overlap, maxOverlap);
/*    */   }
/*    */ 
/*    */   public float scorePayload(int docId, String fieldName, int start, int end, byte[] payload, int offset, int length)
/*    */   {
/* 72 */     return this.delegee.scorePayload(docId, fieldName, start, end, payload, offset, length);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.SimilarityDelegator
 * JD-Core Version:    0.6.0
 */