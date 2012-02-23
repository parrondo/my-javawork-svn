/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.drawings.IOrderLineChartObject;
/*    */ import com.dukascopy.charts.mappers.IMapper;
/*    */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*    */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.geom.Rectangle2D;
/*    */ 
/*    */ public class OrderLineChartObject extends HLineChartObject
/*    */   implements IOrderLineChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final String CHART_OBJECT_NAME = "Order Line";
/*    */ 
/*    */   public OrderLineChartObject()
/*    */   {
/*    */   }
/*    */ 
/*    */   public OrderLineChartObject(String key)
/*    */   {
/*    */   }
/*    */ 
/*    */   public OrderLineChartObject(String key, double price1)
/*    */   {
/* 25 */     super(key, price1);
/*    */   }
/*    */ 
/*    */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*    */   {
/* 30 */     super.drawChartObject(g, mapper, labelDimension, textDimension, formattersManager, drawingMode);
/*    */ 
/* 32 */     int y = mapper.yv(this.prices[0]);
/* 33 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 34 */     String formattedPrice = formattersManager.getValueFormatter().formatHorizontalLinePrice(this.prices[0]);
/* 35 */     double stringWidth = mapper.getWidth() - fontMetrics.getStringBounds(formattedPrice, g).getWidth() - 5.0D;
/* 36 */     g.drawString(formattedPrice, (int)stringWidth, y - 2);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 41 */     return "Order Line";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.OrderLineChartObject
 * JD-Core Version:    0.6.0
 */