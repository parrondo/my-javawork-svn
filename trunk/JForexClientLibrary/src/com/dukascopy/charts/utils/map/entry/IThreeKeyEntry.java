package com.dukascopy.charts.utils.map.entry;

public abstract interface IThreeKeyEntry<K1, K2, K3, V>
{
  public abstract K1 getKey1();

  public abstract K2 getKey2();

  public abstract K3 getKey3();

  public abstract V getValue();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.entry.IThreeKeyEntry
 * JD-Core Version:    0.6.0
 */