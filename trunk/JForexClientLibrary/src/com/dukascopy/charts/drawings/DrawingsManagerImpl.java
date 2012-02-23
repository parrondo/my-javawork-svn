/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.ChartObjectEvent;
/*     */ import com.dukascopy.api.ChartObjectListener;
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.CandlesDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.dialogs.drawings.EditCoordinatesDialog;
/*     */ import com.dukascopy.charts.dialogs.drawings.NumericParamEditDialog;
/*     */ import com.dukascopy.charts.dialogs.drawings.StyledStringTransformer;
/*     */ import com.dukascopy.charts.dialogs.drawings.TextLabelEditDialog;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.listeners.drawing.DrawingActionListener;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.mappers.Mapper;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.utils.AWTThreadExecutor;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.swing.AbstractChartWidgetPanel;
/*     */ import com.dukascopy.charts.view.swing.OhlcChartWidgetPanel;
/*     */ import com.dukascopy.charts.view.swing.PatternChartWidgetPanel;
/*     */ import java.awt.Point;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.JLayeredPane;
/*     */ 
/*     */ abstract class DrawingsManagerImpl
/*     */   implements IDrawingsManager, IDrawingsContainer
/*     */ {
/*     */   public static final int DEFAULT_RANGE = 10;
/*     */   protected final IMapper mapper;
/*     */   protected final GuiRefresher guiRefresher;
/*     */   protected final DrawingsFactory drawingsFactory;
/*     */   protected final FormattersManager formattersManager;
/*     */   protected final ChartsActionListenerRegistry chartsActionListenerRegistry;
/*     */   protected final DrawingActionListener drawingActionListener;
/*     */   protected final DrawingsLabelHelper drawingsLabelHelper;
/*     */   protected final ChartState chartState;
/*     */   protected final Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders;
/*     */   protected final GeometryCalculator geometryCalculator;
/*     */   protected IMainOperationManager mainOperationManager;
/*  57 */   protected final Map<String, IChartObject> commonChartObjects = Collections.synchronizedMap(new HashMap());
/*  58 */   protected final Map<String, IDynamicChartObject> dynamicChartObjects = Collections.synchronizedMap(new HashMap());
/*     */   protected IChartObject newChartObject;
/*     */   protected ChartObject editedChartObject;
/*     */   protected ChartObject highlightedObject;
/*     */   protected Point prevPoint;
/*     */ 
/*     */   public DrawingsManagerImpl(IMapper mapper, FormattersManager formattersManager, GuiRefresher guiRefresher, DrawingsFactory drawingsFactory, DrawingsLabelHelper drawingsLabelHelper, DrawingActionListener drawingActionListener, ChartsActionListenerRegistry chartsActionListenerRegistry, ChartState chartState, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders, GeometryCalculator geometryCalculator)
/*     */   {
/*  80 */     this.mapper = mapper;
/*  81 */     this.guiRefresher = guiRefresher;
/*  82 */     this.formattersManager = formattersManager;
/*  83 */     this.drawingsFactory = drawingsFactory;
/*  84 */     this.drawingsLabelHelper = drawingsLabelHelper;
/*  85 */     this.drawingActionListener = drawingActionListener;
/*  86 */     this.chartsActionListenerRegistry = chartsActionListenerRegistry;
/*  87 */     this.chartState = chartState;
/*  88 */     this.allDataSequenceProviders = allDataSequenceProviders;
/*  89 */     this.geometryCalculator = geometryCalculator;
/*     */   }
/*     */ 
/*     */   public void setMainOperationManager(IMainOperationManager mainOperationManager) {
/*  93 */     this.mainOperationManager = mainOperationManager;
/*     */   }
/*     */ 
/*     */   public ChartsActionListenerRegistry getChartActionListenerRegistry()
/*     */   {
/*  99 */     return this.chartsActionListenerRegistry;
/*     */   }
/*     */ 
/*     */   public IChartObject getNewChartObject()
/*     */   {
/* 104 */     return this.newChartObject;
/*     */   }
/*     */ 
/*     */   public void drawingNew(IChartObject chartObject)
/*     */   {
/* 110 */     this.newChartObject = chartObject;
/*     */   }
/*     */ 
/*     */   public boolean isDrawingNew()
/*     */   {
/* 115 */     return (this.newChartObject != null) && (((ChartObject)this.newChartObject).isUnderEdit());
/*     */   }
/*     */ 
/*     */   public IMapper getMapper() {
/* 119 */     return this.mapper;
/*     */   }
/*     */ 
/*     */   public FormattersManager getFormattersManager() {
/* 123 */     return this.formattersManager;
/*     */   }
/*     */ 
/*     */   public DrawingsLabelHelper getDrawingsLabelHelper() {
/* 127 */     return this.drawingsLabelHelper;
/*     */   }
/*     */ 
/*     */   public boolean contains(IChartObject chartObject)
/*     */   {
/* 132 */     return this.commonChartObjects.containsValue(chartObject);
/*     */   }
/*     */ 
/*     */   public IChartObject getDrawingByKey(String key)
/*     */   {
/* 137 */     return (IChartObject)this.commonChartObjects.get(key);
/*     */   }
/*     */ 
/*     */   public ChartObject getEditedChartObject()
/*     */   {
/* 142 */     return this.editedChartObject;
/*     */   }
/*     */ 
/*     */   public ChartObject getHighlightedChartObject()
/*     */   {
/* 147 */     return this.highlightedObject;
/*     */   }
/*     */ 
/*     */   public boolean isEditingDrawing()
/*     */   {
/* 152 */     return this.editedChartObject != null;
/*     */   }
/*     */ 
/*     */   public void finishDrawingNewDrawing(ChartObject newChartObject)
/*     */   {
/* 157 */     if (newChartObject != null) {
/* 158 */       newChartObject.finishDrawing();
/* 159 */       addNewDrawing(newChartObject);
/* 160 */       this.drawingActionListener.drawingEnded();
/* 161 */       refreshWindow();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean addNewPointToNewDrawing(ChartObject newChartObject, Point point)
/*     */   {
/* 168 */     if (newChartObject.supportsStyledLabel()) {
/* 169 */       if (updateTextChartObject((TextChartObject)newChartObject)) {
/* 170 */         newChartObject.addNewPoint(point, this.mapper);
/* 171 */         addNewDrawing(newChartObject);
/*     */       } else {
/* 173 */         newChartObject = null;
/*     */       }
/* 175 */       return true;
/*     */     }
/* 177 */     boolean drawingIsFinished = newChartObject.addNewPoint(point, this.mapper);
/* 178 */     if (drawingIsFinished) {
/* 179 */       finishDrawingNewDrawing(newChartObject);
/*     */     }
/* 181 */     return drawingIsFinished;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(ChartObject newChartObject, Point point, boolean repaintWindow)
/*     */   {
/* 187 */     newChartObject.modifyNewDrawing(point, this.mapper, 10);
/*     */ 
/* 189 */     updateStickableChartObjectProperties(newChartObject);
/*     */ 
/* 191 */     if (repaintWindow)
/* 192 */       repaintWindow();
/*     */   }
/*     */ 
/*     */   public void modifyEditingDrawing(Point point)
/*     */   {
/* 198 */     if (this.editedChartObject == null) {
/* 199 */       return;
/*     */     }
/* 201 */     this.editedChartObject.modifyEditedDrawing(point, this.prevPoint, this.mapper, 10);
/*     */ 
/* 203 */     updateStickableChartObjectProperties(this.editedChartObject);
/*     */ 
/* 205 */     this.prevPoint = point;
/*     */   }
/*     */ 
/*     */   private void updateStickableChartObjectProperties(IChartObject chartObject) {
/* 209 */     if ((chartObject instanceof AbstractStickablePointsChartObject)) {
/* 210 */       AbstractStickablePointsChartObject stickableChartObject = (AbstractStickablePointsChartObject)chartObject;
/*     */ 
/* 212 */       CandlesDataSequenceProvider candlesDataSequenceProvider = (CandlesDataSequenceProvider)this.allDataSequenceProviders.get(DataType.TIME_PERIOD_AGGREGATION);
/* 213 */       if (candlesDataSequenceProvider.isActive()) {
/* 214 */         stickableChartObject.setPeriodToAllPoints(candlesDataSequenceProvider.getPeriod());
/* 215 */         stickableChartObject.setPrecision(this.mapper.getValuesInOnePixel());
/* 216 */         stickableChartObject.setInstrumentOnShapeEdited(candlesDataSequenceProvider.getInstrument());
/* 217 */         stickableChartObject.setOfferSideOnShapeEdited(candlesDataSequenceProvider.getOfferSide());
/*     */       } else {
/* 219 */         stickableChartObject.setPeriodToAllPoints(Period.TICK);
/* 220 */         stickableChartObject.setPrecision(this.mapper.getValuesInOnePixel());
/* 221 */         stickableChartObject.setInstrumentOnShapeEdited(null);
/* 222 */         stickableChartObject.setOfferSideOnShapeEdited(null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updatePrevPointAndSelectedHandler(Point point)
/*     */   {
/* 229 */     this.prevPoint = point;
/* 230 */     if (this.editedChartObject != null)
/* 231 */       this.editedChartObject.updateSelectedHandler(point, this.mapper, 10);
/*     */   }
/*     */ 
/*     */   public boolean isSomeDrawingHighlighted()
/*     */   {
/* 237 */     return this.highlightedObject != null;
/*     */   }
/*     */ 
/*     */   protected boolean triggerHighlightingOfDrawings(Point point, Collection<IChartObject> iChartObjects) {
/* 241 */     boolean isDrawingHighlighted = false;
/* 242 */     for (IChartObject unlockedChartObject : iChartObjects) {
/* 243 */       ChartObject chartObject = (ChartObject)unlockedChartObject;
/* 244 */       boolean intersects = chartObject.intersects(point, this.mapper, 10);
/* 245 */       if ((intersects) && (this.highlightedObject == null)) {
/* 246 */         if (!chartObject.isHighlighted()) {
/* 247 */           chartObject.setHighlighted(true);
/*     */         }
/* 249 */         this.highlightedObject = chartObject;
/* 250 */         isDrawingHighlighted = true;
/* 251 */       } else if ((!intersects) && (chartObject.isHighlighted())) {
/* 252 */         chartObject.setHighlighted(false);
/* 253 */         if (chartObject == this.highlightedObject) {
/* 254 */           this.highlightedObject = null;
/*     */         }
/*     */       }
/*     */     }
/* 258 */     return isDrawingHighlighted;
/*     */   }
/*     */ 
/*     */   public void selectHighlitedDrawing()
/*     */   {
/* 263 */     selectDrawing(this.highlightedObject);
/*     */   }
/*     */ 
/*     */   public boolean selectDrawingToBeEditedAndStartEditingDrawing(Point point)
/*     */   {
/* 268 */     if (!isSomeDrawingHighlighted()) {
/* 269 */       return false;
/*     */     }
/* 271 */     this.prevPoint = point;
/* 272 */     this.highlightedObject.updateSelectedHandler(point, this.mapper, 10);
/* 273 */     selectDrawing(this.highlightedObject);
/* 274 */     return true;
/*     */   }
/*     */ 
/*     */   public void selectDrawingToBeEdited(Point point)
/*     */   {
/* 279 */     selectDrawing(findIntersectionObject(point));
/*     */   }
/*     */ 
/*     */   public void selectDrawing(IChartObject chartObject)
/*     */   {
/* 284 */     selectDrawing((ChartObject)chartObject);
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEditedAndExitEditingMode()
/*     */   {
/* 289 */     unselectDrawingToBeEdited();
/* 290 */     this.drawingActionListener.drawingEditingEnded();
/* 291 */     refreshWindow();
/*     */   }
/*     */ 
/*     */   protected abstract void selectDrawing(ChartObject paramChartObject);
/*     */ 
/*     */   public boolean intersectsDrawingToBeEdited(Point point)
/*     */   {
/* 299 */     return (this.editedChartObject != null) && (this.editedChartObject.intersects(point, this.mapper, 5));
/*     */   }
/*     */ 
/*     */   public boolean intersectsDrawing(Point point)
/*     */   {
/* 304 */     return findIntersectionObject(point) != null;
/*     */   }
/*     */ 
/*     */   public void deleteSelectedDrawing()
/*     */   {
/* 309 */     if (this.editedChartObject == null) {
/* 310 */       return;
/*     */     }
/*     */ 
/* 313 */     ChartObjectEvent objectEvent = new ChartObjectEvent(this.editedChartObject);
/* 314 */     this.editedChartObject.getChartObjectListener().deleted(objectEvent);
/* 315 */     if (!objectEvent.isCanceled())
/* 316 */       performRemove(this.editedChartObject);
/*     */   }
/*     */ 
/*     */   public boolean updateTextChartObject(TextChartObject textChartObject)
/*     */   {
/* 322 */     TextLabelEditDialog textDialog = new TextLabelEditDialog(null, textChartObject.getStyledStringTransformer());
/* 323 */     StyledStringTransformer newStyledString = textDialog.getValue();
/* 324 */     if (newStyledString != null) {
/* 325 */       textChartObject.setStyledString(newStyledString);
/* 326 */       return true;
/*     */     }
/* 328 */     return false;
/*     */   }
/*     */ 
/*     */   public void updateChartObjectPricesManualy(ChartObject chartObject)
/*     */   {
/* 334 */     new EditCoordinatesDialog(null, chartObject, this.chartState.getInstrument().getPipScale(), this.chartState.getInstrument().getPipValue());
/*     */   }
/*     */ 
/*     */   public void updatePipsPerBarOption(ChartObject chartObject)
/*     */   {
/* 348 */     if ((chartObject instanceof GannAnglesChartObject))
/*     */     {
/* 350 */       NumericParamEditDialog dialog = new NumericParamEditDialog(null, "menu.item.pips.per.bar", ((GannAnglesChartObject)chartObject).getPipsPerBar(), -100.0D, 100.0D, 3, 2, 0.1D);
/*     */ 
/* 352 */       if ((dialog.getResultValue() != null) && (Math.abs(dialog.getResultValue().doubleValue()) > 0.01D))
/*     */       {
/* 354 */         ((GannAnglesChartObject)chartObject).setPipsPerBar(dialog.getResultValue().doubleValue());
/*     */       }
/* 356 */     } else if ((chartObject instanceof GannGridChartObject))
/*     */     {
/* 358 */       NumericParamEditDialog dialog = new NumericParamEditDialog(null, "menu.item.pips.per.bar", ((GannGridChartObject)chartObject).getPipsPerBar(), -100.0D, 100.0D, 3, 2, 0.1D);
/*     */ 
/* 360 */       if ((dialog.getResultValue() != null) && (Math.abs(dialog.getResultValue().doubleValue()) > 0.01D))
/*     */       {
/* 362 */         ((GannGridChartObject)chartObject).setPipsPerBar(dialog.getResultValue().doubleValue());
/*     */       }
/*     */     } else {
/* 365 */       throw new IllegalStateException("unsupported chart object type");
/*     */     }
/*     */     NumericParamEditDialog dialog;
/* 368 */     dialog.dispose();
/*     */   }
/*     */ 
/*     */   public void addNewDrawing(ChartObject newChartObject) {
/* 372 */     newChartObject.setUnderEdit(false);
/* 373 */     addChartObject(newChartObject);
/* 374 */     newChartObject = null;
/*     */   }
/*     */ 
/*     */   protected List<IChartObject> performRemove(List<IChartObject> chartObjects) {
/* 378 */     if (chartObjects == null) {
/* 379 */       return null;
/*     */     }
/*     */ 
/* 382 */     List removedObjects = new ArrayList();
/*     */ 
/* 384 */     for (IChartObject chartObject : chartObjects) {
/* 385 */       IChartObject removed = (IChartObject)this.commonChartObjects.remove(chartObject.getKey());
/* 386 */       if ((chartObject instanceof IDynamicChartObject)) {
/* 387 */         this.dynamicChartObjects.remove(chartObject.getKey());
/*     */       }
/* 389 */       if ((chartObject instanceof AbstractWidgetChartObject)) {
/* 390 */         AbstractChartWidgetPanel widget = ((AbstractWidgetChartObject)chartObject).getChartWidgetPanel();
/* 391 */         this.guiRefresher.getChartsLayeredPane().remove(widget);
/*     */       }
/* 393 */       if (removed != null) {
/* 394 */         removedObjects.add(removed);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 401 */     new AWTThreadExecutor(removedObjects)
/*     */     {
/*     */       public void invoke() {
/* 404 */         for (IChartObject chartObject : this.val$removedObjects) {
/* 405 */           if (chartObject != null) {
/* 406 */             DrawingsManagerImpl.this.drawingRemoved(chartObject);
/*     */           }
/* 408 */           if (chartObject == DrawingsManagerImpl.this.editedChartObject) {
/* 409 */             DrawingsManagerImpl.this.editedChartObject = null;
/*     */           }
/*     */         }
/* 412 */         DrawingsManagerImpl.this.highlightedObject = null;
/* 413 */         DrawingsManagerImpl.this.drawingActionListener.drawingEditingEnded();
/* 414 */         DrawingsManagerImpl.this.refreshWindow();
/*     */       }
/*     */     }
/* 401 */     .execute();
/*     */ 
/* 418 */     return removedObjects;
/*     */   }
/*     */ 
/*     */   protected IChartObject performRemove(IChartObject chartObject) {
/* 422 */     List result = performRemove(Collections.singletonList(chartObject));
/* 423 */     return (result != null) && (!result.isEmpty()) ? (IChartObject)result.get(0) : null;
/*     */   }
/*     */ 
/*     */   protected ChartObject findIntersectionObject(Point point)
/*     */   {
/* 429 */     Collection chartObjects = this.commonChartObjects.values();
/* 430 */     synchronized (this.commonChartObjects) {
/* 431 */       for (IChartObject commonChartObject : chartObjects) {
/* 432 */         ChartObject chartObject = (ChartObject)commonChartObject;
/* 433 */         if (chartObject.intersects(point, this.mapper, 5)) {
/* 434 */           return chartObject;
/*     */         }
/*     */       }
/*     */     }
/* 438 */     return null;
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingLeft()
/*     */   {
/* 444 */     this.editedChartObject.moveLeft(this.mapper);
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingRight()
/*     */   {
/* 449 */     this.editedChartObject.moveRight(this.mapper);
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingDown()
/*     */   {
/* 454 */     this.editedChartObject.moveDown(this.mapper);
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingUp()
/*     */   {
/* 459 */     this.editedChartObject.moveUp(this.mapper);
/*     */   }
/*     */ 
/*     */   public void mouseWheelUp()
/*     */   {
/* 465 */     this.editedChartObject.mouseWheelUp(this.mapper);
/*     */   }
/*     */ 
/*     */   public void mouseWheelDown()
/*     */   {
/* 470 */     this.editedChartObject.mouseWheelDown(this.mapper);
/*     */   }
/*     */ 
/*     */   public void addChartObjects(List<IChartObject> chartObjects)
/*     */   {
/* 476 */     if (chartObjects == null) {
/* 477 */       return;
/*     */     }
/* 479 */     for (IChartObject chartObject : chartObjects)
/* 480 */       addChartObject(chartObject);
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getChartObjects()
/*     */   {
/* 485 */     List chartObjects = new ArrayList();
/* 486 */     if (this.commonChartObjects != null) {
/* 487 */       synchronized (this.commonChartObjects) {
/* 488 */         chartObjects.addAll(this.commonChartObjects.values());
/*     */       }
/*     */     }
/* 491 */     return chartObjects;
/*     */   }
/*     */ 
/*     */   public List<IDynamicChartObject> getDynamicChartObjects() {
/* 495 */     List chartObjects = new ArrayList();
/* 496 */     if (this.dynamicChartObjects != null) {
/* 497 */       synchronized (this.dynamicChartObjects) {
/* 498 */         chartObjects.addAll(this.dynamicChartObjects.values());
/*     */       }
/*     */     }
/* 501 */     return chartObjects;
/*     */   }
/*     */ 
/*     */   public void addChartObject(IChartObject iChartObject)
/*     */   {
/* 506 */     if (iChartObject == null) {
/* 507 */       return;
/*     */     }
/*     */ 
/* 510 */     if ((iChartObject instanceof AbstractWidgetChartObject)) {
/* 511 */       JLayeredPane layeredPane = this.guiRefresher.getChartsLayeredPane();
/* 512 */       AbstractChartWidgetPanel widget = null;
/* 513 */       if ((iChartObject instanceof OhlcChartObject)) {
/* 514 */         widget = new OhlcChartWidgetPanel((OhlcChartObject)iChartObject, this);
/* 515 */       } else if ((iChartObject instanceof PatternWidgetChartObject)) {
/* 516 */         for (Map.Entry entry : this.commonChartObjects.entrySet()) {
/* 517 */           if ((entry.getValue() instanceof PatternWidgetChartObject))
/*     */           {
/* 519 */             return;
/*     */           }
/*     */         }
/* 522 */         widget = new PatternChartWidgetPanel((PatternWidgetChartObject)iChartObject, this, this.chartState, this.allDataSequenceProviders, this.geometryCalculator, this.mainOperationManager, ((Mapper)this.mapper).getTimeToXMapper(), ((Mapper)this.mapper).getValueToYMapper());
/*     */       }
/*     */ 
/* 533 */       layeredPane.add(widget, JLayeredPane.PALETTE_LAYER);
/*     */     }
/*     */ 
/* 536 */     this.commonChartObjects.put(iChartObject.getKey(), iChartObject);
/* 537 */     if ((iChartObject instanceof IDynamicChartObject)) {
/* 538 */       this.dynamicChartObjects.put(iChartObject.getKey(), (IDynamicChartObject)iChartObject);
/*     */     }
/*     */ 
/* 544 */     new AWTThreadExecutor(iChartObject)
/*     */     {
/*     */       public void invoke() {
/* 547 */         DrawingsManagerImpl.this.drawingAdded(this.val$iChartObject);
/*     */       }
/*     */     }
/* 544 */     .execute();
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject)
/*     */   {
/* 555 */     ChartObjectEvent objectEvent = new ChartObjectEvent(chartObject);
/* 556 */     ((ChartObject)chartObject).getChartObjectListener().deleted(objectEvent);
/* 557 */     if (!objectEvent.isCanceled())
/* 558 */       performRemove((ChartObject)chartObject);
/*     */   }
/*     */ 
/*     */   public IChartObject remove(String key)
/*     */   {
/* 563 */     IChartObject chartObjectToRemove = (IChartObject)this.commonChartObjects.get(key);
/* 564 */     remove(chartObjectToRemove);
/*     */ 
/* 566 */     return chartObjectToRemove;
/*     */   }
/*     */ 
/*     */   public void remove(List<IChartObject> chartObjects)
/*     */   {
/* 571 */     for (IChartObject chartObject : chartObjects) {
/* 572 */       this.commonChartObjects.remove(chartObject.getKey());
/* 573 */       if ((chartObject instanceof IDynamicChartObject)) {
/* 574 */         this.dynamicChartObjects.remove(chartObject.getKey());
/*     */       }
/* 576 */       if ((chartObject instanceof AbstractWidgetChartObject)) {
/* 577 */         AbstractChartWidgetPanel widget = ((AbstractWidgetChartObject)chartObject).getChartWidgetPanel();
/* 578 */         this.guiRefresher.getChartsLayeredPane().remove(widget);
/*     */       }
/*     */     }
/* 581 */     refreshWindow();
/*     */   }
/*     */ 
/*     */   public void removeAllDrawings() {
/* 585 */     List chartObjects = new ArrayList(this.commonChartObjects.values());
/* 586 */     this.commonChartObjects.clear();
/* 587 */     this.dynamicChartObjects.clear();
/*     */ 
/* 589 */     for (IChartObject chartObj : chartObjects) {
/* 590 */       if ((chartObj instanceof AbstractWidgetChartObject)) {
/* 591 */         AbstractChartWidgetPanel widget = ((AbstractWidgetChartObject)chartObj).getChartWidgetPanel();
/* 592 */         this.guiRefresher.getChartsLayeredPane().remove(widget);
/*     */       }
/*     */     }
/*     */ 
/* 596 */     refreshWindow();
/* 597 */     drawingsRemoved(chartObjects);
/*     */   }
/*     */ 
/*     */   protected abstract void refreshWindow();
/*     */ 
/*     */   protected abstract void repaintWindow();
/*     */ 
/*     */   protected abstract void drawingAdded(IChartObject paramIChartObject);
/*     */ 
/*     */   protected abstract void drawingRemoved(IChartObject paramIChartObject);
/*     */ 
/*     */   protected abstract void drawingsRemoved(List<IChartObject> paramList);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.DrawingsManagerImpl
 * JD-Core Version:    0.6.0
 */