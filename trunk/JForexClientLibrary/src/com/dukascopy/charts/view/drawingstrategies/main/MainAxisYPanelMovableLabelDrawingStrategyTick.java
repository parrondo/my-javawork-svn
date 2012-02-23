/*    */ package com.dukascopy.charts.view.drawingstrategies.main;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.settings.ChartSettings;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class MainAxisYPanelMovableLabelDrawingStrategyTick extends MainAxisYPanelMovableLabelDrawingStrategyAbstract
/*    */ {
/*    */   private final AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider;
/*    */ 
/*    */   public MainAxisYPanelMovableLabelDrawingStrategyTick(ValueFormatter valueFormatter, ChartState chartState, IValueToYMapper valueToYMapper, AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider)
/*    */   {
/* 29 */     super(chartState, valueFormatter, valueToYMapper);
/*    */ 
/* 31 */     this.dataSequenceProvider = dataSequenceProvider;
/*    */   }
/*    */ 
/*    */   public void drawDataInProgressMark(Graphics g, JComponent jComponent)
/*    */   {
/* 36 */     if (!ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING)) {
/* 37 */       return;
/*    */     }
/*    */ 
/* 40 */     TickData data = (TickData)getDataSequenceProvider().getLastKnownData();
/* 41 */     if (data == null) {
/* 42 */       return;
/*    */     }
/* 44 */     int yAsk = getValueToYMapper().yv(data.ask);
/* 45 */     int yBid = getValueToYMapper().yv(data.bid);
/*    */ 
/* 47 */     Color foregroundColor = getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/* 48 */     Color backgroundColorAsk = getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND_ASK);
/* 49 */     Color backgroundColorBid = getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND_BID);
/*    */ 
/* 51 */     drawBackground(g, jComponent, foregroundColor, backgroundColorAsk, yAsk);
/* 52 */     drawText(g, jComponent, yAsk, getValueFormatter().formatCandleInProgressPrice(data.ask));
/*    */ 
/* 54 */     drawBackground(g, jComponent, foregroundColor, backgroundColorBid, yBid);
/* 55 */     drawText(g, jComponent, yBid, getValueFormatter().formatCandleInProgressPrice(data.bid));
/*    */   }
/*    */ 
/*    */   public AbstractDataSequenceProvider<TickDataSequence, TickData> getDataSequenceProvider() {
/* 59 */     return this.dataSequenceProvider;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelMovableLabelDrawingStrategyTick
 * JD-Core Version:    0.6.0
 */