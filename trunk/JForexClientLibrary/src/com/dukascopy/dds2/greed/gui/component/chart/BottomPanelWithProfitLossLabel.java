/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import javax.swing.BoxLayout;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class BottomPanelWithProfitLossLabel extends BottomTabsAndFramePanel
/*    */ {
/*    */   public BottomPanelWithProfitLossLabel(int chartPanelId, JComponent content)
/*    */   {
/*  9 */     super(chartPanelId);
/* 10 */     setLayout(new BoxLayout(this, 1));
/* 11 */     add(content);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.BottomPanelWithProfitLossLabel
 * JD-Core Version:    0.6.0
 */