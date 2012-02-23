package com.dukascopy.dds2.greed.export.historicaldata;

import com.dukascopy.api.Instrument;

public abstract interface ExportProcessControlListener
{
  public abstract void stateChanged(ExportProcessControl.State paramState);

  public abstract void progressChanged(int paramInt, String paramString);

  public abstract void validated(DataField paramDataField, boolean paramBoolean, Instrument paramInstrument, int paramInt, String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControlListener
 * JD-Core Version:    0.6.0
 */