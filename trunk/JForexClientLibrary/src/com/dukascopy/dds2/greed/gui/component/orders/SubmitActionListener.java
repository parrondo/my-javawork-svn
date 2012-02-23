package com.dukascopy.dds2.greed.gui.component.orders;

import com.dukascopy.transport.common.msg.group.OrderGroupMessage;

public abstract interface SubmitActionListener
{
  public abstract void submitButtonPressed(OrderGroupMessage paramOrderGroupMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.SubmitActionListener
 * JD-Core Version:    0.6.0
 */