package com.dukascopy.charts.chartbuilder;

import java.awt.Point;

public abstract interface MouseControllerMetaDrawingsState
{
  public abstract void setIsZoomingToArea(boolean paramBoolean);

  public abstract boolean isZoomingToArea();

  public abstract void setFirstPointZoomingToArea(Point paramPoint);

  public abstract void setSecondPointZoomingToArea(Point paramPoint);

  public abstract void setSecondFloatingPointZoomingToArea(Point paramPoint);

  public abstract Point getFirstZoomingToAreaPoint();

  public abstract Point getSecondZoomingToAreaPoint();

  public abstract void setIsMeasuringCandles(boolean paramBoolean);

  public abstract boolean isMeasuringCandlesLine();

  public abstract void set1PointMeasuringCandleLine(Point paramPoint);

  public abstract void set2PointMeasuringCandleLine(Point paramPoint);

  public abstract void set2FloatingPointMeasuringCandleLine(Point paramPoint);

  public abstract Point get1MeasuringCandlesLinePoint();

  public abstract Point get2MeasuringCandlesLinePoint();

  public abstract boolean isChartShiftHandlerBeeingShifted();

  public abstract void setChartShiftHandlerBeeingShifted(boolean paramBoolean);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState
 * JD-Core Version:    0.6.0
 */