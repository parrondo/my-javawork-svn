package com.dukascopy.dds2.greed.gui.component.dowjones;

import com.dukascopy.api.INewsMessage;

public abstract interface IColumn<Info extends INewsMessage>
{
  public abstract Object getValue(Info paramInfo);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.IColumn
 * JD-Core Version:    0.6.0
 */