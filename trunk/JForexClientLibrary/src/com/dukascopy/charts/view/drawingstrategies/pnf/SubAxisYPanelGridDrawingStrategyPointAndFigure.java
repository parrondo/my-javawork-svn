/*    */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*    */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelGridDrawingStrategy;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ 
/*    */ public class SubAxisYPanelGridDrawingStrategyPointAndFigure extends SubAxisYPanelGridDrawingStrategy
/*    */ {
/*    */   public SubAxisYPanelGridDrawingStrategyPointAndFigure(SubIndicatorGroup subIndicatorGroup, SubValueToYMapper subValueToYMapper, ChartState chartState)
/*    */   {
/* 25 */     super(subIndicatorGroup, subValueToYMapper, chartState);
/*    */   }
/*    */ 
/*    */   protected int getComponentWidth(Graphics g, Font axisFont)
/*    */   {
/* 31 */     return super.getComponentWidth(g, axisFont) + 30;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.SubAxisYPanelGridDrawingStrategyPointAndFigure
 * JD-Core Version:    0.6.0
 */