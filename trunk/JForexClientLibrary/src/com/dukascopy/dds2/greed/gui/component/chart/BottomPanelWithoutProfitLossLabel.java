/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class BottomPanelWithoutProfitLossLabel extends BottomTabsAndFramePanel
/*    */ {
/*    */   private final JComponent content;
/*    */ 
/*    */   public BottomPanelWithoutProfitLossLabel(int chartPanelId, JComponent content)
/*    */   {
/* 12 */     super(chartPanelId);
/* 13 */     setLayout(new BorderLayout());
/*    */ 
/* 15 */     this.content = content;
/* 16 */     add(content);
/*    */   }
/*    */ 
/*    */   public JComponent getContent() {
/* 20 */     return this.content;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.BottomPanelWithoutProfitLossLabel
 * JD-Core Version:    0.6.0
 */