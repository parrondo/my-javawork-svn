package com.dukascopy.dds2.greed.util.event;

import java.util.EventListener;

public abstract interface ApplicationListener extends EventListener
{
  public abstract void onApplicationEvent(ApplicationEvent paramApplicationEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.event.ApplicationListener
 * JD-Core Version:    0.6.0
 */