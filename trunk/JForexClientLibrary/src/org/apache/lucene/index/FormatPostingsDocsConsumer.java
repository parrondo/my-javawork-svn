package org.apache.lucene.index;

import java.io.IOException;

abstract class FormatPostingsDocsConsumer
{
  abstract FormatPostingsPositionsConsumer addDoc(int paramInt1, int paramInt2)
    throws IOException;

  abstract void finish()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsDocsConsumer
 * JD-Core Version:    0.6.0
 */