/*    */ package com.dukascopy.dds2.greed.gui.helpers;
/*    */ 
/*    */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ 
/*    */ public class CustomIndicatorListManager
/*    */ {
/*    */   private static final String JFX = "jfx";
/*    */   private static final int MAX_ITEM_CNT = 30;
/* 24 */   private static ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*    */ 
/*    */   public static List<String> getCustomIndicators()
/*    */   {
/* 28 */     File folder = new File(clientSettingsStorage.getMyIndicatorsPath());
/* 29 */     List custIndicatorsList = new ArrayList();
/*    */ 
/* 31 */     for (int i = 0; (i < folder.listFiles().length) && (i <= 30); i++) {
/* 32 */       File file = folder.listFiles()[i];
/* 33 */       if ((!file.isFile()) || (!file.getAbsolutePath().endsWith("jfx")))
/*    */         continue;
/* 35 */       custIndicatorsList.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
/*    */     }
/*    */ 
/* 39 */     Collections.sort(custIndicatorsList);
/* 40 */     return custIndicatorsList;
/*    */   }
/*    */ 
/*    */   public static CustIndicatorWrapper getCustomIndicator(String fileName)
/*    */   {
/* 52 */     File folder = new File(clientSettingsStorage.getMyIndicatorsPath());
/* 53 */     CustIndicatorWrapper wrapper = null;
/*    */ 
/* 55 */     for (int i = 0; i < folder.listFiles().length; i++) {
/* 56 */       File file = folder.listFiles()[i];
/* 57 */       if ((file.isFile()) && (file.getName().equals(fileName + "." + "jfx"))) {
/* 58 */         wrapper = new CustIndicatorWrapper();
/* 59 */         wrapper.setBinaryFile(file);
/* 60 */         return wrapper;
/*    */       }
/*    */     }
/* 63 */     return wrapper;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.helpers.CustomIndicatorListManager
 * JD-Core Version:    0.6.0
 */