/*    */ package com.dukascopy.api.connector.helpers;
/*    */ 
/*    */ import com.dukascopy.api.indicators.IIndicator;
/*    */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*    */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*    */ 
/*    */ public class RangeHelper
/*    */ {
/*    */   public static int getRangeAdjustedValue(IntegerRangeDescription description, int value)
/*    */   {
/* 10 */     return description.getMax() < value ? description.getMax() : description.getMin() > value ? description.getMin() : value;
/*    */   }
/*    */ 
/*    */   public static int getRangeAdjustedInputValue(IIndicator indicator, int i, int value)
/*    */   {
/* 21 */     return getRangeAdjustedValue((IntegerRangeDescription)indicator.getOptInputParameterInfo(i).getDescription(), value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.helpers.RangeHelper
 * JD-Core Version:    0.6.0
 */