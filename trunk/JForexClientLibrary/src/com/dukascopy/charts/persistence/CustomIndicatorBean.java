/*    */ package com.dukascopy.charts.persistence;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public class CustomIndicatorBean extends ServiceBean
/*    */ {
/*    */   public CustomIndicatorBean(int id, String sourceFullFileName, String binaryFullFileName)
/*    */   {
/*  8 */     super(Integer.valueOf(id), sourceFullFileName, binaryFullFileName);
/*    */   }
/*    */ 
/*    */   public CustomIndicatorBean(int id, File sourceFile, File binaryFile) {
/* 12 */     super(id, sourceFile, binaryFile);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.CustomIndicatorBean
 * JD-Core Version:    0.6.0
 */