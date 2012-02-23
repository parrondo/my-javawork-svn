package com.dukascopy.charts.chartbuilder;

public abstract interface IGeometryOperationManagerStrategy
{
  public abstract void componentSizeChanged(int paramInt1, int paramInt2);

  public abstract void subComponentHeightChanged(Integer paramInteger, int paramInt);

  public abstract void subIndicatorAdded(Integer paramInteger, int paramInt);

  public abstract int subIndicatorDeleted(Integer paramInteger);

  public abstract void resetGeometry();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IGeometryOperationManagerStrategy
 * JD-Core Version:    0.6.0
 */