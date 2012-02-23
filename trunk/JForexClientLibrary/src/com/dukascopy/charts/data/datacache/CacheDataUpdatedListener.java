package com.dukascopy.charts.data.datacache;

import com.dukascopy.api.Instrument;

public abstract interface CacheDataUpdatedListener
{
  public abstract void cacheUpdated(Instrument paramInstrument, long paramLong1, long paramLong2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.CacheDataUpdatedListener
 * JD-Core Version:    0.6.0
 */