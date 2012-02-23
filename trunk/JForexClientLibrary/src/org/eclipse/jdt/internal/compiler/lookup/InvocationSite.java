package org.eclipse.jdt.internal.compiler.lookup;

public abstract interface InvocationSite
{
  public abstract TypeBinding[] genericTypeArguments();

  public abstract boolean isSuperAccess();

  public abstract boolean isTypeAccess();

  public abstract void setActualReceiverType(ReferenceBinding paramReferenceBinding);

  public abstract void setDepth(int paramInt);

  public abstract void setFieldIndex(int paramInt);

  public abstract int sourceEnd();

  public abstract int sourceStart();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.InvocationSite
 * JD-Core Version:    0.6.0
 */