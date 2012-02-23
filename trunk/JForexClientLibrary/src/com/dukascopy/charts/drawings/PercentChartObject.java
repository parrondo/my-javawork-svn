/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.api.drawings.IPercentChartObject;
/*    */ import com.dukascopy.charts.ChartProperties;
/*    */ import java.util.List;
/*    */ 
/*    */ public class PercentChartObject extends HorizontalRetracementChartObject
/*    */   implements IPercentChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 2L;
/*    */   private static final String CHART_OBJECT_NAME = "Percent";
/*    */ 
/*    */   public PercentChartObject(String key)
/*    */   {
/* 16 */     super(key, IChart.Type.PERCENT);
/*    */   }
/*    */ 
/*    */   public PercentChartObject() {
/* 20 */     super(IChart.Type.PERCENT);
/*    */   }
/*    */ 
/*    */   public PercentChartObject(String key, long time1, double price1, long time2, double price2) {
/* 24 */     super(key, IChart.Type.PERCENT, time1, price1, time2, price2);
/*    */   }
/*    */ 
/*    */   public PercentChartObject(PercentChartObject chartObject) {
/* 28 */     super(chartObject);
/*    */   }
/*    */ 
/*    */   public List<Object[]> getDefaults()
/*    */   {
/* 33 */     return ChartProperties.createDefaultLevelsPercents();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 38 */     return "Percent";
/*    */   }
/*    */ 
/*    */   public PercentChartObject clone()
/*    */   {
/* 43 */     return new PercentChartObject(this);
/*    */   }
/*    */ 
/*    */   public String getLocalizationKey()
/*    */   {
/* 48 */     return "item.precent.lines";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.PercentChartObject
 * JD-Core Version:    0.6.0
 */