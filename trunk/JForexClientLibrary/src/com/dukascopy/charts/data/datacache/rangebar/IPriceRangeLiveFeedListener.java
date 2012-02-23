package com.dukascopy.charts.data.datacache.rangebar;

import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;

public abstract interface IPriceRangeLiveFeedListener extends IPriceAggregationLiveFeedListener<PriceRangeData>
{
  public abstract void newPriceData(PriceRangeData paramPriceRangeData);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener
 * JD-Core Version:    0.6.0
 */