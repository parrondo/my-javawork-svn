/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.SortedSet;
/*    */ import java.util.TreeSet;
/*    */ 
/*    */ public class FieldSortedTermVectorMapper extends TermVectorMapper
/*    */ {
/* 27 */   private Map<String, SortedSet<TermVectorEntry>> fieldToTerms = new HashMap();
/*    */   private SortedSet<TermVectorEntry> currentSet;
/*    */   private String currentField;
/*    */   private Comparator<TermVectorEntry> comparator;
/*    */ 
/*    */   public FieldSortedTermVectorMapper(Comparator<TermVectorEntry> comparator)
/*    */   {
/* 37 */     this(false, false, comparator);
/*    */   }
/*    */ 
/*    */   public FieldSortedTermVectorMapper(boolean ignoringPositions, boolean ignoringOffsets, Comparator<TermVectorEntry> comparator)
/*    */   {
/* 42 */     super(ignoringPositions, ignoringOffsets);
/* 43 */     this.comparator = comparator;
/*    */   }
/*    */ 
/*    */   public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions)
/*    */   {
/* 48 */     TermVectorEntry entry = new TermVectorEntry(this.currentField, term, frequency, offsets, positions);
/* 49 */     this.currentSet.add(entry);
/*    */   }
/*    */ 
/*    */   public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions)
/*    */   {
/* 54 */     this.currentSet = new TreeSet(this.comparator);
/* 55 */     this.currentField = field;
/* 56 */     this.fieldToTerms.put(field, this.currentSet);
/*    */   }
/*    */ 
/*    */   public Map<String, SortedSet<TermVectorEntry>> getFieldToTerms()
/*    */   {
/* 65 */     return this.fieldToTerms;
/*    */   }
/*    */ 
/*    */   public Comparator<TermVectorEntry> getComparator()
/*    */   {
/* 70 */     return this.comparator;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FieldSortedTermVectorMapper
 * JD-Core Version:    0.6.0
 */