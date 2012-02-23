/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler.Event;
/*    */ import com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame;
/*    */ import com.dukascopy.dds2.greed.model.MarketView;
/*    */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*    */ 
/*    */ public class UpdateTradabilityAction extends AppActionEvent
/*    */ {
/*    */   private InstrumentStatusUpdateMessage instrumentStatusUpdateMessage;
/*    */ 
/*    */   public UpdateTradabilityAction(Object source, InstrumentStatusUpdateMessage isuMessage)
/*    */   {
/* 16 */     super(source, false, true);
/* 17 */     this.instrumentStatusUpdateMessage = isuMessage;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 26 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/*    */ 
/* 28 */     InstrumentStatusUpdateMessage lastTradability = marketView.getInstrumentState(this.instrumentStatusUpdateMessage.getInstrument());
/* 29 */     if ((lastTradability == null) || (lastTradability.getTradable() != this.instrumentStatusUpdateMessage.getTradable()))
/*    */     {
/* 32 */       marketView.setInstrumentState(this.instrumentStatusUpdateMessage);
/* 33 */       ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 34 */       gui.getDealPanel().updateTradability(this.instrumentStatusUpdateMessage);
/*    */ 
/* 36 */       MarketOverviewFrame instrumentFrame = (MarketOverviewFrame)GreedContext.get("Dock");
/* 37 */       if ((instrumentFrame != null) && (instrumentFrame.isVisible())) {
/* 38 */         instrumentFrame.updateTradability(this.instrumentStatusUpdateMessage);
/*    */       }
/* 40 */       gui.getLayoutManager().getChartTabsController().handle(IEventHandler.Event.PRESENTED_INSTRUMENTS_CHANGED, this.instrumentStatusUpdateMessage.getInstrument());
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.UpdateTradabilityAction
 * JD-Core Version:    0.6.0
 */