/*    */ package com.dukascopy.charts.view.drawingstrategies.sub;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*    */ import java.text.DecimalFormat;
/*    */ 
/*    */ public final class SubAxisYFormatterUtils
/*    */ {
/*    */   public static void setup(DecimalFormat formatter, SubValueToYMapper subValueToYMapper, IndicatorWrapper indicatorWrapper)
/*    */   {
/* 16 */     float valuesInOnePixel = subValueToYMapper.get(Integer.valueOf(indicatorWrapper.getId())).getValuesInOnePixel();
/* 17 */     if (valuesInOnePixel > 500.0F)
/* 18 */       formatter.applyPattern("#0.###E0");
/* 19 */     else if (valuesInOnePixel > 100.0F)
/* 20 */       formatter.applyPattern("0.#");
/* 21 */     else if (valuesInOnePixel > 50.0F)
/* 22 */       formatter.applyPattern("0.##");
/* 23 */     else if (valuesInOnePixel > 30.0F)
/* 24 */       formatter.applyPattern("0.###");
/* 25 */     else if (valuesInOnePixel > 20.0F)
/* 26 */       formatter.applyPattern("0.####");
/* 27 */     else if (valuesInOnePixel > 10.0F)
/* 28 */       formatter.applyPattern("0.#####");
/*    */     else
/* 30 */       formatter.applyPattern("0.######");
/*    */   }
/*    */ 
/*    */   public static void setup(DecimalFormat formatter, double value)
/*    */   {
/* 35 */     double absoluteValue = Math.abs(value);
/* 36 */     if (absoluteValue >= 1000000.0D)
/* 37 */       formatter.applyPattern("#0.###E0");
/* 38 */     else if ((absoluteValue >= 1000.0D) || (absoluteValue == 0.0D))
/* 39 */       formatter.applyPattern("0");
/* 40 */     else if (absoluteValue >= 10.0D)
/* 41 */       formatter.applyPattern("0.##");
/* 42 */     else if (absoluteValue >= 1.0D)
/* 43 */       formatter.applyPattern("0.###");
/* 44 */     else if (absoluteValue >= 1.0E-006D)
/* 45 */       formatter.applyPattern("0.#######");
/*    */     else
/* 47 */       formatter.applyPattern("#0.###E0");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYFormatterUtils
 * JD-Core Version:    0.6.0
 */