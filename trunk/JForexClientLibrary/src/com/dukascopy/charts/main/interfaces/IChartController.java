package com.dukascopy.charts.main.interfaces;

import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IChart.Type;
import com.dukascopy.api.IChartObject;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.chartbuilder.ChartState;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.charts.listener.ChartModeChangeListener;
import com.dukascopy.charts.listener.DisableEnableListener;
import com.dukascopy.charts.mappers.time.GeometryCalculator;
import com.dukascopy.charts.persistence.ChartBean;
import com.dukascopy.charts.persistence.ITheme;
import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public abstract interface IChartController
{
  public abstract ChartBean getChartBean();

  public abstract JComponent getChartsContainer();

  public abstract JComponent getMainContainer();

  public abstract void addMainChart();

  public abstract IChart getIChart();

  public abstract void startDrawing(IChart.Type paramType);

  public abstract void addDrawings(List<IChartObject> paramList);

  public abstract List<IChartObject> getMainChartDrawings();

  public abstract void addDrawingToIndicator(Integer paramInteger, int paramInt, IChartObject paramIChartObject);

  public abstract void remove(IChartObject paramIChartObject);

  public abstract void remove(List<IChartObject> paramList);

  public abstract void removeAllDrawings();

  public abstract void selectDrawing(IChartObject paramIChartObject);

  public abstract void unselectDrawingToBeEdited();

  public abstract List<IndicatorWrapper> getIndicators();

  public abstract void addIndicators(List<IndicatorWrapper> paramList);

  public abstract void addIndicator(IndicatorWrapper paramIndicatorWrapper);

  public abstract void addIndicatorOnSubWin(Integer paramInteger, IndicatorWrapper paramIndicatorWrapper);

  public abstract void editIndicator(int paramInt, IndicatorWrapper paramIndicatorWrapper);

  public abstract void deleteIndicator(IndicatorWrapper paramIndicatorWrapper);

  public abstract void deleteIndicators(List<IndicatorWrapper> paramList);

  public abstract void setPeriod(Period paramPeriod);

  public abstract void setInstrument(Instrument paramInstrument);

  public abstract void setOfferSide(OfferSide paramOfferSide);

  public abstract void setLineType(DataType.DataPresentationType paramDataPresentationType);

  public abstract void setTickType(DataType.DataPresentationType paramDataPresentationType);

  public abstract void setMouseCursorVisible(boolean paramBoolean);

  public abstract void zoomIn();

  public abstract void zoomOut();

  public abstract void startZoomingToArea();

  public abstract void shiftChartToFront();

  public abstract void shiftChartSilent(int paramInt);

  public abstract void shiftChart(int paramInt);

  public abstract void setCustomRange(int paramInt1, long paramLong, int paramInt2);

  public abstract void setVerticalChartMovementEnabled(boolean paramBoolean);

  public abstract void moveTimeFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public abstract void moveTimeFrameSilent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public abstract void addProgressListener(ProgressListener paramProgressListener);

  public abstract void addDisabledEnableListener(DisableEnableListener paramDisableEnableListener);

  public abstract void addChartsActionListener(DDSChartsActionListener paramDDSChartsActionListener);

  public abstract void addChartModeChangeListener(ChartModeChangeListener paramChartModeChangeListener);

  public abstract void startLoadingData(boolean paramBoolean, int paramInt);

  public abstract void dispose();

  public abstract void refreshContent();

  public abstract void saveWorkspaceImageToFile(File paramFile);

  public abstract void saveWorkspaceImageToClipboard();

  public abstract void printWorkspaceImage();

  public abstract BufferedImage getWorkspaceImage();

  public abstract void selectCustomRange(List<JForexPeriod> paramList);

  public abstract List<IndicatorWrapper> createAddEditIndicatorsDialog();

  public abstract JPopupMenu createPopupMenuForDrawings(IChartObject paramIChartObject, Component paramComponent, Point paramPoint);

  public abstract long getTime();

  public abstract ChartState getChartState();

  public abstract double getYAxisPadding();

  public abstract GeometryCalculator getGeometryCalculator();

  public abstract List<IChartObject> getSubChartDrawings(int paramInt1, int paramInt2);

  public abstract void saveChartTableDataToFile(File paramFile);

  public abstract void applyQuickTableDataFilter(String paramString);

  public abstract void changePriceRange(PriceRange paramPriceRange);

  public abstract void changePriceRangePresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract void setOrderLineVisible(String paramString, boolean paramBoolean);

  public abstract void changeJForexPeriod(JForexPeriod paramJForexPeriod);

  public abstract void reactivate();

  public abstract void changeReversalAmount(ReversalAmount paramReversalAmount);

  public abstract void changePointAndFigurePresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract double getMinPrice();

  public abstract double getMaxPrice();

  public abstract void changeTickBarPresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract void changeRenkoPresentationType(DataType.DataPresentationType paramDataPresentationType);

  public abstract IChartObject getDrawingByKey(String paramString);

  public abstract void setTheme(ITheme paramITheme);

  public abstract ITheme getTheme();

  public abstract void setVerticalAxisScale(double paramDouble1, double paramDouble2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.main.interfaces.IChartController
 * JD-Core Version:    0.6.0
 */