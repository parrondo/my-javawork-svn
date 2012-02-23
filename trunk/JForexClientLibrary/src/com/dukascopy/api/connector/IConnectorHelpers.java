package com.dukascopy.api.connector;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import java.util.Set;
import javax.swing.ComboBoxModel;

public abstract interface IConnectorHelpers
{
  public abstract Object[] getCharts();

  public abstract Object getConnectorInstance();

  public abstract ComboBoxModel getComboBoxModel();

  public abstract int addChart(Instrument paramInstrument, Period paramPeriod);

  public abstract int addChart(Instrument paramInstrument, Period paramPeriod, boolean paramBoolean);

  public abstract void subscribeToInstruments(Set<String> paramSet);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.IConnectorHelpers
 * JD-Core Version:    0.6.0
 */