package com.dukascopy.dds2.management;

import java.util.Collection;

public abstract interface ApiServerMBean
{
  public abstract Collection<String> getUsersOnline();

  public abstract void sendPersonalNotification(String paramString1, String paramString2, String paramString3);

  public abstract void broadcastNotification(String paramString1, String paramString2);

  public abstract void sendPersonalNotification(String paramString1, String paramString2, String paramString3, String paramString4);

  public abstract void broadcastNotification(String paramString1, String paramString2, String paramString3);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.management.ApiServerMBean
 * JD-Core Version:    0.6.0
 */