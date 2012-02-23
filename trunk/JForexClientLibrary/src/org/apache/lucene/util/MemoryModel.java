package org.apache.lucene.util;

public abstract class MemoryModel
{
  public abstract int getArraySize();

  public abstract int getClassSize();

  public abstract int getPrimitiveSize(Class<?> paramClass);

  public abstract int getReferenceSize();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.MemoryModel
 * JD-Core Version:    0.6.0
 */