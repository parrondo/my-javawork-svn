package org.eclipse.jdt.internal.compiler.env;

public abstract interface ISourceType extends IGenericType
{
  public abstract int getDeclarationSourceEnd();

  public abstract int getDeclarationSourceStart();

  public abstract ISourceType getEnclosingType();

  public abstract ISourceField[] getFields();

  public abstract char[][] getInterfaceNames();

  public abstract ISourceType[] getMemberTypes();

  public abstract ISourceMethod[] getMethods();

  public abstract char[] getName();

  public abstract int getNameSourceEnd();

  public abstract int getNameSourceStart();

  public abstract char[] getSuperclassName();

  public abstract char[][][] getTypeParameterBounds();

  public abstract char[][] getTypeParameterNames();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.ISourceType
 * JD-Core Version:    0.6.0
 */