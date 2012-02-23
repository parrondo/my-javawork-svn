package com.dukascopy.charts.chartbuilder;

public abstract interface IDataOperationManager
{
  public abstract boolean dataChanged(long paramLong1, long paramLong2);

  public abstract void ordersChanged(long paramLong1, long paramLong2);

  public abstract void indicatorEdited(int paramInt);

  public abstract void indicatorAdded(int paramInt);

  public abstract void subIndicatorAdded(int paramInt);

  public abstract void subIndicatorEdited(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IDataOperationManager
 * JD-Core Version:    0.6.0
 */