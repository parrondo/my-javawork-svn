package com.dukascopy.charts.data.datacache.tickbar;

import com.dukascopy.charts.data.datacache.TickData;
import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationCreator;

public abstract interface ITickBarCreator extends IPriceAggregationCreator<TickBarData, TickData, ITickBarLiveFeedListener>
{
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.ITickBarCreator
 * JD-Core Version:    0.6.0
 */