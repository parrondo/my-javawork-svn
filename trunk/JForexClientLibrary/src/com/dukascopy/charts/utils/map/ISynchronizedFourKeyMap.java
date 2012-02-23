package com.dukascopy.charts.utils.map;

import com.dukascopy.charts.utils.map.entry.IFourKeyEntry;
import java.util.Map;
import java.util.Set;

public abstract interface ISynchronizedFourKeyMap<K1, K2, K3, K4, V>
{
  public abstract V get(K1 paramK1, K2 paramK2, K3 paramK3, K4 paramK4);

  public abstract void put(K1 paramK1, K2 paramK2, K3 paramK3, K4 paramK4, V paramV);

  public abstract Map<K2, Map<K3, Map<K4, V>>> getSubMap(K1 paramK1);

  public abstract Map<K3, Map<K4, V>> getSubMap(K1 paramK1, K2 paramK2);

  public abstract Map<K4, V> getSubMap(K1 paramK1, K2 paramK2, K3 paramK3);

  public abstract V remove(K1 paramK1, K2 paramK2, K3 paramK3, K4 paramK4);

  public abstract Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet();

  public abstract Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet(K1 paramK1);

  public abstract Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet(K1 paramK1, K2 paramK2);

  public abstract Set<IFourKeyEntry<K1, K2, K3, K4, V>> entrySet(K1 paramK1, K2 paramK2, K3 paramK3);

  public abstract boolean contains(K1 paramK1, K2 paramK2, K3 paramK3, K4 paramK4);

  public abstract int size();

  public abstract void clear();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.ISynchronizedFourKeyMap
 * JD-Core Version:    0.6.0
 */