package com.dukascopy.dds2.greed.gui.component.chart.listeners;

import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramePanel;
import java.util.EventListener;

public abstract interface FrameListener extends EventListener
{
  public abstract boolean isCloseAllowed(TabsAndFramePanel paramTabsAndFramePanel);

  public abstract void frameClosed(TabsAndFramePanel paramTabsAndFramePanel, int paramInt);

  public abstract void frameSelected(int paramInt);

  public abstract void frameAdded(boolean paramBoolean, int paramInt);

  public abstract void frameDocked(int paramInt);

  public abstract void tabClosed(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListener
 * JD-Core Version:    0.6.0
 */