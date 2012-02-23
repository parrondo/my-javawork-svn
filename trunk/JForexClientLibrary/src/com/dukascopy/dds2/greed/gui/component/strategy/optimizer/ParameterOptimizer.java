package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;

import java.awt.Component;
import javax.swing.JPanel;

public abstract interface ParameterOptimizer
{
  public abstract Component getMainComponent();

  public abstract void layoutOptimizerComponents(JPanel paramJPanel, Object paramObject);

  public abstract void validateParams()
    throws CommitErrorException;

  public abstract Object[] getParams();

  public abstract void setParams(Object[] paramArrayOfObject);

  public abstract void addOptimizerListener(ParameterOptimizerListener paramParameterOptimizerListener);

  public abstract void removeOptimizerListener(ParameterOptimizerListener paramParameterOptimizerListener);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParameterOptimizer
 * JD-Core Version:    0.6.0
 */