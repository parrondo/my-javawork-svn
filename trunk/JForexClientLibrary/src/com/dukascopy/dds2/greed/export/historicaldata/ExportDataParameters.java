/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class ExportDataParameters
/*    */ {
/*    */   private List<ExportInstrumentParameter> exportInstrumentParameters;
/*    */   private String outputDirectory;
/*    */   private String dateFormat;
/*    */   private String dateFormatMillisec;
/* 10 */   private String delimiter = " ";
/*    */ 
/*    */   public List<ExportInstrumentParameter> getExportInstrumentParameters() {
/* 13 */     return this.exportInstrumentParameters;
/*    */   }
/*    */ 
/*    */   public void setExportInstrumentParameters(List<ExportInstrumentParameter> exportInstrumentParameters)
/*    */   {
/* 18 */     this.exportInstrumentParameters = exportInstrumentParameters;
/*    */   }
/*    */ 
/*    */   public String getOutputDirectory() {
/* 22 */     return this.outputDirectory;
/*    */   }
/*    */ 
/*    */   public void setOutputDirectory(String outputDirectory) {
/* 26 */     this.outputDirectory = outputDirectory;
/*    */   }
/*    */ 
/*    */   public String getDateFormat() {
/* 30 */     return this.dateFormat;
/*    */   }
/*    */ 
/*    */   public void setDateFormat(String dateFormat) {
/* 34 */     this.dateFormat = dateFormat;
/*    */   }
/*    */ 
/*    */   public String getDateFormatMillisec() {
/* 38 */     return this.dateFormatMillisec;
/*    */   }
/*    */ 
/*    */   public void setDateFormatMillisec(String dateFormatMillisec) {
/* 42 */     this.dateFormatMillisec = dateFormatMillisec;
/*    */   }
/*    */ 
/*    */   public String getDelimiter() {
/* 46 */     return this.delimiter;
/*    */   }
/*    */ 
/*    */   public void setDelimiter(String delimiter) {
/* 50 */     if ((delimiter == null) || (delimiter.length() == 0))
/* 51 */       this.delimiter = " ";
/*    */     else
/* 53 */       this.delimiter = delimiter;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.ExportDataParameters
 * JD-Core Version:    0.6.0
 */