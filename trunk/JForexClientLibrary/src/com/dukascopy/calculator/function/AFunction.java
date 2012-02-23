package com.dukascopy.calculator.function;

import com.dukascopy.calculator.OObject;

public abstract class AFunction extends PObject
{
  public abstract double function(double paramDouble1, double paramDouble2);

  public abstract double function(double paramDouble);

  public abstract OObject function(OObject paramOObject1, OObject paramOObject2);

  public abstract OObject function(OObject paramOObject);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.AFunction
 * JD-Core Version:    0.6.0
 */