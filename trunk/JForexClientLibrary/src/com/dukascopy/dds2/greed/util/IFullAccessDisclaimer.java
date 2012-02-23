package com.dukascopy.dds2.greed.util;

import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
import com.dukascopy.dds2.greed.gui.component.IDisclaimer;

public abstract interface IFullAccessDisclaimer extends IDisclaimer
{
  public abstract boolean isAccepted();

  public abstract boolean showDialog(JFXPack paramJFXPack);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.IFullAccessDisclaimer
 * JD-Core Version:    0.6.0
 */