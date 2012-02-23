package com.dukascopy.charts.chartbuilder;

import com.dukascopy.api.OfferSide;

public abstract interface IOperationManagerStrategy
{
  public abstract boolean zoomIn();

  public abstract boolean zoomOut();

  public abstract void zoomToArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract boolean scaleTimeFrame(int paramInt);

  public abstract boolean moveTimeFrame(int paramInt1, int paramInt2, boolean paramBoolean);

  public abstract boolean moveTimeFrame(int paramInt);

  public abstract void setCustomRange(int paramInt1, long paramLong, int paramInt2);

  public abstract void moveTimeFrame(long paramLong1, long paramLong2);

  public abstract boolean moveValueFrame(int paramInt1, int paramInt2);

  public abstract void shiftChartToFront();

  public abstract void scaleMainValueFrame(boolean paramBoolean, int paramInt);

  public abstract void scaleSubValueFrame(SubIndicatorGroup paramSubIndicatorGroup, boolean paramBoolean);

  public abstract void offerSideChanged(OfferSide paramOfferSide);

  public abstract void setVerticalChartMovementEnabled(boolean paramBoolean);

  public abstract double getChartMinPrice();

  public abstract double getChartMaxPrice();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IOperationManagerStrategy
 * JD-Core Version:    0.6.0
 */