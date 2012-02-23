/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class EditorFileHandler
/*    */ {
/* 11 */   private static final Logger LOGGER = LoggerFactory.getLogger(EditorFileHandler.class);
/*    */   File file;
/*    */ 
/*    */   public String reloadFile()
/*    */   {
/* 16 */     return readFromFile(this.file);
/*    */   }
/*    */ 
/*    */   public String readFromFile(File newFile) {
/* 20 */     String content = "";
/* 21 */     if (!newFile.exists()) {
/* 22 */       return content;
/*    */     }
/*    */ 
/* 25 */     int size = (int)newFile.length();
/* 26 */     if ((size <= 0) && (!newFile.exists())) {
/* 27 */       return content;
/*    */     }
/*    */ 
/* 30 */     byte[] data = new byte[size];
/* 31 */     FileInputStream fis = null;
/*    */     try {
/* 33 */       fis = new FileInputStream(newFile);
/* 34 */       int bytes_read = 0;
/* 35 */       while (bytes_read < size) {
/* 36 */         bytes_read += fis.read(data, bytes_read, size - bytes_read);
/*    */       }
/*    */ 
/* 39 */       this.file = newFile;
/* 40 */       content = new String(data, "UTF-8");
/*    */     } catch (Exception e) {
/* 42 */       LOGGER.error(e.getMessage(), e);
/* 43 */       String str1 = content;
/*    */       return str1;
/*    */     }
/*    */     finally
/*    */     {
/* 45 */       if (fis != null) {
/*    */         try {
/* 47 */           fis.close();
/*    */         }
/*    */         catch (IOException ignorableException)
/*    */         {
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 55 */     content = content.replaceAll("\r\n?", "\n");
/*    */ 
/* 57 */     return content;
/*    */   }
/*    */ 
/*    */   public void writeToFile(String text) {
/* 61 */     writeToFile(this.file, text);
/*    */   }
/*    */ 
/*    */   public void writeToFile(File file, String content) {
/* 65 */     FileOutputStream fileOutputStream = null;
/*    */     try
/*    */     {
/* 68 */       if (!file.exists()) {
/* 69 */         file.createNewFile();
/*    */       }
/* 71 */       fileOutputStream = new FileOutputStream(file);
/* 72 */       ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
/* 73 */       StratUtils.turboPipe(byteArrayInputStream, fileOutputStream);
/* 74 */       this.file = file;
/*    */     } catch (Throwable ignorableException) {
/* 76 */       LOGGER.error(e.getMessage(), e);
/*    */     } finally {
/* 78 */       if (fileOutputStream != null)
/*    */         try {
/* 80 */           fileOutputStream.close();
/*    */         }
/*    */         catch (Exception ignorableException)
/*    */         {
/*    */         }
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean contentWasModified(String text)
/*    */   {
/* 90 */     String savedContent = readFromFile(this.file);
/* 91 */     return !savedContent.equals(text);
/*    */   }
/*    */ 
/*    */   public File getFile() {
/* 95 */     return this.file;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorFileHandler
 * JD-Core Version:    0.6.0
 */