package com.dukascopy.dds2.greed.gui.component.tree;

import com.dukascopy.api.DataType;
import com.dukascopy.api.IChartObject;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.api.impl.ServiceWrapper;
import com.dukascopy.api.impl.StrategyWrapper;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.dds2.greed.agent.strategy.tester.TesterChartData;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

public abstract interface WorkspaceTreeController
{
  public abstract void indicatorAdded(int paramInt1, int paramInt2, IndicatorWrapper paramIndicatorWrapper);

  public abstract void indicatorChanged(Integer paramInteger, IndicatorWrapper paramIndicatorWrapper);

  public abstract void indicatorRemoved(Integer paramInteger, IndicatorWrapper paramIndicatorWrapper);

  public abstract void openChartsForInstruments(Map<Instrument, TesterChartData> paramMap, String paramString);

  public abstract void closeChart(int paramInt);

  public abstract void drawingAdded(int paramInt, IChartObject paramIChartObject);

  public abstract void drawingChanged(int paramInt, IChartObject paramIChartObject);

  public abstract void drawingRemoved(int paramInt, IChartObject paramIChartObject);

  public abstract void periodChanged(Integer paramInteger, Period paramPeriod);

  public abstract void chartClosed(int paramInt);

  public abstract void setInstruments(List<String> paramList);

  public abstract void setSelectedInstrumentByPanelId(int paramInt);

  public abstract void instrumentChanged(int paramInt, Instrument paramInstrument);

  public abstract void dataTypeChanged(int paramInt, DataType paramDataType);

  public abstract void priceRangeChanged(int paramInt, PriceRange paramPriceRange);

  public abstract void jForexPeriodChanged(int paramInt, JForexPeriod paramJForexPeriod);

  public abstract void strategyAdded(StrategyNewBean paramStrategyNewBean);

  public abstract void strategyRemoved(StrategyNewBean paramStrategyNewBean);

  public abstract void deleteStrategyById(int paramInt);

  public abstract void renameServiceByName(int paramInt, File paramFile);

  public abstract void selectNode(int paramInt);

  public abstract ServiceWrapper getServiceWrapperById(int paramInt);

  public abstract Map<Integer, StrategyWrapper> getStrategies();

  public abstract void addStrategyListChangeListener(ActionListener paramActionListener);

  public abstract void removeStrategyListChangeListener(ActionListener paramActionListener);

  public abstract void restoreExpandedStatus();

  public abstract void strategyUpdated(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController
 * JD-Core Version:    0.6.0
 */