package com.dukascopy.dds2.greed.agent.strategy.notification;

import com.dukascopy.api.IBar;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.impl.connect.JForexTaskManager;
import com.dukascopy.api.impl.connect.StrategyProcessor;
import com.dukascopy.api.system.IStrategyExceptionHandler;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
import com.dukascopy.dds2.greed.util.INotificationUtils;

public abstract interface IStrategyNotificationManager<LISTENER, BAR extends IBar>
{
  public abstract void unsubscribeFromAll(IStrategy paramIStrategy);

  public abstract void unsubscribeFromBarsFeedForHistoricalTester(IStrategy paramIStrategy, IStrategyRunner paramIStrategyRunner, LISTENER paramLISTENER);

  public abstract void unsubscribeFromLiveBarsFeed(IStrategy paramIStrategy, LISTENER paramLISTENER);

  public abstract void historicalBarReceived(IStrategy paramIStrategy, Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, BAR paramBAR);

  public abstract void subscribeToBarsFeedForHistoricalTester(IStrategy paramIStrategy, IStrategyRunner paramIStrategyRunner, Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, LISTENER paramLISTENER, IStrategyExceptionHandler paramIStrategyExceptionHandler, JForexTaskManager paramJForexTaskManager, StrategyProcessor paramStrategyProcessor, INotificationUtils paramINotificationUtils);

  public abstract void subscribeToLiveBarsFeed(IStrategy paramIStrategy, Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, LISTENER paramLISTENER, IStrategyExceptionHandler paramIStrategyExceptionHandler, JForexTaskManager paramJForexTaskManager, StrategyProcessor paramStrategyProcessor, INotificationUtils paramINotificationUtils);

  public abstract JForexPeriod getJForexPeriod(IStrategy paramIStrategy, LISTENER paramLISTENER);

  public abstract Instrument getInstrument(IStrategy paramIStrategy, LISTENER paramLISTENER);

  public abstract OfferSide getOfferSide(IStrategy paramIStrategy, LISTENER paramLISTENER);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.IStrategyNotificationManager
 * JD-Core Version:    0.6.0
 */