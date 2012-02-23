package com.dukascopy.api.impl.connect;

import com.dukascopy.api.IStrategy;
import com.dukascopy.api.IStrategyListener;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.system.ISystemListener;
import java.io.File;
import java.util.Map;
import java.util.Set;

public abstract interface ISystemListenerExtended extends ISystemListener
{
  public abstract void subscribeToInstruments(Set<Instrument> paramSet);

  public abstract Set<Instrument> getSubscribedInstruments();

  public abstract long startStrategy(File paramFile, IStrategyListener paramIStrategyListener, Map<String, Object> paramMap, boolean paramBoolean)
    throws JFException;

  public abstract long startStrategy(IStrategy paramIStrategy, IStrategyListener paramIStrategyListener, boolean paramBoolean)
    throws JFException;

  public abstract void stopStrategy(long paramLong);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.ISystemListenerExtended
 * JD-Core Version:    0.6.0
 */