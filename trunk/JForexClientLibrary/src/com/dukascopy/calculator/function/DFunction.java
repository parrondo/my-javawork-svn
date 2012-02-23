package com.dukascopy.calculator.function;

import com.dukascopy.calculator.OObject;

public abstract class DFunction extends PObject
{
  public abstract double function(double paramDouble1, double paramDouble2);

  public abstract OObject function(OObject paramOObject1, OObject paramOObject2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.DFunction
 * JD-Core Version:    0.6.0
 */