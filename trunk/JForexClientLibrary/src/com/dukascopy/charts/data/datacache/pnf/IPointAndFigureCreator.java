package com.dukascopy.charts.data.datacache.pnf;

import com.dukascopy.charts.data.datacache.TickData;
import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationCreator;

public abstract interface IPointAndFigureCreator extends IPriceAggregationCreator<PointAndFigureData, TickData, IPointAndFigureLiveFeedListener>
{
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.IPointAndFigureCreator
 * JD-Core Version:    0.6.0
 */