package com.dukascopy.charts.chartbuilder;

import com.dukascopy.api.DataType;
import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.charts.persistence.ITheme;
import java.awt.Point;

public abstract interface ChartState
{
  public abstract boolean isReadOnly();

  public abstract boolean isMouseCrossCursorVisible();

  public abstract void setMouseCursorVisible(boolean paramBoolean);

  public abstract void changeMouseCursorWindowLocation(int paramInt);

  public abstract void mouseCrossCursorChanged(Point paramPoint);

  public abstract boolean isMouseCursorOnWindow(int paramInt);

  public abstract Point getMouseCursorPoint();

  public abstract Instrument getInstrument();

  public abstract void setInstrument(Instrument paramInstrument);

  public abstract Period getPeriod();

  public abstract void setPeriod(Period paramPeriod);

  public abstract OfferSide getOfferSide();

  public abstract void setOfferSide(OfferSide paramOfferSide);

  public abstract DataType.DataPresentationType getTickType();

  public abstract void setTickType(DataType.DataPresentationType paramDataPresentationType);

  public abstract DataType.DataPresentationType getCandleType();

  public abstract void setCandleType(DataType.DataPresentationType paramDataPresentationType);

  public abstract int getChartShiftHandlerCoordinate();

  public abstract void setChartShiftHandlerCoordinate(int paramInt);

  public abstract boolean isChartShiftActive();

  public abstract void setChartShiftActive(boolean paramBoolean);

  public abstract boolean isVerticalChartMovementEnabled();

  public abstract void setVerticalChartMovementEnabled(boolean paramBoolean);

  public abstract void setPriceRangesPresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract DataType.DataPresentationType getPriceRangesPresentationType();

  public abstract DataType getDataType();

  public abstract void setDataType(DataType paramDataType);

  public abstract PriceRange getPriceRange();

  public abstract void setPriceRange(PriceRange paramPriceRange);

  public abstract void setPointAndFigurePresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract DataType.DataPresentationType getPointAndFigurePresentationType();

  public abstract void setReversalAmount(ReversalAmount paramReversalAmount);

  public abstract ReversalAmount getReversalAmount();

  public abstract void setTickBarSize(TickBarSize paramTickBarSize);

  public abstract TickBarSize getTickBarSize();

  public abstract void setTickBarPresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract DataType.DataPresentationType getTickBarPresentationType();

  public abstract void setRenkoPresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract DataType.DataPresentationType getRenkoPresentationType();

  public abstract void setTheme(ITheme paramITheme);

  public abstract ITheme getTheme();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ChartState
 * JD-Core Version:    0.6.0
 */