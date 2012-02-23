/*    */ package com.dukascopy.dds2.greed.agent.strategy.notification;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.candle.IStrategyCandleNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.candle.StrategyCandleNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.pnf.IStrategyPointAndFigureNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.pnf.StrategyPointAndFigureNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.pr.IStrategyPriceRangeNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.pr.StrategyPriceRangeNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.renko.IStrategyRenkoNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.renko.StrategyRenkoNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.tickbar.IStrategyTickBarNotificationManager;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.tickbar.StrategyTickBarNotificationManager;
/*    */ 
/*    */ public class StrategyRateDataNotificationFactory
/*    */   implements IStrategyRateDataNotificationFactory
/*    */ {
/*    */   private static IStrategyRateDataNotificationFactory isntance;
/*    */   private IStrategyCandleNotificationManager candleNotificationManager;
/*    */   private IStrategyPriceRangeNotificationManager priceRangeNotificationManager;
/*    */   private IStrategyPointAndFigureNotificationManager pointAndFigureNotificationManager;
/*    */   private IStrategyTickBarNotificationManager tickBarNotificationManager;
/*    */   private IStrategyRenkoNotificationManager renkoNotificationManager;
/*    */   private final FeedDataProvider feedDataProvider;
/*    */ 
/*    */   private StrategyRateDataNotificationFactory()
/*    */   {
/* 34 */     this.feedDataProvider = FeedDataProvider.getDefaultInstance();
/*    */   }
/*    */ 
/*    */   public static IStrategyRateDataNotificationFactory getIsntance() {
/* 38 */     if (isntance == null) {
/* 39 */       isntance = new StrategyRateDataNotificationFactory();
/*    */     }
/* 41 */     return isntance;
/*    */   }
/*    */ 
/*    */   private FeedDataProvider getFeedDataProvider() {
/* 45 */     return this.feedDataProvider;
/*    */   }
/*    */ 
/*    */   public void unsubscribeFromAll(IStrategy strategy)
/*    */   {
/* 50 */     getCandleNotificationManager().unsubscribeFromAll(strategy);
/* 51 */     getPriceRangeNotificationManager().unsubscribeFromAll(strategy);
/* 52 */     getPointAndFigureNotificationManager().unsubscribeFromAll(strategy);
/* 53 */     getTickBarNotificationManager().unsubscribeFromAll(strategy);
/* 54 */     getRenkoNotificationManager().unsubscribeFromAll(strategy);
/*    */   }
/*    */ 
/*    */   public IStrategyCandleNotificationManager getCandleNotificationManager()
/*    */   {
/* 59 */     if (this.candleNotificationManager == null) {
/* 60 */       this.candleNotificationManager = new StrategyCandleNotificationManager(getFeedDataProvider());
/*    */     }
/* 62 */     return this.candleNotificationManager;
/*    */   }
/*    */ 
/*    */   public IStrategyPriceRangeNotificationManager getPriceRangeNotificationManager()
/*    */   {
/* 67 */     if (this.priceRangeNotificationManager == null) {
/* 68 */       this.priceRangeNotificationManager = new StrategyPriceRangeNotificationManager(getFeedDataProvider());
/*    */     }
/* 70 */     return this.priceRangeNotificationManager;
/*    */   }
/*    */ 
/*    */   public IStrategyPointAndFigureNotificationManager getPointAndFigureNotificationManager()
/*    */   {
/* 75 */     if (this.pointAndFigureNotificationManager == null) {
/* 76 */       this.pointAndFigureNotificationManager = new StrategyPointAndFigureNotificationManager(getFeedDataProvider());
/*    */     }
/* 78 */     return this.pointAndFigureNotificationManager;
/*    */   }
/*    */ 
/*    */   public IStrategyTickBarNotificationManager getTickBarNotificationManager()
/*    */   {
/* 83 */     if (this.tickBarNotificationManager == null) {
/* 84 */       this.tickBarNotificationManager = new StrategyTickBarNotificationManager(getFeedDataProvider());
/*    */     }
/* 86 */     return this.tickBarNotificationManager;
/*    */   }
/*    */ 
/*    */   public IStrategyRenkoNotificationManager getRenkoNotificationManager()
/*    */   {
/* 91 */     if (this.renkoNotificationManager == null) {
/* 92 */       this.renkoNotificationManager = new StrategyRenkoNotificationManager(getFeedDataProvider());
/*    */     }
/* 94 */     return this.renkoNotificationManager;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.StrategyRateDataNotificationFactory
 * JD-Core Version:    0.6.0
 */