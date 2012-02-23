/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.api.drawings.IDecoratedChartObject.Decoration;
/*    */ import com.dukascopy.api.drawings.IDecoratedChartObject.Placement;
/*    */ import com.dukascopy.api.drawings.ITimeMarkerChartObject;
/*    */ import com.dukascopy.charts.mappers.IMapper;
/*    */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ public class TimeMarkerChartObject extends VLineChartObject
/*    */   implements ITimeMarkerChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final String CHART_OBJECT_NAME = "Time Marker";
/*    */ 
/*    */   public TimeMarkerChartObject(String key)
/*    */   {
/* 18 */     super(key, IChart.Type.TIMEMARKER);
/* 19 */     setDefaultDecorations();
/*    */   }
/*    */ 
/*    */   public TimeMarkerChartObject() {
/* 23 */     super(null, IChart.Type.TIMEMARKER);
/* 24 */     setUnderEdit(true);
/* 25 */     setDefaultDecorations();
/*    */   }
/*    */ 
/*    */   public TimeMarkerChartObject(String key, long time1) {
/* 29 */     super(key, time1, IChart.Type.TIMEMARKER);
/* 30 */     setDefaultDecorations();
/*    */   }
/*    */ 
/*    */   public TimeMarkerChartObject(TimeMarkerChartObject chartObject) {
/* 34 */     super(chartObject);
/*    */   }
/*    */ 
/*    */   private void setDefaultDecorations() {
/* 38 */     setDecoration(IDecoratedChartObject.Placement.Beginning, IDecoratedChartObject.Decoration.FilledArrow);
/* 39 */     setDecoration(IDecoratedChartObject.Placement.End, IDecoratedChartObject.Decoration.FilledArrow);
/*    */   }
/*    */ 
/*    */   protected Rectangle drawFormattedLabel(Graphics2D g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*    */   {
/* 44 */     if ((drawingMode == ChartObjectDrawingMode.DEFAULT) || (drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS)) {
/* 45 */       return drawingsLabelHelper.drawTimeMarkerLabel(g, mapper, this, this.times[0], getLineWidth(), formattersManager.getDateFormatter());
/*    */     }
/* 47 */     return ZERO_RECTANGLE;
/*    */   }
/*    */ 
/*    */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager generalFormatter, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*    */   {
/* 56 */     if ((drawingMode == ChartObjectDrawingMode.DEFAULT) || (drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS)) {
/* 57 */       return drawingsLabelHelper.drawTimeMarkerText(g, mapper, this, this.times[0], getLineWidth(), getText(), labelDimension);
/*    */     }
/* 59 */     return ZERO_RECTANGLE;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 65 */     return "Time Marker";
/*    */   }
/*    */ 
/*    */   public TimeMarkerChartObject clone()
/*    */   {
/* 70 */     return new TimeMarkerChartObject(this);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.TimeMarkerChartObject
 * JD-Core Version:    0.6.0
 */