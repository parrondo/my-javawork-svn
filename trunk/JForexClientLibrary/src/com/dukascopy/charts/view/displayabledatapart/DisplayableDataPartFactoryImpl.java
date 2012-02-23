/*     */ package com.dukascopy.charts.view.displayabledatapart;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.IDisplayableDataPartFactory;
/*     */ import com.dukascopy.charts.chartbuilder.IDisplayableDataPartFactory.PART;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.indicators.IndicatorsManagerImpl;
/*     */ import com.dukascopy.charts.view.drawingstrategies.IDrawingStrategyFactory;
/*     */ import com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DisplayableDataPartFactoryImpl
/*     */   implements IDisplayableDataPartFactory
/*     */ {
/*     */   private final ChartState chartState;
/*     */   private final MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState;
/*     */   private final IDrawingsManagerContainer drawingsManagerContainer;
/*     */   private final IndicatorsManagerImpl indicatorsManager;
/*     */   private final BackGroundDisplayableDataPart backGroundDisplayableDataPart;
/*  28 */   private final Map<DataType, IDrawingStrategyFactory> drawingStrategyFactoriesMap = new HashMap();
/*     */ 
/*     */   public DisplayableDataPartFactoryImpl(ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, IDrawingsManagerContainer drawingsManagerContainer, IndicatorsManagerImpl indicatorsManager, IDrawingStrategyFactory tickDrawingStrategyFactory, IDrawingStrategyFactory candleDrawingStrategyFactory, IDrawingStrategyFactory priceRangeDrawingStrategyFactory, IDrawingStrategyFactory pointAndFigureDrawingStrategyFactory, IDrawingStrategyFactory tickBarDrawingStrategyFactory, IDrawingStrategyFactory renkoDrawingStrategyFactory)
/*     */   {
/*  42 */     this.chartState = chartState;
/*  43 */     this.mouseControllerMetaDrawingsState = mouseControllerMetaDrawingsState;
/*  44 */     this.drawingsManagerContainer = drawingsManagerContainer;
/*  45 */     this.indicatorsManager = indicatorsManager;
/*     */ 
/*  47 */     this.backGroundDisplayableDataPart = new BackGroundDisplayableDataPart(chartState);
/*     */ 
/*  49 */     this.drawingStrategyFactoriesMap.put(DataType.TICKS, tickDrawingStrategyFactory);
/*  50 */     this.drawingStrategyFactoriesMap.put(DataType.TIME_PERIOD_AGGREGATION, candleDrawingStrategyFactory);
/*  51 */     this.drawingStrategyFactoriesMap.put(DataType.PRICE_RANGE_AGGREGATION, priceRangeDrawingStrategyFactory);
/*  52 */     this.drawingStrategyFactoriesMap.put(DataType.POINT_AND_FIGURE, pointAndFigureDrawingStrategyFactory);
/*  53 */     this.drawingStrategyFactoriesMap.put(DataType.TICK_BAR, tickBarDrawingStrategyFactory);
/*  54 */     this.drawingStrategyFactoriesMap.put(DataType.RENKO, renkoDrawingStrategyFactory);
/*     */   }
/*     */ 
/*     */   public IDisplayableDataPart create(IDisplayableDataPartFactory.PART part)
/*     */   {
/*  59 */     Logger logger = LoggerFactory.getLogger(part.name());
/*     */ 
/*  61 */     switch (1.$SwitchMap$com$dukascopy$charts$chartbuilder$IDisplayableDataPartFactory$PART[part.ordinal()]) {
/*     */     case 1:
/*  63 */       return this.backGroundDisplayableDataPart;
/*     */     case 2:
/*  65 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createCommonAxisXPanelGridDrawingStrategyMap());
/*     */     case 3:
/*  71 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createCommonAxisXPanelMovableLabelDrawingStrategyMap());
/*     */     case 4:
/*  77 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createMainChartGridDrawingStrategyMap());
/*     */     case 5:
/*  83 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createMainChartPeriodSeparatorsDrawingStrategyMap());
/*     */     case 6:
/*  89 */       return new MainChartPanelMouseCursorDisplayableDataPart(this.chartState);
/*     */     case 7:
/*  93 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createRawDrawingStrategyMap());
/*     */     case 8:
/*  99 */       return new MainChartPanelDrawingsDisplayableDataPart(this.chartState, this.drawingsManagerContainer);
/*     */     case 9:
/* 104 */       return new MainChartPanelNewDrawingsDisplayableDataPart(this.chartState, this.drawingsManagerContainer);
/*     */     case 10:
/* 109 */       return new MainChartPanelEditedDrawingsDisplayableDataPart(this.chartState, this.drawingsManagerContainer);
/*     */     case 11:
/* 114 */       return new MainChartPanelDynamicDrawingsDisplayableDataPart(this.chartState, this.drawingsManagerContainer);
/*     */     case 12:
/* 119 */       return new MainChartPanelDrawingsHandlersDisplayableDataPart(this.chartState, this.drawingsManagerContainer, this.indicatorsManager);
/*     */     case 13:
/* 125 */       return new MainChartPanelOrdersDisplayableDataPart(logger, this.chartState, createOrdersDrawingStrategyMap());
/*     */     case 14:
/* 131 */       return new MainChartPanelSelectedOrdersDisplayableDataPart(logger, this.chartState, createOrdersDrawingStrategyMap());
/*     */     case 15:
/* 137 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createIndicatorsDrawingStrategyMap());
/*     */     case 16:
/* 143 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createMainChartPanelMetaDrawingsDrawingStrategyMap());
/*     */     case 17:
/* 149 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createMainAxisYPanelGridDrawingStrategyMap());
/*     */     case 18:
/* 155 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createMainAxisYIndicatorValueLabelDrawingStrategyMap());
/*     */     case 19:
/* 161 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createMainAxisYPanelMovableLabelDrawingStrategyMap());
/*     */     }
/*     */ 
/* 167 */     return new NullDisplayableDataPart(logger, part);
/*     */   }
/*     */ 
/*     */   public IDisplayableDataPart create(IDisplayableDataPartFactory.PART part, SubIndicatorGroup subIndicatorGroup)
/*     */   {
/* 173 */     Logger logger = LoggerFactory.getLogger(part.name());
/*     */ 
/* 175 */     switch (1.$SwitchMap$com$dukascopy$charts$chartbuilder$IDisplayableDataPartFactory$PART[part.ordinal()]) {
/*     */     case 20:
/* 177 */       return new SubChartPanelGridDisplayableDataPart(this.chartState, this.mouseControllerMetaDrawingsState);
/*     */     case 21:
/* 179 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createSubChartPeriodSeparatorsDrawingStrategyMap());
/*     */     case 22:
/* 185 */       return new SubChartPanelMouseCursorDisplayableDataPart(subIndicatorGroup, this.chartState, this.mouseControllerMetaDrawingsState);
/*     */     case 23:
/* 187 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createSubIndicatorsDrawingStrategyMap(subIndicatorGroup));
/*     */     case 24:
/* 193 */       return new SubChartPanelDrawingsDisplayableDataPart(this.chartState, this.drawingsManagerContainer, subIndicatorGroup.getSubWindowId());
/*     */     case 25:
/* 199 */       return new SubChartPanelNewDrawingsDisplayableDataPart(this.chartState, this.drawingsManagerContainer, subIndicatorGroup.getSubWindowId());
/*     */     case 26:
/* 205 */       return new SubChartPanelEditedDrawingsDisplayableDataPart(this.chartState, this.drawingsManagerContainer, subIndicatorGroup.getSubWindowId());
/*     */     case 27:
/* 211 */       return new SubChartPanelDrawingsHandlersDisplayableDataPart(this.chartState, this.drawingsManagerContainer, subIndicatorGroup.getSubWindowId());
/*     */     case 28:
/* 217 */       return new SubChartPanelMetaInfoDisplayableDataPart(subIndicatorGroup, this.chartState);
/*     */     case 29:
/* 219 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createSubIndicatorsInfoDrawingStrategyMap(subIndicatorGroup, this.chartState));
/*     */     case 30:
/* 225 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createSubAxisYPanelGridDrawingStrategyMap(subIndicatorGroup));
/*     */     case 31:
/* 231 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createSubAxisYIndicatorValueLabelDrawingStrategyMap(subIndicatorGroup));
/*     */     case 32:
/* 238 */       return new TickCandleDisplayableDataPart(logger, this.chartState, createSubAxisYPanelMovableLabelDrawingStrategyMap(subIndicatorGroup));
/*     */     }
/*     */ 
/* 244 */     return new NullDisplayableDataPart(logger, part);
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createCommonAxisXPanelGridDrawingStrategyMap()
/*     */   {
/* 252 */     Map result = new HashMap();
/* 253 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 254 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createCommonAxisXPanelGridDrawingStrategy());
/*     */     }
/* 256 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createCommonAxisXPanelMovableLabelDrawingStrategyMap() {
/* 260 */     Map result = new HashMap();
/* 261 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 262 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createCommonAxisXPanelMovableLabelDrawingStrategy());
/*     */     }
/* 264 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createMainChartGridDrawingStrategyMap() {
/* 268 */     Map result = new HashMap();
/* 269 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 270 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createMainChartGridDrawingStrategy());
/*     */     }
/* 272 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createMainChartPeriodSeparatorsDrawingStrategyMap() {
/* 276 */     Map result = new HashMap();
/* 277 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 278 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createMainChartPeriodSeparatorsDrawingStrategy());
/*     */     }
/* 280 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createRawDrawingStrategyMap() {
/* 284 */     Map result = new HashMap();
/* 285 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 286 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createRawDrawingStrategy());
/*     */     }
/* 288 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createIndicatorsDrawingStrategyMap() {
/* 292 */     Map result = new HashMap();
/* 293 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 294 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createIndicatorsDrawingStrategy());
/*     */     }
/* 296 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createMainChartPanelMetaDrawingsDrawingStrategyMap() {
/* 300 */     Map result = new HashMap();
/* 301 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 302 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createMainChartPanelMetaDrawingsDrawingStrategy());
/*     */     }
/* 304 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createMainAxisYPanelGridDrawingStrategyMap() {
/* 308 */     Map result = new HashMap();
/* 309 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 310 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createMainAxisYPanelGridDrawingStrategy());
/*     */     }
/* 312 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createMainAxisYIndicatorValueLabelDrawingStrategyMap() {
/* 316 */     Map result = new HashMap();
/* 317 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 318 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createMainAxisYIndicatorValueLabelDrawingStrategy());
/*     */     }
/* 320 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createMainAxisYPanelMovableLabelDrawingStrategyMap() {
/* 324 */     Map result = new HashMap();
/* 325 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 326 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createMainAxisYPanelMovableLabelDrawingStrategy());
/*     */     }
/* 328 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createSubChartPeriodSeparatorsDrawingStrategyMap() {
/* 332 */     Map result = new HashMap();
/* 333 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 334 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createSubChartPeriodSeparatorsDrawingStrategy());
/*     */     }
/* 336 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createSubIndicatorsDrawingStrategyMap(SubIndicatorGroup subIndicatorGroup) {
/* 340 */     Map result = new HashMap();
/* 341 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 342 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createSubIndicatorsDrawingStrategy(subIndicatorGroup));
/*     */     }
/* 344 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createSubIndicatorsInfoDrawingStrategyMap(SubIndicatorGroup subIndicatorGroup, ChartState chartState) {
/* 348 */     Map result = new HashMap();
/* 349 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 350 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createSubIndicatorsInfoDrawingStrategy(subIndicatorGroup, chartState));
/*     */     }
/* 352 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createSubAxisYPanelGridDrawingStrategyMap(SubIndicatorGroup subIndicatorGroup) {
/* 356 */     Map result = new HashMap();
/* 357 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 358 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createSubAxisYPanelGridDrawingStrategy(subIndicatorGroup));
/*     */     }
/* 360 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createSubAxisYIndicatorValueLabelDrawingStrategyMap(SubIndicatorGroup subIndicatorGroup) {
/* 364 */     Map result = new HashMap();
/* 365 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 366 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createSubAxisYIndicatorValueLabelDrawingStrategy(subIndicatorGroup));
/*     */     }
/* 368 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IDrawingStrategy> createSubAxisYPanelMovableLabelDrawingStrategyMap(SubIndicatorGroup subIndicatorGroup) {
/* 372 */     Map result = new HashMap();
/* 373 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 374 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createSubAxisYPanelMovableLabelDrawingStrategy(subIndicatorGroup));
/*     */     }
/* 376 */     return result;
/*     */   }
/*     */ 
/*     */   private Map<DataType, IOrdersDrawingStrategy> createOrdersDrawingStrategyMap() {
/* 380 */     Map result = new HashMap();
/* 381 */     for (DataType dt : this.drawingStrategyFactoriesMap.keySet()) {
/* 382 */       result.put(dt, ((IDrawingStrategyFactory)this.drawingStrategyFactoriesMap.get(dt)).createOrdersDrawingStrategy());
/*     */     }
/* 384 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.DisplayableDataPartFactoryImpl
 * JD-Core Version:    0.6.0
 */