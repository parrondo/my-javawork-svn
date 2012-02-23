/*    */ package com.dukascopy.charts.utils.file.filter;
/*    */ 
/*    */ import java.io.File;
/*    */ import javax.swing.filechooser.FileFilter;
/*    */ 
/*    */ public abstract class AbstractFileFilter extends FileFilter
/*    */ {
/*    */   public abstract String getExtension();
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 14 */     if (file.isDirectory()) {
/* 15 */       return true;
/*    */     }
/*    */ 
/* 18 */     String fileName = file.getName();
/*    */ 
/* 20 */     return fileName.toLowerCase().endsWith("." + getExtension());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.file.filter.AbstractFileFilter
 * JD-Core Version:    0.6.0
 */