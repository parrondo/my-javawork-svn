package com.dukascopy.api.impl.connect;

public abstract interface StrategyListener
{
  public abstract void strategyStartingFailed();

  public abstract void strategyStarted(long paramLong);

  public abstract void strategyStopped(long paramLong);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.StrategyListener
 * JD-Core Version:    0.6.0
 */