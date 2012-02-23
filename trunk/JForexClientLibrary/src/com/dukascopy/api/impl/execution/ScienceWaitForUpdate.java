package com.dukascopy.api.impl.execution;

import com.dukascopy.api.JFException;

public abstract interface ScienceWaitForUpdate
{
  public abstract boolean updated();

  public abstract boolean updated(String[] paramArrayOfString)
    throws JFException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.ScienceWaitForUpdate
 * JD-Core Version:    0.6.0
 */