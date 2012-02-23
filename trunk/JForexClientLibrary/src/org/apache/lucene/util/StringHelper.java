/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public abstract class StringHelper
/*    */ {
/* 35 */   public static StringInterner interner = new SimpleStringInterner(1024, 8);
/*    */ 
/* 69 */   private static Comparator<String> versionComparator = new Comparator() {
/*    */     public int compare(String a, String b) {
/* 71 */       StringTokenizer aTokens = new StringTokenizer(a, ".");
/* 72 */       StringTokenizer bTokens = new StringTokenizer(b, ".");
/*    */ 
/* 74 */       while (aTokens.hasMoreTokens()) {
/* 75 */         int aToken = Integer.parseInt(aTokens.nextToken());
/* 76 */         if (bTokens.hasMoreTokens()) {
/* 77 */           int bToken = Integer.parseInt(bTokens.nextToken());
/* 78 */           if (aToken != bToken) {
/* 79 */             return aToken < bToken ? -1 : 1;
/*    */           }
/*    */ 
/*    */         }
/* 83 */         else if (aToken != 0) {
/* 84 */           return 1;
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/* 90 */       while (bTokens.hasMoreTokens()) {
/* 91 */         if (Integer.parseInt(bTokens.nextToken()) != 0) {
/* 92 */           return -1;
/*    */         }
/*    */       }
/* 95 */       return 0;
/*    */     }
/* 69 */   };
/*    */ 
/*    */   public static String intern(String s)
/*    */   {
/* 39 */     return interner.intern(s);
/*    */   }
/*    */ 
/*    */   public static final int bytesDifference(byte[] bytes1, int len1, byte[] bytes2, int len2)
/*    */   {
/* 51 */     int len = len1 < len2 ? len1 : len2;
/* 52 */     for (int i = 0; i < len; i++)
/* 53 */       if (bytes1[i] != bytes2[i])
/* 54 */         return i;
/* 55 */     return len;
/*    */   }
/*    */ 
/*    */   public static Comparator<String> getVersionComparator()
/*    */   {
/* 66 */     return versionComparator;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.StringHelper
 * JD-Core Version:    0.6.0
 */