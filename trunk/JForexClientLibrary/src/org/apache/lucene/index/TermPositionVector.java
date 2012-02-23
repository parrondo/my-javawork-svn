package org.apache.lucene.index;

public abstract interface TermPositionVector extends TermFreqVector
{
  public abstract int[] getTermPositions(int paramInt);

  public abstract TermVectorOffsetInfo[] getOffsets(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermPositionVector
 * JD-Core Version:    0.6.0
 */