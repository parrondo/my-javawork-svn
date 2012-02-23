/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public final class NoMergeScheduler extends MergeScheduler
/*    */ {
/* 35 */   public static final MergeScheduler INSTANCE = new NoMergeScheduler();
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void merge(IndexWriter writer)
/*    */     throws CorruptIndexException, IOException
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.NoMergeScheduler
 * JD-Core Version:    0.6.0
 */