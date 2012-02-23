package com.dukascopy.api.connector;

public abstract interface ISettings
{
  public abstract void clear();

  public abstract String get(String paramString);

  public abstract String put(String paramString1, String paramString2);

  public abstract String remove(String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.ISettings
 * JD-Core Version:    0.6.0
 */