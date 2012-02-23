/*    */ package com.dukascopy.charts.utils.formatter;
/*    */ 
/*    */ import java.text.DecimalFormat;
/*    */ 
/*    */ public class DukascopyDecimalFormat extends DecimalFormat
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final int decimalPlaces;
/*    */ 
/*    */   public DukascopyDecimalFormat(int decimalPlaces)
/*    */   {
/* 11 */     super("0." + getZerosString(decimalPlaces));
/* 12 */     this.decimalPlaces = decimalPlaces;
/*    */   }
/*    */ 
/*    */   private static String getZerosString(int zerosCount) {
/* 16 */     String result = "";
/* 17 */     for (int i = 0; i < zerosCount; i++) {
/* 18 */       result = result + "0";
/*    */     }
/* 20 */     return result;
/*    */   }
/*    */ 
/*    */   public int getDecimalPlaces() {
/* 24 */     return this.decimalPlaces;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.formatter.DukascopyDecimalFormat
 * JD-Core Version:    0.6.0
 */