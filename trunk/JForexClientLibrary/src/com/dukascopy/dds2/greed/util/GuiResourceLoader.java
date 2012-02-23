/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import java.awt.Toolkit;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.ImageIcon;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class GuiResourceLoader
/*    */ {
/* 30 */   private static final Logger LOGGER = LoggerFactory.getLogger(GuiResourceLoader.class);
/*    */ 
/* 32 */   private static GuiResourceLoader resourceLoader = null;
/*    */ 
/* 34 */   private static Map<String, Object> resourceCache = new HashMap();
/*    */ 
/*    */   public static GuiResourceLoader getInstance() {
/* 37 */     if (resourceLoader == null) {
/* 38 */       resourceLoader = new GuiResourceLoader();
/*    */     }
/* 40 */     return resourceLoader;
/*    */   }
/*    */ 
/*    */   public ImageIcon loadImageIcon(String path)
/*    */   {
/* 47 */     if (resourceCache.containsKey(path)) {
/* 48 */       return (ImageIcon)resourceCache.get(path);
/*    */     }
/* 50 */     ImageIcon result = createImageIcon(path);
/* 51 */     if (result != null) {
/* 52 */       resourceCache.put(path, result);
/*    */     } else {
/* 54 */       LOGGER.warn(" Can't load image icon with name: " + path);
/* 55 */       result = GuiUtilsAndConstants.DEFAULT_ICON;
/*    */     }
/* 57 */     return result;
/*    */   }
/*    */ 
/*    */   protected ImageIcon createImageIcon(String path) {
/* 61 */     int MAX_IMAGE_SIZE = 90000;
/* 62 */     int count = 0;
/* 63 */     BufferedInputStream imgStream = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
/*    */ 
/* 66 */     if (imgStream != null) {
/* 67 */       byte[] buf = new byte[MAX_IMAGE_SIZE];
/*    */       try {
/* 69 */         count = imgStream.read(buf);
/*    */       } catch (IOException ieo) {
/* 71 */         LOGGER.error("Couldn't read stream from file: " + path);
/*    */       }
/*    */       try
/*    */       {
/* 75 */         imgStream.close();
/*    */       } catch (IOException ieo) {
/* 77 */         LOGGER.error("Can't close file " + path);
/*    */       }
/*    */ 
/* 80 */       if (count <= 0) {
/* 81 */         LOGGER.error("Empty file: " + path);
/* 82 */         return null;
/*    */       }
/* 84 */       return new ImageIcon(Toolkit.getDefaultToolkit().createImage(buf));
/*    */     }
/* 86 */     LOGGER.error("Couldn't find file: " + path);
/* 87 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.GuiResourceLoader
 * JD-Core Version:    0.6.0
 */