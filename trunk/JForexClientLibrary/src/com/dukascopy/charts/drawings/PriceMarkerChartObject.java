/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.api.drawings.IDecoratedChartObject.Decoration;
/*    */ import com.dukascopy.api.drawings.IDecoratedChartObject.Placement;
/*    */ import com.dukascopy.api.drawings.IPriceMarkerChartObject;
/*    */ import com.dukascopy.charts.mappers.IMapper;
/*    */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Rectangle;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class PriceMarkerChartObject extends HLineChartObject
/*    */   implements IPriceMarkerChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final String CHART_OBJECT_NAME = "Price Marker";
/*    */ 
/*    */   public PriceMarkerChartObject(String key)
/*    */   {
/* 19 */     super(key, IChart.Type.PRICEMARKER);
/* 20 */     setDefaultDecorations();
/*    */   }
/*    */ 
/*    */   public PriceMarkerChartObject() {
/* 24 */     super(null, IChart.Type.PRICEMARKER);
/* 25 */     setUnderEdit(true);
/* 26 */     setDefaultDecorations();
/*    */   }
/*    */ 
/*    */   public PriceMarkerChartObject(String key, double price1) {
/* 30 */     super(key, price1, IChart.Type.PRICEMARKER);
/* 31 */     setDefaultDecorations();
/*    */   }
/*    */ 
/*    */   public PriceMarkerChartObject(PriceMarkerChartObject chartObject) {
/* 35 */     super(chartObject);
/*    */   }
/*    */ 
/*    */   private void setDefaultDecorations() {
/* 39 */     setDecoration(IDecoratedChartObject.Placement.Beginning, IDecoratedChartObject.Decoration.FilledArrow);
/* 40 */     setDecoration(IDecoratedChartObject.Placement.End, IDecoratedChartObject.Decoration.FilledArrow);
/*    */   }
/*    */ 
/*    */   protected Rectangle drawFormattedLabel(Graphics2D g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*    */   {
/* 45 */     return drawingsLabelHelper.drawPriceMarkerLabelAndTextLabel(g, this, getAdjustedLabel(getText()), this.prices[0], mapper.getWidth(), getLineWidth(), mapper.yv(this.prices[0]), formattersManager.getValueFormatter());
/*    */   }
/*    */ 
/*    */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager generalFormatter, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*    */   {
/* 50 */     return ZERO_RECTANGLE;
/*    */   }
/*    */ 
/*    */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*    */   {
/* 55 */     int y = mapper.yv(this.prices[0]);
/*    */ 
/* 57 */     int beginningOffset = 0;
/* 58 */     int endOffset = 0;
/*    */ 
/* 60 */     Map decorations = getDecorations();
/* 61 */     if (decorations != null) {
/* 62 */       for (IDecoratedChartObject.Placement placement : decorations.keySet()) {
/* 63 */         IDecoratedChartObject.Decoration decoration = (IDecoratedChartObject.Decoration)decorations.get(placement);
/* 64 */         drawDecoration(placement, (IDecoratedChartObject.Decoration)decorations.get(placement), g, mapper.getWidth(), y);
/* 65 */         if (IDecoratedChartObject.Decoration.None != decoration) {
/* 66 */           if (IDecoratedChartObject.Placement.Beginning == placement) {
/* 67 */             beginningOffset = getLineWidth();
/*    */           }
/* 69 */           else if (IDecoratedChartObject.Placement.End == placement) {
/* 70 */             endOffset = getLineWidth();
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 76 */     g.drawLine(beginningOffset, y, labelDimension.x - 5, y);
/* 77 */     g.drawLine(labelDimension.x + labelDimension.width + 3, y, mapper.getWidth() - endOffset, y);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 82 */     return "Price Marker";
/*    */   }
/*    */ 
/*    */   public PriceMarkerChartObject clone()
/*    */   {
/* 87 */     return new PriceMarkerChartObject(this);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.PriceMarkerChartObject
 * JD-Core Version:    0.6.0
 */