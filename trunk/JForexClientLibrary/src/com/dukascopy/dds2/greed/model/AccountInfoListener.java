package com.dukascopy.dds2.greed.model;

import com.dukascopy.transport.common.msg.request.AccountInfoMessage;

public abstract interface AccountInfoListener
{
  public abstract void onAccountInfo(AccountInfoMessage paramAccountInfoMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.model.AccountInfoListener
 * JD-Core Version:    0.6.0
 */