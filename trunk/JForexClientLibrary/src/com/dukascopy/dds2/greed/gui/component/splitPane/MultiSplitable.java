package com.dukascopy.dds2.greed.gui.component.splitPane;

import com.dukascopy.dds2.greed.gui.l10n.components.Hidable;

public abstract interface MultiSplitable extends Hidable
{
  public abstract int getPrefHeight();

  public abstract int getMaxHeight();

  public abstract int getMinHeight();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitable
 * JD-Core Version:    0.6.0
 */