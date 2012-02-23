/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.LayoutManager;
/*     */ import javax.swing.JInternalFrame;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public final class ChartsLayoutManager
/*     */   implements LayoutManager
/*     */ {
/*     */   private static final int COMPONENTS_MIN_HEIGHT = 1;
/*     */   public static final int CHART_VIEW_MIN_HEIGHT = 100;
/*     */   private static final int CHART_VIEW_ABS_MIN_HEIGHT = -65;
/*     */   private static final int CHART_TAB_VIEW_IS_SWITCHED = -29;
/*  17 */   private boolean firstLayout = true;
/*     */ 
/*     */   public void addLayoutComponent(String name, Component component)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void removeLayoutComponent(Component component)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Dimension preferredLayoutSize(Container target) {
/*  28 */     return target.getSize();
/*     */   }
/*     */ 
/*     */   public Dimension minimumLayoutSize(Container target) {
/*  32 */     return target.getSize();
/*     */   }
/*     */ 
/*     */   public void layoutContainer(Container container) {
/*  36 */     int count = container.getComponentCount();
/*     */ 
/*  38 */     if (count == 2)
/*  39 */       layoutMainChartViewAndCommonAxisXPanel(container, count);
/*     */     else
/*  41 */       layoutMainChartViewAndSubViewsAndCommonAxisXPanel(container, count);
/*     */   }
/*     */ 
/*     */   private void layoutMainChartViewAndSubViewsAndCommonAxisXPanel(Container container, int count)
/*     */   {
/*  48 */     JInternalFrame frame = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, container);
/*  49 */     if ((frame != null) && (!frame.isVisible()) && (!this.firstLayout)) {
/*  50 */       return;
/*     */     }
/*  52 */     this.firstLayout = false;
/*     */ 
/*  54 */     int heightWithoutMainChartView = container.getHeight();
/*     */ 
/*  56 */     for (int i = 0; i < count; i++) {
/*  57 */       Component component = container.getComponent(i);
/*  58 */       if (!"MainChartView".equalsIgnoreCase(component.getName())) {
/*  59 */         heightWithoutMainChartView = (int)(heightWithoutMainChartView - component.getSize().getHeight());
/*     */       }
/*     */     }
/*     */ 
/*  63 */     int curY = 0;
/*  64 */     int chartsCurHeight = 100;
/*  65 */     boolean isPusshed = false;
/*     */ 
/*  68 */     for (int i = 0; i < count; i++) {
/*  69 */       Component component = container.getComponent(i);
/*  70 */       if (!component.isVisible())
/*     */       {
/*     */         continue;
/*     */       }
/*  74 */       int componentsHeight = (int)component.getSize().getHeight();
/*     */ 
/*  76 */       if (((int)container.getSize().getHeight() != -29) && 
/*  77 */         ("MainChartView".equalsIgnoreCase(component.getName()))) {
/*  78 */         componentsHeight = heightWithoutMainChartView;
/*  79 */         if (componentsHeight < 100)
/*  80 */           componentsHeight = 100;
/*     */         else {
/*  82 */           chartsCurHeight = componentsHeight;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  87 */       if (("SubChartView".equalsIgnoreCase(component.getName())) && ((int)container.getSize().getHeight() != -29))
/*     */       {
/*  90 */         int compPrefSize = ((int)container.getSize().getHeight() - 100 - 12 - getSubChartPanelCount(count) * 3) / getSubChartPanelCount(count);
/*     */ 
/*  93 */         if ((!container.getSize().equals(new Dimension(0, 0))) && ((int)container.getSize().getHeight() - 100 < 1) && ((int)container.getSize().getHeight() > -65))
/*     */         {
/*  97 */           componentsHeight = 1;
/*     */         }
/*  99 */         else if (((int)container.getSize().getHeight() < 100 + getSubChartPanelCount(count) * 3 + 12) || ((componentsHeight == 1) && (chartsCurHeight > 100)))
/*     */         {
/* 106 */           componentsHeight = 100;
/*     */         }
/* 108 */         else if (heightWithoutMainChartView < getSubChartPanelCount(count) * 3 + 12)
/*     */         {
/* 111 */           componentsHeight = compPrefSize;
/*     */         }
/* 113 */         else if ((compPrefSize > componentsHeight) && (compPrefSize <= 100))
/*     */         {
/* 115 */           container.getComponent(0).setBounds(0, 0, (int)container.getSize().getWidth(), 100);
/* 116 */           componentsHeight = compPrefSize;
/*     */ 
/* 118 */           if (!isPusshed) {
/* 119 */             isPusshed = true;
/* 120 */             curY = curY - chartsCurHeight + 100;
/*     */           }
/* 122 */         } else if (compPrefSize < 100) {
/* 123 */           componentsHeight = compPrefSize;
/*     */         }
/*     */       }
/*     */ 
/* 127 */       component.setBounds(0, curY, (int)container.getSize().getWidth(), componentsHeight);
/* 128 */       curY += componentsHeight;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getSubChartPanelCount(int count) {
/* 133 */     return (count - 2) / 2;
/*     */   }
/*     */ 
/*     */   private void layoutMainChartViewAndCommonAxisXPanel(Container container, int count)
/*     */   {
/* 138 */     Component mainChartView = null;
/* 139 */     Component commonAxisXPanel = null;
/* 140 */     for (int i = 0; i < count; i++) {
/* 141 */       Component component = container.getComponent(i);
/* 142 */       if ("MainChartView".equalsIgnoreCase(component.getName()))
/* 143 */         mainChartView = component;
/* 144 */       else if ("CommonAxisXPanel".equalsIgnoreCase(component.getName())) {
/* 145 */         commonAxisXPanel = component;
/*     */       }
/*     */     }
/*     */ 
/* 149 */     if ((mainChartView == null) || (commonAxisXPanel == null)) {
/* 150 */       return;
/*     */     }
/*     */ 
/* 153 */     Dimension containerSize = container.getSize();
/* 154 */     double commonAxisXPanelHeight = commonAxisXPanel.getSize().getHeight();
/* 155 */     mainChartView.setBounds(0, 0, (int)containerSize.getWidth(), (int)(containerSize.getHeight() - commonAxisXPanelHeight));
/* 156 */     commonAxisXPanel.setBounds(0, (int)(containerSize.getHeight() - commonAxisXPanelHeight), (int)containerSize.getWidth(), (int)commonAxisXPanelHeight);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ChartsLayoutManager
 * JD-Core Version:    0.6.0
 */