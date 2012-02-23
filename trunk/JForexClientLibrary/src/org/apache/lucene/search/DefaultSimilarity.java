/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import org.apache.lucene.index.FieldInvertState;
/*    */ 
/*    */ public class DefaultSimilarity extends Similarity
/*    */ {
/* 74 */   protected boolean discountOverlaps = true;
/*    */ 
/*    */   public float computeNorm(String field, FieldInvertState state)
/*    */   {
/*    */     int numTerms;
/*    */     int numTerms;
/* 36 */     if (this.discountOverlaps)
/* 37 */       numTerms = state.getLength() - state.getNumOverlap();
/*    */     else
/* 39 */       numTerms = state.getLength();
/* 40 */     return state.getBoost() * (float)(1.0D / Math.sqrt(numTerms));
/*    */   }
/*    */ 
/*    */   public float queryNorm(float sumOfSquaredWeights)
/*    */   {
/* 46 */     return (float)(1.0D / Math.sqrt(sumOfSquaredWeights));
/*    */   }
/*    */ 
/*    */   public float tf(float freq)
/*    */   {
/* 52 */     return (float)Math.sqrt(freq);
/*    */   }
/*    */ 
/*    */   public float sloppyFreq(int distance)
/*    */   {
/* 58 */     return 1.0F / (distance + 1);
/*    */   }
/*    */ 
/*    */   public float idf(int docFreq, int numDocs)
/*    */   {
/* 64 */     return (float)(Math.log(numDocs / (docFreq + 1)) + 1.0D);
/*    */   }
/*    */ 
/*    */   public float coord(int overlap, int maxOverlap)
/*    */   {
/* 70 */     return overlap / maxOverlap;
/*    */   }
/*    */ 
/*    */   public void setDiscountOverlaps(boolean v)
/*    */   {
/* 86 */     this.discountOverlaps = v;
/*    */   }
/*    */ 
/*    */   public boolean getDiscountOverlaps()
/*    */   {
/* 91 */     return this.discountOverlaps;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.DefaultSimilarity
 * JD-Core Version:    0.6.0
 */