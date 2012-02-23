/*    */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelGridDrawingStrategy;
/*    */ import com.dukascopy.charts.view.drawingstrategies.util.GridUtil;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class MainAxisYPanelGridDrawingStrategyPointAndFigure extends MainAxisYPanelGridDrawingStrategy
/*    */ {
/*    */   public static final int BOX_MARKER_WIDTH = 30;
/*    */   private static final int PRICE_LABEL_X_OFFSET = 14;
/*    */ 
/*    */   public MainAxisYPanelGridDrawingStrategyPointAndFigure(ChartState chartState, ValueFormatter valueFormatter, IValueToYMapper valueToYMapper)
/*    */   {
/* 29 */     super(chartState, valueFormatter, valueToYMapper);
/*    */   }
/*    */ 
/*    */   protected void drawLabelsAndLines(Graphics g, JComponent component)
/*    */   {
/* 34 */     super.drawLabelsAndLines(g, component);
/*    */ 
/* 36 */     Instrument instrument = this.chartState.getInstrument();
/*    */ 
/* 38 */     int height = component.getHeight();
/* 39 */     double gridSize = getGridSize();
/* 40 */     double max = this.valueToYMapper.vy(0);
/* 41 */     double min = this.valueToYMapper.vy(height);
/*    */ 
/* 43 */     if ((!GridUtil.isValid(min)) || (!GridUtil.isValid(max))) {
/* 44 */       return;
/*    */     }
/*    */ 
/* 47 */     double pipValue = instrument.getPipValue();
/* 48 */     int scale = GridUtil.calculateScale(this.valueToYMapper.getValuesInOnePixel(), instrument, gridSize);
/*    */ 
/* 50 */     int iterations = 0;
/* 51 */     double boxSize = this.chartState.getPriceRange().getPipCount() * this.chartState.getInstrument().getPipValue();
/*    */ 
/* 53 */     for (double value = GridUtil.calculateNearest(instrument, max, gridSize); value >= min; value -= pipValue * gridSize * scale) {
/* 54 */       int y = this.valueToYMapper.yv(value);
/*    */ 
/* 56 */       if ((y >= 0) && (y < height)) {
/* 57 */         g.drawLine(0, y, 30, y);
/*    */       }
/*    */ 
/* 60 */       String boxNumber = String.valueOf(getBoxNumber(value, boxSize));
/* 61 */       g.drawString(boxNumber, 2, y - 1);
/*    */ 
/* 64 */       if (iterations++ > 1000) {
/* 65 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 69 */     drawBoxesAndPricesSeparator(g, component);
/*    */   }
/*    */ 
/*    */   private int getBoxNumber(double price, double boxSize)
/*    */   {
/* 74 */     return (int)(price / boxSize);
/*    */   }
/*    */ 
/*    */   private void drawBoxesAndPricesSeparator(Graphics g, JComponent component) {
/* 78 */     g.drawLine(30, 0, 30, component.getHeight());
/*    */   }
/*    */ 
/*    */   protected int getComponentWidth(Graphics g, Font axisFont)
/*    */   {
/* 83 */     return super.getComponentWidth(g, axisFont) + 30;
/*    */   }
/*    */ 
/*    */   protected int getPriceLineMarkerX()
/*    */   {
/* 88 */     return 30;
/*    */   }
/*    */ 
/*    */   protected int getPriceLabelX(int componentWidth, int labelWidth)
/*    */   {
/* 93 */     return 14 + super.getPriceLabelX(componentWidth, labelWidth);
/*    */   }
/*    */ 
/*    */   protected double getGridSize()
/*    */   {
/* 98 */     return this.chartState.getPriceRange().getPipCount();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.MainAxisYPanelGridDrawingStrategyPointAndFigure
 * JD-Core Version:    0.6.0
 */