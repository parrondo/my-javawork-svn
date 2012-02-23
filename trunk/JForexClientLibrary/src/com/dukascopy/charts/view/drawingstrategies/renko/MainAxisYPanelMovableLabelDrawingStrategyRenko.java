/*    */ package com.dukascopy.charts.view.drawingstrategies.renko;
/*    */ 
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence;
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
/*    */ public class MainAxisYPanelMovableLabelDrawingStrategyRenko extends MainAxisYPanelMovableLabelDrawingStrategyAbstract
/*    */ {
/*    */   private final AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> dataSequenceProvider;
/*    */ 
/*    */   public MainAxisYPanelMovableLabelDrawingStrategyRenko(ChartState chartState, ValueFormatter valueFormatter, IValueToYMapper valueToYMapper, AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> dataSequenceProvider)
/*    */   {
/* 38 */     super(chartState, valueFormatter, valueToYMapper);
/*    */ 
/* 40 */     this.dataSequenceProvider = dataSequenceProvider;
/*    */   }
/*    */ 
/*    */   public void drawDataInProgressMark(Graphics g, JComponent jComponent)
/*    */   {
/* 45 */     RenkoDataSequence dataSequence = (RenkoDataSequence)getDataSequenceProvider().getDataSequence();
/* 46 */     if ((!ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING)) && (!dataSequence.isLatestDataVisible())) {
/* 47 */       return;
/*    */     }
/*    */ 
/* 50 */     RenkoData data = (RenkoData)getDataSequenceProvider().getLastKnownData();
/* 51 */     if (data == null) {
/* 52 */       return;
/*    */     }
/* 54 */     if (data.getInProgressRenko() != null) {
/* 55 */       data = data.getInProgressRenko();
/*    */     }
/*    */ 
/* 58 */     int y = getValueToYMapper().yv(data.getClose());
/*    */ 
/* 60 */     Color foregroundColor = getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/* 61 */     Color backgroundColor = getChartState().getTheme().getColor(getDataSequenceProvider().getOfferSide() == OfferSide.BID ? ITheme.ChartElement.AXIS_LABEL_BACKGROUND_BID : ITheme.ChartElement.AXIS_LABEL_BACKGROUND_ASK);
/*    */ 
/* 66 */     drawBackground(g, jComponent, foregroundColor, backgroundColor, y);
/* 67 */     drawText(g, jComponent, y, getValueFormatter().formatCandleInProgressPrice(data.getClose()));
/*    */   }
/*    */ 
/*    */   public AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> getDataSequenceProvider() {
/* 71 */     return this.dataSequenceProvider;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.renko.MainAxisYPanelMovableLabelDrawingStrategyRenko
 * JD-Core Version:    0.6.0
 */