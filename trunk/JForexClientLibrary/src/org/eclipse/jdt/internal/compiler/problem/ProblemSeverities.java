package org.eclipse.jdt.internal.compiler.problem;

public abstract interface ProblemSeverities
{
  public static final int Ignore = -1;
  public static final int Warning = 0;
  public static final int Error = 1;
  public static final int AbortCompilation = 2;
  public static final int AbortCompilationUnit = 4;
  public static final int AbortType = 8;
  public static final int AbortMethod = 16;
  public static final int Abort = 30;
  public static final int Optional = 32;
  public static final int SecondaryError = 64;
  public static final int Fatal = 128;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.problem.ProblemSeverities
 * JD-Core Version:    0.6.0
 */