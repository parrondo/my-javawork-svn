/*    */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ import com.dukascopy.charts.settings.ChartSettings;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import java.awt.geom.Rectangle2D.Float;
/*    */ 
/*    */ public class CandleVisualisationDrawingStrategy extends AbstractCandleVisualisationDrawingStrategy
/*    */ {
/*    */   public CandleVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 31 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, pathHelper, true);
/*    */   }
/*    */ 
/*    */   protected void plotCandles(Graphics g, CandleDataSequence dataSequence)
/*    */   {
/* 36 */     int candleWidth = this.geometryCalculator.getDataUnitWidthWithoutOverhead();
/* 37 */     boolean drawCandleCanvas = ChartSettings.getBoolean(ChartSettings.Option.CANDLE_CANVAS);
/*    */ 
/* 39 */     for (int i = 0; i < dataSequence.size(); i++)
/* 40 */       plotSingleCandle(dataSequence, candleWidth, drawCandleCanvas, i);
/*    */   }
/*    */ 
/*    */   private void plotSingleCandle(CandleDataSequence dataSequence, int candleWidth, boolean drawCandleCanvas, int i)
/*    */   {
/* 45 */     CandleData candleData = (CandleData)dataSequence.getData(i);
/* 46 */     if (candleData == null) {
/* 47 */       return;
/*    */     }
/*    */ 
/* 50 */     int middle = this.timeToXMapper.xt(candleData.time);
/* 51 */     int leftPriceY = this.valueToYMapper.yv(candleData.open);
/* 52 */     int rightPriceY = this.valueToYMapper.yv(candleData.close);
/* 53 */     int highPriceY = this.valueToYMapper.yv(candleData.high);
/* 54 */     int lowPriceY = this.valueToYMapper.yv(candleData.low);
/*    */ 
/* 56 */     int x = middle - candleWidth / 2;
/* 57 */     int y = Math.min(leftPriceY, rightPriceY);
/* 58 */     int height = Math.abs(leftPriceY - rightPriceY) + 1;
/*    */     GeneralPath borderPath;
/*    */     GeneralPath bodyPath;
/*    */     GeneralPath borderPath;
/* 63 */     if (candleData.open > candleData.close) {
/* 64 */       GeneralPath bodyPath = this.negativePath;
/* 65 */       borderPath = this.negativeBorderPath;
/*    */     }
/*    */     else
/*    */     {
/*    */       GeneralPath borderPath;
/* 66 */       if (candleData.open < candleData.close) {
/* 67 */         GeneralPath bodyPath = this.positivePath;
/* 68 */         borderPath = this.positiveBorderPath;
/*    */       } else {
/* 70 */         bodyPath = this.neutralPath;
/* 71 */         borderPath = this.neutralPath;
/*    */       }
/*    */     }
/* 74 */     plotBody(bodyPath, borderPath, x, y, candleWidth, height, drawCandleCanvas);
/* 75 */     plotStick(borderPath, middle, y, height, highPriceY, lowPriceY);
/* 76 */     this.pathHelper.savePoints(i, middle, highPriceY, lowPriceY, leftPriceY, rightPriceY);
/*    */   }
/*    */ 
/*    */   void plotStick(GeneralPath path, int middle, int bodyTopY, int bodyHeight, int highPriceY, int lowPriceY) {
/* 80 */     if (highPriceY < bodyTopY) {
/* 81 */       path.moveTo(middle, highPriceY);
/* 82 */       path.lineTo(middle, bodyTopY - 1);
/*    */     }
/*    */ 
/* 85 */     float bodyBottomY = bodyTopY + bodyHeight;
/* 86 */     if (bodyBottomY < lowPriceY) {
/* 87 */       path.moveTo(middle, bodyBottomY);
/* 88 */       path.lineTo(middle, lowPriceY);
/*    */     }
/*    */   }
/*    */ 
/*    */   void plotBody(GeneralPath bodyPath, GeneralPath borderPath, int x, int y, int candleWidth, int height, boolean drawCandleCanvas) {
/* 93 */     if (height > 1) {
/* 94 */       if (candleWidth > 1) {
/* 95 */         Shape body = new Rectangle2D.Float(x, y, candleWidth - (drawCandleCanvas ? 1 : 0), height - (drawCandleCanvas ? 1 : 0));
/* 96 */         bodyPath.append(body, false);
/* 97 */         if (drawCandleCanvas)
/* 98 */           borderPath.append(body, false);
/*    */       }
/*    */       else {
/* 101 */         borderPath.moveTo(x, y);
/* 102 */         borderPath.lineTo(x, y + height);
/*    */       }
/*    */     } else {
/* 105 */       borderPath.moveTo(x, y);
/* 106 */       borderPath.lineTo(x + candleWidth - 1, y);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.CandleVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */