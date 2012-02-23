/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class FileManager
/*    */ {
/*    */   public static void copyFile(File sourceFile, File targetFile)
/*    */     throws IOException
/*    */   {
/* 20 */     InputStream in = new FileInputStream(sourceFile);
/*    */ 
/* 22 */     OutputStream out = new FileOutputStream(targetFile);
/*    */ 
/* 24 */     byte[] buf = new byte[1024];
/*    */     int len;
/* 28 */     while ((len = in.read(buf)) > 0) {
/* 29 */       out.write(buf, 0, len);
/*    */     }
/*    */ 
/* 32 */     in.close();
/* 33 */     out.close();
/*    */   }
/*    */ 
/*    */   public static void copyFile(String sourcePath, String targetPath) throws IOException {
/* 37 */     File sourceFile = new File(sourcePath);
/* 38 */     File targetFile = new File(targetPath);
/* 39 */     copyFile(sourceFile, targetFile);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.FileManager
 * JD-Core Version:    0.6.0
 */