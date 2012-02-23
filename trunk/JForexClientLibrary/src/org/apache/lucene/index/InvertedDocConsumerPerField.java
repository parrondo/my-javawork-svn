package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.document.Fieldable;

abstract class InvertedDocConsumerPerField
{
  abstract boolean start(Fieldable[] paramArrayOfFieldable, int paramInt)
    throws IOException;

  abstract void start(Fieldable paramFieldable);

  abstract void add()
    throws IOException;

  abstract void finish()
    throws IOException;

  abstract void abort();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.InvertedDocConsumerPerField
 * JD-Core Version:    0.6.0
 */