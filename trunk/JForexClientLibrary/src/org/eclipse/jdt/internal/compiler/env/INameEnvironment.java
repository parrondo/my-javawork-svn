package org.eclipse.jdt.internal.compiler.env;

public abstract interface INameEnvironment
{
  public abstract NameEnvironmentAnswer findType(char[][] paramArrayOfChar);

  public abstract NameEnvironmentAnswer findType(char[] paramArrayOfChar, char[][] paramArrayOfChar1);

  public abstract boolean isPackage(char[][] paramArrayOfChar, char[] paramArrayOfChar1);

  public abstract void cleanup();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.INameEnvironment
 * JD-Core Version:    0.6.0
 */