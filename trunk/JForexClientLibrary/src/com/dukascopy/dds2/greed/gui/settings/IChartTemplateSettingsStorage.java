package com.dukascopy.dds2.greed.gui.settings;

import com.dukascopy.charts.persistence.ChartBean;
import java.io.File;

public abstract interface IChartTemplateSettingsStorage
{
  public abstract void saveChartTemplate(File paramFile, int paramInt);

  public abstract void saveChartTemplate(int paramInt);

  public abstract ChartBean loadChartTemplate(File paramFile);

  public abstract ChartBean openChartTemplate();

  public abstract ChartBean cloneChart(int paramInt);

  public abstract ChartBean cloneChartBean(ChartBean paramChartBean);

  public abstract void cleanUpChartTemplateRootNode();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.IChartTemplateSettingsStorage
 * JD-Core Version:    0.6.0
 */