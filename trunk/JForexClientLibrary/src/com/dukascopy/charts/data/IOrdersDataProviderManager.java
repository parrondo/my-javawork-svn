package com.dukascopy.charts.data;

import com.dukascopy.api.Instrument;
import com.dukascopy.charts.data.datacache.OrderHistoricalData;
import com.dukascopy.dds2.greed.util.IOrderUtils;

public abstract interface IOrdersDataProviderManager
{
  public abstract OrderHistoricalData[] getOrdersData();

  public abstract IOrderUtils getOrderUtils();

  public abstract void start();

  public abstract void dispose();

  public abstract void changeInstrument(Instrument paramInstrument);

  public abstract long getStartTime();

  public abstract long getEndTime();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.IOrdersDataProviderManager
 * JD-Core Version:    0.6.0
 */