/*    */ package com.dukascopy.dds2.greed.gui.component.connect;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.Icon;
/*    */ 
/*    */ public class DisconnectedIcon
/*    */   implements Icon
/*    */ {
/* 14 */   private static final Color OUTER = new Color(120, 0, 0);
/* 15 */   private static final Color INNER = new Color(220, 0, 0);
/*    */   private static final int WIDTH = 10;
/*    */   private static final int HEIGHT = 10;
/*    */ 
/*    */   public int getIconHeight()
/*    */   {
/* 21 */     return 10;
/*    */   }
/*    */ 
/*    */   public int getIconWidth() {
/* 25 */     return 10;
/*    */   }
/*    */ 
/*    */   public void paintIcon(Component c, Graphics g, int x, int y) {
/* 29 */     g.setColor(OUTER);
/* 30 */     g.drawRect(x, y, 10, 10);
/* 31 */     g.setColor(INNER);
/* 32 */     g.fillRect(x + 1, y + 1, 9, 9);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.connect.DisconnectedIcon
 * JD-Core Version:    0.6.0
 */