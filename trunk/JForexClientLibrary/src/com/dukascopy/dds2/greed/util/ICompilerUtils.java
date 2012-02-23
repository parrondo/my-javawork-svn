package com.dukascopy.dds2.greed.util;

import com.dukascopy.api.impl.ServiceWrapper;

public abstract interface ICompilerUtils
{
  public abstract boolean runCompilation(ServiceWrapper paramServiceWrapper);

  public abstract ServiceWrapper runTesterCompilation(ServiceWrapper paramServiceWrapper);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.ICompilerUtils
 * JD-Core Version:    0.6.0
 */