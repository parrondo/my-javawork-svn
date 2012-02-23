/*    */ package com.dukascopy.dds2.greed.gui.component.chart.buttons;
/*    */ 
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JPopupMenu;
/*    */ 
/*    */ public class ButtonMouseListener extends MouseAdapter
/*    */ {
/*    */   private JButton jButton;
/*    */   private JPopupMenu popupMenu;
/*    */   private MouseAdapter mouseAdapter;
/*    */ 
/*    */   public ButtonMouseListener(JButton drawingsJButton, JPopupMenu drawingsPopupMenu, MouseAdapter mouseAdapter)
/*    */   {
/* 16 */     this.jButton = drawingsJButton;
/* 17 */     this.popupMenu = drawingsPopupMenu;
/* 18 */     this.mouseAdapter = mouseAdapter;
/*    */   }
/*    */ 
/*    */   public ButtonMouseListener(JButton drawingsJButton, MouseAdapter mouseAdapter) {
/* 22 */     this(drawingsJButton, null, mouseAdapter);
/*    */   }
/*    */ 
/*    */   public ButtonMouseListener(JButton drawingsJButton, JPopupMenu drawingsPopupMenu) {
/* 26 */     this(drawingsJButton, drawingsPopupMenu, null);
/*    */   }
/*    */ 
/*    */   public void mouseReleased(MouseEvent e) {
/* 30 */     if (this.mouseAdapter == null) {
/* 31 */       process();
/*    */     }
/*    */     else
/* 34 */       this.mouseAdapter.mouseReleased(e);
/*    */   }
/*    */ 
/*    */   private void process()
/*    */   {
/* 39 */     if (!this.jButton.isEnabled()) {
/* 40 */       return;
/*    */     }
/* 42 */     this.popupMenu.show(this.jButton, 0, this.jButton.getHeight());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.buttons.ButtonMouseListener
 * JD-Core Version:    0.6.0
 */