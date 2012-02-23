package com.dukascopy.charts.data.datacache;

import com.dukascopy.api.Instrument;
import java.util.List;

public abstract interface OrdersListener
{
  public abstract void newOrder(Instrument paramInstrument, OrderHistoricalData paramOrderHistoricalData);

  public abstract void orderChange(Instrument paramInstrument, OrderHistoricalData paramOrderHistoricalData);

  public abstract void orderMerge(Instrument paramInstrument, OrderHistoricalData paramOrderHistoricalData, List<OrderHistoricalData> paramList);

  public abstract void ordersInvalidated(Instrument paramInstrument);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.OrdersListener
 * JD-Core Version:    0.6.0
 */