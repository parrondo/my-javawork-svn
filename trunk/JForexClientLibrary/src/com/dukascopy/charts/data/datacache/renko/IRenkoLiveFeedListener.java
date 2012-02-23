package com.dukascopy.charts.data.datacache.renko;

import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;

public abstract interface IRenkoLiveFeedListener extends IPriceAggregationLiveFeedListener<RenkoData>
{
  public abstract void newPriceData(RenkoData paramRenkoData);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener
 * JD-Core Version:    0.6.0
 */