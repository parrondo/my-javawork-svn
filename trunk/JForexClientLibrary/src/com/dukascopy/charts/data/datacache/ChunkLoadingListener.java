package com.dukascopy.charts.data.datacache;

public abstract interface ChunkLoadingListener
{
  public abstract void chunkLoaded(long[] paramArrayOfLong)
    throws DataCacheException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.ChunkLoadingListener
 * JD-Core Version:    0.6.0
 */