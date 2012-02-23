package com.dukascopy.charts.math.dataprovider;

import com.dukascopy.api.impl.IndicatorWrapper;
import java.util.List;

public abstract interface IIndicatorsContainer
{
  public abstract void addIndicator(IndicatorWrapper paramIndicatorWrapper, int paramInt)
    throws Exception;

  public abstract void editIndicator(IndicatorWrapper paramIndicatorWrapper, int paramInt, boolean paramBoolean);

  public abstract void deleteIndicator(IndicatorWrapper paramIndicatorWrapper)
    throws Exception;

  public abstract void deleteIndicators(List<IndicatorWrapper> paramList);

  public abstract void deleteAllIndicators();

  public abstract List<IndicatorWrapper> getIndicators();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.IIndicatorsContainer
 * JD-Core Version:    0.6.0
 */