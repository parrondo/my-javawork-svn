/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.util.BitSet;
/*    */ import org.apache.lucene.search.DocIdSet;
/*    */ import org.apache.lucene.search.DocIdSetIterator;
/*    */ 
/*    */ public class DocIdBitSet extends DocIdSet
/*    */ {
/*    */   private BitSet bitSet;
/*    */ 
/*    */   public DocIdBitSet(BitSet bitSet)
/*    */   {
/* 31 */     this.bitSet = bitSet;
/*    */   }
/*    */ 
/*    */   public DocIdSetIterator iterator()
/*    */   {
/* 36 */     return new DocIdBitSetIterator(this.bitSet);
/*    */   }
/*    */ 
/*    */   public boolean isCacheable()
/*    */   {
/* 42 */     return true;
/*    */   }
/*    */ 
/*    */   public BitSet getBitSet()
/*    */   {
/* 49 */     return this.bitSet;
/*    */   }
/*    */   private static class DocIdBitSetIterator extends DocIdSetIterator {
/*    */     private int docId;
/*    */     private BitSet bitSet;
/*    */ 
/* 57 */     DocIdBitSetIterator(BitSet bitSet) { this.bitSet = bitSet;
/* 58 */       this.docId = -1;
/*    */     }
/*    */ 
/*    */     public int docID()
/*    */     {
/* 63 */       return this.docId;
/*    */     }
/*    */ 
/*    */     public int nextDoc()
/*    */     {
/* 69 */       int d = this.bitSet.nextSetBit(this.docId + 1);
/*    */ 
/* 71 */       this.docId = (d == -1 ? 2147483647 : d);
/* 72 */       return this.docId;
/*    */     }
/*    */ 
/*    */     public int advance(int target)
/*    */     {
/* 77 */       int d = this.bitSet.nextSetBit(target);
/*    */ 
/* 79 */       this.docId = (d == -1 ? 2147483647 : d);
/* 80 */       return this.docId;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.DocIdBitSet
 * JD-Core Version:    0.6.0
 */