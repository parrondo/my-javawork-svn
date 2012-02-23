/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import javax.swing.border.AbstractBorder;
/*    */ 
/*    */ public class RoundedBorder extends AbstractBorder
/*    */ {
/*    */   private static final int ARC_HEIGHT = 7;
/*    */ 
/*    */   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
/*    */   {
/* 17 */     Graphics2D g2d = (Graphics2D)g;
/*    */ 
/* 19 */     g2d.setColor(CommonUIConstants.TOOLBAR_OUTER_BORDER_COLOR);
/* 20 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 21 */     g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/* 22 */     g2d.drawRoundRect(x, y, width - 2, height - 2, 7, 7);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.RoundedBorder
 * JD-Core Version:    0.6.0
 */