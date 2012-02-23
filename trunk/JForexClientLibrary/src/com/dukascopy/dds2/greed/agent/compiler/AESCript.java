/*    */ package com.dukascopy.dds2.greed.agent.compiler;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import javax.crypto.Cipher;
/*    */ import javax.crypto.KeyGenerator;
/*    */ import javax.crypto.SecretKey;
/*    */ import javax.crypto.spec.SecretKeySpec;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class AESCript
/*    */ {
/* 21 */   private static final Logger LOGGER = LoggerFactory.getLogger(AESCript.class);
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/*    */     try {
/* 26 */       byte[] key = key();
/* 27 */       byte[] enc = encript("".getBytes(), key);
/* 28 */       byte[] dec = decript(enc, key);
/* 29 */       System.out.println(new String(dec));
/*    */     } catch (Exception e) {
/* 31 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static String asHex(byte[] buf)
/*    */   {
/* 43 */     StringBuffer strbuf = new StringBuffer(buf.length * 2);
/*    */ 
/* 46 */     for (int i = 0; i < buf.length; i++) {
/* 47 */       if ((buf[i] & 0xFF) < 16) {
/* 48 */         strbuf.append("0");
/*    */       }
/* 50 */       strbuf.append(Long.toString(buf[i] & 0xFF, 16));
/*    */     }
/*    */ 
/* 53 */     return strbuf.toString();
/*    */   }
/*    */ 
/*    */   public static byte[] key() throws Exception {
/* 57 */     KeyGenerator kgen = KeyGenerator.getInstance("AES");
/* 58 */     kgen.init(128);
/*    */ 
/* 60 */     SecretKey skey = kgen.generateKey();
/* 61 */     return skey.getEncoded();
/*    */   }
/*    */ 
/*    */   public static byte[] encript(byte[] forEnc, byte[] key) throws Exception
/*    */   {
/* 66 */     SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/*    */ 
/* 68 */     Cipher cipher = Cipher.getInstance("AES");
/* 69 */     cipher.init(1, skeySpec);
/* 70 */     return cipher.doFinal(forEnc);
/*    */   }
/*    */ 
/*    */   public static byte[] decript(byte[] encrypted, byte[] key) throws GeneralSecurityException
/*    */   {
/* 75 */     Cipher cipher = Cipher.getInstance("AES");
/* 76 */     SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/* 77 */     cipher.init(2, skeySpec);
/* 78 */     return cipher.doFinal(encrypted);
/*    */   }
/*    */ 
/*    */   public static String digest(byte[] in)
/*    */   {
/* 84 */     MessageDigest sha512 = null;
/*    */     try {
/* 86 */       sha512 = MessageDigest.getInstance("SHA-512");
/*    */     } catch (NoSuchAlgorithmException e) {
/* 88 */       LOGGER.error(e.getMessage(), e);
/* 89 */       return null;
/*    */     }
/*    */ 
/* 92 */     sha512.update(in, 0, in.length);
/*    */ 
/* 94 */     byte[] shadigest = sha512.digest();
/*    */ 
/* 96 */     return Base32.encode(shadigest);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.compiler.AESCript
 * JD-Core Version:    0.6.0
 */