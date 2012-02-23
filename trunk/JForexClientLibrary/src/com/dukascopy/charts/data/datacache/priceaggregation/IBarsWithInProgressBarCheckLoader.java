package com.dukascopy.charts.data.datacache.priceaggregation;

public abstract interface IBarsWithInProgressBarCheckLoader<R>
{
  public abstract R load();

  public abstract void addInProgressListener();

  public abstract void removeInProgressListener();

  public abstract boolean doesInProgressBarExistWaitIfCurrentlyLoading();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.IBarsWithInProgressBarCheckLoader
 * JD-Core Version:    0.6.0
 */