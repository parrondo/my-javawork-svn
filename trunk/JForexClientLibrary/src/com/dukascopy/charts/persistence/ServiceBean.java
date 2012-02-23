/*    */ package com.dukascopy.charts.persistence;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public abstract class ServiceBean
/*    */ {
/*    */   protected Integer id;
/*    */   protected String sourceFullFileName;
/*    */   protected String binaryFullFileName;
/* 10 */   private boolean editable = false;
/*    */ 
/*    */   protected ServiceBean(Integer id, String sourceFullFileName, String binaryFullFileName) {
/* 13 */     this.id = id;
/* 14 */     this.sourceFullFileName = sourceFullFileName;
/* 15 */     this.binaryFullFileName = binaryFullFileName;
/*    */   }
/*    */ 
/*    */   protected ServiceBean(int id, File sourceFile, File binaryFile) {
/* 19 */     this.id = Integer.valueOf(id);
/* 20 */     this.sourceFullFileName = (sourceFile != null ? sourceFile.getAbsolutePath() : "");
/* 21 */     this.binaryFullFileName = (binaryFile != null ? binaryFile.getAbsolutePath() : "");
/*    */   }
/*    */ 
/*    */   public final Integer getId() {
/* 25 */     return this.id;
/*    */   }
/*    */ 
/*    */   public final String getSourceFullFileName() {
/* 29 */     return this.sourceFullFileName;
/*    */   }
/*    */ 
/*    */   public final String getBinaryFullFileName() {
/* 33 */     return this.binaryFullFileName;
/*    */   }
/*    */ 
/*    */   public final void setEditable(boolean value) {
/* 37 */     this.editable = value;
/*    */   }
/*    */ 
/*    */   public final boolean isEditable() {
/* 41 */     return this.editable;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.ServiceBean
 * JD-Core Version:    0.6.0
 */