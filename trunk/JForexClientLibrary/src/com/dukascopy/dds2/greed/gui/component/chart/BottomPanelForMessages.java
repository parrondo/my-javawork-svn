/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class BottomPanelForMessages extends BottomPanelWithProfitLossLabel
/*    */ {
/*    */   private boolean closeButtonVisible;
/*    */   private JPanel wrappedPanel;
/*    */ 
/*    */   public BottomPanelForMessages(int chartPanelId, JPanel content)
/*    */   {
/* 12 */     super(chartPanelId, content);
/* 13 */     this.wrappedPanel = content;
/*    */   }
/*    */ 
/*    */   public JPanel getWrappedPanel() {
/* 17 */     return this.wrappedPanel;
/*    */   }
/*    */ 
/*    */   public boolean isCloseButtonVisible() {
/* 21 */     return this.closeButtonVisible;
/*    */   }
/*    */ 
/*    */   public void setCloseButtonVisible(boolean closeButtonVisible) {
/* 25 */     this.closeButtonVisible = closeButtonVisible;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.BottomPanelForMessages
 * JD-Core Version:    0.6.0
 */