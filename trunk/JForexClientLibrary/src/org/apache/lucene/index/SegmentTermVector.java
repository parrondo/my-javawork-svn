/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ class SegmentTermVector
/*    */   implements TermFreqVector
/*    */ {
/*    */   private String field;
/*    */   private String[] terms;
/*    */   private int[] termFreqs;
/*    */ 
/*    */   SegmentTermVector(String field, String[] terms, int[] termFreqs)
/*    */   {
/* 29 */     this.field = field;
/* 30 */     this.terms = terms;
/* 31 */     this.termFreqs = termFreqs;
/*    */   }
/*    */ 
/*    */   public String getField()
/*    */   {
/* 39 */     return this.field;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 44 */     StringBuilder sb = new StringBuilder();
/* 45 */     sb.append('{');
/* 46 */     sb.append(this.field).append(": ");
/* 47 */     if (this.terms != null) {
/* 48 */       for (int i = 0; i < this.terms.length; i++) {
/* 49 */         if (i > 0) sb.append(", ");
/* 50 */         sb.append(this.terms[i]).append('/').append(this.termFreqs[i]);
/*    */       }
/*    */     }
/* 53 */     sb.append('}');
/*    */ 
/* 55 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   public int size() {
/* 59 */     return this.terms == null ? 0 : this.terms.length;
/*    */   }
/*    */ 
/*    */   public String[] getTerms() {
/* 63 */     return this.terms;
/*    */   }
/*    */ 
/*    */   public int[] getTermFrequencies() {
/* 67 */     return this.termFreqs;
/*    */   }
/*    */ 
/*    */   public int indexOf(String termText) {
/* 71 */     if (this.terms == null)
/* 72 */       return -1;
/* 73 */     int res = Arrays.binarySearch(this.terms, termText);
/* 74 */     return res >= 0 ? res : -1;
/*    */   }
/*    */ 
/*    */   public int[] indexesOf(String[] termNumbers, int start, int len)
/*    */   {
/* 83 */     int[] res = new int[len];
/*    */ 
/* 85 */     for (int i = 0; i < len; i++) {
/* 86 */       res[i] = indexOf(termNumbers[(start + i)]);
/*    */     }
/* 88 */     return res;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentTermVector
 * JD-Core Version:    0.6.0
 */