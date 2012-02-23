package com.dukascopy.dds2.events.handler;

import com.dukascopy.dds2.events.Event;

public abstract interface EventWriter
{
  public abstract void store(Event paramEvent, EventHandler paramEventHandler);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.handler.EventWriter
 * JD-Core Version:    0.6.0
 */