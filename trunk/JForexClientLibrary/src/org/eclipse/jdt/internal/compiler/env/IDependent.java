package org.eclipse.jdt.internal.compiler.env;

public abstract interface IDependent
{
  public static final char JAR_FILE_ENTRY_SEPARATOR = '|';

  public abstract char[] getFileName();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.IDependent
 * JD-Core Version:    0.6.0
 */