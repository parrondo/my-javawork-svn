/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LogDocMergePolicy extends LogMergePolicy
/*    */ {
/*    */   public static final int DEFAULT_MIN_MERGE_DOCS = 1000;
/*    */ 
/*    */   public LogDocMergePolicy()
/*    */   {
/* 32 */     this.minMergeSize = 1000L;
/*    */ 
/* 36 */     this.maxMergeSize = 9223372036854775807L;
/* 37 */     this.maxMergeSizeForOptimize = 9223372036854775807L;
/*    */   }
/*    */ 
/*    */   protected long size(SegmentInfo info) throws IOException
/*    */   {
/* 42 */     return sizeDocs(info);
/*    */   }
/*    */ 
/*    */   public void setMinMergeDocs(int minMergeDocs)
/*    */   {
/* 55 */     this.minMergeSize = minMergeDocs;
/*    */   }
/*    */ 
/*    */   public int getMinMergeDocs()
/*    */   {
/* 62 */     return (int)this.minMergeSize;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.LogDocMergePolicy
 * JD-Core Version:    0.6.0
 */