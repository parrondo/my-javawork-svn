package com.dukascopy.dds2.greed.gui.component.strategy;

import java.util.EventListener;

public abstract interface ReportingPanelListener extends EventListener
{
  public abstract void stateChanged(ReportingPanelEvent paramReportingPanelEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.ReportingPanelListener
 * JD-Core Version:    0.6.0
 */