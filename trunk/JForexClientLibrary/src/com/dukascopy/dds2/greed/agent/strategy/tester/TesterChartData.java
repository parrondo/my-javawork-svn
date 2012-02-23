package com.dukascopy.dds2.greed.agent.strategy.tester;

import com.dukascopy.api.IChart;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.charts.data.datacache.IFeedDataProvider;
import com.dukascopy.charts.data.datacache.JForexPeriod;

public class TesterChartData
{
  public Instrument instrument;
  public OfferSide offerSide;
  public IFeedDataProvider feedDataProvider;
  public int chartPanelId;
  public IChart chart;
  public JForexPeriod jForexPeriod;
  public String templateName;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterChartData
 * JD-Core Version:    0.6.0
 */