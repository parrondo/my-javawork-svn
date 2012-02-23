/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.DockUndockToolBar;
/*    */ import java.awt.BorderLayout;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ class ToolBarAndDesktopPane extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private JComponent toolBar;
/*    */ 
/*    */   ToolBarAndDesktopPane()
/*    */   {
/* 17 */     super(new BorderLayout());
/*    */   }
/*    */ 
/*    */   public void removeCurrentToolBar() {
/* 21 */     if (this.toolBar != null) {
/* 22 */       remove(this.toolBar);
/*    */     }
/* 24 */     this.toolBar = null;
/*    */   }
/*    */ 
/*    */   public void setCurrentToolBar(DockUndockToolBar toolBar) {
/* 28 */     this.toolBar = toolBar;
/* 29 */     add(toolBar, "First");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ToolBarAndDesktopPane
 * JD-Core Version:    0.6.0
 */