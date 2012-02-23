/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ public enum ExportFormat
/*    */ {
/*  4 */   CSV("CSV"), 
/*  5 */   HST("HST");
/*    */ 
/*    */   private String format;
/*    */ 
/*  9 */   private ExportFormat(String format) { this.format = format; }
/*    */ 
/*    */   public String getCaption()
/*    */   {
/* 13 */     return this.format;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 18 */     return this.format;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.ExportFormat
 * JD-Core Version:    0.6.0
 */