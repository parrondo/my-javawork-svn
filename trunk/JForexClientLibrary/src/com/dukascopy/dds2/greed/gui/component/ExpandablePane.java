/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.ComponentListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JSplitPane;
/*     */ 
/*     */ public abstract class ExpandablePane extends JSplitPane
/*     */ {
/*     */   protected static final int ARBITRARY_TOLERANCE = 15;
/*     */   private double splitPos;
/*     */   private double collapsedDivPos;
/*     */   private boolean expanded;
/*     */   private int divSize;
/*     */   private boolean relocate;
/*     */   private boolean isResizable;
/*     */ 
/*     */   public ExpandablePane(String name, int orientation, Component mainComponent, Component sideComponent, double splitPos, int divSize, double collapsedDivPos, boolean expand, boolean resizable)
/*     */   {
/*  32 */     super(orientation, mainComponent, sideComponent);
/*     */ 
/*  34 */     this.divSize = divSize;
/*  35 */     this.collapsedDivPos = collapsedDivPos;
/*  36 */     this.splitPos = splitPos;
/*  37 */     this.isResizable = resizable;
/*     */ 
/*  39 */     setName(name);
/*  40 */     setOneTouchExpandable(false);
/*  41 */     setResizeWeight(1.0D);
/*  42 */     setContinuousLayout(true);
/*  43 */     setBorder(BorderFactory.createEmptyBorder());
/*     */ 
/*  45 */     addPropertyChangeListener(new Listener(this));
/*  46 */     addComponentListener(new ResizeListener(this));
/*     */ 
/*  48 */     if (expand) {
/*  49 */       this.expanded = true;
/*  50 */       setDividerSize(divSize);
/*  51 */       setDividerLocation(splitPos);
/*     */     } else {
/*  53 */       this.expanded = false;
/*  54 */       setDividerSize(0);
/*  55 */       sideComponent.setVisible(false);
/*  56 */       setDividerLocation(collapsedDivPos);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isResizable() {
/*  61 */     return this.isResizable;
/*     */   }
/*     */ 
/*     */   public boolean isExpanded() {
/*  65 */     return this.expanded;
/*     */   }
/*     */ 
/*     */   public double getStaticDivider() {
/*  69 */     if (this.expanded) {
/*  70 */       return this.splitPos;
/*     */     }
/*  72 */     return this.collapsedDivPos;
/*     */   }
/*     */ 
/*     */   public void expand()
/*     */   {
/*  77 */     this.expanded = true;
/*  78 */     this.relocate = true;
/*  79 */     setDividerSize(this.divSize);
/*  80 */     setDividerLocation(this.splitPos);
/*  81 */     this.rightComponent.setVisible(true);
/*     */   }
/*     */ 
/*     */   public void collapse()
/*     */   {
/*  86 */     this.expanded = false;
/*  87 */     this.relocate = true;
/*  88 */     setDividerSize(0);
/*  89 */     setDividerLocation(this.collapsedDivPos);
/*  90 */     this.rightComponent.setVisible(false);
/*     */   }
/*     */ 
/*     */   public void toggleExanded()
/*     */   {
/*  95 */     if (this.expanded)
/*  96 */       collapse();
/*     */     else
/*  98 */       expand();
/*     */   }
/*     */ 
/*     */   public void setDividerLocation(int location)
/*     */   {
/* 104 */     super.setDividerLocation(location);
/*     */   }
/*     */ 
/*     */   class ResizeListener
/*     */     implements ComponentListener
/*     */   {
/*     */     ExpandablePane splitPane;
/*     */ 
/*     */     ResizeListener(ExpandablePane splitPane)
/*     */     {
/* 180 */       this.splitPane = splitPane;
/*     */     }
/*     */ 
/*     */     public void componentResized(ComponentEvent e)
/*     */     {
/* 185 */       if (!this.splitPane.isResizable())
/* 186 */         this.splitPane.setDividerLocation(this.splitPane.getStaticDivider());
/*     */     }
/*     */ 
/*     */     public void componentMoved(ComponentEvent e)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void componentShown(ComponentEvent e)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void componentHidden(ComponentEvent e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   class Listener
/*     */     implements PropertyChangeListener
/*     */   {
/*     */     private boolean inSplitPaneResized;
/*     */     private ExpandablePane splitPane;
/* 111 */     private double prevLocation = -1.0D;
/*     */     private static final double MIN_BOTTOM_PROPORTION = 0.8D;
/*     */     private static final double MIN_TOP_PROPORTION = 0.06D;
/*     */ 
/*     */     public Listener(ExpandablePane splitPane)
/*     */     {
/* 117 */       this.splitPane = splitPane;
/*     */     }
/*     */ 
/*     */     public void propertyChange(PropertyChangeEvent evt) {
/* 121 */       splitPaneResized(evt);
/*     */     }
/*     */ 
/*     */     private void splitPaneResized(PropertyChangeEvent evt)
/*     */     {
/* 126 */       if (this.inSplitPaneResized) {
/* 127 */         return;
/*     */       }
/* 129 */       this.inSplitPaneResized = true;
/*     */ 
/* 131 */       if (evt.getPropertyName().equalsIgnoreCase("dividerLocation")) {
/* 132 */         if (!ExpandablePane.this.expanded)
/*     */         {
/* 134 */           this.splitPane.setDividerLocation(ExpandablePane.this.collapsedDivPos);
/*     */         }
/* 136 */         else if (ExpandablePane.this.isResizable)
/*     */         {
/* 139 */           if (!ExpandablePane.this.relocate)
/*     */           {
/* 141 */             int current = this.splitPane.getDividerLocation();
/* 142 */             int max = this.splitPane.getMaximumDividerLocation();
/*     */ 
/* 144 */             if (current / max > 0.8D) {
/* 145 */               if (this.prevLocation < current) {
/* 146 */                 this.splitPane.setDividerLocation(1.0D);
/*     */               }
/* 148 */               if (this.prevLocation > current) {
/* 149 */                 this.splitPane.setDividerLocation(0.8D);
/*     */               }
/*     */             }
/* 152 */             if (current / max < 0.06D) {
/* 153 */               if (this.prevLocation > current) {
/* 154 */                 this.splitPane.setDividerLocation(0.0D);
/*     */               }
/* 156 */               if (this.prevLocation < current) {
/* 157 */                 this.splitPane.setDividerLocation(0.06D);
/*     */               }
/*     */             }
/* 160 */             this.prevLocation = current;
/*     */           }
/*     */           else
/*     */           {
/* 164 */             ExpandablePane.access$302(ExpandablePane.this, false);
/*     */           }
/*     */         }
/* 167 */         else this.splitPane.setDividerLocation(this.splitPane.getStaticDivider());
/*     */ 
/*     */       }
/*     */ 
/* 171 */       this.inSplitPaneResized = false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ExpandablePane
 * JD-Core Version:    0.6.0
 */