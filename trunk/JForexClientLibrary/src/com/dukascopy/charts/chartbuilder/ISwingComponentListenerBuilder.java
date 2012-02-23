package com.dukascopy.charts.chartbuilder;

import com.dukascopy.api.impl.IndicatorWrapper;
import com.dukascopy.charts.listeners.MainComponentSizeListener;
import java.awt.event.ComponentListener;

public abstract interface ISwingComponentListenerBuilder
{
  public abstract MainComponentSizeListener createMainComponentListener();

  public abstract ComponentListener createSubComponentListener(SubIndicatorGroup paramSubIndicatorGroup);

  public abstract int deleteSubIndicatorFromSubChartView(IndicatorWrapper paramIndicatorWrapper);

  public abstract ComponentListener createFirstResizeListenerToRunTask();

  public abstract void addSubIndicatorToSubChartView(IndicatorWrapper paramIndicatorWrapper, int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ISwingComponentListenerBuilder
 * JD-Core Version:    0.6.0
 */