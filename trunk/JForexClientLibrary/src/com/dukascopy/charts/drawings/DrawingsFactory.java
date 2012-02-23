/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.api.IChartObject;
/*    */ 
/*    */ public class DrawingsFactory
/*    */ {
/*    */   public IChartObject createDrawing(String key, IChart.Type drawingType, long time1, double price1, long time2, double price2, long time3, double price3)
/*    */   {
/*  9 */     switch (1.$SwitchMap$com$dukascopy$api$IChart$Type[drawingType.ordinal()]) { case 1:
/* 10 */       return new TriangleChartObject(key, time1, price1, time2, price2, time3, price3);
/*    */     case 2:
/* 11 */       return new ChannelChartObject(key, time1, price1, time2, price2, time3, price3);
/*    */     case 3:
/* 12 */       return new AndrewsPitchforkChartObject(key, time1, price1, time2, price2, time3, price3); }
/* 13 */     return createDrawing(key, drawingType, time1, price1, time2, price2);
/*    */   }
/*    */ 
/*    */   public IChartObject createDrawing(String key, IChart.Type drawingType, long time1, double price1, long time2, double price2)
/*    */   {
/* 18 */     switch (1.$SwitchMap$com$dukascopy$api$IChart$Type[drawingType.ordinal()]) { case 4:
/* 19 */       return new ShortLineChartObject(key, time1, price1, time2, price2);
/*    */     case 5:
/* 20 */       return new LongLineChartObject(key, time1, price1, time2, price2);
/*    */     case 6:
/* 21 */       return new RayLineChartObject(key, time1, price1, time2, price2);
/*    */     case 7:
/* 22 */       return new RectangleChartObject(key, time1, price1, time2, price2);
/*    */     case 8:
/* 23 */       return new EllipseChartObject(key, time1, price1, time2, price2);
/*    */     case 9:
/* 24 */       return new PercentChartObject(key, time1, price1, time2, price2);
/*    */     case 10:
/* 25 */       return new FiboRetracementChartObject(key, time1, price1, time2, price2);
/*    */     case 11:
/* 26 */       return new FiboArcChartObject(key, time1, price1, time2, price2);
/*    */     case 12:
/* 27 */       return new FiboFanChartObject(key, time1, price1, time2, price2);
/*    */     case 13:
/* 28 */       return new FiboTimeZonesChartObject(key, time1, price1, time2, price2); }
/* 29 */     return createDrawing(key, drawingType, time1, price1);
/*    */   }
/*    */ 
/*    */   public IChartObject createDrawing(String key, IChart.Type drawingType, long time1, double price1)
/*    */   {
/* 35 */     switch (1.$SwitchMap$com$dukascopy$api$IChart$Type[drawingType.ordinal()]) { case 14:
/* 36 */       return new HLineChartObject(key, price1);
/*    */     case 15:
/* 37 */       return new VLineChartObject(key, time1);
/*    */     case 16:
/* 38 */       return new OrderLineChartObject(key, price1);
/*    */     case 17:
/* 39 */       return new SignalUpChartObject(key, time1, price1);
/*    */     case 18:
/* 40 */       return new SignalDownChartObject(key, time1, price1);
/*    */     case 19:
/* 41 */       return new TextChartObject(key, time1, price1);
/*    */     case 20:
/* 42 */       return new LabelChartObject(key, time1, price1);
/*    */     case 21:
/* 43 */       return new TimeMarkerChartObject(key, time1);
/*    */     case 22:
/* 44 */       return new PriceMarkerChartObject(key, price1);
/*    */     case 23:
/* 45 */       return new CyclesChartObject(key, time1, (int)price1); }
/* 46 */     return null;
/*    */   }
/*    */ 
/*    */   public ChartObject createDrawing(IChart.Type chartType)
/*    */   {
/* 51 */     switch (1.$SwitchMap$com$dukascopy$api$IChart$Type[chartType.ordinal()]) { case 14:
/* 52 */       return new HLineChartObject();
/*    */     case 15:
/* 53 */       return new VLineChartObject();
/*    */     case 21:
/* 54 */       return new TimeMarkerChartObject();
/*    */     case 22:
/* 55 */       return new PriceMarkerChartObject();
/*    */     case 24:
/* 56 */       return new PolyLineChartObject();
/*    */     case 4:
/* 57 */       return new ShortLineChartObject();
/*    */     case 5:
/* 58 */       return new LongLineChartObject();
/*    */     case 6:
/* 59 */       return new RayLineChartObject();
/*    */     case 2:
/* 60 */       return new ChannelChartObject();
/*    */     case 7:
/* 61 */       return new RectangleChartObject();
/*    */     case 1:
/* 62 */       return new TriangleChartObject();
/*    */     case 8:
/* 63 */       return new EllipseChartObject();
/*    */     case 19:
/* 64 */       return new TextChartObject();
/*    */     case 17:
/* 65 */       return new SignalUpChartObject();
/*    */     case 18:
/* 66 */       return new SignalDownChartObject();
/*    */     case 23:
/* 67 */       return new CyclesChartObject();
/*    */     case 9:
/* 68 */       return new PercentChartObject();
/*    */     case 11:
/* 69 */       return new FiboArcChartObject();
/*    */     case 12:
/* 70 */       return new FiboFanChartObject();
/*    */     case 10:
/* 71 */       return new FiboRetracementChartObject();
/*    */     case 13:
/* 72 */       return new FiboTimeZonesChartObject();
/*    */     case 25:
/* 73 */       return new FiboExpansionChartObject();
/*    */     case 3:
/* 74 */       return new AndrewsPitchforkChartObject();
/*    */     case 26:
/* 75 */       return new OhlcChartObject();
/*    */     case 27:
/* 76 */       return new PatternWidgetChartObject();
/*    */     case 28:
/* 77 */       return new GannAnglesChartObject();
/*    */     case 29:
/* 78 */       return new GannGridChartObject();
/*    */     case 30:
/* 79 */       return new TimeRangeChartObject();
/*    */     case 16:
/* 80 */     case 20: } return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.DrawingsFactory
 * JD-Core Version:    0.6.0
 */