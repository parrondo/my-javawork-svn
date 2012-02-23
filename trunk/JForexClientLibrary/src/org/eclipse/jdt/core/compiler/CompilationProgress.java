package org.eclipse.jdt.core.compiler;

public abstract class CompilationProgress
{
  public abstract void begin(int paramInt);

  public abstract void done();

  public abstract boolean isCanceled();

  public abstract void setTaskName(String paramString);

  public abstract void worked(int paramInt1, int paramInt2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.core.compiler.CompilationProgress
 * JD-Core Version:    0.6.0
 */