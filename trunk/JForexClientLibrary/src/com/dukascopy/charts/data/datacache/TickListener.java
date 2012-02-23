package com.dukascopy.charts.data.datacache;

import com.dukascopy.transport.common.msg.request.CurrencyMarket;

public abstract interface TickListener
{
  public abstract void tickReceived(CurrencyMarket paramCurrencyMarket);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.TickListener
 * JD-Core Version:    0.6.0
 */