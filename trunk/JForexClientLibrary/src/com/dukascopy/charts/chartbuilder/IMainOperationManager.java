package com.dukascopy.charts.chartbuilder;

import com.dukascopy.api.IChart.Type;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.charts.dialogs.customrange.CustomRange;

public abstract interface IMainOperationManager
{
  public abstract void setCustomRange(CustomRange paramCustomRange);

  public abstract void setCustomRange(JForexPeriod paramJForexPeriod, int paramInt1, long paramLong, int paramInt2);

  public abstract void setCustomRange(int paramInt1, long paramLong, int paramInt2);

  public abstract void moveTimeFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public abstract void moveTimeFrameSilent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public abstract void moveTimeFrame(int paramInt);

  public abstract void moveTimeFrameSilent(int paramInt);

  public abstract void moveTimeFrame(long paramLong1, long paramLong2);

  public abstract void scaleTimeFrame(int paramInt);

  public abstract void zoomIn();

  public abstract void zoomOut();

  public abstract void zoomToArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void scaleMainChartViewIn(int paramInt);

  public abstract void scaleMainChartViewOut(int paramInt);

  public abstract void scaleSubChartViewIn(SubIndicatorGroup paramSubIndicatorGroup);

  public abstract void scaleSubChartViewOut(SubIndicatorGroup paramSubIndicatorGroup);

  public abstract void shiftChartToFront();

  public abstract void setVerticalChartMovementEnabled(boolean paramBoolean);

  public abstract void startDrawing(IChart.Type paramType);

  public abstract void startZoomingToArea();

  public abstract void setPeriod(Period paramPeriod);

  public abstract void setOfferSide(OfferSide paramOfferSide);

  public abstract IOperationManagerStrategy getMainOperationManager();

  public abstract void setVerticalAxisScale(double paramDouble1, double paramDouble2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IMainOperationManager
 * JD-Core Version:    0.6.0
 */