package com.dukascopy.charts.view.displayabledatapart;

import com.dukascopy.api.IChartObject;
import com.dukascopy.charts.drawings.ChartObject;
import com.dukascopy.charts.drawings.IMainDrawingsManager;
import com.dukascopy.charts.drawings.SubDrawingsManagerImpl;
import com.dukascopy.charts.drawings.TextChartObject;
import java.awt.Graphics;
import java.util.LinkedHashMap;
import java.util.Set;

public abstract interface IDrawingsManagerContainer
{
  public abstract void createSubDrawingsManagerForIndicator(int paramInt1, int paramInt2);

  public abstract void deleteSubDrawingsManagerForIndicator(int paramInt1, int paramInt2);

  public abstract void createSubDrawingsManagersMapFor(int paramInt);

  public abstract void deleteSubDrawingsManagersFor(int paramInt);

  public abstract void unselectDrawingToBeEdited();

  public abstract void remove(IChartObject paramIChartObject);

  public abstract boolean updateTextChartObject(TextChartObject paramTextChartObject);

  public abstract void updateChartObjectPricesManualy(ChartObject paramChartObject);

  public abstract void updatePipsPerBarOption(ChartObject paramChartObject);

  public abstract IMainDrawingsManager getMainDrawingsManager();

  public abstract LinkedHashMap<Integer, SubDrawingsManagerImpl> getSubDrawingsManagers(int paramInt);

  public abstract ChartObject getNewChartObject();

  public abstract void setNewChartObject(ChartObject paramChartObject);

  public abstract void drawHandlers(Graphics paramGraphics, int paramInt);

  public abstract void drawEditedDrawing(Graphics paramGraphics, int paramInt);

  public abstract void drawNewDrawing(Graphics paramGraphics, int paramInt);

  public abstract void drawAllDrawings(Graphics paramGraphics, int paramInt);

  public abstract void drawDynamicChartObjects(Graphics paramGraphics, int paramInt);

  public abstract void drawComment(Graphics paramGraphics);

  public abstract Set<Integer> getSubWindowsIds();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.IDrawingsManagerContainer
 * JD-Core Version:    0.6.0
 */