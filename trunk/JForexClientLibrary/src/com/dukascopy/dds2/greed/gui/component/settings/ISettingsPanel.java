package com.dukascopy.dds2.greed.gui.component.settings;

public abstract interface ISettingsPanel
{
  public abstract void resetFields();

  public abstract boolean verifySettings();

  public abstract void applySettings();

  public abstract void resetToDefaults();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.ISettingsPanel
 * JD-Core Version:    0.6.0
 */