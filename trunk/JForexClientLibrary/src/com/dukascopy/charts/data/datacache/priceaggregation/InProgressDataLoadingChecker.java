package com.dukascopy.charts.data.datacache.priceaggregation;

import com.dukascopy.api.feed.IPriceAggregationBar;

public abstract interface InProgressDataLoadingChecker<T extends IPriceAggregationBar>
{
  public abstract boolean isLoadingInProgress();

  public abstract T getInProgressData();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.InProgressDataLoadingChecker
 * JD-Core Version:    0.6.0
 */