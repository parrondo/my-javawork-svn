/*    */ package com.dukascopy.charts.dialogs.indicators.listener;
/*    */ 
/*    */ public abstract interface IndicatorChangeListener
/*    */ {
/*    */   public abstract void indicatorChanged(ParameterType paramParameterType);
/*    */ 
/*    */   public static enum ParameterType
/*    */   {
/*  9 */     INPUT, 
/* 10 */     OPTIONAL, 
/* 11 */     OUTPUT, 
/* 12 */     LEVEL;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener
 * JD-Core Version:    0.6.0
 */