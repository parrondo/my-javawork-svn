package com.dukascopy.transport.common;

import com.dukascopy.transport.common.msg.group.OrderMessage;

public abstract interface OrderListener
{
  public abstract void onOrderReceived(OrderMessage paramOrderMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.OrderListener
 * JD-Core Version:    0.6.0
 */