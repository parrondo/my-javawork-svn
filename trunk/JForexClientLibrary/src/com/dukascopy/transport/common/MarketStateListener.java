package com.dukascopy.transport.common;

import com.dukascopy.transport.common.msg.request.CurrencyMarket;

public abstract interface MarketStateListener
{
  public abstract void onMarketState(CurrencyMarket paramCurrencyMarket);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.MarketStateListener
 * JD-Core Version:    0.6.0
 */