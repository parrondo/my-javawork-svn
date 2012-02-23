package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;

import java.util.EventListener;

public abstract interface ParameterOptimizerListener extends EventListener
{
  public abstract void parametersChanged(ParameterOptimizerEvent paramParameterOptimizerEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParameterOptimizerListener
 * JD-Core Version:    0.6.0
 */