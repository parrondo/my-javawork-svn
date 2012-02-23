package com.dukascopy.dds2.greed.gui.component.orders.ifdone;

import com.dukascopy.transport.common.model.type.OrderSide;
import java.math.BigDecimal;

public abstract interface IEntryOrderHolder
{
  public abstract String getInstrument();

  public abstract OrderSide getOrderSide();

  public abstract BigDecimal getEntryPrice();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.ifdone.IEntryOrderHolder
 * JD-Core Version:    0.6.0
 */