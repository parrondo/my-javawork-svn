/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public abstract class ServiceWrapper
/*    */ {
/*  9 */   private static int newFilesCounter = 1;
/*    */   protected int newFileIndex;
/*    */   protected boolean isNewUnsaved;
/* 13 */   protected boolean isModified = false;
/* 14 */   protected File srcFile = null;
/* 15 */   protected File binFile = null;
/*    */ 
/*    */   public abstract String getName();
/*    */ 
/* 20 */   public boolean isNewUnsaved() { return this.isNewUnsaved; }
/*    */ 
/*    */   public void setNewUnsaved(boolean isNewUnsaved)
/*    */   {
/* 24 */     this.isNewUnsaved = isNewUnsaved;
/* 25 */     if (isNewUnsaved)
/* 26 */       this.newFileIndex = (newFilesCounter++);
/*    */   }
/*    */ 
/*    */   public boolean isModified()
/*    */   {
/* 31 */     return this.isModified;
/*    */   }
/*    */ 
/*    */   public void setIsModified(boolean isModified) {
/* 35 */     this.isModified = isModified;
/*    */   }
/*    */ 
/*    */   public boolean isEditable() {
/* 39 */     return (this.srcFile != null) && (this.srcFile.exists());
/*    */   }
/*    */ 
/*    */   public boolean isRunnable() {
/* 43 */     return (getBinaryFile() != null) && (getBinaryFile().exists());
/*    */   }
/*    */ 
/*    */   public boolean isRemotelyRunnable() {
/* 47 */     return false;
/*    */   }
/*    */ 
/*    */   public final void setBinaryFile(File binaryFile) {
/* 51 */     this.binFile = binaryFile;
/*    */   }
/*    */ 
/*    */   public final File getBinaryFile() {
/* 55 */     if ((this.srcFile != null) && (this.srcFile.exists())) {
/* 56 */       File file = new File(this.srcFile.getParent(), this.srcFile.getName().substring(0, this.srcFile.getName().lastIndexOf('.')) + ".jfx");
/* 57 */       if (file.exists()) {
/* 58 */         this.binFile = file;
/*    */       }
/*    */     }
/* 61 */     return this.binFile;
/*    */   }
/*    */ 
/*    */   public final void setSourceFile(File sourceFile) {
/* 65 */     this.srcFile = sourceFile;
/*    */   }
/*    */ 
/*    */   public File getSourceFile() {
/* 69 */     return this.srcFile;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.ServiceWrapper
 * JD-Core Version:    0.6.0
 */