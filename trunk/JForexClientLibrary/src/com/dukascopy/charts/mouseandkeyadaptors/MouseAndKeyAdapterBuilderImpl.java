/*     */ package com.dukascopy.charts.mouseandkeyadaptors;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*     */ import com.dukascopy.charts.chartbuilder.MainDrawingsMouseAndKeyController;
/*     */ import com.dukascopy.charts.chartbuilder.MainMouseAndKeyController;
/*     */ import com.dukascopy.charts.chartbuilder.MouseAndKeyAdapterBuilder;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.chartbuilder.SubMouseAndKeyControllerFactory;
/*     */ import com.dukascopy.charts.data.IOrdersDataProviderManager;
/*     */ import com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsMouseController;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.orders.OrdersMouseController;
/*     */ import com.dukascopy.charts.view.paintingtechnic.InvalidationContent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class MouseAndKeyAdapterBuilderImpl
/*     */   implements MouseAndKeyAdapterBuilder
/*     */ {
/*     */   final IMainOperationManager mainOperationManager;
/*     */   final IOrdersDataProviderManager ordersDataProviderManager;
/*     */   final IMapper mapper;
/*     */   final GuiRefresher guiRefresher;
/*     */   final IDataManagerAndIndicatorsContainer indicatorsContainer;
/*     */   final ChartState chartState;
/*     */   final MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState;
/*     */   final MainMouseAndKeyController mainMouseAndKeyController;
/*     */   final MetaDrawingsMouseController metaDrawingsMouseController;
/*     */   final MainDrawingsMouseAndKeyController mainDrawingsMouseAndKeyController;
/*     */   final OrdersMouseController ordersMouseController;
/*     */   final SubMouseAndKeyControllerFactory subMouseAndKeyControllerFactory;
/*     */ 
/*     */   public MouseAndKeyAdapterBuilderImpl(IMainOperationManager mainOperationManager, IOrdersDataProviderManager ordersDataProviderManager, IMapper mapper, GuiRefresher guiRefresher, IDataManagerAndIndicatorsContainer indicatorsContainer, ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, MainMouseAndKeyController mainMouseAndKeyController, MetaDrawingsMouseController metaDrawingsMouseController, MainDrawingsMouseAndKeyController mainDrawingsMouseAndKeyController, OrdersMouseController ordersMouseController, SubMouseAndKeyControllerFactory subMouseAndKeyControllerFactory)
/*     */   {
/*  55 */     this.mainOperationManager = mainOperationManager;
/*  56 */     this.ordersDataProviderManager = ordersDataProviderManager;
/*  57 */     this.mapper = mapper;
/*     */ 
/*  59 */     this.guiRefresher = guiRefresher;
/*  60 */     this.indicatorsContainer = indicatorsContainer;
/*  61 */     this.chartState = chartState;
/*  62 */     this.mouseControllerMetaDrawingsState = mouseControllerMetaDrawingsState;
/*     */ 
/*  64 */     this.mainMouseAndKeyController = mainMouseAndKeyController;
/*  65 */     this.metaDrawingsMouseController = metaDrawingsMouseController;
/*  66 */     this.mainDrawingsMouseAndKeyController = mainDrawingsMouseAndKeyController;
/*  67 */     this.ordersMouseController = ordersMouseController;
/*     */ 
/*  69 */     this.subMouseAndKeyControllerFactory = subMouseAndKeyControllerFactory;
/*     */   }
/*     */ 
/*     */   public ChartsMouseAndKeyAdapter createMouseAndKeyAdapterForSub(InvalidationContent contentToBeInvalidated, SubIndicatorGroup subIndicatorGroup) {
/*  73 */     if (InvalidationContent.SUBCHARTPANEL == contentToBeInvalidated) {
/*  74 */       return new SubChartPanelMouseAndKeyAdapter(subIndicatorGroup, this.mainOperationManager, this.mainMouseAndKeyController, this.subMouseAndKeyControllerFactory.createSubDrawingsMouseAndKeyController(subIndicatorGroup), this.guiRefresher, this.indicatorsContainer, this.chartState);
/*     */     }
/*     */ 
/*  83 */     if (InvalidationContent.SUBAXISYPANEL == contentToBeInvalidated) {
/*  84 */       return new SubAxisYPanelMouseAndKeyAdapter(subIndicatorGroup, this.mainOperationManager);
/*     */     }
/*  86 */     return new ChartsMouseAndKeyAdapter();
/*     */   }
/*     */ 
/*     */   public ChartsMouseAndKeyAdapter createMouseAndKeyAdapterForMain(InvalidationContent invalidationContent)
/*     */   {
/*  91 */     if (InvalidationContent.MAINCHARTPANEL == invalidationContent) {
/*  92 */       return new MainChartPanelMouseAndKeyAdapter(this.chartState, this.guiRefresher, this.mainMouseAndKeyController, this.metaDrawingsMouseController, this.mainDrawingsMouseAndKeyController, this.ordersMouseController);
/*     */     }
/*     */ 
/* 100 */     if (InvalidationContent.MAINAXISYPANEL == invalidationContent)
/* 101 */       return new MainAxisYPanelMouseAndKeyAdapter(this.chartState, this.mapper, this.mainOperationManager, this.ordersDataProviderManager);
/* 102 */     if (InvalidationContent.COMMONAXISXPANEL == invalidationContent) {
/* 103 */       return new CommonAxisXPanelMouseAndKeyAdapter(this.mainOperationManager);
/*     */     }
/* 105 */     return new ChartsMouseAndKeyAdapter();
/*     */   }
/*     */ 
/*     */   public MouseAdapter getMouseAndKeyAdapterForDivisionPanel(JComponent chartViewContainer, JComponent mainChartView)
/*     */   {
/* 110 */     return new DivisionPanelMouseAndKeyAdapter(chartViewContainer, mainChartView);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.MouseAndKeyAdapterBuilderImpl
 * JD-Core Version:    0.6.0
 */