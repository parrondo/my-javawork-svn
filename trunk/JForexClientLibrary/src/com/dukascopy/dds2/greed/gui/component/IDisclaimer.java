package com.dukascopy.dds2.greed.gui.component;

public abstract interface IDisclaimer
{
  public abstract boolean isAccepted();

  public abstract boolean isTempAccepted();

  public abstract boolean isPermAccepted();

  public abstract void showDialog();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.IDisclaimer
 * JD-Core Version:    0.6.0
 */