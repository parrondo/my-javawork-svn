package com.dukascopy.charts.data.datacache.priceaggregation;

import com.dukascopy.charts.data.datacache.Data;

public abstract interface IPriceAggregationCreator<D extends AbstractPriceAggregationData, SD extends Data, L extends IPriceAggregationLiveFeedListener<D>>
{
  public abstract void reset();

  public abstract D[] getResult();

  public abstract boolean isAllDesiredDataLoaded();

  public abstract int getDesiredDatasCount();

  public abstract boolean analyse(SD paramSD);

  public abstract boolean isDirectOrder();

  public abstract D getLastData();

  public abstract D getLastCompletedData();

  public abstract D getLastFiredData();

  public abstract D getFirstData();

  public abstract int getLastElementIndex();

  public abstract int getLoadedElementsNumber();

  public abstract void setupLastData(D paramD);

  public abstract boolean canContinueCurrentDataConstruction(D paramD, SD paramSD);

  public abstract void addListener(L paramL);

  public abstract void removeListener(L paramL);

  public abstract boolean contains(L paramL);

  public abstract void fireNewBarCreated(D paramD);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationCreator
 * JD-Core Version:    0.6.0
 */