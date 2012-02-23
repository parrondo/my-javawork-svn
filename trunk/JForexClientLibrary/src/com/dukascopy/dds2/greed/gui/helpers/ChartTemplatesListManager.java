/*    */ package com.dukascopy.dds2.greed.gui.helpers;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ChartTemplatesListManager
/*    */ {
/*    */   private static final String TMPL = "tmpl";
/*    */   private static final int MAX_ITEM_CNT = 30;
/* 20 */   private static ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*    */ 
/*    */   public static List<String> getChartTemplates()
/*    */   {
/* 25 */     File folder = new File(clientSettingsStorage.getMyChartTemplatesPath());
/* 26 */     List chartTemplatesList = new ArrayList();
/*    */ 
/* 28 */     for (int i = 0; (i < folder.listFiles().length) && (i <= 30); i++) {
/* 29 */       File file = folder.listFiles()[i];
/* 30 */       if ((!file.isFile()) || (!file.getAbsolutePath().endsWith("tmpl")))
/*    */         continue;
/* 32 */       chartTemplatesList.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
/*    */     }
/*    */ 
/* 36 */     Collections.sort(chartTemplatesList);
/* 37 */     return chartTemplatesList;
/*    */   }
/*    */ 
/*    */   public static File getCustomIndicator(String fileName)
/*    */   {
/* 48 */     File folder = new File(clientSettingsStorage.getMyChartTemplatesPath());
/*    */ 
/* 50 */     for (int i = 0; i < folder.listFiles().length; i++) {
/* 51 */       File file = folder.listFiles()[i];
/* 52 */       if ((file.isFile()) && (file.getName().equals(fileName + "." + "tmpl"))) {
/* 53 */         return file;
/*    */       }
/*    */     }
/* 56 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.helpers.ChartTemplatesListManager
 * JD-Core Version:    0.6.0
 */