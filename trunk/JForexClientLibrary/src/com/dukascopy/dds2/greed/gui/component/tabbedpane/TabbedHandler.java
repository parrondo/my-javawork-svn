/*     */ package com.dukascopy.dds2.greed.gui.component.tabbedpane;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.List;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.event.MouseInputAdapter;
/*     */ import javax.swing.event.MouseInputListener;
/*     */ import javax.swing.plaf.TabbedPaneUI;
/*     */ 
/*     */ public class TabbedHandler
/*     */ {
/*     */   protected JTabbedPane tPane;
/*     */   private List contentPanes;
/*     */   private MouseInputListener mouseHandler;
/*     */   private Cursor defaultCursor;
/*     */   private Cursor handCursor;
/*     */ 
/*     */   public TabbedHandler()
/*     */   {
/*  27 */     this.tPane = new JTabbedPane();
/*  28 */     this.contentPanes = null;
/*  29 */     this.mouseHandler = new MouseHandler();
/*  30 */     setMouseHandler();
/*     */   }
/*     */   public TabbedHandler(JTabbedPane tPane) {
/*  33 */     this(tPane, null);
/*     */   }
/*     */   public TabbedHandler(JTabbedPane tPane, List contentPanes) {
/*  36 */     this.tPane = tPane;
/*  37 */     this.contentPanes = contentPanes;
/*  38 */     this.mouseHandler = new MouseHandler();
/*  39 */     setMouseHandler();
/*     */   }
/*     */   public JTabbedPane getTabbedPane() {
/*  42 */     return this.tPane;
/*     */   }
/*     */ 
/*     */   public void setTabbedPane(JTabbedPane tPane) {
/*  46 */     this.tPane = tPane;
/*  47 */     setMouseHandler();
/*     */   }
/*     */   public void setMouseHandler() {
/*  50 */     this.tPane.addMouseListener(this.mouseHandler);
/*  51 */     this.tPane.addMouseMotionListener(this.mouseHandler);
/*     */   }
/*     */ 
/*     */   private void dragTab(int dragIndex, int tabIndex) {
/*  55 */     String title = this.tPane.getTitleAt(dragIndex);
/*  56 */     Icon icon = this.tPane.getIconAt(dragIndex);
/*  57 */     Component component = this.tPane.getComponentAt(dragIndex);
/*  58 */     String toolTipText = this.tPane.getToolTipTextAt(dragIndex);
/*     */ 
/*  60 */     Color background = this.tPane.getBackgroundAt(dragIndex);
/*  61 */     Color foreground = this.tPane.getForegroundAt(dragIndex);
/*  62 */     Icon disabledIcon = this.tPane.getDisabledIconAt(dragIndex);
/*  63 */     int mnemonic = this.tPane.getMnemonicAt(dragIndex);
/*  64 */     int displayedMnemonicIndex = this.tPane.getDisplayedMnemonicIndexAt(dragIndex);
/*  65 */     boolean enabled = this.tPane.isEnabledAt(dragIndex);
/*     */ 
/*  67 */     this.tPane.remove(dragIndex);
/*  68 */     this.tPane.insertTab(title, icon, component, toolTipText, tabIndex);
/*     */ 
/*  70 */     this.tPane.setBackgroundAt(tabIndex, background);
/*  71 */     this.tPane.setForegroundAt(tabIndex, foreground);
/*  72 */     this.tPane.setDisabledIconAt(tabIndex, disabledIcon);
/*  73 */     this.tPane.setMnemonicAt(tabIndex, mnemonic);
/*  74 */     this.tPane.setDisplayedMnemonicIndexAt(tabIndex, displayedMnemonicIndex);
/*  75 */     this.tPane.setEnabledAt(tabIndex, enabled);
/*     */ 
/*  77 */     if (this.contentPanes != null) {
/*  78 */       Object obj = this.contentPanes.get(dragIndex);
/*  79 */       this.contentPanes.set(dragIndex, this.contentPanes.get(tabIndex));
/*  80 */       this.contentPanes.set(tabIndex, obj);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Cursor getDefaultCursor() {
/*  85 */     if (this.defaultCursor == null)
/*     */     {
/*  87 */       this.defaultCursor = Cursor.getDefaultCursor();
/*     */     }
/*     */ 
/*  90 */     return this.defaultCursor;
/*     */   }
/*     */ 
/*     */   private Cursor getHandCursor()
/*     */   {
/*  95 */     if (this.handCursor == null)
/*     */     {
/*  97 */       this.handCursor = Cursor.getPredefinedCursor(12);
/*     */     }
/*     */ 
/* 100 */     return this.handCursor;
/*     */   }
/*     */ 
/*     */   private int getTabIndex(int x, int y)
/*     */   {
/* 105 */     return this.tPane.getUI().tabForCoordinate(this.tPane, x, y);
/*     */   }
/*     */ 
/*     */   private void maybeSetDefaultCursor()
/*     */   {
/* 110 */     Cursor cursor = getDefaultCursor();
/*     */ 
/* 112 */     if (this.tPane.getCursor() != cursor)
/*     */     {
/* 114 */       this.tPane.setCursor(cursor);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void maybeSetHandCursor()
/*     */   {
/* 120 */     Cursor cursor = getHandCursor();
/*     */ 
/* 122 */     if (this.tPane.getCursor() != cursor)
/*     */     {
/* 124 */       this.tPane.setCursor(cursor);
/*     */     }
/*     */   }
/*     */ 
/*     */   class MouseHandler extends MouseInputAdapter {
/* 129 */     private int dragIndex = -1;
/*     */ 
/*     */     MouseHandler() {  }
/*     */ 
/* 132 */     public void mouseDragged(MouseEvent e) { JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
/* 133 */       int index = TabbedHandler.this.getTabIndex(e.getX(), e.getY());
/* 134 */       if (this.dragIndex != -1)
/* 135 */         if (index != -1)
/* 136 */           TabbedHandler.this.maybeSetHandCursor();
/*     */         else
/* 138 */           TabbedHandler.this.maybeSetDefaultCursor();
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent e)
/*     */     {
/* 144 */       if ((!e.isPopupTrigger()) && (e.getButton() == 1)) {
/* 145 */         int tabIndex = TabbedHandler.this.getTabIndex(e.getX(), e.getY());
/* 146 */         if (tabIndex != -1)
/* 147 */           this.dragIndex = tabIndex;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent e)
/*     */     {
/* 153 */       if ((!e.isPopupTrigger()) && (e.getButton() == 1) && 
/* 154 */         (this.dragIndex != -1)) {
/* 155 */         int tabIndex = TabbedHandler.this.getTabIndex(e.getX(), e.getY());
/* 156 */         if ((tabIndex != -1) && (tabIndex != this.dragIndex)) {
/* 157 */           TabbedHandler.this.dragTab(this.dragIndex, tabIndex);
/* 158 */           TabbedHandler.this.tPane.setSelectedIndex(tabIndex);
/*     */         }
/* 160 */         this.dragIndex = -1;
/* 161 */         TabbedHandler.this.maybeSetDefaultCursor();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tabbedpane.TabbedHandler
 * JD-Core Version:    0.6.0
 */