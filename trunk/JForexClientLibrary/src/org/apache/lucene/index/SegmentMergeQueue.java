/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.util.PriorityQueue;
/*    */ 
/*    */ final class SegmentMergeQueue extends PriorityQueue<SegmentMergeInfo>
/*    */ {
/*    */   SegmentMergeQueue(int size)
/*    */   {
/* 25 */     initialize(size);
/*    */   }
/*    */ 
/*    */   protected final boolean lessThan(SegmentMergeInfo stiA, SegmentMergeInfo stiB)
/*    */   {
/* 30 */     int comparison = stiA.term.compareTo(stiB.term);
/* 31 */     if (comparison == 0) {
/* 32 */       return stiA.base < stiB.base;
/*    */     }
/* 34 */     return comparison < 0;
/*    */   }
/*    */ 
/*    */   final void close() throws IOException {
/* 38 */     while (top() != null)
/* 39 */       ((SegmentMergeInfo)pop()).close();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentMergeQueue
 * JD-Core Version:    0.6.0
 */