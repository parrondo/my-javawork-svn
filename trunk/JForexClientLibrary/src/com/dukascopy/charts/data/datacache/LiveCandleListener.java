package com.dukascopy.charts.data.datacache;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;

public abstract interface LiveCandleListener
{
  public abstract void newCandle(Instrument paramInstrument, Period paramPeriod, CandleData paramCandleData1, CandleData paramCandleData2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LiveCandleListener
 * JD-Core Version:    0.6.0
 */