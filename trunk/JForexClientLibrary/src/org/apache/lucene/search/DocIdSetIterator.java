package org.apache.lucene.search;

import java.io.IOException;

public abstract class DocIdSetIterator
{
  public static final int NO_MORE_DOCS = 2147483647;

  public abstract int docID();

  public abstract int nextDoc()
    throws IOException;

  public abstract int advance(int paramInt)
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.DocIdSetIterator
 * JD-Core Version:    0.6.0
 */