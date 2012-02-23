package com.dukascopy.transport.common;

import com.dukascopy.transport.common.model.type.Money;
import com.dukascopy.transport.common.msg.group.OrderMessage;
import com.dukascopy.transport.common.msg.group.TradeMessage;

public abstract interface LiquidityProvider
{
  public abstract String submitOrder(OrderMessage paramOrderMessage, Money paramMoney)
    throws TradeException;

  public abstract OrderMessage onTrade(String paramString, TradeMessage paramTradeMessage);

  public abstract boolean isOnline();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.LiquidityProvider
 * JD-Core Version:    0.6.0
 */