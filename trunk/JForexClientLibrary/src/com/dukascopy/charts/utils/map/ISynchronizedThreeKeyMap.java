package com.dukascopy.charts.utils.map;

import com.dukascopy.charts.utils.map.entry.IThreeKeyEntry;
import java.util.Map;
import java.util.Set;

public abstract interface ISynchronizedThreeKeyMap<K1, K2, K3, V>
{
  public abstract V get(K1 paramK1, K2 paramK2, K3 paramK3);

  public abstract void put(K1 paramK1, K2 paramK2, K3 paramK3, V paramV);

  public abstract Map<K2, Map<K3, V>> getSubMap(K1 paramK1);

  public abstract Map<K3, V> getSubMap(K1 paramK1, K2 paramK2);

  public abstract V remove(K1 paramK1, K2 paramK2, K3 paramK3);

  public abstract Set<IThreeKeyEntry<K1, K2, K3, V>> entrySet();

  public abstract Set<IThreeKeyEntry<K1, K2, K3, V>> entrySet(K1 paramK1);

  public abstract Set<IThreeKeyEntry<K1, K2, K3, V>> entrySet(K1 paramK1, K2 paramK2);

  public abstract boolean contains(K1 paramK1, K2 paramK2, K3 paramK3);

  public abstract int size();

  public abstract void clear();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.ISynchronizedThreeKeyMap
 * JD-Core Version:    0.6.0
 */