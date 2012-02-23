/*    */ package com.dukascopy.dds2.greed.gui.component.chart.toolbar;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.ChartsFrame;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableButton;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JSeparator;
/*    */ 
/*    */ public abstract class DockUndockToolBar extends JPanel
/*    */ {
/* 24 */   public static final Dimension SEPARATOR_SIZE = new Dimension(2, 24);
/*    */   public static final int DEFAULT_COMPONENT_HEIGHT = 24;
/* 27 */   public static final Icon ON_TOP_ON = new ResizableIcon("toolbar_chart_pin_active.png");
/* 28 */   public static final Icon ON_TOP_OFF = new ResizableIcon("toolbar_chart_pin_inactive.png");
/* 29 */   public static final Icon dockChartIcon = new ResizableIcon("toolbar_chart_dock_active.png");
/* 30 */   public static final Icon unDockChartIcon = new ResizableIcon("toolbar_chart_undock.png");
/*    */   private JResizableButton pinButton;
/*    */   private JSeparator separator;
/* 35 */   private ChartsFrame chartframe = (ChartsFrame)GreedContext.get("charts.frame");
/*    */ 
/*    */   public JResizableButton getPinButton() {
/* 38 */     if (this.pinButton == null) {
/* 39 */       this.pinButton = new JResizableButton(getPinIcon(), ResizingManager.ComponentSize.TOLBAR_BTN_SIZE);
/* 40 */       this.pinButton.setToolTipText("item.pin.unpin");
/* 41 */       this.pinButton.addActionListener(new ActionListener() {
/*    */         public void actionPerformed(ActionEvent e) {
/* 43 */           boolean isOnTop = DockUndockToolBar.this.chartframe.isAlwaysOnTop();
/* 44 */           DockUndockToolBar.this.chartframe.setAlwaysOnTop(!isOnTop);
/* 45 */           DockUndockToolBar.this.chartframe.refreshPinBtns();
/*    */         } } );
/*    */     }
/* 49 */     return this.pinButton;
/*    */   }
/*    */ 
/*    */   public void refreshPinBtn() {
/* 53 */     this.pinButton.setIcon(getPinIcon());
/*    */   }
/*    */ 
/*    */   public void setPinButtonVisible(boolean aFlag) {
/* 57 */     if (GreedContext.IS_JCLIENT_INVOKED) {
/* 58 */       refreshPinBtn();
/* 59 */       getSeparator().setVisible(aFlag);
/* 60 */       getPinButton().setVisible(aFlag);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void addAlwaysOntopButton() {
/* 65 */     if (GreedContext.IS_JCLIENT_INVOKED) {
/* 66 */       add(getSeparator());
/* 67 */       add(Box.createHorizontalStrut(5));
/* 68 */       add(getPinButton());
/*    */     }
/*    */   }
/*    */ 
/*    */   private JSeparator getSeparator() {
/* 73 */     if (this.separator == null) {
/* 74 */       this.separator = new JSeparator(1);
/* 75 */       setSize(this.separator, SEPARATOR_SIZE);
/*    */     }
/* 77 */     return this.separator;
/*    */   }
/*    */ 
/*    */   protected Icon getPinIcon() {
/* 81 */     return ((ChartsFrame)GreedContext.get("charts.frame")).isAlwaysOnTop() ? ON_TOP_ON : ON_TOP_OFF;
/*    */   }
/*    */ 
/*    */   protected void setSize(JComponent component, Dimension size)
/*    */   {
/* 87 */     component.setPreferredSize(size);
/* 88 */     component.setMinimumSize(size);
/* 89 */     component.setMaximumSize(size);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.toolbar.DockUndockToolBar
 * JD-Core Version:    0.6.0
 */