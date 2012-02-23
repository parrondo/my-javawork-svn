package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;

public abstract interface TermDocs extends Closeable
{
  public abstract void seek(Term paramTerm)
    throws IOException;

  public abstract void seek(TermEnum paramTermEnum)
    throws IOException;

  public abstract int doc();

  public abstract int freq();

  public abstract boolean next()
    throws IOException;

  public abstract int read(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws IOException;

  public abstract boolean skipTo(int paramInt)
    throws IOException;

  public abstract void close()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermDocs
 * JD-Core Version:    0.6.0
 */