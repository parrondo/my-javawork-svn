package com.dukascopy.transport.common.iface;

import com.dukascopy.transport.common.model.type.Money;
import com.dukascopy.transport.common.model.type.OrderSide;
import com.dukascopy.transport.common.model.type.OrderState;
import com.dukascopy.transport.common.msg.pojo.AccountState;
import com.dukascopy.transport.common.msg.pojo.Order;
import com.dukascopy.transport.common.msg.pojo.Trade;
import java.util.List;

public abstract interface IBackoffice
{
  public abstract Boolean isAllovedByMargin(String paramString1, String paramString2, String paramString3);

  public abstract Order createExecutingOrder(String paramString1, String paramString2, Money paramMoney, OrderSide paramOrderSide);

  public abstract Boolean onStateChange(String paramString1, String paramString2, String paramString3, OrderState paramOrderState, String paramString4);

  public abstract Boolean onTrade(String paramString1, String paramString2, String paramString3, Trade paramTrade);

  public abstract Boolean onTrade(String paramString1, String paramString2, String paramString3, List<Trade> paramList);

  public abstract Boolean onTrade(Order paramOrder);

  public abstract void onRouterOnline(Boolean paramBoolean);

  public abstract AccountState getAccountState(String paramString);

  public abstract Money getAccountUsableMargin(String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.iface.IBackoffice
 * JD-Core Version:    0.6.0
 */