/*    */ package com.dukascopy.dds2.greed.gui.component.file.filter;
/*    */ 
/*    */ import com.dukascopy.charts.utils.file.filter.AbstractFileFilter;
/*    */ import java.io.File;
/*    */ 
/*    */ public class XMLFileFilter extends AbstractFileFilter
/*    */ {
/*    */   public static final String EXTENSION = "xml";
/*    */   private final String description;
/*    */ 
/*    */   public XMLFileFilter(String description)
/*    */   {
/* 19 */     this.description = description;
/*    */   }
/*    */ 
/*    */   public XMLFileFilter() {
/* 23 */     this("");
/*    */   }
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 28 */     return (file != null) && (file.getName() != null) && ((file.getName().endsWith("." + getExtension())) || (file.isDirectory()));
/*    */   }
/*    */ 
/*    */   public String getDescription() {
/* 32 */     return this.description;
/*    */   }
/*    */ 
/*    */   public String getExtension()
/*    */   {
/* 37 */     return "xml";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.file.filter.XMLFileFilter
 * JD-Core Version:    0.6.0
 */