package com.dukascopy.charts.main.interfaces;

import com.dukascopy.api.DataType;
import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IChartObject;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import java.util.List;

public abstract interface DDSChartsActionListener
{
  public abstract void indicatorAdded(IndicatorWrapper paramIndicatorWrapper, int paramInt);

  public abstract void indicatorChanged(IndicatorWrapper paramIndicatorWrapper, int paramInt);

  public abstract void indicatorRemoved(IndicatorWrapper paramIndicatorWrapper);

  public abstract void indicatorsRemoved(List<IndicatorWrapper> paramList);

  public abstract void periodChanged(Period paramPeriod);

  public abstract void offerSideChanged(OfferSide paramOfferSide);

  public abstract void filterChanged(Filter paramFilter);

  public abstract void drawingAdded(IChartObject paramIChartObject);

  public abstract void drawingAdded(int paramInt, IChartObject paramIChartObject);

  public abstract void drawingRemoved(IChartObject paramIChartObject);

  public abstract void drawingRemoved(int paramInt, IChartObject paramIChartObject);

  public abstract void drawingsRemoved(List<IChartObject> paramList);

  public abstract void drawingChanged(IChartObject paramIChartObject);

  public abstract void timeFrameMoved(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt);

  public abstract void candleTypeChanged(DataType.DataPresentationType paramDataPresentationType);

  public abstract void tickTypeChanged(DataType.DataPresentationType paramDataPresentationType);

  public abstract void gridVisibilityChanged(boolean paramBoolean);

  public abstract void mouseCursorVisibilityChanged(boolean paramBoolean);

  public abstract void lastCandleVisibilityChanged(boolean paramBoolean);

  public abstract void verticalChartMovementChanged(boolean paramBoolean);

  public abstract void timeFrameMoved(boolean paramBoolean);

  public abstract void timeFrameMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public abstract void timeFrameMoved(boolean paramBoolean, int paramInt);

  public abstract void zoomOutEnabled(boolean paramBoolean);

  public abstract void zoomInEnabled(boolean paramBoolean);

  public abstract void instrumentChanged(Instrument paramInstrument);

  public abstract void chartObjectCreatedForNewDrawing(IChartObject paramIChartObject);

  public abstract void dataTypeChanged(DataType paramDataType);

  public abstract void priceRangesPresentationTypeChanged(DataType.DataPresentationType paramDataPresentationType);

  public abstract void priceRangeChanged(PriceRange paramPriceRange);

  public abstract void jForexPeriodChanged(JForexPeriod paramJForexPeriod);

  public abstract void reversalAmountChanged(ReversalAmount paramReversalAmount);

  public abstract void pointAndFigurePresentationTypeChanged(DataType.DataPresentationType paramDataPresentationType);

  public abstract void tickBarPresentationTypeChanged(DataType.DataPresentationType paramDataPresentationType);

  public abstract void renkoPresentationTypeChanged(DataType.DataPresentationType paramDataPresentationType);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.main.interfaces.DDSChartsActionListener
 * JD-Core Version:    0.6.0
 */