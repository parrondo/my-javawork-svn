package com.dukascopy.api.impl.talib;

import com.dukascopy.api.impl.TaLibMetaData;
import java.util.Set;

public abstract interface TaGrpService
{
  public abstract void execute(String paramString, Set<TaLibMetaData> paramSet)
    throws Exception;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.talib.TaGrpService
 * JD-Core Version:    0.6.0
 */