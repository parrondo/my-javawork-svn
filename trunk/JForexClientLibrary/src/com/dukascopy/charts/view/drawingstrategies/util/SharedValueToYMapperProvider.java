/*    */ package com.dukascopy.charts.view.drawingstrategies.util;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*    */ import com.dukascopy.charts.mappers.value.ValueToYMapper;
/*    */ 
/*    */ public final class SharedValueToYMapperProvider
/*    */ {
/*    */   public static IValueToYMapper getSharedValueToYMapper(IndicatorWrapper wrapper, SubIndicatorGroup subIndicatorGroup, SubValueToYMapper subValueToYMapper)
/*    */   {
/* 19 */     IValueToYMapper valueToYMapper = subValueToYMapper.get(Integer.valueOf(wrapper.getId()));
/*    */ 
/* 21 */     for (IndicatorWrapper w : subIndicatorGroup.getSubIndicators()) {
/* 22 */       if ((w.getName().equals(wrapper.getName())) && (((ValueToYMapper)subValueToYMapper.get(Integer.valueOf(w.getId()))).getValueFrameMaxValue() > ((ValueToYMapper)valueToYMapper).getValueFrameMaxValue()))
/*    */       {
/* 25 */         valueToYMapper = subValueToYMapper.get(Integer.valueOf(w.getId()));
/*    */       }
/*    */     }
/* 28 */     return valueToYMapper;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.util.SharedValueToYMapperProvider
 * JD-Core Version:    0.6.0
 */