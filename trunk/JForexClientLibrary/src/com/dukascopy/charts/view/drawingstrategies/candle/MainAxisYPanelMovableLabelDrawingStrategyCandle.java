/*    */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*    */ 
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.settings.ChartSettings;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelMovableLabelDrawingStrategyAbstract;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class MainAxisYPanelMovableLabelDrawingStrategyCandle extends MainAxisYPanelMovableLabelDrawingStrategyAbstract
/*    */ {
/*    */   private final AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider;
/*    */ 
/*    */   public MainAxisYPanelMovableLabelDrawingStrategyCandle(ChartState chartState, ValueFormatter valueFormatter, IValueToYMapper valueToYMapper, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider)
/*    */   {
/* 31 */     super(chartState, valueFormatter, valueToYMapper);
/* 32 */     this.dataSequenceProvider = dataSequenceProvider;
/*    */   }
/*    */ 
/*    */   public void drawDataInProgressMark(Graphics g, JComponent jComponent)
/*    */   {
/* 37 */     CandleDataSequence candleDataSequence = (CandleDataSequence)getDataSequenceProvider().getDataSequence();
/* 38 */     if ((!ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING)) && (!candleDataSequence.isLatestDataVisible())) {
/* 39 */       return;
/*    */     }
/*    */ 
/* 42 */     CandleData candleData = (CandleData)getDataSequenceProvider().getLastKnownData();
/* 43 */     if (candleData == null) {
/* 44 */       return;
/*    */     }
/* 46 */     int y = getValueToYMapper().yv(candleData.getClose());
/*    */ 
/* 48 */     Color foregroundColor = getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/* 49 */     Color backgroundColor = getChartState().getTheme().getColor(getDataSequenceProvider().getOfferSide() == OfferSide.BID ? ITheme.ChartElement.AXIS_LABEL_BACKGROUND_BID : ITheme.ChartElement.AXIS_LABEL_BACKGROUND_ASK);
/*    */ 
/* 54 */     drawBackground(g, jComponent, foregroundColor, backgroundColor, y);
/* 55 */     drawText(g, jComponent, y, getValueFormatter().formatCandleInProgressPrice(candleData.getClose()));
/*    */   }
/*    */ 
/*    */   public AbstractDataSequenceProvider<CandleDataSequence, CandleData> getDataSequenceProvider() {
/* 59 */     return this.dataSequenceProvider;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.MainAxisYPanelMovableLabelDrawingStrategyCandle
 * JD-Core Version:    0.6.0
 */