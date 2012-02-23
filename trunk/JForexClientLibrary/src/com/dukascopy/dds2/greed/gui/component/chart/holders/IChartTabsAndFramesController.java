package com.dukascopy.dds2.greed.gui.component.chart.holders;

import com.dukascopy.api.DataType;
import com.dukascopy.api.IChart;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.impl.ServiceWrapper;
import com.dukascopy.charts.data.datacache.IFeedDataProvider;
import com.dukascopy.charts.data.datacache.JForexPeriod;
import com.dukascopy.charts.persistence.ChartBean;
import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
import com.dukascopy.dds2.greed.gui.component.chart.ChartPanel;
import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
import com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListener;
import com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler;
import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public abstract interface IChartTabsAndFramesController extends IEventHandler
{
  public abstract WorkspaceTreeController getWorkspaceController();

  public abstract void addChart(ChartBean paramChartBean, boolean paramBoolean1, boolean paramBoolean2);

  public abstract void addChart(int paramInt, Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, boolean paramBoolean1, boolean paramBoolean2);

  public abstract void addServiceSourceEditor(int paramInt, String paramString, File paramFile, ServiceSourceType paramServiceSourceType, boolean paramBoolean);

  public abstract void addServiceSourceEditor(int paramInt, String paramString, File paramFile, ServiceSourceType paramServiceSourceType, boolean paramBoolean1, boolean paramBoolean2);

  public abstract void addOrSelectInstumentTesterChart(int paramInt, String paramString, JForexPeriod paramJForexPeriod, Instrument paramInstrument, OfferSide paramOfferSide, IFeedDataProvider paramIFeedDataProvider);

  public abstract void addOrSelectInstumentTesterChart(String paramString, ChartBean paramChartBean, IFeedDataProvider paramIFeedDataProvider);

  public abstract void openServiceSourceEditor(int paramInt, File paramFile);

  public abstract JPanel createOrGetCustomMainTab(String paramString, Integer paramInteger);

  public abstract boolean selectPanel(int paramInt);

  public abstract boolean selectServiceSourceEditor(int paramInt);

  public abstract void selectLineNumber(int paramInt1, int paramInt2);

  public abstract void closeChart(int paramInt);

  public abstract void closeServiceEditor(int paramInt);

  public abstract void removeCustomMainTab(String paramString, Integer paramInteger);

  public abstract IChart getIChartBy(Instrument paramInstrument);

  public abstract ChartPanel getChartPanelByPanelId(int paramInt);

  public abstract ServiceSourceEditorPanel getEditorPanelByPanelId(int paramInt);

  public abstract ServiceSourceEditorPanel getEditorPanel(ServiceWrapper paramServiceWrapper);

  public abstract void setTabTitle(int paramInt, String paramString);

  public abstract void updatePeriod(int paramInt, Period paramPeriod);

  public abstract void changePeriodForChartPanel(Integer paramInteger, JForexPeriod paramJForexPeriod);

  public abstract void addFrameListener(FrameListener paramFrameListener);

  public abstract void removeEventHandlerFor(Integer paramInteger);

  public abstract void closeAll();

  public abstract void saveState();

  public abstract void restoreState();

  public abstract void populatePopupMenuWithMenuItems(JPopupMenu paramJPopupMenu);

  public abstract void updateInstrument(int paramInt, Instrument paramInstrument);

  public abstract void changeInsturmentForChartPanel(Integer paramInteger, Instrument paramInstrument);

  public abstract void updateDataType(int paramInt, DataType paramDataType);

  public abstract void updatePriceRange(int paramInt, PriceRange paramPriceRange);

  public abstract void updateJForexPeriod(int paramInt, JForexPeriod paramJForexPeriod);

  public abstract void updatePinUnpinBtnState();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController
 * JD-Core Version:    0.6.0
 */