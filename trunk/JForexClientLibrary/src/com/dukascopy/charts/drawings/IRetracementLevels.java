package com.dukascopy.charts.drawings;

import com.dukascopy.api.IChart.Type;
import com.dukascopy.api.drawings.ILeveledChartObject;
import java.util.List;

public abstract interface IRetracementLevels extends ILeveledChartObject
{
  public abstract IChart.Type getType();

  public abstract void setLevels(List<Object[]> paramList);

  public abstract List<Object[]> getLevels();

  public abstract List<Object[]> getDefaults();

  public abstract boolean compareLevels(List<Object[]> paramList1, List<Object[]> paramList2);

  public abstract boolean isLevelValuesInPercents();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.IRetracementLevels
 * JD-Core Version:    0.6.0
 */