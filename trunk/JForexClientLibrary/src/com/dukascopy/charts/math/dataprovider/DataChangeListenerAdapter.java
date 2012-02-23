package com.dukascopy.charts.math.dataprovider;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.charts.data.datacache.Data;

public class DataChangeListenerAdapter
  implements DataChangeListener, OrdersDataChangeListener
{
  public void dataChanged(long from, long to, Period period, OfferSide offerSide)
  {
  }

  public void indicatorAdded(Period period, int id)
  {
  }

  public void indicatorChanged(Period period, int id)
  {
  }

  public void indicatorRemoved(Period period, int id)
  {
  }

  public void indicatorsRemoved(Period period, int[] ids)
  {
  }

  public void loadingStarted(Period period, OfferSide offerSide)
  {
  }

  public void loadingFinished(Period period, OfferSide offerSide)
  {
  }

  public void dataChanged(Instrument instrument, long from, long to)
  {
  }

  public void loadingStarted(Instrument instrument)
  {
  }

  public void loadingFinished(Instrument instrument)
  {
  }

  public void lastKnownDataChanged(Data data)
  {
  }
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.DataChangeListenerAdapter
 * JD-Core Version:    0.6.0
 */