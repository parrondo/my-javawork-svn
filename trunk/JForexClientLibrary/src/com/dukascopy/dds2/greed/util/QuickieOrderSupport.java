package com.dukascopy.dds2.greed.util;

import com.dukascopy.transport.common.model.type.OrderSide;
import java.math.BigDecimal;

public abstract interface QuickieOrderSupport
{
  public abstract void quickieOrder(String paramString, OrderSide paramOrderSide);

  public abstract BigDecimal getAmount();

  public abstract String getSlippageAmount();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.QuickieOrderSupport
 * JD-Core Version:    0.6.0
 */