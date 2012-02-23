/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ 
/*     */ public class ResizableGridLayoutPanel extends JPanel
/*     */ {
/*     */   public static final int DEFAULT_HGAP = 1;
/*     */   public static final int DEFAULT_VGAP = 1;
/*  27 */   public static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
/*     */   private List<Component> items;
/*     */   private Insets panelInsets;
/*     */   private int hgap;
/*     */ 
/*     */   public ResizableGridLayoutPanel()
/*     */   {
/*  35 */     this(DEFAULT_INSETS);
/*     */   }
/*     */ 
/*     */   public ResizableGridLayoutPanel(Insets insets) {
/*  39 */     this(insets, 1, 1);
/*     */   }
/*     */ 
/*     */   public ResizableGridLayoutPanel(Insets insets, int hgap, int vgap)
/*     */   {
/*  44 */     this.panelInsets = insets;
/*  45 */     this.hgap = hgap;
/*     */ 
/*  47 */     setLayout(new GridLayout(0, 1, 0, vgap));
/*  48 */     setBorder(new EmptyBorder(this.panelInsets));
/*     */ 
/*  50 */     addComponentListener(new ComponentAdapter()
/*     */     {
/*     */       public void componentResized(ComponentEvent e) {
/*  53 */         ResizableGridLayoutPanel.this.doItemsLayout();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void setItems(List<Component> items) {
/*  59 */     this.items = items;
/*     */ 
/*  61 */     doItemsLayout();
/*     */   }
/*     */ 
/*     */   public List<Component> getItems() {
/*  65 */     return this.items;
/*     */   }
/*     */ 
/*     */   public int getItemsCount() {
/*  69 */     int ret = 0;
/*  70 */     if (this.items != null) {
/*  71 */       ret = this.items.size();
/*     */     }
/*  73 */     return ret;
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  77 */     if (this.items != null) {
/*  78 */       this.items.clear();
/*     */     }
/*  80 */     removeAll();
/*     */   }
/*     */ 
/*     */   private void doItemsLayout()
/*     */   {
/*  85 */     int panelWidth = (int)getSize().getWidth() - this.panelInsets.left - this.panelInsets.right;
/*     */ 
/*  87 */     if ((panelWidth > 0) && (this.items != null))
/*     */     {
/*  89 */       removeAll();
/*     */ 
/*  91 */       List rows = createRows(panelWidth);
/*  92 */       ((GridLayout)getLayout()).setRows(rows.size());
/*     */ 
/*  94 */       for (JPanel row : rows) {
/*  95 */         add(row);
/*     */       }
/*     */     }
/*     */ 
/*  99 */     revalidate();
/*     */   }
/*     */ 
/*     */   private List<JPanel> createRows(int panelWidth) {
/* 103 */     List rows = new ArrayList();
/*     */ 
/* 105 */     JPanel panel = new JPanel(new FlowLayout(0, this.hgap, 0));
/*     */ 
/* 107 */     int filledWidth = 0;
/*     */ 
/* 109 */     for (Component item : this.items)
/*     */     {
/* 111 */       int itemWidth = (int)item.getPreferredSize().getWidth() + this.hgap;
/*     */ 
/* 113 */       if (filledWidth + itemWidth > panelWidth)
/*     */       {
/* 115 */         filledWidth = 0;
/* 116 */         rows.add(panel);
/*     */ 
/* 118 */         panel = new JPanel(new FlowLayout(0, this.hgap, 0));
/*     */       }
/*     */ 
/* 121 */       panel.add(item);
/* 122 */       filledWidth += itemWidth;
/*     */     }
/* 124 */     rows.add(panel);
/*     */ 
/* 126 */     return rows;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.ResizableGridLayoutPanel
 * JD-Core Version:    0.6.0
 */