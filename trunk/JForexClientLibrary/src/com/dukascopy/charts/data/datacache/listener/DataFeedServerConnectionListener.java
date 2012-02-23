package com.dukascopy.charts.data.datacache.listener;

public abstract interface DataFeedServerConnectionListener
{
  public abstract void connected();

  public abstract void disconnected();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.listener.DataFeedServerConnectionListener
 * JD-Core Version:    0.6.0
 */