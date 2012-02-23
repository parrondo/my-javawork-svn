package com.dukascopy.transport.client;

import com.dukascopy.transport.client.events.DisconnectedEvent;
import com.dukascopy.transport.common.msg.ProtocolMessage;

public abstract interface ClientListener
{
  public abstract void authorized(TransportClient paramTransportClient);

  public abstract void feedbackMessageReceived(TransportClient paramTransportClient, ProtocolMessage paramProtocolMessage);

  public abstract void disconnected(DisconnectedEvent paramDisconnectedEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.ClientListener
 * JD-Core Version:    0.6.0
 */