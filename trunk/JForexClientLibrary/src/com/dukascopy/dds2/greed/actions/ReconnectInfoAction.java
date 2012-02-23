/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.connect.ConnectStatus;
/*    */ 
/*    */ public class ReconnectInfoAction extends AppActionEvent
/*    */ {
/*    */   private ClientForm gui;
/*    */ 
/*    */   public ReconnectInfoAction(Object source)
/*    */   {
/* 17 */     super(source, false, true);
/*    */   }
/*    */ 
/*    */   public void doAction() {
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter() {
/* 24 */     this.gui = ((ClientForm)GreedContext.get("clientGui"));
/* 25 */     this.gui.setConnectStatus(ConnectStatus.OFFLINE);
/* 26 */     if (GreedContext.get("feedDataProvider") != null) {
/* 27 */       FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/* 28 */       feedDataProvider.disconnected();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ReconnectInfoAction
 * JD-Core Version:    0.6.0
 */