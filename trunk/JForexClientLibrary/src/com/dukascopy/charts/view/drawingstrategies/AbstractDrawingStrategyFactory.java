/*     */ package com.dukascopy.charts.view.drawingstrategies;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelperContainer;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.orders.OrdersDrawingManager;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.common.CommonAxisXPanelMovableLabelDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainAxisYPanelGridDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartGridDrawingStrategy;
/*     */ import com.dukascopy.charts.view.drawingstrategies.sub.SubChartIndicatorInfoDrawingStrategy;
/*     */ 
/*     */ public abstract class AbstractDrawingStrategyFactory<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDrawingStrategyFactory
/*     */ {
/*     */   protected final ChartState chartState;
/*     */   protected final MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState;
/*     */   protected final OrdersDrawingManager ordersDrawingManager;
/*     */   protected final PathHelper pathHelper;
/*     */   protected final FormattersManager formattersManager;
/*     */   protected final ITimeToXMapper timeToXMapper;
/*     */   protected final GeometryCalculator geometryCalculator;
/*     */   protected final IValueToYMapper valueToYMapper;
/*     */   protected final SubValueToYMapper subValueToYMapper;
/*     */   protected final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   protected final DrawingsLabelHelperContainer drawingsLabelHelperContainer;
/*     */ 
/*     */   public AbstractDrawingStrategyFactory(ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, FormattersManager formattersManager, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, SubValueToYMapper subValueToYMapper, OrdersDrawingManager ordersDrawingManager, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, PathHelper pathHelper, DrawingsLabelHelperContainer drawingsLabelHelperContainer)
/*     */   {
/*  54 */     this.chartState = chartState;
/*  55 */     this.mouseControllerMetaDrawingsState = mouseControllerMetaDrawingsState;
/*  56 */     this.formattersManager = formattersManager;
/*  57 */     this.geometryCalculator = geometryCalculator;
/*  58 */     this.timeToXMapper = timeToXMapper;
/*  59 */     this.valueToYMapper = valueToYMapper;
/*  60 */     this.subValueToYMapper = subValueToYMapper;
/*  61 */     this.ordersDrawingManager = ordersDrawingManager;
/*  62 */     this.pathHelper = pathHelper;
/*  63 */     this.dataSequenceProvider = dataSequenceProvider;
/*  64 */     this.drawingsLabelHelperContainer = drawingsLabelHelperContainer;
/*     */   }
/*     */ 
/*     */   protected DrawingsLabelHelper getLabelHelperForMain() {
/*  68 */     return this.drawingsLabelHelperContainer.getLabelHelperForMain();
/*     */   }
/*     */ 
/*     */   protected DrawingsLabelHelper getLabelHelper(SubIndicatorGroup subIndicatorGroup) {
/*  72 */     int subWindowId = subIndicatorGroup.getSubWindowId();
/*  73 */     return this.drawingsLabelHelperContainer.getLabelHelperForSubWindow(subWindowId);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainAxisYPanelGridDrawingStrategy()
/*     */   {
/*  78 */     return new MainAxisYPanelGridDrawingStrategy(this.chartState, this.formattersManager.getValueFormatter(), this.valueToYMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createCommonAxisXPanelMovableLabelDrawingStrategy()
/*     */   {
/*  87 */     return new CommonAxisXPanelMovableLabelDrawingStrategy(this.formattersManager.getDateFormatter(), this.chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper);
/*     */   }
/*     */ 
/*     */   public IDrawingStrategy createMainChartGridDrawingStrategy()
/*     */   {
/*  98 */     return new MainChartGridDrawingStrategy(this.chartState, this.valueToYMapper);
/*     */   }
/*     */ 
/*     */   public final IDrawingStrategy createSubIndicatorsInfoDrawingStrategy(SubIndicatorGroup subIndicatorGroup, ChartState chartState)
/*     */   {
/* 106 */     return new SubChartIndicatorInfoDrawingStrategy(subIndicatorGroup, chartState, this.dataSequenceProvider, this.geometryCalculator, this.timeToXMapper);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.AbstractDrawingStrategyFactory
 * JD-Core Version:    0.6.0
 */