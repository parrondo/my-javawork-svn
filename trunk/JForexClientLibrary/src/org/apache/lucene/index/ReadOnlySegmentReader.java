/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import org.apache.lucene.util.BitVector;
/*    */ 
/*    */ class ReadOnlySegmentReader extends SegmentReader
/*    */ {
/*    */   static void noWrite()
/*    */   {
/* 23 */     throw new UnsupportedOperationException("This IndexReader cannot make any changes to the index (it was opened with readOnly = true)");
/*    */   }
/*    */ 
/*    */   protected void acquireWriteLock()
/*    */   {
/* 28 */     noWrite();
/*    */   }
/*    */ 
/*    */   public boolean isDeleted(int n)
/*    */   {
/* 34 */     return (this.deletedDocs != null) && (this.deletedDocs.get(n));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ReadOnlySegmentReader
 * JD-Core Version:    0.6.0
 */