package com.dukascopy.dds2.greed.util.event;

public abstract interface ApplicationEventMulticaster
{
  public abstract void addApplicationListener(ApplicationListener paramApplicationListener);

  public abstract void removeApplicationListener(ApplicationListener paramApplicationListener);

  public abstract void removeAllListeners();

  public abstract void multicastEvent(ApplicationEvent paramApplicationEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.event.ApplicationEventMulticaster
 * JD-Core Version:    0.6.0
 */