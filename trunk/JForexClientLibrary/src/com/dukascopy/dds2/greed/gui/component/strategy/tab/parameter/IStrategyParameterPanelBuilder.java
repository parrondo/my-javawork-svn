package com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter;

public abstract interface IStrategyParameterPanelBuilder
{
  public abstract StrategyParameterPanel buildParameterPanel(StrategyParameterLocal paramStrategyParameterLocal, StrategyParameterChangeNotifier paramStrategyParameterChangeNotifier);

  public abstract void updateParameterPanel(StrategyParameterLocal paramStrategyParameterLocal, StrategyParameterPanel paramStrategyParameterPanel);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.IStrategyParameterPanelBuilder
 * JD-Core Version:    0.6.0
 */