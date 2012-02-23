package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Collection;

public abstract class Spans
{
  public abstract boolean next()
    throws IOException;

  public abstract boolean skipTo(int paramInt)
    throws IOException;

  public abstract int doc();

  public abstract int start();

  public abstract int end();

  public abstract Collection<byte[]> getPayload()
    throws IOException;

  public abstract boolean isPayloadAvailable();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.Spans
 * JD-Core Version:    0.6.0
 */