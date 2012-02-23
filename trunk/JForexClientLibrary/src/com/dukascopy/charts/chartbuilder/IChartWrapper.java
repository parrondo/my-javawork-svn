/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.drawings.IChartObjectFactory;
/*     */ import com.dukascopy.api.impl.IndicatorHolder;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.IMainDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.SubDrawingsManagerImpl;
/*     */ import com.dukascopy.charts.main.DDSChartsControllerImpl;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.main.interfaces.IChartController;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingsManagerContainer;
/*     */ import com.dukascopy.charts.wrapper.AbstractChartWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.JColorComboBox;
/*     */ import com.dukascopy.dds2.greed.util.IndicatorHelper;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IChartWrapper extends AbstractChartWrapper
/*     */ {
/*  53 */   static final Logger LOGGER = LoggerFactory.getLogger(IChartWrapper.class);
/*     */   Instrument instrument;
/*     */   OfferSide offerSide;
/*     */   final GuiRefresher guiRefresher;
/*     */   final IMainDrawingsManager mainDrawingsManager;
/*     */   final IDataManager dataManager;
/*     */   IChartController chartController;
/*     */   final IDrawingsManagerContainer drawingsManagerContainer;
/*     */ 
/*     */   IChartWrapper(Instrument instrument, OfferSide initialOfferSide, GuiRefresher guiRefresher, IDataManager dataManager, IMainDrawingsManager mainDrawingsManager, IDrawingsManagerContainer drawingsManagerContainer)
/*     */   {
/*  72 */     this.instrument = instrument;
/*  73 */     this.offerSide = initialOfferSide;
/*  74 */     this.guiRefresher = guiRefresher;
/*  75 */     this.dataManager = dataManager;
/*  76 */     this.mainDrawingsManager = mainDrawingsManager;
/*  77 */     this.drawingsManagerContainer = drawingsManagerContainer;
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/*  83 */     return this.mainDrawingsManager.draw(key, type, time1, price1, time2, price2, time3, price3);
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/*  90 */     return this.mainDrawingsManager.draw(key, type, time1, price1, time2, price2);
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1)
/*     */   {
/*  95 */     return this.mainDrawingsManager.draw(key, type, time1, price1);
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/* 101 */     return this.mainDrawingsManager.drawUnlocked(key, type, time1, price1, time2, price2, time3, price3);
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/* 108 */     return this.mainDrawingsManager.drawUnlocked(key, type, time1, price1, time2, price2);
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1)
/*     */   {
/* 114 */     return this.mainDrawingsManager.drawUnlocked(key, type, time1, price1);
/*     */   }
/*     */ 
/*     */   public void move(IChartObject objectToMove, long newTime, double newPrice)
/*     */   {
/* 119 */     objectToMove.move(newTime, newPrice);
/*     */   }
/*     */ 
/*     */   public void move(String chartObjectKey, long newTime, double newPrice)
/*     */   {
/* 124 */     IChartObject chartObjectToMove = this.mainDrawingsManager.get(chartObjectKey);
/* 125 */     if (chartObjectToMove != null)
/* 126 */       chartObjectToMove.move(newTime, newPrice);
/*     */   }
/*     */ 
/*     */   public void comment(String comment)
/*     */   {
/* 132 */     this.mainDrawingsManager.addComment(comment);
/* 133 */     this.guiRefresher.refreshMainContent();
/*     */   }
/*     */ 
/*     */   public void setCommentColor(Color color)
/*     */   {
/* 138 */     this.mainDrawingsManager.setCommentColor(color);
/*     */   }
/*     */ 
/*     */   public Color getCommentColor()
/*     */   {
/* 143 */     return this.mainDrawingsManager.getCommentColor();
/*     */   }
/*     */ 
/*     */   public void setCommentFont(Font font)
/*     */   {
/* 148 */     this.mainDrawingsManager.setCommentFont(font);
/*     */   }
/*     */ 
/*     */   public Font getCommentFont()
/*     */   {
/* 153 */     return this.mainDrawingsManager.getCommentFont();
/*     */   }
/*     */ 
/*     */   public void setCommentHorizontalPosition(int position)
/*     */   {
/* 158 */     this.mainDrawingsManager.setCommentHorizontalPosition(position);
/*     */   }
/*     */ 
/*     */   public int getCommentHorizontalPosition()
/*     */   {
/* 163 */     return this.mainDrawingsManager.getCommentHorizontalPosition();
/*     */   }
/*     */ 
/*     */   public void setCommentVerticalPosition(int position)
/*     */   {
/* 168 */     this.mainDrawingsManager.setCommentVerticalPosition(position);
/*     */   }
/*     */ 
/*     */   public int getCommentVerticalPosition()
/*     */   {
/* 173 */     return this.mainDrawingsManager.getCommentVerticalPosition();
/*     */   }
/*     */ 
/*     */   public IChartObject get(String key)
/*     */   {
/* 178 */     return this.mainDrawingsManager.get(key);
/*     */   }
/*     */ 
/*     */   public IChartObject remove(String key)
/*     */   {
/* 183 */     List result = doRemove(Collections.singletonList(key));
/* 184 */     return (result != null) && (!result.isEmpty()) ? (IChartObject)result.get(0) : null;
/*     */   }
/*     */ 
/*     */   public List<IChartObject> doRemove(List<String> keys)
/*     */   {
/* 190 */     if (keys == null) {
/* 191 */       return null;
/*     */     }
/*     */ 
/* 194 */     List removedObjects = this.mainDrawingsManager.removeChartObjectsByKeys(keys);
/*     */ 
/* 196 */     for (Iterator i$ = removedObjects.iterator(); i$.hasNext(); ) { removedObject = (IChartObject)i$.next();
/* 197 */       Set subWindowIds = this.drawingsManagerContainer.getSubWindowsIds();
/* 198 */       for (Integer id : subWindowIds) {
/* 199 */         Map subDrawingManagers = this.drawingsManagerContainer.getSubDrawingsManagers(id.intValue());
/* 200 */         for (Map.Entry entry : subDrawingManagers.entrySet())
/* 201 */           if (((SubDrawingsManagerImpl)entry.getValue()).getDrawingByKey(removedObject.getKey()) != null)
/* 202 */             removedObject = ((SubDrawingsManagerImpl)entry.getValue()).remove(removedObject.getKey());
/*     */       }
/*     */     }
/*     */     IChartObject removedObject;
/* 207 */     return removedObjects;
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject)
/*     */   {
/* 212 */     remove(chartObject.getKey());
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getAll()
/*     */   {
/* 217 */     return this.mainDrawingsManager.getAll();
/*     */   }
/*     */ 
/*     */   public void removeAll()
/*     */   {
/* 222 */     this.mainDrawingsManager.removeAllDrawings();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 227 */     return this.mainDrawingsManager.size();
/*     */   }
/*     */ 
/*     */   public Iterator<IChartObject> iterator()
/*     */   {
/* 232 */     return this.mainDrawingsManager.iterator();
/*     */   }
/*     */ 
/*     */   public double priceMin(int index)
/*     */   {
/* 237 */     Integer indicatorid = this.guiRefresher.getBasicIndicatorIdByWindowIndex(index);
/* 238 */     return this.dataManager.getMinPriceFor(indicatorid);
/*     */   }
/*     */ 
/*     */   public double priceMax(int index)
/*     */   {
/* 243 */     Integer indicatorid = this.guiRefresher.getBasicIndicatorIdByWindowIndex(index);
/* 244 */     return this.dataManager.getMaxPriceFor(indicatorid);
/*     */   }
/*     */ 
/*     */   public int getBarsCount()
/*     */   {
/* 249 */     return this.dataManager.getSequenceSize();
/*     */   }
/*     */ 
/*     */   public int windowsTotal()
/*     */   {
/* 254 */     return this.guiRefresher.getWindowsCount();
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 259 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument)
/*     */   {
/* 264 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public Period getSelectedPeriod()
/*     */   {
/* 269 */     return this.dataManager.getPeriod();
/*     */   }
/*     */ 
/*     */   public OfferSide getSelectedOfferSide()
/*     */   {
/* 274 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   public void offerSideChanged(OfferSide newOfferSide) {
/* 278 */     this.offerSide = newOfferSide;
/*     */   }
/*     */ 
/*     */   public void setChartController(IChartController chartController) {
/* 282 */     this.chartController = chartController;
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator)
/*     */   {
/* 287 */     addIndicator(indicator, null, null, null, null, null);
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams)
/*     */   {
/* 292 */     addIndicator(indicator, optParams, null, null, null, null);
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] outputDrawingStyles, int[] outputWidths)
/*     */   {
/* 299 */     addIndicator(indicator, optParams, outputColors, outputDrawingStyles, outputWidths, null);
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] outputDrawingStyles, int[] outputWidths, int[] outputShifts)
/*     */   {
/* 312 */     if (this.chartController != null)
/*     */     {
/* 314 */       IndicatorHolder cachedHolder = IndicatorsProvider.getInstance().getSavedIndicatorHolder(indicator);
/*     */       IndicatorHolder indicatorHolder;
/*     */       IndicatorHolder indicatorHolder;
/* 315 */       if (cachedHolder == null) {
/* 316 */         indicatorHolder = new IndicatorHolder(indicator, IndicatorHelper.createIndicatorContext());
/*     */       }
/*     */       else {
/* 319 */         indicatorHolder = cachedHolder;
/* 320 */         IndicatorsProvider.getInstance().removeSavedIndicatorHolder(indicator);
/*     */       }
/*     */ 
/* 324 */       IndicatorInfo info = indicator.getIndicatorInfo();
/* 325 */       if (info != null) {
/* 326 */         int oNumbers = info.getNumberOfOutputs();
/* 327 */         if ((oNumbers > 0) && (indicator.getOutputParameterInfo(0).getColor() == null)) {
/* 328 */           for (int i = 0; i < oNumbers; i++) {
/* 329 */             OutputParameterInfo paramInfo = indicator.getOutputParameterInfo(i);
/* 330 */             if ((paramInfo != null) && (i < JColorComboBox.COLORS.length)) {
/* 331 */               paramInfo.setColor(JColorComboBox.COLORS[i]);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 338 */         AccessController.doPrivileged(new PrivilegedExceptionAction(indicatorHolder, optParams, outputColors, outputDrawingStyles, outputWidths, outputShifts)
/*     */         {
/*     */           public Object run() throws Exception {
/* 341 */             IChartWrapper.this.chartController.addIndicator(new IndicatorWrapper(this.val$indicatorHolder, this.val$optParams, this.val$outputColors, this.val$outputDrawingStyles, this.val$outputWidths, this.val$outputShifts));
/*     */ 
/* 345 */             return null;
/*     */           } } );
/*     */       } catch (PrivilegedActionException ex) {
/* 349 */         LOGGER.error("Error while adding indicator : " + indicator, ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addSubIndicator(Integer subChartId, IIndicator indicator)
/*     */   {
/* 357 */     if (this.chartController != null)
/*     */       try {
/* 359 */         AccessController.doPrivileged(new PrivilegedExceptionAction(subChartId, indicator) {
/*     */           public Object run() throws Exception {
/* 361 */             IChartWrapper.this.chartController.addIndicatorOnSubWin(this.val$subChartId, new IndicatorWrapper(new IndicatorHolder(this.val$indicator, IndicatorHelper.createIndicatorContext())));
/*     */ 
/* 371 */             return null;
/*     */           } } );
/*     */       } catch (PrivilegedActionException ex) {
/* 375 */         LOGGER.error("Error while adding indicator : " + indicator, ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public List<IIndicator> getIndicators()
/*     */   {
/* 382 */     List indicators = new ArrayList();
/* 383 */     if (this.chartController != null) {
/*     */       try {
/* 385 */         AccessController.doPrivileged(new PrivilegedExceptionAction(indicators) {
/*     */           public Object run() throws Exception {
/* 387 */             if (IChartWrapper.this.chartController != null) {
/* 388 */               for (IndicatorWrapper indicatorWrapper : IChartWrapper.this.chartController.getIndicators()) {
/* 389 */                 this.val$indicators.add(indicatorWrapper.getIndicator());
/*     */               }
/*     */             }
/* 392 */             return null;
/*     */           } } );
/*     */       } catch (PrivilegedActionException ex) {
/* 396 */         LOGGER.error("Error while getting indicators", ex);
/*     */       }
/*     */     }
/* 399 */     return indicators;
/*     */   }
/*     */ 
/*     */   public void removeIndicator(IIndicator indicator)
/*     */   {
/* 404 */     if ((this.chartController != null) && 
/* 405 */       (this.chartController != null))
/*     */       try {
/* 407 */         AccessController.doPrivileged(new PrivilegedExceptionAction(indicator) {
/*     */           public Object run() throws Exception {
/* 409 */             for (IndicatorWrapper indicatorWrapper : IChartWrapper.this.chartController.getIndicators()) {
/* 410 */               if (indicatorWrapper.getIndicator() == this.val$indicator) {
/* 411 */                 IChartWrapper.this.chartController.deleteIndicator(indicatorWrapper);
/* 412 */                 break;
/*     */               }
/*     */             }
/* 415 */             return null;
/*     */           } } );
/*     */       } catch (PrivilegedActionException ex) {
/* 419 */         LOGGER.error("Error while removing indicator : " + indicator, ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 427 */     return this.dataManager.getDataType();
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange()
/*     */   {
/* 432 */     return this.dataManager.getPriceRange();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 437 */     return this.instrument.toString() + ", " + getSelectedPeriod();
/*     */   }
/*     */ 
/*     */   public void setOrderLineVisible(String orderId, boolean visible) {
/* 441 */     this.chartController.setOrderLineVisible(orderId, visible);
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount()
/*     */   {
/* 446 */     return this.dataManager.getReversalAmount();
/*     */   }
/*     */ 
/*     */   public void repaint()
/*     */   {
/* 451 */     if (SwingUtilities.isEventDispatchThread())
/* 452 */       this.guiRefresher.refreshAllContent();
/*     */     else
/* 454 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 456 */           IChartWrapper.this.guiRefresher.refreshAllContent();
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public IChartObjectFactory getChartObjectFactory()
/*     */   {
/* 464 */     return new ChartObjectFactory();
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToMainChart(T object)
/*     */   {
/* 469 */     if (object == null) {
/* 470 */       return;
/*     */     }
/* 472 */     validateMainChartAddition(object);
/* 473 */     this.mainDrawingsManager.addChartObject(object);
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToSubChart(Integer subChartId, int indicatorId, T object)
/*     */   {
/* 478 */     if (object == null) {
/* 479 */       return;
/*     */     }
/* 481 */     validateSubChartAddition(subChartId, indicatorId, object);
/* 482 */     this.chartController.addDrawingToIndicator(subChartId, indicatorId, object);
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToMainChartUnlocked(T object)
/*     */   {
/* 487 */     validateMainChartAddition(object);
/* 488 */     this.mainDrawingsManager.addChartObject(object);
/*     */   }
/*     */ 
/*     */   public Boolean isChartObjectUnlocked(IChartObject chartObject)
/*     */   {
/* 496 */     return Boolean.FALSE;
/*     */   }
/*     */ 
/*     */   protected DDSChartsController getDDSChartsController() {
/* 500 */     DDSChartsController mainChartsController = DDSChartsControllerImpl.getInstance();
/* 501 */     return mainChartsController;
/*     */   }
/*     */ 
/*     */   private <T extends IChartObject> void validateMainChartAddition(T object) {
/* 505 */     validateUniueKey(object);
/* 506 */     validateAdditionOnSingleParent(object);
/*     */   }
/*     */ 
/*     */   private <T extends IChartObject> void validateUniueKey(T object) {
/* 510 */     if (this.mainDrawingsManager.getDrawingByKey(object.getKey()) != null)
/* 511 */       throw new IllegalArgumentException("Chart object with key '" + object.getKey() + "' already exists! Please, set other one!");
/*     */   }
/*     */ 
/*     */   private <T extends IChartObject> void validateAdditionOnSingleParent(T object)
/*     */   {
/* 516 */     DDSChartsController mainChartsController = getDDSChartsController();
/* 517 */     Set chartControllerIdies = mainChartsController.getChartControllerIdies();
/* 518 */     for (Integer chartId : chartControllerIdies) {
/* 519 */       IChartObject chartObjectForKey = mainChartsController.getDrawingByKey(chartId.intValue(), object.getKey());
/* 520 */       if (chartObjectForKey != null)
/* 521 */         throw new IllegalArgumentException("Object with key = " + object.getKey() + " is already added on this or other chart! Please, remove it first or create another one.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T> void validateSubChartAddition(Integer subChartId, int indicatorId, T object)
/*     */   {
/* 527 */     DDSChartsController mainChartsController = getDDSChartsController();
/* 528 */     List allChartObjects = new ArrayList();
/* 529 */     Set chartControllerIdies = mainChartsController.getChartControllerIdies();
/*     */ 
/* 531 */     for (Integer chartId : chartControllerIdies) {
/* 532 */       List indicators = mainChartsController.getIndicators(chartId);
/* 533 */       if (indicators != null) {
/* 534 */         for (IndicatorWrapper indicatorWrapper : indicators) {
/* 535 */           Integer subPanelId = indicatorWrapper.getSubPanelId();
/* 536 */           int id = indicatorWrapper.getId();
/* 537 */           List subChartDrawings = mainChartsController.getSubChartDrawings(chartId, subPanelId.intValue(), id);
/* 538 */           allChartObjects.addAll(subChartDrawings);
/*     */         }
/*     */       }
/*     */ 
/* 542 */       for (IChartObject chartObject : allChartObjects)
/* 543 */         if (chartObject == object)
/* 544 */           throw new IllegalArgumentException("This object reference is already present in sub chart with id [" + subChartId + "] and indicator id = [" + indicatorId + "], please create new instance of this object.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize()
/*     */   {
/* 553 */     return this.dataManager.getTickBarSize();
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getStrategyChartObjects()
/*     */   {
/* 562 */     return getAll();
/*     */   }
/*     */ 
/*     */   protected String getOwnerId(IChartObject chartObject)
/*     */   {
/* 567 */     if (chartObject == null) {
/* 568 */       return null;
/*     */     }
/*     */ 
/* 571 */     return ((ChartObject)chartObject).getOwnerId();
/*     */   }
/*     */ 
/*     */   protected void setOwnerId(IChartObject chartObject, String ownerId)
/*     */   {
/* 577 */     if (chartObject != null)
/* 578 */       ((ChartObject)chartObject).setOwnerId(ownerId);
/*     */   }
/*     */ 
/*     */   public List<IChartObject> remove(List<IChartObject> chartObjects)
/*     */   {
/* 584 */     List keys = new ArrayList();
/* 585 */     for (IChartObject object : chartObjects) {
/* 586 */       keys.add(object.getKey());
/*     */     }
/* 588 */     return doRemove(keys);
/*     */   }
/*     */ 
/*     */   public Color getThemeColor(ITheme.ChartElement element) {
/* 592 */     return this.chartController.getTheme().getColor(element);
/*     */   }
/*     */ 
/*     */   public BufferedImage getImage()
/*     */   {
/* 600 */     return this.chartController.getWorkspaceImage();
/*     */   }
/*     */ 
/*     */   public void setVerticalAxisScale(double minPriceValue, double maxPriceValue)
/*     */   {
/* 605 */     this.chartController.setVerticalChartMovementEnabled(true);
/* 606 */     this.chartController.setVerticalAxisScale(minPriceValue, maxPriceValue);
/*     */   }
/*     */ 
/*     */   public void setVerticalAutoscale(boolean autoscale)
/*     */   {
/* 611 */     this.chartController.setVerticalChartMovementEnabled(!autoscale);
/*     */   }
/*     */ 
/*     */   public void setDataPresentationType(DataType.DataPresentationType presentationType)
/*     */   {
/* 617 */     DataType dataType = getDataType();
/* 618 */     if (!dataType.isPresentationTypeSupported(presentationType)) {
/* 619 */       throw new IllegalArgumentException(presentationType.name() + " is not supported by current DataType");
/*     */     }
/*     */ 
/* 622 */     switch (6.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) { case 1:
/* 623 */       this.chartController.setLineType(presentationType); break;
/*     */     case 2:
/* 624 */       this.chartController.setTickType(presentationType); break;
/*     */     case 3:
/* 625 */       this.chartController.changeTickBarPresentationType(presentationType); break;
/*     */     case 4:
/* 626 */       this.chartController.changePriceRangePresentationType(presentationType); break;
/*     */     case 5:
/* 627 */       this.chartController.changePointAndFigurePresentationType(presentationType); break;
/*     */     case 6:
/* 628 */       this.chartController.changeRenkoPresentationType(presentationType); break;
/*     */     default:
/* 630 */       throw new IllegalStateException("Unsupported DataType");
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IChartWrapper
 * JD-Core Version:    0.6.0
 */