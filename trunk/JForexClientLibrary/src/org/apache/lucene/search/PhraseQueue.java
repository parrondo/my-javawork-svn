/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import org.apache.lucene.util.PriorityQueue;
/*    */ 
/*    */ final class PhraseQueue extends PriorityQueue<PhrasePositions>
/*    */ {
/*    */   PhraseQueue(int size)
/*    */   {
/* 24 */     initialize(size);
/*    */   }
/*    */ 
/*    */   protected final boolean lessThan(PhrasePositions pp1, PhrasePositions pp2)
/*    */   {
/* 29 */     if (pp1.doc == pp2.doc) {
/* 30 */       if (pp1.position == pp2.position)
/*    */       {
/* 33 */         if (pp1.offset == pp2.offset) {
/* 34 */           return pp1.ord < pp2.ord;
/*    */         }
/* 36 */         return pp1.offset < pp2.offset;
/*    */       }
/*    */ 
/* 39 */       return pp1.position < pp2.position;
/*    */     }
/*    */ 
/* 42 */     return pp1.doc < pp2.doc;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PhraseQueue
 * JD-Core Version:    0.6.0
 */