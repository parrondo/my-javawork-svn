/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.Switchable;
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.geom.Line2D;
/*    */ import java.awt.geom.Line2D.Float;
/*    */ import java.awt.geom.Rectangle2D;
/*    */ import java.awt.geom.Rectangle2D.Float;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class JRoundedBorderSwitch extends JRoundedBorder
/*    */   implements Switchable
/*    */ {
/* 26 */   private boolean isOn = true;
/* 27 */   private boolean isHidable = false;
/*    */ 
/*    */   public JRoundedBorderSwitch(JComponent parent) {
/* 30 */     super(parent);
/*    */   }
/*    */ 
/*    */   public JRoundedBorderSwitch(JComponent parent, String text, int topInset, int leftInset, int bottomInset, int rightInset)
/*    */   {
/* 39 */     super(parent, text, topInset, leftInset, bottomInset, rightInset);
/*    */   }
/*    */ 
/*    */   public JRoundedBorderSwitch(JComponent parent, String text, boolean isHidable) {
/* 43 */     super(parent, text);
/* 44 */     this.isHidable = isHidable;
/*    */   }
/*    */ 
/*    */   public JRoundedBorderSwitch(JComponent parent, String text) {
/* 48 */     super(parent, text);
/*    */   }
/*    */ 
/*    */   public void setSwitch(boolean isOn)
/*    */   {
/* 53 */     this.isOn = isOn;
/*    */   }
/*    */ 
/*    */   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
/*    */   {
/* 58 */     super.paintBorder(c, g, x, y, width, height);
/* 59 */     if (this.isHidable) drawRect(g); 
/*    */   }
/*    */ 
/*    */   private void drawRect(Graphics g)
/*    */   {
/* 63 */     Graphics2D g2 = (Graphics2D)g;
/* 64 */     float startX = this.relativeWidth - 10;
/* 65 */     float startY = this.topBorder - 4;
/* 66 */     float width = 8.0F;
/* 67 */     float height = 8.0F;
/* 68 */     float lineTicknes = 1.0F;
/*    */ 
/* 70 */     Rectangle2D rect = new Rectangle2D.Float(startX, startY, width, height);
/* 71 */     g2.fill(rect);
/* 72 */     g2.setColor(Color.GRAY);
/* 73 */     g2.setStroke(new BasicStroke(lineTicknes));
/* 74 */     g2.draw(rect);
/*    */ 
/* 76 */     Line2D hLine = new Line2D.Float(startX + width / 4.0F, startY + height / 2.0F, startX + width - width / 4.0F, startY + height / 2.0F); g2.draw(hLine);
/* 77 */     Line2D vLine = new Line2D.Float(startX + width / 2.0F, startY + height / 4.0F, startX + width / 2.0F, startY + height - height / 4.0F);
/*    */ 
/* 79 */     g2.draw(hLine);
/* 80 */     if (!this.isOn) g2.draw(vLine);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.JRoundedBorderSwitch
 * JD-Core Version:    0.6.0
 */