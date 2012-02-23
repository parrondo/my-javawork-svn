/*     */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelMovableLabelDrawingStrategyAbstract;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class MainAxisYPanelMovableLabelDrawingStrategyPointAndFigure extends MainAxisYPanelMovableLabelDrawingStrategyAbstract
/*     */ {
/*     */   private final AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider;
/*     */ 
/*     */   public MainAxisYPanelMovableLabelDrawingStrategyPointAndFigure(ChartState chartState, ValueFormatter valueFormatter, IValueToYMapper valueToYMapper, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider)
/*     */   {
/*  36 */     super(chartState, valueFormatter, valueToYMapper);
/*     */ 
/*  38 */     this.dataSequenceProvider = dataSequenceProvider;
/*     */   }
/*     */ 
/*     */   public void drawDataInProgressMark(Graphics g, JComponent jComponent)
/*     */   {
/*  43 */     PointAndFigureDataSequence sequence = (PointAndFigureDataSequence)getDataSequenceProvider().getDataSequence();
/*  44 */     if ((!ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING)) && (!sequence.isLatestDataVisible())) {
/*  45 */       return;
/*     */     }
/*     */ 
/*  48 */     PointAndFigureData data = (PointAndFigureData)getDataSequenceProvider().getLastKnownData();
/*  49 */     if (data == null) {
/*  50 */       return;
/*     */     }
/*  52 */     int y = getValueToYMapper().yv(data.getClose());
/*     */ 
/*  54 */     Color foregroundColor = getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND);
/*  55 */     Color backgroundColor = getChartState().getTheme().getColor(getDataSequenceProvider().getOfferSide() == OfferSide.BID ? ITheme.ChartElement.AXIS_LABEL_BACKGROUND_BID : ITheme.ChartElement.AXIS_LABEL_BACKGROUND_ASK);
/*     */ 
/*  60 */     drawBackground(g, jComponent, foregroundColor, backgroundColor, y);
/*     */ 
/*  62 */     drawMark(g, jComponent, data.getClose());
/*     */   }
/*     */ 
/*     */   protected void drawMouseCursorMark(Graphics g, JComponent jComponent, int curMouseY)
/*     */   {
/*  67 */     PointAndFigureDataSequence sequence = (PointAndFigureDataSequence)getDataSequenceProvider().getDataSequence();
/*  68 */     drawBackground(g, jComponent, getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND), getChartState().getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND), curMouseY);
/*     */ 
/*  75 */     if (sequence.isEmpty()) {
/*  76 */       return;
/*     */     }
/*     */ 
/*  79 */     double price = getValueToYMapper().vy(curMouseY);
/*  80 */     drawMark(g, jComponent, price);
/*     */   }
/*     */ 
/*     */   private void drawMark(Graphics g, JComponent jComponent, double price)
/*     */   {
/*  88 */     String boxNumber = getBoxNumber(price);
/*  89 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  90 */     int boxNumberTextWidth = fontMetrics.stringWidth(boxNumber);
/*  91 */     int y = getValueToYMapper().yv(price);
/*     */ 
/*  93 */     drawText(g, jComponent, y, 4, boxNumber);
/*  94 */     drawText(g, jComponent, y, boxNumberTextWidth + 12, getValueFormatter().formatCandleInProgressPrice(price));
/*     */   }
/*     */ 
/*     */   private String getBoxNumber(double price) {
/*  98 */     int boxNumber = (int)(price / (this.dataSequenceProvider.getInstrument().getPipValue() * getChartState().getPriceRange().getPipCount()));
/*  99 */     String boxNumberStr = String.valueOf(boxNumber) + " |";
/* 100 */     return boxNumberStr;
/*     */   }
/*     */ 
/*     */   public AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> getDataSequenceProvider()
/*     */   {
/* 105 */     return this.dataSequenceProvider;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.MainAxisYPanelMovableLabelDrawingStrategyPointAndFigure
 * JD-Core Version:    0.6.0
 */