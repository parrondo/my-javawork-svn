/*     */ package com.dukascopy.dds2.greed.gui.component.splitPane;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.marketdepth.MarketDepthPanel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.accessibility.AccessibleContext;
/*     */ import javax.accessibility.AccessibleRole;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPanel.AccessibleJPanel;
/*     */ import javax.swing.event.MouseInputAdapter;
/*     */ 
/*     */ public class MultiSplitPane extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  43 */   private AccessibleContext accessibleContext = null;
/*  44 */   private boolean continuousLayout = true;
/*  45 */   private DividerPainter dividerPainter = new DefaultDividerPainter(null);
/*     */   private static final String layoutDef = "(COLUMN orderEntry marketDepth ticker workspace)";
/*     */   private static final String classicLayoutDef = "(COLUMN orderEntry marketDepth ticker)";
/* 232 */   private boolean dragUnderway = false;
/* 233 */   private MultiSplitLayout.Divider dragDivider = null;
/* 234 */   private Rectangle initialDividerBounds = null;
/* 235 */   private boolean oldFloatingDividers = true;
/* 236 */   private int dragOffsetY = 0;
/* 237 */   private int dragMin = -1;
/* 238 */   private int dragMax = -1;
/*     */ 
/*     */   public MultiSplitPane()
/*     */   {
/*  54 */     super(new MultiSplitLayout());
/*  55 */     InputHandler inputHandler = new InputHandler(null);
/*  56 */     addMouseListener(inputHandler);
/*  57 */     addMouseMotionListener(inputHandler);
/*  58 */     addKeyListener(inputHandler);
/*  59 */     setFocusable(true);
/*  60 */     init();
/*     */   }
/*     */ 
/*     */   private void init() {
/*  64 */     setDividerSize(5);
/*  65 */     getMultiSplitLayout().setModel(MultiSplitLayout.parseModel(GreedContext.isStrategyAllowed() ? "(COLUMN orderEntry marketDepth ticker workspace)" : "(COLUMN orderEntry marketDepth ticker)"));
/*     */   }
/*     */ 
/*     */   public final MultiSplitLayout getMultiSplitLayout()
/*     */   {
/*  79 */     return (MultiSplitLayout)getLayout();
/*     */   }
/*     */ 
/*     */   public final void setModel(MultiSplitLayout.Node model)
/*     */   {
/*  92 */     getMultiSplitLayout().setModel(model);
/*     */   }
/*     */ 
/*     */   public final void setDividerSize(int dividerSize)
/*     */   {
/* 106 */     getMultiSplitLayout().setDividerSize(dividerSize);
/*     */   }
/*     */ 
/*     */   public void setContinuousLayout(boolean continuousLayout)
/*     */   {
/* 119 */     boolean oldContinuousLayout = continuousLayout;
/* 120 */     this.continuousLayout = continuousLayout;
/* 121 */     firePropertyChange("continuousLayout", oldContinuousLayout, continuousLayout);
/*     */   }
/*     */ 
/*     */   public boolean isContinuousLayout()
/*     */   {
/* 133 */     return this.continuousLayout;
/*     */   }
/*     */ 
/*     */   public MultiSplitLayout.Divider activeDivider()
/*     */   {
/* 143 */     return this.dragDivider;
/*     */   }
/*     */ 
/*     */   public DividerPainter getDividerPainter()
/*     */   {
/* 187 */     return this.dividerPainter;
/*     */   }
/*     */ 
/*     */   public void setDividerPainter(DividerPainter dividerPainter)
/*     */   {
/* 204 */     this.dividerPainter = dividerPainter;
/*     */   }
/*     */ 
/*     */   protected void paintChildren(Graphics g)
/*     */   {
/* 216 */     super.paintChildren(g);
/* 217 */     DividerPainter dp = getDividerPainter();
/* 218 */     Rectangle clipR = g.getClipBounds();
/* 219 */     if ((dp != null) && (clipR != null)) {
/* 220 */       Graphics dpg = g.create();
/*     */       try {
/* 222 */         MultiSplitLayout msl = getMultiSplitLayout();
/* 223 */         for (MultiSplitLayout.Divider divider : msl.dividersThatOverlap(clipR))
/* 224 */           dp.paint(dpg, divider);
/*     */       }
/*     */       finally {
/* 227 */         dpg.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void startDrag(int mx, int my)
/*     */   {
/* 241 */     requestFocusInWindow();
/* 242 */     MultiSplitLayout msl = getMultiSplitLayout();
/* 243 */     MultiSplitLayout.Divider divider = msl.dividerAt(mx, my);
/*     */ 
/* 245 */     if (divider != null) {
/* 246 */       MultiSplitLayout.Node prevNode = divider.previousSibling();
/* 247 */       MultiSplitLayout.Node nextNode = divider.nextSibling();
/*     */ 
/* 249 */       if ((prevNode == null) || (nextNode == null)) {
/* 250 */         this.dragUnderway = false;
/*     */       } else {
/* 252 */         this.initialDividerBounds = divider.getBounds();
/* 253 */         this.dragOffsetY = (my - this.initialDividerBounds.y);
/* 254 */         this.dragDivider = divider;
/* 255 */         Rectangle prevNodeBounds = prevNode.getBounds();
/* 256 */         Rectangle nextNodeBounds = nextNode.getBounds();
/*     */ 
/* 258 */         MultiSplitable prevComp = getComponentByNode(prevNode);
/* 259 */         MultiSplitable nextComp = getComponentByNode(nextNode);
/*     */ 
/* 261 */         int prewMin = prevNodeBounds.y + prevComp.getMinHeight();
/* 262 */         int prewMax = prevNodeBounds.y + prevComp.getMaxHeight();
/*     */ 
/* 264 */         int nextMin = nextComp.getMinHeight();
/* 265 */         int nextMax = nextComp.getMaxHeight();
/*     */ 
/* 267 */         this.dragMin = Math.max(prewMin, nextNodeBounds.y + nextNodeBounds.height - nextMax);
/* 268 */         this.dragMax = Math.min(prewMax, nextNodeBounds.y + nextNodeBounds.height - nextMin);
/* 269 */         this.dragMax -= this.dragDivider.getBounds().height;
/*     */ 
/* 271 */         if (((isMktDepthDiv(this.dragDivider)) && (!getComponentByNode(this.dragDivider.nextSibling()).isExpanded())) || ((isTickerDiv(this.dragDivider)) && (!getComponentByNode(this.dragDivider.previousSibling()).isExpanded())))
/*     */         {
/* 274 */           MultiSplitLayout.Divider otherDiv = getOtherDivider(this.dragDivider);
/* 275 */           if (otherDiv.getBounds().y > this.dragDivider.getBounds().y) {
/* 276 */             this.dragMax += otherDiv.nextSibling().getBounds().height + getMultiSplitLayout().getDividerSize() - getComponentByNode(otherDiv.nextSibling()).getMinHeight();
/*     */           }
/*     */           else {
/* 279 */             this.dragMin -= otherDiv.previousSibling().getBounds().height - getComponentByNode(otherDiv.previousSibling()).getMinHeight();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 284 */         this.oldFloatingDividers = getMultiSplitLayout().getFloatingDividers();
/*     */ 
/* 286 */         getMultiSplitLayout().setFloatingDividers(false);
/*     */ 
/* 288 */         if ((isOrderEntryDiv(divider)) || ((!getComponentByNode(divider.previousSibling()).isExpanded()) && (isMktDepthDiv(divider))) || ((!getComponentByNode(getOtherDivider(divider).previousSibling()).isExpanded()) && (!getComponentByNode(divider.previousSibling()).isExpanded()) && (isTickerDiv(divider))))
/*     */         {
/* 292 */           this.dragUnderway = false;
/*     */         }
/* 294 */         else this.dragUnderway = true; 
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 298 */       this.dragUnderway = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private MultiSplitable getComponentByNode(MultiSplitLayout.Node searchNode) {
/* 303 */     List nodeList = ((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren();
/* 304 */     int counter = 0;
/* 305 */     for (int i = 0; i < nodeList.size(); i++) {
/* 306 */       if ((nodeList.get(i) instanceof MultiSplitLayout.Leaf)) {
/* 307 */         if (((MultiSplitLayout.Node)nodeList.get(i)).equals(searchNode)) {
/* 308 */           return (MultiSplitable)getComponent(counter);
/*     */         }
/* 310 */         counter++;
/*     */       }
/*     */     }
/*     */ 
/* 314 */     return (MultiSplitable)getComponent(counter);
/*     */   }
/*     */ 
/*     */   private void repaintDragLimits() {
/* 318 */     Rectangle damageR = this.dragDivider.getBounds();
/* 319 */     if (this.dragDivider.isVertical()) {
/* 320 */       damageR.x = this.dragMin;
/* 321 */       damageR.width = (this.dragMax - this.dragMin);
/*     */     } else {
/* 323 */       damageR.y = this.dragMin;
/* 324 */       damageR.height = (this.dragMax - this.dragMin);
/*     */     }
/* 326 */     repaint(damageR);
/*     */   }
/*     */ 
/*     */   private MultiSplitLayout.Divider getOtherDivider(MultiSplitLayout.Divider dragDivider) {
/* 330 */     if (isMktDepthDiv(dragDivider))
/* 331 */       return getDividerByName("ticker");
/* 332 */     if (isTickerDiv(dragDivider)) {
/* 333 */       return getDividerByName("marketDepth");
/*     */     }
/* 335 */     return null;
/*     */   }
/*     */ 
/*     */   private void updateDrag(int mx, int my) {
/* 339 */     if (!this.dragUnderway) return;
/*     */ 
/* 341 */     Rectangle oldBounds = this.dragDivider.getBounds();
/* 342 */     Rectangle bounds = new Rectangle(oldBounds);
/*     */ 
/* 344 */     bounds.y = (my - this.dragOffsetY);
/* 345 */     bounds.y = Math.max(bounds.y, this.dragMin);
/* 346 */     bounds.y = Math.min(bounds.y, this.dragMax);
/*     */ 
/* 348 */     MultiSplitLayout.Divider otherDiv = getOtherDivider(this.dragDivider);
/* 349 */     if (otherDiv != null) {
/* 350 */       Rectangle otherDivBounds = otherDiv.getBounds();
/*     */ 
/* 352 */       if (((isMktDepthDiv(this.dragDivider)) && (!getComponentByNode(this.dragDivider.nextSibling()).isExpanded())) || ((isTickerDiv(this.dragDivider)) && (!getComponentByNode(this.dragDivider.previousSibling()).isExpanded())))
/*     */       {
/* 354 */         otherDivBounds.y += bounds.y - oldBounds.y;
/*     */       }
/*     */ 
/* 357 */       otherDiv.setBounds(otherDivBounds);
/* 358 */       repaint(otherDivBounds);
/*     */     }
/*     */ 
/* 361 */     this.dragDivider.setBounds(bounds);
/*     */ 
/* 363 */     if (isContinuousLayout()) {
/* 364 */       revalidate();
/* 365 */       repaintDragLimits();
/*     */     } else {
/* 367 */       repaint(oldBounds.union(bounds));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void locateNewDividerPositions(String compName)
/*     */   {
/* 373 */     List dividers = new ArrayList();
/* 374 */     for (MultiSplitLayout.Node node : ((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren()) {
/* 375 */       if ((node instanceof MultiSplitLayout.Divider)) {
/* 376 */         dividers.add((MultiSplitLayout.Divider)node);
/*     */       }
/*     */     }
/* 379 */     MultiSplitLayout.Divider firstDivider = (MultiSplitLayout.Divider)dividers.get(dividers.size() - 3);
/* 380 */     MultiSplitLayout.Divider divider = (MultiSplitLayout.Divider)dividers.get(dividers.size() - 2);
/* 381 */     MultiSplitLayout.Divider lastDivider = (MultiSplitLayout.Divider)dividers.get(dividers.size() - 1);
/*     */ 
/* 384 */     Rectangle bounds = null;
/* 385 */     Rectangle lastDivBounds = null;
/* 386 */     Rectangle firstDivBounds = null;
/*     */ 
/* 390 */     if (("orderEntry".equals(compName)) || ("conOrderEntry".equals(compName))) {
/* 391 */       firstDivBounds = firstDivider.getBounds();
/* 392 */       Rectangle oldBounds = firstDivider.getBounds();
/* 393 */       MultiSplitLayout.Node prevNode = firstDivider.previousSibling();
/*     */ 
/* 395 */       bounds = divider.getBounds();
/* 396 */       lastDivBounds = lastDivider.getBounds();
/*     */ 
/* 398 */       firstDivBounds.y = getComponentByNode(prevNode).getPrefHeight();
/* 399 */       int diff = firstDivBounds.y - oldBounds.y;
/* 400 */       bounds.y += diff;
/* 401 */       lastDivBounds.y += diff;
/*     */     }
/* 403 */     else if ("marketDepth".equals(compName)) {
/* 404 */       MultiSplitLayout.Node prevNode = divider.previousSibling();
/* 405 */       firstDivBounds = firstDivider.getBounds();
/*     */ 
/* 407 */       Rectangle oldBounds = divider.getBounds();
/* 408 */       bounds = new Rectangle(oldBounds);
/* 409 */       lastDivBounds = lastDivider.getBounds();
/* 410 */       int divDiffSize = lastDivBounds.y - oldBounds.y;
/*     */ 
/* 412 */       if (((ClientForm)GreedContext.get("clientGui")).getDealPanel().getMarketDepthPanel().isExpanded()) {
/* 413 */         bounds.y += getComponentByNode(prevNode).getPrefHeight() - getMultiSplitLayout().getDividerSize();
/* 414 */         bounds.y -= dividers.size() * getMultiSplitLayout().getDividerSize();
/*     */       } else {
/* 416 */         if (prevNode.getBounds().height > 0) {
/* 417 */           ((ClientSettingsStorage)GreedContext.get("settingsStorage")).saveMarketDepthPanelHeight(prevNode.getBounds().height - getMultiSplitLayout().getDividerSize() * 2);
/*     */         }
/*     */ 
/* 420 */         firstDivBounds = firstDivider.getBounds();
/* 421 */         firstDivBounds.y += getComponentByNode(prevNode).getMinHeight();
/* 422 */         bounds.y += getMultiSplitLayout().getDividerSize();
/*     */       }
/*     */ 
/* 425 */       bounds.y += divDiffSize;
/*     */ 
/* 427 */       int diff = getMultiSplitLayout().getModel().getBounds().height - (lastDivBounds.y + getComponentByNode(lastDivider.nextSibling()).getMinHeight());
/*     */ 
/* 430 */       if (diff < 0) {
/* 431 */         lastDivBounds.y = (getMultiSplitLayout().getModel().getBounds().height - getComponentByNode(lastDivider.nextSibling()).getMinHeight() - lastDivBounds.height);
/*     */ 
/* 435 */         bounds.y = (lastDivBounds.y - getComponentByNode(lastDivider.previousSibling()).getMinHeight() - bounds.height);
/*     */ 
/* 439 */         int correction = getComponentByNode(divider.previousSibling()).getMaxHeight() - bounds.y - bounds.height;
/*     */ 
/* 442 */         if (correction < 0) {
/* 443 */           bounds.y += correction;
/*     */         }
/*     */       }
/*     */ 
/* 447 */       lastDivider.setBounds(lastDivBounds);
/* 448 */       divider.setBounds(bounds);
/* 449 */       repaint(lastDivider.getBounds());
/* 450 */     } else if ("ticker".equals(compName))
/*     */     {
/* 452 */       bounds = new Rectangle(divider.getBounds());
/* 453 */       lastDivBounds = new Rectangle(divider.getBounds());
/* 454 */       MultiSplitLayout.Node prevNode = lastDivider.previousSibling();
/*     */ 
/* 456 */       if (getComponentByNode(prevNode).isExpanded()) {
/* 457 */         lastDivBounds.y += getComponentByNode(prevNode).getPrefHeight();
/*     */       } else {
/* 459 */         if (prevNode.getBounds().height > 0) {
/* 460 */           ((ClientSettingsStorage)GreedContext.get("settingsStorage")).saveInstrumentsPanelHeight(prevNode.getBounds().height + getMultiSplitLayout().getDividerSize());
/*     */         }
/*     */ 
/* 464 */         lastDivBounds.y += getComponentByNode(prevNode).getMinHeight();
/* 465 */         lastDivBounds.y += getMultiSplitLayout().getDividerSize();
/*     */       }
/*     */ 
/* 468 */       int diff = getMultiSplitLayout().getModel().getBounds().height - (lastDivBounds.y + getComponentByNode(lastDivider.nextSibling()).getMinHeight());
/*     */ 
/* 471 */       if (diff < 0) {
/* 472 */         lastDivBounds.y = (getMultiSplitLayout().getModel().getBounds().height - getComponentByNode(lastDivider.nextSibling()).getMinHeight() - lastDivBounds.height);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 478 */     int layoutSize = getMultiSplitLayout().getModel().getBounds().height;
/* 479 */     if ((lastDivider != null) && (lastDivBounds != null) && (lastDivBounds.y > layoutSize - getComponentByNode(lastDivider.nextSibling()).getMinHeight()))
/*     */     {
/* 481 */       lastDivBounds.y = (layoutSize - getComponentByNode(lastDivider.nextSibling()).getMinHeight() - getMultiSplitLayout().getDividerSize());
/*     */     }
/* 483 */     if ((divider != null) && (bounds != null) && (lastDivBounds != null) && (bounds.y > lastDivBounds.y - getComponentByNode(divider.nextSibling()).getMinHeight()))
/*     */     {
/* 485 */       bounds.y = (lastDivBounds.y - getComponentByNode(divider.nextSibling()).getMinHeight() - getMultiSplitLayout().getDividerSize());
/*     */     }
/*     */ 
/* 488 */     if (firstDivBounds != null) {
/* 489 */       firstDivider.setBounds(firstDivBounds);
/* 490 */       repaint(firstDivider.getBounds());
/*     */     }
/*     */ 
/* 493 */     if (lastDivBounds != null) {
/* 494 */       lastDivider.setBounds(lastDivBounds);
/* 495 */       repaint(lastDivider.getBounds());
/*     */     }
/* 497 */     if (bounds != null) {
/* 498 */       divider.setBounds(bounds);
/*     */     }
/* 500 */     if ((isContinuousLayout()) && (divider != null)) {
/* 501 */       revalidate();
/* 502 */       repaint(divider.getBounds());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void locateDivPosForClassic(String compName) {
/* 507 */     List dividers = new ArrayList();
/* 508 */     for (MultiSplitLayout.Node node : ((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren()) {
/* 509 */       if ((node instanceof MultiSplitLayout.Divider)) {
/* 510 */         dividers.add((MultiSplitLayout.Divider)node);
/*     */       }
/*     */     }
/*     */ 
/* 514 */     MultiSplitLayout.Divider firstDivider = (MultiSplitLayout.Divider)dividers.get(dividers.size() - 2);
/* 515 */     MultiSplitLayout.Divider lastDivider = (MultiSplitLayout.Divider)dividers.get(dividers.size() - 1);
/*     */ 
/* 518 */     Rectangle firstDivBounds = firstDivider.getBounds();
/* 519 */     Rectangle lastDivBounds = lastDivider.getBounds();
/*     */ 
/* 521 */     if (("orderEntry".equals(compName)) || ("conOrderEntry".equals(compName))) {
/* 522 */       firstDivBounds = firstDivider.getBounds();
/* 523 */       Rectangle oldBounds = firstDivider.getBounds();
/* 524 */       MultiSplitLayout.Node prevNode = firstDivider.previousSibling();
/*     */ 
/* 526 */       firstDivBounds.y = getComponentByNode(prevNode).getPrefHeight();
/* 527 */       int diff = firstDivBounds.y - oldBounds.y;
/* 528 */       lastDivBounds.y += diff;
/*     */     }
/* 530 */     else if ("marketDepth".equals(compName)) {
/* 531 */       if (getComponentByNode(firstDivider.nextSibling()).isExpanded()) {
/* 532 */         firstDivBounds.y += getComponentByNode(lastDivider.previousSibling()).getPrefHeight();
/*     */       } else {
/* 534 */         if (firstDivider.nextSibling().getBounds().height > 0) {
/* 535 */           ((ClientSettingsStorage)GreedContext.get("settingsStorage")).saveMarketDepthPanelHeight(firstDivider.nextSibling().getBounds().height + getMultiSplitLayout().getDividerSize());
/*     */         }
/*     */ 
/* 538 */         firstDivBounds.y += getComponentByNode(lastDivider.previousSibling()).getMinHeight();
/*     */       }
/*     */     }
/*     */ 
/* 542 */     int layoutSize = getMultiSplitLayout().getModel().getBounds().height;
/* 543 */     if (lastDivBounds.y > layoutSize - getComponentByNode(lastDivider.nextSibling()).getMinHeight()) {
/* 544 */       lastDivBounds.y = (layoutSize - getComponentByNode(lastDivider.nextSibling()).getMinHeight() - getMultiSplitLayout().getDividerSize());
/*     */     }
/*     */ 
/* 547 */     firstDivider.setBounds(firstDivBounds);
/* 548 */     lastDivider.setBounds(lastDivBounds);
/*     */ 
/* 550 */     if ((isContinuousLayout()) && (firstDivider != null)) {
/* 551 */       revalidate();
/* 552 */       repaint(firstDivider.getBounds());
/*     */     }
/*     */ 
/* 555 */     if ((isContinuousLayout()) && (lastDivider != null)) {
/* 556 */       revalidate();
/* 557 */       repaint(lastDivider.getBounds());
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isOrderEntryDiv(MultiSplitLayout.Divider divider) {
/* 562 */     return ((MultiSplitLayout.Divider)((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren().get(1)).equals(divider);
/*     */   }
/*     */ 
/*     */   private boolean isMktDepthDiv(MultiSplitLayout.Divider divider) {
/* 566 */     return ((MultiSplitLayout.Divider)((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren().get(3)).equals(divider);
/*     */   }
/*     */ 
/*     */   private boolean isTickerDiv(MultiSplitLayout.Divider divider) {
/* 570 */     if (!GreedContext.isStrategyAllowed()) return false;
/*     */ 
/* 572 */     return ((MultiSplitLayout.Divider)((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren().get(5)).equals(divider);
/*     */   }
/*     */ 
/*     */   private MultiSplitLayout.Divider getDividerByName(String divName) {
/* 576 */     if ("orderEntry".equals(divName))
/* 577 */       return (MultiSplitLayout.Divider)((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren().get(1);
/* 578 */     if ("marketDepth".equals(divName))
/* 579 */       return (MultiSplitLayout.Divider)((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren().get(GreedContext.isStrategyAllowed() ? 3 : 1);
/* 580 */     if ("ticker".equals(divName)) {
/* 581 */       return (MultiSplitLayout.Divider)((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren().get(GreedContext.isStrategyAllowed() ? 5 : 3);
/*     */     }
/* 583 */     return null;
/*     */   }
/*     */ 
/*     */   private void clearDragState() {
/* 587 */     this.dragDivider = null;
/* 588 */     this.initialDividerBounds = null;
/* 589 */     this.oldFloatingDividers = true;
/* 590 */     this.dragOffsetY = 0;
/* 591 */     this.dragMin = (this.dragMax = -1);
/* 592 */     this.dragUnderway = false;
/*     */   }
/*     */ 
/*     */   private void finishDrag(int x, int y) {
/* 596 */     if (this.dragUnderway) {
/* 597 */       clearDragState();
/* 598 */       if (!isContinuousLayout()) {
/* 599 */         revalidate();
/* 600 */         repaint();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void cancelDrag() {
/* 606 */     if (this.dragUnderway) {
/* 607 */       this.dragDivider.setBounds(this.initialDividerBounds);
/* 608 */       getMultiSplitLayout().setFloatingDividers(this.oldFloatingDividers);
/* 609 */       setCursor(Cursor.getPredefinedCursor(0));
/* 610 */       repaint();
/* 611 */       revalidate();
/* 612 */       clearDragState();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateCursor(int x, int y, boolean show) {
/* 617 */     if (this.dragUnderway) {
/* 618 */       return;
/*     */     }
/* 620 */     int cursorID = 0;
/* 621 */     if (show) {
/* 622 */       MultiSplitLayout.Divider divider = getMultiSplitLayout().dividerAt(x, y);
/* 623 */       if ((divider != null) && (!isOrderEntryDiv(divider)) && ((getComponentByNode(divider.previousSibling()).isExpanded()) || (!isMktDepthDiv(divider))) && ((getComponentByNode(getOtherDivider(divider).previousSibling()).isExpanded()) || (getComponentByNode(divider.previousSibling()).isExpanded()) || (!isTickerDiv(divider))))
/*     */       {
/* 628 */         cursorID = divider.isVertical() ? 11 : 8;
/*     */       }
/*     */     }
/*     */ 
/* 632 */     setCursor(Cursor.getPredefinedCursor(cursorID));
/*     */   }
/*     */ 
/*     */   public AccessibleContext getAccessibleContext()
/*     */   {
/* 675 */     if (this.accessibleContext == null) {
/* 676 */       this.accessibleContext = new AccessibleMultiSplitPane();
/*     */     }
/* 678 */     return this.accessibleContext;
/*     */   }
/*     */ 
/*     */   public void switchVisibility(String compName)
/*     */   {
/* 697 */     if (GreedContext.isStrategyAllowed())
/* 698 */       locateNewDividerPositions(compName);
/*     */     else
/* 700 */       locateDivPosForClassic(compName);
/*     */   }
/*     */ 
/*     */   public List<Rectangle> getDividerPositions()
/*     */   {
/* 705 */     List result = new ArrayList();
/* 706 */     if ((getMultiSplitLayout().getModel() instanceof MultiSplitLayout.Split)) {
/* 707 */       for (MultiSplitLayout.Node node : ((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren()) {
/* 708 */         if ((node instanceof MultiSplitLayout.Divider)) {
/* 709 */           result.add(node.getBounds());
/*     */         }
/*     */       }
/*     */     }
/* 713 */     return result;
/*     */   }
/*     */ 
/*     */   public void refreshDividerPositions(List<Rectangle> positionList)
/*     */   {
/*     */     int i;
/* 717 */     if (positionList != null) {
/* 718 */       i = 0;
/* 719 */       if ((getMultiSplitLayout().getModel() instanceof MultiSplitLayout.Split))
/* 720 */         for (MultiSplitLayout.Node node : ((MultiSplitLayout.Split)getMultiSplitLayout().getModel()).getChildren())
/* 721 */           if ((node instanceof MultiSplitLayout.Divider)) {
/* 722 */             Rectangle newBound = (Rectangle)positionList.get(i++);
/* 723 */             ((MultiSplitLayout.Divider)node).setBounds(newBound);
/* 724 */             repaint(node.getBounds());
/*     */           }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class AccessibleMultiSplitPane extends JPanel.AccessibleJPanel
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     protected AccessibleMultiSplitPane()
/*     */     {
/* 681 */       super();
/*     */     }
/*     */ 
/*     */     public AccessibleRole getAccessibleRole() {
/* 685 */       return AccessibleRole.SPLIT_PANE;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class InputHandler extends MouseInputAdapter
/*     */     implements KeyListener
/*     */   {
/*     */     private InputHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseEntered(MouseEvent e)
/*     */     {
/* 638 */       MultiSplitPane.this.updateCursor(e.getX(), e.getY(), true);
/*     */     }
/*     */ 
/*     */     public void mouseMoved(MouseEvent e) {
/* 642 */       MultiSplitPane.this.updateCursor(e.getX(), e.getY(), true);
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent e) {
/* 646 */       MultiSplitPane.this.updateCursor(e.getX(), e.getY(), false);
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent e) {
/* 650 */       MultiSplitPane.this.startDrag(e.getX(), e.getY());
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent e) {
/* 654 */       MultiSplitPane.this.finishDrag(e.getX(), e.getY());
/*     */     }
/*     */ 
/*     */     public void mouseDragged(MouseEvent e) {
/* 658 */       MultiSplitPane.this.updateDrag(e.getX(), e.getY());
/*     */     }
/*     */ 
/*     */     public void keyPressed(KeyEvent e) {
/* 662 */       if (e.getKeyCode() == 27)
/* 663 */         MultiSplitPane.this.cancelDrag();
/*     */     }
/*     */ 
/*     */     public void keyReleased(KeyEvent e)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void keyTyped(KeyEvent e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class DefaultDividerPainter extends MultiSplitPane.DividerPainter
/*     */   {
/*     */     private DefaultDividerPainter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void paint(Graphics g, MultiSplitLayout.Divider divider)
/*     */     {
/* 167 */       if (!"orderEntry".equals(divider.getDivName())) {
/* 168 */         Graphics2D g2d = (Graphics2D)g;
/* 169 */         g2d.setColor(Color.white);
/* 170 */         Rectangle rect = divider.getBounds();
/* 171 */         rect.x = 7;
/* 172 */         rect.width = 243;
/* 173 */         rect.height = 1;
/* 174 */         g2d.fill(rect);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class DividerPainter
/*     */   {
/*     */     public abstract void paint(Graphics paramGraphics, MultiSplitLayout.Divider paramDivider);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitPane
 * JD-Core Version:    0.6.0
 */