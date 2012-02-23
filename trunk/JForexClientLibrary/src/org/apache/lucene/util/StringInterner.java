/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ public class StringInterner
/*    */ {
/*    */   public String intern(String s)
/*    */   {
/* 30 */     return s.intern();
/*    */   }
/*    */ 
/*    */   public String intern(char[] arr, int offset, int len)
/*    */   {
/* 35 */     return intern(new String(arr, offset, len));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.StringInterner
 * JD-Core Version:    0.6.0
 */