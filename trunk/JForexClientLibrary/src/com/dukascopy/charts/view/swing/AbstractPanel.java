/*    */ package com.dukascopy.charts.view.swing;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ class AbstractPanel extends JComponent
/*    */ {
/*    */   PaintingTechnic paintingTechnic;
/*    */ 
/*    */   public AbstractPanel(PaintingTechnic paintingTechnic)
/*    */   {
/* 11 */     this.paintingTechnic = paintingTechnic;
/*    */   }
/*    */ 
/*    */   public void paint(Graphics g)
/*    */   {
/* 16 */     this.paintingTechnic.paint(g, this);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.AbstractPanel
 * JD-Core Version:    0.6.0
 */