package com.dukascopy.charts.utils.map.entry;

public abstract interface IFourKeyEntry<K1, K2, K3, K4, V> extends IThreeKeyEntry<K1, K2, K3, V>
{
  public abstract K4 getKey4();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.entry.IFourKeyEntry
 * JD-Core Version:    0.6.0
 */