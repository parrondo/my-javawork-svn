package com.dukascopy.dds2.greed.gui.component.strategy.tab.preset;

import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParamsPanel;
import java.util.List;

public abstract interface IStrategyPresetsController
{
  public static final String DEFAULT_PRESET_ID = "DEFAULT_PRESET_ID";
  public static final String DEFAULT_PRESET_NAME = "Default Preset";
  public static final String PRESETS_FILE_EXT = ".xml";
  public static final String PRESETS_NODE = "presets";
  public static final String PRESET_NODE = "preset";
  public static final String PARAMETER_NODE = "parameter";
  public static final String ID_KEY = "id";
  public static final String NAME_KEY = "name";
  public static final String TYPE_KEY = "type";
  public static final String VALUE_KEY = "value";

  public abstract StrategyPreset getStrategyPresetBy(List<StrategyPreset> paramList, String paramString);

  public abstract void savePreset(StrategyNewBean paramStrategyNewBean, StrategyPresetsComboBoxModel paramStrategyPresetsComboBoxModel, StrategyParamsPanel paramStrategyParamsPanel);

  public abstract void deletePreset(StrategyNewBean paramStrategyNewBean, StrategyPresetsComboBoxModel paramStrategyPresetsComboBoxModel);

  public abstract StrategyPreset createPreset(StrategyPresetsComboBoxModel paramStrategyPresetsComboBoxModel, StrategyParamsPanel paramStrategyParamsPanel, String paramString);

  public abstract void updatePreset(StrategyParamsPanel paramStrategyParamsPanel, StrategyPreset paramStrategyPreset);

  public abstract void savePresets(StrategyNewBean paramStrategyNewBean, List<StrategyPreset> paramList);

  public abstract List<StrategyPreset> loadPresets(StrategyNewBean paramStrategyNewBean);

  public abstract List<StrategyParameterLocal> retrievePresetParameters(String paramString, StrategyParamsPanel paramStrategyParamsPanel);

  public abstract boolean parametersValid(StrategyParamsPanel paramStrategyParamsPanel);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.IStrategyPresetsController
 * JD-Core Version:    0.6.0
 */