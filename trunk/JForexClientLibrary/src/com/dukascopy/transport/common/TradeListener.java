package com.dukascopy.transport.common;

import com.dukascopy.transport.common.msg.group.TradeMessage;

public abstract interface TradeListener
{
  public abstract void onTrade(TradeMessage paramTradeMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.TradeListener
 * JD-Core Version:    0.6.0
 */