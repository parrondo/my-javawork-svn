/*    */ package com.dukascopy.transport.util;
/*    */ 
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ 
/*    */ public class MD5
/*    */ {
/*    */   public static String getDigest(String s)
/*    */   {
/* 19 */     String result = null;
/*    */     try
/*    */     {
/* 22 */       MessageDigest md = MessageDigest.getInstance("MD5");
/* 23 */       md.update(s.getBytes());
/* 24 */       result = toHexString(md.digest());
/*    */     }
/*    */     catch (NoSuchAlgorithmException nsae)
/*    */     {
/*    */     }
/* 29 */     return result;
/*    */   }
/*    */ 
/*    */   public static String toHexString(byte[] bytes)
/*    */   {
/* 39 */     String hex = "0123456789abcdef";
/* 40 */     StringBuffer sb = new StringBuffer();
/* 41 */     for (byte b : bytes) {
/* 42 */       int hi = 0xFF & (b & 0xF0) >> 4;
/* 43 */       int lo = b & 0xF;
/* 44 */       sb.append("0123456789abcdef".charAt(hi)).append("0123456789abcdef".charAt(lo));
/*    */     }
/* 46 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.util.MD5
 * JD-Core Version:    0.6.0
 */