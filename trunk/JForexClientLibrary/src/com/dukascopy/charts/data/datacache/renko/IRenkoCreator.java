package com.dukascopy.charts.data.datacache.renko;

import com.dukascopy.charts.data.datacache.TickData;
import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationCreator;

public abstract interface IRenkoCreator extends IPriceAggregationCreator<RenkoData, TickData, IRenkoLiveFeedListener>
{
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.IRenkoCreator
 * JD-Core Version:    0.6.0
 */