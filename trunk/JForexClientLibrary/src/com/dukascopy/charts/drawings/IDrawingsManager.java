package com.dukascopy.charts.drawings;

import com.dukascopy.api.IChartObject;
import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
import java.awt.Point;
import java.util.List;

public abstract interface IDrawingsManager
{
  public abstract ChartsActionListenerRegistry getChartActionListenerRegistry();

  public abstract IChartObject getNewChartObject();

  public abstract ChartObject getEditedChartObject();

  public abstract ChartObject getHighlightedChartObject();

  public abstract boolean isEditingDrawing();

  public abstract void drawingNew(IChartObject paramIChartObject);

  public abstract boolean isDrawingNew();

  public abstract void finishDrawingNewDrawing(ChartObject paramChartObject);

  public abstract boolean addNewPointToNewDrawing(ChartObject paramChartObject, Point paramPoint);

  public abstract void modifyNewDrawing(ChartObject paramChartObject, Point paramPoint, boolean paramBoolean);

  public abstract void modifyEditingDrawing(Point paramPoint);

  public abstract void updatePrevPointAndSelectedHandler(Point paramPoint);

  public abstract boolean isSomeDrawingHighlighted();

  public abstract boolean triggerHighlighting(Point paramPoint);

  public abstract void selectHighlitedDrawing();

  public abstract boolean selectDrawingToBeEditedAndStartEditingDrawing(Point paramPoint);

  public abstract void selectDrawingToBeEdited(Point paramPoint);

  public abstract void selectDrawing(IChartObject paramIChartObject);

  public abstract void unselectDrawingToBeEdited();

  public abstract void unselectDrawingToBeEditedAndExitEditingMode();

  public abstract boolean intersectsDrawingToBeEdited(Point paramPoint);

  public abstract boolean intersectsDrawing(Point paramPoint);

  public abstract void deleteSelectedDrawing();

  public abstract boolean updateTextChartObject(TextChartObject paramTextChartObject);

  public abstract void updateChartObjectPricesManualy(ChartObject paramChartObject);

  public abstract void updatePipsPerBarOption(ChartObject paramChartObject);

  public abstract void addChartObjects(List<IChartObject> paramList);

  public abstract void addChartObject(IChartObject paramIChartObject);

  public abstract void remove(IChartObject paramIChartObject);

  public abstract void remove(List<IChartObject> paramList);

  public abstract void moveEditedDrawingLeft();

  public abstract void moveEditedDrawingRight();

  public abstract void moveEditedDrawingDown();

  public abstract void moveEditedDrawingUp();

  public abstract void mouseWheelUp();

  public abstract void mouseWheelDown();

  public abstract IChartObject getDrawingByKey(String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.IDrawingsManager
 * JD-Core Version:    0.6.0
 */