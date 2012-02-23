package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public abstract interface IBinaryField extends IGenericField
{
  public abstract IBinaryAnnotation[] getAnnotations();

  public abstract Constant getConstant();

  public abstract char[] getGenericSignature();

  public abstract char[] getName();

  public abstract long getTagBits();

  public abstract char[] getTypeName();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.IBinaryField
 * JD-Core Version:    0.6.0
 */