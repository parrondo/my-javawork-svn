/*     */ package com.dukascopy.charts.view.drawingstrategies.tick;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelperContainer;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*     */ import com.dukascopy.charts.orders.OrdersDrawingManager;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IOrdersDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.AbstractDrawingStrategyFactory;
/*     */ import com.dukascopy.charts.view.drawingstrategies.common.CommonAxisXPanelGridDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYIndicatorValueLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelMovableLabelDrawingStrategyTick;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartPeriodSeparatorsDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYIndicatorValueLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelGridDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubAxisYPanelMovableLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubChartPeriodSeparatorsDrawingStrategy;
/*     */ 
/*     */ public class TickDrawingStrategyFactory extends AbstractDrawingStrategyFactory<TickDataSequence, TickData>
/*     */ {
/*     */   public TickDrawingStrategyFactory(ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, FormattersManager formattersManager, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, SubValueToYMapper subValueToYMapper, AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider, OrdersDrawingManager ordersDrawingManager, PathHelper pathHelper, DrawingsLabelHelperContainer drawingsLabelHelperContainer)
/*     */   {
/*  44 */     super(chartState, mouseControllerMetaDrawingsState, formattersManager, geometryCalculator, timeToXMapper, valueToYMapper, subValueToYMapper, ordersDrawingManager, dataSequenceProvider, pathHelper, drawingsLabelHelperContainer);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createRawDrawingStrategy()
/*     */   {
/*  61 */     return new TickRawDataStrategy(this.chartState, new TickLineVisualisationDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper, this.valueToYMapper, this.pathHelper), new TickBarVisualisationDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper, this.valueToYMapper, this.pathHelper));
/*     */   }
/*     */ 
/*     */   public final IDrawingStrategy createMainChartPanelMetaDrawingsDrawingStrategy()
/*     */   {
/*  86 */     return new MainChartPanelMetaDrawingsDrawingsStrategyTick(this.timeToXMapper, this.valueToYMapper, this.dataSequenceProvider, this.formattersManager.getValueFormatter(), this.chartState, this.mouseControllerMetaDrawingsState);
/*     */   }
/*     */ 
/*     */   public IOrdersDrawingStrategy createOrdersDrawingStrategy()
/*     */   {
/*  98 */     return new OrdersTickDrawingLogic(this.ordersDrawingManager, this.timeToXMapper, this.valueToYMapper, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createIndicatorsDrawingStrategy()
/*     */   {
/* 103 */     return new TicksIndicatorDrawingStrategy(this.dataSequenceProvider, this.geometryCalculator, this.valueToYMapper, this.timeToXMapper, this.pathHelper, getLabelHelperForMain(), this.formattersManager, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYPanelMovableLabelDrawingStrategy()
/*     */   {
/* 117 */     return new MainAxisYPanelMovableLabelDrawingStrategyTick(this.formattersManager.getValueFormatter(), this.chartState, this.valueToYMapper, this.dataSequenceProvider);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYIndicatorValueLabelDrawingStrategy()
/*     */   {
/* 127 */     return new MainAxisYIndicatorValueLabelDrawingStrategy(this.dataSequenceProvider, this.valueToYMapper, this.timeToXMapper, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYIndicatorValueLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 137 */     return new SubAxisYIndicatorValueLabelDrawingStrategy(subIndicatorGroup, this.chartState, this.dataSequenceProvider, this.subValueToYMapper, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubIndicatorsDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 148 */     return new TicksSubIndicatorDrawingStrategy(subIndicatorGroup, this.geometryCalculator, this.dataSequenceProvider, this.subValueToYMapper, this.timeToXMapper, this.pathHelper, getLabelHelper(subIndicatorGroup), this.formattersManager, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYPanelGridDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 163 */     return new SubAxisYPanelGridDrawingStrategy(subIndicatorGroup, this.subValueToYMapper, this.chartState);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubAxisYPanelMovableLabelDrawingStrategy(SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 172 */     return new SubAxisYPanelMovableLabelDrawingStrategy(subIndicatorGroup, this.chartState, this.dataSequenceProvider, this.subValueToYMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createCommonAxisXPanelGridDrawingStrategy()
/*     */   {
/* 182 */     return new CommonAxisXPanelGridDrawingStrategy(this.chartState, this.timeToXMapper, this.dataSequenceProvider, this.formattersManager.getDateFormatter());
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainChartPeriodSeparatorsDrawingStrategy()
/*     */   {
/* 192 */     return new MainChartPeriodSeparatorsDrawingStrategy(this.chartState, this.dataSequenceProvider, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createSubChartPeriodSeparatorsDrawingStrategy()
/*     */   {
/* 197 */     return new SubChartPeriodSeparatorsDrawingStrategy(this.chartState, this.dataSequenceProvider, this.timeToXMapper);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.TickDrawingStrategyFactory
 * JD-Core Version:    0.6.0
 */