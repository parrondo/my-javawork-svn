package com.dukascopy.charts.mappers.value;

import com.dukascopy.api.Instrument;

public abstract interface IValueToYMapper
{
  public abstract double vy(int paramInt);

  public abstract int yv(double paramDouble);

  public abstract int getHeight();

  public abstract boolean isYOutOfRange(int paramInt);

  public abstract float getValuesInOnePixel();

  public abstract void computeGeometry(int paramInt);

  public abstract void computeGeometry(double paramDouble1, double paramDouble2);

  public abstract void computeGeometry();

  public abstract void setPadding(double paramDouble);

  public abstract double getPadding();

  public abstract Instrument getInstrument();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.value.IValueToYMapper
 * JD-Core Version:    0.6.0
 */