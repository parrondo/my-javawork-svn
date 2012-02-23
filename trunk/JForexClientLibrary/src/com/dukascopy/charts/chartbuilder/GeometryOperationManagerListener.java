package com.dukascopy.charts.chartbuilder;

import com.dukascopy.api.impl.IndicatorWrapper;

public abstract interface GeometryOperationManagerListener
{
  public abstract void componentSizeChanged(int paramInt1, int paramInt2);

  public abstract void subComponentHeightChanged(IndicatorWrapper paramIndicatorWrapper, int paramInt);

  public abstract void subIndicatorAdded(IndicatorWrapper paramIndicatorWrapper, int paramInt);

  public abstract int subIndicatorDeleted(IndicatorWrapper paramIndicatorWrapper);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.GeometryOperationManagerListener
 * JD-Core Version:    0.6.0
 */