package com.dukascopy.charts.data;

import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.data.datacache.Data;
import com.dukascopy.charts.math.dataprovider.DataChangeListener;
import com.dukascopy.charts.math.dataprovider.IDataSequence;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract interface IDataSequenceProvider<D extends Data, DataSequenceClass extends IDataSequence<D>>
{
  public abstract void addDataChangeListener(DataChangeListener paramDataChangeListener);

  public abstract void removeDataChangeListener(DataChangeListener paramDataChangeListener);

  public abstract long getLastTickTime();

  public abstract DataSequenceClass getDataSequence();

  public abstract DataSequenceClass getBufferedDataSequence(int paramInt1, int paramInt2);

  public abstract void setActive(boolean paramBoolean);

  public abstract boolean isActive();

  public abstract void dispose();

  public abstract boolean containsIndicator(IndicatorWrapper paramIndicatorWrapper);

  public abstract void addIndicator(IndicatorWrapper paramIndicatorWrapper)
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

  public abstract void editIndicator(IndicatorWrapper paramIndicatorWrapper)
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

  public abstract void removeIndicator(IndicatorWrapper paramIndicatorWrapper);

  public abstract void removeIndicators(List<IndicatorWrapper> paramList);

  public abstract void removeAllIndicators();

  public abstract long getIndicatorLatestTime(int paramInt);

  public abstract Object getIndicatorLatestValue(int paramInt1, int paramInt2);

  public abstract D getLastKnownData();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.IDataSequenceProvider
 * JD-Core Version:    0.6.0
 */