package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.impl.TimedData;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import java.util.concurrent.BlockingQueue;

public abstract interface IDataLoadingThread<TD extends TimedData> extends Runnable
{
  public abstract void stopThread();

  public abstract BlockingQueue<TD> getQueue();

  public abstract JForexPeriod getJForexPeriod();

  public abstract Instrument getInstrument();

  public abstract OfferSide getOfferSide();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread
 * JD-Core Version:    0.6.0
 */