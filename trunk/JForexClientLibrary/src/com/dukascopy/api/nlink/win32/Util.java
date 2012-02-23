/*     */ package com.dukascopy.api.nlink.win32;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class Util
/*     */ {
/*     */   public static <T> T ptrToStruct(int ptr, Class<T> structclass)
/*     */   {
/*  15 */     Object o = null;
/*     */     try {
/*  17 */       o = structclass.newInstance();
/*     */     } catch (Exception e) {
/*  19 */       e.printStackTrace();
/*  20 */       return null;
/*     */     }
/*  22 */     int len = getStructSize(o.getClass());
/*  23 */     PtrToStruct(ptr, o, len);
/*  24 */     return o;
/*     */   }
/*     */ 
/*     */   public static void ptrToStruct(int ptr, Object o) {
/*  28 */     int len = getStructSize(o.getClass());
/*     */ 
/*  30 */     PtrToStruct(ptr, o, len);
/*     */   }
/*     */ 
/*     */   public static String ptrToString(int ptr) {
/*  34 */     if (ptr == 0)
/*  35 */       return null;
/*  36 */     return PtrToString(ptr);
/*     */   }
/*     */ 
/*     */   public static String ptrToStringAnsi(int ptr) {
/*  40 */     if (ptr == 0)
/*  41 */       return null;
/*  42 */     return PtrToStringAnsi(ptr);
/*     */   }
/*     */ 
/*     */   public static void free(int ptr) {
/*  46 */     Free(ptr);
/*     */   }
/*     */ 
/*     */   public static void freeCoTaskMem(int ptr) {
/*  50 */     FreeCoTaskMem(ptr);
/*     */   }
/*     */ 
/*     */   public static int stringToPtr(String s)
/*     */   {
/*  55 */     if (s == null)
/*  56 */       return 0;
/*  57 */     return StringToPtr(s);
/*     */   }
/*     */ 
/*     */   public static int stringToPtrAnsi(String s) {
/*  61 */     if (s == null)
/*  62 */       return 0;
/*  63 */     return StringToPtrAnsi(s);
/*     */   }
/*     */ 
/*     */   public static int sizeOfChar() {
/*  67 */     return 1;
/*     */   }
/*     */ 
/*     */   public static int sizeOfPointer() {
/*  71 */     return 4;
/*     */   }
/*     */ 
/*     */   public static int sizeOfWideChar() {
/*  75 */     return 2;
/*     */   }
/*     */ 
/*     */   public static int getStructSize(Object struct) {
/*  79 */     return getStructSize(struct.getClass());
/*     */   }
/*     */ 
/*     */   public static int getStructSize(Class structClass) {
/*     */     try {
/*  84 */       return com.dukascopy.api.nlink.win32.engine.Util.getStructSize(structClass);
/*     */     } catch (IllegalArgumentException e) {
/*  86 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/*  88 */       e.printStackTrace();
/*     */     } catch (IllegalAccessException e) {
/*  90 */       e.printStackTrace();
/*     */     }
/*  92 */     return 0;
/*     */   }
/*     */ 
/*     */   public static int byteArrayToPtr(byte[] ba) {
/*  96 */     if (ba == null) {
/*  97 */       return 0;
/*     */     }
/*  99 */     return ByteArrayToPtr(ba);
/*     */   }
/*     */ 
/*     */   public static byte[] ptrToByteArray(int ptr, int numelements) {
/* 103 */     if (ptr == 0) {
/* 104 */       return null;
/*     */     }
/* 106 */     return PtrToByteArray(ptr, numelements);
/*     */   }
/*     */ 
/*     */   public static int getWindowHandle(Component component) {
/* 110 */     return GetWindowHandle(component);
/*     */   }
/*     */ 
/*     */   public static int getLastError() {
/* 114 */     return GetLastError();
/*     */   }
/*     */ 
/*     */   public static byte readByte(int ptr) {
/* 118 */     return ReadByte(ptr);
/*     */   }
/*     */ 
/*     */   public static short readShort(int ptr) {
/* 122 */     return ReadShort(ptr);
/*     */   }
/*     */ 
/*     */   public static int readInt(int ptr) {
/* 126 */     return ReadInt(ptr);
/*     */   }
/*     */ 
/*     */   public static long readLong(int ptr) {
/* 130 */     return ReadLong(ptr);
/*     */   }
/*     */ 
/*     */   public static void writeByte(int ptr, byte b) {
/* 134 */     WriteByte(ptr, b);
/*     */   }
/*     */ 
/*     */   public static void readShort(int ptr, short s) {
/* 138 */     WriteShort(ptr, s);
/*     */   }
/*     */ 
/*     */   public static void writeInt(int ptr, int i) {
/* 142 */     WriteInt(ptr, i);
/*     */   }
/*     */ 
/*     */   public static void writeLong(int ptr, long l) {
/* 146 */     WriteLong(ptr, l);
/*     */   }
/*     */ 
/*     */   private static native void PtrToStruct(int paramInt1, Object paramObject, int paramInt2);
/*     */ 
/*     */   private static native void Free(int paramInt);
/*     */ 
/*     */   private static native void FreeCoTaskMem(int paramInt);
/*     */ 
/*     */   private static native String PtrToString(int paramInt);
/*     */ 
/*     */   private static native String PtrToStringAnsi(int paramInt);
/*     */ 
/*     */   private static native String PtrToStringN(int paramInt1, int paramInt2);
/*     */ 
/*     */   private static native String PtrToStringAnsiN(int paramInt1, int paramInt2);
/*     */ 
/*     */   private static native int StringToPtr(String paramString);
/*     */ 
/*     */   private static native int StringToPtrAnsi(String paramString);
/*     */ 
/*     */   private static native int ByteArrayToPtr(byte[] paramArrayOfByte);
/*     */ 
/*     */   private static native byte[] PtrToByteArray(int paramInt1, int paramInt2);
/*     */ 
/*     */   private static native int GetWindowHandle(Component paramComponent);
/*     */ 
/*     */   private static native int GetLastError();
/*     */ 
/*     */   private static native byte ReadByte(int paramInt);
/*     */ 
/*     */   private static native short ReadShort(int paramInt);
/*     */ 
/*     */   private static native int ReadInt(int paramInt);
/*     */ 
/*     */   private static native long ReadLong(int paramInt);
/*     */ 
/*     */   private static native void WriteByte(int paramInt, byte paramByte);
/*     */ 
/*     */   private static native void WriteShort(int paramInt, short paramShort);
/*     */ 
/*     */   private static native void WriteInt(int paramInt1, int paramInt2);
/*     */ 
/*     */   private static native void WriteLong(int paramInt, long paramLong);
/*     */ 
/*     */   static
/*     */   {
/*  11 */     JInvoke.loadNativeLib();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.Util
 * JD-Core Version:    0.6.0
 */