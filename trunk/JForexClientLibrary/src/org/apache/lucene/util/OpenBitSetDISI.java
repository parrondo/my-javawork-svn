/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.search.DocIdSetIterator;
/*    */ 
/*    */ public class OpenBitSetDISI extends OpenBitSet
/*    */ {
/*    */   public OpenBitSetDISI(DocIdSetIterator disi, int maxSize)
/*    */     throws IOException
/*    */   {
/* 31 */     super(maxSize);
/* 32 */     inPlaceOr(disi);
/*    */   }
/*    */ 
/*    */   public OpenBitSetDISI(int maxSize)
/*    */   {
/* 40 */     super(maxSize);
/*    */   }
/*    */ 
/*    */   public void inPlaceOr(DocIdSetIterator disi)
/*    */     throws IOException
/*    */   {
/* 51 */     long size = size();
/*    */     int doc;
/* 52 */     while ((doc = disi.nextDoc()) < size)
/* 53 */       fastSet(doc);
/*    */   }
/*    */ 
/*    */   public void inPlaceAnd(DocIdSetIterator disi)
/*    */     throws IOException
/*    */   {
/* 64 */     int bitSetDoc = nextSetBit(0);
/*    */     int disiDoc;
/* 66 */     while ((bitSetDoc != -1) && ((disiDoc = disi.advance(bitSetDoc)) != 2147483647)) {
/* 67 */       clear(bitSetDoc, disiDoc);
/* 68 */       bitSetDoc = nextSetBit(disiDoc + 1);
/*    */     }
/* 70 */     if (bitSetDoc != -1)
/* 71 */       clear(bitSetDoc, size());
/*    */   }
/*    */ 
/*    */   public void inPlaceNot(DocIdSetIterator disi)
/*    */     throws IOException
/*    */   {
/* 83 */     long size = size();
/*    */     int doc;
/* 84 */     while ((doc = disi.nextDoc()) < size)
/* 85 */       fastClear(doc);
/*    */   }
/*    */ 
/*    */   public void inPlaceXor(DocIdSetIterator disi)
/*    */     throws IOException
/*    */   {
/* 97 */     long size = size();
/*    */     int doc;
/* 98 */     while ((doc = disi.nextDoc()) < size)
/* 99 */       fastFlip(doc);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.OpenBitSetDISI
 * JD-Core Version:    0.6.0
 */