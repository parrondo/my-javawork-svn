package com.dukascopy.dds2.greed.gui.component.strategy.tab.properties;

import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParamsPanel;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
import java.util.List;
import javax.swing.JComboBox;

public abstract interface IStrategyPropertiesPanelBuilder
{
  public abstract StrategyParamsPanel buildParametersPanel(JComboBox paramJComboBox, StrategyNewBean paramStrategyNewBean, StrategyPreset paramStrategyPreset);

  public abstract void updateParametersPanel(StrategyParamsPanel paramStrategyParamsPanel, List<StrategyParameterLocal> paramList);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.properties.IStrategyPropertiesPanelBuilder
 * JD-Core Version:    0.6.0
 */