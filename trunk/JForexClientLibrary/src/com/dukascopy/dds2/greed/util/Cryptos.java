/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Cryptos
/*     */ {
/*  15 */   private static final Logger LOGGER = LoggerFactory.getLogger(Cryptos.class);
/*     */   private static final char endKey = 'a';
/*     */   private static final int length = 40;
/*     */   private static final String hashKey = "639dfe4c71a0b582";
/*     */   private static final String hashVal = "fb932ec4518d70a6";
/*     */   private static final String SECURE_KEY = "secretto";
/*  26 */   private static final Random random = new Random();
/*     */ 
/*     */   public static String encrypt(String numberToEncode) {
/*  29 */     return encrypt(numberToEncode, 40);
/*     */   }
/*     */ 
/*     */   public static String encrypt(String numberToEncode, int length)
/*     */   {
/*  34 */     String result = null;
/*     */     try {
/*  36 */       result = encodeToSHA1(numberToEncode.concat("secretto"));
/*     */     } catch (NoSuchAlgorithmException e) {
/*  38 */       e.printStackTrace();
/*     */     } catch (UnsupportedEncodingException e) {
/*  40 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  43 */     return result.substring(0, length);
/*     */   }
/*     */ 
/*     */   public static String encrypt(long numberToEncode)
/*     */   {
/*  48 */     String numberAsString = null;
/*     */     try
/*     */     {
/*  51 */       numberAsString = String.valueOf(numberToEncode);
/*     */     } catch (Exception e) {
/*  53 */       LOGGER.error(e.getMessage(), e);
/*  54 */       return "";
/*     */     }
/*     */ 
/*  57 */     if (numberAsString.length() + 2 > 40) return "";
/*     */ 
/*  59 */     int len = "639dfe4c71a0b582".length();
/*  60 */     int max = len - 1;
/*  61 */     Map hashMap = new LinkedHashMap();
/*  62 */     String encryptedText = "";
/*  63 */     char[] openText = new StringBuilder().append(numberAsString).append('a').toString().toCharArray();
/*  64 */     char openKey = "639dfe4c71a0b582".charAt(random.nextInt(max));
/*  65 */     int shift = openKey % len;
/*  66 */     String hashKey = rightShift("639dfe4c71a0b582", shift);
/*     */ 
/*  68 */     for (int j = 0; j < len; j++) {
/*  69 */       hashMap.put(Character.valueOf(hashKey.charAt(j)), Character.valueOf("fb932ec4518d70a6".charAt(j)));
/*     */     }
/*     */ 
/*  72 */     for (char c : openText) {
/*  73 */       encryptedText = new StringBuilder().append(encryptedText).append((Character)hashMap.get(Character.valueOf(c))).toString();
/*     */     }
/*     */ 
/*  76 */     len = 40 - openText.length - 1;
/*  77 */     for (int j = 0; j < len; j++) {
/*  78 */       encryptedText = new StringBuilder().append(encryptedText).append("fb932ec4518d70a6".charAt(random.nextInt(max))).toString();
/*     */     }
/*     */ 
/*  81 */     int i = -1;
/*  82 */     while (i++ < shift) {
/*  83 */       encryptedText = mixEvenChars(encryptedText);
/*  84 */       encryptedText = reverseHalf(encryptedText);
/*  85 */       encryptedText = mixOddChars(encryptedText);
/*     */     }
/*     */ 
/*  88 */     encryptedText = mixEvenChars(new StringBuilder().append(String.valueOf(openKey)).append(encryptedText).toString());
/*     */ 
/*  90 */     return encryptedText;
/*     */   }
/*     */ 
/*     */   private static String rightShift(String startStr, int shiftLength)
/*     */   {
/*  98 */     if ((startStr == null) || (shiftLength < 0)) return null;
/*  99 */     int shift = shiftLength % startStr.length();
/* 100 */     if ((shift == 0) || (shift == startStr.length())) return startStr;
/*     */ 
/* 102 */     int shiftLengthFromStart = startStr.length() - shift;
/*     */ 
/* 104 */     String part1 = startStr.substring(0, shiftLengthFromStart);
/* 105 */     String part2 = startStr.substring(shiftLengthFromStart);
/*     */ 
/* 107 */     return new StringBuilder().append(part2).append(part1).toString();
/*     */   }
/*     */ 
/*     */   private static String mixEvenChars(String startStr)
/*     */   {
/* 113 */     return mix(startStr, true);
/*     */   }
/*     */ 
/*     */   private static String mixOddChars(String startStr)
/*     */   {
/* 118 */     return mix(startStr, false);
/*     */   }
/*     */ 
/*     */   private static String mix(String startStr, boolean even)
/*     */   {
/* 123 */     if (startStr == null) return null;
/* 124 */     if (startStr.length() == 1) return startStr;
/*     */ 
/* 126 */     int substCount = (int)(startStr.length() * 0.5D);
/*     */ 
/* 128 */     StringBuilder copy = new StringBuilder(startStr);
/*     */ 
/* 130 */     int startIndex = 0;
/* 131 */     int endIndex = 0;
/*     */ 
/* 133 */     char chStart = ' ';
/* 134 */     char chEnd = ' ';
/*     */ 
/* 136 */     int mode = even ? 0 : 1;
/*     */ 
/* 138 */     for (int i = 0; i < substCount; i++) {
/* 139 */       if (i % 2 == mode) {
/* 140 */         startIndex = i;
/* 141 */         endIndex = copy.length() - i - 1;
/*     */ 
/* 143 */         chStart = copy.charAt(startIndex);
/* 144 */         chEnd = copy.charAt(endIndex);
/*     */ 
/* 146 */         copy = copy.replace(startIndex, startIndex + 1, Character.toString(chEnd));
/* 147 */         copy = copy.replace(endIndex, endIndex + 1, Character.toString(chStart));
/*     */       }
/*     */     }
/*     */ 
/* 151 */     return copy.toString();
/*     */   }
/*     */ 
/*     */   private static String reverse(String str)
/*     */   {
/* 156 */     return new StringBuffer(str).reverse().toString();
/*     */   }
/*     */ 
/*     */   private static String reverseHalf(String str)
/*     */   {
/* 161 */     if (str == null) return null;
/* 162 */     if (str.length() < 3) return str;
/*     */ 
/* 164 */     int position = (int)(str.length() * 0.5D);
/* 165 */     return new StringBuilder().append(reverse(str.substring(0, position))).append(str.substring(position)).toString();
/*     */   }
/*     */ 
/*     */   private static String encodeToSHA1(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException
/*     */   {
/* 170 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/* 171 */     md.update(string.getBytes("iso-8859-1"), 0, string.length());
/* 172 */     byte[] sha1hash = md.digest();
/* 173 */     return convertToHex(sha1hash);
/*     */   }
/*     */ 
/*     */   private static String convertToHex(byte[] data) {
/* 177 */     StringBuffer buf = new StringBuffer();
/* 178 */     for (byte aData : data) {
/* 179 */       int halfbyte = aData >>> 4 & 0xF;
/* 180 */       int twoHalfs = 0;
/*     */       do {
/* 182 */         if ((0 <= halfbyte) && (halfbyte <= 9))
/* 183 */           buf.append((char)(48 + halfbyte));
/*     */         else
/* 185 */           buf.append((char)(97 + (halfbyte - 10)));
/* 186 */         halfbyte = aData & 0xF;
/* 187 */       }while (twoHalfs++ < 1);
/*     */     }
/* 189 */     return buf.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.Cryptos
 * JD-Core Version:    0.6.0
 */