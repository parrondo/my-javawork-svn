package com.dukascopy.charts.chartbuilder;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.charts.listeners.datachange.MainDataChangeListener;
import com.dukascopy.charts.main.interfaces.ProgressListener;

public abstract interface IDataManager
{
  public abstract void addMainDataChangeListener(MainDataChangeListener paramMainDataChangeListener);

  public abstract void addProgressListener(ProgressListener paramProgressListener);

  public abstract double getMinPriceFor(Integer paramInteger);

  public abstract double getMaxPriceFor(Integer paramInteger);

  public abstract void setSequenceSize(int paramInt);

  public abstract int getSequenceSize();

  public abstract void setTime(long paramLong);

  public abstract long getTime();

  public abstract void setPeriod(Period paramPeriod1, Period paramPeriod2);

  public abstract Period getPeriod();

  public abstract void setInstrument(Instrument paramInstrument1, Instrument paramInstrument2);

  public abstract Instrument getInstrument();

  public abstract void shift(int paramInt);

  public abstract void start();

  public abstract void dispose();

  public abstract long getMinimalTime(Period paramPeriod);

  public abstract void changeDataType(DataType paramDataType1, DataType paramDataType2);

  public abstract DataType getDataType();

  public abstract void priceRangeChanged(PriceRange paramPriceRange);

  public abstract PriceRange getPriceRange();

  public abstract void reversalAmountChanged(ReversalAmount paramReversalAmount);

  public abstract void setJForexPeriod(JForexPeriod paramJForexPeriod);

  public abstract void reactivate();

  public abstract ReversalAmount getReversalAmount();

  public abstract TickBarSize getTickBarSize();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IDataManager
 * JD-Core Version:    0.6.0
 */