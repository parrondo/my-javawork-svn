/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import java.awt.event.ActionListener;
/*    */ 
/*    */ public class ButtonTabPanelForBottomPanelWithCloseButton extends ButtonTabPanelForBottomPanel
/*    */ {
/*    */   public ButtonTabPanelForBottomPanelWithCloseButton(int panelId, String title, ActionListener actionListener)
/*    */   {
/*  9 */     super(panelId, title, actionListener);
/* 10 */     add(this.closeTabButton);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ButtonTabPanelForBottomPanelWithCloseButton
 * JD-Core Version:    0.6.0
 */