/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ 
/*    */ public final class NoDeletionPolicy
/*    */   implements IndexDeletionPolicy
/*    */ {
/* 31 */   public static final IndexDeletionPolicy INSTANCE = new NoDeletionPolicy();
/*    */ 
/*    */   public void onCommit(List<? extends IndexCommit> commits)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onInit(List<? extends IndexCommit> commits)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.NoDeletionPolicy
 * JD-Core Version:    0.6.0
 */