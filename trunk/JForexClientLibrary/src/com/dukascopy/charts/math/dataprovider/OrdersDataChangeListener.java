package com.dukascopy.charts.math.dataprovider;

import com.dukascopy.api.Instrument;

public abstract interface OrdersDataChangeListener
{
  public abstract void dataChanged(Instrument paramInstrument, long paramLong1, long paramLong2);

  public abstract void loadingStarted(Instrument paramInstrument);

  public abstract void loadingFinished(Instrument paramInstrument);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.OrdersDataChangeListener
 * JD-Core Version:    0.6.0
 */