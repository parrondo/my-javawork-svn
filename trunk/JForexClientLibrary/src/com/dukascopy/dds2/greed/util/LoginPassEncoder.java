/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ 
/*    */ public class LoginPassEncoder
/*    */ {
/*    */   private static final String CHARSET_ISO = "ISO-8859-1";
/*    */   private static final String ALGORITHM_MD5 = "MD5";
/*    */ 
/*    */   public static String encode(String login, String password)
/*    */   {
/* 17 */     char[] loginCharsEncoded = shiftByPosition(login);
/* 18 */     char[] loginEncoded = toHexString(loginCharsEncoded);
/*    */ 
/* 21 */     char[] passEncoded = encodePassword(password);
/*    */ 
/* 24 */     char[] result = new char[loginEncoded.length + passEncoded.length];
/*    */ 
/* 26 */     int i = 0;
/* 27 */     int j = 0;
/* 28 */     while (i < Math.max(loginEncoded.length, passEncoded.length)) {
/* 29 */       if (i < passEncoded.length) {
/* 30 */         result[j] = passEncoded[i];
/* 31 */         j++;
/*    */       }
/* 33 */       if (i < loginEncoded.length) {
/* 34 */         result[j] = loginEncoded[i];
/* 35 */         j++;
/*    */       }
/* 37 */       i++;
/*    */     }
/* 39 */     return new String(result);
/*    */   }
/*    */ 
/*    */   public static char[] encodePassword(String password) {
/* 43 */     char[] passCharsEncoded = shiftByPosition(password);
/*    */ 
/* 46 */     MessageDigest md5 = null;
/*    */     try {
/* 48 */       md5 = MessageDigest.getInstance("MD5");
/*    */     } catch (NoSuchAlgorithmException e) {
/* 50 */       throw new IllegalStateException(e.getMessage());
/*    */     }
/*    */ 
/* 54 */     char[] encodedChars = new char[0];
/*    */     try {
/* 56 */       byte[] encodedBytes = md5.digest(new String(passCharsEncoded).getBytes("ISO-8859-1"));
/* 57 */       encodedChars = new String(encodedBytes, "ISO-8859-1").toCharArray();
/*    */     } catch (UnsupportedEncodingException e) {
/* 59 */       throw new IllegalStateException(e.getMessage());
/*    */     }
/*    */ 
/* 63 */     return toHexString(encodedChars);
/*    */   }
/*    */ 
/*    */   private static char[] shiftByPosition(String password)
/*    */   {
/* 72 */     char[] passChars = password.toCharArray();
/* 73 */     char[] passCharsEncoded = new char[passChars.length];
/* 74 */     for (int i = 0; i < passChars.length; i++) {
/* 75 */       passCharsEncoded[i] = (char)(passChars[i] + i);
/*    */     }
/* 77 */     return passCharsEncoded;
/*    */   }
/*    */ 
/*    */   public static char[] toHexString(char[] chars)
/*    */   {
/* 86 */     String loginStr = "";
/* 87 */     String charHexStr = "";
/* 88 */     for (int i = 0; i < chars.length; i++) {
/* 89 */       charHexStr = Integer.toHexString(chars[i]);
/* 90 */       if (charHexStr.length() == 1) {
/* 91 */         charHexStr = '0' + charHexStr;
/*    */       }
/* 93 */       loginStr = loginStr + charHexStr;
/*    */     }
/* 95 */     return loginStr.toCharArray();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.LoginPassEncoder
 * JD-Core Version:    0.6.0
 */