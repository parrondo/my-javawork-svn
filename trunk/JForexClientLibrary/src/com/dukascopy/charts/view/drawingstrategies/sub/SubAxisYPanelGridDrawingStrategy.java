/*    */ package com.dukascopy.charts.view.drawingstrategies.sub;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AxisYPanelDrawingStrategy;
/*    */ import java.awt.Graphics;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.List;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class SubAxisYPanelGridDrawingStrategy extends AxisYPanelDrawingStrategy
/*    */ {
/* 19 */   private final DecimalFormat formatter = new DecimalFormat("0.######");
/*    */   private final SubIndicatorGroup subIndicatorGroup;
/*    */   private final SubValueToYMapper subValueToYMapper;
/*    */ 
/*    */   public SubAxisYPanelGridDrawingStrategy(SubIndicatorGroup subIndicatorGroup, SubValueToYMapper subValueToYMapper, ChartState chartState)
/*    */   {
/* 29 */     super(chartState);
/*    */ 
/* 31 */     this.subIndicatorGroup = subIndicatorGroup;
/* 32 */     this.subValueToYMapper = subValueToYMapper;
/*    */   }
/*    */ 
/*    */   protected void drawLabelsAndLines(Graphics g, JComponent jComponent)
/*    */   {
/* 37 */     List indicatorWrappers = this.subIndicatorGroup.getSubIndicators();
/* 38 */     if (indicatorWrappers.isEmpty()) {
/* 39 */       return;
/*    */     }
/* 41 */     int height = jComponent.getHeight();
/* 42 */     for (int y = height - 30; y > 0; y -= 30) {
/* 43 */       double value = getValueToYMapper().vy(y);
/* 44 */       drawLine(g, y);
/* 45 */       drawLabel(g, jComponent, y, value);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void drawLine(Graphics g, int y) {
/* 50 */     g.drawLine(0, y, 4, y);
/*    */   }
/*    */ 
/*    */   private void drawLabel(Graphics g, JComponent jComponent, int y, double value) {
/* 54 */     SubAxisYFormatterUtils.setup(this.formatter, value);
/* 55 */     drawLabel(g, jComponent, this.formatter.format(value), y);
/*    */   }
/*    */ 
/*    */   private IValueToYMapper getValueToYMapper() {
/* 59 */     IndicatorWrapper basicIndicatorWrapper = this.subIndicatorGroup.getBasicSubIndicator();
/* 60 */     return this.subValueToYMapper.get(Integer.valueOf(basicIndicatorWrapper.getId()));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelGridDrawingStrategy
 * JD-Core Version:    0.6.0
 */