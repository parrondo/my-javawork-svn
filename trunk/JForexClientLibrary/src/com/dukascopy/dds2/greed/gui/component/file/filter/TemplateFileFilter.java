/*    */ package com.dukascopy.dds2.greed.gui.component.file.filter;
/*    */ 
/*    */ import com.dukascopy.charts.utils.file.filter.AbstractFileFilter;
/*    */ import java.io.File;
/*    */ 
/*    */ public class TemplateFileFilter extends AbstractFileFilter
/*    */ {
/*    */   private final String description;
/*    */ 
/*    */   public TemplateFileFilter(String description)
/*    */   {
/* 16 */     this.description = description;
/*    */   }
/*    */ 
/*    */   public String getExtension()
/*    */   {
/* 21 */     return "tmpl";
/*    */   }
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 26 */     return (file != null) && (file.getName() != null) && ((file.getName().endsWith("." + getExtension())) || (file.isDirectory()));
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 31 */     return this.description;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.file.filter.TemplateFileFilter
 * JD-Core Version:    0.6.0
 */