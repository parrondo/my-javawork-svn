/*    */ package com.dukascopy.charts.view.drawingstrategies.tick;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*    */ import com.dukascopy.charts.settings.ChartSettings;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AbstractVisualisationDrawingStrategy;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Stroke;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class AbstractTickVisualisationDrawingStrategy extends AbstractVisualisationDrawingStrategy<TickDataSequence, TickData>
/*    */ {
/* 31 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTickVisualisationDrawingStrategy.class);
/*    */   protected final PathHelper pathHelper;
/*    */ 
/*    */   public AbstractTickVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 44 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper);
/*    */ 
/* 46 */     this.pathHelper = pathHelper;
/*    */   }
/*    */ 
/*    */   public final void drawData(Graphics g, JComponent jComponent)
/*    */   {
/* 51 */     TickDataSequence dataSequence = (TickDataSequence)this.dataSequenceProvider.getDataSequence();
/* 52 */     if (dataSequence.isEmpty()) {
/* 53 */       return;
/*    */     }
/*    */ 
/* 56 */     int diff = this.geometryCalculator.getDataUnitsCount() - this.dataSequenceProvider.intervalsCount(dataSequence) - this.dataSequenceProvider.getMargin();
/*    */ 
/* 58 */     if (LOGGER.isTraceEnabled()) {
/* 59 */       LOGGER.trace("dataUnitsCount = " + this.geometryCalculator.getDataUnitsCount() + " tickDataSize = " + this.dataSequenceProvider.intervalsCount(dataSequence) + " margin = " + this.dataSequenceProvider.getMargin() + " diff = " + diff);
/*    */     }
/*    */ 
/* 68 */     drawTicks((Graphics2D)g, jComponent, dataSequence, this.chartState.getTheme().getColor(ITheme.ChartElement.BID), this.chartState.getTheme().getColor(ITheme.ChartElement.ASK));
/*    */ 
/* 74 */     if ((ChartSettings.getBoolean(ChartSettings.Option.LAST_CANDLE_TRACKING)) && (ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING)))
/*    */     {
/* 76 */       drawLastCandleMarker(g, jComponent, (TickData)this.dataSequenceProvider.getLastKnownData());
/*    */     }
/*    */   }
/*    */ 
/*    */   void drawLastCandleMarker(Graphics g, JComponent jComponent, TickData lastTick) {
/* 81 */     if (lastTick == null) {
/* 82 */       return;
/*    */     }
/* 84 */     Graphics2D g2d = (Graphics2D)g;
/* 85 */     int x = 0;
/*    */ 
/* 87 */     int yAsk = this.valueToYMapper.yv(lastTick.ask);
/* 88 */     int yBid = this.valueToYMapper.yv(lastTick.bid);
/*    */ 
/* 90 */     Stroke stroke = g2d.getStroke();
/*    */ 
/* 92 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.LAST_CANDLE_TRACKING_LINE));
/* 93 */     g2d.setStroke(this.chartState.getTheme().getStroke(ITheme.StrokeElement.LAST_CANDLE_TRACKING_LINE_STROKE));
/* 94 */     g2d.drawLine(x + 1, yAsk, jComponent.getWidth(), yAsk);
/* 95 */     g2d.drawLine(x + 1, yBid, jComponent.getWidth(), yBid);
/*    */ 
/* 97 */     g2d.setStroke(stroke);
/*    */   }
/*    */ 
/*    */   protected abstract void drawTicks(Graphics2D paramGraphics2D, JComponent paramJComponent, TickDataSequence paramTickDataSequence, Color paramColor1, Color paramColor2);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.AbstractTickVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */