package com.dukascopy.charts.mappers.time;

import com.dukascopy.api.Period;

public abstract interface ITimeToXMapper
{
  public abstract long tx(int paramInt);

  public abstract int xt(long paramLong);

  public abstract boolean isXOutOfRange(int paramInt);

  public abstract int getWidth();

  public abstract int getBarWidth();

  public abstract long getInterval();

  public abstract Period getPeriod();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.ITimeToXMapper
 * JD-Core Version:    0.6.0
 */