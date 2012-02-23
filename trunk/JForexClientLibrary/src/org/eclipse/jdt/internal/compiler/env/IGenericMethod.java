package org.eclipse.jdt.internal.compiler.env;

public abstract interface IGenericMethod
{
  public abstract int getModifiers();

  public abstract boolean isConstructor();

  public abstract char[][] getArgumentNames();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.IGenericMethod
 * JD-Core Version:    0.6.0
 */