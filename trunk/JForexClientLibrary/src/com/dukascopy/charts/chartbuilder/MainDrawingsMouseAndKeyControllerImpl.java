/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.ChartObjectEvent;
/*     */ import com.dukascopy.api.ChartObjectListener;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.IChartObject.ATTR_DOUBLE;
/*     */ import com.dukascopy.api.IChartObject.ATTR_LONG;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import com.dukascopy.charts.drawings.AbstractWidgetChartObject;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.IMainDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.NewDrawingsCoordinator;
/*     */ import com.dukascopy.charts.drawings.PopupManagerForDrawings;
/*     */ import com.dukascopy.charts.indicators.IndicatorsManagerImpl;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.math.dataprovider.IIndicatorsContainer;
/*     */ import com.dukascopy.charts.orders.OrdersManagerImpl;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.geom.Ellipse2D.Float;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPopupMenu;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class MainDrawingsMouseAndKeyControllerImpl
/*     */   implements MainDrawingsMouseAndKeyController
/*     */ {
/*  36 */   private static final Logger LOGGER = LoggerFactory.getLogger(MainDrawingsMouseAndKeyControllerImpl.class);
/*     */   final GuiRefresher guiRefresher;
/*     */   final IMainDrawingsManager mainDrawingsManager;
/*     */   final NewDrawingsCoordinator newDrawingsCoordinator;
/*     */   final IIndicatorsContainer indicatorsContainer;
/*     */   final IndicatorsManagerImpl indicatorsManagerImpl;
/*     */   final PopupManagerForDrawings popupManagerForDrawings;
/*     */   final GeometryCalculator geometryCalculator;
/*     */   final PathHelper pathHelper;
/*     */   private OrdersManagerImpl ordersManagerImpl;
/*  48 */   boolean allowDraggingContent = false;
/*     */ 
/*  50 */   private final float STICKING_SENSETIVITY = 25.0F;
/*     */   long time1BeforeEditing;
/*     */   long time2BeforeEditing;
/*     */   long time3BeforeEditing;
/*     */   double price1BeforeEditing;
/*     */   double price2BeforeEditing;
/*     */   double price3BeforeEditing;
/*     */   float xPosBeforeEditing;
/*     */   float yPosBeforeEditing;
/*     */ 
/*     */   public MainDrawingsMouseAndKeyControllerImpl(GuiRefresher guiRefresher, IMainDrawingsManager mainDrawingsManager, NewDrawingsCoordinator newDrawingsCoordinator, IIndicatorsContainer indicatorsContainer, IndicatorsManagerImpl indicatorsManagerImpl, PopupManagerForDrawings popupManagerForDrawings, GeometryCalculator geometryCalculator, PathHelper pathHelper, OrdersManagerImpl ordersManagerImpl)
/*     */   {
/*  64 */     this.guiRefresher = guiRefresher;
/*  65 */     this.mainDrawingsManager = mainDrawingsManager;
/*  66 */     this.newDrawingsCoordinator = newDrawingsCoordinator;
/*  67 */     this.indicatorsContainer = indicatorsContainer;
/*  68 */     this.indicatorsManagerImpl = indicatorsManagerImpl;
/*  69 */     this.popupManagerForDrawings = popupManagerForDrawings;
/*  70 */     this.geometryCalculator = geometryCalculator;
/*  71 */     this.pathHelper = pathHelper;
/*  72 */     this.ordersManagerImpl = ordersManagerImpl;
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e) {
/*  76 */     if (this.newDrawingsCoordinator.isDrawingStatus()) {
/*  77 */       ChartObject chartObject = this.newDrawingsCoordinator.getNewDrawing();
/*  78 */       if (((chartObject == null) || (!chartObject.isGlobal())) && (
/*  79 */         (!newChartObjectOwnerExists()) || (!doWeOwnNewChartObject(e.getSource(), false))))
/*  80 */         this.mainDrawingsManager.drawingNew(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e)
/*     */   {
/*  87 */     if (this.newDrawingsCoordinator.isDrawingStatus()) {
/*  88 */       if (!doWeOwnNewChartObject(e.getSource(), false)) {
/*  89 */         return;
/*     */       }
/*  91 */       this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte mouseClicked(MouseEvent e) {
/*  96 */     byte returnValue = -1;
/*  97 */     if (e.isConsumed()) {
/*  98 */       return returnValue;
/*     */     }
/*     */ 
/* 101 */     boolean shouldBeConsumed = false;
/* 102 */     if (isDrawingEditing()) {
/* 103 */       if (e.getClickCount() >= 2) {
/* 104 */         if (this.mainDrawingsManager.intersectsDrawingToBeEdited(e.getPoint())) {
/* 105 */           this.mainDrawingsManager.unselectDrawingToBeEditedAndExitEditingMode();
/* 106 */           returnValue = 0;
/* 107 */         } else if (this.mainDrawingsManager.intersectsDrawing(e.getPoint())) {
/* 108 */           this.mainDrawingsManager.unselectDrawingToBeEdited();
/* 109 */           this.mainDrawingsManager.selectDrawingToBeEdited(e.getPoint());
/* 110 */           saveEditedObjectParameters();
/* 111 */           returnValue = 1;
/*     */         } else {
/* 113 */           this.mainDrawingsManager.unselectDrawingToBeEditedAndExitEditingMode();
/* 114 */           this.indicatorsManagerImpl.tryToSelectIndicator(e.getPoint());
/* 115 */           returnValue = -1;
/*     */         }
/* 117 */         shouldBeConsumed = true;
/*     */       }
/*     */     }
/* 120 */     else if (e.getClickCount() >= 2)
/*     */     {
/* 124 */       this.newDrawingsCoordinator.unselectDrawingToBeEditedAndExitEditingMode();
/* 125 */       this.ordersManagerImpl.unselectSeletedOrders();
/*     */ 
/* 127 */       shouldBeConsumed = this.mainDrawingsManager.selectDrawingToBeEditedAndStartEditingDrawing(e.getPoint());
/*     */ 
/* 129 */       if (shouldBeConsumed) {
/* 130 */         if (this.indicatorsManagerImpl.isSomeIndicatorSelected()) {
/* 131 */           this.indicatorsManagerImpl.unseletSelectedIndicator();
/*     */         }
/* 133 */         returnValue = 1;
/* 134 */         shouldBeConsumed = true;
/*     */       }
/*     */       else {
/* 137 */         boolean indicatorSelected = this.indicatorsManagerImpl.tryToSelectIndicator(e.getPoint());
/* 138 */         if (indicatorSelected) {
/* 139 */           e.getComponent().repaint();
/* 140 */           shouldBeConsumed = true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 147 */     if (shouldBeConsumed) {
/* 148 */       e.consume();
/* 149 */       this.guiRefresher.refreshSubContents();
/*     */     }
/*     */ 
/* 152 */     return returnValue;
/*     */   }
/*     */ 
/*     */   private boolean newChartObjectOwnerExists() {
/* 156 */     return this.newDrawingsCoordinator.getNewChartObjectCurrentOwner() != null;
/*     */   }
/*     */ 
/*     */   private boolean doWeOwnNewChartObject(Object owner, boolean canSetOwner) {
/* 160 */     if ((newChartObjectOwnerExists()) && (this.newDrawingsCoordinator.getNewChartObjectCurrentOwner() != owner)) {
/* 161 */       return false;
/*     */     }
/*     */ 
/* 164 */     if (canSetOwner) {
/* 165 */       this.newDrawingsCoordinator.setNewChartObjectCurrentOwner(owner);
/*     */     }
/* 167 */     return true;
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/* 174 */     if (e.isConsumed()) {
/* 175 */       return;
/*     */     }
/*     */ 
/* 178 */     if (this.newDrawingsCoordinator.hasNewDrawing())
/*     */     {
/* 182 */       if (this.newDrawingsCoordinator.hasNewGlobalDrawing()) {
/* 183 */         this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */       }
/*     */ 
/* 186 */       if (!this.newDrawingsCoordinator.isDrawingStatus()) {
/* 187 */         this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/* 188 */         this.newDrawingsCoordinator.setStatusIsDrawing();
/*     */       }
/*     */     }
/*     */ 
/* 192 */     boolean shouldBeConsumed = false;
/* 193 */     if (isDrawingNew()) {
/* 194 */       if (!doWeOwnNewChartObject(e.getSource(), true)) {
/* 195 */         return;
/*     */       }
/* 197 */       ChartObject chartObject = this.newDrawingsCoordinator.getNewDrawing();
/* 198 */       if (e.getButton() == 1) {
/* 199 */         Point point = moveMousePointerToHighLow(e);
/* 200 */         boolean finished = this.mainDrawingsManager.addNewPointToNewDrawing(chartObject, point);
/* 201 */         if (finished) {
/* 202 */           this.newDrawingsCoordinator.resetNewDrawing();
/*     */         }
/* 204 */         shouldBeConsumed = true;
/* 205 */       } else if (e.getButton() == 2) {
/* 206 */         this.mainDrawingsManager.finishDrawingNewDrawing(chartObject);
/* 207 */         this.newDrawingsCoordinator.resetNewDrawing();
/* 208 */         shouldBeConsumed = true;
/*     */       }
/* 210 */       if ((shouldBeConsumed) && (chartObject.isGlobal())) {
/* 211 */         this.guiRefresher.refreshSubContents();
/*     */       }
/*     */     }
/* 214 */     else if (isDrawingEditing()) {
/* 215 */       saveEditedObjectParameters();
/* 216 */       processDrawingEditing(e);
/* 217 */       shouldBeConsumed = true;
/*     */     }
/* 219 */     else if (e.isPopupTrigger())
/*     */     {
/*     */       IndicatorWrapper indicatorToBeEdited;
/* 221 */       if (this.indicatorsManagerImpl.isSomeIndicatorHighlighted()) {
/* 222 */         indicatorToBeEdited = this.indicatorsManagerImpl.getHighlightedIndicator();
/*     */       }
/*     */       else
/*     */       {
/*     */         IndicatorWrapper indicatorToBeEdited;
/* 223 */         if (this.indicatorsManagerImpl.isSomeIndicatorSelected())
/* 224 */           indicatorToBeEdited = this.indicatorsManagerImpl.getSelectedIndicator();
/*     */         else
/* 226 */           return;
/*     */       }
/*     */       IndicatorWrapper indicatorToBeEdited;
/* 228 */       this.popupManagerForDrawings.triggerPopupDialogForIndicators(-1, indicatorToBeEdited, e);
/* 229 */       shouldBeConsumed = true;
/*     */     }
/*     */ 
/* 233 */     if (shouldBeConsumed)
/* 234 */       e.consume();
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/* 240 */     if (e.isConsumed()) {
/* 241 */       return;
/*     */     }
/*     */ 
/* 244 */     boolean shouldBeConsumed = false;
/*     */ 
/* 246 */     if (isDrawingEditing()) {
/* 247 */       if (e.isPopupTrigger()) {
/* 248 */         JPopupMenu jPopupMenu = this.popupManagerForDrawings.createPopup(this.mainDrawingsManager.getEditedChartObject(), this.guiRefresher, e.getLocationOnScreen());
/* 249 */         if (jPopupMenu != null)
/* 250 */           jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
/*     */       }
/*     */       else {
/* 253 */         adjustDrawingPosition(e.getComponent(), false);
/*     */       }
/* 255 */       shouldBeConsumed = true;
/*     */     }
/* 257 */     else if (e.isPopupTrigger())
/*     */     {
/*     */       IndicatorWrapper indicatorToBeEdited;
/* 259 */       if (this.indicatorsManagerImpl.isSomeIndicatorHighlighted()) {
/* 260 */         indicatorToBeEdited = this.indicatorsManagerImpl.getHighlightedIndicator();
/*     */       }
/*     */       else
/*     */       {
/*     */         IndicatorWrapper indicatorToBeEdited;
/* 261 */         if (this.indicatorsManagerImpl.isSomeIndicatorSelected())
/* 262 */           indicatorToBeEdited = this.indicatorsManagerImpl.getSelectedIndicator();
/*     */         else
/* 264 */           return;
/*     */       }
/*     */       IndicatorWrapper indicatorToBeEdited;
/* 266 */       this.popupManagerForDrawings.triggerPopupDialogForIndicators(-1, indicatorToBeEdited, e);
/* 267 */       shouldBeConsumed = true;
/*     */     }
/*     */ 
/* 271 */     if (shouldBeConsumed)
/* 272 */       e.consume();
/*     */   }
/*     */ 
/*     */   public byte mouseMoved(MouseEvent event)
/*     */   {
/* 279 */     if (event.isConsumed()) {
/* 280 */       return -1;
/*     */     }
/* 282 */     boolean shouldBeConsumed = false;
/*     */ 
/* 284 */     if (this.newDrawingsCoordinator.hasNewDrawing())
/*     */     {
/* 288 */       if (this.newDrawingsCoordinator.hasNewGlobalDrawing()) {
/* 289 */         this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */       }
/*     */ 
/* 292 */       if (!this.newDrawingsCoordinator.isDrawingStatus()) {
/* 293 */         this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/* 294 */         this.newDrawingsCoordinator.setStatusIsDrawing();
/*     */       }
/*     */     }
/*     */ 
/* 298 */     if (isDrawingNew()) {
/* 299 */       if (!doWeOwnNewChartObject(event.getSource(), false)) {
/* 300 */         return -1;
/*     */       }
/*     */ 
/* 303 */       ChartObject newChartObject = this.newDrawingsCoordinator.getNewDrawing();
/* 304 */       Point point = event.getPoint();
/* 305 */       if (newChartObject.isSticky()) {
/* 306 */         point = moveMousePointerToHighLow(event);
/*     */       }
/* 308 */       this.mainDrawingsManager.modifyNewDrawing(newChartObject, point, true);
/* 309 */       if (newChartObject.isGlobal()) {
/* 310 */         this.guiRefresher.refreshSubContents();
/*     */       }
/* 312 */       shouldBeConsumed = true;
/* 313 */     } else if ((!isDrawingNew()) && (!isDrawingEditing())) {
/* 314 */       boolean isDrawingHighlighted = this.mainDrawingsManager.triggerHighlighting(event.getPoint());
/* 315 */       if (isDrawingHighlighted) {
/* 316 */         String tooltip = this.mainDrawingsManager.getHighlightedChartObject().getTooltip();
/* 317 */         ((JComponent)event.getComponent()).setToolTipText(tooltip);
/*     */ 
/* 319 */         shouldBeConsumed = true;
/*     */       } else {
/* 321 */         boolean someHighlightingActionPerformed = this.indicatorsManagerImpl.triggerHighlighting(event.getPoint());
/* 322 */         if (someHighlightingActionPerformed) {
/* 323 */           if (this.indicatorsManagerImpl.getHighlightedIndicator() != null) {
/* 324 */             LevelInfo level = this.indicatorsManagerImpl.getHighlightedLevel();
/* 325 */             if (level != null) {
/* 326 */               String tooltip = (level.getLabel() == null) || (level.getLabel().isEmpty()) ? "Level: " + (int)level.getValue() : level.getLabel();
/*     */ 
/* 328 */               ((JComponent)event.getComponent()).setToolTipText(tooltip);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 334 */           repaint(event.getComponent());
/* 335 */           shouldBeConsumed = true;
/*     */         }
/* 337 */         else if ((!this.indicatorsManagerImpl.isSomeIndicatorHighlighted()) && (this.mainDrawingsManager.getHighlightedChartObject() == null)) {
/* 338 */           ((JComponent)event.getComponent()).setToolTipText(null);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 344 */     if (shouldBeConsumed) {
/* 345 */       event.consume();
/*     */     }
/*     */ 
/* 348 */     if (this.mainDrawingsManager.getHighlightedChartObject() != null)
/* 349 */       return 0;
/* 350 */     if (this.mainDrawingsManager.intersectsDrawingToBeEdited(event.getPoint())) {
/* 351 */       return 1;
/*     */     }
/* 353 */     return -1;
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e)
/*     */   {
/* 358 */     if (e.isConsumed()) {
/* 359 */       return;
/*     */     }
/*     */ 
/* 362 */     if (isDrawingNew()) {
/* 363 */       if (!doWeOwnNewChartObject(e.getSource(), false)) {
/* 364 */         return;
/*     */       }
/*     */ 
/* 367 */       ChartObject newChartObject = this.newDrawingsCoordinator.getNewDrawing();
/* 368 */       Point point = e.getPoint();
/* 369 */       if (newChartObject.isSticky()) {
/* 370 */         point = moveMousePointerToHighLow(e);
/*     */       }
/* 372 */       this.mainDrawingsManager.modifyNewDrawing(newChartObject, point, true);
/* 373 */       repaint(e.getComponent());
/* 374 */       e.consume();
/*     */     }
/* 376 */     else if ((isDrawingEditing()) && (!this.allowDraggingContent)) {
/* 377 */       ChartObject editedChartObject = this.mainDrawingsManager.getEditedChartObject();
/* 378 */       Point point = e.getPoint();
/* 379 */       if ((editedChartObject.isSticky()) && (editedChartObject.isHandlerSelected())) {
/* 380 */         point = moveMousePointerToHighLow(e);
/*     */       }
/* 382 */       this.mainDrawingsManager.modifyEditingDrawing(point);
/* 383 */       adjustDrawingPosition(e.getComponent(), true);
/* 384 */       repaint(e.getComponent());
/* 385 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseWheelMoved(MouseWheelEvent e) {
/* 390 */     if (e.isConsumed()) {
/* 391 */       return;
/*     */     }
/*     */ 
/* 394 */     if (isDrawingEditing()) {
/* 395 */       processDrawingEditing(e);
/* 396 */       adjustDrawingPosition(e.getComponent(), false);
/* 397 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/* 402 */     if (e.isConsumed()) {
/* 403 */       return;
/*     */     }
/*     */ 
/* 406 */     boolean shouldBeConsumed = false;
/*     */ 
/* 408 */     int keyCode = e.getKeyCode();
/* 409 */     if ((isDrawingNew()) && (!isDrawingEditing())) {
/* 410 */       processDrawingCreation(keyCode);
/* 411 */       shouldBeConsumed = true;
/* 412 */     } else if ((!isDrawingNew()) && (isDrawingEditing())) {
/* 413 */       processDrawingEditing(e, keyCode);
/* 414 */       shouldBeConsumed = true;
/* 415 */     } else if ((!isDrawingNew()) && (!isDrawingEditing())) {
/* 416 */       if (10 == keyCode) {
/* 417 */         if (this.mainDrawingsManager.isSomeDrawingHighlighted()) {
/* 418 */           ChartObject chartObject = this.mainDrawingsManager.getHighlightedChartObject();
/* 419 */           this.mainDrawingsManager.selectHighlitedDrawing();
/* 420 */           if (chartObject.isGlobal()) {
/* 421 */             this.guiRefresher.refreshSubContents();
/*     */           }
/* 423 */           shouldBeConsumed = true;
/* 424 */         } else if (this.indicatorsManagerImpl.isSomeIndicatorHighlighted()) {
/* 425 */           this.indicatorsManagerImpl.selectHighlightedIndicator();
/*     */         }
/*     */       }
/* 428 */       else if ((127 == keyCode) && (this.indicatorsManagerImpl.isSomeIndicatorSelected())) {
/* 429 */         IndicatorWrapper selectedIndicator = this.indicatorsManagerImpl.getSelectedIndicator();
/* 430 */         deleteIndicator(selectedIndicator);
/* 431 */         this.indicatorsManagerImpl.unseletSelectedIndicator();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 436 */     if (shouldBeConsumed)
/* 437 */       e.consume();
/*     */   }
/*     */ 
/*     */   void deleteIndicator(IndicatorWrapper selectedIndicator)
/*     */   {
/*     */     try {
/* 443 */       this.indicatorsContainer.deleteIndicator(selectedIndicator);
/*     */     } catch (Exception exc) {
/* 445 */       LOGGER.error(exc.getMessage(), exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   Point moveMousePointerToHighLow(MouseEvent event)
/*     */   {
/* 451 */     return moveTo(event, this.pathHelper.getMiddlePoints(), this.pathHelper.getHighs(), this.pathHelper.getLows(), this.pathHelper.getOpenPoints(), this.pathHelper.getClosePoints());
/*     */   }
/*     */ 
/*     */   private Point moveTo(MouseEvent event, float[] middles, float[] highs, float[] lows, float[] opens, float[] closes)
/*     */   {
/* 469 */     float width = this.geometryCalculator.getDataUnitWidth();
/*     */ 
/* 471 */     for (int i = 0; i < middles.length; i++) {
/* 472 */       float middle = middles[i];
/*     */ 
/* 474 */       if ((middle == -1.0F) || (Math.abs(middle - event.getX()) > width))
/*     */       {
/*     */         continue;
/*     */       }
/* 478 */       float highY = highs[i];
/* 479 */       float lowY = lows[i];
/* 480 */       float openY = opens[i];
/* 481 */       float closeY = closes[i];
/* 482 */       float topY = openY > closeY ? openY : closeY;
/* 483 */       float bottomY = openY < closeY ? openY : closeY;
/*     */ 
/* 485 */       float highTopDiff = Math.abs(topY - highY) / 2.0F;
/* 486 */       float topBottomDiff = Math.abs(topY - bottomY) / 2.0F;
/* 487 */       float bottomLowDiff = Math.abs(lowY - bottomY) / 2.0F;
/*     */ 
/* 489 */       float highAreaHeight = getHeight(width, highY + highTopDiff, highY - highTopDiff);
/* 490 */       float topAreaHeight = getHeight(width, topY + highTopDiff, topY - topBottomDiff);
/* 491 */       float bottomAreaHeight = getHeight(width, bottomY + topBottomDiff, bottomY - bottomLowDiff);
/* 492 */       float lowAreaHeight = getHeight(width, lowY + bottomLowDiff, lowY - bottomLowDiff);
/*     */ 
/* 494 */       Ellipse2D.Float highArea = createArea(middle, highY, width, highAreaHeight);
/* 495 */       Ellipse2D.Float topArea = createArea(middle, topY, width, topAreaHeight);
/* 496 */       Ellipse2D.Float bottomArea = createArea(middle, bottomY, width, bottomAreaHeight);
/* 497 */       Ellipse2D.Float lowArea = createArea(middle, lowY, width, lowAreaHeight);
/*     */ 
/* 499 */       if (highArea.contains(event.getPoint()))
/* 500 */         return getPoint(highArea, event);
/* 501 */       if (topArea.contains(event.getPoint()))
/* 502 */         return getPoint(topArea, event);
/* 503 */       if (bottomArea.contains(event.getPoint()))
/* 504 */         return getPoint(bottomArea, event);
/* 505 */       if (lowArea.contains(event.getPoint())) {
/* 506 */         return getPoint(lowArea, event);
/*     */       }
/*     */     }
/*     */ 
/* 510 */     return event.getPoint();
/*     */   }
/*     */ 
/*     */   private Point getPoint(Ellipse2D.Float area, MouseEvent event)
/*     */   {
/* 517 */     int y = (int)(area.getY() + area.getHeight() / 2.0D);
/* 518 */     return new Point(event.getX(), y);
/*     */   }
/*     */ 
/*     */   private Ellipse2D.Float createArea(float x, float y, float width, float height) {
/* 522 */     Ellipse2D.Float area = new Ellipse2D.Float(x - width / 2.0F, y - height / 2.0F, width, height);
/* 523 */     return area;
/*     */   }
/*     */ 
/*     */   private float getHeight(float dataUnitWidth, float x1, float x2) {
/* 527 */     float height = dataUnitWidth;
/* 528 */     if (height < 25.0F) {
/* 529 */       height = 25.0F;
/*     */     }
/* 531 */     float diff = Math.abs(x1 - x2);
/*     */ 
/* 533 */     if (diff < height) {
/* 534 */       height = diff;
/*     */     }
/* 536 */     return height;
/*     */   }
/*     */ 
/*     */   void processDrawingEditing(KeyEvent e, int pressedKeyCode) {
/* 540 */     if ((27 == pressedKeyCode) || (10 == pressedKeyCode)) {
/* 541 */       this.mainDrawingsManager.unselectDrawingToBeEditedAndExitEditingMode();
/* 542 */       this.guiRefresher.refreshSubContents();
/* 543 */     } else if (127 == pressedKeyCode) {
/* 544 */       this.mainDrawingsManager.deleteSelectedDrawing();
/* 545 */       this.guiRefresher.refreshSubContents();
/* 546 */     } else if (37 == pressedKeyCode) {
/* 547 */       saveEditedObjectParameters();
/* 548 */       this.mainDrawingsManager.moveEditedDrawingLeft();
/* 549 */       adjustDrawingPosition(e.getComponent(), false);
/* 550 */       repaint(e.getComponent());
/* 551 */     } else if (39 == pressedKeyCode) {
/* 552 */       saveEditedObjectParameters();
/* 553 */       this.mainDrawingsManager.moveEditedDrawingRight();
/* 554 */       adjustDrawingPosition(e.getComponent(), false);
/* 555 */       repaint(e.getComponent());
/* 556 */     } else if (40 == pressedKeyCode) {
/* 557 */       saveEditedObjectParameters();
/* 558 */       this.mainDrawingsManager.moveEditedDrawingDown();
/* 559 */       adjustDrawingPosition(e.getComponent(), false);
/* 560 */       repaint(e.getComponent());
/* 561 */     } else if (38 == pressedKeyCode) {
/* 562 */       saveEditedObjectParameters();
/* 563 */       this.mainDrawingsManager.moveEditedDrawingUp();
/* 564 */       adjustDrawingPosition(e.getComponent(), false);
/* 565 */       repaint(e.getComponent());
/*     */     }
/*     */   }
/*     */ 
/*     */   void processDrawingCreation(int pressedKeyCode) {
/* 570 */     if ((10 == pressedKeyCode) || (27 == pressedKeyCode)) {
/* 571 */       ChartObject chartObject = this.newDrawingsCoordinator.getNewDrawing();
/* 572 */       this.mainDrawingsManager.finishDrawingNewDrawing(chartObject);
/* 573 */       this.newDrawingsCoordinator.resetNewDrawing();
/* 574 */       if (chartObject.isGlobal())
/* 575 */         this.guiRefresher.refreshSubContents();
/*     */     }
/*     */   }
/*     */ 
/*     */   void processDrawingEditing(MouseEvent e)
/*     */   {
/* 581 */     if (e.isPopupTrigger()) {
/* 582 */       JPopupMenu jPopupMenu = this.popupManagerForDrawings.createPopup(this.mainDrawingsManager.getEditedChartObject(), this.guiRefresher, e.getLocationOnScreen());
/* 583 */       if (jPopupMenu != null) {
/* 584 */         jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
/*     */       }
/*     */     }
/* 587 */     else if (this.mainDrawingsManager.intersectsDrawingToBeEdited(e.getPoint())) {
/* 588 */       this.mainDrawingsManager.updatePrevPointAndSelectedHandler(e.getPoint());
/* 589 */       repaint(e.getComponent());
/* 590 */       this.allowDraggingContent = false;
/*     */     } else {
/* 592 */       this.allowDraggingContent = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   void processDrawingEditing(MouseWheelEvent e)
/*     */   {
/* 598 */     if (e.getWheelRotation() < 0)
/* 599 */       this.mainDrawingsManager.mouseWheelUp();
/* 600 */     else if (e.getWheelRotation() > 0) {
/* 601 */       this.mainDrawingsManager.mouseWheelDown();
/*     */     }
/* 603 */     repaint(e.getComponent());
/*     */   }
/*     */ 
/*     */   private void repaint(Component component) {
/* 607 */     component.repaint();
/* 608 */     if ((this.mainDrawingsManager.isEditingGlobalDrawing()) || (this.newDrawingsCoordinator.hasNewGlobalDrawing()))
/* 609 */       this.guiRefresher.refreshSubContents();
/*     */   }
/*     */ 
/*     */   boolean isDrawingNew()
/*     */   {
/* 614 */     return this.newDrawingsCoordinator.hasNewDrawing();
/*     */   }
/*     */ 
/*     */   boolean isDrawingEditing() {
/* 618 */     return this.mainDrawingsManager.isEditingDrawing();
/*     */   }
/*     */ 
/*     */   void saveEditedObjectParameters()
/*     */   {
/* 633 */     IChartObject editedChartObject = this.mainDrawingsManager.getEditedChartObject();
/* 634 */     if (editedChartObject != null)
/* 635 */       if ((editedChartObject instanceof AbstractWidgetChartObject)) {
/* 636 */         this.xPosBeforeEditing = ((AbstractWidgetChartObject)editedChartObject).getPosX();
/* 637 */         this.yPosBeforeEditing = ((AbstractWidgetChartObject)editedChartObject).getPosY();
/*     */       } else {
/* 639 */         this.time1BeforeEditing = editedChartObject.getAttrLong(IChartObject.ATTR_LONG.TIME1);
/* 640 */         this.time2BeforeEditing = editedChartObject.getAttrLong(IChartObject.ATTR_LONG.TIME2);
/* 641 */         this.time3BeforeEditing = editedChartObject.getAttrLong(IChartObject.ATTR_LONG.TIME3);
/* 642 */         this.price1BeforeEditing = editedChartObject.getAttrDouble(IChartObject.ATTR_DOUBLE.PRICE1);
/* 643 */         this.price2BeforeEditing = editedChartObject.getAttrDouble(IChartObject.ATTR_DOUBLE.PRICE2);
/* 644 */         this.price3BeforeEditing = editedChartObject.getAttrDouble(IChartObject.ATTR_DOUBLE.PRICE3);
/*     */       }
/*     */   }
/*     */ 
/*     */   void adjustDrawingPosition(Component component, boolean isAdjusting)
/*     */   {
/* 651 */     ChartObject editedChartObject = this.mainDrawingsManager.getEditedChartObject();
/* 652 */     if (editedChartObject == null) {
/* 653 */       return;
/*     */     }
/*     */ 
/* 656 */     if ((editedChartObject instanceof AbstractWidgetChartObject)) {
/* 657 */       if (!isAdjusting) {
/* 658 */         AbstractWidgetChartObject widget = (AbstractWidgetChartObject)editedChartObject;
/* 659 */         ChartObjectEvent event = new ChartObjectEvent(widget);
/* 660 */         widget.getChartObjectListener().moved(event);
/* 661 */         if (event.isCanceled()) {
/* 662 */           widget.setPosX(this.xPosBeforeEditing);
/* 663 */           widget.setPosY(this.yPosBeforeEditing);
/*     */         }
/*     */       }
/*     */     } else {
/* 667 */       long newTime = editedChartObject.getTime(0);
/* 668 */       double newPrice = editedChartObject.getPrice(0);
/* 669 */       ChartObjectEvent chartObjectEvent = new ChartObjectEvent(editedChartObject, this.time1BeforeEditing, newTime, this.price1BeforeEditing, newPrice, isAdjusting);
/* 670 */       editedChartObject.getChartObjectListener().moved(chartObjectEvent);
/* 671 */       if ((chartObjectEvent.isCanceled()) && (!isAdjusting)) {
/* 672 */         editedChartObject.setAttrLong(IChartObject.ATTR_LONG.TIME1, this.time1BeforeEditing);
/* 673 */         editedChartObject.setAttrLong(IChartObject.ATTR_LONG.TIME2, this.time2BeforeEditing);
/* 674 */         editedChartObject.setAttrLong(IChartObject.ATTR_LONG.TIME3, this.time3BeforeEditing);
/* 675 */         editedChartObject.setAttrDouble(IChartObject.ATTR_DOUBLE.PRICE1, this.price1BeforeEditing);
/* 676 */         editedChartObject.setAttrDouble(IChartObject.ATTR_DOUBLE.PRICE2, this.price2BeforeEditing);
/* 677 */         editedChartObject.setAttrDouble(IChartObject.ATTR_DOUBLE.PRICE3, this.price3BeforeEditing);
/* 678 */       } else if (!isAdjusting) {
/* 679 */         if (newTime != chartObjectEvent.getNewLong()) {
/* 680 */           long timeDiff = chartObjectEvent.getOldLong() - chartObjectEvent.getNewLong();
/* 681 */           editedChartObject.setAttrLong(IChartObject.ATTR_LONG.TIME1, chartObjectEvent.getNewLong());
/* 682 */           editedChartObject.setAttrLong(IChartObject.ATTR_LONG.TIME2, this.time2BeforeEditing - timeDiff);
/* 683 */           editedChartObject.setAttrLong(IChartObject.ATTR_LONG.TIME3, this.time3BeforeEditing - timeDiff);
/*     */         }
/* 685 */         if (newPrice != chartObjectEvent.getNewDouble()) {
/* 686 */           double priceDiff = chartObjectEvent.getOldDouble() - chartObjectEvent.getNewDouble();
/* 687 */           editedChartObject.setAttrDouble(IChartObject.ATTR_DOUBLE.PRICE1, chartObjectEvent.getNewDouble());
/* 688 */           editedChartObject.setAttrDouble(IChartObject.ATTR_DOUBLE.PRICE2, this.price2BeforeEditing - priceDiff);
/* 689 */           editedChartObject.setAttrDouble(IChartObject.ATTR_DOUBLE.PRICE3, this.price3BeforeEditing - priceDiff);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 694 */     component.getParent().repaint();
/* 695 */     if (editedChartObject.isGlobal())
/* 696 */       this.guiRefresher.refreshSubContents();
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MainDrawingsMouseAndKeyControllerImpl
 * JD-Core Version:    0.6.0
 */