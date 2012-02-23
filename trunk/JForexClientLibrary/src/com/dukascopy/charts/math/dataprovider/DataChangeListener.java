package com.dukascopy.charts.math.dataprovider;

import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.charts.data.datacache.Data;

public abstract interface DataChangeListener
{
  public abstract void dataChanged(long paramLong1, long paramLong2, Period paramPeriod, OfferSide paramOfferSide);

  public abstract void indicatorAdded(Period paramPeriod, int paramInt);

  public abstract void indicatorChanged(Period paramPeriod, int paramInt);

  public abstract void indicatorRemoved(Period paramPeriod, int paramInt);

  public abstract void indicatorsRemoved(Period paramPeriod, int[] paramArrayOfInt);

  public abstract void loadingStarted(Period paramPeriod, OfferSide paramOfferSide);

  public abstract void loadingFinished(Period paramPeriod, OfferSide paramOfferSide);

  public abstract void lastKnownDataChanged(Data paramData);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.DataChangeListener
 * JD-Core Version:    0.6.0
 */