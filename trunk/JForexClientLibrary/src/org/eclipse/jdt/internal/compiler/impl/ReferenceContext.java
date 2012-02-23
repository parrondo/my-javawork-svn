package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;

public abstract interface ReferenceContext
{
  public abstract void abort(int paramInt, CategorizedProblem paramCategorizedProblem);

  public abstract CompilationResult compilationResult();

  public abstract boolean hasErrors();

  public abstract void tagAsHavingErrors();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.ReferenceContext
 * JD-Core Version:    0.6.0
 */