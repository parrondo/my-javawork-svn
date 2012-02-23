package com.dukascopy.charts.data.datacache.firsttimes;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import java.util.List;

public abstract interface IFeedDataHistoryFirstTimesManager
{
  public abstract long getFirstFeedDataTime(Instrument paramInstrument, Period paramPeriod);

  public abstract List<Instrument> getSupportedInstruments();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.firsttimes.IFeedDataHistoryFirstTimesManager
 * JD-Core Version:    0.6.0
 */