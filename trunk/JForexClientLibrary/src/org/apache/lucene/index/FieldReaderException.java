/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ public class FieldReaderException extends RuntimeException
/*    */ {
/*    */   public FieldReaderException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FieldReaderException(Throwable cause)
/*    */   {
/* 47 */     super(cause);
/*    */   }
/*    */ 
/*    */   public FieldReaderException(String message)
/*    */   {
/* 59 */     super(message);
/*    */   }
/*    */ 
/*    */   public FieldReaderException(String message, Throwable cause)
/*    */   {
/* 77 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FieldReaderException
 * JD-Core Version:    0.6.0
 */