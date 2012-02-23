/*     */ package com.dukascopy.charts.view.displayabledatapart;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.CandleInfoParams;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.PriceAgregatedInfoParams;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.TickInfoParams;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*     */ import com.dukascopy.charts.drawings.AbstractStickablePointsChartObject;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.DrawingsFactory;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelperContainer;
/*     */ import com.dukascopy.charts.drawings.IDynamicChartObject;
/*     */ import com.dukascopy.charts.drawings.IMainDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.MainDrawingsManagerImpl;
/*     */ import com.dukascopy.charts.drawings.OhlcChartObject;
/*     */ import com.dukascopy.charts.drawings.SubDrawingsManagerImpl;
/*     */ import com.dukascopy.charts.drawings.TextChartObject;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.listeners.drawing.DrawingActionListener;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.mappers.Mapper;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DrawingsManagerContainerImpl
/*     */   implements IDrawingsManagerContainer
/*     */ {
/*  63 */   private static final Logger LOGGER = LoggerFactory.getLogger(DrawingsManagerContainerImpl.class);
/*     */   private final ITimeToXMapper timeToXMapper;
/*     */   private final IValueToYMapper mainValueToYMapper;
/*     */   private final SubValueToYMapper subValueToYMapper;
/*     */   private final FormattersManager formattersManager;
/*     */   private final GuiRefresher guiRefresher;
/*     */   private final DrawingsFactory drawingsFactory;
/*     */   private final DrawingsLabelHelperContainer drawingsLabelHelperContainer;
/*     */   private final DrawingActionListener drawingActionListener;
/*     */   private final ChartsActionListenerRegistry chartsActionListenerRegistry;
/*     */   private final ChartState chartState;
/*     */   private final Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders;
/*     */   final GeometryCalculator geometryCalculator;
/*     */   private ChartObject newChartObject;
/*     */   MainDrawingsManagerImpl mainDrawingsManagerImpl;
/*  84 */   Map<Integer, LinkedHashMap<Integer, SubDrawingsManagerImpl>> subDrawingsManagersByWindowId = new HashMap();
/*     */ 
/*     */   public DrawingsManagerContainerImpl(ITimeToXMapper timeToXMapper, IValueToYMapper mainValueToYMapper, SubValueToYMapper subValueToYMapper, FormattersManager formattersManager, GuiRefresher guiRefresher, DrawingsFactory drawingsFactory, DrawingsLabelHelperContainer labelHelperContainer, DrawingActionListener drawingActionListener, ChartsActionListenerRegistry chartsActionListenerRegistry, ChartState chartState, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders, GeometryCalculator geometryCalculator)
/*     */   {
/* 101 */     this.timeToXMapper = timeToXMapper;
/* 102 */     this.mainValueToYMapper = mainValueToYMapper;
/* 103 */     this.subValueToYMapper = subValueToYMapper;
/* 104 */     this.formattersManager = formattersManager;
/* 105 */     this.guiRefresher = guiRefresher;
/* 106 */     this.drawingsFactory = drawingsFactory;
/* 107 */     this.drawingActionListener = drawingActionListener;
/* 108 */     this.chartsActionListenerRegistry = chartsActionListenerRegistry;
/* 109 */     this.chartState = chartState;
/*     */ 
/* 111 */     this.allDataSequenceProviders = allDataSequenceProviders;
/*     */ 
/* 113 */     this.geometryCalculator = geometryCalculator;
/*     */ 
/* 115 */     this.drawingsLabelHelperContainer = labelHelperContainer;
/* 116 */     this.mainDrawingsManagerImpl = new MainDrawingsManagerImpl(new Mapper(timeToXMapper, mainValueToYMapper), formattersManager, guiRefresher, drawingsFactory, labelHelperContainer.getLabelHelperForMain(), drawingActionListener, chartsActionListenerRegistry, chartState, allDataSequenceProviders, geometryCalculator);
/*     */   }
/*     */ 
/*     */   public void setMainOperationManager(IMainOperationManager mainOperationManager)
/*     */   {
/* 132 */     this.mainDrawingsManagerImpl.setMainOperationManager(mainOperationManager);
/*     */   }
/*     */ 
/*     */   public IValueToYMapper getMainValueToYMapper() {
/* 136 */     return this.mainValueToYMapper;
/*     */   }
/*     */ 
/*     */   public void createSubDrawingsManagerForIndicator(int subWindowId, int indicatorId)
/*     */   {
/* 141 */     SubDrawingsManagerImpl subDrawingsManagerImpl = new SubDrawingsManagerImpl(subWindowId, indicatorId, new Mapper(this.timeToXMapper, this.subValueToYMapper.get(Integer.valueOf(indicatorId))), this.formattersManager, this.guiRefresher, this.drawingsFactory, this.drawingsLabelHelperContainer.getLabelHelperForSubWindow(subWindowId), this.drawingActionListener, this.chartsActionListenerRegistry, this.chartState, this.allDataSequenceProviders, this.geometryCalculator);
/*     */ 
/* 156 */     LinkedHashMap subDrawingsManagersByIndicatorId = (LinkedHashMap)this.subDrawingsManagersByWindowId.get(Integer.valueOf(subWindowId));
/* 157 */     subDrawingsManagersByIndicatorId.put(Integer.valueOf(indicatorId), subDrawingsManagerImpl);
/*     */   }
/*     */ 
/*     */   public void deleteSubDrawingsManagerForIndicator(int subWindowId, int indicatorId)
/*     */   {
/* 162 */     Map subDrawingsManagersByIndicatorId = (Map)this.subDrawingsManagersByWindowId.get(Integer.valueOf(subWindowId));
/* 163 */     if (subDrawingsManagersByIndicatorId == null) {
/* 164 */       return;
/*     */     }
/* 166 */     SubDrawingsManagerImpl subDrawingsManagerImpl = (SubDrawingsManagerImpl)subDrawingsManagersByIndicatorId.get(Integer.valueOf(indicatorId));
/* 167 */     subDrawingsManagerImpl.removeAllDrawings();
/* 168 */     subDrawingsManagersByIndicatorId.remove(Integer.valueOf(indicatorId));
/*     */   }
/*     */ 
/*     */   public void createSubDrawingsManagersMapFor(int subWindowId)
/*     */   {
/* 173 */     this.subDrawingsManagersByWindowId.put(Integer.valueOf(subWindowId), new LinkedHashMap());
/*     */   }
/*     */ 
/*     */   public void deleteSubDrawingsManagersFor(int subWindowId)
/*     */   {
/* 178 */     this.subDrawingsManagersByWindowId.remove(Integer.valueOf(subWindowId));
/*     */   }
/*     */ 
/*     */   public ChartObject getNewChartObject()
/*     */   {
/* 183 */     return this.newChartObject;
/*     */   }
/*     */ 
/*     */   public void setNewChartObject(ChartObject newChartObject)
/*     */   {
/* 188 */     this.newChartObject = newChartObject;
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEdited()
/*     */   {
/* 193 */     this.mainDrawingsManagerImpl.unselectDrawingToBeEdited();
/*     */ 
/* 195 */     if (this.mainDrawingsManagerImpl.isDrawingNew()) {
/* 196 */       this.mainDrawingsManagerImpl.finishDrawingNewDrawing(this.newChartObject);
/*     */     }
/*     */ 
/* 199 */     for (Integer subWindowId : this.subDrawingsManagersByWindowId.keySet()) {
/* 200 */       Map subDrawingsManagers = (Map)this.subDrawingsManagersByWindowId.get(subWindowId);
/* 201 */       if (subDrawingsManagers != null) {
/* 202 */         Collection managers = subDrawingsManagers.values();
/* 203 */         for (SubDrawingsManagerImpl manager : managers)
/*     */         {
/* 205 */           manager.unselectDrawingToBeEdited();
/* 206 */           if (manager.isDrawingNew())
/* 207 */             manager.finishDrawingNewDrawing(this.newChartObject);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public MainDrawingsManagerImpl getMainDrawingsManager()
/*     */   {
/* 216 */     return this.mainDrawingsManagerImpl;
/*     */   }
/*     */ 
/*     */   public LinkedHashMap<Integer, SubDrawingsManagerImpl> getSubDrawingsManagers(int subWindowId)
/*     */   {
/* 221 */     return (LinkedHashMap)this.subDrawingsManagersByWindowId.get(Integer.valueOf(subWindowId));
/*     */   }
/*     */ 
/*     */   public Set<Integer> getSubWindowsIds()
/*     */   {
/* 226 */     return this.subDrawingsManagersByWindowId.keySet();
/*     */   }
/*     */ 
/*     */   protected Collection<SubDrawingsManagerImpl> getSubDrawingsManagerList(int subWindowId) {
/* 230 */     LinkedHashMap map = (LinkedHashMap)this.subDrawingsManagersByWindowId.get(Integer.valueOf(subWindowId));
/* 231 */     if (map != null) {
/* 232 */       return map.values();
/*     */     }
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */   public void selectDrawing(IChartObject chartObject)
/*     */   {
/* 239 */     if (this.mainDrawingsManagerImpl.contains(chartObject)) {
/* 240 */       this.mainDrawingsManagerImpl.selectDrawing(chartObject);
/*     */     } else {
/* 242 */       Collection subDrawingsManagersByWindowId = this.subDrawingsManagersByWindowId.values();
/* 243 */       for (LinkedHashMap subDrawingsManagersByIndicatorId : subDrawingsManagersByWindowId) {
/* 244 */         Collection subDrawingsManagers = subDrawingsManagersByIndicatorId.values();
/* 245 */         for (SubDrawingsManagerImpl subDrawingsManager : subDrawingsManagers)
/* 246 */           if (subDrawingsManager.contains(chartObject)) {
/* 247 */             subDrawingsManager.selectDrawing(chartObject);
/* 248 */             return;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAllDrawings()
/*     */   {
/* 260 */     this.mainDrawingsManagerImpl.removeAllDrawings();
/* 261 */     for (SubDrawingsManagerImpl subManager : getSubDrawingManagers())
/* 262 */       subManager.removeAllDrawings();
/*     */   }
/*     */ 
/*     */   public void remove(List<IChartObject> chartObjects)
/*     */   {
/* 273 */     this.mainDrawingsManagerImpl.remove(chartObjects);
/*     */ 
/* 277 */     this.guiRefresher.refreshSubContents();
/*     */ 
/* 280 */     for (IChartObject drawing : chartObjects)
/* 281 */       remove(drawing);
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject)
/*     */   {
/*     */     Integer indicatorId;
/*     */     Integer indicatorId;
/* 292 */     if (this.mainDrawingsManagerImpl.contains(chartObject)) {
/* 293 */       this.mainDrawingsManagerImpl.remove(chartObject);
/* 294 */       indicatorId = null;
/*     */     }
/*     */     else {
/* 297 */       Collection subDrawingsManagersByWindowId = this.subDrawingsManagersByWindowId.values();
/* 298 */       Iterator iter = subDrawingsManagersByWindowId.iterator();
/* 299 */       indicatorId = null;
/*     */       LinkedHashMap subDrawingsManagersByIndicatorId;
/* 300 */       while ((iter.hasNext()) && (indicatorId == null)) {
/* 301 */         subDrawingsManagersByIndicatorId = (LinkedHashMap)iter.next();
/* 302 */         for (Integer id : subDrawingsManagersByIndicatorId.keySet()) {
/* 303 */           SubDrawingsManagerImpl subDrawingsManager = (SubDrawingsManagerImpl)subDrawingsManagersByIndicatorId.get(id);
/* 304 */           if (subDrawingsManager.contains(chartObject)) {
/* 305 */             subDrawingsManager.remove(chartObject);
/* 306 */             indicatorId = id;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 311 */     if (((ChartObject)chartObject).isUnderEdit()) {
/* 312 */       this.drawingActionListener.drawingEditingEnded();
/*     */     }
/*     */ 
/* 316 */     if (indicatorId == null) {
/* 317 */       this.chartsActionListenerRegistry.drawingRemoved(chartObject);
/*     */     }
/*     */     else
/* 320 */       this.chartsActionListenerRegistry.drawingRemoved(indicatorId.intValue(), chartObject);
/*     */   }
/*     */ 
/*     */   public List<SubDrawingsManagerImpl> getSubDrawingManagers()
/*     */   {
/* 326 */     List result = new LinkedList();
/*     */ 
/* 328 */     Collection managersByWindow = this.subDrawingsManagersByWindowId.values();
/* 329 */     for (LinkedHashMap managersByIndicator : managersByWindow) {
/* 330 */       for (SubDrawingsManagerImpl subDrawingsManager : managersByIndicator.values()) {
/* 331 */         result.add(subDrawingsManager);
/*     */       }
/*     */     }
/*     */ 
/* 335 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean updateTextChartObject(TextChartObject textChartObject)
/*     */   {
/* 340 */     if (this.mainDrawingsManagerImpl.contains(textChartObject)) {
/* 341 */       return this.mainDrawingsManagerImpl.updateTextChartObject(textChartObject);
/*     */     }
/* 343 */     Collection subDrawingsManagersByWindowId = this.subDrawingsManagersByWindowId.values();
/* 344 */     for (LinkedHashMap subDrawingsManagersByIndicatorId : subDrawingsManagersByWindowId) {
/* 345 */       Collection subDrawingsManagers = subDrawingsManagersByIndicatorId.values();
/* 346 */       for (SubDrawingsManagerImpl subDrawingsManager : subDrawingsManagers) {
/* 347 */         if (subDrawingsManager.contains(textChartObject)) {
/* 348 */           return subDrawingsManager.updateTextChartObject(textChartObject);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 353 */     return false;
/*     */   }
/*     */ 
/*     */   public void updateChartObjectPricesManualy(ChartObject chartObject)
/*     */   {
/* 358 */     if (this.mainDrawingsManagerImpl.contains(chartObject)) {
/* 359 */       this.mainDrawingsManagerImpl.updateChartObjectPricesManualy(chartObject);
/*     */     } else {
/* 361 */       Collection subDrawingsManagersByWindowId = this.subDrawingsManagersByWindowId.values();
/* 362 */       for (LinkedHashMap subDrawingsManagersByIndicatorId : subDrawingsManagersByWindowId) {
/* 363 */         Collection subDrawingsManagers = subDrawingsManagersByIndicatorId.values();
/* 364 */         for (SubDrawingsManagerImpl subDrawingsManager : subDrawingsManagers)
/* 365 */           if (subDrawingsManager.contains(chartObject)) {
/* 366 */             subDrawingsManager.updateChartObjectPricesManualy(chartObject);
/* 367 */             return;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updatePipsPerBarOption(ChartObject chartObject)
/*     */   {
/* 377 */     if (this.mainDrawingsManagerImpl.contains(chartObject))
/* 378 */       this.mainDrawingsManagerImpl.updatePipsPerBarOption(chartObject);
/*     */   }
/*     */ 
/*     */   public void drawComment(Graphics g)
/*     */   {
/* 385 */     getMainDrawingsManager().drawComment(g);
/*     */   }
/*     */ 
/*     */   public void drawNewDrawing(Graphics g, int subWindowId)
/*     */   {
/* 390 */     drawImpl(g, subWindowId, true);
/*     */   }
/*     */ 
/*     */   public void drawEditedDrawing(Graphics g, int subWindowId)
/*     */   {
/* 395 */     drawImpl(g, subWindowId, false);
/*     */   }
/*     */ 
/*     */   protected void drawImpl(Graphics g, int subWindowId, boolean newDrawings)
/*     */   {
/*     */     ChartObject chartObject;
/* 399 */     if (subWindowId < 0)
/*     */     {
/* 401 */       MainDrawingsManagerImpl manager = getMainDrawingsManager();
/*     */       ChartObject chartObject;
/*     */       ChartObject chartObject;
/* 403 */       if (newDrawings)
/*     */       {
/*     */         ChartObject chartObject;
/* 404 */         if (manager.isDrawingNew())
/* 405 */           chartObject = this.newChartObject;
/*     */         else
/* 407 */           chartObject = null;
/*     */       }
/*     */       else {
/* 410 */         chartObject = manager.getEditedChartObject();
/*     */       }
/*     */ 
/* 413 */       if (chartObject != null) {
/* 414 */         IMapper mapper = manager.getMapper();
/* 415 */         FormattersManager formatters = manager.getFormattersManager();
/* 416 */         DrawingsLabelHelper labelHelper = manager.getDrawingsLabelHelper();
/* 417 */         draw(chartObject, g, mapper, formatters, labelHelper, -1);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 422 */       IMainDrawingsManager mainManager = getMainDrawingsManager();
/*     */       ChartObject chartObject;
/* 424 */       if (newDrawings)
/* 425 */         chartObject = this.newChartObject;
/*     */       else {
/* 427 */         chartObject = mainManager.getEditedChartObject();
/*     */       }
/*     */ 
/* 431 */       Collection managers = getSubDrawingsManagerList(subWindowId);
/* 432 */       if (managers != null)
/* 433 */         for (SubDrawingsManagerImpl drawingsManager : managers)
/*     */         {
/* 435 */           if ((chartObject != null) && (chartObject.isGlobal()))
/*     */           {
/* 437 */             IMapper mapper = drawingsManager.getMapper();
/* 438 */             FormattersManager formatters = drawingsManager.getFormattersManager();
/* 439 */             DrawingsLabelHelper labelHelper = drawingsManager.getDrawingsLabelHelper();
/* 440 */             draw(chartObject, g, mapper, formatters, labelHelper, subWindowId);
/*     */           }
/*     */           else
/*     */           {
/*     */             ChartObject subChartObject;
/*     */             ChartObject subChartObject;
/* 445 */             if (newDrawings)
/*     */             {
/*     */               ChartObject subChartObject;
/* 446 */               if (drawingsManager.isDrawingNew())
/* 447 */                 subChartObject = this.newChartObject;
/*     */               else
/* 449 */                 subChartObject = null;
/*     */             }
/*     */             else
/*     */             {
/* 453 */               subChartObject = drawingsManager.getEditedChartObject();
/*     */             }
/* 455 */             if (subChartObject != null) {
/* 456 */               IMapper mapper = drawingsManager.getMapper();
/* 457 */               FormattersManager formatters = drawingsManager.getFormattersManager();
/* 458 */               DrawingsLabelHelper labelHelper = drawingsManager.getDrawingsLabelHelper();
/* 459 */               draw(subChartObject, g, mapper, formatters, labelHelper, subWindowId);
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawHandlers(Graphics g, int subWindowId)
/*     */   {
/* 470 */     MainDrawingsManagerImpl mainManager = getMainDrawingsManager();
/* 471 */     List mainChartObjects = mainManager.getChartObjects();
/* 472 */     if (subWindowId < 0) {
/* 473 */       IMapper mapper = mainManager.getMapper();
/* 474 */       drawHandlers(mainChartObjects, g, mapper, false, -1);
/*     */     }
/*     */     else
/*     */     {
/* 479 */       Collection managers = getSubDrawingsManagerList(subWindowId);
/* 480 */       if (managers != null)
/* 481 */         for (SubDrawingsManagerImpl drawingsManager : managers) {
/* 482 */           IMapper mapper = drawingsManager.getMapper();
/* 483 */           drawHandlers(mainChartObjects, g, mapper, true, subWindowId);
/* 484 */           drawHandlers(drawingsManager.getChartObjects(), g, mapper, false, subWindowId);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawHandlers(Collection<IChartObject> chartObjects, Graphics g, IMapper mapper, boolean globalOnly, int subWindowId)
/*     */   {
/* 491 */     for (IChartObject commonChartObject : chartObjects) {
/* 492 */       ChartObject chartObject = (ChartObject)commonChartObject;
/* 493 */       drawHandlers(chartObject, g, mapper, globalOnly, subWindowId);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawHandlers(ChartObject chartObject, Graphics g, IMapper mapper, boolean globalOnly, int subWindowId) {
/*     */     try {
/* 499 */       if (chartObject.isGlobal())
/*     */       {
/* 501 */         if (subWindowId < 0) {
/* 502 */           chartObject.drawHandlers(g, mapper);
/* 503 */           this.guiRefresher.refreshSubContents();
/*     */         }
/*     */       }
/* 506 */       else if (!globalOnly)
/* 507 */         chartObject.drawHandlers(g, mapper);
/*     */     }
/*     */     catch (Exception e) {
/* 510 */       LOGGER.error("Error drawing handlers for " + chartObject.getClass().getName(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawDynamicChartObjects(Graphics g, int subWindowId)
/*     */   {
/* 516 */     MainDrawingsManagerImpl mainManager = getMainDrawingsManager();
/* 517 */     IMapper mapper = mainManager.getMapper();
/* 518 */     FormattersManager formatters = mainManager.getFormattersManager();
/* 519 */     DrawingsLabelHelper labelHelper = mainManager.getDrawingsLabelHelper();
/* 520 */     List chartObjects = mainManager.getDynamicChartObjects();
/*     */ 
/* 522 */     provideOhlcChartObjectsWithInfo(chartObjects, mapper, formatters);
/*     */ 
/* 524 */     for (IDynamicChartObject chartObject : chartObjects)
/* 525 */       if (!((ChartObject)chartObject).isUnderEdit())
/* 526 */         ((ChartObject)chartObject).draw(g, mapper, formatters, labelHelper);
/*     */   }
/*     */ 
/*     */   private void provideOhlcChartObjectsWithInfo(List<IDynamicChartObject> chartObjects, IMapper mapper, FormattersManager formatters)
/*     */   {
/* 533 */     int ROUNDING_SCALE = 8;
/*     */ 
/* 535 */     List ohlcChartObjects = new ArrayList();
/* 536 */     for (IDynamicChartObject chartObject : chartObjects)
/* 537 */       if ((chartObject instanceof OhlcChartObject))
/* 538 */         ohlcChartObjects.add((OhlcChartObject)chartObject);
/*     */     List indicators;
/*     */     AbstractDataSequence dataSequence;
/*     */     AbstractPriceAggregationData data;
/*     */     int index;
/* 541 */     if (!ohlcChartObjects.isEmpty()) {
/* 542 */       int curMouseX = this.chartState.getMouseCursorPoint().x;
/*     */ 
/* 544 */       indicators = ((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(this.chartState.getDataType())).getIndicators();
/*     */       CandleDataSequence dataSequence;
/*     */       CandleData data;
/*     */       int index;
/* 546 */       if (DataType.TIME_PERIOD_AGGREGATION.equals(this.chartState.getDataType())) {
/* 547 */         dataSequence = (CandleDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(DataType.TIME_PERIOD_AGGREGATION)).getDataSequence();
/*     */ 
/* 549 */         data = null;
/* 550 */         index = -1;
/*     */ 
/* 552 */         if ((this.chartState.isChartShiftActive()) && (this.chartState.isMouseCursorOnWindow(-2))) {
/* 553 */           data = (CandleData)dataSequence.getLastData();
/* 554 */           index = dataSequence.size() - 1;
/*     */         } else {
/* 556 */           long time = mapper.tx(curMouseX);
/* 557 */           if ((dataSequence.getFrom() <= time) && (time <= dataSequence.getTo())) {
/* 558 */             index = dataSequence.indexOf(time);
/* 559 */             data = (CandleData)dataSequence.getData(index);
/*     */           }
/*     */         }
/* 562 */         if (data != null)
/* 563 */           for (OhlcChartObject ohlcChartObject : ohlcChartObjects) {
/* 564 */             ohlcChartObject.setDataType(this.chartState.getDataType());
/*     */ 
/* 566 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.DATE, formatters.getDateFormatter().formatDateWithoutTime(data.time));
/* 567 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.TIME, formatters.getDateFormatter().formatTimeWithoutDate(data.time));
/*     */ 
/* 569 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.OPEN, Double.valueOf(StratUtils.round(Double.valueOf(data.open).doubleValue(), 8)));
/* 570 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.HIGH, Double.valueOf(StratUtils.round(Double.valueOf(data.high).doubleValue(), 8)));
/* 571 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.LOW, Double.valueOf(StratUtils.round(Double.valueOf(data.low).doubleValue(), 8)));
/* 572 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.CLOSE, Double.valueOf(StratUtils.round(Double.valueOf(data.close).doubleValue(), 8)));
/* 573 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.VOL, Double.valueOf(StratUtils.round(Double.valueOf(data.vol).doubleValue(), 8)));
/* 574 */             ohlcChartObject.setParamValue(IOhlcChartObject.CandleInfoParams.INDEX, index + 1 + " of " + dataSequence.size());
/*     */ 
/* 576 */             provideOhlcChartObjectWithIndicatorInfo(ohlcChartObject, dataSequence, indicators, data.time);
/*     */           }
/*     */       }
/*     */       else
/*     */       {
/*     */         TickDataSequence dataSequence;
/*     */         TickData chartsTick;
/* 579 */         if (DataType.TICKS.equals(this.chartState.getDataType())) {
/* 580 */           dataSequence = (TickDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(DataType.TICKS)).getDataSequence();
/*     */ 
/* 582 */           if ((dataSequence != null) && (!dataSequence.isEmpty())) {
/* 583 */             chartsTick = (TickData)dataSequence.getLastData();
/* 584 */             if ((dataSequence.size() > 1) && ((!this.chartState.isChartShiftActive()) || (!this.chartState.isMouseCursorOnWindow(-2)))) {
/* 585 */               if (curMouseX <= mapper.xt(((TickData)dataSequence.getData(0)).time))
/*     */               {
/* 587 */                 chartsTick = (TickData)dataSequence.getData(0);
/* 588 */               } else if (mapper.xt(chartsTick.time) <= curMouseX)
/*     */               {
/* 590 */                 chartsTick = (TickData)dataSequence.getLastData();
/*     */               }
/* 592 */               else chartsTick = findNearestTickUnderMouseCursor(mapper, dataSequence, curMouseX);
/*     */ 
/*     */             }
/*     */ 
/* 596 */             if (chartsTick != null)
/* 597 */               for (OhlcChartObject ohlcChartObject : ohlcChartObjects) {
/* 598 */                 ohlcChartObject.setDataType(this.chartState.getDataType());
/* 599 */                 ohlcChartObject.setParamValue(IOhlcChartObject.TickInfoParams.DATE, formatters.getDateFormatter().formatRightTime(chartsTick.time));
/* 600 */                 ohlcChartObject.setParamValue(IOhlcChartObject.TickInfoParams.TIME, formatters.getDateFormatter().formatTime(chartsTick.time));
/* 601 */                 ohlcChartObject.setParamValue(IOhlcChartObject.TickInfoParams.ASK, Double.valueOf(StratUtils.round(Double.valueOf(chartsTick.ask).doubleValue(), 8)));
/* 602 */                 ohlcChartObject.setParamValue(IOhlcChartObject.TickInfoParams.BID, Double.valueOf(StratUtils.round(Double.valueOf(chartsTick.bid).doubleValue(), 8)));
/* 603 */                 ohlcChartObject.setParamValue(IOhlcChartObject.TickInfoParams.ASK_VOL, Double.valueOf(StratUtils.round(Double.valueOf(chartsTick.askVol).doubleValue(), 8)));
/* 604 */                 ohlcChartObject.setParamValue(IOhlcChartObject.TickInfoParams.BID_VOL, Double.valueOf(StratUtils.round(Double.valueOf(chartsTick.bidVol).doubleValue(), 8)));
/*     */ 
/* 606 */                 provideOhlcChartObjectWithIndicatorInfo(ohlcChartObject, dataSequence, indicators, chartsTick.time);
/*     */               }
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           try
/*     */           {
/* 614 */             dataSequence = (AbstractDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(this.chartState.getDataType())).getDataSequence();
/*     */           } catch (ClassCastException e) {
/* 616 */             throw new IllegalStateException("Unknown DataType");
/*     */           }
/*     */ 
/* 619 */           data = null;
/* 620 */           index = -1;
/*     */ 
/* 622 */           if ((this.chartState.isChartShiftActive()) && (this.chartState.isMouseCursorOnWindow(-2))) {
/* 623 */             data = (AbstractPriceAggregationData)dataSequence.getLastData();
/* 624 */             index = dataSequence.size() - 1;
/*     */           } else {
/* 626 */             long time = mapper.tx(curMouseX);
/* 627 */             if ((dataSequence.getFrom() <= time) && (time <= dataSequence.getTo())) {
/* 628 */               index = dataSequence.indexOf(time);
/* 629 */               data = (AbstractPriceAggregationData)dataSequence.getData(index);
/*     */             }
/*     */           }
/*     */ 
/* 633 */           if (data != null)
/* 634 */             for (OhlcChartObject ohlcChartObject : ohlcChartObjects) {
/* 635 */               ohlcChartObject.setDataType(this.chartState.getDataType());
/* 636 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.DATE, formatters.getDateFormatter().formatRightTime(data.getTime()));
/* 637 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.START_TIME, formatters.getDateFormatter().formatTime(data.getTime()));
/* 638 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.END_TIME, formatters.getDateFormatter().formatTime(data.getEndTime()));
/* 639 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.OPEN, Double.valueOf(StratUtils.round(Double.valueOf(data.getOpen()).doubleValue(), 8)));
/* 640 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.HIGH, Double.valueOf(StratUtils.round(Double.valueOf(data.getHigh()).doubleValue(), 8)));
/* 641 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.LOW, Double.valueOf(StratUtils.round(Double.valueOf(data.getLow()).doubleValue(), 8)));
/* 642 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.CLOSE, Double.valueOf(StratUtils.round(Double.valueOf(data.getClose()).doubleValue(), 8)));
/* 643 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.VOL, Double.valueOf(StratUtils.round(Double.valueOf(data.getVolume()).doubleValue(), 8)));
/* 644 */               ohlcChartObject.setParamValue(IOhlcChartObject.PriceAgregatedInfoParams.INDEX, index + 1 + " of " + dataSequence.size());
/*     */ 
/* 646 */               provideOhlcChartObjectWithIndicatorInfo(ohlcChartObject, dataSequence, indicators, data.time);
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void provideOhlcChartObjectWithIndicatorInfo(OhlcChartObject ohlcChartObject, AbstractDataSequence<? extends Data> dataSequence, List<IndicatorWrapper> indicators, long time)
/*     */   {
/* 657 */     ohlcChartObject.clearIndicatorMap();
/* 658 */     for (IndicatorWrapper ind : indicators) {
/* 659 */       for (int outputIdx = 0; outputIdx < ind.getIndicator().getIndicatorInfo().getNumberOfOutputs(); outputIdx++) {
/* 660 */         if ((OutputParameterInfo.DrawingStyle.PATTERN_BOOL.equals(ind.getDrawingStyles()[outputIdx])) || (OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR.equals(ind.getDrawingStyles()[outputIdx])) || (OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH.equals(ind.getDrawingStyles()[outputIdx])))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 666 */         if ((ohlcChartObject.getShowIndicatorInfo()) || (Boolean.TRUE.equals(ohlcChartObject.getIndVisibilityMap().get(ind.getName())))) {
/* 667 */           OutputParameterInfo outpInfo = ind.getIndicator().getOutputParameterInfo(outputIdx);
/*     */ 
/* 669 */           String label = outpInfo.getName();
/* 670 */           Color color = ind.getOutputColors()[outputIdx];
/* 671 */           Object value = dataSequence.getFormulaValue(ind.getId(), outputIdx, time);
/* 672 */           if ((OutputParameterInfo.Type.DOUBLE.equals(outpInfo.getType())) && (value != null))
/* 673 */             value = Double.valueOf(StratUtils.round(((Double)value).doubleValue(), 8));
/* 674 */           else if (OutputParameterInfo.Type.OBJECT.equals(outpInfo.getType())) {
/*     */               continue;
/*     */             }
/* 677 */           ohlcChartObject.addIndicatorValue(ind.getName(), label, value, color);
/*     */         } else {
/* 679 */           ohlcChartObject.addIndicatorValue(ind.getName(), null, null, null);
/*     */         }
/*     */       }
/*     */     }
/* 683 */     ohlcChartObject.cleanIndicatorVisibilityMap();
/*     */   }
/*     */ 
/*     */   private TickData findNearestTickUnderMouseCursor(IMapper mapper, TickDataSequence dataSequence, int curMouseX) {
/* 687 */     int sequenceSize = dataSequence.size();
/*     */ 
/* 689 */     for (int i = 1; i < sequenceSize - 1; i++) {
/* 690 */       float prevTimePx = mapper.xt(((TickData)dataSequence.getData(i - 1)).time);
/* 691 */       float curTimePx = mapper.xt(((TickData)dataSequence.getData(i)).time);
/* 692 */       float nextTimePx = mapper.xt(((TickData)dataSequence.getData(i + 1)).time);
/*     */ 
/* 694 */       if ((prevTimePx + Math.abs(curTimePx - prevTimePx) / 2.0F < curMouseX) && (curMouseX < nextTimePx - Math.abs(curTimePx - nextTimePx) / 2.0F))
/*     */       {
/* 697 */         return (TickData)dataSequence.getData(i);
/*     */       }
/*     */     }
/*     */ 
/* 701 */     return null;
/*     */   }
/*     */ 
/*     */   public void drawAllDrawings(Graphics g, int subWindowId)
/*     */   {
/* 707 */     MainDrawingsManagerImpl mainManager = getMainDrawingsManager();
/* 708 */     IMapper mainMapper = mainManager.getMapper();
/* 709 */     FormattersManager mainFormatters = mainManager.getFormattersManager();
/* 710 */     DrawingsLabelHelper mainLabelHelper = mainManager.getDrawingsLabelHelper();
/*     */ 
/* 712 */     List mainObjects = mainManager.getChartObjects();
/*     */ 
/* 715 */     if (subWindowId < 0) {
/* 716 */       mainLabelHelper.reset();
/* 717 */       draw(mainObjects, g, mainMapper, mainFormatters, mainLabelHelper, true, false, -1);
/*     */     }
/*     */     else
/*     */     {
/* 721 */       Collection subDrawingsManagers = getSubDrawingsManagerList(subWindowId);
/* 722 */       for (SubDrawingsManagerImpl subDrawingsManager : subDrawingsManagers) {
/* 723 */         IMapper mapper = subDrawingsManager.getMapper();
/* 724 */         FormattersManager formatters = subDrawingsManager.getFormattersManager();
/* 725 */         DrawingsLabelHelper labelHelper = subDrawingsManager.getDrawingsLabelHelper();
/*     */ 
/* 727 */         labelHelper.reset();
/*     */ 
/* 729 */         draw(mainObjects, g, mapper, formatters, labelHelper, true, true, subWindowId);
/*     */ 
/* 731 */         Collection chartObjects = subDrawingsManager.getChartObjects();
/* 732 */         draw(chartObjects, g, mapper, formatters, labelHelper, true, false, subWindowId);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void draw(Collection<IChartObject> chartObjects, Graphics g, IMapper mapper, FormattersManager formatters, DrawingsLabelHelper labelHelper, boolean ignoreEdit, boolean globalOnly, int subWindowId) {
/* 738 */     for (IChartObject object : chartObjects) {
/* 739 */       ChartObject chartObject = (ChartObject)object;
/* 740 */       if (((ignoreEdit) && (chartObject.isUnderEdit())) || ((chartObject instanceof IDynamicChartObject))) {
/*     */         continue;
/*     */       }
/*     */       try
/*     */       {
/* 745 */         if ((chartObject.isSticky()) && ((chartObject instanceof AbstractStickablePointsChartObject)))
/*     */         {
/* 747 */           updateStickedTimePoints((AbstractStickablePointsChartObject)chartObject);
/*     */         }
/*     */ 
/* 750 */         if (chartObject.isGlobal()) {
/* 751 */           if (subWindowId < 0) {
/* 752 */             chartObject.drawGlobalOnMain(g, mapper, formatters, labelHelper, hasSubcharts());
/* 753 */             this.guiRefresher.refreshSubContents();
/*     */           } else {
/* 755 */             chartObject.drawGlobalOnSub(g, mapper, formatters, labelHelper, isLast(subWindowId));
/*     */           }
/*     */         }
/* 758 */         else if (!globalOnly)
/* 759 */           chartObject.draw(g, mapper, formatters, labelHelper);
/*     */       }
/*     */       catch (Exception e) {
/* 762 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void draw(ChartObject chartObject, Graphics g, IMapper mapper, FormattersManager formatters, DrawingsLabelHelper labelHelper, int subWindowId) {
/*     */     try {
/* 769 */       if (chartObject.isGlobal()) {
/* 770 */         if (subWindowId < 0) {
/* 771 */           chartObject.drawGlobalOnMain(g, mapper, formatters, labelHelper, hasSubcharts());
/* 772 */           this.guiRefresher.refreshSubContents();
/*     */         } else {
/* 774 */           chartObject.drawGlobalOnSub(g, mapper, formatters, labelHelper, isLast(subWindowId));
/*     */         }
/*     */       }
/*     */       else
/* 778 */         chartObject.draw(g, mapper, formatters, labelHelper);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 782 */       LOGGER.error("Error drawing " + chartObject.getClass().getName(), e);
/*     */     }
/* 784 */     drawHandlers(chartObject, g, mapper, false, subWindowId);
/*     */   }
/*     */ 
/*     */   private void updateStickedTimePoints(AbstractStickablePointsChartObject chartObject) {
/* 788 */     DataType dataType = this.chartState.getDataType();
/* 789 */     if (((!DataType.TIME_PERIOD_AGGREGATION.equals(dataType)) && (!DataType.TICKS.equals(dataType))) || (!((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(dataType)).getInstrument().equals(chartObject.getInstrumentOnShapeEdited())) || (!((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(DataType.TIME_PERIOD_AGGREGATION)).getOfferSide().equals(chartObject.getOfferSideOnShapeEdited())))
/*     */     {
/* 793 */       return;
/*     */     }
/*     */ 
/* 796 */     Period seqPeriod = ((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(dataType)).getPeriod();
/* 797 */     long seqFrom = ((AbstractDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(dataType)).getDataSequence()).getFrom();
/* 798 */     long seqTo = ((AbstractDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(dataType)).getDataSequence()).getTo();
/*     */ 
/* 800 */     for (int i = 0; i < chartObject.getPointsCount(); i++) {
/* 801 */       long pointAnalyzeInterval = chartObject.getPeriodOnPointsEdited(i).getInterval();
/*     */ 
/* 804 */       if (seqPeriod.getInterval() >= pointAnalyzeInterval)
/*     */         continue;
/* 806 */       double price = chartObject.getPrice(i);
/* 807 */       long time = chartObject.getTime(i);
/* 808 */       long timeAnalyzeEnd = time + pointAnalyzeInterval;
/*     */ 
/* 810 */       if ((seqFrom > time) || (seqTo < time)) {
/*     */         continue;
/*     */       }
/* 813 */       boolean fullIntervalAvailable = seqTo >= timeAnalyzeEnd;
/* 814 */       if (!fullIntervalAvailable) {
/* 815 */         timeAnalyzeEnd = seqTo;
/*     */       }
/*     */ 
/* 818 */       float precision = chartObject.getPrecision();
/*     */       long stickedTime;
/*     */       long stickedTime;
/* 820 */       if (DataType.TIME_PERIOD_AGGREGATION.equals(dataType)) {
/* 821 */         stickedTime = getStickedCandleTime(time, timeAnalyzeEnd, price, precision);
/*     */       }
/*     */       else {
/* 824 */         stickedTime = getStickedTickTime(time, timeAnalyzeEnd, price, precision, chartObject.getOfferSideOnShapeEdited());
/*     */       }
/*     */ 
/* 828 */       if (stickedTime != -1L) {
/* 829 */         chartObject.setTime(i, stickedTime);
/* 830 */         chartObject.setPeriodOnPointsEdited(i, seqPeriod);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean comparePrices(double price1, double price2, float precision)
/*     */   {
/* 839 */     return Math.abs(price1 - price2) < precision;
/*     */   }
/*     */ 
/*     */   private long getStickedCandleTime(long startTime, long timeAnalyzeEnd, double price, float precision) {
/* 843 */     long stickedTime = -1L;
/* 844 */     CandleDataSequence candleSequence = (CandleDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(DataType.TIME_PERIOD_AGGREGATION)).getDataSequence();
/* 845 */     CandleData[] candles = (CandleData[])candleSequence.getData();
/*     */ 
/* 848 */     if (comparePrices(candles[candleSequence.indexOf(startTime + 1L)].getOpen(), price, precision))
/*     */     {
/* 850 */       stickedTime = startTime;
/*     */     }
/* 852 */     else if (comparePrices(candles[candleSequence.indexOf(timeAnalyzeEnd)].getClose(), price, precision))
/*     */     {
/* 854 */       stickedTime = candles[candleSequence.indexOf(timeAnalyzeEnd)].getTime();
/*     */     }
/*     */     else {
/* 857 */       for (int index = candleSequence.indexOf(startTime + 1L); (index < candles.length) && (candles[index].getTime() <= timeAnalyzeEnd); index++) {
/* 858 */         if (comparePrices(candles[index].getHigh(), price, precision)) {
/* 859 */           stickedTime = candles[index].getTime();
/* 860 */           break;
/* 861 */         }if (comparePrices(candles[index].getLow(), price, precision)) {
/* 862 */           stickedTime = candles[index].getTime();
/* 863 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 867 */     if ((-1L != stickedTime) && (stickedTime < startTime))
/*     */     {
/* 869 */       stickedTime = -1L;
/*     */     }
/* 871 */     return stickedTime;
/*     */   }
/*     */ 
/*     */   private long getStickedTickTime(long startTime, long timeAnalyzeEnd, double price, float precision, OfferSide offerSide) {
/* 875 */     long stickedTime = -1L;
/* 876 */     TickDataSequence tickSequence = (TickDataSequence)((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(DataType.TICKS)).getDataSequence();
/* 877 */     TickData[] ticks = (TickData[])tickSequence.getData();
/*     */ 
/* 879 */     int index = tickSequence.indexOf(startTime);
/* 880 */     if (ticks[index].getTime() < startTime) {
/* 881 */       index++;
/*     */     }
/* 883 */     for (; (index < ticks.length) && (ticks[index].getTime() <= timeAnalyzeEnd); index++) {
/* 884 */       double tickPrice = OfferSide.ASK.equals(offerSide) ? ticks[index].getAsk() : ticks[index].getBid();
/* 885 */       if (comparePrices(tickPrice, price, precision)) {
/* 886 */         stickedTime = ticks[index].getTime();
/* 887 */         break;
/*     */       }
/*     */     }
/* 890 */     if ((-1L != stickedTime) && (stickedTime < startTime))
/*     */     {
/* 892 */       stickedTime = -1L;
/*     */     }
/* 894 */     return stickedTime;
/*     */   }
/*     */ 
/*     */   protected boolean hasSubcharts()
/*     */   {
/* 899 */     return this.guiRefresher.getWindowsCount() > 1;
/*     */   }
/*     */ 
/*     */   protected boolean isLast(int subWindowId) {
/* 903 */     return this.guiRefresher.isSubChartLast(subWindowId);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.DrawingsManagerContainerImpl
 * JD-Core Version:    0.6.0
 */