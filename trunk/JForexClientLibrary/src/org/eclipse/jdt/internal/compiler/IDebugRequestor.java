package org.eclipse.jdt.internal.compiler;

public abstract interface IDebugRequestor
{
  public abstract void acceptDebugResult(CompilationResult paramCompilationResult);

  public abstract boolean isActive();

  public abstract void activate();

  public abstract void deactivate();

  public abstract void reset();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.IDebugRequestor
 * JD-Core Version:    0.6.0
 */