/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class TermVectorEntryFreqSortedComparator
/*    */   implements Comparator<TermVectorEntry>
/*    */ {
/*    */   public int compare(TermVectorEntry entry, TermVectorEntry entry1)
/*    */   {
/* 28 */     int result = 0;
/* 29 */     result = entry1.getFrequency() - entry.getFrequency();
/* 30 */     if (result == 0)
/*    */     {
/* 32 */       result = entry.getTerm().compareTo(entry1.getTerm());
/* 33 */       if (result == 0)
/*    */       {
/* 35 */         result = entry.getField().compareTo(entry1.getField());
/*    */       }
/*    */     }
/* 38 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorEntryFreqSortedComparator
 * JD-Core Version:    0.6.0
 */