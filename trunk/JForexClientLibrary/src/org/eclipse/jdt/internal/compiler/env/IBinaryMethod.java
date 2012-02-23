package org.eclipse.jdt.internal.compiler.env;

public abstract interface IBinaryMethod extends IGenericMethod
{
  public abstract IBinaryAnnotation[] getAnnotations();

  public abstract Object getDefaultValue();

  public abstract char[][] getExceptionTypeNames();

  public abstract char[] getGenericSignature();

  public abstract char[] getMethodDescriptor();

  public abstract IBinaryAnnotation[] getParameterAnnotations(int paramInt);

  public abstract char[] getSelector();

  public abstract long getTagBits();

  public abstract boolean isClinit();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.IBinaryMethod
 * JD-Core Version:    0.6.0
 */