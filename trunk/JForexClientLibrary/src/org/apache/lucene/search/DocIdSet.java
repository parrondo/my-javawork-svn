/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class DocIdSet
/*    */ {
/* 29 */   public static final DocIdSet EMPTY_DOCIDSET = new DocIdSet()
/*    */   {
/* 31 */     private final DocIdSetIterator iterator = new DocIdSetIterator() {
/*    */       public int advance(int target) throws IOException {
/* 33 */         return 2147483647;
/*    */       }
/* 35 */       public int docID() { return 2147483647; } 
/*    */       public int nextDoc() throws IOException {
/* 37 */         return 2147483647;
/*    */       }
/* 31 */     };
/*    */ 
/*    */     public DocIdSetIterator iterator()
/*    */     {
/* 42 */       return this.iterator;
/*    */     }
/*    */ 
/*    */     public boolean isCacheable()
/*    */     {
/* 47 */       return true;
/*    */     }
/* 29 */   };
/*    */ 
/*    */   public abstract DocIdSetIterator iterator()
/*    */     throws IOException;
/*    */ 
/*    */   public boolean isCacheable()
/*    */   {
/* 65 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.DocIdSet
 * JD-Core Version:    0.6.0
 */