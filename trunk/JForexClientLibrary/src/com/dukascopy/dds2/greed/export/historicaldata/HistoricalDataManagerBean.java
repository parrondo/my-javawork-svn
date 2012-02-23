/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class HistoricalDataManagerBean
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String outputDirectory;
/*    */   private String dateFormat;
/*    */   private String delimiter;
/*    */ 
/*    */   public String getOutputDirectory()
/*    */   {
/* 13 */     return this.outputDirectory;
/*    */   }
/*    */   public void setOutputDirectory(String outputDirectory) {
/* 16 */     this.outputDirectory = outputDirectory;
/*    */   }
/*    */   public String getDelimiter() {
/* 19 */     return this.delimiter;
/*    */   }
/*    */   public void setDelimiter(String delimiter) {
/* 22 */     this.delimiter = delimiter;
/*    */   }
/*    */   public String getDateFormat() {
/* 25 */     return this.dateFormat;
/*    */   }
/*    */   public void setDateFormat(String dateFormat) {
/* 28 */     this.dateFormat = dateFormat;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.HistoricalDataManagerBean
 * JD-Core Version:    0.6.0
 */