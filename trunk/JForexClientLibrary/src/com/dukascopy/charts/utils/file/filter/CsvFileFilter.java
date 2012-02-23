/*    */ package com.dukascopy.charts.utils.file.filter;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public class CsvFileFilter extends AbstractFileFilter
/*    */ {
/*    */   public String getExtension()
/*    */   {
/* 14 */     return "csv";
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 19 */     return LocalizationManager.getText("csv.files");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.file.filter.CsvFileFilter
 * JD-Core Version:    0.6.0
 */