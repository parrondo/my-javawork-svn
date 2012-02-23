package com.dukascopy.charts.data.datacache.priceaggregation;

public abstract interface IPriceAggregationLiveFeedListener<T extends AbstractPriceAggregationData>
{
  public abstract void newPriceData(T paramT);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener
 * JD-Core Version:    0.6.0
 */