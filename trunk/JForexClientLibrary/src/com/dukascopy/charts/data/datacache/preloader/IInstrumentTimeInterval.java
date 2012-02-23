package com.dukascopy.charts.data.datacache.preloader;

import com.dukascopy.api.Instrument;
import com.dukascopy.charts.data.datacache.wrapper.ITimeInterval;

public abstract interface IInstrumentTimeInterval extends ITimeInterval
{
  public abstract Instrument getInstrument();

  public abstract void setInstrument(Instrument paramInstrument);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.preloader.IInstrumentTimeInterval
 * JD-Core Version:    0.6.0
 */