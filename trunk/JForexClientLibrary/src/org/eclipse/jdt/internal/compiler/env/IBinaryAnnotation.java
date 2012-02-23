package org.eclipse.jdt.internal.compiler.env;

public abstract interface IBinaryAnnotation
{
  public abstract char[] getTypeName();

  public abstract IBinaryElementValuePair[] getElementValuePairs();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation
 * JD-Core Version:    0.6.0
 */