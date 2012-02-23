package com.dukascopy.charts.data.datacache;

public abstract interface LoadingProgressListener
{
  public abstract void dataLoaded(long paramLong1, long paramLong2, long paramLong3, String paramString);

  public abstract void loadingFinished(boolean paramBoolean, long paramLong1, long paramLong2, long paramLong3, Exception paramException);

  public abstract boolean stopJob();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadingProgressListener
 * JD-Core Version:    0.6.0
 */