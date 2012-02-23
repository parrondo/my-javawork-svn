package com.dukascopy.charts.orders;

import com.dukascopy.charts.orders.orderparts.ClosingLine;
import com.dukascopy.charts.orders.orderparts.ClosingPoint;
import com.dukascopy.charts.orders.orderparts.EntryLine;
import com.dukascopy.charts.orders.orderparts.HorizontalLine;
import com.dukascopy.charts.orders.orderparts.MergingLine;
import com.dukascopy.charts.orders.orderparts.OpeningPoint;
import com.dukascopy.charts.orders.orderparts.StopLossLine;
import com.dukascopy.charts.orders.orderparts.TakeProfitLine;
import java.awt.event.MouseEvent;
import java.util.Map;

public abstract interface OrdersDrawingManager
{
  public abstract HorizontalLine[] getSelectedHorizontalLines();

  public abstract Map<String, EntryLine> getEntryLines();

  public abstract Map<String, StopLossLine> getStopLossLines();

  public abstract Map<String, TakeProfitLine> getTakeProfitLines();

  public abstract Map<String, OpeningPoint> getOpeningPoints();

  public abstract Map<String, MergingLine> getMergingLines();

  public abstract Map<String, ClosingLine> getClosingLines();

  public abstract Map<String, ClosingPoint> getClosingPoints();

  public abstract void prepareOrderParts();

  public abstract String getToolTipText(MouseEvent paramMouseEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.OrdersDrawingManager
 * JD-Core Version:    0.6.0
 */