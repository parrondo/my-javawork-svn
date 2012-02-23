/*    */ package com.dukascopy.charts.persistence;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public class EnabledIndicatorBean
/*    */ {
/*    */   private String name;
/*    */   protected String sourceFullFileName;
/*    */   protected String binaryFullFileName;
/*    */ 
/*    */   public EnabledIndicatorBean(String name, String sourceFullFileName, String binaryFullFileName)
/*    */   {
/* 14 */     this.name = name;
/* 15 */     this.sourceFullFileName = sourceFullFileName;
/* 16 */     this.binaryFullFileName = binaryFullFileName;
/*    */   }
/*    */ 
/*    */   public EnabledIndicatorBean(String name, File sourceFile, File binaryFile) {
/* 20 */     this.name = name;
/* 21 */     this.sourceFullFileName = (sourceFile != null ? sourceFile.getAbsolutePath() : "");
/* 22 */     this.binaryFullFileName = (binaryFile != null ? binaryFile.getAbsolutePath() : "");
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 26 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getSourceFullFileName() {
/* 30 */     return this.sourceFullFileName;
/*    */   }
/*    */ 
/*    */   public String getBinaryFullFileName() {
/* 34 */     return this.binaryFullFileName;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.EnabledIndicatorBean
 * JD-Core Version:    0.6.0
 */