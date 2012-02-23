package org.eclipse.jdt.internal.compiler.env;

public abstract interface ICompilationUnit extends IDependent
{
  public abstract char[] getContents();

  public abstract char[] getMainTypeName();

  public abstract char[][] getPackageName();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.ICompilationUnit
 * JD-Core Version:    0.6.0
 */