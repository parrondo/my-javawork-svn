package com.dukascopy.charts.persistence;

import java.util.List;

public abstract interface SettingsStorage
{
  public static final String COMMON_NODE = "common";
  public static final String SYSTEM_PROPERTIES_PREFIX = "com.dukascopy.";

  public abstract List<EnabledIndicatorBean> getEnabledIndicators();

  public abstract void saveEnabledIndicator(EnabledIndicatorBean paramEnabledIndicatorBean);

  public abstract void removeEnabledIndicator(EnabledIndicatorBean paramEnabledIndicatorBean);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.SettingsStorage
 * JD-Core Version:    0.6.0
 */