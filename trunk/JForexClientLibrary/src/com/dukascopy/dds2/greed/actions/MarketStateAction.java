/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*    */ import com.dukascopy.dds2.greed.model.MarketView;
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*    */ 
/*    */ public class MarketStateAction extends AppActionEvent
/*    */ {
/* 15 */   private ClientForm gui = null;
/*    */   private CurrencyMarket marketState;
/*    */   public String instrument;
/*    */   private MarketView marketView;
/*    */   private static final long FLASHICON_TIMEOUT_MS = 1000L;
/* 22 */   private static long lastFlashIconTimeMs = System.currentTimeMillis();
/*    */ 
/*    */   public MarketStateAction(Object source, CurrencyMarket marketState) {
/* 25 */     super(source, false, true);
/* 26 */     this.marketState = marketState;
/* 27 */     this.instrument = marketState.getInstrument();
/*    */   }
/*    */ 
/*    */   public void doAction() {
/* 31 */     this.gui = ((ClientForm)GreedContext.get("clientGui"));
/* 32 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter() {
/* 36 */     if (System.currentTimeMillis() - lastFlashIconTimeMs > 1000L) {
/* 37 */       this.gui.flashConnectIcon();
/* 38 */       lastFlashIconTimeMs = System.currentTimeMillis();
/*    */     }
/*    */ 
/* 41 */     this.marketView.onMarketState(CurrencyMarketWrapper.valueOf(this.marketState));
/* 42 */     this.gui.onMarketState(CurrencyMarketWrapper.valueOf(this.marketState));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.MarketStateAction
 * JD-Core Version:    0.6.0
 */