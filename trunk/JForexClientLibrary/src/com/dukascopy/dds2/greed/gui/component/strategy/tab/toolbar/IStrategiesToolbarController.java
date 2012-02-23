package com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar;

import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.IStrategyPresetsController;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable;
import java.io.File;
import java.util.List;

public abstract interface IStrategiesToolbarController
{
  public abstract List<StrategyNewBean> addStrategies(StrategiesTable paramStrategiesTable, StrategiesToolbar paramStrategiesToolbar, IStrategyPresetsController paramIStrategyPresetsController);

  public abstract List<StrategyNewBean> deleteStrategies(StrategiesTable paramStrategiesTable);

  public abstract StrategyNewBean createStrategyBean(File paramFile, IStrategyPresetsController paramIStrategyPresetsController, String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.IStrategiesToolbarController
 * JD-Core Version:    0.6.0
 */