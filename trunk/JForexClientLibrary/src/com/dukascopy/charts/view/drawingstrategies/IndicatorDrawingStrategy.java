/*    */ package com.dukascopy.charts.view.drawingstrategies;
/*    */ 
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*    */ import com.dukascopy.charts.view.drawingstrategies.util.IndicatorLevelDrawingHelper;
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Point;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public abstract class IndicatorDrawingStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/* 20 */   private static final float[] DASH_LINE_PATTERN = { 6.0F, 4.0F };
/* 21 */   private static final float[] DOT_LINE_PATTERN = { 1.0F, 6.0F };
/* 22 */   private static final float[] DASH_DOT_LINE_PATTERN = { 6.0F, 4.0F, 1.0F, 4.0F };
/* 23 */   private static final float[] DASH_DOT_DOT_LINE_PATTERN = { 6.0F, 4.0F, 1.0F, 4.0F, 1.0F, 4.0F };
/*    */ 
/* 25 */   private static final BasicStroke SOLID_LINE_STROKE = new BasicStroke(1.0F, 0, 0, 10.0F, null, 0.0F);
/* 26 */   private static final BasicStroke DASHDOTDOT_LINE_STROKE = new BasicStroke(1.0F, 0, 0, 10.0F, DASH_DOT_DOT_LINE_PATTERN, 0.0F);
/* 27 */   private static final BasicStroke DASHDOT_LINE_STROKE = new BasicStroke(1.0F, 0, 0, 10.0F, DASH_DOT_LINE_PATTERN, 0.0F);
/* 28 */   private static final BasicStroke DASH_LINE_STROKE = new BasicStroke(1.0F, 0, 0, 10.0F, DASH_LINE_PATTERN, 0.0F);
/* 29 */   private static final BasicStroke DOT_LINE_STROKE = new BasicStroke(2.0F, 1, 1, 10.0F, DOT_LINE_PATTERN, 0.0F);
/*    */ 
/* 31 */   protected final GeneralPath tmpPath = new GeneralPath();
/* 32 */   protected final List<Point> tmpHandlesPoints = new ArrayList();
/*    */   protected final IndicatorLevelDrawingHelper indicatorLevelDrawingHelper;
/*    */   protected final PathHelper pathHelper;
/*    */ 
/*    */   public abstract void draw(Graphics paramGraphics, JComponent paramJComponent);
/*    */ 
/*    */   protected IndicatorDrawingStrategy(PathHelper pathHelper, ChartState chartState)
/*    */   {
/* 41 */     this.pathHelper = pathHelper;
/* 42 */     this.indicatorLevelDrawingHelper = new IndicatorLevelDrawingHelper(chartState);
/*    */   }
/*    */ 
/*    */   public static BasicStroke getStroke(OutputParameterInfo.DrawingStyle drawingStyle, int width) {
/* 46 */     switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$DrawingStyle[drawingStyle.ordinal()]) {
/*    */     case 1:
/*    */     case 2:
/* 49 */       return getStroke(width, SOLID_LINE_STROKE);
/*    */     case 3:
/*    */     case 4:
/* 52 */       return getStroke(width, DASH_LINE_STROKE);
/*    */     case 5:
/*    */     case 6:
/* 55 */       return getStroke(width, DASHDOT_LINE_STROKE);
/*    */     case 7:
/*    */     case 8:
/* 58 */       return getStroke(width, DASHDOTDOT_LINE_STROKE);
/*    */     case 9:
/*    */     case 10:
/* 61 */       return getStroke(width, DOT_LINE_STROKE);
/*    */     }
/* 63 */     return null;
/*    */   }
/*    */ 
/*    */   private static BasicStroke getStroke(int width, BasicStroke stroke)
/*    */   {
/* 68 */     return new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), scale(stroke.getDashArray(), width), stroke.getDashPhase());
/*    */   }
/*    */ 
/*    */   private static float[] scale(float[] pattern, int ratio)
/*    */   {
/* 79 */     if (pattern == null) {
/* 80 */       return null;
/*    */     }
/* 82 */     float[] result = new float[pattern.length];
/* 83 */     for (int i = 0; i < pattern.length; i++) {
/* 84 */       pattern[i] *= ratio;
/*    */     }
/* 86 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.IndicatorDrawingStrategy
 * JD-Core Version:    0.6.0
 */