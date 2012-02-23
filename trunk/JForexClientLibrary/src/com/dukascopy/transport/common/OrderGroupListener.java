package com.dukascopy.transport.common;

import com.dukascopy.transport.common.msg.group.OrderGroupMessage;

public abstract interface OrderGroupListener
{
  public abstract void onOrderGroupReceived(OrderGroupMessage paramOrderGroupMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.OrderGroupListener
 * JD-Core Version:    0.6.0
 */