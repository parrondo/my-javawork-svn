package com.dukascopy.calculator;

public abstract interface ReadOnlyDisplayPanel
{
  public abstract boolean getOn();

  public abstract ReadOnlyCalculatorApplet getApplet();

  public abstract boolean hasCaret(ScrollableLabel paramScrollableLabel);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ReadOnlyDisplayPanel
 * JD-Core Version:    0.6.0
 */