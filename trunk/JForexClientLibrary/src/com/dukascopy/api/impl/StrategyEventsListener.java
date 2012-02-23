package com.dukascopy.api.impl;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;

public abstract interface StrategyEventsListener
{
  public abstract void onTick(Instrument paramInstrument, ITick paramITick);

  public abstract void onBar(Instrument paramInstrument, Period paramPeriod, IBar paramIBar1, IBar paramIBar2);

  public abstract void onMessage(IMessage paramIMessage);

  public abstract void onAccount(IAccount paramIAccount);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.StrategyEventsListener
 * JD-Core Version:    0.6.0
 */