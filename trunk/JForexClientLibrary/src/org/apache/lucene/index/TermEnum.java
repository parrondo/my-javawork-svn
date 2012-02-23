package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;

public abstract class TermEnum
  implements Closeable
{
  public abstract boolean next()
    throws IOException;

  public abstract Term term();

  public abstract int docFreq();

  public abstract void close()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermEnum
 * JD-Core Version:    0.6.0
 */