package com.dukascopy.charts.data.datacache;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;

public abstract interface LiveFeedListener
{
  public abstract void newTick(Instrument paramInstrument, long paramLong, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);

  public abstract void newCandle(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LiveFeedListener
 * JD-Core Version:    0.6.0
 */