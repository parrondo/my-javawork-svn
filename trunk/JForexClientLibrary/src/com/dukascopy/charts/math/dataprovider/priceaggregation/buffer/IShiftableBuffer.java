package com.dukascopy.charts.math.dataprovider.priceaggregation.buffer;

public abstract interface IShiftableBuffer<D>
{
  public abstract void clear();

  public abstract int getMaxSize();

  public abstract int getSize();

  public abstract int getLastIndex();

  public abstract boolean isEmpty();

  public abstract boolean isFull();

  public abstract void addToEnd(D paramD);

  public abstract void addToHead(D paramD);

  public abstract void addToEnd(D[] paramArrayOfD);

  public abstract void addToHead(D[] paramArrayOfD);

  public abstract D[] getAll(D[] paramArrayOfD);

  public abstract D getFirst();

  public abstract D getLast();

  public abstract void set(D paramD, int paramInt);

  public abstract void setUp(D[] paramArrayOfD);

  public abstract D get(int paramInt);

  public abstract D[] copyOfRange(D[] paramArrayOfD, int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.buffer.IShiftableBuffer
 * JD-Core Version:    0.6.0
 */