/*     */ package com.dukascopy.dds2.greed.connector.helpers;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.lang.reflect.Array;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.DoubleBuffer;
/*     */ import java.nio.IntBuffer;
/*     */ import java.nio.LongBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.BitSet;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ 
/*     */ public class FileHelpers
/*     */ {
/*     */   public static final String endOfLine = "\r\n";
/*     */ 
/*     */   public static boolean isHandlerForBinFile(int handler)
/*     */   {
/*  25 */     BitSet bitSet = CommonHelpers.bitSetFromInt(handler);
/*  26 */     return bitSet.get(7);
/*     */   }
/*     */ 
/*     */   public static File getMyHistoryDir() {
/*  30 */     File myDocs = FileSystemView.getFileSystemView().getDefaultDirectory();
/*  31 */     File dir = new File(new StringBuilder().append(myDocs.getPath()).append(File.separator).append("My Strategies/History").toString());
/*  32 */     return dir;
/*     */   }
/*     */ 
/*     */   public static File getMyStrategiesDir() {
/*  36 */     File myDocs = FileSystemView.getFileSystemView().getDefaultDirectory();
/*  37 */     File dir = new File(new StringBuilder().append(myDocs.getPath()).append(File.separator).append("My Strategies").toString());
/*  38 */     return dir;
/*     */   }
/*     */ 
/*     */   public static File getFilesForHistoryDir()
/*     */   {
/*  44 */     File dir = new File(new StringBuilder().append(getMyHistoryDir().getAbsolutePath()).append(File.separator).append("files").toString());
/*  45 */     if (!dir.exists()) {
/*  46 */       dir.mkdirs();
/*     */     }
/*  48 */     return dir;
/*     */   }
/*     */ 
/*     */   public static File getFilesForStrategiesDir() {
/*  52 */     File dir = new File(new StringBuilder().append(getMyStrategiesDir().getAbsolutePath()).append(File.separator).append("files").toString());
/*  53 */     if (!dir.exists()) {
/*  54 */       dir.mkdirs();
/*     */     }
/*  56 */     return dir;
/*     */   }
/*     */ 
/*     */   public static int FileWriteArray(RandomAccessFile accessFile, Object array, int start, int count)
/*     */   {
/*  63 */     int result = 0;
/*  64 */     int elementSize = 0;
/*  65 */     int arraySize = 0;
/*  66 */     Class elementType = null;
/*  67 */     if (accessFile != null) {
/*  68 */       if (array.getClass().isArray()) {
/*  69 */         elementSize = ArrayHelpers.ArrayElementSize(array);
/*  70 */         arraySize = Array.getLength(array);
/*  71 */         elementType = ArrayHelpers.ArrayElementType(array);
/*  72 */         if (arraySize < start + count)
/*  73 */           CommonHelpers.notFunctionSupported();
/*     */       }
/*     */       else {
/*  76 */         CommonHelpers.notFunctionSupported();
/*     */       }
/*     */       try {
/*  79 */         ByteBuffer byteBuffer = null;
/*  80 */         if (!elementType.equals(String.class)) {
/*  81 */           byteBuffer = ByteBuffer.allocate(count * elementSize / 8);
/*  82 */           if (elementType.equals(Integer.TYPE)) {
/*  83 */             int[] arrayToWrite = new int[count];
/*  84 */             IntBuffer buffer = byteBuffer.asIntBuffer();
/*  85 */             System.arraycopy(array, start, arrayToWrite, 0, count);
/*  86 */             buffer.put((int[])arrayToWrite);
/*  87 */           } else if (elementType.equals(Long.TYPE)) {
/*  88 */             long[] arrayToWrite = new long[count];
/*  89 */             System.arraycopy(array, start, arrayToWrite, 0, count);
/*  90 */             LongBuffer buffer = byteBuffer.asLongBuffer();
/*  91 */             buffer.put((long[])arrayToWrite);
/*  92 */           } else if (elementType.equals(Double.TYPE)) {
/*  93 */             double[] arrayToWrite = new double[count];
/*  94 */             System.arraycopy(array, start, arrayToWrite, 0, count);
/*  95 */             DoubleBuffer buffer = byteBuffer.asDoubleBuffer();
/*  96 */             buffer.put((double[])arrayToWrite);
/*  97 */           } else if (elementType.equals(Byte.TYPE)) {
/*  98 */             byte[] arrayToWrite = new byte[count];
/*  99 */             System.arraycopy(array, start, arrayToWrite, 0, count);
/* 100 */             byteBuffer.put((byte[])arrayToWrite);
/* 101 */           } else if (elementType.equals(Character.TYPE)) {
/* 102 */             char[] arrayToWrite = new char[count];
/* 103 */             System.arraycopy(array, start, arrayToWrite, 0, count);
/* 104 */             CharBuffer buffer = byteBuffer.asCharBuffer();
/* 105 */             buffer.put((char[])arrayToWrite);
/*     */           }
/* 107 */           if (byteBuffer.array() != null) {
/* 108 */             accessFile.write(byteBuffer.array());
/* 109 */             result = byteBuffer.array().length;
/*     */           }
/*     */         } else {
/* 112 */           for (int i = 0; i < count; i++) {
/* 113 */             String str = (String)Array.get(array, i + start);
/* 114 */             accessFile.write(str.getBytes());
/* 115 */             accessFile.write("\r\n".getBytes());
/* 116 */             result += str.length() + 2;
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/* 120 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 123 */     return result;
/*     */   }
/*     */ 
/*     */   public static int FileReadArray(RandomAccessFile accessFile, Object array, int start, int count) {
/* 127 */     int result = 0;
/* 128 */     int elementSize = 0;
/* 129 */     int arraySize = 0;
/* 130 */     Class elementType = null;
/* 131 */     if (accessFile != null) {
/* 132 */       if (array.getClass().isArray()) {
/* 133 */         elementSize = ArrayHelpers.ArrayElementSize(array);
/* 134 */         arraySize = Array.getLength(array);
/* 135 */         elementType = ArrayHelpers.ArrayElementType(array);
/* 136 */         if (arraySize < start + count)
/* 137 */           CommonHelpers.notFunctionSupported();
/*     */       }
/*     */       else {
/* 140 */         CommonHelpers.notFunctionSupported();
/*     */       }
/*     */       try {
/* 143 */         if (!elementType.equals(String.class))
/*     */         {
/* 145 */           ByteBuffer byteBuffer = ByteBuffer.allocate(arraySize * elementSize / 8);
/* 146 */           accessFile.getChannel().read(byteBuffer);
/* 147 */           byteBuffer.rewind();
/*     */ 
/* 149 */           if (elementType.equals(Integer.TYPE)) {
/* 150 */             int[] dst = new int[arraySize];
/* 151 */             IntBuffer buffer = byteBuffer.asIntBuffer();
/* 152 */             buffer.rewind();
/* 153 */             buffer.get(dst);
/* 154 */             System.arraycopy(dst, start, array, start, count);
/* 155 */           } else if (elementType.equals(Long.TYPE)) {
/* 156 */             long[] dst = new long[arraySize];
/* 157 */             LongBuffer buffer = byteBuffer.asLongBuffer();
/* 158 */             buffer.rewind();
/* 159 */             buffer.get(dst);
/* 160 */             System.arraycopy(dst, start, array, start, count);
/* 161 */           } else if (elementType.equals(Double.TYPE)) {
/* 162 */             double[] dst = new double[arraySize];
/* 163 */             DoubleBuffer buffer = byteBuffer.asDoubleBuffer();
/* 164 */             buffer.rewind();
/* 165 */             buffer.get(dst);
/* 166 */             System.arraycopy(dst, start, array, start, count);
/* 167 */           } else if (elementType.equals(Byte.TYPE)) {
/* 168 */             System.arraycopy(byteBuffer.array(), start, array, start, count);
/* 169 */           } else if (elementType.equals(Character.TYPE)) {
/* 170 */             char[] dst = new char[arraySize];
/* 171 */             CharBuffer buffer = byteBuffer.asCharBuffer();
/* 172 */             buffer.rewind();
/* 173 */             buffer.get(dst);
/* 174 */             System.arraycopy(dst, start, array, start, count);
/*     */           }
/* 176 */           result = byteBuffer.array().length;
/*     */         } else {
/* 178 */           for (int i = 0; i < count + 1; i++) {
/* 179 */             String str = accessFile.readLine();
/* 180 */             if (i > start - 1) {
/* 181 */               Array.set(array, i, str);
/* 182 */               result += str.length();
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/* 187 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 190 */     return result;
/*     */   }
/*     */   public static int FileWriteString(RandomAccessFile accessFile, String value, int length, Character delimiter) throws IOException {
/* 193 */     int result = -1;
/* 194 */     byte[] buff = null;
/* 195 */     if (length > 0) {
/* 196 */       buff = new byte[length];
/* 197 */       System.arraycopy(value, 0, buff, 0, length);
/*     */     } else {
/* 199 */       buff = value.getBytes();
/*     */     }
/* 201 */     accessFile.write(buff);
/* 202 */     result = buff.length;
/* 203 */     return result;
/*     */   }
/*     */ 
/*     */   public static String FileReadString(RandomAccessFile accessFile, int length, Character delimiter) {
/* 207 */     StringBuilder rc = new StringBuilder();
/* 208 */     if (delimiter == null) {
/*     */       try
/*     */       {
/* 211 */         byte[] buff = new byte[length];
/* 212 */         accessFile.read(buff);
/* 213 */         rc.append(buff);
/*     */       } catch (Exception e) {
/* 215 */         e.printStackTrace();
/*     */       }
/*     */ 
/*     */     }
/* 219 */     else if (accessFile != null) {
/*     */       try {
/* 221 */         char c = 65535;
/*     */         do {
/* 223 */           if (c != 65535) {
/* 224 */             rc.append(c);
/*     */           }
/* 226 */           c = (char)accessFile.read();
/*     */ 
/* 228 */           if ((c == delimiter.charValue()) || (c == '\n')) break; 
/* 228 */         }while (c != 65535);
/*     */       } catch (Exception e) {
/* 230 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/* 234 */     return rc.toString().trim();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.helpers.FileHelpers
 * JD-Core Version:    0.6.0
 */