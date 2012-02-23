package com.dukascopy.dds2.greed.gui.component.chart;

import com.dukascopy.dds2.greed.gui.component.chart.holders.IPanelIdHolder;
import java.awt.Rectangle;

public abstract interface DockedUndockedFrame extends IPanelIdHolder
{
  public abstract String getTitle_();

  public abstract void setTitle(String paramString);

  public abstract void setToolTipText(String paramString);

  public abstract String getToolTipText();

  public abstract TabsAndFramePanel getContent();

  public abstract void dispose();

  public abstract void updateMenuItems(TabsOrderingMenuContainer paramTabsOrderingMenuContainer);

  public abstract void setVisible(boolean paramBoolean);

  public abstract void setSelected(boolean paramBoolean);

  public abstract boolean isSelected();

  public abstract void toFront();

  public abstract void setMaximum(boolean paramBoolean);

  public abstract void setBounds(Rectangle paramRectangle);

  public abstract Rectangle getBounds();

  public abstract boolean isUndocked();

  public abstract void setAlwaysOnTop(boolean paramBoolean);

  public abstract boolean shouldBeLocalized();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.DockedUndockedFrame
 * JD-Core Version:    0.6.0
 */