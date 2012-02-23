/*     */ package com.dukascopy.transport.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ 
/*     */ public class Base64
/*     */ {
/*     */   private static final String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
/*     */ 
/*     */   public static byte[] decode(String encoded)
/*     */   {
/*  20 */     byte[] output = new byte[3];
/*     */ 
/*  23 */     ByteArrayOutputStream data = new ByteArrayOutputStream(encoded.length());
/*     */ 
/*  25 */     int state = 1;
/*  26 */     for (int i = 0; i < encoded.length(); i++)
/*     */     {
/*  29 */       char alpha = encoded.charAt(i);
/*  30 */       if (Character.isWhitespace(alpha))
/*     */         continue;
/*  32 */       byte c;
/*  32 */       if ((alpha >= 'A') && (alpha <= 'Z')) { c = (byte)(alpha - 'A');
/*     */       }
/*     */       else
/*     */       {
/*  33 */         byte c;
/*  33 */         if ((alpha >= 'a') && (alpha <= 'z')) { c = (byte)(26 + (alpha - 'a'));
/*     */         }
/*     */         else
/*     */         {
/*  34 */           byte c;
/*  34 */           if ((alpha >= '0') && (alpha <= '9')) { c = (byte)(52 + (alpha - '0'));
/*     */           }
/*     */           else
/*     */           {
/*  35 */             byte c;
/*  35 */             if (alpha == '+') { c = 62;
/*     */             }
/*     */             else
/*     */             {
/*  36 */               byte c;
/*  36 */               if (alpha == '/') { c = 63; } else {
/*  37 */                 if (alpha == '=') break;
/*  38 */                 return null;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       byte c;
/*  41 */       switch (state) {
/*     */       case 1:
/*  43 */         output[0] = (byte)(c << 2);
/*  44 */         break;
/*     */       case 2:
/*     */         int tmp213_212 = 0;
/*     */         byte[] tmp213_211 = output; tmp213_211[tmp213_212] = (byte)(tmp213_211[tmp213_212] | (byte)(c >>> 4));
/*  47 */         output[1] = (byte)((c & 0xF) << 4);
/*  48 */         break;
/*     */       case 3:
/*     */         int tmp239_238 = 1;
/*     */         byte[] tmp239_237 = output; tmp239_237[tmp239_238] = (byte)(tmp239_237[tmp239_238] | (byte)(c >>> 2));
/*  51 */         output[2] = (byte)((c & 0x3) << 6);
/*  52 */         break;
/*     */       case 4:
/*     */         int tmp265_264 = 2;
/*     */         byte[] tmp265_263 = output; tmp265_263[tmp265_264] = (byte)(tmp265_263[tmp265_264] | c);
/*  55 */         data.write(output, 0, output.length);
/*     */       }
/*     */ 
/*  58 */       state = state < 4 ? state + 1 : 1;
/*     */     }
/*     */ 
/*  61 */     if (i < encoded.length()) {
/*  62 */       switch (state) {
/*     */       case 3:
/*  64 */         data.write(output, 0, 1);
/*  65 */         return (encoded.charAt(i) == '=') && (encoded.charAt(i + 1) == '=') ? data.toByteArray() : null;
/*     */       case 4:
/*  68 */         data.write(output, 0, 2);
/*  69 */         return encoded.charAt(i) == '=' ? data.toByteArray() : null;
/*     */       }
/*  71 */       return null;
/*     */     }
/*  73 */     return state == 1 ? data.toByteArray() : null;
/*     */   }
/*     */ 
/*     */   public static String encode(byte[] data)
/*     */   {
/*  88 */     char[] output = new char[4];
/*  89 */     int state = 1;
/*  90 */     int restbits = 0;
/*  91 */     int chunks = 0;
/*     */ 
/*  93 */     StringBuffer encoded = new StringBuffer();
/*     */ 
/*  95 */     for (int i = 0; i < data.length; i++) {
/*  96 */       int ic = data[i] >= 0 ? data[i] : (data[i] & 0x7F) + 128;
/*  97 */       switch (state) {
/*     */       case 1:
/*  99 */         output[0] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(ic >>> 2);
/* 100 */         restbits = ic & 0x3;
/* 101 */         break;
/*     */       case 2:
/* 103 */         output[1] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 4 | ic >>> 4);
/* 104 */         restbits = ic & 0xF;
/* 105 */         break;
/*     */       case 3:
/* 107 */         output[2] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 2 | ic >>> 6);
/* 108 */         output[3] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(ic & 0x3F);
/* 109 */         encoded.append(output);
/*     */ 
/* 112 */         chunks++;
/* 113 */         if (chunks % 19 != 0) break; encoded.append("\r\n");
/*     */       }
/*     */ 
/* 116 */       state = state < 3 ? state + 1 : 1;
/*     */     }
/*     */ 
/* 120 */     switch (state) {
/*     */     case 2:
/* 122 */       output[1] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 4);
/* 123 */       output[3] = 61; output[2] = 61;
/* 124 */       encoded.append(output);
/* 125 */       break;
/*     */     case 3:
/* 127 */       output[2] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 2);
/* 128 */       output[3] = '=';
/* 129 */       encoded.append(output);
/*     */     }
/*     */ 
/* 133 */     return encoded.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.util.Base64
 * JD-Core Version:    0.6.0
 */