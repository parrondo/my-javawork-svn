package com.dukascopy.dds2.greed.mt.common;

import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
import com.dukascopy.transport.common.msg.ProtocolMessage;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract interface IAgent
{
  public static final String MT_STRATEGY_ID = "Metatrader external DLL strategy";
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
  public static final byte ARSP_DLL_ID_THREAD_INCORRECT = -20;
  public static final byte ARSP_INSTRUMENT_INCORRECT = -21;
  public static final byte ARSP_VERSION_INCORRECT = -22;
  public static final byte ARSP_HISTORY_NOT_SUPPORTED = -23;
  public static final byte ARSP_UNKNOWN = -99;
  public static final String ARSP_OK_MSG = "ARSP_OK_MSG";
  public static final String ARSP_LABEL_INCONSISTENT_MSG = "ARSP_LABEL_INCONSISTENT_MSG";
  public static final String ARSP_ORDER_CMD_UNKNOWN_MSG = "ARSP_ORDER_CMD_UNKNOWN_MSG";
  public static final String ARSP_TIMEOUT_MSG = "ARSP_TIMEOUT_MSG";
  public static final String ARSP_EXCEPTION_MSG = "ARSP_EXCEPTION_MSG";
  public static final String ARSP_ID_NOT_FOUND_MSG = "ARSP_ID_NOT_FOUND_MSG";
  public static final String ARSP_ID_NOT_UNIQUE_MSG = "ARSP_ID_NOT_UNIQUE_MSG";
  public static final String ARSP_NEGATIVE_PRICE_MSG = "ARSP_NEGATIVE_PRICE_MSG";
  public static final String ARSP_INVALID_AMOUNT_MSG = "ARSP_INVALID_AMOUNT_MSG";
  public static final String ARSP_NO_LIQUIDITY_MSG = "ARSP_NO_LIQUIDITY_MSG";
  public static final String ARSP_NEGATIVE_TIME_MSG = "ARSP_NEGATIVE_TIME_MSG";
  public static final String ARSP_THREAD_INCORRECT_MSG = "ARSP_THREAD_INCORRECT_MSG";
  public static final String ARSP_DLL_ID_THREAD_INCORRECT_MSG = "ARSP_DLL_ID_THREAD_INCORRECT_MSG";
  public static final String ARSP_INSTRUMENT_INCORRECT_MSG = "ARSP_INSTRUMENT_INCORRECT_MSG";
  public static final String ARSP_VERSION_INCORRECT_MSG = "ARSP_VERSION_INCORRECT_MSG";
  public static final String ARSP_HISTORY_NOT_SUPPORTED_MSG = "ARSP_HISTORY_NOT_SUPPORTED_MSG";
  public static final String ARSP_UNKNOWN_MSG = "ARSP_UNKNOWN_MSG";
  public static final String MAGIC_DELIM = "__";
  public static final String MAGIC_PREFIX = "MTAG";
  public static final int PORT = 7000;
  public static final int AG_REQUEST_SIZE = 4096;
  public static final int MAX_PARAMETERS = 11;
  public static final int VERSION = 1;
  public static final String EXTERNAL_MODULE_NAME = "mt4jfx.dll";
  public static final int ATYPE_VOID = 0;
  public static final int ATYPE_STRING = 1;
  public static final int ATYPE_DOUBLE = 2;
  public static final int ATYPE_LONG = 3;
  public static final int ATYPE_INT = 4;

  public abstract <T> Future<T> executeTask(Callable<T> paramCallable);

  public abstract void setError(Integer paramInteger, int paramInt, String paramString);

  public abstract void putNotifMsg(String paramString, ProtocolMessage paramProtocolMessage);

  public abstract boolean isTestMode();

  public abstract void setTestMode(boolean paramBoolean);

  public abstract boolean MOrderClose(int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, int paramInt3, long paramLong)
    throws MTAgentException;

  public abstract boolean MOrderCloseBy(int paramInt1, int paramInt2, int paramInt3, long paramLong)
    throws MTAgentException;

  public abstract double MOrderClosePrice(int paramInt)
    throws MTAgentException;

  public abstract long MOrderCloseTime(int paramInt)
    throws MTAgentException;

  public abstract String MOrderComment(int paramInt)
    throws MTAgentException;

  public abstract double MOrderCommission(int paramInt)
    throws MTAgentException;

  public abstract boolean MOrderDelete(int paramInt1, int paramInt2, long paramLong)
    throws MTAgentException;

  public abstract long MOrderExpiration(int paramInt)
    throws MTAgentException;

  public abstract double MOrderLots(int paramInt)
    throws MTAgentException;

  public abstract int MOrderMagicNumber(int paramInt)
    throws MTAgentException;

  public abstract boolean MOrderModify(int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, long paramLong1, long paramLong2)
    throws MTAgentException;

  public abstract double MOrderOpenPrice(int paramInt)
    throws MTAgentException;

  public abstract long MOrderOpenTime(int paramInt)
    throws MTAgentException;

  public abstract void MOrderPrint(int paramInt)
    throws MTAgentException;

  public abstract double MOrderProfit(int paramInt)
    throws MTAgentException;

  public abstract boolean MOrderSelect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MTAgentException;

  public abstract int MOrderSend(int paramInt1, String paramString1, int paramInt2, double paramDouble1, double paramDouble2, int paramInt3, double paramDouble3, double paramDouble4, String paramString2, int paramInt4, long paramLong1, long paramLong2)
    throws MTAgentException;

  public abstract int MOrdersHistoryTotal(int paramInt)
    throws MTAgentException;

  public abstract double MOrderStopLoss(int paramInt)
    throws MTAgentException;

  public abstract int MOrdersTotal(int paramInt)
    throws MTAgentException;

  public abstract double MOrderSwap(int paramInt)
    throws MTAgentException;

  public abstract String MOrderSymbol(int paramInt)
    throws MTAgentException;

  public abstract double MOrderTakeProfit(int paramInt)
    throws MTAgentException;

  public abstract int MOrderTicket(int paramInt)
    throws MTAgentException;

  public abstract int MOrderType(int paramInt)
    throws MTAgentException;

  public abstract int MGetLastError(int paramInt)
    throws MTAgentException;

  public abstract boolean MIsConnected(int paramInt)
    throws MTAgentException;

  public abstract boolean MIsDemo(int paramInt)
    throws MTAgentException;

  public abstract boolean MIsDllsAllowed(int paramInt)
    throws MTAgentException;

  public abstract boolean MIsExpertEnabled(int paramInt)
    throws MTAgentException;

  public abstract double MAsk(int paramInt, String paramString)
    throws MTAgentException;

  public abstract double MBid(int paramInt, String paramString)
    throws MTAgentException;

  public abstract long MTime(int paramInt, String paramString)
    throws MTAgentException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.common.IAgent
 * JD-Core Version:    0.6.0
 */