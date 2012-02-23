/*    */ package com.dukascopy.charts.view.swing;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Point;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class MouseChartWidgetSubComponentsMouseHandler extends MouseAdapter
/*    */ {
/*    */   private static final int BORDER_WIDTH = 3;
/*    */   private AbstractChartWidgetPanel widget;
/*    */ 
/*    */   public MouseChartWidgetSubComponentsMouseHandler(AbstractChartWidgetPanel widget)
/*    */   {
/* 21 */     this.widget = widget;
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent e)
/*    */   {
/* 26 */     if (!this.widget.getMenuButtonsPanel().isVisible()) {
/* 27 */       this.widget.getMenuButtonsPanel().setVisible(true);
/* 28 */       this.widget.setMouseOver(true);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent e)
/*    */   {
/* 34 */     Point widgetPoint = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), this.widget);
/*    */ 
/* 39 */     if ((widgetPoint.getX() < 3.0D) || (widgetPoint.getY() < 3.0D) || (widgetPoint.getX() > this.widget.getWidth() - 3) || (widgetPoint.getY() > this.widget.getHeight() - 3))
/*    */     {
/* 44 */       this.widget.getMenuButtonsPanel().setVisible(false);
/* 45 */       this.widget.setMouseOver(false);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.MouseChartWidgetSubComponentsMouseHandler
 * JD-Core Version:    0.6.0
 */