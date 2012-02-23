package com.dukascopy.charts.main.interfaces;

import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IChart.Type;
import com.dukascopy.api.IChartObject;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.charts.listener.ChartModeChangeListener;
import com.dukascopy.charts.listener.DisableEnableListener;
import com.dukascopy.charts.persistence.ChartBean;
import com.dukascopy.charts.persistence.IChartClient;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public abstract interface DDSChartsController
{
  public abstract JPanel getChartPanel(Integer paramInteger);

  public abstract JPanel createNewChartOrGetById(ChartBean paramChartBean);

  public abstract void removeChart(Integer paramInteger);

  public abstract IChart getIChartBy(Instrument paramInstrument);

  public abstract Set<IChart> getICharts(Instrument paramInstrument);

  public abstract IChart getIChartBy(Integer paramInteger);

  public abstract IChart getLastActiveIChart();

  public abstract void startDrawing(Integer paramInteger, IChart.Type paramType);

  public abstract void add(Integer paramInteger, List<IChartObject> paramList);

  public abstract List<IChartObject> getMainChartDrawings(Integer paramInteger);

  public abstract void addDrawingToIndicator(int paramInt1, int paramInt2, int paramInt3, IChartObject paramIChartObject);

  public abstract void reactivate(Integer paramInteger);

  public abstract void remove(Integer paramInteger, IChartObject paramIChartObject);

  public abstract void remove(Integer paramInteger, List<IChartObject> paramList);

  public abstract void removeAllDrawings(int paramInt);

  public abstract void selectCustomRange(Integer paramInteger, List<JForexPeriod> paramList);

  public abstract void addIndicator(Integer paramInteger, IndicatorWrapper paramIndicatorWrapper);

  public abstract void addIndicator(Integer paramInteger1, Integer paramInteger2, IndicatorWrapper paramIndicatorWrapper);

  public abstract void addIndicators(Integer paramInteger, List<IndicatorWrapper> paramList);

  public abstract void editIndicator(Integer paramInteger, int paramInt, IndicatorWrapper paramIndicatorWrapper);

  public abstract void deleteIndicator(Integer paramInteger, IndicatorWrapper paramIndicatorWrapper);

  public abstract void deleteIndicators(Integer paramInteger, List<IndicatorWrapper> paramList);

  public abstract void changeLineType(Integer paramInteger, DataType.DataPresentationType paramDataPresentationType);

  public abstract void changeTickType(Integer paramInteger, DataType.DataPresentationType paramDataPresentationType);

  public abstract void changeAggregationPeriod(Integer paramInteger, Period paramPeriod);

  public abstract void switchBidAskTo(Integer paramInteger, OfferSide paramOfferSide);

  public abstract void zoomIn(Integer paramInteger);

  public abstract void zoomOut(Integer paramInteger);

  public abstract void startZoomingToArea(Integer paramInteger);

  public abstract void shiftChartToFront(Integer paramInteger);

  public abstract void shiftChartSilent(Integer paramInteger, int paramInt);

  public abstract void shiftChartSilent(Integer paramInteger, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public abstract void navigateToDrawing(Integer paramInteger, IChartObject paramIChartObject);

  public abstract void switchMouseCursor(Integer paramInteger, boolean paramBoolean);

  public abstract void switchRangeScrollBarVisibility(Integer paramInteger);

  public abstract void setVerticalChartMovementEnabled(Integer paramInteger, boolean paramBoolean);

  public abstract void addProgressListener(int paramInt, ProgressListener paramProgressListener);

  public abstract void addEnableDisableListener(int paramInt, DisableEnableListener paramDisableEnableListener);

  public abstract void addChartsActionListener(Integer paramInteger, DDSChartsActionListener paramDDSChartsActionListener);

  public abstract void addChartModeChangeListener(int paramInt, ChartModeChangeListener paramChartModeChangeListener);

  public abstract void startLoadingData(Integer paramInteger, boolean paramBoolean, int paramInt);

  public abstract void saveWorkspaceImageToFile(Integer paramInteger, File paramFile);

  public abstract void saveWorkspaceImageToClipboard(Integer paramInteger);

  public abstract void printWorkspaceImage(Integer paramInteger);

  public abstract List<IndicatorWrapper> createAddEditIndicatorsDialog(int paramInt);

  public abstract JPopupMenu createPopupMenuForDrawing(int paramInt, IChartObject paramIChartObject, Component paramComponent, Point paramPoint);

  public abstract void refreshChartsContent();

  public abstract void dispose();

  public abstract void registerGuiObserversFor(int paramInt, JComponent[] paramArrayOfJComponent);

  public abstract Set<Integer> getChartControllerIdies();

  public abstract void changeInstrument(Integer paramInteger, Instrument paramInstrument);

  public abstract ChartBean synchronizeAndGetChartBean(Integer paramInteger);

  public abstract List<IndicatorWrapper> getIndicators(Integer paramInteger);

  public abstract List<IChartObject> getSubChartDrawings(Integer paramInteger, int paramInt1, int paramInt2);

  public abstract void saveChartTableDataToFile(Integer paramInteger, File paramFile);

  public abstract void applyQuickTableDataFilter(Integer paramInteger, String paramString);

  public abstract void changePriceRange(Integer paramInteger, PriceRange paramPriceRange);

  public abstract void changePriceRangePresentationType(Integer paramInteger, DataType.DataPresentationType paramDataPresentationType);

  public abstract void changeJForexPeriod(Integer paramInteger, JForexPeriod paramJForexPeriod);

  public abstract void changePointAndFigurePresentationType(Integer paramInteger, DataType.DataPresentationType paramDataPresentationType);

  public abstract void changeTickBarPresentationType(Integer paramInteger, DataType.DataPresentationType paramDataPresentationType);

  public abstract void changeRenkoPresentationType(Integer paramInteger, DataType.DataPresentationType paramDataPresentationType);

  public abstract void setChartClient(IChartClient paramIChartClient);

  public abstract IChartClient getChartClient();

  public abstract IChartObject getDrawingByKey(int paramInt, String paramString);

  public abstract boolean setTheme(int paramInt, String paramString);

  public abstract String getTheme(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.main.interfaces.DDSChartsController
 * JD-Core Version:    0.6.0
 */