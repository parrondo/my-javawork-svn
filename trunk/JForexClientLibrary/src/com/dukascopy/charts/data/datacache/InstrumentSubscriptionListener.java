package com.dukascopy.charts.data.datacache;

import com.dukascopy.api.Instrument;

public abstract interface InstrumentSubscriptionListener
{
  public abstract void subscribedToInstrument(Instrument paramInstrument);

  public abstract void unsubscribedFromInstrument(Instrument paramInstrument);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.InstrumentSubscriptionListener
 * JD-Core Version:    0.6.0
 */