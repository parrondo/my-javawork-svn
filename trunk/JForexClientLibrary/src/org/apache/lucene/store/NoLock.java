/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ class NoLock extends Lock
/*    */ {
/*    */   public boolean obtain()
/*    */     throws IOException
/*    */   {
/* 61 */     return true;
/*    */   }
/*    */ 
/*    */   public void release()
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean isLocked()
/*    */   {
/* 70 */     return false;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 75 */     return "NoLock";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.NoLock
 * JD-Core Version:    0.6.0
 */