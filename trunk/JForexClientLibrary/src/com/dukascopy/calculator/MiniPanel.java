/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Polygon;
/*    */ import java.awt.RenderingHints;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.border.EmptyBorder;
/*    */ 
/*    */ public class MiniPanel extends JPanel
/*    */ {
/*    */   private boolean illuminated;
/*    */   private final boolean left;
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public MiniPanel(boolean left)
/*    */   {
/* 15 */     this.left = left;
/* 16 */     setBorder(new EmptyBorder(0, 0, 0, 0));
/* 17 */     setIlluminated(false);
/*    */   }
/*    */ 
/*    */   public void paintComponent(Graphics graphics)
/*    */   {
/* 25 */     Graphics2D g = (Graphics2D)graphics;
/* 26 */     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */ 
/* 28 */     graphics.clearRect(0, 0, getWidth(), getHeight());
/* 29 */     if (this.illuminated) {
/* 30 */       int l = 0;
/* 31 */       int r = 0;
/* 32 */       int w = (int)(0.8D * getWidth() + 0.5D);
/* 33 */       int c = (int)(0.8D * getHeight() + 0.5D);
/* 34 */       if (this.left) {
/* 35 */         r = (int)((getWidth() - w) * 0.5D + 0.5D);
/* 36 */         l = r + w;
/*    */       } else {
/* 38 */         l = (int)((getWidth() - w) * 0.5D + 0.5D);
/* 39 */         r = l + w;
/*    */       }
/* 41 */       g.setPaint(Color.gray);
/* 42 */       Polygon triangle = new Polygon();
/* 43 */       triangle.addPoint(l, c);
/* 44 */       triangle.addPoint(l, c + w);
/* 45 */       triangle.addPoint(r, c);
/* 46 */       triangle.addPoint(l, c - w);
/* 47 */       g.fill(triangle);
/*    */     }
/*    */   }
/*    */ 
/*    */   void setIlluminated(boolean illuminated)
/*    */   {
/* 62 */     this.illuminated = illuminated;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.MiniPanel
 * JD-Core Version:    0.6.0
 */