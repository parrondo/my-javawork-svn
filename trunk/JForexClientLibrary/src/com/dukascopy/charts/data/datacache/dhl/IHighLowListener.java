package com.dukascopy.charts.data.datacache.dhl;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;

public abstract interface IHighLowListener
{
  public abstract void highUpdated(Instrument paramInstrument, Period paramPeriod, double paramDouble);

  public abstract void lowUpdated(Instrument paramInstrument, Period paramPeriod, double paramDouble);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.dhl.IHighLowListener
 * JD-Core Version:    0.6.0
 */