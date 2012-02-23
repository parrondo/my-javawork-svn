package com.dukascopy.dds2.greed.util;

public abstract interface INotificationUtils
{
  public abstract void postInfoMessage(String paramString);

  public abstract void postInfoMessage(String paramString, boolean paramBoolean);

  public abstract void postInfoMessage(String paramString, Throwable paramThrowable);

  public abstract void postInfoMessage(String paramString, Throwable paramThrowable, boolean paramBoolean);

  public abstract void postWarningMessage(String paramString);

  public abstract void postWarningMessage(String paramString, boolean paramBoolean);

  public abstract void postWarningMessage(String paramString, Throwable paramThrowable);

  public abstract void postWarningMessage(String paramString, Throwable paramThrowable, boolean paramBoolean);

  public abstract void postErrorMessage(String paramString);

  public abstract void postErrorMessage(String paramString, boolean paramBoolean);

  public abstract void postErrorMessage(String paramString, Throwable paramThrowable);

  public abstract void postErrorMessage(String paramString, Throwable paramThrowable, boolean paramBoolean);

  public abstract void postFatalMessage(String paramString);

  public abstract void postFatalMessage(String paramString, boolean paramBoolean);

  public abstract void postFatalMessage(String paramString, Throwable paramThrowable);

  public abstract void postFatalMessage(String paramString, Throwable paramThrowable, boolean paramBoolean);

  public abstract void postMessage(String paramString, NotificationLevel paramNotificationLevel);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.INotificationUtils
 * JD-Core Version:    0.6.0
 */