/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.news.NewsAdapter;
/*    */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup;
/*    */ 
/*    */ public class NewsUpdateAction extends AppActionEvent
/*    */ {
/*    */   private MarketNewsMessageGroup marketNewsMessageGroup;
/*    */ 
/*    */   public NewsUpdateAction(Object source, MarketNewsMessageGroup marketNewsMessageGroup)
/*    */   {
/* 15 */     super(source, false, true);
/* 16 */     this.marketNewsMessageGroup = marketNewsMessageGroup;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter() {
/* 20 */     ((NewsAdapter)GreedContext.get("newsAdapter")).updateNews(this.marketNewsMessageGroup.getMarketNewsList());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.NewsUpdateAction
 * JD-Core Version:    0.6.0
 */