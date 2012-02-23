/*    */ package com.dukascopy.charts.utils.file.filter;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public class HtmlFileFilter extends AbstractFileFilter
/*    */ {
/*    */   public String getExtension()
/*    */   {
/*  9 */     return "html";
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 14 */     return LocalizationManager.getText("HTML Files");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.file.filter.HtmlFileFilter
 * JD-Core Version:    0.6.0
 */