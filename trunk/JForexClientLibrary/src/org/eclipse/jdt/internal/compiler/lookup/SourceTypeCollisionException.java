package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

public class SourceTypeCollisionException extends RuntimeException
{
  private static final long serialVersionUID = 4798247636899127380L;
  public ICompilationUnit[] newAnnotationProcessorUnits;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException
 * JD-Core Version:    0.6.0
 */