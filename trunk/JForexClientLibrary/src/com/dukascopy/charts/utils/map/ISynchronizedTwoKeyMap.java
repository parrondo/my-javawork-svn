package com.dukascopy.charts.utils.map;

import com.dukascopy.charts.utils.map.entry.ITwoKeyEntry;
import java.util.Map;
import java.util.Set;

public abstract interface ISynchronizedTwoKeyMap<K1, K2, V>
{
  public abstract V get(K1 paramK1, K2 paramK2);

  public abstract void put(K1 paramK1, K2 paramK2, V paramV);

  public abstract Map<K2, V> getSubMap(K1 paramK1);

  public abstract V remove(K1 paramK1, K2 paramK2);

  public abstract Set<ITwoKeyEntry<K1, K2, V>> entrySet();

  public abstract Set<ITwoKeyEntry<K1, K2, V>> entrySet(K1 paramK1);

  public abstract boolean contains(K1 paramK1, K2 paramK2);

  public abstract int size();

  public abstract void clear();

  public abstract boolean isEmpty();

  public abstract void removeValue(V paramV);

  public abstract ISynchronizedTwoKeyMap<K1, K2, V> copy();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.ISynchronizedTwoKeyMap
 * JD-Core Version:    0.6.0
 */