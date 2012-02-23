/*    */ package com.dukascopy.charts.view.drawingstrategies.util;
/*    */ 
/*    */ import java.text.DecimalFormat;
/*    */ 
/*    */ public class IndicatorValueFormatter
/*    */ {
/* 11 */   final DecimalFormat formatter = new DecimalFormat("0.######");
/*    */ 
/*    */   public String format(double value)
/*    */   {
/* 26 */     if (value != value) {
/* 27 */       return null;
/*    */     }
/*    */ 
/* 30 */     double absoluteValue = Math.abs(value);
/*    */ 
/* 32 */     if (absoluteValue < 1.0E-006D)
/*    */     {
/* 34 */       if (value == 0.0D)
/* 35 */         return "0";
/* 36 */       if (value < 0.0D) {
/* 37 */         return "-0.000001";
/*    */       }
/* 39 */       return "0.000001";
/*    */     }
/*    */ 
/* 42 */     if (absoluteValue >= 1000000.0D)
/* 43 */       this.formatter.applyPattern("#0.###E0");
/* 44 */     else if (absoluteValue < 10.0D)
/* 45 */       this.formatter.applyPattern("0.#######");
/*    */     else {
/* 47 */       this.formatter.applyPattern("0.#####");
/*    */     }
/* 49 */     return this.formatter.format(value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.util.IndicatorValueFormatter
 * JD-Core Version:    0.6.0
 */