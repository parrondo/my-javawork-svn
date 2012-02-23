package com.dukascopy.charts.chartbuilder;

public abstract interface IDataOperationManagerStrategy
{
  public abstract boolean dataChanged(long paramLong1, long paramLong2);

  public abstract void mainIndicatorEdited(int paramInt);

  public abstract void mainIndicatorAdded(int paramInt);

  public abstract void subIndicatorAdded(int paramInt);

  public abstract void subIndicatorEdited(int paramInt);

  public abstract void orderChanged(long paramLong1, long paramLong2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IDataOperationManagerStrategy
 * JD-Core Version:    0.6.0
 */