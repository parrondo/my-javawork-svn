package com.dukascopy.dds2.greed.agent.strategy.tester;

import java.util.EventListener;

public abstract interface ExecutionControlListener extends EventListener
{
  public abstract void speedChanged(ExecutionControlEvent paramExecutionControlEvent);

  public abstract void stateChanged(ExecutionControlEvent paramExecutionControlEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlListener
 * JD-Core Version:    0.6.0
 */