package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.util.List;
import javax.annotation.processing.Processor;

public abstract interface IProcessorProvider
{
  public abstract ProcessorInfo discoverNextProcessor();

  public abstract List<ProcessorInfo> getDiscoveredProcessors();

  public abstract void reportProcessorException(Processor paramProcessor, Exception paramException);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.IProcessorProvider
 * JD-Core Version:    0.6.0
 */