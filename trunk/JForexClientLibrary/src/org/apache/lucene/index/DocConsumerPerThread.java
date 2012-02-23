package org.apache.lucene.index;

import java.io.IOException;

abstract class DocConsumerPerThread
{
  abstract DocumentsWriter.DocWriter processDocument()
    throws IOException;

  abstract void abort();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocConsumerPerThread
 * JD-Core Version:    0.6.0
 */