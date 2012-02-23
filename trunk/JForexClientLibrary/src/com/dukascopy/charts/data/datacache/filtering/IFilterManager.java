package com.dukascopy.charts.data.datacache.filtering;

import com.dukascopy.api.Filter;
import com.dukascopy.api.Period;
import com.dukascopy.charts.data.datacache.CandleData;
import com.dukascopy.charts.data.datacache.DataCacheException;
import com.dukascopy.charts.data.datacache.LoadingProgressListener;
import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
import java.util.List;

public abstract interface IFilterManager
{
  public abstract boolean isWeekendTime(long paramLong, Period paramPeriod);

  public abstract TimeInterval getWeekend(long paramLong);

  public abstract List<TimeInterval> calculateWeekends(Period paramPeriod, int paramInt, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract List<TimeInterval> getApproximateWeekends(long paramLong1, long paramLong2);

  public abstract boolean isFlat(CandleData paramCandleData);

  public abstract boolean isFlat(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5);

  public abstract boolean matchedFilter(Period paramPeriod, Filter paramFilter, CandleData paramCandleData);

  public abstract boolean matchedFilter(Period paramPeriod, Filter paramFilter, long paramLong, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.filtering.IFilterManager
 * JD-Core Version:    0.6.0
 */