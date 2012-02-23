/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class ExtraPanel extends JPanel
/*    */ {
/*    */   private ReadOnlyDisplayPanel panel;
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ExtraPanel(ReadOnlyDisplayPanel panel)
/*    */   {
/* 18 */     this.panel = panel;
/*    */   }
/*    */ 
/*    */   public void paintComponent(Graphics graphics)
/*    */   {
/* 28 */     Graphics2D g2 = (Graphics2D)graphics;
/* 29 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */ 
/* 31 */     graphics.setFont(getFont().deriveFont(getPanel().getApplet().extraTextSize()));
/* 32 */     int fontHeight = (int)Math.ceil(getPanel().getApplet().extraTextSize());
/* 33 */     int x = 0;
/* 34 */     int xx = getWidth() - graphics.getFontMetrics().stringWidth("Hex ");
/* 35 */     int y = getHeight() - (int)(fontHeight * 0.5D);
/* 36 */     if (getPanel().getOn()) {
/* 37 */       if (getPanel().getApplet().getNotation().nonComplex()) {
/* 38 */         graphics.drawString(getPanel().getApplet().getAngleType().toString(), x, y);
/*    */       } else {
/* 40 */         if (getPanel().getApplet().getAngleType() == AngleType.DEGREES)
/* 41 */           graphics.drawString("Deg", x, y);
/*    */         else
/* 43 */           graphics.drawString("Rad", x, y);
/* 44 */         graphics.drawString("Cplx", xx, y);
/*    */       }
/* 46 */       y = getHeight() - (int)(fontHeight * 2.0D);
/* 47 */       if (getPanel().getApplet().getNotation().scientific())
/* 48 */         graphics.drawString("Sci", xx, y);
/* 49 */       if (getPanel().getApplet().getStat())
/* 50 */         graphics.drawString("Stat", x, y);
/* 51 */       y = getHeight() - (int)(fontHeight * 3.5D);
/* 52 */       if (!getPanel().getApplet().getMemory().isZero())
/* 53 */         graphics.drawString("Mem", x, y);
/* 54 */       switch (1.$SwitchMap$com$dukascopy$calculator$Base[getPanel().getApplet().getBase().ordinal()]) {
/*    */       case 1:
/* 56 */         graphics.drawString("Hex", xx, y);
/* 57 */         break;
/*    */       case 2:
/* 59 */         graphics.drawString("Oct", xx, y);
/* 60 */         break;
/*    */       case 3:
/* 62 */         graphics.drawString("Bin", xx, y);
/*    */       }
/*    */ 
/* 65 */       y = getHeight() - (int)(fontHeight * 5.0D);
/* 66 */       if (getPanel().getApplet().getShift())
/* 67 */         graphics.drawString("Shift", x, y);
/* 68 */       if (getPanel().getApplet().getNotation().polar())
/* 69 */         graphics.drawString("Pol", xx, y);
/*    */     }
/*    */   }
/*    */ 
/*    */   public ReadOnlyDisplayPanel getPanel()
/*    */   {
/* 77 */     return this.panel;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ExtraPanel
 * JD-Core Version:    0.6.0
 */