package org.apache.lucene.index;

public abstract interface TermFreqVector
{
  public abstract String getField();

  public abstract int size();

  public abstract String[] getTerms();

  public abstract int[] getTermFrequencies();

  public abstract int indexOf(String paramString);

  public abstract int[] indexesOf(String[] paramArrayOfString, int paramInt1, int paramInt2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermFreqVector
 * JD-Core Version:    0.6.0
 */