package com.dukascopy.charts.math.dataprovider;

import com.dukascopy.api.Instrument;
import com.dukascopy.charts.data.datacache.OrderHistoricalData;

public abstract interface IOrdersDataProvider
{
  public abstract void start();

  public abstract void dispose();

  public abstract OrderHistoricalData[] getOrdersData(long paramLong1, long paramLong2, boolean paramBoolean);

  public abstract void addOrdersDataChangeListener(OrdersDataChangeListener paramOrdersDataChangeListener);

  public abstract void removeOrdersDataChangeListener(OrdersDataChangeListener paramOrdersDataChangeListener);

  public abstract void changeInstrument(Instrument paramInstrument);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.IOrdersDataProvider
 * JD-Core Version:    0.6.0
 */