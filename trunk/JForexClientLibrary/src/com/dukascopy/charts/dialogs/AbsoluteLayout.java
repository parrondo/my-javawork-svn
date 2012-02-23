/*     */ package com.dukascopy.charts.dialogs;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager2;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AbsoluteLayout
/*     */   implements LayoutManager2, Serializable
/*     */ {
/*  10 */   static final AbsoluteLayoutConstraints defaultConstraints = new AbsoluteLayoutConstraints();
/*     */ 
/*  12 */   int width = 0;
/*  13 */   int height = 0;
/*     */ 
/*  15 */   Map<Component, Object> info = new HashMap();
/*     */ 
/*     */   public AbsoluteLayout()
/*     */   {
/*     */   }
/*     */ 
/*     */   public AbsoluteLayout(int width, int height)
/*     */   {
/*  23 */     this.width = width;
/*  24 */     this.height = height;
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/*  28 */     return this.width;
/*     */   }
/*     */ 
/*     */   public void setWidth(int width) {
/*  32 */     this.width = width;
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/*  36 */     return this.height;
/*     */   }
/*     */ 
/*     */   public void setHeight(int height) {
/*  40 */     this.height = height;
/*     */   }
/*     */ 
/*     */   public void addLayoutComponent(String name, Component component) {
/*     */   }
/*     */ 
/*     */   public void removeLayoutComponent(Component component) {
/*  47 */     this.info.remove(component);
/*     */   }
/*     */ 
/*     */   public Dimension preferredLayoutSize(Container target) {
/*  51 */     return getLayoutSize(target, true);
/*     */   }
/*     */ 
/*     */   public Dimension minimumLayoutSize(Container target) {
/*  55 */     return getLayoutSize(target, false);
/*     */   }
/*     */ 
/*     */   public void layoutContainer(Container target) {
/*  59 */     Insets insets = target.getInsets();
/*  60 */     int count = target.getComponentCount();
/*  61 */     for (int i = 0; i < count; i++) {
/*  62 */       Component component = target.getComponent(i);
/*  63 */       if (component.isVisible()) {
/*  64 */         Rectangle r = getComponentBounds(component, true);
/*  65 */         component.setBounds(insets.left + r.x, insets.top + r.y, r.width, r.height);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addLayoutComponent(Component component, Object constraints) {
/*  71 */     if ((constraints instanceof AbsoluteLayoutConstraints))
/*  72 */       this.info.put(component, constraints);
/*     */   }
/*     */ 
/*     */   public Dimension maximumLayoutSize(Container target)
/*     */   {
/*  78 */     return new Dimension(2147483647, 2147483647);
/*     */   }
/*     */ 
/*     */   public float getLayoutAlignmentX(Container target) {
/*  82 */     return 0.5F;
/*     */   }
/*     */ 
/*     */   public float getLayoutAlignmentY(Container target) {
/*  86 */     return 0.5F;
/*     */   }
/*     */ 
/*     */   public void invalidateLayout(Container target) {
/*     */   }
/*     */ 
/*     */   Dimension getLayoutSize(Container target, boolean doPreferred) {
/*  93 */     Dimension dim = new Dimension(0, 0);
/*  94 */     if ((this.width <= 0) || (this.height <= 0)) {
/*  95 */       int count = target.getComponentCount();
/*  96 */       for (int i = 0; i < count; i++) {
/*  97 */         Component component = target.getComponent(i);
/*  98 */         if (component.isVisible()) {
/*  99 */           Rectangle r = getComponentBounds(component, doPreferred);
/* 100 */           dim.width = Math.max(dim.width, r.x + r.width);
/* 101 */           dim.height = Math.max(dim.height, r.y + r.height);
/*     */         }
/*     */       }
/*     */     }
/* 105 */     if (this.width > 0)
/* 106 */       dim.width = this.width;
/* 107 */     if (this.height > 0)
/* 108 */       dim.height = this.height;
/* 109 */     Insets insets = target.getInsets();
/* 110 */     dim.width += insets.left + insets.right;
/* 111 */     dim.height += insets.top + insets.bottom;
/* 112 */     return dim;
/*     */   }
/*     */ 
/*     */   Rectangle getComponentBounds(Component component, boolean doPreferred) {
/* 116 */     AbsoluteLayoutConstraints constraints = (AbsoluteLayoutConstraints)this.info.get(component);
/* 117 */     if (constraints == null)
/* 118 */       constraints = defaultConstraints;
/* 119 */     Rectangle r = new Rectangle(constraints.x, constraints.y, constraints.width, constraints.height);
/* 120 */     if ((r.width <= 0) || (r.height <= 0)) {
/* 121 */       Dimension d = doPreferred ? component.getPreferredSize() : component.getMinimumSize();
/* 122 */       if (r.width <= 0)
/* 123 */         r.width = d.width;
/* 124 */       if (r.height <= 0)
/* 125 */         r.height = d.height;
/*     */     }
/* 127 */     return r;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.AbsoluteLayout
 * JD-Core Version:    0.6.0
 */