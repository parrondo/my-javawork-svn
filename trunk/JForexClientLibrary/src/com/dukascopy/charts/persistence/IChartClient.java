package com.dukascopy.charts.persistence;

import com.dukascopy.api.IChart.Type;
import java.util.List;
import java.util.Map;

public abstract interface IChartClient
{
  public abstract Map<String, List<Object[]>> restoreHorizontalRetracementPresets(IChart.Type paramType);

  public abstract void saveHorizontalRetracementPresets(IChart.Type paramType, Map<String, List<Object[]>> paramMap);

  public abstract int getLastActiveChartPanelId();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.IChartClient
 * JD-Core Version:    0.6.0
 */