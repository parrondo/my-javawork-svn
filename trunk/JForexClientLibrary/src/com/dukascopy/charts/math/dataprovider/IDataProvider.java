package com.dukascopy.charts.math.dataprovider;

import com.dukascopy.api.Filter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.data.datacache.Data;
import java.lang.reflect.InvocationTargetException;

public abstract interface IDataProvider<D extends Data, S extends IDataSequence<D>>
{
  public abstract void start();

  public abstract long getLatestDataTime();

  public abstract long getLatestDataTime(int paramInt);

  public abstract long getFirstKnownTime();

  public abstract Object getLatestValue(int paramInt1, int paramInt2);

  public abstract void setFilter(Filter paramFilter);

  public abstract void setDailyFilterPeriod(Period paramPeriod);

  public abstract void setPeriod(Period paramPeriod);

  public abstract void setOfferSide(OfferSide paramOfferSide);

  public abstract void setInstrument(Instrument paramInstrument);

  public abstract Filter getFilter();

  public abstract Period getDailyFilterPeriod();

  public abstract Period getPeriod();

  public abstract OfferSide getOfferSide();

  public abstract Instrument getInstrument();

  public abstract S getDataSequence(int paramInt1, long paramLong, int paramInt2);

  public abstract S getBufferedDataSequence(int paramInt1, long paramLong, int paramInt2);

  public abstract void addDataChangeListener(DataChangeListener paramDataChangeListener);

  public abstract void removeDataChangeListener(DataChangeListener paramDataChangeListener);

  public abstract boolean containsIndicator(int paramInt);

  public abstract void addIndicator(IndicatorWrapper paramIndicatorWrapper)
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

  public abstract void editIndicator(int paramInt)
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

  public abstract void removeIndicator(int paramInt);

  public abstract void removeIndicators(int[] paramArrayOfInt);

  public abstract void removeAllIndicators();

  public abstract int[] getIndicatorIds();

  public abstract void setActive(boolean paramBoolean);

  public abstract boolean isActive();

  public abstract void dispose();

  public abstract D getLastKnownData();

  public abstract S getDataSequence(long paramLong1, long paramLong2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.IDataProvider
 * JD-Core Version:    0.6.0
 */