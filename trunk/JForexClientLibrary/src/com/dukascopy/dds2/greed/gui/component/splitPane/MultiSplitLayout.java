/*      */ package com.dukascopy.dds2.greed.gui.component.splitPane;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.Rectangle;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.beans.PropertyChangeSupport;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StreamTokenizer;
/*      */ import java.io.StringReader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import javax.swing.UIManager;
/*      */ 
/*      */ public class MultiSplitLayout
/*      */   implements LayoutManager
/*      */ {
/*   55 */   private final Map<String, Component> childMap = new HashMap();
/*   56 */   private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
/*      */   private Node model;
/*      */   private int dividerSize;
/*   59 */   private boolean floatingDividers = true;
/*      */ 
/*      */   public MultiSplitLayout()
/*      */   {
/*   68 */     this(new Leaf("default"));
/*      */   }
/*      */ 
/*      */   public MultiSplitLayout(Node model)
/*      */   {
/*   77 */     this.model = model;
/*   78 */     this.dividerSize = UIManager.getInt("SplitPane.dividerSize");
/*   79 */     if (this.dividerSize == 0)
/*   80 */       this.dividerSize = 7;
/*      */   }
/*      */ 
/*      */   public void addPropertyChangeListener(PropertyChangeListener listener)
/*      */   {
/*   85 */     if (listener != null)
/*   86 */       this.pcs.addPropertyChangeListener(listener);
/*      */   }
/*      */ 
/*      */   public void removePropertyChangeListener(PropertyChangeListener listener)
/*      */   {
/*   91 */     if (listener != null)
/*   92 */       this.pcs.removePropertyChangeListener(listener);
/*      */   }
/*      */ 
/*      */   public PropertyChangeListener[] getPropertyChangeListeners()
/*      */   {
/*   97 */     return this.pcs.getPropertyChangeListeners();
/*      */   }
/*      */ 
/*      */   private void firePCS(String propertyName, Object oldValue, Object newValue) {
/*  101 */     if ((oldValue == null) || (newValue == null) || (!oldValue.equals(newValue)))
/*  102 */       this.pcs.firePropertyChange(propertyName, oldValue, newValue);
/*      */   }
/*      */ 
/*      */   public Node getModel()
/*      */   {
/*  114 */     return this.model;
/*      */   }
/*      */ 
/*      */   public void setModel(Node model)
/*      */   {
/*  129 */     if ((model == null) || ((model instanceof Divider))) {
/*  130 */       throw new IllegalArgumentException("invalid model");
/*      */     }
/*  132 */     Node oldModel = model;
/*  133 */     this.model = model;
/*  134 */     firePCS("model", oldModel, model);
/*      */   }
/*      */ 
/*      */   public int getDividerSize()
/*      */   {
/*  145 */     return this.dividerSize;
/*      */   }
/*      */ 
/*      */   public void setDividerSize(int dividerSize)
/*      */   {
/*  160 */     if (dividerSize < 0) {
/*  161 */       throw new IllegalArgumentException("invalid dividerSize");
/*      */     }
/*  163 */     int oldDividerSize = this.dividerSize;
/*  164 */     this.dividerSize = dividerSize;
/*  165 */     firePCS("dividerSize", Integer.valueOf(oldDividerSize), Integer.valueOf(dividerSize));
/*      */   }
/*      */ 
/*      */   public boolean getFloatingDividers()
/*      */   {
/*  173 */     return this.floatingDividers;
/*      */   }
/*      */ 
/*      */   public void setFloatingDividers(boolean floatingDividers)
/*      */   {
/*  186 */     boolean oldFloatingDividers = this.floatingDividers;
/*  187 */     this.floatingDividers = floatingDividers;
/*  188 */     firePCS("floatingDividers", Boolean.valueOf(oldFloatingDividers), Boolean.valueOf(floatingDividers));
/*      */   }
/*      */ 
/*      */   public void addLayoutComponent(String name, Component child)
/*      */   {
/*  206 */     if (name == null) {
/*  207 */       throw new IllegalArgumentException("name not specified");
/*      */     }
/*  209 */     this.childMap.put(name, child);
/*      */   }
/*      */ 
/*      */   public void removeLayoutComponent(Component child)
/*      */   {
/*  220 */     String name = child.getName();
/*  221 */     if (name != null)
/*  222 */       this.childMap.remove(name);
/*      */   }
/*      */ 
/*      */   private Component childForNode(Node node)
/*      */   {
/*  227 */     if ((node instanceof Leaf)) {
/*  228 */       Leaf leaf = (Leaf)node;
/*  229 */       String name = leaf.getName();
/*  230 */       return name != null ? (Component)this.childMap.get(name) : null;
/*      */     }
/*  232 */     return null;
/*      */   }
/*      */ 
/*      */   private Dimension preferredComponentSize(Node node) {
/*  236 */     Component child = childForNode(node);
/*  237 */     return child != null ? child.getPreferredSize() : new Dimension(0, 0);
/*      */   }
/*      */ 
/*      */   private Dimension minimumComponentSize(Node node)
/*      */   {
/*  242 */     Component child = childForNode(node);
/*  243 */     return child != null ? child.getMinimumSize() : new Dimension(0, 0);
/*      */   }
/*      */ 
/*      */   private Dimension preferredNodeSize(Node root)
/*      */   {
/*  248 */     if ((root instanceof Leaf))
/*  249 */       return preferredComponentSize(root);
/*  250 */     if ((root instanceof Divider)) {
/*  251 */       int dividerSize = getDividerSize();
/*  252 */       return new Dimension(dividerSize, dividerSize);
/*      */     }
/*  254 */     Split split = (Split)root;
/*  255 */     List splitChildren = split.getChildren();
/*  256 */     int width = 0;
/*  257 */     int height = 0;
/*  258 */     if (split.isRowLayout())
/*  259 */       for (Node splitChild : splitChildren) {
/*  260 */         Dimension size = preferredNodeSize(splitChild);
/*  261 */         width += size.width;
/*  262 */         height = Math.max(height, size.height);
/*      */       }
/*      */     else {
/*  265 */       for (Node splitChild : splitChildren) {
/*  266 */         Dimension size = preferredNodeSize(splitChild);
/*  267 */         width = Math.max(width, size.width);
/*  268 */         height += size.height;
/*      */       }
/*      */     }
/*  271 */     return new Dimension(width, height);
/*      */   }
/*      */ 
/*      */   private Dimension minimumNodeSize(Node root)
/*      */   {
/*  276 */     if ((root instanceof Leaf)) {
/*  277 */       Component child = childForNode(root);
/*  278 */       return child != null ? child.getMinimumSize() : new Dimension(0, 0);
/*      */     }
/*  280 */     if ((root instanceof Divider)) {
/*  281 */       int dividerSize = getDividerSize();
/*  282 */       return new Dimension(dividerSize, dividerSize);
/*      */     }
/*  284 */     Split split = (Split)root;
/*  285 */     List splitChildren = split.getChildren();
/*  286 */     int width = 0;
/*  287 */     int height = 0;
/*  288 */     if (split.isRowLayout())
/*  289 */       for (Node splitChild : splitChildren) {
/*  290 */         Dimension size = minimumNodeSize(splitChild);
/*  291 */         width += size.width;
/*  292 */         height = Math.max(height, size.height);
/*      */       }
/*      */     else {
/*  295 */       for (Node splitChild : splitChildren) {
/*  296 */         Dimension size = minimumNodeSize(splitChild);
/*  297 */         width = Math.max(width, size.width);
/*  298 */         height += size.height;
/*      */       }
/*      */     }
/*  301 */     return new Dimension(width, height);
/*      */   }
/*      */ 
/*      */   private Dimension sizeWithInsets(Container parent, Dimension size)
/*      */   {
/*  306 */     Insets insets = parent.getInsets();
/*  307 */     int width = size.width + insets.left + insets.right;
/*  308 */     int height = size.height + insets.top + insets.bottom;
/*  309 */     return new Dimension(width, height);
/*      */   }
/*      */ 
/*      */   public Dimension preferredLayoutSize(Container parent) {
/*  313 */     Dimension size = preferredNodeSize(getModel());
/*  314 */     return sizeWithInsets(parent, size);
/*      */   }
/*      */ 
/*      */   public Dimension minimumLayoutSize(Container parent) {
/*  318 */     Dimension size = minimumNodeSize(getModel());
/*  319 */     return sizeWithInsets(parent, size);
/*      */   }
/*      */ 
/*      */   private Rectangle boundsWithYandHeight(Rectangle bounds, double y, double height)
/*      */   {
/*  324 */     Rectangle r = new Rectangle();
/*  325 */     r.setBounds((int)bounds.getX(), (int)y, (int)bounds.getWidth(), (int)height);
/*      */ 
/*  327 */     return r;
/*      */   }
/*      */ 
/*      */   private Rectangle boundsWithXandWidth(Rectangle bounds, double x, double width)
/*      */   {
/*  332 */     Rectangle r = new Rectangle();
/*  333 */     r.setBounds((int)x, (int)bounds.getY(), (int)width, (int)bounds.getHeight());
/*      */ 
/*  335 */     return r;
/*      */   }
/*      */ 
/*      */   private void minimizeSplitBounds(Split split, Rectangle bounds) {
/*  339 */     Rectangle splitBounds = new Rectangle(bounds.x, bounds.y, 0, 0);
/*  340 */     List splitChildren = split.getChildren();
/*  341 */     Node lastChild = (Node)splitChildren.get(splitChildren.size() - 1);
/*  342 */     Rectangle lastChildBounds = lastChild.getBounds();
/*  343 */     if (split.isRowLayout()) {
/*  344 */       int lastChildMaxX = lastChildBounds.x + lastChildBounds.width;
/*  345 */       splitBounds.add(lastChildMaxX, bounds.y + bounds.height);
/*      */     } else {
/*  347 */       int lastChildMaxY = lastChildBounds.y + lastChildBounds.height;
/*  348 */       splitBounds.add(bounds.x + bounds.width, lastChildMaxY);
/*      */     }
/*  350 */     split.setBounds(splitBounds);
/*      */   }
/*      */ 
/*      */   private void layoutShrink(Split split, Rectangle bounds) {
/*  354 */     Rectangle splitBounds = split.getBounds();
/*  355 */     ListIterator splitChildren = split.getChildren().listIterator();
/*  356 */     Node lastWeightedChild = split.lastWeightedChild();
/*      */ 
/*  358 */     if (split.isRowLayout()) {
/*  359 */       int totalWidth = 0;
/*  360 */       int minWeightedWidth = 0;
/*      */ 
/*  362 */       int totalWeightedWidth = 0;
/*  363 */       for (Node splitChild : split.getChildren()) {
/*  364 */         int nodeWidth = splitChild.getBounds().width;
/*  365 */         int nodeMinWidth = Math.min(nodeWidth, minimumNodeSize(splitChild).width);
/*      */ 
/*  367 */         totalWidth += nodeWidth;
/*  368 */         if (splitChild.getWeight() > 0.0D) {
/*  369 */           minWeightedWidth += nodeMinWidth;
/*  370 */           totalWeightedWidth += nodeWidth;
/*      */         }
/*      */       }
/*      */ 
/*  374 */       double x = bounds.getX();
/*  375 */       double extraWidth = splitBounds.getWidth() - bounds.getWidth();
/*  376 */       double availableWidth = extraWidth;
/*  377 */       boolean onlyShrinkWeightedComponents = totalWeightedWidth - minWeightedWidth > extraWidth;
/*      */ 
/*  379 */       while (splitChildren.hasNext()) {
/*  380 */         Node splitChild = (Node)splitChildren.next();
/*  381 */         Rectangle splitChildBounds = splitChild.getBounds();
/*  382 */         double minSplitChildWidth = minimumNodeSize(splitChild).getWidth();
/*      */ 
/*  384 */         double splitChildWeight = onlyShrinkWeightedComponents ? splitChild.getWeight() : splitChildBounds.getWidth() / totalWidth;
/*      */ 
/*  388 */         if (!splitChildren.hasNext()) {
/*  389 */           double newWidth = Math.max(minSplitChildWidth, bounds.getMaxX() - x);
/*      */ 
/*  392 */           Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
/*      */ 
/*  394 */           layout2(splitChild, newSplitChildBounds);
/*  395 */         } else if ((availableWidth > 0.0D) && (splitChildWeight > 0.0D)) {
/*  396 */           double allocatedWidth = Math.rint(splitChildWeight * extraWidth);
/*      */ 
/*  398 */           double oldWidth = splitChildBounds.getWidth();
/*  399 */           double newWidth = Math.max(minSplitChildWidth, oldWidth - allocatedWidth);
/*      */ 
/*  401 */           Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
/*      */ 
/*  403 */           layout2(splitChild, newSplitChildBounds);
/*  404 */           availableWidth -= oldWidth - splitChild.getBounds().getWidth();
/*      */         }
/*      */         else {
/*  407 */           double existingWidth = splitChildBounds.getWidth();
/*  408 */           Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, existingWidth);
/*      */ 
/*  410 */           layout2(splitChild, newSplitChildBounds);
/*      */         }
/*  412 */         x = splitChild.getBounds().getMaxX();
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  417 */       int totalHeight = 0;
/*  418 */       int minWeightedHeight = 0;
/*      */ 
/*  420 */       int totalWeightedHeight = 0;
/*      */ 
/*  422 */       for (Node splitChild : split.getChildren()) {
/*  423 */         int nodeHeight = splitChild.getBounds().height;
/*  424 */         int nodeMinHeight = Math.min(nodeHeight, minimumNodeSize(splitChild).height);
/*      */ 
/*  426 */         totalHeight += nodeHeight;
/*  427 */         if (splitChild.getWeight() > 0.0D) {
/*  428 */           minWeightedHeight += nodeMinHeight;
/*  429 */           totalWeightedHeight += nodeHeight;
/*      */         }
/*      */       }
/*      */ 
/*  433 */       double y = bounds.getY();
/*  434 */       double extraHeight = splitBounds.getHeight() - bounds.getHeight();
/*  435 */       double availableHeight = extraHeight;
/*  436 */       boolean onlyShrinkWeightedComponents = totalWeightedHeight - minWeightedHeight > extraHeight;
/*      */ 
/*  438 */       while (splitChildren.hasNext()) {
/*  439 */         Node splitChild = (Node)splitChildren.next();
/*  440 */         Rectangle splitChildBounds = splitChild.getBounds();
/*  441 */         double minSplitChildHeight = minimumNodeSize(splitChild).getHeight();
/*      */ 
/*  443 */         double splitChildWeight = onlyShrinkWeightedComponents ? splitChild.getWeight() : splitChildBounds.getHeight() / totalHeight;
/*      */ 
/*  447 */         if (!splitChildren.hasNext()) {
/*  448 */           double oldHeight = splitChildBounds.getHeight();
/*  449 */           double newHeight = Math.max(minSplitChildHeight, bounds.getMaxY() - y);
/*      */ 
/*  452 */           Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
/*      */ 
/*  454 */           layout2(splitChild, newSplitChildBounds);
/*  455 */           availableHeight -= oldHeight - splitChild.getBounds().getHeight();
/*      */         }
/*  457 */         else if ((availableHeight > 0.0D) && (splitChildWeight > 0.0D)) {
/*  458 */           double allocatedHeight = Math.rint(splitChildWeight * extraHeight);
/*      */ 
/*  460 */           double oldHeight = splitChildBounds.getHeight();
/*  461 */           double newHeight = Math.max(minSplitChildHeight, oldHeight - allocatedHeight);
/*      */ 
/*  463 */           Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
/*      */ 
/*  465 */           layout2(splitChild, newSplitChildBounds);
/*  466 */           availableHeight -= oldHeight - splitChild.getBounds().getHeight();
/*      */         }
/*      */         else {
/*  469 */           double existingHeight = splitChildBounds.getHeight();
/*  470 */           Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, existingHeight);
/*      */ 
/*  472 */           layout2(splitChild, newSplitChildBounds);
/*      */         }
/*  474 */         y = splitChild.getBounds().getMaxY();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  485 */     minimizeSplitBounds(split, bounds);
/*      */   }
/*      */ 
/*      */   private void layoutGrow(Split split, Rectangle bounds) {
/*  489 */     Rectangle splitBounds = split.getBounds();
/*  490 */     ListIterator splitChildren = split.getChildren().listIterator();
/*  491 */     Node lastWeightedChild = split.lastWeightedChild();
/*      */ 
/*  503 */     if (split.isRowLayout()) {
/*  504 */       double x = bounds.getX();
/*  505 */       double extraWidth = bounds.getWidth() - splitBounds.getWidth();
/*  506 */       double availableWidth = extraWidth;
/*      */ 
/*  508 */       while (splitChildren.hasNext()) {
/*  509 */         Node splitChild = (Node)splitChildren.next();
/*  510 */         Rectangle splitChildBounds = splitChild.getBounds();
/*  511 */         double splitChildWeight = splitChild.getWeight();
/*      */ 
/*  513 */         if (!splitChildren.hasNext()) {
/*  514 */           double newWidth = bounds.getMaxX() - x;
/*  515 */           Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
/*      */ 
/*  517 */           layout2(splitChild, newSplitChildBounds);
/*  518 */         } else if ((availableWidth > 0.0D) && (splitChildWeight > 0.0D)) {
/*  519 */           double allocatedWidth = splitChild.equals(lastWeightedChild) ? availableWidth : Math.rint(splitChildWeight * extraWidth);
/*      */ 
/*  522 */           double newWidth = splitChildBounds.getWidth() + allocatedWidth;
/*      */ 
/*  524 */           Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
/*      */ 
/*  526 */           layout2(splitChild, newSplitChildBounds);
/*  527 */           availableWidth -= allocatedWidth;
/*      */         } else {
/*  529 */           double existingWidth = splitChildBounds.getWidth();
/*  530 */           Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, existingWidth);
/*      */ 
/*  532 */           layout2(splitChild, newSplitChildBounds);
/*      */         }
/*  534 */         x = splitChild.getBounds().getMaxX();
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  549 */       double y = bounds.getY();
/*  550 */       double extraHeight = bounds.getMaxY() - splitBounds.getHeight();
/*  551 */       double availableHeight = extraHeight;
/*      */ 
/*  553 */       while (splitChildren.hasNext()) {
/*  554 */         Node splitChild = (Node)splitChildren.next();
/*  555 */         Rectangle splitChildBounds = splitChild.getBounds();
/*  556 */         double splitChildWeight = splitChild.getWeight();
/*      */ 
/*  558 */         if (!splitChildren.hasNext()) {
/*  559 */           double newHeight = bounds.getMaxY() - y;
/*  560 */           Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
/*      */ 
/*  562 */           layout2(splitChild, newSplitChildBounds);
/*  563 */         } else if ((availableHeight > 0.0D) && (splitChildWeight > 0.0D)) {
/*  564 */           double allocatedHeight = splitChild.equals(lastWeightedChild) ? availableHeight : Math.rint(splitChildWeight * extraHeight);
/*      */ 
/*  567 */           double newHeight = splitChildBounds.getHeight() + allocatedHeight;
/*      */ 
/*  569 */           Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
/*      */ 
/*  571 */           layout2(splitChild, newSplitChildBounds);
/*  572 */           availableHeight -= allocatedHeight;
/*      */         } else {
/*  574 */           double existingHeight = splitChildBounds.getHeight();
/*  575 */           Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, existingHeight);
/*      */ 
/*  577 */           layout2(splitChild, newSplitChildBounds);
/*      */         }
/*  579 */         y = splitChild.getBounds().getMaxY();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void layout2(Node root, Rectangle bounds)
/*      */   {
/*  589 */     if ((root instanceof Leaf)) {
/*  590 */       Component child = childForNode(root);
/*  591 */       if (child != null) {
/*  592 */         child.setBounds(bounds);
/*      */       }
/*  594 */       root.setBounds(bounds);
/*  595 */     } else if ((root instanceof Divider)) {
/*  596 */       root.setBounds(bounds);
/*  597 */     } else if ((root instanceof Split)) {
/*  598 */       Split split = (Split)root;
/*  599 */       boolean grow = split.getBounds().width <= bounds.width;
/*      */ 
/*  601 */       if (grow) {
/*  602 */         layoutGrow(split, bounds);
/*  603 */         root.setBounds(bounds);
/*      */       } else {
/*  605 */         layoutShrink(split, bounds);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void layout1(Node root, Rectangle bounds)
/*      */   {
/*  625 */     if ((root instanceof Leaf)) {
/*  626 */       root.setBounds(bounds);
/*  627 */     } else if ((root instanceof Split)) {
/*  628 */       Split split = (Split)root;
/*  629 */       Iterator splitChildren = split.getChildren().iterator();
/*  630 */       Rectangle childBounds = null;
/*  631 */       int dividerSize = getDividerSize();
/*      */ 
/*  641 */       if (split.isRowLayout()) {
/*  642 */         double x = bounds.getX();
/*  643 */         while (splitChildren.hasNext()) {
/*  644 */           Node splitChild = (Node)splitChildren.next();
/*  645 */           Divider dividerChild = splitChildren.hasNext() ? (Divider)(Divider)splitChildren.next() : null;
/*      */ 
/*  649 */           double childWidth = 0.0D;
/*  650 */           if (getFloatingDividers()) {
/*  651 */             childWidth = preferredNodeSize(splitChild).getWidth();
/*      */           }
/*  653 */           else if (dividerChild != null)
/*  654 */             childWidth = dividerChild.getBounds().getX() - x;
/*      */           else {
/*  656 */             childWidth = split.getBounds().getMaxX() - x;
/*      */           }
/*      */ 
/*  659 */           childBounds = boundsWithXandWidth(bounds, x, childWidth);
/*  660 */           layout1(splitChild, childBounds);
/*      */ 
/*  662 */           if ((getFloatingDividers()) && (dividerChild != null)) {
/*  663 */             double dividerX = childBounds.getMaxX();
/*  664 */             Rectangle dividerBounds = boundsWithXandWidth(bounds, dividerX, dividerSize);
/*      */ 
/*  666 */             dividerChild.setBounds(dividerBounds);
/*      */           }
/*  668 */           if (dividerChild != null) {
/*  669 */             x = dividerChild.getBounds().getMaxX();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  681 */         double y = bounds.getY();
/*  682 */         while (splitChildren.hasNext()) {
/*  683 */           Node splitChild = (Node)splitChildren.next();
/*  684 */           Divider dividerChild = splitChildren.hasNext() ? (Divider)(Divider)splitChildren.next() : null;
/*      */ 
/*  688 */           double childHeight = 0.0D;
/*  689 */           if (getFloatingDividers()) {
/*  690 */             childHeight = preferredNodeSize(splitChild).getHeight();
/*      */           }
/*  692 */           else if (dividerChild != null)
/*  693 */             childHeight = dividerChild.getBounds().getY() - y;
/*      */           else {
/*  695 */             childHeight = split.getBounds().getMaxY() - y;
/*      */           }
/*      */ 
/*  698 */           childBounds = boundsWithYandHeight(bounds, y, childHeight);
/*  699 */           layout1(splitChild, childBounds);
/*      */ 
/*  701 */           if ((getFloatingDividers()) && (dividerChild != null)) {
/*  702 */             double dividerY = childBounds.getMaxY();
/*  703 */             Rectangle dividerBounds = boundsWithYandHeight(bounds, dividerY, dividerSize);
/*      */ 
/*  705 */             dividerChild.setBounds(dividerBounds);
/*      */           }
/*  707 */           if (dividerChild != null) {
/*  708 */             y = dividerChild.getBounds().getMaxY();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  719 */       minimizeSplitBounds(split, bounds);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void throwInvalidLayout(String msg, Node node)
/*      */   {
/*  744 */     throw new InvalidLayoutException(msg, node);
/*      */   }
/*      */ 
/*      */   private void checkLayout(Node root) {
/*  748 */     if ((root instanceof Split)) {
/*  749 */       Split split = (Split)root;
/*  750 */       if (split.getChildren().size() <= 2) {
/*  751 */         throwInvalidLayout("Split must have > 2 children", root);
/*      */       }
/*  753 */       Iterator splitChildren = split.getChildren().iterator();
/*  754 */       double weight = 0.0D;
/*  755 */       while (splitChildren.hasNext()) {
/*  756 */         Node splitChild = (Node)splitChildren.next();
/*  757 */         if ((splitChild instanceof Divider)) {
/*  758 */           throwInvalidLayout("expected a Split or Leaf Node", splitChild);
/*      */         }
/*      */ 
/*  761 */         if (splitChildren.hasNext()) {
/*  762 */           Node dividerChild = (Node)splitChildren.next();
/*  763 */           if (!(dividerChild instanceof Divider)) {
/*  764 */             throwInvalidLayout("expected a Divider Node", dividerChild);
/*      */           }
/*      */         }
/*      */ 
/*  768 */         weight += splitChild.getWeight();
/*  769 */         checkLayout(splitChild);
/*      */       }
/*  771 */       if (weight > 1.0D)
/*  772 */         throwInvalidLayout("Split children's total weight > 1.0", root);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void layoutContainer(Container parent)
/*      */   {
/*  783 */     checkLayout(getModel());
/*  784 */     Insets insets = parent.getInsets();
/*  785 */     Dimension size = parent.getSize();
/*  786 */     int width = size.width - (insets.left + insets.right);
/*  787 */     int height = size.height - (insets.top + insets.bottom);
/*  788 */     Rectangle bounds = new Rectangle(insets.left, insets.top, width, height);
/*  789 */     layout1(getModel(), bounds);
/*  790 */     layout2(getModel(), bounds);
/*      */   }
/*      */ 
/*      */   private Divider dividerAt(Node root, int x, int y) {
/*  794 */     if ((root instanceof Divider)) {
/*  795 */       Divider divider = (Divider)root;
/*  796 */       return divider.getBounds().contains(x, y) ? divider : null;
/*  797 */     }if ((root instanceof Split)) {
/*  798 */       Split split = (Split)root;
/*  799 */       for (Node child : split.getChildren()) {
/*  800 */         if (child.getBounds().contains(x, y)) {
/*  801 */           return dividerAt(child, x, y);
/*      */         }
/*      */       }
/*      */     }
/*  805 */     return null;
/*      */   }
/*      */ 
/*      */   public Divider dividerAt(int x, int y)
/*      */   {
/*  819 */     return dividerAt(getModel(), x, y);
/*      */   }
/*      */ 
/*      */   private boolean nodeOverlapsRectangle(Node node, Rectangle r2) {
/*  823 */     Rectangle r1 = node.getBounds();
/*  824 */     return (r1.x <= r2.x + r2.width) && (r1.x + r1.width >= r2.x) && (r1.y <= r2.y + r2.height) && (r1.y + r1.height >= r2.y);
/*      */   }
/*      */ 
/*      */   private List<Divider> dividersThatOverlap(Node root, Rectangle r)
/*      */   {
/*  829 */     if ((nodeOverlapsRectangle(root, r)) && ((root instanceof Split))) {
/*  830 */       List dividers = new ArrayList();
/*  831 */       for (Node child : ((Split)root).getChildren()) {
/*  832 */         if ((child instanceof Divider)) {
/*  833 */           if (nodeOverlapsRectangle(child, r))
/*  834 */             dividers.add((Divider)child);
/*      */         }
/*  836 */         else if ((child instanceof Split)) {
/*  837 */           dividers.addAll(dividersThatOverlap(child, r));
/*      */         }
/*      */       }
/*  840 */       return dividers;
/*      */     }
/*  842 */     return Collections.emptyList();
/*      */   }
/*      */ 
/*      */   public List<Divider> dividersThatOverlap(Rectangle r)
/*      */   {
/*  856 */     if (r == null) {
/*  857 */       throw new IllegalArgumentException("null Rectangle");
/*      */     }
/*  859 */     return dividersThatOverlap(getModel(), r);
/*      */   }
/*      */ 
/*      */   private static void throwParseException(StreamTokenizer st, String msg)
/*      */     throws Exception
/*      */   {
/* 1205 */     throw new Exception("MultiSplitLayout.parseModel Error: " + msg);
/*      */   }
/*      */ 
/*      */   private static void parseAttribute(String name, StreamTokenizer st, Node node) throws Exception
/*      */   {
/* 1210 */     if (st.nextToken() != 61) {
/* 1211 */       throwParseException(st, "expected '=' after " + name);
/*      */     }
/* 1213 */     if (name.equalsIgnoreCase("WEIGHT")) {
/* 1214 */       if (st.nextToken() == -2)
/* 1215 */         node.setWeight(st.nval);
/*      */       else
/* 1217 */         throwParseException(st, "invalid weight");
/*      */     }
/* 1219 */     else if (name.equalsIgnoreCase("NAME")) {
/* 1220 */       if (st.nextToken() == -3) {
/* 1221 */         if ((node instanceof Leaf))
/* 1222 */           ((Leaf)node).setName(st.sval);
/*      */         else
/* 1224 */           throwParseException(st, "can't specify name for " + node);
/*      */       }
/*      */       else
/* 1227 */         throwParseException(st, "invalid name");
/*      */     }
/*      */     else
/* 1230 */       throwParseException(st, "unrecognized attribute \"" + name + "\"");
/*      */   }
/*      */ 
/*      */   private static void addSplitChild(Split parent, Node child)
/*      */   {
/* 1235 */     List children = new ArrayList(parent.getChildren());
/* 1236 */     if (children.size() == 0) {
/* 1237 */       children.add(child);
/*      */     } else {
/* 1239 */       children.add(new Divider());
/* 1240 */       children.add(child);
/*      */     }
/* 1242 */     parent.setChildren(children);
/*      */   }
/*      */ 
/*      */   private static void parseLeaf(StreamTokenizer st, Split parent) throws Exception
/*      */   {
/* 1247 */     Leaf leaf = new Leaf();
/*      */     int token;
/* 1249 */     while (((token = st.nextToken()) != -1) && 
/* 1250 */       (token != 41))
/*      */     {
/* 1253 */       if (token == -3) {
/* 1254 */         parseAttribute(st.sval, st, leaf); continue;
/*      */       }
/* 1256 */       throwParseException(st, "Bad Leaf: " + leaf);
/*      */     }
/*      */ 
/* 1259 */     addSplitChild(parent, leaf);
/*      */   }
/*      */ 
/*      */   private static void parseSplit(StreamTokenizer st, Split parent)
/*      */     throws Exception
/*      */   {
/*      */     int token;
/* 1265 */     while (((token = st.nextToken()) != -1) && 
/* 1266 */       (token != 41))
/*      */     {
/* 1268 */       if (token == -3) {
/* 1269 */         if (st.sval.equalsIgnoreCase("WEIGHT")) {
/* 1270 */           parseAttribute(st.sval, st, parent); continue;
/*      */         }
/* 1272 */         addSplitChild(parent, new Leaf(st.sval)); continue;
/*      */       }
/* 1274 */       if (token == 40) {
/* 1275 */         if ((token = st.nextToken()) != -3) {
/* 1276 */           throwParseException(st, "invalid node type");
/*      */         }
/* 1278 */         String nodeType = st.sval.toUpperCase();
/* 1279 */         if (nodeType.equals("LEAF")) {
/* 1280 */           parseLeaf(st, parent);
/* 1281 */         } else if ((nodeType.equals("ROW")) || (nodeType.equals("COLUMN"))) {
/* 1282 */           Split split = new Split();
/* 1283 */           split.setRowLayout(nodeType.equals("ROW"));
/* 1284 */           addSplitChild(parent, split);
/* 1285 */           parseSplit(st, split);
/*      */         } else {
/* 1287 */           throwParseException(st, "unrecognized node type '" + nodeType + "'");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Node parseModel(Reader r)
/*      */   {
/* 1295 */     StreamTokenizer st = new StreamTokenizer(r);
/*      */     try {
/* 1297 */       Split root = new Split();
/* 1298 */       parseSplit(st, root);
/* 1299 */       Node localNode = (Node)root.getChildren().get(0);
/*      */       return localNode;
/*      */     }
/*      */     catch (Exception ignore)
/*      */     {
/* 1301 */       System.err.println(e);
/*      */     } finally {
/*      */       try {
/* 1304 */         r.close();
/*      */       } catch (IOException ignore) {
/*      */       }
/*      */     }
/* 1308 */     return null;
/*      */   }
/*      */ 
/*      */   public static Node parseModel(String s)
/*      */   {
/* 1358 */     return parseModel(new StringReader(s));
/*      */   }
/*      */ 
/*      */   private static void printModel(String indent, Node root) {
/* 1362 */     if ((root instanceof Split)) {
/* 1363 */       Split split = (Split)root;
/* 1364 */       System.out.println(indent + split);
/* 1365 */       for (Node child : split.getChildren())
/* 1366 */         printModel(indent + "  ", child);
/*      */     }
/*      */     else {
/* 1369 */       System.out.println(indent + root);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void printModel(Node root)
/*      */   {
/* 1377 */     printModel("", root);
/*      */   }
/*      */ 
/*      */   public static class Divider extends MultiSplitLayout.Node
/*      */   {
/*      */     public final boolean isVertical()
/*      */     {
/* 1180 */       MultiSplitLayout.Split parent = getParent();
/* 1181 */       return parent != null ? parent.isRowLayout() : false;
/*      */     }
/*      */ 
/*      */     public void setWeight(double weight)
/*      */     {
/* 1190 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1194 */       return "MultiSplitLayout.Divider " + getBounds().toString();
/*      */     }
/*      */ 
/*      */     public String getDivName() {
/* 1198 */       return ((MultiSplitLayout.Leaf)previousSibling()).getName();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Leaf extends MultiSplitLayout.Node
/*      */   {
/* 1101 */     private String name = "";
/*      */ 
/*      */     public Leaf()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Leaf(String name)
/*      */     {
/* 1123 */       if (name == null) {
/* 1124 */         throw new IllegalArgumentException("name is null");
/*      */       }
/* 1126 */       this.name = name;
/*      */     }
/*      */ 
/*      */     public String getName()
/*      */     {
/* 1136 */       return this.name;
/*      */     }
/*      */ 
/*      */     public void setName(String name)
/*      */     {
/* 1148 */       if (name == null) {
/* 1149 */         throw new IllegalArgumentException("name is null");
/*      */       }
/* 1151 */       this.name = name;
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1155 */       StringBuffer sb = new StringBuffer("MultiSplitLayout.Leaf");
/* 1156 */       sb.append(" \"");
/* 1157 */       sb.append(getName());
/* 1158 */       sb.append("\"");
/* 1159 */       sb.append(" weight=");
/* 1160 */       sb.append(getWeight());
/* 1161 */       sb.append(" ");
/* 1162 */       sb.append(getBounds());
/* 1163 */       return sb.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Split extends MultiSplitLayout.Node
/*      */   {
/* 1002 */     private List<MultiSplitLayout.Node> children = Collections.emptyList();
/* 1003 */     private boolean rowLayout = true;
/*      */ 
/*      */     public boolean isRowLayout()
/*      */     {
/* 1014 */       return this.rowLayout;
/*      */     }
/*      */ 
/*      */     public void setRowLayout(boolean rowLayout)
/*      */     {
/* 1028 */       this.rowLayout = rowLayout;
/*      */     }
/*      */ 
/*      */     public List<MultiSplitLayout.Node> getChildren()
/*      */     {
/* 1039 */       return new ArrayList(this.children);
/*      */     }
/*      */ 
/*      */     public void setChildren(List<MultiSplitLayout.Node> children)
/*      */     {
/* 1055 */       if (children == null) {
/* 1056 */         throw new IllegalArgumentException("children must be a non-null List");
/*      */       }
/*      */ 
/* 1059 */       for (MultiSplitLayout.Node child : this.children) {
/* 1060 */         child.setParent(null);
/*      */       }
/* 1062 */       this.children = new ArrayList(children);
/* 1063 */       for (MultiSplitLayout.Node child : this.children)
/* 1064 */         child.setParent(this);
/*      */     }
/*      */ 
/*      */     public final MultiSplitLayout.Node lastWeightedChild()
/*      */     {
/* 1076 */       List children = getChildren();
/* 1077 */       MultiSplitLayout.Node weightedChild = null;
/* 1078 */       for (MultiSplitLayout.Node child : children) {
/* 1079 */         if (child.getWeight() > 0.0D) {
/* 1080 */           weightedChild = child;
/*      */         }
/*      */       }
/* 1083 */       return weightedChild;
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1087 */       int nChildren = getChildren().size();
/* 1088 */       StringBuffer sb = new StringBuffer("MultiSplitLayout.Split");
/* 1089 */       sb.append(isRowLayout() ? " ROW [" : " COLUMN [");
/* 1090 */       sb.append(new StringBuilder().append(nChildren).append(nChildren == 1 ? " child" : " children").toString());
/* 1091 */       sb.append("] ");
/* 1092 */       sb.append(getBounds());
/* 1093 */       return sb.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class Node
/*      */   {
/*  866 */     private MultiSplitLayout.Split parent = null;
/*  867 */     private Rectangle bounds = new Rectangle();
/*  868 */     private double weight = 0.0D;
/*      */ 
/*      */     public MultiSplitLayout.Split getParent()
/*      */     {
/*  877 */       return this.parent;
/*      */     }
/*      */ 
/*      */     public void setParent(MultiSplitLayout.Split parent)
/*      */     {
/*  889 */       this.parent = parent;
/*      */     }
/*      */ 
/*      */     public Rectangle getBounds()
/*      */     {
/*  899 */       return new Rectangle(this.bounds);
/*      */     }
/*      */ 
/*      */     public void setBounds(Rectangle bounds)
/*      */     {
/*  914 */       if (bounds == null) {
/*  915 */         throw new IllegalArgumentException("null bounds");
/*      */       }
/*  917 */       this.bounds = new Rectangle(bounds);
/*      */     }
/*      */ 
/*      */     public double getWeight()
/*      */     {
/*  929 */       return this.weight;
/*      */     }
/*      */ 
/*      */     public void setWeight(double weight)
/*      */     {
/*  948 */       if ((weight < 0.0D) || (weight > 1.0D)) {
/*  949 */         throw new IllegalArgumentException("invalid weight");
/*      */       }
/*  951 */       this.weight = weight;
/*      */     }
/*      */ 
/*      */     private Node siblingAtOffset(int offset) {
/*  955 */       MultiSplitLayout.Split parent = getParent();
/*  956 */       if (parent == null) {
/*  957 */         return null;
/*      */       }
/*  959 */       List siblings = parent.getChildren();
/*  960 */       int index = siblings.indexOf(this);
/*  961 */       if (index == -1) {
/*  962 */         return null;
/*      */       }
/*  964 */       index += offset;
/*  965 */       return (index > -1) && (index < siblings.size()) ? (Node)siblings.get(index) : null;
/*      */     }
/*      */ 
/*      */     public Node nextSibling()
/*      */     {
/*  980 */       return siblingAtOffset(1);
/*      */     }
/*      */ 
/*      */     public Node previousSibling()
/*      */     {
/*  994 */       return siblingAtOffset(-1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class InvalidLayoutException extends RuntimeException
/*      */   {
/*      */     private final MultiSplitLayout.Node node;
/*      */ 
/*      */     public InvalidLayoutException(String msg, MultiSplitLayout.Node node)
/*      */     {
/*  731 */       super();
/*  732 */       this.node = node;
/*      */     }
/*      */ 
/*      */     public MultiSplitLayout.Node getNode()
/*      */     {
/*  739 */       return this.node;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitLayout
 * JD-Core Version:    0.6.0
 */