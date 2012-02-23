package com.dukascopy.charts.chartbuilder;

import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.tablebuilder.ITablePresentationManager;
import com.dukascopy.charts.tablebuilder.component.table.DataTablePresentationAbstractJTable;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

public abstract interface GuiRefresher
{
  public abstract void refreshMainContent();

  public abstract void refreshSubContentByIndicatorId(Integer paramInteger);

  public abstract void refreshSubContentBySubViewId(int paramInt);

  public abstract void refreshAllContent();

  public abstract void repaintMainContent();

  public abstract void refreshSubContents();

  public abstract void repaintSubContentBySubViewId(int paramInt);

  public abstract void invalidateMainContent();

  public abstract void invalidateAllContent();

  public abstract Integer getBasicIndicatorIdByWindowIndex(int paramInt);

  public abstract int getWindowsCount();

  public abstract JComponent getChartsContainer();

  public abstract JComponent getMainContainer();

  public abstract void setFocusToMainChartView();

  public abstract DataTablePresentationAbstractJTable<?, ?> getCurrentChartDataTable();

  public abstract void createMainChartView();

  public abstract int createSubChartView();

  public abstract void createSubChartView(Integer paramInteger);

  public abstract void deleteSubChartView(Integer paramInteger);

  public abstract boolean isSubChartLast(int paramInt);

  public abstract Integer getSubChartViewIdFor(int paramInt);

  public abstract void addSubIndicatorToSubChartView(int paramInt, IndicatorWrapper paramIndicatorWrapper);

  public abstract int deleteSubIndicatorFromSubChartView(int paramInt, IndicatorWrapper paramIndicatorWrapper);

  public abstract void deleteSubChartViewsIfNecessary(List<IndicatorWrapper> paramList);

  public abstract boolean isIndicatorShownOnSubWindow(int paramInt);

  public abstract boolean isSubViewEmpty(Integer paramInteger);

  public abstract boolean doesSubViewExists(Integer paramInteger);

  public abstract ITablePresentationManager getCandleTablePresentationManager();

  public abstract ITablePresentationManager getTickTablePresentationManager();

  public abstract ITablePresentationManager getPriceRangeTablePresentationManager();

  public abstract ITablePresentationManager getPointAndFigureTablePresentationManager();

  public abstract ITablePresentationManager getTickBarTablePresentationManager();

  public abstract ITablePresentationManager getRenkoTablePresentationManager();

  public abstract JLayeredPane getChartsLayeredPane();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.GuiRefresher
 * JD-Core Version:    0.6.0
 */