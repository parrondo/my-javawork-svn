package org.eclipse.jdt.internal.compiler.env;

public abstract interface ISourceField extends IGenericField
{
  public abstract int getDeclarationSourceEnd();

  public abstract int getDeclarationSourceStart();

  public abstract char[] getInitializationSource();

  public abstract int getNameSourceEnd();

  public abstract int getNameSourceStart();

  public abstract char[] getTypeName();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.ISourceField
 * JD-Core Version:    0.6.0
 */