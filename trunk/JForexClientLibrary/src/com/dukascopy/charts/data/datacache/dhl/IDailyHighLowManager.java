package com.dukascopy.charts.data.datacache.dhl;

import com.dukascopy.api.Instrument;
import java.util.List;
import java.util.Map;

public abstract interface IDailyHighLowManager
{
  public abstract void addDailyHighLowListener(Instrument paramInstrument, IHighLowListener paramIHighLowListener);

  public abstract void removeDailyHighLowListener(IHighLowListener paramIHighLowListener);

  public abstract List<IHighLowListener> getDailyHighLowListeners(Instrument paramInstrument);

  public abstract Map<Instrument, List<IHighLowListener>> getDailyHighLowListeners();

  public abstract void removeAllListeners();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.dhl.IDailyHighLowManager
 * JD-Core Version:    0.6.0
 */