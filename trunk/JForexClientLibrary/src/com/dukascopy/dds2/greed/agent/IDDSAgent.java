package com.dukascopy.dds2.greed.agent;

import com.dukascopy.dds2.greed.agent.strategy.objects.AccountInfo;
import com.dukascopy.dds2.greed.agent.strategy.objects.Order;
import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract interface IDDSAgent
{
  public static final int AGENT_TEST = 0;
  public static final int AGENT_DDS = 1;
  public static final int OP_BUY = 0;
  public static final int OP_SELL = 1;
  public static final int OP_BUYLIMIT = 2;
  public static final int OP_SELLLIMIT = 3;
  public static final int OP_BUYSTOP = 4;
  public static final int OP_SELLSTOP = 5;
  public static final int OP_BUYLIMIT_BYBID = 6;
  public static final int OP_SELLLIMIT_BYASK = 7;
  public static final int OP_BUYSTOP_BYBID = 8;
  public static final int OP_SELLSTOP_BYASK = 9;
  public static final int OP_STOPLOSS = 0;
  public static final int OP_TAKEPROFIT = 1;
  public static final int OP_STOPLOSS_BYASK = 2;
  public static final int OP_STOPLOSS_BYBID = 3;
  public static final int OP_TAKEPROFIT_BYASK = 4;
  public static final int OP_TAKEPROFIT_BYBID = 5;
  public static final byte ARSP_OK = 0;
  public static final byte ARSP_LABEL_INCONSISTENT = -4;
  public static final byte ARSP_ORDER_CMD_UNKNOWN = -10;
  public static final byte ARSP_TIMEOUT = -11;
  public static final byte ARSP_EXCEPTION = -12;
  public static final byte ARSP_ID_NOT_FOUND = -13;
  public static final byte ARSP_ID_NOT_UNIQUE = -14;
  public static final byte ARSP_NEGATIVE_PRICE = -15;
  public static final byte ARSP_INVALID_AMOUNT = -16;
  public static final byte ARSP_NO_LIQUIDITY = -17;
  public static final byte ARSP_NEGATIVE_TIME = -18;
  public static final byte ARSP_THREAD_INCORRECT = -19;
  public static final byte ARSP_UNKNOWN = -99;
  public static final int PORT = 7000;
  public static final int AG_REQUEST_SIZE = 119;
  public static final int MAX_PARAMETERS = 8;
  public static final int VERSION = 1;
  public static final int ATYPE_VOID = 0;
  public static final int ATYPE_STRING = 1;
  public static final int ATYPE_DOUBLE = 2;
  public static final int ATYPE_LONG = 3;
  public static final int ATYPE_INT = 4;

  public abstract int submitOrder(String paramString1, String paramString2, int paramInt1, double paramDouble1, double paramDouble2, int paramInt2, int paramInt3)
    throws AgentException;

  public abstract int submitOrder(String paramString1, String paramString2, int paramInt1, double paramDouble1, double paramDouble2, int paramInt2, int paramInt3, String paramString3)
    throws AgentException;

  public abstract int placeOffer(String paramString1, String paramString2, int paramInt1, double paramDouble1, double paramDouble2, int paramInt2)
    throws AgentException;

  public abstract int closePosition(String paramString);

  public abstract int closePosition(String paramString, double paramDouble1, double paramDouble2);

  public abstract int closeProfitPosition(String paramString, int paramInt);

  public abstract int cancelOrder(String paramString);

  public abstract int submitStop(String paramString, int paramInt1, int paramInt2)
    throws AgentException;

  public abstract int submitStop(String paramString, int paramInt1, int paramInt2, double paramDouble)
    throws AgentException;

  public abstract int setSlipageControl(double paramDouble);

  public abstract Collection<Order> getOrders(boolean paramBoolean, String paramString);

  public abstract void updateAccountInfo(AccountInfo paramAccountInfo);

  public abstract void onOrderGroupReceived(OrderGroupMessage paramOrderGroupMessage);

  public abstract Date getGMT();

  public abstract void setConsole(Object paramObject);

  public abstract Object getProperty(String paramString);

  public abstract void setProperty(String paramString, Object paramObject);

  public abstract int getImplementation();

  public abstract <T> Future<T> executeTask(Callable<T> paramCallable);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.IDDSAgent
 * JD-Core Version:    0.6.0
 */