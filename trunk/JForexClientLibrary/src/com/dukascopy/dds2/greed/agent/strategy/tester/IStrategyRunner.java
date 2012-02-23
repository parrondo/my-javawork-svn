package com.dukascopy.dds2.greed.agent.strategy.tester;

import com.dukascopy.api.IOrder.State;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.impl.StrategyEventsListener;
import com.dukascopy.api.impl.TimedData;
import com.dukascopy.api.impl.execution.ScienceWaitForUpdate;
import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
import com.dukascopy.charts.data.datacache.IFeedDataProvider;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract interface IStrategyRunner
{
  public abstract void runUntilChange(ScienceWaitForUpdate paramScienceWaitForUpdate, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;

  public abstract void runUntilChange(ScienceWaitForUpdate paramScienceWaitForUpdate, long paramLong, TimeUnit paramTimeUnit, IOrder.State[] paramArrayOfState)
    throws InterruptedException, JFException;

  public abstract Set<Instrument> getInstruments();

  public abstract long perfStartTime();

  public abstract void perfStopTime(long paramLong, ITesterReport.PerfStats paramPerfStats);

  public abstract boolean isStrategyThread();

  public abstract boolean wasCanceled();

  public abstract ITesterClient.DataLoadingMethod getDataLoadingMethod();

  public abstract long getFrom();

  public abstract long getTo();

  public abstract IFeedDataProvider getFeedDataProvider();

  public abstract <TD extends TimedData> void addDataLoadingThread(IDataLoadingThread<TD> paramIDataLoadingThread);

  public abstract <TD extends TimedData> void removeDataLoadingThread(IDataLoadingThread<TD> paramIDataLoadingThread);

  public abstract <TD extends TimedData> boolean containsDataLoadingThread(Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide);

  public abstract long getLastTickTime();

  public abstract void addStrategyEventsListener(StrategyEventsListener paramStrategyEventsListener);

  public abstract void removeStrategyEventsListener();

  public abstract StrategyEventsListener getStrategyEventsListener();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner
 * JD-Core Version:    0.6.0
 */