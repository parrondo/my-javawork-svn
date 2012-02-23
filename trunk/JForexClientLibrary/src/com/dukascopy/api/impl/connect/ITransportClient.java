package com.dukascopy.api.impl.connect;

import com.dukascopy.transport.common.msg.ProtocolMessage;

public abstract interface ITransportClient
{
  public abstract ProtocolMessage controlRequest(ProtocolMessage paramProtocolMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.ITransportClient
 * JD-Core Version:    0.6.0
 */