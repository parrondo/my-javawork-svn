/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.Box;
/*    */ 
/*    */ public class ButtonTabPanelForButtomCustomPanel extends ButtonTabPanelForBottomPanel
/*    */ {
/*    */   public ButtonTabPanelForButtomCustomPanel(int panelId, String title, ActionListener actionListener)
/*    */   {
/* 10 */     super(panelId, title, actionListener);
/*    */   }
/*    */ 
/*    */   protected void addCommonComponents() {
/* 14 */     add(Box.createHorizontalStrut(10));
/* 15 */     add(this.textLabel);
/* 16 */     add(Box.createHorizontalStrut(10));
/* 17 */     add(this.undockTabButton);
/* 18 */     add(this.closeTabButton);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ButtonTabPanelForButtomCustomPanel
 * JD-Core Version:    0.6.0
 */