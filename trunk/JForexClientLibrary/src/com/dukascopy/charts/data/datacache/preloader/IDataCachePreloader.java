package com.dukascopy.charts.data.datacache.preloader;

import com.dukascopy.api.Instrument;
import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
import java.util.List;

public abstract interface IDataCachePreloader
{
  public abstract void loadTicksInCache(List<Instrument> paramList, TimeInterval paramTimeInterval, DataCachePreloadControl paramDataCachePreloadControl);

  public abstract void loadTicksInCache(List<InstrumentTimeInterval> paramList, DataCachePreloadControl paramDataCachePreloadControl);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.preloader.IDataCachePreloader
 * JD-Core Version:    0.6.0
 */