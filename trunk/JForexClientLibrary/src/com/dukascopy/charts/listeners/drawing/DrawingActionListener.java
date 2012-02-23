package com.dukascopy.charts.listeners.drawing;

import com.dukascopy.api.IChart.Type;

public abstract interface DrawingActionListener
{
  public abstract void drawingStarted(IChart.Type paramType);

  public abstract void drawingEnded();

  public abstract void drawingEditingStarted();

  public abstract void drawingEditingEnded();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.drawing.DrawingActionListener
 * JD-Core Version:    0.6.0
 */