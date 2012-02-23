package com.dukascopy.transport.common.msg.strategy;

public abstract interface IStrategyMessage
{
  public static final String ACCOUNT_NAME = "account_name";

  public abstract void setAccountName(String paramString);

  public abstract String getAccountName();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.IStrategyMessage
 * JD-Core Version:    0.6.0
 */