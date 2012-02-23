package com.dukascopy.charts.wrapper;

import com.dukascopy.api.IChart;
import com.dukascopy.api.IChartObject;

public abstract class AbstractChartWrapper
  implements IChart
{
  protected abstract void setOwnerId(IChartObject paramIChartObject, String paramString);

  protected abstract String getOwnerId(IChartObject paramIChartObject);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.wrapper.AbstractChartWrapper
 * JD-Core Version:    0.6.0
 */