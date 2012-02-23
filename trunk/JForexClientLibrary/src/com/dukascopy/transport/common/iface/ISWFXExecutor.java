package com.dukascopy.transport.common.iface;

import com.dukascopy.transport.common.model.type.Money;
import com.dukascopy.transport.common.model.type.OrderSide;
import com.dukascopy.transport.common.model.type.StopDirection;
import com.dukascopy.transport.common.msg.pojo.AccountState;
import com.dukascopy.transport.common.msg.pojo.Order;
import java.math.BigDecimal;
import java.util.List;

public abstract interface ISWFXExecutor
{
  public abstract Boolean placeOffer(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Money paramMoney1, Money paramMoney2, OrderSide paramOrderSide, Long paramLong);

  public abstract Boolean changeOffer(String paramString1, String paramString2, String paramString3, String paramString4, Money paramMoney1, Money paramMoney2, OrderSide paramOrderSide, Long paramLong);

  public abstract Boolean cancelOrder(String paramString1, String paramString2, String paramString3);

  public abstract Boolean placeConditionOrder(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, Money paramMoney1, Money paramMoney2, BigDecimal paramBigDecimal, StopDirection paramStopDirection, OrderSide paramOrderSide);

  public abstract Boolean changeConditionOrder(String paramString1, String paramString2, Money paramMoney1, Money paramMoney2, BigDecimal paramBigDecimal, StopDirection paramStopDirection, OrderSide paramOrderSide);

  public abstract Boolean executeByMarket(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Money paramMoney1, OrderSide paramOrderSide, Money paramMoney2, Money paramMoney3);

  public abstract void setCustodianOnline(Boolean paramBoolean, String paramString);

  public abstract Boolean subscribeSession(String paramString);

  public abstract Boolean addLiquidityProviderAccount(String paramString, AccountState paramAccountState);

  public abstract List<Order> positionMassClose(String paramString, List<Order> paramList);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.iface.ISWFXExecutor
 * JD-Core Version:    0.6.0
 */