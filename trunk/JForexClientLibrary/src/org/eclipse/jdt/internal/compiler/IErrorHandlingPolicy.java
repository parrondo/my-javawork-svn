package org.eclipse.jdt.internal.compiler;

public abstract interface IErrorHandlingPolicy
{
  public abstract boolean proceedOnErrors();

  public abstract boolean stopOnFirstError();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy
 * JD-Core Version:    0.6.0
 */