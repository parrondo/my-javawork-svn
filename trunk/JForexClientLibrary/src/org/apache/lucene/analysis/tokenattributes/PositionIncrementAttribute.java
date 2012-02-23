package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public abstract interface PositionIncrementAttribute extends Attribute
{
  public abstract void setPositionIncrement(int paramInt);

  public abstract int getPositionIncrement();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 * JD-Core Version:    0.6.0
 */