/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.border.Border;
/*    */ 
/*    */ public class ButtonMouseListener extends MouseAdapter
/*    */ {
/* 16 */   private Border roundedBorder = new RoundedBorder();
/*    */   protected final JButton button;
/*    */ 
/*    */   public ButtonMouseListener(JButton button)
/*    */   {
/* 21 */     this.button = button;
/* 22 */     button.setBorderPainted(true);
/*    */   }
/*    */ 
/*    */   public void mousePressed(MouseEvent e)
/*    */   {
/* 27 */     if (this.button.isEnabled())
/* 28 */       this.button.setBorder(this.roundedBorder);
/*    */   }
/*    */ 
/*    */   public void mouseReleased(MouseEvent e)
/*    */   {
/* 34 */     if (this.button.isEnabled())
/* 35 */       this.button.setBorder(this.roundedBorder);
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent e)
/*    */   {
/* 41 */     if (this.button.isEnabled())
/* 42 */       this.button.setBorder(this.roundedBorder);
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent e)
/*    */   {
/* 48 */     this.button.setBorder(BorderFactory.createEmptyBorder());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.ButtonMouseListener
 * JD-Core Version:    0.6.0
 */