package com.dukascopy.charts.listeners.zoomtoarea;

public abstract interface MetaDrawingsActionListener
{
  public abstract void zoomingToAreaStarted();

  public abstract void zoomingToAreaEnded();

  public abstract void measuringCandlesStarted();

  public abstract void measuringCandlesEnded();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsActionListener
 * JD-Core Version:    0.6.0
 */