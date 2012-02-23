/*    */ package com.dukascopy.charts.view.drawingstrategies.nticksbar;
/*    */ 
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence;
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
/*    */ public class MainAxisYPanelMovableLabelDrawingStrategyTickBar extends MainAxisYPanelMovableLabelDrawingStrategyAbstract
/*    */ {
/*    */   private final AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> dataSequenceProvider;
/*    */ 
/*    */   public MainAxisYPanelMovableLabelDrawingStrategyTickBar(ChartState chartState, ValueFormatter valueFormatter, IValueToYMapper valueToYMapper, AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> dataSequenceProvider)
/*    */   {
/* 35 */     super(chartState, valueFormatter, valueToYMapper);
/*    */ 
/* 37 */     this.dataSequenceProvider = dataSequenceProvider;
/*    */   }
/*    */ 
/*    */   public void drawDataInProgressMark(Graphics g, JComponent jComponent)
/*    */   {
/* 42 */     TickBarDataSequence dataSequence = (TickBarDataSequence)getDataSequenceProvider().getDataSequence();
/* 43 */     if ((!ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING)) && (!dataSequence.isLatestDataVisible())) {
/* 44 */       return;
/*    */     }
/*    */ 
/* 47 */     TickBarData data = (TickBarData)getDataSequenceProvider().getLastKnownData();
/* 48 */     if (data == null) {
/* 49 */       return;
/*    */     }
/* 51 */     int y = getValueToYMapper().yv(data.getClose());
/*    */ 
/* 53 */     Color foregroundColor = getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/* 54 */     Color backgroundColor = getChartState().getTheme().getColor(getDataSequenceProvider().getOfferSide() == OfferSide.BID ? ITheme.ChartElement.AXIS_LABEL_BACKGROUND_BID : ITheme.ChartElement.AXIS_LABEL_BACKGROUND_ASK);
/*    */ 
/* 59 */     drawBackground(g, jComponent, foregroundColor, backgroundColor, y);
/* 60 */     drawText(g, jComponent, y, getValueFormatter().formatCandleInProgressPrice(data.getClose()));
/*    */   }
/*    */ 
/*    */   public AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> getDataSequenceProvider() {
/* 64 */     return this.dataSequenceProvider;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.nticksbar.MainAxisYPanelMovableLabelDrawingStrategyTickBar
 * JD-Core Version:    0.6.0
 */