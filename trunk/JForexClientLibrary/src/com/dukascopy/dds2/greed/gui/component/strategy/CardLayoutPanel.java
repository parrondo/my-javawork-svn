/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import java.awt.CardLayout;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class CardLayoutPanel extends JPanel
/*    */ {
/*    */   public CardLayoutPanel()
/*    */   {
/* 15 */     setLayout(new CardLayout());
/*    */   }
/*    */ 
/*    */   public void showComponent(String constraint)
/*    */   {
/* 23 */     CardLayout layout = (CardLayout)getLayout();
/* 24 */     layout.show(this, constraint);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.CardLayoutPanel
 * JD-Core Version:    0.6.0
 */