package com.dukascopy.charts.data.datacache.pnf;

import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;

public abstract interface IPointAndFigureLiveFeedListener extends IPriceAggregationLiveFeedListener<PointAndFigureData>
{
  public abstract void newPriceData(PointAndFigureData paramPointAndFigureData);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener
 * JD-Core Version:    0.6.0
 */