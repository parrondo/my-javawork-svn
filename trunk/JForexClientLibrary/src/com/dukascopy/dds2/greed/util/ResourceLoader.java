/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ResourceLoader
/*    */ {
/* 22 */   private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoader.class);
/*    */ 
/* 24 */   private static ResourceLoader resourceLoader = null;
/*    */ 
/*    */   public static ResourceLoader getInstance() {
/* 27 */     if (resourceLoader == null) {
/* 28 */       resourceLoader = new ResourceLoader();
/*    */     }
/* 30 */     return resourceLoader;
/*    */   }
/*    */ 
/*    */   public String readFirstLineFromTextResource(String path)
/*    */   {
/* 37 */     String result = null;
/* 38 */     List sl = readTextResource(path, true);
/* 39 */     if (sl.size() > 0) {
/* 40 */       result = (String)sl.get(0);
/*    */     }
/* 42 */     return result;
/*    */   }
/*    */ 
/*    */   public List<String> readTextResource(String path) {
/* 46 */     return readTextResource(path, false);
/*    */   }
/*    */ 
/*    */   public List<String> readTextResource(String path, boolean readOnlyFirstLine) {
/* 50 */     List result = new ArrayList();
/*    */     try {
/* 52 */       InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
/* 53 */       if (resourceAsStream == null) {
/* 54 */         throw new FileNotFoundException(path);
/*    */       }
/* 56 */       BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream));
/*    */ 
/* 58 */       boolean flag = false;
/*    */       String str;
/* 59 */       while ((str = in.readLine()) != null) {
/* 60 */         flag = true;
/* 61 */         result.add(str);
/* 62 */         if ((readOnlyFirstLine) && (flag)) {
/* 63 */           break;
/*    */         }
/*    */       }
/* 66 */       in.close();
/*    */     } catch (IOException e) {
/* 68 */       LOGGER.error(e.getMessage(), e);
/*    */     }
/* 70 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.ResourceLoader
 * JD-Core Version:    0.6.0
 */