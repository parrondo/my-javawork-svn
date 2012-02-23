package com.dukascopy.charts.data.datacache.tickbar;

import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;

public abstract interface ITickBarLiveFeedListener extends IPriceAggregationLiveFeedListener<TickBarData>
{
  public abstract void newPriceData(TickBarData paramTickBarData);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener
 * JD-Core Version:    0.6.0
 */