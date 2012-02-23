package org.eclipse.jdt.internal.compiler.env;

public abstract interface ISourceMethod extends IGenericMethod
{
  public abstract int getDeclarationSourceEnd();

  public abstract int getDeclarationSourceStart();

  public abstract char[][] getExceptionTypeNames();

  public abstract int getNameSourceEnd();

  public abstract int getNameSourceStart();

  public abstract char[] getReturnTypeName();

  public abstract char[][] getTypeParameterNames();

  public abstract char[][][] getTypeParameterBounds();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.ISourceMethod
 * JD-Core Version:    0.6.0
 */