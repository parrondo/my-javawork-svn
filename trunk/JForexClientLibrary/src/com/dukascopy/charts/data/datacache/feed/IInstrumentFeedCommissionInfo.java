package com.dukascopy.charts.data.datacache.feed;

import com.dukascopy.charts.data.datacache.preloader.IInstrumentTimeInterval;

public abstract interface IInstrumentFeedCommissionInfo extends IInstrumentTimeInterval
{
  public abstract void setFeedCommission(double paramDouble);

  public abstract double getFeedCommission();

  public abstract long getPriority();

  public abstract void setPriority(long paramLong);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.feed.IInstrumentFeedCommissionInfo
 * JD-Core Version:    0.6.0
 */