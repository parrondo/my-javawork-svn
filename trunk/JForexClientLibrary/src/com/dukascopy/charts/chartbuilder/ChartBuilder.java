/*      */ package com.dukascopy.charts.chartbuilder;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.CandlesDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.OrdersDataProviderManager;
/*      */ import com.dukascopy.charts.data.TicksDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.datacache.Data;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.priceaggregation.PointAndFigureDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.priceaggregation.PriceRangeDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.priceaggregation.RenkoDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.priceaggregation.TickBarDataSequenceProvider;
/*      */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorWrapperChangeListener;
/*      */ import com.dukascopy.charts.drawings.DrawingsFactory;
/*      */ import com.dukascopy.charts.drawings.DrawingsLabelHelperContainer;
/*      */ import com.dukascopy.charts.drawings.NewDrawingsCoordinator;
/*      */ import com.dukascopy.charts.drawings.PopupManagerForDrawings;
/*      */ import com.dukascopy.charts.indicators.IndicatorTooltipManager;
/*      */ import com.dukascopy.charts.indicators.IndicatorsManagerImpl;
/*      */ import com.dukascopy.charts.listeners.ChartSystemListenerManager;
/*      */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*      */ import com.dukascopy.charts.listeners.SwingComponentListenerBuilder;
/*      */ import com.dukascopy.charts.listeners.datachange.MainDataChangeListener;
/*      */ import com.dukascopy.charts.listeners.datachange.ProgressController;
/*      */ import com.dukascopy.charts.listeners.lock.ChartModeChangeListenersRegistry;
/*      */ import com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsMouseController;
/*      */ import com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsMouseControllerImpl;
/*      */ import com.dukascopy.charts.main.interfaces.IChartController;
/*      */ import com.dukascopy.charts.mappers.CompositeTimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.Mapper;
/*      */ import com.dukascopy.charts.mappers.time.CandleTimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*      */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.time.PointAndFigureTimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.time.PriceRangeTimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.time.RenkoTimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.time.TickBarTimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.time.TickTimeToXMapper;
/*      */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*      */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*      */ import com.dukascopy.charts.mappers.value.ValueFrame;
/*      */ import com.dukascopy.charts.mappers.value.ValueToYMapper;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*      */ import com.dukascopy.charts.mouseandkeyadaptors.MouseAndKeyAdapterBuilderImpl;
/*      */ import com.dukascopy.charts.orders.OrdersManagerImpl;
/*      */ import com.dukascopy.charts.persistence.ChartBean;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ThemeManager;
/*      */ import com.dukascopy.charts.tablebuilder.CandleDataTablePresentationManager;
/*      */ import com.dukascopy.charts.tablebuilder.ITablePresentationManager;
/*      */ import com.dukascopy.charts.tablebuilder.PointAndFigureDataTablePresentationManager;
/*      */ import com.dukascopy.charts.tablebuilder.PriceRangeDataTablePresentationManager;
/*      */ import com.dukascopy.charts.tablebuilder.RenkoTablePresentationManager;
/*      */ import com.dukascopy.charts.tablebuilder.TickBarDataTablePresentationManager;
/*      */ import com.dukascopy.charts.tablebuilder.TickDataTablePresentationManager;
/*      */ import com.dukascopy.charts.utils.PathHelper;
/*      */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*      */ import com.dukascopy.charts.view.displayabledatapart.DisplayableDataPartFactoryImpl;
/*      */ import com.dukascopy.charts.view.displayabledatapart.DrawingsManagerContainerImpl;
/*      */ import com.dukascopy.charts.view.displayabledatapart.StaticDynamicDataToDisplayableDataPartMapper;
/*      */ import com.dukascopy.charts.view.drawingstrategies.IDrawingStrategyFactory;
/*      */ import com.dukascopy.charts.view.drawingstrategies.candle.CandleDrawingStrategyFactory;
/*      */ import com.dukascopy.charts.view.drawingstrategies.nticksbar.TickBarDrawingStrategyFactory;
/*      */ import com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureDrawingStrategyFactory;
/*      */ import com.dukascopy.charts.view.drawingstrategies.pricerange.PriceRangeDrawingStrategyFactory;
/*      */ import com.dukascopy.charts.view.drawingstrategies.renko.RenkoDrawingStrategyFactory;
/*      */ import com.dukascopy.charts.view.drawingstrategies.tick.TickDrawingStrategyFactory;
/*      */ import com.dukascopy.charts.view.paintingtechnic.PaintingTechnicBuilder;
/*      */ import com.dukascopy.charts.view.staticdynamicdata.StaticDynamicDataManager;
/*      */ import com.dukascopy.charts.view.staticdynamicdata.ViewMode;
/*      */ import com.dukascopy.charts.view.swing.ChartSwingViewBuilder;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.EnumMap;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.TimeZone;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class ChartBuilder
/*      */ {
/*   98 */   private static final Logger LOGGER = LoggerFactory.getLogger(ChartBuilder.class);
/*   99 */   private static final SimpleDateFormat FORMATTER = new SimpleDateFormat() {  } ;
/*      */   private final ChartBean chartBean;
/*      */   private ChartState chartState;
/*      */   private MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState;
/*      */   private ViewModeChangeListenerRegistryImpl viewModeChangeListenerRegistryImpl;
/*      */   private ChartSystemListenerManager chartSystemListenerManager;
/*      */   private ProgressController progressController;
/*      */   private FormattersManager formattersManager;
/*      */   private TicksDataSequenceProvider ticksDataSequenceProvider;
/*      */   private CandlesDataSequenceProvider candlesDataSequenceProvider;
/*      */   private PriceRangeDataSequenceProvider priceRangeDataSequenceProvider;
/*      */   private PointAndFigureDataSequenceProvider pointAndFigureDataSequenceProvider;
/*      */   private TickBarDataSequenceProvider tickBarDataSequenceProvider;
/*      */   private RenkoDataSequenceProvider renkoDataSequenceProvider;
/*      */   private Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders;
/*      */   private OrdersDataProviderManager ordersDataProviderManager;
/*      */   private DataManagerImpl dataManagerImpl;
/*      */   private final ValueFrame valueFrame;
/*      */   private IValueToYMapper mainValueToYMapper;
/*  135 */   private final SubValueToYMapper subValueToYMapper = new SubValueToYMapper(this.chartState, 0.1D);
/*      */   private GeometryCalculator geometryCalculator;
/*      */   private ITimeToXMapper tickTimeToXMapper;
/*      */   private ITimeToXMapper candleTimeToXMapper;
/*      */   private ITimeToXMapper priceRangeTimeToXMapper;
/*      */   private ITimeToXMapper pointAndFigureTimeToXMapper;
/*      */   private ITimeToXMapper tickBarTimeToXMapper;
/*      */   private ITimeToXMapper renkoTimeToXMapper;
/*      */   private ITimeToXMapper compositeTimeToXMapper;
/*  147 */   private final PathHelper pathHelper = new PathHelper();
/*      */   private IDrawingStrategyFactory tickDrawingStrategyFactory;
/*      */   private IDrawingStrategyFactory candleDrawingStrategyFactory;
/*      */   private IDrawingStrategyFactory priceRangeDrawingStrategyFactory;
/*      */   private IDrawingStrategyFactory pointAndFigureDrawingStrategyFactory;
/*      */   private IDrawingStrategyFactory tickBarDrawingStrategyFactory;
/*      */   private IDrawingStrategyFactory renkoDrawingStrategyFactory;
/*      */   private IGeometryOperationManagerStrategy tickGeometryOperationManagerStrategy;
/*      */   private IDataOperationManagerStrategy tickDataOperationManagerStrategy;
/*      */   private IOperationManagerStrategy tickOperationManagerStrategy;
/*      */   private IGeometryOperationManagerStrategy candleGeometryOperationManagerStrategy;
/*      */   private IDataOperationManagerStrategy candleDataOperationManagerStrategy;
/*      */   private IOperationManagerStrategy candleOperationManagerStrategy;
/*      */   private IGeometryOperationManagerStrategy priceRangeGeometryOperationManagerStrategy;
/*      */   private IDataOperationManagerStrategy priceRangeDataOperationManagerStrategy;
/*      */   private IOperationManagerStrategy priceRangeOperationManagerStrategy;
/*      */   private IGeometryOperationManagerStrategy pointAndFigureGeometryOperationManagerStrategy;
/*      */   private IDataOperationManagerStrategy pointAndFigureDataOperationManagerStrategy;
/*      */   private IOperationManagerStrategy pointAndFigureOperationManagerStrategy;
/*      */   private IGeometryOperationManagerStrategy tickBarGeometryOperationManagerStrategy;
/*      */   private IDataOperationManagerStrategy tickBarDataOperationManagerStrategy;
/*      */   private IOperationManagerStrategy tickBarOperationManagerStrategy;
/*      */   private IGeometryOperationManagerStrategy renkoGeometryOperationManagerStrategy;
/*      */   private IDataOperationManagerStrategy renkoDataOperationManagerStrategy;
/*      */   private IOperationManagerStrategy renkoOperationManagerStrategy;
/*      */   private IMainOperationManager mainOperationManager;
/*      */   private IDataOperationManager mainDataOperationManager;
/*      */   private GeometryOperationManagerListener mainGeometryOperationManagerListener;
/*      */   private IndicatorsManagerImpl indicatorsManagerImpl;
/*      */   private IndicatorTooltipManager indicatorTooltipManager;
/*      */   private DrawingsManagerContainerImpl drawingsManagerContainerImpl;
/*      */   private OrdersManagerImpl ordersManagerImpl;
/*      */   private NewDrawingsCoordinator newDrawingsCoordinator;
/*      */   private MainMouseAndKeyController mainMouseAndKeyController;
/*      */   private MainDrawingsMouseAndKeyController mainDrawingsMouseAndKeyController;
/*      */   private MetaDrawingsMouseController metaDrawingsMouseController;
/*      */   private SubMouseAndKeyControllerFactory subMouseAndKeyControllerFactory;
/*      */   private PopupManagerForDrawings popupManagerForDrawings;
/*  209 */   private final DrawingsFactory drawingsFactory = new DrawingsFactory();
/*      */   private MainDataChangeListener mainDataChangeListener;
/*      */   private ISwingComponentListenerBuilder swingComponentListenerBuilder;
/*      */   private MouseAndKeyAdapterBuilder mouseAndKeyAdapterBuilder;
/*      */   private IDisplayableDataPartFactory displayableDataPartFactory;
/*  218 */   private final GuiRefresherImpl guiRefresherImpl = new GuiRefresherImpl();
/*      */   private IChartWrapper chartWrapper;
/*      */   private IChartController chartController;
/*      */   private DrawingsLabelHelperContainer drawingsLabelHelperContainer;
/*      */ 
/*  229 */   public ChartBuilder(ChartBean chartBean) { if ((chartBean.getInstrument() == null) || (chartBean.getPeriod() == null) || (chartBean.getOfferSide() == null) || (chartBean.getStartLoadingDataRunnable() == null))
/*      */     {
/*  234 */       throw new IllegalArgumentException("Parameters not set correctly: instrument=" + chartBean.getInstrument() + ", period=" + chartBean.getPeriod() + ", offerSide=" + chartBean.getOfferSide() + ", taskToBeRunAfterResize=" + chartBean.getStartLoadingDataRunnable());
/*      */     }
/*      */ 
/*  242 */     this.chartBean = chartBean;
/*      */ 
/*  244 */     if ((chartBean.getMinPrice() != -1.0D) && (chartBean.getMaxPrice() != -1.0D))
/*  245 */       this.valueFrame = new ValueFrame(chartBean.getMinPrice(), chartBean.getMaxPrice());
/*      */     else {
/*  247 */       this.valueFrame = new ValueFrame(1.0D, 2.0D);
/*      */     }
/*      */ 
/*  250 */     if (this.chartBean.getFeedDataProvider() == null)
/*  251 */       this.chartBean.setFeedDataProvider(FeedDataProvider.getDefaultInstance());
/*      */   }
/*      */ 
/*      */   public IChartController buildChart()
/*      */   {
/*  256 */     LOGGER.trace("Start building chart for : " + getChartBean().getInstrument() + ", " + getChartBean().getPeriod() + ", " + getChartBean().getOfferSide() + ", " + FORMATTER.format(new Long(getChartBean().getEndTime())));
/*      */ 
/*  264 */     buildHelpersAndCalculators();
/*      */ 
/*  266 */     buildInfrastructure();
/*  267 */     buildData();
/*      */ 
/*  269 */     buildChartDataTypeModels();
/*      */ 
/*  271 */     buildMappers();
/*  272 */     buildHelperManagers();
/*      */ 
/*  274 */     buildChartTickDrawingStrategyFactory();
/*  275 */     buildChartTickOperationManagers();
/*      */ 
/*  277 */     buildChartCandleDrawingStrategyFactory();
/*  278 */     buildChartCandleComponents();
/*      */ 
/*  280 */     buildChartPriceRangeComponents();
/*  281 */     buildChartPriceRangeDrawingStrategyFactory();
/*      */ 
/*  283 */     buildChartPointAndFigureDrawingStrategyFactory();
/*  284 */     buildChartPointAndFigureComponents();
/*      */ 
/*  286 */     buildChartTickBarDrawingStrategyFactory();
/*  287 */     buildChartTickBarComponents();
/*      */ 
/*  289 */     buildChartRenkoDrawingStrategyFactory();
/*  290 */     buildChartRenkoComponents();
/*      */ 
/*  292 */     buildMainManagers();
/*      */ 
/*  294 */     buildController();
/*  295 */     buildMouseControllers();
/*  296 */     preBuildGui();
/*  297 */     buildGui();
/*      */ 
/*  299 */     buildMainDataChangeListener();
/*      */ 
/*  301 */     registerListeners();
/*      */ 
/*  303 */     return this.chartController;
/*      */   }
/*      */ 
/*      */   private void buildHelpersAndCalculators() {
/*  307 */     this.geometryCalculator = new GeometryCalculator(getChartBean().getDataUnitWidth());
/*      */   }
/*      */ 
/*      */   private void buildInfrastructure() {
/*  311 */     LOGGER.trace("Building infrastructure");
/*      */ 
/*  313 */     ChartBean bean = getChartBean();
/*      */ 
/*  315 */     ITheme theme = bean.getTheme() == null ? ThemeManager.getTheme() : bean.getTheme();
/*      */ 
/*  318 */     this.chartState = new ChartStateImpl(bean.getInstrument(), bean.getPeriod(), bean.getOfferSide(), bean.getTicksPresentationType(), bean.getTimePeriodPresentationType(), bean.getPriceRangePresentationType(), bean.getPointAndFigurePresentationType(), bean.getTickBarPresentationType(), bean.getRenkoPresentationType(), bean.getReadOnly(), bean.getDataType(), bean.getPriceRange(), bean.getReversalAmount(), bean.getVerticalMovementEnabledAsBoolean(), theme);
/*      */ 
/*  336 */     this.mouseControllerMetaDrawingsState = new MouseControllerMetaDrawingsStateImpl();
/*      */ 
/*  338 */     this.chartSystemListenerManager = new ChartSystemListenerManager();
/*  339 */     this.progressController = new ProgressController(this.chartState);
/*  340 */     this.viewModeChangeListenerRegistryImpl = new ViewModeChangeListenerRegistryImpl(ViewMode.ALL_STATIC);
/*  341 */     this.formattersManager = new FormattersManager(this.chartState);
/*      */   }
/*      */ 
/*      */   private void buildData() {
/*  345 */     LOGGER.trace("building data layer...");
/*      */ 
/*  347 */     this.ordersDataProviderManager = new OrdersDataProviderManager(getChartBean().getInstrument(), this.chartState, getChartBean().getFeedDataProvider());
/*      */ 
/*  351 */     this.ticksDataSequenceProvider = new TicksDataSequenceProvider(getChartBean().getInstrument(), getChartBean().getEndTime(), 2000, getChartBean().getFeedDataProvider());
/*      */ 
/*  357 */     this.candlesDataSequenceProvider = new CandlesDataSequenceProvider(getChartBean().getInstrument(), Period.TICK == getChartBean().getPeriod() ? Period.TEN_SECS : getChartBean().getPeriod(), getChartBean().getOfferSide(), getChartBean().getEndTime(), 2000, getChartBean().getFeedDataProvider());
/*      */ 
/*  366 */     this.priceRangeDataSequenceProvider = new PriceRangeDataSequenceProvider(getChartBean().getInstrument(), getChartBean().getOfferSide(), getChartBean().getPriceRange(), getChartBean().getEndTime(), 2000, getChartBean().getFeedDataProvider());
/*      */ 
/*  375 */     this.pointAndFigureDataSequenceProvider = new PointAndFigureDataSequenceProvider(getChartBean().getInstrument(), getChartBean().getPeriod(), getChartBean().getOfferSide(), getChartBean().getPriceRange(), getChartBean().getReversalAmount(), getChartBean().getEndTime(), 2000, getChartBean().getFeedDataProvider());
/*      */ 
/*  386 */     this.tickBarDataSequenceProvider = new TickBarDataSequenceProvider(getChartBean().getInstrument(), getChartBean().getOfferSide(), getChartBean().getEndTime(), 2000, getChartBean().getTickBarSize(), getChartBean().getFeedDataProvider());
/*      */ 
/*  395 */     this.renkoDataSequenceProvider = new RenkoDataSequenceProvider(getChartBean().getInstrument(), getChartBean().getOfferSide(), getChartBean().getPriceRange(), getChartBean().getEndTime(), 2000, getChartBean().getFeedDataProvider());
/*      */ 
/*  405 */     this.allDataSequenceProviders = new EnumMap(DataType.class);
/*      */ 
/*  407 */     this.allDataSequenceProviders.put(DataType.TICKS, this.ticksDataSequenceProvider);
/*  408 */     this.allDataSequenceProviders.put(DataType.TIME_PERIOD_AGGREGATION, this.candlesDataSequenceProvider);
/*  409 */     this.allDataSequenceProviders.put(DataType.PRICE_RANGE_AGGREGATION, this.priceRangeDataSequenceProvider);
/*  410 */     this.allDataSequenceProviders.put(DataType.POINT_AND_FIGURE, this.pointAndFigureDataSequenceProvider);
/*  411 */     this.allDataSequenceProviders.put(DataType.TICK_BAR, this.tickBarDataSequenceProvider);
/*  412 */     this.allDataSequenceProviders.put(DataType.RENKO, this.renkoDataSequenceProvider);
/*      */ 
/*  415 */     this.dataManagerImpl = new DataManagerImpl(this.guiRefresherImpl, this.chartState, this.chartSystemListenerManager, this.allDataSequenceProviders);
/*      */ 
/*  422 */     for (Map.Entry entry : this.allDataSequenceProviders.entrySet())
/*  423 */       ((AbstractDataSequenceProvider)entry.getValue()).setIndicatorsContainer(this.dataManagerImpl);
/*      */   }
/*      */ 
/*      */   private void buildChartDataTypeModels()
/*      */   {
/*  428 */     LOGGER.trace("Building chart data type models");
/*      */ 
/*  430 */     this.tickTimeToXMapper = new TickTimeToXMapper(this.chartState, this.ticksDataSequenceProvider, this.geometryCalculator);
/*  431 */     this.candleTimeToXMapper = new CandleTimeToXMapper(this.chartState, this.candlesDataSequenceProvider, this.geometryCalculator);
/*  432 */     this.priceRangeTimeToXMapper = new PriceRangeTimeToXMapper(this.chartState, this.priceRangeDataSequenceProvider, this.geometryCalculator);
/*  433 */     this.pointAndFigureTimeToXMapper = new PointAndFigureTimeToXMapper(this.chartState, this.pointAndFigureDataSequenceProvider, this.geometryCalculator);
/*  434 */     this.tickBarTimeToXMapper = new TickBarTimeToXMapper(this.chartState, this.tickBarDataSequenceProvider, this.geometryCalculator);
/*  435 */     this.renkoTimeToXMapper = new RenkoTimeToXMapper(this.chartState, this.renkoDataSequenceProvider, this.geometryCalculator);
/*      */   }
/*      */ 
/*      */   private void buildMappers() {
/*  439 */     double yAxisPadding = getChartBean().getYAxisPadding() <= -1.0D ? 0.025D : getChartBean().getYAxisPadding();
/*  440 */     this.mainValueToYMapper = new ValueToYMapper(this.chartState, this.valueFrame, 0.025D, yAxisPadding);
/*  441 */     this.compositeTimeToXMapper = new CompositeTimeToXMapper(this.chartState, this.tickTimeToXMapper, this.candleTimeToXMapper, this.priceRangeTimeToXMapper, this.pointAndFigureTimeToXMapper, this.tickBarTimeToXMapper, this.renkoTimeToXMapper);
/*      */   }
/*      */ 
/*      */   private void buildHelperManagers()
/*      */   {
/*  453 */     LOGGER.trace("Building helper managers(drawings, orders)");
/*      */ 
/*  455 */     this.drawingsLabelHelperContainer = new DrawingsLabelHelperContainer(this.chartState.getTheme());
/*  456 */     this.drawingsManagerContainerImpl = new DrawingsManagerContainerImpl(this.compositeTimeToXMapper, this.mainValueToYMapper, this.subValueToYMapper, this.formattersManager, this.guiRefresherImpl, this.drawingsFactory, this.drawingsLabelHelperContainer, this.viewModeChangeListenerRegistryImpl, this.chartSystemListenerManager.getChartsActionListenerRegistry(), this.chartState, this.allDataSequenceProviders, this.geometryCalculator);
/*      */ 
/*  471 */     this.ordersManagerImpl = new OrdersManagerImpl(this.chartState, new Mapper(this.compositeTimeToXMapper, this.mainValueToYMapper), this.guiRefresherImpl, this.viewModeChangeListenerRegistryImpl, getChartBean(), this.ordersDataProviderManager);
/*      */ 
/*  480 */     this.indicatorTooltipManager = new IndicatorTooltipManager(this.compositeTimeToXMapper, this.mainValueToYMapper, this.formattersManager, this.chartState, this.allDataSequenceProviders);
/*      */ 
/*  488 */     this.indicatorsManagerImpl = new IndicatorsManagerImpl(this.chartSystemListenerManager.getChartsActionListenerRegistry(), this.pathHelper, this.indicatorTooltipManager);
/*      */ 
/*  494 */     this.chartWrapper = new IChartWrapper(getChartBean().getInstrument(), getChartBean().getOfferSide(), this.guiRefresherImpl, this.dataManagerImpl, this.drawingsManagerContainerImpl.getMainDrawingsManager(), this.drawingsManagerContainerImpl);
/*      */   }
/*      */ 
/*      */   private void buildChartTickDrawingStrategyFactory()
/*      */   {
/*  505 */     LOGGER.trace("building chart tick drawing strategy factory");
/*      */ 
/*  507 */     this.tickDrawingStrategyFactory = new TickDrawingStrategyFactory(this.chartState, this.mouseControllerMetaDrawingsState, this.formattersManager, this.geometryCalculator, this.tickTimeToXMapper, this.mainValueToYMapper, this.subValueToYMapper, this.ticksDataSequenceProvider, this.ordersManagerImpl, this.pathHelper, this.drawingsLabelHelperContainer);
/*      */   }
/*      */ 
/*      */   private void buildChartTickOperationManagers()
/*      */   {
/*  523 */     LOGGER.trace("Building chart tick operation managers");
/*      */ 
/*  525 */     this.tickOperationManagerStrategy = new OperationManagerStrategy(this.chartState, this.ticksDataSequenceProvider, this.geometryCalculator, this.tickTimeToXMapper, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  535 */     this.tickGeometryOperationManagerStrategy = new GeometryOperationManagerStrategy(this.chartState, this.geometryCalculator, this.ticksDataSequenceProvider, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  544 */     this.tickDataOperationManagerStrategy = new DataOperationManagerStrategy(this.chartState, this.ticksDataSequenceProvider, this.geometryCalculator, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper, this.tickGeometryOperationManagerStrategy);
/*      */   }
/*      */ 
/*      */   private void buildChartCandleDrawingStrategyFactory()
/*      */   {
/*  556 */     LOGGER.trace("Building chart candle drawing strategy factory");
/*      */ 
/*  558 */     this.candleDrawingStrategyFactory = new CandleDrawingStrategyFactory(this.chartState, this.mouseControllerMetaDrawingsState, this.formattersManager, this.geometryCalculator, this.candleTimeToXMapper, this.mainValueToYMapper, this.subValueToYMapper, this.ordersManagerImpl, this.candlesDataSequenceProvider, this.pathHelper, this.drawingsLabelHelperContainer);
/*      */   }
/*      */ 
/*      */   private void buildChartCandleComponents()
/*      */   {
/*  574 */     LOGGER.trace("Building chart candle components");
/*      */ 
/*  576 */     this.ordersDataProviderManager.setTicksDataSequenceProvider(this.ticksDataSequenceProvider);
/*  577 */     this.ordersDataProviderManager.setCandlesDataSequenceProvicer(this.candlesDataSequenceProvider);
/*  578 */     this.ordersDataProviderManager.setPriceRangeDataSequenceProvicer(this.priceRangeDataSequenceProvider);
/*  579 */     this.ordersDataProviderManager.setPointAndFigureDataSequenceProvider(this.pointAndFigureDataSequenceProvider);
/*  580 */     this.ordersDataProviderManager.setTickBarDataSequenceProvider(this.tickBarDataSequenceProvider);
/*  581 */     this.ordersDataProviderManager.setRenkoDataSequenceProvider(this.renkoDataSequenceProvider);
/*      */ 
/*  583 */     this.candleOperationManagerStrategy = new OperationManagerStrategy(this.chartState, this.candlesDataSequenceProvider, this.geometryCalculator, this.candleTimeToXMapper, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  593 */     this.candleGeometryOperationManagerStrategy = new GeometryOperationManagerStrategy(this.chartState, this.geometryCalculator, this.candlesDataSequenceProvider, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  602 */     this.candleDataOperationManagerStrategy = new DataOperationManagerStrategy(this.chartState, this.candlesDataSequenceProvider, this.geometryCalculator, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper, this.candleGeometryOperationManagerStrategy);
/*      */   }
/*      */ 
/*      */   private void buildChartPriceRangeComponents()
/*      */   {
/*  614 */     LOGGER.trace("Building chart range bar components");
/*      */ 
/*  616 */     this.priceRangeOperationManagerStrategy = new OperationManagerStrategy(this.chartState, this.priceRangeDataSequenceProvider, this.geometryCalculator, this.priceRangeTimeToXMapper, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  626 */     this.priceRangeGeometryOperationManagerStrategy = new GeometryOperationManagerStrategy(this.chartState, this.geometryCalculator, this.priceRangeDataSequenceProvider, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  636 */     this.priceRangeDataOperationManagerStrategy = new DataOperationManagerStrategy(this.chartState, this.priceRangeDataSequenceProvider, this.geometryCalculator, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper, this.priceRangeGeometryOperationManagerStrategy);
/*      */   }
/*      */ 
/*      */   private void buildChartPriceRangeDrawingStrategyFactory()
/*      */   {
/*  648 */     LOGGER.trace("Building chart range bar drawing strategy factory");
/*      */ 
/*  650 */     this.priceRangeDrawingStrategyFactory = new PriceRangeDrawingStrategyFactory(this.chartState, this.mouseControllerMetaDrawingsState, this.formattersManager, this.geometryCalculator, this.priceRangeTimeToXMapper, this.mainValueToYMapper, this.subValueToYMapper, this.ordersManagerImpl, this.priceRangeDataSequenceProvider, this.pathHelper, this.drawingsLabelHelperContainer);
/*      */   }
/*      */ 
/*      */   private void buildChartPointAndFigureComponents()
/*      */   {
/*  666 */     LOGGER.trace("Building chart and figure components");
/*      */ 
/*  668 */     this.pointAndFigureOperationManagerStrategy = new OperationManagerStrategy(this.chartState, this.pointAndFigureDataSequenceProvider, this.geometryCalculator, this.pointAndFigureTimeToXMapper, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  678 */     this.pointAndFigureGeometryOperationManagerStrategy = new GeometryOperationManagerStrategy(this.chartState, this.geometryCalculator, this.pointAndFigureDataSequenceProvider, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  688 */     this.pointAndFigureDataOperationManagerStrategy = new DataOperationManagerStrategy(this.chartState, this.pointAndFigureDataSequenceProvider, this.geometryCalculator, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper, this.pointAndFigureGeometryOperationManagerStrategy);
/*      */   }
/*      */ 
/*      */   private void buildChartPointAndFigureDrawingStrategyFactory()
/*      */   {
/*  701 */     LOGGER.trace("Building chart point and figure drawing strategy factory");
/*      */ 
/*  703 */     this.pointAndFigureDrawingStrategyFactory = new PointAndFigureDrawingStrategyFactory(this.chartState, this.mouseControllerMetaDrawingsState, this.formattersManager, this.geometryCalculator, this.pointAndFigureTimeToXMapper, this.mainValueToYMapper, this.subValueToYMapper, this.ordersManagerImpl, this.pointAndFigureDataSequenceProvider, this.pathHelper, this.drawingsLabelHelperContainer);
/*      */   }
/*      */ 
/*      */   private void buildChartTickBarComponents()
/*      */   {
/*  721 */     LOGGER.trace("Building chart TickBar components");
/*      */ 
/*  723 */     this.tickBarOperationManagerStrategy = new OperationManagerStrategy(this.chartState, this.tickBarDataSequenceProvider, this.geometryCalculator, this.tickBarTimeToXMapper, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  733 */     this.tickBarGeometryOperationManagerStrategy = new GeometryOperationManagerStrategy(this.chartState, this.geometryCalculator, this.tickBarDataSequenceProvider, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  742 */     this.tickBarDataOperationManagerStrategy = new DataOperationManagerStrategy(this.chartState, this.tickBarDataSequenceProvider, this.geometryCalculator, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper, this.tickBarGeometryOperationManagerStrategy);
/*      */   }
/*      */ 
/*      */   private void buildChartTickBarDrawingStrategyFactory()
/*      */   {
/*  754 */     LOGGER.trace("Building chart TickBar drawing strategy factory");
/*      */ 
/*  756 */     this.tickBarDrawingStrategyFactory = new TickBarDrawingStrategyFactory(this.chartState, this.mouseControllerMetaDrawingsState, this.formattersManager, this.geometryCalculator, this.tickBarTimeToXMapper, this.mainValueToYMapper, this.subValueToYMapper, this.ordersManagerImpl, this.tickBarDataSequenceProvider, this.pathHelper, this.drawingsLabelHelperContainer);
/*      */   }
/*      */ 
/*      */   private void buildChartRenkoComponents()
/*      */   {
/*  772 */     LOGGER.trace("Building chart Renko components");
/*      */ 
/*  775 */     this.renkoOperationManagerStrategy = new OperationManagerStrategy(this.chartState, this.renkoDataSequenceProvider, this.geometryCalculator, this.renkoTimeToXMapper, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  785 */     this.renkoGeometryOperationManagerStrategy = new GeometryOperationManagerStrategy(this.chartState, this.geometryCalculator, this.renkoDataSequenceProvider, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper);
/*      */ 
/*  794 */     this.renkoDataOperationManagerStrategy = new DataOperationManagerStrategy(this.chartState, this.renkoDataSequenceProvider, this.geometryCalculator, this.valueFrame, this.mainValueToYMapper, this.subValueToYMapper, this.renkoGeometryOperationManagerStrategy);
/*      */   }
/*      */ 
/*      */   private void buildChartRenkoDrawingStrategyFactory()
/*      */   {
/*  807 */     LOGGER.trace("Building chart Renko drawing strategy factory");
/*      */ 
/*  809 */     this.renkoDrawingStrategyFactory = new RenkoDrawingStrategyFactory(this.chartState, this.mouseControllerMetaDrawingsState, this.formattersManager, this.geometryCalculator, this.renkoTimeToXMapper, this.mainValueToYMapper, this.subValueToYMapper, this.ordersManagerImpl, this.renkoDataSequenceProvider, this.pathHelper, this.drawingsLabelHelperContainer);
/*      */   }
/*      */ 
/*      */   private void buildMainManagers()
/*      */   {
/*  825 */     LOGGER.trace("Building main managers(drawings, geometry, data)");
/*      */ 
/*  827 */     this.newDrawingsCoordinator = new NewDrawingsCoordinator(this.drawingsManagerContainerImpl, this.drawingsFactory, this.viewModeChangeListenerRegistryImpl, this.guiRefresherImpl, this.chartSystemListenerManager.getChartsActionListenerRegistry(), this.chartState);
/*      */ 
/*  836 */     this.mainOperationManager = new MainOperationManager(this.chartState, this.guiRefresherImpl, this.indicatorsManagerImpl, this.newDrawingsCoordinator, this.mouseControllerMetaDrawingsState, this.chartSystemListenerManager.getChartsActionListenerRegistry(), this.candleOperationManagerStrategy, this.tickOperationManagerStrategy, this.priceRangeOperationManagerStrategy, this.pointAndFigureOperationManagerStrategy, this.tickBarOperationManagerStrategy, this.renkoOperationManagerStrategy, this.dataManagerImpl, this.ordersManagerImpl, this.valueFrame);
/*      */ 
/*  855 */     this.drawingsManagerContainerImpl.setMainOperationManager(this.mainOperationManager);
/*      */ 
/*  857 */     this.mainGeometryOperationManagerListener = new GeometryOperationManagerImpl(this.chartState, this.tickGeometryOperationManagerStrategy, this.candleGeometryOperationManagerStrategy, this.priceRangeGeometryOperationManagerStrategy, this.pointAndFigureGeometryOperationManagerStrategy, this.tickBarGeometryOperationManagerStrategy, this.renkoGeometryOperationManagerStrategy);
/*      */ 
/*  867 */     this.mainDataOperationManager = new MainDataOperationManager(this.chartState, this.tickDataOperationManagerStrategy, this.candleDataOperationManagerStrategy, this.priceRangeDataOperationManagerStrategy, this.pointAndFigureDataOperationManagerStrategy, this.tickBarDataOperationManagerStrategy, this.renkoDataOperationManagerStrategy);
/*      */   }
/*      */ 
/*      */   private void buildController()
/*      */   {
/*  879 */     LOGGER.trace("Building Controller");
/*      */ 
/*  881 */     this.popupManagerForDrawings = new PopupManagerForDrawings(this.chartState, this.indicatorsManagerImpl, this.dataManagerImpl, this.drawingsManagerContainerImpl, this.chartSystemListenerManager.getChartsActionListenerRegistry(), this.guiRefresherImpl);
/*      */ 
/*  890 */     this.chartController = new ChartController(getChartBean(), this.guiRefresherImpl, this.dataManagerImpl, this.dataManagerImpl, this.drawingsManagerContainerImpl, this.ordersManagerImpl, this.mainOperationManager, this.chartWrapper, this.chartSystemListenerManager, this.chartState, this.geometryCalculator, this.popupManagerForDrawings);
/*      */   }
/*      */ 
/*      */   private void buildMouseControllers()
/*      */   {
/*  907 */     LOGGER.trace("Building mouse controllers(main, meta, drawings, orders)");
/*      */ 
/*  909 */     this.mainMouseAndKeyController = new MainMouseAndKeyControllerImpl(this.chartState, this.mainOperationManager, this.chartController);
/*      */ 
/*  915 */     this.mainDrawingsMouseAndKeyController = new MainDrawingsMouseAndKeyControllerImpl(this.guiRefresherImpl, this.drawingsManagerContainerImpl.getMainDrawingsManager(), this.newDrawingsCoordinator, this.dataManagerImpl, this.indicatorsManagerImpl, this.popupManagerForDrawings, this.geometryCalculator, this.pathHelper, this.ordersManagerImpl);
/*      */ 
/*  927 */     this.metaDrawingsMouseController = new MetaDrawingsMouseControllerImpl(this.mainOperationManager, this.chartState, this.mouseControllerMetaDrawingsState, this.viewModeChangeListenerRegistryImpl);
/*      */ 
/*  934 */     this.subMouseAndKeyControllerFactory = new SubMouseAndKeyControllerFactory(this.guiRefresherImpl, this.drawingsManagerContainerImpl, this.newDrawingsCoordinator, this.popupManagerForDrawings, this.ordersManagerImpl);
/*      */   }
/*      */ 
/*      */   private void preBuildGui()
/*      */   {
/*  944 */     LOGGER.trace("Pre building GUI");
/*      */ 
/*  946 */     this.mouseAndKeyAdapterBuilder = new MouseAndKeyAdapterBuilderImpl(this.mainOperationManager, this.ordersDataProviderManager, new Mapper(this.compositeTimeToXMapper, this.mainValueToYMapper), this.guiRefresherImpl, this.dataManagerImpl, this.chartState, this.mouseControllerMetaDrawingsState, this.mainMouseAndKeyController, this.metaDrawingsMouseController, this.mainDrawingsMouseAndKeyController, this.ordersManagerImpl, this.subMouseAndKeyControllerFactory);
/*      */ 
/*  961 */     this.swingComponentListenerBuilder = new SwingComponentListenerBuilder(this.guiRefresherImpl, this.mainGeometryOperationManagerListener, getChartBean().getStartLoadingDataRunnable());
/*      */ 
/*  967 */     this.displayableDataPartFactory = new DisplayableDataPartFactoryImpl(this.chartState, this.mouseControllerMetaDrawingsState, this.drawingsManagerContainerImpl, this.indicatorsManagerImpl, this.tickDrawingStrategyFactory, this.candleDrawingStrategyFactory, this.priceRangeDrawingStrategyFactory, this.pointAndFigureDrawingStrategyFactory, this.tickBarDrawingStrategyFactory, this.renkoDrawingStrategyFactory);
/*      */   }
/*      */ 
/*      */   private void buildGui()
/*      */   {
/*  982 */     LOGGER.trace("Building GUI");
/*      */ 
/*  984 */     if ((this.displayableDataPartFactory == null) || (this.mouseAndKeyAdapterBuilder == null) || (this.swingComponentListenerBuilder == null) || (this.chartSystemListenerManager == null))
/*      */     {
/*  989 */       throw new RuntimeException("Failed to build gui!  displayableDataPartFactory= " + this.displayableDataPartFactory + " mouseAndKeyAdapterBuilder=" + this.mouseAndKeyAdapterBuilder + " swingComponentListnerBuilder=" + this.swingComponentListenerBuilder + " chartSystemListenerManager=" + this.chartSystemListenerManager);
/*      */     }
/*      */ 
/*  997 */     StaticDynamicDataToDisplayableDataPartMapper staticDynamicDataToDisplayableDataPartMapper = new StaticDynamicDataToDisplayableDataPartMapper(this.displayableDataPartFactory);
/*  998 */     StaticDynamicDataManager staticDynamicDataManager = new StaticDynamicDataManager(this.viewModeChangeListenerRegistryImpl, staticDynamicDataToDisplayableDataPartMapper);
/*  999 */     PaintingTechnicBuilder paintingTechnicBuilder = new PaintingTechnicBuilder(staticDynamicDataManager, PaintingTechnicBuilder.createInvalidator());
/* 1000 */     ChartSwingViewBuilder chartSwingViewBuilder = new ChartSwingViewBuilder(this.mouseAndKeyAdapterBuilder, this.chartState);
/*      */ 
/* 1002 */     ITablePresentationManager tickTablePresentationManager = new TickDataTablePresentationManager(this.chartState, this.ticksDataSequenceProvider);
/* 1003 */     ITablePresentationManager candleTablePresentationManager = new CandleDataTablePresentationManager(this.chartState, this.candlesDataSequenceProvider);
/* 1004 */     ITablePresentationManager priceRangeTablePresentationManager = new PriceRangeDataTablePresentationManager(this.chartState, this.priceRangeDataSequenceProvider);
/* 1005 */     ITablePresentationManager pointAndFigureTablePresentationManager = new PointAndFigureDataTablePresentationManager(this.chartState, this.pointAndFigureDataSequenceProvider);
/* 1006 */     ITablePresentationManager tickBarTablePresentationManager = new TickBarDataTablePresentationManager(this.chartState, this.tickBarDataSequenceProvider);
/* 1007 */     ITablePresentationManager renkoTablePresentationManager = new RenkoTablePresentationManager(this.chartState, this.renkoDataSequenceProvider);
/*      */ 
/* 1009 */     ChartsGuiManager chartsGuiManager = new ChartsGuiManager(chartSwingViewBuilder, this.swingComponentListenerBuilder, paintingTechnicBuilder, this.drawingsManagerContainerImpl, this.chartState, tickTablePresentationManager, candleTablePresentationManager, priceRangeTablePresentationManager, pointAndFigureTablePresentationManager, tickBarTablePresentationManager, renkoTablePresentationManager);
/*      */ 
/* 1023 */     this.chartSystemListenerManager.getChartModeChangeListenersRegistry().addListener(chartsGuiManager);
/* 1024 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().registerListener(chartsGuiManager);
/*      */ 
/* 1026 */     IndicatorWrapperChangeListener wrapperListener = new IndicatorWrapperChangeListener();
/* 1027 */     this.chartSystemListenerManager.getChartsActionListenerRegistry().registerListener(wrapperListener);
/*      */ 
/* 1030 */     this.guiRefresherImpl.setChartsGuiManager(chartsGuiManager);
/*      */   }
/*      */ 
/*      */   private void buildMainDataChangeListener() {
/* 1034 */     LOGGER.trace("Building main data change listener");
/*      */ 
/* 1036 */     this.mainDataChangeListener = new MainDataChangeListener(getChartBean().getInstrument(), this.chartState, this.guiRefresherImpl, this.mainDataOperationManager, this.progressController);
/*      */ 
/* 1044 */     this.dataManagerImpl.addMainDataChangeListener(this.mainDataChangeListener);
/* 1045 */     this.ordersDataProviderManager.addOrdersDataChangeListener(this.mainDataChangeListener);
/*      */   }
/*      */ 
/*      */   private void registerListeners() {
/* 1049 */     this.progressController.registerListener(this.chartSystemListenerManager.getDisableEnableListenersRegistry());
/*      */   }
/*      */ 
/*      */   public ChartBean getChartBean() {
/* 1053 */     return this.chartBean;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ChartBuilder
 * JD-Core Version:    0.6.0
 */