package com.dukascopy.charts.math.dataprovider.priceaggregation.buffer;

import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
import java.util.List;

public abstract interface IPriceAggregationShiftableBuffer<D extends AbstractPriceAggregationData> extends IShiftableBuffer<D>
{
  public abstract boolean addOrReplace(D paramD);

  public abstract boolean containsStartTime(long paramLong);

  public abstract boolean containsTime(long paramLong);

  public abstract int getStartTimeIndex(long paramLong);

  public abstract int getTimeIndex(long paramLong);

  public abstract List<D> getAfterTimeInclude(long paramLong);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.buffer.IPriceAggregationShiftableBuffer
 * JD-Core Version:    0.6.0
 */