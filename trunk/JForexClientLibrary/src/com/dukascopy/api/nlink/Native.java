/*     */ package com.dukascopy.api.nlink;
/*     */ 
/*     */ import com.dukascopy.api.RequiresFullAccess;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ 
/*     */ @RequiresFullAccess
/*     */ public class Native
/*     */ {
/*     */   static final String LIB_NAME = "nlink.dll";
/*     */ 
/*     */   static native int loadLibrary(String paramString);
/*     */ 
/*     */   static native int freeLibrary(String paramString);
/*     */ 
/*     */   static native int getLastError();
/*     */ 
/*     */   static native String formatErrorMessage(int paramInt);
/*     */ 
/*     */   static native Object invoke(int paramInt1, Object[] paramArrayOfObject, int[] paramArrayOfInt, Class<?> paramClass, int paramInt2);
/*     */ 
/*     */   static native int getProcAddress2(int paramInt1, int paramInt2);
/*     */ 
/*     */   static native int getProcAddress(int paramInt, String paramString);
/*     */ 
/*     */   static String getPackageName(Class c)
/*     */   {
/*  39 */     String fullyQualifiedName = c.getName();
/*  40 */     int lastDot = fullyQualifiedName.lastIndexOf(46);
/*  41 */     if (lastDot == -1) {
/*  42 */       return "";
/*     */     }
/*  44 */     return fullyQualifiedName.substring(0, lastDot);
/*     */   }
/*     */ 
/*     */   static void loadNativeLibrary()
/*     */   {
/*     */     try
/*     */     {
/*  51 */       System.loadLibrary("nlink.dll");
/*  52 */       return;
/*     */     }
/*     */     catch (Throwable packageName)
/*     */     {
/*  58 */       String packageName = new String(getPackageName(Native.class));
/*     */ 
/*  60 */       packageName = "com/dukascopy/api/nlink";
/*  61 */       URL res = Native.class.getClassLoader().getResource(packageName + "/Const.class");
/*  62 */       String url = res.toExternalForm();
/*  63 */       if (url.startsWith("jar://")) {
/*  64 */         int idx = url.lastIndexOf(33);
/*  65 */         String filePortion = url.substring(6, idx);
/*  66 */         if (filePortion.startsWith("file://")) {
/*  67 */           File jarFile = new File(filePortion.substring(7));
/*  68 */           File dllFile = new File(jarFile.getParentFile(), "nlink.dll");
/*  69 */           System.load(dllFile.getPath());
/*  70 */           return;
/*     */         }
/*     */       }
/*     */ 
/*  74 */       if (extract("nlink.dll")) {
/*  75 */         return;
/*     */       }
/*     */     }
/*  78 */     throw new UnsatisfiedLinkError("Unable to load nlink.dll");
/*     */   }
/*     */ 
/*     */   static boolean extract(String fileName) {
/*  82 */     FileOutputStream os = null;
/*  83 */     InputStream is = null;
/*  84 */     String tempdir = System.getProperty("java.io.tmpdir");
/*  85 */     String path = tempdir;
/*  86 */     if ((!tempdir.endsWith("/")) || (!tempdir.endsWith("\\"))) {
/*  87 */       path = path + File.separator;
/*     */     }
/*  89 */     path = path + "jfxide";
/*  90 */     path = path + File.separator;
/*  91 */     path = path + fileName;
/*  92 */     File file = new File(path);
/*     */     try {
/*  94 */       if (!file.exists()) {
/*  95 */         is = Native.class.getResourceAsStream("/" + fileName);
/*  96 */         if (is != null)
/*     */         {
/*  98 */           byte[] buffer = new byte[4096];
/*  99 */           os = new FileOutputStream(file);
/*     */           int read;
/* 100 */           while ((read = is.read(buffer)) != -1) {
/* 101 */             os.write(buffer, 0, read);
/*     */           }
/* 103 */           os.close();
/* 104 */           is.close();
/*     */         }
/*     */       }
/* 107 */       load(file.getAbsolutePath());
/* 108 */       return true;
/*     */     } catch (Throwable ex) {
/*     */       try {
/* 111 */         if (os != null)
/* 112 */           os.close();
/*     */       }
/*     */       catch (IOException ex1) {
/*     */       }
/*     */       try {
/* 117 */         if (is != null)
/* 118 */           is.close();
/*     */       }
/*     */       catch (IOException ex1)
/*     */       {
/*     */       }
/* 123 */       if (file.exists())
/* 124 */         file.delete();
/*     */     }
/* 126 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean load(String libName) {
/*     */     try {
/* 131 */       System.load(libName);
/* 132 */       return true;
/*     */     } catch (UnsatisfiedLinkError e) {
/* 134 */       e.printStackTrace();
/*     */     }
/* 136 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  33 */     loadNativeLibrary();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.Native
 * JD-Core Version:    0.6.0
 */