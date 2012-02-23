/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ public final class ThreadInterruptedException extends RuntimeException
/*    */ {
/*    */   public ThreadInterruptedException(InterruptedException ie)
/*    */   {
/* 28 */     super(ie);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.ThreadInterruptedException
 * JD-Core Version:    0.6.0
 */