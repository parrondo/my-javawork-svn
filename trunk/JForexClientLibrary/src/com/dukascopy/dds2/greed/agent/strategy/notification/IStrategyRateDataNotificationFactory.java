package com.dukascopy.dds2.greed.agent.strategy.notification;

import com.dukascopy.api.IStrategy;
import com.dukascopy.dds2.greed.agent.strategy.notification.candle.IStrategyCandleNotificationManager;
import com.dukascopy.dds2.greed.agent.strategy.notification.pnf.IStrategyPointAndFigureNotificationManager;
import com.dukascopy.dds2.greed.agent.strategy.notification.pr.IStrategyPriceRangeNotificationManager;
import com.dukascopy.dds2.greed.agent.strategy.notification.renko.IStrategyRenkoNotificationManager;
import com.dukascopy.dds2.greed.agent.strategy.notification.tickbar.IStrategyTickBarNotificationManager;

public abstract interface IStrategyRateDataNotificationFactory
{
  public abstract IStrategyCandleNotificationManager getCandleNotificationManager();

  public abstract IStrategyPriceRangeNotificationManager getPriceRangeNotificationManager();

  public abstract IStrategyPointAndFigureNotificationManager getPointAndFigureNotificationManager();

  public abstract IStrategyTickBarNotificationManager getTickBarNotificationManager();

  public abstract IStrategyRenkoNotificationManager getRenkoNotificationManager();

  public abstract void unsubscribeFromAll(IStrategy paramIStrategy);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.IStrategyRateDataNotificationFactory
 * JD-Core Version:    0.6.0
 */