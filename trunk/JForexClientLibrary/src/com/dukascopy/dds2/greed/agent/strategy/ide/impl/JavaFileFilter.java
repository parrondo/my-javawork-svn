/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*    */ import java.io.File;
/*    */ import javax.swing.filechooser.FileFilter;
/*    */ 
/*    */ public class JavaFileFilter extends FileFilter
/*    */ {
/* 10 */   protected String extension = "java";
/*    */ 
/*    */   public JavaFileFilter(ServiceSourceLanguage serviceSourceLanguage) {
/* 13 */     if (serviceSourceLanguage == ServiceSourceLanguage.MQ4)
/* 14 */       this.extension = "mq4";
/* 15 */     if (serviceSourceLanguage == ServiceSourceLanguage.MQ5)
/* 16 */       this.extension = "mq5";
/*    */   }
/*    */ 
/*    */   public JavaFileFilter()
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean accept(File f) {
/* 24 */     if (f.isDirectory()) {
/* 25 */       return true;
/*    */     }
/*    */ 
/* 28 */     String extension = getExtension(f);
/* 29 */     if (extension != null)
/*    */     {
/* 31 */       return extension.equals(this.extension);
/*    */     }
/*    */ 
/* 36 */     return false;
/*    */   }
/*    */ 
/*    */   public String getExtension(File f) {
/* 40 */     String ext = null;
/* 41 */     String s = f.getName();
/* 42 */     int i = s.lastIndexOf('.');
/* 43 */     if ((i > 0) && (i < s.length() - 1)) {
/* 44 */       ext = s.substring(i + 1).toLowerCase();
/*    */     }
/* 46 */     return ext;
/*    */   }
/*    */ 
/*    */   public String getDescription() {
/* 50 */     return "*." + this.extension;
/*    */   }
/*    */ 
/*    */   public String getExtension() {
/* 54 */     return this.extension;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.JavaFileFilter
 * JD-Core Version:    0.6.0
 */