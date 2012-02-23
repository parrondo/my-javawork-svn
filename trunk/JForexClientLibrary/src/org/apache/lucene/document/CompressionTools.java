/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.Inflater;
/*     */ import org.apache.lucene.util.UnicodeUtil;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF16Result;
/*     */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*     */ 
/*     */ public class CompressionTools
/*     */ {
/*     */   public static byte[] compress(byte[] value, int offset, int length, int compressionLevel)
/*     */   {
/*  46 */     ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
/*     */ 
/*  48 */     Deflater compressor = new Deflater();
/*     */     try
/*     */     {
/*  51 */       compressor.setLevel(compressionLevel);
/*  52 */       compressor.setInput(value, offset, length);
/*  53 */       compressor.finish();
/*     */ 
/*  56 */       byte[] buf = new byte[1024];
/*  57 */       while (!compressor.finished()) {
/*  58 */         int count = compressor.deflate(buf);
/*  59 */         bos.write(buf, 0, count);
/*     */       }
/*     */     } finally {
/*  62 */       compressor.end();
/*     */     }
/*     */ 
/*  65 */     return bos.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] compress(byte[] value, int offset, int length)
/*     */   {
/*  70 */     return compress(value, offset, length, 9);
/*     */   }
/*     */ 
/*     */   public static byte[] compress(byte[] value)
/*     */   {
/*  75 */     return compress(value, 0, value.length, 9);
/*     */   }
/*     */ 
/*     */   public static byte[] compressString(String value)
/*     */   {
/*  80 */     return compressString(value, 9);
/*     */   }
/*     */ 
/*     */   public static byte[] compressString(String value, int compressionLevel)
/*     */   {
/*  87 */     UnicodeUtil.UTF8Result result = new UnicodeUtil.UTF8Result();
/*  88 */     UnicodeUtil.UTF16toUTF8(value, 0, value.length(), result);
/*  89 */     return compress(result.result, 0, result.length, compressionLevel);
/*     */   }
/*     */ 
/*     */   public static byte[] decompress(byte[] value)
/*     */     throws DataFormatException
/*     */   {
/*  96 */     ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
/*     */ 
/*  98 */     Inflater decompressor = new Inflater();
/*     */     try
/*     */     {
/* 101 */       decompressor.setInput(value);
/*     */ 
/* 104 */       byte[] buf = new byte[1024];
/* 105 */       while (!decompressor.finished()) {
/* 106 */         int count = decompressor.inflate(buf);
/* 107 */         bos.write(buf, 0, count);
/*     */       }
/*     */     } finally {
/* 110 */       decompressor.end();
/*     */     }
/*     */ 
/* 113 */     return bos.toByteArray();
/*     */   }
/*     */ 
/*     */   public static String decompressString(byte[] value)
/*     */     throws DataFormatException
/*     */   {
/* 119 */     UnicodeUtil.UTF16Result result = new UnicodeUtil.UTF16Result();
/* 120 */     byte[] bytes = decompress(value);
/* 121 */     UnicodeUtil.UTF8toUTF16(bytes, 0, bytes.length, result);
/* 122 */     return new String(result.result, 0, result.length);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.CompressionTools
 * JD-Core Version:    0.6.0
 */