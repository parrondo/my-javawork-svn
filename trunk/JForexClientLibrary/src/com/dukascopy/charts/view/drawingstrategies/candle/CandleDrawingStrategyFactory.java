/*     */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelperContainer;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*     */ import com.dukascopy.charts.orders.OrdersDrawingManager;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.AbstractDrawingStrategyFactory;
/*     */ import com.dukascopy.charts.view.drawingstrategies.common.CommonAxisXPanelGridDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYIndicatorValueLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartPeriodSeparatorsDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYIndicatorValueLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelGridDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelMovableLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubChartPeriodSeparatorsDrawingStrategy;
/*     */ 
/*     */ public class CandleDrawingStrategyFactory extends AbstractDrawingStrategyFactory<CandleDataSequence, CandleData>
/*     */ {
/*     */   public CandleDrawingStrategyFactory(ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, FormattersManager formattersManager, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, SubValueToYMapper subValueToYMapper, OrdersDrawingManager ordersDrawingManager, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider, PathHelper pathHelper, DrawingsLabelHelperContainer drawingsLabelHelperContainer)
/*     */   {
/*  42 */     super(chartState, mouseControllerMetaDrawingsState, formattersManager, geometryCalculator, timeToXMapper, valueToYMapper, subValueToYMapper, ordersDrawingManager, dataSequenceProvider, pathHelper, drawingsLabelHelperContainer);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createRawDrawingStrategy()
/*     */   {
/*  59 */     return new CandleRawDataStrategy(this.chartState, new LineVisualisationDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper, this.valueToYMapper, this.pathHelper), new BarVisualisationDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper, this.valueToYMapper, this.pathHelper), new CandleVisualisationDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper, this.valueToYMapper, this.pathHelper));
/*     */   }
/*     */ 
/*     */   public final IDrawingStrategy createMainChartPanelMetaDrawingsDrawingStrategy()
/*     */   {
/*  93 */     return new MainChartPanelMetaDrawingsDrawingsStrategyCandle(this.formattersManager.getValueFormatter(), this.chartState, this.mouseControllerMetaDrawingsState, this.geometryCalculator, this.valueToYMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createIndicatorsDrawingStrategy()
/*     */   {
/* 104 */     return new CandlesIndicatorDrawingStrategy(this.dataSequenceProvider, this.geometryCalculator, this.valueToYMapper, this.timeToXMapper, this.pathHelper, getLabelHelperForMain(), this.formattersManager, this.chartState);
/*     */   }
/*     */ 
/*     */   public OrdersCandleDrawingLogic<CandleDataSequence, CandleData> createOrdersDrawingStrategy()
/*     */   {
/* 118 */     return new OrdersCandleDrawingLogic(this.ordersDrawingManager, this.dataSequenceProvider, this.timeToXMapper, this.valueToYMapper, this.geometryCalculator, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYPanelMovableLabelDrawingStrategy()
/*     */   {
/* 130 */     return new MainAxisYPanelMovableLabelDrawingStrategyCandle(this.chartState, this.formattersManager.getValueFormatter(), this.valueToYMapper, this.dataSequenceProvider);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYIndicatorValueLabelDrawingStrategy()
/*     */   {
/* 140 */     return new MainAxisYIndicatorValueLabelDrawingStrategy(this.dataSequenceProvider, this.valueToYMapper, this.timeToXMapper, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubIndicatorsDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 150 */     return new CandlesSubIndicatorDrawingStrategy(subIndicatorGroup, this.geometryCalculator, this.dataSequenceProvider, this.subValueToYMapper, this.timeToXMapper, this.pathHelper, getLabelHelper(subIndicatorGroup), this.formattersManager, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYPanelGridDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 165 */     return new SubAxisYPanelGridDrawingStrategy(subIndicatorGroup, this.subValueToYMapper, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYPanelMovableLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 174 */     return new SubAxisYPanelMovableLabelDrawingStrategy(subIndicatorGroup, this.chartState, this.dataSequenceProvider, this.subValueToYMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYIndicatorValueLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 184 */     return new SubAxisYIndicatorValueLabelDrawingStrategy(subIndicatorGroup, this.chartState, this.dataSequenceProvider, this.subValueToYMapper, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createCommonAxisXPanelGridDrawingStrategy()
/*     */   {
/* 195 */     return new CommonAxisXPanelGridDrawingStrategy(this.chartState, this.timeToXMapper, this.dataSequenceProvider, this.formattersManager.getDateFormatter());
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainChartPeriodSeparatorsDrawingStrategy()
/*     */   {
/* 205 */     return new MainChartPeriodSeparatorsDrawingStrategy(this.chartState, this.dataSequenceProvider, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubChartPeriodSeparatorsDrawingStrategy()
/*     */   {
/* 210 */     return new SubChartPeriodSeparatorsDrawingStrategy(this.chartState, this.dataSequenceProvider, this.timeToXMapper);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.CandleDrawingStrategyFactory
 * JD-Core Version:    0.6.0
 */