package com.dukascopy.charts.drawings;

import com.dukascopy.api.IChart.Type;
import com.dukascopy.api.IChartObject;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;

public abstract interface IMainDrawingsManager extends IDrawingsManager
{
  public abstract void addComment(String paramString);

  public abstract void setCommentHorizontalPosition(int paramInt);

  public abstract int getCommentHorizontalPosition();

  public abstract void setCommentVerticalPosition(int paramInt);

  public abstract int getCommentVerticalPosition();

  public abstract void setCommentFont(Font paramFont);

  public abstract Font getCommentFont();

  public abstract void setCommentColor(Color paramColor);

  public abstract Color getCommentColor();

  public abstract IChartObject draw(String paramString, IChart.Type paramType, long paramLong1, double paramDouble1, long paramLong2, double paramDouble2, long paramLong3, double paramDouble3);

  public abstract IChartObject draw(String paramString, IChart.Type paramType, long paramLong1, double paramDouble1, long paramLong2, double paramDouble2);

  public abstract IChartObject draw(String paramString, IChart.Type paramType, long paramLong, double paramDouble);

  public abstract IChartObject drawUnlocked(String paramString, IChart.Type paramType, long paramLong1, double paramDouble1, long paramLong2, double paramDouble2, long paramLong3, double paramDouble3);

  public abstract IChartObject drawUnlocked(String paramString, IChart.Type paramType, long paramLong1, double paramDouble1, long paramLong2, double paramDouble2);

  public abstract IChartObject drawUnlocked(String paramString, IChart.Type paramType, long paramLong, double paramDouble);

  public abstract IChartObject get(String paramString);

  public abstract IChartObject remove(String paramString);

  public abstract List<IChartObject> removeChartObjectsByKeys(List<String> paramList);

  public abstract List<IChartObject> getAll();

  public abstract int size();

  public abstract Iterator<IChartObject> iterator();

  public abstract void removeAllDrawings();

  public abstract boolean intersectsGlobalDrawing(Point paramPoint);

  public abstract boolean intersectsGlobalDrawingToBeEdited(Point paramPoint);

  public abstract boolean isEditingGlobalDrawing();

  public abstract boolean triggerGlobalHighlighting(Point paramPoint);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.IMainDrawingsManager
 * JD-Core Version:    0.6.0
 */