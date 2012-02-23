package org.eclipse.jdt.internal.compiler.lookup;

public abstract interface Substitution
{
  public abstract TypeBinding substitute(TypeVariableBinding paramTypeVariableBinding);

  public abstract LookupEnvironment environment();

  public abstract boolean isRawSubstitution();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.Substitution
 * JD-Core Version:    0.6.0
 */