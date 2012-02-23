/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.border.EmptyBorder;
/*    */ 
/*    */ public class WhitePanel extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public WhitePanel()
/*    */   {
/*  7 */     setBorder(new EmptyBorder(0, 0, 0, 0));
/*    */   }
/*    */ 
/*    */   public void paintComponent(Graphics graphics) {
/* 11 */     super.paintComponent(graphics);
/* 12 */     graphics.setColor(Color.WHITE);
/* 13 */     graphics.fillRect(0, 0, getWidth(), getHeight());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.WhitePanel
 * JD-Core Version:    0.6.0
 */