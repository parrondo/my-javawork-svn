package com.dukascopy.charts.data.orders;

import com.dukascopy.api.Instrument;
import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler.OrdersDataStruct;
import com.dukascopy.charts.data.datacache.OrderHistoricalData;
import com.dukascopy.charts.data.datacache.OrdersListener;
import com.dukascopy.dds2.greed.util.IOrderUtils;
import java.util.Collection;
import java.util.Map;

public abstract interface IOrdersProvider
{
  public abstract Map<String, OrderHistoricalData> getOrdersForInstrument(Instrument paramInstrument);

  public abstract Collection<OrderHistoricalData> getOpenOrdersForInstrument(Instrument paramInstrument, long paramLong1, long paramLong2);

  public abstract Collection<?>[] processHistoricalData(Instrument paramInstrument, long paramLong1, long paramLong2, ICurvesProtocolHandler.OrdersDataStruct paramOrdersDataStruct);

  public abstract IOrderUtils getOrderUtils();

  public abstract void addOrdersListener(Instrument paramInstrument, OrdersListener paramOrdersListener);

  public abstract void removeOrdersListener(OrdersListener paramOrdersListener);

  public abstract void close();

  public abstract double recalculateEquity();

  public abstract void clear();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.orders.IOrdersProvider
 * JD-Core Version:    0.6.0
 */