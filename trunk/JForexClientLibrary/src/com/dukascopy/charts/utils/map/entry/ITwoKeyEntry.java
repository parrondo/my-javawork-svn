package com.dukascopy.charts.utils.map.entry;

public abstract interface ITwoKeyEntry<K1, K2, V>
{
  public abstract K1 getKey1();

  public abstract K2 getKey2();

  public abstract V getValue();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.entry.ITwoKeyEntry
 * JD-Core Version:    0.6.0
 */