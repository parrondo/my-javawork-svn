package com.dukascopy.charts.data;

import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.math.dataprovider.DataChangeListener;
import java.lang.reflect.InvocationTargetException;

public abstract interface DataProvider<C>
{
  public abstract void start();

  public abstract void dispose();

  public abstract void setActive(boolean paramBoolean);

  public abstract C getCachedDataSequence();

  public abstract double getMinPriceFor(Integer paramInteger);

  public abstract double getMaxPriceFor(Integer paramInteger);

  public abstract void addDataChangeListener(DataChangeListener paramDataChangeListener);

  public abstract void addIndicator(IndicatorWrapper paramIndicatorWrapper)
    throws IllegalAccessException, InvocationTargetException;

  public abstract void editIndicator(IndicatorWrapper paramIndicatorWrapper)
    throws IllegalAccessException, InvocationTargetException;

  public abstract void deleteIndicator(IndicatorWrapper paramIndicatorWrapper);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.DataProvider
 * JD-Core Version:    0.6.0
 */