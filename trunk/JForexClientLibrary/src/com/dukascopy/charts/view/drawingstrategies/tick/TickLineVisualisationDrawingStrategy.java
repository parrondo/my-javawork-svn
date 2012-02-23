/*    */ package com.dukascopy.charts.view.drawingstrategies.tick;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.Ellipse2D.Float;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ class TickLineVisualisationDrawingStrategy extends AbstractTickVisualisationDrawingStrategy
/*    */ {
/* 24 */   final GeneralPath askPath = new GeneralPath();
/* 25 */   final GeneralPath bidPath = new GeneralPath();
/* 26 */   final GeneralPath askPoints = new GeneralPath();
/* 27 */   final GeneralPath bidPoints = new GeneralPath();
/*    */ 
/* 29 */   boolean drawPoints = false;
/*    */ 
/*    */   public TickLineVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 40 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, pathHelper);
/*    */   }
/*    */ 
/*    */   protected void drawTicks(Graphics2D g2, JComponent jComponent, TickDataSequence tickDataSequence, Color positiveColor, Color negativeColor)
/*    */   {
/* 45 */     clear();
/* 46 */     renderTicks(jComponent, tickDataSequence);
/*    */ 
/* 48 */     draw(g2, this.askPath, this.askPoints, positiveColor);
/* 49 */     draw(g2, this.bidPath, this.bidPoints, negativeColor);
/*    */   }
/*    */ 
/*    */   void draw(Graphics2D g2, Shape shape, Shape pointsShape, Color color) {
/* 53 */     g2.setColor(color);
/* 54 */     g2.draw(shape);
/* 55 */     if (this.drawPoints) {
/* 56 */       g2.draw(pointsShape);
/* 57 */       g2.fill(pointsShape);
/*    */     }
/*    */   }
/*    */ 
/*    */   void clear() {
/* 62 */     this.askPath.reset();
/* 63 */     this.askPath.moveTo(-1.0F, -1.0F);
/*    */ 
/* 65 */     this.bidPath.reset();
/* 66 */     this.bidPath.moveTo(-1.0F, -1.0F);
/*    */ 
/* 68 */     this.askPoints.reset();
/* 69 */     this.bidPoints.reset();
/*    */ 
/* 71 */     this.drawPoints = (this.timeToXMapper.getBarWidth() > 15);
/*    */   }
/*    */ 
/*    */   void renderTicks(JComponent jComponent, TickDataSequence tickDataSequence) {
/* 75 */     int i = tickDataSequence.getExtraBefore();
/* 76 */     if (i > 0) {
/* 77 */       i--;
/*    */     }
/* 79 */     int sequenceSize = tickDataSequence.size(true) - tickDataSequence.getExtraAfter();
/* 80 */     if (tickDataSequence.getExtraAfter() > 0) {
/* 81 */       sequenceSize++;
/*    */     }
/* 83 */     TickData[] ticks = (TickData[])tickDataSequence.getData();
/*    */ 
/* 85 */     boolean firstPoint = true;
/* 86 */     for (; i < sequenceSize; i++) {
/* 87 */       TickData tick = ticks[i];
/*    */ 
/* 89 */       float x = this.timeToXMapper.xt(tick.time);
/* 90 */       float askY = this.valueToYMapper.yv(tick.ask);
/* 91 */       float bidY = this.valueToYMapper.yv(tick.bid);
/*    */ 
/* 93 */       if (firstPoint) {
/* 94 */         this.bidPath.moveTo(x, bidY);
/* 95 */         this.askPath.moveTo(x, askY);
/* 96 */         firstPoint = false;
/*    */       } else {
/* 98 */         this.bidPath.lineTo(x, bidY);
/* 99 */         this.askPath.lineTo(x, askY);
/*    */       }
/*    */ 
/* 102 */       if (this.drawPoints) {
/* 103 */         this.askPoints.append(new Ellipse2D.Float((int)x - 2, (int)askY - 2, 5.0F, 5.0F), false);
/* 104 */         this.bidPoints.append(new Ellipse2D.Float((int)x - 2, (int)bidY - 2, 5.0F, 5.0F), false);
/*    */       }
/*    */ 
/* 107 */       this.pathHelper.savePoints(i, x, askY, bidY, askY, bidY);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.TickLineVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */