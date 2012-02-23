/*     */ package com.dukascopy.dds2.greed.agent.strategy.notification.pnf;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.feed.IPointAndFigure;
/*     */ import com.dukascopy.api.feed.IPointAndFigureFeedListener;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureLiveFeedAdapter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractStrategyRateDataNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.PointAndFigureDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ 
/*     */ public class StrategyPointAndFigureNotificationManager extends AbstractStrategyRateDataNotificationManager<IPointAndFigureFeedListener, IPointAndFigureLiveFeedListener, LivePointAndFigureFeedListenerWrapper, IPointAndFigure, PointAndFigureData>
/*     */   implements IStrategyPointAndFigureNotificationManager
/*     */ {
/*     */   public StrategyPointAndFigureNotificationManager(FeedDataProvider feedDataProvider)
/*     */   {
/*  41 */     super(feedDataProvider);
/*     */   }
/*     */ 
/*     */   protected void addLiveInProgressBarListener(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, LivePointAndFigureFeedListenerWrapper listenerWrapper)
/*     */   {
/*  51 */     getFeedDataProvider().getIntraperiodBarsGenerator().addPointAndFigureNotificationListener(instrument, offerSide, jForexPeriod.getPriceRange(), jForexPeriod.getReversalAmount(), listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected IDataLoadingThread<PointAndFigureData> createDataLoadingThread(IStrategyRunner strategyRunner, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, ArrayBlockingQueue<PointAndFigureData> queue)
/*     */   {
/*  68 */     String name = "Strategy tester data loading thread - " + jForexPeriod.toString();
/*     */ 
/*  70 */     return new PointAndFigureDataLoadingThread(name, instrument, jForexPeriod, offerSide, queue, strategyRunner.getFrom(), strategyRunner.getTo(), strategyRunner.getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected LivePointAndFigureFeedListenerWrapper createListenerWrapper(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IPointAndFigureFeedListener barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/*  93 */     return new LivePointAndFigureFeedListenerWrapper(barFeedListener, new PointAndFigureLiveFeedAdapter()
/*     */     {
/*     */     }
/*     */     , instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */   }
/*     */ 
/*     */   protected void onBar(LivePointAndFigureFeedListenerWrapper listenerWrapper, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IPointAndFigure bar)
/*     */   {
/* 114 */     ((IPointAndFigureFeedListener)listenerWrapper.getLiveBarFormedListener()).onBar(instrument, offerSide, jForexPeriod.getPriceRange(), jForexPeriod.getReversalAmount(), bar);
/*     */   }
/*     */ 
/*     */   protected void removeLiveInProgressBarListener(LivePointAndFigureFeedListenerWrapper listenerWrapper)
/*     */   {
/* 125 */     getFeedDataProvider().getIntraperiodBarsGenerator().removePointAndFigureNotificationListener(listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected String validateJForexPeriodFields(JForexPeriod jForexPeriod)
/*     */   {
/* 130 */     if (!DataType.POINT_AND_FIGURE.equals(jForexPeriod.getDataType())) {
/* 131 */       return "DataType has to be " + DataType.POINT_AND_FIGURE;
/*     */     }
/* 133 */     if ((jForexPeriod.getPeriod() == null) || (jForexPeriod.getPriceRange() == null) || (jForexPeriod.getReversalAmount() == null))
/*     */     {
/* 138 */       return "Period, Price Range and Reversal Amount could not be null!";
/*     */     }
/* 140 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.pnf.StrategyPointAndFigureNotificationManager
 * JD-Core Version:    0.6.0
 */