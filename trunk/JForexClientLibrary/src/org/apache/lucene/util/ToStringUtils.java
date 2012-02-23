/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ public final class ToStringUtils
/*    */ {
/*    */   public static String boost(float boost)
/*    */   {
/* 31 */     if (boost != 1.0F)
/* 32 */       return "^" + Float.toString(boost);
/* 33 */     return "";
/*    */   }
/*    */ 
/*    */   public static void byteArray(StringBuilder buffer, byte[] bytes) {
/* 37 */     for (int i = 0; i < bytes.length; i++) {
/* 38 */       buffer.append("b[").append(i).append("]=").append(bytes[i]);
/* 39 */       if (i < bytes.length - 1)
/* 40 */         buffer.append(',');
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.ToStringUtils
 * JD-Core Version:    0.6.0
 */