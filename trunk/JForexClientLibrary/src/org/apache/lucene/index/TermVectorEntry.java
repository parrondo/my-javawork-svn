/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ public class TermVectorEntry
/*    */ {
/*    */   private String field;
/*    */   private String term;
/*    */   private int frequency;
/*    */   private TermVectorOffsetInfo[] offsets;
/*    */   int[] positions;
/*    */ 
/*    */   public TermVectorEntry()
/*    */   {
/*    */   }
/*    */ 
/*    */   public TermVectorEntry(String field, String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions)
/*    */   {
/* 34 */     this.field = field;
/* 35 */     this.term = term;
/* 36 */     this.frequency = frequency;
/* 37 */     this.offsets = offsets;
/* 38 */     this.positions = positions;
/*    */   }
/*    */ 
/*    */   public String getField()
/*    */   {
/* 43 */     return this.field;
/*    */   }
/*    */ 
/*    */   public int getFrequency() {
/* 47 */     return this.frequency;
/*    */   }
/*    */ 
/*    */   public TermVectorOffsetInfo[] getOffsets() {
/* 51 */     return this.offsets;
/*    */   }
/*    */ 
/*    */   public int[] getPositions() {
/* 55 */     return this.positions;
/*    */   }
/*    */ 
/*    */   public String getTerm() {
/* 59 */     return this.term;
/*    */   }
/*    */ 
/*    */   void setFrequency(int frequency)
/*    */   {
/* 64 */     this.frequency = frequency;
/*    */   }
/*    */ 
/*    */   void setOffsets(TermVectorOffsetInfo[] offsets) {
/* 68 */     this.offsets = offsets;
/*    */   }
/*    */ 
/*    */   void setPositions(int[] positions) {
/* 72 */     this.positions = positions;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 78 */     if (this == o) return true;
/* 79 */     if ((o == null) || (getClass() != o.getClass())) return false;
/*    */ 
/* 81 */     TermVectorEntry that = (TermVectorEntry)o;
/*    */ 
/* 83 */     return this.term != null ? this.term.equals(that.term) : that.term == null;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 90 */     return this.term != null ? this.term.hashCode() : 0;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 95 */     return "TermVectorEntry{field='" + this.field + '\'' + ", term='" + this.term + '\'' + ", frequency=" + this.frequency + '}';
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorEntry
 * JD-Core Version:    0.6.0
 */