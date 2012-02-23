package com.dukascopy.charts.data.datacache.wrapper;

public abstract interface ITimeInterval
{
  public abstract long getStart();

  public abstract void setStart(long paramLong);

  public abstract long getEnd();

  public abstract void setEnd(long paramLong);

  public abstract boolean isInInterval(long paramLong);

  public abstract boolean intersects(ITimeInterval paramITimeInterval);

  public abstract boolean isIntervalTheSame(ITimeInterval paramITimeInterval);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.wrapper.ITimeInterval
 * JD-Core Version:    0.6.0
 */