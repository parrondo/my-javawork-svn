/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.IDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.IMainDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.NewDrawingsCoordinator;
/*     */ import com.dukascopy.charts.drawings.PopupManagerForDrawings;
/*     */ import com.dukascopy.charts.orders.OrdersManagerImpl;
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import javax.swing.JPopupMenu;
/*     */ 
/*     */ public class SubDrawingsMouseAndKeyControllerImpl
/*     */   implements SubDrawingsMouseAndKeyController
/*     */ {
/*     */   final GuiRefresher guiRefresher;
/*     */   final SubDrawingsManagersContainer subDrawingsManagersContainer;
/*     */   final NewDrawingsCoordinator newDrawingsCoordinator;
/*     */   final PopupManagerForDrawings popupManagerForDrawings;
/*     */   private final OrdersManagerImpl ordersManagerImpl;
/*     */   private final IMainDrawingsManager mainDrawingsManager;
/*  26 */   boolean allowDraggingContent = false;
/*     */ 
/*     */   public SubDrawingsMouseAndKeyControllerImpl(GuiRefresher guiRefresher, SubDrawingsManagersContainer subDrawingsManagersContainer, NewDrawingsCoordinator newDrawingsCoordinator, PopupManagerForDrawings popupManagerForDrawings, OrdersManagerImpl ordersManagerImpl, IMainDrawingsManager mainDrawingsManager)
/*     */   {
/*  37 */     this.guiRefresher = guiRefresher;
/*  38 */     this.subDrawingsManagersContainer = subDrawingsManagersContainer;
/*  39 */     this.newDrawingsCoordinator = newDrawingsCoordinator;
/*  40 */     this.popupManagerForDrawings = popupManagerForDrawings;
/*  41 */     this.ordersManagerImpl = ordersManagerImpl;
/*  42 */     this.mainDrawingsManager = mainDrawingsManager;
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e) {
/*  46 */     if (this.newDrawingsCoordinator.isDrawingStatus()) {
/*  47 */       ChartObject chartObject = this.newDrawingsCoordinator.getNewDrawing();
/*  48 */       if ((chartObject == null) || (!chartObject.isGlobal()))
/*     */       {
/*  51 */         if ((!newChartObjectOwnerExists()) || (!doWeOwnNewChartObject(e.getSource(), false)))
/*     */         {
/*  53 */           this.subDrawingsManagersContainer.drawingNew(null);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e) {
/*  59 */     if (this.newDrawingsCoordinator.isDrawingStatus()) {
/*  60 */       ChartObject chartObject = this.newDrawingsCoordinator.getNewDrawing();
/*  61 */       if ((chartObject != null) && (chartObject.isGlobal()))
/*     */       {
/*  63 */         this.mainDrawingsManager.drawingNew(chartObject);
/*     */       }
/*  65 */       else if (doWeOwnNewChartObject(e.getSource(), false))
/*     */       {
/*  68 */         this.subDrawingsManagersContainer.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte mouseClicked(MouseEvent e) {
/*  74 */     byte returnValue = -1;
/*  75 */     if (e.isConsumed()) {
/*  76 */       return returnValue;
/*     */     }
/*     */ 
/*  79 */     boolean shouldBeConsumed = false;
/*  80 */     if (isDrawingEditing()) {
/*  81 */       if (e.getClickCount() >= 2) {
/*  82 */         if (this.subDrawingsManagersContainer.intersectsDrawingToBeEdited(e.getPoint())) {
/*  83 */           this.subDrawingsManagersContainer.unselectDrawingToBeEditedAndExitEditingMode();
/*  84 */           returnValue = 0;
/*  85 */         } else if (this.subDrawingsManagersContainer.intersectsDrawing(e.getPoint())) {
/*  86 */           this.subDrawingsManagersContainer.unselectDrawingToBeEdited();
/*  87 */           this.subDrawingsManagersContainer.selectDrawingToBeEdited(e.getPoint());
/*  88 */           returnValue = 1;
/*     */         } else {
/*  90 */           this.subDrawingsManagersContainer.unselectDrawingToBeEditedAndExitEditingMode();
/*  91 */           returnValue = -1;
/*     */         }
/*  93 */         shouldBeConsumed = true;
/*     */       }
/*     */     }
/*  96 */     else if (e.getClickCount() >= 2) {
/*  97 */       this.newDrawingsCoordinator.unselectDrawingToBeEditedAndExitEditingMode();
/*  98 */       this.ordersManagerImpl.unselectSeletedOrders();
/*     */ 
/* 101 */       if (this.subDrawingsManagersContainer.selectDrawingToBeEditedAndStartEditingDrawing(e.getPoint())) {
/* 102 */         shouldBeConsumed = true;
/*     */       }
/*     */       else {
/* 105 */         Point point = getGlobalPoint(e);
/* 106 */         if ((this.mainDrawingsManager.intersectsGlobalDrawing(point)) && 
/* 107 */           (this.mainDrawingsManager.selectDrawingToBeEditedAndStartEditingDrawing(point))) {
/* 108 */           shouldBeConsumed = true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 113 */       if (shouldBeConsumed) {
/* 114 */         returnValue = 1;
/* 115 */         shouldBeConsumed = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 120 */     if (shouldBeConsumed) {
/* 121 */       e.consume();
/*     */     }
/*     */ 
/* 124 */     return returnValue;
/*     */   }
/*     */ 
/*     */   private boolean newChartObjectOwnerExists() {
/* 128 */     return this.newDrawingsCoordinator.getNewChartObjectCurrentOwner() != null;
/*     */   }
/*     */ 
/*     */   private boolean doWeOwnNewChartObject(Object owner, boolean canSetOwner) {
/* 132 */     if ((newChartObjectOwnerExists()) && (this.newDrawingsCoordinator.getNewChartObjectCurrentOwner() != owner)) {
/* 133 */       return false;
/*     */     }
/*     */ 
/* 136 */     if (canSetOwner) {
/* 137 */       this.newDrawingsCoordinator.setNewChartObjectCurrentOwner(owner);
/*     */     }
/* 139 */     return true;
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/* 145 */     if (e.isConsumed()) {
/* 146 */       return;
/*     */     }
/*     */ 
/* 149 */     if ((this.newDrawingsCoordinator.hasNewDrawing()) && (!this.newDrawingsCoordinator.isDrawingStatus())) {
/* 150 */       if (this.newDrawingsCoordinator.hasNewGlobalDrawing())
/* 151 */         this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */       else {
/* 153 */         this.subDrawingsManagersContainer.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */       }
/* 155 */       this.newDrawingsCoordinator.setStatusIsDrawing();
/*     */     }
/*     */ 
/* 158 */     boolean shouldBeConsumed = false;
/* 159 */     if (isDrawingNew()) {
/* 160 */       ChartObject newDrawing = this.newDrawingsCoordinator.getNewDrawing();
/* 161 */       if (newDrawing.isGlobal()) {
/* 162 */         if (e.getButton() == 1) {
/* 163 */           boolean finished = this.mainDrawingsManager.addNewPointToNewDrawing(newDrawing, e.getPoint());
/* 164 */           if (finished) {
/* 165 */             this.newDrawingsCoordinator.resetNewDrawing();
/*     */           }
/* 167 */           shouldBeConsumed = true;
/* 168 */         } else if (e.getButton() == 2) {
/* 169 */           this.mainDrawingsManager.finishDrawingNewDrawing(newDrawing);
/* 170 */           this.newDrawingsCoordinator.resetNewDrawing();
/* 171 */           shouldBeConsumed = true;
/*     */         }
/*     */       }
/* 174 */       else if (doWeOwnNewChartObject(e.getSource(), true)) {
/* 175 */         if (e.getButton() == 1) {
/* 176 */           boolean finished = this.subDrawingsManagersContainer.addNewPointToNewDrawing(newDrawing, e.getPoint());
/* 177 */           if (finished) {
/* 178 */             this.newDrawingsCoordinator.resetNewDrawing();
/*     */           }
/* 180 */           shouldBeConsumed = true;
/* 181 */         } else if (e.getButton() == 2) {
/* 182 */           this.subDrawingsManagersContainer.finishDrawingNewDrawing(newDrawing);
/* 183 */           this.newDrawingsCoordinator.resetNewDrawing();
/* 184 */           shouldBeConsumed = true;
/*     */         }
/*     */       }
/*     */     }
/* 188 */     else if (isDrawingEditing()) {
/* 189 */       processDrawingEditing(e, false);
/* 190 */       shouldBeConsumed = true;
/*     */     }
/*     */ 
/* 193 */     if (shouldBeConsumed)
/* 194 */       e.consume();
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/* 200 */     if (e.isConsumed()) {
/* 201 */       return;
/*     */     }
/*     */ 
/* 204 */     if ((e.isPopupTrigger()) && 
/* 205 */       (showEditPopupMenu(e)))
/* 206 */       e.consume();
/*     */   }
/*     */ 
/*     */   public byte mouseMoved(MouseEvent event)
/*     */   {
/* 212 */     if (event.isConsumed()) {
/* 213 */       return -1;
/*     */     }
/*     */ 
/* 216 */     boolean shouldBeConsumed = false;
/*     */ 
/* 219 */     if (this.newDrawingsCoordinator.hasNewDrawing())
/*     */     {
/* 223 */       if (this.newDrawingsCoordinator.hasNewGlobalDrawing()) {
/* 224 */         this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */       }
/*     */ 
/* 227 */       if (!this.newDrawingsCoordinator.isDrawingStatus()) {
/* 228 */         if (this.newDrawingsCoordinator.hasNewGlobalDrawing())
/* 229 */           this.mainDrawingsManager.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */         else {
/* 231 */           this.subDrawingsManagersContainer.drawingNew(this.newDrawingsCoordinator.getNewDrawing());
/*     */         }
/* 233 */         this.newDrawingsCoordinator.setStatusIsDrawing();
/*     */       }
/*     */     }
/*     */ 
/* 237 */     if (isDrawingNew()) {
/* 238 */       if (this.newDrawingsCoordinator.hasNewGlobalDrawing()) {
/* 239 */         Point point = getGlobalPoint(event);
/* 240 */         this.mainDrawingsManager.modifyNewDrawing(this.newDrawingsCoordinator.getNewDrawing(), point, false);
/* 241 */         this.guiRefresher.refreshAllContent();
/* 242 */         shouldBeConsumed = true;
/*     */       }
/* 244 */       else if (doWeOwnNewChartObject(event.getSource(), false)) {
/* 245 */         Point point = event.getPoint();
/* 246 */         this.subDrawingsManagersContainer.modifyNewDrawing(this.newDrawingsCoordinator.getNewDrawing(), point, true);
/* 247 */         shouldBeConsumed = true;
/*     */       } else {
/* 249 */         return -1;
/*     */       }
/*     */     }
/* 252 */     else if ((!isDrawingNew()) && (!isDrawingEditing()) && (!this.mainDrawingsManager.isEditingGlobalDrawing()))
/*     */     {
/* 254 */       boolean isDrawingHighlighted = this.subDrawingsManagersContainer.triggerHighlighting(event.getPoint());
/* 255 */       if (isDrawingHighlighted) {
/* 256 */         shouldBeConsumed = true;
/*     */       } else {
/* 258 */         Point point = getGlobalPoint(event);
/* 259 */         if (this.mainDrawingsManager.triggerGlobalHighlighting(point)) {
/* 260 */           shouldBeConsumed = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 265 */     if (shouldBeConsumed) {
/* 266 */       event.consume();
/*     */     }
/*     */ 
/* 270 */     if (this.mainDrawingsManager.intersectsGlobalDrawingToBeEdited(getGlobalPoint(event)))
/*     */     {
/* 272 */       return 1;
/* 273 */     }if (this.mainDrawingsManager.isEditingGlobalDrawing())
/*     */     {
/* 275 */       return -1;
/* 276 */     }if (this.subDrawingsManagersContainer.getHighlightedChartObject() != null)
/*     */     {
/* 278 */       return 0;
/* 279 */     }if (this.subDrawingsManagersContainer.intersectsDrawingToBeEdited(event.getPoint()))
/*     */     {
/* 281 */       return 1;
/* 282 */     }if (this.mainDrawingsManager.intersectsGlobalDrawing(getGlobalPoint(event)))
/*     */     {
/* 284 */       return 0;
/*     */     }
/* 286 */     return -1;
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e)
/*     */   {
/* 291 */     if (e.isConsumed()) {
/* 292 */       return;
/*     */     }
/*     */ 
/* 295 */     if (isDrawingNew()) {
/* 296 */       if (this.newDrawingsCoordinator.hasNewGlobalDrawing())
/*     */       {
/* 298 */         Point point = getGlobalPoint(e);
/* 299 */         this.mainDrawingsManager.modifyNewDrawing(this.newDrawingsCoordinator.getNewDrawing(), point, false);
/* 300 */         this.guiRefresher.refreshAllContent();
/* 301 */         e.consume();
/*     */       }
/* 303 */       else if (doWeOwnNewChartObject(e.getSource(), false)) {
/* 304 */         Point point = e.getPoint();
/* 305 */         this.subDrawingsManagersContainer.modifyNewDrawing(this.newDrawingsCoordinator.getNewDrawing(), point, false);
/* 306 */         e.getComponent().repaint();
/* 307 */         e.consume();
/*     */       }
/*     */     }
/* 310 */     else if ((isDrawingEditing()) && (!this.allowDraggingContent)) {
/* 311 */       Point point = e.getPoint();
/* 312 */       this.subDrawingsManagersContainer.modifyEditingDrawing(point);
/* 313 */       e.getComponent().repaint();
/* 314 */       e.consume();
/*     */     }
/* 316 */     else if (this.mainDrawingsManager.isEditingGlobalDrawing())
/*     */     {
/* 318 */       this.mainDrawingsManager.modifyEditingDrawing(getGlobalPoint(e));
/* 319 */       this.guiRefresher.refreshAllContent();
/* 320 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseWheelMoved(MouseWheelEvent e) {
/* 325 */     if (e.isConsumed()) {
/* 326 */       return;
/*     */     }
/*     */ 
/* 329 */     if (isDrawingEditing()) {
/* 330 */       processDrawingEditing(e, false);
/* 331 */       e.consume();
/*     */     }
/* 333 */     else if (this.mainDrawingsManager.isEditingGlobalDrawing()) {
/* 334 */       processDrawingEditing(e, true);
/* 335 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   void processDrawingEditing(MouseWheelEvent e, boolean global)
/*     */   {
/*     */     IDrawingsManager manager;
/*     */     IDrawingsManager manager;
/* 341 */     if (global)
/* 342 */       manager = this.mainDrawingsManager;
/*     */     else {
/* 344 */       manager = this.subDrawingsManagersContainer;
/*     */     }
/* 346 */     if (e.getWheelRotation() < 0)
/* 347 */       manager.mouseWheelUp();
/* 348 */     else if (e.getWheelRotation() > 0) {
/* 349 */       manager.mouseWheelDown();
/*     */     }
/* 351 */     if (global)
/* 352 */       this.guiRefresher.refreshAllContent();
/*     */     else
/* 354 */       e.getComponent().repaint();
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e)
/*     */   {
/* 359 */     if (e.isConsumed()) {
/* 360 */       return;
/*     */     }
/* 362 */     boolean shouldBeConsumed = false;
/*     */ 
/* 364 */     int keyCode = e.getKeyCode();
/* 365 */     if ((isDrawingNew()) && (!isDrawingEditing())) {
/* 366 */       processDrawingCreation(keyCode, this.newDrawingsCoordinator.getNewDrawing());
/* 367 */       shouldBeConsumed = true;
/*     */     }
/* 369 */     else if ((!isDrawingNew()) && (isDrawingEditing())) {
/* 370 */       processDrawingEditing(this.subDrawingsManagersContainer, e, keyCode);
/* 371 */       shouldBeConsumed = true;
/*     */     }
/* 373 */     else if ((!isDrawingNew()) && (this.mainDrawingsManager.isEditingGlobalDrawing())) {
/* 374 */       processDrawingEditing(this.mainDrawingsManager, e, keyCode);
/* 375 */       this.guiRefresher.refreshAllContent();
/* 376 */       shouldBeConsumed = true;
/*     */     }
/* 378 */     else if ((!isDrawingNew()) && (!isDrawingEditing()) && 
/* 379 */       (10 == keyCode)) {
/* 380 */       if (this.subDrawingsManagersContainer.isSomeDrawingHighlighted()) {
/* 381 */         this.subDrawingsManagersContainer.selectHighlitedDrawing();
/* 382 */         shouldBeConsumed = true;
/*     */       } else {
/* 384 */         ChartObject object = this.mainDrawingsManager.getHighlightedChartObject();
/* 385 */         if ((object != null) && (object.isGlobal())) {
/* 386 */           this.mainDrawingsManager.selectHighlitedDrawing();
/* 387 */           this.guiRefresher.refreshAllContent();
/* 388 */           shouldBeConsumed = true;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 394 */     if (shouldBeConsumed)
/* 395 */       e.consume();
/*     */   }
/*     */ 
/*     */   boolean isDrawingNew()
/*     */   {
/* 402 */     return this.newDrawingsCoordinator.hasNewDrawing();
/*     */   }
/*     */ 
/*     */   boolean isDrawingEditing() {
/* 406 */     return this.subDrawingsManagersContainer.isEditingDrawing();
/*     */   }
/*     */ 
/*     */   void processDrawingCreation(int pressedKeyCode, ChartObject chartObject)
/*     */   {
/* 411 */     if ((10 == pressedKeyCode) || (27 == pressedKeyCode)) {
/* 412 */       if (chartObject.isGlobal())
/* 413 */         this.mainDrawingsManager.finishDrawingNewDrawing(chartObject);
/*     */       else {
/* 415 */         this.subDrawingsManagersContainer.finishDrawingNewDrawing(chartObject);
/*     */       }
/* 417 */       this.newDrawingsCoordinator.resetNewDrawing();
/*     */     }
/*     */   }
/*     */ 
/*     */   void processDrawingEditing(MouseEvent e, boolean editingGlobal) {
/* 422 */     if (e.isPopupTrigger()) {
/* 423 */       if (showEditPopupMenu(e))
/* 424 */         e.consume();
/*     */     }
/* 426 */     else if (editingGlobal) {
/* 427 */       Point point = getGlobalPoint(e);
/* 428 */       if (this.mainDrawingsManager.intersectsGlobalDrawing(point)) {
/* 429 */         this.mainDrawingsManager.updatePrevPointAndSelectedHandler(point);
/* 430 */         e.getComponent().repaint();
/* 431 */         this.allowDraggingContent = false;
/*     */       } else {
/* 433 */         this.allowDraggingContent = true;
/*     */       }
/*     */ 
/*     */     }
/* 437 */     else if (this.subDrawingsManagersContainer.intersectsDrawingToBeEdited(e.getPoint())) {
/* 438 */       this.subDrawingsManagersContainer.updatePrevPointAndSelectedHandler(e.getPoint());
/* 439 */       e.getComponent().repaint();
/* 440 */       this.allowDraggingContent = false;
/*     */     } else {
/* 442 */       this.allowDraggingContent = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean showEditPopupMenu(MouseEvent e)
/*     */   {
/*     */     JPopupMenu jPopupMenu;
/*     */     JPopupMenu jPopupMenu;
/* 449 */     if (isDrawingEditing()) {
/* 450 */       jPopupMenu = this.popupManagerForDrawings.createPopup(this.subDrawingsManagersContainer.getEditedChartObject(), this.guiRefresher, e.getLocationOnScreen());
/*     */     }
/*     */     else
/*     */     {
/*     */       JPopupMenu jPopupMenu;
/* 452 */       if (this.mainDrawingsManager.isEditingGlobalDrawing()) {
/* 453 */         jPopupMenu = this.popupManagerForDrawings.createPopup(this.mainDrawingsManager.getEditedChartObject(), this.guiRefresher, e.getLocationOnScreen());
/*     */       }
/*     */       else {
/* 456 */         jPopupMenu = null;
/*     */       }
/*     */     }
/* 459 */     if (jPopupMenu != null) {
/* 460 */       jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
/* 461 */       return true;
/*     */     }
/* 463 */     return false;
/*     */   }
/*     */ 
/*     */   void processDrawingEditing(IDrawingsManager manager, KeyEvent e, int pressedKeyCode)
/*     */   {
/* 469 */     if ((27 == pressedKeyCode) || (10 == pressedKeyCode)) {
/* 470 */       manager.unselectDrawingToBeEditedAndExitEditingMode();
/* 471 */     } else if (127 == pressedKeyCode) {
/* 472 */       manager.deleteSelectedDrawing();
/* 473 */     } else if (37 == pressedKeyCode)
/*     */     {
/* 475 */       manager.moveEditedDrawingLeft();
/*     */ 
/* 477 */       e.getComponent().repaint();
/* 478 */     } else if (39 == pressedKeyCode)
/*     */     {
/* 480 */       manager.moveEditedDrawingRight();
/*     */ 
/* 482 */       e.getComponent().repaint();
/* 483 */     } else if (40 == pressedKeyCode)
/*     */     {
/* 485 */       manager.moveEditedDrawingDown();
/*     */ 
/* 487 */       e.getComponent().repaint();
/* 488 */     } else if (38 == pressedKeyCode)
/*     */     {
/* 490 */       manager.moveEditedDrawingUp();
/*     */ 
/* 492 */       e.getComponent().repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Point getGlobalPoint(MouseEvent e) {
/* 497 */     Point result = new Point(e.getPoint());
/* 498 */     result.y = 2;
/* 499 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.SubDrawingsMouseAndKeyControllerImpl
 * JD-Core Version:    0.6.0
 */