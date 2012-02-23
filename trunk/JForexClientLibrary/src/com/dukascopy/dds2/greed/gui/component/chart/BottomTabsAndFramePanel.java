/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public abstract class BottomTabsAndFramePanel extends TabsAndFramePanel
/*    */ {
/*    */   public BottomTabsAndFramePanel(int chartPanelId)
/*    */   {
/* 18 */     super(chartPanelId);
/*    */   }
/*    */ 
/*    */   public static TabsAndFramePanel create(int chartPanelId, JPanel panelToWrap, TabsTypes tabType) {
/* 22 */     if (tabType == TabsTypes.MESSAGE_TAB)
/* 23 */       return new BottomPanelForMessages(chartPanelId, panelToWrap);
/* 24 */     if (tabType == TabsTypes.NEWS)
/* 25 */       return new BottomPanelWithoutProfitLossLabelForNews(chartPanelId, panelToWrap);
/* 26 */     if (tabType == TabsTypes.FIXED) {
/* 27 */       return new BottomPanelWithProfitLossLabel(chartPanelId, panelToWrap);
/*    */     }
/* 29 */     return new BottomPanelWithoutProfitLossLabel(chartPanelId, panelToWrap);
/*    */   }
/*    */ 
/*    */   public static enum TabsTypes
/*    */   {
/*  9 */     FIXED, 
/* 10 */     NEWS, 
/* 11 */     MESSAGE_TAB, 
/* 12 */     PRICE_ALERTER, 
/* 13 */     STRATEGIES, 
/* 14 */     HISTORICAL_DATA_MANAGER;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramePanel
 * JD-Core Version:    0.6.0
 */