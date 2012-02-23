/*     */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelperContainer;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*     */ import com.dukascopy.charts.orders.OrdersDrawingManager;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IOrdersDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.AbstractDrawingStrategyFactory;
/*     */ import com.dukascopy.charts.view.drawingstrategies.DefaultPriceAggregationIndicatorDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.DefaultPriceAggregationSubIndicatorsDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.candle.MainChartPanelMetaDrawingsDrawingsStrategyCandle;
/*     */ import com.dukascopy.charts.view.drawingstrategies.candle.OrdersCandleDrawingLogic;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYIndicatorValueLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartPeriodSeparatorsDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYIndicatorValueLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelMovableLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubChartPeriodSeparatorsDrawingStrategy;
/*     */ 
/*     */ public class PointAndFigureDrawingStrategyFactory extends AbstractDrawingStrategyFactory<PointAndFigureDataSequence, PointAndFigureData>
/*     */ {
/*     */   public PointAndFigureDrawingStrategyFactory(ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, FormattersManager formattersManager, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, SubValueToYMapper subValueToYMapper, OrdersDrawingManager ordersDrawingManager, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider, PathHelper pathHelper, DrawingsLabelHelperContainer drawingsLabelHelperContainer)
/*     */   {
/*  53 */     super(chartState, mouseControllerMetaDrawingsState, formattersManager, geometryCalculator, timeToXMapper, valueToYMapper, subValueToYMapper, ordersDrawingManager, dataSequenceProvider, pathHelper, drawingsLabelHelperContainer);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createCommonAxisXPanelGridDrawingStrategy()
/*     */   {
/*  71 */     return new PointAndFigureAxisXPanelGridDrawingStrategy(this.chartState, this.timeToXMapper, this.dataSequenceProvider, this.formattersManager.getDateFormatter(), this.geometryCalculator);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createIndicatorsDrawingStrategy()
/*     */   {
/*  82 */     return new DefaultPriceAggregationIndicatorDrawingStrategy(this.dataSequenceProvider, this.geometryCalculator, this.valueToYMapper, this.timeToXMapper, this.pathHelper, getLabelHelperForMain(), this.formattersManager, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYPanelMovableLabelDrawingStrategy()
/*     */   {
/*  96 */     return new MainAxisYPanelMovableLabelDrawingStrategyPointAndFigure(this.chartState, this.formattersManager.getValueFormatter(), this.valueToYMapper, this.dataSequenceProvider);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYIndicatorValueLabelDrawingStrategy()
/*     */   {
/* 106 */     return new MainAxisYIndicatorValueLabelDrawingStrategy(this.dataSequenceProvider, this.valueToYMapper, this.timeToXMapper, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYIndicatorValueLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 116 */     return new SubAxisYIndicatorValueLabelDrawingStrategy(subIndicatorGroup, this.chartState, this.dataSequenceProvider, this.subValueToYMapper, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainChartPanelMetaDrawingsDrawingStrategy()
/*     */   {
/* 127 */     return new MainChartPanelMetaDrawingsDrawingsStrategyCandle(this.formattersManager.getValueFormatter(), this.chartState, this.mouseControllerMetaDrawingsState, this.geometryCalculator, this.valueToYMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainChartPeriodSeparatorsDrawingStrategy()
/*     */   {
/* 138 */     return new MainChartPeriodSeparatorsDrawingStrategy(this.chartState, this.dataSequenceProvider, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IOrdersDrawingStrategy createOrdersDrawingStrategy()
/*     */   {
/* 147 */     return new OrdersCandleDrawingLogic(this.ordersDrawingManager, this.dataSequenceProvider, this.timeToXMapper, this.valueToYMapper, this.geometryCalculator, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createRawDrawingStrategy()
/*     */   {
/* 159 */     return new PointAndFigureRawDataStrategy(this.chartState, new PointAndFigureAsBoxVisualisationDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper, this.valueToYMapper, this.pathHelper), new PointAndFigureAsBarVisualisationDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper, this.valueToYMapper, this.pathHelper));
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYPanelGridDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 185 */     return new SubAxisYPanelGridDrawingStrategyPointAndFigure(subIndicatorGroup, this.subValueToYMapper, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYPanelMovableLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 194 */     return new SubAxisYPanelMovableLabelDrawingStrategy(subIndicatorGroup, this.chartState, this.dataSequenceProvider, this.subValueToYMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubChartPeriodSeparatorsDrawingStrategy()
/*     */   {
/* 204 */     return new SubChartPeriodSeparatorsDrawingStrategy(this.chartState, this.dataSequenceProvider, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubIndicatorsDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 209 */     return new DefaultPriceAggregationSubIndicatorsDrawingStrategy(subIndicatorGroup, this.geometryCalculator, this.dataSequenceProvider, this.subValueToYMapper, this.timeToXMapper, this.pathHelper, getLabelHelper(subIndicatorGroup), this.formattersManager, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createCommonAxisXPanelMovableLabelDrawingStrategy()
/*     */   {
/* 225 */     return new PointAndFigureAxisXPanelMovableLabelDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYPanelGridDrawingStrategy()
/*     */   {
/* 236 */     return new MainAxisYPanelGridDrawingStrategyPointAndFigure(this.chartState, this.formattersManager.getValueFormatter(), this.valueToYMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainChartGridDrawingStrategy()
/*     */   {
/* 245 */     return new PointAndFigureGridDrawingStrategy(this.chartState, this.valueToYMapper, this.dataSequenceProvider, this.geometryCalculator);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureDrawingStrategyFactory
 * JD-Core Version:    0.6.0
 */